package ru.alexprogs.dressroom;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.adapter.AdapterListViewMainMenu;
import ru.alexprogs.dressroom.adapter.PagerAdapterDressroom;
import ru.alexprogs.dressroom.components.ViewPagerHeightWrapping;
import ru.alexprogs.dressroom.db.mysql.MySQLCheckIsSaveCurrentCollection;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDress;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDressCollectionSwipe;
import ru.alexprogs.dressroom.db.sqlite.AsyncTaskLoadUserDetailsFromLocalDB;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDressLastView;
import ru.alexprogs.dressroom.db.sqlite.DBSQLiteHelper;
import ru.alexprogs.dressroom.dialogs.DialogMain;
import ru.alexprogs.dressroom.geolocation.LocationUser;
import ru.alexprogs.dressroom.gesturelistener.GestureListenerDressroom;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.FunctionsScreen;
import ru.alexprogs.dressroom.navigationdrawer.NavigationDrawerFragment;
import ru.alexprogs.dressroom.tabs.ContentFragment;
import ru.alexprogs.dressroom.tabs.FragmentSlidingTabsColors;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Свойства данного класса

    // Разные элементы
    private Toolbar mToolbar;                                   // верхняя панель инструментов
    private TextView mTextViewToolbarTitle;                     // элемент, представляющий собой заголовок для верхней панели
    private LinearLayout mLinearLayoutDressAdd;                 // элемент LinearLayout, являющейся контейнером для всплывающего меню выбора
    private LinearLayout mLinearLayoutMainMenu;                 // элемент LinearLayout, являющейся контейнером для главного меню приложения
    private RelativeLayout mRelativeLayoutButtonDressAdd;       // контейнер для кнопки добавления новой категории одежды на виртуальный манекен
    private ImageView mButtonDressAdd;                          // кнопка добавления новой категории одежды на виртуальный манекен
    private ImageView mButtonDressSave;                         // кнопка сохранения текущего набора одежды в БД для текущего пользователя
    private View mViewShadow;                                   // элемент View, предназначенный для затемнения экрана
    private FrameLayout mFrameLayoutContentMain;

    private static int mDressRotationAngle;                     // угол, на который повернуты изображения одежды вокруг вертикальной оси
    private static int mMainActivityViewType;                   // тип просматриваемой информации в данном Activity (одежда или коллекции)

    // Фрагмент для формирования внешнего вида пункта из списка для боковой выдвигающейся панели
    private static NavigationDrawerFragment mNavigationDrawerFragment;

    //==============================================================================================
    // Метод, вызываемый при создании активности
    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main);

        //------------------------------------------------------------------------------------------
        // Устанавливаем панель инструментов приложения
        this.setToolbar((Toolbar) findViewById(R.id.toolbar));

        if(this.getToolbar() != null) {
            setSupportActionBar(this.getToolbar());
        }

        //------------------------------------------------------------------------------------------
        // Инициализируем элемент, представляющий собой заголовок для верхней панели инструментов приложения
        this.setTextViewToolbarTitle((TextView) findViewById(R.id.textViewToolbarTitle));

        //------------------------------------------------------------------------------------------
        // Задаем шрифт для приложения
        if(this.getTextViewToolbarTitle() != null) {
            try {
                Typeface appTypeface = Typeface.createFromAsset(getAssets(), "fonts/calibri.ttf");

                if (appTypeface != null) {
                    GlobalFlags.setAppTypeface(appTypeface);
                    this.getTextViewToolbarTitle().setTypeface(GlobalFlags.getAppTypeface());
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error Create Font From Asset: " + exception.toString());
            }
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        MainActivity.setDressRotationAngle(0);

        //------------------------------------------------------------------------------------------
        // Элемент FrameLayout, в которрый загружается тот или иной контент в зависимости
        // от наличия подключения к Интернету
        this.setFrameLayoutContentMain((FrameLayout) findViewById(R.id.frameLayoutContentMain));

        //------------------------------------------------------------------------------------------
        // Инициализация боковой выдвигаюейся панели и панели инструментов приложения
        MainActivity.setNavigationDrawerFragment((NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer));
        MainActivity.getNavigationDrawerFragment().setUp(R.id.navigationDrawer, this.getFrameLayoutContentMain(), (DrawerLayout) findViewById(R.id.drawerLayout), this.getToolbar());

        //------------------------------------------------------------------------------------------
        // Инициализируем экземпляр класса, хранящего информацию о местоположении текущего пользователя
        GlobalFlags.setLocationUser(new LocationUser(this));
    }

    //==============================================================================================
    // Метод, вызываемый при возвращении фокуса в данную активность
    @Override
    protected void onResume() {
        super.onResume();

        //------------------------------------------------------------------------------------------
        // Инициализируем все переменные основного класса для работы с БД
        DBMain.initializeVariables(this, GlobalFlags.DB_TYPE_MYSQL);

        //------------------------------------------------------------------------------------------
        // Если приложение запущено впервые
        if(GlobalFlags.getIsAppFirstRun().equals(true)) {
            // Инициализируем необходимые переменные

            //--------------------------------------------------------------------------------------
            // Определяем ширину и высоту экрана устройства в пикселях
            FunctionsScreen.getDisplayResolution(MainActivity.this);

            //--------------------------------------------------------------------------------------
            // Инициализируем переменную, необходимую для перевода из dp в px
            GlobalFlags.DpToPx = this.getApplicationContext().getResources().getDisplayMetrics().density;

            // Создаем объект (экземпляр класса) для работы с локальной БД SQLite
            DBMain.setDBSQLiteHelper(new DBSQLiteHelper(this));

            // Инициализируем все подкласса основного класса для работы с локальной БДSQLite
            DBMain.initializeSubClasses();

            // Считываем информацию о категориях одежды
            DBMain.synchronizeAllDressCategories(GlobalFlags.ACTION_NO, true, true);

            //--------------------------------------------------------------------------------------
            // Инициализируем переменную sharedPreferencesUserDetails
            UserDetails.setSharedPreferencesUserDetails(this.getSharedPreferences(UserDetails.KEY_USER_DETAILS, 0));

            //--------------------------------------------------------------------------------------
            // Считываем данные о текущем пользователе из локальной БД
            AsyncTaskLoadUserDetailsFromLocalDB asyncTaskLoadUserDetailsFromLocalDB = new AsyncTaskLoadUserDetailsFromLocalDB(this);
            asyncTaskLoadUserDetailsFromLocalDB.execute();
        }
        // Иначе, если данное Activity запускается не впервый раз, то запускаем функцию инициализации всех
        // необходимых компонентов
        else {
            this.initializeComponents(MainActivity.getMainActivityViewType());
        }
    }

    //==============================================================================================
    // Метод, вызываемый при закрытии приложения
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(GlobalFlags.getLocationUser() != null) {
            GlobalFlags.getLocationUser().disconnect();
        }
    }

    //==============================================================================================
    // Метод для считывания заголовка для верхней панели
    private TextView getTextViewToolbarTitle() {
        return this.mTextViewToolbarTitle;
    }

    //==============================================================================================
    // Метод для задания заголовка для верхней панели
    private void setTextViewToolbarTitle(TextView textViewToolbarTitle) {
        this.mTextViewToolbarTitle = textViewToolbarTitle;
    }

    //==============================================================================================
    // Метод для считывания элемента LinearLayout, являющегося контейнером для всплывающего меню выбора
    private LinearLayout getLinearLayoutDressAdd() {
        return this.mLinearLayoutDressAdd;
    }

    //==============================================================================================
    // Метод для задания элемента LinearLayout, являющегося контейнером для всплывающего меню выбора
    private void setLinearLayoutDressAdd(LinearLayout linearLayoutDressAdd) {
        this.mLinearLayoutDressAdd = linearLayoutDressAdd;
    }

    //==============================================================================================
    // Метод для считывания элемента LinearLayout, являющегося контейнером для главного меню приложения
    private LinearLayout getLinearLayoutMainMenu() {
        return this.mLinearLayoutMainMenu;
    }

    //==============================================================================================
    // Метод для задания элемента LinearLayout, являющегося контейнером для главного меню приложения
    private void setLinearLayoutMainMenu(LinearLayout linearLayoutMainMenu) {
        this.mLinearLayoutMainMenu = linearLayoutMainMenu;
    }

    //==============================================================================================
    // Метод для считывания контейнера для кнопки добавления новой категории одежды на виртуальный манекен
    private RelativeLayout getRelativeLayoutButtonDressAdd() {
        return this.mRelativeLayoutButtonDressAdd;
    }

    //==============================================================================================
    // Метод для задания контейнера для кнопки добавления новой категории одежды на виртуальный манекен
    private void setRelativeLayoutButtonDressAdd(RelativeLayout relativeLayoutButtonDressAdd) {
        this.mRelativeLayoutButtonDressAdd = relativeLayoutButtonDressAdd;
    }

    //==============================================================================================
    // Метод для считывания кнопки добавления новой категории одежды на виртуальный манекен
    private ImageView getButtonDressAdd() {
        return this.mButtonDressAdd;
    }

    //==============================================================================================
    // Метод для задания кнопки добавления новой категории одежды на виртуальный манекен
    private void setButtonDressAdd(ImageView buttonDressAdd) {
        this.mButtonDressAdd = buttonDressAdd;
    }

    //==============================================================================================
    // Метод для считывания ссылки на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    public ImageView getButtonDressSave() {
        return this.mButtonDressSave;
    }

    //==============================================================================================
    // Метод для задания ссылки на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    private void setButtonDressSave(ImageView buttonDressSave) {
        this.mButtonDressSave = buttonDressSave;
    }

    //==============================================================================================
    // Метод для считывания элемента View, предназначенного для затемнения экрана
    private View getViewShadow() {
        return this.mViewShadow;
    }

    //==============================================================================================
    // Метод для задания элемента View, предназначенного для затемнения экрана
    private void setViewShadow(View viewShadow) {
        this.mViewShadow = viewShadow;
    }

    //==============================================================================================
    // Метод для считывания элемента FrameLayout, представляющего собой основное содержимое окна приложения
    public FrameLayout getFrameLayoutContentMain() {
        return this.mFrameLayoutContentMain;
    }

    //==============================================================================================
    // Метод для задания элемента FrameLayout, представляющего собой основное содержимое окна приложения
    private void setFrameLayoutContentMain(FrameLayout frameLayoutContentMain) {
        this.mFrameLayoutContentMain = frameLayoutContentMain;
    }

    //==============================================================================================
    // Метод для считывания значения угла, на который повернуты изображения одежды вокруг вертикальной оси
    public static int getDressRotationAngle() {
        return MainActivity.mDressRotationAngle;
    }

    //==============================================================================================
    // Метод для задания значения угла, на который повернуты изображения одежды вокруг вертикальной оси
    public static void setDressRotationAngle(int dressRotationAngle) {
        MainActivity.mDressRotationAngle = dressRotationAngle;
    }

    //==============================================================================================
    // Метод для считывания типа просматриваемой информации в данном Activity (одежда или коллекции)
    public static int getMainActivityViewType() {
        return MainActivity.mMainActivityViewType;
    }

    //==============================================================================================
    // Метод для задания типа просматриваемой информации в данном Activity (одежда или коллекции)
    public static void setMainActivityViewType(int mainActivityViewType) {
        MainActivity.mMainActivityViewType = mainActivityViewType;
    }

    //==============================================================================================
    // Метод для считывания объекта NavigationDrawerFragment
    public static NavigationDrawerFragment getNavigationDrawerFragment() {
        return MainActivity.mNavigationDrawerFragment;
    }

    //==============================================================================================
    // Метод для задания объекта NavigationDrawerFragment
    private static void setNavigationDrawerFragment(NavigationDrawerFragment navigationDrawerFragment) {
        MainActivity.mNavigationDrawerFragment = navigationDrawerFragment;
    }

    //==============================================================================================
    // Метод для считывания ссылки на верхнюю панель инструментов
    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    //==============================================================================================
    // Метод для задания ссылки на верхнюю панель инструментов
    private void setToolbar(Toolbar toolbar) {
        this.mToolbar = toolbar;
    }

    //==============================================================================================
    // Метод для первоначальной инициализации необходимых компонентов
    // Передаваемые параметры
    // viewType - тип просматриваемого контента (одежда или коллекции)
    public void initializeComponents(int viewType) {
        // Запоминаем тип просматриваемого контента
        MainActivity.setMainActivityViewType(viewType);

        //------------------------------------------------------------------------------------------
        // Обновляем адаптер для боковой всплывающей панели
        if(MainActivity.getNavigationDrawerFragment() != null) {
            if(MainActivity.getNavigationDrawerFragment().getNavigationDrawerAdapter() != null) {
                MainActivity.getNavigationDrawerFragment().getNavigationDrawerAdapter().notifyDataSetChanged();
            }
        }

        //------------------------------------------------------------------------------------------
        // В зависимости от типа просматриваемого контента выполняем определнные операции
        switch (viewType) {
            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION:        // если просматриваются коллекции
                View viewCollections = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.collections, this.getFrameLayoutContentMain(), false);

                if(this.getFrameLayoutContentMain() == null) {
                    this.setFrameLayoutContentMain((FrameLayout) findViewById(R.id.frameLayoutContentMain));
                }

                this.getFrameLayoutContentMain().removeAllViews();
                this.getFrameLayoutContentMain().addView(viewCollections);

                //----------------------------------------------------------------------------------
                // Считываем данные о коллекциях одежды
                DBMain.synchronizeDressCollectionInfo(GlobalFlags.ACTION_NO);

                break;

            case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS:         // если просматриваем одежду
            default:
                // Отображаем основное содержимое окно онлайн-примерочной
                View viewContentMain = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.content_main, this.getFrameLayoutContentMain(), false);

                if(this.getFrameLayoutContentMain() == null) {
                    this.setFrameLayoutContentMain((FrameLayout) findViewById(R.id.frameLayoutContentMain));
                }

                this.getFrameLayoutContentMain().removeAllViews();
                this.getFrameLayoutContentMain().addView(viewContentMain);

                //----------------------------------------------------------------------------------
                if(DBMain.getMySQLGoToDressLastView() == null) {
                    DBMain.setMySQLGoToDressLastView(new MySQLGoToDressLastView());
                }

                //----------------------------------------------------------------------------------
                // Загружаем информацию об одежде
                DBMain.synchronizeGoToDressLastView(GlobalFlags.ACTION_NO, true);

                break;
        }
    }

    //==============================================================================================
    // Метод для начальной инициализации компонентов при запуске приложения для layout = "content_main"
    public void initializeComponentsLayoutContentMain() {
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        MainActivity.setDressRotationAngle(0);

        //------------------------------------------------------------------------------------------
        // Инициализируем свойства данного класса
        GlobalFlags.setGestureListenerDressroom(new GestureListenerDressroom(MainActivity.this));

        //------------------------------------------------------------------------------------------
        // Формируем переменные-ссылки на элементы интерфейса
        this.setRelativeLayoutButtonDressAdd((RelativeLayout) findViewById(R.id.relativeLayoutButtonDressAdd));     // контейнер для кнопки добавления новой категории одежды на виртуальный манекен
        this.setButtonDressAdd((ImageView) findViewById(R.id.buttonDressAdd));                      // кнопка добавления новой категории одежды на виртуальный манекен
        ImageView buttonDressInfo = (ImageView) findViewById(R.id.buttonDressInfo);                 // кнопка для отображения окна с полной информацией обо всей одежде, одетой на виртальном манекене
        this.setButtonDressSave((ImageView) findViewById(R.id.buttonDressSave));                    // кнопка для сохранения информации о текущем наборе одежде для текущего пользователя
        this.setLinearLayoutDressAdd((LinearLayout) findViewById(R.id.linearLayoutDressAdd));       // элемент LinearLayout, являющейся контейнером для всплывающего меню выбора добавляемой на манекен одежды
        this.setLinearLayoutMainMenu((LinearLayout) findViewById(R.id.linearLayoutMainMenu));       // элемент LinearLayout, являющейся контейнером для главного меню приложения
        this.setViewShadow(findViewById(R.id.viewShadow));                                          // элемент View, предназначенный для затемнения экрана

        final HashMap<String, ViewPagerHeightWrapping> arrayViewPagerDressroom = new HashMap<>();
        arrayViewPagerDressroom.put(GlobalFlags.TAG_DRESS_HEAD, (ViewPagerHeightWrapping) findViewById(R.id.viewPagerDressroomHead));
        arrayViewPagerDressroom.put(GlobalFlags.TAG_DRESS_BODY, (ViewPagerHeightWrapping) findViewById(R.id.viewPagerDressroomBody));
        arrayViewPagerDressroom.put(GlobalFlags.TAG_DRESS_LEG, (ViewPagerHeightWrapping) findViewById(R.id.viewPagerDressroomLeg));
        arrayViewPagerDressroom.put(GlobalFlags.TAG_DRESS_FOOT, (ViewPagerHeightWrapping) findViewById(R.id.viewPagerDressroomFoot));

        //------------------------------------------------------------------------------------------
        // Сохраняем массив элементов ViewPager, предназначенных для листания одежды
        DBMain.setArrayViewPagerDressroom(arrayViewPagerDressroom);

        //------------------------------------------------------------------------------------------
        // Устанавливаем адаптеры для соответствующих элементов ViewPager
        if(DBMain.getArrayPagerAdapterDressroom() != null) {
            for(int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                String currentDressType = GlobalFlags.getArrayTagDressType().get(indexDressType);

                if(DBMain.getArrayPagerAdapterDressroom().containsKey(currentDressType) && DBMain.getArrayPagerAdapterDressroom().get(currentDressType) != null) {
                    if(arrayViewPagerDressroom.containsKey(currentDressType) && arrayViewPagerDressroom.get(currentDressType) != null) {
                        arrayViewPagerDressroom.get(currentDressType).setAdapter(DBMain.getArrayPagerAdapterDressroom().get(currentDressType));

                        // Устанавливаем текущую позицию для соответствующего элемента ViewPager
                        // равной номеру позиции одежды, отображаемой в первую очередь для соответствующего адаптера
                        arrayViewPagerDressroom.get(currentDressType).setCurrentItem(DBMain.getArrayPagerAdapterDressroom().get(currentDressType).getPositionDressShowNow());
                    }
                }
            }
        }

        //------------------------------------------------------------------------------------------
        // Если проинициализирован ViewPager для верха и низа, то задаем отрицательное верхнее смещеие
        // для элемента ViewPager, отображаюещго одежду для низа
        if(DBMain.getArrayPagerAdapterDressroom() != null) {
            if(DBMain.getArrayPagerAdapterDressroom().containsKey(GlobalFlags.TAG_DRESS_LEG) && DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.TAG_DRESS_LEG) != null) {
                RelativeLayout.LayoutParams layoutParamsViewPagerDressRoomLeg = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsViewPagerDressRoomLeg.addRule(RelativeLayout.BELOW, R.id.viewPagerDressroomBody);

                if(DBMain.getArrayPagerAdapterDressroom().containsKey(GlobalFlags.TAG_DRESS_BODY) && DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.TAG_DRESS_BODY) != null) {
                    layoutParamsViewPagerDressRoomLeg.setMargins(0, (int) ((-20) * GlobalFlags.DpToPx), 0, 0);
                }
                else {
                    layoutParamsViewPagerDressRoomLeg.setMargins(0, 0, 0, 0);
                }

                if(DBMain.getArrayViewPagerDressroom() != null) {
                    if (DBMain.getArrayViewPagerDressroom().containsKey(GlobalFlags.TAG_DRESS_LEG) && DBMain.getArrayViewPagerDressroom().get(GlobalFlags.TAG_DRESS_LEG) != null) {
                        DBMain.getArrayViewPagerDressroom().get(GlobalFlags.TAG_DRESS_LEG).setLayoutParams(layoutParamsViewPagerDressRoomLeg);
                    }
                }
            }
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем обработчики листания для каждого элемента ViewPager
        for(int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
            final String currentDressType = GlobalFlags.getArrayTagDressType().get(indexDressType);

            if(arrayViewPagerDressroom.containsKey(currentDressType)) {
                if(arrayViewPagerDressroom.get(currentDressType) != null) {
                    arrayViewPagerDressroom.get(currentDressType).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        float currentPositionOffset = 0;        // переменная, хранящая смещение при прокрутке одежды
                        int swipeDirection = 0;                 // переменная, хранящая направление листания одежды

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            // Определяем направление листания
                            if(positionOffset - currentPositionOffset > 0) {
                                swipeDirection = GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT;
                            }
                            else {
                                swipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT;
                            }

                            // Сохраняем текущее значение смещения
                            currentPositionOffset = positionOffset;
                        }

                        // Данный метод выполняется, когда будет активна другая страница
                        @Override
                        public void onPageSelected(int position) {
                            // Запускаем метод для проверки, сохранен ли набор одежды, в данный момент
                            // отображаемый на виртуальном манекене, для текущего пользователя
                            MySQLCheckIsSaveCurrentCollection mySQLCheckIsSaveCurrentCollection = new MySQLCheckIsSaveCurrentCollection(MainActivity.this.getButtonDressSave());
                            mySQLCheckIsSaveCurrentCollection.startCheckIsSaveCurrentCollection();

                            //----------------------------------------------------------------------
                            // Логическая переменная, определяющая необходимо ли загружать информацию
                            // о соответствующей одежды
                            Boolean isLoadDressInfo = false;

                            //----------------------------------------------------------------------
                            // Адаптер для текущего элемента ViewPager
                            PagerAdapterDressroom currentPagerAdapterDressroom = null;

                            if(DBMain.getArrayPagerAdapterDressroom() != null) {
                                if (DBMain.getArrayPagerAdapterDressroom().containsKey(currentDressType)) {
                                    if (DBMain.getArrayPagerAdapterDressroom().get(currentDressType) != null) {
                                        currentPagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom().get(currentDressType);
                                    }
                                }
                            }

                            //----------------------------------------------------------------------
                            // В зависимости от направления листания
                            switch (swipeDirection) {
                                case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                         // листание слева направо
                                    // Номер стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
                                    int arrayParamsPositionStart = 0;

                                    if(currentPagerAdapterDressroom != null) {
                                        if(currentPagerAdapterDressroom.getArrayParamsPositionStart() >= 0) {
                                            arrayParamsPositionStart = currentPagerAdapterDressroom.getArrayParamsPositionStart();
                                        }
                                    }

                                    if(arrayParamsPositionStart > 0 && position - arrayParamsPositionStart <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                        isLoadDressInfo = true;
                                    }

                                    break;

                                case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                         // листание справа налево
                                    // Номер конечной позиции, с которой в массиве mArrayParams присутствуют параметры
                                    int arrayParamsPositionEnd = 0;

                                    if(currentPagerAdapterDressroom != null) {
                                        if(currentPagerAdapterDressroom.getArrayParamsPositionEnd() >= 0) {
                                            arrayParamsPositionEnd = currentPagerAdapterDressroom.getArrayParamsPositionEnd();
                                        }
                                    }

                                    if(arrayParamsPositionEnd - position <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                        isLoadDressInfo = true;
                                    }

                                    break;
                            }

                            //----------------------------------------------------------------------
                            // Вызываем метод для загрузки информации о дополнительной одежде
                            if(isLoadDressInfo.equals(true)) {
                                MySQLGoToDress mySQLGoToDress = new MySQLGoToDress(MainActivity.this);
                                mySQLGoToDress.startGoToDress(GlobalFlags.ACTION_NO, swipeDirection, currentDressType);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем высоту элемента LinearLayout, являющейся контейнером для всплывающего меню
        // выбора добавляемой на манекен одежды, равной половине высоты экрана
        if(this.getLinearLayoutDressAdd() != null) {
            int heightLinearLayoutDressAdd = FunctionsScreen.getScreenHeight() / 2;
            RelativeLayout.LayoutParams linearLayoutParamsDressAdd = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightLinearLayoutDressAdd);
            linearLayoutParamsDressAdd.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            this.getLinearLayoutDressAdd().setLayoutParams(linearLayoutParamsDressAdd);
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем обработчик события клика по элементу View
        if(MainActivity.this.getViewShadow() != null) {
            MainActivity.this.getViewShadow().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Закрываем нижнюю панель для добавления новой одежды
                    if (MainActivity.this.getLinearLayoutDressAdd() != null) {
                        if (MainActivity.this.getLinearLayoutDressAdd().getVisibility() == View.VISIBLE) {
                            MainActivity.this.closeLinearLayoutDressAdd();
                        }
                    }

                    // Закрываем главное меню приложения
                    MainActivity.this.hideLinearLayoutMainMenu();
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Создаем цветные вкладки
        this.createTabsForLinearLayoutDressAdd();

        //------------------------------------------------------------------------------------------
        // Устанавливаем обработчики событий

        // Устанавливаем обработчики события для кнопки добавления (отображения на экране)
        // новой одежды для просмотра (виртуальной примерки)
        if(this.getRelativeLayoutButtonDressAdd() != null) {
            this.getRelativeLayoutButtonDressAdd().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Закрываем главное меню приложения
                    MainActivity.this.hideLinearLayoutMainMenu();

                    //------------------------------------------------------------------------------
                    // Если элемент LinearLayout, являющийся контейнером для всплывающего меню
                    // выбора добавляемой на манекен одежды, отображен, то закрываем его
                    if (MainActivity.this.getLinearLayoutDressAdd() != null) {
                        if (MainActivity.this.getLinearLayoutDressAdd().getVisibility() == View.VISIBLE) {
                            MainActivity.this.closeLinearLayoutDressAdd();
                        }
                        // Иначе, отображаем его
                        else {
                            // Меняем фон кнопки добавления одежды
                            MainActivity.this.getRelativeLayoutButtonDressAdd().setBackgroundResource(R.drawable.background_button_bottom_panel);

                            if (MainActivity.this.getButtonDressAdd() != null) {
                                MainActivity.this.getButtonDressAdd().setImageResource(R.drawable.add_click);
                            }

                            //----------------------------------------------------------------------
                            // Затемняем
                            if (MainActivity.this.getViewShadow() != null) {
                                MainActivity.this.getViewShadow().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_shadow));
                                MainActivity.this.getViewShadow().setVisibility(View.VISIBLE);
                            }

                            //----------------------------------------------------------------------
                            // Отображаем непосредственно необходимый элемент LinearLayout
                            TranslateAnimation translateAnimation = AnimationCustom.getTranslateAnimation(0.0F, 0.0F, FunctionsScreen.getScreenHeight() / 2, 0.0F, 300);

                            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    MainActivity.this.getLinearLayoutDressAdd().setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    MainActivity.this.getLinearLayoutDressAdd().clearAnimation();
                                }
                            });

                            MainActivity.this.getLinearLayoutDressAdd().startAnimation(translateAnimation);
                        }
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем обработчик события для кнопки отображения информации обо всех вещах,
        // которые в данный момент одеты на виртальный манекен
        if(buttonDressInfo != null) {
            buttonDressInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Отображаем Activity с информацией обо всех вещах, которые в данный момент одеты на виртальный манекен
                    Intent intentDressInfo = new Intent(MainActivity.this, ActivityDressInfo.class);

                    //------------------------------------------------------------------------------
                    // В качестве параметра для отображаемого Acvtivity передаем переменную, которая определяет
                    // что тип текущего набора одежды - коллекция
                    intentDressInfo.putExtra(GlobalFlags.TAG_DRESS_COLLECTION_TYPE, GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION);

                    //------------------------------------------------------------------------------
                    // Считываем id вещей, представленных в данный момент на вирутальном манекене
                    // и передаем их в качестве параметра для отображаемого Acvtivity
                    if (DBMain.getArrayPagerAdapterDressroom() != null) {
                        // В цикле перебираем все типы одежды
                        for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                            ViewPager currentViewPagerDressroom = arrayViewPagerDressroom.get(GlobalFlags.getArrayTagDressType().get(indexDressType));
                            PagerAdapterDressroom currentPagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType));

                            if (currentViewPagerDressroom != null && currentPagerAdapterDressroom != null) {
                                // Считываем параметры для одежды для текущего типа
                                HashMap<String, String> currentItemParams = currentPagerAdapterDressroom.getItemParams(currentViewPagerDressroom.getCurrentItem());

                                if (currentItemParams != null) {
                                    if (currentItemParams.containsKey(GlobalFlags.TAG_ID)) {
                                        intentDressInfo.putExtra(GlobalFlags.getArrayTagDressType().get(indexDressType), currentItemParams.get(GlobalFlags.TAG_ID));
                                    }
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Отображаем созданное Activity
                    startActivity(intentDressInfo);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Устанавливаем обработчик события для события щелчка по кнопке "Сохранить набор одежды"
        if(this.getButtonDressSave() != null) {
            this.getButtonDressSave().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Закрываем главное меню приложения
                    MainActivity.this.hideLinearLayoutMainMenu();

                    //------------------------------------------------------------------------------
                    try {
                        // Проверяем, если пользователь не авторизован, то перенаправляем на страницу
                        // авторизации пользователя
                        if (UserDetails.getIsUserLogged().equals(false)) {
                            Intent intentLoginRegister = new Intent(MainActivity.this, ActivityLoginRegister.class);
                            startActivity(intentLoginRegister);
                        }
                        // Иначе, сохраняем текущий набор одежды
                        else {
                            DBMain.setContext(MainActivity.this);

                            // Извлекаем id текущего набора одежды из тега для текущей кнопки
                            int currentCollectionId = 0;

                            if (MainActivity.this.getButtonDressSave().getTag() != null) {
                                currentCollectionId = (int) MainActivity.this.getButtonDressSave().getTag();
                            }

                            // Если id текущего набора одежды >0
                            if (currentCollectionId > 0) {
                                // Удаляем информацию о текущем наборе одежды для текущего пользователя
                                DBMain.startDressCollectionUnSave(
                                        GlobalFlags.ACTION_NO,
                                        currentCollectionId,
                                        GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                                        MainActivity.this.getButtonDressSave(),
                                        true
                                );
                            }
                            // Иначе считаем, что текущий набор одежды НЕ был ранее сохранен для текущего пользователя
                            else {
                                // Считываем id вещей, представленных в данный момент на вирутальном манекене
                                HashMap<String, String> arrayDressListId = new HashMap<>();

                                if (DBMain.getArrayPagerAdapterDressroom() != null) {
                                    // В цикле перебираем все типы одежды
                                    for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                                        ViewPager currentViewPagerDressroom = arrayViewPagerDressroom.get(GlobalFlags.getArrayTagDressType().get(indexDressType));
                                        PagerAdapterDressroom currentPagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType));

                                        if (currentViewPagerDressroom != null && currentPagerAdapterDressroom != null) {
                                            // Считываем параметры для одежды для текущего типа
                                            HashMap<String, String> currentItemParams = currentPagerAdapterDressroom.getItemParams(currentViewPagerDressroom.getCurrentItem());

                                            if (currentItemParams != null) {
                                                if (currentItemParams.containsKey(GlobalFlags.TAG_ID)) {
                                                    arrayDressListId.put(GlobalFlags.getArrayTagDressType().get(indexDressType), currentItemParams.get(GlobalFlags.TAG_ID));
                                                }
                                            }
                                        }
                                    }
                                }

                                //------------------------------------------------------------------
                                // Сохраняем текущий набор одежды
                                DBMain.startDressCollectionSave(
                                        GlobalFlags.ACTION_NO,
                                        GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                                        arrayDressListId,
                                        MainActivity.this.getButtonDressSave(),
                                        true
                                );
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        FunctionsLog.logPrint("Error Dress Collection Save: " + exception.toString());
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Инициализируем элемент LinearLayout, являющейся контейнером для главного меню приложения
        this.initializeLinearLayoutMainMenu();
    }

    //==============================================================================================
    // Метод для начальной инициализации компонентов при запуске приложения для layout = "collections"
    public void initializeComponentsLayoutCollections() {
        // Устанавливаем, что первоначально угол поворота вещей составляет 0 градусов
        MainActivity.setDressRotationAngle(0);

        //------------------------------------------------------------------------------------------
        // Отображаем непосредственно наборы одежды

        // Сохраняем элемент ViewPager, предназначенный для листания коллекций одежды
        DBMain.setViewPagerDressCollection((ViewPagerHeightWrapping) findViewById(R.id.viewPagerDressCollections));

        if (DBMain.getViewPagerDressCollection() != null) {
            if(DBMain.getPagerAdapterDressCollection() != null) {
                // Устанавливаем адаптер для текущего элемента ViewPager
                DBMain.getViewPagerDressCollection().setAdapter(DBMain.getPagerAdapterDressCollection());

                //----------------------------------------------------------------------------------
                // Устанавливаем обработчик листания для текущего элемента ViewPager
                DBMain.getViewPagerDressCollection().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    float currentPositionOffset = 0;        // переменная, хранящая смещение при прокрутке коллекций одежды
                    int swipeDirection = 0;                 // переменная, хранящая направление листания коллекций одежды

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Определяем направление листания
                        if (positionOffset - currentPositionOffset > 0) {
                            swipeDirection = GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT;
                        } else {
                            swipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT;
                        }

                        // Сохраняем текущее значение смещения
                        currentPositionOffset = positionOffset;
                    }

                    // Данный метод выполняется, когда будет активна другая страница
                    @Override
                    public void onPageSelected(int position) {
                        // Если выбран раздел "Мои коллекции"
                        if (MainActivity.getNavigationDrawerFragment() != null && DBMain.getPagerAdapterDressCollection() != null) {
                            if (MainActivity.getNavigationDrawerFragment().getCurrentSelectedPositionItemExpandableChilds() == 0) {
                                // Считываем id текущей активной коллекции
                                int currentDressCollectionId = DBMain.getPagerAdapterDressCollection().getItemParamsId(position);

                                // Если id текущей коллекции одежды успешно считан
                                if (currentDressCollectionId > 0) {
                                    // Устанавливаем id текущей коллекции одежды в качестве тега
                                    // для кнопки сохранения информации о текущем наборе одежды
                                    if (MainActivity.this.getButtonDressSave() != null) {
                                        MainActivity.this.getButtonDressSave().setTag(currentDressCollectionId);
                                    }
                                }
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Логическая переменная, определяющая необходимо ли загружать информацию
                        // о соответствующей коллекции одежды
                        Boolean isLoadDressCollectionInfo = false;

                        //--------------------------------------------------------------------------
                        // В зависимости от направления листания
                        switch (swipeDirection) {
                            case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                             // листание слева направо
                                // Номер стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
                                int arrayParamsPositionStart = 0;

                                if (DBMain.getPagerAdapterDressCollection() != null) {
                                    if (DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() >= 0) {
                                        arrayParamsPositionStart = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart();
                                    }
                                }

                                if (arrayParamsPositionStart > 0 && position - arrayParamsPositionStart <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                    isLoadDressCollectionInfo = true;
                                }

                                break;

                            case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                             // листание справа налево
                                // Номер конечной позиции, с которой в массиве mArrayParams присутствуют параметры
                                int arrayParamsPositionEnd = 0;

                                if (DBMain.getPagerAdapterDressCollection() != null) {
                                    if (DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() >= 0) {
                                        arrayParamsPositionEnd = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd();
                                    }
                                }

                                if (arrayParamsPositionEnd - position <= GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                    isLoadDressCollectionInfo = true;
                                }

                                break;
                        }

                        //--------------------------------------------------------------------------
                        // Вызываем метод для загрузки информации о дополнительной коллекции одежде
                        if (isLoadDressCollectionInfo.equals(true)) {
                            MySQLGoToDressCollectionSwipe mySQLGoToDressCollectionSwipe = new MySQLGoToDressCollectionSwipe();
                            mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(swipeDirection);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Инициализируем кнопки-стрелки листания

            // Листание влево
            ImageView imageViewArrowLeft = (ImageView) findViewById(R.id.imageViewArrowLeft);

            if(imageViewArrowLeft != null) {
                imageViewArrowLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(DBMain.getViewPagerDressCollection() != null) {
                            // Если достигнуто начало списка
                            if (DBMain.getViewPagerDressCollection().getCurrentItem() <= 0) {
                                // Выводим соответствующее предупреждение об этом
                                Toast toastNoCollectionsPrev = Toast.makeText(MainActivity.this, R.string.string_no_dress_prev, Toast.LENGTH_SHORT);
                                toastNoCollectionsPrev.setGravity(Gravity.CENTER, 0, 0);
                                toastNoCollectionsPrev.show();
                            }
                            // Иначе, перелистываем влево
                            else {
                                // Определяем порядковый номер текущей позиции
                                int currentPosition = DBMain.getViewPagerDressCollection().getCurrentItem();

                                // Номер стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
                                int arrayParamsPositionStart = 0;

                                if (DBMain.getPagerAdapterDressCollection() != null) {
                                    if (DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() >= 0) {
                                        arrayParamsPositionStart = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart();
                                    }
                                }

                                if (arrayParamsPositionStart > 0 && currentPosition - arrayParamsPositionStart < GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                    MySQLGoToDressCollectionSwipe mySQLGoToDressCollectionSwipe = new MySQLGoToDressCollectionSwipe();
                                    mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT);
                                }

                                // Осуществляем непорседственно перелистывание влево
                                DBMain.getViewPagerDressCollection().setCurrentItem(currentPosition - 1);
                            }
                        }
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Листание вправо
            ImageView imageViewArrowRight = (ImageView) findViewById(R.id.imageViewArrowRight);

            if(imageViewArrowRight != null) {
                imageViewArrowRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(DBMain.getViewPagerDressCollection() != null) {
                            // Если достигнут конец списка
                            if (DBMain.getPagerAdapterDressCollection() != null && DBMain.getViewPagerDressCollection().getCurrentItem() >= DBMain.getPagerAdapterDressCollection().getCount() - 1) {
                                // Выводим соответствующее предупреждение об этом
                                Toast toastNoCollectionsNext = Toast.makeText(MainActivity.this, R.string.string_no_dress_next, Toast.LENGTH_SHORT);
                                toastNoCollectionsNext.setGravity(Gravity.CENTER, 0, 0);
                                toastNoCollectionsNext.show();
                            }
                            // Иначе, перелистываем вправо
                            else {
                                // Определяем порядковый номер текущей позиции
                                int currentPosition = DBMain.getViewPagerDressCollection().getCurrentItem();

                                // Номер конечной позиции, с которой в массиве mArrayParams присутствуют параметры
                                int arrayParamsPositionEnd = 0;

                                if (DBMain.getPagerAdapterDressCollection() != null) {
                                    if (DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() >= 0) {
                                        arrayParamsPositionEnd = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd();
                                    }
                                }

                                if (arrayParamsPositionEnd - currentPosition < GlobalFlags.COUNT_DRESS_READ_FROM_DB / 2) {
                                    MySQLGoToDressCollectionSwipe mySQLGoToDressCollectionSwipe = new MySQLGoToDressCollectionSwipe();
                                    mySQLGoToDressCollectionSwipe.startGoToDressCollectionSwipe(GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT);
                                }

                                // Осуществляем непосредственно перелистывание вправо
                                DBMain.getViewPagerDressCollection().setCurrentItem(DBMain.getViewPagerDressCollection().getCurrentItem() + 1);
                            }
                        }
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Устанавливаем обработчик события клика по элементу View, предназначенного для затемнения
            this.setViewShadow(findViewById(R.id.viewShadow));

            if(MainActivity.this.getViewShadow() != null) {
                MainActivity.this.getViewShadow().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Закрываем нижнюю панель для добавления новой одежды
                        if (MainActivity.this.getLinearLayoutDressAdd() != null) {
                            if (MainActivity.this.getLinearLayoutDressAdd().getVisibility() == View.VISIBLE) {
                                MainActivity.this.closeLinearLayoutDressAdd();
                            }
                        }

                        // Закрываем главное меню приложения
                        MainActivity.this.hideLinearLayoutMainMenu();
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Кнопка отображения информации о текущей одежде
            ImageView buttonDressInfo = (ImageView) findViewById(R.id.buttonDressInfo);

            // Устанавливаем обработчик события для кнопки отображения информации обо всех вещах,
            // которые в данный момент одеты на виртальный манекен
            if(buttonDressInfo != null) {
                buttonDressInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Отображаем Activity с информацией обо всех вещах, которые в данный момент одеты на виртальный манекен
                        Intent intentDressInfo = new Intent(MainActivity.this, ActivityDressInfo.class);

                        //--------------------------------------------------------------------------
                        // В качестве параметра для отображаемого Acvtivity передаем переменную, которая определяет
                        // что тип текущего набора одежды - коллекция
                        intentDressInfo.putExtra(GlobalFlags.TAG_DRESS_COLLECTION_TYPE, GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION);

                        //--------------------------------------------------------------------------
                        // Считываем id вещей, представленных в данный момент на вирутальном манекене
                        // и передаем их в качестве параметра для отображаемого Acvtivity
                        if(DBMain.getPagerAdapterDressCollection() != null && DBMain.getViewPagerDressCollection() != null) {
                            // Считываем общий массив параметров для текущего набора одежды
                            HashMap<String, ArrayList<HashMap<String, String>>> currentItemParams = DBMain.getPagerAdapterDressCollection().getItemParams(
                                    DBMain.getViewPagerDressCollection().getCurrentItem()
                            );

                            // В цикле перебираем все типы одежды
                            for(int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                                // Перебираем все возможные типы одежды
                                if(currentItemParams != null) {
                                    if (currentItemParams.containsKey(GlobalFlags.getArrayTagDressType().get(indexDressType))) {
                                        // Считываем параметры одежды кокретно для текущего типа
                                        ArrayList<HashMap<String, String>> currentItemParamsForType = currentItemParams.get(GlobalFlags.getArrayTagDressType().get(indexDressType));

                                        // В цикле перебираем всю одежду для текущего типа
                                        if(currentItemParamsForType != null) {
                                            for (int indexCurrentItemParamsForType = 0; indexCurrentItemParamsForType < currentItemParamsForType.size(); indexCurrentItemParamsForType++) {
                                                // Считываем параметры для текущей одежды
                                                HashMap<String, String> currentItemParamsForTypeForDress = currentItemParamsForType.get(indexCurrentItemParamsForType);

                                                if (currentItemParamsForTypeForDress != null) {
                                                    if (currentItemParamsForTypeForDress.containsKey(GlobalFlags.TAG_ID)) {
                                                        if (currentItemParamsForTypeForDress.get(GlobalFlags.TAG_ID) != null) {
                                                            intentDressInfo.putExtra(GlobalFlags.getArrayTagDressType().get(indexDressType), currentItemParamsForTypeForDress.get(GlobalFlags.TAG_ID));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Отображаем созданное Activity
                        startActivity(intentDressInfo);
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Кнопка удаления информации о текущем наборе одежды
            this.setButtonDressSave((ImageView) findViewById(R.id.buttonDressSave));

            // Устанавливаем кнопку сохранения информации о текущем наборе одежды видимой
            // только для раздела ""Мои коллекции
            if(MainActivity.getNavigationDrawerFragment() != null) {
                if(MainActivity.getNavigationDrawerFragment().getCurrentSelectedPositionItemExpandableChilds() == 0) {
                    this.getButtonDressSave().setVisibility(View.VISIBLE);
                }
                else {
                    this.getButtonDressSave().setVisibility(View.GONE);
                }
            }

            // Устанавливаем обработчик клика по кнопке сохранения информации о текущем наборе одежды
            if(this.getButtonDressSave() != null) {
                this.getButtonDressSave().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Закрываем главное меню приложения
                        MainActivity.this.hideLinearLayoutMainMenu();

                        //--------------------------------------------------------------------------
                        // Выводим диалог подтверждения
                        AlertDialog.Builder dialogUndress = new AlertDialog.Builder(MainActivity.this);

                        // Устанавливаем заголовок окна
                        dialogUndress.setTitle(R.string.string_text_dialog_dress_collection_delete);

                        // Кнопка "Да" формируемого диалогового окна
                        dialogUndress.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBMain.setContext(MainActivity.this);

                                // Извлекаем id текущего набора одежды из тега для текущей кнопки
                                if (MainActivity.this.getButtonDressSave().getTag() != null) {
                                    int currentCollectionId = (int) MainActivity.this.getButtonDressSave().getTag();

                                    // Удаляем информацию о текущем наборе одежды для текущего пользователя
                                    DBMain.startDressCollectionUnSave(
                                            GlobalFlags.ACTION_NO,
                                            currentCollectionId,
                                            GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION,
                                            MainActivity.this.getButtonDressSave(),
                                            true
                                    );
                                }
                            }
                        });

                        // Кнопка "Нет" формируемого диалогового окна
                        dialogUndress.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        // Отображаем диалоговое окно
                        dialogUndress.show();
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Кнопка для расшаривания текущей коллекции для других пользователей
            ImageView buttonDressShare = (ImageView) findViewById(R.id.buttonDressShare);

            if(buttonDressShare != null) {
                buttonDressShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Закрываем главное меню приложения
                        MainActivity.this.hideLinearLayoutMainMenu();

                        //--------------------------------------------------------------------------
                        DialogMain.createDialog(MainActivity.this, GlobalFlags.DIALOG_SHARE, null);
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Инициализируем элемент LinearLayout, являющейся контейнером для главного меню приложения
            this.setLinearLayoutMainMenu((LinearLayout) findViewById(R.id.linearLayoutMainMenu));
            this.initializeLinearLayoutMainMenu();
        }
    }

    //==============================================================================================
    // Метод для инициализации элемента LinearLayout, являющегося контейнером для главного меню приложения
    private void initializeLinearLayoutMainMenu() {
        if(this.getLinearLayoutMainMenu() != null) {
            // Получаем ссылку на соответствующий элемент ListView
            ListView listViewMainMenu = (ListView) this.getLinearLayoutMainMenu().findViewById(R.id.listViewMainMenu);

            if(listViewMainMenu != null) {
                // Формируем список пунктов главного меню приложения в зависимости от типа просматриваемой
                // информации в главном окне приложения
                ArrayList<Integer> arrayMainMenuItems = new ArrayList<>();
                arrayMainMenuItems.add(GlobalFlags.MAIN_MENI_ITEM_ROTATE);          // пункт "Повернуть"

                if (MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
//                  arrayMainMenuItems.add(GlobalFlags.MAIN_MENI_ITEM_RANDOM);      // пункт "Случайный"
                    arrayMainMenuItems.add(GlobalFlags.MAIN_MENI_ITEM_SHARE);       // пункт "Поделиться"
                }

//              arrayMainMenuItems.add(GlobalFlags.MAIN_MENI_ITEM_SETTINGS);        // пункт "Настройки"
//                arrayMainMenuItems.add(GlobalFlags.MAIN_MENI_ITEM_EXIT);            // пункт "Выход"

                // Формируем адаптер для списка пунктов глвного меню приложения
                final AdapterListViewMainMenu adapterListViewMainMenu = new AdapterListViewMainMenu(arrayMainMenuItems);
                listViewMainMenu.setAdapter(adapterListViewMainMenu);

                //----------------------------------------------------------------------------------
                // Устанавливаем обработчик клика для пунктов меню
                listViewMainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Закрываем главное меню приложения
                        MainActivity.this.hideLinearLayoutMainMenu();

                        switch (adapterListViewMainMenu.getItem(position)) {
                            case GlobalFlags.MAIN_MENI_ITEM_ROTATE:                 // если текущий пункт - поворот виртуального манекена
                                // Поворачиваем виртуальный манекен на 180 градусов
                                switch (MainActivity.getDressRotationAngle()) {
                                    case GlobalFlags.DRESS_ROTATION_ANGLE_0:                        // если в настоящий момент угол поворота манекена составляет 0 градусов
                                        MainActivity.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_180);
                                        break;
                                    case GlobalFlags.DRESS_ROTATION_ANGLE_180:                      // если в настоящий момент угол поворота манекена составляет 180 градусов
                                        MainActivity.setDressRotationAngle(GlobalFlags.DRESS_ROTATION_ANGLE_0);
                                        break;
                                }

                                //------------------------------------------------------------------
                                // Перезагружаем соответствующие адаптеры в зависимости от типа
                                // просматриваемого содержимого в главном окне приложения
                                switch (MainActivity.getMainActivityViewType()) {
                                    case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS:                 // если тип содержимого  - ОДЕЖДА
                                        if (DBMain.getArrayPagerAdapterDressroom() != null) {
                                            for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                                                String currentDressType = GlobalFlags.getArrayTagDressType().get(indexDressType);

                                                if (DBMain.getArrayPagerAdapterDressroom().containsKey(currentDressType)) {
                                                    if (DBMain.getArrayPagerAdapterDressroom().get(currentDressType) != null) {
                                                        DBMain.getArrayPagerAdapterDressroom().get(currentDressType).notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }

                                        break;

                                    case GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION:            // если тип содержимого  - СОХРАНЕННЫЕ КОЛЛЕКЦИИ
                                        if(DBMain.getPagerAdapterDressCollection() != null) {
                                            DBMain.getPagerAdapterDressCollection().notifyDataSetChanged();
                                        }

                                        break;
                                }
                                break;
                            case GlobalFlags.MAIN_MENI_ITEM_SHARE:                  // если текущий пункт - расшарить текущую коллекцию для других пользователей
                                DialogMain.createDialog(MainActivity.this, GlobalFlags.DIALOG_SHARE, null);
                                break;
                            case GlobalFlags.MAIN_MENI_ITEM_RANDOM:                 // если текущий пункт - случайный набор одежды
                                break;
                            case GlobalFlags.MAIN_MENI_ITEM_SETTINGS:               // если текущий пункт - настройки
                                break;
                            case GlobalFlags.MAIN_MENI_ITEM_EXIT:                   // если текущий пункт - выход из приложения
                                openQuitDialog(MainActivity.this);
                                break;
                        }
                    }
                });
            }
        }
    }

    //==============================================================================================
    // Метод для отображения/скрытия элемента LinearLayout, являющегося контейнером для главного меню приложения
    private void showHideLinearLayoutMainMenu() {
        // Отображаем или скрываем главное меню приложения
        if(this.getLinearLayoutMainMenu() != null) {
            // Если главное меню приложения отображено, то скрываем его
            if(this.getLinearLayoutMainMenu().getVisibility() == View.VISIBLE) {
                this.hideLinearLayoutMainMenu();
            }
            // Иначе, отображаем главное меню приложения
            else {
                this.showLinearLayoutMainMenu();
            }
        }
    }

    //==============================================================================================
    // Метод для отображения элемента LinearLayout, являющегося контейнером для главного меню приложения
    private void showLinearLayoutMainMenu() {
        // Закрываем нижнюю панель для добавления новой одежды
        this.closeLinearLayoutDressAdd();

        //------------------------------------------------------------------------------------------
        // Получаем ссылку на боковую выдвигающуюся панель
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Если боковая всплывающая панель открыта, то закрываем открытую боковую всплывающую панель
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        //------------------------------------------------------------------------------------------
        // Затемняем
        if (MainActivity.this.getViewShadow() != null) {
            MainActivity.this.getViewShadow().setBackgroundColor(Color.TRANSPARENT);
            MainActivity.this.getViewShadow().setVisibility(View.VISIBLE);
        }

        //------------------------------------------------------------------------------------------
        // Отображаем непосредственно главное меню приложения
        if(this.getLinearLayoutMainMenu() != null) {
            if(this.getLinearLayoutMainMenu().getVisibility() != View.VISIBLE) {
                // Почему-то анимация без этой строки не работает
                MainActivity.this.getLinearLayoutMainMenu().setVisibility(View.VISIBLE);

                AlphaAnimation alphaAnimation = AnimationCustom.getAlphaAnimation(0.0F, 1.0F, 150);

                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        MainActivity.this.getLinearLayoutMainMenu().setVisibility(View.VISIBLE);
                        MainActivity.this.getLinearLayoutMainMenu().clearAnimation();
                    }
                });

                MainActivity.this.getLinearLayoutMainMenu().startAnimation(alphaAnimation);
            }
        }
    }

    //==============================================================================================
    // Метод для скрытия элемента LinearLayout, являющегося контейнером для главного меню приложения
    private void hideLinearLayoutMainMenu() {
        // Убираем затемнение
        if (MainActivity.this.getViewShadow() != null) {
            MainActivity.this.getViewShadow().setVisibility(View.GONE);
        }

        //------------------------------------------------------------------------------------------
        // Закрываем непосредственно главное меню приложения
        if(this.getLinearLayoutMainMenu() != null) {
            if(this.getLinearLayoutMainMenu().getVisibility() == View.VISIBLE) {
                AlphaAnimation alphaAnimation = AnimationCustom.getAlphaAnimation(1.0F, 0.0F, 150);

                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        MainActivity.this.getLinearLayoutMainMenu().setVisibility(View.GONE);
                        MainActivity.this.getLinearLayoutMainMenu().clearAnimation();
                    }
                });

                MainActivity.this.getLinearLayoutMainMenu().startAnimation(alphaAnimation);
            }
        }
    }

    //===============================================================================================
    // Метод для создания вкладок для нижней выдвигающейся панели для выбора категории
    // добавляемой на виртуальный манекен одежды
    public void createTabsForLinearLayoutDressAdd() {
        // Создаем цветные вкладки
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Создаем массив, содержащий параметры для создаваемых вкладок
        ArrayList<HashMap<String, String>> arrayTabsInfo = new ArrayList<>();

        // Считываем многомерный массив, хранящий список категорий
        HashMap<String, ArrayList<HashMap<String, String>>> listCategoriesDress = DBMain.getListCategoriesDress(GlobalFlags.getDressForWho());

        if(listCategoriesDress != null) {
            if (listCategoriesDress.size() > 0) {
                // В цикле перебираем все возможные группы категорий одежды
                for(int indexTagDressType = 0; indexTagDressType < GlobalFlags.getArrayTagDressType().size(); indexTagDressType++) {
                    // Считываем текущий тег, определяющий типы одежды (головные уборы, обувь и т.д.)
                    String currentDressType = GlobalFlags.getArrayTagDressType().get(indexTagDressType);

                    // Если в глобальном массиве, содержащем сведения о категориях одежды,
                    // присутствуют сведения о категориях для текущей группы одежды
                    if (listCategoriesDress.containsKey(currentDressType)) {
                        if(listCategoriesDress.get(currentDressType) != null) {
                            // Формируем массив, содержащий информацию о текущем типе одежде (текущей вкладке)
                            HashMap<String, String> arrayCurrentTabInfo = new HashMap<>();

                            String currentTabTitle = null;      // заголовок текущей вкладки
                            int currentTabType     = 0;         // тип текущей вкладки

                            switch (currentDressType) {
                                case GlobalFlags.TAG_DRESS_HEAD:
                                    currentTabTitle = getString(R.string.string_dress_categories_group_head);
                                    currentTabType = GlobalFlags.SLIDING_TABS_COLORS_CONTENT_DRESS_CATEGORY_HEAD;
                                    break;
                                case GlobalFlags.TAG_DRESS_BODY:
                                    currentTabTitle = getString(R.string.string_dress_categories_group_body);
                                    currentTabType = GlobalFlags.SLIDING_TABS_COLORS_CONTENT_DRESS_CATEGORY_BODY;
                                    break;
                                case GlobalFlags.TAG_DRESS_LEG:
                                    currentTabTitle = getString(R.string.string_dress_categories_group_leg);
                                    currentTabType = GlobalFlags.SLIDING_TABS_COLORS_CONTENT_DRESS_CATEGORY_LEG;
                                    break;
                                case GlobalFlags.TAG_DRESS_FOOT:
                                    currentTabTitle = getString(R.string.string_dress_categories_group_foot);
                                    currentTabType = GlobalFlags.SLIDING_TABS_COLORS_CONTENT_DRESS_CATEGORY_FOOT;
                                    break;
                                case GlobalFlags.TAG_DRESS_ACCESSORY:
                                    currentTabTitle = getString(R.string.string_dress_categories_group_accessories);
                                    currentTabType = GlobalFlags.SLIDING_TABS_COLORS_CONTENT_DRESS_CATEGORY_ACCESSORY;
                                    break;
                            }

                            arrayCurrentTabInfo.put(GlobalFlags.TAG_TITLE, currentTabTitle);
                            arrayCurrentTabInfo.put(GlobalFlags.TAG_TYPE, String.valueOf(currentTabType));

                            // Добавляем информацию о текущей вкладке в общий массив
                            arrayTabsInfo.add(arrayCurrentTabInfo);
                        }
                    }
                }
            }
        }

        // Задаем контекст
        ContentFragment.setContentFragmentContext(MainActivity.this);

        FragmentSlidingTabsColors fragmentSlidingTabsColors = new FragmentSlidingTabsColors();
        fragmentSlidingTabsColors.initialize(arrayTabsInfo, true);
        fragmentTransaction.replace(R.id.tab_fragment_content, fragmentSlidingTabsColors);
        fragmentTransaction.commit();
    }

    //==============================================================================================
    // Метод для обработки нажатия клавиш
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Если нажата клавиша "Меню"
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            this.showHideLinearLayoutMainMenu();
            return true;
        }
        // Иначе, если нажата клавиша назад
        else if(keyCode == KeyEvent.KEYCODE_BACK) {
            // Получаем ссылку на боковую выдвигающуюся панель
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

            // Если боковая всплывающая панель открыта, то при нажатии кнопки "Назад" закрываем
            // открытую боковую всплывающую панель
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            // Иначе, если открыто главное меню приложения, то закрываем его
            else if(this.getLinearLayoutMainMenu() != null && this.getLinearLayoutMainMenu().getVisibility() == View.VISIBLE) {
                this.hideLinearLayoutMainMenu();
            }
            // Иначе, если открыт элемент LinearLayout, являющейся контейнером для всплывающего меню выбора,
            // то закрываем данный элемент
            else if(this.getLinearLayoutDressAdd() != null && this.getLinearLayoutDressAdd().getVisibility() == View.VISIBLE) {
                this.closeLinearLayoutDressAdd();
            }
            // Иначе, если боковая всплывающая панель закрыта, то при нажатии кнопки "Назад"
            // выводим запрос-подтверждение о выходе из приложения
            else {
                openQuitDialog(MainActivity.this);
            }

            return true;
        }
        // Иначе, если нажата какая-то другая клавиша
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //==============================================================================================
    // Метод выхода из приложения
    public void openQuitDialog(Context context) {
        // Создаем диалоговое окно подтверждения закрытия приложения
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);

        // Устанавливаем заголовок окна
        quitDialog.setTitle(R.string.string_title_dialog_app_close);

        // Кнопка "Да" формируемого диалогового окна подтверждения закрытия приложения
        quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Кнопка "Нет" формируемого диалогового окна подтверждения закрытия приложения
        quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Отображаем диалоговое окно подтверждения закрытия приложения
        quitDialog.show();
    }

    //==============================================================================================
    // Метод для закрытия элемента LinearLayout, являющегося контейнером для всплывающего меню выбора
    public void closeLinearLayoutDressAdd() {
        // Меняем фон кнопки добавления одежды
        if(this.getRelativeLayoutButtonDressAdd() != null) {
            this.getRelativeLayoutButtonDressAdd().setBackgroundColor(Color.TRANSPARENT);
        }

        // Убираем затемнение
        if (MainActivity.this.getViewShadow() != null) {
            MainActivity.this.getViewShadow().setVisibility(View.GONE);
        }

        if(this.getButtonDressAdd() != null) {
            this.getButtonDressAdd().setImageResource(R.drawable.add);
        }

        //----------------------------------------------------------------------
        // Закрываем непосредственно необходимый элемент LinearLayout
        if(this.getLinearLayoutDressAdd() != null && this.getLinearLayoutDressAdd().getVisibility() == View.VISIBLE) {
            TranslateAnimation translateAnimation = AnimationCustom.getTranslateAnimation(0.0F, 0.0F, 0.0F, FunctionsScreen.getScreenHeight() / 2, 300);

            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    MainActivity.this.getLinearLayoutDressAdd().setVisibility(View.GONE);
                    MainActivity.this.getLinearLayoutDressAdd().clearAnimation();
                }
            });

            MainActivity.this.getLinearLayoutDressAdd().startAnimation(translateAnimation);
        }
    }

    //==============================================================================================
    // Метод, вызываемый при выборе пункта из боковой всплывающей панели
    @Override
    public void onNavigationDrawerItemSelected(int position) {
/*
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment.newInstance())
                    .commit();
        } else if (position == 1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MailFragment.newInstance())
                    .commit();
        } else if (position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commit();
        }
*/
    }

    //==============================================================================================
    // Метод для создания главного меню приложения
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MainActivity.getNavigationDrawerFragment() != null) {
            // Если боковая всплывающая панель закрыта
            if (!MainActivity.getNavigationDrawerFragment().isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen if the drawer is not showing.
                // Otherwise, let the drawer decide what to show in the action bar.

                // Отображаем главное меню приложения
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    //==============================================================================================
    // Метод, обрабатывающий выбор пуктов главного меню приложения
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // В зависимости от выбранного пункта меню, выполняем соответствующие действия
        switch(id) {
            case R.id.mainMenuItemMenu:                                     // если выбран пункт "Меню" главного меню приложения
                // Закрываем или отображаем главное меню приложения
                if (this.getLinearLayoutMainMenu() != null) {
                    this.showHideLinearLayoutMainMenu();
                }
                break;
            case android.R.id.home:                                         // если нажата кнопка открытия/закрытия боковой всплывающей панели
                if(MainActivity.getNavigationDrawerFragment() != null) {
                    // Если боковая всплывающая панель открыта, то закрываем ее
                    if (MainActivity.getNavigationDrawerFragment().getDrawerLayout().isDrawerOpen(MainActivity.getNavigationDrawerFragment().getDrawerListView())) {
                        MainActivity.getNavigationDrawerFragment().getDrawerLayout().closeDrawer(MainActivity.getNavigationDrawerFragment().getDrawerListView());
                    }
                    // Иначе, если боковая всплывающая панель закрыта, то открываем ее
                    else {
                        MainActivity.getNavigationDrawerFragment().getDrawerLayout().openDrawer(MainActivity.getNavigationDrawerFragment().getDrawerListView());
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //==============================================================================================
    // Метод для изменения заголовка одежды в зависимости от типа просматриваемой одежды
    public void setDressroomTitleText() {
        if(this.getTextViewToolbarTitle() != null) {
            switch (GlobalFlags.getDressForWho()) {
                case GlobalFlags.DRESS_MAN:
                    this.getTextViewToolbarTitle().setText(R.string.bar_item_dress_man);
                    break;
                case GlobalFlags.DRESS_WOMAN:
                    this.getTextViewToolbarTitle().setText(R.string.bar_item_dress_woman);
                    break;
                case GlobalFlags.DRESS_KID:
                    this.getTextViewToolbarTitle().setText(R.string.bar_item_dress_kid);
                    break;
            }
        }
    }

    //==============================================================================================
    // Метод для изменения заголовка одежды
    // Передаваемые параметры
    // titleText - заголовок для текущего окна
    public void setDressroomTitleText(String titleText) {
        if(this.getTextViewToolbarTitle() != null) {
            this.getTextViewToolbarTitle().setText(titleText);
        }
    }
}
