import UIKit

class ViewControllerMain: UIViewController, UIPageViewControllerDataSource, UIPageViewControllerDelegate {
    
    // Свойства данного класса
    internal static var presentWindow : UIWindow?
    private static var mDressRotationAngle: Int = 0        // угол, на который повернуты изображения одежды вокруг вертикальной оси
    private static var mMainActivityViewType: Int = GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS          // тип просматриваемой информации в данном Activity (одежда или коллекции)
    
    // Элементы интерфейса для текущего окна
    @IBOutlet weak var mToolbarBottom: UIToolbar!           // нижняя панель кнопок
    @IBOutlet weak var mButtonDressInfo: UIBarButtonItem!   // кнопка отображения информации о текущем наборе одежды
    @IBOutlet weak var mButtonDressSave: UIBarButtonItem!   // кнопка сохранения текущего набора одежды в БД для текущего пользователя
    @IBOutlet weak var mButtonDressAddOrShare: UIBarButtonItem!    // кнопка добавления новой категории одежды на виртуальный манекен
    @IBOutlet weak var viewBorderButtonDressAdd: UIView!    // нижняя граница для кнопки отображения панели добавления одежды
    @IBOutlet weak var viewMainWindowContent: UIView!
    @IBOutlet weak var viewDressAdd: UIView!                // панель добавления новой одежды
    @IBOutlet weak var mViewDressAddConstraintBottom: NSLayoutConstraint!
    @IBOutlet weak var imageViewDressAddClose: UIImageView!
    @IBOutlet weak var viewMainMenu: UIView!                // главное меню приложения
    @IBOutlet weak var viewMainMenuRotate: UIView!          // пункт Повернуть главного меню приложения
    @IBOutlet weak var viewMainMenuShare: UIView!           // пункт Поделиться главного меню приложения
   
    // Элементы pageViewController для виртуального манекена
    var pageViewControllerHead: UIPageViewController!               // для одежды из категории "Головные уборы"
    var pageViewControllerBody: UIPageViewController!               // для одежды из категории "Верх"
    var pageViewControllerLeg: UIPageViewController!                // для одежды из категории "Низ"
    var pageViewControllerFoot: UIPageViewController!               // для одежды из категории "Обувь"
    var pageViewControllerAccessory: UIPageViewController!          // для одежды из категории "Аксессуары"
    var pageViewControllerDressCollection: UIPageViewController!    // для избранных наборов одежды
    
    private var mPageMenuDressAdd : CAPSPageMenu?               // меню, отображаемое в виде вкладок для добавления одежды на манекен
    
    @IBOutlet weak var viewShadow: UIView!
    @IBOutlet weak var viewDialogShowDressInfo: UIView!         // ссылка на внешний вид всплывающего окна с информацией о текущей одежде
    @IBOutlet weak var viewDialogShare: ViewDialogShare!
    
    @IBOutlet weak var imageViewArrowLeft: UIImageView!
    @IBOutlet weak var imageViewArrowRight: UIImageView!
    
    //=======================================================================================
    // Метод, запускаемый при загрузке текущего View
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Инициализируем свойства текущего класса
        ViewControllerMain.presentWindow = UIApplication.sharedApplication().keyWindow
        DBMain.setContext(self)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        ViewControllerMain.setDressRotationAngle(0)
        
