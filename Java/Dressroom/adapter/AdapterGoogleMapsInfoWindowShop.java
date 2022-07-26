package ru.alexprogs.dressroom.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import ru.alexprogs.dressroom.ApplicationContextProvider;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.mapinfowindowcustom.MapWrapperLayout;
import ru.alexprogs.dressroom.mapinfowindowcustom.OnTouchListenerInfoWindowElement;

// Адаптер для информационных окон на карте для магазинов одежды
public class AdapterGoogleMapsInfoWindowShop implements GoogleMap.InfoWindowAdapter {

    // Свойства данного класса
    private Context mContext;                                                   // контекст
    private HashMap<String, HashMap<String, String>> mArrayMarkerShop;          // массив, содержащий соответствие id текущего маркера и информации о соответствующем магазине
    private MapWrapperLayout mMapWrapperLayout;                                 // элемент-оболочка над картой GoogleMap

    //==============================================================================================
    // Конструктор
    // Передаваемые параметры
    // context - контекст
    // arrayMarkerShop - массив, содержащий соответствие id текущего маркера и информации о соответствующем магазина
    // mapWrapperLayout - элемент-оболочка над картой GoogleMap
    public AdapterGoogleMapsInfoWindowShop(Context context, HashMap<String, HashMap<String, String>> arrayMarkerShop, MapWrapperLayout mapWrapperLayout) {
        this.setContext(context);
        this.setArrayMarkerShop(arrayMarkerShop);
        this.setMapWrapperLayout(mapWrapperLayout);
    }

    //==============================================================================================
    // Метод для считывания объекта Context
    private Context getContext() {
        return this.mContext;
    }

    //==============================================================================================
    // Метод для задания объекта Context
    private void setContext(Context сontext) {
        this.mContext = сontext;
    }

    //==============================================================================================
    // Метод для считывания многомерного массива, содержащего соответствие id текущего маркера и
    // информации о соответствующем магазине
    private HashMap<String, HashMap<String, String>> getArrayMarkerShop() {
        return this.mArrayMarkerShop;
    }

    //==============================================================================================
    // Метод для задания многомерного массива, содержащего соответствие id текущего маркера и
    // информации о соответствующем магазине
    private void setArrayMarkerShop(HashMap<String, HashMap<String, String>> arrayMarkerShop) {
        this.mArrayMarkerShop = arrayMarkerShop;
    }

    //==============================================================================================
    // Метод для считывания ссылки на элемент-оболочку над картой GoogleMap
    private MapWrapperLayout getMapWrapperLayout() {
        return this.mMapWrapperLayout;
    }

    //==============================================================================================
    // Метод для задания ссылки на элемент-оболочку над картой GoogleMap
    private void setMapWrapperLayout(MapWrapperLayout mapWrapperLayout) {
        this.mMapWrapperLayout = mapWrapperLayout;
    }

    //==============================================================================================
    // Метод для получения параметров для необходимого элемента из списка
    // Передаваемые параметры
    // id - id маркера, для которого необходимо считать параметры
    public HashMap<String, String> getItem(String position) {
        if(this.getArrayMarkerShop() == null) {
            return null;
        }
        else {
            return this.getArrayMarkerShop().get(position);
        }
    }

