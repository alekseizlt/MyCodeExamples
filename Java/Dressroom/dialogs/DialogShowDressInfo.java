package ru.alexprogs.dressroom.dialogs;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.ActivityBrandInfo;
import ru.alexprogs.dressroom.ActivityDressInfo;
import ru.alexprogs.dressroom.ActivityLoginRegister;
import ru.alexprogs.dressroom.ActivityShopList;
import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.MainActivity;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.UserDetails;
import ru.alexprogs.dressroom.adapter.PagerAdapterMultipleViewPages;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.db.mysql.MySQLCheckIsSaveCurrentCollection;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDress;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.images.BitmapDecode;

/**
* Класс для отображения всплывающего окна вывода информации об одежде
*/
public class DialogShowDressInfo {

    // Свойства данного класса
    private Context mContext;                                                               // контекст
    private HashMap<String, String> mArrayDressInfo;                                        // массив, хранящий информацию об одежде
    private HashMap<String, ImageView> mArrayImageViewDressColor;                           // массив, хранящий ссылки на элементы ImageView, представляющие собой возможные цвета одежды
    private AlertDialog mAlertDialogShowDressInfo;
    private PagerAdapterMultipleViewPages mPagerAdapterMultipleViewPagesDressStyle;         // адаптер для элемента ViewPager выбора стиля одежды

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    // arrayDressInfo - массив, хранящий информацию об одежде
    public DialogShowDressInfo(Context сontext, HashMap<String, String> arrayDressInfo) {
        // Инициализируем свйоства текущего класса
        this.setContext(сontext);                       // контекст
        this.setArrayDressInfo(arrayDressInfo);         // массив, хранящий информацию об одежде
    }

    //==============================================================================================
    // Метод для считывания значения контекста
    private Context getContext() {
        return this.mContext;
    }

