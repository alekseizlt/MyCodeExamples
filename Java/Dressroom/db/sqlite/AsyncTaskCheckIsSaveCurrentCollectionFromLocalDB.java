package ru.alexprogs.dressroom.db.sqlite;

import android.os.AsyncTask;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.db.DBMain;

// Метод для проверки, сохранен ли текущий набор одежды, для текущего пользователя
// с использованием локальной БД
public class AsyncTaskCheckIsSaveCurrentCollectionFromLocalDB extends AsyncTask<String, Void, Boolean> {

    // Свойства данного класса
    private int mIsFavoriteDressShowNow;                        // переменная, показывающая был ли сохранен набор одежды, отображаемый в первую очередь для текущего пользователя
    private int mCollectionIdForDressShowNow;                   // id коллекции для набора одежды, отображаемого в первую очередь для текущего пользователя
    private ImageView mButtonDressSave;                         // кнопка сохранения текущего набора одежды в БД для текущего пользователя
                                                                // после сохранения текущего набора одежды для данной кнопки необходимо поменять изображение

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // buttonDressSave - ссылка на кнопку сохранения текущего набора одежды в БД для текущего пользователя
    public AsyncTaskCheckIsSaveCurrentCollectionFromLocalDB(ImageView buttonDressSave) {
        this.setIsFavoriteDressShowNow(0);
        this.setCollectionIdForDressShowNow(0);
        this.setButtonDressSave(buttonDressSave);
    }

    //==============================================================================================
    // Метод для считывания значения логической переменной, определяющей является ли сохраненным
    // для текущего пользователя набор одежды, который будет первоначально отображен на виртуальном
    // манекене после выполнения всех нижеприведенных операторов
    private int getIsFavoriteDressShowNow() {
        return this.mIsFavoriteDressShowNow;
    }

