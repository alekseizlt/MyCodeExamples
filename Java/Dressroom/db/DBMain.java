package ru.alexprogs.dressroom.db;

import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.adapter.PagerAdapterDressCollection;
import ru.alexprogs.dressroom.adapter.PagerAdapterDressroom;
import ru.alexprogs.dressroom.components.ViewPagerHeightWrapping;
import ru.alexprogs.dressroom.db.mysql.MySQLDressCollectionLoad;
import ru.alexprogs.dressroom.db.mysql.MySQLDressCollectionSave;
import ru.alexprogs.dressroom.db.mysql.MySQLGetAllDressBrands;
import ru.alexprogs.dressroom.db.mysql.MySQLGetAllDressCategories;
import ru.alexprogs.dressroom.db.mysql.MySQLGetBaseDressCategories;
import ru.alexprogs.dressroom.db.mysql.MySQLGetDressDefault;

import ru.alexprogs.dressroom.db.mysql.MySQLGoToDressLastView;
import ru.alexprogs.dressroom.db.sqlite.DBSQLiteHelper;
import ru.alexprogs.dressroom.lib.Functions;
import ru.alexprogs.dressroom.lib.FunctionsLog;

/**
* Основной класс для работы с БД
*/
public class DBMain {

    // Свойства текущего класса
    private static Context mContext;                       // контекст
    private static String mRemoteDBType;                   // тип удаленной БД (возможные значения "mysql" или "pgsql")

    private static DBSQLiteHelper mDBSQLiteHelper;         // класс, содержащий методы для создания и обновления локальной БД SQLite

    // Экземпяры классов
    private static MySQLGetBaseDressCategories mMySQLGetBaseDressCategories;   // класс для считывания информации о базовых категориях одежды
    private static MySQLGetAllDressCategories  mMySQLGetAllDressCategories;    // класс для считывания информации обо всех категориях одежды
    private static MySQLGetAllDressBrands      mMySQLGetAllDressBrands;        // класс для считывания информации обо всех брендах одежды
    private static MySQLGetDressDefault        mMySQLGetDressDefault;          // класс для считывания информации (изображения) об одежде по умолчанию
    private static MySQLDressCollectionSave    mMySQLDressCollectionSave;      // класс для сохранения текущего набора одежды для пользователя
    private static MySQLDressCollectionLoad    mMySQLDressCollectionLoad;      // класс для загрузки информации о текущем наборе одежды для пользователя
    private static MySQLGoToDressLastView      mMySQLGoToDressLastView;        // класс для считывания информации о последней просматриваемой одежде

    // Массив адаптеров для элементов ViewPager, предназначенных для листания одежды
    private static HashMap<String, PagerAdapterDressroom> mArrayPagerAdapterDressroom;

    // Массив элементов ViewPager, предназначенных для листания одежды
    private static HashMap<String, ViewPagerHeightWrapping> mArrayViewPagerDressroom;

    // Адаптер для элемента ViewPager, предназначенного для листания коллекций одежды
    private static PagerAdapterDressCollection mPagerAdapterDressCollection;

    // Элемент ViewPager, предназначенный для листания коллекций одежды
    private static ViewPagerHeightWrapping mViewPagerDressCollection;

    // Ассоциативные массивы
    // Ассоциативный массив, хранящий реальные размеры одежды для каждой группы одежды
    private static HashMap<String, Double> mArrayDressSizeRealMan;             // для мужской одежды
    private static HashMap<String, Double> mArrayDressSizeRealWoman;           // для женской одежды
    private static HashMap<String, Double> mArrayDressSizeRealKid;             // для детской одежды

    // Ассоциативный массив, хранящий размеры, к которым должны быть преобразованы размеры одежды,
    // для каждой группы одежды после загрузки с сервера
    private static HashMap<String, Integer> mArrayDressSizeTargetMan;             // для мужской одежды
    private static HashMap<String, Integer> mArrayDressSizeTargetWoman;           // для женской одежды
    private static HashMap<String, Integer> mArrayDressSizeTargetKid;             // для детской одежды

    // Массивы, хранящие информацию о последних отображаемых на виртуальном манекене вещей
    // Первый ключ у данных массивов - тип одежды (головные уборы, обувь и т.д.)
    private static HashMap<String, ArrayList<HashMap<String, String>>> mArrayCurrentDressInfoMan;          // для мужской одежды
    private static HashMap<String, ArrayList<HashMap<String, String>>> mArrayCurrentDressInfoWoman;        // для женской одежды
    private static HashMap<String, ArrayList<HashMap<String, String>>> mArrayCurrentDressInfoKid;          // для детской одежды

    // Массив, хранящий id и названия всех брендов одежды
    // У данного массива:
    // Ключ - id бренда одежды, значение - название соответствующего бренда одежды
    private static HashMap<String, String> mListAllDressBrands;

    // Массивы, хранящие id базовых категорий одежды
    private static HashMap<String, String> mListBaseCategoriesDressIdMan;      // мужская одежда
    private static HashMap<String, String> mListBaseCategoriesDressIdWoman;    // женская одежда
    private static HashMap<String, String> mListBaseCategoriesDressIdKid;      // детская одежда

    // Многомерный массив, хранящий список категорий
    // Первый ключ типа String определяет тип одежды (головные уборы, обувь и т.д.)
    // Второй ключ типа Integer определяет порядковый номер текущей вещи для данного типа одежды
    // Третий ключ типа String определяет атрибут текущей категории одежды (id, название и т.д.)
    private static HashMap<String, ArrayList<HashMap<String, String>>> mListCategoriesDressMan;      // мужская одежда
    private static HashMap<String, ArrayList<HashMap<String, String>>> mListCategoriesDressWoman;    // женская одежда
    private static HashMap<String, ArrayList<HashMap<String, String>>> mListCategoriesDressKid;      // детская одежда

    //==============================================================================================
    // Метод для инициализации свойств текущего класса
    // Передаваемые параметры
    // context - контекст
    // remoteDBType - тип удаленной БД (возможные значения "mysql" или "pgsql")
    public static void initializeVariables(Context context, String remoteDBType) {
        // Инициализируем свойства данного класса
        DBMain.setContext(context);                   // контекст
        DBMain.setRemoteDBType(remoteDBType);         // тип удаленной БД (возможные значения "mysql" или "pgsql")
    }

