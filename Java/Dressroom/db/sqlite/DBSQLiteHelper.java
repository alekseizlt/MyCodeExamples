package ru.alexprogs.dressroom.db.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsLog;

/**
* Класс, предназначенный для создания и обновления локальной БД SQLite
*/
public class DBSQLiteHelper extends SQLiteOpenHelper {

    //==============================================================================================
    // Конструктор
    public DBSQLiteHelper(Context context) {
        // Конструктор супер класса (родительского класса)
        // Передаваемые параметры
        // Первый параметр - контекст
        // Второй параметр - название БД
        // Третий параметр - объект Cursor
        // Четвертый параметр - версия БД
        super(context, "Dressroom", null, 1);
    }

    //==============================================================================================
    // Метод для создания БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицу, хранящую информацию о текущем пользователе
        String queryCreateTableUser = "create table if not exists " + GlobalFlags.TAG_TABLE_USER + " (" +
                    "id integer primary key," +                         // id текущего пользователя в удаленной БД
                    "group_id integer DEFAULT 0," +                     // id группы для текущего пользователя
                    "name text DEFAULT NULL," +                         // имя текущего пользователя
                    "surname text DEFAULT NULL," +                      // фамилия текущего пользователя
                    "login text NOT NULL," +                            // логин текущего пользователя
                    "type text NOT NULL DEFAULT 'internal'," +          // тип учетной записи текущего пользователя
                    "mail text DEFAULT NULL," +                         // адрес электронной почты для текущего пользователя
                    "image text DEFAULT NULL," +                        // адрес изображения для текущего пользователя
                    "token text DEFAULT NULL," +                        // токен для текущего пользователя
                    "collections_count integer NOT NULL DEFAULT 0," +   // количество коллекций одежды для текущего пользователя
                    "for_who text NOT NULL DEFAULT 'man'," +            // для кого текущий пользователь предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
                    "profile_url text DEFAULT NULL," +                  // ссылка на профиль текущего пользователя в социальной сети
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущем пользователе
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableUser);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о категориях и количестве одежды,
        // входящей в состав избранных наборов одежды для текущего пользователя
        String queryCreateTableDressInUserCollection = "create table if not exists " + GlobalFlags.TAG_TABLE_DRESS_IN_USER_COLLECTION + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в локальной БД SQLite
                    "catid integer unique," +                           // id текущей категории одежды в удаленной БД
                    "title text NOT NULL," +                            // название текущей категории одежды
                    "alias text DEFAULT NULL," +                        // алиас для названия текущей категории одежды
                    "type text NOT NULL DEFAULT 'head'," +              // тип одежды (головные уборы, обувь и т.д.) для данной категории
                    "dress_count integer NOT NULL DEFAULT 0," +         // количество вещей (одежды) для текущей категории
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableDressInUserCollection);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о категориях одежды
        String queryCreateTableCategories = "create table if not exists " + GlobalFlags.TAG_TABLE_CATEGORIES + " (" +
                    "id integer primary key," +                         // id текущей категории одежды в удаленной БД
                    "title text NOT NULL," +                            // название текущей категории одежды
                    "alias text DEFAULT NULL," +                        // алиас для названия текущей категории одежды
                    "for_who text NOT NULL DEFAULT 'man'," +            // для кого предназначена одежда (для мужчины, для женщины или для детей) из данной категории
                    "type text NOT NULL DEFAULT 'head'," +              // тип одежды (головные уборы, обувь и т.д.) из данной категории
                    "level integer NOT NULL DEFAULT 0," +               // уровень вложенности текущей категории одежды
                    "parent_id integer NOT NULL DEFAULT 0," +           // id родительской категории для текущей категории одежды
                    "description text DEFAULT NULL," +                  // описание текущей категории одежды
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущей категории одежды
                    "dress_count integer NOT NULL DEFAULT 0," +         // количество вещей (одежды) для текущей категории
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCategories);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию об одежде
        String queryCreateTableDress = "create table if not exists " + GlobalFlags.TAG_TABLE_DRESS + " (" +
                    "id integer primary key," +                         // id текущей вещи в удаленной БД
                    "catid integer NOT NULL DEFAULT 0," +				// id категории, к которой относится текущая вещь (одежда)
                    "title text NOT NULL," +                            // название текущей вещи (одежды)
                    "alias text DEFAULT NULL," +                        // алиас для названия текущей вещи (одежды)
                    "for_who text NOT NULL DEFAULT 'man'," +            // для кого предназначена данная вещь (одежда) (для мужчины, для женщины или для детей)
                    "type text NOT NULL DEFAULT 'head'," +              // тип текущей вещи (одежды) (для мужчины, для женщины или для детей)
                    "brand_id integer NOT NULL DEFAULT 0," +            // id бренда, к которому относится текущая вещь (одежда)
                    "thumb text DEFAULT NULL," +                        // ссылка на файл-превью-изображение для текущей вещи (одежды)
                    "thumb_width integer NOT NULL DEFAULT 0," +         // ширина файла-превью-изображения для текущей вещи (одежды)
                    "thumb_height integer NOT NULL DEFAULT 0," +        // высота файла-превью-изображения для текущей вещи (одежды)
                    "image text DEFAULT NULL," +                        // ссылка на файл-изображение для текущей вещи (одежды)
                    "image_width integer NOT NULL DEFAULT 0," +         // ширина (в пикселях) файла-изображение для текущей вещи (одежды)
                    "image_height integer NOT NULL DEFAULT 0," +        // высота (в пикселях) файла-изображение для текущей вещи (одежды)
                    "image_back text DEFAULT NULL," +                   // ссылка на файл-изображение для задней (тыловой) стороны текущей вещи (одежды)
                    "image_back_width integer NOT NULL DEFAULT 0," +    // ширина (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                    "image_back_height integer NOT NULL DEFAULT 0," +   // высота (в пикселях) файла-изображение для задней (тыловой) стороны текущей вещи (одежды)
                    "color text NOT NULL," +                            // цвет текущей вещи (одежды)
                    "style text DEFAULT NULL," +                        // стиль текущей вещи (одежды)
                    "short_description text DEFAULT NULL," +            // краткое описание текущей вещи (одежды)
                    "description text DEFAULT NULL," +                  // описание текущей вещи (одежды)
                    "hits integer NOT NULL DEFAULT 0," +                // рейтинг текущей вещи (одежды)
                    "dress_default integer NOT NULL DEFAULT 0," +       // логическая переменная, определяющая отображать ли данную вещь по умолчанию при первоначальном заходе пользователя на страницу
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущей одежде
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableDress);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о соответствии id вещи (одежды)
        // и ссылки на соответствующее данной вещи (одежде) дополнительное изображению
        String queryCreateTableDressImage = "create table if not exists " + GlobalFlags.TAG_TABLE_DRESS_IMAGE + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в таблице в удаленной БД
                    "dress_id integer NOT NULL," +                      // id текущей вещи (одежды)
                    "image text NOT NULL," +                            // ссылка на файл-изображение для текущей вещи (одежды)
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableDressImage);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о брендах одежды
        String queryCreateTableBrand = "create table if not exists " + GlobalFlags.TAG_TABLE_BRAND + " (" +
                    "id integer primary key," +                         // id текущего бренда одежды в удаленной БД
                    "title text NOT NULL," +                            // название текущего бренда одежды
                    "alias text DEFAULT NULL," +                        // алиас названия текущего бренда одежды
                    "image text DEFAULT NULL," +                        // изображение для текущего бренда
                    "short_description text DEFAULT NULL," +            // краткое описание текущего бренда одежды
                    "description text DEFAULT NULL," +                  // описание текущего бренда одежды
                    "hits integer NOT NULL DEFAULT 0," +                // рейтинг текущего бренда одежды
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущей одежде
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableBrand);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о соответствии id бренда одежды
        // и ссылки на соответствующее данной вещи (одежде) дополнительное изображению
        String queryCreateTableBrandImage = "create table if not exists " + GlobalFlags.TAG_TABLE_BRAND_IMAGE + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в таблице в удаленной БД
                    "brand_id integer NOT NULL," +                      // id текущего бренда одежды
                    "image text NOT NULL," +                            // ссылка на файл-изображение для текущей вещи (одежды)
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableBrandImage);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о компаниях-производителях одежды
        String queryCreateTableCompany = "create table if not exists " + GlobalFlags.TAG_TABLE_COMPANY + " (" +
                    "id integer primary key," +                         // id текущей компании-производителя одежды в удаленной БД
                    "title text NOT NULL," +                            // название текущей компании-производителя одежды
                    "alias text DEFAULT NULL," +                        // алиас названия текущей компании-производителя одежды
                    "image text DEFAULT NULL," +                        // изображение-логотип для текущей компании-производителя одежды
                    "short_description text DEFAULT NULL," +            // краткое описание текущей компании-производителя одежды
                    "description text DEFAULT NULL," +                  // описание текущей компании-производителя одежды
                    "hits integer NOT NULL DEFAULT 0," +                // рейтинг текущей компании-производителя одежды
                    "postcode text DEFAULT NULL," +                     // почтовый индекс месторасположения головного офиса текущей компании-производителя одежды
                    "country text DEFAULT NULL," +                      // страна месторасположения головного офиса текущей компании-производителя одежды
                    "country_code text DEFAULT NULL," +                 // код страны месторасположения головного офиса текущей компании-производителя одежды
                    "region text DEFAULT NULL," +                       // регион (область, штат) месторасположения головного офиса текущей компании-производителя одежды
                    "city text DEFAULT NULL," +                         // город месторасположения головного офиса текущей компании-производителя одежды
                    "street text DEFAULT NULL," +                       // улица месторасположения головного офиса текущей компании-производителя одежды
                    "building text DEFAULT NULL," +                     // номер дома (здания) месторасположения головного офиса текущей компании-производителя одежды
                    "telephone text DEFAULT NULL," +                    // номера телефонов головного офиса текущей компании-производителя одежды
                    "mobile text DEFAULT NULL," +                       // мобильные номера телефонов головного офиса текущей компании-производителя одежды
                    "fax text DEFAULT NULL," +                          // номер факса головного офиса текущей компании-производителя одежды
                    "webpage text DEFAULT NULL," +                      // адрес сайта текущей компании-производителя одежды
                    "email_to text DEFAULT NULL," +                     // адрес электронной почты текущей компании-производителя одежды
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущей компании-производителе одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCompany);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о соответствии id компании-производителя одежды
        // и ссылки на соответствующее данной вещи (одежде) дополнительное изображению
        String queryCreateTableCompanyImage = "create table if not exists " + GlobalFlags.TAG_TABLE_COMPANY_IMAGE + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в таблице в локальной БД
                    "company_id integer NOT NULL," +                    // id текущей компании-производетля одежды
                    "image text NOT NULL," +                            // ссылка на файл-изображение для текущей компании-производителя одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCompanyImage);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о соответствии бренда одежды определенной компании-производителю одежды
        String queryCreateTableCompanyBrand = "create table if not exists " + GlobalFlags.TAG_TABLE_COMPANY_BRAND + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в локальной БД
                    "company_id integer NOT NULL," +                    // id текущей компании-производителя одежды
                    "brand_id integer NOT NULL," +                      // id текущего бренда одежды, выпускаемого данной компанией-производителем одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCompanyBrand);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о магазинах, продающих тот или иной бренд
        String queryCreateTableShop = "create table if not exists " + GlobalFlags.TAG_TABLE_SHOP + " (" +
                    "id integer primary key," +                         // id текущего магазина одежды в удаленной БД
                    "title text NOT NULL," +                            // название текущего магазина одежды
                    "alias text DEFAULT NULL," +                        // алиас названия текущего магазина одежды
                    "image text DEFAULT NULL," +                        // изображение-логотип для текущего магазина одежды
                    "short_description text DEFAULT NULL," +            // краткое описание текущего магазина одежды
                    "description text DEFAULT NULL," +                  // описание текущего магазина одежды
                    "hits integer NOT NULL DEFAULT 0," +                // рейтинг текущего магазина одежды
                    "latitude real DEFAULT NULL," +                     // широта местоположения текущего магазина одежды
                    "longitude real DEFAULT NULL," +                    // долгота местоположения текущего магазина одежды
                    "postcode text DEFAULT NULL," +                     // почтовый индекс месторасположения текущего магазина одежды
                    "country text DEFAULT NULL," +                      // страна месторасположения текущего магазина одежды
                    "country_code text DEFAULT NULL," +                 // код страны месторасположения текущего магазина одежды
                    "region text DEFAULT NULL," +                       // регион (область, штат) месторасположения текущего магазина одежды
                    "city text DEFAULT NULL," +                         // город месторасположения текущего магазина одежды
                    "street text DEFAULT NULL," +                       // улица месторасположения текущего магазина одежды
                    "building text DEFAULT NULL," +                     // номер дома (здания) месторасположения текущего магазина одежды
                    "telephone text DEFAULT NULL," +                    // номера телефонов текущего магазина одежды
                    "mobile text DEFAULT NULL," +                       // мобильные номера телефонов текущего магазина одежды
                    "fax text DEFAULT NULL," +                          // номер факса текущего магазина одежды
                    "webpage text DEFAULT NULL," +                      // адрес сайта текущего магазина одежды
                    "email_to text DEFAULT NULL," +                     // адрес электронной почты текущего магазина одежды
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущем магазине одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableShop);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую информацию о соответствии id бренда одежды
        // и ссылки на соответствующее данной вещи (одежде) дополнительное изображению
        String queryCreateTableShopImage = "create table if not exists " + GlobalFlags.TAG_TABLE_SHOP_IMAGE + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в таблице в локальной БД
                    "shop_id integer NOT NULL," +                       // id текущего магазина одежды
                    "image text NOT NULL," +                            // ссылка на файл-изображение для текущей вещи (одежды)
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableShopImage);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о соответствии id магазина и id бренда, продаваемого данным магазином
        String queryCreateTableShopBrand = "create table if not exists " + GlobalFlags.TAG_TABLE_SHOP_BRAND + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в удаленной БД
                    "shop_id integer NOT NULL," +                       // id текущего магазина одежды
                    "brand_id integer NOT NULL," +                      // id текущего бренда одежды, продаваемого данным магазином
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableShopBrand);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о соответствии id магазина и id одежды, продаваемой данным магазином
        String queryCreateTableShopDress = "create table if not exists " + GlobalFlags.TAG_TABLE_SHOP_DRESS + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в удаленной БД
                    "shop_id integer NOT NULL," +                       // id текущего магазина одежды
                    "dress_id integer NOT NULL," +                      // id текущей одежды, продаваемой данным магазином
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableShopDress);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о наборах одежды, сохраненной тем или иным пользователем
        // в своем личном кабинете для дальнейшего просмотра
        String queryCreateTableCollection = "create table if not exists " + GlobalFlags.TAG_TABLE_COLLECTION + " (" +
                    "id integer primary key," +                         // id текущего набора одежды в удаленной БД
                    "title text DEFAULT NULL," +                        // название текущего набора одежды
                    "alias text DEFAULT NULL," +                        // алиас названия текущего набора одежды
                    "type text DEFAULT 'collection'," +                 // тип текущего набора одежды (коллекция или одежда)
                    "short_description text DEFAULT NULL," +            // краткое описание текущего набора одежды
                    "description text DEFAULT NULL," +                  // описание текущего набора одежды
                    "version integer NOT NULL DEFAULT 1," +             // версия информации о текущем наборе одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCollection);

        //------------------------------------------------------------------------------------------
        // Создаем таблицу, хранящую сведения о соответствии id набора одежды
        // и id вещи (одежды), входящей в состав данного набора одежды
        String queryCreateTableCollectionDress = "create table if not exists " + GlobalFlags.TAG_TABLE_COLLECTION_DRESS + " (" +
                    "id integer primary key autoincrement," +           // id текущей записи в удаленной БД
                    "collection_id integer NOT NULL," +                 // id текущего набора одежды
                    "dress_id integer NOT NULL," +                      // id текущей вещи (одежды), входящей в состав данного набора одежды
                    "record_number integer NOT NULL DEFAULT 1" +        // порядковый номер текущей записи в таблице в локальной БД SQLite
                ");";

        db.execSQL(queryCreateTableCollectionDress);
    }

    //==============================================================================================
    // Метод для обновления БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //==============================================================================================
    // Метод для считывания строк из таблицы в БД MySQL на сервере согласно условиям выборки
    // Передаваемые параметры
    // tableName - название таблицы
    // columns - список названий полей, значения которых необходимо считать из таблицы для соответствующей строки
    // selection - условие выборки
    // selectionArgs - массив аргументов для условия выборки
    // limit количество строк, которые необходимо считать из локальной БД
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public ArrayList<HashMap<String, String>> getRecordsFromDB(String tableName, String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
        // Если не передано название таблицы
        if(tableName == null) {
            return null;
        }

        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        ArrayList<HashMap<String, String>> objectFromDB = null;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String groupBy = null;
            String having = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        objectFromDB = new ArrayList<>();

                        do {
                            HashMap<String, String> mapCurrentRow = new HashMap<>();

                            // В цикле перебираем все столбцы, присутствующие в БД
                            for (String columnName : cursor.getColumnNames()) {
                                mapCurrentRow.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                            }

                            // Добавляем текущую строку в общий массив
                            objectFromDB.add(mapCurrentRow);
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return null;
                }
            }
            // Иначе возвращаем null в качестве результат выполнения текущей функции
            else {
                return null;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getRecordsFromDB): " + exception.toString());
            return null;
        }

        return objectFromDB;
    }

    //==============================================================================================
    // Метод для считывания строки из таблицы по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // columns - список названий полей, значения которых необходимо считать из таблицы для соответствующей строки
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public HashMap<String, String> getRecordFromDBByIdServerMySQL(String tableName, String idServerMySQL, String[] columns) {
        // Если не передано название таблицы или не передано id данной записи в удаленной БД
        if(tableName == null || idServerMySQL == null) {
            return null;
        }

        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return null;
        }

        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        HashMap<String, String> objectFromDB = null;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String selection = GlobalFlags.TAG_ID + " = ?";
            String[] selectionArgs = new String[]{idServerMySQL};
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = "1";

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        objectFromDB = new HashMap<>();

                        // В цикле перебираем все столбцы, присутствующие в БД
                        for (String columnName : cursor.getColumnNames()) {
                            objectFromDB.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                        }
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return null;
                }
            }
            // Иначе возвращаем null в качестве результат выполнения текущей функции
            else {
                return null;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getRecordFromDBByIdServer): " + exception.toString());
            return null;
        }

        return objectFromDB;
    }

    //==============================================================================================
    // Метод для считывания одной (первой) строки из таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // columns - список названий полей, значения которых необходимо считать из таблицы для соответствующей строки
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public HashMap<String, String> getFirstRecordFromDB(String tableName, String[] columns) {
        // Если не передано название таблицы
        if(tableName == null) {
            return null;
        }

        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        HashMap<String, String> objectFromDB = null;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = "1";

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        objectFromDB = new HashMap<>();

                        // В цикле перебираем все столбцы, присутствующие в БД
                        for (String columnName : cursor.getColumnNames()) {
                            objectFromDB.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                        }
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return null;
                }
            }
            // Иначе возвращаем null в качестве результат выполнения текущей функции
            else {
                return null;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getFirstRecordFromDB): " + exception.toString());
            return null;
        }

        return objectFromDB;
    }

    //==============================================================================================
    // Метод для считывания всех строк из таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // columns - список названий полей, значения которых необходимо считать из таблицы для соответствующей строки
    // orderBy - сортировка столбцов
    // Возращаемое значение
    // objectFromDB - ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
    public ArrayList<HashMap<String, String>> getAllRecordsFromDB(String tableName, String[] columns, String orderBy) {
        // Если не передано название таблицы
        if(tableName == null) {
            return null;
        }

        // Возвращаемый ассоциативный массив, содержащий сведени, выбранные из БД согласно условиям выборки
        ArrayList<HashMap<String, String>> objectFromDB = null;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    if (cursor.moveToFirst()) {
                        objectFromDB = new ArrayList<>();

                        do {
                            HashMap<String, String> mapCurrentRow = new HashMap<>();

                            // В цикле перебираем все столбцы, присутствующие в БД
                            for (String columnName : cursor.getColumnNames()) {
                                mapCurrentRow.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                            }

                            // Добавляем текущую строку в общий массив
                            objectFromDB.add(mapCurrentRow);
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return null;
                }
            }
            // Иначе возвращаем null в качестве результат выполнения текущей функции
            else {
                return null;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getAllRecordsFromDB): " + exception.toString());
            return null;
        }

        return objectFromDB;
    }

    //==============================================================================================
    // Метод для считывания версии информации о текущей записи из таблицы по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возращаемое значение
    // version - версии информации о текущей записи из таблицы по id данной строки в БД MySQL на сервере
    public int getRecordVersionFromDBByIdServerMySQL(String tableName, String idServerMySQL) {
        // Если не передано название таблицы или не передано id данной записи в удаленной БД
        if(tableName == null || idServerMySQL == null) {
            return 0;
        }

        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return 0;
        }

        // Возвращаемая переменная, содержащая версию информации о текущей записи из таблицы
        // по id данной строки в БД MySQL на сервере
        int version = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{GlobalFlags.TAG_VERSION};
            String selection = GlobalFlags.TAG_ID + " = ?";
            String[] selectionArgs = new String[]{idServerMySQL};
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = "1";

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        version = cursor.getInt(cursor.getColumnIndex(GlobalFlags.TAG_VERSION));
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return 0;
                }
            }
            // Иначе возвращаем null в качестве результат выполнения текущей функции
            else {
                return 0;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getRecordVersionFromDBByIdServer): " + exception.toString());
            return 0;
        }

        return version;
    }

    //==============================================================================================
    // Метод для определения минимального значения порядкового номера присутствующей в данной таблице строки
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // minRecordNumber - минимальное значение порядкового номера присутствующей в данной таблице строки
    public int getMinRecordNumber(String tableName) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Возвращаемый результат, минимальное значение порядкового номера присутствующей в данной таблице строки
        Integer minRecordNumber = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.trim().toLowerCase();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{"min(record_number) as min_record_number"};
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        String minRecordNumberString = cursor.getString(cursor.getColumnIndex("min_record_number"));

                        if( minRecordNumberString != null ) {
                            minRecordNumber = Integer.parseInt(minRecordNumberString);
                        }
                        else {
                            minRecordNumber = 0;
                        }
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return 0;
                }
            }
            // Иначе возвращаем 0 в качестве результат выполнения текущей функции
            else {
                return 0;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getMinRecordNumber): " + exception.toString());
            return 0;
        }

        return minRecordNumber;
    }

    //==============================================================================================
    // Метод для определения максимального значения порядкового номера присутствующей в данной таблице строки
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // maxRecordNumber - максимальное значение порядкового номера присутствующей в данной таблице строки
    public int getMaxRecordNumber(String tableName) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Возвращаемый результат, максимальное значение порядкового номера присутствующей в данной таблице строки
        Integer maxRecordNumber = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.trim().toLowerCase();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{"max(record_number) as max_record_number"};
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            // Если количество строк больше 0, то считаем, что результат найден
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Перемещаемся к первой записи в результате
                    // (при этом данная запись является в то же время и единственной)
                    if (cursor.moveToFirst()) {
                        String maxRecordNumberString = cursor.getString(cursor.getColumnIndex("max_record_number"));

                        if( maxRecordNumberString != null ) {
                            maxRecordNumber = Integer.parseInt(maxRecordNumberString);
                        }
                        else {
                            maxRecordNumber = 0;
                        }
                    }

                    cursor.close();
                }
                else {
                    cursor.close();
                    return 0;
                }
            }
            // Иначе возвращаем 0 в качестве результат выполнения текущей функции
            else {
                return 0;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getMaxRecordNumber): " + exception.toString());
            return 0;
        }

        return maxRecordNumber;
    }

    //==============================================================================================
    // Метод для определения количетсва строк из таблицы id данных строк в БД MySQL на сервере,
    // которых равен значения, переданному в качестве параметра idServerMySQL для данной функции
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public int getCountRecordFromDBByIdServerMySQL(String tableName, String idServerMySQL) {
        // Если не передано название таблицы или
        // Если не задано значение id данной строки в БД MySQL на сервере,
        // то возвращаем 0 в качестве результата выполнения данной функции
        if(tableName == null || idServerMySQL == null) {
            return 0;
        }

        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return 0;
        }

        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        int countRows = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        // Подключаемся к БД
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{GlobalFlags.TAG_ID};
            String selection = GlobalFlags.TAG_ID + " = ?";
            String[] selectionArgs = new String[]{idServerMySQL};
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            if (cursor != null) {
                countRows = cursor.getCount();

                cursor.close();
            }
        }
       catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getCountRecordFromDBByIdServer): " + exception.toString());
            return 0;
        }

        return countRows;
    }

    //==============================================================================================
    // Метод для определения количетсва строк из таблицы, удовлетворяющих условиям выборки
    // Передаваемые параметры
    // tableName - название таблицы
    // selection - условие выборки
    // selectionArgs - массив аргументов для условия выборки
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public int getCountRecordFromDB(String tableName, String selection, String[] selectionArgs) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        int countRows = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        // Подключаемся к БД
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{GlobalFlags.TAG_ID};
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            if (cursor != null) {
                countRows = cursor.getCount();

                cursor.close();
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getCountRecordFromDB): " + exception.toString());
            return 0;
        }

        return countRows;
    }

    //==============================================================================================
    // Метод для определения общего количества строк из таблицы
    // Передаваемые параметры
    // tableName - название таблицы
    // Возращаемое значение
    // countRows - количество строк, удовлетворяющих условиям поиска
    public int getTotalCountRecord(String tableName) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Возвращаемый результат, количество строк, удовлетворяющих условиям выборки
        int countRows = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getReadableDatabase();

            // Считываем из необходимой таблицы необходимые строки
            String[] columns = new String[]{GlobalFlags.TAG_ID};
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = GlobalFlags.TAG_ID;
            String limit = null;

            Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            // Определяем сколько строк содержит результат выборки из БД
            if (cursor != null) {
                countRows = cursor.getCount();

                cursor.close();
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (getTotalCountRecord): " + exception.toString());
            return 0;
        }

        return countRows;
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
    public long insertRecordToDB(String tableName, HashMap<String, String> insertFieldsValues) {
        // Если не передано название таблицы
        if(tableName == null || insertFieldsValues == null) {
            return 0;
        }

        // id новой добавленой в таблицу строки
        long newRowId = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Проверяем существует ли такая запись уже в таблице
            // Определять наличие будем по полю "id", хранящем id данной записи в БД MySQL на сервере
            int countRows = 0;

            if(insertFieldsValues.containsKey(GlobalFlags.TAG_ID)) {
                if(insertFieldsValues.get(GlobalFlags.TAG_ID) != null) {
                    countRows = this.getCountRecordFromDBByIdServerMySQL(tableName, insertFieldsValues.get(GlobalFlags.TAG_ID));
                }
            }

            // Добавляем новую строку в БД только в случае отсутствия данной строки в БД
            if (countRows <= 0) {
                // Определяем общее количество строк в данной таблице
                int totalCountRecordInCurrentTable = this.getTotalCountRecord(tableName);

                // Подключаемся к БД
                SQLiteDatabase db = this.getWritableDatabase();

                //----------------------------------------------------------------------------------
                // Если количество строк в данной таблице превысило допустимое число,
                // то удаляем строки с наименьшим значением порядкового номера
                if(GlobalFlags.getArrayMaxCountRowsInTable() != null) {
                    if (GlobalFlags.getArrayMaxCountRowsInTable().containsKey(tableName)) {
                        if (totalCountRecordInCurrentTable >= GlobalFlags.getArrayMaxCountRowsInTable().get(tableName)) {
                            // Определяем минимальное значение порядкового номера строки в данной таблице
                            int minRecordNumberInCurrentTable = this.getMinRecordNumber(tableName);

                            // Удаляем из текущий таблицы строки с минимальным значением порядкового номера строки
                            String whereCause = "record_number = ?";
                            String[] whereArgs = new String[]{String.valueOf(minRecordNumberInCurrentTable)};
                            int countRowsDelete = db.delete(tableName, whereCause, whereArgs);
                        }
                    }
                }

                //----------------------------------------------------------------------------------
                // Считываем названия всех полей (список ключей из ассоциативного массива insertFieldsValues
                Collection<String> insertFieldsCollection = insertFieldsValues.keySet();

                ContentValues contentValues = new ContentValues();

                // В цикле перебираем все поля, значения которых необходимо вставить в БД
                for (String currentInsertField : insertFieldsCollection) {
                    contentValues.put(currentInsertField, insertFieldsValues.get(currentInsertField));
                }

                // Определяем максимальное значение порядкового номера строки в данной таблице
                int maxRecordNumberInCurrentTable = this.getMaxRecordNumber(tableName);

                // Задаем порядковый номер для текущей добавляемой строки на +1 больше,
                // чем максимальное значение порядкового номера строки в данной таблице
                int recordNumberForCurrentRow = 1;

                if(maxRecordNumberInCurrentTable > 0) {
                    recordNumberForCurrentRow = maxRecordNumberInCurrentTable + 1;
                }

                contentValues.put("record_number", String.valueOf(recordNumberForCurrentRow));

                // Вставляем строку в таблицу
                newRowId = db.insert(tableName, null, contentValues);
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (insertRecordToDB): " + exception.toString());
            return 0;
        }

        return newRowId;
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
    public int updateRecordInDBByIdServerMySQL(String tableName, String idServerMySQL, HashMap<String, String> updateFieldsValues) {
        // Если не передано название таблицы или не перадн id данной записи в удаленной БД
        if(tableName == null || idServerMySQL == null) {
            return 0;
        }

        // Если переданное в функцию значение id данной строки в БД MySQL на сервере,
        // то возвращаем 0 в качестве результат выполнения функции
        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return 0;
        }

        // Количество обновленных в БД строк
        int countRowsUpdate = 0;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            if (updateFieldsValues.containsKey(GlobalFlags.TAG_VERSION)) {
                if (updateFieldsValues.get(GlobalFlags.TAG_VERSION) != null) {
                    // Считываем версию информации о текущей записи из БД
                    String versionFromDB = String.valueOf(this.getRecordVersionFromDBByIdServerMySQL(tableName, idServerMySQL));

                    // Если значение версии информации из ассоциативного массива updateFieldsValues
                    // отличается от значения версии информации о текущей записи, считанной из БД,
                    // то выполняем обновление информации

                    if (!versionFromDB.equals(updateFieldsValues.get(GlobalFlags.TAG_VERSION))) {
                        // Подключаемся к БД
                        SQLiteDatabase db = this.getWritableDatabase();

                        // Считываем названия всех полей (список ключей из ассоциативного массива insertFieldsValues)
                        Collection<String> updateFieldsCollection = updateFieldsValues.keySet();

                        // Значения, которые должны быть обновлены в таблице
                        ContentValues contentValues = new ContentValues();

                        // В цикле перебираем все поля, значения которых необходимо обновить в БД
                        for (String currentUpdateField : updateFieldsCollection) {
                            contentValues.put(currentUpdateField, updateFieldsValues.get(currentUpdateField));
                        }

                        // Обновляем строку в таблицу
                        String whereCause = GlobalFlags.TAG_ID + " = ?";
                        String[] whereArgs = new String[]{idServerMySQL};
                        countRowsUpdate = db.update(tableName, contentValues, whereCause, whereArgs);
                    }
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (updateRecordInDBByIdServer): " + exception.toString());
            return 0;
        }

        return countRowsUpdate;
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
    public HashMap<String, String> updateOrInsertRecordToDBByIdServerMySQL(String tableName, String idServerMySQL, HashMap<String, String> updateOrInsertFieldsValues) {
        // Если не передано название таблицы или не перадн id данной записи в удаленной БД
        if(tableName == null || idServerMySQL == null) {
            return null;
        }

        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return null;
        }

        // Возвращаемый ассоциативный массив
        HashMap<String, String> resultArrayUpdateOrInsert = new HashMap<>();

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.trim().toLowerCase();

        try {
            // Проверяем существует ли такая запись уже в таблице

            // Если данная строка уже присутствует в БД, то обновляем ее
            if (this.getCountRecordFromDBByIdServerMySQL(tableName, idServerMySQL) > 0) {
                // Количество обновленных строк в БД
                int countRowsUpdate = this.updateRecordInDBByIdServerMySQL(tableName, idServerMySQL, updateOrInsertFieldsValues);

                // Заполняем возвращаемый ассоциативный массив
                resultArrayUpdateOrInsert.put("task", "update");
                resultArrayUpdateOrInsert.put("count_or_id", String.valueOf(countRowsUpdate));
            }
            // Иначе, вставляем данную строку в БД
            else {
                // Вставляем строку в таблицу
                long newRowId = this.insertRecordToDB(tableName, updateOrInsertFieldsValues);

                // Заполняем возвращаемый ассоциативный массив
                resultArrayUpdateOrInsert.put("task", "insert");
                resultArrayUpdateOrInsert.put("count_or_id", String.valueOf(newRowId));
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (updateOrInsertRecordToDBByIdServer): " + exception.toString());
            return null;
        }

        return resultArrayUpdateOrInsert;
    }

    //==============================================================================================
    // Метод для удаления строки из таблицы, найденной по id данной строки в БД MySQL на сервере
    // Передаваемые параметры
    // tableName - название таблицы
    // idServerMySQL - значение id данной строки в БД MySQL на сервере
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из БД строк
    public int deleteRecordFromDBByIdServerMySQL(String tableName, String idServerMySQL) {
        // Если не передано название таблицы или не перадн id данной записи в удаленной БД
        if(tableName == null || idServerMySQL == null) {
            return 0;
        }

        if(idServerMySQL.trim().equalsIgnoreCase("")) {
            return 0;
        }

        // Количество удаленных из БД строк
        int countRowsDelete;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getWritableDatabase();

            // Удаляем строку из таблицы
            String whereCause = GlobalFlags.TAG_ID + " = ?";
            String[] whereArgs = new String[]{idServerMySQL};
            countRowsDelete = db.delete(tableName, whereCause, whereArgs);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (deleteRecordFromDBByIdServer): " + exception.toString());
            return 0;
        }

        return countRowsDelete;
    }

    //==============================================================================================
    // Метод для удаления строки из таблицы
    // Передаваемые параметры
    // tableName - название таблицы
    // whereCause - названия столбцов, по которым необходимо выполнять выборку
    // whereArgs - значения вышеуказанных столбцов, по которым необходимо выполнять выборку
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из БД строк
    public int deleteRecordFromDB(String tableName, String whereCause, String[] whereArgs) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Количество удаленных из БД строк
        int countRowsDelete;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getWritableDatabase();

            // Удаляем строку из таблицы
            countRowsDelete = db.delete(tableName, whereCause, whereArgs);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (deleteRecordFromDB): " + exception.toString());
            return 0;
        }

        return countRowsDelete;
    }

    //==============================================================================================
    // Метод для очистки таблицы в локальной БД SQLite
    // Передаваемые параметры
    // tableName - название таблицы
    // Возвращаемое значение
    // countRowsDelete - количество удаленных из таблицы строк
    public int clearTable(String tableName) {
        // Если не передано название таблицы
        if(tableName == null) {
            return 0;
        }

        // Количество удаленных из таблицы строк
        int countRowsDelete;

        // Удаляем начальные и конечные пробелы из названия таблицы
        tableName = tableName.toLowerCase().trim();

        try {
            // Подключаемся к БД
            SQLiteDatabase db = this.getWritableDatabase();

            // Очищаем таблицу
            countRowsDelete = db.delete(tableName, null, null);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("DB SQLite Error (clearTable): " + exception.toString());
            return 0;
        }

        return countRowsDelete;
    }
}