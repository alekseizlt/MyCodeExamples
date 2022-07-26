import Foundation
import CoreData
import UIKit

// Класс, предназначенный для создания и обновления локальной БД SQLite
public class DBSQLiteHelper: NSObject, NSFetchedResultsControllerDelegate {
    
    // Контроллер, содержащий результат выполнения запрос к локальной БД
    var mFetchedResultsController: NSFetchedResultsController?
    
    //==============================================================================================
    // Метод для считывания ссылки на контроллер, содержащий результат выполнения запрос к локальной БД
    private func getFetchedResultsController() -> NSFetchedResultsController? {
        return self.mFetchedResultsController
    }
    
    //==============================================================================================
    // Метод для задания ссылки на контроллер, содержащий результат выполнения запрос к локальной БД
    private func setFetchedResultsController(fetchedResultsController: NSFetchedResultsController?) {
        self.mFetchedResultsController = fetchedResultsController
    }
    
    //==============================================================================================
    // Конструктор
    override init() {
        
    }
    
    //==============================================================================================
    // Метод для считывания строк из таблицы в БД MySQL на сервере согласно условиям выборки
    // Передаваемые параметры
    // tableName - название таблицы
    // selection - условие выборки
    // orderBy - столбцы, по которым осуществляется сортировка
    // ascendingOrderBy - направление сортировки по каждому из столбцов
    // limit количество строк, которые необходимо считать из локальной БД
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public func getRecordsFromDB(tableName: String, selection: NSPredicate, orderBy: [String], ascendingOrderBy: [Bool], limit: Int) -> [AnyObject]? {
        // Возвращаемый массив объектов
        var returnArrayObjects: [AnyObject]?
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        var sortDescriptors: [NSSortDescriptor] = [NSSortDescriptor]()
        
        if(orderBy.count == ascendingOrderBy.count) {
            for indexOrderBy in 0..<orderBy.count {
                let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: orderBy[indexOrderBy], ascending: ascendingOrderBy[indexOrderBy])
                sortDescriptors.append(sortDescriptor)
            }
        }
        
        fetchRequest.sortDescriptors = sortDescriptors
        
        // Задаем условия выборки
        fetchRequest.predicate = selection
        
