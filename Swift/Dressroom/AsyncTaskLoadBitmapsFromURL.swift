import Foundation
import CoreGraphics
import Kingfisher

// Класс для выполнения асинхронной операции скачивания необходимых изобраэений одежды
// и формирования из них одного общего изображения
public class AsyncTaskLoadBitmapsFromURL {
    
    // Свойства данного класса
    private var mArrayDressImage: [Dictionary<String, String>]?     // массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
    private var mCountDressImageLoaded: Int = 0                     // количество успешно загруженных изображений из текущего набора
    private var mIsDressTypeLegExists: Bool = false                 // существует ли одежда для ног в текущем наборе одежды
    
    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // arrayDressImage - массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
    init(arrayDressImage: [Dictionary<String, String>]) {
        self.setArrayDressImage(arrayDressImage)        // массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
    }

    //==============================================================================================
    // Метод для считывания массива, содержащего ссылки на изображения, входящие в состав текущей коллекции
    private func getArrayDressImage() -> [Dictionary<String, String>]? {
        return self.mArrayDressImage
    }
    
    //==============================================================================================
    // Метод для задания массива, содержащего ссылки на изображения, входящие в состав текущей коллекции
    private func setArrayDressImage(arrayDressImage: [Dictionary<String, String>]) {
        self.mArrayDressImage = arrayDressImage
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, хранящей количество успешно загруженных изображений из текущего набора
    private func getCountDressImageLoaded() -> Int {
        return self.mCountDressImageLoaded
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, хранящей количество успешно загруженных изображений из текущего набора
    private func setCountDressImageLoaded(countDressImageLoaded: Int) {
        self.mCountDressImageLoaded = countDressImageLoaded
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, определяющей существует ли одежда для ног в текущем наборе одежды
    private func getIsDressTypeLegExists() -> Bool {
        return self.mIsDressTypeLegExists
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, определяющей существует ли одежда для ног в текущем наборе одежды
    private func setIsDressTypeLegExists(isDressTypeLegExists: Bool) {
        self.mIsDressTypeLegExists = isDressTypeLegExists
    }
    
    //==============================================================================================
    // Метод для скачивания текущего изображения из общего массива
    // Передаваемые параметры
    // minDressImageWidth - минимальная ширина из всех изображений одежды, представленных в массиве
    // offsetHeightParam - смещение по вертикали для текущего изображения одежды
    // sumImageHeight - суммарная высота всех изображений для текущего набора
    private func downloadImage(minDressImageWidth: Int, offsetHeightParam: Float, sumImageHeight: Float) {
        if(self.getArrayDressImage() != nil) {
            // Индекс текущего обрбатываемого изображения
            let indexCurrentDressImage: Int = self.getCountDressImageLoaded()
        
            // Если индекс текущего обрабатываемого сообщения не выходит за границы массива
            if(indexCurrentDressImage >= 0 && indexCurrentDressImage < self.getArrayDressImage()!.count) {
                // Определяем коэффициент, во сколько раз необходимо уменьшить текущее изображение
                var k: Float = 1.0
        
                if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH] != nil && minDressImageWidth > 0) {
                    if(Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH]!) != nil) {
                        k = Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH]!)! / Float(minDressImageWidth)
                    }
                }
        
                // Определяем конечные ширину и высоту текущего изображения с учетом коэффициента масштабирования
                var targetImageWidth: Float = 0.0
        
                if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                    if(Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH]!) != nil) {
                        targetImageWidth = Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_WIDTH]!)! / k
                    }
                }
        
                var targetImageHeight: Float = 0.0
        
                if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                    if(Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_HEIGHT]!) != nil) {
                        targetImageHeight = Float(self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE_HEIGHT]!)! / k
                    }
                }
        
                // Задаем смещение для текущей одежды
                var offsetHeight: Float = offsetHeightParam + targetImageHeight
        
                // Если тип текущей одежды - "Низ", то делаем об этом соответствующую пометку
                if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_TYPE] != nil) {
                    // Если текущий тип одежды - "Низ"
                    if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG) {
                        self.setIsDressTypeLegExists(true)
                    }
                    // Иначе, если текущий тип одежды - "Верх"
                    else if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY) {
                        // Если одежда из типа "Низ" была отображена на виртуальном манекене
                        // то убираем из смещения для текущей одежды 20dp
                        if(self.getIsDressTypeLegExists() == true) {
                            offsetHeight -= 40
                        }
                    }
                }
        
                // Скачиваем непорседственно текущее изображение
                if (self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE] != nil) {
                    KingfisherManager.sharedManager.downloader.downloadImageWithURL(
                        NSURL(string: self.getArrayDressImage()![indexCurrentDressImage][GlobalFlags.TAG_IMAGE]!)!,
                        options: nil,
                        progressBlock: { (receivedSize, totalSize) -> () in
                    
                        },
                        completionHandler: { (image, error, cacheType, imageURL) -> () in
                            // Если в процессе загрузки не возникло никаких ошибок, то рисуем скачанное изображение
                            if(error == nil && image != nil) {
                                // Увеличиваем счетчик успешно скачанных изображений на +1
                                self.setCountDressImageLoaded(self.getCountDressImageLoaded() + 1)
                            
                                // Начинаем процесс рисования
                                let imageSizeFull: CGSize = CGSize(width: minDressImageWidth, height: Int(sumImageHeight))
                                let imageBoundsFull: CGRect = CGRect(origin: CGPoint.zero, size: imageSizeFull)
                                let imageOpaque: Bool = false
                                let imageScale: CGFloat = UIScreen.mainScreen().scale
                            
                                UIGraphicsBeginImageContextWithOptions(imageSizeFull, imageOpaque, imageScale)
                            
                                if(DialogShare.getViewDialogShare() != nil) {
                                    if(DialogShare.getViewDialogShare()!.imageViewDialogShareImage.image != nil) {
                                        DialogShare.getViewDialogShare()!.imageViewDialogShareImage.image!.drawInRect(imageBoundsFull)
                                    }
                                }
                           
                                // Рисуем непосредственно текущее изображение
                                let imageOrigin: CGPoint = CGPoint(x: 0, y: Int(sumImageHeight - offsetHeight))
                                let imageSize: CGSize = CGSize(width: Int(targetImageWidth), height: Int(targetImageHeight))
                                let imageBounds: CGRect = CGRect(origin: imageOrigin, size: imageSize)
                            
                                image!.drawInRect(imageBounds)
                                
                                // Завершаем процедуру рисования
                                let imageFinal: UIImage = UIGraphicsGetImageFromCurrentImageContext()
                            
                                UIGraphicsEndImageContext()
                                
                                if(DialogShare.getViewDialogShare() != nil) {
                                    DialogShare.getViewDialogShare()!.imageViewDialogShareImage.image = imageFinal
                                }
                            
                                // Скачиваем следующее изображение
                                self.downloadImage(minDressImageWidth, offsetHeightParam: offsetHeight, sumImageHeight: sumImageHeight)
                                
                                // Если это последнее изображения
                                if(self.getCountDressImageLoaded() == self.getArrayDressImage()!.count) {
                                    if(DialogShare.getViewDialogShare() != nil) {
                                        DialogShare.getViewDialogShare()!.imageViewDialogShareImage.hidden = false
                                        DialogShare.getViewDialogShare()!.activityIndicatorDialogShareImage.hidden = true
                                        DialogShare.getViewDialogShare()!.labelDialogShareImageNo.hidden = true
                                    }
                                }
                            }
                            // Иначе. если возникла ошибка при скачивании текущего изображения
                            else {
                                if(DialogShare.getViewDialogShare() != nil) {
                                    DialogShare.getViewDialogShare()!.imageViewDialogShareImage.hidden = true
                                    DialogShare.getViewDialogShare()!.activityIndicatorDialogShareImage.hidden = true
                                    DialogShare.getViewDialogShare()!.labelDialogShareImageNo.hidden = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    
    //==============================================================================================
    // Выполняем операцию скачивания изображений
    public func execute() {
        // Проверяем наличие Интернет-соединения
        let isInternetConnection: Bool = FunctionsConnection.isInternetConnection()
        
        // Если отсутствует Интернет-соединение, то выводим об этом соответствующее уведомление
        if(isInternetConnection != true) {
            // Выводим уведомление о том, что отсутствует Интернет-соединение
            if(ViewControllerMain.presentWindow != nil) {
                ViewControllerMain.presentWindow!.makeToast(message: GlobalFlagsStringsNotification.stringNoInternetConnectionToast, duration: 2, position: "center")
            }
        }
        
        //------------------------------------------------------------------------------------------
        // Стираем предыдущее изображение
        if(DialogShare.getViewDialogShare() != nil) {
            DialogShare.getViewDialogShare()!.imageViewDialogShareImage.image = nil
        }
        
        //------------------------------------------------------------------------------------------
        // Если успешно передан массив, содержащий информацию о необходимых изображениях
        if(self.getArrayDressImage() != nil) {
            // Определяем минимальную ширину среди всех изображений
            var minDressImageWidth: Int = 0
            
            // В цикле перебираем все изображения
            for indexImage in 0..<self.getArrayDressImage()!.count {
                // Ширина текущего изображения
                var currentDressImageWidth: Int = 0
                
                if (self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                    if(Int(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH]!) != nil) {
                        currentDressImageWidth = Int(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH]!)!
                    }
                }
                
                // Сверяем ширину изображения для текущей одежды с минимальным значением ширины
                if (minDressImageWidth == 0 || currentDressImageWidth < minDressImageWidth) {
                    minDressImageWidth = currentDressImageWidth
                }
            }
            
            //--------------------------------------------------------------------------------------
            // Определяем суммарную высоту всех изображений
            var sumImageHeight: Float = 0
            
            // В цикле перебираем все изображения
            for indexImage in 0..<self.getArrayDressImage()!.count {
                // Определяем коэффициент, во сколько раз необходимо уменьшить текущее изображение
                var k: Float = 1.0
                
                if (self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH] != nil && minDressImageWidth > 0) {
                    if(Float(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH]!) != nil) {
                        k = Float(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_WIDTH]!)! / Float(minDressImageWidth)
                    }
                }
                    
                // Определяем конечную высоту текущего изображения с учетом коэффициента масштабирования
                var targetImageHeight: Float = 0.0
                
                if (self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                    if(Float(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_HEIGHT]!) != nil) {
                        targetImageHeight = Float(self.getArrayDressImage()![indexImage][GlobalFlags.TAG_IMAGE_HEIGHT]!)! / k
                    }
                }
                    
                // Прибавляем конечную высоту текущего изображения к суммарной высоте всех изображений
                sumImageHeight += targetImageHeight
            }
            
            //------------------------------------------------------------------------------
            // Загружаем первое изображение из массива
            self.downloadImage(minDressImageWidth, offsetHeightParam: 0, sumImageHeight: sumImageHeight)
        }
    }
}