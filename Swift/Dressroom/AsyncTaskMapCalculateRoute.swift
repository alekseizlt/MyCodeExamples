import Foundation
import GoogleMaps
import SwiftyJSON

// Асинхронный класс для прокладки маршрута между двумя точками на карте
public class AsyncTaskMapCalculateRoute {
    
    // Свойства данного класса
    private var mContext: ViewControllerGoogleMaps?             // контекст
    private var mStartGeoPoint: CLLocationCoordinate2D?         // точка начала для прокладки маршрута
    private var mStopGeoPoint: CLLocationCoordinate2D?          // точка конца для прокладки маршрута
    
    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    // startGeoPoint - точка начала для прокладки маршрута
    // stopGeoPoint - точка конца для прокладки маршрута
    init(context: ViewControllerGoogleMaps?, startGeoPoint: CLLocationCoordinate2D, stopGeoPoint: CLLocationCoordinate2D) {
        self.setContext(context)                   // контекст
        self.setStartGeoPoint(startGeoPoint)       // точка начала для прокладки маршрута
        self.setStopGeoPoint(stopGeoPoint)         // точка конца для прокладки маршрута
    }
    
    //==============================================================================================
    // Метод для считывания контекста
    private func getContext() -> ViewControllerGoogleMaps? {
        return self.mContext
    }
    
    //==============================================================================================
    // Метод для задания контекст
    private func setContext(context: ViewControllerGoogleMaps?) {
        self.mContext = context
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, хранящей координаты точки начала прокладки маршрута
    private func getStartGeoPoint() -> CLLocationCoordinate2D? {
        return self.mStartGeoPoint
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, хранящей координаты точки начала прокладки маршрута
    private func setStartGeoPoint(startGeoPoint: CLLocationCoordinate2D) {
        self.mStartGeoPoint = startGeoPoint
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, хранящей координаты точки конца прокладки маршрута
    private func getStopGeoPoint() -> CLLocationCoordinate2D? {
        return self.mStopGeoPoint
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, хранящей координаты точки конца прокладки маршрута
    private func setStopGeoPoint(stopGeoPoint: CLLocationCoordinate2D) {
        self.mStopGeoPoint = stopGeoPoint
    }
    
    //==============================================================================================
    // Метод для формирования url-адреса для запроса с сервера Google промежуточных точек
    // для прокладки маршрута между двумя точками
    private func createURL() -> String {
        // Точка начала прокладки маршрута
        var stringOrigin: String = ""
        
        if(self.getStartGeoPoint() != nil) {
            stringOrigin = "origin=" + String(self.getStartGeoPoint()!.latitude) + "," + String(self.getStartGeoPoint()!.longitude)
        }
        
        // Точка окончания прокладки маршрута
        var stringDestination: String = ""
        
        if(self.getStopGeoPoint() != nil) {
            stringDestination = "destination=" + String(self.getStopGeoPoint()!.latitude) + "," + String(self.getStopGeoPoint()!.longitude)
        }
        
        // Sensor enabled
        let stringSensor: String = "sensor=false"
        
        // Параметры, передаваемые серверу
        let stringParameters = stringOrigin + "&" + stringDestination + "&" + stringSensor
        
        // Output format
        let stringOutput = "json"
        
        return "https://maps.googleapis.com/maps/api/directions/" + stringOutput + "?" + stringParameters
    }
    
    //==============================================================================================
    // Метод для декодирования промежуточных точек, необходимых для построения маршрута
    private func decodePolyline(encoded: String) -> [CLLocationCoordinate2D] {
        var arrayPoints: [CLLocationCoordinate2D] = [CLLocationCoordinate2D]()
        
        var index: Int = 0
        var latitude: Int = 0
        var longitude: Int = 0
        
        while (index < encoded.characters.count) {
            var charCode: Int = 0
            
            // Разбираем широту
            var shift: Int = 0
            var result: Int = 0
            
            repeat {
                charCode = Int((encoded as NSString).characterAtIndex(index)) - 63
                result |= (charCode & 0x1f) << shift
                shift += 5
                index += 1
            } while (charCode >= 0x20)
            
            let deltaLatitude: Int = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1))
            latitude += deltaLatitude
            
            // Разбираем долготу
            shift = 0
            result = 0
            
            repeat {
                charCode = Int((encoded as NSString).characterAtIndex(index)) - 63
                result |= (charCode & 0x1f) << shift
                shift += 5
                index += 1
            } while (charCode >= 0x20)
            
            let deltaLongitude = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1))
            longitude += deltaLongitude
            
            let point: CLLocationCoordinate2D = CLLocationCoordinate2DMake(
                Double(latitude) / 1E5,
                Double(longitude) / 1E5
            )
            
            arrayPoints.append(point)
        }
        
        return arrayPoints;
    }
    
