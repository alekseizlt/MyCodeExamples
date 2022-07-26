package ru.alexprogs.dressroom.db.mysql;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.UserDetails;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.db.sqlite.AsyncTaskLoadDressCollectionInfoSwipeFromLocalDB;
import ru.alexprogs.dressroom.httppostrequest.HttpGetPostRequest;
import ru.alexprogs.dressroom.lib.FunctionsConnection;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.FunctionsString;

// клаас для считывания информации о дополнительной коллекции одежды при листании коллекций одежды
public class MySQLGoToDressCollectionSwipe {

    // Свойства данного класса
    private int mDressCollectionId;                         // id крайней (первой или последней) коллекции одежды
    private int mSwipeDirection;                            // направление листание коллекций одежды пальцем

    //==============================================================================================
    // Конструктор
    public MySQLGoToDressCollectionSwipe() {
    }

    //==============================================================================================
    // Метод для считывания id крайней (первой или последней) коллекции одежды
    private int getDressCollectionId() {
        return this.mDressCollectionId;
    }

    //==============================================================================================
    // Метод для задания id крайней (первой или последней) коллекции одежды
    private void setDressCollectionId(int dressCollectionId) {
        this.mDressCollectionId = dressCollectionId;
    }

    //==============================================================================================
    // Метод для считывания значения переменной, определяющей направление листания коллекций одежды пальцем
    private int getSwipeDirection() {
        return this.mSwipeDirection;
    }

    //==============================================================================================
    // Метод для задания значения переменной, определяющей направление листания коллекций одежды пальцем
    private void setSwipeDirection(int swipeDirection) {
        // Заметим, что возможные значения - это "left_to_right" или "right_to_left"
        // Если переданное в текущую функцию значение не совпадает ни с одним из разрешенных,
        // то устанавливаем в качестве значения по умолчанию - значение "left_to_right"
        if (swipeDirection != GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT && swipeDirection != GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT) {
            this.mSwipeDirection = GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT;
        }
        // Иначе
        else {
            this.mSwipeDirection = swipeDirection;
        }
    }

    //==============================================================================================
    // Метод, запускающий процесс считывания данных о коллекции одежды при листании коллекций одежды пальцем
    // Передаваемые параметры
    // swipeDirection - направление листание коллекций одежды пальцем
    public void startGoToDressCollectionSwipe(int swipeDirection) {
        // Задаем направление листания коллекций одежды пальцем
        this.setSwipeDirection(swipeDirection);

        //------------------------------------------------------------------------------------------
        // Определяем id коллекции одежды, информацию о которой необходимо считать из БД, в зависимости от направления листания
        if(DBMain.getPagerAdapterDressCollection() != null) {
            switch (this.getSwipeDirection()) {
                case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                                         // листание слева направо
                    // Считываем id для самой первой коллекции одежды
                    int firstDressCollectionId = 0;

                    if (DBMain.getPagerAdapterDressCollection().getItemParamsId(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart()) != null) {
                        firstDressCollectionId = DBMain.getPagerAdapterDressCollection().getItemParamsId(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart());
                    }

                    // Задаем id для самой первой коллекции одежды
                    this.setDressCollectionId(firstDressCollectionId);

                    //------------------------------------------------------------------------------
                    // Уменьшаем начальный и конечный индексы присутствия параметров
                    // в массиве параметров пунктов для соответствующего адаптера на -1
                    if(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() > 0) {
                        DBMain.getPagerAdapterDressCollection().setArrayParamsPositionStart(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() - 1);
                        DBMain.getPagerAdapterDressCollection().setArrayParamsPositionEnd(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() - 1);
                    }

                    break;

                case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:
                    // Считываем id для самой последней коллекции одежды
                    int lastDressCollectionId = 0;

                    if (DBMain.getPagerAdapterDressCollection().getItemParamsId(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd()) != null) {
                        lastDressCollectionId = DBMain.getPagerAdapterDressCollection().getItemParamsId(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd());
                    }

                    // Задаем id для самой последней коллекции одежды
                    this.setDressCollectionId(lastDressCollectionId);

                    // Увеличиваем начальный и конечный индексы присутствия параметров
                    // в массиве параметров пунктов для соответствующего адаптера на +1
                    DBMain.getPagerAdapterDressCollection().setArrayParamsPositionStart(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() + 1);
                    DBMain.getPagerAdapterDressCollection().setArrayParamsPositionEnd(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() + 1);

                    break;
            }
        }

        //------------------------------------------------------------------------------------------
        // Если успешно считан id коллекции одежды
        if(this.getDressCollectionId() > 0) {
            // Проверяем наличие Интернет-соединения
            Boolean isInternetConnection = FunctionsConnection.isInternetConnection();

            // Если Интернет-соединение присутствует, то загружаем данные о коллекции одежды из удаленной БД
            if (isInternetConnection.equals(true)) {
                AsyncTaskLoadDressCollectionInfo asyncTaskLoadDressCollectionInfo = new AsyncTaskLoadDressCollectionInfo();
                asyncTaskLoadDressCollectionInfo.execute();
            }
            // Иначе, если Интернет-соединение отсутствует, то загружаем данные о коллекции одежды из локальной БД
            else {
                AsyncTaskLoadDressCollectionInfoSwipeFromLocalDB asyncTaskLoadDressCollectionInfoSwipeFromLocalDB = new AsyncTaskLoadDressCollectionInfoSwipeFromLocalDB(
                        this.getDressCollectionId(),
                        swipeDirection
                );

                asyncTaskLoadDressCollectionInfoSwipeFromLocalDB.execute();
            }
        }
    }

