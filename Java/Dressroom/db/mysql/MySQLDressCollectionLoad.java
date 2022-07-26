package ru.alexprogs.dressroom.db.mysql;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.MainActivity;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.UserDetails;
import ru.alexprogs.dressroom.adapter.PagerAdapterDressCollection;
import ru.alexprogs.dressroom.db.sqlite.AsyncTaskLoadDressCollectionInfoFromLocalDB;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.httppostrequest.HttpGetPostRequest;
import ru.alexprogs.dressroom.lib.FunctionsConnection;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.FunctionsString;

// Класс для загрузки информации о наборах одежды (коллекциях) для текущего пользователя
public class MySQLDressCollectionLoad {
    // Свойства данного класса
    private ProgressDialog mProgressDialogDressCollectionLoad;  // ссылка на модальное окно, отображающее процесс загрузки данных с сервера БД
    private int mNextAction;                                    // флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    private String mDressCategoryId;                            // id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    private String mDressCategoryTitle;                         // название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя

    //==============================================================================================
    // Конструктор
    public MySQLDressCollectionLoad() {
    }

    //==============================================================================================
    // Метод для считывания ссылки на модальное окно, отображающее процесс загрузки данных с сервера БД
    private ProgressDialog getProgressDialogDressCollectionLoad() {
        return this.mProgressDialogDressCollectionLoad;
    }

    //==============================================================================================
    // Метод для задания ссылки на модальное окно, отображающее процесс загрузки данных с сервера БД
    private void setProgressDialogDressCollectionLoad(ProgressDialog progressDialogDressCollectionLoad) {
        this.mProgressDialogDressCollectionLoad = progressDialogDressCollectionLoad;
    }

    //==============================================================================================
    // Метод для считывания флага, определяющего какое действие будет выполнено после выполнения
    // текущей асинхронной операции
    private int getNextAction() {
        return this.mNextAction;
    }

    //==============================================================================================
    // Метод для задания флага, определяющего какое действие будет выполнено после выполнения
    // текущей асинхронной операции
    private void setNextAction(int nextAction) {
        this.mNextAction = nextAction;
    }

