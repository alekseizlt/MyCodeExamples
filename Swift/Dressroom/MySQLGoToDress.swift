import Foundation
import SwiftyJSON

// Класс для считывания информации о предыдущей или следующей одежде
public class MySQLGoToDress {

    // Свойства данного класса
    private var mContext: ViewControllerMain?               // контекст
    private var mNextAction: Int = GlobalFlags.ACTION_NO    // флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    private var mDressId: Int = 0                           // id текущей или крайней (первой или последней) вещи в зависимости от значения переменной mActionSubType
    private var mSwipeDirection: Int = 0                    // направление листание одежды пальцем
    private var mIsDressImageLoad: Bool = true              // необходимо ли загружать с сервера изображение для текущей одежды
    private var mIsProgressDialogShow: Bool = true          // необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
    
    // Переменная, определяющая тип поддействия относительно действия считывания информации о текущей одежде
    private var mActionSubType: Int = 0
    
    // Параметры одежды, в соответствии с которыми необходимо считать информацию об одежде с сервера
    private var mTargetDressCategoryId: String?        		// id категории для текущей вещи
    private var mTargetDressType: String?          			// тип текущих вещей (головные уборы, обувь и т.д.)
    private var mTargetDressColor: String?             		// цвет текущей вещи
    private var mTargetDressStyle: String?                  // стиль текущей вещи
    
    private var mCollectionIdForDressShowNow: Int = 0       // id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    
    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    init(context: ViewControllerMain?) {
        // Инициализируем переменные-свойства для текущего класса
        self.setContext(context)
        self.setCollectionIdForDressShowNow(0)
    }
    
    //==============================================================================================
    // Метод для считывания значения контекста
    private func getContext() -> ViewControllerMain? {
        return self.mContext
    }
    
    //==============================================================================================
    // Метод для задания значения контекста
    private func setContext(context: ViewControllerMain?) {
        self.mContext = context
    }
    
    //==============================================================================================
    // Метод для считывания флага, определяющего какое действие будет выполнено после выполнения
    // текущей асинхронной операции
    private func getNextAction() -> Int {
        return self.mNextAction
    }
    
    //==============================================================================================
    // Метод для задания флага, определяющего какое действие будет выполнено после выполнения
    // текущей асинхронной операции
    private func setNextAction(nextAction: Int) {
        self.mNextAction = nextAction
    }
    
    //==============================================================================================
    // Метод для считывания id вещи, для которой необходимо получить информацию (картину) с сервера
    private func getDressId() -> Int {
        return self.mDressId
    }
    
    //==============================================================================================
    // Метод для задания id вещи, для которой необходимо получить информацию (картину) с сервера
    private func setDressId(dressId: Int) {
        self.mDressId = dressId
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, определяющей направление листание одежды пальцем
    private func getSwipeDirection() -> Int {
        return self.mSwipeDirection
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, определяющей направление листание одежды пальцем
    private func setSwipeDirection(swipeDirection: Int) {
        // Заметим, что возможные значения - это "left_to_right" или "right_to_left"
        // Если переданное в текущую функцию значение не совпадает ни с одним из разрешенных,
        // то устанавливаем в качестве значения по умолчанию - значение "left_to_right"
        if (swipeDirection != GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT && swipeDirection != GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT) {
            self.mSwipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT
        }
        // Иначе
        else {
            self.mSwipeDirection = swipeDirection
        }
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, определяющей необходимо ли
    // загружать с сервера изображение для текущей одежды
    private func getIsDressImageLoad() -> Bool {
        return self.mIsDressImageLoad
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, определяющей необходимо ли
    // загружать с сервера изображение для текущей одежды
    private func setIsDressImageLoad(isDressImageLoad: Bool) {
        self.mIsDressImageLoad = isDressImageLoad
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, определяющей необходимо ли
    // отображать ProgressDialog в процессе загрузке информации о текущей вещи
    private func getIsProgressDialogShow() -> Bool {
        return self.mIsProgressDialogShow
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, определяющей необходимо ли
    // отображать ProgressDialog в процессе загрузке информации о текущей вещи
    private func setIsProgressDialogShow(isProgressDialogShow: Bool) {
        self.mIsProgressDialogShow = isProgressDialogShow
    }
    
    //==============================================================================================
    // Метод для считывания значения переменной, определяющей тип поддействия
    // относительно действия считывания информации о текущей одежде
    private func getActionSubType() -> Int {
        return self.mActionSubType
    }
    
    //==============================================================================================
    // Метод для задания значения переменной, определяющей тип поддействия
    // относительно действия считывания информации о текущей одежде
    private func setActionSubType(actionSubType: Int) {
        self.mActionSubType = actionSubType
    }
    
    //==============================================================================================
    // Метод для считывания значения id категории, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func getTargetDressCategoryId() -> String? {
        return self.mTargetDressCategoryId
    }
    
    //==============================================================================================
    // Метод для задания значения id категории, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func setTargetDressCategoryId(targetDressCategoryId: String?) {
        self.mTargetDressCategoryId = targetDressCategoryId
    }
    
    //==============================================================================================
    // Метод для считывания значения типа одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func getTargetDressType() -> String? {
        return self.mTargetDressType
    }
    
    //==============================================================================================
    // Метод для задания значения типа одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func setTargetDressType(targetDressType: String?) {
        self.mTargetDressType = targetDressType
    }
    
    //==============================================================================================
    // Метод для считывания значения цвета одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func getTargetDressColor() -> String? {
        return self.mTargetDressColor
    }
    
    //==============================================================================================
    // Метод для задания значения цвета одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func setTargetDressColor(targetDressColor: String?) {
        self.mTargetDressColor = targetDressColor
    }
    
