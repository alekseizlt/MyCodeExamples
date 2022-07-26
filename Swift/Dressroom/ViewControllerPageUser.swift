import UIKit

// Класс для страницы пользователя
class ViewControllerPageUser: UIViewController {

    @IBOutlet weak var pageUserImageViewUserAvatar: UIImageView!    // аватар пользователя
    @IBOutlet weak var pageUserLabelUserName: UILabel!              // полное имя пользователя
    @IBOutlet weak var pageUserLabelUserEmail: UILabel!             // адрес электронной почты пользователя
    @IBOutlet weak var pageUserViewEditPassword: UIView!            // ссылка для редактирования пароля пользователя
    @IBOutlet weak var pageUserViewEditUserDetails: UIView!         // ссылка для редактирования данных о текущем пользователе
    @IBOutlet weak var pageUserViewLogout: UIView!                  // ссылка для выхода пользователя из своей учетной записи
    @IBOutlet weak var pageUserViewCollections: UIView!             // кнопка для перехода к просмотру избранных наборов одежды
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewCollectionsHead: UIView!
    @IBOutlet weak var imageViewArrowLeftCollectionsHead: UIImageView!
    
    @IBOutlet weak var viewLeftCollectionsHead: UIView!
    @IBOutlet weak var imageViewLeftCollectionsHead: UIImageView!
    @IBOutlet weak var labelLeftCollectionsHead: UILabel!
    
    @IBOutlet weak var viewCenterCollectionsHead: UIView!
    @IBOutlet weak var imageViewCenterCollectionsHead: UIImageView!
    @IBOutlet weak var labelCenterCollectionsHead: UILabel!
    
    @IBOutlet weak var viewRightCollectionsHead: UIView!
    @IBOutlet weak var imageViewRightCollectionsHead: UIImageView!
    @IBOutlet weak var labelRightCollectionsHead: UILabel!
    
    @IBOutlet weak var imageViewArrowRightCollectionsHead: UIImageView!
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewCollectionsBody: UIView!
    @IBOutlet weak var imageViewArrowLeftCollectionsBody: UIImageView!
    
    @IBOutlet weak var viewLeftCollectionsBody: UIView!
    @IBOutlet weak var imageViewLeftCollectionsBody: UIImageView!
    @IBOutlet weak var labelLeftCollectionsBody: UILabel!
    
    @IBOutlet weak var viewCenterCollectionsBody: UIView!
    @IBOutlet weak var imageViewCenterCollectionsBody: UIImageView!
    @IBOutlet weak var labelCenterCollectionsBody: UILabel!
    
    @IBOutlet weak var viewRightCollectionsBody: UIView!
    @IBOutlet weak var imageViewRightCollectionsBody: UIImageView!
    @IBOutlet weak var labelRightCollectionsBody: UILabel!
    
    @IBOutlet weak var imageViewArrowRightCollectionsBody: UIImageView!
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewCollectionsLeg: UIView!
    @IBOutlet weak var imageViewArrowLeftCollectionsLeg: UIImageView!
    
    @IBOutlet weak var viewLeftCollectionsLeg: UIView!
    @IBOutlet weak var imageViewLeftCollectionsLeg: UIImageView!
    @IBOutlet weak var labelLeftCollectionsLeg: UILabel!
    
    @IBOutlet weak var viewCenterCollectionsLeg: UIView!
    @IBOutlet weak var imageViewCenterCollectionsLeg: UIImageView!
    @IBOutlet weak var labelCenterCollectionsLeg: UILabel!
    
    @IBOutlet weak var viewRightCollectionsLeg: UIView!
    @IBOutlet weak var imageViewRightCollectionsLeg: UIImageView!
    @IBOutlet weak var labelRightCollectionsLeg: UILabel!
    
    @IBOutlet weak var imageViewArrowRightCollectionsLeg: UIImageView!
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewCollectionsFoot: UIView!
    @IBOutlet weak var imageViewArrowLeftCollectionsFoot: UIImageView!
    
    @IBOutlet weak var viewLeftCollectionsFoot: UIView!
    @IBOutlet weak var imageViewLeftCollectionsFoot: UIImageView!
    @IBOutlet weak var labelLeftCollectionsFoot: UILabel!
    
    @IBOutlet weak var viewCenterCollectionsFoot: UIView!
    @IBOutlet weak var imageViewCenterCollectionsFoot: UIImageView!
    @IBOutlet weak var labelCenterCollectionsFoot: UILabel!
    
    @IBOutlet weak var viewRightCollectionsFoot: UIView!
    @IBOutlet weak var imageViewRightCollectionsFoot: UIImageView!
    @IBOutlet weak var labelRightCollectionsFoot: UILabel!
    
    @IBOutlet weak var imageViewArrowRightCollectionsFoot: UIImageView!
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewCollectionsAccessory: UIView!
    @IBOutlet weak var imageViewArrowLeftCollectionsAccessory: UIImageView!
    
    @IBOutlet weak var viewLeftCollectionsAccessory: UIView!
    @IBOutlet weak var imageViewLeftCollectionsAccessory: UIImageView!
    @IBOutlet weak var labelLeftCollectionsAccessory: UILabel!
    
    @IBOutlet weak var viewCenterCollectionsAccessory: UIView!
    @IBOutlet weak var imageViewCenterCollectionsAccessory: UIImageView!
    @IBOutlet weak var labelCenterCollectionsAccessory: UILabel!
    
    @IBOutlet weak var viewRightCollectionsAccessory: UIView!
    @IBOutlet weak var imageViewRightCollectionsAccessory: UIImageView!
    @IBOutlet weak var labelRightCollectionsAccessory: UILabel!
    
    @IBOutlet weak var imageViewArrowRightCollectionsAccessory: UIImageView!
    
    //------------------------------------------------------------------
    @IBOutlet weak var pageUserViewContentHeight: NSLayoutConstraint!
    @IBOutlet weak var pageUserViewCollectionsBodyConstraintTop: NSLayoutConstraint!
    @IBOutlet weak var pageUserViewCollectionsLegConstraintTop: NSLayoutConstraint!
    @IBOutlet weak var pageUserViewCollectionsFootConstraintTop: NSLayoutConstraint!
    @IBOutlet weak var pageUserViewCollectionsAccessoryConstraintTop: NSLayoutConstraint!
    
    //------------------------------------------------------------------
    @IBOutlet weak var viewShadow: UIView!
    @IBOutlet weak var viewDialogEditPassword: ViewDialogEditPassword!
    @IBOutlet weak var viewDialogEditUserDetails: ViewDialogEditUserDetails!
    
    // Массивы, хранящие информацию об избранных категориях одежды в зависимости от типа
    // представленной в них одежде
    private var mArrayPagerAdapterDressCollectionCategory: Dictionary<String, PagerAdapterDressCollectionCategory>?
    
    // Высота вместе с верхним смещением для одного элемента ViewPager, 
    // предназначенного для избранных категорий одежды для текущего пользователя
    private static let pageUserViewCollectionsHeight: Int = 145
    
    // Расстояние по вертикали между элементами ViewPager,
    // предназначенными для избранных категорий одежды для текущего пользователя
    private static let pageUserViewCollectionsOffset: Int = 25
    
    //=========================================================================
    // Метод, выполняемый при загрузке текущего окна
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //-----------------------------------------------------
        // Устанавливаем закругленные углы для изображения-аватара
        // для текущего пользователя
//        self.pageUserImageViewUserAvatar.layer.cornerRadius = self.pageUserImageViewUserAvatar.frame.size.width / 2   // для круглого изображения
        self.pageUserImageViewUserAvatar.layer.cornerRadius = 10.0      // для квадратного изображения с закругленными углами
        self.pageUserImageViewUserAvatar.clipsToBounds = true;
        
        // Добавление рамки для изображения-аватара
//        self.pageUserImageViewUserAvatar.layer.borderWidth = 3.0;
//        self.pageUserImageViewUserAvatar.layer.borderColor = UIColor.whiteColor().CGColor
        
        //----------------------------------------------------------------
        // Заполняем данные о текущем пользователе
        self.fillPageUserComponentsFromUserDetails()
        
        //----------------------------------------------------------------
        // Загружаем информацию о категориях сохраненных наборов одежды
        self.fillPageUserCollections()
        