        //-----------------------------------------------------------------------------------
        // Определяем разрешение экрана
        let screenSize: CGRect = UIScreen.mainScreen().bounds
        FunctionsScreen.setScreenWidth(Int(screenSize.width))
        FunctionsScreen.setScreenHeight(Int(screenSize.height))
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по View затемнения
        let tapGestureRecognizerViewShadow = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickViewShadow))
        self.viewShadow.userInteractionEnabled = true
        self.viewShadow.addGestureRecognizer(tapGestureRecognizerViewShadow)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по пункту "Повернуть" главного меню приложения
        let tapGestureRecognizerViewMainMenuRotate = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickViewMainMenuRotate))
        self.viewMainMenuRotate.userInteractionEnabled = true
        self.viewMainMenuRotate.addGestureRecognizer(tapGestureRecognizerViewMainMenuRotate)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по пункту "Поделиться" главного меню приложения
        let tapGestureRecognizerViewMainMenuShare = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickViewMainMenuShare))
        self.viewMainMenuShare.userInteractionEnabled = true
        self.viewMainMenuShare.addGestureRecognizer(tapGestureRecognizerViewMainMenuShare)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по кнопке - крестику закрытия панели добавления одежды
        let tapGestureRecognizerImageViewDressAddClose = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickImageViewDressAddClose))
        self.imageViewDressAddClose.userInteractionEnabled = true
        self.imageViewDressAddClose.addGestureRecognizer(tapGestureRecognizerImageViewDressAddClose)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по стрелке ВЛЕВО листания избранных наборов одежды
        let tapGestureRecognizerImageViewArrowLeft = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickImageViewArrowLeft))
        self.imageViewArrowLeft.userInteractionEnabled = true
        self.imageViewArrowLeft.addGestureRecognizer(tapGestureRecognizerImageViewArrowLeft)
        
        //-----------------------------------------------------------------------------------
        // Устанавливаем обработчик клика по стрелке ВПРАВО листания избранных наборов одежды
        let tapGestureRecognizerImageViewArrowRight = UITapGestureRecognizer(target: self, action:#selector(ViewControllerMain.clickImageViewArrowRight))
        self.imageViewArrowRight.userInteractionEnabled = true
        self.imageViewArrowRight.addGestureRecognizer(tapGestureRecognizerImageViewArrowRight)
        
        //-----------------------------------------------------------------------------------
        // Инициализируем боковое выдвигающееся меню
        self.setupSideMenu()
        
        //-----------------------------------------------------------------------------------
        // Создаем объект (экземпляр класса) для работы с локальной БД SQLite
        DBMain.setDBSQLiteHelper(DBSQLiteHelper())
        
        // Инициализируем все подкласса основного класса для работы с локальной БДSQLite
        DBMain.initializeSubClasses()
        
        //----------------------------------------------------------------------------------
        // Считываем информацию о категориях одежды
        DBMain.synchronizeAllDressCategories(
            GlobalFlags.ACTION_NO,
            isShowProgressDialogGetAllDressCategories: true,
            isCheckLocalDB: true
        )
        
        //--------------------------------------------------------------------------------------
        // Считываем данные о текущем пользователе из локальной БД
        let asyncTaskLoadUserDetailsFromLocalDB: AsyncTaskLoadUserDetailsFromLocalDB = AsyncTaskLoadUserDetailsFromLocalDB(context: self)
        asyncTaskLoadUserDetailsFromLocalDB.execute()
    }
    
    //=======================================================================================
    // Метод, запускаемый при отображении текущего View
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        
        // Устанавливаем заголовок для текущего окна
        switch(ViewControllerMain.getMainActivityViewType()) {
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION:
                self.title = GlobalFlagsStringsBar.barItemDressCollection
                break
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS:
                switch(GlobalFlags.getDressForWho()) {
                    case GlobalFlags.DRESS_MAN:
                        self.title = GlobalFlagsStringsBar.barItemDressMan
                        break
                    case GlobalFlags.DRESS_WOMAN:
                        self.title = GlobalFlagsStringsBar.barItemDressWoman
                        break
                    case GlobalFlags.DRESS_KID:
                        self.title = GlobalFlagsStringsBar.barItemDressKid
                        break
                    default:
                        self.title = GlobalFlagsStringsBar.barItemDressMan
                        break
                }
                break
            default:
                switch(GlobalFlags.getDressForWho()) {
                    case GlobalFlags.DRESS_MAN:
                        self.title = GlobalFlagsStringsBar.barItemDressMan
                        break
                    case GlobalFlags.DRESS_WOMAN:
                        self.title = GlobalFlagsStringsBar.barItemDressWoman
                        break
                    case GlobalFlags.DRESS_KID:
                        self.title = GlobalFlagsStringsBar.barItemDressKid
                        break
                    default:
                        self.title = GlobalFlagsStringsBar.barItemDressMan
                        break
                }
                break
        }
        
        // Отображаем верхнюю панель навигации
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        
        //-----------------------------------------------------------------------------------
        // Если данное окно запускается не впервый раз, то запускаем функцию инициализации всех
        // необходимых компонентов
        if(GlobalFlags.getIsAppFirstRun() == false) {
            self.initializeComponents(ViewControllerMain.getMainActivityViewType())
        }
    }
    
    //=======================================================================================
    // Метод, запускаемый при возникновении ошибки
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
  
    //=======================================================================================
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Меняем заголовок текущего окна, чтобы убрать надпись "Back" на кнопке назад
     //   self.navigationItem.title = " "
    }
    
    //==============================================================================================
    // Метод для считывания значения угла, на который повернуты изображения одежды вокруг вертикальной оси
    internal static func getDressRotationAngle() -> Int {
        return ViewControllerMain.mDressRotationAngle
    }
    
    //==============================================================================================
    // Метод для задания значения угла, на который повернуты изображения одежды вокруг вертикальной оси
    internal static func setDressRotationAngle(dressRotationAngle: Int) {
        ViewControllerMain.mDressRotationAngle = dressRotationAngle
    }
    
    //==============================================================================================
    // Метод для считывания типа просматриваемой информации в данном Activity (одежда или коллекции)
    internal static func getMainActivityViewType() -> Int {
        return ViewControllerMain.mMainActivityViewType
    }
    
    //==============================================================================================
    // Метод для задания типа просматриваемой информации в данном Activity (одежда или коллекции)
    internal static func setMainActivityViewType(mainActivityViewType: Int) {
        ViewControllerMain.mMainActivityViewType = mainActivityViewType
    }
    
    //==============================================================================================
    // Метод для считывания ссылки на меню, отображаемое в виде вкладок для добавления одежды на манекен
    internal func getPageMenuDressAdd() -> CAPSPageMenu? {
        return self.mPageMenuDressAdd
    }
    
    //==============================================================================================
    // Метод для задания ссылки на меню, отображаемое в виде вкладок для добавления одежды на манекен
    internal func setPageMenuDressAdd(pageMenuDressAdd: CAPSPageMenu) {
        self.mPageMenuDressAdd = pageMenuDressAdd
    }
    
    //==============================================================================================
    // Метод - обработчик клика отображения информации об одежде, одетой в текущий момент на виртуальном манекене
    @IBAction func clickButtonDressInfo(sender: UIBarButtonItem) {
        // Закрываем главное меню приложения и панель добавления одежды
        self.closeViewDressAdd()
        self.hideViewMainMenu()
        
        var arrayDressListId: Dictionary<String, String> = Dictionary<String, String>()
        
        // Если тип просматриваемого содержимого в главном окне приложения - ОДЕЖДА
        if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
            // Считываем id вещей, представленных в данный момент на вирутальном манекене
            // и передаем их в качестве параметра для отображаемого Acvtivity
            if (DBMain.getArrayPagerAdapterDressroom() != nil) {
                // В цикле перебираем все типы одежды
                for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                    let currentPagerAdapterDressroom: PagerAdapterDressroom? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.getArrayTagDressType()[indexDressType]]
                
                    if (currentPagerAdapterDressroom != nil) {
                        // Считываем параметры для одежды для текущего типа
                        let currentItemParams: Dictionary<String, String>? = currentPagerAdapterDressroom!.getItemParams(currentPagerAdapterDressroom!.getCurrentItemPosition())
                    
                        if (currentItemParams != nil) {
                            if(currentItemParams![GlobalFlags.TAG_ID] != nil) {
                                arrayDressListId[GlobalFlags.getArrayTagDressType()[indexDressType]] = currentItemParams![GlobalFlags.TAG_ID]
                            }
                        }
                    }
                }
            }
        }
        // Иначе, если тип просматриваемого содержимого в главном окне приложения - 
        // ИЗБРАННЫЕ НАБОРЫ ОДЕЖДЫ
        else if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
            // Считываем id вещей, представленных в данный момент на вирутальном манекене
            // и передаем их в качестве параметра для отображаемого Acvtivity
            if(DBMain.getPagerAdapterDressCollection() != nil) {
                // Считываем общий массив параметров для текущего набора одежды
                let currentItemParams: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getPagerAdapterDressCollection()!.getItemParams(
                    DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition()
                )
                
                // В цикле перебираем все типы одежды
                for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                    // Перебираем все возможные типы одежды
                    if(currentItemParams != nil) {
                        if (currentItemParams![GlobalFlags.getArrayTagDressType()[indexDressType]] != nil) {
                            // Считываем параметры одежды кокретно для текущего типа
                            let currentItemParamsForType: [Dictionary<String, String>] = currentItemParams![GlobalFlags.getArrayTagDressType()[indexDressType]]!
                            
                            // В цикле перебираем всю одежду для текущего типа
                            for indexCurrentItemParamsForType in 0..<currentItemParamsForType.count {
                                // Считываем параметры для текущей одежды
                                let currentItemParamsForTypeForDress: Dictionary<String, String> = currentItemParamsForType[indexCurrentItemParamsForType]
                                
                                if (currentItemParamsForTypeForDress[GlobalFlags.TAG_ID] != nil) {
                                    arrayDressListId[GlobalFlags.getArrayTagDressType()[indexDressType]] = currentItemParamsForTypeForDress[GlobalFlags.TAG_ID]
                                          
                                }
                            }
                        }
                    }
                }
            }
        }
        
        let mySQLGetDressFullInfo: MySQLGetDressFullInfo = MySQLGetDressFullInfo(
            context: self,
            dressCollectionType: GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
            arrayDressListId: arrayDressListId
        )
            
        mySQLGetDressFullInfo.startDressFullInfoLoad()
    }
    
    //==============================================================================================
    // Метод - обработчик клика по кнопке отображения/скрытия главногоменю приложения
    @IBAction func clickButtonMainMenu(sender: UIBarButtonItem) {
        self.showHideViewMainMenu()
    }
    
    //==============================================================================================
    // Метод - обработчик добавления набора одежды, представленного в текущий момент времени
    // на виртуальном манекене в раздел Мои коллекции
    @IBAction func clickButtonDressSave(sender: UIBarButtonItem) {
        // Закрываем главное меню приложения и панель добавления одежды
        self.closeViewDressAdd()
        self.hideViewMainMenu()
        
        // Если тип просматриваемого содержимого в главном окне приложения - ОДЕЖДА
        if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
            // Проверяем, если пользователь не авторизован, то перенаправляем на страницу
            // авторизации пользователя
            if (UserDetails.getIsUserLogged() == false) {
                // Перенаправляем на страницу авторизации пользователя
                let viewControllerLoginRegister: ViewControllerLoginRegister = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerLoginRegister") as! ViewControllerLoginRegister
            
                if(DBMain.getContext() != nil) {
                    DBMain.getContext()!.navigationItem.title = " "
                    DBMain.getContext()!.navigationController?.pushViewController(viewControllerLoginRegister, animated: true)
                }
            }
            // Иначе, сохраняем текущий набор одежды
            else {
                // Извлекаем id текущего набора одежды из тега для текущей кнопки
                let currentCollectionId: Int = self.mButtonDressSave.tag
            
                // Если id текущего набора одежды >0
                if (currentCollectionId > 0) {
                    // Удаляем информацию о текущем наборе одежды для текущего пользователя
                    DBMain.startDressCollectionUnSave(
                        GlobalFlags.ACTION_NO,
                        collectionUnSaveId: currentCollectionId,
                        dressCollectionType: GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                        buttonDressSaveBarButtonItem: self.mButtonDressSave,
                        isShowProgressDialogDressCollectionSave: true
                    )
                }
                // Иначе считаем, что текущий набор одежды НЕ был ранее сохранен для текущего пользователя
                else {
                    // Считываем id вещей, представленных в данный момент на вирутальном манекене
                    var arrayDressListId: Dictionary<String, String> = Dictionary<String, String>()
                
                    if (DBMain.getArrayPagerAdapterDressroom() != nil) {
                        // В цикле перебираем все типы одежды
                        for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                            let currentPagerAdapterDressroom: PagerAdapterDressroom? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.getArrayTagDressType()[indexDressType]]
                        
                            if (currentPagerAdapterDressroom != nil) {
                                // Считываем параметры для одежды для текущего типа
                                let currentItemParams: Dictionary<String, String>? = currentPagerAdapterDressroom!.getItemParams(currentPagerAdapterDressroom!.getCurrentItemPosition())
                            
                                if (currentItemParams != nil) {
                                    if (currentItemParams![GlobalFlags.TAG_ID] != nil) {
                                        arrayDressListId[GlobalFlags.getArrayTagDressType()[indexDressType]] = currentItemParams![GlobalFlags.TAG_ID]
                                    }
                                }
                            }
                        }
                    }
                
                    //------------------------------------------------------------------
                    // Сохраняем текущий набор одежды
                    DBMain.startDressCollectionSave(
                        GlobalFlags.ACTION_NO,
                        dressCollectionType: GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                        arrayDressListId: arrayDressListId,
                        buttonDressSaveBarButtonItem: self.mButtonDressSave,
                        isShowProgressDialogDressCollectionSave: true
                    )
                }
            }
        }
        // Иначе, если тип просматриваемого содержимого в главном окне приложения -
        // ИЗБРАННЫЕ НАБОРЫ ОДЕЖДЫ
        else if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
            // Создаем диалоговое окно подтверждения
            let alertDressCollectionUnSave = UIAlertController(title: nil, message: GlobalFlagsStringsNotification.stringTextDialogDressCollectionDelete, preferredStyle: UIAlertControllerStyle.Alert)
        
            // Добавляем кнопку Да
            alertDressCollectionUnSave.addAction(UIAlertAction(title: GlobalFlagsStrings.yes, style: .Default, handler: { (action: UIAlertAction!) in
                // Извлекаем id текущего набора одежды из тега для текущей кнопки
                let currentCollectionId: Int = self.mButtonDressSave.tag
                
                // Если id текущего набора одежды >0
                if (currentCollectionId > 0) {
                    // Удаляем информацию о текущем наборе одежды для текущего пользователя
                    DBMain.startDressCollectionUnSave(
                        GlobalFlags.ACTION_NO,
                        collectionUnSaveId: currentCollectionId,
                        dressCollectionType: GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                        buttonDressSaveBarButtonItem: self.mButtonDressSave,
                        isShowProgressDialogDressCollectionSave: true
                    )
                }
            }))
        
            // Добавляем кнопку Нет
            alertDressCollectionUnSave.addAction(UIAlertAction(title: GlobalFlagsStrings.no, style: .Cancel, handler: { (action: UIAlertAction!) in
            
            }))
        
            // Отображаем непосредственно всплывающее окно подтверждения
            self.presentViewController(alertDressCollectionUnSave, animated: true, completion: nil)
        }
    }
    
    //==============================================================================================
    // Метод - обработчик отображения панели с категориями одежды
    @IBAction func clickButtonDressAddOrShare(sender: AnyObject) {
        // Если тип содержимого, просматриваемого в главном окне приложения - ОДЕЖДА
        if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
            // Закрываем главное меню приложения
            self.hideViewMainMenu()
            
            // Если элемент View, являющийся контейнером для всплывающего меню
            // выбора добавляемой на манекен одежды, отображен, то закрываем его
            if (self.viewDressAdd.hidden == false) {
                self.closeViewDressAdd()
            }
            // Иначе, отображаем его
            else {
                // Меняем фон кнопки добавления одежды
                self.mButtonDressAddOrShare.tintColor = UIColor.whiteColor()
                
                //----------------------------------------------------------------------
                // Выводим кнопку - крестик закрытия панели добавления одежды на передний фронт
                self.viewDressAdd.bringSubviewToFront(self.imageViewDressAddClose)
                
                //----------------------------------------------------------------------
                // Затемняем
                self.view.bringSubviewToFront(self.viewShadow)
                self.viewShadow.hidden = false
                
                //----------------------------------------------------------------------
                // Отображаем непосредственно необходимый элемент View в виде анимации
                self.view.bringSubviewToFront(self.viewDressAdd)
                self.view.bringSubviewToFront(self.mToolbarBottom)
                
                // Отображаем подчеркивание кнопки отображения панели добавления одежды
                self.view.bringSubviewToFront(self.viewBorderButtonDressAdd)
                self.viewBorderButtonDressAdd.hidden = false
                
                // Устанавливаем наальную позицию панели добавления одежды
                self.mViewDressAddConstraintBottom.constant = (-1) * self.viewDressAdd.frame.height
                self.view.layoutIfNeeded()
                self.viewDressAdd.hidden = false
                
                // Выполняем анимацию перемещения для панели добавления одежды
                UIView.animateWithDuration(0.3, animations: {
                    self.mViewDressAddConstraintBottom.constant = 0
                    self.view.layoutIfNeeded()
                })
            }
        }
        // Иначе, если тип просматриваемого содержимого в главном окне приложения -
        // ИЗБРАННЫЕ НАБОРЫ ОДЕЖДЫ
        else if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
            self.clickViewMainMenuShare()
        }
    }
    
    //=======================================================================================
    // Метод - обработчик клика по кнопке - крестику закрытия панели добавления одежды
    func clickImageViewDressAddClose() {
        self.closeViewDressAdd()
    }
    
    //=======================================================================================
    // Метод - обработчик клика по стрелке ВЛЕВО листания избранных наборов одежды
    func clickImageViewArrowLeft() {
        if(DBMain.getPagerAdapterDressCollection() != nil) {
            // Если достигнуто начало списка
            if (DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition() <= 0) {
                // Выводим соответствующее предупреждение об этом
                if(ViewControllerMain.presentWindow != nil) {
                    ViewControllerMain.presentWindow!.makeToast(message: GlobalFlagsStringsNotification.stringNoDressPrev, duration: 2, position: "center")
                }
            }
            // Иначе, перелистываем влево
            else {
                // Уменьшаем текущий порядковый номер набора одежды на -1
                DBMain.getPagerAdapterDressCollection()!.setCurrentItemPosition(
                    DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition() - 1
                )
                
                // Определяем порядковый номер текущей позиции
                let currentPosition: Int = DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition()
                
                if(currentPosition <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                    let mySQLGoToDressCollectionSwipe: MySQLGoToDressCollectionSwipe = MySQLGoToDressCollectionSwipe()
                    mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT)
                }
                
                // Перезагружаем элемент ViewPager для ибранных наборов одежды
                self.reloadViewPagerDressCollection()
            }
        }
    }
    
    //=======================================================================================
    // Метод - обработчик клика по стрелке ВПРАВО листания избранных наборов одежды
    func clickImageViewArrowRight() {
        if(DBMain.getPagerAdapterDressCollection() != nil) {
            // Если достигнут конец списка
            if (DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition() >= DBMain.getPagerAdapterDressCollection()!.getCount() - 1) {
                // Выводим соответствующее предупреждение об этом
                if(ViewControllerMain.presentWindow != nil) {
                    ViewControllerMain.presentWindow!.makeToast(message: GlobalFlagsStringsNotification.stringNoDressNext, duration: 2, position: "center")
                }
            }
            // Иначе, перелистываем вправо
            else {
                // Увеличиваем текущий порядковый номер набора одежды на +1
                DBMain.getPagerAdapterDressCollection()!.setCurrentItemPosition(
                    DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition() + 1
                )
                
                // Определяем порядковый номер текущей позиции
                let currentPosition: Int = DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition()
                
                // Общее количество отображаемых наборов одежды
                let arrayParamsCount: Int = DBMain.getPagerAdapterDressCollection()!.getCount()
                
                if(arrayParamsCount > 0 && (arrayParamsCount - 1) - currentPosition <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                    let mySQLGoToDressCollectionSwipe: MySQLGoToDressCollectionSwipe = MySQLGoToDressCollectionSwipe()
                    mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT)
                }
               
                // Перезагружаем элемент ViewPager для ибранных наборов одежды
                self.reloadViewPagerDressCollection()
            }
        }
    }
    
    //=======================================================================================
    // Метод - обработчик клика по пункту "Повернуть" главного меню приложения
    func clickViewMainMenuRotate() {
        // Закрываем главное меню приложения
        self.hideViewMainMenu()
        
        //----------------------------------------------------------------
        // Поворачиваем виртуальный манекен на 180 градусов
        switch (ViewControllerMain.getDressRotationAngle()) {
            case GlobalFlags.DRESS_ROTATION_ANGLE_0:                        // если в настоящий момент угол поворота манекена составляет 0 градусов
                ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_180)
                break
            case GlobalFlags.DRESS_ROTATION_ANGLE_180:                      // если в настоящий момент угол поворота манекена составляет 180 градусов
                ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                break
            default:
                ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                break
        }
        
        //------------------------------------------------------------------
        // Перезагружаем соответствующие адаптеры в зависимости от типа
        // просматриваемого содержимого в главном окне приложения
        switch (ViewControllerMain.getMainActivityViewType()) {
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS:                 // если тип содержимого  - ОДЕЖДА
                self.reloadViewPagerDressroom()
                break
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION:            // если тип содержимого  - СОХРАНЕННЫЕ КОЛЛЕКЦИИ
                self.reloadViewPagerDressCollection()
                break
            default:
                break
        }
    }
    
    //=======================================================================================
    // Метод - обработчик клика по пункту "Поделиться" главного меню приложения
    func clickViewMainMenuShare() {
        // Закрываем главное меню приложения
        self.hideViewMainMenu()
        
        DialogMain.createDialog(GlobalFlags.DIALOG_SHARE, dialogParams: nil, message: nil)
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для категории одежды "Головные уборы"
    func pageViewAtIndexHead(pageIndex: Int) -> ViewControllerDressroomHeadContent {
        let pageContentViewControllerHead: ViewControllerDressroomHeadContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressroomHeadContent") as! ViewControllerDressroomHeadContent
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_HEAD] != nil) {
                var currentPagerAdapterDressroomParams: Dictionary<String, String>? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_HEAD]!.getItemParams(pageIndex)
                
                if(currentPagerAdapterDressroomParams != nil) {
                    // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                    switch (ViewControllerMain.getDressRotationAngle()) {
                        case GlobalFlags.DRESS_ROTATION_ANGLE_0:        // если в настоящий момент угол поворота манекена составляет 0 градусов
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerHead.imageDressroomHead = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerHead.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                        
                        case GlobalFlags.DRESS_ROTATION_ANGLE_180:      // если в настоящий момент угол поворота манекена составляет 180 градусов
                            // Загружаем изображение для обратной части
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                pageContentViewControllerHead.imageDressroomHead = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerHead.labelText = GlobalFlagsStrings.stringDressImageBackNo
                        
                            break
                        
                        default:
                            // По умолчанию считаем, что манекен должен быть повернут на 0 градусов
                            ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                        
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerHead.imageDressroomHead = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                            
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerHead.labelText = GlobalFlagsStrings.stringDressImageError
                            
                            break
                    }
                    
                    // Задаем id для соответствующей одежды
                    if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID] != nil) {
                        pageContentViewControllerHead.currentDressId = currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID]!
                    }
                    
                    // Задаем порядковый номер для текущей страницы
                    pageContentViewControllerHead.pageIndex = pageIndex
                }
            }
        }
        
        return pageContentViewControllerHead
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для категории одежды "Верх"
    func pageViewAtIndexBody(pageIndex: Int) -> ViewControllerDressroomBodyContent {
        let pageContentViewControllerBody: ViewControllerDressroomBodyContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressroomBodyContent") as! ViewControllerDressroomBodyContent
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_BODY] != nil) {
                var currentPagerAdapterDressroomParams: Dictionary<String, String>? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_BODY]!.getItemParams(pageIndex)
                
                if(currentPagerAdapterDressroomParams != nil) {
                    // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                    switch (ViewControllerMain.getDressRotationAngle()) {
                        case GlobalFlags.DRESS_ROTATION_ANGLE_0:        // если в настоящий момент угол поворота манекена составляет 0 градусов
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerBody.imageDressroomBody = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerBody.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                        
                        case GlobalFlags.DRESS_ROTATION_ANGLE_180:      // если в настоящий момент угол поворота манекена составляет 180 градусов
                            // Загружаем изображение для обратной части
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                pageContentViewControllerBody.imageDressroomBody = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerBody.labelText = GlobalFlagsStrings.stringDressImageBackNo
                        
                            break
                        
                        default:
                            // По умолчанию считаем, что манекен должен быть повернут на 0 градусов
                            ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                        
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerBody.imageDressroomBody = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerBody.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                    }
                    
                    // Задаем id для соответствующей одежды
                    if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID] != nil) {
                        pageContentViewControllerBody.currentDressId = currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID]!
                    }
                    
                    // Задаем порядковый номер для текущей страницы
                    pageContentViewControllerBody.pageIndex = pageIndex
                }
            }
        }
        
        return pageContentViewControllerBody
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для категории одежды "Низ"
    func pageViewAtIndexLeg(pageIndex: Int) -> ViewControllerDressroomLegContent {
        let pageContentViewControllerLeg: ViewControllerDressroomLegContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressroomLegContent") as! ViewControllerDressroomLegContent
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_LEG] != nil) {
                var currentPagerAdapterDressroomParams: Dictionary<String, String>? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_LEG]!.getItemParams(pageIndex)
                
                if(currentPagerAdapterDressroomParams != nil) {
                    // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                    switch (ViewControllerMain.getDressRotationAngle()) {
                        case GlobalFlags.DRESS_ROTATION_ANGLE_0:        // если в настоящий момент угол поворота манекена составляет 0 градусов
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerLeg.imageDressroomLeg = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerLeg.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                        
                        case GlobalFlags.DRESS_ROTATION_ANGLE_180:      // если в настоящий момент угол поворота манекена составляет 180 градусов
                            // Загружаем изображение для обратной части
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                pageContentViewControllerLeg.imageDressroomLeg = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerLeg.labelText = GlobalFlagsStrings.stringDressImageBackNo
                        
                            break
                        
                        default:
                            // По умолчанию считаем, что манекен должен быть повернут на 0 градусов
                            ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                        
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerLeg.imageDressroomLeg = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerLeg.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                    }
                    
                    // Задаем id для соответствующей одежды
                    if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID] != nil) {
                        pageContentViewControllerLeg.currentDressId = currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID]!
                    }
                    
                    // Задаем порядковый номер для текущей страницы
                    pageContentViewControllerLeg.pageIndex = pageIndex
                }
            }
        }
        
        return pageContentViewControllerLeg
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для категории одежды "Обувь"
    func pageViewAtIndexFoot(pageIndex: Int) -> ViewControllerDressroomFootContent {
        let pageContentViewControllerFoot: ViewControllerDressroomFootContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressroomFootContent") as! ViewControllerDressroomFootContent
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_FOOT] != nil) {
                var currentPagerAdapterDressroomParams: Dictionary<String, String>? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_FOOT]!.getItemParams(pageIndex)
                
                if(currentPagerAdapterDressroomParams != nil) {
                    // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                    switch (ViewControllerMain.getDressRotationAngle()) {
                        case GlobalFlags.DRESS_ROTATION_ANGLE_0:        // если в настоящий момент угол поворота манекена составляет 0 градусов
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerFoot.imageDressroomFoot = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerFoot.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                        
                        case GlobalFlags.DRESS_ROTATION_ANGLE_180:      // если в настоящий момент угол поворота манекена составляет 180 градусов
                            // Загружаем изображение для обратной части
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                pageContentViewControllerFoot.imageDressroomFoot = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerFoot.labelText = GlobalFlagsStrings.stringDressImageBackNo
                        
                            break
                        
                        default:
                            // По умолчанию считаем, что манекен должен быть повернут на 0 градусов
                            ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                        
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerFoot.imageDressroomFoot = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerFoot.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                    }
                    
                    // Задаем id для соответствующей одежды
                    if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID] != nil) {
                        pageContentViewControllerFoot.currentDressId = currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID]!
                    }
                    
                    // Задаем порядковый номер для текущей страницы
                    pageContentViewControllerFoot.pageIndex = pageIndex
                }
            }
        }
        
        return pageContentViewControllerFoot
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для категории одежды "Аксессуары"
    func pageViewAtIndexAccessory(pageIndex: Int) -> ViewControllerDressroomAccessoryContent {
        let pageContentViewControllerAccessory: ViewControllerDressroomAccessoryContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressroomAccessoryContent") as! ViewControllerDressroomAccessoryContent
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_ACCESSORY] != nil) {
                var currentPagerAdapterDressroomParams: Dictionary<String, String>? = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_ACCESSORY]!.getItemParams(pageIndex)
                
                if(currentPagerAdapterDressroomParams != nil) {
                    // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                    switch (ViewControllerMain.getDressRotationAngle()) {
                        case GlobalFlags.DRESS_ROTATION_ANGLE_0:        // если в настоящий момент угол поворота манекена составляет 0 градусов
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerAccessory.imageDressroomAccessory = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerAccessory.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                        
                        case GlobalFlags.DRESS_ROTATION_ANGLE_180:      // если в настоящий момент угол поворота манекена составляет 180 градусов
                            // Загружаем изображение для обратной части
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK] != nil) {
                                pageContentViewControllerAccessory.imageDressroomAccessory = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE_BACK]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerAccessory.labelText = GlobalFlagsStrings.stringDressImageBackNo
                        
                            break
                        
                        default:
                            // По умолчанию считаем, что манекен должен быть повернут на 0 градусов
                            ViewControllerMain.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0)
                        
                            // Загружаем изображение для лицевой стороны
                            if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE] != nil) {
                                pageContentViewControllerAccessory.imageDressroomAccessory = currentPagerAdapterDressroomParams![GlobalFlags.TAG_IMAGE]!
                            }
                        
                            // Задаем текст для соответствующего элемента Label
                            pageContentViewControllerAccessory.labelText = GlobalFlagsStrings.stringDressImageError
                        
                            break
                    }
                    
                    // Задаем id для соответствующей одежды
                    if(currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID] != nil) {
                        pageContentViewControllerAccessory.currentDressId = currentPagerAdapterDressroomParams![GlobalFlags.TAG_ID]!
                    }
                    
                    // Задаем порядковый номер для текущей страницы
                    pageContentViewControllerAccessory.pageIndex = pageIndex
                }
            }
        }
        
        return pageContentViewControllerAccessory
    }
    
    //=======================================================================================
    // Метод для извлечения View для текущей активной страницы для текущего ViewPager
    // для избранных наборов одежды
    func pageViewAtIndexCollection(pageIndex: Int) -> ViewControllerDressCollectionContent {
        let pageContentViewControllerDressCollection: ViewControllerDressCollectionContent = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressCollectionContent") as! ViewControllerDressCollectionContent
        
        // Задаем порядковый номер для текущей страницы
        pageContentViewControllerDressCollection.pageIndex = pageIndex
        
        return pageContentViewControllerDressCollection
    }
    
    //========================================================================================
    // Листание элемента ViewPager влево
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        var pageIndex: Int = 0      // порядковый номер текущей активной страницы для текущего элемента ViewPager
        var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD
        
        // Определяем тип текущего ViewPager по его id
        if(pageViewController.restorationIdentifier != nil) {
            switch(pageViewController.restorationIdentifier!) {
                case "PageViewControllerDressroomHead":
                    let viewController: ViewControllerDressroomHeadContent = viewController as! ViewControllerDressroomHeadContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_HEAD
                    break
                case "PageViewControllerDressroomBody":
                    let viewController: ViewControllerDressroomBodyContent = viewController as! ViewControllerDressroomBodyContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_BODY
                    break
                case "PageViewControllerDressroomLeg":
                    let viewController: ViewControllerDressroomLegContent = viewController as! ViewControllerDressroomLegContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_LEG
                    break
                case "PageViewControllerDressroomFoot":
                    let viewController: ViewControllerDressroomFootContent = viewController as! ViewControllerDressroomFootContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_FOOT
                    break
                case "PageViewControllerDressroomAccessory":
                    let viewController: ViewControllerDressroomAccessoryContent = viewController as! ViewControllerDressroomAccessoryContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_ACCESSORY
                    break
                case "PageViewControllerDressCollection":
                    let viewController: ViewControllerDressCollectionContent = viewController as! ViewControllerDressCollectionContent
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_COLLECTION
                    break
                default:
                    break
            }
        }
        
        if(pageIndex == 0 || pageIndex == NSNotFound) {
            return nil
        }
        
        // Уменьшаем порядковый номер текущей активной страницы на -1
        pageIndex -= 1
        
        // В зависимости от типа текущей одежды возвращаем соответствующее значение
        switch (currentDressType) {
            case GlobalFlags.TAG_DRESS_HEAD:
                return self.pageViewAtIndexHead(pageIndex)
            case GlobalFlags.TAG_DRESS_BODY:
                return self.pageViewAtIndexBody(pageIndex)
            case GlobalFlags.TAG_DRESS_LEG:
                return self.pageViewAtIndexLeg(pageIndex)
            case GlobalFlags.TAG_DRESS_FOOT:
                return self.pageViewAtIndexFoot(pageIndex)
            case GlobalFlags.TAG_DRESS_ACCESSORY:
                return self.pageViewAtIndexAccessory(pageIndex)
            case GlobalFlags.TAG_COLLECTION:
                return self.pageViewAtIndexCollection(pageIndex)
            default:
                return nil
        }
    }
    
    //========================================================================================
    // Листание элемента ViewPager вправо
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        var pageIndex: Int = 0          // порядковый номер текущей активной страницы для текущего элемента ViewPager
        var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD   // тип одежды для текущего ViewPager
        var pageTotalCount: Int = 0     // общее количество страниц для текущего ViewPager
        
        // Определяем тип текущего ViewPager по его id
        if(pageViewController.restorationIdentifier != nil) {
            switch(pageViewController.restorationIdentifier!) {
                case "PageViewControllerDressroomHead":
                    let viewController: ViewControllerDressroomHeadContent = viewController as! ViewControllerDressroomHeadContent
                
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_HEAD
                
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                        if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_HEAD] != nil) {
                            pageTotalCount = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_HEAD]!.getCount()
                        }
                    }
                
                    break
                
                case "PageViewControllerDressroomBody":
                    let viewController: ViewControllerDressroomBodyContent = viewController as! ViewControllerDressroomBodyContent
                
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_BODY
                
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                        if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_BODY] != nil) {
                            pageTotalCount = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_BODY]!.getCount()
                        }
                    }
                
                    break
                
                case "PageViewControllerDressroomLeg":
                    let viewController: ViewControllerDressroomLegContent = viewController as! ViewControllerDressroomLegContent
                
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_LEG
                
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                        if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_LEG] != nil) {
                            pageTotalCount = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_LEG]!.getCount()
                        }
                    }
                
                    break
                
                case "PageViewControllerDressroomFoot":
                    let viewController: ViewControllerDressroomFootContent = viewController as! ViewControllerDressroomFootContent
                
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_FOOT
                
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                        if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_FOOT] != nil) {
                            pageTotalCount = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_FOOT]!.getCount()
                        }
                    }
                
                    break
                
                case "PageViewControllerDressroomAccessory":
                    let viewController: ViewControllerDressroomAccessoryContent = viewController as! ViewControllerDressroomAccessoryContent
                
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_DRESS_ACCESSORY
                
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                        if(DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_ACCESSORY] != nil) {
                            pageTotalCount = DBMain.getArrayPagerAdapterDressroom()![GlobalFlags.TAG_DRESS_ACCESSORY]!.getCount()
                        }
                    }
                    
                    break
                
                case "PageViewControllerDressCollection":
                    let viewController: ViewControllerDressCollectionContent = viewController as! ViewControllerDressCollectionContent
                    
                    pageIndex = viewController.pageIndex as Int
                    currentDressType = GlobalFlags.TAG_COLLECTION
                    
                    // Определяем общее количество страниц для текущего ViewPager
                    if(DBMain.getPagerAdapterDressCollection() != nil) {
                        pageTotalCount = DBMain.getPagerAdapterDressCollection()!.getCount()
                    }
                    
                    break
                
                default:
                    break
            }
        }
        
        if(pageIndex == NSNotFound) {
            return nil
        }
        
        pageIndex += 1
        
        if(pageIndex == pageTotalCount) {
            return nil
        }
        
        // В зависимости от типа текущей одежды возвращаем соответствующее значение
        switch (currentDressType) {
            case GlobalFlags.TAG_DRESS_HEAD:
                return self.pageViewAtIndexHead(pageIndex)
            case GlobalFlags.TAG_DRESS_BODY:
                return self.pageViewAtIndexBody(pageIndex)
            case GlobalFlags.TAG_DRESS_LEG:
                return self.pageViewAtIndexLeg(pageIndex)
            case GlobalFlags.TAG_DRESS_FOOT:
                return self.pageViewAtIndexFoot(pageIndex)
            case GlobalFlags.TAG_DRESS_ACCESSORY:
                return self.pageViewAtIndexAccessory(pageIndex)
            case GlobalFlags.TAG_COLLECTION:
                return self.pageViewAtIndexCollection(pageIndex)
            default:
                return nil
        }
    }

    //===================================================================
    // Метод - обработчик события перелистывания элемента ViewPager
    func pageViewController(pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        var pageIndex: Int = 0                                      // порядковый номер текущей активной страницы для текущего элемента ViewPager
        var currentDressType: String = GlobalFlags.TAG_DRESS_HEAD   // тип одежды для текущего ViewPager
        
        // Если перелистывание завершено
        if(completed && pageViewController.viewControllers != nil) {
            // Определяем тип текущего ViewPager по его id
            if(pageViewController.restorationIdentifier != nil) {
                switch(pageViewController.restorationIdentifier!) {
                    case "PageViewControllerDressroomHead":
                        let viewController: ViewControllerDressroomHeadContent = pageViewController.viewControllers!.first as! ViewControllerDressroomHeadContent
                    
                        // Определяем тип одежды и текущий порядковый номер для текущего элемента ViewPager
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_DRESS_HEAD
 
                        break
                    
                    case "PageViewControllerDressroomBody":
                        let viewController: ViewControllerDressroomBodyContent = pageViewController.viewControllers!.first as! ViewControllerDressroomBodyContent
                    
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_DRESS_BODY
                    
                        break
                    
                    case "PageViewControllerDressroomLeg":
                        let viewController: ViewControllerDressroomLegContent = pageViewController.viewControllers!.first as! ViewControllerDressroomLegContent
                    
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_DRESS_LEG
                        
                        break
                    
                    case "PageViewControllerDressroomFoot":
                        let viewController: ViewControllerDressroomFootContent = pageViewController.viewControllers!.first as! ViewControllerDressroomFootContent
                    
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_DRESS_FOOT
                    
                        break
                    
                    case "PageViewControllerDressroomAccessory":
                        let viewController: ViewControllerDressroomAccessoryContent = pageViewController.viewControllers!.first as! ViewControllerDressroomAccessoryContent
                    
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_DRESS_ACCESSORY
                    
                        break
                    
                    case "PageViewControllerDressCollection":
                        let viewController: ViewControllerDressCollectionContent = pageViewController.viewControllers!.first as! ViewControllerDressCollectionContent
                    
                        pageIndex = viewController.pageIndex as Int
                        currentDressType = GlobalFlags.TAG_COLLECTION
                    
                        break
                    
                    default:
                        break
                }
            }
        }
        
        //---------------------------------------------------------------
        // Подгружаем дополнительную одежду
        
        // Если тип просматриваемого содержимого - ОДЕЖДА
        if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
            if(DBMain.getArrayPagerAdapterDressroom() != nil) {
                if (DBMain.getArrayPagerAdapterDressroom()![currentDressType] != nil) {
                    var swipeDirection: Int = 0     // переменная, хранящая направление листания одежды
                    
                    // Определяем направление листания
                    if(DBMain.getArrayPagerAdapterDressroom()![currentDressType]!.getCurrentItemPosition() - pageIndex < 0) {
                        swipeDirection = GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT
                    }
                    else {
                        swipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT
                    }
                    
                    //----------------------------------------------------------------
                    // Сохраняем номер текущей активной страницы для соответствующего элемента ViewPager
                    DBMain.getArrayPagerAdapterDressroom()![currentDressType]!.setCurrentItemPosition(pageIndex)
                    
                    //-----------------------------------------------------------
                    // Запускаем метод для проверки, сохранен ли набор одежды, в данный момент
                    // отображаемый на виртуальном манекене, для текущего пользователя
                    let mySQLCheckIsSaveCurrentCollection: MySQLCheckIsSaveCurrentCollection = MySQLCheckIsSaveCurrentCollection(buttonDressSaveBarButtonItem: self.mButtonDressSave)
                    mySQLCheckIsSaveCurrentCollection.startCheckIsSaveCurrentCollection()
                        
                    //------------------------------------------------------------
                    // Логическая переменная, определяющая необходимо ли загружать информацию
                    // о соответствующей одежды
                    var isLoadDressInfo: Bool = false
                        
                    //----------------------------------------------------------------------
                    // Адаптер для текущего элемента ViewPager
                    let currentPagerAdapterDressroom: PagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom()![currentDressType]!
                
                    //----------------------------------------------------------------------
                    // В зависимости от направления листания
                    switch (swipeDirection) {
                        case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                         // листание слева направо
                            if(pageIndex <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                isLoadDressInfo = true
                            }
                            
                            break
                        
                        case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                         // листание справа налево
                            let arrayParamsCount: Int = currentPagerAdapterDressroom.getCount()
                            
                            if(arrayParamsCount > 0 && (arrayParamsCount - 1) - pageIndex <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                isLoadDressInfo = true
                            }
                            
                            break
                
                        default:
                            break
                    }
                        
                    //----------------------------------------------------------------------
                    // Вызываем метод для загрузки информации о дополнительной одежде
                    if(isLoadDressInfo == true) {
                        let mySQLGoToDress: MySQLGoToDress = MySQLGoToDress(context: self)
                        mySQLGoToDress.startGoToDress(GlobalFlags.ACTION_NO, swipeDirection: swipeDirection, targetDressType: currentDressType)
                    }
                }
            }
        }
        // Иначе, если тип просматриваемого содержимого - ИЗБРАННЫЕ НАБОРЫ ОДЕЖДЫ
        else if(ViewControllerMain.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
            if(DBMain.getPagerAdapterDressCollection() != nil) {
                var swipeDirection: Int = 0     // переменная, хранящая направление листания коллекций одежды
                
                // Определяем направление листания
                if (DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition() - pageIndex < 0) {
                    swipeDirection = GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT;
                }
                else {
                    swipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT;
                }
                
                //----------------------------------------------------------------
                // Сохраняем номер текущей активной страницы для соответствующего элемента ViewPager
                DBMain.getPagerAdapterDressCollection()!.setCurrentItemPosition(pageIndex)
                    
                //------------------------------------------------------------------------
                // Если выбран раздел "Мои коллекции"
                if (GlobalFlags.getLeftMenuSelectedSection() == GlobalFlags.LEFT_MENU_SELECTED_SECTION_MY_COLLECTIONS_CATEGORY &&
                    GlobalFlags.getLeftMenuSelectedRow() == 0) {
                    // Считываем id текущей активной коллекции
                    let currentDressCollectionId: Int = DBMain.getPagerAdapterDressCollection()!.getItemParamsId(pageIndex)
                            
                    // Если id текущей коллекции одежды успешно считан
                    if (currentDressCollectionId > 0) {
                        // Устанавливаем id текущей коллекции одежды в качестве тега
                        // для кнопки сохранения информации о текущем наборе одежды
                        self.mButtonDressSave.tag = currentDressCollectionId
                    }
                }
                    
                //--------------------------------------------------------------------------
                // Логическая переменная, определяющая необходимо ли загружать информацию
                // о соответствующей коллекции одежды
                var isLoadDressCollectionInfo: Bool = false
                    
                //--------------------------------------------------------------------------
                // В зависимости от направления листания
                switch (swipeDirection) {
                    case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                             // листание слева направо
                        if (pageIndex <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                            isLoadDressCollectionInfo = true
                        }
                        
                        break
                    
                    case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                             // листание справа налево
                        let arrayParamsCount: Int = DBMain.getPagerAdapterDressCollection()!.getCount()
                        
                        if (arrayParamsCount > 0 && (arrayParamsCount - 1) - pageIndex <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                            isLoadDressCollectionInfo = true
                        }
                        
                        break
                    
                    default:
                        break
                }
                    
                //--------------------------------------------------------------------------
                // Вызываем метод для загрузки информации о дополнительной коллекции одежде
                if (isLoadDressCollectionInfo == true) {
                    let mySQLGoToDressCollectionSwipe: MySQLGoToDressCollectionSwipe = MySQLGoToDressCollectionSwipe()
                    mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(swipeDirection)
                }
            }
        }
    }
    
    //===================================================================
    // Метод для инициализации бокового выдвигающегося меню
    private func setupSideMenu() {
        let menuLeftNavigationController: UISideMenuNavigationController = self.storyboard?.instantiateViewControllerWithIdentifier("LeftMenuNavigationController") as! UISideMenuNavigationController
          
        SideMenuManager.menuLeftNavigationController = menuLeftNavigationController
        
        SideMenuManager.menuAddPanGestureToPresent(toView: self.navigationController!.navigationBar)
        SideMenuManager.menuAddScreenEdgePanGesturesToPresent(toView: self.navigationController!.view)
    }
    
    //==============================================================================================
    // Метод для первоначальной инициализации необходимых компонентов
    // Передаваемые параметры
    // viewType - тип просматриваемого контента (одежда или коллекции)
    internal func initializeComponents(viewType: Int) {
        // Очищаем главное окно приложения
        self.clearViewMainWindow()
        
        //------------------------------------------------------------------------------------------
        // Запоминаем тип просматриваемого контента
        ViewControllerMain.setMainActivityViewType(viewType)

        //------------------------------------------------------------------------------------------
        // В зависимости от типа просматриваемого контента выполняем определнные операции
        switch (viewType) {
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION:        // если просматриваются коллекции
                // Считываем данные о коллекциях одежды
                DBMain.synchronizeDressCollectionInfo(GlobalFlags.ACTION_NO)
    
                break
    
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS:         // если просматриваем одежду
                if(DBMain.getMySQLGoToDressLastView() == nil) {
                    DBMain.setMySQLGoToDressLastView(MySQLGoToDressLastView(context: self))
                }
                
                //----------------------------------------------------------------------------------
                // Загружаем информацию об одежде
                DBMain.synchronizeGoToDressLastView(GlobalFlags.ACTION_NO, isProgressDialogShow: true)
                
                break
            
            default:
                if(DBMain.getMySQLGoToDressLastView() == nil) {
                    DBMain.setMySQLGoToDressLastView(MySQLGoToDressLastView(context: self))
                }
    
                //----------------------------------------------------------------------------------
                // Загружаем информацию об одежде
                DBMain.synchronizeGoToDressLastView(GlobalFlags.ACTION_NO, isProgressDialogShow: true)
    
                break
        }
    }
  
    //====================================================================
    // Метод для начальной инициализации компонентов при запуске приложения
    internal func initializeComponentsLayoutContentMain() {
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        ViewControllerMain.setDressRotationAngle(0)
        
        //---------------------------------------------------------------
        self.mButtonDressAddOrShare.image = UIImage(named: "add")
        
        //---------------------------------------------------------------
        // Инициализируем элементы ViewPager
        self.showViewPagerDressroom()
        
        //---------------------------------------------------------------
        // Переводим на передний фон диалоговое окно с информацией 
        // о текущей одежде и затемнение
        self.view.bringSubviewToFront(self.viewShadow)
        self.view.bringSubviewToFront(self.viewDialogShowDressInfo)
        self.view.bringSubviewToFront(self.viewDialogShare)
    }
    
    //====================================================================
    // Метод для начальной инициализации компонентов при запуске приложения для layout = "collections"
    internal func initializeComponentsLayoutCollections() {
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        ViewControllerMain.setDressRotationAngle(0)
        
        //---------------------------------------------------------------
        self.mButtonDressAddOrShare.image = UIImage(named: "share-toolbar")
        self.mButtonDressSave.image = UIImage(named: "favorite2")
        
        //---------------------------------------------------------------
        // Инициализируем элементы ViewPager
        self.showViewPagerDressCollection()
    }
    
    //====================================================================
    // Метод для инициализации элементов ViewPager
    internal func showViewPagerDressroom() {
        // Переменная, хранящая смещение текущего изображения по высоте относительно
        // предыдущего изображения
        var offsetHeight: CGFloat = 0
        
        //----------------------------------------------------------------------------------
        // Определяем суммарную высоту всей одежды для текущего набора
        var currentDressCollectionSumHeight: CGFloat = 0
        
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                let currentDressType: String = GlobalFlags.getArrayTagDressType()[indexDressType]
                
                if(DBMain.getArrayPagerAdapterDressroom()![currentDressType] != nil) {
                    // Определяем высоту для текущего элемента ViewPager
                    var pageViewControllerCurrentTypeHeight: CGFloat = 0
                
                    // Определяем высоту для текущего элемента ViewPager равной целевой высоте изображения
                    // для текущей одежды
                    if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + currentDressType + "_2"] != nil) {
                            pageViewControllerCurrentTypeHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + currentDressType + "_2"]!)
                        }
                    }
                
                    // Иначе определяем высоту для текущего элемента ViewPager равной исходной высоте изображения
                    // для текущей одежды
                    if(pageViewControllerCurrentTypeHeight == 0) {
                        if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + currentDressType + "_2"] != nil) {
                                pageViewControllerCurrentTypeHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + currentDressType + "_2"]!)
                            }
                        }
                    }
                
                    // Прибавляем высоту текущего элемента ViewPager к общей высоте
                    currentDressCollectionSumHeight += pageViewControllerCurrentTypeHeight
                }
            }
        }
        
        //----------------------------------------------------------------------------------
        // Чтобы разместить текущий набор одежды на экране по высоте по центру,
        // добавляем к смещению по высоте половину разницы между высотой экрана устройства и
        // суммарной высоты всей одежды для текущего набора
        offsetHeight += (CGFloat(FunctionsScreen.getScreenHeight()) - currentDressCollectionSumHeight) / 2
        
        //----------------------------------------------------------------
        // Логическая переменная, указывающая на то, было ли отображено
        // изображение для категории одежды "Низ"
        var isDressTypeLegExists: Bool = false
        
        //-----------------------------------------------------------------
        // Инициализируем элементы ViewPager
        if(DBMain.getArrayPagerAdapterDressroom() != nil) {
            for indexDressType in (0..<GlobalFlags.getArrayTagDressType().count).reverse() {
                let currentDressType: String = GlobalFlags.getArrayTagDressType()[indexDressType]
                
                // Инициализируем соответствующий элемент ViewPager
                switch(currentDressType) {
                    case GlobalFlags.TAG_DRESS_HEAD:
                        self.pageViewControllerHead = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressroomHead") as! UIPageViewController
                        break
                    case GlobalFlags.TAG_DRESS_BODY:
                        self.pageViewControllerBody = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressroomBody") as! UIPageViewController
                        break
                    case GlobalFlags.TAG_DRESS_LEG:
                        self.pageViewControllerLeg = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressroomLeg") as! UIPageViewController
                        break
                    case GlobalFlags.TAG_DRESS_FOOT:
                        self.pageViewControllerFoot = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressroomFoot") as! UIPageViewController
                        break
                    case GlobalFlags.TAG_DRESS_ACCESSORY:
                        self.pageViewControllerAccessory = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressroomAccessory") as! UIPageViewController
                        break
                    default:
                        break
                }
                
                // Устанавливаем контент для соответствующего элемента ViewPager
                if(DBMain.getArrayPagerAdapterDressroom()![currentDressType] != nil) {
                    // Текущая позицию для соответствующего элемента ViewPager
                    // равной номеру позиции одежды, отображаемой в первую очередь для соответствующего адаптера
                    let currentPosition: Int = DBMain.getArrayPagerAdapterDressroom()![currentDressType]!.getCurrentItemPosition()
                    
                    switch(currentDressType) {
                    case GlobalFlags.TAG_DRESS_HEAD:                    // головные уборы
                        self.pageViewControllerHead.dataSource = self
                        self.pageViewControllerHead.delegate = self
                        
                        let initialPageViewControllerDressroomHead: ViewControllerDressroomHeadContent = self.pageViewAtIndexHead(currentPosition)
                        
                        let viewControllersDressroomHead: NSArray = NSArray(object: initialPageViewControllerDressroomHead)
                        
                        self.pageViewControllerHead.setViewControllers(viewControllersDressroomHead as! [ViewControllerDressroomHeadContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
                        // Задаем высоту для текущего элемента ViewPager
                        var pageViewControllerHeadHeight: CGFloat = 0
                        
                        // Задаем высоту для текущего элемента ViewPager равной целевой высоте изображения
                        // для текущей одежды
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_HEAD + "_2"] != nil) {
                                pageViewControllerHeadHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_HEAD + "_2"]!)
                            }
                        }
                        
                        // Иначе задаем высоту для текущего элемента ViewPager равной исходной высоте изображения
                        // для текущей одежды
                        if(pageViewControllerHeadHeight == 0) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                                if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_HEAD + "_2"] != nil) {
                                    pageViewControllerHeadHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_HEAD + "_2"]!)
                                }
                            }
                        }
                        
                        // Иначе устанавливаем высоту элемента ViewPager равной высоте экрана
                        if(pageViewControllerHeadHeight == 0) {
                            pageViewControllerHeadHeight = self.view.frame.size.height - 100
                        }
                        
                        // Устанавливаем смещение по высоте для текущего изображения
                        offsetHeight += pageViewControllerHeadHeight
                        
                        self.pageViewControllerHead.view.frame = CGRectMake(0, self.view.frame.size.height - offsetHeight, self.view.frame.size.width, pageViewControllerHeadHeight)
                        self.pageViewControllerHead.view.backgroundColor = UIColor.clearColor()
                        
                        self.addChildViewController(self.pageViewControllerHead)
                        self.viewMainWindowContent.addSubview(self.pageViewControllerHead.view)
                        self.pageViewControllerHead.didMoveToParentViewController(self)
                        
                        break
                        
                    case GlobalFlags.TAG_DRESS_BODY:                    // верх
                        self.pageViewControllerBody.dataSource = self
                        self.pageViewControllerBody.delegate = self
                        
                        let initialPageViewControllerDressroomBody: ViewControllerDressroomBodyContent = self.pageViewAtIndexBody(currentPosition)
                        
                        let viewControllersDressroomBody: NSArray = NSArray(object: initialPageViewControllerDressroomBody)
                        
                        self.pageViewControllerBody.setViewControllers(viewControllersDressroomBody as! [ViewControllerDressroomBodyContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
                        // Задаем высоту для текущего элемента ViewPager
                        var pageViewControllerBodyHeight: CGFloat = 0
                        
                        // Задаем высоту для текущего элемента ViewPager равной целевой высоте изображения
                        // для текущей одежды
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_BODY + "_2"] != nil) {
                                pageViewControllerBodyHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_BODY + "_2"]!)
                            }
                        }
                        
                        // Иначе задаем высоту для текущего элемента ViewPager равной исходной высоте изображения
                        // для текущей одежды
                        if(pageViewControllerBodyHeight == 0) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                                if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_BODY + "_2"] != nil) {
                                    pageViewControllerBodyHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_BODY + "_2"]!)
                                }
                            }
                        }
                        
                        // Иначе устанавливаем высоту элемента ViewPager равной высоте экрана
                        if(pageViewControllerBodyHeight == 0) {
                            pageViewControllerBodyHeight = self.view.frame.size.height - 100
                        }
                        
                        // Устанавливаем смещение по высоте для текущего изображения
                        offsetHeight += pageViewControllerBodyHeight
                        
                        // Если одежда из типа "Низ" была отображена на виртуальном манекене
                        // то убираем из смещения для текущей одежды 20dp
                        if(isDressTypeLegExists == true) {
                            offsetHeight -= CGFloat(GlobalFlags.OFFSET_BETWEEN_DRESS_BODY_AND_LEG)
                        }
                        
                        self.pageViewControllerBody.view.frame = CGRectMake(0, self.view.frame.size.height - offsetHeight, self.view.frame.size.width, pageViewControllerBodyHeight)
                        self.pageViewControllerBody.view.backgroundColor = UIColor.clearColor()
                        
                        self.addChildViewController(self.pageViewControllerBody)
                        self.viewMainWindowContent.addSubview(self.pageViewControllerBody.view)
                        self.pageViewControllerBody.didMoveToParentViewController(self)
                        
                        break
                        
                    case GlobalFlags.TAG_DRESS_LEG:                    // низ
                        isDressTypeLegExists = true
                        
                        self.pageViewControllerLeg.dataSource = self
                        self.pageViewControllerLeg.delegate = self
                        
                        let initialPageViewControllerDressroomLeg: ViewControllerDressroomLegContent = self.pageViewAtIndexLeg(currentPosition)
                        
                        let viewControllersDressroomLeg: NSArray = NSArray(object: initialPageViewControllerDressroomLeg)
                        
                        self.pageViewControllerLeg.setViewControllers(viewControllersDressroomLeg as! [ViewControllerDressroomLegContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
                        // Задаем высоту для текущего элемента ViewPager
                        var pageViewControllerLegHeight: CGFloat = 0
                        
                        // Задаем высоту для текущего элемента ViewPager равной целевой высоте изображения
                        // для текущей одежды
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_LEG + "_2"] != nil) {
                                pageViewControllerLegHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_LEG + "_2"]!)
                            }
                        }
                        
                        // Иначе задаем высоту для текущего элемента ViewPager равной исходной высоте изображения
                        // для текущей одежды
                        if(pageViewControllerLegHeight == 0) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                                if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_LEG + "_2"] != nil) {
                                    pageViewControllerLegHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_LEG + "_2"]!)
                                }
                            }
                        }
                        
                        // Иначе устанавливаем высоту элемента ViewPager равной высоте экрана
                        if(pageViewControllerLegHeight == 0) {
                            pageViewControllerLegHeight = self.view.frame.size.height - 100
                        }
                        
                        // Устанавливаем смещение по высоте для текущего изображения
                        offsetHeight += pageViewControllerLegHeight
                        
                        self.pageViewControllerLeg.view.frame = CGRectMake(0, self.view.frame.size.height - offsetHeight, self.view.frame.size.width, pageViewControllerLegHeight)
                        self.pageViewControllerLeg.view.backgroundColor = UIColor.clearColor()
                        
                        self.addChildViewController(self.pageViewControllerLeg)
                        self.viewMainWindowContent.addSubview(self.pageViewControllerLeg.view)
                        self.pageViewControllerLeg.didMoveToParentViewController(self)
                        
                        break
                        
                    case GlobalFlags.TAG_DRESS_FOOT:                    // обувь
                        self.pageViewControllerFoot.dataSource = self
                        self.pageViewControllerFoot.delegate = self
                        
                        let initialPageViewControllerDressroomFoot: ViewControllerDressroomFootContent = self.pageViewAtIndexFoot(currentPosition)
                        
                        let viewControllersDressroomFoot: NSArray = NSArray(object: initialPageViewControllerDressroomFoot)
                        
                        self.pageViewControllerFoot.setViewControllers(viewControllersDressroomFoot as! [ViewControllerDressroomFootContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
                        // Задаем высоту для текущего элемента ViewPager
                        var pageViewControllerFootHeight: CGFloat = 0
                        
                        // Задаем высоту для текущего элемента ViewPager равной целевой высоте изображения
                        // для текущей одежды
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_FOOT + "_2"] != nil) {
                                pageViewControllerFootHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_FOOT + "_2"]!)
                            }
                        }
                        
                        // Иначе задаем высоту для текущего элемента ViewPager равной исходной высоте изображения
                        // для текущей одежды
                        if(pageViewControllerFootHeight == 0) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                                if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_FOOT + "_2"] != nil) {
                                    pageViewControllerFootHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_FOOT + "_2"]!)
                                }
                            }
                        }
                        
                        // Иначе устанавливаем высоту элемента ViewPager равной высоте экрана
                        if(pageViewControllerFootHeight == 0) {
                            pageViewControllerFootHeight = self.view.frame.size.height - 100
                        }
                        
                        // Устанавливаем смещение по высоте для текущего изображения
                        offsetHeight += pageViewControllerFootHeight
                        
                        self.pageViewControllerFoot.view.frame = CGRectMake(0, self.view.frame.size.height - offsetHeight, self.view.frame.size.width, pageViewControllerFootHeight)
                        self.pageViewControllerFoot.view.backgroundColor = UIColor.clearColor()
                        
                        self.addChildViewController(self.pageViewControllerFoot)
                        self.viewMainWindowContent.addSubview(self.pageViewControllerFoot.view)
                        self.pageViewControllerFoot.didMoveToParentViewController(self)
                        
                        break
                        
                    case GlobalFlags.TAG_DRESS_ACCESSORY:                   // аксессуары
                        self.pageViewControllerAccessory.dataSource = self
                        self.pageViewControllerAccessory.delegate = self
                        
                        let initialPageViewControllerDressroomAccessory: ViewControllerDressroomAccessoryContent = self.pageViewAtIndexAccessory(currentPosition)
                        
                        let viewControllersDressroomAccessory: NSArray = NSArray(object: initialPageViewControllerDressroomAccessory)
                        
                        self.pageViewControllerAccessory.setViewControllers(viewControllersDressroomAccessory as! [ViewControllerDressroomAccessoryContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
                        // Задаем высоту для текущего элемента ViewPager
                        var pageViewControllerAccessoryHeight: CGFloat = 0
                        
                        // Задаем высоту для текущего элемента ViewPager равной целевой высоте изображения
                        // для текущей одежды
                        if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho()) != nil) {
                            if(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_ACCESSORY + "_2"] != nil) {
                                pageViewControllerAccessoryHeight = CGFloat(DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_ACCESSORY + "_2"]!)
                            }
                        }
                        
                        // Иначе задаем высоту для текущего элемента ViewPager равной исходной высоте изображения
                        // для текущей одежды
                        if(pageViewControllerAccessoryHeight == 0) {
                            if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho()) != nil) {
                                if(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_ACCESSORY + "_2"] != nil) {
                                    pageViewControllerAccessoryHeight = CGFloat(DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho())!["y_" + GlobalFlags.TAG_DRESS_ACCESSORY + "_2"]!)
                                }
                            }
                        }
                        
                        // Иначе устанавливаем высоту элемента ViewPager равной высоте экрана
                        if(pageViewControllerAccessoryHeight == 0) {
                            pageViewControllerAccessoryHeight = self.view.frame.size.height - 100
                        }
                        
                        // Устанавливаем смещение по высоте для текущего изображения
                        offsetHeight += pageViewControllerAccessoryHeight
                        
                        self.pageViewControllerAccessory.view.frame = CGRectMake(0, self.view.frame.size.height - offsetHeight, self.view.frame.size.width, pageViewControllerAccessoryHeight)
                        self.pageViewControllerAccessory.view.backgroundColor = UIColor.clearColor()
                        
                        self.addChildViewController(self.pageViewControllerAccessory)
                        self.viewMainWindowContent.addSubview(self.pageViewControllerAccessory.view)
                        self.pageViewControllerAccessory.didMoveToParentViewController(self)
                        
                        break
                        
                    default:
                        break
                    }
                }
            }
        }
    }
    
    //====================================================================
    // Метод для скрытия всех элементов ViewPager
    internal func hideViewPagerDressroom() {
        // Скрываем ViewPager для головных уборов
        if(self.pageViewControllerHead != nil) {
            self.pageViewControllerHead.willMoveToParentViewController(nil)
            self.pageViewControllerHead.view.removeFromSuperview()
            self.pageViewControllerHead.removeFromParentViewController()
        }
        
        // Скрываем ViewPager для одежды, одеваемой на тело
        if(self.pageViewControllerBody != nil) {
            self.pageViewControllerBody.willMoveToParentViewController(nil)
            self.pageViewControllerBody.view.removeFromSuperview()
            self.pageViewControllerBody.removeFromParentViewController()
        }
        
        // Скрываем ViewPager для одежды, одеваемой на ноги
        if(self.pageViewControllerLeg != nil) {
            self.pageViewControllerLeg.willMoveToParentViewController(nil)
            self.pageViewControllerLeg.view.removeFromSuperview()
            self.pageViewControllerLeg.removeFromParentViewController()
        }
        
        // Скрываем ViewPager для обуви
        if(self.pageViewControllerFoot != nil) {
            self.pageViewControllerFoot.willMoveToParentViewController(nil)
            self.pageViewControllerFoot.view.removeFromSuperview()
            self.pageViewControllerFoot.removeFromParentViewController()
        }
        
        // Скрываем ViewPager для аксессуаров
        if(self.pageViewControllerAccessory != nil) {
            self.pageViewControllerAccessory.willMoveToParentViewController(nil)
            self.pageViewControllerAccessory.view.removeFromSuperview()
            self.pageViewControllerAccessory.removeFromParentViewController()
        }
    }
    
    //====================================================================
    // Метод для перересовки всех элементов ViewPager
    internal func reloadViewPagerDressroom() {
        self.hideViewPagerDressroom()
        self.showViewPagerDressroom()
    }
    
    //====================================================================
    // Метод для отображения ViewPager для коллекций одежды
    private func showViewPagerDressCollection() {
        // Инициализируем соответствующий элемент ViewPager
        self.pageViewControllerDressCollection = self.storyboard?.instantiateViewControllerWithIdentifier("PageViewControllerDressCollection") as! UIPageViewController
        
        // Текущая позицию для соответствующего элемента ViewPager
        // равной номеру позиции одежды, отображаемой в первую очередь для соответствующего адаптера
        var currentPosition: Int = 0
        
        if(DBMain.getPagerAdapterDressCollection() != nil) {
            currentPosition = DBMain.getPagerAdapterDressCollection()!.getCurrentItemPosition()
        }
        
        self.pageViewControllerDressCollection.dataSource = self
        self.pageViewControllerDressCollection.delegate = self
                        
        let initialPageViewControllerDressCollection: ViewControllerDressCollectionContent = self.pageViewAtIndexCollection(currentPosition)
                        
        let viewControllersDressCollection: NSArray = NSArray(object: initialPageViewControllerDressCollection)
                        
        self.pageViewControllerDressCollection.setViewControllers(viewControllersDressCollection as! [ViewControllerDressCollectionContent], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
                        
        // Задаем высоту для текущего элемента ViewPager равной высоте экрана
        let pageViewControllerDressCollectionHeight: CGFloat = self.view.frame.size.height - 50
        
        self.pageViewControllerDressCollection.view.frame = CGRectMake(0, 0, self.view.frame.size.width, pageViewControllerDressCollectionHeight)
        self.pageViewControllerDressCollection.view.backgroundColor = UIColor.clearColor()
                        
        self.addChildViewController(self.pageViewControllerDressCollection)
        self.viewMainWindowContent.addSubview(self.pageViewControllerDressCollection.view)
        self.pageViewControllerDressCollection.didMoveToParentViewController(self)
        
        //---------------------------------------------------------------
        // Отображаем стрелки листания ВЛЕВО и ВПРАВО избранных наборов одежды
        self.viewMainWindowContent.bringSubviewToFront(self.imageViewArrowLeft)
        self.viewMainWindowContent.bringSubviewToFront(self.imageViewArrowRight)
        
        self.imageViewArrowLeft.hidden = false
        self.imageViewArrowRight.hidden = false
    }
    
    //====================================================================
    // Метод для скрытия ViewPager для коллекций одежды
    private func hideViewPagerDressCollection() {
        if(self.pageViewControllerDressCollection != nil) {
            self.pageViewControllerDressCollection.willMoveToParentViewController(nil)
            self.pageViewControllerDressCollection.view.removeFromSuperview()
            self.pageViewControllerDressCollection.removeFromParentViewController()
        }
    }
    
    //====================================================================
    // Метод для перезагрузки ViewPager для коллекций одежды
    private func reloadViewPagerDressCollection() {
        self.hideViewPagerDressCollection()
        self.showViewPagerDressCollection()
    }
    
    //=====================================================================
    // Обработчик клика по View затемнения
    func clickViewShadow() {
        // Если отображено главное меню приложения, то закрываем его
        if(self.viewMainMenu.hidden == false) {
            self.hideViewMainMenu()
        }
        
        // Если отображена панель добавления одежды, то закрываем ее
        if(self.viewDressAdd.hidden == false) {
            self.closeViewDressAdd()
        }
    }

    //==============================================================================================
    // Метод для отображения/скрытия элемента LinearLayout, являющегося контейнером для главного меню приложения
    private func showHideViewMainMenu() {
        // Отображаем или скрываем главное меню приложения
    
        // Если главное меню приложения отображено, то скрываем его
        if(self.viewMainMenu.hidden == false) {
            self.hideViewMainMenu()
        }
        // Иначе, отображаем главное меню приложения
        else {
            self.showViewMainMenu()
        }
    }


    //===================================================================================
    // Метод для отображения элемента LinearLayout, являющегося контейнером для главного меню приложения
    private func showViewMainMenu() {
        // Закрываем нижнюю панель для добавления новой одежды
        self.closeViewDressAdd()
    
        //-------------------------------------------------------------------------------
        // Затемняем
        self.view.bringSubviewToFront(self.viewShadow)
        self.viewShadow.backgroundColor = UIColor.clearColor()
        self.viewShadow.hidden = false
    
        //-------------------------------------------------------------------------------
        // Отображаем непосредственно главное меню приложения
        self.view.bringSubviewToFront(self.viewMainMenu)
        self.viewMainMenu.alpha = 0.0
        self.viewMainMenu.hidden = false
        
        UIView.animateWithDuration(0.3, animations: {
            self.viewMainMenu.alpha = 1.0
        })
    }


    //==================================================================================
    // Метод для скрытия элемента LinearLayout, являющегося контейнером для главного меню приложения
    private func hideViewMainMenu() {
        UIView.animateWithDuration(0.3, animations: {
            self.viewMainMenu.alpha = 0.0
        })
        
        // Убираем затемнение
        self.viewShadow.hidden = true
        self.viewShadow.backgroundColor = UIColor(hexString: GlobalFlagsColors.colorShadow)
        
        //------------------------------------------------------------------------------
        // Закрываем непосредственно главное меню приложения
        self.viewMainMenu.hidden = true
        self.viewMainMenu.alpha = 1.0
    }

    //====================================================================
    // Метод для очистки главного окна приложения
    private func clearViewMainWindow() {
        self.hideViewPagerDressroom()
        self.hideViewPagerDressCollection()
        self.hideViewMainMenu()
        self.closeViewDressAdd()
        
        self.viewShadow.hidden = true
        self.viewDialogShare.hidden = true
        self.viewDialogShowDressInfo.hidden = true
        self.imageViewArrowLeft.hidden = true
        self.imageViewArrowRight.hidden = true
        
        if(ViewControllerMain.presentWindow != nil) {
            // диалоговое окно отображения выполнения длительной операции
            let viewDialogWait: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_DIALOG_WAIT)
            
            if(viewDialogWait != nil) {
                viewDialogWait!.removeFromSuperview()
            }
            
            //------------------------------------------------------------
            let viewMessage: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_MESSAGE)
            
            if(viewMessage != nil) {
                viewMessage!.removeFromSuperview()
            }
            
            //------------------------------------------------------------
            let viewMessageNoDress: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_MESSAGE_NO_DRESS)
            
            if(viewMessageNoDress != nil) {
                viewMessageNoDress!.removeFromSuperview()
            }
            
            //------------------------------------------------------------
            let viewMessageNoCollection: UIView? = ViewControllerMain.presentWindow!.viewWithTag(GlobalFlags.TAG_MESSAGE_NO_COLLECTION)
            
            if(viewMessageNoCollection != nil) {
                viewMessageNoCollection!.removeFromSuperview()
            }
        }
    }
    
    //====================================================================
    // Метод для создания вкладок для нижней выдвигающейся панели для выбора категории
    // добавляемой на виртуальный манекен одежды
    internal func createTabsForLinearLayoutDressAdd() {
        // Считываем многомерный массив, хранящий список категорий
        let listCategoriesDress: Dictionary<String, [Dictionary<String, String>]>? = DBMain.getListCategoriesDress(GlobalFlags.getDressForWho())
        
        if(listCategoriesDress != nil) {
            if (listCategoriesDress!.count > 0) {
                // Инициализируем контроллеры для отображения их в виде вкладок
                var viewControllerArray : [UIViewController] = []
                
                // В цикле перебираем все возможные группы категорий одежды
                for indexTagDressType in 0..<GlobalFlags.getArrayTagDressType().count {
                    // Считываем текущий тег, определяющий типы одежды (головные уборы, обувь и т.д.)
                    let currentDressType: String = GlobalFlags.getArrayTagDressType()[indexTagDressType]
                    
                    // Если в глобальном массиве, содержащем сведения о категориях одежды,
                    // присутствуют сведения о категориях для текущей группы одежды
                    if(listCategoriesDress![currentDressType] != nil) {
                        let viewControllerDressAdd: ViewControllerDressAdd = self.storyboard?.instantiateViewControllerWithIdentifier("ViewControllerDressAdd") as! ViewControllerDressAdd
                        
                        // Задаем массив, содержащий информацию о категориях для текущего
                        // типа одежды для текущего создаваемого контроллера
                        viewControllerDressAdd.setDressCategoryList(listCategoriesDress![currentDressType]!)
                        
                        // Задаем заголовок для текущего контроллера
                        switch (currentDressType) {
                            case GlobalFlags.TAG_DRESS_HEAD:
                                viewControllerDressAdd.title = GlobalFlagsStrings.stringDressCategoriesGroupHead
                                break
                            case GlobalFlags.TAG_DRESS_BODY:
                                viewControllerDressAdd.title = GlobalFlagsStrings.stringDressCategoriesGroupBody
                                break
                            case GlobalFlags.TAG_DRESS_LEG:
                                viewControllerDressAdd.title = GlobalFlagsStrings.stringDressCategoriesGroupLeg
                                break
                            case GlobalFlags.TAG_DRESS_FOOT:
                                viewControllerDressAdd.title = GlobalFlagsStrings.stringDressCategoriesGroupFoot
                                break
                            case GlobalFlags.TAG_DRESS_ACCESSORY:
                                viewControllerDressAdd.title = GlobalFlagsStrings.stringDressCategoriesGroupAccessories
                                break
                            default:
                                break
                        }
                        
                        // Добавляем текущий создаваемый контроллер в общий массив
                        viewControllerArray.append(viewControllerDressAdd)
                    }
                }
                
                // Customize menu (Optional)
                let parameters: [CAPSPageMenuOption] = [
                    .ScrollMenuBackgroundColor(UIColor(hexString: GlobalFlagsColors.colorTabBackground)!),
                    .ViewBackgroundColor(UIColor(hexString: GlobalFlagsColors.colorDrawerBackground)!),
                    .SelectionIndicatorColor(UIColor(hexString: GlobalFlagsColors.colorElementClickable)!),
                    .BottomMenuHairlineColor(UIColor(hexString: GlobalFlagsColors.colorTabBackground)!),
                    .MenuItemFont(UIFont(name: "Arial", size: 15.0)!),
                    .MenuHeight(40.0),
                    .MenuItemWidth(90.0),
                    .CenterMenuItems(false)
                ]
        
                // Initialize scroll menu
                self.setPageMenuDressAdd(
                    CAPSPageMenu(
                        viewControllers: viewControllerArray,
                        frame: CGRectMake(0.0, 0.0, self.viewDressAdd.frame.width, self.viewDressAdd.frame.height),
                        pageMenuOptions: parameters
                    )
                )
        
                self.addChildViewController(self.getPageMenuDressAdd()!)
                self.viewDressAdd.addSubview(self.getPageMenuDressAdd()!.view)
        
                self.getPageMenuDressAdd()!.didMoveToParentViewController(self)
            }
        }
    }
    
    //==============================================================================================
    // Метод для закрытия элемента LinearLayout, являющегося контейнером для всплывающего меню выбора
    internal func closeViewDressAdd() {
        // Меняем фон кнопки добавления одежды
        self.mButtonDressAddOrShare.tintColor = UIColor(hexString: GlobalFlagsColors.colorElementClickable)
        
        // Убираем подчеркивание для кнопки добавления одежды
        self.viewBorderButtonDressAdd.hidden = true
        
        // Убираем затемнение
        self.viewShadow.hidden = true
        
        //----------------------------------------------------------------------
        // Закрываем непосредственно необходимый элемент View
        UIView.animateWithDuration(
            0.3,
            animations: {
                self.mViewDressAddConstraintBottom.constant = (-1) * self.viewDressAdd.frame.height
                self.view.layoutIfNeeded()
            },
            completion: { (completed: Bool) -> Void in
                self.viewDressAdd.hidden = true
            }
        )
    }
    
    //=====================================================================
    // Метод для изменения заголовка одежды в зависимости от типа просматриваемой одежды
    internal func setDressroomTitleText() {
        switch (GlobalFlags.getDressForWho()) {
            case GlobalFlags.DRESS_MAN:
                self.title = GlobalFlagsStringsBar.barItemDressMan
                break
            case GlobalFlags.DRESS_WOMAN:
                self.title = GlobalFlagsStringsBar.barItemDressWoman
                break
            case GlobalFlags.DRESS_KID:
                self.title = GlobalFlagsStringsBar.barItemDressKid
                break
            default:
                break
        }
    }
    
    //=======================================================================
    // Метод для изменения заголовка одежды
    // Передаваемые параметры
    // titleText - заголовок для текущего окна
    internal func setDressroomTitleText(titleText: String) {
        self.title = titleText
    }
}