    //==============================================================================================
    // Метод для считывания значения стиля одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func getTargetDressStyle() -> String? {
        return self.mTargetDressStyle
    }
    
    //==============================================================================================
    // Метод для задания значения стиля одежды, в соответствии с которым необходимо
    // считать информацию об одежде с сервера
    private func setTargetDressStyle(targetDressStyle: String?) {
        self.mTargetDressStyle = targetDressStyle
    }
   
    //==============================================================================================
    // Метод для считывания id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private func getCollectionIdForDressShowNow() -> Int {
        return self.mCollectionIdForDressShowNow
    }
    
    //==============================================================================================
    // Метод для задания id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private func setCollectionIdForDressShowNow(collectionIdForDressShowNow: Int) {
        self.mCollectionIdForDressShowNow = collectionIdForDressShowNow
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде, отображаемой в текущий момент на виртуальном манекене
    // ПРИ КЛИКЕ ПАЛЬЦЕМ ПО ИЗОБРАЖЕНИЮ ОДЕЖДЫ
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // currentDressId - id вещи, информацию о которой необходимо считать из удаленной БД
    public func startGoToDress(nextAction: Int, currentDressId: Int) {
        // Задаем флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
        self.setNextAction(nextAction)
        
        // Задаем id вещи, информацию о которой необходимо считать из удаленной БД
        self.setDressId(currentDressId)
        
        // Задаем переменную, определяющую, что тип поддействия относительно действия
        // считывания информации о текущей одежде - КЛИК ПАЛЬЦЕМ ПО ИЗОБРАЖЕНИЮ ОДЕЖДЫ
        self.setActionSubType(GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_CLICK)
        
        // Задаем логическую переменную, указывающую необходимо ли загружать изображение для текущей вещи
        self.setIsDressImageLoad(false)
        
        // Задаем логическую переменную, определяющую необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
        self.setIsProgressDialogShow(true)
        
        // Проверяем наличие Интернет-соединения
        let isInternetConnection: Bool = FunctionsConnection.isInternetConnection()
        
        // Если Интернет-соединение присутствует, то загружаем данные об одежде из удаленной БД
        if(isInternetConnection == true) {
            self.executeLoadDressInfo()
        }
            // Иначе, если Интернет-соединение отсутствует, то загружаем данные об одежде из локальной БД
        else {
            let asyncTaskLoadDressInfoFromLocalDB: AsyncTaskLoadDressInfoFromLocalDB = AsyncTaskLoadDressInfoFromLocalDB(
                currentDressId: currentDressId,
                nextAction: nextAction
            )
            
            asyncTaskLoadDressInfoFromLocalDB.execute()
        }
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде,
    // отображаемой в текущий момент на виртуальном манекене
    // ПРИ ЗАГРУЗКЕ ОДЕЖДЫ С ДРУГИМИ ПАРАМЕТРАМИ
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // targetDressCategoryId - id категории для текущей вещи
    // targetDressType - тип текущих вещей (головные уборы, обувь и т.д.)
    // targetDressColor - цвет текущей вещи
    // targetDressStyle - стиль для текущей вещи
    // alertDialog - ссылка на объект AlertDialog, который необходимо закрыть после выполнения асинхронной
    //               операции считывания данных с сервера
    public func startGoToDress(nextAction: Int, targetDressCategoryId: String, targetDressType: String, targetDressColor: String, targetDressStyle: String) {
        // Задаем флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
        self.setNextAction(nextAction)
    
        // Задаем переменную, определяющую, что тип поддействия относительно действия
        // считывания информации о текущей одежде - ЗАГРУЗКА ОДЕЖДЫ С ДРУГИМИ ПАРАМЕТРАМИ
        self.setActionSubType(GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_PARAMS)
    
        // Задаем параметры, по которым необходимо считать информацию об одежде с сервера
        self.setTargetDressCategoryId(targetDressCategoryId)
        self.setTargetDressType(targetDressType)
        self.setTargetDressColor(targetDressColor)
        self.setTargetDressStyle(targetDressStyle)
    
        // Задаем логическую переменную, указывающую необходимо ли загружать изображение для текущей вещи
        self.setIsDressImageLoad(true)
    
        // Задаем логическую переменную, определяющую необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
        self.setIsProgressDialogShow(true)
    
        //------------------------------------------------------------------------------------------
        // Проверяем наличие Интернет-соединения
        let isInternetConnection: Bool = FunctionsConnection.isInternetConnection()
    
        // Если Интернет-соединение присутствует, то загружаем данные об одежде из удаленной БД
        if(isInternetConnection == true) {
            self.executeLoadDressInfo()
        }
        // Иначе, если Интернет-соединение отсутствует, то загружаем данные об одежде из локальной БД
        else {
            let asyncTaskLoadDressInfoForOtherParamsFromLocaDB: AsyncTaskLoadDressInfoForOtherParamsFromLocalDB = AsyncTaskLoadDressInfoForOtherParamsFromLocalDB(
                context: self.getContext(),
                targetDressCategoryId: targetDressCategoryId,
                targetDressType: targetDressType,
                targetDressColor: targetDressColor,
                targetDressStyle: targetDressStyle
            )
    
            asyncTaskLoadDressInfoForOtherParamsFromLocaDB.execute()
        }
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде,
    // отображаемой в текущий момент на виртуальном манекене
    // ПРИ ЗАГРУЗКЕ ОДЕЖДЫ ДЛЯ ДРУГОЙ КАТЕГОРИИ
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // targetDressCategoryId - id категории для текущей вещи
    // targetDressType - тип текущих вещей (головные уборы, обувь и т.д.)
    public func startGoToDress(nextAction: Int, targetDressCategoryId: String, targetDressType: String) {
        // Задаем флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
        self.setNextAction(nextAction)
    
        // Задаем переменную, определяющую, что тип поддействия относительно действия
        // считывания информации о текущей одежде - ЗАГРУЗКА ОДЕЖДЫ ДЛЯ ДРУГОЙ КАТЕГОРИИ
        self.setActionSubType(GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_CATEGORY)
    
        // Задаем id категории, для которой необходимо считать информацию об одежде с сервера
        self.setTargetDressCategoryId(targetDressCategoryId)
        self.setTargetDressType(targetDressType)
    
        // Задаем логическую переменную, указывающую необходимо ли загружать изображение для текущей вещи
        self.setIsDressImageLoad(true)
    
        // Задаем логическую переменную, определяющую необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
        self.setIsProgressDialogShow(true)
    
        //------------------------------------------------------------------------------------------
        // Проверяем наличие Интернет-соединения
        let isInternetConnection: Bool = FunctionsConnection.isInternetConnection()
    
        // Если Интернет-соединение присутствует, то загружаем данные об одежде из удаленной БД
        if(isInternetConnection == true) {
            self.executeLoadDressInfo()
        }
        // Иначе, если Интернет-соединение отсутствует, то загружаем данные об одежде из локальной БД
        else {
            let asyncTaskLoadDressInfoForOtherCategoryFromLocaDB: AsyncTaskLoadDressInfoForOtherCategoryFromLocalDB = AsyncTaskLoadDressInfoForOtherCategoryFromLocalDB(
                context: self.getContext(),
                targetDressCategoryId: targetDressCategoryId,
                targetDressType: targetDressType
            )
    
            asyncTaskLoadDressInfoForOtherCategoryFromLocaDB.execute()
        }
    }
    
    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде, отображаемой в текущий момент на виртуальном манекене
    // ПРИ ЛИСТАНИИ ОДЕЖДЫ ПАЛЬЦЕМ
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // swipeDirection - направление листание одежды пальцем
    // targetDressType - тип текущих вещей (головные уборы, обувь и т.д.)
    public func startGoToDress(nextAction: Int, swipeDirection: Int, targetDressType: String) {
        // Задаем флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
        self.setNextAction(nextAction)
    
        // Задаем переменную, определяющую, что тип поддействия относительно действия
        // считывания информации о текущей одежде - ЛИСТАНИЕ ОДЕЖДЫ ПАЛЬЦЕМ
        self.setActionSubType(GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_SWIPE)
    
        // Задаем переменную, определяющую направление листание одежды пальцем
        self.setSwipeDirection(swipeDirection)
    
        self.setTargetDressType(targetDressType)
    
        // Задаем логическую переменную, указывающую необходимо ли загружать изображение для текущей вещи
        self.setIsDressImageLoad(true)
    
        // Задаем логическую переменную, определяющую необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
        self.setIsProgressDialogShow(false)
    
        //------------------------------------------------------------------------------------------
        // Определяем id вещи, информацию о которой необходимо считать из БД, в зависимости от направления листания
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if (DBMain.getArrayPagerAdapterDressroom()![targetDressType] != nil) {
                let currentPagerAdapterDressroom: PagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom()![targetDressType]!
    
                switch (swipeDirection) {
                    case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                                 // листание слева направо
                        // Задаем id крайней (первой)
                        self.setDressId(currentPagerAdapterDressroom.getItemParamsId(0))
    
                        break
    
                    case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:
                        // Задаем id последней вещи
                        self.setDressId(currentPagerAdapterDressroom.getItemParamsId(currentPagerAdapterDressroom.getCount() - 1))
    
                        break
                    
                    default:
                        break
                }
            }
        }
    
        //------------------------------------------------------------------------------------------
        // Если успешно считан id крайней (первой или последней) одежды
        if(self.getDressId() > 0) {
            // Проверяем наличие Интернет-соединения
            let isInternetConnection: Bool = FunctionsConnection.isInternetConnection()
    
            // Если Интернет-соединение присутствует, то загружаем данные об одежде из удаленной БД
            if (isInternetConnection == true) {
                self.executeLoadDressInfo()
            }
            // Иначе, если Интернет-соединение отсутствует, то загружаем данные об одежде из локальной БД
            else {
                let asyncTaskLoadDressInfoSwipeFromLocalDB: AsyncTaskLoadDressInfoSwipeFromLocalDB = AsyncTaskLoadDressInfoSwipeFromLocalDB(
                    context: self.getContext(),
                    dressId: self.getDressId(),
                    swipeDirection: swipeDirection,
                    targetDressType: targetDressType
                )
    
                asyncTaskLoadDressInfoSwipeFromLocalDB.execute()
            }
        }
    }
    
    //==============================================================================================
    // Получаем информацию о необходимой одежде из url
    private func executeLoadDressInfo() {
        // Отображаем диалоговое окно выполнения длительного процесса
        if(ViewControllerMain.presentWindow != nil && self.getIsProgressDialogShow() == true) {
            ViewControllerMain.presentWindow!.addSubview(DialogMain.createDialog(GlobalFlags.DIALOG_WAIT, dialogParams: nil, message: nil)!)
        }
    
        // Загружаем необходимую информацию об одежде в асинхронном потоке
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
            // Возвращаемый массив, содержащий информацию о вещах (одежде), сведения о которой были
            // считаны из удаленной БД
            var returnArrayDressInfo: [Dictionary<String, String>]?
    
            // Массив параметров, передаваемых на сервер
            var postDataParams: Dictionary<String, String> = Dictionary<String, String>()
    
            postDataParams[GlobalFlags.TAG_ACTION_DB] = GlobalFlags.TAG_ACTION_DB_GO_TO_DRESS
            postDataParams[GlobalFlags.TAG_ACTION_GO_TO_DRESS_SUBTYPE] = String(self.getActionSubType())
            postDataParams[GlobalFlags.TAG_USER_ID] = String(UserDetails.getUserIdServer())
            
            // В зависимости от типа поддействия относительно действия считывания информации о текущей одежде
            switch (self.getActionSubType()) {
                case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_SWIPE:
                    if(self.getDressId() > 0) {
                        postDataParams[GlobalFlags.TAG_COUNT_DRESS_READ_FROM_DB] = "1"          // считываем информацию только об одной вещи (одежде)
                        postDataParams[GlobalFlags.TAG_DRESS_ID] = String(self.getDressId())    // id вещи, информацию о которой необходимо считать
                        postDataParams[GlobalFlags.TAG_SWIPE_DIRECTION] = String(self.getSwipeDirection())  // направление листания одежды
                    }
                    else {
                        return
                    }
                    break
                case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_CLICK:
                    if(self.getDressId() > 0) {
                        postDataParams[GlobalFlags.TAG_COUNT_DRESS_READ_FROM_DB] = "1"          // считываем информацию только об одной вещи (одежде)
                        postDataParams[GlobalFlags.TAG_DRESS_ID] = String(self.getDressId())    // id вещи, информацию о которой необходимо считать
                    }
                    else {
                        return
                    }
                    break
                case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_PARAMS:
                    postDataParams[GlobalFlags.TAG_COUNT_DRESS_READ_FROM_DB] = String(GlobalFlags.COUNT_DRESS_READ_FROM_DB)
                    postDataParams[GlobalFlags.TAG_CATID]   = self.getTargetDressCategoryId()
                    postDataParams[GlobalFlags.TAG_FOR_WHO] = Functions.dressForWhoIntToString(GlobalFlags.getDressForWho())
                    postDataParams[GlobalFlags.TAG_TYPE]    = self.getTargetDressType()
                    postDataParams[GlobalFlags.TAG_COLOR]   = self.getTargetDressColor()
                    postDataParams[GlobalFlags.TAG_STYLE]   = self.getTargetDressStyle()
                    break
                case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_CATEGORY:
                    postDataParams[GlobalFlags.TAG_COUNT_DRESS_READ_FROM_DB] = String(GlobalFlags.COUNT_DRESS_READ_FROM_DB)
                    postDataParams[GlobalFlags.TAG_CATID]   = self.getTargetDressCategoryId()
                    postDataParams[GlobalFlags.TAG_FOR_WHO] = Functions.dressForWhoIntToString(GlobalFlags.getDressForWho())
                    postDataParams[GlobalFlags.TAG_TYPE]    = self.getTargetDressType()
                    break
                default:
                    break
            }
            
            //--------------------------------------------------------------------------------------
            // Если тип поддействия - ЗАГРУЗКА ОДЕЖДЫ С ДРУГИМИ ПАРАМЕТРАМИ ИЛИ ДЛЯ ДРУГОЙ КАТЕГОРИИ
            if(self.getActionSubType() == GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_PARAMS ||
                self.getActionSubType() == GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_CATEGORY) {
    
                // Формируем массив id вещей, которые в данный момент присутствуют на виртуальном манекене,
                // кроме вещей для того типа, для которого необходимо загрузить информацию о новых вещах
                var arrayDressListId: Dictionary<String, String>? = DBMain.createArrayListDressId(self.getTargetDressType())
            
                // В цикле перебираем все возможные типы одежды
                if (arrayDressListId != nil) {
                    for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                        var arrayDressListIdForType: String = ""
    
                        if (arrayDressListId![GlobalFlags.getArrayTagDressType()[indexDressType]] != nil) {
                            arrayDressListIdForType = arrayDressListId![GlobalFlags.getArrayTagDressType()[indexDressType]]!.trim()
                        }
    
                        postDataParams[GlobalFlags.getArrayTagDressType()[indexDressType]] = arrayDressListIdForType
                    }
                }
            }
        
            // Пересылаем данные на сервер
            HttpGetPostRequest.executePostRequest(GlobalFlags.TAG_URL, postDataParams: postDataParams) { (dataResult, errorResult) -> () in
                if(dataResult != nil) {
                    let jSONObject: JSON = JSON(data: dataResult!, options: NSJSONReadingOptions.MutableContainers, error: nil)
                    
                    // Разбираем ответ от сервера
                    if(jSONObject != nil) {
                        // Считываем id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
                        var collectionIdForDressShowNow: Int = 0
                        
                        if(jSONObject[GlobalFlags.TAG_COLLECTION_ID] != nil) {
                            collectionIdForDressShowNow = jSONObject[GlobalFlags.TAG_COLLECTION_ID].intValue
                        }
                        
                        self.setCollectionIdForDressShowNow(collectionIdForDressShowNow)
                        
                        //----------------------------------------------------------------------------------
                        // Получаем SUCCESS тег для проверки статуса ответа сервера
                        let success: Int = jSONObject[GlobalFlags.TAG_SUCCESS].intValue
                        
                        // Если категории одежды найдены
                        if (success == 1) {
                            // Получаем JSON объект для массива одежды
                            if(jSONObject[GlobalFlags.TAG_DRESS] != nil) {
                                let jSONArrayDress: [JSON] = jSONObject[GlobalFlags.TAG_DRESS].arrayValue
    
                                // Инициализируем возвращаемый массив, содержащий информацию о вещах (одежде),
                                // считанной из удлаенной БД
                                returnArrayDressInfo = [Dictionary<String, String>]()
    
                                // В цикле перебираем всю одежду, информация о которой была считана из БД
                                for indexDress in 0..<jSONArrayDress.count {
                                    // Получаем объект, хранящий информацию о текущей одежде из массива
                                    let jSONСurrentDressInfo: JSON = jSONArrayDress[indexDress]
    
                                    //----------------------------------------------------------------------
                                    // Разбираем json-объект, содержащий информацию о текущей вещи (одежде) из массива
    
                                    // id текущей одежды
                                    var currentDressId: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_ID] != nil) {
                                        currentDressId = jSONСurrentDressInfo[GlobalFlags.TAG_ID].stringValue
                                    }
    
                                    // id категории для текущей одежды
                                    var currentDressCatId: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_CATID] != nil) {
                                        currentDressCatId = jSONСurrentDressInfo[GlobalFlags.TAG_CATID].stringValue
                                    }
    
                                    // Название категории для текущей одежды
                                    var currentDressCategoryTitle: String = ""
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE] != nil) {
                                        currentDressCategoryTitle = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE].stringValue)
                                    }
    
                                    // Название текущей одежды
                                    var currentDressTitle: String = ""
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_TITLE] != nil) {
                                        currentDressTitle = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_TITLE].stringValue)
                                    }
    
                                    // Алиас названия текущей одежды
                                    var currentDressAlias: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_ALIAS] != nil) {
                                        currentDressAlias = jSONСurrentDressInfo[GlobalFlags.TAG_ALIAS].stringValue
                                    }
    
                                        // Для кого предназначена текущая одежда
                                    var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        currentDressForWho = jSONСurrentDressInfo[GlobalFlags.TAG_FOR_WHO].stringValue
                                    }
    
                                    // Тип текущей одежды
                                    var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_TYPE] != nil) {
                                        currentDressType = jSONСurrentDressInfo[GlobalFlags.TAG_TYPE].stringValue
                                    }
    
                                    // id бренда для текущей одежды
                                    var currentDressBrandId: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_BRAND_ID] != nil) {
                                        currentDressBrandId = jSONСurrentDressInfo[GlobalFlags.TAG_BRAND_ID].stringValue
                                    }
    
                                    // Название бренда для текущей одежды
                                    var currentDressBrandTitle: String = ""
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_BRAND_TITLE] != nil) {
                                        currentDressBrandTitle = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_BRAND_TITLE].stringValue)
                                    }
    
                                    // Ссылка на изображение для текущей одежды
                                    var currentDressImage: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE] != nil) {
                                        currentDressImage = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE].stringValue)
                                    }
    
                                        // Ширина изображения для текущей одежды
                                    var currentDressImageWidth: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        currentDressImageWidth = jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH].stringValue
                                    }
    
                                    // Высота изображения для текущей одежды
                                    var currentDressImageHeight: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        currentDressImageHeight = jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT].stringValue
                                    }
    
                                    // Ссылка на изображение для текущей одежды с обратной стороны
                                    var currentDressImageBack: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        currentDressImageBack = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK].stringValue)
                                    }
    
                                    // Ширина изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackWidth: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        currentDressImageBackWidth = jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH].stringValue
                                    }
    
                                    // Высота изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackHeight: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        currentDressImageBackHeight = jSONСurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT].stringValue
                                    }
    
                                    // Цвет текущей одежды
                                    var currentDressColor: String = ""
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_COLOR] != nil) {
                                        currentDressColor = jSONСurrentDressInfo[GlobalFlags.TAG_COLOR].stringValue
                                    }
    
                                    // Стиль текущей одежды
                                    var currentDressStyle: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_STYLE] != nil) {
                                        currentDressStyle = jSONСurrentDressInfo[GlobalFlags.TAG_STYLE].stringValue
                                    }
    
                                    // Краткое описание для текущей одежды
                                    var currentDressShortDescription: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        currentDressShortDescription = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION].stringValue)
                                    }
    
                                    // Полное описание для текущей одежды
                                    var currentDressDescription: String?
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        currentDressDescription = FunctionsString.jsonDecode(jSONСurrentDressInfo[GlobalFlags.TAG_DESCRIPTION].stringValue)
                                    }
    
                                    // Уровень популярности текущей одежды
                                    var currentDressHits: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_HITS] != nil) {
                                        currentDressHits = jSONСurrentDressInfo[GlobalFlags.TAG_HITS].stringValue
                                    }
    
                                    // Версия информации о текущей вещи
                                    var currentDressVersion: String = "1"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_VERSION] != nil) {
                                        currentDressVersion = jSONСurrentDressInfo[GlobalFlags.TAG_VERSION].stringValue
                                    }
    
                                    // Флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    var currentDressDefault = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                                        currentDressDefault = jSONСurrentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT].stringValue
                                    }
    
                                    // Флаг, определяющий является будет ли отображена текущая вещь в первую очередь
                                    var currentDressShowNow: String = "0"
    
                                    if (jSONСurrentDressInfo[GlobalFlags.TAG_DRESS_SHOW_NOW] != nil) {
                                        currentDressShowNow = jSONСurrentDressInfo[GlobalFlags.TAG_DRESS_SHOW_NOW].stringValue
                                    }
    
                                    //----------------------------------------------------------------------
                                    // Создаем новый HashMap
                                    var mapDressInfo: Dictionary<String, String> = Dictionary<String, String>()
    
                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapDressInfo[GlobalFlags.TAG_ID]                = currentDressId                  // id текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_CATID]             = currentDressCatId               // id категории для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_CATEGORY_TITLE]    = currentDressCategoryTitle       // название категории для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_TITLE]             = currentDressTitle               // название текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_ALIAS]             = currentDressAlias               // алиас названия текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_FOR_WHO]           = currentDressForWho              // для кого предназначена текущая одежда
                                    mapDressInfo[GlobalFlags.TAG_TYPE]              = currentDressType                // тип текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_BRAND_ID]          = currentDressBrandId             // id бренда текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_BRAND_TITLE]       = currentDressBrandTitle          // название бренда для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_IMAGE]             = currentDressImage               // ссылка на изображение для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_IMAGE_WIDTH]       = currentDressImageWidth          // ширина изображения для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT]      = currentDressImageHeight         // высота изображения для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_IMAGE_BACK]        = currentDressImageBack           // ссылка на изображение для текущей одежды с обратной стороны
                                    mapDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH]  = currentDressImageBackWidth      // ширина изображения для текущей одежды с обратной стороны
                                    mapDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight     // высота изображения для текущей одежды с обратной стороны
                                    mapDressInfo[GlobalFlags.TAG_COLOR]             = currentDressColor               // цвет текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_STYLE]             = currentDressStyle               // стиль текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] = currentDressShortDescription    // краткое описание для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_DESCRIPTION]       = currentDressDescription         // полное описание для текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_HITS]              = currentDressHits                // уровень популярности текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_VERSION]           = currentDressVersion             // версия информации о текущей одежды
                                    mapDressInfo[GlobalFlags.TAG_DRESS_DEFAULT]     = currentDressDefault             // флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    mapDressInfo[GlobalFlags.TAG_DRESS_SHOW_NOW]    = currentDressShowNow             // флаг, определяющий будет ли отображена текущая вещь в первую очередь
    
                                    // Сохраняем данные о текущей вещи в общем глобальном массиве только при условии,
                                    // что данная вещь будет отображена в текущий момент на вертикальном манекене
                                    if(currentDressShowNow == "1") {
                                        // Уудаляем сначала из глобального массива сведения об одежде той же категории, что и текущая
                                        DBMain.deleteDressFromGlobalArrayByCategoryId(Int(currentDressCatId)!, dressForWho: Functions.dressForWhoStringToInt(currentDressForWho), dressType: currentDressType)
    
                                        // Теперь непосредственно сохраняем данные о текущей вещи в общем глобальном массиве
                                        var mapDressInfoForGlobalArray: Dictionary<String, String> = Dictionary<String, String>()
                                        
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_ID]                  = currentDressId                // id текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_CATID]               = currentDressCatId             // id категории для текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_FOR_WHO]             = currentDressForWho            // для кого предназначена текущая одежда
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_TYPE]                = currentDressType              // тип текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE]               = currentDressImage             // ссылка на изображение для текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE_WIDTH]         = currentDressImageWidth        // ширина изображения для текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE_HEIGHT]        = currentDressImageHeight       // высота изображения для текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE_BACK]          = currentDressImageBack         // ссылка на изображение для текущей одежды с обратной стороны
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE_BACK_WIDTH]    = currentDressImageBackWidth    // ширина изображения для текущей одежды с обратной стороны
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]   = currentDressImageBackHeight   // высота изображения для текущей одежды с обратной стороны
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_COLOR]               = currentDressColor             // цвет текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_STYLE]               = currentDressStyle             // стиль текущей одежды
                                        mapDressInfoForGlobalArray[GlobalFlags.TAG_DRESS_SHOW_NOW]      = currentDressShowNow           // флаг, определяющий будет ли отображена текущая вещь в первую очередь
    
                                        DBMain.addDressToGlobalArray(Int(currentDressId)!, dressForWho: Functions.dressForWhoStringToInt(currentDressForWho), dressType: currentDressType, arrayCurrentDressInfo: mapDressInfoForGlobalArray)
                                    }
    
                                    // Добавляем сведения о текущей одежде в возвращаемый ассоциативный массив
                                    returnArrayDressInfo!.append(mapDressInfo)
                                }
                            }
                        }
                    }
                }
 
                //------------------------------------------------------------------------------------------
                // После завершения фоновой задачи закрываем прогресс-диалог
    
                //--------------------------------------------------------------------------------------
                // Выполняем последующие действия
                if(returnArrayDressInfo != nil) {
                    switch (self.getActionSubType()) {
                        case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_SWIPE:                        // листание одежды
                            var arrayCurrentDressInfoSwipe: Dictionary<String, String>?
                        
                            // Из всего массива одежды выбираем только необходимую
                            // Учитываем, что при листании считывается информация только об одной вещи
                            if(returnArrayDressInfo!.count > 0) {
                                if (returnArrayDressInfo![0][GlobalFlags.TAG_ID] != nil) {
                                    arrayCurrentDressInfoSwipe = returnArrayDressInfo![0]
                                }
                            }
                        
                            //--------------------------------------------------------------------------
                            // Формируем массив, содержащий ТОЛЬКО НЕОБХОДИМЫЕ сведения о текущей,
                            // считанной из БД одежде
                            var arrayCurrentDressInfoForAdapter: Dictionary<String, String>?
                        
                            if(arrayCurrentDressInfoSwipe != nil) {
                                arrayCurrentDressInfoForAdapter = Dictionary<String, String>()
                            
                                // id текущей одежды
                                var currentDressId = "0"
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_ID] != nil) {
                                    currentDressId = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_ID]!
                                }
                            
                                // id категории для текущей одежды
                                var currentDressCatId: String = "0"
                            
                                if (arrayCurrentDressInfoSwipe![GlobalFlags.TAG_CATID] != nil) {
                                    currentDressCatId = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_CATID]!
                                }
                            
                                // Для кого предназначена текущая одежда
                                var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_FOR_WHO] != nil) {
                                    currentDressForWho = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_FOR_WHO]!
                                }
                            
                                // Тип текущей одежды
                                var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_TYPE] != nil) {
                                    currentDressType = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_TYPE]!
                                }
                            
                                // Ссылка на изображение для текущей одежды
                                var currentDressImage: String?
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE] != nil) {
                                    currentDressImage = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE]
                                }
                            
                                // Ширина изображения для текущей одежды
                                var currentDressImageWidth: String = "0"
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                    currentDressImageWidth = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_WIDTH]!
                                }
                            
                                // Высота изображения для текущей одежды
                                var currentDressImageHeight: String = "0"
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                    currentDressImageHeight = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_HEIGHT]!
                                }
                            
                                // Ссылка на изображение для текущей одежды с обратной стороны
                                var currentDressImageBack: String?
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                    currentDressImageBack = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK]
                                }
                            
                                // Ширина изображения для текущей одежды с обратной стороны
                                var currentDressImageBackWidth: String = "0"
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                    currentDressImageBackWidth = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK_WIDTH]!
                                }
                            
                                // Высота изображения для текущей одежды с обратной стороны
                                var currentDressImageBackHeight: String = "0"
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                    currentDressImageBackHeight = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!
                                }
                            
                                // Цвет текущей одежды
                                var currentDressColor: String = ""
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_COLOR] != nil) {
                                    currentDressColor = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_COLOR]!
                                }
                            
                                // Стиль текущей одежды
                                var currentDressStyle: String?
                            
                                if(arrayCurrentDressInfoSwipe![GlobalFlags.TAG_STYLE] != nil) {
                                    currentDressStyle = arrayCurrentDressInfoSwipe![GlobalFlags.TAG_STYLE]
                                }
                            
                                //----------------------------------------------------------------------
                                // Создаем HashMap для текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_ID] = currentDressId                              // id текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_CATID] = currentDressCatId                         // id категории для текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_FOR_WHO] = currentDressForWho                      // для кого предназначена текущая одежда
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_TYPE] = currentDressType                           // тип текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE] = currentDressImage                         // ссылка на изображение для текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE_WIDTH] = currentDressImageWidth              // ширина изображения для текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE_HEIGHT] = currentDressImageHeight            // высота изображения для текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE_BACK] = currentDressImageBack                // ссылка на изображение для текущей одежды с обратной стороны
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE_BACK_WIDTH] = currentDressImageBackWidth     // ширина изображения для текущей одежды с обратной стороны
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight   // высота изображения для текущей одежды с обратной стороны
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_COLOR] = currentDressColor                         // цвет текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_STYLE] = currentDressStyle                         // стиль текущей одежды
                                arrayCurrentDressInfoForAdapter![GlobalFlags.TAG_DRESS_SHOW_NOW] = "0"                              // флаг, определяющий будет ли отображена текущая вещь в первую очередь
                            }
                        
                            //--------------------------------------------------------------------------
                            // Считываем массив параметров для текущего адаптера
                            var currentPagerAdapterDressroom: PagerAdapterDressroom?
                            var currentPagerAdapterDressroomItemsParams: [Dictionary<String, String>]?
                        
                            if(DBMain.getArrayPagerAdapterDressroom() != nil && self.getTargetDressType() != nil) {
                                if (DBMain.getArrayPagerAdapterDressroom()![self.getTargetDressType()!] != nil) {
                                    currentPagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom()![self.getTargetDressType()!]
                                    currentPagerAdapterDressroomItemsParams = currentPagerAdapterDressroom!.getArrayParams()
                                }
                            }
                            
                            //--------------------------------------------------------------------------
                            // Итоговый конечный массив, содержащий параметры для текущего адаптера
                            var currentPagerAdapterDressroomItemsParamsNew: [Dictionary<String, String>]?
                            
                            if(currentPagerAdapterDressroom != nil && currentPagerAdapterDressroomItemsParams != nil) {
                                currentPagerAdapterDressroomItemsParamsNew = [Dictionary<String, String>]()
                                
                                // В зависимости от направления листания выполняем соответствующие действия
                                // вышеуказанным массивом параметров
                                switch (self.getSwipeDirection()) {
                                    case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                         // листание слева направо
                                        // Добавляем в начало текущую, считанную информацию об одежде
                                        currentPagerAdapterDressroomItemsParamsNew!.append(arrayCurrentDressInfoForAdapter!)
                                    
                                        //--------------------------------------------------------------
                                        // Добавляем в новый массив все элементы из старого кроме последнего,
                                        // т.к. первый выкидываем из рассмотрения
                                        if(currentPagerAdapterDressroom!.getArrayParams() != nil) {
                                            for indexItem in 0..<currentPagerAdapterDressroom!.getArrayParams()!.count - 1 {
                                                currentPagerAdapterDressroomItemsParamsNew!.append(currentPagerAdapterDressroom!.getArrayParams()![indexItem])
                                            }
                                        }
                                        
                                        //--------------------------------------------------------------
                                        // Т.к. добавили в начало элемент, то увеличиваем индекс текущей позиции на -1
                                        if(currentPagerAdapterDressroom!.getCurrentItemPosition() < currentPagerAdapterDressroom!.getCount() - 1) {
                                            currentPagerAdapterDressroom!.setCurrentItemPosition(
                                                currentPagerAdapterDressroom!.getCurrentItemPosition() + 1
                                            )
                                        }
                                        
                                        break
                                    
                                    case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                         // листание справа налево
                                        // Добавляем в новый массив все элементы из старого кроме первого,
                                        // т.к. первый выкидываем из рассмотрения
                                        if(currentPagerAdapterDressroom!.getArrayParams() != nil) {
                                            for indexItem in 1..<currentPagerAdapterDressroom!.getArrayParams()!.count {
                                                currentPagerAdapterDressroomItemsParamsNew!.append(currentPagerAdapterDressroom!.getArrayParams()![indexItem])
                                            }
                                        }
                                    
                                        //--------------------------------------------------------------
                                        // Добавляем в конец текущую, считанную информацию об одежде
                                        currentPagerAdapterDressroomItemsParamsNew!.append(arrayCurrentDressInfoForAdapter!)
                                    
                                        //--------------------------------------------------------------
                                        // Т.к. удалили первый элемент, то уменьшаем индекс текущей позиции на -1
                                        if(currentPagerAdapterDressroom!.getCurrentItemPosition() > 0) {
                                            currentPagerAdapterDressroom!.setCurrentItemPosition(
                                                currentPagerAdapterDressroom!.getCurrentItemPosition() - 1
                                            )
                                        }
                                        
                                        break
                                    
                                    default:
                                        break
                                }
                                
                                //----------------------------------------------------------------------
                                // Обновляем текущий адаптер
                                if(currentPagerAdapterDressroomItemsParamsNew != nil) {
                                    currentPagerAdapterDressroom!.setArrayParams(currentPagerAdapterDressroomItemsParamsNew)
                                }
                            }
                            
                            //-------------------------------------------------------------------------------------
                            // Обновляем UI
                            dispatch_async(dispatch_get_main_queue(), {
                                // Обновляем элементы ViewPager
                                if(self.getContext() != nil) {
                                    self.getContext()!.reloadViewPagerDressroom()
                                }
                                
                                //----------------------------------------------------------------------
                                // В цикле перебираем всю одежду и сохраняем информацию о ней в локальную БД SQLite
                                for indexDress in 0..<returnArrayDressInfo!.count {
                                    var currentDressInfo: Dictionary<String, String> = returnArrayDressInfo![indexDress]
                                    
                                    // id текущей одежды
                                    var currentDressId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ID] != nil) {
                                        currentDressId = currentDressInfo[GlobalFlags.TAG_ID]!
                                    }
                                    
                                    // id категории для текущей одежды
                                    var currentDressCatId: String = "0"
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATID] != nil) {
                                        currentDressCatId = currentDressInfo[GlobalFlags.TAG_CATID]!
                                    }
                                    
                                    // Название категории для текущей одежды
                                    var currentDressCategoryTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE] != nil) {
                                        currentDressCategoryTitle = currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE]!
                                    }
                                    
                                    // Название текущей одежды
                                    var currentDressTitle: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TITLE] != nil) {
                                        currentDressTitle = currentDressInfo[GlobalFlags.TAG_TITLE]!
                                    }
                                    
                                    // Алиас названия текущей одежды
                                    var currentDressAlias: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ALIAS] != nil) {
                                        currentDressAlias = currentDressInfo[GlobalFlags.TAG_ALIAS]
                                    }
                                    
                                    // Для кого предназначена текущая одежда
                                    var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        currentDressForWho = currentDressInfo[GlobalFlags.TAG_FOR_WHO]!
                                    }
                                    
                                    // Тип текущей одежды
                                    var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TYPE] != nil) {
                                        currentDressType = currentDressInfo[GlobalFlags.TAG_TYPE]!
                                    }
                                    
                                    // id бренда для текущей одежды
                                    var currentDressBrandId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_BRAND_ID] != nil) {
                                        currentDressBrandId = currentDressInfo[GlobalFlags.TAG_BRAND_ID]!
                                    }
                                    
                                    // Название бренда для текущей одежды
                                    var currentDressBrandTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_BRAND_TITLE] != nil) {
                                        currentDressBrandTitle = currentDressInfo[GlobalFlags.TAG_BRAND_TITLE]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды
                                    var currentDressImage: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE] != nil) {
                                        currentDressImage = currentDressInfo[GlobalFlags.TAG_IMAGE]
                                    }
                                    
                                    // Ширина изображения для текущей одежды
                                    var currentDressImageWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        currentDressImageWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды
                                    var currentDressImageHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        currentDressImageHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды с обратной стороны
                                    var currentDressImageBack: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        currentDressImageBack = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK]
                                    }
                                    
                                    // Ширина изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        currentDressImageBackWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        currentDressImageBackHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!
                                    }
                                    
                                    // Цвет текущей одежды
                                    var currentDressColor: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_COLOR] != nil) {
                                        currentDressColor = currentDressInfo[GlobalFlags.TAG_COLOR]!
                                    }
                                    
                                    // Стиль текущей одежды
                                    var currentDressStyle: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_STYLE] != nil) {
                                        currentDressStyle = currentDressInfo[GlobalFlags.TAG_STYLE]
                                    }
                                    
                                    // Краткое описание для текущей одежды
                                    var currentDressShortDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        currentDressShortDescription = currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                                    
                                    // Полное описание для текущей одежды
                                    var currentDressDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        currentDressDescription = currentDressInfo[GlobalFlags.TAG_DESCRIPTION]
                                    }
                                    
                                    // Уровень популярности текущей одежды
                                    var currentDressHits: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_HITS] != nil) {
                                        currentDressHits = currentDressInfo[GlobalFlags.TAG_HITS]!
                                    }
                                    
                                    // Версия информации о текущей вещи
                                    var currentDressVersion: String = "1"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_VERSION] != nil) {
                                        currentDressVersion = currentDressInfo[GlobalFlags.TAG_VERSION]!
                                    }
                                    
                                    // Флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    var currentDressDefault: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                                        currentDressDefault = currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT]!
                                    }
                                    
                                    //------------------------------------------------------------------------------
                                    // Создаем новый HashMap
                                    var mapCurrentDressInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    
                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapCurrentDressInfo[GlobalFlags.TAG_ID] = currentDressId                                 // id текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_CATID] = currentDressCatId                           // id категории для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_TITLE] = currentDressTitle                           // название текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_ALIAS] = currentDressAlias                           // алиас названия текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_FOR_WHO] = currentDressForWho                        // для кого предназначена текущая одежда
                                    mapCurrentDressInfo[GlobalFlags.TAG_TYPE] = currentDressType                             // тип текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_BRAND_ID] = currentDressBrandId                      // id бренда текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE] = currentDressImage                           // ссылка на изображение для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] = currentDressImageWidth                // ширина изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] = currentDressImageHeight              // высота изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK] = currentDressImageBack                  // ссылка на изображение для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] = currentDressImageBackWidth       // ширина изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight     // высота изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_COLOR] = currentDressColor                           // цвет текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_STYLE] = currentDressStyle                           // стиль текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] = currentDressShortDescription    // краткое описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DESCRIPTION] = currentDressDescription               // полное описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_HITS] = currentDressHits                             // уровень популярности текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_VERSION] = currentDressVersion                       // версия информации о текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] = currentDressDefault                 // флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    
                                    //------------------------------------------------------------------------------
                                    // Добавляем или обновляем информации об одежде по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_DRESS,
                                        idServerMySQL: Int(currentDressId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о категории для текущей одежды
                                    var mapCurrentDressCategoryInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_ID] = currentDressCatId
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_TITLE] = currentDressCategoryTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_CATEGORIES,
                                        idServerMySQL: Int(currentDressCatId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressCategoryInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о бренде для текущей одежды
                                    var mapCurrentDressBrandInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_ID] = currentDressBrandId
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_TITLE] = currentDressBrandTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_BRAND,
                                        idServerMySQL: Int(currentDressBrandId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressBrandInfo
                                    )
                                }
                            })

                            break
    
                        case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_CLICK:               // клик пальцем по изображению для текущей вещи (одежды)
                            var arrayCurrentDressInfo: Dictionary<String, String>?
    
                            // Из всего массива одежды выбираем только необходимую
                            // Учитываем, что при этом считывалась информация толкько об одной вещи
                            if(returnArrayDressInfo!.count > 0) {
                                if (returnArrayDressInfo![0][GlobalFlags.TAG_ID] != nil) {
                                    if (Int(returnArrayDressInfo![0][GlobalFlags.TAG_ID]!) == self.getDressId()) {
                                        arrayCurrentDressInfo = returnArrayDressInfo![0]
                                    }
                                }
                            }
 
                            // Добавим в массив, содержащий информацию о текущей вещи, сведения о том,
                            // является ли текущая вещь СОХРАНЕННОЙ для текущего пользователя
                            arrayCurrentDressInfo![GlobalFlags.TAG_COLLECTION_ID] = String(self.getCollectionIdForDressShowNow())
    
                            //-------------------------------------------------------------------------------------
                            // Обновляем UI
                            dispatch_async(dispatch_get_main_queue(), {
                                // Если отображали ProgressDialog, то теперь закрываем его
                                if(ViewControllerMain.presentWindow != nil) {
                                    let viewDialogWait: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_WAIT)
                                    
                                    if(viewDialogWait != nil) {
                                        viewDialogWait!.removeFromSuperview()
                                    }
                                    
                                    // Отображаем затемнение
                                    let viewShadow: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_VIEW_SHADOW)
                                    
                                    if(viewShadow != nil && DBMain.getContext() != nil) {
                                        DBMain.getContext()!.view.bringSubviewToFront(viewShadow!)
                                        viewShadow!.hidden = false
                                    }
                                    
                                    // Отображаем непосредственно диалоговое окно с информацией о текущей вещи
                                    let viewDialogShowDressInfo: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_SHOW_DRESS_INFO)
                                    
                                    if(viewDialogShowDressInfo != nil && DBMain.getContext() != nil) {
                                        DBMain.getContext()!.view.bringSubviewToFront(viewDialogShowDressInfo!)
                                        
                                        // Заполняем окно с информацией о текущей одежде непосредственно необходимыми данными
                                        DialogMain.createDialog(GlobalFlags.DIALOG_DRESS_INFO, dialogParams: arrayCurrentDressInfo, message: nil)
                                        
                                        // Теперь непосредственно отображаем сформированное диалоговое окно
                                        viewDialogShowDressInfo!.hidden = false
                                    }
                                }
                                
                                //----------------------------------------------------------------------------------
                                // В цикле перебираем всю одежду и сохраняем информацию о ней в локальную БД SQLite
                                for indexDress in 0..<returnArrayDressInfo!.count {
                                    var currentDressInfo: Dictionary<String, String> = returnArrayDressInfo![indexDress]
                                    
                                    // id текущей одежды
                                    var currentDressId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ID] != nil) {
                                        currentDressId = currentDressInfo[GlobalFlags.TAG_ID]!
                                    }
                                    
                                    // id категории для текущей одежды
                                    var currentDressCatId: String = "0"
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATID] != nil) {
                                        currentDressCatId = currentDressInfo[GlobalFlags.TAG_CATID]!
                                    }
                                    
                                    // Название категории для текущей одежды
                                    var currentDressCategoryTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE] != nil) {
                                        currentDressCategoryTitle = currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE]!
                                    }
                                    
                                    // Название текущей одежды
                                    var currentDressTitle: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TITLE] != nil) {
                                        currentDressTitle = currentDressInfo[GlobalFlags.TAG_TITLE]!
                                    }
                                    
                                    // Алиас названия текущей одежды
                                    var currentDressAlias: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ALIAS] != nil) {
                                        currentDressAlias = currentDressInfo[GlobalFlags.TAG_ALIAS]
                                    }
                                    
                                    // Для кого предназначена текущая одежда
                                    var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        currentDressForWho = currentDressInfo[GlobalFlags.TAG_FOR_WHO]!
                                    }
                                    
                                    // Тип текущей одежды
                                    var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TYPE] != nil) {
                                        currentDressType = currentDressInfo[GlobalFlags.TAG_TYPE]!
                                    }
                                    
                                    // id бренда для текущей одежды
                                    var currentDressBrandId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_BRAND_ID] != nil) {
                                        currentDressBrandId = currentDressInfo[GlobalFlags.TAG_BRAND_ID]!
                                    }
                                    
                                    // Название бренда для текущей одежды
                                    var currentDressBrandTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_BRAND_TITLE] != nil) {
                                        currentDressBrandTitle = currentDressInfo[GlobalFlags.TAG_BRAND_TITLE]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды
                                    var currentDressImage: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE] != nil) {
                                        currentDressImage = currentDressInfo[GlobalFlags.TAG_IMAGE]
                                    }
                                    
                                    // Ширина изображения для текущей одежды
                                    var currentDressImageWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        currentDressImageWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды
                                    var currentDressImageHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        currentDressImageHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды с обратной стороны
                                    var currentDressImageBack: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        currentDressImageBack = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK]
                                    }
                                    
                                    // Ширина изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        currentDressImageBackWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        currentDressImageBackHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!
                                    }
                                    
                                    // Цвет текущей одежды
                                    var currentDressColor: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_COLOR] != nil) {
                                        currentDressColor = currentDressInfo[GlobalFlags.TAG_COLOR]!
                                    }
                                    
                                    // Стиль текущей одежды
                                    var currentDressStyle: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_STYLE] != nil) {
                                        currentDressStyle = currentDressInfo[GlobalFlags.TAG_STYLE]
                                    }
                                    
                                    // Краткое описание для текущей одежды
                                    var currentDressShortDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        currentDressShortDescription = currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                                    
                                    // Полное описание для текущей одежды
                                    var currentDressDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        currentDressDescription = currentDressInfo[GlobalFlags.TAG_DESCRIPTION]
                                    }
                                    
                                    // Уровень популярности текущей одежды
                                    var currentDressHits: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_HITS] != nil) {
                                        currentDressHits = currentDressInfo[GlobalFlags.TAG_HITS]!
                                    }
                                    
                                    // Версия информации о текущей вещи
                                    var currentDressVersion: String = "1"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_VERSION] != nil) {
                                        currentDressVersion = currentDressInfo[GlobalFlags.TAG_VERSION]!
                                    }
                                    
                                    // Флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    var currentDressDefault: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                                        currentDressDefault = currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT]!
                                    }
                                    
                                    //------------------------------------------------------------------------------
                                    // Создаем новый HashMap
                                    var mapCurrentDressInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    
                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapCurrentDressInfo[GlobalFlags.TAG_ID] = currentDressId                                 // id текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_CATID] = currentDressCatId                           // id категории для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_TITLE] = currentDressTitle                           // название текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_ALIAS] = currentDressAlias                           // алиас названия текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_FOR_WHO] = currentDressForWho                        // для кого предназначена текущая одежда
                                    mapCurrentDressInfo[GlobalFlags.TAG_TYPE] = currentDressType                             // тип текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_BRAND_ID] = currentDressBrandId                      // id бренда текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE] = currentDressImage                           // ссылка на изображение для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] = currentDressImageWidth                // ширина изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] = currentDressImageHeight              // высота изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK] = currentDressImageBack                  // ссылка на изображение для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] = currentDressImageBackWidth       // ширина изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight     // высота изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_COLOR] = currentDressColor                           // цвет текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_STYLE] = currentDressStyle                           // стиль текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] = currentDressShortDescription    // краткое описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DESCRIPTION] = currentDressDescription               // полное описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_HITS] = currentDressHits                             // уровень популярности текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_VERSION] = currentDressVersion                       // версия информации о текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] = currentDressDefault                 // флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    
                                    //------------------------------------------------------------------------------
                                    // Добавляем или обновляем информации об одежде по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_DRESS,
                                        idServerMySQL: Int(currentDressId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о категории для текущей одежды
                                    var mapCurrentDressCategoryInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_ID] = currentDressCatId
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_TITLE] = currentDressCategoryTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_CATEGORIES,
                                        idServerMySQL: Int(currentDressCatId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressCategoryInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о бренде для текущей одежды
                                    var mapCurrentDressBrandInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_ID] = currentDressBrandId
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_TITLE] = currentDressBrandTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_BRAND,
                                        idServerMySQL: Int(currentDressBrandId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressBrandInfo
                                    )
                                }
                            })
    
                            break
    
                        case GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_PARAMS, GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_OTHER_CATEGORY:                 // загрузка информации об одежде для других параметров или загрузка информации об одежде для другой категории

                            //--------------------------------------------------------------------------
                            // Из всего массива returnArrayDressInfo выбираем сведения только об одежде,
                            // которая будет отображена в данный момент на вирткальном манекене
                            var arrayCurrentViewDressInfo: Dictionary<String, String>?
    
                            for indexDress in 0..<returnArrayDressInfo!.count {
                                if(returnArrayDressInfo![indexDress][GlobalFlags.TAG_DRESS_SHOW_NOW] != nil) {
                                    if (returnArrayDressInfo![indexDress][GlobalFlags.TAG_DRESS_SHOW_NOW] == "1") {
                                        arrayCurrentViewDressInfo = returnArrayDressInfo![indexDress]
                                        break
                                    }
                                }
                            }
    
                            //--------------------------------------------------------------------------
                            // Определяем тип, ширину и высоту одежды, которая будет отображена в данный момент на виртуальном манекене
                            var currentViewDressType: String?
                            var currentViewDressWidth: Int = 0
                            var currentViewDressHeight: Int = 0
    
                            if(arrayCurrentViewDressInfo != nil) {
                                // Тип одежды
                                currentViewDressType = arrayCurrentViewDressInfo![GlobalFlags.TAG_TYPE]
    
                                if (currentViewDressType != nil) {
                                    currentViewDressType = currentViewDressType!.trim()
    
                                    if (currentViewDressType == "") {
                                        currentViewDressType = nil
                                    }
                                }
    
                                // Ширина изображения
                                if (arrayCurrentViewDressInfo![GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                    currentViewDressWidth = Int(arrayCurrentViewDressInfo![GlobalFlags.TAG_IMAGE_WIDTH]!)!
                                }
    
                                // Высота изображения
                                if (arrayCurrentViewDressInfo![GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                    currentViewDressHeight = Int(arrayCurrentViewDressInfo![GlobalFlags.TAG_IMAGE_HEIGHT]!)!
                                }
                            }
    
                            //--------------------------------------------------------------------------
                            // Определяем ширину, высоту предыдущей одежды по типу текущей одежды
                            var prevDressWidth: Int = 0
                            var prevDressHeight: Int = 0
    
                            if(currentViewDressType != nil) {
                                var arrayDressSizeReal: Dictionary<String, Float>? = DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())
    
                                if (arrayDressSizeReal != nil) {
                                    if (arrayDressSizeReal!["x_" + currentViewDressType! + "_1"] != nil) {
                                        prevDressWidth = Int(arrayDressSizeReal!["x_" + currentViewDressType! + "_1"]!)
                                    }
    
                                    if (arrayDressSizeReal!["y_" + currentViewDressType! + "_1"] != nil) {
                                        prevDressHeight = Int(arrayDressSizeReal!["y_" + currentViewDressType! + "_1"]!)
                                    }
                                }
                            }
    
                            //--------------------------------------------------------------------------
                            // Формируем массив, содержащий ТОЛЬКО НЕОБХОДИМЫЕ сведения об одежде
                            // для текущего типа для создаваемого адаптера
                            var arrayDressInfoForCurrentAdapter: [Dictionary<String, String>] = [Dictionary<String, String>]()
    
                            if(returnArrayDressInfo != nil) {
                                for indexDress in 0..<returnArrayDressInfo!.count {
                                    var currentDressInfo: Dictionary<String, String> = returnArrayDressInfo![indexDress]
    
                                    // id текущей одежды
                                    var currentDressId: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_ID] != nil) {
                                        currentDressId = currentDressInfo[GlobalFlags.TAG_ID]!
                                    }
    
                                    // id категории для текущей одежды
                                    var currentDressCatId: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_CATID] != nil) {
                                        currentDressCatId = currentDressInfo[GlobalFlags.TAG_CATID]!
                                    }
    
                                    // Для кого предназначена текущая одежда
                                    var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
    
                                    if (currentDressInfo[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        currentDressForWho = currentDressInfo[GlobalFlags.TAG_FOR_WHO]!
                                    }
    
                                    // Тип текущей одежды
                                    var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
    
                                    if (currentDressInfo[GlobalFlags.TAG_TYPE] != nil) {
                                        currentDressType = currentDressInfo[GlobalFlags.TAG_TYPE]!
                                    }
    
                                    // Ссылка на изображение для текущей одежды
                                    var currentDressImage: String?
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE] != nil) {
                                        currentDressImage = currentDressInfo[GlobalFlags.TAG_IMAGE]
                                    }
    
                                    // Ширина изображения для текущей одежды
                                    var currentDressImageWidth: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        currentDressImageWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH]!
                                    }
       
                                    // Высота изображения для текущей одежды
                                    var currentDressImageHeight: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        currentDressImageHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT]!
                                    }
    
                                    // Ссылка на изображение для текущей одежды с обратной стороны
                                    var currentDressImageBack: String?
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        currentDressImageBack = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK]
                                    }
        
                                    // Ширина изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackWidth: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        currentDressImageBackWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!
                                    }
    
                                    // Высота изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackHeight: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        currentDressImageBackHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!
                                    }
    
                                    // Цвет текущей одежды
                                    var currentDressColor: String = ""
    
                                    if (currentDressInfo[GlobalFlags.TAG_COLOR] != nil) {
                                        currentDressColor = currentDressInfo[GlobalFlags.TAG_COLOR]!
                                    }
    
                                    // Стиль текущей одежды
                                    var currentDressStyle: String?
    
                                    if (currentDressInfo[GlobalFlags.TAG_STYLE] != nil) {
                                        currentDressStyle = currentDressInfo[GlobalFlags.TAG_STYLE]
                                    }
    
                                    // Будет ли отображена текущая вещь в первую очередь
                                    var currentDressShowNow: String = "0"
    
                                    if (currentDressInfo[GlobalFlags.TAG_DRESS_SHOW_NOW] != nil) {
                                        currentDressShowNow = currentDressInfo[GlobalFlags.TAG_DRESS_SHOW_NOW]!
                                    }
    
                                    //--------------------------------------------------------------
                                    // Создаем HashMap для текущей одежды
                                    var mapCurrentDressInfoForCurrentAdapter: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_ID]                = currentDressId                // id текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_CATID]             = currentDressCatId             // id категории для текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_FOR_WHO]           = currentDressForWho            // для кого предназначена текущая одежда
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_TYPE]              = currentDressType              // тип текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE]             = currentDressImage             // ссылка на изображение для текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE_WIDTH]       = currentDressImageWidth        // ширина изображения для текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE_HEIGHT]      = currentDressImageHeight       // высота изображения для текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE_BACK]        = currentDressImageBack         // ссылка на изображение для текущей одежды с обратной стороны
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE_BACK_WIDTH]  = currentDressImageBackWidth    // ширина изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight   // высота изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_COLOR]             = currentDressColor             // цвет текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_STYLE]             = currentDressStyle             // стиль текущей одежды
                                    mapCurrentDressInfoForCurrentAdapter[GlobalFlags.TAG_DRESS_SHOW_NOW]    = currentDressShowNow           // флаг, определяющий будет ли отображена текущая вещь в первую очередь
    
                                    // Добавляем текущий HashMap в общий ArrayList
                                    arrayDressInfoForCurrentAdapter.append(mapCurrentDressInfoForCurrentAdapter)
                                }
                            }
                    
                            //--------------------------------------------------------------------------
                            // Если размеры текущей одежды не совпадает с размерами предыдущей одежды того же типа
                            if (prevDressWidth == 0 || prevDressHeight == 0 || currentViewDressWidth != prevDressWidth || currentViewDressHeight != prevDressHeight) {
                                if(currentViewDressType != nil) {
                                    // Заменяем ширину и высоту одежды в массиве arrayDressSizeReal для соответствующего типа
                                    var arrayDressSizeReal: Dictionary<String, Float>? = DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())
    
                                    if (arrayDressSizeReal != nil) {
                                        // Ширина одежды
                                        arrayDressSizeReal!["x_" + currentViewDressType! + "_1"] = Float(currentViewDressWidth)
    
                                        // Высота одежды
                                        arrayDressSizeReal!["y_" + currentViewDressType! + "_1"] = Float(currentViewDressHeight)
    
                                        // Сохраняем текущий массив arrayDressSizeReal в глобальном массиве
                                        DBMain.setArrayDressSizeReal(GlobalFlags.getDressForWho(), arrayDressSizeReal: arrayDressSizeReal!);
                                    }
    
                                    //------------------------------------------------------------------
                                    // Сохраняем текущий тип одежды в глобальном массиве, хранящем присутствующие
                                    // на виртуальном манекене типы одежды
                                    var arrayDressGroupExists: [String]? = DBMain.getArrayDressGroupExists()
                                    
                                    if(arrayDressGroupExists == nil) {
                                        arrayDressGroupExists = [String]()
                                    }
                                    
                                    if(arrayDressGroupExists!.contains(currentViewDressType!) == false) {
                                        arrayDressGroupExists!.append(currentViewDressType!)
                                    }
                  
                                    //------------------------------------------------------------------
                                    // Вычисляем размеры одежды, к которым необходимо подогнать реальные размеры рассматриваемых вещей
                                    var arrayDressSizeTarget: Dictionary<String, Int>?
                                    
                                    if(arrayDressSizeReal != nil) {
                                        arrayDressSizeTarget = BitmapDecode.calculateBitmapSize(arrayDressGroupExists, arrayDressSizeReal: arrayDressSizeReal!)
                                    }
                                    
                                    //------------------------------------------------------------------
                                    // Сохраняем массив с размерами, к которым необходимо преобразовать размеры одежды, в глобальный массив
                                    if(arrayDressSizeTarget != nil) {
                                        DBMain.setArrayDressSizeTarget(GlobalFlags.getDressForWho(), arrayDressSizeTarget: arrayDressSizeTarget!)
                                    }
                                }
                            }
    
                            //--------------------------------------------------------------------------
                            // Создаем адаптер для соответствующего элемента ViewPager для текущей одежде,
                            // отображаемой на виртуальном манекене
                            let pagerAdapterDressroom: PagerAdapterDressroom = PagerAdapterDressroom(arrayParams: arrayDressInfoForCurrentAdapter)
    
                            // Сохраняем созданный адаптер для примерочной в глобальном масстве
                            var arrayPagerAdapterDressroom: Dictionary<String, PagerAdapterDressroom>? = DBMain.getArrayPagerAdapterDressroom()
                            
                            if(arrayPagerAdapterDressroom == nil) {
                                arrayPagerAdapterDressroom = Dictionary<String, PagerAdapterDressroom>()
                            }
                            
                            if(currentViewDressType != nil) {
                                arrayPagerAdapterDressroom![currentViewDressType!] = pagerAdapterDressroom
                            }
                            
                            DBMain.setArrayPagerAdapterDressroom(arrayPagerAdapterDressroom!)
                            
                            //-------------------------------------------------------------------------------------
                            // Обновляем UI
                            dispatch_async(dispatch_get_main_queue(), {
                                // Если отображали ProgressDialog, то теперь закрываем его
                                if(ViewControllerMain.presentWindow != nil) {
                                    let viewDialogWait: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_WAIT)
                                    
                                    if(viewDialogWait != nil) {
                                        viewDialogWait!.removeFromSuperview()
                                    }
                                    
                                    //-----------------------------------------------------------------------
                                    // Скрываем затемнение
                                    let viewShadow: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_VIEW_SHADOW)
                                    
                                    if(viewShadow != nil) {
                                        viewShadow!.hidden = true
                                    }
                                    
                                    // Скрываем непосредственно диалоговое окно с информацией о текущей вещи
                                    let viewDialogShowDressInfo: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_SHOW_DRESS_INFO)
                                    
                                    if(viewDialogShowDressInfo != nil) {
                                        viewDialogShowDressInfo!.hidden = true
                                    }
                                }

                                //--------------------------------------------------------------------------
                                // Обновляем адаптеры для элементов ViewPager
                                if(self.getContext() != nil) {
                                    self.getContext()!.reloadViewPagerDressroom()
                                }
                                
                                //--------------------------------------------------------------------------
                                // Проверяем сохранен ли набор одежды, в данный момент
                                // отображаемый на виртуальном манекене, для текущего пользователя
                                if(self.getContext() != nil) {
                                    if (self.getContext()!.mButtonDressSave != nil) {
                                        // Если набор одежды, отображаемый в первую очередь является СОХРАНЕННЫМ для текущего пользователя
                                        if (self.getCollectionIdForDressShowNow() > 0) {
                                            // Меняем изображение для кнопки сохранения текущего набора одежды
                                            self.getContext()!.mButtonDressSave.image = UIImage(named: "favorite2")
    
                                            // Устанавливаем id текущего набора одежды в качестве тега для соответствующей кнопки сохранения
                                            self.getContext()!.mButtonDressSave.tag = self.getCollectionIdForDressShowNow()
                                        }
                                        // Иначе
                                        else {
                                            // Меняем изображение для кнопки сохранения текущего набора одежды
                                            self.getContext()!.mButtonDressSave.image = UIImage(named: "favorite")
    
                                            // Устанавливаем 0 в качестве тега для соответствующей кнопки сохранения
                                            self.getContext()!.mButtonDressSave.tag = 0
                                        }
                                    }
                                }
                                
                                // В цикле перебираем всю одежду и сохраняем информацию о ней в локальную БД SQLite
                                for indexDress in 0..<returnArrayDressInfo!.count {
                                    var currentDressInfo: Dictionary<String, String> = returnArrayDressInfo![indexDress]
                                    
                                    // id текущей одежды
                                    var currentDressId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ID] != nil) {
                                        currentDressId = currentDressInfo[GlobalFlags.TAG_ID]!
                                    }
                                    
                                    // id категории для текущей одежды
                                    var currentDressCatId: String = "0"
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATID] != nil) {
                                        currentDressCatId = currentDressInfo[GlobalFlags.TAG_CATID]!
                                    }
                                    
                                    // Название категории для текущей одежды
                                    var currentDressCategoryTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE] != nil) {
                                        currentDressCategoryTitle = currentDressInfo[GlobalFlags.TAG_CATEGORY_TITLE]!
                                    }
                                    
                                    // Название текущей одежды
                                    var currentDressTitle: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TITLE] != nil) {
                                        currentDressTitle = currentDressInfo[GlobalFlags.TAG_TITLE]!
                                    }
                                    
                                    // Алиас названия текущей одежды
                                    var currentDressAlias: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_ALIAS] != nil) {
                                        currentDressAlias = currentDressInfo[GlobalFlags.TAG_ALIAS]
                                    }
                                    
                                    // Для кого предназначена текущая одежда
                                    var currentDressForWho: String = GlobalFlags.TAG_DRESS_MAN
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_FOR_WHO] != nil) {
                                        currentDressForWho = currentDressInfo[GlobalFlags.TAG_FOR_WHO]!
                                    }
                                    
                                    // Тип текущей одежды
                                    var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_TYPE] != nil) {
                                        currentDressType = currentDressInfo[GlobalFlags.TAG_TYPE]!
                                    }
                                    
                                    // id бренда для текущей одежды
                                    var currentDressBrandId: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_BRAND_ID] != nil) {
                                        currentDressBrandId = currentDressInfo[GlobalFlags.TAG_BRAND_ID]!
                                    }
                                    
                                    // Название бренда для текущей одежды
                                    var currentDressBrandTitle: String = ""
                                    
                                    if (currentDressInfo[GlobalFlags.TAG_BRAND_TITLE] != nil) {
                                        currentDressBrandTitle = currentDressInfo[GlobalFlags.TAG_BRAND_TITLE]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды
                                    var currentDressImage: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE] != nil) {
                                        currentDressImage = currentDressInfo[GlobalFlags.TAG_IMAGE]
                                    }
                                    
                                    // Ширина изображения для текущей одежды
                                    var currentDressImageWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] != nil) {
                                        currentDressImageWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды
                                    var currentDressImageHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] != nil) {
                                        currentDressImageHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT]!
                                    }
                                    
                                    // Ссылка на изображение для текущей одежды с обратной стороны
                                    var currentDressImageBack: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                        currentDressImageBack = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK]
                                    }
                                    
                                    // Ширина изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackWidth: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] != nil) {
                                        currentDressImageBackWidth = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH]!
                                    }
                                    
                                    // Высота изображения для текущей одежды с обратной стороны
                                    var currentDressImageBackHeight: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] != nil) {
                                        currentDressImageBackHeight = currentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT]!
                                    }
                                    
                                    // Цвет текущей одежды
                                    var currentDressColor: String = ""
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_COLOR] != nil) {
                                        currentDressColor = currentDressInfo[GlobalFlags.TAG_COLOR]!
                                    }
                                    
                                    // Стиль текущей одежды
                                    var currentDressStyle: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_STYLE] != nil) {
                                        currentDressStyle = currentDressInfo[GlobalFlags.TAG_STYLE]
                                    }
                                    
                                    // Краткое описание для текущей одежды
                                    var currentDressShortDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] != nil) {
                                        currentDressShortDescription = currentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION]
                                    }
                                    
                                    // Полное описание для текущей одежды
                                    var currentDressDescription: String?
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DESCRIPTION] != nil) {
                                        currentDressDescription = currentDressInfo[GlobalFlags.TAG_DESCRIPTION]
                                    }
                                    
                                    // Уровень популярности текущей одежды
                                    var currentDressHits: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_HITS] != nil) {
                                        currentDressHits = currentDressInfo[GlobalFlags.TAG_HITS]!
                                    }
                                    
                                    // Версия информации о текущей вещи
                                    var currentDressVersion: String = "1"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_VERSION] != nil) {
                                        currentDressVersion = currentDressInfo[GlobalFlags.TAG_VERSION]!
                                    }
                                    
                                    // Флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    var currentDressDefault: String = "0"
                                    
                                    if(currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] != nil) {
                                        currentDressDefault = currentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT]!
                                    }
                                    
                                    //------------------------------------------------------------------------------
                                    // Создаем новый HashMap
                                    var mapCurrentDressInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    
                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapCurrentDressInfo[GlobalFlags.TAG_ID] = currentDressId                                 // id текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_CATID] = currentDressCatId                           // id категории для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_TITLE] = currentDressTitle                           // название текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_ALIAS] = currentDressAlias                           // алиас названия текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_FOR_WHO] = currentDressForWho                        // для кого предназначена текущая одежда
                                    mapCurrentDressInfo[GlobalFlags.TAG_TYPE] = currentDressType                             // тип текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_BRAND_ID] = currentDressBrandId                      // id бренда текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE] = currentDressImage                           // ссылка на изображение для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_WIDTH] = currentDressImageWidth                // ширина изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_HEIGHT] = currentDressImageHeight              // высота изображения для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK] = currentDressImageBack                  // ссылка на изображение для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_WIDTH] = currentDressImageBackWidth       // ширина изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_IMAGE_BACK_HEIGHT] = currentDressImageBackHeight     // высота изображения для текущей одежды с обратной стороны
                                    mapCurrentDressInfo[GlobalFlags.TAG_COLOR] = currentDressColor                           // цвет текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_STYLE] = currentDressStyle                           // стиль текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_SHORT_DESCRIPTION] = currentDressShortDescription    // краткое описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DESCRIPTION] = currentDressDescription               // полное описание для текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_HITS] = currentDressHits                             // уровень популярности текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_VERSION] = currentDressVersion                       // версия информации о текущей одежды
                                    mapCurrentDressInfo[GlobalFlags.TAG_DRESS_DEFAULT] = currentDressDefault                 // флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                    
                                    //------------------------------------------------------------------------------
                                    // Добавляем или обновляем информации об одежде по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_DRESS,
                                        idServerMySQL: Int(currentDressId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о категории для текущей одежды
                                    var mapCurrentDressCategoryInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_ID] = currentDressCatId
                                    mapCurrentDressCategoryInfo[GlobalFlags.TAG_TITLE] = currentDressCategoryTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_CATEGORIES,
                                        idServerMySQL: Int(currentDressCatId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressCategoryInfo
                                    )
                                    
                                    //------------------------------------------------------------------------------
                                    // Сохраняем информацию о бренде для текущей одежды
                                    var mapCurrentDressBrandInfo: Dictionary<String, String> = Dictionary<String, String>()
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_ID] = currentDressBrandId
                                    mapCurrentDressBrandInfo[GlobalFlags.TAG_TITLE] = currentDressBrandTitle
                                    
                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper()!.updateOrInsertRecordToDBByIdServerMySQL(
                                        GlobalFlags.TAG_TABLE_BRAND,
                                        idServerMySQL: Int(currentDressBrandId)!,
                                        updateOrInsertFieldsValues: mapCurrentDressBrandInfo
                                    )
                                }
                            })
    
                            break
                        
                        default:
                            break
                    }
                }
                // Иначе, выводим сообщение о возникшей ошибке
                else {
                    if(self.getActionSubType() != GlobalFlags.ACTION_GO_TO_DRESS_SUBTYPE_DRESS_SWIPE) {
                        dispatch_async(dispatch_get_main_queue(), {
                            if(ViewControllerMain.presentWindow != nil) {
                                // Если отображали ProgressDialog, то теперь закрываем его
                                let viewDialogWait: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_WAIT)
                                
                                if(viewDialogWait != nil) {
                                    viewDialogWait!.removeFromSuperview()
                                }
                                
                                // Выводим непосредственно сообщение
                                ViewControllerMain.presentWindow!.makeToast(message: GlobalFlagsStringsNotification.stringShowDressInfoNo, duration: 2, position: "center")
                            }
                        })
                    }
                }
            }
        })
    }
}