    //==============================================================================================
    // Метод для инициализации подклассов текущего класса
    public static void initializeSubClasses() {
        // В зависимости от типа удаленной БД (возможные значения "mysql" или "pgsql")
        // инициализируем соответствующие классы
        // Если тип удаленной БД - mysql
        if(DBMain.getRemoteDBType().equals(GlobalFlags.DB_TYPE_MYSQL)) {
            // Инициализируем необходимые классы
//            DBMain.setMySQLGetBaseDressCategories(new MySQLGetBaseDressCategories());
            DBMain.setMySQLGetAllDressCategories(new MySQLGetAllDressCategories());
//            DBMain.setMySQLGetAllDressBrands(new MySQLGetAllDressBrands());
//            DBMain.setMySQLGetDressDefault(new MySQLGetDressDefault());
            DBMain.setMySQLDressCollectionSave(new MySQLDressCollectionSave());
            DBMain.setMySQLDressCollectionLoad(new MySQLDressCollectionLoad());
            DBMain.setMySQLGoToDressLastView(new MySQLGoToDressLastView());
        }
    }

    //==============================================================================================
    // Метод для считывания значения контекста
    public static Context getContext() {
        return DBMain.mContext;
    }

    //==============================================================================================
    // Метод для задания значения контекста
    public static void setContext(Context context) {
        DBMain.mContext = context;
    }

    //==============================================================================================
    // Метод для считывания типа удаленной БД (возможные значения "mysql" или "pgsql")
    public static String getRemoteDBType() {
        return DBMain.mRemoteDBType;
    }

