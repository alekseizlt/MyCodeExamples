import Foundation
import UIKit

/**
 * Основной класс для работы с БД
 */
public class DBMain {
    
    // Свойства текущего класса
    private static var mContext: ViewControllerMain?                        // контекст
    private static var mRemoteDBType: String = GlobalFlags.DB_TYPE_MYSQL    // тип удаленной БД (возможные значения "mysql" или "pgsql")
    private static var mDBSQLiteHelper: DBSQLiteHelper?                     // класс, содержащий методы для создания и обновления локальной БД SQLite
    
    // Экземпяры классов
    private static var mMySQLGetBaseDressCategories: MySQLGetBaseDressCategories?   // класс для считывания информации о базовых категориях одежды
    private static var mMySQLGetAllDressCategories: MySQLGetAllDressCategories?     // класс для считывания информации обо всех категориях одежды
    private static var mMySQLGetAllDressBrands: MySQLGetAllDressBrands?             // класс для считывания информации обо всех брендах одежды
    private static var mMySQLGetDressDefault: MySQLGetDressDefault?                 // класс для считывания информации (изображения) об одежде по умолчанию
    private static var mMySQLDressCollectionSave: MySQLDressCollectionSave?         // класс для сохранения текущего набора одежды для пользователя
    private static var mMySQLDressCollectionLoad: MySQLDressCollectionLoad?         // класс для загрузки информации о текущем наборе одежды для пользователя
    private static var mMySQLGoToDressLastView: MySQLGoToDressLastView?             // класс для считывания информации о последней просматриваемой одежде
    
    // Массив адаптеров для элементов ViewPager, предназначенных для листания одежды
    private static var mArrayPagerAdapterDressroom: Dictionary<String, PagerAdapterDressroom>?
    
    // Адаптер для элемента ViewPager, предназначенного для листания коллекций одежды
    private static var mPagerAdapterDressCollection: PagerAdapterDressCollection?
    
    // Ассоциативные массивы
    // Ассоциативный массив, хранящий реальные размеры одежды для каждой группы одежды
    private static var mArrayDressSizeRealMan: Dictionary<String, Float>?             // для мужской одежды
    private static var mArrayDressSizeRealWoman: Dictionary<String, Float>?           // для женской одежды
    private static var mArrayDressSizeRealKid: Dictionary<String, Float>?             // для детской одежды
    
    // Ассоциативный массив, хранящий размеры, к которым должны быть преобразованы размеры одежды,
    // для каждой группы одежды после загрузки с сервера
    private static var mArrayDressSizeTargetMan: Dictionary<String, Int>?             // для мужской одежды
    private static var mArrayDressSizeTargetWoman: Dictionary<String, Int>?           // для женской одежды
    private static var mArrayDressSizeTargetKid: Dictionary<String, Int>?             // для детской одежды
    
    // Массивы, хранящие информацию о последних отображаемых на виртуальном манекене вещей
    // Первый ключ у данных массивов - тип одежды (головные уборы, обувь и т.д.)
    private static var mArrayCurrentDressInfoMan: Dictionary<String, [Dictionary<String, String>]>?          // для мужской одежды
    private static var mArrayCurrentDressInfoWoman: Dictionary<String, [Dictionary<String, String>]>?        // для женской одежды
    private static var mArrayCurrentDressInfoKid: Dictionary<String, [Dictionary<String, String>]>?          // для детской одежды
    
    // Массив, хранящий id и названия всех брендов одежды
    // У данного массива:
    // Ключ - id бренда одежды, значение - название соответствующего бренда одежды
    private static var mListAllDressBrands: Dictionary<String, String>?
    
    // Массивы, хранящие id базовых категорий одежды
    private static var mListBaseCategoriesDressIdMan: Dictionary<String, String>?      // мужская одежда
    private static var mListBaseCategoriesDressIdWoman: Dictionary<String, String>?    // женская одежда
    private static var mListBaseCategoriesDressIdKid: Dictionary<String, String>?      // детская одежда
    
    // Многомерный массив, хранящий список категорий
    // Первый ключ типа String определяет тип одежды (головные уборы, обувь и т.д.)
    // Второй ключ типа Integer определяет порядковый номер текущей вещи для данного типа одежды
    // Третий ключ типа String определяет атрибут текущей категории одежды (id, название и т.д.)
    private static var mListCategoriesDressMan: Dictionary<String, [Dictionary<String, String>]>?      // мужская одежда
    private static var mListCategoriesDressWoman: Dictionary<String, [Dictionary<String, String>]>?    // женская одежда
    private static var mListCategoriesDressKid: Dictionary<String, [Dictionary<String, String>]>?      // детская одежда
    
    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    init(context: ViewControllerMain?) {
        DBMain.setContext(context)
    }
    
    //==============================================================================================
    // Метод для инициализации свойств текущего класса
    // Передаваемые параметры
    // remoteDBType - тип удаленной БД (возможные значения "mysql" или "pgsql")
    public static func initializeVariables(remoteDBType: String) {
        // Инициализируем свойства данного класса
        DBMain.setRemoteDBType(remoteDBType)         // тип удаленной БД (возможные значения "mysql" или "pgsql")
    }
    
    //==============================================================================================
    // Метод для инициализации подклассов текущего класса
    public static func initializeSubClasses() {
        // В зависимости от типа удаленной БД (возможные значения "mysql" или "pgsql")
        // инициализируем соответствующие классы
        // Если тип удаленной БД - mysql
        if(DBMain.getRemoteDBType() == GlobalFlags.DB_TYPE_MYSQL) {
//            DBMain.setMySQLGetBaseDressCategories(new MySQLGetBaseDressCategories());
            DBMain.setMySQLGetAllDressCategories(MySQLGetAllDressCategories())
//            DBMain.setMySQLGetAllDressBrands(new MySQLGetAllDressBrands())
//            DBMain.setMySQLGetDressDefault(new MySQLGetDressDefault())
            DBMain.setMySQLDressCollectionSave(MySQLDressCollectionSave())
            DBMain.setMySQLDressCollectionLoad(MySQLDressCollectionLoad())
//            DBMain.setMySQLGoToDressLastView(MySQLGoToDressLastView())
        }
    }

    //==============================================================================================
    // Метод для считывания конекста
    internal static func getContext() -> ViewControllerMain? {
        return DBMain.mContext
    }
    
    //==============================================================================================
    // Метод для задания контекста
    internal static func setContext(context: ViewControllerMain?) {
        DBMain.mContext = context
    }
    
    //==============================================================================================
    // Метод для считывания типа удаленной БД (возможные значения "mysql" или "pgsql")
    public static func getRemoteDBType() -> String {
        return DBMain.mRemoteDBType
    }
    