    //==============================================================================================
    // Осуществляем попытку прокладки маршрута между двумя точками на карте
    public func execute() {
        // Загружаем необходимую информацию в асинхронном потоке
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
            var routes: [[Dictionary<String, String>]] = [[Dictionary<String, String>]]()
        
            // Выполняем запрос к серверу для считывания промежуточных точек, необходимых для прокладки маршрута
            HttpGetPostRequest.executeGetRequest(self.createURL()) { (dataResult, errorResult) -> () in
                if(dataResult != nil) {
                    let jSONObject: JSON = JSON(data: dataResult!, options: NSJSONReadingOptions.MutableContainers, error: nil)
                
                    // Разбираем ответ от сервера
                    if(jSONObject != nil) {
                        if(jSONObject[GlobalFlags.TAG_ROUTES] != nil) {
                            let jSONArrayRoutes: [JSON] = jSONObject[GlobalFlags.TAG_ROUTES].arrayValue
                
                            // В цикле разбираем все участки для построения маршрута
                            for indexRoute in 0..<jSONArrayRoutes.count {
                                let jSONCurrentRoute: JSON = jSONArrayRoutes[indexRoute]
                    
                                if(jSONCurrentRoute[GlobalFlags.TAG_LEGS] != nil) {
                                    let jSONArrayLegs: [JSON] = jSONCurrentRoute[GlobalFlags.TAG_LEGS].arrayValue
                        
                                    var path: [Dictionary<String, String>] = [Dictionary<String, String>]()
                        
                                    // В цикле перебираем все legs
                                    for indexLeg in 0..<jSONArrayLegs.count {
                                        let jSONCurrentLeg: JSON = jSONArrayLegs[indexLeg]
                            
                                        if (jSONCurrentLeg[GlobalFlags.TAG_STEPS] != nil) {
                                            let jSONArraySteps: [JSON] = jSONCurrentLeg[GlobalFlags.TAG_STEPS].arrayValue
                                
                                            // В цикле разбираем все steps
                                            for indexStep in 0..<jSONArraySteps.count {
                                                let jSONCurrentStep: JSON = jSONArraySteps[indexStep]
                                    
                                                var polyline: String = ""
                                    
                                                if (jSONCurrentStep[GlobalFlags.TAG_POLYLINE] != nil) {
                                                    let jSONCurrentPolyline: JSON = jSONCurrentStep[GlobalFlags.TAG_POLYLINE]
                                        
                                                    if (jSONCurrentPolyline[GlobalFlags.TAG_POINTS] != nil) {
                                                        polyline = jSONCurrentPolyline[GlobalFlags.TAG_POINTS].stringValue
                                            
                                                        // Получаем список координат промежуточных точек для текущего отрезка маршрута
                                                        let listPoints: [CLLocationCoordinate2D] = self.decodePolyline(polyline)
                                            
                                                        // В цикле разбираем координаты всех промежуточных точек для текущего отрезка маршрута
                                                        for indexPoint in 0..<listPoints.count {
                                                            var mapPoints: Dictionary<String, String> = Dictionary<String, String>()
                                                            mapPoints[GlobalFlags.TAG_LATITUDE] = String(listPoints[indexPoint].latitude)
                                                            mapPoints[GlobalFlags.TAG_LONGITUDE] = String(listPoints[indexPoint].longitude)
                                                
                                                            path.append(mapPoints)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    routes.append(path)
                                }
                            }
                        }
                    }
                }
            
                //==============================================================================================
                // После завершения фоновой задачи
                dispatch_async(dispatch_get_main_queue(), {
                    let gmsMutablePathPath: GMSMutablePath = GMSMutablePath()
            
                    // В цикле разбираем все routes
                    for indexRoute in 0..<routes.count {
                        let path: [Dictionary<String, String>] = routes[indexRoute]
            
                        // В цикле разбираем все path
                        for indexPath in 0..<path.count {
                            let point: Dictionary<String, String> = path[indexPath]
                
                            if(point[GlobalFlags.TAG_LATITUDE] != nil && point[GlobalFlags.TAG_LONGITUDE] != nil) {
                                if(Double(point[GlobalFlags.TAG_LATITUDE]!) != nil && Double(point[GlobalFlags.TAG_LONGITUDE]!) != nil) {
                                    gmsMutablePathPath.addLatitude(
                                        Double(point[GlobalFlags.TAG_LATITUDE]!)!,
                                        longitude: Double(point[GlobalFlags.TAG_LONGITUDE]!)!
                                    )
                                }
                            }
                        }
                    }
        
                    if(self.getContext() != nil) {
                        // Рисуем непосредственно текущую линию
                        let polyline = GMSPolyline(path: gmsMutablePathPath)
                
                        polyline.strokeWidth = 3.0
                        polyline.geodesic = true
                        polyline.strokeColor = UIColor.blueColor()
                        polyline.map = self.getContext()!.getGoogleMap()
                    }
                })
            }
        })
    }
}