    //==============================================================================================
    // Метод для задания типа удаленной БД (возможные значения "mysql" или "pgsql")
    public static void setRemoteDBType(String remoteDBType) {
        remoteDBType = remoteDBType.toLowerCase().trim();

        // Заметим, что возможные значения - это "mysql" или "pgsql"
        // Если переданное в текущую функцию значение типа удаленной БД
        // не совпадает ни с одним из разрешенных, то устанавливаем в качестве
        // значения по умолчанию - значение "mysql"
        if(!remoteDBType.equals(GlobalFlags.DB_TYPE_PGSQL) && !remoteDBType.equals(GlobalFlags.DB_TYPE_MYSQL))
            DBMain.mRemoteDBType = GlobalFlags.DB_TYPE_MYSQL;
        // Иначе
        else
            DBMain.mRemoteDBType = remoteDBType;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса, содержащего методы
    // для создания и обновления локальной БД SQLite
    public static DBSQLiteHelper getDBSQLiteHelper() {
        return DBMain.mDBSQLiteHelper;
    }

    //==============================================================================================
    // Метод для задания объекта GestureListener
    public static void setDBSQLiteHelper(DBSQLiteHelper dbSQLiteHelper) {
        DBMain.mDBSQLiteHelper = dbSQLiteHelper;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации о базовых категориях одежды
    public static MySQLGetBaseDressCategories getMySQLGetBaseDressCategories() {
        return DBMain.mMySQLGetBaseDressCategories;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации о базовых категориях одежды
    public static void setMySQLGetBaseDressCategories(MySQLGetBaseDressCategories mysqlGetBaseDressCategories) {
        DBMain.mMySQLGetBaseDressCategories = mysqlGetBaseDressCategories;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации обо всех категориях одежды
    public static MySQLGetAllDressCategories getMySQLGetAllDressCategories() {
        return DBMain.mMySQLGetAllDressCategories;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации обо всех категориях одежды
    public static void setMySQLGetAllDressCategories(MySQLGetAllDressCategories mysqlGetAllDressCategories) {
        DBMain.mMySQLGetAllDressCategories = mysqlGetAllDressCategories;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации обо всех брендах одежды
    public static MySQLGetAllDressBrands getMySQLGetAllDressBrands() {
        return DBMain.mMySQLGetAllDressBrands;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации обо всех брендах одежды
    public static void setMySQLGetAllDressBrands(MySQLGetAllDressBrands mysqlGetAllDressBrands) {
        DBMain.mMySQLGetAllDressBrands = mysqlGetAllDressBrands;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации (изображения) об одежде по умолчанию
    public static MySQLGetDressDefault getMySQLGetDressDefault() {
        return DBMain.mMySQLGetDressDefault;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации (изображения) об одежде по умолчанию
    public static void setMySQLGetDressDefault(MySQLGetDressDefault mysqlGetDressDefault) {
        DBMain.mMySQLGetDressDefault = mysqlGetDressDefault;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для сохранения набора одежды
    public static MySQLDressCollectionSave getMySQLDressCollectionSave() {
        return DBMain.mMySQLDressCollectionSave;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для сохранения набора одежды
    public static void setMySQLDressCollectionSave(MySQLDressCollectionSave mysqlDressCollectionSave) {
        DBMain.mMySQLDressCollectionSave = mysqlDressCollectionSave;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для загрузки наборов одежды
    public static MySQLDressCollectionLoad getMySQLDressCollectionLoad() {
        return DBMain.mMySQLDressCollectionLoad;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для загрузки наборов одежды
    public static void setMySQLDressCollectionLoad(MySQLDressCollectionLoad mysqlDressCollectionLoad) {
        DBMain.mMySQLDressCollectionLoad = mysqlDressCollectionLoad;
    }

    //==============================================================================================
    // Метод для считывания объекта экземпляра класса для считывания информации о последней просматриваемой одежде
    public static MySQLGoToDressLastView getMySQLGoToDressLastView() {
        return DBMain.mMySQLGoToDressLastView;
    }

    //==============================================================================================
    // Метод для задания объекта экземпляра класса для считывания информации о последней просматриваемой одежде
    public static void setMySQLGoToDressLastView(MySQLGoToDressLastView mySQLGoToDressLastView) {
        DBMain.mMySQLGoToDressLastView = mySQLGoToDressLastView;
    }

    //==============================================================================================
    // Метод для считывания массива адаптеров для элементов ViewPager, предназначенных для листания одежды
    public static HashMap<String, PagerAdapterDressroom> getArrayPagerAdapterDressroom() {
        return DBMain.mArrayPagerAdapterDressroom;
    }

    //==============================================================================================
    // Метод для задания массива адаптеров для элементов ViewPager, предназначенных для листания одежды
    public static void setArrayPagerAdapterDressroom(HashMap<String, PagerAdapterDressroom> arrayPagerAdapterDressroom) {
        DBMain.mArrayPagerAdapterDressroom = arrayPagerAdapterDressroom;
    }

    //==============================================================================================
    // Метод для считывания массива элементов ViewPager, предназначенных для листания одежды
    public static HashMap<String, ViewPagerHeightWrapping> getArrayViewPagerDressroom() {
        return DBMain.mArrayViewPagerDressroom;
    }

    //==============================================================================================
    // Метод для задания массива элементов ViewPager, предназначенных для листания одежды
    public static void setArrayViewPagerDressroom(HashMap<String, ViewPagerHeightWrapping> arrayViewPagerDressroom) {
        DBMain.mArrayViewPagerDressroom = arrayViewPagerDressroom;
    }

    //==============================================================================================
    // Метод для считывания адаптера для элемента ViewPager, предназначенного для листания коллекций одежды
    public static PagerAdapterDressCollection getPagerAdapterDressCollection() {
        return DBMain.mPagerAdapterDressCollection;
    }

    //==============================================================================================
    // Метод для задания адаптера для элемента ViewPager, предназначенного для листания коллекций одежды
    public static void setPagerAdapterDressCollection(PagerAdapterDressCollection pagerAdapterDressCollection) {
        DBMain.mPagerAdapterDressCollection = pagerAdapterDressCollection;
    }

    //==============================================================================================
    // Метод для считывания элемента ViewPager, предназначенного для листания коллекций одежды
    public static ViewPagerHeightWrapping getViewPagerDressCollection() {
        return DBMain.mViewPagerDressCollection;
    }

    //==============================================================================================
    // Метод для задания элемента ViewPager, предназначенного для листания коллекций одежды
    public static void setViewPagerDressCollection(ViewPagerHeightWrapping viewPagerDressCollection) {
        DBMain.mViewPagerDressCollection = viewPagerDressCollection;
    }

    //==============================================================================================
    // Метод для перезагрузки адаптеров для элементов ViewPager
    // Передаваемые параметры
    // currentViewDressType - тип одежды (головные уборы, убовь и т.д.), для которого не надо
    //                        перезагружать адаптер
    public static void restartPagerAdapterDressroom(String currentViewDressType) {
        // В цикле перебираем все типы одежды
        if(DBMain.getArrayPagerAdapterDressroom() != null) {
            for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                if(currentViewDressType == null || !GlobalFlags.getArrayTagDressType().get(indexDressType).equals(currentViewDressType)) {
                    if(DBMain.getArrayPagerAdapterDressroom().containsKey(GlobalFlags.getArrayTagDressType().get(indexDressType))) {
                        if (DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType)) != null) {
                            DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType)).notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для мужской одежды
    private static HashMap<String, Double> getArrayDressSizeRealMan() {
        return DBMain.mArrayDressSizeRealMan;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для мужской одежды
    private static void setArrayDressSizeRealMan(HashMap<String, Double> arrayDressSizeRealMan) {
        DBMain.mArrayDressSizeRealMan = arrayDressSizeRealMan;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для женской одежды
    private static HashMap<String, Double> getArrayDressSizeRealWoman() {
        return DBMain.mArrayDressSizeRealWoman;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для женской одежды
    private static void setArrayDressSizeRealWoman(HashMap<String, Double> ArrayDressSizeRealWoman) {
        DBMain.mArrayDressSizeRealWoman = ArrayDressSizeRealWoman;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для детской одежды
    private static HashMap<String, Double> getArrayDressSizeRealKid() {
        return DBMain.mArrayDressSizeRealKid;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    // для детской одежды
    private static void setArrayDressSizeRealKid(HashMap<String, Double> arrayDressSizeRealKid) {
        DBMain.mArrayDressSizeRealKid = arrayDressSizeRealKid;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего максимальные реальные размеры одежды
    public static HashMap<String, Double> getArrayDressSizeReal(int dressForWho) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayDressSizeRealMan();
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayDressSizeRealWoman();
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayDressSizeRealKid();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего максимальные реальные размеры одежды
    public static void setArrayDressSizeReal(int dressForWho, HashMap<String, Double> arrayDressSizeReal) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayDressSizeRealMan(arrayDressSizeReal);
                break;
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayDressSizeRealWoman(arrayDressSizeReal);
                break;
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayDressSizeRealKid(arrayDressSizeReal);
                break;
        }
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для мужской одежды после загрузки с сервера
    private static HashMap<String, Integer> getArrayDressSizeTargetMan() {
        return DBMain.mArrayDressSizeTargetMan;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для мужской одежды после загрузки с сервера
    private static void setArrayDressSizeTargetMan(HashMap<String, Integer> arrayDressSizeTargetMan) {
        DBMain.mArrayDressSizeTargetMan = arrayDressSizeTargetMan;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для женской одежды после загрузки с сервера
    private static HashMap<String, Integer> getArrayDressSizeTargetWoman() {
        return DBMain.mArrayDressSizeTargetWoman;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для женской одежды после загрузки с сервера
    private static void setArrayDressSizeTargetWoman(HashMap<String, Integer> ArrayDressSizeTargetWoman) {
        DBMain.mArrayDressSizeTargetWoman = ArrayDressSizeTargetWoman;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для детской одежды после загрузки с сервера
    private static HashMap<String, Integer> getArrayDressSizeTargetKid() {
        return DBMain.mArrayDressSizeTargetKid;
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, для детской одежды после загрузки с сервера
    private static void setArrayDressSizeTargetKid(HashMap<String, Integer> arrayDressSizeTargetKid) {
        DBMain.mArrayDressSizeTargetKid = arrayDressSizeTargetKid;
    }

    //==============================================================================================
    // Метод для считывания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, после загрузки с сервера
    public static HashMap<String, Integer> getArrayDressSizeTarget(int dressForWho) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayDressSizeTargetMan();
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayDressSizeTargetWoman();
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayDressSizeTargetKid();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания ассоциативного массива, хранящего размеры, к которым должны быть
    // преобразованы размеры одежды, после загрузки с сервера
    public static void setArrayDressSizeTarget(int dressForWho, HashMap<String, Integer> arrayDressSizeTarget) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayDressSizeTargetMan(arrayDressSizeTarget);
                break;
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayDressSizeTargetWoman(arrayDressSizeTarget);
                break;
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayDressSizeTargetKid(arrayDressSizeTarget);
                break;
        }
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для мужской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static HashMap<String, ArrayList<HashMap<String, String>>> getArrayCurrentDressInfoMan() {
        return DBMain.mArrayCurrentDressInfoMan;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для мужской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static void setArrayCurrentDressInfoMan(HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfoMan) {
        DBMain.mArrayCurrentDressInfoMan = arrayCurrentDressInfoMan;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для женской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static HashMap<String, ArrayList<HashMap<String, String>>> getArrayCurrentDressInfoWoman() {
        return DBMain.mArrayCurrentDressInfoWoman;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для женской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static void setArrayCurrentDressInfoWoman(HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfoWoman) {
        DBMain.mArrayCurrentDressInfoWoman = arrayCurrentDressInfoWoman;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для детской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static HashMap<String, ArrayList<HashMap<String, String>>> getArrayCurrentDressInfoKid() {
        return DBMain.mArrayCurrentDressInfoKid;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей для детской одежды
    // Первый ключ у данного массива - тип одежды (головные уборы, обувь и т.д.)
    public static void setArrayCurrentDressInfoKid(HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfoKid) {
        DBMain.mArrayCurrentDressInfoKid = arrayCurrentDressInfoKid;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей
    public static HashMap<String, ArrayList<HashMap<String, String>>> getArrayCurrentDressInfo(int dressForWho) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getArrayCurrentDressInfoMan();
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getArrayCurrentDressInfoWoman();
            case GlobalFlags.DRESS_KID:
                return DBMain.getArrayCurrentDressInfoKid();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящего информацию о последних отображаемых
    // на виртуальном манекене вещей
    public static void setArrayCurrentDressInfo(int dressForWho, HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setArrayCurrentDressInfoMan(arrayCurrentDressInfo);
                break;
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setArrayCurrentDressInfoWoman(arrayCurrentDressInfo);
                break;
            case GlobalFlags.DRESS_KID:
                DBMain.setArrayCurrentDressInfoKid(arrayCurrentDressInfo);
                break;
        }
    }

    //==============================================================================================
    // Метод для определения групп одежды, вещи из которых присутствуют в данный момент
    // на виртуальном манекене, кроме группы "Аксессуары"
    public static ArrayList<String> getArrayDressGroupExists() {
        // Возвращаемый массив
        ArrayList<String> arrayDressGroupExists = null;

        // В цикле перебираем все типы одежды
        if(DBMain.getArrayPagerAdapterDressroom() != null) {
            arrayDressGroupExists = new ArrayList<>();

            for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                switch (GlobalFlags.getArrayTagDressType().get(indexDressType)) {
                    case GlobalFlags.TAG_DRESS_HEAD:
                    case GlobalFlags.TAG_DRESS_BODY:
                    case GlobalFlags.TAG_DRESS_LEG:
                    case GlobalFlags.TAG_DRESS_FOOT:
                        if(DBMain.getArrayPagerAdapterDressroom().containsKey(GlobalFlags.getArrayTagDressType().get(indexDressType))) {
                            if (DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType)) != null) {
                                if (DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.getArrayTagDressType().get(indexDressType)).getCount() > 0) {
                                    arrayDressGroupExists.add(GlobalFlags.getArrayTagDressType().get(indexDressType));
                                }
                            }
                        }

                        break;
                }
            }
        }

        return arrayDressGroupExists;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего id и названия всех брендов одежды
    public static HashMap<String, String> getListAllDressBrands() {
        return DBMain.mListAllDressBrands;
    }

    //==============================================================================================
    // Метод для задания объекта GestureListener
    public static void setListAllDressBrands(HashMap<String, String> listAllDressBrands) {
        DBMain.mListAllDressBrands = listAllDressBrands;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для мужской одежды
    public static HashMap<String, String> getListBaseCategoriesDressIdMan() {
        return DBMain.mListBaseCategoriesDressIdMan;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для мужской одежды
    public static void setListBaseCategoriesDressIdMan(HashMap<String, String> listBaseCategoriesDressIdMan) {
        DBMain.mListBaseCategoriesDressIdMan = listBaseCategoriesDressIdMan;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для женской одежды
    public static HashMap<String, String> getListBaseCategoriesDressIdWoman() {
        return DBMain.mListBaseCategoriesDressIdWoman;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для женской одежды
    public static void setListBaseCategoriesDressIdWoman(HashMap<String, String> listBaseCategoriesDressIdWoman) {
        DBMain.mListBaseCategoriesDressIdWoman = listBaseCategoriesDressIdWoman;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий для детской одежды
    public static HashMap<String, String> getListBaseCategoriesDressIdKid() {
        return DBMain.mListBaseCategoriesDressIdKid;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий для детской одежды
    public static void setListBaseCategoriesDressIdKid(HashMap<String, String> listBaseCategoriesDressIdKid) {
        DBMain.mListBaseCategoriesDressIdKid = listBaseCategoriesDressIdKid;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего список id базовых категорий одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    public static HashMap<String, String> getListBaseCategoriesDressId(int dressForWho)
    {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getListBaseCategoriesDressIdMan();
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getListBaseCategoriesDressIdWoman();
            case GlobalFlags.DRESS_KID:
                return DBMain.getListBaseCategoriesDressIdKid();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания массива, хранящего список id базовых категорий одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    // listBaseCategoriesDressId - список id базовых категорий одежды
    public static void setListBaseCategoriesDressId(int dressForWho, HashMap<String, String> listBaseCategoriesDressId)
    {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                DBMain.setListBaseCategoriesDressIdMan(listBaseCategoriesDressId);
                break;
            case GlobalFlags.DRESS_WOMAN:
                DBMain.setListBaseCategoriesDressIdWoman(listBaseCategoriesDressId);
                break;
            case GlobalFlags.DRESS_KID:
                DBMain.setListBaseCategoriesDressIdKid(listBaseCategoriesDressId);
                break;
        }
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для мужской одежды
    public static HashMap<String, ArrayList<HashMap<String, String>>> getListCategoriesDressMan() {
        return DBMain.mListCategoriesDressMan;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для мужской одежды
    public static void setListCategoriesDressMan(HashMap<String, ArrayList<HashMap<String, String>>> listCategoriesDressMan) {
        DBMain.mListCategoriesDressMan = listCategoriesDressMan;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для женской одежды
    public static HashMap<String, ArrayList<HashMap<String, String>>> getListCategoriesDressWoman() {
        return DBMain.mListCategoriesDressWoman;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для женской одежды
    public static void setListCategoriesDressWoman(HashMap<String, ArrayList<HashMap<String, String>>> listCategoriesDressWoman) {
        DBMain.mListCategoriesDressWoman = listCategoriesDressWoman;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящий список категорий для детской одежды
    public static HashMap<String, ArrayList<HashMap<String, String>>> getListCategoriesDressKid() {
        return DBMain.mListCategoriesDressKid;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, хранящий список категорий для детской одежды
    public static void setListCategoriesDressKid(HashMap<String, ArrayList<HashMap<String, String>>> listCategoriesDressKid) {
        DBMain.mListCategoriesDressKid = listCategoriesDressKid;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, хранящего сведения о категориях одежды
    // Передаваемые параметры
    // dressForWho - параметр, определяющий для кого предназначены текущие вещи
    public static HashMap<String, ArrayList<HashMap<String, String>>> getListCategoriesDress(int dressForWho) {
        switch(dressForWho) {
            case GlobalFlags.DRESS_MAN:
                return DBMain.getListCategoriesDressMan();
            case GlobalFlags.DRESS_WOMAN:
                return DBMain.getListCategoriesDressWoman();
            case GlobalFlags.DRESS_KID:
                return DBMain.getListCategoriesDressKid();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для проверки существования информации о запрашиваемой одежде в глобальном массиве
    public static Boolean checkDressInGlobalArray(int currentDressId, int dressForWho, String dressType) {
        // Возвращаемое значение
        Boolean isDressInLocalArray = false;

        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                // Считываем информацию об одежде для соответствуюещго типа (головные уборы, обувь и т.д.)
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                        // информация о которых представлена в массивах ArrayCurrentDressInfoMan, ArrayCurrentDressInfoWoman
                        // или ArrayCurrentDressInfoKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            // Считываем id одной из вещей из соответствуюещго массива
                            Integer dressIdInLocalArray = 0;

                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                if(arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_ID)) {
                                    if (arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID) != null) {
                                        dressIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID));
                                    }
                                }
                            }

                            // Если id совпали, то завершаем выполнение цикла
                            if (currentDressId == dressIdInLocalArray) {
                                isDressInLocalArray = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (checkDressInLocalArray): " + exception.toString());
            return false;
        }

        return isDressInLocalArray;
    }

    //==============================================================================================
    // Метод для проверки существования информации об одежде в глобальном массиве для необходимой категории
    // Передаваемые параметры
    // currentCategoryDressId - id категории, для которой необходимо проверить существование информации
    //                          об одежде в глобальном массиве
    // dressForWho            - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType              - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static Boolean checkCategoryDressInGlobalArray(int currentCategoryDressId, int dressForWho, String dressType) {
        // Возвращаемое значение
        Boolean isCategoryDressInLocalArray = false;

        if(dressType == null) {
            return false;
        }

        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id категории для текущей вещи с id категории хотя бы для одной из вещей,
                        // информация о которых представлена в глобальных массивах mListCategoriesDressMan, mListCategoriesDressWoman
                        // или mListCategoriesDressKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            // Считываем id категории для одной из вещей из соответствуюещго массива
                            Integer categoryDressIdInLocalArray = 0;

                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                if(arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_CATID)) {
                                    if (arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_CATID) != null) {
                                        categoryDressIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_CATID));
                                    }
                                }
                            }

                            // Если id совпали, то завершаем выполнение цикла
                            if (currentCategoryDressId == categoryDressIdInLocalArray) {
                                isCategoryDressInLocalArray = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (checkCategoryDressInGlobalArray): " + exception.toString());
            return false;
        }

        return isCategoryDressInLocalArray;
    }

    //==============================================================================================
    // Метод для вставки данных об определенной вещи в глобальный общий массив
    // Передаваемые параметры
    // currentDressId        - id текущей одежды, информацию о которой необходимо добавить в глобальный массив
    // dressForWho           - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType             - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    // arrayCurrentDressInfo - массив, содержащий информацию о текущей одежде, информацию о которой
    //                         необходимо добавить в глобальный массив
    public static void addDressToGlobalArray(int currentDressId, int dressForWho, String dressType, HashMap<String, String> arrayCurrentDressInfo) {
        // Сначала проверяем не присутствуют ли данные о текущей одежде в общем глобальном массиве
        Boolean isDressInLocalArray = DBMain.checkDressInGlobalArray(currentDressId, dressForWho, dressType);

        // Если данные о текущей вещи присутствуют в общем глобальном массиве, то удаляем
        // данные об указанной вещи из данного общего глобального массива
        if(isDressInLocalArray.equals(true)) {
            DBMain.deleteDressFromGlobalArrayById(currentDressId, dressForWho, dressType);
        }

        // Теперь вставляем данные о текущей вещи в общий глобальный массив
        switch (dressForWho) {
            case GlobalFlags.DRESS_MAN:                                 // для мужской одежды
                if (DBMain.getArrayCurrentDressInfoMan() == null) {
                    DBMain.setArrayCurrentDressInfoMan(new HashMap<String, ArrayList<HashMap<String, String>>>());
                }

                if(!DBMain.getArrayCurrentDressInfoMan().containsKey(dressType)) {
                    DBMain.getArrayCurrentDressInfoMan().put(dressType, new ArrayList<HashMap<String, String>>());
                }

                DBMain.getArrayCurrentDressInfoMan().get(dressType).add(arrayCurrentDressInfo);

                break;
            case GlobalFlags.DRESS_WOMAN:                               // для женской одежды
                if (DBMain.getArrayCurrentDressInfoWoman() == null) {
                    DBMain.setArrayCurrentDressInfoWoman(new HashMap<String, ArrayList<HashMap<String, String>>>());
                }

                if(!DBMain.getArrayCurrentDressInfoWoman().containsKey(dressType)) {
                    DBMain.getArrayCurrentDressInfoWoman().put(dressType, new ArrayList<HashMap<String, String>>());
                }

                DBMain.getArrayCurrentDressInfoWoman().get(dressType).add(arrayCurrentDressInfo);

                break;
            case GlobalFlags.DRESS_KID:                                 // для детской одежды
                if (DBMain.getArrayCurrentDressInfoKid() == null) {
                    DBMain.setArrayCurrentDressInfoKid(new HashMap<String, ArrayList<HashMap<String, String>>>());
                }

                if(!DBMain.getArrayCurrentDressInfoKid().containsKey(dressType)) {
                    DBMain.getArrayCurrentDressInfoKid().put(dressType, new ArrayList<HashMap<String, String>>());
                }

                DBMain.getArrayCurrentDressInfoKid().get(dressType).add(arrayCurrentDressInfo);

                break;
        }
    }

    //==============================================================================================
    // Метод для удаления данных об указанной вещи из общего глобального массива по id вещи
    // Передаваемые параметры
    // currentDressId - id текущей одежды, информацию о которой необходимо удалить из глобального массива
    // dressForWho    - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType      - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static void deleteDressFromGlobalArrayById(int currentDressId, int dressForWho, String dressType) {
        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                        // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                        // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                // Считываем id одной из вещей из соответствуюещго массива
                                Integer dressIdInLocalArray = 0;

                                if (arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_ID)) {
                                    if (arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID) != null) {
                                        dressIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID));
                                    }
                                }

                                // Если id совпали, то удаляем данные о текущей одежде
                                if (currentDressId == dressIdInLocalArray) {
                                    arrayCurrentDressInfo.get(dressType).remove(indexDress);

                                    // Если больше не осталось вещей такого же типа, то удаляем сам подмассив
                                    if (arrayCurrentDressInfo.get(dressType).size() <= 0) {
                                        arrayCurrentDressInfo.remove(dressType);
                                    }

                                    // Обновляем теперь непосредственно глобальный массив
                                    DBMain.setArrayCurrentDressInfo(dressForWho, arrayCurrentDressInfo);

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (deleteDressFromLocalArrayById): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для удаления данных об указанной вещи из общего глобального массива по id категории вещи
    // Передаваемые параметры
    // currentDressCategoryId - id категории текущей одежды, информацию о которой необходимо удалить из глобального массива
    // dressForWho            - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType              - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static void deleteDressFromGlobalArrayByCategoryId(int currentDressCategoryId, int dressForWho, String dressType) {
        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id категории текущей вещи с id категории одной из вещей,
                        // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                        // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                // Считываем id категории одной из вещей из соответствуюещго массива
                                Integer dressCategoryIdInLocalArray = 0;

                                if(arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_CATID)) {
                                    if(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_CATID) != null) {
                                        dressCategoryIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_CATID));
                                    }
                                }

                                // Если id категорий совпали, то удаляем данные о текущей одежде
                                if (currentDressCategoryId == dressCategoryIdInLocalArray) {
                                    arrayCurrentDressInfo.get(dressType).remove(indexDress);

                                    // Если больше не осталось вещей такого же типа, то удаляем сам подмассив
                                    if (arrayCurrentDressInfo.get(dressType).size() <= 0) {
                                        arrayCurrentDressInfo.remove(dressType);
                                    }

                                    // Обновляем теперь непосредственно глобальный массив
                                    DBMain.setArrayCurrentDressInfo(dressForWho, arrayCurrentDressInfo);

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (deleteDressFromLocalArrayByCategoryId): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для извлечения данных об указанной вещи из общего глобального массива по ее id
    // Передаваемые параметры
    // currentDressId - id текущей одежды, информацию о которой необходимо извлечь из глобального массива
    // dressForWho    - для кого предназначена одежда из текущей категории (для мужчин, женщин или детей)
    // dressType      - тип одежды из текущей категории (головные уборы, обувь и т.д.)
    public static HashMap<String, String> getDressFromGlobalArrayById(int currentDressId, int dressForWho, String dressType) {
        // Возвращаемые ассоциативный массив
        HashMap<String, String> returnArrayDressInfo = null;

        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                        // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                        // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                // Считываем id одной из вещей из соответствуюещго массива
                                Integer dressIdInLocalArray = 0;

                                if (arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_ID)) {
                                    if (arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID) != null) {
                                        dressIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID));
                                    }
                                }

                                // Если id совпали, то возвращаем данные о текущей одежде
                                if (currentDressId == dressIdInLocalArray) {
                                    returnArrayDressInfo = arrayCurrentDressInfo.get(dressType).get(indexDress);

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (getDressFromLocalArrayById): " + exception.toString());
            return null;
        }

        return returnArrayDressInfo;
    }

    //==============================================================================================
    // Метод для извлечения только одного поля об указанной вещи из общего глобального массива по ее id
    // Передаваемые параметры
    // currentDressId - id вещи, для которой необходимо считать данные
    // dressForWho - параметр "for_who" вещи, для которой необходимо считать данные
    // dressType - тип вещи, для которой необходимо считать данные
    // fieldName - название поля, для которого необходимо считать данные
    public static String getDressFieldFromGlobalArrayById(int currentDressId, int dressForWho, String dressType, String fieldName) {
        // Если в функцию не переданы названия полей, значения которых необходимо считать, то
        // завершаем выполнение данной функции
        if(fieldName == null) {
            return null;
        }

        // Возвращаемая переменная
        String returnDressFieldValue = null;

        try {
            // Массив, содержащий информацию об одежде для мужчины, женщины или ребенка
            HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

            if (arrayCurrentDressInfo != null) {
                if(arrayCurrentDressInfo.containsKey(dressType)) {
                    if (arrayCurrentDressInfo.get(dressType) != null) {
                        // В цикле проверяем не совпадает ли id текущей вещи с id одной из вещей,
                        // информация о которых представлена в массивах ArrayCurrentDressInfoMan,
                        // ArrayCurrentDressInfoWoman или ArrayCurrentDressInfoKid
                        for (int indexDress = 0; indexDress < arrayCurrentDressInfo.get(dressType).size(); indexDress++) {
                            if(arrayCurrentDressInfo.get(dressType).get(indexDress) != null) {
                                // Считываем id одной из вещей из соответствуюещго массива
                                Integer dressIdInLocalArray = 0;

                                if (arrayCurrentDressInfo.get(dressType).get(indexDress).containsKey(GlobalFlags.TAG_ID)) {
                                    if (arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID) != null) {
                                        dressIdInLocalArray = Integer.parseInt(arrayCurrentDressInfo.get(dressType).get(indexDress).get(GlobalFlags.TAG_ID));
                                    }
                                }

                                // Если id совпали, то возвращаем данные о текущей одежде
                                if (currentDressId == dressIdInLocalArray) {
                                    HashMap<String, String> currentDressInfo = arrayCurrentDressInfo.get(dressType).get(indexDress);

                                    if (currentDressInfo.containsKey(fieldName)) {
                                        returnDressFieldValue = currentDressInfo.get(fieldName);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (getDressFieldFromLocalArrayById): " + exception.toString());
            return null;
        }

        return returnDressFieldValue;
    }

    //==============================================================================================
    // Метод для считывания информации о соответствующей одежде из локальной БД
    // Передаваемые параметры
    // currentDressId - id вещи, сведения о которой необходимо скачать из локальной БД
    // isAddToLocalArray - логическая переменная, которая определяет сохранять ли данные
    //                     о текущей одежде (вещи) в глобальный массив
    public static HashMap<String, String> getDressFromLocalDBById(int currentDressId, Boolean isAddToLocalArray) {
        // // Возвращаемые ассоциативный массив
        HashMap<String, String> returnArrayDressInfo;

        try {
            // Считываем информацию о текущей вещи (одежде) из таблицы "dress"
            returnArrayDressInfo = DBMain.getDBSQLiteHelper().getRecordFromDBByIdServerMySQL(
                    GlobalFlags.TAG_TABLE_DRESS,
                    String.valueOf(currentDressId),
                    null);

            if(returnArrayDressInfo != null) {
                // Считываем информацию о категории для текущей вещи (одежде) из таблицы "category"
                HashMap<String, String> arrayCategoryInfo;

                if (returnArrayDressInfo.containsKey(GlobalFlags.TAG_CATID)) {
                    if (returnArrayDressInfo.get(GlobalFlags.TAG_CATID) != null) {
                        arrayCategoryInfo = DBMain.getDBSQLiteHelper().getRecordFromDBByIdServerMySQL(
                                GlobalFlags.TAG_TABLE_CATEGORIES,
                                returnArrayDressInfo.get(GlobalFlags.TAG_CATID),
                                null);

                        // Добавляем в возвращаемый массив сведения о текущей категории
                        if (arrayCategoryInfo != null) {
                            // Добавляем название текущей категории
                            if (arrayCategoryInfo.containsKey(GlobalFlags.TAG_TITLE)) {
                                returnArrayDressInfo.put(GlobalFlags.TAG_CATEGORY_TITLE, arrayCategoryInfo.get(GlobalFlags.TAG_TITLE));
                            }
                        }
                    }
                }

                //----------------------------------------------------------------------------------
                // Считываем информацию о бренде для текущей вещи (одежде) из таблицы "brand"
                HashMap<String, String> arrayBrandInfo;

                if (returnArrayDressInfo.containsKey(GlobalFlags.TAG_BRAND_ID)) {
                    if (returnArrayDressInfo.get(GlobalFlags.TAG_BRAND_ID) != null) {
                        arrayBrandInfo = DBMain.getDBSQLiteHelper().getRecordFromDBByIdServerMySQL(
                                GlobalFlags.TAG_TABLE_BRAND,
                                returnArrayDressInfo.get(GlobalFlags.TAG_BRAND_ID),
                                null);

                        // Добавляем в возвращаемый массив сведения о текущем бренде
                        if (arrayBrandInfo != null) {
                            // Добавляем название текущего бренда
                            if (arrayBrandInfo.containsKey(GlobalFlags.TAG_TITLE)) {
                                returnArrayDressInfo.put(GlobalFlags.TAG_BRAND_TITLE, arrayBrandInfo.get(GlobalFlags.TAG_TITLE));
                            }
                        }
                    }
                }

                //----------------------------------------------------------------------------------
                // При необходимости добавляем данные о текущей вещи (одежде) в глобальный массив
                if (isAddToLocalArray.equals(true)) {
                    // Определяем тип текущей вещи (одежды) и для кого предназначена текущая вещь
                    String currentDressType = null;
                    String currentDressForWho = null;

                    if (returnArrayDressInfo.containsKey(GlobalFlags.TAG_TYPE)) {
                        currentDressType = returnArrayDressInfo.get(GlobalFlags.TAG_TYPE);
                    }

                    if (returnArrayDressInfo.containsKey(GlobalFlags.TAG_FOR_WHO)) {
                        currentDressForWho = returnArrayDressInfo.get(GlobalFlags.TAG_FOR_WHO);
                    }

                    // Добавляем данные о текущей вещи (одежде) в глобальный массив
                    if (currentDressType != null && currentDressForWho != null) {
                        DBMain.addDressToGlobalArray(currentDressId, Functions.dressForWhoStringToInt(currentDressForWho), currentDressType, returnArrayDressInfo);
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (getDressFromLocalDBById): " + exception.toString());
            return null;
        }

        return returnArrayDressInfo;
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
    public static HashMap<String, String> createArrayListDressId(int dressForWho) {
        // Возвращаемый ассоциативный массив
        HashMap<String, String> arrayListDressId = null;

        if(dressForWho != GlobalFlags.DRESS_MAN && dressForWho != GlobalFlags.DRESS_WOMAN && dressForWho != GlobalFlags.DRESS_KID) {
            dressForWho = GlobalFlags.getDressForWho();
        }

        // Считываем многомерный массив, хранящий информацию об одежде, одетой в данный момент на виртуальный манекен
        HashMap<String, ArrayList<HashMap<String, String>>> listCurrentDressInfo = DBMain.getArrayCurrentDressInfo(dressForWho);

        // Если многомерный массив, хранящий информацию об одежде, которая в данный момент
        // присутствует на виртуальном манекене, НЕ пуст
        if (listCurrentDressInfo != null) {
            // Инициализируем ассоциативный массив
            arrayListDressId = new HashMap<>();

            // Извлекаем набор ключей из вышеуказанного массива listDressInfo
            Collection<String> listDressGroupKeyCollection = listCurrentDressInfo.keySet();

            // Массив, содержащий сведения об одежде для текущей группы одежды
            ArrayList<HashMap<String, String>> listDressInfoForCurrentGroup;

            // В цикле перебираем каждую группу одежды
            for (String listDressGroupKey : listDressGroupKeyCollection) {
                // Переменная, ранящая id вещей для текущей группы, которые в данный момент присутствуют на виртуальном манекене
                String stringListDressIdForCurrentGroup = "";

                // Массив, содержащий сведения об одежде для текущей группы одежды
                listDressInfoForCurrentGroup = listCurrentDressInfo.get(listDressGroupKey);

                // В цикле разбираем всю одежду для текущей группы
                if(listDressInfoForCurrentGroup != null) {
                    for (int indexListDressInfoForCurrentGroup = 0; indexListDressInfoForCurrentGroup < listDressInfoForCurrentGroup.size(); indexListDressInfoForCurrentGroup++) {
                        if (listDressInfoForCurrentGroup.get(indexListDressInfoForCurrentGroup) != null) {
                            // Извлекаем id текущей вещи
                            String currentDressId = "0";

                            if (listDressInfoForCurrentGroup.get(indexListDressInfoForCurrentGroup).containsKey(GlobalFlags.TAG_ID)) {
                                if (listDressInfoForCurrentGroup.get(indexListDressInfoForCurrentGroup).get(GlobalFlags.TAG_ID) != null) {
                                    currentDressId = listDressInfoForCurrentGroup.get(indexListDressInfoForCurrentGroup).get(GlobalFlags.TAG_ID);
                                }
                            }

                            stringListDressIdForCurrentGroup += currentDressId;

                            // Если это не последняя вещь в списке для текущей группы, то добавляем
                            // в конец знак разделителя "___"
                            if (indexListDressInfoForCurrentGroup < listDressInfoForCurrentGroup.size() - 1) {
                                stringListDressIdForCurrentGroup += "___";
                            }
                        }
                    }

                    // Добавляем в возвращаемый массив список id одежды, присутствующей на виртуальном манекене, для текущей группы
                    arrayListDressId.put(listDressGroupKey, stringListDressIdForCurrentGroup);
                }
            }
        }

        return arrayListDressId;
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
    public static HashMap<String, String> createArrayListDressId(String dressTypeExclude) {
        // Возвращаемый ассоциативный массив
        HashMap<String, String> arrayListDressId = null;

        if(dressTypeExclude == null) {
            dressTypeExclude = "";
        }

        if (DBMain.getArrayPagerAdapterDressroom() != null && DBMain.getArrayViewPagerDressroom() != null) {
            // В цикле перебираем все типы одежды
            for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                String dressTypeCurrent = GlobalFlags.getArrayTagDressType().get(indexDressType);

                if(!dressTypeCurrent.equals(dressTypeExclude)) {
                    ViewPager currentViewPagerDressroom = null;

                    if (DBMain.getArrayViewPagerDressroom().containsKey(dressTypeCurrent)) {
                        currentViewPagerDressroom = DBMain.getArrayViewPagerDressroom().get(dressTypeCurrent);
                    }

                    PagerAdapterDressroom currentPagerAdapterDressroom = null;

                    if (DBMain.getArrayPagerAdapterDressroom().containsKey(dressTypeCurrent)) {
                        currentPagerAdapterDressroom = DBMain.getArrayPagerAdapterDressroom().get(dressTypeCurrent);
                    }

                    if (currentViewPagerDressroom != null && currentPagerAdapterDressroom != null) {
                        // Считываем параметры для одежды для текущего типа
                        HashMap<String, String> currentItemParams = currentPagerAdapterDressroom.getItemParams(currentViewPagerDressroom.getCurrentItem());

                        if (currentItemParams != null) {
                            if (currentItemParams.containsKey(GlobalFlags.TAG_ID)) {
                                if (currentItemParams.get(GlobalFlags.TAG_ID) != null) {
                                    if (arrayListDressId == null) {
                                        arrayListDressId = new HashMap<>();
                                    }

                                    arrayListDressId.put(GlobalFlags.getArrayTagDressType().get(indexDressType), currentItemParams.get(GlobalFlags.TAG_ID));
                                }
                            }
                        }
                    }
                }
            }
        }

        return arrayListDressId;
    }

    //==============================================================================================
    // Метод для загрузки всей информации, необходимой для первоначальной инициализации приложения
    public static void synchronize() {
        // Запускаем считывание информации о базовых категориях одежды
        // При этом указываем, что после считывания информации о базовых категориях одежды
        // необходимо считать информацию об остальных категориях одежды
        DBMain.synchronizeBaseDressCategories(GlobalFlags.ACTION_GET_ALL_DRESS_CATEGORIES);
    }

    //==============================================================================================
    // Метод для загрузки информации о базовых категориях одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static void synchronizeBaseDressCategories(int nextAction) {
        try {
            if(DBMain.getMySQLGetBaseDressCategories() == null) {
                DBMain.setMySQLGetBaseDressCategories(new MySQLGetBaseDressCategories());
            }

            DBMain.getMySQLGetBaseDressCategories().startGetBaseCategoriesDress(nextAction);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeBaseDressCategories): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для загрузки информации обо всех категориях одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isShowProgressDialogGetAllDressCategories - логическая переменная, определяющая отображать или нет модальное окно загрузки
    // isCheckLocalDB - логическая переменная, определяющая необходимо ли проверять наличие записей
    //                  о категориях одежды в локальной БД
    public static void synchronizeAllDressCategories(int nextAction, Boolean isShowProgressDialogGetAllDressCategories, Boolean isCheckLocalDB) {
        try {
            if(DBMain.getMySQLGetAllDressCategories() == null) {
                DBMain.setMySQLGetAllDressCategories(new MySQLGetAllDressCategories());
            }

            DBMain.getMySQLGetAllDressCategories().startGetAllDressCategories(nextAction, isShowProgressDialogGetAllDressCategories, isCheckLocalDB);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeAllDressCategories): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для загрузки информации обо всех брендах одежды из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static void synchronizeAllDressBrands(int nextAction) {
        try {
            if(DBMain.getMySQLGetAllDressBrands() == null) {
                DBMain.setMySQLGetAllDressBrands(new MySQLGetAllDressBrands());
            }

            DBMain.getMySQLGetAllDressBrands().startGetAllDressBrands(nextAction);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeAllDressBrands): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для загрузки информации об одежде по умолчанию из БД
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isProgressDialogShow - необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
    // textViewError - строка для вывода сообщений об ошибках
    public static void synchronizeDressDefault(int nextAction, Boolean isProgressDialogShow) {
        try {
            if(DBMain.getMySQLGetDressDefault() == null) {
                DBMain.setMySQLGetDressDefault(new MySQLGetDressDefault());
            }

            DBMain.getMySQLGetDressDefault().startGetDressDefault(nextAction, isProgressDialogShow);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeDressDefault): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод, запускающий процесс считывания данных об одежде, просматриваемой в последний раз
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // isProgressDialogShow - логическая переменная, указывающая необходимо ли отображать ProgressDialog в процессе загрузке информации о текущей вещи
    public static void synchronizeGoToDressLastView(int nextAction, Boolean isProgressDialogShow) {
        try {
            if(DBMain.getMySQLGoToDressLastView() == null) {
                DBMain.setMySQLGoToDressLastView(new MySQLGoToDressLastView());
            }

            DBMain.getMySQLGoToDressLastView().startGoToDressLastView(nextAction, isProgressDialogShow);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeGoToDressLastView): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод, запускающий процесс сохранения набора одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // arrayDressListId - массив, хранящий id одежды, информацию о которой необходимо считать
    // buttonDressSave - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static void startDressCollectionSave(int nextAction, String dressCollectionType, HashMap<String, String> arrayDressListId,
                                                ImageView buttonDressSave, Boolean isShowProgressDialogDressCollectionSave) {
        try {
            if(DBMain.getMySQLDressCollectionSave() == null) {
                DBMain.setMySQLDressCollectionSave(new MySQLDressCollectionSave());
            }

            DBMain.getMySQLDressCollectionSave().startDressCollectionSave(nextAction, dressCollectionType, arrayDressListId, buttonDressSave, isShowProgressDialogDressCollectionSave);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (startDressCollectionSave): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод, запускающий процесс удаления информации о текущем наборе одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    // collectionUnSaveId - id коллекции, информацию о которой необходимо удалить из БД для текущего пользователя
    // dressCollectionType - тип текущего сохраняемого набора одежды
    // buttonDressSave - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    // isShowProgressDialogDressCollectionSave - логическая переменная, указывающая отображать или нет
    //                                           модальное окно, отображающее процесс загрузки данных с сервера БД
    public static void startDressCollectionUnSave(int nextAction, int collectionUnSaveId, String dressCollectionType, ImageView buttonDressSave, Boolean isShowProgressDialogDressCollectionSave) {
        try {
            if(DBMain.getMySQLDressCollectionSave() == null) {
                DBMain.setMySQLDressCollectionSave(new MySQLDressCollectionSave());
            }

            DBMain.getMySQLDressCollectionSave().startDressCollectionUnSave(nextAction, collectionUnSaveId, dressCollectionType, buttonDressSave, isShowProgressDialogDressCollectionSave);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (startDressCollectionUnSave): " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод, запускающий процесс считывания данных о текущем отображаемом наборе одежды
    // Передаваемые параметры
    // nextAction  - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public static void synchronizeDressCollectionInfo(int nextAction) {
        try {
            if(DBMain.getMySQLDressCollectionLoad() == null) {
                DBMain.setMySQLDressCollectionLoad(new MySQLDressCollectionLoad());
            }

            DBMain.getMySQLDressCollectionLoad().startDressCollectionLoad(nextAction);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (synchronizeDressCollectionInfo): " + exception.toString());
        }
    }
}