    //==============================================================================================
    // Фоновый Async Task для загрузки данных о коллекции одежды
    class AsyncTaskLoadDressCollectionInfo extends AsyncTask<String, Void, HashMap<String, ArrayList<HashMap<String, String>>>> {

        //------------------------------------------------------------------------------------------
        // Перед началом фонового потока
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //------------------------------------------------------------------------------------------
        // Получаем информацию о текущем отображаемом наборе (коллекции) одежды
        protected HashMap<String, ArrayList<HashMap<String, String>>> doInBackground(String... args) {
            // Массив, возвращаемый в качестве результата выполнения текущей функции
            HashMap<String, ArrayList<HashMap<String, String>>> returnArrayCollectionsInfo = null;

            // Массив параметров, передаваемых на сервер
            HashMap<String, String> postDataParams = new HashMap<>();

            postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_DRESS_COLLECTION_SWIPE);
            postDataParams.put(GlobalFlags.TAG_USER_ID, String.valueOf(UserDetails.getUserIdServer()));
            postDataParams.put(GlobalFlags.TAG_COLLECTION_ID, String.valueOf(MySQLGoToDressCollectionSwipe.this.getDressCollectionId()));
            postDataParams.put(GlobalFlags.TAG_SWIPE_DIRECTION, String.valueOf(MySQLGoToDressCollectionSwipe.this.getSwipeDirection()));

            if(DBMain.getMySQLDressCollectionLoad() != null) {
                if(DBMain.getMySQLDressCollectionLoad().getDressCategoryId() != null) {
                    postDataParams.put(GlobalFlags.TAG_CATID, DBMain.getMySQLDressCollectionLoad().getDressCategoryId());
                }
            }

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
                FunctionsLog.logPrint("JSON Parser Error (Go To Dress Collection Swipe): " + exception.toString());
                return null;
            }
            catch(Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Go To Dress Collection Swipe): " + exception.toString());
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
                    if(DBMain.getMySQLDressCollectionLoad() != null) {
                        if (DBMain.getMySQLDressCollectionLoad().getDressCategoryId() != null) {
                            // Получаем JSON объект для всей одежды для соответствующей категории
                            if (!jSONObject.isNull(GlobalFlags.TAG_DRESS)) {
                                JSONArray jSONArrayDress = jSONObject.getJSONArray(GlobalFlags.TAG_DRESS);

                                // В цикле разбираем одежду, информация о которой считана из БД
                                for (int indexDress = 0; indexDress < jSONArrayDress.length(); indexDress++) {
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
                                    // Сохраняем информацию о данной вещи в возвращаемом массиве
                                    if (returnArrayCollectionsInfo == null) {
                                        returnArrayCollectionsInfo = new HashMap<>();
                                    }

                                    returnArrayCollectionsInfo.put(currentDressType, arrayDressForCurrentCollectionForType);

                                    //------------------------------------------------------------------
                                    // Добавляем в массив, содержащий сведения о текущей коллекции одежды,
                                    // дополнительные сведения
                                    HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);             // id текущего набора одежды

                                    ArrayList<HashMap<String, String>> arrayCurrentCollectionInfo = new ArrayList<>();
                                    arrayCurrentCollectionInfo.add(mapCurrentCollectionInfo);

                                    returnArrayCollectionsInfo.put(GlobalFlags.TAG_COLLECTION, arrayCurrentCollectionInfo);
                                }
                            }
                        }
                        // Иначе
                        else {
                            // Получаем JSON объект для необходимой коллекции одежды
                            if (!jSONObject.isNull(GlobalFlags.TAG_COLLECTION)) {
                                JSONArray jSONArrayCollection = jSONObject.getJSONArray(GlobalFlags.TAG_COLLECTION);

                                // С учетом, что из БД считана информация только об одной коллекции одежды
                                if (jSONArrayCollection != null) {
                                    if (!jSONArrayCollection.isNull(0)) {
                                        JSONObject jSONCurrentCollection = jSONArrayCollection.getJSONObject(0);

                                        //--------------------------------------------------------------
                                        // Считываем информацию о текущем наборе одежды

                                        // id текущего набора одежды
                                        String currentDressCollectionId = "0";

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_ID)) {
                                            currentDressCollectionId = jSONCurrentCollection.getString(GlobalFlags.TAG_ID);
                                        }

                                        // Название текущего набора одежды
                                        String currentDressCollectionTitle = null;

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_TITLE)) {
                                            currentDressCollectionTitle = FunctionsString.jsonDecode(jSONCurrentCollection.getString(GlobalFlags.TAG_TITLE));
                                        }

