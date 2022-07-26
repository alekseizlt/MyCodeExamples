package ru.alexprogs.dressroom.db.sqlite;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.lib.FunctionsLog;

// Метод для загрузки информации о коллекции одежды из локальной БД
public class AsyncTaskLoadDressCollectionInfoSwipeFromLocalDB extends AsyncTask<String, Void, HashMap<String, ArrayList<HashMap<String, String>>>> {

    // Свойства данного класса
    private int mDressCollectionId;                         // id крайней (первой или последней) коллекции одежды
    private int mSwipeDirection;                            // направление листание коллекций одежды пальцем

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // dressCollectionId - id крайней (первой или последней) коллекции одежды
    // swipeDirection - направление листание коллекций одежды пальцем
    public AsyncTaskLoadDressCollectionInfoSwipeFromLocalDB(int dressCollectionId, int swipeDirection) {
        // Инициализируем свойства данного класса
        this.setDressCollectionId(dressCollectionId);       // id крайней (первой или последней) коллекции одежды
        this.setSwipeDirection(swipeDirection);             // направление листание коллекций одежды пальцем
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
    // Перед началом фонового потока
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //==============================================================================================
    // Получаем информацию о текущем отображаемом наборе (коллекции) одежды
    protected HashMap<String, ArrayList<HashMap<String, String>>> doInBackground(String... args) {
        // Массив, возвращаемый в качестве результата выполнения текущей функции
        HashMap<String, ArrayList<HashMap<String, String>>> returnArrayCollectionsInfo = null;

        //------------------------------------------------------------------------------------------
        // Если информация считывалась для определенной категории
        if(DBMain.getMySQLDressCollectionLoad() != null && DBMain.getMySQLDressCollectionLoad().getDressCategoryId() != null) {
            // Считываем id всех коллекций для текущего пользователя
            ArrayList<HashMap<String, String>> arrayCollectionsId = DBMain.getDBSQLiteHelper().getAllRecordsFromDB(
                    GlobalFlags.TAG_TABLE_COLLECTION,
                    new String[]{GlobalFlags.TAG_ID},
                    GlobalFlags.TAG_ID
            );

            // Если id коллекций одежды для текущего пользователя успешно считаны
            if(arrayCollectionsId != null) {
                // Теперь формируем массив, содержащий id всей одежды, входящей в состав всех коллекций
                // для текущего пользователя
                ArrayList<String> arrayDressIdInCollectionForCurrentUser = null;

                // Перебираем в цикле все коллекции одежды для текущего пользователя
                for( int indexCollection = 0; indexCollection < arrayCollectionsId.size(); indexCollection++) {
                    if(arrayCollectionsId.get(indexCollection) != null) {
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
                if(arrayDressIdInCollectionForCurrentUser != null) {
                    ArrayList<Integer> arrayDressIdForCurrentUser = null;

                    // Формируем запрос к БД
                    String where = "";
                    String whereArgsString = "";

                    for(int indexDress = 0; indexDress < arrayDressIdInCollectionForCurrentUser.size(); indexDress++) {
                        if(indexDress == 0) {
                            where += "(";
                        }

                        where += GlobalFlags.TAG_ID + " = ?";
                        whereArgsString += arrayDressIdInCollectionForCurrentUser.get(indexDress) + "___";

                        if( indexDress < arrayDressIdInCollectionForCurrentUser.size() - 1 ) {
                            where += " OR ";
                        }

                        if( indexDress == arrayDressIdInCollectionForCurrentUser.size() - 1 ) {
                            where += ")";
                        }
                    }

                    where += " AND catid = ?";
                    whereArgsString += DBMain.getMySQLDressCollectionLoad().getDressCategoryId();

                    String[] whereArgs = whereArgsString.split("___");

                    // Выполняем непосредственно запрос к БД
                    ArrayList<HashMap<String, String>> arrayDressFilter = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                            GlobalFlags.TAG_TABLE_DRESS,
                            new String[] {GlobalFlags.TAG_ID},
                            where,
                            whereArgs,
                            GlobalFlags.TAG_ID,
                            null
                    );

                    // Теперь заполняем массив arrayDressIdForCurrentUser
                    if(arrayDressFilter != null) {
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
                    if(arrayDressIdForCurrentUser != null) {
                        // Определяем порядковый номер текущей одежды (в данном случае одежда совпадает с коллекцией)
                        int positionCurrentDress = -1;

                        for (int indexDress = 0; indexDress < arrayDressIdForCurrentUser.size(); indexDress++) {
                            if (arrayDressIdForCurrentUser.get(indexDress) == this.getDressCollectionId()) {
                                positionCurrentDress = indexDress;
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Определяем позицию в массиве той одежды, информацию о которой необходимо считать
                        int positionRequireDress = -1;

                        if (positionCurrentDress >= 0) {
                            // Если направление листания - слева направо
                            if (this.getSwipeDirection() == GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT) {
                                positionRequireDress = positionCurrentDress - 1;
                            }
                            // Иначе, если направление листания - справа налево
                            else if (this.getSwipeDirection() == GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT) {
                                positionRequireDress = positionCurrentDress + 1;
                            }
                        }

                        //--------------------------------------------------------------------------
                        // Считываем полную информацию о необходимой одежде
                        if (positionRequireDress >= 0 && positionRequireDress <arrayDressIdForCurrentUser.size()) {
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
                                    new String[]{String.valueOf(arrayDressIdForCurrentUser.get(positionRequireDress))},
                                    GlobalFlags.TAG_ID,
                                    "1"
                            );

                            //----------------------------------------------------------------------
                            // Сохраняем информацию о текущей одежде в возвращаемом массиве
                            if (arrayRequireDressInfo != null) {
                                if(arrayRequireDressInfo.size() > 0) {
                                    if (arrayRequireDressInfo.get(0) != null) {
                                        returnArrayCollectionsInfo = new HashMap<>();

                                        //----------------------------------------------------------
                                        // Формируем HashMap, содержащий информацию о текущем наборе одежды
                                        // для возвращаемого массива
                                        HashMap<String, String> returnMapCurrentCollectionInfo = new HashMap<>();

                                        // Добавляем каждый елемент (параметр категории одежды) в HashMap ключ => значение
                                        returnMapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, arrayRequireDressInfo.get(0).get(GlobalFlags.TAG_ID));       // id текущего набора одежды

                                        ArrayList<HashMap<String, String>> returnArrayCurrentCollectionInfo = new ArrayList<>();
                                        returnArrayCurrentCollectionInfo.add(returnMapCurrentCollectionInfo);

                                        // Сохраняем информацию о текущей коллекции одежды в глобальном массиве
                                        returnArrayCollectionsInfo.put(GlobalFlags.TAG_COLLECTION, returnArrayCurrentCollectionInfo);

                                        //----------------------------------------------------------
                                        // Добавляем в возвращаемый массив информацию о текущей вещи
                                        returnArrayCollectionsInfo.put(
                                                arrayRequireDressInfo.get(0).get(GlobalFlags.TAG_TYPE),
                                                arrayRequireDressInfo
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Иначе, если считывается полностью вся коллекция одежды
        else {
            // Массив, хранящий информацию о текущей необходимой коллекции одежды
            ArrayList<HashMap<String, String>> arrayRequireCollectionInfo = null;

            // Считываем информацию о необходимой коллекции одежды в зависимости от направления листания
            switch (this.getSwipeDirection()) {
                case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                 // если направление листания - слева направо
                    // Считываем информацию о предыдущей коллекции одежды
                    arrayRequireCollectionInfo = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                            GlobalFlags.TAG_TABLE_COLLECTION,
                            new String[] {GlobalFlags.TAG_ID},
                            GlobalFlags.TAG_ID + " < ?",
                            new String[] {String.valueOf(this.getDressCollectionId())},
                            GlobalFlags.TAG_ID + " DESC",
                            "1"
                    );

                    break;

                case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                 // если направление листания - справа налево
                    // Считываем информацию о следующей коллекции одежды
                    arrayRequireCollectionInfo = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                            GlobalFlags.TAG_TABLE_COLLECTION,
                            new String[] {GlobalFlags.TAG_ID},
                            GlobalFlags.TAG_ID + " > ?",
                            new String[] {String.valueOf(this.getDressCollectionId())},
                            GlobalFlags.TAG_ID,
                            "1"
                    );

                    break;
            }

            //--------------------------------------------------------------------------------------
            // Если id необходимой коллекции одежды успешно считан
            if( arrayRequireCollectionInfo != null ) {
                if(arrayRequireCollectionInfo.size() > 0) {
                    if(arrayRequireCollectionInfo.get(0) != null) {
                        // Формируем HashMap, содержащий информацию о текущем наборе одежды
                        HashMap<String, String> mapCurrentCollectionInfo = new HashMap<>();

                        // Добавляем каждый елемент в HashMap ключ => значение
                        mapCurrentCollectionInfo.put(GlobalFlags.TAG_ID, arrayRequireCollectionInfo.get(0).get(GlobalFlags.TAG_ID));          // id текущего набора одежды

                        ArrayList<HashMap<String, String>> arrayInfoForCurrentCollection = new ArrayList<>();
                        arrayInfoForCurrentCollection.add(mapCurrentCollectionInfo);

                        returnArrayCollectionsInfo = new HashMap<>();
                        returnArrayCollectionsInfo.put(GlobalFlags.TAG_COLLECTION, arrayInfoForCurrentCollection);

                        //--------------------------------------------------------------------------
                        // Считываем id всей одежды, присутствующей в данной коллекции одежды
                        ArrayList<HashMap<String, String>> arrayDressIdForRequireCollection = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                                GlobalFlags.TAG_TABLE_COLLECTION_DRESS,
                                new String[]{GlobalFlags.TAG_DRESS_ID},
                                GlobalFlags.TAG_COLLECTION_ID + " = ?",
                                new String[]{arrayRequireCollectionInfo.get(0).get(GlobalFlags.TAG_ID)},
                                null,
                                null
                        );

                        //--------------------------------------------------------------------------
                        if (arrayDressIdForRequireCollection != null) {
                            // Разбиваем весь массив одежды по типу одежду (головные уборы, обувь и т.д.)
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoHead = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoBody = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoLeg = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoFoot = null;
                            ArrayList<HashMap<String, String>> arrayCurrentDressInfoAccessory = null;

                            //----------------------------------------------------------------------
                            // В цикле считываем информацию об одежде для текущей коллекции одежды
                            for (int indexDressInCurrentCollection = 0; indexDressInCurrentCollection < arrayDressIdForRequireCollection.size(); indexDressInCurrentCollection++) {
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
                                        new String[]{arrayDressIdForRequireCollection.get(indexDressInCurrentCollection).get(GlobalFlags.TAG_DRESS_ID)},
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
                                                        case GlobalFlags.TAG_DRESS_HEAD:            // головные уборы
                                                            if (arrayCurrentDressInfoHead == null) {
                                                                arrayCurrentDressInfoHead = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoHead.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_BODY:            // одежда для тела
                                                            if (arrayCurrentDressInfoBody == null) {
                                                                arrayCurrentDressInfoBody = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoBody.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_LEG:             // одежда для ног
                                                            if (arrayCurrentDressInfoLeg == null) {
                                                                arrayCurrentDressInfoLeg = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoLeg.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_FOOT:            // обувь
                                                            if (arrayCurrentDressInfoFoot == null) {
                                                                arrayCurrentDressInfoFoot = new ArrayList<>();
                                                            }

                                                            arrayCurrentDressInfoFoot.add(arrayCurrentDressInfo.get(0));

                                                            break;

                                                        case GlobalFlags.TAG_DRESS_ACCESSORY:       // аксессуары
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
                                returnArrayCollectionsInfo.put(GlobalFlags.TAG_DRESS_HEAD, arrayCurrentDressInfoHead);
                            }

                            // Одежда для тела
                            if (arrayCurrentDressInfoBody != null) {
                                returnArrayCollectionsInfo.put(GlobalFlags.TAG_DRESS_BODY, arrayCurrentDressInfoBody);
                            }

                            // Одежда для ног
                            if (arrayCurrentDressInfoLeg != null) {
                                returnArrayCollectionsInfo.put(GlobalFlags.TAG_DRESS_LEG, arrayCurrentDressInfoLeg);
                            }

                            // Обувь
                            if (arrayCurrentDressInfoFoot != null) {
                                returnArrayCollectionsInfo.put(GlobalFlags.TAG_DRESS_FOOT, arrayCurrentDressInfoFoot);
                            }

                            // Акссесуары
                            if (arrayCurrentDressInfoAccessory != null) {
                                returnArrayCollectionsInfo.put(GlobalFlags.TAG_DRESS_ACCESSORY, arrayCurrentDressInfoAccessory);
                            }
                        }
                    }

                }
            }
        }

        return returnArrayCollectionsInfo;
    }

    //==============================================================================================
    // После завершения фоновой задачи
    protected void onPostExecute(HashMap<String, ArrayList<HashMap<String, String>>> resultArrayDressInfo) {
        super.onPostExecute(resultArrayDressInfo);

        //------------------------------------------------------------------------------------------
        try {
            // Если многомерный массив, хранящий информацию о текущем наборе одежды, НЕ пуст
            if (resultArrayDressInfo != null) {
                // Если количество коллекций одежды больше 0
                if (resultArrayDressInfo.size() > 0) {
                    // Считываем массив параметров для текущего адаптера
                    ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> currentPagerAdapterDressCollectionItemsParams = null;

                    if(DBMain.getPagerAdapterDressCollection() != null) {
                        currentPagerAdapterDressCollectionItemsParams = DBMain.getPagerAdapterDressCollection().getArrayParams();
                    }

                    //------------------------------------------------------------------------------
                    // Итоговый конечный массив, содержащий параметры для текущего адаптера
                    ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> currentPagerAdapterDressCollectionItemsParamsNew;

                    if(DBMain.getPagerAdapterDressCollection() != null && currentPagerAdapterDressCollectionItemsParams != null) {
                        currentPagerAdapterDressCollectionItemsParamsNew = new ArrayList<>();

                        // В зависимости от направления листания выполняем соответствующие действия
                        // вышеуказанным массивом параметров
                        switch (this.getSwipeDirection()) {
                            case GlobalFlags.DRESS_SWIPE_LEFT_TO_RIGHT:                             // листание слева направо
                                for(int indexItem = 0; indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                }

                                //------------------------------------------------------------------
                                // Добавляем в начало текущую, считанную информацию о коллекции одежды
                                currentPagerAdapterDressCollectionItemsParamsNew.add(resultArrayDressInfo);

                                //------------------------------------------------------------------
                                // Определяем последний индекс, для которого присутствуют данные
                                // в массиве currentPagerAdapterDressCollectionItemsParams
                                int indexItemEnd = currentPagerAdapterDressCollectionItemsParams.size() - 1;

                                if(DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() < currentPagerAdapterDressCollectionItemsParams.size() - 1) {
                                    indexItemEnd = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd();
                                }

                                //------------------------------------------------------------------
                                // Сохраняем все элементы массива currentPagerAdapterDressroomItemsParams кроме первого
                                for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart() + 1; indexItem <= indexItemEnd; indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(currentPagerAdapterDressCollectionItemsParams.get(indexItem));
                                }

                                //------------------------------------------------------------------
                                for(int indexItem = indexItemEnd + 1; indexItem < currentPagerAdapterDressCollectionItemsParams.size() - 1; indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                }

                                break;

                            case GlobalFlags.DRESS_SWIPE_RIGHT_TO_LEFT:                             // листание справа налево
                                for(int indexItem = 0; indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                }

                                //------------------------------------------------------------------
                                // Сохраняем все элементы массива currentPagerAdapterDressroomItemsParams кроме первого
                                for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionStart(); indexItem < DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd(); indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(currentPagerAdapterDressCollectionItemsParams.get(indexItem));
                                }

                                //------------------------------------------------------------------
                                // Добавляем в конец текущую, считанную информацию об одежде
                                currentPagerAdapterDressCollectionItemsParamsNew.add(resultArrayDressInfo);

                                //------------------------------------------------------------------
                                for(int indexItem = DBMain.getPagerAdapterDressCollection().getArrayParamsPositionEnd() + 1; indexItem < currentPagerAdapterDressCollectionItemsParams.size() - 1; indexItem++) {
                                    currentPagerAdapterDressCollectionItemsParamsNew.add(null);
                                }

                                break;
                        }

                        //--------------------------------------------------------------------------
                        // Обновляем текущий адаптер
                        DBMain.getPagerAdapterDressCollection().setArrayParams(currentPagerAdapterDressCollectionItemsParamsNew);
                        DBMain.getPagerAdapterDressCollection().notifyDataSetChanged();
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Post Execute (Go To Dress Collection Swipe From Local DB): " + exception.toString());
        }
    }
}
