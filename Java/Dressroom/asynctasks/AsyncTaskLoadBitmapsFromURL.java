package ru.alexprogs.dressroom.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.ActivityFullScreenImageShare;
import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsConnection;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.images.BitmapDecode;

// Класс для выполнения асинхронной операции скачивания необходимых изобраэений одежды
// и формирования из них одного общего изображения
public class AsyncTaskLoadBitmapsFromURL extends AsyncTask<String, Void, Bitmap> {

    // Свойства данного класса
    private Context mContext;                                           // контекст
    private ArrayList<HashMap<String, String>> mArrayDressImage;        // массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
    private RelativeLayout mRelativeLayoutCollectionShare;              // элемент RelativeLayout, являющийся контейнером для итогового суммарного изображения

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    // arrayDressImage - массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
    // relativeLayoutCollectionShare - элемент RelativeLayout, являющийся контейнером для итогового суммарного изображения
    public AsyncTaskLoadBitmapsFromURL(Context context, ArrayList<HashMap<String, String>> arrayDressImage, RelativeLayout relativeLayoutCollectionShare) {
        this.setContext(context);                                                   // контекст
        this.setArrayDressImage(arrayDressImage);                                   // массив, содержащий ссылки на изображения, входящие в состав текущей коллекции
        this.setRelativeLayoutCollectionShare(relativeLayoutCollectionShare);       // элемент RelativeLayout, являющийся контейнером для итогового суммарного изображения
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
    // Метод для считывания массива, содержащего ссылки на изображения, входящие в состав текущей коллекции
    private ArrayList<HashMap<String, String>> getArrayDressImage() {
        return this.mArrayDressImage;
    }

    //==============================================================================================
    // Метод для задания массива, содержащего ссылки на изображения, входящие в состав текущей коллекции
    private void setArrayDressImage(ArrayList<HashMap<String, String>> arrayDressImage) {
        this.mArrayDressImage = arrayDressImage;
    }

    //==============================================================================================
    // Метод для считывания ссылки на элемент RelativeLayout, являющийся контейнером для итогового
    // суммарного изображения
    private RelativeLayout getRelativeLayoutCollectionShare() {
        return this.mRelativeLayoutCollectionShare;
    }

    //==============================================================================================
    // Метод для задания ссылки на элемент RelativeLayout, являющийся контейнером для итогового
    // суммарного изображения
    private void setRelativeLayoutCollectionShare(RelativeLayout relativeLayoutCollectionShare) {
        this.mRelativeLayoutCollectionShare = relativeLayoutCollectionShare;
    }

    //==============================================================================================
    // Перед началом фонового потока
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //==============================================================================================
    // Выполняем операцию скачивания изображений
    protected Bitmap doInBackground(String... args) {
        // Возвращаемое итоговое суммарное изображение
        Bitmap bitmapOverlay = null;

        //------------------------------------------------------------------------------------------
        // Проверяем наличие Интернет-соединения
        Boolean isInternetConnection = FunctionsConnection.isInternetConnection();

        // Если отсутствует Интернет-соединение, то выводим об этом соответствующее уведомление
        if(!isInternetConnection.equals(true)) {
            if(this.getContext() != null) {
                Toast toastNoInternetConnection = Toast.makeText(this.getContext(), R.string.string_no_internet_connection_toast, Toast.LENGTH_SHORT);
                toastNoInternetConnection.setGravity(Gravity.CENTER, 0, 0);
                toastNoInternetConnection.show();
            }
        }

        //------------------------------------------------------------------------------------------
        // Если успешно передан массив, содержащий информацию о необходимых изображениях
        if(this.getArrayDressImage() != null) {
            // Определяем минимальную ширину среди всех изображений
            int minDressImageWidth = 0;

            // В цикле перебираем все изображения
            for (int indexImage = 0; indexImage < this.getArrayDressImage().size(); indexImage++) {
                // Ширина текущего изображения
                int currentDressImageWidth = 0;

                if (this.getArrayDressImage().get(indexImage) != null) {
                    if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                        if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH) != null) {
                            currentDressImageWidth = Integer.parseInt(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH));
                        }
                    }
                }

