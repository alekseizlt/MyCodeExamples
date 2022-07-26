package ru.alexprogs.dressroom.adapter;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.MainActivity;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDress;
import ru.alexprogs.dressroom.db.DBMain;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Адаптер для листания одежды, примеряемой на виртуальном манекене
public class PagerAdapterDressroom extends PagerAdapter {

    // Свойства данного класса
    private ArrayList<HashMap<String, String>> mArrayParams;    // массив параметров для соответствующей вещи
    private int mArrayParamsPositionStart;                      // номер стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
    private int mArrayParamsPositionEnd;                        // номер конечной позиции, до которой в массиве mArrayParams присутствуют параметры

    //==============================================================================================
    // Метод для считывания массива параметров для соответствующей вещи
    public ArrayList<HashMap<String, String>> getArrayParams() {
        return this.mArrayParams;
    }

    //==============================================================================================
    // Метод для задания массива параметров для соответствующей вещи
    public void setArrayParams(ArrayList<HashMap<String, String>> arrayParams) {
        this.mArrayParams = arrayParams;
    }

    //==============================================================================================
    // Метод для считывания значения номера стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
    public int getArrayParamsPositionStart() {
        return this.mArrayParamsPositionStart;
    }

    //==============================================================================================
    // Метод для задания значения номера стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
    public void setArrayParamsPositionStart(int arrayParamsPositionStart) {
        this.mArrayParamsPositionStart = arrayParamsPositionStart;
    }

    //==============================================================================================
    // Метод для считывания значения номера конечной позиции, до которой в массиве mArrayParams присутствуют параметры
    public int getArrayParamsPositionEnd() {
        return this.mArrayParamsPositionEnd;
    }

    //==============================================================================================
    // Метод для задания значения номера конечной позиции, до которой в массиве mArrayParams присутствуют параметры
    public void setArrayParamsPositionEnd(int arrayParamsPositionEnd) {
        this.mArrayParamsPositionEnd = arrayParamsPositionEnd;
    }

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // arrayParams - массив параметров для
    public PagerAdapterDressroom(ArrayList<HashMap<String, String>> arrayParams) {
        // Задаем массив параметров для соответствующей вещи
        this.setArrayParams(arrayParams);

        //------------------------------------------------------------------------------------------
        // Задаем номер стартовой позиции, с которой в массиве mArrayParams присутствуют параметры
        if(arrayParams == null) {
            this.setArrayParamsPositionStart(0);
        }
        else if(arrayParams.size() <= 0 ) {
            this.setArrayParamsPositionStart(0);
        }
        else {
            for (int indexItem = 0; indexItem < arrayParams.size(); indexItem++) {
                if(arrayParams.get(indexItem) != null) {
                    if (arrayParams.get(indexItem).containsKey(GlobalFlags.TAG_IMAGE)) {
                        if (arrayParams.get(indexItem).get(GlobalFlags.TAG_IMAGE) != null) {
                            this.setArrayParamsPositionStart(indexItem);
                            break;
                        }
                    }
                }
            }
        }

        //------------------------------------------------------------------------------------------
        // Задаем номер конечной позиции, с которой в массиве mArrayParams присутствуют параметры
        if(arrayParams == null) {
            this.setArrayParamsPositionEnd(0);
        }
        else if(arrayParams.size() <= 0 ) {
            this.setArrayParamsPositionEnd(0);
        }
        else {
            this.setArrayParamsPositionEnd(arrayParams.size() - 1);
        }
    }

    //==============================================================================================
    // Метод для считывания порядквого номера текущего элемента
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //==============================================================================================
    // Метод для считывания параметров для текущего элемента
    public HashMap<String, String> getItemParams(int position) {
        if(this.getArrayParams() == null) {
            return null;
        }
        else if(position < 0 || position > this.getArrayParams().size() - 1) {
            return null;
        }
        else {
            return this.getArrayParams().get(position);
        }
    }

    //==============================================================================================
    // Метод для считывания id для текущего элемента (одежды)
    public Integer getItemParamsId(int position) {
        if(this.getArrayParams() == null) {
            return 0;
        }
        else if(position < 0 || position > this.getArrayParams().size() - 1) {
            return 0;
        }
        else {
            // Возвращаемое значение
            int currentItemId = 0;

            if(this.getArrayParams().get(position) != null) {
                if (this.getArrayParams().get(position).containsKey(GlobalFlags.TAG_ID)) {
                    if (this.getArrayParams().get(position).get(GlobalFlags.TAG_ID) != null) {
                        currentItemId = Integer.parseInt(this.getArrayParams().get(position).get(GlobalFlags.TAG_ID));
                    }
                }
            }

            return currentItemId;
        }
    }