        //----------------------------------------------------------------
        // Устанавливаем обработчик клика по ссылке редактирования пароля
        let pageUserViewEditPasswordTapGestureRecognizer = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickPageUserViewEditPassword))
        self.pageUserViewEditPassword.userInteractionEnabled = true
        self.pageUserViewEditPassword.addGestureRecognizer(pageUserViewEditPasswordTapGestureRecognizer)
        
        //----------------------------------------------------------------
        // Устанавливаем обработчик клика по ссылке редактирования данных о текущем пользователе
        let pageUserViewEditUserDetailsTapGestureRecognizer = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickPageUserViewEditUserDetails))
        self.pageUserViewEditUserDetails.userInteractionEnabled = true
        self.pageUserViewEditUserDetails.addGestureRecognizer(pageUserViewEditUserDetailsTapGestureRecognizer)
        
        //----------------------------------------------------------------
        // Устанавливаем обработчик клика по ссылке Выход из аккаунта
        let pageUserViewLogoutTapGestureRecognizer = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickPageUserViewLogout))
        self.pageUserViewLogout.userInteractionEnabled = true
        self.pageUserViewLogout.addGestureRecognizer(pageUserViewLogoutTapGestureRecognizer)
        
        //----------------------------------------------------------------
        // Устанавливаем обработчик клика по кнопке Мои коллекции
        let pageUserViewCollectionsTapGestureRecognizer = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickPageUserViewCollections))
        self.pageUserViewCollections.userInteractionEnabled = true
        self.pageUserViewCollections.addGestureRecognizer(pageUserViewCollectionsTapGestureRecognizer)
 
        //======================================================================
        // Для головных уборов
        //======================================================================
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки влево
        let tapGestureRecognizerArrowLeftCollectionsHead = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowLeftCollectionsHead))
        self.imageViewArrowLeftCollectionsHead.userInteractionEnabled = true
        self.imageViewArrowLeftCollectionsHead.addGestureRecognizer(tapGestureRecognizerArrowLeftCollectionsHead)
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки вправо
        let tapGestureRecognizerArrowRightCollectionsHead = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowRightCollectionsHead))
        self.imageViewArrowRightCollectionsHead.userInteractionEnabled = true
        self.imageViewArrowRightCollectionsHead.addGestureRecognizer(tapGestureRecognizerArrowRightCollectionsHead)
        
        //---------------------------------------------------------------------
        // Устанавливаем перелиствание вариантов для кого предназначена одежда влево и вправо
        let swipeCollectionsHeadLeft = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsHead(_:)))
        let swipeCollectionsHeadRight = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsHead(_:)))
        
        swipeCollectionsHeadLeft.direction = .Left
        swipeCollectionsHeadRight.direction = .Right
        
        self.pageUserViewCollectionsHead.addGestureRecognizer(swipeCollectionsHeadLeft)
        self.pageUserViewCollectionsHead.addGestureRecognizer(swipeCollectionsHeadRight)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для левого изображения
        let tapGestureRecognizerImageViewLeftCollectionsHead = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewLeftCollectionsHead))
        self.imageViewLeftCollectionsHead.userInteractionEnabled = true
        self.imageViewLeftCollectionsHead.addGestureRecognizer(tapGestureRecognizerImageViewLeftCollectionsHead)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для центрального изображения
        let tapGestureRecognizerImageViewCenterCollectionsHead = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewCenterCollectionsHead))
        self.imageViewCenterCollectionsHead.userInteractionEnabled = true
        self.imageViewCenterCollectionsHead.addGestureRecognizer(tapGestureRecognizerImageViewCenterCollectionsHead)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для правого изображения
        let tapGestureRecognizerImageViewRightCollectionsHead = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewRightCollectionsHead))
        self.imageViewRightCollectionsHead.userInteractionEnabled = true
        self.imageViewRightCollectionsHead.addGestureRecognizer(tapGestureRecognizerImageViewRightCollectionsHead)
        
        //======================================================================
        // Для одежды, одеваемой на тело
        //======================================================================
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки влево
        let tapGestureRecognizerArrowLeftCollectionsBody = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowLeftCollectionsBody))
        self.imageViewArrowLeftCollectionsBody.userInteractionEnabled = true
        self.imageViewArrowLeftCollectionsBody.addGestureRecognizer(tapGestureRecognizerArrowLeftCollectionsBody)
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки вправо
        let tapGestureRecognizerArrowRightCollectionsBody = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowRightCollectionsBody))
        self.imageViewArrowRightCollectionsBody.userInteractionEnabled = true
        self.imageViewArrowRightCollectionsBody.addGestureRecognizer(tapGestureRecognizerArrowRightCollectionsBody)
        
        //---------------------------------------------------------------------
        // Устанавливаем перелиствание вариантов для кого предназначена одежда влево и вправо
        let swipeCollectionsBodyLeft = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsBody(_:)))
        let swipeCollectionsBodyRight = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsBody(_:)))
        
        swipeCollectionsBodyLeft.direction = .Left
        swipeCollectionsBodyRight.direction = .Right
        
        self.pageUserViewCollectionsBody.addGestureRecognizer(swipeCollectionsBodyLeft)
        self.pageUserViewCollectionsBody.addGestureRecognizer(swipeCollectionsBodyRight)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для левого изображения
        let tapGestureRecognizerImageViewLeftCollectionsBody = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewLeftCollectionsBody))
        self.imageViewLeftCollectionsBody.userInteractionEnabled = true
        self.imageViewLeftCollectionsBody.addGestureRecognizer(tapGestureRecognizerImageViewLeftCollectionsBody)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для центрального изображения
        let tapGestureRecognizerImageViewCenterCollectionsBody = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewCenterCollectionsBody))
        self.imageViewCenterCollectionsBody.userInteractionEnabled = true
        self.imageViewCenterCollectionsBody.addGestureRecognizer(tapGestureRecognizerImageViewCenterCollectionsBody)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для правого изображения
        let tapGestureRecognizerImageViewRightCollectionsBody = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewRightCollectionsBody))
        self.imageViewRightCollectionsBody.userInteractionEnabled = true
        self.imageViewRightCollectionsBody.addGestureRecognizer(tapGestureRecognizerImageViewRightCollectionsBody)
        
        //======================================================================
        // Для одежды, одеваемой на ноги
        //======================================================================
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки влево
        let tapGestureRecognizerArrowLeftCollectionsLeg = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowLeftCollectionsLeg))
        self.imageViewArrowLeftCollectionsLeg.userInteractionEnabled = true
        self.imageViewArrowLeftCollectionsLeg.addGestureRecognizer(tapGestureRecognizerArrowLeftCollectionsLeg)
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки вправо
        let tapGestureRecognizerArrowRightCollectionsLeg = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowRightCollectionsLeg))
        self.imageViewArrowRightCollectionsLeg.userInteractionEnabled = true
        self.imageViewArrowRightCollectionsLeg.addGestureRecognizer(tapGestureRecognizerArrowRightCollectionsLeg)
        
        //---------------------------------------------------------------------
        // Устанавливаем перелиствание вариантов для кого предназначена одежда влево и вправо
        let swipeCollectionsLegLeft = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsLeg(_:)))
        let swipeCollectionsLegRight = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsLeg(_:)))
        
        swipeCollectionsLegLeft.direction = .Left
        swipeCollectionsLegRight.direction = .Right
        
        self.pageUserViewCollectionsLeg.addGestureRecognizer(swipeCollectionsLegLeft)
        self.pageUserViewCollectionsLeg.addGestureRecognizer(swipeCollectionsLegRight)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для левого изображения
        let tapGestureRecognizerImageViewLeftCollectionsLeg = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewLeftCollectionsLeg))
        self.imageViewLeftCollectionsLeg.userInteractionEnabled = true
        self.imageViewLeftCollectionsLeg.addGestureRecognizer(tapGestureRecognizerImageViewLeftCollectionsLeg)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для центрального изображения
        let tapGestureRecognizerImageViewCenterCollectionsLeg = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewCenterCollectionsLeg))
        self.imageViewCenterCollectionsLeg.userInteractionEnabled = true
        self.imageViewCenterCollectionsLeg.addGestureRecognizer(tapGestureRecognizerImageViewCenterCollectionsLeg)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для правого изображения
        let tapGestureRecognizerImageViewRightCollectionsLeg = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewRightCollectionsLeg))
        self.imageViewRightCollectionsLeg.userInteractionEnabled = true
        self.imageViewRightCollectionsLeg.addGestureRecognizer(tapGestureRecognizerImageViewRightCollectionsLeg)
        
        //======================================================================
        // Для обуви
        //======================================================================
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки влево
        let tapGestureRecognizerArrowLeftCollectionsFoot = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowLeftCollectionsFoot))
        self.imageViewArrowLeftCollectionsFoot.userInteractionEnabled = true
        self.imageViewArrowLeftCollectionsFoot.addGestureRecognizer(tapGestureRecognizerArrowLeftCollectionsFoot)
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки вправо
        let tapGestureRecognizerArrowRightCollectionsFoot = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowRightCollectionsFoot))
        self.imageViewArrowRightCollectionsFoot.userInteractionEnabled = true
        self.imageViewArrowRightCollectionsFoot.addGestureRecognizer(tapGestureRecognizerArrowRightCollectionsFoot)
        
        //---------------------------------------------------------------------
        // Устанавливаем перелиствание вариантов для кого предназначена одежда влево и вправо
        let swipeCollectionsFootLeft = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsFoot(_:)))
        let swipeCollectionsFootRight = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsFoot(_:)))
        
        swipeCollectionsFootLeft.direction = .Left
        swipeCollectionsFootRight.direction = .Right
        
        self.pageUserViewCollectionsFoot.addGestureRecognizer(swipeCollectionsFootLeft)
        self.pageUserViewCollectionsFoot.addGestureRecognizer(swipeCollectionsFootRight)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для левого изображения
        let tapGestureRecognizerImageViewLeftCollectionsFoot = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewLeftCollectionsFoot))
        self.imageViewLeftCollectionsFoot.userInteractionEnabled = true
        self.imageViewLeftCollectionsFoot.addGestureRecognizer(tapGestureRecognizerImageViewLeftCollectionsFoot)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для центрального изображения
        let tapGestureRecognizerImageViewCenterCollectionsFoot = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewCenterCollectionsFoot))
        self.imageViewCenterCollectionsFoot.userInteractionEnabled = true
        self.imageViewCenterCollectionsFoot.addGestureRecognizer(tapGestureRecognizerImageViewCenterCollectionsFoot)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для правого изображения
        let tapGestureRecognizerImageViewRightCollectionsFoot = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewRightCollectionsFoot))
        self.imageViewRightCollectionsFoot.userInteractionEnabled = true
        self.imageViewRightCollectionsFoot.addGestureRecognizer(tapGestureRecognizerImageViewRightCollectionsFoot)
        
        //======================================================================
        // Для аксессуаров
        //======================================================================
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки влево
        let tapGestureRecognizerArrowLeftCollectionsAccessory = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowLeftCollectionsAccessory))
        self.imageViewArrowLeftCollectionsAccessory.userInteractionEnabled = true
        self.imageViewArrowLeftCollectionsAccessory.addGestureRecognizer(tapGestureRecognizerArrowLeftCollectionsAccessory)
        
        //----------------------------------------------------------------------
        // Устанавливаем обработчик клика по изображению стрелки вправо
        let tapGestureRecognizerArrowRightCollectionsAccessory = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewArrowRightCollectionsAccessory))
        self.imageViewArrowRightCollectionsAccessory.userInteractionEnabled = true
        self.imageViewArrowRightCollectionsAccessory.addGestureRecognizer(tapGestureRecognizerArrowRightCollectionsAccessory)
        
        //---------------------------------------------------------------------
        // Устанавливаем перелиствание вариантов для кого предназначена одежда влево и вправо
        let swipeCollectionsAccessoryLeft = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsAccessory(_:)))
        let swipeCollectionsAccessoryRight = UISwipeGestureRecognizer(target: self, action: #selector(ViewControllerPageUser.swipeCollectionsAccessory(_:)))
        
        swipeCollectionsAccessoryLeft.direction = .Left
        swipeCollectionsAccessoryRight.direction = .Right
        
        self.pageUserViewCollectionsAccessory.addGestureRecognizer(swipeCollectionsAccessoryLeft)
        self.pageUserViewCollectionsAccessory.addGestureRecognizer(swipeCollectionsAccessoryRight)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для левого изображения
        let tapGestureRecognizerImageViewLeftCollectionsAccessory = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewLeftCollectionsAccessory))
        self.imageViewLeftCollectionsAccessory.userInteractionEnabled = true
        self.imageViewLeftCollectionsAccessory.addGestureRecognizer(tapGestureRecognizerImageViewLeftCollectionsAccessory)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для центрального изображения
        let tapGestureRecognizerImageViewCenterCollectionsAccessory = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewCenterCollectionsAccessory))
        self.imageViewCenterCollectionsAccessory.userInteractionEnabled = true
        self.imageViewCenterCollectionsAccessory.addGestureRecognizer(tapGestureRecognizerImageViewCenterCollectionsAccessory)
        
        //---------------------------------------------------------------------
        // Устанавливаем обработчик клика для правого изображения
        let tapGestureRecognizerImageViewRightCollectionsAccessory = UITapGestureRecognizer(target: self, action:#selector(ViewControllerPageUser.clickImageViewRightCollectionsAccessory))
        self.imageViewRightCollectionsAccessory.userInteractionEnabled = true
        self.imageViewRightCollectionsAccessory.addGestureRecognizer(tapGestureRecognizerImageViewRightCollectionsAccessory)
    }
    
    //===============================================================================
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        
        // Устанавливаем надпись
        self.title = GlobalFlagsStrings.titleActivityPageUser
    }
    
    //=========================================================================
    // Метод, выполняемый при возникновении ошибки
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    //=========================================================================
    // Метод для считывания массива, хранящего информацию об избранных категориях одежды 
    private func getArrayPagerAdapterDressCollectionCategory() -> Dictionary<String, PagerAdapterDressCollectionCategory>? {
        return self.mArrayPagerAdapterDressCollectionCategory
    }
    
    //=========================================================================
    // Метод для задания массива, хранящего информацию об избранных категориях одежды
    private func setArrayPagerAdapterDressCollectionCategory(arrayPagerAdapterDressCollectionCategory: Dictionary<String, PagerAdapterDressCollectionCategory>) {
        self.mArrayPagerAdapterDressCollectionCategory = arrayPagerAdapterDressCollectionCategory
    }
    
    //==========================================================================
    // Метод для заполнения информацией о текущем пользователе необходимых элементов на странице пользователя
    internal func fillPageUserComponentsFromUserDetails() {
        // Полное имя пользователя (фамилия и имя)
        var userSurname: String = ""
    
        if(UserDetails.getUserSurname() != nil) {
            userSurname = UserDetails.getUserSurname()!.trim()
    
            if(userSurname.lowercaseString == "null" || userSurname.lowercaseString == "nil") {
                userSurname = ""
            }
        }
    
        var userName: String = ""
    
        if(UserDetails.getUserName() != nil) {
            userName = UserDetails.getUserName()!.trim()
    
            if(userName.lowercaseString == "null" || userName.lowercaseString == "nil") {
                userName = ""
            }
        }
    
        let fullUserName: String = userSurname + " " + userName
    
        //------------------------------------------------------------------------------------------
        // Логин текущего пользователя
        var userLogin: String = ""
    
        if(UserDetails.getUserLogin() != nil) {
            userLogin = UserDetails.getUserLogin()!.trim()
    
            if(userLogin.lowercaseString == "null" || userLogin.lowercaseString == "nil") {
                userLogin = ""
            }
        }
    
        //------------------------------------------------------------------------------------------
        // Адрес электронной почты пользователя
        var userMail: String = ""
    
        if (UserDetails.getUserMail() != nil) {
            userMail = UserDetails.getUserMail()!.trim()
    
            if(userMail.lowercaseString == "null" || userMail.lowercaseString == "nil") {
                userMail = ""
            }
        }
    
        //------------------------------------------------------------------------------------------
        // Ссылка на изображение-аватар для текущего пользователя
        var userAvatarURL: String = ""
    
        if(UserDetails.getUserAvatarURL() != nil) {
            userAvatarURL = UserDetails.getUserAvatarURL()!.trim()
    
            if(userAvatarURL.lowercaseString == "null" || userAvatarURL.lowercaseString == "nil") {
                userAvatarURL = ""
            }
        }
    
        //------------------------------------------------------------------------------------------
        // Текстовые поля для отображения полного имени пользователя на странице пользователя
        // Если фамилия и имя пользователя НЕ пустые
        if (userSurname != "" || userName != "") {
            self.pageUserLabelUserName.text = fullUserName
        }
        // Иначе, если фамилия и имя пользователя пустые
        else {
            self.pageUserLabelUserName.text = userLogin
        }
    
        //------------------------------------------------------------------------------------------
        // Текстовые поля для отображения адреса электронной почты пользователя на странице пользователя
        self.pageUserLabelUserEmail.text = userMail
    
        //------------------------------------------------------------------------------------------
        // Изображение-аватар для текущего пользователя на странице пользователя
        // Загружаем изображение для текущей одежды
        if(userAvatarURL != "") {
            self.pageUserImageViewUserAvatar.kf_setImageWithURL(NSURL(string: userAvatarURL)!,
                        placeholderImage: UIImage(named: "noavatar"),
                        optionsInfo: nil,
                        progressBlock: { (receivedSize, totalSize) -> () in
                                                        
                        },
                        completionHandler: { (image, error, cacheType, imageURL) -> () in
                            // Если возникла ошибка в процессе загрузки изображения
                            if(error != nil) {
                                self.pageUserImageViewUserAvatar.image = UIImage(named: "noavatar")
                            }
                        }
            )
        }
    }
    
    //==============================================================================================
    // Метод для очистки информации о текущем пользователе необходимых элементов
    internal func clearComponentsFromUserDetails() {
        // Текстовые поля для отображения полного имени пользователя на странице пользователя
        self.pageUserLabelUserName.text = GlobalFlagsStrings.stringAutorizationRegistration
    
        //------------------------------------------------------------------------------------------
        // Адрес электронной почты пользователя
    
        // Текстовые поля для отображения адреса электронной почты пользователя на странице пользователя
        self.pageUserLabelUserEmail.text = ""
    
        //------------------------------------------------------------------------------------------
        // Изображение-аватар для текущего пользователя
    
        // Изображение-аватар для текущего пользователя на странице пользователя
        self.pageUserImageViewUserAvatar.image = UIImage(named: "noavatar")
    }
    
    //==========================================================================================
    // Метод для загрузки информации о категориях сохраненных наборов одежды
    private func fillPageUserCollections() {
        // Добавляем избранные категории одежды для текущего пользователя
        
        // Массивы, хранящие информацию об избранных категориях одежды в зависимости от типа
        // представленной в них одежде
        var arrayCategoryInfoHead: [Dictionary<String, String>]?            // для головных уборов
        var arrayCategoryInfoBody: [Dictionary<String, String>]?            // для одежды, одеваемой на тело
        var arrayCategoryInfoLeg: [Dictionary<String, String>]?             // для одежды, одеваемой на ноги
        var arrayCategoryInfoFoot: [Dictionary<String, String>]?            // для обуви
        var arrayCategoryInfoAccessory: [Dictionary<String, String>]?       // для аксессуаров
        
        if(UserDetails.getUserCountCollections() > 0) {
            let arrayDressInUserCollection: [Dictionary<String, String>]? = UserDetails.getArrayDressInUserCollections()
            
            if(arrayDressInUserCollection != nil) {
                for indexCategory in 0..<arrayDressInUserCollection!.count {
                    if(arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_TYPE] != nil) {
                        var mapCurrentCategory: Dictionary<String, String> = Dictionary<String, String>()
                    
                        mapCurrentCategory[GlobalFlags.TAG_IMAGE]       = nil
                        mapCurrentCategory[GlobalFlags.TAG_CATID]       = arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_CATID]
                        mapCurrentCategory[GlobalFlags.TAG_TITLE]       = arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_TITLE]
                        mapCurrentCategory[GlobalFlags.TAG_ALIAS]       = arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_ALIAS]
                        mapCurrentCategory[GlobalFlags.TAG_TYPE]        = arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_TYPE]
                        mapCurrentCategory[GlobalFlags.TAG_DRESS_COUNT] = arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_DRESS_COUNT]
                        mapCurrentCategory[GlobalFlags.TAG_POSITION]    = String(indexCategory + 1)
                        
                        //--------------------------------------------------------------------------
                        // В зависимости от типа одежды для текущей категории, добавляем информацию
                        // о текущей категории в соответствующий массив
                        switch (arrayDressInUserCollection![indexCategory][GlobalFlags.TAG_TYPE]!) {
                            case GlobalFlags.TAG_DRESS_HEAD:                                        // головные уборы
                                if(arrayCategoryInfoHead == nil) {
                                    arrayCategoryInfoHead = [Dictionary<String, String>]()
                                }
                            
                                arrayCategoryInfoHead!.append(mapCurrentCategory)
                            
                                break
                            
                            case GlobalFlags.TAG_DRESS_BODY:                                        // для одежды, одеваемой на тело
                                if(arrayCategoryInfoBody == nil) {
                                    arrayCategoryInfoBody = [Dictionary<String, String>]()
                                }
                            
                                arrayCategoryInfoBody!.append(mapCurrentCategory)
                            
                                break
                            
                            case GlobalFlags.TAG_DRESS_LEG:                                         // для одежды, одеваемой на ноги
                                if(arrayCategoryInfoLeg == nil) {
                                    arrayCategoryInfoLeg = [Dictionary<String, String>]()
                                }
                            
                                arrayCategoryInfoLeg!.append(mapCurrentCategory)
                            
                                break
                            
                            case GlobalFlags.TAG_DRESS_FOOT:                                        // для обуви
                                if(arrayCategoryInfoFoot == nil) {
                                    arrayCategoryInfoFoot = [Dictionary<String, String>]()
                                }
                            
                                arrayCategoryInfoFoot!.append(mapCurrentCategory)
                            
                                break
                            
                            case GlobalFlags.TAG_DRESS_ACCESSORY:                                   // для акссесуаров
                                if(arrayCategoryInfoAccessory == nil) {
                                    arrayCategoryInfoAccessory = [Dictionary<String, String>]()
                                }
                            
                                arrayCategoryInfoAccessory!.append(mapCurrentCategory)
                            
                                break
                            
                            default:
                                break
                        }
                    }
                }
            }
        }
 
        //------------------------------------------------------------------------------------------
        // Отступы для элементов ViewPager для избранных категорий одежды для текущего пользователя
        var pageUserViewCollectionsBodyConstraintTop: Int = ViewControllerPageUser.pageUserViewCollectionsOffset
        var pageUserViewCollectionsLegConstraintTop: Int = ViewControllerPageUser.pageUserViewCollectionsOffset
        var pageUserViewCollectionsFootConstraintTop: Int = ViewControllerPageUser.pageUserViewCollectionsOffset
        var pageUserViewCollectionsAccessoryConstraintTop: Int = ViewControllerPageUser.pageUserViewCollectionsOffset
        
        //------------------------------------------------------------------------------------------
        // Инициализируем элементы ViewPager для избранных категорий одежды для текущего пользователя
        for indexDressType in 0..<GlobalFlags.getArrayTagDressType().count {
            var arrayCategoryInfoCurrentType: [Dictionary<String, String>]?
            var pageUserViewCollectionsCurrentType: UIView?
            var pageUserImageViewArrowLeftCollectionsCurrentType: UIImageView?
            var pageUserImageViewArrowRightCollectionsCurrentType: UIImageView?

            switch (GlobalFlags.getArrayTagDressType()[indexDressType]) {
                case GlobalFlags.TAG_DRESS_HEAD:                            // головные уборы
                    arrayCategoryInfoCurrentType = arrayCategoryInfoHead
                    pageUserViewCollectionsCurrentType = self.pageUserViewCollectionsHead
                    pageUserImageViewArrowLeftCollectionsCurrentType = self.imageViewArrowLeftCollectionsHead
                    pageUserImageViewArrowRightCollectionsCurrentType = self.imageViewArrowRightCollectionsHead
                    break
                case GlobalFlags.TAG_DRESS_BODY:                            // одежда, одеваемая на тело
                    arrayCategoryInfoCurrentType = arrayCategoryInfoBody
                    pageUserViewCollectionsCurrentType = self.pageUserViewCollectionsBody
                    pageUserImageViewArrowLeftCollectionsCurrentType = self.imageViewArrowLeftCollectionsBody
                    pageUserImageViewArrowRightCollectionsCurrentType = self.imageViewArrowRightCollectionsBody
                    break
                case GlobalFlags.TAG_DRESS_LEG:                             // одежда, одеваемая на ноги
                    arrayCategoryInfoCurrentType = arrayCategoryInfoLeg
                    pageUserViewCollectionsCurrentType = self.pageUserViewCollectionsLeg
                    pageUserImageViewArrowLeftCollectionsCurrentType = self.imageViewArrowLeftCollectionsLeg
                    pageUserImageViewArrowRightCollectionsCurrentType = self.imageViewArrowRightCollectionsLeg
                    break
                case GlobalFlags.TAG_DRESS_FOOT:                            // обувь
                    arrayCategoryInfoCurrentType = arrayCategoryInfoFoot
                    pageUserViewCollectionsCurrentType = self.pageUserViewCollectionsFoot
                    pageUserImageViewArrowLeftCollectionsCurrentType = self.imageViewArrowLeftCollectionsFoot
                    pageUserImageViewArrowRightCollectionsCurrentType = self.imageViewArrowRightCollectionsFoot
                    break
                case GlobalFlags.TAG_DRESS_ACCESSORY:                       // акссесуары
                    arrayCategoryInfoCurrentType = arrayCategoryInfoAccessory
                    pageUserViewCollectionsCurrentType = self.pageUserViewCollectionsAccessory
                    pageUserImageViewArrowLeftCollectionsCurrentType = self.imageViewArrowLeftCollectionsAccessory
                    pageUserImageViewArrowRightCollectionsCurrentType = self.imageViewArrowRightCollectionsAccessory
                    break
                default:
                    break
            }
            
            if(arrayCategoryInfoCurrentType != nil) {
                // Инициализируем адаптер для соответствующего элемента ViewPager
                if(self.getArrayPagerAdapterDressCollectionCategory() == nil) {
                    self.setArrayPagerAdapterDressCollectionCategory(Dictionary<String, PagerAdapterDressCollectionCategory>())
                }
                
                self.mArrayPagerAdapterDressCollectionCategory![GlobalFlags.getArrayTagDressType()[indexDressType]] = PagerAdapterDressCollectionCategory(arrayParams: arrayCategoryInfoCurrentType!)
                    
                // Настраиваем врешний вид для соответствующего элемента View
                self.setImageViewDressCategory(GlobalFlags.getArrayTagDressType()[indexDressType])
                    
                // Устанавливаем видимость для соответствующего элемента View
                if(pageUserViewCollectionsCurrentType != nil) {
                    pageUserViewCollectionsCurrentType!.hidden = false
                }
   
                // Инициализируем кнопку листания влево
                if(pageUserImageViewArrowLeftCollectionsCurrentType != nil) {
                    // Если количество вещей для текущего типа больше 3, то инициализируем кнопку листания влево
                    if(arrayCategoryInfoCurrentType!.count > 3) {
                        pageUserImageViewArrowLeftCollectionsCurrentType!.hidden = false
                    }
                    // Иначе, делаем невидимым кнопку листания влево
                    else {
                        pageUserImageViewArrowLeftCollectionsCurrentType!.hidden = true
                    }
                }
                    
                // Инициализируем кнопку листания вправо
                if(pageUserImageViewArrowRightCollectionsCurrentType != nil) {
                    // Если количество вещей для текущего типа больше 3, то инициализируем кнопку листания вправо
                    if(arrayCategoryInfoCurrentType!.count > 3) {
                        pageUserImageViewArrowRightCollectionsCurrentType!.hidden = false
                    }
                        // Иначе, делаем невидимым кнопку листания вправо
                    else {
                        pageUserImageViewArrowRightCollectionsCurrentType!.hidden = true
                    }
                }
                
                //-----------------------------------------------------------------------------
                // Устанавливаем смещение сверху для соответствующего элемента ViewPager
                switch (GlobalFlags.getArrayTagDressType()[indexDressType]) {
                    case GlobalFlags.TAG_DRESS_HEAD:                            // головные уборы
                        pageUserViewCollectionsBodyConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsLegConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsFootConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsAccessoryConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        break
                    case GlobalFlags.TAG_DRESS_BODY:                            // одежда, одеваемая на тело
                        pageUserViewCollectionsLegConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsFootConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsAccessoryConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        break
                    case GlobalFlags.TAG_DRESS_LEG:                             // одежда, одеваемая на ноги
                        pageUserViewCollectionsFootConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        pageUserViewCollectionsAccessoryConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        break
                    case GlobalFlags.TAG_DRESS_FOOT:                            // обувь
                        pageUserViewCollectionsAccessoryConstraintTop += ViewControllerPageUser.pageUserViewCollectionsHeight
                        break
                    case GlobalFlags.TAG_DRESS_ACCESSORY:                       // акссесуары
                        break
                    default:
                        break
                }
            }
        }
        
        //--------------------------------------------------------------------------------------
        // Устанавливаем отступы для элементов ViewPager для избранных категорий одежды для текущего пользователя
        self.pageUserViewCollectionsBodyConstraintTop.constant = CGFloat(pageUserViewCollectionsBodyConstraintTop)
        self.pageUserViewCollectionsLegConstraintTop.constant = CGFloat(pageUserViewCollectionsLegConstraintTop)
        self.pageUserViewCollectionsFootConstraintTop.constant = CGFloat(pageUserViewCollectionsFootConstraintTop)
        self.pageUserViewCollectionsAccessoryConstraintTop.constant = CGFloat(pageUserViewCollectionsAccessoryConstraintTop)
        
        //--------------------------------------------------------------------------------------
        // Устанавливаем общую высоту пролистываемого контента
        var pageUserViewContent: CGFloat = 0
        
        if(self.pageUserViewCollectionsAccessory.hidden == false) {
            pageUserViewContent = self.pageUserViewCollectionsAccessory.frame.origin.y
        }
        else if(self.pageUserViewCollectionsFoot.hidden == false) {
            pageUserViewContent = self.pageUserViewCollectionsFoot.frame.origin.y
        }
        else if(self.pageUserViewCollectionsLeg.hidden == false) {
            pageUserViewContent = self.pageUserViewCollectionsLeg.frame.origin.y
        }
        else if(self.pageUserViewCollectionsBody.hidden == false) {
            pageUserViewContent = self.pageUserViewCollectionsBody.frame.origin.y
        }
        else if(self.pageUserViewCollectionsHead.hidden == false) {
            pageUserViewContent = self.pageUserViewCollectionsHead.frame.origin.y
        }
        
        self.pageUserViewContentHeight.constant = pageUserViewContent
    }
    
    //==========================================================================================
    // Метод - обработчик клика по ссылке редактирования пароля
    func clickPageUserViewEditPassword() {
        self.view.bringSubviewToFront(self.viewShadow)
        self.view.bringSubviewToFront(self.viewDialogEditPassword)
        
        self.viewShadow.hidden = false
        self.viewDialogEditPassword.hidden = false
    }
    
    //==========================================================================================
    // Метод - обработчик клика по ссылке редактирования данных о текущем пользователе
    func clickPageUserViewEditUserDetails() {
        DialogEditUserDetails.setContext(self)
        DialogMain.createDialog(GlobalFlags.DIALOG_EDIT_USER_DETAILS, dialogParams: nil, message: nil)
    }
    
    //==========================================================================================
    // Метод - обработчик клика по ссылке Выход из аккаунта
    func clickPageUserViewLogout() {
        // Очищаем все данные о текущем пользователе
        UserDetails.logout()
        self.clearComponentsFromUserDetails()
        
        //------------------------------------------------------------------------------
        // Загружаем страницу онлайн-примерочной с мужской одеждой
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    //==========================================================================================
    // Метод - обработчик клика по кнопке Мои коллекции
    func clickPageUserViewCollections() {
        // Устанавливаем, что тип просматриваемого содержимого в главном окне приложения
        // Коллекции для текущего пользователя
        ViewControllerMain.setMainActivityViewType(GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION)
        
        if(DBMain.getMySQLDressCollectionLoad() == nil) {
            DBMain.setMySQLDressCollectionLoad(MySQLDressCollectionLoad())
        }
        
        DBMain.getMySQLDressCollectionLoad()!.setParams(
            nil,
            dressCategoryTitle: GlobalFlagsStringsBar.barItemDressCollection,
            dressCategoryRowIndex: 0
        )
      
        //------------------------------------------------------------------------------
        // Закрываем текущую страницу пользователя и автоматически загружается
        // страница с сохраненными коллекциями для текущего пользователя
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    //========================================================================
    // Метод для установки соответствующих изображений для избранных категорий одежды
    // Передаваемые параметры
    // dressType - тип одежды
    private func setImageViewDressCategory(dressType: String) {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![dressType] != nil) {
                let pagerAdapterDressCollectionCategory: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![dressType]!
                
                //-----------------------------------------------------------
                // Для левого изображения

                // Считываем параметры для левого изображения
                var dressCategoryLeftId: Int = 0            // id текущей категории
                var dressCategoryLeftTitle: String = ""     // название текущей категории
                var dressCategoryLeftAlias: String = ""     // алиас названия текущей категории
                var dressCategoryLeftPosition: Int = 0      // порядковый номер текущей категории
                
                let arrayParamsDressCategoryLeft: Dictionary<String, String>? = pagerAdapterDressCollectionCategory.getItemParams(
                    pagerAdapterDressCollectionCategory.getCurrentPosition()
                )
                
                if(arrayParamsDressCategoryLeft != nil) {
                    // id текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_CATID] != nil) {
                        if(Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_CATID]!) != nil) {
                            dressCategoryLeftId = Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_CATID]!)!
                        }
                    }
                    
                    // название текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_TITLE] != nil) {
                        dressCategoryLeftTitle = arrayParamsDressCategoryLeft![GlobalFlags.TAG_TITLE]!
                    }
                    
                    // алиас названия текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_ALIAS] != nil) {
                        dressCategoryLeftAlias = arrayParamsDressCategoryLeft![GlobalFlags.TAG_ALIAS]!
                    }
                    
                    // порядковый номер текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION] != nil) {
                        if(Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!) != nil) {
                            dressCategoryLeftPosition = Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!)!
                        }
                    }
                }
                
                // Устанавливаем текст и изображение для левой категории
                switch(dressType) {
                    case GlobalFlags.TAG_DRESS_HEAD:
                        self.viewLeftCollectionsHead.tag = dressCategoryLeftId
                        self.labelLeftCollectionsHead.text = dressCategoryLeftTitle
                        
                        switch (dressCategoryLeftAlias) {
                            default:
                                self.imageViewLeftCollectionsHead.image = nil
                                break
                        }
                        
                        self.imageViewLeftCollectionsHead.tag = dressCategoryLeftPosition
                        
                        break
                    
                    case GlobalFlags.TAG_DRESS_BODY:
                        self.viewLeftCollectionsBody.tag = dressCategoryLeftId
                        self.labelLeftCollectionsBody.text = dressCategoryLeftTitle
                        
                        switch (dressCategoryLeftAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_FUTBOLKI:
                                self.imageViewLeftCollectionsBody.image = UIImage(named: "category_dress_man_body_futbolki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KOSTYUMI:
                                self.imageViewLeftCollectionsBody.image = UIImage(named: "category_dress_man_body_kostyumi")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KURTKI:
                                self.imageViewLeftCollectionsBody.image = UIImage(named: "category_dress_man_body_kurtki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_RUBASHKI:
                                self.imageViewLeftCollectionsBody.image = UIImage(named: "category_dress_man_body_rubashki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_TOLSTOVKI:
                                self.imageViewLeftCollectionsBody.image = UIImage(named: "category_dress_man_body_tolstovki")
                                break
                            default:
                                self.imageViewLeftCollectionsBody.image = nil
                                break
                        }
                        
                        self.imageViewLeftCollectionsBody.tag = dressCategoryLeftPosition
                        
                        break
                    
                    case GlobalFlags.TAG_DRESS_LEG:
                        self.viewLeftCollectionsLeg.tag = dressCategoryLeftId
                        self.labelLeftCollectionsLeg.text = dressCategoryLeftTitle
                        
                        switch (dressCategoryLeftAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_BRYUKI:
                                self.imageViewLeftCollectionsLeg.image = UIImage(named: "category_dress_man_leg_bryuki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_DZINCI:
                                self.imageViewLeftCollectionsLeg.image = UIImage(named: "category_dress_man_leg_dzinci")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_SHORTI:
                                self.imageViewLeftCollectionsLeg.image = UIImage(named: "category_dress_man_leg_shorti")
                                break
                            default:
                                self.imageViewLeftCollectionsLeg.image = nil
                                break
                        }
                        
                        self.imageViewLeftCollectionsLeg.tag = dressCategoryLeftPosition
                        
                        break
                        
                    case GlobalFlags.TAG_DRESS_FOOT:
                        self.viewLeftCollectionsFoot.tag = dressCategoryLeftId
                        self.labelLeftCollectionsFoot.text = dressCategoryLeftTitle
                        
                        switch (dressCategoryLeftAlias) {
                            default:
                                self.imageViewLeftCollectionsFoot.image = nil
                                break
                        }
                        
                        self.imageViewLeftCollectionsFoot.tag = dressCategoryLeftPosition
                        
                        break
                    
                    case GlobalFlags.TAG_DRESS_ACCESSORY:
                        self.viewLeftCollectionsAccessory.tag = dressCategoryLeftId
                        self.labelLeftCollectionsAccessory.text = dressCategoryLeftTitle
                        
                        switch (dressCategoryLeftAlias) {
                            default:
                                self.imageViewLeftCollectionsAccessory.image = nil
                                break
                        }
                        
                        self.imageViewLeftCollectionsAccessory.tag = dressCategoryLeftPosition
                        
                        break
                    default:
                        break
                }
                
                //-----------------------------------------------------------
                // Для центрального изображения
                
                // Считываем параметры для центрального изображения
                var dressCategoryCenterId: Int = 0            // id текущей категории
                var dressCategoryCenterTitle: String = ""     // название текущей категории
                var dressCategoryCenterAlias: String = ""     // алиас названия текущей категории
                var dressCategoryCenterPosition: Int = 0      // порядковый номер текущей категории
                
                let arrayParamsDressCategoryCenter: Dictionary<String, String>? = pagerAdapterDressCollectionCategory.getItemParams(
                    pagerAdapterDressCollectionCategory.getCurrentPosition() + 1
                )
                
                if(arrayParamsDressCategoryCenter != nil) {
                    // id текущей категории
                    if(arrayParamsDressCategoryCenter![GlobalFlags.TAG_CATID] != nil) {
                        if(Int(arrayParamsDressCategoryCenter![GlobalFlags.TAG_CATID]!) != nil) {
                            dressCategoryCenterId = Int(arrayParamsDressCategoryCenter![GlobalFlags.TAG_CATID]!)!
                        }
                    }
                    
                    // название текущей категории
                    if(arrayParamsDressCategoryCenter![GlobalFlags.TAG_TITLE] != nil) {
                        dressCategoryCenterTitle = arrayParamsDressCategoryCenter![GlobalFlags.TAG_TITLE]!
                    }
                    
                    // алиас названия текущей категории
                    if(arrayParamsDressCategoryCenter![GlobalFlags.TAG_ALIAS] != nil) {
                        dressCategoryCenterAlias = arrayParamsDressCategoryCenter![GlobalFlags.TAG_ALIAS]!
                    }
                    
                    // порядковый номер текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION] != nil) {
                        if(Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!) != nil) {
                            dressCategoryCenterPosition = Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!)!
                        }
                    }
                }
                
                // Устанавливаем текст и изображение для центральной категории
                switch(dressType) {
                    case GlobalFlags.TAG_DRESS_HEAD:
                        self.viewCenterCollectionsHead.tag = dressCategoryCenterId
                        self.labelCenterCollectionsHead.text = dressCategoryCenterTitle
                    
                        switch (dressCategoryCenterAlias) {
                            default:
                                self.imageViewCenterCollectionsHead.image = nil
                            break
                        }
                        
                        self.imageViewCenterCollectionsHead.tag = dressCategoryCenterPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_BODY:
                        self.viewCenterCollectionsBody.tag = dressCategoryCenterId
                        self.labelCenterCollectionsBody.text = dressCategoryCenterTitle
                    
                        switch (dressCategoryCenterAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_FUTBOLKI:
                                self.imageViewCenterCollectionsBody.image = UIImage(named: "category_dress_man_body_futbolki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KOSTYUMI:
                                self.imageViewCenterCollectionsBody.image = UIImage(named: "category_dress_man_body_kostyumi")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KURTKI:
                                self.imageViewCenterCollectionsBody.image = UIImage(named: "category_dress_man_body_kurtki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_RUBASHKI:
                                self.imageViewCenterCollectionsBody.image = UIImage(named: "category_dress_man_body_rubashki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_TOLSTOVKI:
                                self.imageViewCenterCollectionsBody.image = UIImage(named: "category_dress_man_body_tolstovki")
                                break
                            default:
                                self.imageViewCenterCollectionsBody.image = nil
                            break
                        }
                        
                        self.imageViewCenterCollectionsBody.tag = dressCategoryCenterPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_LEG:
                        self.viewCenterCollectionsLeg.tag = dressCategoryCenterId
                        self.labelCenterCollectionsLeg.text = dressCategoryCenterTitle
                    
                        switch (dressCategoryCenterAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_BRYUKI:
                                self.imageViewCenterCollectionsLeg.image = UIImage(named: "category_dress_man_leg_bryuki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_DZINCI:
                                self.imageViewCenterCollectionsLeg.image = UIImage(named: "category_dress_man_leg_dzinci")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_SHORTI:
                                self.imageViewCenterCollectionsLeg.image = UIImage(named: "category_dress_man_leg_shorti")
                                break
                            default:
                                self.imageViewCenterCollectionsLeg.image = nil
                                break
                        }
                        
                        self.imageViewCenterCollectionsLeg.tag = dressCategoryCenterPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_FOOT:
                        self.viewCenterCollectionsFoot.tag = dressCategoryCenterId
                        self.labelCenterCollectionsFoot.text = dressCategoryCenterTitle
                    
                        switch (dressCategoryCenterAlias) {
                            default:
                                self.imageViewCenterCollectionsFoot.image = nil
                                break
                        }
                        
                        self.imageViewCenterCollectionsFoot.tag = dressCategoryCenterPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_ACCESSORY:
                        self.viewCenterCollectionsAccessory.tag = dressCategoryCenterId
                        self.labelCenterCollectionsAccessory.text = dressCategoryCenterTitle
                    
                        switch (dressCategoryCenterAlias) {
                            default:
                                self.imageViewCenterCollectionsAccessory.image = nil
                                break
                        }
                        
                        self.imageViewCenterCollectionsAccessory.tag = dressCategoryCenterPosition
                    
                        break
                    
                    default:
                        break
                }
                
                //-----------------------------------------------------------
                // Для правого изображения
                
                // Считываем параметры для правого изображения
                var dressCategoryRightId: Int = 0            // id текущей категории
                var dressCategoryRightTitle: String = ""     // название текущей категории
                var dressCategoryRightAlias: String = ""     // алиас названия текущей категории
                var dressCategoryRightPosition: Int = 0      // порядковый номер текущей категории
                
                let arrayParamsDressCategoryRight: Dictionary<String, String>? = pagerAdapterDressCollectionCategory.getItemParams(
                    pagerAdapterDressCollectionCategory.getCurrentPosition() + 2
                )
                
                if(arrayParamsDressCategoryRight != nil) {
                    // id текущей категории
                    if(arrayParamsDressCategoryRight![GlobalFlags.TAG_CATID] != nil) {
                        if(Int(arrayParamsDressCategoryRight![GlobalFlags.TAG_CATID]!) != nil) {
                            dressCategoryRightId = Int(arrayParamsDressCategoryRight![GlobalFlags.TAG_CATID]!)!
                        }
                    }
                    
                    // название текущей категории
                    if(arrayParamsDressCategoryRight![GlobalFlags.TAG_TITLE] != nil) {
                        dressCategoryRightTitle = arrayParamsDressCategoryRight![GlobalFlags.TAG_TITLE]!
                    }
                    
                    // алиас названия текущей категории
                    if(arrayParamsDressCategoryRight![GlobalFlags.TAG_ALIAS] != nil) {
                        dressCategoryRightAlias = arrayParamsDressCategoryRight![GlobalFlags.TAG_ALIAS]!
                    }
                    
                    // порядковый номер текущей категории
                    if(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION] != nil) {
                        if(Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!) != nil) {
                            dressCategoryRightPosition = Int(arrayParamsDressCategoryLeft![GlobalFlags.TAG_POSITION]!)!
                        }
                    }
                }
                
                // Устанавливаем текст и изображение для центральной категории
                switch(dressType) {
                    case GlobalFlags.TAG_DRESS_HEAD:
                        self.viewRightCollectionsHead.tag = dressCategoryRightId
                        self.labelRightCollectionsHead.text = dressCategoryRightTitle
                    
                        switch (dressCategoryRightAlias) {
                            default:
                                self.imageViewRightCollectionsHead.image = nil
                                break
                        }
                        
                        self.imageViewRightCollectionsHead.tag = dressCategoryRightPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_BODY:
                        self.viewRightCollectionsBody.tag = dressCategoryRightId
                        self.labelRightCollectionsBody.text = dressCategoryRightTitle
                    
                        switch (dressCategoryRightAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_FUTBOLKI:
                                self.imageViewRightCollectionsBody.image = UIImage(named: "category_dress_man_body_futbolki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KOSTYUMI:
                                self.imageViewRightCollectionsBody.image = UIImage(named: "category_dress_man_body_kostyumi")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_KURTKI:
                                self.imageViewRightCollectionsBody.image = UIImage(named: "category_dress_man_body_kurtki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_RUBASHKI:
                                self.imageViewRightCollectionsBody.image = UIImage(named: "category_dress_man_body_rubashki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_BODY_TOLSTOVKI:
                                self.imageViewRightCollectionsBody.image = UIImage(named: "category_dress_man_body_tolstovki")
                                break
                            default:
                                self.imageViewRightCollectionsBody.image = nil
                                break
                        }
                        
                        self.imageViewRightCollectionsBody.tag = dressCategoryRightPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_LEG:
                        self.viewRightCollectionsLeg.tag = dressCategoryRightId
                        self.labelRightCollectionsLeg.text = dressCategoryRightTitle
                    
                        switch (dressCategoryRightAlias) {
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_BRYUKI:
                                self.imageViewRightCollectionsLeg.image = UIImage(named: "category_dress_man_leg_bryuki")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_DZINCI:
                                self.imageViewRightCollectionsLeg.image = UIImage(named: "category_dress_man_leg_dzinci")
                                break
                            case GlobalFlags.TAG_CATEGORY_DRESS_ALIAS_MAN_LEG_SHORTI:
                                self.imageViewRightCollectionsLeg.image = UIImage(named: "category_dress_man_leg_shorti")
                                break
                            default:
                                self.imageViewRightCollectionsLeg.image = nil
                                break
                        }
                        
                        self.imageViewRightCollectionsLeg.tag = dressCategoryRightPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_FOOT:
                        self.viewRightCollectionsFoot.tag = dressCategoryRightId
                        self.labelRightCollectionsFoot.text = dressCategoryRightTitle
                    
                        switch (dressCategoryRightAlias) {
                            default:
                                self.imageViewRightCollectionsFoot.image = nil
                            break
                        }
                        
                        self.imageViewRightCollectionsFoot.tag = dressCategoryRightPosition
                    
                        break
                    
                    case GlobalFlags.TAG_DRESS_ACCESSORY:
                        self.viewRightCollectionsAccessory.tag = dressCategoryRightId
                        self.labelRightCollectionsAccessory.text = dressCategoryRightTitle
                    
                        switch (dressCategoryRightAlias) {
                            default:
                                self.imageViewRightCollectionsAccessory.image = nil
                                break
                        }
                        
                        self.imageViewRightCollectionsAccessory.tag = dressCategoryRightPosition
                    
                        break
                    
                    default:
                        break
                }
            }
        }
    }

    //========================================================================
    // Обработчик клика для изображения категории
    // Передаваемые параметры
    // currentCategoryId - id текущей категории одежды
    // currentCategoryTitle - название текущей категории одежды
    // currentCategoryPosition - порядковый номер в левом боковом меню для текущей категории одежды
    func clickImageViewCollections(currentCategoryIdParam: String, currentCategoryTitleParam: String, currentCategoryPosition: Int) {
        // Устанавливаем, что тип просматриваемого содержимого в главном окне приложения
        // Коллекции для текущего пользователя
        ViewControllerMain.setMainActivityViewType(GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION)
        
        if (DBMain.getMySQLDressCollectionLoad() == nil) {
            DBMain.setMySQLDressCollectionLoad(MySQLDressCollectionLoad())
        }
        
        //----------------------------------------------------------------------
        // Формируем название текущей категории
        let currentCategoryTitle: String = GlobalFlagsStrings.stringMy + " " + currentCategoryTitleParam.lowercaseString
        
        DBMain.getMySQLDressCollectionLoad()!.setParams(
            currentCategoryIdParam,
            dressCategoryTitle: currentCategoryTitle,
            dressCategoryRowIndex: currentCategoryPosition
        )

        //----------------------------------------------------------------------
        // Закрываем текущую страницу пользователя и автоматически загружается
        // страница с сохраненными коллекциями для текущего пользователя
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    //========================================================================
    // Для головных уборов
    //========================================================================
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки влево
    func clickImageViewArrowLeftCollectionsHead() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_HEAD] != nil) {
                let pagerAdapterDressCollectionCategoryHead: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_HEAD]!
                
                // Уменьшаем текущую позицию для головных уборов на -1
                if(pagerAdapterDressCollectionCategoryHead.getCurrentPosition() > 0) {
                    pagerAdapterDressCollectionCategoryHead.setCurrentPosition(pagerAdapterDressCollectionCategoryHead.getCurrentPosition() - 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_HEAD)
            }
        }
    }
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки вправо
    func clickImageViewArrowRightCollectionsHead() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_HEAD] != nil) {
                let pagerAdapterDressCollectionCategoryHead: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_HEAD]!
                
                // Увеличиваем текущую позицию для головных уборов на +1
                if(pagerAdapterDressCollectionCategoryHead.getCurrentPosition() < pagerAdapterDressCollectionCategoryHead.getCount() - 3) {
                    pagerAdapterDressCollectionCategoryHead.setCurrentPosition(pagerAdapterDressCollectionCategoryHead.getCurrentPosition() + 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_HEAD)
            }
        }
    }
    
    //==========================================================================
    // Функция - обработчик пеерлистывания для кого предназначена одеждв влево и вправо
    func swipeCollectionsHead(sender: UISwipeGestureRecognizer) {
        if (sender.direction == .Left) {
            self.clickImageViewArrowRightCollectionsHead()
        }
        
        if (sender.direction == .Right) {
            self.clickImageViewArrowLeftCollectionsHead()
        }
    }
    
    //========================================================================
    // Обработчик клика для левого изображения категории для головных уборов
    func clickImageViewLeftCollectionsHead() {
        // Считываем id текущей категории
        if(self.viewLeftCollectionsHead.tag > 0 && self.labelLeftCollectionsHead.text != nil) {
            let currentCategoryId: String = String(self.viewLeftCollectionsHead.tag)
            let currentCategoryTitle: String = self.labelLeftCollectionsHead.text!
            let currentCategoryPosition: Int = self.imageViewLeftCollectionsHead.tag
        
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для центрального изображения категории для головных уборов
    func clickImageViewCenterCollectionsHead() {
        // Считываем id текущей категории
        if(self.viewCenterCollectionsHead.tag > 0 && self.labelCenterCollectionsHead.text != nil) {
            let currentCategoryId: String = String(self.viewCenterCollectionsHead.tag)
            let currentCategoryTitle: String = self.labelCenterCollectionsHead.text!
            let currentCategoryPosition: Int = self.imageViewCenterCollectionsHead.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для правого изображения категории для головных уборов
    func clickImageViewRightCollectionsHead() {
        // Считываем id текущей категории
        if(self.viewRightCollectionsHead.tag > 0 && self.labelRightCollectionsHead.text != nil) {
            let currentCategoryId: String = String(self.viewRightCollectionsHead.tag)
            let currentCategoryTitle: String = self.labelRightCollectionsHead.text!
            let currentCategoryPosition: Int = self.imageViewRightCollectionsHead.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Для одежды, одеваемой на тело
    //========================================================================
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки влево
    func clickImageViewArrowLeftCollectionsBody() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_BODY] != nil) {
                let pagerAdapterDressCollectionCategoryBody: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_BODY]!
                
                // Уменьшаем текущую позицию для головных уборов на -1
                if(pagerAdapterDressCollectionCategoryBody.getCurrentPosition() > 0) {
                    pagerAdapterDressCollectionCategoryBody.setCurrentPosition(pagerAdapterDressCollectionCategoryBody.getCurrentPosition() - 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_BODY)
            }
        }
    }
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки вправо
    func clickImageViewArrowRightCollectionsBody() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_BODY] != nil) {
                let pagerAdapterDressCollectionCategoryBody: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_BODY]!
                
                // Увеличиваем текущую позицию для головных уборов на +1
                if(pagerAdapterDressCollectionCategoryBody.getCurrentPosition() < pagerAdapterDressCollectionCategoryBody.getCount() - 3) {
                    pagerAdapterDressCollectionCategoryBody.setCurrentPosition(pagerAdapterDressCollectionCategoryBody.getCurrentPosition() + 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_BODY)
            }
        }
    }
    
    //==========================================================================
    // Функция - обработчик пеерлистывания для кого предназначена одеждв влево и вправо
    func swipeCollectionsBody(sender: UISwipeGestureRecognizer) {
        if (sender.direction == .Left) {
            self.clickImageViewArrowRightCollectionsBody()
        }
        
        if (sender.direction == .Right) {
            self.clickImageViewArrowLeftCollectionsBody()
        }
    }
    
    //========================================================================
    // Обработчик клика для левого изображения категории для одежды, одеваемой на тело
    func clickImageViewLeftCollectionsBody() {
        // Считываем id текущей категории
        if(self.viewLeftCollectionsBody.tag > 0 && self.labelLeftCollectionsBody.text != nil) {
            let currentCategoryId: String = String(self.viewLeftCollectionsBody.tag)
            let currentCategoryTitle: String = self.labelLeftCollectionsBody.text!
            let currentCategoryPosition: Int = self.imageViewLeftCollectionsBody.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для центрального изображения категории для одежды, одеваемой на тело
    func clickImageViewCenterCollectionsBody() {
        // Считываем id текущей категории
        if(self.viewCenterCollectionsBody.tag > 0 && self.labelCenterCollectionsBody.text != nil) {
            let currentCategoryId: String = String(self.viewCenterCollectionsBody.tag)
            let currentCategoryTitle: String = self.labelCenterCollectionsBody.text!
            let currentCategoryPosition: Int = self.imageViewCenterCollectionsBody.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для правого изображения категории для одежды, одеваемой на тело
    func clickImageViewRightCollectionsBody() {
        // Считываем id текущей категории
        if(self.viewRightCollectionsBody.tag > 0 && self.labelRightCollectionsBody.text != nil) {
            let currentCategoryId: String = String(self.viewRightCollectionsBody.tag)
            let currentCategoryTitle: String = self.labelRightCollectionsBody.text!
            let currentCategoryPosition: Int = self.imageViewRightCollectionsBody.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Для одежды, одеваемой на ноги
    //========================================================================
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки влево
    func clickImageViewArrowLeftCollectionsLeg() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_LEG] != nil) {
                let pagerAdapterDressCollectionCategoryLeg: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_LEG]!
                
                // Уменьшаем текущую позицию для головных уборов на -1
                if(pagerAdapterDressCollectionCategoryLeg.getCurrentPosition() > 0) {
                    pagerAdapterDressCollectionCategoryLeg.setCurrentPosition(pagerAdapterDressCollectionCategoryLeg.getCurrentPosition() - 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_LEG)
            }
        }
    }
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки вправо
    func clickImageViewArrowRightCollectionsLeg() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_LEG] != nil) {
                let pagerAdapterDressCollectionCategoryLeg: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_LEG]!
                
                // Увеличиваем текущую позицию для головных уборов на +1
                if(pagerAdapterDressCollectionCategoryLeg.getCurrentPosition() < pagerAdapterDressCollectionCategoryLeg.getCount() - 3) {
                    pagerAdapterDressCollectionCategoryLeg.setCurrentPosition(pagerAdapterDressCollectionCategoryLeg.getCurrentPosition() + 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_LEG)
            }
        }
    }
    
    //==========================================================================
    // Функция - обработчик пеерлистывания для кого предназначена одеждв влево и вправо
    func swipeCollectionsLeg(sender: UISwipeGestureRecognizer) {
        if (sender.direction == .Left) {
            self.clickImageViewArrowRightCollectionsLeg()
        }
        
        if (sender.direction == .Right) {
            self.clickImageViewArrowLeftCollectionsLeg()
        }
    }
    
    //========================================================================
    // Обработчик клика для левого изображения категории для одежды, одеваемой на ноги
    func clickImageViewLeftCollectionsLeg() {
        // Считываем id текущей категории
        if(self.viewLeftCollectionsLeg.tag > 0 && self.labelLeftCollectionsLeg.text != nil) {
            let currentCategoryId: String = String(self.viewLeftCollectionsLeg.tag)
            let currentCategoryTitle: String = self.labelLeftCollectionsLeg.text!
            let currentCategoryPosition: Int = self.imageViewLeftCollectionsLeg.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для центрального изображения категории для одежды, одеваемой на ноги
    func clickImageViewCenterCollectionsLeg() {
        // Считываем id текущей категории
        if(self.viewCenterCollectionsLeg.tag > 0 && self.labelCenterCollectionsLeg.text != nil) {
            let currentCategoryId: String = String(self.viewCenterCollectionsLeg.tag)
            let currentCategoryTitle: String = self.labelCenterCollectionsLeg.text!
            let currentCategoryPosition: Int = self.imageViewCenterCollectionsLeg.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для правого изображения категории для одежды, одеваемой на ноги
    func clickImageViewRightCollectionsLeg() {
        // Считываем id текущей категории
        if(self.viewRightCollectionsLeg.tag > 0 && self.labelRightCollectionsLeg.text != nil) {
            let currentCategoryId: String = String(self.viewRightCollectionsLeg.tag)
            let currentCategoryTitle: String = self.labelRightCollectionsLeg.text!
            let currentCategoryPosition: Int = self.imageViewRightCollectionsLeg.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Для обуви
    //========================================================================
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки влево
    func clickImageViewArrowLeftCollectionsFoot() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_FOOT] != nil) {
                let pagerAdapterDressCollectionCategoryFoot: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_FOOT]!
                
                // Уменьшаем текущую позицию для головных уборов на -1
                if(pagerAdapterDressCollectionCategoryFoot.getCurrentPosition() > 0) {
                    pagerAdapterDressCollectionCategoryFoot.setCurrentPosition(pagerAdapterDressCollectionCategoryFoot.getCurrentPosition() - 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_FOOT)
            }
        }
    }
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки вправо
    func clickImageViewArrowRightCollectionsFoot() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_FOOT] != nil) {
                let pagerAdapterDressCollectionCategoryFoot: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_FOOT]!
                
                // Увеличиваем текущую позицию для головных уборов на +1
                if(pagerAdapterDressCollectionCategoryFoot.getCurrentPosition() < pagerAdapterDressCollectionCategoryFoot.getCount() - 3) {
                    pagerAdapterDressCollectionCategoryFoot.setCurrentPosition(pagerAdapterDressCollectionCategoryFoot.getCurrentPosition() + 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_FOOT)
            }
        }
    }
    
    //==========================================================================
    // Функция - обработчик пеерлистывания для кого предназначена одеждв влево и вправо
    func swipeCollectionsFoot(sender: UISwipeGestureRecognizer) {
        if (sender.direction == .Left) {
            self.clickImageViewArrowRightCollectionsFoot()
        }
        
        if (sender.direction == .Right) {
            self.clickImageViewArrowLeftCollectionsFoot()
        }
    }
    
    //========================================================================
    // Обработчик клика для левого изображения категории для обуви
    func clickImageViewLeftCollectionsFoot() {
        // Считываем id текущей категории
        if(self.viewLeftCollectionsFoot.tag > 0 && self.labelLeftCollectionsFoot.text != nil) {
            let currentCategoryId: String = String(self.viewLeftCollectionsFoot.tag)
            let currentCategoryTitle: String = self.labelLeftCollectionsFoot.text!
            let currentCategoryPosition: Int = self.imageViewLeftCollectionsFoot.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для центрального изображения категории для обуви
    func clickImageViewCenterCollectionsFoot() {
        // Считываем id текущей категории
        if(self.viewCenterCollectionsFoot.tag > 0 && self.labelCenterCollectionsFoot.text != nil) {
            let currentCategoryId: String = String(self.viewCenterCollectionsFoot.tag)
            let currentCategoryTitle: String = self.labelCenterCollectionsFoot.text!
            let currentCategoryPosition: Int = self.imageViewCenterCollectionsFoot.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для правого изображения категории для обуви
    func clickImageViewRightCollectionsFoot() {
        // Считываем id текущей категории
        if(self.viewRightCollectionsFoot.tag > 0 && self.labelRightCollectionsFoot.text != nil) {
            let currentCategoryId: String = String(self.viewRightCollectionsFoot.tag)
            let currentCategoryTitle: String = self.labelRightCollectionsFoot.text!
            let currentCategoryPosition: Int = self.imageViewRightCollectionsFoot.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Для аксессуаров
    //========================================================================
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки влево
    func clickImageViewArrowLeftCollectionsAccessory() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_ACCESSORY] != nil) {
                let pagerAdapterDressCollectionCategoryAccessory: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_ACCESSORY]!
                
                // Уменьшаем текущую позицию для головных уборов на -1
                if(pagerAdapterDressCollectionCategoryAccessory.getCurrentPosition() > 0) {
                    pagerAdapterDressCollectionCategoryAccessory.setCurrentPosition(pagerAdapterDressCollectionCategoryAccessory.getCurrentPosition() - 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_ACCESSORY)
            }
        }
    }
    
    //========================================================================
    // Функция-обработчик клика по изображению стрелки вправо
    func clickImageViewArrowRightCollectionsAccessory() {
        if(self.getArrayPagerAdapterDressCollectionCategory() != nil) {
            if(self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_ACCESSORY] != nil) {
                let pagerAdapterDressCollectionCategoryAccessory: PagerAdapterDressCollectionCategory = self.getArrayPagerAdapterDressCollectionCategory()![GlobalFlags.TAG_DRESS_ACCESSORY]!
                
                // Увеличиваем текущую позицию для головных уборов на +1
                if(pagerAdapterDressCollectionCategoryAccessory.getCurrentPosition() < pagerAdapterDressCollectionCategoryAccessory.getCount() - 3) {
                    pagerAdapterDressCollectionCategoryAccessory.setCurrentPosition(pagerAdapterDressCollectionCategoryAccessory.getCurrentPosition() + 1)
                }
                
                // Вызываем функция установки изображений для листания
                self.setImageViewDressCategory(GlobalFlags.TAG_DRESS_ACCESSORY)
            }
        }
    }
    
    //==========================================================================
    // Функция - обработчик пеерлистывания для кого предназначена одеждв влево и вправо
    func swipeCollectionsAccessory(sender: UISwipeGestureRecognizer) {
        if (sender.direction == .Left) {
            self.clickImageViewArrowRightCollectionsAccessory()
        }
        
        if (sender.direction == .Right) {
            self.clickImageViewArrowLeftCollectionsAccessory()
        }
    }
    
    //========================================================================
    // Обработчик клика для левого изображения категории для аксессуаров
    func clickImageViewLeftCollectionsAccessory() {
        // Считываем id текущей категории
        if(self.viewLeftCollectionsAccessory.tag > 0 && self.labelLeftCollectionsAccessory.text != nil) {
            let currentCategoryId: String = String(self.viewLeftCollectionsAccessory.tag)
            let currentCategoryTitle: String = self.labelLeftCollectionsAccessory.text!
            let currentCategoryPosition: Int = self.imageViewLeftCollectionsAccessory.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для центрального изображения категории для аксессуаров
    func clickImageViewCenterCollectionsAccessory() {
        // Считываем id текущей категории
        if(self.viewCenterCollectionsAccessory.tag > 0 && self.labelCenterCollectionsAccessory.text != nil) {
            let currentCategoryId: String = String(self.viewCenterCollectionsAccessory.tag)
            let currentCategoryTitle: String = self.labelCenterCollectionsAccessory.text!
            let currentCategoryPosition: Int = self.imageViewCenterCollectionsAccessory.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
    
    //========================================================================
    // Обработчик клика для правого изображения категории для аксессуаров
    func clickImageViewRightCollectionsAccessory() {
        // Считываем id текущей категории
        if(self.viewRightCollectionsAccessory.tag > 0 && self.labelRightCollectionsAccessory.text != nil) {
            let currentCategoryId: String = String(self.viewRightCollectionsAccessory.tag)
            let currentCategoryTitle: String = self.labelRightCollectionsAccessory.text!
            let currentCategoryPosition: Int = self.imageViewRightCollectionsAccessory.tag
            
            // Вызываем непосредственно функцию - обработчик клика
            self.clickImageViewCollections(
                currentCategoryId,
                currentCategoryTitleParam: currentCategoryTitle,
                currentCategoryPosition: currentCategoryPosition
            )
        }
    }
}