    //==============================================================================================
    // Метод для задания значения логической переменной, определяющей является ли сохраненным
    // для текущего пользователя набор одежды, который будет первоначально отображен на виртуальном
    // манекене после выполнения всех нижеприведенных операторов
    private void setIsFavoriteDressShowNow(int isFavoriteDressShowNow) {
        this.mIsFavoriteDressShowNow = isFavoriteDressShowNow;
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
    // Перед началом фонового потока
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //==============================================================================================
    // Получаем информацию из url
    protected Boolean doInBackground(String... args) {
        // Возвращаемая логическая переменная, определяющая сохранен ли текущий набор одежды для текущего пользователя
        Boolean isSaveCurrentCollection = false;

        //------------------------------------------------------------------------------------------
        // Формируем массив id вещей, которые в данный момент представлены на виртуальном манекене
        ArrayList<Integer> arrayListDressIdShowNow = null;

        HashMap<String, String> arrayListDressId = DBMain.createArrayListDressId(null);

        if(arrayListDressId != null) {
            arrayListDressIdShowNow = new ArrayList<>();

            Collection<String> arrayListDressIdKeyCollection = arrayListDressId.keySet();

            for (String arrayListDressIdKey : arrayListDressIdKeyCollection) {
                String currentDressId = arrayListDressId.get(arrayListDressIdKey);

                if(currentDressId != null) {
                    arrayListDressIdShowNow.add(Integer.parseInt(currentDressId));
                }
            }
        }

        //------------------------------------------------------------------------------------------
        // Считываем id всех коллекций для текущего пользователя из локальной БД
        if(DBMain.getDBSQLiteHelper() == null) {
            DBMain.setDBSQLiteHelper(new DBSQLiteHelper(DBMain.getContext()));
        }

        ArrayList<HashMap<String, String>> arrayCollectionsId = DBMain.getDBSQLiteHelper().getAllRecordsFromDB(
                GlobalFlags.TAG_TABLE_COLLECTION,
                new String[]{GlobalFlags.TAG_ID},
                GlobalFlags.TAG_ID
        );

        if(arrayCollectionsId != null) {
            // В цикле перебираем все коллекции одежды для текущего пользователя,
            // находим те, в которой количество вещей равно arrayListDressIdShowNow.size() и
            // сравниваем id одежды, присутствующей в данной коллекции с id вещей, представленных в массиве arrayListDressIdShowNow
            for (int indexCollection = 0; indexCollection < arrayCollectionsId.size(); indexCollection++) {
                // Извлекаем id вещей для данной коллекции
                ArrayList<HashMap<String, String>> arrayDressIdForCurrentCollection = DBMain.getDBSQLiteHelper().getRecordsFromDB(
                        GlobalFlags.TAG_TABLE_COLLECTION_DRESS,
                        new String[]{GlobalFlags.TAG_DRESS_ID},
                        GlobalFlags.TAG_COLLECTION_ID + " = ?",
                        new String[]{arrayCollectionsId.get(indexCollection).get(GlobalFlags.TAG_ID)},
                        null,
                        null
                );

                // Если количество одежды, входящей в состав текущего набора одежды совпадает с
                // количеством вещей, представленных в массиве arrayListDressIdShowNow
                if (arrayDressIdForCurrentCollection != null && arrayListDressIdShowNow != null) {
                    if (arrayDressIdForCurrentCollection.size() == arrayListDressIdShowNow.size()) {
                        // Сравниваем элементы двух массивов
                        Boolean isTwoArraysEquals = true;

                        for (int i = 0; i < arrayListDressIdShowNow.size(); i++) {
                            Boolean isCurrentValueInBothArrays = false;

                            for (int j = 0; j < arrayDressIdForCurrentCollection.size(); j++) {
                                if (arrayDressIdForCurrentCollection.get(j) != null) {
                                    if (arrayDressIdForCurrentCollection.get(j).containsKey(GlobalFlags.TAG_DRESS_ID)) {
                                        if (arrayDressIdForCurrentCollection.get(j).get(GlobalFlags.TAG_DRESS_ID) != null) {
                                            if (arrayListDressIdShowNow.get(i) == Integer.parseInt(arrayDressIdForCurrentCollection.get(j).get(GlobalFlags.TAG_DRESS_ID))) {
                                                isCurrentValueInBothArrays = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            // Если текущее значение не присутствует в обоих массивах
                            if (isCurrentValueInBothArrays.equals(false)) {
                                isTwoArraysEquals = false;
                                break;
                            }
                        }

                        // Если два массива оказались одинаковыми
                        if (isTwoArraysEquals.equals(true)) {
                            isSaveCurrentCollection = true;
                            this.setIsFavoriteDressShowNow(1);
                            this.setCollectionIdForDressShowNow(Integer.parseInt(arrayCollectionsId.get(indexCollection).get(GlobalFlags.TAG_ID)));
                            break;
                        }


                    }
                }
            }
        }

        return isSaveCurrentCollection;
    }

    //==============================================================================================
    // После завершения фоновой задачи
    protected void onPostExecute(Boolean resultIsSaveCurrentCollection) {
        super.onPostExecute(resultIsSaveCurrentCollection);

        //--------------------------------------------------------------------------------------
        // Делаем выделенной или нет кнопку сохранения информации о текущем наборе одежды
        if (this.getButtonDressSave() != null) {
            // Если текущий набор одежды был СОХРАНЕН для текущего пользователя
            if(this.getIsFavoriteDressShowNow() == 1) {
                // Меняем изображение для кнопки сохранения текущего набора одежды
                this.getButtonDressSave().setImageResource(R.drawable.favorite2);

                // Устанавливаем id текущего набора одежды в качестве тега для соответствующей кнопки сохранения
                this.getButtonDressSave().setTag(this.getCollectionIdForDressShowNow());
            }
            // Иначе
            else {
                // Меняем изображение для кнопки сохранения текущего набора одежды
                this.getButtonDressSave().setImageResource(R.drawable.favorite);

                // Устанавливаем 0 в качестве тега для соответствующей кнопки сохранения
                this.getButtonDressSave().setTag(0);
            }
        }
    }
}