    //==============================================================================================
    @Override
    public View getInfoContents(Marker marker) {
        // Формируем внешний вид для информационного окна
        View viewInfoWindowShop = null;

        // Считываем информацию о магазине для текущего маркера
        HashMap<String, String> currentShopInfo = this.getItem(marker.getId());

        if(currentShopInfo != null) {
            // Если установлен флаг, что текущий маркер относится к пользователю
            if(currentShopInfo.containsKey(GlobalFlags.TAG_USER)) {
                if(currentShopInfo.get(GlobalFlags.TAG_USER) != null) {
                    viewInfoWindowShop = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.google_maps_info_window_user, null, false);

                    if(viewInfoWindowShop != null) {
                        TextView textViewUserText = (TextView) viewInfoWindowShop.findViewById(R.id.textViewUserText);

                        if (textViewUserText != null) {
                            // Устанавливаем шрифт
                            if (GlobalFlags.getAppTypeface() != null) {
                                textViewUserText.setTypeface(GlobalFlags.getAppTypeface());
                            }

                            // Выводим надпись
                            textViewUserText.setText(R.string.string_i_am_here);
                        }
                    }
                }
            }
            // Иначе, считаем, что информационное окно относится к магазину одежды
            else {
                viewInfoWindowShop = ApplicationContextProvider.getLayoutInflater().inflate(R.layout.google_maps_info_window_shop, null, false);

                if (viewInfoWindowShop != null) {
                    // Заполняем название текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_TITLE)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_TITLE) != null) {
                            TextView textViewInfoWindowShopTitle = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopTitle);

                            if (textViewInfoWindowShopTitle != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopTitle.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно название магазина
                                textViewInfoWindowShopTitle.setText(currentShopInfo.get(GlobalFlags.TAG_TITLE).trim());
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Заполняем адрес текущего магазина
                    TextView textViewInfoWindowShopAddress = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopAddress);

                    if (textViewInfoWindowShopAddress != null) {
                        // Устанавливаем шрифт
                        if (GlobalFlags.getAppTypeface() != null) {
                            textViewInfoWindowShopAddress.setTypeface(GlobalFlags.getAppTypeface());
                        }

                        //--------------------------------------------------------------------------
                        // Формируем адрес текущего магазина
                        String currentShopAddress = "";

                        // Добавляем почтовый индекс
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_POSTCODE)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_POSTCODE) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_POSTCODE) + ", ";
                            }
                        }

                        // Добавляем название страны
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_COUNTRY)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_COUNTRY) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_COUNTRY) + ", ";
                            }
                        }

                        // Добавляем название региона/области
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_REGION)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_REGION) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_REGION) + ", ";
                            }
                        }

                        // Добавляем название города
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_CITY)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_CITY) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_CITY) + ", ";
                            }
                        }

                        // Добавляем название улицы
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_STREET)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_STREET) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_STREET) + ", ";
                            }
                        }

                        // Добавляем номер дома
                        if (currentShopInfo.containsKey(GlobalFlags.TAG_BUILDING)) {
                            if (currentShopInfo.get(GlobalFlags.TAG_BUILDING) != null) {
                                currentShopAddress += currentShopInfo.get(GlobalFlags.TAG_BUILDING) + ", ";
                            }
                        }

                        // Удаляем из строки currentShopAddress последнее вхождение подстроки ", "
                        currentShopAddress = currentShopAddress.substring(0, currentShopAddress.lastIndexOf(", "));

                        // Выводим непосредственно адрес текущего магазина
                        textViewInfoWindowShopAddress.setText(currentShopAddress);
                    }

                    //------------------------------------------------------------------------------
                    // Меняем шрифт для метки "Телефон:"
                    TextView textViewInfoWindowShopTelephoneLabel = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopTelephoneLabel);

                    if (textViewInfoWindowShopTelephoneLabel != null) {
                        if (GlobalFlags.getAppTypeface() != null) {
                            textViewInfoWindowShopTelephoneLabel.setTypeface(GlobalFlags.getAppTypeface());
                        }
                    }

                    // Заполняем номер телефона текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_TELEPHONE)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_TELEPHONE) != null) {
                            TextView textViewInfoWindowShopTelephoneValue = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopTelephoneValue);

                            if (textViewInfoWindowShopTelephoneValue != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopTelephoneValue.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно номер телефона текущего магазина
                                textViewInfoWindowShopTelephoneValue.setText(currentShopInfo.get(GlobalFlags.TAG_TELEPHONE).trim());
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Меняем шрифт для метки "Мобильный телефон:"
                    TextView textViewInfoWindowShopMobileLabel = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopMobileLabel);

                    if (textViewInfoWindowShopMobileLabel != null) {
                        if (GlobalFlags.getAppTypeface() != null) {
                            textViewInfoWindowShopMobileLabel.setTypeface(GlobalFlags.getAppTypeface());
                        }
                    }

                    // Заполняем номер мобильного телефона текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_MOBILE)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_MOBILE) != null) {
                            TextView textViewInfoWindowShopMobileValue = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopMobileValue);

                            if (textViewInfoWindowShopMobileValue != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopMobileValue.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно номер мобильного телефона текущего магазина
                                textViewInfoWindowShopMobileValue.setText(currentShopInfo.get(GlobalFlags.TAG_MOBILE).trim());
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Меняем шрифт для надписи "Факс:"
                    TextView textViewInfoWindowShopFaxLabel = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopFaxLabel);

                    if (textViewInfoWindowShopFaxLabel != null) {
                        if (GlobalFlags.getAppTypeface() != null) {
                            textViewInfoWindowShopFaxLabel.setTypeface(GlobalFlags.getAppTypeface());
                        }
                    }

                    // Заполняем номер факса текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_FAX)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_FAX) != null) {
                            TextView textViewInfoWindowShopFaxValue = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopFaxValue);

                            if (textViewInfoWindowShopFaxValue != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopFaxValue.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно номер факса текущего магазина
                                textViewInfoWindowShopFaxValue.setText(currentShopInfo.get(GlobalFlags.TAG_FAX).trim());
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Меняем шрифт для метки "Сайт:"
                    TextView textViewInfoWindowShopWebPageLabel = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopWebPageLabel);

                    if (textViewInfoWindowShopWebPageLabel != null) {
                        if (GlobalFlags.getAppTypeface() != null) {
                            textViewInfoWindowShopWebPageLabel.setTypeface(GlobalFlags.getAppTypeface());
                        }
                    }

                    // Заполняем адрес web-сайта текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_WEBPAGE)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_WEBPAGE) != null) {
                            TextView textViewInfoWindowShopWebPageValue = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopWebPageValue);

                            if (textViewInfoWindowShopWebPageValue != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopWebPageValue.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно адрес web-сайта
                                String shopWebPage = currentShopInfo.get(GlobalFlags.TAG_WEBPAGE).trim();

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    textViewInfoWindowShopWebPageValue.setText(Html.fromHtml("<u>" + shopWebPage + "</u>", Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    textViewInfoWindowShopWebPageValue.setText(Html.fromHtml("<u>" + shopWebPage + "</u>"));
                                }

                                // Заполняем данными элемент-оболочку над картой GoogleMap
                                if (this.getMapWrapperLayout() != null) {
                                    this.getMapWrapperLayout().setMarkerWithInfoWindow(marker, viewInfoWindowShop);
                                }

                                // Устанавливаем обработчик касания для адреса web-сайта текущего магазина
                                textViewInfoWindowShopWebPageValue.setOnTouchListener(new OnTouchListenerInfoWindowElement(
                                        this.getContext(),
                                        textViewInfoWindowShopWebPageValue,
                                        marker,
                                        shopWebPage
                                ) {
                                    @Override
                                    protected void onClickConfirmed(View v, Marker marker) {

                                    }
                                });
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Меняем шрифт для надписи "Email"
                    TextView textViewInfoWindowShopEmailLabel = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopEmailLabel);

                    if (textViewInfoWindowShopEmailLabel != null && GlobalFlags.getAppTypeface() != null) {
                        textViewInfoWindowShopEmailLabel.setTypeface(GlobalFlags.getAppTypeface());
                    }

                    // Заполняем адрес электронной почты текущего магазина
                    if (currentShopInfo.containsKey(GlobalFlags.TAG_EMAIL)) {
                        if (currentShopInfo.get(GlobalFlags.TAG_EMAIL) != null) {
                            TextView textViewInfoWindowShopEmail = (TextView) viewInfoWindowShop.findViewById(R.id.textViewInfoWindowShopEmailValue);

                            if (textViewInfoWindowShopEmail != null) {
                                // Устанавливаем шрифт
                                if (GlobalFlags.getAppTypeface() != null) {
                                    textViewInfoWindowShopEmail.setTypeface(GlobalFlags.getAppTypeface());
                                }

                                // Выводим непосредственно адрес электронной почты
                                textViewInfoWindowShopEmail.setText(currentShopInfo.get(GlobalFlags.TAG_EMAIL).trim());
                            }
                        }
                    }
                }
            }
        }

        return viewInfoWindowShop;
    }

    //==============================================================================================
    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }
}