        // Задаем количество извлекаемых строк из таблицы
        if(limit > 0) {
            fetchRequest.fetchLimit = limit
        }
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов в зависимости
                returnArrayObjects = self.getFetchedResultsController()!.fetchedObjects
            }
        }
        
        return returnArrayObjects
    }
    
    //==============================================================================================
    // Метод для считывания строки из таблицы по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public func getRecordFromDBByIdServerMySQL(tableName: String, idServerMySQL: Int) -> AnyObject? {
        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        var objectFromDB: AnyObject?
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = NSPredicate(format: GlobalFlags.TAG_ID + " = %d", argumentArray: [idServerMySQL])
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        objectFromDB = self.getFetchedResultsController()!.fetchedObjects![0]
                    }
                }
            }
        }
        
        return objectFromDB
    }
    
    //==============================================================================================
    // Метод для считывания одной (первой) строки из таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public func getFirstRecordFromDB(tableName: String) -> AnyObject? {
        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        var objectFromDB: AnyObject?
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        objectFromDB = self.getFetchedResultsController()!.fetchedObjects![0]
                    }
                }
            }
        }
        
        return objectFromDB
    }
    
    //==============================================================================================
    // Метод для считывания всех строк из таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // orderBy - сортировка столбцов
    // ascendingOrderBy - направление сортировки
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public func getAllRecordsFromDB(tableName: String, orderBy: [String]?, ascendingOrderBy: [Bool]?) -> [AnyObject]? {
        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        var objectFromDB: [AnyObject]?
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        if(orderBy != nil && ascendingOrderBy != nil) {
            var sortDescriptors: [NSSortDescriptor] = [NSSortDescriptor]()
        
            for indexOrderBy in 0..<orderBy!.count {
                let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: orderBy![indexOrderBy], ascending: ascendingOrderBy![indexOrderBy])
                sortDescriptors.append(sortDescriptor)
            }
        
            fetchRequest.sortDescriptors = sortDescriptors
        }
            
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    objectFromDB = self.getFetchedResultsController()!.fetchedObjects
                }
            }
        }
        
        return objectFromDB
    }
    
    //==============================================================================================
    // Метод для считывания версии информации о текущей записи из таблицы по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возращаемое значение
    // version - версии информации о текущей записи из таблицы по id данной строки в БД MySQL на сервере
    public func getRecordVersionFromDBByIdServerMySQL(tableName: String, idServerMySQL: Int) -> Int {
        // Возвращаемая переменная, содержащая версию информации о текущей записи из таблицы
        // по id данной строки в БД MySQL на сервере
        var version: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = NSPredicate(format: GlobalFlags.TAG_ID + " = %d", argumentArray: [idServerMySQL])
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        // В зависимости от типа таблицы извлекаем искомое значение из соответствующего объекта
                        switch(tableName) {
                            case GlobalFlags.TAG_TABLE_USER:
                                let currentObjectUser: User = self.getFetchedResultsController()!.fetchedObjects![0] as! User
                                version = Int(currentObjectUser.version)
                                break
                            case GlobalFlags.TAG_TABLE_CATEGORIES:
                                let currentObjectCategories: Categories = self.getFetchedResultsController()!.fetchedObjects![0] as! Categories
                                version = Int(currentObjectCategories.version)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS:
                                let currentObjectDress: Dress = self.getFetchedResultsController()!.fetchedObjects![0] as! Dress
                                version = Int(currentObjectDress.version)
                                break
                            case GlobalFlags.TAG_TABLE_BRAND:
                                let currentObjectBrand: Brand = self.getFetchedResultsController()!.fetchedObjects![0] as! Brand
                                version = Int(currentObjectBrand.version)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY:
                                let currentObjectCompany: Company = self.getFetchedResultsController()!.fetchedObjects![0] as! Company
                                version = Int(currentObjectCompany.version)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP:
                                let currentObjectShop: Shop = self.getFetchedResultsController()!.fetchedObjects![0] as! Shop
                                version = Int(currentObjectShop.version)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION:
                                let currentObjectCollection: Collection = self.getFetchedResultsController()!.fetchedObjects![0] as! Collection
                                version = Int(currentObjectCollection.version)
                                break
                            default:
                                break
                        }
                    }
                }
            }
        }
        
        return version
    }
    
    //==============================================================================================
    // Метод для определения минимального значения порядкового номера присутствующей в данной таблице строки
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // minRecordNumber - минимальное значение порядкового номера присутствующей в данной таблице строки
    public func getMinRecordNumber(tableName: String) -> Int {
        // Возвращаемый результат, минимальное значение порядкового номера присутствующей в данной таблице строки
        var minRecordNumber: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_RECORD_NUMBER, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        // В зависимости от типа таблицы извлекаем искомое значение из соответствующего объекта
                        switch(tableName) {
                            case GlobalFlags.TAG_TABLE_USER:
                                let currentObjectUser: User = self.getFetchedResultsController()!.fetchedObjects![0] as! User
                                minRecordNumber = Int(currentObjectUser.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                                let currentObjectDressInUserCollection: DressInUserCollection = self.getFetchedResultsController()!.fetchedObjects![0] as! DressInUserCollection
                                minRecordNumber = Int(currentObjectDressInUserCollection.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_CATEGORIES:
                                let currentObjectCategories: Categories = self.getFetchedResultsController()!.fetchedObjects![0] as! Categories
                                minRecordNumber = Int(currentObjectCategories.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS:
                                let currentObjectDress: Dress = self.getFetchedResultsController()!.fetchedObjects![0] as! Dress
                                minRecordNumber = Int(currentObjectDress.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                                let currentObjectDressImage: DressImage = self.getFetchedResultsController()!.fetchedObjects![0] as! DressImage
                                minRecordNumber = Int(currentObjectDressImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_BRAND:
                                let currentObjectBrand: Brand = self.getFetchedResultsController()!.fetchedObjects![0] as! Brand
                                minRecordNumber = Int(currentObjectBrand.record_number)
                                break
                            case GlobalFlags.TAG_BRAND_IMAGE:
                                let currentObjectBrandImage: BrandImage = self.getFetchedResultsController()!.fetchedObjects![0] as! BrandImage
                                minRecordNumber = Int(currentObjectBrandImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY:
                                let currentObjectCompany: Company = self.getFetchedResultsController()!.fetchedObjects![0] as! Company
                                minRecordNumber = Int(currentObjectCompany.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                                let currentObjectCompanyImage: CompanyImage = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyImage
                                minRecordNumber = Int(currentObjectCompanyImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                                let currentObjectCompanyBrand: CompanyBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyBrand
                                minRecordNumber = Int(currentObjectCompanyBrand.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP:
                                let currentObjectShop: Shop = self.getFetchedResultsController()!.fetchedObjects![0] as! Shop
                                minRecordNumber = Int(currentObjectShop.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                                let currentObjectShopBrand: ShopBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopBrand
                                minRecordNumber = Int(currentObjectShopBrand.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                                let currentObjectShopDress: ShopDress = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopDress
                                minRecordNumber = Int(currentObjectShopDress.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                                let currentObjectShopImage: ShopImage = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopImage
                                minRecordNumber = Int(currentObjectShopImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION:
                                let currentObjectCollection: Collection = self.getFetchedResultsController()!.fetchedObjects![0] as! Collection
                                minRecordNumber = Int(currentObjectCollection.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                                let currentObjectCollectionDress: CollectionDress = self.getFetchedResultsController()!.fetchedObjects![0] as! CollectionDress
                                minRecordNumber = Int(currentObjectCollectionDress.record_number)
                                break
                            default:
                                break
                        }
                    }
                }
            }
        }
        
        return minRecordNumber
    }
    
    //==============================================================================================
    // Метод для определения максимального значения порядкового номера присутствующей в данной таблице строки
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // maxRecordNumber - максимальное значение порядкового номера присутствующей в данной таблице строки
    public func getMaxRecordNumber(tableName: String) -> Int {
        // Возвращаемый результат, максимальное значение порядкового номера присутствующей в данной таблице строки
        var maxRecordNumber: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_RECORD_NUMBER, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        // В зависимости от типа таблицы извлекаем искомое значение из соответствующего объекта
                        switch(tableName) {
                            case GlobalFlags.TAG_TABLE_USER:
                                let currentObjectUser: User = self.getFetchedResultsController()!.fetchedObjects![0] as! User
                                maxRecordNumber = Int(currentObjectUser.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                                let currentObjectDressInUserCollection: DressInUserCollection = self.getFetchedResultsController()!.fetchedObjects![0] as! DressInUserCollection
                                maxRecordNumber = Int(currentObjectDressInUserCollection.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_CATEGORIES:
                                let currentObjectCategories: Categories = self.getFetchedResultsController()!.fetchedObjects![0] as! Categories
                                maxRecordNumber = Int(currentObjectCategories.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS:
                                let currentObjectDress: Dress = self.getFetchedResultsController()!.fetchedObjects![0] as! Dress
                                maxRecordNumber = Int(currentObjectDress.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                                let currentObjectDressImage: DressImage = self.getFetchedResultsController()!.fetchedObjects![0] as! DressImage
                                maxRecordNumber = Int(currentObjectDressImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_BRAND:
                                let currentObjectBrand: Brand = self.getFetchedResultsController()!.fetchedObjects![0] as! Brand
                                maxRecordNumber = Int(currentObjectBrand.record_number)
                                break
                            case GlobalFlags.TAG_BRAND_IMAGE:
                                let currentObjectBrandImage: BrandImage = self.getFetchedResultsController()!.fetchedObjects![0] as! BrandImage
                                maxRecordNumber = Int(currentObjectBrandImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY:
                                let currentObjectCompany: Company = self.getFetchedResultsController()!.fetchedObjects![0] as! Company
                                maxRecordNumber = Int(currentObjectCompany.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                                let currentObjectCompanyImage: CompanyImage = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyImage
                                maxRecordNumber = Int(currentObjectCompanyImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                                let currentObjectCompanyBrand: CompanyBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyBrand
                                maxRecordNumber = Int(currentObjectCompanyBrand.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP:
                                let currentObjectShop: Shop = self.getFetchedResultsController()!.fetchedObjects![0] as! Shop
                                maxRecordNumber = Int(currentObjectShop.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                                let currentObjectShopBrand: ShopBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopBrand
                                maxRecordNumber = Int(currentObjectShopBrand.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                                let currentObjectShopDress: ShopDress = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopDress
                                maxRecordNumber = Int(currentObjectShopDress.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                                let currentObjectShopImage: ShopImage = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopImage
                                maxRecordNumber = Int(currentObjectShopImage.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION:
                                let currentObjectCollection: Collection = self.getFetchedResultsController()!.fetchedObjects![0] as! Collection
                                maxRecordNumber = Int(currentObjectCollection.record_number)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                                let currentObjectCollectionDress: CollectionDress = self.getFetchedResultsController()!.fetchedObjects![0] as! CollectionDress
                                maxRecordNumber = Int(currentObjectCollectionDress.record_number)
                                break
                            default:
                                break
                        }
                    }
                }
            }
        }
        
        return maxRecordNumber
    }
    
    //==============================================================================================
    // Метод для определения количетсва строк из таблицы id данных строк в БД MySQL на сервере,
    // которых равен значения, переданному в качестве параметра idServerMySQL для данной функции
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public func getCountRecordFromDBByIdServerMySQL(tableName: String, idServerMySQL: Int) -> Int {
        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        var countRows: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = NSPredicate(format: GlobalFlags.TAG_ID + " = %d", argumentArray: [idServerMySQL])
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    countRows = self.getFetchedResultsController()!.fetchedObjects!.count
                }
            }
        }
        
        return countRows
    }
    
    //==============================================================================================
    // Метод для определения количетсва строк из таблицы, удовлетворяющих условиям выборки
    // Передаваемые параметры
    // tableName - название таблицы
    // selection - условие выборки
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public func getCountRecordFromDB(tableName: String, selection: NSPredicate) -> Int {
        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        var countRows: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = selection
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
      
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    countRows = self.getFetchedResultsController()!.fetchedObjects!.count
                }
            }
        }
        
        return countRows
    }
    
    //==============================================================================================
    // Метод для определения общего количества строк из таблицы
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public func getTotalCountRecord(tableName: String) -> Int {
        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        var countRows: Int = 0
        
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
        
                // Возвращаем массив объектов в зависимости
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    countRows = self.getFetchedResultsController()!.fetchedObjects!.count
                }
            }
        }
        
        return countRows
    }
    
    //==============================================================================================
    // Метод для вставки новой строки в таблицу
    // Передаваемые параметры
    // tableName - название таблицы
    // insertFieldsValues - ассоциативный массив, значения, которые должны быть вставлены в таблицу tableName
    //                      В данном ассоциативном массиве:
    //                      ключ - название столбца
    //                      значение - значение соответствующего столбца
    // Возвращаемое значение
    // new_row_id - id только что добавленой в таблицу строки
    public func insertRecordToDB(tableName: String, insertFieldsValues: Dictionary<String, String>) {
        // Проверяем существует ли такая запись уже в таблице
        // Определять наличие будем по полю "id", хранящем id данной записи в БД MySQL на сервере
        var countRows: Int = 0
        
        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
            countRows = self.getCountRecordFromDBByIdServerMySQL(tableName, idServerMySQL: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
        }
        
        //---------------------------------------------------------------------------------------
        if (countRows <= 0) {
            // Определяем общее количество строк в данной таблице
            let totalCountRecordInCurrentTable: Int = self.getTotalCountRecord(tableName)
            
            //----------------------------------------------------------------------------------
            // Если количество строк в данной таблице превысило допустимое число,
            // то удаляем строки с наименьшим значением порядкового номера
            if (GlobalFlags.getArrayMaxCountRowsInTable()[tableName] != nil) {
                if (totalCountRecordInCurrentTable >= GlobalFlags.getArrayMaxCountRowsInTable()[tableName]) {
                    // Определяем минимальное значение порядкового номера строки в данной таблице
                    let minRecordNumberInCurrentTable = self.getMinRecordNumber(tableName)
                        
                    // Удаляем из текущий таблицы строки с минимальным значением порядкового номера строки
                    self.deleteRecordFromDB(tableName, selection: NSPredicate(format: GlobalFlags.TAG_RECORD_NUMBER + " = %d", argumentArray: [minRecordNumberInCurrentTable]))
                }
            }
        
            //------------------------------------------------------------------------------------
            // Вставляем непосредственно данные в таблицу
            if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
                // В зависимости от названия таблицы выполняем соответствующие действия
                switch (tableName) {
                    case GlobalFlags.TAG_TABLE_BRAND:
                        let brand: Brand = NSEntityDescription.insertNewObjectForEntityForName("Brand", inManagedObjectContext: managedObjectContext) as! Brand
                
                        // Заполняем информацию о текущем объекте бренда
                
                        // id текущего бренда
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            brand.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            brand.id = 0
                        }
                
                        // название текущего бренда одежды
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            brand.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            brand.title = ""
                        }
                
                        // алиас названия текущего бренда одежды
                        brand.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                
                        // изображение для текущего бренда
                        brand.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]
                
                        // краткое описание текущего бренда одежды
                        brand.short_desc = insertFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                    
                        // описание текущего бренда одежды
                        brand.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                    
                        // рейтинг текущего бренда одежды
                        var brandHits: Int = 0
                    
                        if(insertFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_HITS]!) >= 0) {
                                brandHits = Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)!
                            }
                        }
                    
                        brand.hits = NSNumber(integer: brandHits)

                        // версия информации о текущей одежде
                        var brandVersion: Int = 1
                    
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                brandVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                    
                        brand.version = NSNumber(integer: brandVersion)
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        brand.record_number = NSNumber(integer: recordNumberForCurrentRow)
                
                        break
                
                    case GlobalFlags.TAG_TABLE_BRAND_IMAGE:
                        let brandImage: BrandImage = NSEntityDescription.insertNewObjectForEntityForName("BrandImage", inManagedObjectContext: managedObjectContext) as! BrandImage
                
                        // Заполняем информацию о текущем объекте

                        // id текущего бренда одежды
                        if(insertFieldsValues[GlobalFlags.TAG_BRAND_ID] != nil) {
                            brandImage.brand_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_BRAND_ID]!)!)
                        }
                        else {
                            brandImage.brand_id = 0
                        }

                        // ссылка на файл-изображение для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                            brandImage.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]!
                        }
                        else {
                            brandImage.image = ""
                        }
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        brandImage.record_number = NSNumber(integer: recordNumberForCurrentRow)

                        break

                    case GlobalFlags.TAG_TABLE_CATEGORIES:
                        let category: Categories = NSEntityDescription.insertNewObjectForEntityForName("Categories", inManagedObjectContext: managedObjectContext) as! Categories
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущей категории одежды в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            category.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            category.id = 0
                        }
                        
                        // название текущей категории одежды
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            category.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            category.title = ""
                        }
                        
                        // алиас для названия текущей категории одежды
                        category.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                        
                        // для кого предназначена одежда (для мужчины, для женщины или для детей) из данной категории
                        var categoryForWho: String = GlobalFlags.TAG_DRESS_MAN
                        
                        if(insertFieldsValues[GlobalFlags.TAG_FOR_WHO] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_MAN || insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_WOMAN || insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_KID) {
                                categoryForWho = insertFieldsValues[GlobalFlags.TAG_FOR_WHO]!
                            }
                        }
                        
                        category.for_who = categoryForWho
                        
                        // тип одежды (головные уборы, обувь и т.д.) из данной категории
                        var categoryType: String = GlobalFlags.TAG_DRESS_HEAD
                        
                        if(insertFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_HEAD || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_FOOT || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_ACCESSORY) {
                                categoryType = insertFieldsValues[GlobalFlags.TAG_TYPE]!
                            }
                        }
                        
                        category.type = categoryType
                        
                        // уровень вложенности текущей категории одежды
                        if(insertFieldsValues[GlobalFlags.TAG_LEVEL] != nil) {
                            category.level = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_LEVEL]!)!)
                        }
                        else {
                            category.level = 0
                        }
                        
                        // id родительской категории для текущей категории одежды
                        if(insertFieldsValues[GlobalFlags.TAG_PARENT_ID] != nil) {
                            category.parent_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_PARENT_ID]!)!)
                        }
                        else {
                            category.parent_id = 0
                        }
                        
                        // описание текущей категории одежды
                        category.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                        
                        // версия информации о текущей категории одежды
                        var categoryVersion: Int = 1
                        
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                categoryVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                        
                        category.version = NSNumber(integer: categoryVersion)
                        
                        // количество вещей (одежды) для текущей категории
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_COUNT] != nil) {
                            category.dress_count = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_DRESS_COUNT]!)!)
                        }
                        else {
                            category.dress_count = 0
                        }
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        category.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_COLLECTION:
                        let collection: Collection = NSEntityDescription.insertNewObjectForEntityForName("Collection", inManagedObjectContext: managedObjectContext) as! Collection
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущего набора одежды в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            collection.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            collection.id = 0
                        }
                        
                        // название текущего набора одежды
                        collection.title = insertFieldsValues[GlobalFlags.TAG_TITLE]
                        
                        // алиас названия текущего набора одежды
                        collection.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                        
                        // тип текущего набора одежды (коллекция или одежда)
                        var collectionType: String = GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION
                        
                        if(insertFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.DRESS_COLLECTION_TYPE_DRESS || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION) {
                                collectionType = insertFieldsValues[GlobalFlags.TAG_TYPE]!
                            }
                        }
                        
                        collection.type = collectionType
                        
                        // краткое описание текущего набора одежды
                        collection.short_desc = insertFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                        
                        // описание текущего набора одежды
                        collection.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                        
                        // версия информации о текущем наборе одежды
                        var collectionVersion: Int = 1
                        
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                collectionVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                        
                        collection.version = NSNumber(integer: collectionVersion)
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        collection.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                        let collectionDress: CollectionDress = NSEntityDescription.insertNewObjectForEntityForName("CollectionDress", inManagedObjectContext: managedObjectContext) as! CollectionDress
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущего набора одежды
                        if(insertFieldsValues[GlobalFlags.TAG_COLLECTION_ID] != nil) {
                            collectionDress.collection_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_COLLECTION_ID]!)!)
                        }
                        else {
                            collectionDress.collection_id = 0
                        }
                        
                        // id текущей вещи (одежды), входящей в состав данного набора одежды
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_ID] != nil) {
                            collectionDress.dress_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_DRESS_ID]!)!)
                        }
                        else {
                            collectionDress.dress_id = 0
                        }
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        collectionDress.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_COMPANY:
                        let company: Company = NSEntityDescription.insertNewObjectForEntityForName("Company", inManagedObjectContext: managedObjectContext) as! Company
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущей компании-производителя одежды в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            company.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            company.id = 0
                        }
                        
                        // название текущей компании-производителя одежды
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            company.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            company.title = ""
                        }
                        
                        // алиас названия текущей компании-производителя одежды
                        company.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                        
                        // изображение-логотип для текущей компании-производителя одежды
                        company.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]
                        
                        // краткое описание текущей компании-производителя одежды
                        company.short_desc = insertFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                        
                        // описание текущей компании-производителя одежды
                        company.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                        
                        // рейтинг текущей компании-производителя одежды
                        var companyHits: Int = 0
                        
                        if(insertFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                companyHits = Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)!
                            }
                        }
                        
                        company.hits = companyHits
                        
                        // почтовый индекс месторасположения головного офиса текущей компании-производителя одежды
                        company.postcode = insertFieldsValues[GlobalFlags.TAG_POSTCODE]
                        
                        // страна месторасположения головного офиса текущей компании-производителя одежды
                        company.country = insertFieldsValues[GlobalFlags.TAG_COUNTRY]
                        
                        // код страны месторасположения головного офиса текущей компании-производителя одежды
                        company.country_code = insertFieldsValues[GlobalFlags.TAG_COUNTRY_CODE]
                        
                        // регион (область, штат) месторасположения головного офиса текущей компании-производителя одежды
                        company.region = insertFieldsValues[GlobalFlags.TAG_REGION]
                        
                        // город месторасположения головного офиса текущей компании-производителя одежды
                        company.city = insertFieldsValues[GlobalFlags.TAG_CITY]
                        
                        // улица месторасположения головного офиса текущей компании-производителя одежды
                        company.street = insertFieldsValues[GlobalFlags.TAG_STREET]
                        
                        // номер дома (здания) месторасположения головного офиса текущей компании-производителя одежды
                        company.building = insertFieldsValues[GlobalFlags.TAG_BUILDING]
                        
                        // номера телефонов головного офиса текущей компании-производителя одежды
                        company.telephone = insertFieldsValues[GlobalFlags.TAG_TELEPHONE]
                        
                        // мобильные номера телефонов головного офиса текущей компании-производителя одежды
                        company.mobile = insertFieldsValues[GlobalFlags.TAG_MOBILE]
                        
                        // номер факса головного офиса текущей компании-производителя одежды
                        company.fax = insertFieldsValues[GlobalFlags.TAG_FAX]
                        
                        // адрес сайта текущей компании-производителя одежды
                        company.webpage = insertFieldsValues[GlobalFlags.TAG_WEBPAGE]
                        
                        // адрес электронной почты текущей компании-производителя одежды
                        company.email_to = insertFieldsValues[GlobalFlags.TAG_EMAIL]
                        
                        // версия информации о текущей компании-производителе одежды
                        var companyVersion: Int = 1
                        
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                companyVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                        
                        company.version = NSNumber(integer: companyVersion)
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        company.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                        let companyBrand: CompanyBrand = NSEntityDescription.insertNewObjectForEntityForName("CompanyBrand", inManagedObjectContext: managedObjectContext) as! CompanyBrand
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущей компании-производителя одежды
                        if(insertFieldsValues[GlobalFlags.TAG_COMPANY_ID] != nil) {
                            companyBrand.company_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_COMPANY_ID]!)!)
                        }
                        else {
                            companyBrand.company_id = 0
                        }
                        
                        // id текущего бренда одежды, выпускаемого данной компанией-производителем одежды
                        if(insertFieldsValues[GlobalFlags.TAG_BRAND_ID] != nil) {
                            companyBrand.brand_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_BRAND_ID]!)!)
                        }
                        else {
                            companyBrand.brand_id = 0
                        }
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        companyBrand.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                        let companyImage: CompanyImage = NSEntityDescription.insertNewObjectForEntityForName("CompanyImage", inManagedObjectContext: managedObjectContext) as! CompanyImage
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущей компании-производителя одежды
                        if(insertFieldsValues[GlobalFlags.TAG_COMPANY_ID] != nil) {
                            companyImage.company_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_COMPANY_ID]!)!)
                        }
                        else {
                            companyImage.company_id = 0
                        }
                    
                        // ссылка на файл-изображение для текущей компании-производителя одежды
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                            companyImage.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]!
                        }
                        else {
                            companyImage.image = ""
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        companyImage.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_DRESS:
                        let dress: Dress = NSEntityDescription.insertNewObjectForEntityForName("Dress", inManagedObjectContext: managedObjectContext) as! Dress
                        
                        // Заполняем информацию о текущем объекте
                        
                        // id текущей вещи в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            dress.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            dress.id = 0
                        }
                        
                        // id категории, к которой относится текущая вещь (одежда)
                        if(insertFieldsValues[GlobalFlags.TAG_CATID] != nil) {
                            dress.catid = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_CATID]!)!)
                        }
                        else {
                            dress.catid = 0
                        }
                        
                        // название текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            dress.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            dress.title = ""
                        }
                        
                        // алиас для названия текущей вещи (одежды)
                        dress.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                        
                        // для кого предназначена данная вещь (одежда) (для мужчины, для женщины или для детей)
                        var dressForWho: String = GlobalFlags.TAG_DRESS_MAN
                        
                        if(insertFieldsValues[GlobalFlags.TAG_FOR_WHO] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_MAN || insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_WOMAN || insertFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_KID) {
                                dressForWho = insertFieldsValues[GlobalFlags.TAG_FOR_WHO]!
                            }
                        }
                        
                        dress.for_who = dressForWho
                        
                        // тип текущей вещи (одежды) (для мужчины, для женщины или для детей)
                        var dressType: String = GlobalFlags.TAG_DRESS_HEAD
                        
                        if(insertFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_HEAD || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_FOOT || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_ACCESSORY) {
                                dressType = insertFieldsValues[GlobalFlags.TAG_TYPE]!
                            }
                        }
                        
                        dress.type = dressType
                        
                        // id бренда, к которому относится текущая вещь (одежда)
                        if(insertFieldsValues[GlobalFlags.TAG_BRAND_ID] != nil) {
                            dress.brand_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_BRAND_ID]!)!)
                        }
                        else {
                            dress.brand_id = 0
                        }
                        
                        // ссылка на файл-превью-изображение для текущей вещи (одежды)
                        dress.thumb = insertFieldsValues[GlobalFlags.TAG_THUMB]
                        
                        // ширина файла-превью-изображения для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_THUMB_WIDTH] != nil) {
                            dress.thumb_width = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_THUMB_WIDTH]!)!)
                        }
                        else {
                            dress.thumb_width = 0
                        }
                        
                        // высота файла-превью-изображения для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_THUMB_HEIGHT] != nil) {
                            dress.thumb_height = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_THUMB_HEIGHT]!)!)
                        }
                        else {
                            dress.thumb_height = 0
                        }
                        
                        // ссылка на файл-изображение для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                            dress.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]!
                        }
                        else {
                            dress.image = ""
                        }
                        
                        // ширина (в пикселях) файла-изображение для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                            dress.image_width = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_IMAGE_WIDTH]!)!)
                        }
                        else {
                            dress.image_width = 0
                        }
                        
                        // высота (в пикселях) файла-изображение для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                            dress.image_height = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_IMAGE_HEIGHT]!)!)
                        }
                        else {
                            dress.image_height = 0
                        }
                        
                        // ссылка на файл-изображение для задней (тыловой) стороны текущей вещи (одежды)
                        dress.image_back = insertFieldsValues[GlobalFlags.TAG_IMAGE_BACK]
                        
                        // ширина (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                            dress.image_back_width = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!)!)
                        }
                        else {
                            dress.image_back_width = 0
                        }
                        
                        // высота (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                            dress.image_back_height = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!)!)
                        }
                        else {
                            dress.image_back_height = 0
                        }
                        
                        // цвет текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_COLOR] != nil) {
                            dress.color = insertFieldsValues[GlobalFlags.TAG_COLOR]!
                        }
                        else {
                            dress.color = ""
                        }
                        
                        // стиль текущей вещи (одежды)
                        dress.style = insertFieldsValues[GlobalFlags.TAG_STYLE]
                        
                        // краткое описание текущей вещи (одежды)
                        dress.short_desc = insertFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                        
                        // описание текущей вещи (одежды)
                        dress.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                        
                        // рейтинг текущей вещи (одежды)
                        var dressHits: Int = 0
                        
                        if(insertFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                dressHits = Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)!
                            }
                        }
                        
                        dress.hits = dressHits
                        
                        // логическая переменная, определяющая отображать ли данную вещь по умолчанию при первоначальном заходе пользователя на страницу
                        var dressDefault: Int = 0
                        
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT]!) > 0) {
                                dressDefault = Int(insertFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT]!)!
                            }
                        }
                        
                        dress.dress_default = dressDefault
                        
                        // версия информации о текущей одежде
                        var dressVersion: Int = 1
                        
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                dressVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                        
                        dress.version = NSNumber(integer: dressVersion)
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        dress.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
                    
                    case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                        let dressImage: DressImage = NSEntityDescription.insertNewObjectForEntityForName("DressImage", inManagedObjectContext: managedObjectContext) as! DressImage
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_ID] != nil) {
                            dressImage.dress_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_DRESS_ID]!)!)
                        }
                        else {
                            dressImage.dress_id = 0
                        }
                    
                        // ссылка на файл-изображение для текущей вещи (одежды)
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                            dressImage.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]!
                        }
                        else {
                            dressImage.image = ""
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        dressImage.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                        let dressInUserCollection: DressInUserCollection = NSEntityDescription.insertNewObjectForEntityForName("DressInUserCollection", inManagedObjectContext: managedObjectContext) as! DressInUserCollection
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущей категории одежды в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_CATID] != nil) {
                            dressInUserCollection.catid = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_CATID]!)!)
                        }
                        else {
                            dressInUserCollection.catid = 0
                        }
                    
                        // название текущей категории одежды
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            dressInUserCollection.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            dressInUserCollection.title = ""
                        }
                        
                        // алиас для названия текущей категории одежды
                        dressInUserCollection.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                        
                        // тип одежды (головные уборы, обувь и т.д.) для данной категории
                        var dressInUserCollectionType: String = GlobalFlags.TAG_DRESS_HEAD
                        
                        if(insertFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_HEAD || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_FOOT || insertFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_ACCESSORY) {
                                dressInUserCollectionType = insertFieldsValues[GlobalFlags.TAG_TYPE]!
                            }
                        }
                        
                        dressInUserCollection.type = dressInUserCollectionType
                        
                        
                        // количество вещей (одежды) для текущей категории
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_COUNT] != nil) {
                            dressInUserCollection.dress_count = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_DRESS_COUNT]!)!)
                        }
                        else {
                            dressInUserCollection.dress_count = 0
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        dressInUserCollection.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_SHOP:
                        let shop: Shop = NSEntityDescription.insertNewObjectForEntityForName("Shop", inManagedObjectContext: managedObjectContext) as! Shop
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущего магазина одежды в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_ID] != nil) {
                            shop.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_ID]!)!)
                        }
                        else {
                            shop.id = 0
                        }
                    
                        // название текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                            shop.title = insertFieldsValues[GlobalFlags.TAG_TITLE]!
                        }
                        else {
                            shop.title = ""
                        }
                    
                        // алиас названия текущего магазина одежды
                        shop.alias = insertFieldsValues[GlobalFlags.TAG_ALIAS]
                    
                        // изображение-логотип для текущего магазина одежды
                        shop.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]
                    
                        // краткое описание текущего магазина одежды
                        shop.short_desc = insertFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                    
                        // описание текущего магазина одежды
                        shop.desc = insertFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                    
                        // рейтинг текущего магазина одежды
                        var shopHits: Int = 0
                    
                        if(insertFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                shopHits = Int(insertFieldsValues[GlobalFlags.TAG_HITS]!)!
                            }
                        }
                    
                        shop.hits = shopHits
                        
                        // широта местоположения текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_LATITUDE] != nil) {
                            shop.latitude = NSNumber(double: Double(insertFieldsValues[GlobalFlags.TAG_LATITUDE]!)!)
                        }
                        else {
                            shop.latitude = 0
                        }
                        
                        // долгота местоположения текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_LONGITUDE] != nil) {
                            shop.longitude = NSNumber(double: Double(insertFieldsValues[GlobalFlags.TAG_LONGITUDE]!)!)
                        }
                        else {
                            shop.longitude = 0
                        }
                    
                        // почтовый индекс месторасположения текущего магазина одежды
                        shop.postcode = insertFieldsValues[GlobalFlags.TAG_POSTCODE]
                    
                        // страна месторасположения текущего магазина одежды
                        shop.country = insertFieldsValues[GlobalFlags.TAG_COUNTRY]
                    
                        // код страны месторасположения текущего магазина одежды
                        shop.country_code = insertFieldsValues[GlobalFlags.TAG_COUNTRY_CODE]
                    
                        // регион (область, штат) месторасположения текущего магазина одежды
                        shop.region = insertFieldsValues[GlobalFlags.TAG_REGION]
                    
                        // город месторасположения текущего магазина одежды
                        shop.city = insertFieldsValues[GlobalFlags.TAG_CITY]
                    
                        // улица месторасположения текущего магазина одежды
                        shop.street = insertFieldsValues[GlobalFlags.TAG_STREET]
                    
                        // номер дома (здания) месторасположения текущего магазина одежды
                        shop.building = insertFieldsValues[GlobalFlags.TAG_BUILDING]
                    
                        // номера телефонов текущего магазина одежды
                        shop.telephone = insertFieldsValues[GlobalFlags.TAG_TELEPHONE]
                    
                        // мобильные номера телефонов текущего магазина одежды
                        shop.mobile = insertFieldsValues[GlobalFlags.TAG_MOBILE]
                    
                        // номер факса текущего магазина одежды
                        shop.fax = insertFieldsValues[GlobalFlags.TAG_FAX]
                    
                        // адрес сайта текущего магазина одежды
                        shop.webpage = insertFieldsValues[GlobalFlags.TAG_WEBPAGE]
                    
                        // адрес электронной почты текущего магазина одежды
                        shop.email_to = insertFieldsValues[GlobalFlags.TAG_EMAIL]
                    
                        // версия информации о текущем магазине одежды
                        var shopVersion: Int = 1
                    
                        if(insertFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                shopVersion = Int(insertFieldsValues[GlobalFlags.TAG_VERSION]!)!
                            }
                        }
                    
                        shop.version = NSNumber(integer: shopVersion)
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        shop.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                        let shopBrand: ShopBrand = NSEntityDescription.insertNewObjectForEntityForName("ShopBrand", inManagedObjectContext: managedObjectContext) as! ShopBrand
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_SHOP_ID] != nil) {
                            shopBrand.shop_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_SHOP_ID]!)!)
                        }
                        else {
                            shopBrand.shop_id = 0
                        }
                    
                        // id текущего бренда одежды, продаваемого данным магазином
                        if(insertFieldsValues[GlobalFlags.TAG_BRAND_ID] != nil) {
                            shopBrand.brand_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_BRAND_ID]!)!)
                        }
                        else {
                            shopBrand.brand_id = 0
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        shopBrand.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                        let shopDress: ShopDress = NSEntityDescription.insertNewObjectForEntityForName("ShopDress", inManagedObjectContext: managedObjectContext) as! ShopDress
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_SHOP_ID] != nil) {
                            shopDress.shop_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_SHOP_ID]!)!)
                        }
                        else {
                           shopDress.shop_id = 0
                        }
                    
                        // id текущей одежды, продаваемой данным магазином
                        if(insertFieldsValues[GlobalFlags.TAG_DRESS_ID] != nil) {
                            shopDress.dress_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_DRESS_ID]!)!)
                        }
                        else {
                            shopDress.dress_id = 0
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        shopDress.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
                    
                    case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                        let shopImage: ShopImage = NSEntityDescription.insertNewObjectForEntityForName("ShopImage", inManagedObjectContext: managedObjectContext) as! ShopImage
                    
                        // Заполняем информацию о текущем объекте
                    
                        // id текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_SHOP_ID] != nil) {
                            shopImage.shop_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_SHOP_ID]!)!)
                        }
                        else {
                            shopImage.shop_id = 0
                        }
                    
                        // ссылка на файл-изображение для текущего магазина одежды
                        if(insertFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                            shopImage.image = insertFieldsValues[GlobalFlags.TAG_IMAGE]!
                        }
                        else {
                            shopImage.image = ""
                        }
                    
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                    
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                    
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                    
                        shopImage.record_number = NSNumber(integer: recordNumberForCurrentRow)
                    
                        break
               
                    case GlobalFlags.TAG_TABLE_USER:
                        let user: User = NSEntityDescription.insertNewObjectForEntityForName("User", inManagedObjectContext: managedObjectContext) as! User
                     
                        // Заполняем информацию о текущем объекте
                        
                        // id текущего пользователя в удаленной БД
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_ID] != nil) {
                            user.id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_ID]!)!)
                        }
                        else {
                            user.id = 0
                        }
                        
                        // id группы для текущего пользователя
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_GROUP_ID] != nil) {
                            user.group_id = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_GROUP_ID]!)!)
                        }
                        else {
                            user.group_id = 0
                        }
                        
                        // имя текущего пользователя
                        user.name = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_NAME]
                        
                        // фамилия текущего пользователя
                        user.surname = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_SURNAME]
                        
                        // логин текущего пользователя
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN] != nil) {
                            user.login = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN]!
                        }
                        else {
                            user.login = ""
                        }
                        
                        // тип учетной записи текущего пользователя
                        var userLoginType: String = GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL
                        
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] == GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] == GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_VKONTAKTE || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] == GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_FACEBOOK || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] == GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_TWITTER || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE] == GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_GOOGLE) {
                                userLoginType = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE]!
                            }
                        }
                        
                        user.type = userLoginType
                        
                        // адрес электронной почты для текущего пользователя
                        user.mail = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_MAIL]
                        
                        // адрес изображения для текущего пользователя
                        user.image = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_IMAGE]
                        
                        // токен для текущего пользователя
                        user.token = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_TOKEN]
                        
                        // количество коллекций одежды для текущего пользователя
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS] != nil) {
                            user.collections_count = NSNumber(integer: Int(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS]!)!)
                        }
                        else {
                            user.collections_count = 0
                        }
                        
                        // для кого текущий пользователь предпочитает просматривать
                        // по умолчанию вещи (для мужчин, женщин или детей)
                        var userForWho: String = GlobalFlags.TAG_DRESS_MAN
                        
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_FOR_WHO] != nil) {
                            if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_FOR_WHO] == GlobalFlags.TAG_DRESS_MAN || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_FOR_WHO] == GlobalFlags.TAG_DRESS_WOMAN || insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_FOR_WHO] == GlobalFlags.TAG_DRESS_KID) {
                                userForWho = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_FOR_WHO]!
                            }
                        }
                        
                        user.for_who = userForWho
                        
                        // ссылка на профиль текущего пользователя в социальной сети
                        user.profile_url = insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_PROFILE_URL]
                        
                        // версия информации о текущем пользователе
                        var userVersion: Int = 1
                        
                        if(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_VERSION] != nil) {
                            if(Int(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_VERSION]!) >= 1) {
                                userVersion = Int(insertFieldsValues[GlobalFlags.TAG_USER_DETAILS_VERSION]!)!
                            }
                        }
                        
                        user.version = NSNumber(integer: userVersion)
                        
                        // Определяем максимальное значение порядкового номера строки в данной таблице
                        let maxRecordNumberInCurrentTable: Int = self.getMaxRecordNumber(tableName)
                        
                        // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                        // чем максимальное значение порядкового номера строки в данной таблице
                        var recordNumberForCurrentRow: Int = 1
                        
                        if(maxRecordNumberInCurrentTable > 0) {
                            recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1
                        }
                        
                        user.record_number = NSNumber(integer: recordNumberForCurrentRow)
                        
                        break
 
                    default:
                        return
                }
         
                // Сохраняем информацию в локальной БД
                do {
                    try managedObjectContext.save()
                }
                catch {
                    print(error)
                    return
                }
            }
        }
    }
    
    //==============================================================================================
    // Метод для обновления строки в таблице, найденной по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // updateFieldsValues - ассоциативный массив, значения, которые должны быть обновлены в таблице tableName
    //                      В данном ассоциативном массиве:
    //                      ключ - название столбца
    //                      значение - значение соответствующего столбца
    // Возвращаемое значение
    // countRowsUpdate - количество обновленных в БД строк
    public func updateRecordInDBByIdServerMySQL(tableName: String, idServerMySQL: Int, updateFieldsValues: Dictionary<String, String>) -> Int {
        // Количество обновленных в БД строк
        var countRowsUpdate: Int = 0
        
        if (updateFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
            // Считываем версию информации о текущей записи из БД
            let versionFromDB: Int = self.getRecordVersionFromDBByIdServerMySQL(tableName, idServerMySQL: idServerMySQL)
                    
            // Если значение версии информации из ассоциативного массива updateFieldsValues
            // отличается от значения версии информации о текущей записи, считанной из БД,
            // то выполняем обновление информации
            if (Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!) > versionFromDB) {
                // Формируем запрос к БД
                let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
                
                // Задаем условия сортировки
                let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
                fetchRequest.sortDescriptors = [sortDescriptor]
                
                // Задаем условия выборки
                fetchRequest.predicate = NSPredicate(format: GlobalFlags.TAG_ID + " = %d", argumentArray: [idServerMySQL])
                
                // Задаем количество извлекаемых строк из таблицы
                fetchRequest.fetchLimit = 1
                
                if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
                    self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
                    
                    if(self.getFetchedResultsController() != nil) {
                        self.getFetchedResultsController()!.delegate = self
                    
                        // Выполняем сформированный запрос к БД
                        do {
                            try self.getFetchedResultsController()!.performFetch()
                        }
                        catch {
                            print(error)
                        }
                        
                        if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                            if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                                // В зависимости от названия таблицы выполняем соответствующие действия
                                switch (tableName) {
                                    case GlobalFlags.TAG_TABLE_BRAND:
                                        let brand: Brand = self.getFetchedResultsController()!.fetchedObjects![0] as! Brand
                            
                                        // Заполняем информацию о текущем объекте бренда
                        
                                        // название текущего бренда одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                            brand.title = updateFieldsValues[GlobalFlags.TAG_TITLE]!
                                        }
                        
                                        // алиас названия текущего бренда одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                            brand.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                        }
                        
                                        // изображение для текущего бренда
                                        if(updateFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                                            brand.image = updateFieldsValues[GlobalFlags.TAG_IMAGE]
                                        }
                        
                                        // краткое описание текущего бренда одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                            brand.short_desc = updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                        }
                        
                                        // описание текущего бренда одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                            brand.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                        }
                        
                                        // рейтинг текущего бренда одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                                            if(Int(updateFieldsValues[GlobalFlags.TAG_HITS]!) >= 0) {
                                                brand.hits = Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)!
                                            }
                                        }
                        
                                        // версия информации о текущей одежде
                                        brand.version = Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!
                           
                                        // Устанавливаем, что количество обновленных строк равно 1
                                        countRowsUpdate = 1
                            
                                        break
                        
                                    case GlobalFlags.TAG_TABLE_CATEGORIES:
                                        let category: Categories = self.getFetchedResultsController()!.fetchedObjects![0] as! Categories
                            
                                        // Заполняем информацию о текущем объекте
                        
                                        // название текущей категории одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                            category.title = updateFieldsValues[GlobalFlags.TAG_TITLE]!
                                        }
                        
                                        // алиас для названия текущей категории одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                            category.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                        }
                        
                                        // для кого предназначена одежда (для мужчины, для женщины или для детей) из данной категории
                                        if(updateFieldsValues[GlobalFlags.TAG_FOR_WHO] != nil) {
                                            if(updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_MAN || updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_WOMAN || updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_KID) {
                                                category.for_who = updateFieldsValues[GlobalFlags.TAG_FOR_WHO]!
                                            }
                                        }
                        
                                        // тип одежды (головные уборы, обувь и т.д.) из данной категории
                                        if(updateFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                                            if(updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_HEAD || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_FOOT || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_ACCESSORY) {
                                                category.type = updateFieldsValues[GlobalFlags.TAG_TYPE]!
                                            }
                                        }
                        
                                        // уровень вложенности текущей категории одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_LEVEL] != nil) {
                                            category.level = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_LEVEL]!)!)
                                        }
                            
                                        // id родительской категории для текущей категории одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_PARENT_ID] != nil) {
                                            category.parent_id = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_PARENT_ID]!)!)
                                        }
                       
                                        // описание текущей категории одежды
                                        if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                            category.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                        }
                        
                                        // версия информации о текущей категории одежды
                                        category.version = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!)
                        
                                        // количество вещей (одежды) для текущей категории
                                        if(updateFieldsValues[GlobalFlags.TAG_DRESS_COUNT] != nil) {
                                            if(Int(updateFieldsValues[GlobalFlags.TAG_DRESS_COUNT]!)! >= 0) {
                                                category.dress_count = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_DRESS_COUNT]!)!)
                                            }
                                        }
                        
                                        // Устанавливаем, что количество обновленных строк равно 1
                                        countRowsUpdate = 1
                            
                                    break
                        
                                case GlobalFlags.TAG_TABLE_COLLECTION:
                                    let collection: Collection = self.getFetchedResultsController()!.fetchedObjects![0] as! Collection
                            
                                    // Заполняем информацию о текущем объекте
                        
                                    // название текущего набора одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                        collection.title = updateFieldsValues[GlobalFlags.TAG_TITLE]
                                    }
                        
                                    // алиас названия текущего набора одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                        collection.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                    }
                        
                                    // тип текущего набора одежды (коллекция или одежда)
                                    if(updateFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                                        if(updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.DRESS_COLLECTION_TYPE_DRESS || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION) {
                                            collection.type = updateFieldsValues[GlobalFlags.TAG_TYPE]!
                                        }
                                    }
                        
                                    // краткое описание текущего набора одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        collection.short_desc = updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                        
                                    // описание текущего набора одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        collection.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                    }
                        
                                    // версия информации о текущем наборе одежды
                                    collection.version = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!)
                        
                                    // Устанавливаем, что количество обновленных строк равно 1
                                    countRowsUpdate = 1
                            
                                    break
                        
                                case GlobalFlags.TAG_TABLE_COMPANY:
                                    let company: Company = self.getFetchedResultsController()!.fetchedObjects![0] as! Company
                            
                                    // Заполняем информацию о текущем объекте
                        
                                    // название текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                        company.title = updateFieldsValues[GlobalFlags.TAG_TITLE]!
                                    }
                       
                                    // алиас названия текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                        company.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                    }
                        
                                    // изображение-логотип для текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                                        company.image = updateFieldsValues[GlobalFlags.TAG_IMAGE]
                                    }
                        
                                    // краткое описание текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        company.short_desc = updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                        
                                    // описание текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        company.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                    }
                        
                                    // рейтинг текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                                        if(Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                            company.hits = Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)!
                                        }
                                    }
                        
                                    // почтовый индекс месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_POSTCODE] != nil) {
                                        company.postcode = updateFieldsValues[GlobalFlags.TAG_POSTCODE]
                                    }
                        
                                    // страна месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_COUNTRY] != nil) {
                                        company.country = updateFieldsValues[GlobalFlags.TAG_COUNTRY]
                                    }
                        
                                    // код страны месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_COUNTRY_CODE] != nil) {
                                        company.country_code = updateFieldsValues[GlobalFlags.TAG_COUNTRY_CODE]
                                    }
                        
                                    // регион (область, штат) месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_REGION] != nil) {
                                        company.region = updateFieldsValues[GlobalFlags.TAG_REGION]
                                    }
                        
                                    // город месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_CITY] != nil) {
                                        company.city = updateFieldsValues[GlobalFlags.TAG_CITY]
                                    }
                        
                                    // улица месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_STREET] != nil) {
                                        company.street = updateFieldsValues[GlobalFlags.TAG_STREET]
                                    }
                        
                                    // номер дома (здания) месторасположения головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_BUILDING] != nil) {
                                        company.building = updateFieldsValues[GlobalFlags.TAG_BUILDING]
                                    }
                        
                                    // номера телефонов головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TELEPHONE] != nil) {
                                        company.telephone = updateFieldsValues[GlobalFlags.TAG_TELEPHONE]
                                    }
                        
                                    // мобильные номера телефонов головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_MOBILE] != nil) {
                                        company.mobile = updateFieldsValues[GlobalFlags.TAG_MOBILE]
                                    }
                        
                                    // номер факса головного офиса текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_FAX] != nil) {
                                        company.fax = updateFieldsValues[GlobalFlags.TAG_FAX]
                                    }
                        
                                    // адрес сайта текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_WEBPAGE] != nil) {
                                        company.webpage = updateFieldsValues[GlobalFlags.TAG_WEBPAGE]
                                    }
                        
                                    // адрес электронной почты текущей компании-производителя одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_EMAIL] != nil) {
                                        company.email_to = updateFieldsValues[GlobalFlags.TAG_EMAIL]
                                    }
                        
                                    // версия информации о текущей компании-производителе одежды
                                    company.version = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!)
                        
                                    // Устанавливаем, что количество обновленных строк равно 1
                                    countRowsUpdate = 1
                            
                                    break
                        
                                case GlobalFlags.TAG_TABLE_DRESS:
                                    let dress: Dress = self.getFetchedResultsController()!.fetchedObjects![0] as! Dress
                            
                                    // Заполняем информацию о текущем объекте
                        
                                    // id категории, к которой относится текущая вещь (одежда)
                                    if(updateFieldsValues[GlobalFlags.TAG_CATID] != nil) {
                                        dress.catid = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_CATID]!)!)
                                    }
                        
                                    // название текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                        dress.title = updateFieldsValues[GlobalFlags.TAG_TITLE]!
                                    }
                            
                                    // алиас для названия текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                        dress.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                    }
                        
                                    // для кого предназначена данная вещь (одежда) (для мужчины, для женщины или для детей)
                                    if(updateFieldsValues[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        if(updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_MAN || updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_WOMAN || updateFieldsValues[GlobalFlags.TAG_FOR_WHO] == GlobalFlags.TAG_DRESS_KID) {
                                            dress.for_who = updateFieldsValues[GlobalFlags.TAG_FOR_WHO]!
                                        }
                                    }
                        
                                    // тип текущей вещи (одежды) (для мужчины, для женщины или для детей)
                                    if(updateFieldsValues[GlobalFlags.TAG_TYPE] != nil) {
                                        if(updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_HEAD || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_BODY || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_LEG || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_FOOT || updateFieldsValues[GlobalFlags.TAG_TYPE] == GlobalFlags.TAG_DRESS_ACCESSORY) {
                                            dress.type = updateFieldsValues[GlobalFlags.TAG_TYPE]!
                                        }
                                    }
                        
                                    // id бренда, к которому относится текущая вещь (одежда)
                                    if(updateFieldsValues[GlobalFlags.TAG_BRAND_ID] != nil) {
                                        dress.brand_id = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_BRAND_ID]!)!)
                                    }
                            
                                    // ссылка на файл-превью-изображение для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_THUMB] != nil) {
                                        dress.thumb = updateFieldsValues[GlobalFlags.TAG_THUMB]
                                    }
                        
                                    // ширина файла-превью-изображения для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_THUMB_WIDTH] != nil) {
                                        dress.thumb_width = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_THUMB_WIDTH]!)!)
                                    }
                       
                                    // высота файла-превью-изображения для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_THUMB_HEIGHT] != nil) {
                                        dress.thumb_height = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_THUMB_HEIGHT]!)!)
                                    }
                        
                                    // ссылка на файл-изображение для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                                        dress.image = updateFieldsValues[GlobalFlags.TAG_IMAGE]!
                                    }
                        
                                    // ширина (в пикселях) файла-изображение для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        dress.image_width = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_IMAGE_WIDTH]!)!)
                                    }
                        
                                    // высота (в пикселях) файла-изображение для текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        dress.image_height = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_IMAGE_HEIGHT]!)!)
                                    }
                       
                                    // ссылка на файл-изображение для задней (тыловой) стороны текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        dress.image_back = updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK]
                                    }
                        
                                    // ширина (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        dress.image_back_width = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!)!)
                                    }
                       
                                    // высота (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        dress.image_back_height = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!)!)
                                    }
                       
                                    // цвет текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_COLOR] != nil) {
                                        dress.color = updateFieldsValues[GlobalFlags.TAG_COLOR]!
                                    }
                        
                                    // стиль текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_STYLE] != nil) {
                                        dress.style = updateFieldsValues[GlobalFlags.TAG_STYLE]
                                    }
                        
                                    // краткое описание текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        dress.short_desc = updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                        
                                    // описание текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        dress.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                    }
                        
                                    // рейтинг текущей вещи (одежды)
                                    if(updateFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                                        if(Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                            dress.hits = Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)!
                                        }
                                    }
                        
                                    // логическая переменная, определяющая отображать ли данную вещь по умолчанию при первоначальном заходе пользователя на страницу
                                    if(updateFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                                        if(Int(updateFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT]!) > 0) {
                                            dress.dress_default = Int(updateFieldsValues[GlobalFlags.TAG_DRESS_DEFAULT]!)!
                                        }
                                    }
                        
                                    // версия информации о текущей одежде
                                    dress.version = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!)
                        
                                    // Устанавливаем, что количество обновленных строк равно 1
                                    countRowsUpdate = 1
                            
                                    break
                        
                                case GlobalFlags.TAG_TABLE_SHOP:
                                    let shop: Shop = self.getFetchedResultsController()!.fetchedObjects![0] as! Shop
                            
                                    // Заполняем информацию о текущем объекте
                        
                                    // название текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TITLE] != nil) {
                                        shop.title = updateFieldsValues[GlobalFlags.TAG_TITLE]!
                                    }
                        
                                    // алиас названия текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_ALIAS] != nil) {
                                        shop.alias = updateFieldsValues[GlobalFlags.TAG_ALIAS]
                                    }
                        
                                    // изображение-логотип для текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_IMAGE] != nil) {
                                        shop.image = updateFieldsValues[GlobalFlags.TAG_IMAGE]
                                    }
                        
                                    // краткое описание текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        shop.short_desc = updateFieldsValues[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                        
                                    // описание текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        shop.desc = updateFieldsValues[GlobalFlags.TAG_DESCRIPTION]
                                    }
                        
                                    // рейтинг текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_HITS] != nil) {
                                        if(Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)! > 0) {
                                            shop.hits = Int(updateFieldsValues[GlobalFlags.TAG_HITS]!)!
                                        }
                                    }
                        
                                    // широта местоположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_LATITUDE] != nil) {
                                        shop.latitude = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_LATITUDE]!)!)
                                    }
                        
                                    // долгота местоположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_LONGITUDE] != nil) {
                                        shop.longitude = NSNumber(integer: Int(updateFieldsValues[GlobalFlags.TAG_LONGITUDE]!)!)
                                    }
                        
                                    // почтовый индекс месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_POSTCODE] != nil) {
                                        shop.postcode = updateFieldsValues[GlobalFlags.TAG_POSTCODE]
                                    }
                        
                                    // страна месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_COUNTRY] != nil) {
                                        shop.country = updateFieldsValues[GlobalFlags.TAG_COUNTRY]
                                    }
                        
                                    // код страны месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_COUNTRY_CODE] != nil) {
                                        shop.country_code = updateFieldsValues[GlobalFlags.TAG_COUNTRY_CODE]
                                    }
                        
                                    // регион (область, штат) месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_REGION] != nil) {
                                        shop.region = updateFieldsValues[GlobalFlags.TAG_REGION]
                                    }
                        
                                    // город месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_CITY] != nil) {
                                        shop.city = updateFieldsValues[GlobalFlags.TAG_CITY]
                                    }
                        
                                    // улица месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_STREET] != nil) {
                                        shop.street = updateFieldsValues[GlobalFlags.TAG_STREET]
                                    }
                        
                                    // номер дома (здания) месторасположения текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_BUILDING] != nil) {
                                        shop.building = updateFieldsValues[GlobalFlags.TAG_BUILDING]
                                    }
                        
                                    // номера телефонов текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TELEPHONE] != nil) {
                                        shop.telephone = updateFieldsValues[GlobalFlags.TAG_TELEPHONE]
                                    }
                        
                                    // мобильные номера телефонов текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_MOBILE] != nil) {
                                        shop.mobile = updateFieldsValues[GlobalFlags.TAG_MOBILE]
                                    }
                        
                                    // номер факса текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_TELEPHONE] != nil) {
                                        shop.fax = updateFieldsValues[GlobalFlags.TAG_FAX]
                                    }
                        
                                    // адрес сайта текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_WEBPAGE] != nil) {
                                        shop.webpage = updateFieldsValues[GlobalFlags.TAG_WEBPAGE]
                                    }
                        
                                    // адрес электронной почты текущего магазина одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_EMAIL] != nil) {
                                        shop.email_to = updateFieldsValues[GlobalFlags.TAG_EMAIL]
                                    }
                        
                                    // версия информации о текущем магазине одежды
                                    if(updateFieldsValues[GlobalFlags.TAG_VERSION] != nil) {
                                        if(Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!) >= 1) {
                                            shop.version = Int(updateFieldsValues[GlobalFlags.TAG_VERSION]!)!
                                        }
                                    }
                        
                                    // Устанавливаем, что количество обновленных строк равно 1
                                    countRowsUpdate = 1
                        
                                    break
                        
                                default:
                                    return 0
                                }
                    
                                // Сохраняем информацию в локальной БД
                                do {
                                    try managedObjectContext.save()
                                }
                                catch {
                                    print(error)
                                    return 0
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return countRowsUpdate
    }
    
    //==============================================================================================
    // Метод для обновления строки в БД, а при отсутствии данной строки в таблице -
    // для вставки данной строки в таблицу в качестве новой
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // updateOrInsertFieldsValues - ассоциативный массив, значения, которые должны быть обновлены или вставлены в таблицу tableName
    //                              В данном ассоциативном массиве:
    //                              ключ - название столбца
    //                              значение - значение соответствующего столбца
    // Возвращаемое значение
    // resultArrayUpdateOrInsert - ассоциативный массив, хранящий результат выполнения функции
    //                             обновления или вставки данной строки в качестве новой в таблицу
    //                             Значения (ключи) данного ассоциативного массива:
    //                             task - какая операция была выполнена
    //                                    Возможные значения:
    //                                    update - обновления информации
    //                                    insert - добавление информации
    //                             count_or_id - количество обновленных строк в случае обновления информации
    //                                           или id новой добавленной строки
    public func updateOrInsertRecordToDBByIdServerMySQL(tableName: String, idServerMySQL: Int, updateOrInsertFieldsValues: Dictionary<String, String>) -> Dictionary<String, String> {
        // Возвращаемый ассоциативный массив
        var resultArrayUpdateOrInsert: Dictionary<String, String> = Dictionary<String, String>()
        
        // Проверяем существует ли такая запись уже в таблице
            
        // Если данная строка уже присутствует в БД, то обновляем ее
        if (self.getCountRecordFromDBByIdServerMySQL(tableName, idServerMySQL: idServerMySQL) > 0) {
            // Количество обновленных строк в БД
            let countRowsUpdate: Int = self.updateRecordInDBByIdServerMySQL(tableName, idServerMySQL: idServerMySQL, updateFieldsValues: updateOrInsertFieldsValues)
                
            // Заполняем возвращаемый ассоциативный массив
            resultArrayUpdateOrInsert["task"] = "update"
            resultArrayUpdateOrInsert["count_or_id"] = String(countRowsUpdate)
        }
        // Иначе, вставляем данную строку в БД
        else {
            // Вставляем строку в таблицу
            self.insertRecordToDB(tableName, insertFieldsValues: updateOrInsertFieldsValues);
                
            // Заполняем возвращаемый ассоциативный массив
            resultArrayUpdateOrInsert["task"] = "insert"
            resultArrayUpdateOrInsert["count_or_id"] = "0"
        }
        
        return resultArrayUpdateOrInsert
    }
    
    //==============================================================================================
    // Метод для удаления строки из таблицы, найденной по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из БД строк
    public func deleteRecordFromDBByIdServerMySQL(tableName: String, idServerMySQL: Int) {
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = NSPredicate(format: GlobalFlags.TAG_ID + " = %d", argumentArray: [idServerMySQL])
        
        // Задаем количество извлекаемых строк из таблицы
        fetchRequest.fetchLimit = 1
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Удаляем искомый объект из БД
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    if(self.getFetchedResultsController()!.fetchedObjects!.count > 0) {
                        switch(tableName) {
                            case GlobalFlags.TAG_TABLE_USER:
                                let currentObjectUser: User = self.getFetchedResultsController()!.fetchedObjects![0] as! User
                                managedObjectContext.deleteObject(currentObjectUser)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                                let currentObjectDressInUserCollection: DressInUserCollection = self.getFetchedResultsController()!.fetchedObjects![0] as! DressInUserCollection
                                managedObjectContext.deleteObject(currentObjectDressInUserCollection)
                                break
                            case GlobalFlags.TAG_TABLE_CATEGORIES:
                                let currentObjectCategories: Categories = self.getFetchedResultsController()!.fetchedObjects![0] as! Categories
                                managedObjectContext.deleteObject(currentObjectCategories)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS:
                                let currentObjectDress: Dress = self.getFetchedResultsController()!.fetchedObjects![0] as! Dress
                                managedObjectContext.deleteObject(currentObjectDress)
                                break
                            case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                                let currentObjectDressImage: DressImage = self.getFetchedResultsController()!.fetchedObjects![0] as! DressImage
                                managedObjectContext.deleteObject(currentObjectDressImage)
                                break
                            case GlobalFlags.TAG_TABLE_BRAND:
                                let currentObjectBrand: Brand = self.getFetchedResultsController()!.fetchedObjects![0] as! Brand
                                managedObjectContext.deleteObject(currentObjectBrand)
                                break
                            case GlobalFlags.TAG_BRAND_IMAGE:
                                let currentObjectBrandImage: BrandImage = self.getFetchedResultsController()!.fetchedObjects![0] as! BrandImage
                                managedObjectContext.deleteObject(currentObjectBrandImage)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY:
                                let currentObjectCompany: Company = self.getFetchedResultsController()!.fetchedObjects![0] as! Company
                                managedObjectContext.deleteObject(currentObjectCompany)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                                let currentObjectCompanyImage: CompanyImage = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyImage
                                managedObjectContext.deleteObject(currentObjectCompanyImage)
                                break
                            case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                                let currentObjectCompanyBrand: CompanyBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! CompanyBrand
                                managedObjectContext.deleteObject(currentObjectCompanyBrand)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP:
                                let currentObjectShop: Shop = self.getFetchedResultsController()!.fetchedObjects![0] as! Shop
                                managedObjectContext.deleteObject(currentObjectShop)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                                let currentObjectShopBrand: ShopBrand = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopBrand
                                managedObjectContext.deleteObject(currentObjectShopBrand)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                                let currentObjectShopDress: ShopDress = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopDress
                                managedObjectContext.deleteObject(currentObjectShopDress)
                                break
                            case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                                let currentObjectShopImage: ShopImage = self.getFetchedResultsController()!.fetchedObjects![0] as! ShopImage
                                managedObjectContext.deleteObject(currentObjectShopImage)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION:
                                let currentObjectCollection: Collection = self.getFetchedResultsController()!.fetchedObjects![0] as! Collection
                                managedObjectContext.deleteObject(currentObjectCollection)
                                break
                            case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                                let currentObjectCollectionDress: CollectionDress = self.getFetchedResultsController()!.fetchedObjects![0] as! CollectionDress
                                managedObjectContext.deleteObject(currentObjectCollectionDress)
                                break
                            default:
                                break
                        }

                        // Сохраняем информацию в локальной БД
                        do {
                            try managedObjectContext.save()
                        }
                        catch {
                            print(error)
                            return
                        }
                    }
                }
            }
        }
    }
    
    //==============================================================================================
    // Метод для удаления строки из таблицы
    // Передаваемые параметры
    // tableName - название таблицы
    // selection - условие выборки
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из БД строк
    public func deleteRecordFromDB(tableName: String, selection: NSPredicate) {
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        // Задаем условия выборки
        fetchRequest.predicate = selection
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Удаляем искомый объект из БД
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    switch(tableName) {
                        case GlobalFlags.TAG_TABLE_USER:
                            for currentObjectUser: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectUser as! User)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                            for currentObjectDressInUserCollection: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDressInUserCollection as! DressInUserCollection)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_CATEGORIES:
                            for currentObjectCategories: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCategories as! Categories)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS:
                            for currentObjectDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDress as! Dress)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                            for currentObjectDressImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDressImage as! DressImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_BRAND:
                            for currentObjectBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectBrand as! Brand)
                            }
                            break
                        case GlobalFlags.TAG_BRAND_IMAGE:
                            for currentObjectBrandImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectBrandImage as! BrandImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY:
                            for currentObjectCompany: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompany as! Company)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                            for currentObjectCompanyImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompanyImage as! CompanyImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                            for currentObjectCompanyBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompanyBrand as! CompanyBrand)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP:
                            for currentObjectShop: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShop as! Shop)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                            for currentObjectShopBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopBrand as! ShopBrand)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                            for currentObjectShopDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopDress as! ShopDress)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                            for currentObjectShopImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopImage as! ShopImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COLLECTION:
                            for currentObjectCollection: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCollection as! Collection)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                            for currentObjectCollectionDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCollectionDress as! CollectionDress)
                            }
                            break
                        default:
                            break
                    }
                    
                    // Сохраняем информацию в локальной БД
                    do {
                        try managedObjectContext.save()
                    }
                    catch {
                        print(error)
                        return
                    }
                }
            }
        }
    }
    
    //==============================================================================================
    // Метод для очистки таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из таблицы строк
    public func clearTable(tableName: String) {
        // Формируем запрос к БД
        let fetchRequest: NSFetchRequest = NSFetchRequest(entityName: tableName)
        
        // Задаем условия сортировки
        let sortDescriptor: NSSortDescriptor = NSSortDescriptor(key: GlobalFlags.TAG_ID, ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        if let managedObjectContext = (UIApplication.sharedApplication().delegate as? AppDelegate)?.managedObjectContext {
            self.setFetchedResultsController(NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: nil))
            
            if(self.getFetchedResultsController() != nil) {
                self.getFetchedResultsController()!.delegate = self
            
                // Выполняем сформированный запрос к БД
                do {
                    try self.getFetchedResultsController()!.performFetch()
                }
                catch {
                    print(error)
                }
            
                // Удаляем искомый объект из БД
                if(self.getFetchedResultsController()!.fetchedObjects != nil) {
                    switch(tableName) {
                        case GlobalFlags.TAG_TABLE_USER:
                            for currentObjectUser: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectUser as! User)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION:
                            for currentObjectDressInUserCollection: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDressInUserCollection as! DressInUserCollection)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_CATEGORIES:
                            for currentObjectCategories: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCategories as! Categories)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS:
                            for currentObjectDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDress as! Dress)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_DRESS_IMAGE:
                            for currentObjectDressImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectDressImage as! DressImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_BRAND:
                            for currentObjectBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectBrand as! Brand)
                            }
                            break
                        case GlobalFlags.TAG_BRAND_IMAGE:
                            for currentObjectBrandImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectBrandImage as! BrandImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY:
                            for currentObjectCompany: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompany as! Company)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY_IMAGE:
                            for currentObjectCompanyImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompanyImage as! CompanyImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COMPANY_BRAND:
                            for currentObjectCompanyBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCompanyBrand as! CompanyBrand)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP:
                            for currentObjectShop: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShop as! Shop)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_BRAND:
                            for currentObjectShopBrand: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopBrand as! ShopBrand)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_DRESS:
                            for currentObjectShopDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopDress as! ShopDress)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_SHOP_IMAGE:
                            for currentObjectShopImage: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectShopImage as! ShopImage)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COLLECTION:
                            for currentObjectCollection: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCollection as! Collection)
                            }
                            break
                        case GlobalFlags.TAG_TABLE_COLLECTION_DRESS:
                            for currentObjectCollectionDress: AnyObject in self.getFetchedResultsController()!.fetchedObjects! {
                                managedObjectContext.deleteObject(currentObjectCollectionDress as! CollectionDress)
                            }
                            break
                        default:
                            break
                    }

                    // Сохраняем информацию в локальной БД
                    do {
                        try managedObjectContext.save()
                    }
                    catch {
                        print(error)
                        return
                    }
                }
            }
        }
    }
}