                // Сверяем ширину изображения для текущей одежды с минимальным значением ширины
                if (minDressImageWidth == 0 || currentDressImageWidth < minDressImageWidth) {
                    minDressImageWidth = currentDressImageWidth;
                }
            }

            //--------------------------------------------------------------------------------------
            // Определяем суммарную высоту всех изображений
            int sumImageHeight = 0;

            // В цикле перебираем все изображения
            for (int indexImage = 0; indexImage < this.getArrayDressImage().size(); indexImage++) {
                if (this.getArrayDressImage().get(indexImage) != null) {
                    // Определяем коэффициент, во сколько раз необходимо уменьшить текущее изображение
                    Double k = 1.0;

                    if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                        if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH) != null && minDressImageWidth > 0) {
                            k = Double.valueOf(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH)) / minDressImageWidth;
                        }
                    }

                    // Определяем конечную высоту текущего изображения с учетом коэффициента масштабирования
                    Double targetImageHeight = 0.0;

                    if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                        if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_HEIGHT) != null) {
                            targetImageHeight = Double.valueOf(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_HEIGHT)) / k;
                        }
                    }

                    // Прибавляем конечную высоту текущего изображения к суммарной высоте всех изображений
                    sumImageHeight += targetImageHeight.intValue();
                }
            }

            //--------------------------------------------------------------------------------------
            // Создаем холст, на котором будем рисовать все необходимые изображения
            bitmapOverlay = Bitmap.createBitmap(minDressImageWidth, sumImageHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapOverlay);
            canvas.drawColor(Color.WHITE);

            //--------------------------------------------------------------------------------------
            // Переменная, хранящая смещение текущего изображения по высоте относительно предыдущего
            // изображения
            int offsetHeight = 0;

            //--------------------------------------------------------------------------------------
            // Логическая переменная, указывающая на то, было ли отображено изображение для
            // категории одежды "Низ"
            Boolean isDressTypeLegExists = false;

            //----------------------------------------------------------------------------------
            // Выполняем слияние всех изображений в одно изображение (перебираем все изображения в цикле)
            // При этом учитываем, что массив ArrayDressImage сформирован В ОБРАТНОМ ПОРЯДКЕ ПО ТИПАМ ОДЕЖДЫ
            for (int indexImage = 0; indexImage < this.getArrayDressImage().size(); indexImage++) {
                if (this.getArrayDressImage().get(indexImage) != null) {
                    try {
                        // Определяем коэффициент, во сколько раз необходимо уменьшить текущее изображение
                        Double k = 1.0;

                        if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                            if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH) != null && minDressImageWidth > 0) {
                                k = Double.valueOf(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH)) / minDressImageWidth;
                            }
                        }

                        // Определяем конечные ширину и высоту текущего изображения с учетом коэффициента масштабирования
                        Double targetImageWidth = 0.0;

                        if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_WIDTH)) {
                            if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH) != null) {
                                targetImageWidth = Double.valueOf(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_WIDTH)) / k;
                            }
                        }

                        Double targetImageHeight = 0.0;

                        if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE_HEIGHT)) {
                            if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_HEIGHT) != null) {
                                targetImageHeight = Double.valueOf(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE_HEIGHT)) / k;
                            }
                        }

                        // Задаем смещение для текущей одежды
                        offsetHeight += targetImageHeight;

                        // Если тип текущей одежды - "Низ", то делаем об этом соответствующую пометку
                        if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_TYPE)) {
                            if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_TYPE) != null) {
                                // Если текущий тип одежды - "Низ"
                                if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_TYPE).equals(GlobalFlags.TAG_DRESS_LEG)) {
                                    isDressTypeLegExists = true;
                                }
                                // Иначе, если текущий тип одежды - "Верх"
                                else if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_TYPE).equals(GlobalFlags.TAG_DRESS_BODY)) {
                                    // Если одежда из типа "Низ" была отображена на виртуальном манекене
                                    // то убираем из смещения для текущей одежды 20dp
                                    if(isDressTypeLegExists.equals(true)) {
                                        offsetHeight -= GlobalFlags.OFFSET_BETWEEN_DRESS_BODY_AND_LEG * GlobalFlags.DpToPx;
                                    }
                                }
                            }
                        }

                        // Скачиваем непорседственно текущее изображение
                        Bitmap bitmapImage = null;

                        if (this.getArrayDressImage().get(indexImage).containsKey(GlobalFlags.TAG_IMAGE)) {
                            if (this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE) != null) {
                                bitmapImage = Glide
                                        .with(this.getContext())
                                        .asBitmap()
                                        .load(this.getArrayDressImage().get(indexImage).get(GlobalFlags.TAG_IMAGE))
                                        .into(targetImageWidth.intValue(), targetImageHeight.intValue())
                                        .get();
                            }
                        }

                        // Рисуем скачанное изображение
                        if(bitmapImage != null) {
                            canvas.drawBitmap(bitmapImage, 0, bitmapOverlay.getHeight() - offsetHeight, null);
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        FunctionsLog.logPrint("Error Async Task Load Bitmaps From URL: " + exception.toString());
                    }
                }
            }
        }

        return bitmapOverlay;
    }

    //==============================================================================================
    // После завершения фоновой задачи
    protected void onPostExecute(Bitmap bitmapOverlay) {
        super.onPostExecute(bitmapOverlay);

        //------------------------------------------------------------------------------------------
        // Получаем ссылки на необходимые элементы
        ImageView imageViewCollectionShare = null;
        ProgressBar progressBarCollectionShare = null;
        TextView textViewLoadImageCollectionShare = null;

        if(this.getRelativeLayoutCollectionShare() != null) {
            imageViewCollectionShare = (ImageView) this.getRelativeLayoutCollectionShare().findViewById(R.id.imageViewCollectionShare);
            progressBarCollectionShare = (ProgressBar) this.getRelativeLayoutCollectionShare().findViewById(R.id.progressBarCollectionShare);
            textViewLoadImageCollectionShare = (TextView) this.getRelativeLayoutCollectionShare().findViewById(R.id.textViewLoadImageCollectionShare);
        }

        final ImageView imageViewCollectionShareFinal = imageViewCollectionShare;

        // Скрываем элемент ProgressBar
        if(progressBarCollectionShare != null) {
            progressBarCollectionShare.setVisibility(View.GONE);
        }

        //------------------------------------------------------------------------------------------
        // Если итоговое изображение было успешно сформировано, то отображаем его
        if(bitmapOverlay != null) {
            // Отображаем непосредственно изображение
            if(imageViewCollectionShareFinal != null) {
                imageViewCollectionShareFinal.setImageBitmap(bitmapOverlay);
                imageViewCollectionShareFinal.setVisibility(View.VISIBLE);

                //----------------------------------------------------------------------------------
                // Устанавливаем обработчик клика по изображению
                imageViewCollectionShareFinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(AsyncTaskLoadBitmapsFromURL.this.getContext() != null) {
                            try {
                                Intent intentFullScreenImageShare = new Intent(AsyncTaskLoadBitmapsFromURL.this.getContext(), ActivityFullScreenImageShare.class);

                                // Считываем передаваемое изображение
                                Uri imageUriCollectionShare = BitmapDecode.getLocalBitmapUri(imageViewCollectionShareFinal);

                                if(imageUriCollectionShare != null) {
                                    intentFullScreenImageShare.putExtra(GlobalFlags.TAG_IMAGE, imageUriCollectionShare.toString());
                                }

                                AsyncTaskLoadBitmapsFromURL.this.getContext().startActivity(intentFullScreenImageShare);
                            }
                            catch (Exception exception) {
                                exception.printStackTrace();
                                FunctionsLog.logPrint("Error Start Activity Full Screen Image Share: " + exception.toString());
                            }
                        }
                    }
                });
            }

            // Скрываем элемент TextView
            if(textViewLoadImageCollectionShare != null) {
                textViewLoadImageCollectionShare.setVisibility(View.GONE);
            }
        }
        // Иначе, если не удалось сформировать итоговое изображение
        else {
            // Скрываем непосредственно изображение
            if(imageViewCollectionShareFinal != null) {
                imageViewCollectionShareFinal.setVisibility(View.GONE);
            }

            // Отображаем элемент TextView
            if(textViewLoadImageCollectionShare != null) {
                textViewLoadImageCollectionShare.setTextColor(ContextCompat.getColor(ApplicationContextProvider.getContext(), R.color.color_red));
                textViewLoadImageCollectionShare.setText(ApplicationContextProvider.getContext().getString(R.string.string_dress_image_error));
                textViewLoadImageCollectionShare.setVisibility(View.VISIBLE);
            }
        }
    }
}
