package ru.alexprogs.dressroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.alexprogs.dressroom.db.mysql.MySQLGetShopFullInfo;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsLog;

public class ActivityShopInfo extends AppCompatActivity {

    // Свойства данного класса
    private int mShopId;                                                // id магазина, о котором необходимо отобразить информацию
    private RelativeLayout mRelativeLayoutShopFullInfo;                 // элемент RelativeLayout, представляющий собой основное содержимое окна

    //==============================================================================================
    // Метод, вызываемый при создании текущего Activity
    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_info);

        //------------------------------------------------------------------------------------------
        // Формируем панель инструментов
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
        // Считываем значение переменной, определяющей id магазина одежды, информацию о котором необходимо отобразить
        // Данная переменная передана данному Activity в качестве параметра при прмрщи метода putExtra()

        // Переменная, хранящая название текущего магазина
        String currentShopTitle = "";

        try {
            Intent intentShopInfo = getIntent();

            if(intentShopInfo != null) {
                this.setShopId(intentShopInfo.getIntExtra(GlobalFlags.TAG_SHOP_ID, 0));
                currentShopTitle = intentShopInfo.getStringExtra(GlobalFlags.TAG_SHOP_TITLE);
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Create Activity Shop Info: " + exception.toString());
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
            if(currentShopTitle.equalsIgnoreCase("") || currentShopTitle.equalsIgnoreCase("null")) {
                textViewToolbarTitle.setText(R.string.title_activity_shop_info);
            }
            else {
                textViewToolbarTitle.setText(currentShopTitle.toUpperCase().trim());
            }
        }

        //------------------------------------------------------------------------------------------
        // Задаем ссылку на элемент RelativeLayout, представляющий собой основное содержимое окна
        this.setRelativeLayoutShopFullInfo((RelativeLayout) findViewById(R.id.relativeLayoutShopFullInfo));

        //------------------------------------------------------------------------------------------
        // Загружаем все данные о текущем выбранном магазине одежды из БД в фоновом потоке
        MySQLGetShopFullInfo mySQLGetShopFullInfo = new MySQLGetShopFullInfo(
                this,
                this.getShopId(),
                this.getRelativeLayoutShopFullInfo()
        );

        mySQLGetShopFullInfo.startShopFullInfoLoad();
    }

    //==============================================================================================
    // Метод для считывания значения id магазина, о котором необходимо отобразить информацию
    private int getShopId() {
        return this.mShopId;
    }

    //==============================================================================================
    // Метод для задания значения id магазина, о котором необходимо отобразить информацию
    private void setShopId(int shopId) {
        this.mShopId = shopId;
    }

    //==============================================================================================
    // Метод для считывания ссылки на элемент RelativeLayout, представляющий собой основное содержимое окна
    private RelativeLayout getRelativeLayoutShopFullInfo() {
        return this.mRelativeLayoutShopFullInfo;
    }

    //==============================================================================================
    // Метод для задания ссылки на элемент RelativeLayout, представляющий собой основное содержимое окна
    private void setRelativeLayoutShopFullInfo(RelativeLayout relativeLayoutShopFullInfo) {
        this.mRelativeLayoutShopFullInfo = relativeLayoutShopFullInfo;
    }
}
