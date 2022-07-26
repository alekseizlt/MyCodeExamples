package ru.alexprogs.dressroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.alexprogs.dressroom.db.mysql.MySQLGetShopList;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsLog;

public class ActivityShopList extends AppCompatActivity {

    // Свойства данного класса
    private int mDressId;                                           // id текущей одежды
    private String mDressBrandTitle;                                // название бренда для текущей одежды
    private RelativeLayout mRelativeLayoutShopList;                 // элемент RelativeLayout, представляющий собой основное содержимое окна

    //==============================================================================================
    // Метод, вызываемый при создании текущего Activity
    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list);

        //------------------------------------------------------------------------------------------
        // Инициализируем верхнюю панель инструментов
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        if(getSupportActionBar() != null) {
            // Меняем индикатор для стрелочки назад
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material_my);
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.color_element_clickable), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //------------------------------------------------------------------------------------------
        // Считываем значение переменной, определяющей id текущей одежды, для которой необходимо отобразить список магазинов
        // Данная переменная передана данному Activity в качестве параметра при помощи метода putExtra()
        try {
            Intent intentShopList = getIntent();

            if(intentShopList != null) {
                this.setDressId(intentShopList.getIntExtra(GlobalFlags.TAG_DRESS_ID, 0));
                this.setDressBrandTitle(intentShopList.getStringExtra(GlobalFlags.TAG_BRAND_TITLE));
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Create Activity Shop List: " + exception.toString());
        }

        //------------------------------------------------------------------------------------------
        // Инициализируем элемент, представляющий собой заголовок для верхней панели инструментов приложения
        TextView textViewToolbarTitle = (TextView) findViewById(R.id.textViewToolbarTitle);

        if(textViewToolbarTitle != null) {
            // Задаем шрифт для приложения
            if(GlobalFlags.getAppTypeface() != null) {
                textViewToolbarTitle.setTypeface(GlobalFlags.getAppTypeface());
            }

            // Задаем заголовок текущего окна
            String toolbarTitleText = getString(R.string.string_shops).toUpperCase().trim();

            if(this.getDressBrandTitle() != null) {
                toolbarTitleText += " " + this.getDressBrandTitle().toUpperCase().trim();
            }

            textViewToolbarTitle.setText(toolbarTitleText);
        }

        //------------------------------------------------------------------------------------------
        // Получаем ссылку на элемент RelativeLayout, представляющий собой основное содержимое окна
        this.setRelativeLayoutShopList((RelativeLayout) findViewById(R.id.relativeLayoutShopList));

        //------------------------------------------------------------------------------------------
        // Инициализируем кнопку отображения магазинов одежды на карте в виде маркеров
        RelativeLayout relativeLayoutButtonShopListMap = (RelativeLayout) findViewById(R.id.relativeLayoutButtonShopListMap);

        if(relativeLayoutButtonShopListMap != null) {
            relativeLayoutButtonShopListMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Отображаем Activity "Карта"
                    Intent intentMaps = new Intent(ActivityShopList.this, ActivityGoogleMaps.class);

                    // Передаем, что действие при отображении карты - отображение нескольких магазинов одежды на карте в виде маркера
                    intentMaps.putExtra(GlobalFlags.TAG_ACTION_ONLOAD_MAP, GlobalFlags.ACTION_ONLOAD_MAP_SUBTYPE_SHOW_SOME_SHOP);

                    // Передаем id текущей вещи
                    intentMaps.putExtra(GlobalFlags.TAG_DRESS_ID, ActivityShopList.this.getDressId());

                    startActivity(intentMaps);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Загружаем данные о магазинах для текущей одежды из удаленной БД в фоновом потоке
        MySQLGetShopList mySQLGetShopList = new MySQLGetShopList(
                this,
                true,
                false,
                this.getDressId(),
                this.getRelativeLayoutShopList()
        );

        mySQLGetShopList.startLoadShopList();
    }

    //==============================================================================================
    // Метод для считывания значения id текущей одежды, для которой необходимо считать список магазинов
    private int getDressId() {
        return this.mDressId;
    }

    //==============================================================================================
    // Метод для задания значения id текущей одежды, для которой необходимо считать список магазинов
    private void setDressId(int dressId) {
        this.mDressId = dressId;
    }

    //==============================================================================================
    // Метод для считывания названия бренда для текущей одежды, для которой необходимо считать список магазинов
    private String getDressBrandTitle() {
        return this.mDressBrandTitle;
    }

    //==============================================================================================
    // Метод для задания названия бренда для текущей одежды, для которой необходимо считать список магазинов
    private void setDressBrandTitle(String dressBrandTitle) {
        this.mDressBrandTitle = dressBrandTitle;
    }

    //==============================================================================================
    // Метод для считывания ссылки на элемент RelativeLayout, представляющий собой основное содержимое окна
    private RelativeLayout getRelativeLayoutShopList() {
        return this.mRelativeLayoutShopList;
    }

    //==============================================================================================
    // Метод для задания ссылки на элемент RelativeLayout, представляющий собой основное содержимое окна
    private void setRelativeLayoutShopList(RelativeLayout relativeLayoutShopList) {
        this.mRelativeLayoutShopList = relativeLayoutShopList;
    }
}
