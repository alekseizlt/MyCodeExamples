package ru.alexprogs.dressroom.db.mysql;

import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.UserDetails;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.db.sqlite.AsyncTaskCheckIsSaveCurrentCollectionFromLocalDB;
import ru.alexprogs.dressroom.httppostrequest.HttpGetPostRequest;
import ru.alexprogs.dressroom.lib.FunctionsConnection;
import ru.alexprogs.dressroom.lib.FunctionsLog;

// Класс, предназначенный для проверки, сохранен ли текущий набор для текущего пользователя
public class MySQLCheckIsSaveCurrentCollection {

    // Свойства данного класса
    private int mCollectionIdForDressShowNow;                   // id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private ImageView mButtonDressSave;                         // кнопка сохранения текущего набора одежды в БД для текущего пользователя
                                                                // после сохранения текущего набора одежды для данной кнопки необходимо поменять изображение

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // buttonDressSave - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    public MySQLCheckIsSaveCurrentCollection(ImageView buttonDressSave) {
        this.setCollectionIdForDressShowNow(0);
        this.setButtonDressSave(buttonDressSave);
    }

    //==============================================================================================
    // Метод для считывания id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private int getCollectionIdForDressShowNow() {
        return this.mCollectionIdForDressShowNow;
    }

    //==============================================================================================
    // Метод для задания id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private void setCollectionIdForDressShowNow(int collectionIdForDressShowNow) {
        this.mCollectionIdForDressShowNow = collectionIdForDressShowNow;
    }

    //==============================================================================================
    // Метод для считывания ссылки на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    private ImageView getButtonDressSave() {
        return this.mButtonDressSave;
    }

    //==============================================================================================
    // Метод для задания ссылки на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    private void setButtonDressSave(ImageView buttonDressSave) {
        this.mButtonDressSave = buttonDressSave;
    }

    //==============================================================================================
    // Метод для запуска проверки, сохранен ли текущий набор для текущего пользователя
    public void startCheckIsSaveCurrentCollection() {
        // Проверяем наличие Интернет-соединения
        Boolean isInternetConnection = FunctionsConnection.isInternetConnection();

        // Если Интернет-соединение присутствует, то загружаем данные об одежде из удаленной БД
        if(isInternetConnection.equals(true)) {
            AsyncTaskCheckIsSaveCurrentCollection asyncTaskCheckIsSaveCurrentCollection = new AsyncTaskCheckIsSaveCurrentCollection();
            asyncTaskCheckIsSaveCurrentCollection.execute();
        }
        // Иначе, если Интернет-соединение отсутствует, то загружаем данные об одежде из локальной БД
        else {
            AsyncTaskCheckIsSaveCurrentCollectionFromLocalDB asyncTaskCheckIsSaveCurrentCollectionFromLocalDB = new AsyncTaskCheckIsSaveCurrentCollectionFromLocalDB(
                    this.getButtonDressSave()
            );

            asyncTaskCheckIsSaveCurrentCollectionFromLocalDB.execute();
        }
    }

    //==============================================================================================
    // Фоновый Async Task для проверки, сохранен ли текущий набор одежды для текущего пользователя, по HTTP запросу
    class AsyncTaskCheckIsSaveCurrentCollection extends AsyncTask<String, Void, Boolean> {

        //------------------------------------------------------------------------------------------
        // Перед началом фонового потока Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //------------------------------------------------------------------------------------------
        // Получаем информацию из url
        protected Boolean doInBackground(String... args) {
            // Возвращаемая логическая переменная, определяющая сохранен ли текущий набор одежды для текущего пользователя
            Boolean isSaveCurrentCollection = false;

            //--------------------------------------------------------------------------------------
            // Массив параметров, передаваемых на сервер
            HashMap<String, String> postDataParams = new HashMap<>();

            postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_CHECK_IS_SAVE_CURRENT_COLLECTION);
            postDataParams.put(GlobalFlags.TAG_USER_ID, String.valueOf(UserDetails.getUserIdServer()));

            // Формируем массив id вещей, которые в данный момент присутствуют на виртуальном манекене,
            // кроме вещей для того типа, для которого необходимо загрузить информацию о новых вещах
            HashMap<String, String> arrayDressListId = DBMain.createArrayListDressId(null);

            // В цикле перебираем все возможные типы одежды
            if (arrayDressListId != null) {
                for (int indexDressType = 0; indexDressType < GlobalFlags.getArrayTagDressType().size(); indexDressType++) {
                    String arrayDressListIdForType = "";

                    if (arrayDressListId.containsKey(GlobalFlags.getArrayTagDressType().get(indexDressType))) {
                        if (arrayDressListId.get(GlobalFlags.getArrayTagDressType().get(indexDressType)) != null) {
                            arrayDressListIdForType = arrayDressListId.get(GlobalFlags.getArrayTagDressType().get(indexDressType)).trim();
                        }
                    }

                    postDataParams.put(GlobalFlags.getArrayTagDressType().get(indexDressType), arrayDressListIdForType);
                }
            }

            //--------------------------------------------------------------------------------------
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
                FunctionsLog.logPrint("JSON Parser Error (Check Is Save Current Collection): " + exception.toString());
                return false;
            }
            catch(Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Check Is Save Current Collection): " + exception.toString());
                return false;
            }

            //--------------------------------------------------------------------------------------
            // Разбираем ответ от сервера
            try {
                // Считываем id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
                int collectionIdForDressShowNow = 0;

                if (!jSONObject.isNull(GlobalFlags.TAG_COLLECTION_ID)) {
                    collectionIdForDressShowNow = jSONObject.getInt(GlobalFlags.TAG_COLLECTION_ID);
                }

                MySQLCheckIsSaveCurrentCollection.this.setCollectionIdForDressShowNow(collectionIdForDressShowNow);

                //----------------------------------------------------------------------------------
                if(MySQLCheckIsSaveCurrentCollection.this.getCollectionIdForDressShowNow() > 0) {
                    isSaveCurrentCollection = true;
                }
            }
            catch (JSONException exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("JSON Parser Error (Check Is Save Current Collection): " + exception.toString());
                return false;
            }
            catch(Exception exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error (Check Is Save Current Collection): " + exception.toString());
                return false;
            }

            return isSaveCurrentCollection;
        }

        //------------------------------------------------------------------------------------------
        // После завершения фоновой задачи
        protected void onPostExecute(Boolean resultIsSaveCurrentCollection) {
            super.onPostExecute(resultIsSaveCurrentCollection);

            //--------------------------------------------------------------------------------------
            // Делаем выделенной или нет кнопку сохранения информации о текущем наборе одежды
            if (MySQLCheckIsSaveCurrentCollection.this.getButtonDressSave() != null) {
                // Если текущий набор одежды был СОХРАНЕН для текущего пользователя
                if(MySQLCheckIsSaveCurrentCollection.this.getCollectionIdForDressShowNow() > 0) {
                    // Меняем изображение для кнопки сохранения текущего набора одежды
                    MySQLCheckIsSaveCurrentCollection.this.getButtonDressSave().setImageResource(R.drawable.favorite2);

                    // Устанавливаем id текущего набора одежды в качестве тега для соответствующей кнопки сохранения
                    MySQLCheckIsSaveCurrentCollection.this.getButtonDressSave().setTag(MySQLCheckIsSaveCurrentCollection.this.getCollectionIdForDressShowNow());
                }
                // Иначе
                else {
                    // Меняем изображение для кнопки сохранения текущего набора одежды
                    MySQLCheckIsSaveCurrentCollection.this.getButtonDressSave().setImageResource(R.drawable.favorite);

                    // Устанавливаем 0 в качестве тега для соответствующей кнопки сохранения
                    MySQLCheckIsSaveCurrentCollection.this.getButtonDressSave().setTag(0);
                }
            }
        }
    }
}