    //==============================================================================================
    // Метод для задания значения контекста
    private void setContext(Context context) {
        this.mContext = context;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего информацию об одежде
    private HashMap<String, String> getArrayDressInfo() {
        return this.mArrayDressInfo;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего информацию об одежде
    private void setArrayDressInfo(HashMap<String, String> arrayDressInfo) {
        this.mArrayDressInfo = arrayDressInfo;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего ссылки на элементы ImageView,
    // представляющие собой возможные цвета одежды
    private HashMap<String, ImageView> getArrayImageViewDressColor() {
        return this.mArrayImageViewDressColor;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего ссылки на элементы ImageView,
    // представляющие собой возможные цвета одежды
    private void setArrayImageViewDressColor(HashMap<String, ImageView> arrayImageViewDressColor) {
        this.mArrayImageViewDressColor = arrayImageViewDressColor;
    }

    //==============================================================================================
    // Метод для считывания
    private AlertDialog getAlertDialogShowDressInfo() {
        return this.mAlertDialogShowDressInfo;
    }

    //==============================================================================================
    // Метод для задания
    private void setAlertDialogShowDressInfo(AlertDialog alertDialogShowDressInfo) {
        this.mAlertDialogShowDressInfo = alertDialogShowDressInfo;
    }

    //==============================================================================================
    // Метод для считывания адаптера для элемента ViewPager выбора стиля одежды
    private PagerAdapterMultipleViewPages getPagerAdapterMultipleViewPagesDressStyle() {
        return this.mPagerAdapterMultipleViewPagesDressStyle;
    }

    //==============================================================================================
    // Метод для задания адаптера для элемента ViewPager выбора стиля одежды
    private void setPagerAdapterMultipleViewPagesDressStyle(PagerAdapterMultipleViewPages pgerAdapterMultipleViewPagesDressStyle) {
        this.mPagerAdapterMultipleViewPagesDressStyle = pgerAdapterMultipleViewPagesDressStyle;
    }

    //==============================================================================================
    // Метод создания текущего диалогового окна выбора категорий одежды
    public void create() {
        AlertDialog.Builder builderDialogShowDressInfo = new AlertDialog.Builder(this.getContext());

        // Создаем view из dialog_dress_info.xml
        final View viewDialogShowDressInfo = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.dialog_dress_info, null);

        // Формируем контент для всплывающего окна отображения информации об одежде
        // Результат выполнения функции формирования контента всплывающего окна отображения информации об одежде
        Boolean resultCreateContentDialogShowDressInfo = createContentDialogShowDressInfo(viewDialogShowDressInfo);

        // В зависимости от результата выполнения функции формирования контента всплывающего окна
        // отображения информации об одежде отображаем всплывающее диалоговое окно с соответствующим контентом

        // Если контент всплывающего окна отображения информации об одежде был успешно СФОРМИРОВАН
        if (resultCreateContentDialogShowDressInfo.equals(true) && viewDialogShowDressInfo != null) {
            // Устанавливаем view, полученный из dialog_dress_info.xml, как содержимое тела диалога
            builderDialogShowDressInfo.setView(viewDialogShowDressInfo);

            this.setAlertDialogShowDressInfo(builderDialogShowDressInfo.create());
        }
        // Иначе, считаем, что в ходе создания контента для всплывающего окна отображения информации об одежде
        // возникла ошибка
        else {
            // Создаем view из page_error.xml
            View viewDialogShowDressInfoError = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.page_error, null);

            // Устанавливаем view, полученный из page_error.xml, как содержимое тела диалога
            builderDialogShowDressInfo.setView(viewDialogShowDressInfoError);

            // Выводим соответствующее сообщение об ошибке во всплывающем окне отображения информации об одежде
            TextView textViewError = (TextView) viewDialogShowDressInfoError.findViewById(R.id.textViewError);

            if(textViewError != null) {
                textViewError.setText(R.string.string_show_dress_info_error);
            }

            // Устанавливаем нейтральную кнопку "Закрыть"
            builderDialogShowDressInfo.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }

    //==============================================================================================
    // Метод отображения созданного текущего диалогового окна отображения информации об одежде
    public void show() {
        try {
            // Отображаем диалоговое окно отображения информации об одежде
            if(this.getAlertDialogShowDressInfo() != null) {
                this.getAlertDialogShowDressInfo().show();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Show Dialog Dress Info: " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для формирования контента всплывающего окна отображения информации об одежде
    protected Boolean createContentDialogShowDressInfo(View view) {
        // Если массив с информацией об одежде пуст, то возвращаем false в качестве
        // результата выполнения ыункции
        if(this.getArrayDressInfo() == null) {
            return false;
        }

        //------------------------------------------------------------------------------------------
        // Выводим информацию о вещи из общего массива mArrayDressInfo
        try {
            // Задаем название текущей вещи (одежды)
            TextView textViewDressTitle = (TextView) view.findViewById(R.id.textViewDressTitle);

            if(textViewDressTitle != null) {
                // Задаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressTitle.setTypeface(GlobalFlags.getAppTypeface());
                }

                // Выводим непосредственно название текущей вещи
                if (this.getArrayDressInfo().containsKey(GlobalFlags.TAG_TITLE)) {
                    if (this.getArrayDressInfo().get(GlobalFlags.TAG_TITLE) != null) {
                        textViewDressTitle.setText(this.getArrayDressInfo().get(GlobalFlags.TAG_TITLE).trim());
                    }
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик клика по кнопке сохранения текущей одежды (текущей вещи)
            final ImageView imageViewDressSave = (ImageView) view.findViewById(R.id.imageViewDressSave);

            if(imageViewDressSave != null) {
                // Если тип просматриваемого содержимого в главном окне приложения - ОДЕЖДА
                if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                    // Делаем видимым данную кнопку
                    imageViewDressSave.setVisibility(View.VISIBLE);

                    //------------------------------------------------------------------------------
                    // Проверяем, присутствуют ли в массиве, хранящем сведения о текущей одежде,
                    // данные о том, является ли сохраненной данная одежда для текущего пользователя

                    // Если указанные сведения отсутствуют
                    if(!this.getArrayDressInfo().containsKey(GlobalFlags.TAG_COLLECTION_ID)) {
                        // Осуществляем запрос к БД на выяснение указзанного параметра
                        MySQLCheckIsSaveCurrentCollection mySQLCheckIsSaveCurrentCollection = new MySQLCheckIsSaveCurrentCollection(imageViewDressSave);
                        mySQLCheckIsSaveCurrentCollection.startCheckIsSaveCurrentCollection();
                    }
                    // Иначе, если указанные сведения присутствуют
                    else {
                        // Определяем является ли текущая вещь СОХРАНЕННОЙ для текущего пользователя
                        Boolean isCurrentDressFavorite = false;

                        if (this.getArrayDressInfo().containsKey(GlobalFlags.TAG_COLLECTION_ID)) {
                            if (this.getArrayDressInfo().get(GlobalFlags.TAG_COLLECTION_ID) != null) {
                                if (Integer.parseInt(this.getArrayDressInfo().get(GlobalFlags.TAG_COLLECTION_ID)) > 0) {
                                    isCurrentDressFavorite = true;
                                }
                            }
                        }

                        // Инициализируем кнопку сохранения информации о текущей вещи
                        if (isCurrentDressFavorite.equals(true)) {
                            // Устанавливаем изображение для для данной кнопки
                            imageViewDressSave.setImageResource(R.drawable.favorite2);

                            // В качестве тега устанавливаем id коллекции для данной вещи
                            imageViewDressSave.setTag(
                                    Integer.parseInt(this.getArrayDressInfo().get(GlobalFlags.TAG_COLLECTION_ID))
                            );
                        } else {
                            // Устанавливаем изображение для для данной кнопки
                            imageViewDressSave.setImageResource(R.drawable.favorite);

                            // В качестве тега устанавливаем id коллекции для данной вещи
                            imageViewDressSave.setTag(0);
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Устанавливаем обработчик клика для данной кнопки
                    imageViewDressSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Tсли пользователь не авторизован
                            if (UserDetails.getIsUserLogged().equals(false)) {
                                // Перенаправляем на страницу авторизации пользователя
                                Intent intentLoginRegister = new Intent(DialogShowDressInfo.this.getContext(), ActivityLoginRegister.class);
                                DialogShowDressInfo.this.getContext().startActivity(intentLoginRegister);
                            }
                            // Иначе, если пользователь авторизован
                            else {
                                DBMain.setContext(DialogShowDressInfo.this.getContext());

                                // Извлекаем id текущего набора одежды из тега для текущей кнопки
                                int currentCollectionId = 0;

                                if (imageViewDressSave.getTag() != null) {
                                    currentCollectionId = (int) imageViewDressSave.getTag();
                                }

                                // Если id текущего набора одежды >0
                                if (currentCollectionId > 0) {
                                    // Удаляем информацию о текущем наборе одежды для текущего пользователя
                                    DBMain.startDressCollectionUnSave(
                                            GlobalFlags.ACTION_NO,
                                            currentCollectionId,
                                            GlobalFlags.DRESS_COLLECTION_TYPE_DRESS,
                                            imageViewDressSave,
                                            true
                                    );
                                }
                                // Иначе считаем, что текущий набор одежды НЕ был ранее сохранен для текущего пользователя
                                else {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_ID) &&
                                            DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_TYPE)) {
                                        if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID) != null &&
                                                DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) != null) {
                                            HashMap<String, String> arrayDressListId = new HashMap<>();

                                            arrayDressListId.put(
                                                    DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE),
                                                    DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID)
                                            );

                                            DBMain.startDressCollectionSave(
                                                    GlobalFlags.ACTION_NO,
                                                    GlobalFlags.DRESS_COLLECTION_TYPE_DRESS,
                                                    arrayDressListId,
                                                    imageViewDressSave,
                                                    null
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                // Иначе, если тип просматриваемого содержимого в главном окне приложения - СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ
                else if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                    // Скрываем данную кнопку
                    imageViewDressSave.setVisibility(View.GONE);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик события для кнопки "Крестик" закрытия окна информации о текущей вещи
            ImageView imageViewDialogClose = (ImageView) view.findViewById(R.id.imageViewDialogClose);

            if(imageViewDialogClose != null) {
                imageViewDialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(DialogShowDressInfo.this.getAlertDialogShowDressInfo() != null) {
                            DialogShowDressInfo.this.getAlertDialogShowDressInfo().dismiss();
                        }
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для надписи "Категория"
            TextView textViewDressCategoryTitleLabel = (TextView) view.findViewById(R.id.textViewDressCategoryTitleLabel);

            if(textViewDressCategoryTitleLabel != null) {
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressCategoryTitleLabel.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем название категории для текущей вещи
            TextView textViewDressCategoryTitleValue = (TextView) view.findViewById(R.id.textViewDressCategoryTitleValue);

            String dressCategoryTitle = "";

            if(this.getArrayDressInfo().containsKey(GlobalFlags.TAG_CATEGORY_TITLE)) {
                if(this.getArrayDressInfo().get(GlobalFlags.TAG_CATEGORY_TITLE) != null) {
                    dressCategoryTitle = this.getArrayDressInfo().get(GlobalFlags.TAG_CATEGORY_TITLE).toLowerCase().trim();
                }

                if(dressCategoryTitle.equalsIgnoreCase("null")) {
                    dressCategoryTitle = "";
                }
            }

            if(textViewDressCategoryTitleValue != null) {
                // Задаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressCategoryTitleValue.setTypeface(GlobalFlags.getAppTypeface());
                }

                // Выводим непосредственно название категории для текущей вещи
                if(!dressCategoryTitle.equalsIgnoreCase("")) {
                    textViewDressCategoryTitleValue.setTextColor(ContextCompat.getColor(this.getContext(), R.color.color_white));
                    textViewDressCategoryTitleValue.setText(dressCategoryTitle);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для надписи "Бренд"
            TextView textViewDressBrandTitleLabel = (TextView) view.findViewById(R.id.textViewDressBrandTitleLabel);

            if(textViewDressBrandTitleLabel != null) {
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressBrandTitleLabel.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем название бренда для текущей вещи
            TextView textViewDressBrandTitleValue = (TextView) view.findViewById(R.id.textViewDressBrandTitleValue);

            String dressBrandTitle = "";

            if(this.getArrayDressInfo().containsKey(GlobalFlags.TAG_BRAND_TITLE)) {
                if(this.getArrayDressInfo().get(GlobalFlags.TAG_BRAND_TITLE) != null) {
                    dressBrandTitle = this.getArrayDressInfo().get(GlobalFlags.TAG_BRAND_TITLE).trim();
                }

                if(dressBrandTitle.equalsIgnoreCase("nul")) {
                    dressBrandTitle = "";
                }
            }

            final String dressBrandTitleFinal = dressBrandTitle;

            if(textViewDressBrandTitleValue != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressBrandTitleValue.setTypeface(GlobalFlags.getAppTypeface());
                }

                if(!dressBrandTitleFinal.equalsIgnoreCase("")) {
                    textViewDressBrandTitleValue.setTextColor(ContextCompat.getColor(this.getContext(), R.color.color_element_clickable));
                    textViewDressBrandTitleValue.setText(Html.fromHtml("<u>" + dressBrandTitleFinal + "</u>"));

                    // Устанавливаем обработчик щелчка по названию бренда одежды
                    textViewDressBrandTitleValue.setClickable(true);

                    textViewDressBrandTitleValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Закрываем текущее диалоговое окно с описанием текущей одежды
                            if(DialogShowDressInfo.this.getAlertDialogShowDressInfo() != null) {
                                DialogShowDressInfo.this.getAlertDialogShowDressInfo().dismiss();
                            }

                            // Отображаем Activity "Информация о юренде"
                            Intent intentBrandInfo = new Intent(DialogShowDressInfo.this.getContext(), ActivityBrandInfo.class);

                            // Передаем id текущего бренда одежды
                            int dressBrandId = 0;

                            if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_BRAND_ID)) {
                                if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_BRAND_ID) != null) {
                                    dressBrandId = Integer.parseInt(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_BRAND_ID));
                                }
                            }

                            intentBrandInfo.putExtra(GlobalFlags.TAG_BRAND_ID, dressBrandId);

                            DialogShowDressInfo.this.getContext().startActivity(intentBrandInfo);
                        }
                    });
                }
            }

            //--------------------------------------------------------------------------------------
            // Устанавливаем шрифт для надписи "Подробнее"
            TextView textViewButtonMore = (TextView) view.findViewById(R.id.textViewButtonMore);

            if(textViewButtonMore != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewButtonMore.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик клика по кнопке "Подробнее"
            RelativeLayout relativeLayoutButtonMore = (RelativeLayout) view.findViewById(R.id.relativeLayoutButtonMore);

            if(relativeLayoutButtonMore != null) {
                relativeLayoutButtonMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Закрываем текущее диалоговое окно с описанием текущей одежды
                        if (DialogShowDressInfo.this.getAlertDialogShowDressInfo() != null) {
                            DialogShowDressInfo.this.getAlertDialogShowDressInfo().dismiss();
                        }

                        // Отображаем Activity "Информация об одежде"
                        Intent intentDressInfo = new Intent(DialogShowDressInfo.this.getContext(), ActivityDressInfo.class);

                        // В качестве параметра для отображаемого Acvtivity передаем переменную, которая определяет
                        // что тип текущего набора одежды - одна единственная вещь
                        intentDressInfo.putExtra(GlobalFlags.TAG_DRESS_COLLECTION_TYPE, GlobalFlags.DRESS_COLLECTION_TYPE_DRESS);

                        // Передаем id текущей вещи
                        if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_ID) && DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_TYPE)) {
                            if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID) != null && DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) != null) {
                                intentDressInfo.putExtra(
                                        DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE),
                                        DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID)
                                );
                            }
                        }

                        // Отображаем созданное Activity
                        DialogShowDressInfo.this.getContext().startActivity(intentDressInfo);
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Устанавливаем шрифт для надписи "КУПИТЬ"
            TextView textViewBuy = (TextView) view.findViewById(R.id.textViewBuy);

            if(textViewBuy != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewBuy.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик щелчка по элементу RelativeLayout отображения списка магазинов для текущей одежды
            RelativeLayout relativeLayoutBuy = (RelativeLayout) view.findViewById(R.id.relativeLayoutBuy);

            if(relativeLayoutBuy != null) {
                relativeLayoutBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Закрываем текущее диалоговое окно с описанием текущей одежды
                        if(DialogShowDressInfo.this.getAlertDialogShowDressInfo() != null) {
                            DialogShowDressInfo.this.getAlertDialogShowDressInfo().dismiss();
                        }

                        // Отображаем Activity "Список магазинов одежды"
                        Intent intentShopList = new Intent(DialogShowDressInfo.this.getContext(), ActivityShopList.class);

                        // Передаем id текущей вещи
                        int dressId = 0;

                        if(DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_ID)) {
                            if(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID) != null) {
                                dressId = Integer.parseInt(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID));
                            }
                        }

                        intentShopList.putExtra(GlobalFlags.TAG_DRESS_ID, dressId);

                        // Передаем название бренда для текущей одежды
                        intentShopList.putExtra(GlobalFlags.TAG_BRAND_TITLE, dressBrandTitleFinal);

                        DialogShowDressInfo.this.getContext().startActivity(intentShopList);
                    }
                });
            }

            //--------------------------------------------------------------------------------------
            // Устанавливаем шрифт для надписи "Описание"
            TextView textViewDressShortDescriptionLabel = (TextView) view.findViewById(R.id.textViewDressShortDescriptionLabel);

            if(textViewDressShortDescriptionLabel != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressShortDescriptionLabel.setTypeface(GlobalFlags.getAppTypeface());
                }
            }


            //--------------------------------------------------------------------------------------
            // Задаем краткое описание для текущей вещи
            TextView textViewDressShortDescriptionValue = (TextView) view.findViewById(R.id.textViewDressShortDescriptionValue);

            String dressShortDescriptionTitle = "";

            if(DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_SHORT_DESCRIPTION)) {
                if(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_SHORT_DESCRIPTION) != null) {
                    dressShortDescriptionTitle = DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_SHORT_DESCRIPTION).trim();
                }
            }

            if(textViewDressShortDescriptionValue != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressShortDescriptionValue.setTypeface(GlobalFlags.getAppTypeface());
                }

                // Выводим непосредственно краткое описание текущей вещи
                if(!dressShortDescriptionTitle.equalsIgnoreCase("")) {
                    textViewDressShortDescriptionValue.setTextColor(ContextCompat.getColor(this.getContext(), R.color.color_white));
                    textViewDressShortDescriptionValue.setText(dressShortDescriptionTitle);
                }
                else {
                    textViewDressShortDescriptionValue.setTextColor(ContextCompat.getColor(this.getContext(), R.color.color_red));
                    textViewDressShortDescriptionValue.setText(R.string.string_dress_short_description_no);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для надписи "Общие параметры поиска"
            TextView textViewDressParameters = (TextView) view.findViewById(R.id.textViewDressParameters);

            if(textViewDressParameters != null) {
                // Если тип просматриваемого содержимого основного окна приложения - ОДЕЖДА
                if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                    // Устанавливаем шрифт
                    if (GlobalFlags.getAppTypeface() != null) {
                        textViewDressParameters.setTypeface(GlobalFlags.getAppTypeface());
                    }

                    // Делаем надпись "Общие параметры поиска" видимой
                    textViewDressParameters.setVisibility(View.VISIBLE);
                }
                // Иначе, если тип просматриваемого содержимого основного окна приложения -
                // СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ ДЛЯ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
                else if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                    // Делаем надпись "Общие параметры поиска" НЕвидимой
                    textViewDressParameters.setVisibility(View.GONE);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для надписи "Цвет"
            TextView textViewDressColorLabel = (TextView) view.findViewById(R.id.textViewDressColorLabel);

            if(textViewDressColorLabel != null) {
                // Устанавливаем шрифт
                if (GlobalFlags.getAppTypeface() != null) {
                    textViewDressColorLabel.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Если тип просматриваемого содержимого основного окна приложения - ОДЕЖДА
            if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                // Скрываем значок, обозначающий текущий цвет данной одежды
                ImageView imageViewDressCurrentColor = (ImageView) view.findViewById(R.id.imageViewDressCurrentColor);

                if(imageViewDressCurrentColor != null) {
                    imageViewDressCurrentColor.setVisibility(View.GONE);
                }

                // Отображаем панель с возможными цветами одежды
                LinearLayout linearLayoutDressColor = (LinearLayout) view.findViewById(R.id.linearLayoutDressColor);

                if(linearLayoutDressColor != null) {
                    linearLayoutDressColor.setVisibility(View.VISIBLE);
                }

                // Инициализируем все возможные цвета
                // Инициализируем массив, хранящий ссылки на элементы ImageView, представляющие собой возможные цвета одежды
                this.setArrayImageViewDressColor(new HashMap<String, ImageView>());
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_RED, (ImageView) view.findViewById(R.id.imageViewDressColorRed));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_ORANGE, (ImageView) view.findViewById(R.id.imageViewDressColorOrange));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_YELLOW, (ImageView) view.findViewById(R.id.imageViewDressColorYellow));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_GREEN, (ImageView) view.findViewById(R.id.imageViewDressColorGreen));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_CYAN, (ImageView) view.findViewById(R.id.imageViewDressColorCyan));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_BLUE, (ImageView) view.findViewById(R.id.imageViewDressColorBlue));
                this.getArrayImageViewDressColor().put(GlobalFlags.TAG_COLOR_VALUE_MAGENTA, (ImageView) view.findViewById(R.id.imageViewDressColorMagenta));

                // Выделяем необходимый цвет
                if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_COLOR)) {
                    if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR) != null) {
                        if (this.getArrayImageViewDressColor().containsKey(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR))) {
                            if (this.getArrayImageViewDressColor().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR)) != null) {
                                this.getArrayImageViewDressColor().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR)).setImageResource(R.drawable.background_color_selected);
                                this.getArrayImageViewDressColor().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR)).setTag(1);
                            }
                        }
                    }
                }

                // Устанавливаем соответствующие обработчики событий
                Collection<String> collectionKeyArrayImageViewDressColor = this.getArrayImageViewDressColor().keySet();

                for (String keyArrayImageViewDressColor : collectionKeyArrayImageViewDressColor) {
                    if (this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor) != null) {
                        this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).setOnClickListener(buttonDressColorClick);
                    }
                }
            }
            // Иначе, если тип просматриваемого содержимого основного окна приложения -
            // СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ ДЛЯ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
            else if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                // Скрываем панель с возможными цветами одежды
                LinearLayout linearLayoutDressColor = (LinearLayout) view.findViewById(R.id.linearLayoutDressColor);

                if(linearLayoutDressColor != null) {
                    linearLayoutDressColor.setVisibility(View.GONE);
                }

                // Отображаем значок, обозначающий текущий цвет данной одежды, и закрышиваем его соответствующим цветом
                ImageView imageViewDressCurrentColor = (ImageView) view.findViewById(R.id.imageViewDressCurrentColor);

                if(imageViewDressCurrentColor != null) {
                    // Закрашиваем данный значок соответствующим цветом
                    if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_COLOR)) {
                        if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR) != null) {
                            switch (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR)) {
                                case GlobalFlags.TAG_COLOR_VALUE_RED:                               // красный цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_red));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_ORANGE:                            // оранжевый цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_orange));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_YELLOW:                            // желтый цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_yellow));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_GREEN:                             // зеленый цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_green));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_CYAN:                              // голубой цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_cyan));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_BLUE:                              // синий цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_blue));
                                    break;
                                case GlobalFlags.TAG_COLOR_VALUE_MAGENTA:                           // фиолетовый цвет
                                    imageViewDressCurrentColor.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.color_magenta));
                                    break;
                            }
                        }
                    }

                    // Делаем видимым указанный значок
                    imageViewDressCurrentColor.setVisibility(View.VISIBLE);
                }
            }

            //--------------------------------------------------------------------------------------
            // Если тип просматриваемого содержимого основного окна приложения - ОДЕЖДА
            if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                // Скрываем надпись отображающую стиль текущей одежды
                TextView textViewDressCurrentStyle = (TextView) view.findViewById(R.id.textViewDressCurrentStyle);

                if(textViewDressCurrentStyle != null) {
                    textViewDressCurrentStyle.setVisibility(View.GONE);
                }

                //----------------------------------------------------------------------------------
                // Отображаем прокручивающий список возможных стилей одежды
                RelativeLayout relativeLayoutDressStyle = (RelativeLayout) view.findViewById(R.id.relativeLayoutDressStyle);

                if(relativeLayoutDressStyle != null) {
                    relativeLayoutDressStyle.setVisibility(View.VISIBLE);
                }

                //----------------------------------------------------------------------------------
                // Формируем адаптер для ViewPager, содержащего возможные стили одежды
                final ViewPager viewPagerDressStyleValue = (ViewPager) view.findViewById(R.id.viewPagerDressStyleValue);

                // Порядковый номер пункта, который должен быть выделен
                int positionSelected = 0;

                ArrayList<HashMap<String, String>> arrayItemParamsDressStyle = new ArrayList<>();

                if (GlobalFlags.getArrayDressStyleValue() != null && GlobalFlags.getArrayDressStyleNameValue() != null) {
                    for (int indexDressStyle = 0; indexDressStyle < GlobalFlags.getArrayDressStyleValue().size(); indexDressStyle++) {
                        HashMap<String, String> currentItemParamsDressStyle = new HashMap<>();
                        currentItemParamsDressStyle.put(GlobalFlags.TAG_TITLE, GlobalFlags.getArrayDressStyleNameValue().get(GlobalFlags.getArrayDressStyleValue().get(indexDressStyle)));
                        currentItemParamsDressStyle.put(GlobalFlags.TAG_VALUE, GlobalFlags.getArrayDressStyleValue().get(indexDressStyle));

                        arrayItemParamsDressStyle.add(currentItemParamsDressStyle);

                        // Если значение для текущего пункта совпадает со значением для текущей вещи,
                        // то запоминаем порядковый номер позиции текущего пункта как выделенного
                        if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_STYLE)) {
                            if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE) != null) {
                                if (GlobalFlags.getArrayDressStyleValue().get(indexDressStyle).equals(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE))) {
                                    positionSelected = indexDressStyle;
                                }
                            }
                        }
                    }
                }

                this.setPagerAdapterMultipleViewPagesDressStyle(new PagerAdapterMultipleViewPages(arrayItemParamsDressStyle, viewPagerDressStyleValue, positionSelected));

                if (viewPagerDressStyleValue != null) {
                    viewPagerDressStyleValue.setAdapter(this.getPagerAdapterMultipleViewPagesDressStyle());
                    viewPagerDressStyleValue.setCurrentItem(positionSelected);
                    viewPagerDressStyleValue.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            // Задаем порядковый номер выделенной позиции
                            if (DialogShowDressInfo.this.getPagerAdapterMultipleViewPagesDressStyle() != null) {
                                DialogShowDressInfo.this.getPagerAdapterMultipleViewPagesDressStyle().setSelectedPosition(
                                        viewPagerDressStyleValue.getCurrentItem() + 1
                                );
                                DialogShowDressInfo.this.getPagerAdapterMultipleViewPagesDressStyle().notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    //------------------------------------------------------------------------------
                    // Инициализируем кнопки-стрелки листания возможных стилей одежды

                    // Листание влево
                    ImageView imageViewArrowLeft = (ImageView) view.findViewById(R.id.imageViewArrowLeft);

                    if (imageViewArrowLeft != null) {
                        imageViewArrowLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Осуществляем переметоку, если не достигнуто начало списка
                                if (viewPagerDressStyleValue.getCurrentItem() > 0) {
                                    viewPagerDressStyleValue.setCurrentItem(viewPagerDressStyleValue.getCurrentItem() - 1);
                                }
                            }
                        });
                    }

                    // Листание вправо
                    ImageView imageViewArrowRight = (ImageView) view.findViewById(R.id.imageViewArrowRight);

                    if (imageViewArrowRight != null) {
                        imageViewArrowRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Осуществляем переметоку, если не достигнут конец списка
                                if (viewPagerDressStyleValue.getCurrentItem() < viewPagerDressStyleValue.getChildCount() - 3) {
                                    viewPagerDressStyleValue.setCurrentItem(viewPagerDressStyleValue.getCurrentItem() + 1);
                                }
                            }
                        });
                    }
                }
            }
            // Иначе, если тип просматриваемого содержимого освного окна приложения -
            // СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ ДЛЯ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
            else if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                // Скрываем прокручивающий список возможных стилей одежды
                RelativeLayout relativeLayoutDressStyle = (RelativeLayout) view.findViewById(R.id.relativeLayoutDressStyle);

                if (relativeLayoutDressStyle != null) {
                    relativeLayoutDressStyle.setVisibility(View.GONE);
                }

                //----------------------------------------------------------------------------------
                // Отображаем надпись отображающую стиль текущей одежды
                TextView textViewDressCurrentStyle = (TextView) view.findViewById(R.id.textViewDressCurrentStyle);

                if (textViewDressCurrentStyle != null) {
                    // Устанавливаем шрифт
                    if(GlobalFlags.getAppTypeface() != null) {
                        textViewDressCurrentStyle.setTypeface(GlobalFlags.getAppTypeface());
                    }

                    // Выводим название текущего стиля одежды
                    if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_STYLE)) {
                        if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE) != null) {
                            if(GlobalFlags.getArrayDressStyleNameValue() != null) {
                                if(GlobalFlags.getArrayDressStyleNameValue().containsKey(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE))) {
                                    if(GlobalFlags.getArrayDressStyleNameValue().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE)) != null) {
                                        textViewDressCurrentStyle.setText(GlobalFlags.getArrayDressStyleNameValue().get(
                                                DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE)).toLowerCase().trim()
                                        );
                                    }
                                }
                            }
                        }
                    }

                    // Делаем видимым текущую надпись
                    textViewDressCurrentStyle.setVisibility(View.VISIBLE);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для кнопки "СНЯТЬ"
            TextView textViewButtonDelete = (TextView) view.findViewById(R.id.textViewButtonDelete);

            if(textViewButtonDelete != null) {
                if(GlobalFlags.getAppTypeface() != null) {
                    textViewButtonDelete.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик события для кнопки "УДАЛИТЬ"
            RelativeLayout relativeLayoutButtonDelete = (RelativeLayout) view.findViewById(R.id.relativeLayoutButtonDelete);

            if(relativeLayoutButtonDelete != null) {
                // Если тип просматриваемого содержимого в главном окне приложения - ОДЕЖДА
                if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                    // Делаем видимым данную кнопку
                    relativeLayoutButtonDelete.setVisibility(View.VISIBLE);

                    // Устанавливаем обработчик клика
                    relativeLayoutButtonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Создаем диалоговое окно подтверждения удаления/закрытия информации об одежде
                            AlertDialog.Builder quitDialog = new AlertDialog.Builder(DialogShowDressInfo.this.getContext());

                            // Устанавливаем заголовок окна
                            quitDialog.setTitle(R.string.string_text_dialog_dress_close);

                            // Кнопка "Да" формируемого диалогового окна
                            quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_ID) && DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_TYPE)) {
                                        if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID) != null &&
                                                DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) != null) {
                                            // Удаляем информацию о текущей вещи из глобального массива
                                            DBMain.deleteDressFromGlobalArrayById(
                                                    Integer.parseInt(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_ID)),
                                                    GlobalFlags.getDressForWho(),
                                                    DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE)
                                            );

                                            //------------------------------------------------------
                                            // Очищаем соответствующий элемент ViewPager
                                            if (DBMain.getArrayViewPagerDressroom() != null) {
                                                if (DBMain.getArrayViewPagerDressroom().containsKey(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE))) {
                                                    ViewPager currentDressroomViewPager = DBMain.getArrayViewPagerDressroom().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE));

                                                    if (currentDressroomViewPager != null) {
                                                        currentDressroomViewPager.setTag(null);
                                                        currentDressroomViewPager.removeAllViews();
                                                    }
                                                }
                                            }

                                            //------------------------------------------------------
                                            // Уничтожаем соответствующий элемент PagerAdapterDressroom
                                            if (DBMain.getArrayPagerAdapterDressroom() != null) {
                                                if (DBMain.getArrayPagerAdapterDressroom().containsKey(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE))) {
                                                    if (DBMain.getArrayPagerAdapterDressroom().get(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE)) != null) {
                                                        DBMain.getArrayPagerAdapterDressroom().remove(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE));
                                                    }
                                                }
                                            }

                                            //------------------------------------------------------
                                            // Удаляем информацию о текущей группе одежды из массива ArrayDressSizeReal
                                            HashMap<String, Double> arrayDressSizeReal = DBMain.getArrayDressSizeReal(GlobalFlags.getDressForWho());

                                            if (arrayDressSizeReal != null) {
                                                if (arrayDressSizeReal.containsKey("x_" + DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) + "_1")) {
                                                    arrayDressSizeReal.remove("x_" + DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) + "_1");
                                                }

                                                if (arrayDressSizeReal.containsKey("y_" + DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) + "_1")) {
                                                    arrayDressSizeReal.remove("y_" + DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) + "_1");
                                                }

                                                // Обновляем глобальный массив
                                                DBMain.setArrayDressSizeReal(GlobalFlags.getDressForWho(), arrayDressSizeReal);
                                            }

                                            //------------------------------------------------------
                                            // Определяем какие группы одежды отображены в текущий момент
                                            // на виртуальном манекене, кроме группы "Аксессуары"
                                            ArrayList<String> arrayDressGroupExists = DBMain.getArrayDressGroupExists();

                                            //------------------------------------------------------
                                            // Вычисляем размеры одежды, к которым необходимо подогнать
                                            // реальные размеры рассматриваемых вещей после закрытия текущей вещи
                                            HashMap<String, Integer> arrayDressSizeTarget = new HashMap<>();

                                            // Изначально размеры, к которым надо подогнать исходные размеры изображений равны исходным
                                            if (arrayDressSizeReal != null) {
                                                Collection<String> arrayDressSizeRealKeyCollection = arrayDressSizeReal.keySet();

                                                for (String arrayDressSizeRealKey : arrayDressSizeRealKeyCollection) {
                                                    arrayDressSizeTarget.put(arrayDressSizeRealKey, arrayDressSizeReal.get(arrayDressSizeRealKey).intValue());
                                                }
                                            }

                                            try {
                                                arrayDressSizeTarget = BitmapDecode.calculateBitmapSize(arrayDressGroupExists, arrayDressSizeReal);
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                                FunctionsLog.logPrint("Error Calculate Bitmap Size: " + exception.toString());
                                            }

                                            //------------------------------------------------------
                                            // Сохраняем массив с размерами, к которым необходимо преобразовать размеры одежды, в глобальный массив
                                            DBMain.setArrayDressSizeTarget(GlobalFlags.getDressForWho(), arrayDressSizeTarget);

                                            //------------------------------------------------------
                                            // Перегружаем все адаптеры для всех элементов ViewPager, кроме текущего currentViewDressType
                                            DBMain.restartPagerAdapterDressroom(DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE));

                                            //------------------------------------------------------
                                            // Переинициализируем всплывающее окно добавления новой одежды
                                            if (DialogShowDressInfo.this.getContext() != null) {
                                                if (DialogShowDressInfo.this.getContext().getClass().toString().contains("MainActivity")) {
                                                    ((MainActivity) DialogShowDressInfo.this.getContext()).createTabsForLinearLayoutDressAdd();
                                                }
                                            }

                                            //------------------------------------------------------
                                            // формируем ссылку на кнопку сохранения информации о текущем наборе одежды
                                            // в главном окне приложения
                                            ImageView buttonDressSave = null;

                                            if(DialogShowDressInfo.this.getContext() != null) {
                                                if (DialogShowDressInfo.this.getContext().getClass().toString().contains("MainActivity")) {
                                                    buttonDressSave = ((MainActivity) DialogShowDressInfo.this.getContext()).getButtonDressSave();
                                                }
                                            }

                                            // Проверяем является ли оставшийся набор одежды сохраненным для текущего пользователя
                                            MySQLCheckIsSaveCurrentCollection mySQLCheckIsSaveCurrentCollection = new MySQLCheckIsSaveCurrentCollection(buttonDressSave);
                                            mySQLCheckIsSaveCurrentCollection.startCheckIsSaveCurrentCollection();

                                            //------------------------------------------------------
                                            // Закрываем текущее диалоговое окно с описанием текущей одежды
                                            if(DialogShowDressInfo.this.getAlertDialogShowDressInfo() != null) {
                                                DialogShowDressInfo.this.getAlertDialogShowDressInfo().dismiss();
                                            }
                                        }
                                    }
                                    // Выводим соответствующее сообщение об ошибке
                                    else {
                                        Toast toastDressCloseError = Toast.makeText(DialogShowDressInfo.this.getContext(), R.string.string_show_dress_info_close, Toast.LENGTH_SHORT);
                                        toastDressCloseError.setGravity(Gravity.CENTER, 0, 0);
                                        toastDressCloseError.show();
                                    }
                                }
                            });

                            // Кнопка "Нет" формируемого диалогового окна
                            quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            // Отображаем диалоговое окно
                            quitDialog.show();
                        }
                    });
                }
                // Иначе, если тип просматриваемого содержимого главного окна приложения - СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ
                else if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                    // Делаем невидимым данную кнопку
                    relativeLayoutButtonDelete.setVisibility(View.GONE);
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем шрифт для надписи "ПРИМЕНИТЬ"
            TextView textViewButtonApply = (TextView) view.findViewById(R.id.textViewButtonApply);

            if(textViewButtonApply != null) {
                if(GlobalFlags.getAppTypeface() != null) {
                    textViewButtonApply.setTypeface(GlobalFlags.getAppTypeface());
                }
            }

            //--------------------------------------------------------------------------------------
            // Задаем обработчик клика для кнопки "ПРИМЕНИТЬ"
            RelativeLayout relativeLayoutButtonApply = (RelativeLayout) view.findViewById(R.id.relativeLayoutButtonApply);

            if(relativeLayoutButtonApply != null) {
                // Если тип просматриваемого содержимого в главном окне приложения - ОДЕЖДА
                if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_DRESS) {
                    // Делаем видимым данную кнопку
                    relativeLayoutButtonApply.setVisibility(View.VISIBLE);

                    // Устанавливаем обработчик клика
                    relativeLayoutButtonApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Собираем информацию о выбранных параметрах, для которых пользователь
                            // собирается считать информацию из БД

                            //----------------------------------------------------------------------
                            // Цвет одежды
                            String targetDressColor = GlobalFlags.TAG_COLOR_VALUE_RED;

                            if (DialogShowDressInfo.this.getArrayImageViewDressColor() != null) {
                                Collection<String> collectionKeyArrayImageViewDressColor = DialogShowDressInfo.this.getArrayImageViewDressColor().keySet();

                                for (String keyArrayImageViewDressColor : collectionKeyArrayImageViewDressColor) {
                                    if (DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor) != null) {
                                        // Определяем тег для текущего цвета
                                        int currentImageViewDressColorTag = 0;

                                        if (DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).getTag() != null) {
                                            currentImageViewDressColorTag = (int) DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).getTag();
                                        }

                                        // Для выбранного цвета тег равен 1, а для остальных 0
                                        if (currentImageViewDressColorTag == 1) {
                                            switch (DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).getId()) {
                                                case R.id.imageViewDressColorRed:                       // красный цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_RED;
                                                    break;
                                                case R.id.imageViewDressColorOrange:                    // оранжевый цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_ORANGE;
                                                    break;
                                                case R.id.imageViewDressColorYellow:                    // желтый цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_YELLOW;
                                                    break;
                                                case R.id.imageViewDressColorGreen:                     // зеленый цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_GREEN;
                                                    break;
                                                case R.id.imageViewDressColorCyan:                      // голубой цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_CYAN;
                                                    break;
                                                case R.id.imageViewDressColorBlue:                      // синий цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_BLUE;
                                                    break;
                                                case R.id.imageViewDressColorMagenta:                   // фиолетовый цвет
                                                    targetDressColor = GlobalFlags.TAG_COLOR_VALUE_MAGENTA;
                                                    break;
                                            }

                                            // Завершаем выполнение цикла
                                            break;
                                        }
                                    }
                                }
                            }

                            //----------------------------------------------------------------------
                            // Стидь одежды
                            String targetDressStyle = GlobalFlags.TAG_STYLE_VALUE_ALL;

                            // Извлекаем параметры для текущего выбранного пункта из возможных стилей одежды
                            HashMap<String, String> currentItemParamsDressStyle = DialogShowDressInfo.this.getPagerAdapterMultipleViewPagesDressStyle().getSelectedItemParams();

                            // Извлекаем значение стиля одежды для текущего выбранного пункта из возможных стилей одежды
                            if (currentItemParamsDressStyle != null) {
                                if (currentItemParamsDressStyle.containsKey(GlobalFlags.TAG_VALUE)) {
                                    if (currentItemParamsDressStyle.get(GlobalFlags.TAG_VALUE) != null) {
                                        targetDressStyle = currentItemParamsDressStyle.get(GlobalFlags.TAG_VALUE);
                                    }
                                }
                            }

                            //----------------------------------------------------------------------
                            // Загружаем данные об одежде, удовлетворяющей выбранным параметрам только
                            // при условии, что выбранные параметры отличаются от параметров текущей
                            // отображаемой одежды

                            // Считываем параметры текущей отображаемой одежды
                            String currentDressCategoryId = "0";
                            String currentDressType = GlobalFlags.TAG_DRESS_HEAD;
                            String currentDressColor = GlobalFlags.TAG_COLOR_VALUE_RED;
                            String currentDressStyle = GlobalFlags.TAG_STYLE_VALUE_ALL;

                            if (DialogShowDressInfo.this.getArrayDressInfo() != null) {
                                if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_CATID)) {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_CATID) != null) {
                                        currentDressCategoryId = DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_CATID);
                                    }
                                }

                                if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_TYPE)) {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE) != null) {
                                        currentDressType = DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_TYPE);
                                    }
                                }

                                if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_COLOR)) {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR) != null) {
                                        currentDressColor = DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_COLOR);
                                    }
                                }

                                if (DialogShowDressInfo.this.getArrayDressInfo().containsKey(GlobalFlags.TAG_STYLE)) {
                                    if (DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE) != null) {
                                        currentDressStyle = DialogShowDressInfo.this.getArrayDressInfo().get(GlobalFlags.TAG_STYLE);
                                    }
                                }
                            }

                            // Если не совпадают
                            if (!targetDressColor.equals(currentDressColor) || !targetDressStyle.equals(currentDressStyle)) {
                                // Получаем изображение одежды для указанных параметров
                                MySQLGoToDress mySQLGoToDress = new MySQLGoToDress(DialogShowDressInfo.this.getContext());

                                mySQLGoToDress.startGoToDress(GlobalFlags.ACTION_NO,
                                        currentDressCategoryId,
                                        currentDressType,
                                        targetDressColor,
                                        targetDressStyle,
                                        DialogShowDressInfo.this.getAlertDialogShowDressInfo());
                            }
                            // Иначе выводим предупреждение о том, что выбранные параметры не отличаются от исходных
                            else {
                                Toast toastDressSameParams = Toast.makeText(DialogShowDressInfo.this.getContext(), R.string.string_target_params_same_current, Toast.LENGTH_LONG);
                                toastDressSameParams.setGravity(Gravity.CENTER, 0, 0);
                                toastDressSameParams.show();
                            }
                        }
                    });
                }
                // Иначе, если тип просматриваемого содержимого в главном окне приложения - СОХРАНЕННЫЕ НАБОРЫ ОДЕЖДЫ
                if(MainActivity.getMainActivityViewType() == GlobalFlags.MAIN_ACTIVITY_VIEW_TYPE_COLLECTION) {
                    // Делаем видимым данную кнопку
                    relativeLayoutButtonApply.setVisibility(View.GONE);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Create Content For Dialog Dress Info: " + exception.toString());
            return false;
        }

        return true;
    }

    //==============================================================================================
    // Обработчик клика по цвету одежды
    private View.OnClickListener buttonDressColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(DialogShowDressInfo.this.getArrayImageViewDressColor() != null) {
                Collection<String> collectionKeyArrayImageViewDressColor = DialogShowDressInfo.this.getArrayImageViewDressColor().keySet();

                for (String keyArrayImageViewDressColor : collectionKeyArrayImageViewDressColor) {
                    if (DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor) != null) {
                        // Если это цвет, по которому кликнули
                        if(DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).getId() == v.getId()) {
                            // Выделяем данный цвет
                            DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).setImageResource(R.drawable.background_color_selected);
                            DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).setTag(1);
                        }
                        // Иначе
                        else {
                            // Устанавливаем первоначальные цвета квадратов
                            DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).setImageResource(android.R.color.transparent);
                            DialogShowDressInfo.this.getArrayImageViewDressColor().get(keyArrayImageViewDressColor).setTag(0);
                        }
                    }
                }
            }
        }
    };
}