    //==============================================================================================
    // Метод для задания типа удаленной БД (возможные значения "mysql" или "pgsql")
    public static func setRemoteDBType(remoteDBType: String) {
        let remoteDBTypeVar: String = remoteDBType.lowercaseString.trim()
    
        // Заметим, что возможные значения - это "mysql" или "pgsql"
        // Если переданное в текущую функцию значение типа удаленной БД
        // не совпадает ни с одним из разрешенных, то устанавливаем в качестве
        // значения по умолчанию - значение "mysql"
        if(remoteDBTypeVar != GlobalFlags.DB_TYPE_PGSQL && remoteDBType != GlobalFlags.DB_TYPE_MYSQL) {
            DBMain.mRemoteDBType = GlobalFlags.DB_TYPE_MYSQL
        }
        // Иначе
        else {
            DBMain.mRemoteDBType = remoteDBTypeVar
        }
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса, содержащего методы
    // для создания и обновления локальной БД SQLite
    public static func getDBSQLiteHelper() -> DBSQLiteHelper? {
        return DBMain.mDBSQLiteHelper
    }
    
    //==============================================================================================
    // Метод для задания объекта GestureListener
    public static func setDBSQLiteHelper(dbSQLiteHelper: DBSQLiteHelper) {
        DBMain.mDBSQLiteHelper = dbSQLiteHelper
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации о базовых категориях одежды
    public static func getMySQLGetBaseDressCategories() -> MySQLGetBaseDressCategories? {
        return DBMain.mMySQLGetBaseDressCategories
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации о базовых категориях одежды
    public static func setMySQLGetBaseDressCategories(mysqlGetBaseDressCategories: MySQLGetBaseDressCategories) {
        DBMain.mMySQLGetBaseDressCategories = mysqlGetBaseDressCategories
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации обо всех категориях одежды
    public static func getMySQLGetAllDressCategories() -> MySQLGetAllDressCategories? {
        return DBMain.mMySQLGetAllDressCategories
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации обо всех категориях одежды
    public static func setMySQLGetAllDressCategories(mysqlGetAllDressCategories: MySQLGetAllDressCategories) {
        DBMain.mMySQLGetAllDressCategories = mysqlGetAllDressCategories
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации обо всех брендах одежды
    public static func getMySQLGetAllDressBrands() -> MySQLGetAllDressBrands? {
        return DBMain.mMySQLGetAllDressBrands
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации обо всех брендах одежды
    public static func setMySQLGetAllDressBrands(mysqlGetAllDressBrands: MySQLGetAllDressBrands) {
        DBMain.mMySQLGetAllDressBrands = mysqlGetAllDressBrands
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации (изображения) об одежде по умолчанию
    public static func getMySQLGetDressDefault() -> MySQLGetDressDefault? {
        return DBMain.mMySQLGetDressDefault
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации (изображения) об одежде по умолчанию
    public static func setMySQLGetDressDefault(mysqlGetDressDefault: MySQLGetDressDefault) {
        DBMain.mMySQLGetDressDefault = mysqlGetDressDefault
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для сохранения набора одежды
    public static func getMySQLDressCollectionSave() -> MySQLDressCollectionSave? {
        return DBMain.mMySQLDressCollectionSave
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для сохранения набора одежды
    public static func setMySQLDressCollectionSave(mysqlDressCollectionSave: MySQLDressCollectionSave) {
        DBMain.mMySQLDressCollectionSave = mysqlDressCollectionSave
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для загрузки наборов одежды
    public static func getMySQLDressCollectionLoad() -> MySQLDressCollectionLoad? {
        return DBMain.mMySQLDressCollectionLoad
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для загрузки наборов одежды
    public static func setMySQLDressCollectionLoad(mysqlDressCollectionLoad: MySQLDressCollectionLoad) {
        DBMain.mMySQLDressCollectionLoad = mysqlDressCollectionLoad
    }
    
    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации о последней просматриваемой одежде
    public static func getMySQLGoToDressLastView() -> MySQLGoToDressLastView? {
        return DBMain.mMySQLGoToDressLastView
    }
    
    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации о последней просматриваемой одежде
    public static func setMySQLGoToDressLastView(mySQLGoToDressLastView: MySQLGoToDressLastView) {
        DBMain.mMySQLGoToDressLastView = mySQLGoToDressLastView
    }
    
    //==============================================================================================
    // Метод для считывания массива адаптеров для элементов ViewPager, предназначенных для листания одежды
    public static func getArrayPagerAdapterDressroom() -> Dictionary<String, PagerAdapterDressroom>? {
        return DBMain.mArrayPagerAdapterDressroom
    }
    
    //==============================================================================================
    // Метод для задания массива адаптеров для элементов ViewPager, предназначенных для листания одежды
    public static func setArrayPagerAdapterDressroom(arrayPagerAdapterDressroom: Dictionary<String, PagerAdapterDressroom>) {
        DBMain.mArrayPagerAdapterDressroom = arrayPagerAdapterDressroom
    }
    
    //==============================================================================================
    // Метод для считывания адаптера для элемента ViewPager, предназначенного для листания коллекций одежды
    public static func getPagerAdapterDressCollection() -> PagerAdapterDressCollection? {
        return DBMain.mPagerAdapterDressCollection
    }
    
    //==============================================================================================
    // Метод для задания адаптера для элемента ViewPager, предназначенного для листания коллекций одежды
    public static func setPagerAdapterDressCollection(pagerAdapterDressCollection: PagerAdapterDressCollection) {
        DBMain.mPagerAdapterDressCollection = pagerAdapterDressCollection
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для мужской одежды
    private static func getArrayDressSizeRealMan() -> Dictionary<String, Float>? {
        return DBMain.mArrayDressSizeRealMan
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для мужской одежды
    private static func setArrayDressSizeRealMan(arrayDressSizeRealMan: Dictionary<String, Float>) {
        DBMain.mArrayDressSizeRealMan = arrayDressSizeRealMan
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для женской одежды
    private static func getArrayDressSizeRealWoman() -> Dictionary<String, Float>? {
        return DBMain.mArrayDressSizeRealWoman
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для женской одежды
    private static func setArrayDressSizeRealWoman(arrayDressSizeRealWoman: Dictionary<String, Float>) {
        DBMain.mArrayDressSizeRealWoman = arrayDressSizeRealWoman
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для детской одежды
    private static func getArrayDressSizeRealKid() -> Dictionary<String, Float>? {
        return DBMain.mArrayDressSizeRealKid
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для детской одежды
    private static func setArrayDressSizeRealKid(arrayDressSizeRealKid: Dictionary<String, Float>) {
        DBMain.mArrayDressSizeRealKid = arrayDressSizeRealKid
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    public static func getArrayDressSizeReal(dressForWho: Int) -> Dictionary<String, Float>? {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayDressSizeRealMan()
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayDressSizeRealWoman()
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayDressSizeRealKid()
            default:
                return nil
        }
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    public static func setArrayDressSizeReal(dressForWho: Int, arrayDressSizeReal: Dictionary<String, Float>) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayDressSizeRealMan(arrayDressSizeReal)
                break
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayDressSizeRealWoman(arrayDressSizeReal)
                break
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayDressSizeRealKid(arrayDressSizeReal)
                break
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для мужской одежды после загрузки с сервера
    private static func getArrayDressSizeTargetMan() -> Dictionary<String, Int>? {
        return DBMain.mArrayDressSizeTargetMan
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для мужской одежды после загрузки с сервера
    private static func setArrayDressSizeTargetMan(arrayDressSizeTargetMan: Dictionary<String, Int>) {
        DBMain.mArrayDressSizeTargetMan = arrayDressSizeTargetMan
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для женской одежды после загрузки с сервера
    private static func getArrayDressSizeTargetWoman() -> Dictionary<String, Int>? {
        return DBMain.mArrayDressSizeTargetWoman
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для женской одежды после загрузки с сервера
    private static func setArrayDressSizeTargetWoman(arrayDressSizeTargetWoman: Dictionary<String, Int>) {
        DBMain.mArrayDressSizeTargetWoman = arrayDressSizeTargetWoman
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для детской одежды после загрузки с сервера
    private static func getArrayDressSizeTargetKid() -> Dictionary<String, Int>? {
        return DBMain.mArrayDressSizeTargetKid
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для детской одежды после загрузки с сервера
    private static func setArrayDressSizeTargetKid(arrayDressSizeTargetKid: Dictionary<String, Int>) {
        DBMain.mArrayDressSizeTargetKid = arrayDressSizeTargetKid
    }
    
    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, после загрузки с сервера
    public static func getArrayDressSizeTarget(dressForWho: Int) -> Dictionary<String, Int>? {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayDressSizeTargetMan()
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayDressSizeTargetWoman()
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayDressSizeTargetKid()
            default:
                return nil
        }
    }
    
    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, после загрузки с сервера
    public static func setArrayDressSizeTarget(dressForWho: Int, arrayDressSizeTarget: Dictionary<String, Int>) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayDressSizeTargetMan(arrayDressSizeTarget)
                break
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayDressSizeTargetWoman(arrayDressSizeTarget)
                break
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayDressSizeTargetKid(arrayDressSizeTarget)
                break
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для мужской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func getArrayCurrentDressInfoMan() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mArrayCurrentDressInfoMan
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для мужской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func setArrayCurrentDressInfoMan(arrayCurrentDressInfoMan: Dictionary<String, [Dictionary<String, String>]>) {
        DBMain.mArrayCurrentDressInfoMan = arrayCurrentDressInfoMan
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для женской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func getArrayCurrentDressInfoWoman() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mArrayCurrentDressInfoWoman
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для женской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func setArrayCurrentDressInfoWoman(arrayCurrentDressInfoWoman: Dictionary<String, [Dictionary<String, String>]>) {
        DBMain.mArrayCurrentDressInfoWoman = arrayCurrentDressInfoWoman
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для детской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func getArrayCurrentDressInfoKid() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mArrayCurrentDressInfoKid
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для детской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static func setArrayCurrentDressInfoKid(arrayCurrentDressInfoKid: Dictionary<String, [Dictionary<String, String>]>) {
        DBMain.mArrayCurrentDressInfoKid = arrayCurrentDressInfoKid
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей
    public static func getArrayCurrentDressInfo(dressForWho: Int) -> Dictionary<String, [Dictionary<String, String>]>? {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayCurrentDressInfoMan()
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayCurrentDressInfoWoman()
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayCurrentDressInfoKid()
            default:
                return nil
        }
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей
    public static func setArrayCurrentDressInfo(dressForWho: Int, arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayCurrentDressInfoMan(arrayCurrentDressInfo)
                break
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayCurrentDressInfoWoman(arrayCurrentDressInfo)
                break
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayCurrentDressInfoKid(arrayCurrentDressInfo)
                break
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для считывания массива, хранящего id и названия всех брендов одежды
    public static func getListAllDressBrands() -> Dictionary<String, String>? {
        return DBMain.mListAllDressBrands
    }
    
    //==============================================================================================
    // Метод для задания объекта GestureListener
    public static func setListAllDressBrands(listAllDressBrands: Dictionary<String, String>) {
        DBMain.mListAllDressBrands = listAllDressBrands
    }
    
    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для мужской одежды
    public static func getListBaseCategoriesDressIdMan() -> Dictionary<String, String>? {
        return DBMain.mListBaseCategoriesDressIdMan
    }
    
    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для мужской одежды
    public static func setListBaseCategoriesDressIdMan(listBaseCategoriesDressIdMan: Dictionary<String, String>) {
        DBMain.mListBaseCategoriesDressIdMan = listBaseCategoriesDressIdMan
    }
    
    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для женской одежды
    public static func getListBaseCategoriesDressIdWoman() -> Dictionary<String, String>? {
        return DBMain.mListBaseCategoriesDressIdWoman
    }
    
    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для женской одежды
    public static func setListBaseCategoriesDressIdWoman(listBaseCategoriesDressIdWoman: Dictionary<String, String>) {
        DBMain.mListBaseCategoriesDressIdWoman = listBaseCategoriesDressIdWoman
    }
    
    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для детской одежды
    public static func getListBaseCategoriesDressIdKid() -> Dictionary<String, String>? {
        return DBMain.mListBaseCategoriesDressIdKid
    }
    
    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для детской одежды
    public static func setListBaseCategoriesDressIdKid(listBaseCategoriesDressIdKid: Dictionary<String, String>) {
        DBMain.mListBaseCategoriesDressIdKid = listBaseCategoriesDressIdKid
    }
    
    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    public static func getListBaseCategoriesDressId(dressForWho: Int) -> Dictionary<String, String>? {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getListBaseCategoriesDressIdMan()
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getListBaseCategoriesDressIdWoman()
            case GlobalFlags.DRESS_KID:
                return DBMain.getListBaseCategoriesDressIdKid()
            default:
                return nil
        }
    }
    
    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    // listBaseCategoriesDressId - список id базовых категорий одежды
    public static func setListBaseCategoriesDressId(dressForWho: Int, listBaseCategoriesDressId: Dictionary<String, String>) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setListBaseCategoriesDressIdMan(listBaseCategoriesDressId)
                break
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setListBaseCategoriesDressIdWoman(listBaseCategoriesDressId)
                break
            case GlobalFlags.DRESS_KID:
                DBMain.setListBaseCategoriesDressIdKid(listBaseCategoriesDressId)
                break
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для мужской одежды
    public static func getListCategoriesDressMan() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mListCategoriesDressMan
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для мужской одежды
    public static func setListCategoriesDressMan(listCategoriesDressMan: Dictionary<String, [Dictionary<String, String>]>?) {
        DBMain.mListCategoriesDressMan = listCategoriesDressMan
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для женской одежды
    public static func getListCategoriesDressWoman() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mListCategoriesDressWoman
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для женской одежды
    public static func setListCategoriesDressWoman(listCategoriesDressWoman: Dictionary<String, [Dictionary<String, String>]>?) {
        DBMain.mListCategoriesDressWoman = listCategoriesDressWoman
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для детской одежды
    public static func getListCategoriesDressKid() -> Dictionary<String, [Dictionary<String, String>]>? {
        return DBMain.mListCategoriesDressKid
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для детской одежды
    public static func setListCategoriesDressKid(listCategoriesDressKid: Dictionary<String, [Dictionary<String, String>]>?) {
        DBMain.mListCategoriesDressKid = listCategoriesDressKid
    }
    
    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего сведения о категориях одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    public static func getListCategoriesDress(dressForWho: Int) -> Dictionary<String, [Dictionary<String, String>]>? {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getListCategoriesDressMan()
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getListCategoriesDressWoman()
            case GlobalFlags.DRESS_KID:
                return DBMain.getListCategoriesDressKid()
            default:
                return nil
        }
    }
    
    //==============================================================================================
    // Метод для задания многомерного массива, хранящего сведения о категориях одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    public static func setListCategoriesDress(dressForWho: Int, listCategoriesDress: Dictionary<String, [Dictionary<String, String>]>?) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setListCategoriesDressMan(listCategoriesDress)
                break
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setListCategoriesDressWoman(listCategoriesDress)
                break
            case GlobalFlags.DRESS_KID:
                DBMain.setListCategoriesDressKid(listCategoriesDress)
                break
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для определения групп одежды, вещи из которых присутствуют в данный момент
    // на виртуальном манекене, кроме группы "Аксессуары"
    public static func getArrayDressGroupExists() -> [String]? {
        // Возвращаемый массив
        var arrayDressGroupExists: [String]?
    
        // В цикле перебираем все типы одежды
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            arrayDressGroupExists = [String]()
    
            for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                switch (GlobalFlags.getArrayTagDressType()[indexDressType]) {
                    case GlobalFlags.TAG_DRESS_HEAD,
                         GlobalFlags.TAG_DRESS_BODY,
                         GlobalFlags.TAG_DRESS_LEG,
                         GlobalFlags.TAG_DRESS_FOOT:
                        
                        if (DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.getArrayTagDressType()[indexDressType]] != nil) {
                            if (DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.getArrayTagDressType()[indexDressType]]!.getCount() > 0) {
                                arrayDressGroupExists!.append(GlobalFlags.getArrayTagDressType()[indexDressType])
                            }
                        }
        
                        break
                    
                    default:
                        break
                }
            }
        }
    
        return arrayDressGroupExists
    }
    
    //==============================================================================================
    // Метод для проверки существования информации о запрашиваемой одежде в глобальном массиве
    public static func checkDressInGlobalArray(currentDressId: Int, dressForWho: Int, dressType: String) -> Bool {
        // Возвращаемое значение
        var isDressInLocalArray: Bool = false
    
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        let arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            // Считываем информацию об одежде для соответствуюещго типа (головные уборы, обувь и т.д.)
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                // информация о которых представлена в массивах ArrayCurrentDressInfoMan, ArrayCurrentDressInfoWoman
                // или ArrayCurrentDressInfoKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id одной из вещей из соответствуюещго массива
                    var dressIdInLocalArray: Int = 0
    
                    if (arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID] != nil) {
                        dressIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID]!)!
                    }
        
                    // Если id совпали, то завершаем выполнение цикла
                    if (currentDressId == dressIdInLocalArray) {
                        isDressInLocalArray = true
                        break
                    }
                }
            }
        }
    
        return isDressInLocalArray
    }
    
    //==============================================================================================
    // Метод для проверки существования информации об одежде в глобальном массиве для необходимой категории
    // Передаваемые параметры
    // currentCategoryDressId - id категории, для которой необходимо проверить существование информации
    //                          об одежде в глобальном массиве
    // dressForWho            - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType              - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static func checkCategoryDressInGlobalArray(currentCategoryDressId: Int, dressForWho: Int, dressType: String) -> Bool {
        // Возвращаемое значение
        var isCategoryDressInLocalArray: Bool = false
    
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        let arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id категории для текущей вещи с id категории хотя бы для одной из вещей,
                // информация о которых представлена в глобальных массивах mListCategoriesDressMan, mListCategoriesDressWoman
                // или mListCategoriesDressKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id категории для одной из вещей из соответствуюещго массива
                    var categoryDressIdInLocalArray: Int = 0
    
                    if (arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_CATID] != nil) {
                        categoryDressIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_CATID]!)!
                    }
    
    
                    // Если id совпали, то завершаем выполнение цикла
                    if (currentCategoryDressId == categoryDressIdInLocalArray) {
                        isCategoryDressInLocalArray = true
                        break
                    }
                }
            }
        }
    
        return isCategoryDressInLocalArray
    }
    
    //==============================================================================================
    // Метод для вставки данных об определенной вещи в глобальный общий массив
    // Передаваемые параметры
    // currentDressId        - id текущей одежды, информацию о которой необходимо добавить в глобальный массив
    // dressForWho           - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType             - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    // arrayCurrentDressInfo - массив, содержащий информацию о текущей одежде, информацию о которой
    //                         необходимо добавить в глобальный массив
    public static func addDressToGlobalArray(currentDressId: Int, dressForWho: Int, dressType: String, arrayCurrentDressInfo: Dictionary<String, String>) {
        // Сначала проверяем не присутствуют ли данные о текущей одежде в общем глобальном массиве
        let isDressInLocalArray: Bool = DBMain.checkDressInGlobalArray(currentDressId, dressForWho: dressForWho, dressType: dressType)
    
        // Если данные о текущей вещи присутствуют в общем глобальном массиве, то удаляем
        // данные об указанной вещи из данного общего глобального массива
        if(isDressInLocalArray == true) {
            DBMain.deleteDressFromGlobalArrayById(currentDressId, dressForWho: dressForWho, dressType: dressType)
        }
    
        // Теперь вставляем данные о текущей вещи в общий глобальный массив
        switch (dressForWho) {
            case GlobalFlags.DRESS_MAN:                                 // для мужской одежды
                if (DBMain.getArrayCurrentDressInfoMan() == nil) {
                    DBMain.setArrayCurrentDressInfoMan(Dictionary<String, [Dictionary<String, String>]>())
                }
    
                var arrayCurrentDressInfoMan: Dictionary<String, [Dictionary<String, String>]> = DBMain.getArrayCurrentDressInfoMan()!
                
                if(arrayCurrentDressInfoMan[dressType] == nil) {
                    arrayCurrentDressInfoMan[dressType] = [Dictionary<String, String>]()
                }
                    
                arrayCurrentDressInfoMan[dressType]!.append(arrayCurrentDressInfo)
                
                DBMain.setArrayCurrentDressInfoMan(arrayCurrentDressInfoMan)
    
                break
            
            case GlobalFlags.DRESS_WOMAN:                               // для женской одежды
                if (DBMain.getArrayCurrentDressInfoWoman() == nil) {
                    DBMain.setArrayCurrentDressInfoWoman(Dictionary<String, [Dictionary<String, String>]>())
                }
                
                var arrayCurrentDressInfoWoman: Dictionary<String, [Dictionary<String, String>]> = DBMain.getArrayCurrentDressInfoWoman()!
                
                if(arrayCurrentDressInfoWoman[dressType] == nil) {
                    arrayCurrentDressInfoWoman[dressType] = [Dictionary<String, String>]()
                }
                    
                arrayCurrentDressInfoWoman[dressType]!.append(arrayCurrentDressInfo)
                
                DBMain.setArrayCurrentDressInfoWoman(arrayCurrentDressInfoWoman)
                
                break
    
            case GlobalFlags.DRESS_KID:                                 // для детской одежды
                if (DBMain.getArrayCurrentDressInfoKid() == nil) {
                    DBMain.setArrayCurrentDressInfoKid(Dictionary<String, [Dictionary<String, String>]>())
                }
    
                var arrayCurrentDressInfoKid: Dictionary<String, [Dictionary<String, String>]> = DBMain.getArrayCurrentDressInfoKid()!
                
                if(arrayCurrentDressInfoKid[dressType] == nil) {
                    arrayCurrentDressInfoKid[dressType] = [Dictionary<String, String>]()
                }
                
                arrayCurrentDressInfoKid[dressType]!.append(arrayCurrentDressInfo)
                
                DBMain.setArrayCurrentDressInfoKid(arrayCurrentDressInfoKid)
    
                break
            
            default:
                break
        }
    }
    
    //==============================================================================================
    // Метод для удаления данных об указанной вещи из общего глобального массива по id вещи
    // Передаваемые параметры
    // currentDressId - id текущей одежды, информацию о которой необходимо удалить из глобального массива
    // dressForWho    - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType      - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static func deleteDressFromGlobalArrayById(currentDressId: Int, dressForWho: Int, dressType: String) {
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        var arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id одной из вещей из соответствуюещго массива
                    var dressIdInLocalArray: Int = 0
   
                    if (arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID] != nil) {
                        dressIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID]!)!
                    }
    
                    // Если id совпали, то удаляем данные о текущей одежде
                    if (currentDressId == dressIdInLocalArray) {
                        arrayCurrentDressInfo![dressType]!.removeAtIndex(indexDress)
    
                        // Если больше не осталось вещей такого же типа, то удаляем сам подмассив
                        if (arrayCurrentDressInfo![dressType]!.count <= 0) {
                            arrayCurrentDressInfo!.removeValueForKey(dressType)
                        }
    
                        // Обновляем теперь непосредственно глобальный массив
                        DBMain.setArrayCurrentDressInfo(dressForWho, arrayCurrentDressInfo: arrayCurrentDressInfo!)
    
                        break
                    }
                }
            }
        }
    }
    
    //==============================================================================================
    // Метод для удаления данных об указанной вещи из общего глобального массива по id категории вещи
    // Передаваемые параметры
    // currentDressCategoryId - id категории текущей одежды, информацию о которой необходимо удалить из глобального массива
    // dressForWho            - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType              - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static func deleteDressFromGlobalArrayByCategoryId(currentDressCategoryId: Int, dressForWho: Int, dressType: String) {
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        var arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id категории текущей вещи с id категории одной из вещей,
                // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id категории одной из вещей из соответствуюещго массива
                    var dressCategoryIdInLocalArray: Int = 0
    
                    if(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_CATID] != nil) {
                        dressCategoryIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_CATID]!)!
                    }
                    
                    // Если id категорий совпали, то удаляем данные о текущей одежде
                    if (currentDressCategoryId == dressCategoryIdInLocalArray) {
                        arrayCurrentDressInfo![dressType]!.removeAtIndex(indexDress)
    
                        // Если больше не осталось вещей такого же типа, то удаляем сам подмассив
                        if (arrayCurrentDressInfo![dressType]!.count <= 0) {
                            arrayCurrentDressInfo!.removeValueForKey(dressType)
                        }
    
                        // Обновляем теперь непосредственно глобальный массив
                        DBMain.setArrayCurrentDressInfo(dressForWho, arrayCurrentDressInfo: arrayCurrentDressInfo!)
    
                        break
                    }
                }
            }
        }
    }
    
    //==============================================================================================
    // Метод для извлечения данных об указанной вещи из общего глобального массива по ее id
    // Передаваемые параметры
    // currentDressId - id текущей одежды, информацию о которой необходимо извлечь из глобального массива
    // dressForWho    - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType      - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static func getDressFromGlobalArrayById(currentDressId: Int, dressForWho: Int, dressType: String) -> Dictionary<String, String>? {
        // Возвращаемые ассоциативный массив
        var returnArrayDressInfo: Dictionary<String, String>?
    
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        var arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id одной из вещей из соответствуюещго массива
                    var dressIdInLocalArray: Int = 0
    
                    if (arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID] != nil) {
                        dressIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID]!)!
                    }
    
                    // Если id совпали, то возвращаем данные о текущей одежде
                    if (currentDressId == dressIdInLocalArray) {
                        returnArrayDressInfo = arrayCurrentDressInfo![dressType]![indexDress]
    
                        break
                    }
                }
            }
        }
    
        return returnArrayDressInfo
    }
    
    //==============================================================================================
    // Метод для извлечения только одного поля об указанной вещи из общего глобального массива по ее id
    // Передаваемые параметры
    // currentDressId - id вещи, для которой необходимо считать данные
    // dressForWho - параметр "for_who" вещи, для которой необходимо считать данные
    // dressType - тип вещи, для которой необходимо считать данные
    // fieldName - название поля, для которого необходимо считать данные
    public static func getDressFieldFromGlobalArrayById(currentDressId: Int, dressForWho: Int, dressType: String, fieldName: String) -> String? {
        // Возвращаемая переменная
        var returnDressFieldValue: String?
    
        // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
        var arrayCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        if (arrayCurrentDressInfo != nil) {
            if (arrayCurrentDressInfo![dressType] != nil) {
                // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                for indexDress in 0..<arrayCurrentDressInfo![dressType]!.count {
                    // Считываем id одной из вещей из соответствуюещго массива
                    var dressIdInLocalArray: Int = 0
    
                    if (arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID] != nil) {
                        dressIdInLocalArray = Int(arrayCurrentDressInfo![dressType]![indexDress][GlobalFlags.TAG_ID]!)!
                    }

                    // Если id совпали, то возвращаем данные о текущей одежде
                    if (currentDressId == dressIdInLocalArray) {
                        let currentDressInfo: Dictionary<String, String> = arrayCurrentDressInfo![dressType]![indexDress]
    
                        if (currentDressInfo[fieldName] != nil) {
                            returnDressFieldValue = currentDressInfo[fieldName]
                        }
    
                        break
                    }
                }
            }
        }
    
        return returnDressFieldValue
    }
    
    //==============================================================================================
    // Метод для считывания информации о соответствующей одежде из локальной БД
    // Передаваемые параметры
    // currentDressId - id вещи, сведения о которой необходимо скачать из локальной БД
    // isAddToLocalArray - логическая переменная, которая определяет сохранять ли данные
    //                     о текущей одежде (вещи) в глобальный массив
    internal static func getDressFromLocalDBById(currentDressId: Int, isAddToLocalArray: Bool) -> Dictionary<String, String>? {
        // Возвращаемые ассоциативный массив
        var returnArrayDressInfo: Dictionary<String, String>?
    
        // Считываем информацию о текущей вещи (одежде) из таблицы "dress"
        let currentDressInfo: Dress? = DBMain.getDBSQLiteHelper()!.getRecordFromDBByIdServerMySQL(
                                    GlobalFlags.TAG_TABLE_DRESS,
                                    idServerMySQL: currentDressId
        ) as? Dress
    
        if(currentDressInfo != nil) {
            // Извлекаем и помещаем всю информацию о текущей одежде в возвращаемый массив
            returnArrayDressInfo = Dictionary<String, String>()
            
            returnArrayDressInfo![GlobalFlags.TAG_ID] = String(currentDressInfo!.id)            // id текущей вещи в удаленной БД
            returnArrayDressInfo![GlobalFlags.TAG_CATID] = String(currentDressInfo!.catid)      // id категории, к которой относится текущая вещь (одежда)
            returnArrayDressInfo![GlobalFlags.TAG_TITLE] = currentDressInfo!.title              // название текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_ALIAS] = currentDressInfo!.alias              // алиас для названия текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_FOR_WHO] = currentDressInfo!.for_who          // для кого предназначена данная вещь (одежда)
            returnArrayDressInfo![GlobalFlags.TAG_TYPE] = currentDressInfo!.type                // тип текущей вещи (для мужчины, для женщины или для детей)
            returnArrayDressInfo![GlobalFlags.TAG_BRAND_ID] = String(currentDressInfo!.brand_id)// id бренда, к которому относится текущая вещь (одежда)
            returnArrayDressInfo![GlobalFlags.TAG_THUMB] = currentDressInfo!.thumb              // ссылка на файл-превью-изображение для текущей вещи
            returnArrayDressInfo![GlobalFlags.TAG_THUMB_WIDTH] = String(currentDressInfo!.thumb_width)  // ширина файла-превью-изображения для текущей вещи
            returnArrayDressInfo![GlobalFlags.TAG_THUMB_HEIGHT] = String(currentDressInfo!.thumb_height)// высота файла-превью-изображения для текущей вещи
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE] = currentDressInfo!.image               // ссылка на файл-изображение для текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE_WIDTH] = String(currentDressInfo!.image_width)  // ширина (в пикселях) файла-изображение для текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE_HEIGHT] = String(currentDressInfo!.image_height)// высота (в пикселях) файла-изображение для текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE_BACK] = currentDressInfo!.image_back     // ссылка на файл-изображение для задней (тыловой) стороны текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE_BACK_WIDTH] = String(currentDressInfo!.image_back_width)    // ширина (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = String(currentDressInfo!.image_back_height)  // высота (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_COLOR] = currentDressInfo!.color               // цвет текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_STYLE] = currentDressInfo!.style               // стиль текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_SHORT_DESCRIPTION] = currentDressInfo!.short_desc     // краткое описание текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_DESCRIPTION] = currentDressInfo!.desc                 // описание текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_HITS] = String(currentDressInfo!.hits)                // рейтинг текущей вещи (одежды)
            returnArrayDressInfo![GlobalFlags.TAG_DRESS_DEFAULT] = String(currentDressInfo!.dress_default)  // логическая переменная, определяющая отображать ли данную вещь по умолчанию при первоначальном заходе пользователя на страницу
         
            //--------------------------------------------------------------------------------------
            // Считываем информацию о категории для текущей вещи (одежде) из таблицы "category"
            let currentCategoryInfo: Categories? = DBMain.getDBSQLiteHelper()!.getRecordFromDBByIdServerMySQL(
                                    GlobalFlags.TAG_TABLE_CATEGORIES,
                                    idServerMySQL: Int(currentDressInfo!.catid)
            ) as? Categories
    
            // Добавляем в возвращаемый массив сведения о текущей категории
            if (currentCategoryInfo != nil) {
                // Добавляем название текущей категории
                returnArrayDressInfo![GlobalFlags.TAG_CATEGORY_TITLE] = currentCategoryInfo!.title
            }
    
            //-------------------------------------------------------------------------------------
            // Считываем информацию о бренде для текущей вещи (одежде) из таблицы "brand"
            let currentBrandInfo: Brand? = DBMain.getDBSQLiteHelper()!.getRecordFromDBByIdServerMySQL(
                                    GlobalFlags.TAG_TABLE_BRAND,
                                    idServerMySQL: Int(currentDressInfo!.brand_id)
            ) as? Brand
    
            // Добавляем в возвращаемый массив сведения о текущем бренде
            if (currentBrandInfo != nil) {
                // Добавляем название текущего бренда
                returnArrayDressInfo![GlobalFlags.TAG_BRAND_TITLE] = currentBrandInfo!.title
            }
        
            //-------------------------------------------------------------------------------------
            // При необходимости добавляем данные о текущей вещи (одежде) в глобальный массив
            if (isAddToLocalArray == true) {
                DBMain.addDressToGlobalArray(currentDressId,
                    dressForWho: Functions.dressForWhoStringToInt(currentDressInfo!.for_who),
                    dressType: currentDressInfo!.type,
                    arrayCurrentDressInfo: returnArrayDressInfo!
                )
            }
        }
        
        return returnArrayDressInfo
    }
    
    //==============================================================================================
    // Метод для формирования ассоциативного массива, содержащего id вещей, одетых в данный
    // момент на виртуальном манекене для каждой группы одежды
    // Передаваемые параметры
    // dressForWho - для кого предназначены вещи, из id которых необходимо сформировать данный массив
    // В данном ассоциативном массиве:
    // Ключ - название группы одежды (головные уборы, обувь и т.д.)
    // Значение - строка, содержащая id одежды, которая одета в текущий момент на виртуальном
    //            манекене, для соответствующей группы одежды.
    //            При этом id для одной группы разделены между собой знаком тройного подчеркивания "___"
    public static func createArrayListDressId(dressForWhoParam: Int) -> Dictionary<String, String>? {
        // Возвращаемый ассоциативный массив
        var arrayListDressId: Dictionary<String, String>?
    
        var dressForWho: Int = dressForWhoParam
        
        if(dressForWho != GlobalFlags.DRESS_MAN && dressForWho != GlobalFlags.DRESS_WOMAN && dressForWho != GlobalFlags.DRESS_KID) {
            dressForWho = GlobalFlags.getDressForWho()
        }
    
        // Считываем многомерный массив, хранящий информацию об одежде, одетой в данный момент на виртуальный манекен
        let listCurrentDressInfo: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getArrayCurrentDressInfo(dressForWho)
    
        // Если многомерный массив, хранящий информацию об одежде, которая в данный момент
        // присутствует на виртуальном манекене, НЕ пуст
        if (listCurrentDressInfo != nil) {
            // Инициализируем ассоциативный массив
            arrayListDressId = Dictionary<String, String>()
    
            // В цикле перебираем каждую группу одежды
            for (listCurrentDressInfoKey, listDressInfoForCurrentGroup) in listCurrentDressInfo! {
                // Переменная, ранящая id вещей для текущей группы, которые в данный момент присутствуют на виртуальном манекене
                var stringListDressIdForCurrentGroup: String = ""
    
                // В цикле разбираем всю одежду для текущей группы
                for indexListDressInfoForCurrentGroup in 0..<listDressInfoForCurrentGroup.count {
                    // Извлекаем id текущей вещи
                    var currentDressId: String = "0"
    
                    if (listDressInfoForCurrentGroup[indexListDressInfoForCurrentGroup][GlobalFlags.TAG_ID] != nil) {
                        currentDressId = listDressInfoForCurrentGroup[indexListDressInfoForCurrentGroup][GlobalFlags.TAG_ID]!
                    }
                    
                    stringListDressIdForCurrentGroup += currentDressId
    
                    // Если это не последняя вещь в списке для текущей группы, то добавляем
                    // в конец знак разделителя "___"
                    if (indexListDressInfoForCurrentGroup < listDressInfoForCurrentGroup.count - 1) {
                        stringListDressIdForCurrentGroup += "___"
                    }
                }
                
                // Добавляем в возвращаемый массив список id одежды, присутствующей на виртуальном манекене, для текущей группы
                arrayListDressId![listCurrentDressInfoKey] = stringListDressIdForCurrentGroup
            }
        }
    
        return arrayListDressId
    }
    
    //==============================================================================================
    // Метод для формирования ассоциативного массива, содержащего id вещей, одетых в данный
    // момент на виртуальном манекене для каждой группы одежды
    // Передаваемые параметры
    // dressTypeExclude - тип одежды, который необходимо исключить из формируемого ассоциативного массива
    // Возвращаемое значение
    // arrayListDressId - ассоциативный массив
    // В данном ассоциативном массиве:
    // Ключ - название группы одежды (головные уборы, обувь и т.д.)
    // Значение - строка, содержащая id одежды, которая одета в текущий момент на виртуальном
    //            манекене, для соответствующей группы одежды.
    //            При этом id для одной группы разделены между собой знаком тройного подчеркивания "___"
    public static func createArrayListDressId(dressTypeExcludeParam: String?) -> Dictionary<String, String>? {
        // Возвращаемый ассоциативный массив
        var arrayListDressId: Dictionary<String, String>?
    
        var dressTypeExclude: String? = dressTypeExcludeParam
        
        if(dressTypeExclude == nil) {
            dressTypeExclude = ""
        }
    
        if (DBMain.getArrayPagerAdapterDressroom() != nil) {
            // В цикле перебираем все типы одежды
            for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                let dressTypeCurrent: String = GlobalFlags.getArrayTagDressType()[indexDressType]
    
                if(dressTypeCurrent != dressTypeExclude) {
                    if (DBMain.getArrayPagerAdapterDressroom()![dressTypeCurrent] != nil) {
                        // Ссылка на текущий адаптер
                        let currentPagerAdapterDressroom: PagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom()![dressTypeCurrent]!
                        
                        // Считываем параметры для одежды для текущего типа
                        let currentItemParams: Dictionary<String, String>? = currentPagerAdapterDressroom.getItemParams(currentPagerAdapterDressroom.getCurrentItemPosition())
    
                        if (currentItemParams != nil) {
                            if (currentItemParams![GlobalFlags.TAG_ID] != nil) {
                                if (arrayListDressId == nil) {
                                    arrayListDressId = Dictionary<String, String>()
                                }
    
                                arrayListDressId![GlobalFlags.getArrayTagDressType()[indexDressType]] = currentItemParams![GlobalFlags.TAG_ID]
                            }
                        }
                    }
                }
            }
        }

        return arrayListDressId
    }
    
    //==============================================================================================
    // Метод для загрузки всей информации, необходимой для первоначальной инициализации приложения
    public static func synchronize() {
        // Запускаем считывание информации о базовых категориях одежды
        // При этом указываем, что после считывания информации о базовых категориях одежды
        // необходимо считать информацию об остальных категориях одежды
        DBMain.synchronizeBaseDressCategories(GlobalFlags.ACTION_GET_ALL_DRESS_CATEGORIES)
    }
    
    //==============================================================================================
    // Метод для загрузки информации о базовых категориях одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static func synchronizeBaseDressCategories(nextAction: Int) {
        if(DBMain.getMySQLGetBaseDressCategories() == nil) {
            DBMain.setMySQLGetBaseDressCategories(MySQLGetBaseDressCategories())
        }
    
        DBMain.getMySQLGetBaseDressCategories()!.startGetBaseCategoriesDress(nextAction)
    }

    //==============================================================================================
    // Метод для загрузки информации обо всех категориях одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isShowProgressDialogGetAllDressCategories - логическая переменная, определяющая отображать или нет модальное окно загрузки
    // isCheckLocalDB - логическая переменная, определяющая необходимо ли проверять наличие записей
    //                  о категориях одежды в локальной БД
    public static func synchronizeAllDressCategories(nextAction: Int, isShowProgressDialogGetAllDressCategories: Bool, isCheckLocalDB: Bool) {
        if(DBMain.getMySQLGetAllDressCategories() == nil) {
            DBMain.setMySQLGetAllDressCategories(MySQLGetAllDressCategories())
        }
    
        DBMain.getMySQLGetAllDressCategories()!.startGetAllDressCategories(
            nextAction,
            isShowProgressDialogGetAllDressCategories: isShowProgressDialogGetAllDressCategories,
            isCheckLocalDB: isCheckLocalDB
        )
    }
    
    //==============================================================================================
    // Метод для загрузки информации обо всех брендах одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static func synchronizeAllDressBrands(nextAction: Int) {
        if(DBMain.getMySQLGetAllDressBrands() == nil) {
            DBMain.setMySQLGetAllDressBrands(MySQLGetAllDressBrands())
        }
    
        DBMain.getMySQLGetAllDressBrands()!.startGetAllDressBrands(nextAction)
    }
        
    //==============================================================================================
    // Метод для загрузки информации об одежде по умолчанию из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isProgressDialogShow - необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
    // textViewError - строка для вывода сообщений об ошибках
    public static func synchronizeDressDefault(nextAction: Int, isProgressDialogShow: Bool) {
        if(DBMain.getMySQLGetDressDefault() == nil) {
            DBMain.setMySQLGetDressDefault(MySQLGetDressDefault(context: DBMain.getContext()!))
        }
    
        DBMain.getMySQLGetDressDefault()!.startGetDressDefault(nextAction, isProgressDialogShow: isProgressDialogShow)
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде, просматриваемой в последний раз
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isProgressDialogShow - логическая переменная, указывающая необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
    public static func synchronizeGoToDressLastView(nextAction: Int, isProgressDialogShow: Bool) {
        if(DBMain.getMySQLGoToDressLastView() == nil) {
            DBMain.setMySQLGoToDressLastView(MySQLGoToDressLastView(context: DBMain.getContext()!))
        }
    
        DBMain.getMySQLGoToDressLastView()!.startGoToDressLastView(nextAction, isProgressDialogShow: isProgressDialogShow)
    }
    
    //==============================================================================================
    // Метод, запускающий процесс сохранения набора одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // arrayDressListId - массив, хранящий id одежды, информацию о которой необходимо считать
    // buttonDressSaveImageView - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя (тип UIImageView)
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static func startDressCollectionSave(nextAction: Int, dressCollectionType: String, arrayDressListId: Dictionary<String, String>?,
                                                buttonDressSaveImageView: UIImageView, isShowProgressDialogDressCollectionSave: Bool) {
        if(DBMain.getMySQLDressCollectionSave() == nil) {
            DBMain.setMySQLDressCollectionSave(MySQLDressCollectionSave())
        }
    
        DBMain.getMySQLDressCollectionSave()!.startDressCollectionSave(
            nextAction,
            dressCollectionType: dressCollectionType,
            arrayDressListId: arrayDressListId,
            buttonDressSaveImageView: buttonDressSaveImageView,
            isShowProgressDialogDressCollectionSave: isShowProgressDialogDressCollectionSave
        )
    }
    
    //==============================================================================================
    // Метод, запускающий процесс сохранения набора одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // arrayDressListId - массив, хранящий id одежды, информацию о которой необходимо считать
    // buttonDressSaveBarButtonItem - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя (тип UIBarButtonItem)
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static func startDressCollectionSave(nextAction: Int, dressCollectionType: String, arrayDressListId: Dictionary<String, String>?,
                                                buttonDressSaveBarButtonItem: UIBarButtonItem, isShowProgressDialogDressCollectionSave: Bool) {
        if(DBMain.getMySQLDressCollectionSave() == nil) {
            DBMain.setMySQLDressCollectionSave(MySQLDressCollectionSave())
        }
        
        DBMain.getMySQLDressCollectionSave()!.startDressCollectionSave(
            nextAction,
            dressCollectionType: dressCollectionType,
            arrayDressListId: arrayDressListId,
            buttonDressSaveBarButtonItem: buttonDressSaveBarButtonItem,
            isShowProgressDialogDressCollectionSave: isShowProgressDialogDressCollectionSave
        )
    }
    
    //==============================================================================================
    // Метод, запускающий процесс удаления информации о текущем наборе одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // collectionUnSaveId - id коллекции, информацию о которой необходимо удалить из БД для текущего пользователя
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // buttonDressSaveImageView - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя (тип UIImageView)
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static func startDressCollectionUnSave(nextAction: Int, collectionUnSaveId: Int, dressCollectionType: String,
                                                  buttonDressSaveImageView: UIImageView, isShowProgressDialogDressCollectionSave: Bool) {
        if(DBMain.getMySQLDressCollectionSave() == nil) {
            DBMain.setMySQLDressCollectionSave(MySQLDressCollectionSave())
        }
    
        DBMain.getMySQLDressCollectionSave()!.startDressCollectionUnSave(
            nextAction,
            collectionUnSaveId: collectionUnSaveId,
            dressCollectionType: dressCollectionType,
            buttonDressSaveImageView: buttonDressSaveImageView,
            isShowProgressDialogDressCollectionSave: isShowProgressDialogDressCollectionSave
        )
    }
    
    //==============================================================================================
    // Метод, запускающий процесс удаления информации о текущем наборе одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // collectionUnSaveId - id коллекции, информацию о которой необходимо удалить из БД для текущего пользователя
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // buttonDressSaveBarButtonItem - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя (тип UIBarButtonItem)
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static func startDressCollectionUnSave(nextAction: Int, collectionUnSaveId: Int, dressCollectionType: String,
                                                  buttonDressSaveBarButtonItem: UIBarButtonItem, isShowProgressDialogDressCollectionSave: Bool) {
        if(DBMain.getMySQLDressCollectionSave() == nil) {
            DBMain.setMySQLDressCollectionSave(MySQLDressCollectionSave())
        }
        
        DBMain.getMySQLDressCollectionSave()!.startDressCollectionUnSave(
            nextAction,
            collectionUnSaveId: collectionUnSaveId,
            dressCollectionType: dressCollectionType,
            buttonDressSaveBarButtonItem: buttonDressSaveBarButtonItem,
            isShowProgressDialogDressCollectionSave: isShowProgressDialogDressCollectionSave
        )
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных о текущем отображаемом наборе одежды
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static func synchronizeDressCollectionInfo(nextAction: Int) {
        if(DBMain.getMySQLDressCollectionLoad() == nil) {
            DBMain.setMySQLDressCollectionLoad(MySQLDressCollectionLoad())
        }
    
        DBMain.getMySQLDressCollectionLoad()!.startDressCollectionLoad(nextAction)
    }
}