    //==============================================================================================
    // Метод для считывания позиции одежды, отображаемой в первую очередь
    public int getPositionDressShowNow() {
        // Переменная, содержащая номер позиции одежды, отображаемой в первую очередь
        int positionDressShowNow = 0;

        if(this.getArrayParams() != null) {
            for(int indexItem = 0; indexItem < this.getArrayParams().size(); indexItem++) {
                if(this.getArrayParams().get(indexItem) != null) {
                    if(this.getArrayParams().get(indexItem).containsKey(GlobalFlags.TAG_DRESS_SHOW_NOW)) {
                        if(this.getArrayParams().get(indexItem).get(GlobalFlags.TAG_DRESS_SHOW_NOW) != null) {
                            if(this.getArrayParams().get(indexItem).get(GlobalFlags.TAG_DRESS_SHOW_NOW).equals("1")) {
                                positionDressShowNow = indexItem;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return positionDressShowNow;
    }

    //==============================================================================================
    // Метод для получения количества элементов
    @Override
    public int getCount() {
        if(this.getArrayParams() == null) {
            return 0;
        }
        else {
            return this.getArrayParams().size();
        }
    }

    //==============================================================================================
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //==============================================================================================
    // Метод для создания элемента для соответствующего ViewPager
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View viewLayoutDressroom = null;

        if(this.getArrayParams() != null) {
            // Считываем параметры именно для текущей одежды
            HashMap<String, String> currentDressParams = this.getArrayParams().get(position);

            if(currentDressParams != null) {
                // Формируем внешний вид для текущего элемента, только при условии, что для
                // данного элемента присутствует изображение
                if(currentDressParams.containsKey(GlobalFlags.TAG_IMAGE)) {
                    if (currentDressParams.get(GlobalFlags.TAG_IMAGE) != null) {
                        viewLayoutDressroom = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.content_main_dressroom_item, container, false);

                        final ImageView imageViewDressroom = (ImageView) viewLayoutDressroom.findViewById(R.id.imageViewDressroom);
                        final ProgressBar progressBarDressroom = (ProgressBar) viewLayoutDressroom.findViewById(R.id.progressBarDressroom);
                        final TextView textViewLoadImageDressroom = (TextView) viewLayoutDressroom.findViewById(R.id.textViewLoadImageDressroom);

                        //--------------------------------------------------------------------------
                        // Устанавливаем шрифт для текста
                        if (textViewLoadImageDressroom != null && GlobalFlags.getAppTypeface() != null) {
                            textViewLoadImageDressroom.setTypeface(GlobalFlags.getAppTypeface());
                        }

                        //--------------------------------------------------------------------------
                        // Извлекаем всю необходимую иформацию о текущей вещи
                        String currentDressId = "0";            // id текущей вещи
                        String currentDressImage = null;        // ссылка на изображение для лицевой части для текущей вещи
                        String currentDressImageWidth = "0";    // оригинальная ширина изображения для лицевой части для текущей вещи
                        String currentDressImageHeight = "0";   // оригинальная высота изображения для лицевой части для текущей вещи
                        String currentDressType = null;         // тип текущей вещи (одежды)
                        String currentDressShowNow = "0";       // будет ли отображена текущая вещь в первую очередь

                        // id текущей вещи
                        if (currentDressParams.containsKey(GlobalFlags.TAG_ID)) {
                            if (currentDressParams.get(GlobalFlags.TAG_ID) != null) {
                                currentDressId = currentDressParams.get(GlobalFlags.TAG_ID);
                            }
                        }

                        // В зависимости от угла поворота виртуального манекена загружаем соответствующее изображение
                        switch (MainActivity.getDressRotationAngle()) {
                            case GlobalFlags.DRESS_ROTATION_ANGLE_0:                                // если в настоящий момент угол поворота манекена составляет 0 градусов
                                // Загружаем изображение для лицевой стороны
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE)) {
                                    currentDressImage = currentDressParams.get(GlobalFlags.TAG_IMAGE);
                                }

                                // Оригинальная ширина изображения для лицевой стороны для текущей вещи
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                                    if (currentDressParams.get(GlobalFlags.TAG_IMAGE_WIDTH) != null) {
                                        currentDressImageWidth = currentDressParams.get(GlobalFlags.TAG_IMAGE_WIDTH);
                                    }
                                }

                                // Оригинальная высота изображения для лицевой стороны для текущей вещи
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                                    if (currentDressParams.get(GlobalFlags.TAG_IMAGE_HEIGHT) != null) {
                                        currentDressImageHeight = currentDressParams.get(GlobalFlags.TAG_IMAGE_HEIGHT);
                                    }
                                }

                                break;

                            case GlobalFlags.DRESS_ROTATION_ANGLE_180:                              // если в настоящий момент угол поворота манекена составляет 180 градусов
                                // Загружаем изображение для обратной части
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE_BACK)) {
                                    currentDressImage = currentDressParams.get(GlobalFlags.TAG_IMAGE_BACK);
                                }

                                // Оригинальная ширина изображения для обратной стороны для текущей вещи
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE_BACK_WIDTH)) {
                                    if (currentDressParams.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH) != null) {
                                        currentDressImageWidth = currentDressParams.get(GlobalFlags.TAG_IMAGE_BACK_WIDTH);
                                    }
                                }

                                // Оригинальная высота изображения для обратной стороны для текущей вещи
                                if (currentDressParams.containsKey(GlobalFlags.TAG_IMAGE_BACK_HEIGHT)) {
                                    if (currentDressParams.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT) != null) {
                                        currentDressImageHeight = currentDressParams.get(GlobalFlags.TAG_IMAGE_BACK_HEIGHT);
                                    }
                                }