                                        // Алиас название текущего набора одежды
                                        String currentDressCollectionAlias = null;

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_ALIAS)) {
                                            currentDressCollectionAlias = jSONCurrentCollection.getString(GlobalFlags.TAG_ALIAS);
                                        }

                                        // Тип текущего набора одежды
                                        String currentDressCollectionType = GlobalFlags.DRESS_COLLECTION_TYPE_COLLECTION;

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_TYPE)) {
                                            currentDressCollectionType = jSONCurrentCollection.getString(GlobalFlags.TAG_TYPE);
                                        }

                                        // Краткое описание для текущей одежды
                                        String currentDressCollectionShortDescription = null;

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                                            currentDressCollectionShortDescription = FunctionsString.jsonDecode(jSONCurrentCollection.getString(GlobalFlags.TAG_SHORT_DESCRIPTION));
                                        }

                                        // Полное описание для текущей одежды
                                        String currentDressCollectionDescription = null;

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_DESCRIPTION)) {
                                            currentDressCollectionDescription = FunctionsString.jsonDecode(jSONCurrentCollection.getString(GlobalFlags.TAG_DESCRIPTION));
                                        }

                                        // Версия информации о текущей вещи
                                        String currentDressCollectionVersion = "1";

                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_VERSION)) {
                                            currentDressCollectionVersion = jSONCurrentCollection.getString(GlobalFlags.TAG_VERSION);
                                        }

                                        //--------------------------------------------------------------
                                        // Создаем новый HashMap
                                        HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                                        // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);                                 // id текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_TITLE, currentDressCollectionTitle);                           // название текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_ALIAS, currentDressCollectionAlias);                           // алиас названия текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_TYPE, currentDressCollectionType);                             // тип текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_SHORT_DESCRIPTION, currentDressCollectionShortDescription);    // краткое описание текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_DESCRIPTION, currentDressCollectionDescription);               // краткое описание текущего набора одежды
                                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_VERSION, currentDressCollectionVersion);                       // версия информации о текущем наборе одежды

                                        //--------------------------------------------------------------
                                        // Добавляем или обновляем информации о текущей коллекции одежды в локальную БД SQLite
                                        DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_COLLECTION, currentDressCollectionId, mapCurrentCollectionInfo);

                                        //--------------------------------------------------------------
                                        // Удаляем из локальной БД сведения о соответствии
                                        // текущего набора одежды и текущей вещи
                                        DBMain.getDBSQLiteHelper().deleteRecordFromDB(
                                                GlobalFlags.TAG_TABLE_COLLECTION_DRESS,
                                                GlobalFlags.TAG_COLLECTION_ID + " = ?",
                                                new String[]{currentDressCollectionId}
                                        );

                                        //--------------------------------------------------------------
                                        // Формируем HashMap, содержащий информацию о текущем наборе одежды
                                        // для возвращаемого массива
                                        HashMap<String, String> returnMapCurrentCollectionInfo = new HashMap<>();

                                        // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                        returnMapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, currentDressCollectionId);                             // id текущего набора одежды

                                        ArrayList<HashMap<String, String>> returnArrayCurrentCollectionInfo = new ArrayList<>();
                                        returnArrayCurrentCollectionInfo.add(returnMapCurrentCollectionInfo);

                                        //--------------------------------------------------------------
                                        // Сохраняем информацию о текущей коллекции одежды в глобальном массиве
                                        if (returnArrayCollectionsInfo == null) {
                                            returnArrayCollectionsInfo = new HashMap<>();
                                        }

                                        returnArrayCollectionsInfo.put(GlobalFlags.TAG_COLLECTION, returnArrayCurrentCollectionInfo);

                                        //--------------------------------------------------------------
                                        // Считываем информацию об одежде для текущего набора одежды
                                        if (!jSONCurrentCollection.isNull(GlobalFlags.TAG_DRESS)) {
                                            JSONObject jSONObjectDressInCurrentCollection = jSONCurrentCollection.getJSONObject(GlobalFlags.TAG_DRESS);

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

                                                        //----------------------------------------------
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

                                                        //----------------------------------------------
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

                                                        //----------------------------------------------
                                                        // Добавляем информацию о соответствии текущего набора одежды
                                                        // и текущей вещи в локальную БД SQLite
                                                        HashMap<String, String> mapCurrentDressCollectionInfo = new HashMap<>();
                                                        mapCurrentDressCollectionInfo.put(GlobalFlags.TAG_COLLECTION_ID, currentDressCollectionId);
                                                        mapCurrentDressCollectionInfo.put(GlobalFlags.TAG_DRESS_ID, currentDressId);

                                                        // Добавляем или обновляем информации в локальную БД SQLite
                                                        DBMain.getDBSQLiteHelper().insertRecordToDB(GlobalFlags.TAG_TABLE_COLLECTION_DRESS, mapCurrentDressCollectionInfo);

                                                        //----------------------------------------------
                                                        // добавляем HashList в ArrayList
                                                        arrayDressForCurrentCollectionForType.add(mapCurrentDressInfo);
                                                    }

                                                    //--------------------------------------------------
                                                    // Сохраняем информацию об одежде для текущего типа
                                                    // в глобальном массиве
                                                    returnArrayCollectionsInfo.put(CURRENT_TAG_DRESS_TYPE, arrayDressForCurrentCollectionForType);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (JSONException exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("JSON Parser Error (Go To Dress Collection Swipe): " + exception.toString());
                return null;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Go To Dress Collection Swipe): " + exception.toString());
                return null;
            }

            return returnArrayCollectionsInfo;
        }

        //------------------------------------------------------------------------------------------
        // После завершения фоновой задачи
        protected void onPostExecute(HashMap<String, ArrayList<HashMap<String, String>>> resultArrayDressInfo) {
            super.onPostExecute(resultArrayDressInfo);

            //--------------------------------------------------------------------------------------
            try {
                // Если многомерный массив, хранящий информацию о текущем наборе одежды, НЕ пуст
                if (resultArrayDressInfo != null) {
                    // Если количество коллекций одежды больше 0
                    if (resultArrayDressInfo.size() > 0) {
                        // Формируем массив, содержащий только НЕОБХОДИМУЮ информацию об одежде из текущего набора одежды
                        HashMap<String, ArrayList<HashMap<String, String>>> arrayCollectionsInfoForAdapter = new HashMap<>();

                        // Считываем набор ключей (присутствующих типов одежды) для текущей коллекции
                        Collection<String> arrayCurrentCollectionInfoKeyCollection = resultArrayDressInfo.keySet();

                        for (String arrayCurrentCollectionInfoKey : arrayCurrentCollectionInfoKeyCollection) {
                            // Массив, содержащий сведения об одежде для текущего типа
                            ArrayList<HashMap<String, String>> arrayDressInfoForCurrentGroup = resultArrayDressInfo.get(arrayCurrentCollectionInfoKey);

                            if(arrayCurrentCollectionInfoKey.equals(GlobalFlags.TAG_COLLECTION)) {
                                arrayCollectionsInfoForAdapter.put(GlobalFlags.TAG_COLLECTION, arrayDressInfoForCurrentGroup);
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

                                    //--------------------------------------------------------------
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

                                arrayCollectionsInfoForAdapter.put(arrayCurrentCollectionInfoKey, arrayDressInfoForCurrentGroupForAdapter);
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Считываем массив параметров для текущего адаптера
                        ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> currentPagerAdapterDressCollectionItemsParams = null;

                        if(DBMain.getPagerAdapterDressCollection() != null) {
                            currentPagerAdapterDressCollectionItemsParams = DBMain.getPagerAdapterDressCollection().getArrayParams();
                        }

                        //--------------------------------------------------------------------------
                        // Итоговый конечный массив, содержащий параметры для текущего адаптера
                        ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> currentPagerAdapterDressCollectionItemsParamsNew;

                        if(DBMain.getPagerAdapterDressCollection() != null && currentPagerAdapterDressCollectionItemsParams != null) {
                            currentPagerAdapterDressCollectionItemsParamsNew = new ArrayList<>();

                            // В зависимости от направления листания выполняем соответствующие действия
                            // вышеуказанным массивом параметров
                            switch (MySQLGoToDressCollectionSwipe.this.getSwipeDirection()) {
                                case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                         // листание слева направо
                                    for(int indexItem = 0; indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                    }

                                    //--------------------------------------------------------------
                                    // Добавляем в начало текущую, считанную информацию о коллекции одежды
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(arrayCollectionsInfoForAdapter);

                                    //--------------------------------------------------------------
                                    // Определяем последний индекс, для которого присутствуют данные
                                    // в массиве currentPagerAdapterDressCollectionItemsParams
                                    int indexItemEnd = currentPagerAdapterDressCollectionItemsParams.size() - 1;

                                    if(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() < currentPagerAdapterDressCollectionItemsParams.size() - 1) {
                                        indexItemEnd = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd();
                                    }

                                    //--------------------------------------------------------------
                                    // Сохраняем все элементы массива currentPagerAdapterDressroomItemsParams кроме первого
                                    for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() + 1; indexItem <= indexItemEnd; indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(currentPagerAdapterDressCollectionItemsParams.get(indexItem));
                                    }

                                    //--------------------------------------------------------------
                                    for(int indexItem = indexItemEnd + 1; indexItem < currentPagerAdapterDressCollectionItemsParams.size() - 1; indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                    }

                                    break;

                                case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                         // листание справа налево
                                    for(int indexItem = 0; indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                    }

                                    //--------------------------------------------------------------
                                    // Сохраняем все элементы массива currentPagerAdapterDressroomItemsParams кроме первого
                                    for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd(); indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(currentPagerAdapterDressCollectionItemsParams.get(indexItem));
                                    }

                                    //--------------------------------------------------------------
                                    // Добавляем в конец текущую, считанную информацию об одежде
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(arrayCollectionsInfoForAdapter);

                                    //--------------------------------------------------------------
                                    for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() + 1; indexItem < currentPagerAdapterDressCollectionItemsParams.size() - 1; indexItem++) {
                                        currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                    }

                                    break;
                            }

                            //----------------------------------------------------------------------
                            // Обновляем текущий адаптер
                            if(currentPagerAdapterDressCollectionItemsParamsNew != null) {
                                DBMain.getPagerAdapterDressCollection().setArrayParams(currentPagerAdapterDressCollectionItemsParamsNew);
                                DBMain.getPagerAdapterDressCollection().notifyDataSetChanged();
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Сохраняем данные о считанной из удаленной БД одежде в локальную БД SQLite
                        for (String arrayCurrentCollectionInfoKey : arrayCurrentCollectionInfoKeyCollection) {
                            if(!arrayCurrentCollectionInfoKey.equals(GlobalFlags.TAG_COLLECTION)) {
                                // Массив, содержащий сведения об одежде для текущего типа
                                ArrayList<HashMap<String, String>> arrayDressInfoForCurrentGroup = resultArrayDressInfo.get(arrayCurrentCollectionInfoKey);

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

                                    //--------------------------------------------------------------
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

                                    //--------------------------------------------------------------
                                    // Добавляем или обновляем информации об одежде по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_DRESS, currentDressId, mapCurrentDressInfo);

                                    //--------------------------------------------------------------
                                    // Сохраняем информацию о категории для текущей одежды
                                    HashMap<String, String> mapCurrentDressCategoryInfo = new HashMap<>();
                                    mapCurrentDressCategoryInfo.put(GlobalFlags.TAG_ID, currentDressCatId);
                                    mapCurrentDressCategoryInfo.put(GlobalFlags.TAG_TITLE, currentDressCategoryTitle);

                                    // Добавляем или обновляем информации о категории для текущей одежды по умолчанию в локальную БД SQLite
                                    DBMain.getDBSQLiteHelper().updateOrInsertRecordToDBByIdServerMySQL(GlobalFlags.TAG_TABLE_CATEGORIES, currentDressCatId, mapCurrentDressCategoryInfo);

                                    //--------------------------------------------------------------
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
            }
            catch (Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error Post Execute (Go To Dress Collection Swipe): " + exception.toString());
            }
        }
    }
}
