package ru.alexprogs.dressroom.db.sqlite;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.MainActivity;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.adapter.PagerAdapterDressCollection;
import ru.alexprogs.dressroom.asynctasks.AsyncTaskCheckInternetConnection;
import ru.alexprogs.dressroom.db.DBMain;

import ru.alexprogs.dressroom.lib.FunctionsLog;

// Класс для загрузки информации о коллекциях одежды для текущего пользователя из локальной БД
public class AsyncTaskLoadDressCollectionInfoFromLocalDB extends AsyncTask<String, Void, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> {

    private ProgressDialog mProgressDialogDressCollectionLoad;  // ссылка на модальное окно, отображающее процесс загрузки данных из локальной БД
    private String mDressCategoryId;                            // id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    private String mDressCategoryTitle;                         // название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // dressCategoryId - id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    // dressCategoryTitle - название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
    public AsyncTaskLoadDressCollectionInfoFromLocalDB(String dressCategoryId, String dressCategoryTitle) {
        // Задаем id категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
        this.setDressCategoryId(dressCategoryId);

        // Задаем название категории, для которой необходимо считать инфо об одежде, входящей в состав наборов одежды для текущего пользователя
        this.setDressCategoryTitle(dressCategoryTitle);
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
    // Метод для считывания id категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    private String getDressCategoryId() {
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
    public String getDressCategoryTitle() {
        return this.mDressCategoryTitle;
    }

    //==============================================================================================
    // Метод для задания id категории, для которой необходимо считать инфо об одежде,
    // входящей в состав наборов одежды для текущего пользователя
    private void setDressCategoryTitle(String dressCategoryTitle) {
        this.mDressCategoryTitle = dressCategoryTitle;
    }

    //==============================================================================================
    // Перед началом фонового потока
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.setProgressDialogDressCollectionLoad(new ProgressDialog(DBMain.getContext()));
        this.getProgressDialogDressCollectionLoad().setMessage(DBMain.getContext().getResources().getString(R.string.string_title_progressdialog_load_data));
        this.getProgressDialogDressCollectionLoad().setIndeterminate(false);
        this.getProgressDialogDressCollectionLoad().setCancelable(false);
        this.getProgressDialogDressCollectionLoad().show();
    }