                                break;
                        }

                        // ссылка на изображение для лицевой части для текущей вещи
                        if (currentDressParams.containsKey(GlobalFlags.TAG_TYPE)) {
                            currentDressType = currentDressParams.get(GlobalFlags.TAG_TYPE);
                        }

                        // будет ли отображена текущая вещь в первую очередь
                        if (currentDressParams.containsKey(GlobalFlags.TAG_DRESS_SHOW_NOW)) {
                            if (currentDressParams.get(GlobalFlags.TAG_DRESS_SHOW_NOW) != null) {
                                currentDressShowNow = currentDressParams.get(GlobalFlags.TAG_DRESS_SHOW_NOW);
                            }
                        }

                        final int currentDressIdFinal = Integer.parseInt(currentDressId);
                        final String currentDressTypeFinal = currentDressType;

                        //--------------------------------------------------------------------------
                        // Загружаем соответствующее изображение
                        if (imageViewDressroom != null && currentDressImage != null) {
                            // Отображаем круговой индикатор загрузки изображения
                            if (progressBarDressroom != null) {
                                progressBarDressroom.setVisibility(View.VISIBLE);
                            }

                            //----------------------------------------------------------------------
                            // Устанавливаем обработчик клика по изображению одежды
                            imageViewDressroom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (currentDressIdFinal > 0 && currentDressTypeFinal != null) {
                                        // Загружаем данные о текущей вещи из БД
                                        MySQLGoToDress mySQLGoToDress = new MySQLGoToDress(DBMain.getContext());
                                        mySQLGoToDress.startGoToDress(GlobalFlags.ACTION_SHOW_DIALOG_DRESS_INFO, currentDressIdFinal);
                                    }
                                }
                            });

                            //----------------------------------------------------------------------
                            // Задаем значения ширины и высоты, под которые необходимо подгонять
                            // размеры загружаемых изображения
                            int targetImageWidth = Integer.parseInt(currentDressImageWidth);
                            int targetImageHeight = Integer.parseInt(currentDressImageHeight);

                            HashMap<String, Integer> arrayDressSizeTarget = DBMain.getArrayDressSizeTarget(GlobalFlags.getDressForWho());

                            if (arrayDressSizeTarget != null) {
                                if (arrayDressSizeTarget.containsKey("x_" + currentDressType + "_2") && arrayDressSizeTarget.containsKey("y_" + currentDressType + "_2")) {
                                    targetImageWidth = arrayDressSizeTarget.get("x_" + currentDressType + "_2");
                                    targetImageHeight = arrayDressSizeTarget.get("y_" + currentDressType + "_2");
                                }
                            }

                            //----------------------------------------------------------------------
                            // Если проинициализирован ViewPager для верха и низа, то задаем отрицательное верхнее смещеие
                            // для элемента ViewPager, отображаюещго одежду для низа
                            if(currentDressType != null) {
                                if(currentDressType.equalsIgnoreCase(GlobalFlags.TAG_DRESS_LEG)) {
                                    RelativeLayout.LayoutParams layoutParamsViewPagerDressRoomLeg = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                    );

                                    layoutParamsViewPagerDressRoomLeg.addRule(RelativeLayout.BELOW, R.id.viewPagerDressroomBody);

                                    if(DBMain.getArrayPagerAdapterDressroom() != null) {
                                        if (DBMain.getArrayPagerAdapterDressroom().containsKey(GlobalFlags.TAG_DRESS_BODY) &&
                                                DBMain.getArrayPagerAdapterDressroom().get(GlobalFlags.TAG_DRESS_BODY) != null) {
                                            layoutParamsViewPagerDressRoomLeg.setMargins(0, (int) ((-20) * GlobalFlags.DpToPx), 0, 0);
                                        } else {
                                            layoutParamsViewPagerDressRoomLeg.setMargins(0, 0, 0, 0);
                                        }

                                        if (DBMain.getArrayViewPagerDressroom() != null) {
                                            if (DBMain.getArrayViewPagerDressroom().containsKey(GlobalFlags.TAG_DRESS_LEG) &&
                                                    DBMain.getArrayViewPagerDressroom().get(GlobalFlags.TAG_DRESS_LEG) != null) {
                                                DBMain.getArrayViewPagerDressroom().get(GlobalFlags.TAG_DRESS_LEG).setLayoutParams(layoutParamsViewPagerDressRoomLeg);
                                            }
                                        }
                                    }
                                }
                            }

                            //----------------------------------------------------------------------
                            // Для вещей, отображаемых в первую очередь, устанавливаем высокий приоритет
                            Priority priorityLoadCurrentDressImage = Priority.NORMAL;

                            if (currentDressShowNow.equals("1")) {
                                priorityLoadCurrentDressImage = Priority.HIGH;
                            }

                            //----------------------------------------------------------------------
                            Glide.with(ApplicationContextProvider.getContext())
                                    .load(currentDressImage)
                                    .override(targetImageWidth, targetImageHeight)
                                    .transition(withCrossFade())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            // Делаем соответствующее изображение невидимым
                                            imageViewDressroom.setVisibility(View.GONE);

                                            // Делаем соответствующий элемент ProgressBar невидимым
                                            if (progressBarDressroom != null) {
                                                progressBarDressroom.setVisibility(View.GONE);
                                            }

                                            // Отображаем сообщение об ошибке, возникшей при загрузке изображения
                                            if (textViewLoadImageDressroom != null) {
                                                textViewLoadImageDressroom.setTextColor(ContextCompat.getColor(ApplicationContextProvider.getContext(), R.color.color_red));
                                                textViewLoadImageDressroom.setText(R.string.string_dress_image_error);
                                                textViewLoadImageDressroom.setVisibility(View.VISIBLE);
                                            }

                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            // Делаем соответствующий элемент ProgressBar невидимым
                                            if (progressBarDressroom != null) {
                                                progressBarDressroom.setVisibility(View.GONE);
                                            }

                                            // Делаем соответствующий элемент TextView невидимым
                                            if (textViewLoadImageDressroom != null) {
                                                textViewLoadImageDressroom.setVisibility(View.GONE);
                                            }

                                            // Делаем соответствующее изображение видимым
                                            imageViewDressroom.setVisibility(View.VISIBLE);

                                            return false;
                                        }
                                    })
                                    .priority(priorityLoadCurrentDressImage)
                                    .into(imageViewDressroom);
                        }
                        // Иначе, если изображение для текущей вещи отсутствует, то выводим об этом сообщение
                        else if(imageViewDressroom != null) {
                            // Делаем соответствующий элемент ProgressBar невидимым
                            if (progressBarDressroom != null) {
                                progressBarDressroom.setVisibility(View.GONE);
                            }

                            // Выводим соответствующее сообщение
                            if (textViewLoadImageDressroom != null) {
                                textViewLoadImageDressroom.setTextColor(ContextCompat.getColor(ApplicationContextProvider.getContext(), R.color.color_red));

                                // Выводим текст в зависимости от угла поворота
                                switch (MainActivity.getDressRotationAngle()) {
                                    case GlobalFlags.DRESS_ROTATION_ANGLE_0:                        // если в настоящий момент угол поворота манекена составляет 0 градусов
                                        textViewLoadImageDressroom.setText(R.string.string_dress_image_no);
                                        break;
                                    case GlobalFlags.DRESS_ROTATION_ANGLE_180:                      // если в настоящий момент угол поворота манекена составляет 180 градусов
                                        textViewLoadImageDressroom.setText(R.string.string_dress_image_back_no);
                                        break;
                                }

                                textViewLoadImageDressroom.setVisibility(View.VISIBLE);
                            }
                        }

                        ((ViewPager) container).addView(viewLayoutDressroom);
                    }
                }
            }
        }

        return viewLayoutDressroom;
    }

    //==============================================================================================
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