    //==============================================================================================
    // Метод для считывания id категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    public String getDressCategoryId() {
        return this.mDressCategoryId;
    }

    //==============================================================================================
    // Метод для задания id категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    private void setDressCategoryId(String dressCategoryId) {
        this.mDressCategoryId = dressCategoryId;
    }

    //==============================================================================================
    // Метод для считывания названия категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    private String getDressCategoryTitle() {
        return this.mDressCategoryTitle;
    }

    //==============================================================================================
    // Метод для задания id категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    private void setDressCategoryTitle(String dressCategoryTitle) {
        this.mDressCategoryTitle = dressCategoryTitle;
    }

    //==============================================================================================
    // Метод для установки параметров
    // Передаваемые параметры
    // dressCategoryId - id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    // dressCategoryTitle - название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    public void setParams(String dressCategoryId, String dressCategoryTitle) {
        // Задаем id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
        this.setDressCategoryId(dressCategoryId);

        // Задаем название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
        this.setDressCategoryTitle(dressCategoryTitle);
    }

    //==============================================================================================
    // Метод, запускающий процесс считывания данных о наборе одежды
    // Передаваемые параметры
    // nextAction - флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
    public void startDressCollectionLoad(int nextAction) {
        // Задаем флаг, определяющий какое действие будет выполнено после выполнения текущей асинхронной операции
        this.setNextAction(nextAction);

        // Проверяем наличие Интернет-соединения
        Boolean isInternetConnection = FunctionsConnection.isInternetConnection();

        // Если Интернет-соединение присутствует, то загружаем данные о коллекциях одежды пользователя
        // из удаленной БД
        if(isInternetConnection.equals(true)) {
            AsyncTaskLoadDressCollection asyncTaskLoadDressCollection = new AsyncTaskLoadDressCollection();
            asyncTaskLoadDressCollection.execute();
        }
        // Иначе, если Интернет-соединение отсутствует, то загружаем данные о коллекциях одежды
        // из локальной БД
        else {
            // Выводим уведомление о том, что отсутствует Интернет-соединение
            Toast toastNoInternetConnection = Toast.makeText(DBMain.getContext(), R.string.string_no_internet_connection_toast, Toast.LENGTH_SHORT);
            toastNoInternetConnection.setGravity(Gravity.CENTER, 0, 0);
            toastNoInternetConnection.show();

            //--------------------------------------------------------------------------------------
            // Загружаем непосредственно информацию о коллекциях одежды для текущего пользователя
            // из локальной БД
            AsyncTaskLoadDressCollectionInfoFromLocalDB asyncTaskLoadDressCollectionInfoFromLocalDB = new AsyncTaskLoadDressCollectionInfoFromLocalDB(
                    this.getDressCategoryId(),
                    this.getDressCategoryTitle()
            );

            asyncTaskLoadDressCollectionInfoFromLocalDB.execute();
        }
    }

    //==============================================================================================
    // Фоновый Async Task для загрузки данных о текущем отображаемом наборе (коллекции) одежды
    class AsyncTaskLoadDressCollection extends AsyncTask<String, Void, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> {

        //------------------------------------------------------------------------------------------
        // Перед началом фонового потока Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(DBMain.getContext() != null) {
                MySQLDressCollectionLoad.this.setProgressDialogDressCollectionLoad(new ProgressDialog(DBMain.getContext()));
                MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad().setMessage(DBMain.getContext().getResources().getString(R.string.string_title_progressdialog_load_data));
                MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad().setIndeterminate(false);
                MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad().setCancelable(false);
                MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad().show();
            }
        }

        //------------------------------------------------------------------------------------------
        // Получаем информацию о текущем отображаемом наборе (коллекции) одежды
        protected ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> doInBackground(String... args) {
            // Массив, возвращаемый в качестве результата выполнения текущей функции
            ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> returnArrayCollectionsInfo = null;

            // Массив параметров, передаваемых на сервер
            HashMap<String, String> postDataParams = new HashMap<>();

            postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_DRESS_COLLECTION_LOAD);
            postDataParams.put(GlobalFlags.TAG_USER_ID, String.valueOf(UserDetails.getUserIdServer()));
            postDataParams.put(GlobalFlags.TAG_CATID, MySQLDressCollectionLoad.this.getDressCategoryId());
            postDataParams.put(GlobalFlags.TAG_COUNT_DRESS_READ_FROM_DB, String.valueOf(GlobalFlags.COUNT_DRESS_READ_FROM_DB));

            // Пересылаем данные на сервер
            String requestResult = HttpGetPostRequest.executePostRequest(GlobalFlags.TAG_URL, postDataParams);

            //--------------------------------------------------------------------------------------
            // Парсим строку в JSON объект
            JSONObject jSONObject;

            try {
                jSONObject = new JSONObject(requestResult.substring(requestResult.indexOf("{"), requestResult.lastIndexOf("}") + 1));
            }
            catch (JSONException exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("JSON Parser Error (Dress Collection Load): " + exception.toString());
                return null;
            }
            catch(Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Dress Collection Load): " + exception.toString());
                return null;
            }

            //--------------------------------------------------------------------------------------
            // Разбираем ответ от сервера
            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = jSONObject.getInt(GlobalFlags.TAG_SUCCESS);

                // Если информация о коллекциях одежды найдена
                if (success == 1) {
                    // Если информация считывалась для определенной категории
                    if(MySQLDressCollectionLoad.this.getDressCategoryId() != null) {
                        // Получаем JSON объект для всей одежды для соответствующей категории
                        if (!jSONObject.isNull(GlobalFlags.TAG_DRESS)) {
                            JSONArray jSONArrayDress = jSONObject.getJSONArray(GlobalFlags.TAG_DRESS);

                            // В цикле разбираем одежду, информация о которой считана из БД
                            for(int indexDress = 0; indexDress < jSONArrayDress.length(); indexDress++) {
                                JSONObject jSONCurrentDress = jSONArrayDress.getJSONObject(indexDress);

                                // id текущей коллекции одежды
                                // При показе одежды из определенной категории, для каждой коллекции
                                // отображается только одна вещь, поэтому используем id данной вещи
                                // в качестве id текущей коллекции одежды
                                String currentDressCollectionId = "0";

                                //------------------------------------------------------------------
                                // Сохраняем каждый json елемент в переменную
                                // (сохраняем все данные о текущей одежде по умолчанию для текущего типа)

                                // id текущей одежды
                                String currentDressId = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_ID)) {
                                    currentDressId = jSONCurrentDress.getString(GlobalFlags.TAG_ID);

                                    // Сохраняем id текущей вещи в качестве id текущей коллекции одежды
                                    currentDressCollectionId = currentDressId;
                                }

                                // id категории для текущей одежды
                                String currentDressCatId = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_CATID)) {
                                    currentDressCatId = jSONCurrentDress.getString(GlobalFlags.TAG_CATID);
                                }

                                // Название категории для текущей одежды
                                String currentDressCategoryTitle = "";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_CATEGORY_TITLE)) {
                                    currentDressCategoryTitle = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_CATEGORY_TITLE));
                                }

                                // Название текущей одежды
                                String currentDressTitle = "";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_TITLE)) {
                                    currentDressTitle = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_TITLE));
                                }

                                // Алиас названия текущей одежды
                                String currentDressAlias = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_ALIAS)) {
                                    currentDressAlias = jSONCurrentDress.getString(GlobalFlags.TAG_ALIAS);
                                }

                                // Для кого предназначена текущая одежда (для мужчин, женщин или детей)
                                String currentDressForWho = GlobalFlags.TAG_DRESS_MAN;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_FOR_WHO)) {
                                    currentDressForWho = jSONCurrentDress.getString(GlobalFlags.TAG_FOR_WHO);
                                }

                                // Тип текущей одежды (головной убор, обувь и т.д.)
                                String currentDressType = GlobalFlags.TAG_DRESS_HEAD;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_TYPE)) {
                                    currentDressType = jSONCurrentDress.getString(GlobalFlags.TAG_TYPE);
                                }

                                // id бренда для текущей одежды
                                String currentDressBrandId = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_BRAND_ID)) {
                                    currentDressBrandId = jSONCurrentDress.getString(GlobalFlags.TAG_BRAND_ID);
                                }

                                // Название бренда для текущей одежды
                                String currentDressBrandTitle = "";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_BRAND_TITLE)) {
                                    currentDressBrandTitle = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_BRAND_TITLE));
                                }

                                // Ссылка на изображение для текущей одежды
                                String currentDressImage = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE)) {
                                    currentDressImage = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE));
                                }

                                // Ширина изображения для текущей одежды
                                String currentDressImageWidth = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE_WIDTH)) {
                                    currentDressImageWidth = jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE_WIDTH);
                                }

                                // Высота изображения для текущей одежды
                                String currentDressImageHeight = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                                    currentDressImageHeight = jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE_HEIGHT);
                                }

                                // Ссылка на изображение для текущей одежды с обратной стороны
                                String currentDressImageBack = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE_BACK)) {
                                    currentDressImageBack = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE_BACK));
                                }

                                // Ширина изображения для текущей одежды с обратной стороны
                                String currentDressImageBackWidth = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE_BACK_WIDTH)) {
                                    currentDressImageBackWidth = jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE_BACK_WIDTH);
                                }

                                // Высота изображения для текущей одежды с обратной стороны
                                String currentDressImageBackHeight = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_IMAGE_BACK_HEIGHT)) {
                                    currentDressImageBackHeight = jSONCurrentDress.getString(GlobalFlags.TAG_IMAGE_BACK_HEIGHT);
                                }

                                // Цвет текущей одежды
                                String currentDressColor = "";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_COLOR)) {
                                    currentDressColor = jSONCurrentDress.getString(GlobalFlags.TAG_COLOR);
                                }

                                // Стиль текущей одежды
                                String currentDressStyle = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_STYLE)) {
                                    currentDressStyle = jSONCurrentDress.getString(GlobalFlags.TAG_STYLE);
                                }

                                // Краткое описание для текущей одежды
                                String currentDressShortDescription = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                                    currentDressShortDescription = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_SHORT_DESCRIPTION));
                                }

                                // Полное описание для текущей одежды
                                String currentDressDescription = null;

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_DESCRIPTION)) {
                                    currentDressDescription = FunctionsString.jsonDecode(jSONCurrentDress.getString(GlobalFlags.TAG_DESCRIPTION));
                                }

                                // Уровень популярности текущей одежды
                                String currentDressHits = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_HITS)) {
                                    currentDressHits = jSONCurrentDress.getString(GlobalFlags.TAG_HITS);
                                }

                                // Версия информации о текущей вещи
                                String currentDressVersion = "1";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_VERSION)) {
                                    currentDressVersion = jSONCurrentDress.getString(GlobalFlags.TAG_VERSION);
                                }

                                // Является ли текущая вещь вещью, отображаемой по умолчанию
                                String currentDressDefault = "0";

                                if (!jSONCurrentDress.isNull(GlobalFlags.TAG_DRESS_DEFAULT)) {
                                    currentDressDefault = jSONCurrentDress.getString(GlobalFlags.TAG_DRESS_DEFAULT);
                                }

                                //------------------------------------------------------------------
                                // Создаем новый HashMap
                                HashMap<String, String> mapCurrentDressInfo = new HashMap<>();

                                // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                mapCurrentDressInfo.put(GlobalFlags.TAG_ID, currentDressId);                                // id текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_CATID, currentDressCatId);                          // id категории для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_CATEGORY_TITLE, currentDressCategoryTitle);         // название категории для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_TITLE, currentDressTitle);                          // название текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_ALIAS, currentDressAlias);                          // алиас названия текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_FOR_WHO, currentDressForWho);                       // для кого предназначена текущая одежда
                                mapCurrentDressInfo.put(GlobalFlags.TAG_TYPE, currentDressType);                            // тип текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_BRAND_ID, currentDressBrandId);                     // id бренда текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_BRAND_TITLE, currentDressBrandTitle);               // название бренда для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE, currentDressImage);                          // ссылка на изображение для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_WIDTH, currentDressImageWidth);               // ширина изображения для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_HEIGHT, currentDressImageHeight);             // высота изображения для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK, currentDressImageBack);                 // ссылка на изображение для текущей одежды с обратной стороны
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_WIDTH, currentDressImageBackWidth);      // ширина изображения для текущей одежды с обратной стороны
                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_HEIGHT, currentDressImageBackHeight);    // высота изображения для текущей одежды с обратной стороны
                                mapCurrentDressInfo.put(GlobalFlags.TAG_COLOR, currentDressColor);                          // цвет текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_STYLE, currentDressStyle);                          // стиль текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_SHORT_DESCRIPTION, currentDressShortDescription);   // краткое описание для текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_DESCRIPTION, currentDressDescription);              // полное описание для текущей вещи
                                mapCurrentDressInfo.put(GlobalFlags.TAG_HITS, currentDressHits);                            // уровень популярности текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_VERSION, currentDressVersion);                      // версия информации о текущей одежды
                                mapCurrentDressInfo.put(GlobalFlags.TAG_DRESS_DEFAULT, currentDressDefault);                // является ли текущая вещь вещью, отображаемой по умолчанию

                                //------------------------------------------------------------------
                                // Добавляем HashList в ArrayList
                                ArrayList<HashMap<String, String>> arrayDressForCurrentCollectionForType = new ArrayList<>();
                                arrayDressForCurrentCollectionForType.add(mapCurrentDressInfo);

                                //------------------------------------------------------------------
                                // Массив, содержащий информацию об одежде, входящей в состав текущего набора одежды
                                // При этом учитываем, что при считывании одежды для определенной категории
                                // для каждой коллекции отображается только одна вещь
                                HashMap<String, ArrayList<HashMap<String, String>>> arrayDressForCurrentCollection = new HashMap<>();
                                arrayDressForCurrentCollection.put(currentDressType, arrayDressForCurrentCollectionForType);

                                //------------------------------------------------------------------
                                // Добавляем в массив, содержащий сведения о текущей коллекции одежды,
                                // дополнительные сведения
                                HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                                // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);             // id текущего набора одежды

                                ArrayList<HashMap<String, String>> arrayCurrentCollectionInfo = new ArrayList<>();
                                arrayCurrentCollectionInfo.add(mapCurrentCollectionInfo);

                                arrayDressForCurrentCollection.put(GlobalFlags.TAG_COLLECTION, arrayCurrentCollectionInfo);

                                //------------------------------------------------------------------
                                if (returnArrayCollectionsInfo == null) {
                                    returnArrayCollectionsInfo = new ArrayList<>();
                                }

                                returnArrayCollectionsInfo.add(arrayDressForCurrentCollection);
                            }
                        }
                    }
                    // Иначе, если считывается информация обо всей одежде, входящей в состав текущей коллекции
                    else {
                        // Получаем JSON объект для всех коллекциях одежды
                        if (!jSONObject.isNull(GlobalFlags.TAG_COLLECTION)) {
                            JSONArray jSONArrayCollection = jSONObject.getJSONArray(GlobalFlags.TAG_COLLECTION);

                            // Очищаем таблицы от старой информации о коллекциях одежды для текущего пользователя
                            DBMain.getDBSQLiteHelper().clearTable(GlobalFlags.TAG_TABLE_COLLECTION);
                            DBMain.getDBSQLiteHelper().clearTable(GlobalFlags.TAG_TABLE_COLLECTION_DRESS);

                            // В цикле перебираем все наборы одежды
                            for (int indexCollection = 0; indexCollection < jSONArrayCollection.length(); indexCollection++) {
                                // Считываем объект для текущего набора одежды
                                JSONObject jSONObjectCurrentCollection = jSONArrayCollection.getJSONObject(indexCollection);

                                //------------------------------------------------------------------
                                // Считываем информацию о текущем наборе одежды

                                // id текущего набора одежды
                                String currentDressCollectionId = "0";

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_ID)) {
                                    currentDressCollectionId = jSONObjectCurrentCollection.getString(GlobalFlags.TAG_ID);
                                }

                                // Название текущего набора одежды
                                String currentDressCollectionTitle = null;

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_TITLE)) {
                                    currentDressCollectionTitle = FunctionsString.jsonDecode(jSONObjectCurrentCollection.getString(GlobalFlags.TAG_TITLE));
                                }

                                // Алиас название текущего набора одежды
                                String currentDressCollectionAlias = null;

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_ALIAS)) {
                                    currentDressCollectionAlias = jSONObjectCurrentCollection.getString(GlobalFlags.TAG_ALIAS);
                                }

                                // Тип текущего набора одежды
                                String currentDressCollectionType = GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION;

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_TYPE)) {
                                    currentDressCollectionType = jSONObjectCurrentCollection.getString(GlobalFlags.TAG_TYPE);
                                }

                                // Краткое описание для текущей одежды
                                String currentDressCollectionShortDescription = null;

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                                    currentDressCollectionShortDescription = FunctionsString.jsonDecode(jSONObjectCurrentCollection.getString(GlobalFlags.TAG_SHORT_DESCRIPTION));
                                }

                                // Полное описание для текущей одежды
                                String currentDressCollectionDescription = null;

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_DESCRIPTION)) {
                                    currentDressCollectionDescription = FunctionsString.jsonDecode(jSONObjectCurrentCollection.getString(GlobalFlags.TAG_DESCRIPTION));
                                }

                                // Версия информации о текущей вещи
                                String currentDressCollectionVersion = "1";

                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_VERSION)) {
                                    currentDressCollectionVersion = jSONObjectCurrentCollection.getString(GlobalFlags.TAG_VERSION);
                                }

                                //------------------------------------------------------------------
                                // Создаем новый HashMap
                                HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                                // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);                                 // id текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_TITLE, currentDressCollectionTitle);                           // название текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_ALIAS, currentDressCollectionAlias);                           // алиас названия текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_TYPE, currentDressCollectionType);                             // тип текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_SHORT_DESCRIPTION, currentDressCollectionShortDescription);    // краткое описание текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_DESCRIPTION, currentDressCollectionDescription);               // полное описание текущего набора одежды
                                mapCurrentCollectionInfo.put(GlobalFlags.TAG_VERSION, currentDressCollectionVersion);                       // версия информации о текущем наборе одежды

                                //------------------------------------------------------------------
                                // Добавляем или обновляем информации о текущей коллекции в локальной БД SQLite
                                DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_COLLECTION, currentDressCollectionId, mapCurrentCollectionInfo);

                                //------------------------------------------------------------------
                                // Формируем HashMap, содержащий информацию о текущем наборе одежды
                                // для возвращаемого массива
                                HashMap<String, String> returnMapCurrentCollectionInfo = new HashMap<>();

                                // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                returnMapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);                // id текущего набора одежды

                                ArrayList<HashMap<String, String>> returnArrayCurrentCollectionInfo = new ArrayList<>();
                                returnArrayCurrentCollectionInfo.add(returnMapCurrentCollectionInfo);

                                //------------------------------------------------------------------
                                // Считываем информацию об одежде для текущего набора одежды
                                if (!jSONObjectCurrentCollection.isNull(GlobalFlags.TAG_DRESS)) {
                                    JSONObject jSONObjectDressInCurrentCollection = jSONObjectCurrentCollection.getJSONObject(GlobalFlags.TAG_DRESS);

                                    // Массив, содержащий информацию об одежде, входящей в состав текущего набора одежды
                                    HashMap<String, ArrayList<HashMap<String, String>>> arrayDressForCurrentCollection = new HashMap<>();

                                    // В цикле перебираем все типы одежды (головные уборы, обувь и т.д.)
                                    for (int indexTagDressType = 0; indexTagDressType < GlobalFlags.getArrayTagDressType().size(); indexTagDressType++) {
                                        // Считываем текущий тег, определяющий типы одежды (головные уборы, обувь и т.д.)
                                        String CURRENT_TAG_DRESS_TYPE = GlobalFlags.getArrayTagDressType().get(indexTagDressType);

                                        // Проверяем, присутствует ли в результате, полученном с сервера текущий тип одежды
                                        if (!jSONObjectDressInCurrentCollection.isNull(CURRENT_TAG_DRESS_TYPE)) {
                                            JSONArray jSONArrayDressInCurrentCollectionForType = jSONObjectDressInCurrentCollection.getJSONArray(CURRENT_TAG_DRESS_TYPE);

                                            // Массив, содержащий информацию об одежде, входящей в состав текущего набора одежды
                                            // для текущего типа одеждя
                                            ArrayList<HashMap<String, String>> arrayDressForCurrentCollectionForType = new ArrayList<>();

                                            // В цикле перебираем всю одежду, присутствующую в данном наборе одежды
                                            // для текущего типа одежды
                                            for (int indexDressInCurrentCollectionForType = 0; indexDressInCurrentCollectionForType < jSONArrayDressInCurrentCollectionForType.length(); indexDressInCurrentCollectionForType++) {
                                                // Получаем объект, хранящий информацию о текущей одежде
                                                JSONObject jSONCurrentDressInCurrentCollectionForType = jSONArrayDressInCurrentCollectionForType.getJSONObject(indexDressInCurrentCollectionForType);

                                                //--------------------------------------------------
                                                // Сохраняем каждый json елемент в переменную
                                                // (сохраняем все данные о текущей одежде по умолчанию для текущего типа)

                                                // id текущей одежды
                                                String currentDressId = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_ID)) {
                                                    currentDressId = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_ID);
                                                }

                                                // id категории для текущей одежды
                                                String currentDressCatId = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_CATID)) {
                                                    currentDressCatId = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_CATID);
                                                }

                                                // Название категории для текущей одежды
                                                String currentDressCategoryTitle = "";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_CATEGORY_TITLE)) {
                                                    currentDressCategoryTitle = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_CATEGORY_TITLE));
                                                }

                                                // Название текущей одежды
                                                String currentDressTitle = "";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_TITLE)) {
                                                    currentDressTitle = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_TITLE));
                                                }

                                                // Алиас названия текущей одежды
                                                String currentDressAlias = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_ALIAS)) {
                                                    currentDressAlias = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_ALIAS);
                                                }

                                                // Для кого предназначена текущая одежда (для мужчин, женщин или детей)
                                                String currentDressForWho = GlobalFlags.TAG_DRESS_MAN;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_FOR_WHO)) {
                                                    currentDressForWho = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_FOR_WHO);
                                                }

                                                // Тип текущей одежды (головной убор, обувь и т.д.)
                                                String currentDressType = GlobalFlags.TAG_DRESS_HEAD;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_TYPE)) {
                                                    currentDressType = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_TYPE);
                                                }

                                                // id бренда для текущей одежды
                                                String currentDressBrandId = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_BRAND_ID)) {
                                                    currentDressBrandId = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_BRAND_ID);
                                                }

                                                // Название бренда для текущей одежды
                                                String currentDressBrandTitle = "";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_BRAND_TITLE)) {
                                                    currentDressBrandTitle = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_BRAND_TITLE));
                                                }

                                                // Ссылка на изображение для текущей одежды
                                                String currentDressImage = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE)) {
                                                    currentDressImage = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE));
                                                }

                                                // Ширина изображения для текущей одежды
                                                String currentDressImageWidth = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE_WIDTH)) {
                                                    currentDressImageWidth = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE_WIDTH);
                                                }

                                                // Высота изображения для текущей одежды
                                                String currentDressImageHeight = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                                                    currentDressImageHeight = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE_HEIGHT);
                                                }

                                                // Ссылка на изображение для текущей одежды с обратной стороны
                                                String currentDressImageBack = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE_BACK)) {
                                                    currentDressImageBack = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE_BACK));
                                                }

                                                // Ширина изображения для текущей одежды с обратной стороны
                                                String currentDressImageBackWidth = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE_BACK_WIDTH)) {
                                                    currentDressImageBackWidth = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE_BACK_WIDTH);
                                                }

                                                // Высота изображения для текущей одежды с обратной стороны
                                                String currentDressImageBackHeight = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_IMAGE_BACK_HEIGHT)) {
                                                    currentDressImageBackHeight = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_IMAGE_BACK_HEIGHT);
                                                }

                                                // Цвет текущей одежды
                                                String currentDressColor = "";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_COLOR)) {
                                                    currentDressColor = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_COLOR);
                                                }

                                                // Стиль текущей одежды
                                                String currentDressStyle = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_STYLE)) {
                                                    currentDressStyle = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_STYLE);
                                                }

                                                // Краткое описание для текущей одежды
                                                String currentDressShortDescription = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                                                    currentDressShortDescription = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_SHORT_DESCRIPTION));
                                                }

                                                // Полное описание для текущей одежды
                                                String currentDressDescription = null;

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_DESCRIPTION)) {
                                                    currentDressDescription = FunctionsString.jsonDecode(jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_DESCRIPTION));
                                                }

                                                // Уровень популярности текущей одежды
                                                String currentDressHits = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_HITS)) {
                                                    currentDressHits = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_HITS);
                                                }

                                                // Версия информации о текущей вещи
                                                String currentDressVersion = "1";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_VERSION)) {
                                                    currentDressVersion = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_VERSION);
                                                }

                                                // Является ли текущая вещь вещью, отображаемой по умолчанию
                                                String currentDressDefault = "0";

                                                if (!jSONCurrentDressInCurrentCollectionForType.isNull(GlobalFlags.TAG_DRESS_DEFAULT)) {
                                                    currentDressDefault = jSONCurrentDressInCurrentCollectionForType.getString(GlobalFlags.TAG_DRESS_DEFAULT);
                                                }

                                                //--------------------------------------------------
                                                // Создаем новый HashMap
                                                HashMap<String, String> mapCurrentDressInfo = new HashMap<>();

                                                // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_ID, currentDressId);                                // id текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_CATID, currentDressCatId);                          // id категории для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_CATEGORY_TITLE, currentDressCategoryTitle);         // название категории для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_TITLE, currentDressTitle);                          // название текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_ALIAS, currentDressAlias);                          // алиас названия текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_FOR_WHO, currentDressForWho);                       // для кого предназначена текущая одежда
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_TYPE, currentDressType);                            // тип текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_BRAND_ID, currentDressBrandId);                     // id бренда текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_BRAND_TITLE, currentDressBrandTitle);               // название бренда для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE, currentDressImage);                          // ссылка на изображение для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_WIDTH, currentDressImageWidth);               // ширина изображения для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_HEIGHT, currentDressImageHeight);             // высота изображения для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK, currentDressImageBack);                 // ссылка на изображение для текущей одежды с обратной стороны
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_WIDTH, currentDressImageBackWidth);      // ширина изображения для текущей одежды с обратной стороны
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_HEIGHT, currentDressImageBackHeight);    // высота изображения для текущей одежды с обратной стороны
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_COLOR, currentDressColor);                          // цвет текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_STYLE, currentDressStyle);                          // стиль текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_SHORT_DESCRIPTION, currentDressShortDescription);   // краткое описание для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_DESCRIPTION, currentDressDescription);              // полное описание для текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_HITS, currentDressHits);                            // уровень популярности текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_VERSION, currentDressVersion);                      // версия информации о текущей одежды
                                                mapCurrentDressInfo.put(GlobalFlags.TAG_DRESS_DEFAULT, currentDressDefault);                // является ли текущая вещь вещью, отображаемой по умолчанию

                                                //--------------------------------------------------
                                                // Добавляем информацию о соответствии текущего набора одежды
                                                // и текущей вещи в локальную БД SQLite
                                                HashMap<String, String> mapCurrentDressCollectionInfo = new HashMap<>();
                                                mapCurrentDressCollectionInfo.put(GlobalFlags.TAG_COLLECTION_ID, currentDressCollectionId);
                                                mapCurrentDressCollectionInfo.put(GlobalFlags.TAG_DRESS_ID, currentDressId);

                                                // Добавляем или обновляем информации в локальную БД SQLite
                                                DBMain.getDBSQLiteHelper().insertRecordToDB(GlobalFlags.TAG_TABLE_COLLECTION_DRESS, mapCurrentDressCollectionInfo);

                                                //--------------------------------------------------
                                                // добавляем HashList в ArrayList
                                                arrayDressForCurrentCollectionForType.add(mapCurrentDressInfo);
                                            }

                                            //------------------------------------------------------
                                            arrayDressForCurrentCollection.put(CURRENT_TAG_DRESS_TYPE, arrayDressForCurrentCollectionForType);
                                        }
                                    }

                                    //--------------------------------------------------------------
                                    // Добавляем в массив, содержащий информацию об одежде для текущего
                                    // набора одежды, непосредственно информацию о текущем наборе одежды
                                    arrayDressForCurrentCollection.put(GlobalFlags.TAG_COLLECTION, returnArrayCurrentCollectionInfo);

                                    //--------------------------------------------------------------
                                    if (returnArrayCollectionsInfo == null) {
                                        returnArrayCollectionsInfo = new ArrayList<>();
                                    }

                                    returnArrayCollectionsInfo.add(arrayDressForCurrentCollection);
                                }
                            }
                        }
                    }
                }

                //----------------------------------------------------------------------------------
                // Получаем JSON объект для текущего пользователя
                if( !jSONObject.isNull(GlobalFlags.TAG_USER) ) {
                    JSONObject jSONCurrentUser = jSONObject.getJSONObject(GlobalFlags.TAG_USER);

                    // Количество коллекция одежды для текущего пользователя
                    String currentUserCountCollections = "0";

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS)) {
                        currentUserCountCollections = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS);
                    }

                    //------------------------------------------------------------------------------
                    // Обновляем данные о текущем пользователе
                    HashMap<String, Object> mapUserDetails = new HashMap<>();
                    mapUserDetails.put(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS, Integer.parseInt(currentUserCountCollections));

                    UserDetails.updateUserDetails(mapUserDetails);

                    //------------------------------------------------------------------------------
                    // Формируем массив, хранящий информацию о категориях и количестве одежды,
                    // входящей в состав избранных наборов одежды для текущего пользователя
                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DRESS_IN_COLLECTIONS)) {
                        JSONArray jSONArrayDressInCollections = jSONCurrentUser.getJSONArray(GlobalFlags.TAG_USER_DRESS_IN_COLLECTIONS);

                        // Массив, хранящий информацию для всех категорий одежды,
                        // входящей в состав избранных наборов одежды для текущего пользователя
                        ArrayList<HashMap<String, String>> arrayDressInUserCollections = null;

                        // В цикле разбираем каждую категорию одежды
                        for(int indexCategory = 0; indexCategory < jSONArrayDressInCollections.length(); indexCategory++) {
                            // Считываем объект, хранящий информацию о текущей категории
                            JSONObject jSONObjectCurrentCategory = jSONArrayDressInCollections.getJSONObject(indexCategory);

                            // Разбираем данные о текущей категории

                            // id текущей категории
                            String currentCategoryId = "0";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_CATID)) {
                                currentCategoryId = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_CATID);
                            }

                            // Название текущей категории
                            String currentCategoryTitle = "";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_TITLE)) {
                                currentCategoryTitle = FunctionsString.jsonDecode(jSONObjectCurrentCategory.getString(GlobalFlags.TAG_TITLE));
                            }

                            // Алиас для названия текущей категории
                            String currentCategoryAlias = null;

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_ALIAS)) {
                                currentCategoryAlias = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_ALIAS);
                            }

                            // Тип одежды для текущей категории
                            String currentCategoryType = GlobalFlags.TAG_DRESS_HEAD;

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_TYPE)) {
                                currentCategoryType = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_TYPE);
                            }

                            // Количество одежды для текущей категории
                            String currentCategoryDressCount = "0";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_DRESS_COUNT)) {
                                currentCategoryDressCount = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_DRESS_COUNT);
                            }

                            //----------------------------------------------------------------------
                            // Сохраняем данные о текщей категории
                            HashMap<String, String> mapCurrentCategoryInfo = new HashMap<>();
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_ID, currentCategoryId);
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_CATID, currentCategoryId);                   // id текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_TITLE, currentCategoryTitle);                // название текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_ALIAS, currentCategoryAlias);                // алиас для названия текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_TYPE, currentCategoryType);                  // тип одежды для текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_DRESS_COUNT, currentCategoryDressCount);     // количество одежды для текущей категории

                            if(arrayDressInUserCollections == null) {
                                arrayDressInUserCollections = new ArrayList<>();
                            }

                            arrayDressInUserCollections.add(mapCurrentCategoryInfo);
                        }

                        //--------------------------------------------------------------------------
                        // Сохраняем массив arrayDressInUserCollections в глобальном массиве
                        UserDetails.setArrayDressInUserCollections(arrayDressInUserCollections);
                    }
                }
            }
            catch (JSONException exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("JSON Parser Error (Dress Collection Load): " + exception.toString());
                return null;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Dress Collection Load): " + exception.toString());
                return null;
            }

            return returnArrayCollectionsInfo;
        }

        //------------------------------------------------------------------------------------------
        // После завершения фоновой задачи закрываем прогресс-диалог
        protected void onPostExecute(ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> resultArrayCollectionsInfo) {
            super.onPostExecute(resultArrayCollectionsInfo);

            //--------------------------------------------------------------------------------------
            try {
                // Закрываем прогресс-диалог
                if (MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad() != null) {
                    MySQLDressCollectionLoad.this.getProgressDialogDressCollectionLoad().dismiss();
                }

                //----------------------------------------------------------------------------------
                // Меняем заголовок страницы
                String textTitle = DBMain.getContext().getString(R.string.bar_item_dress_collection);

                if(MySQLDressCollectionLoad.this.getDressCategoryTitle() != null) {
                    textTitle = MySQLDressCollectionLoad.this.getDressCategoryTitle();
                }

                if(DBMain.getContext() != null) {
                    if (DBMain.getContext().getClass().toString().contains("MainActivity")) {
                        ((MainActivity) DBMain.getContext()).setDressroomTitleText(textTitle);
                    }
                }

                //------------------------------------------------------------------------------------------
                // Обновляем адаптер для боковой всплывающей панели
                if (MainActivity.getNavigationDrawerFragment() != null) {
                    if (MainActivity.getNavigationDrawerFragment().getNavigationDrawerAdapter() != null) {
                        MainActivity.getNavigationDrawerFragment().getNavigationDrawerAdapter().notifyDataSetChanged();
                    }
                }

                //----------------------------------------------------------------------------------
                // Если многомерный массив, хранящий информацию о наборах одежды, НЕ пуст
                if (resultArrayCollectionsInfo != null) {
                    // Если количество коллекций одежды больше 0
                    if (resultArrayCollectionsInfo.size() > 0) {
                        // Формируем массив, содержащий только НЕОБХОДИМУЮ информацию об одежде, для создаваемого адаптера
                        ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> arrayCollectionsInfoForAdapter = new ArrayList<>();

                        for(int indexCollection = 0; indexCollection < resultArrayCollectionsInfo.size(); indexCollection++) {
                            // Считываем набор ключей (присутствующих типов одежды) для текущей коллекции
                            Collection<String> arrayCurrentCollectionInfoKeyCollection = resultArrayCollectionsInfo.get(indexCollection).keySet();

                            // Массив, содержащий сведения об одежде для текущего набора одежды для адаптера
                            HashMap<String, ArrayList<HashMap<String, String>>> arrayDressInfoForCurrentCollectionForAdapter = new HashMap<>();

                            for (String arrayCurrentViewDressInfoKey : arrayCurrentCollectionInfoKeyCollection) {
                                // Массив, содержащий сведения об одежде для текущего типа
                                ArrayList<HashMap<String, String>> arrayDressInfoForCurrentGroup = resultArrayCollectionsInfo.get(indexCollection).get(arrayCurrentViewDressInfoKey);

                                if(arrayCurrentViewDressInfoKey.equals(GlobalFlags.TAG_COLLECTION)) {
                                    arrayDressInfoForCurrentCollectionForAdapter.put(GlobalFlags.TAG_COLLECTION, arrayDressInfoForCurrentGroup);
                                }
                                else {
                                    // Массив, содержащий сведения об одежде для текущего типа для адаптера
                                    ArrayList<HashMap<String, String>> arrayDressInfoForCurrentGroupForAdapter = new ArrayList<>();

                                    // В цикле перебираем всю одежду и сохраняем информацию о ней в локальную БД SQLite
                                    for (int indexDress = 0; indexDress < arrayDressInfoForCurrentGroup.size(); indexDress++) {
                                        HashMap<String, String> currentDressInfo = arrayDressInfoForCurrentGroup.get(indexDress);

                                        // id текущей одежды
                                        String currentDressId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_ID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_ID) != null) {
                                                currentDressId = currentDressInfo.get(GlobalFlags.TAG_ID);
                                            }
                                        }

                                        // id категории для текущей одежды
                                        String currentDressCatId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_CATID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_CATID) != null) {
                                                currentDressCatId = currentDressInfo.get(GlobalFlags.TAG_CATID);
                                            }
                                        }

                                        // Для кого предназначена текущая одежда
                                        String currentDressForWho = GlobalFlags.TAG_DRESS_MAN;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_FOR_WHO)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_FOR_WHO) != null) {
                                                currentDressForWho = currentDressInfo.get(GlobalFlags.TAG_FOR_WHO);
                                            }
                                        }

                                        // Тип текущей одежды
                                        String currentDressType = GlobalFlags.TAG_DRESS_HEAD;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_TYPE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_TYPE) != null) {
                                                currentDressType = currentDressInfo.get(GlobalFlags.TAG_TYPE);
                                            }
                                        }

                                        // id бренда для текущей одежды
                                        String currentDressBrandId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_BRAND_ID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_BRAND_ID) != null) {
                                                currentDressBrandId = currentDressInfo.get(GlobalFlags.TAG_BRAND_ID);
                                            }
                                        }

                                        // Ссылка на изображение для текущей одежды
                                        String currentDressImage = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE) != null) {
                                                currentDressImage = currentDressInfo.get(GlobalFlags.TAG_IMAGE);
                                            }
                                        }

                                        // Ширина изображения для текущей одежды
                                        String currentDressImageWidth = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_WIDTH) != null) {
                                                currentDressImageWidth = currentDressInfo.get(GlobalFlags.TAG_IMAGE_WIDTH);
                                            }
                                        }

                                        // Высота изображения для текущей одежды
                                        String currentDressImageHeight = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_HEIGHT) != null) {
                                                currentDressImageHeight = currentDressInfo.get(GlobalFlags.TAG_IMAGE_HEIGHT);
                                            }
                                        }

                                        // Ссылка на изображение для текущей одежды с обратной стороны
                                        String currentDressImageBack = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK) != null) {
                                                currentDressImageBack = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK);
                                            }
                                        }

                                        // Ширина изображения для текущей одежды с обратной стороны
                                        String currentDressImageBackWidth = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK_WIDTH)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH) != null) {
                                                currentDressImageBackWidth = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH);
                                            }
                                        }

                                        // Высота изображения для текущей одежды с обратной стороны
                                        String currentDressImageBackHeight = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK_HEIGHT)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT) != null) {
                                                currentDressImageBackHeight = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT);
                                            }
                                        }

                                        // Цвет текущей одежды
                                        String currentDressColor = "";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_COLOR)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_COLOR) != null) {
                                                currentDressColor = currentDressInfo.get(GlobalFlags.TAG_COLOR);
                                            }
                                        }

                                        // Стиль текущей одежды
                                        String currentDressStyle = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_STYLE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_STYLE) != null) {
                                                currentDressStyle = currentDressInfo.get(GlobalFlags.TAG_STYLE);
                                            }
                                        }

                                        //----------------------------------------------------------
                                        // Создаем новый HashMap для текущей одежды
                                        HashMap<String, String> mapCurrentDressInfoForAdapter = new HashMap<>();

                                        // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_ID, currentDressId);                                 // id текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_CATID, currentDressCatId);                           // id категории для текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_FOR_WHO, currentDressForWho);                        // для кого предназначена текущая одежда
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_TYPE, currentDressType);                             // тип текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_BRAND_ID, currentDressBrandId);                      // id бренда текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE, currentDressImage);                           // ссылка на изображение для текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE_WIDTH, currentDressImageWidth);                // ширина изображения для текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE_HEIGHT, currentDressImageHeight);              // высота изображения для текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE_BACK, currentDressImageBack);                  // ссылка на изображение для текущей одежды с обратной стороны
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE_BACK_WIDTH, currentDressImageBackWidth);       // ширина изображения для текущей одежды с обратной стороны
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_IMAGE_BACK_HEIGHT, currentDressImageBackHeight);     // высота изображения для текущей одежды с обратной стороны
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_COLOR, currentDressColor);                           // цвет текущей одежды
                                        mapCurrentDressInfoForAdapter.put(GlobalFlags.TAG_STYLE, currentDressStyle);                           // стиль текущей одежды

                                        // Добавляем текущий HashMap в ArrayList
                                        arrayDressInfoForCurrentGroupForAdapter.add(mapCurrentDressInfoForAdapter);
                                    }

                                    arrayDressInfoForCurrentCollectionForAdapter.put(arrayCurrentViewDressInfoKey, arrayDressInfoForCurrentGroupForAdapter);
                                }
                            }

                            arrayCollectionsInfoForAdapter.add(arrayDressInfoForCurrentCollectionForAdapter);
                        }

                        //--------------------------------------------------------------------------
                        // Задаем адаптер для листания коллекций одежды
                        PagerAdapterDressCollection pagerAdapterDressCollection = new PagerAdapterDressCollection(arrayCollectionsInfoForAdapter);
                        DBMain.setPagerAdapterDressCollection(pagerAdapterDressCollection);

                        //--------------------------------------------------------------------------
                        // Считываем id первого отображаемого набора одежды
                        int firstCollectionId = 0;

                        if(DBMain.getPagerAdapterDressCollection() != null) {
                            if(DBMain.getPagerAdapterDressCollection().getItemParamsId(0) != null) {
                                firstCollectionId = DBMain.getPagerAdapterDressCollection().getItemParamsId(0);
                            }
                        }

                        //--------------------------------------------------------------------------
                        if(DBMain.getContext() != null) {
                            if (DBMain.getContext().getClass().toString().contains("MainActivity")) {
                                // Запускаем функцию инициализации компонентов окна
                                ((MainActivity) DBMain.getContext()).initializeComponentsLayoutCollections();

                                // Задаем тег для кнопки сохранения информации о текущем наборе одежды
                                if (((MainActivity) DBMain.getContext()).getButtonDressSave() != null) {
                                    ((MainActivity) DBMain.getContext()).getButtonDressSave().setTag(firstCollectionId);
                                }
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Сохраняем данные о считанной из удаленной БД одежде в локальную БД SQLite
                        // В цикле перебираем все наборы одежды, информация о которых считана из удаленной БД
                        for(int indexCollection = 0; indexCollection < resultArrayCollectionsInfo.size(); indexCollection++) {
                            // Считываем инаборов ключей (присутствующих типов одежды) для текущей коллекции
                            Collection<String> arrayCurrentViewDressInfoKeyCollection = resultArrayCollectionsInfo.get(indexCollection).keySet();

                            for (String arrayCurrentViewDressInfoKey : arrayCurrentViewDressInfoKeyCollection) {
                                if(!arrayCurrentViewDressInfoKey.equals(GlobalFlags.TAG_COLLECTION)) {
                                    // Массив, содержащий сведения об одежде для текущего типа
                                    ArrayList<HashMap<String, String>> arrayDressInfoForCurrentGroup = resultArrayCollectionsInfo.get(indexCollection).get(arrayCurrentViewDressInfoKey);

                                    // В цикле перебираем всю одежду и сохраняем информацию о ней в локальную БД SQLite
                                    for (int indexDress = 0; indexDress < arrayDressInfoForCurrentGroup.size(); indexDress++) {
                                        HashMap<String, String> currentDressInfo = arrayDressInfoForCurrentGroup.get(indexDress);

                                        // id текущей одежды
                                        String currentDressId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_ID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_ID) != null) {
                                                currentDressId = currentDressInfo.get(GlobalFlags.TAG_ID);
                                            }
                                        }

                                        // id категории для текущей одежды
                                        String currentDressCatId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_CATID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_CATID) != null) {
                                                currentDressCatId = currentDressInfo.get(GlobalFlags.TAG_CATID);
                                            }
                                        }

                                        // Название категории для текущей одежды
                                        String currentDressCategoryTitle = "";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_CATEGORY_TITLE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_CATEGORY_TITLE) != null) {
                                                currentDressCategoryTitle = currentDressInfo.get(GlobalFlags.TAG_CATEGORY_TITLE);
                                            }
                                        }

                                        // Название текущей одежды
                                        String currentDressTitle = "";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_TITLE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_TITLE) != null) {
                                                currentDressTitle = currentDressInfo.get(GlobalFlags.TAG_TITLE);
                                            }
                                        }

                                        // Алиас названия текущей одежды
                                        String currentDressAlias = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_ALIAS)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_ALIAS) != null) {
                                                currentDressAlias = currentDressInfo.get(GlobalFlags.TAG_ALIAS);
                                            }
                                        }

                                        // Для кого предназначена текущая одежда
                                        String currentDressForWho = GlobalFlags.TAG_DRESS_MAN;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_FOR_WHO)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_FOR_WHO) != null) {
                                                currentDressForWho = currentDressInfo.get(GlobalFlags.TAG_FOR_WHO);
                                            }
                                        }

                                        // Тип текущей одежды
                                        String currentDressType = GlobalFlags.TAG_DRESS_HEAD;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_TYPE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_TYPE) != null) {
                                                currentDressType = currentDressInfo.get(GlobalFlags.TAG_TYPE);
                                            }
                                        }

                                        // id бренда для текущей одежды
                                        String currentDressBrandId = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_BRAND_ID)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_BRAND_ID) != null) {
                                                currentDressBrandId = currentDressInfo.get(GlobalFlags.TAG_BRAND_ID);
                                            }
                                        }

                                        // Название бренда для текущей одежды
                                        String currentDressBrandTitle = "";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_BRAND_TITLE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_BRAND_TITLE) != null) {
                                                currentDressBrandTitle = currentDressInfo.get(GlobalFlags.TAG_BRAND_TITLE);
                                            }
                                        }

                                        // Ссылка на изображение для текущей одежды
                                        String currentDressImage = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE) != null) {
                                                currentDressImage = currentDressInfo.get(GlobalFlags.TAG_IMAGE);
                                            }
                                        }

                                        // Ширина изображения для текущей одежды
                                        String currentDressImageWidth = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_WIDTH) != null) {
                                                currentDressImageWidth = currentDressInfo.get(GlobalFlags.TAG_IMAGE_WIDTH);
                                            }
                                        }

                                        // Высота изображения для текущей одежды
                                        String currentDressImageHeight = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_HEIGHT) != null) {
                                                currentDressImageHeight = currentDressInfo.get(GlobalFlags.TAG_IMAGE_HEIGHT);
                                            }
                                        }

                                        // Ссылка на изображение для текущей одежды с обратной стороны
                                        String currentDressImageBack = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK) != null) {
                                                currentDressImageBack = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK);
                                            }
                                        }

                                        // Ширина изображения для текущей одежды с обратной стороны
                                        String currentDressImageBackWidth = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK_WIDTH)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH) != null) {
                                                currentDressImageBackWidth = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH);
                                            }
                                        }

                                        // Высота изображения для текущей одежды с обратной стороны
                                        String currentDressImageBackHeight = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_IMAGE_BACK_HEIGHT)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT) != null) {
                                                currentDressImageBackHeight = currentDressInfo.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT);
                                            }
                                        }

                                        // Цвет текущей одежды
                                        String currentDressColor = "";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_COLOR)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_COLOR) != null) {
                                                currentDressColor = currentDressInfo.get(GlobalFlags.TAG_COLOR);
                                            }
                                        }

                                        // Стиль текущей одежды
                                        String currentDressStyle = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_STYLE)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_STYLE) != null) {
                                                currentDressStyle = currentDressInfo.get(GlobalFlags.TAG_STYLE);
                                            }
                                        }

                                        // Краткое описание для текущей одежды
                                        String currentDressShortDescription = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_SHORT_DESCRIPTION) != null) {
                                                currentDressShortDescription = currentDressInfo.get(GlobalFlags.TAG_SHORT_DESCRIPTION);
                                            }
                                        }

                                        // Полное описание для текущей одежды
                                        String currentDressDescription = null;

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_DESCRIPTION)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_DESCRIPTION) != null) {
                                                currentDressDescription = currentDressInfo.get(GlobalFlags.TAG_DESCRIPTION);
                                            }
                                        }

                                        // Уровень популярности текущей одежды
                                        String currentDressHits = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_HITS)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_HITS) != null) {
                                                currentDressHits = currentDressInfo.get(GlobalFlags.TAG_HITS);
                                            }
                                        }

                                        // Версия информации о текущей вещи
                                        String currentDressVersion = "1";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_VERSION)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_VERSION) != null) {
                                                currentDressVersion = currentDressInfo.get(GlobalFlags.TAG_VERSION);
                                            }
                                        }

                                        // Флаг, определяющий является ли текущая вещь одеждой по умолчанию
                                        String currentDressDefault = "0";

                                        if (currentDressInfo.containsKey(GlobalFlags.TAG_DRESS_DEFAULT)) {
                                            if (currentDressInfo.get(GlobalFlags.TAG_DRESS_DEFAULT) != null) {
                                                currentDressDefault = currentDressInfo.get(GlobalFlags.TAG_DRESS_DEFAULT);
                                            }
                                        }

                                        //----------------------------------------------------------
                                        // Создаем новый HashMap для текущей одежды
                                        HashMap<String, String> mapCurrentDressInfo = new HashMap<>();

                                        // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_ID, currentDressId);                                 // id текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_CATID, currentDressCatId);                           // id категории для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_TITLE, currentDressTitle);                           // название текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_ALIAS, currentDressAlias);                           // алиас названия текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_FOR_WHO, currentDressForWho);                        // для кого предназначена текущая одежда
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_TYPE, currentDressType);                             // тип текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_BRAND_ID, currentDressBrandId);                      // id бренда текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE, currentDressImage);                           // ссылка на изображение для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_WIDTH, currentDressImageWidth);                // ширина изображения для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_HEIGHT, currentDressImageHeight);              // высота изображения для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK, currentDressImageBack);                  // ссылка на изображение для текущей одежды с обратной стороны
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_WIDTH, currentDressImageBackWidth);       // ширина изображения для текущей одежды с обратной стороны
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_IMAGE_BACK_HEIGHT, currentDressImageBackHeight);     // высота изображения для текущей одежды с обратной стороны
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_COLOR, currentDressColor);                           // цвет текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_STYLE, currentDressStyle);                           // стиль текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_SHORT_DESCRIPTION, currentDressShortDescription);    // краткое описание для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_DESCRIPTION, currentDressDescription);               // полное описание для текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_HITS, currentDressHits);                             // уровень популярности текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_VERSION, currentDressVersion);                       // версия информации о текущей одежды
                                        mapCurrentDressInfo.put(GlobalFlags.TAG_DRESS_DEFAULT, currentDressDefault);                 // флаг, определяющий является ли текущая вещь одеждой по умолчанию

                                        //----------------------------------------------------------
                                        // Добавляем или обновляем информации об одежде по умолчанию в локальную БД SQLite
                                        DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_DRESS, currentDressId, mapCurrentDressInfo);

                                        //----------------------------------------------------------
                                        // Сохраняем информацию о категории для текущей одежды
                                        HashMap<String, String> mapCurrentDressCategoryInfo = new HashMap<>();
                                        mapCurrentDressCategoryInfo.put(GlobalFlags.TAG_ID, currentDressCatId);
                                        mapCurrentDressCategoryInfo.put(GlobalFlags.TAG_TITLE, currentDressCategoryTitle);

                                        // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                        DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_CATEGORIES, currentDressCatId, mapCurrentDressCategoryInfo);

                                        //----------------------------------------------------------
                                        // Сохраняем информацию о бренде для текущей одежды
                                        HashMap<String, String> mapCurrentDressBrandInfo = new HashMap<>();
                                        mapCurrentDressBrandInfo.put(GlobalFlags.TAG_ID, currentDressBrandId);
                                        mapCurrentDressBrandInfo.put(GlobalFlags.TAG_TITLE, currentDressBrandTitle);

                                        // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                        DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_BRAND, currentDressBrandId, mapCurrentDressBrandInfo);
                                    }
                                }
                            }
                        }
                    }
                    // Иначе, если не найдено ни одной коллекции
                    else {
                        if(DBMain.getContext() != null) {
                            if (DBMain.getContext().getClass().toString().contains("MainActivity")) {
                                if (((MainActivity) DBMain.getContext()).getFrameLayoutContentMain() != null) {
                                    View viewPageError = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.page_error, ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain(), false);

                                    //--------------------------------------------------------------
                                    if (viewPageError != null) {
                                        // Отображаем сообщение об ошибке
                                        TextView textViewError = (TextView) viewPageError.findViewById(R.id.textViewError);

                                        if (textViewError != null) {
                                            // Устанавливаем шрифт
                                            if (GlobalFlags.getAppTypeface() != null) {
                                                textViewError.setTypeface(GlobalFlags.getAppTypeface());
                                            }

                                            String messageError = DBMain.getContext().getString(R.string.string_dress_no) + '"' + textTitle + '"';
                                            textViewError.setText(messageError);
                                        }

                                        ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().removeAllViews();
                                        ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().addView(viewPageError);
                                    }
                                }
                            }
                        }
                    }
                }
                // Иначе, если возникла ошибка при считывании наборов одежды
                else {
                    if(DBMain.getContext() != null) {
                        if (DBMain.getContext().getClass().toString().contains("MainActivity")) {
                            if (((MainActivity) DBMain.getContext()).getFrameLayoutContentMain() != null) {
                                View viewPageError = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.page_error, ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain(), false);

                                //------------------------------------------------------------------
                                if (viewPageError != null) {
                                    // Отображаем сообщение об ошибке
                                    TextView textViewError = (TextView) viewPageError.findViewById(R.id.textViewError);

                                    if (textViewError != null) {
                                        // Устанавливаем шрифт
                                        if (GlobalFlags.getAppTypeface() != null) {
                                            textViewError.setTypeface(GlobalFlags.getAppTypeface());
                                        }

                                        String messageError = DBMain.getContext().getString(R.string.string_dress_no) + '"' + textTitle + '"';
                                        textViewError.setText(messageError);
                                    }

                                    ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().removeAllViews();
                                    ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().addView(viewPageError);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error Post Execute Dress Collection Load: " + exception.toString());
            }
        }
    }
}