    //==============================================================================================
    // Считываем информацию о коллекциях одежды для текущего пользователя из локальной БД
    protected ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> doInBackground(String... args) {
        // Массив, возвращаемый в качестве результата выполнения текущей функции
        ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> returnArrayCollectionsInfo = null;

        //------------------------------------------------------------------------------------------
        if(DBMain.getDBSQLiteHelper() == null) {
            DBMain.setDBSQLiteHelper(new DBSQLiteHelper(DBMain.getContext()));
        }

        //------------------------------------------------------------------------------------------
        // Если информация считывалась для определенной категории
        if(this.getDressCategoryId() != null) {
            // Считываем id всех коллекций для текущего пользователя
            ArrayList<HashMap<String, String>> arrayCollectionsId = DBMain.getDBSQLiteHelper().getAllRecordsFromDB(
                    GlobalFlags.TAG_TABLE_COLLECTION,
                    new String[]{GlobalFlags.TAG_ID},
                    GlobalFlags.TAG_ID
            );

            // Если id коллекций одежды для текущего пользователя успешно считаны
            if (arrayCollectionsId != null) {
                // Теперь формируем массив, содержащий id всей одежды, входящей в состав всех коллекций
                // для текущего пользователя
                ArrayList<String> arrayDressIdInCollectionForCurrentUser = null;

                // Перебираем в цикле все коллекции одежды для текущего пользователя
                for (int indexCollection = 0; indexCollection < arrayCollectionsId.size(); indexCollection++) {
                    if (arrayCollectionsId.get(indexCollection) != null) {
                        // Считываем id всей одежды, присутствующей в данном наборе одежды
                        ArrayList<HashMap<String, String>> arrayDressIdForCurrentCollection = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                                GlobalFlags.TAG_TABLE_COLLECTION_DRESS,
                                new String[]{GlobalFlags.TAG_DRESS_ID},
                                GlobalFlags.TAG_COLLECTION_ID + " = ?",
                                new String[]{arrayCollectionsId.get(indexCollection).get(GlobalFlags.TAG_ID)},
                                GlobalFlags.TAG_DRESS_ID,
                                null
                        );

                        // Заполняем массив arrayDressIdInCollectionForCurrentUser
                        if (arrayDressIdForCurrentCollection != null) {
                            for (int indexDress = 0; indexDress < arrayDressIdForCurrentCollection.size(); indexDress++) {
                                if (arrayDressIdForCurrentCollection.get(indexDress) != null) {
                                    if (arrayDressIdForCurrentCollection.get(indexDress).containsKey(GlobalFlags.TAG_DRESS_ID)) {
                                        if (arrayDressIdForCurrentCollection.get(indexDress).get(GlobalFlags.TAG_DRESS_ID) != null) {
                                            if (arrayDressIdInCollectionForCurrentUser == null) {
                                                arrayDressIdInCollectionForCurrentUser = new ArrayList<>();
                                            }

                                            // Если в массиве arrayDressIdInCollectionForCurrentUser не содержится
                                            // еще данной значение id одежды
                                            if (!arrayDressIdInCollectionForCurrentUser.contains(arrayDressIdForCurrentCollection.get(indexDress).get(GlobalFlags.TAG_DRESS_ID))) {
                                                arrayDressIdInCollectionForCurrentUser.add(arrayDressIdForCurrentCollection.get(indexDress).get(GlobalFlags.TAG_DRESS_ID));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //----------------------------------------------------------------------------------
                // Теперь из массива, хранящего id для всей одежды для всех коллекций для текущего пользователя,
                // отфильтровываем только те вещи, которые относятся к искомой категории
                if (arrayDressIdInCollectionForCurrentUser != null) {
                    ArrayList<Integer> arrayDressIdForCurrentUser = null;

                    // Формируем запрос к БД
                    String where = "";
                    String whereArgsString = "";

                    for (int indexDress = 0; indexDress < arrayDressIdInCollectionForCurrentUser.size(); indexDress++) {
                        if (indexDress == 0) {
                            where += "(";
                        }

                        where += GlobalFlags.TAG_ID + " = ?";
                        whereArgsString += arrayDressIdInCollectionForCurrentUser.get(indexDress) + "___";

                        if (indexDress < arrayDressIdInCollectionForCurrentUser.size() - 1) {
                            where += " OR ";
                        }

                        if (indexDress == arrayDressIdInCollectionForCurrentUser.size() - 1) {
                            where += ")";
                        }
                    }

                    where += " AND catid = ?";
                    whereArgsString += this.getDressCategoryId();

                    String[] whereArgs = whereArgsString.split("___");

                    // Выполняем непосредственно запрос к БД
                    ArrayList<HashMap<String, String>> arrayDressFilter = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                            GlobalFlags.TAG_TABLE_DRESS,
                            new String[]{GlobalFlags.TAG_ID},
                            where,
                            whereArgs,
                            GlobalFlags.TAG_ID,
                            null
                    );

                    // Теперь заполняем массив arrayDressIdForCurrentUser
                    if (arrayDressFilter != null) {
                        for (int indexDress = 0; indexDress < arrayDressFilter.size(); indexDress++) {
                            if (arrayDressFilter.get(indexDress) != null) {
                                if (arrayDressFilter.get(indexDress).containsKey(GlobalFlags.TAG_ID)) {
                                    if (arrayDressFilter.get(indexDress).get(GlobalFlags.TAG_ID) != null) {
                                        if (arrayDressIdForCurrentUser == null) {
                                            arrayDressIdForCurrentUser = new ArrayList<>();
                                        }

                                        arrayDressIdForCurrentUser.add(Integer.parseInt(arrayDressFilter.get(indexDress).get(GlobalFlags.TAG_ID)));
                                    }
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    if (arrayDressIdForCurrentUser != null) {
                        // Определяем количество вещей из массива $array_dress_id_for_current_user,
                        // информацию о которых необходимо считать из БД
                        int lastIndexDress = arrayDressIdForCurrentUser.size();

                        if (GlobalFlags.COUNT_DRESS_READ_FROM_DB < arrayDressIdForCurrentUser.size()) {
                            lastIndexDress = GlobalFlags.COUNT_DRESS_READ_FROM_DB;
                        }

                        //--------------------------------------------------------------------------
                        // В цикле считываем полную информацию о необходимой одежде
                        for (int indexDress = 0; indexDress < lastIndexDress; indexDress++) {
                            // Выполняем запрос к БД
                            ArrayList<HashMap<String, String>> arrayRequireDressInfo = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                                    GlobalFlags.TAG_TABLE_DRESS,
                                    new String[]{
                                            GlobalFlags.TAG_ID,
                                            GlobalFlags.TAG_CATID,
                                            GlobalFlags.TAG_FOR_WHO,
                                            GlobalFlags.TAG_TYPE,
                                            GlobalFlags.TAG_BRAND_ID,
                                            GlobalFlags.TAG_IMAGE,
                                            GlobalFlags.TAG_IMAGE_WIDTH,
                                            GlobalFlags.TAG_IMAGE_HEIGHT,
                                            GlobalFlags.TAG_IMAGE_BACK,
                                            GlobalFlags.TAG_IMAGE_BACK_WIDTH,
                                            GlobalFlags.TAG_IMAGE_BACK_HEIGHT,
                                            GlobalFlags.TAG_COLOR,
                                            GlobalFlags.TAG_STYLE
                                    },
                                    GlobalFlags.TAG_ID + " = ?",
                                    new String[]{String.valueOf(arrayDressIdForCurrentUser.get(indexDress))},
                                    GlobalFlags.TAG_ID,
                                    "1"
                            );

                            //----------------------------------------------------------------------
                            // Сохраняем информацию о текущей одежде в возвращаемом массиве
                            if (arrayRequireDressInfo != null) {
                                if (arrayRequireDressInfo.get(0) != null) {
                                    if(returnArrayCollectionsInfo == null) {
                                        returnArrayCollectionsInfo = new ArrayList<>();
                                    }

                                    //--------------------------------------------------------------
                                    HashMap<String, ArrayList<HashMap<String, String>>> returnMapCurrentCollectionInfo = new HashMap<>();

                                    //--------------------------------------------------------------
                                    // Формируем HashMap, содержащий информацию о текущем наборе одежды
                                    // для возвращаемого массива
                                    HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                                    // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                    mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, arrayRequireDressInfo.get(0).get(GlobalFlags.TAG_ID));       // id текущего набора одежды

                                    ArrayList<HashMap<String, String>> returnArrayCurrentCollectionInfo = new ArrayList<>();
                                    returnArrayCurrentCollectionInfo.add(mapCurrentCollectionInfo);

                                    // Сохраняем информацию о текущей коллекции одежды в глобальном массиве
                                    returnMapCurrentCollectionInfo.put(GlobalFlags.TAG_COLLECTION, returnArrayCurrentCollectionInfo);

                                    //--------------------------------------------------------------
                                    // Добавляем в возвращаемый массив информацию о текущей вещи
                                    returnMapCurrentCollectionInfo.put(
                                            arrayRequireDressInfo.get(0).get(GlobalFlags.TAG_TYPE),
                                            arrayRequireDressInfo
                                    );

                                    //--------------------------------------------------------------
                                    returnArrayCollectionsInfo.add(returnMapCurrentCollectionInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
        // Иначе, если считывается информация полностью для всей одежды из коллекции
        else {
            // Извлекаем id все коллекций одежды для текущего пользователя
            ArrayList<HashMap<String, String>> arrayCollectionsId = DBMain.getDBSQLiteHelper().getAllRecordsFromDB(
                    GlobalFlags.TAG_TABLE_COLLECTION,
                    new String[]{GlobalFlags.TAG_ID},
                    GlobalFlags.TAG_ID
            );

            if (arrayCollectionsId != null) {
                // Определяем количество коллекций, информацию о которых необходимо считать из БД
                int lastIndexCollection = arrayCollectionsId.size();

                if (GlobalFlags.COUNT_DRESS_READ_FROM_DB < arrayCollectionsId.size()) {
                    lastIndexCollection = GlobalFlags.COUNT_DRESS_READ_FROM_DB;
                }

                // В массиве перебираем все коллекции одежды
                for (int indexCollection = 0; indexCollection < lastIndexCollection; indexCollection++) {
                    if(arrayCollectionsId.get(indexCollection) != null) {
                        // Массив, содержащий информацию для текущей коллекции
                        HashMap<String, ArrayList<HashMap<String, String>>> arrayCurrentCollectionInfo = new HashMap<>();

                        //--------------------------------------------------------------------------
                        // Формируем HashMap, содержащий информацию о текущем наборе одежды
                        HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                        // Добавляем каждый елемент в HashMap ключ => значение
                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, arrayCollectionsId.get(indexCollection).get(GlobalFlags.TAG_ID));          // id текущего набора одежды

                        ArrayList<HashMap<String, String>> arrayInfoForCurrentCollection = new ArrayList<>();
                        arrayInfoForCurrentCollection.add(mapCurrentCollectionInfo);

                        arrayCurrentCollectionInfo.put(GlobalFlags.TAG_COLLECTION, arrayInfoForCurrentCollection);

                        //--------------------------------------------------------------------------
                        // Извлекаем id всей одежды для текущей коллекции одежды
                        ArrayList<HashMap<String, String>> arrayDressIdForCurrentCollection = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                                GlobalFlags.TAG_TABLE_COLLECTION_DRESS,
                                new String[]{GlobalFlags.TAG_DRESS_ID},
                                GlobalFlags.TAG_COLLECTION_ID + " = ?",
                                new String[]{arrayCollectionsId.get(indexCollection).get(GlobalFlags.TAG_ID)},
                                null,
                                null
                        );

                        //--------------------------------------------------------------------------
                        if (arrayDressIdForCurrentCollection != null) {
                            // Разбиваем весь массив одежды по типу одежду (головные уборы, обувь и т.д.)
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoHead = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoBody = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoLeg = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoFoot = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoAccessory = null;

                            //----------------------------------------------------------------------
                            // В цикле считываем информацию об одежде для текущей коллекции одежды
                            for (int indexDressInCurrentCollection = 0; indexDressInCurrentCollection < arrayDressIdForCurrentCollection.size(); indexDressInCurrentCollection++) {
                                // Считываем информацию о текущей одежде для текущей коллекции одежды
                                ArrayList<HashMap<String, String>> arrayCurrentDressInfo = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                                        GlobalFlags.TAG_TABLE_DRESS,
                                        new String[]{
                                                GlobalFlags.TAG_ID,
                                                GlobalFlags.TAG_CATID,
                                                GlobalFlags.TAG_FOR_WHO,
                                                GlobalFlags.TAG_TYPE,
                                                GlobalFlags.TAG_BRAND_ID,
                                                GlobalFlags.TAG_IMAGE,
                                                GlobalFlags.TAG_IMAGE_WIDTH,
                                                GlobalFlags.TAG_IMAGE_HEIGHT,
                                                GlobalFlags.TAG_IMAGE_BACK,
                                                GlobalFlags.TAG_IMAGE_BACK_WIDTH,
                                                GlobalFlags.TAG_IMAGE_BACK_HEIGHT,
                                                GlobalFlags.TAG_COLOR,
                                                GlobalFlags.TAG_STYLE
                                        },
                                        GlobalFlags.TAG_ID + " = ?",
                                        new String[]{arrayDressIdForCurrentCollection.get(indexDressInCurrentCollection).get(GlobalFlags.TAG_DRESS_ID)},
                                        null,
                                        "1"
                                );

                                //------------------------------------------------------------------
                                // Обрабатываем информацию о текущей одежде
                                if (arrayCurrentDressInfo != null) {
                                    if (arrayCurrentDressInfo.size() > 0) {
                                        if (arrayCurrentDressInfo.get(0) != null) {
                                            if (arrayCurrentDressInfo.get(0).containsKey(GlobalFlags.TAG_TYPE)) {
                                                if (arrayCurrentDressInfo.get(0).get(GlobalFlags.TAG_TYPE) != null) {
                                                    // Определяем тип текущей одежды
                                                    switch (arrayCurrentDressInfo.get(0).get(GlobalFlags.TAG_TYPE)) {
                                                        case GlobalFlags.TAG_DRESS_HEAD:                // головные уборы
                                                            if (arrayCurrentDressInfoHead == null) {
                                                                arrayCurrentDressInfoHead = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoHead.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_BODY:                // одежда для тела
                                                            if (arrayCurrentDressInfoBody == null) {
                                                                arrayCurrentDressInfoBody = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoBody.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_LEG:                 // одежда для ног
                                                            if (arrayCurrentDressInfoLeg == null) {
                                                                arrayCurrentDressInfoLeg = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoLeg.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_FOOT:                // обувь
                                                            if (arrayCurrentDressInfoFoot == null) {
                                                                arrayCurrentDressInfoFoot = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoFoot.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_ACCESSORY:           // аксессуары
                                                            if (arrayCurrentDressInfoAccessory == null) {
                                                                arrayCurrentDressInfoAccessory = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoAccessory.add(arrayCurrentDressInfo.get(0));

                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //----------------------------------------------------------------------
                            // Заполняем массив о текущей коллекции

                            // Головные уборы
                            if (arrayCurrentDressInfoHead != null) {
                                arrayCurrentCollectionInfo.put(GlobalFlags.TAG_DRESS_HEAD, arrayCurrentDressInfoHead);
                            }

                            // Одежда для тела
                            if (arrayCurrentDressInfoBody != null) {
                                arrayCurrentCollectionInfo.put(GlobalFlags.TAG_DRESS_BODY, arrayCurrentDressInfoBody);
                            }

                            // Одежда для ног
                            if (arrayCurrentDressInfoLeg != null) {
                                arrayCurrentCollectionInfo.put(GlobalFlags.TAG_DRESS_LEG, arrayCurrentDressInfoLeg);
                            }

                            // Обувь
                            if (arrayCurrentDressInfoFoot != null) {
                                arrayCurrentCollectionInfo.put(GlobalFlags.TAG_DRESS_FOOT, arrayCurrentDressInfoFoot);
                            }

                            // Акссесуары
                            if (arrayCurrentDressInfoAccessory != null) {
                                arrayCurrentCollectionInfo.put(GlobalFlags.TAG_DRESS_ACCESSORY, arrayCurrentDressInfoAccessory);
                            }

                            //----------------------------------------------------------------------
                            // Сохраняем информацию о текущей коллекции в общий возвращаемый массив
                            if (returnArrayCollectionsInfo == null) {
                                returnArrayCollectionsInfo = new ArrayList<>();
                            }

                            returnArrayCollectionsInfo.add(arrayCurrentCollectionInfo);
                        }
                    }
                }
            }
        }

        return returnArrayCollectionsInfo;
    }

    //----------------------------------------------------------------------------------------------
    // После завершения фоновой задачи закрываем прогресс-диалог
    protected void onPostExecute(ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> resultArrayDressInfo) {
        super.onPostExecute(resultArrayDressInfo);

        //------------------------------------------------------------------------------------------
        try {
            // Закрываем прогресс-диалог
            if (this.getProgressDialogDressCollectionLoad() != null) {
                this.getProgressDialogDressCollectionLoad().dismiss();
            }

            //--------------------------------------------------------------------------------------
            // Меняем заголовок страницы
            String textTitle = DBMain.getContext().getString(R.string.bar_item_dress_collection);

            if(this.getDressCategoryTitle() != null) {
                textTitle = this.getDressCategoryTitle();
            }

            ((MainActivity) DBMain.getContext()).setDressroomTitleText(textTitle);

            //--------------------------------------------------------------------------------------
            // Если многомерный массив, хранящий информацию о наборах одежды, НЕ пуст
            if (resultArrayDressInfo != null) {
                // Если количество коллекций одежды больше 0
                if (resultArrayDressInfo.size() > 0) {
                    // Задаем адаптер для листания коллекций одежды
                    PagerAdapterDressCollection pagerAdapterDressCollection = new PagerAdapterDressCollection(resultArrayDressInfo);
                    DBMain.setPagerAdapterDressCollection(pagerAdapterDressCollection);

                    //------------------------------------------------------------------------------
                    // Запускаем функцию инициализации компонентов окна
                    ((MainActivity) DBMain.getContext()).initializeComponentsLayoutCollections();
                }
                // Иначе, если не найдено ни одной коллекции
                else {
                    if(DBMain.getContext().getClass().toString().contains("MainActivity")) {
                        if (((MainActivity) DBMain.getContext()).getFrameLayoutContentMain() != null) {
                            View viewPageNoInternetConnection = ApplicationContextProvider.getLayoutInflater().inflate(
                                    R.layout.page_no_internet_connection,
                                    ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain(),
                                    false
                            );

                            //----------------------------------------------------------------------
                            if (viewPageNoInternetConnection != null) {
                                // Получаем ссылку на кнопку запуска повторной проверки наличия Интернет подключения
                                Button buttonCheckInternetConnection = (Button) viewPageNoInternetConnection.findViewById(R.id.buttonCheckInternetConnection);

                                // Получаем ссылку на текстовое поле для вывода сообщения об отсутствии Интернет подключения
                                final TextView textViewErrorNoInternetConnection = (TextView) viewPageNoInternetConnection.findViewById(R.id.textViewErrorNoInternetConnection);

                                // Получаем ссылку на вращающийся круг, отображающий выполнение фоновой задачи проверки Интернет подключения
                                final ProgressBar progressBarCheckInternetConnection = (ProgressBar) viewPageNoInternetConnection.findViewById(R.id.progressBarCheckInternetConnection);

                                // Устанавливаем обработчик события для кнопки проверки наличия Интернет подключения
                                if (buttonCheckInternetConnection != null) {
                                    buttonCheckInternetConnection.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (textViewErrorNoInternetConnection != null && progressBarCheckInternetConnection != null) {
                                                // Выполняем задачу проверки Интернет подключения в асинхронном потоке
                                                AsyncTaskCheckInternetConnection checkInternetConnection = new AsyncTaskCheckInternetConnection(
                                                        DBMain.getContext(),
                                                        (Button) v,
                                                        textViewErrorNoInternetConnection,
                                                        progressBarCheckInternetConnection);

                                                checkInternetConnection.execute();
                                            }
                                        }
                                    });
                                }

                                ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().removeAllViews();
                                ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().addView(viewPageNoInternetConnection);
                            }
                        }
                    }
                }
            }
            // Иначе, если возникла ошибка при считывании наборов одежды
            else {
                if(DBMain.getContext().getClass().toString().contains("MainActivity")) {
                    if (((MainActivity) DBMain.getContext()).getFrameLayoutContentMain() != null) {
                        View viewPageNoInternetConnection = ApplicationContextProvider.getLayoutInflater().inflate(
                                R.layout.page_no_internet_connection,
                                ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain(),
                                false
                        );

                        //--------------------------------------------------------------------------
                        if (viewPageNoInternetConnection != null) {
                            // Получаем ссылку на кнопку запуска повторной проверки наличия Интернет подключения
                            Button buttonCheckInternetConnection = (Button) viewPageNoInternetConnection.findViewById(R.id.buttonCheckInternetConnection);

                            // Получаем ссылку на текстовое поле для вывода сообщения об отсутствии Интернет подключения
                            final TextView textViewErrorNoInternetConnection = (TextView) viewPageNoInternetConnection.findViewById(R.id.textViewErrorNoInternetConnection);

                            // Получаем ссылку на вращающийся круг, отображающий выполнение фоновой задачи проверки Интернет подключения
                            final ProgressBar progressBarCheckInternetConnection = (ProgressBar) viewPageNoInternetConnection.findViewById(R.id.progressBarCheckInternetConnection);

                            // Устанавливаем обработчик события для кнопки проверки наличия Интернет подключения
                            if (buttonCheckInternetConnection != null) {
                                buttonCheckInternetConnection.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (textViewErrorNoInternetConnection != null && progressBarCheckInternetConnection != null) {
                                            // Выполняем задачу проверки Интернет подключения в асинхронном потоке
                                            AsyncTaskCheckInternetConnection checkInternetConnection = new AsyncTaskCheckInternetConnection(
                                                    DBMain.getContext(),
                                                    (Button) v,
                                                    textViewErrorNoInternetConnection,
                                                    progressBarCheckInternetConnection);

                                            checkInternetConnection.execute();
                                        }
                                    }
                                });
                            }

                            ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().removeAllViews();
                            ((MainActivity) DBMain.getContext()).getFrameLayoutContentMain().addView(viewPageNoInternetConnection);
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Post Execute Dress Collection Load From Local DB: " + exception.toString());
        }
    }
}
