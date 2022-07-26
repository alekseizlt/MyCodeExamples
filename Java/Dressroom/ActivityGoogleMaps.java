package ru.alexprogs.dressroom;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.adapter.AdapterGoogleMapsInfoWindowShop;
import ru.alexprogs.dressroom.asynctasks.AsyncTaskMapCalculateRoute;
import ru.alexprogs.dressroom.db.mysql.MySQLGetShopInfoForMap;
import ru.alexprogs.dressroom.db.mysql.MySQLGetShopListIdForDress;
import ru.alexprogs.dressroom.geolocation.LocationUser;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.lib.FunctionsArray;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.FunctionsScreen;
import ru.alexprogs.dressroom.mapinfowindowcustom.MapWrapperLayout;

public class ActivityGoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;                                               // объект GoogleMap
    private MapWrapperLayout mMapWrapperLayout;                                 // элемент-оболочка над картой GoogleMap
    private int mActionOnLoadMap;                                               // тип действия, выполняемого при загрузке карты
    private ArrayList<Integer> mArrayShopListId;                                // массив, хранящий список id магазинов, которые необходимо отобразить на карте в виде маркеров
    private int mArrayShopIdCurrentPosition;                                    // порядковый номер текущего магазина в массиве mArrayShopListId
    private HashMap<String, HashMap<String, String>> mArrayMarkerShop;          // массив, содержащий соответствие id маркера и информации о соответствующем магазине
    private int mCurrentDressId;                                                // id текущей одежды

    //==============================================================================================
    // Метод, вызываемый при создании текущего Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_maps);

        //------------------------------------------------------------------------------------------
        // Считываем значение переменной, определяющей тип выполняемого действия
        // Данная переменная передана данному Activity в качестве параметра при прмрщи метода putExtra()
        try {
            Intent intentShopMap = getIntent();

            if(intentShopMap != null) {
                this.setActionOnLoadMap(intentShopMap.getIntExtra(GlobalFlags.TAG_ACTION_ONLOAD_MAP, GlobalFlags.ACTION_ONLOAD_MAP_SUBTYPE_SHOW_ONE_SHOP));

                // Если тип действия при отображении карты - отображение одного магазина одежды на карте в виде маркера
                if (this.getActionOnLoadMap() == GlobalFlags.ACTION_ONLOAD_MAP_SUBTYPE_SHOW_ONE_SHOP) {
                    // Формируем массив, хранящий список id магазинов, которые необходимо отобразить на карте в виде маркеров
                    // В данном случае он будет состоять из одного элемента
                    ArrayList<Integer> arrayShopListId = new ArrayList<>();
                    arrayShopListId.add(intentShopMap.getIntExtra(GlobalFlags.TAG_SHOP_ID, 0));

                    // Сохраняем полученный массив в виде глобального массива
                    this.setArrayShopListId(arrayShopListId);
                }
                // Иначе, считаем, что тип действия при отображении карты - отображение всех магазинов
                // для текущей одежды на карте в виде маркеров
                else {
                    this.setCurrentDressId(intentShopMap.getIntExtra(GlobalFlags.TAG_DRESS_ID, 0));
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Create Activity Google Map: " + exception.toString());
        }

        //------------------------------------------------------------------------------------------
        // Задаем, что изначально порядковый номер магазина в массиве mArrayShopListId,
        // информацию о котором необходимо отобразить на карте в виде маркера, равен 0
        this.setArrayShopIdCurrentPosition(0);

        //------------------------------------------------------------------------------------------
        // Инициализируем кнопку "Закрыть"
        Button buttonCloseMap = (Button) findViewById(R.id.buttonCloseMap);

        // Устанавливаем обработчик щелчка по кнопке "Закрыть"
        if (buttonCloseMap != null) {
            buttonCloseMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityGoogleMaps.this.finish();
                }
            });
        }

        //------------------------------------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    //==============================================================================================
    // Метод для считывания объекта GoogleMap
    public GoogleMap getGoogleMap() {
        return this.mGoogleMap;
    }

    //==============================================================================================
    // Метод для задания объекта GoogleMap
    private void setGoogleMap(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
    }

    //==============================================================================================
    // Метод для считывания ссылки на элемент-оболочку над картой GoogleMap
    public MapWrapperLayout getMapWrapperLayout() {
        return this.mMapWrapperLayout;
    }

    //==============================================================================================
    // Метод для задания ссылки на элемент-оболочку над картой GoogleMap
    private void setMapWrapperLayout(MapWrapperLayout mapWrapperLayout) {
        this.mMapWrapperLayout = mapWrapperLayout;
    }

    //==============================================================================================
    // Метод для считывания значения переменной, определяющей тип действия, выполняемого при загрузке карты
    private int getActionOnLoadMap() {
        return this.mActionOnLoadMap;
    }

    //==============================================================================================
    // Метод для задания значения переменной, определяющей тип действия, выполняемого при загрузке карты
    private void setActionOnLoadMap(int actionOnLoadMap) {
        this.mActionOnLoadMap = actionOnLoadMap;
    }

    //==============================================================================================
    // Метод для считывания массива, хранящего список id магазинов для текущей одежды
    private ArrayList<Integer> getArrayShopListId() {
        return this.mArrayShopListId;
    }

    //==============================================================================================
    // Метод для задания массива, хранящего список id магазинов для текущей одежды
    public void setArrayShopListId(ArrayList<Integer> arrayShopListId) {
        this.mArrayShopListId = arrayShopListId;
    }

    //==============================================================================================
    // Метод для считывания порядкового номера текущего магазина одежды
    private int getArrayShopIdCurrentPosition() {
        return this.mArrayShopIdCurrentPosition;
    }

    //==============================================================================================
    // Метод для задания порядкового номера текущего магазина одежды
    private void setArrayShopIdCurrentPosition(int arrayShopIdCurrentPosition) {
        this.mArrayShopIdCurrentPosition = arrayShopIdCurrentPosition;
    }

    //==============================================================================================
    // Метод для считывания массива, содержащего соответствие id маркера и информации о
    // соответствующем магазине
    public HashMap<String, HashMap<String, String>> getArrayMarkerShop() {
        return this.mArrayMarkerShop;
    }

    //==============================================================================================
    // Метод для задания массива, содержащего соответствие id маркера и информации о
    // соответствующем магазине
    public void setArrayMarkerShop(HashMap<String, HashMap<String, String>> arrayMarkerShop) {
        this.mArrayMarkerShop = arrayMarkerShop;
    }

    //==============================================================================================
    // Метод для считывания значения id текущей одежды
    private int getCurrentDressId() {
        return this.mCurrentDressId;
    }

    //==============================================================================================
    // Метод для задания значения id текущей одежды
    private void setCurrentDressId(int currentDressId) {
        this.mCurrentDressId = currentDressId;
    }

    //==============================================================================================
    // Метод для загрузки информации о необходимом магазине для его последующего отображения на карте
    // Передаваемые параметры
    public void startLoadCurrentShopInfoForMap() {
        if(this.getArrayShopListId() != null) {
            // Логическая переменная, определяющая отображать или нет модальное окно,
            // отображающее процесс загрузки данных из БД
            Boolean isShowProgressDialogLoadShopInfoForMap = true;

            // Если общее количество магазинов больше 1
            if(this.getArrayShopListId().size() > 1) {
                isShowProgressDialogLoadShopInfoForMap = false;
            }

            //--------------------------------------------------------------------------------------
            // Если передан корректный порядковый номер магазина
            if (this.getArrayShopIdCurrentPosition() >= 0 && this.getArrayShopIdCurrentPosition() < this.getArrayShopListId().size()) {
                // Запускаем непорсдественно процесс считывания информации о текущем магазине
                MySQLGetShopInfoForMap mySQLGetShopInfoForMap = new MySQLGetShopInfoForMap(
                        this,
                        this.getArrayShopListId().get(this.getArrayShopIdCurrentPosition()),
                        isShowProgressDialogLoadShopInfoForMap
                );

                mySQLGetShopInfoForMap.startLoadShopInfoForMap();

                // Увеличиваем на +1 порядковый номер текущего магазина
                this.setArrayShopIdCurrentPosition(this.getArrayShopIdCurrentPosition() + 1);
            }
            // Иначе, если достигнут конец списка id магазинов
            else if(this.getArrayShopIdCurrentPosition() >= this.getArrayShopListId().size()) {
                // Считываем местоположение текущего пользователя
                LocationUser locationUser = GlobalFlags.getLocationUser();

                Double locationUserLatitude;
                Double locationUserLongitude;

                if(locationUser.getCurrentLocation() != null) {
                    locationUserLatitude = locationUser.getCurrentLocation().getLatitude();
                    locationUserLongitude = locationUser.getCurrentLocation().getLongitude();
                }
                else {
                    locationUserLatitude = GlobalFlags.LOCATION_USER_LATITUDE;
                    locationUserLongitude = GlobalFlags.LOCATION_USER_LONGITUDE;

                    // Выводим сообщение о том, что не удалось определить текущее положение пользователя
                    Toast toastLocationUserIsNull = Toast.makeText(this, R.string.string_user_location_is_null, Toast.LENGTH_LONG);
                    toastLocationUserIsNull.setGravity(Gravity.CENTER, 0, 0);
                    toastLocationUserIsNull.show();
                }

                //----------------------------------------------------------------------------------
                // Добавляем маркер на карту для местоположения текущего пользователя
                final LatLng currentUserLocation = new LatLng(locationUserLatitude, locationUserLongitude);

                Marker markerCurrentUser = this.getGoogleMap().addMarker(new MarkerOptions().position(currentUserLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user))
                        .title(getResources().getString(R.string.string_i_am_here)));

                this.getGoogleMap().moveCamera(CameraUpdateFactory.newLatLng(currentUserLocation));

                //----------------------------------------------------------------------------------
                if(this.getArrayMarkerShop() != null) {
                    // Формируем массивы, содержащий значения широты о долготы соответственно
                    // для каждого из магазинов, отображаемых в виде маркеров на карте
                    ArrayList<Double> arrayShopLatitude = null;
                    ArrayList<Double> arrayShopLongitude = null;

                    Collection<String> arrayMarkerShopCollectionKey = this.getArrayMarkerShop().keySet();

                    for(String arrayMarkerShopKey : arrayMarkerShopCollectionKey) {
                        if(this.getArrayMarkerShop().get(arrayMarkerShopKey) != null) {
                            // Извлекаем значение широты для текущего магазина
                            if(this.getArrayMarkerShop().get(arrayMarkerShopKey).containsKey(GlobalFlags.TAG_LATITUDE)) {
                                if(this.getArrayMarkerShop().get(arrayMarkerShopKey).get(GlobalFlags.TAG_LATITUDE) != null) {
                                    if(arrayShopLatitude == null) {
                                        arrayShopLatitude = new ArrayList<>();
                                    }

                                    arrayShopLatitude.add(
                                            Double.valueOf(this.getArrayMarkerShop().get(arrayMarkerShopKey).get(GlobalFlags.TAG_LATITUDE))
                                    );
                                }
                            }

                            // Извлекаем значение долготы для текущего магазина
                            if(this.getArrayMarkerShop().get(arrayMarkerShopKey).containsKey(GlobalFlags.TAG_LONGITUDE)) {
                                if(this.getArrayMarkerShop().get(arrayMarkerShopKey).get(GlobalFlags.TAG_LONGITUDE) != null) {
                                    if(arrayShopLongitude == null) {
                                        arrayShopLongitude = new ArrayList<>();
                                    }

                                    arrayShopLongitude.add(
                                            Double.valueOf(this.getArrayMarkerShop().get(arrayMarkerShopKey).get(GlobalFlags.TAG_LONGITUDE))
                                    );
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Добавляем широту и долготу местоположения пользователя в массивы
                    // arrayShopLatitude и arrayShopLongitude соответственно
                    if(arrayShopLatitude == null) {
                        arrayShopLatitude = new ArrayList<>();
                    }

                    arrayShopLatitude.add(locationUserLatitude);

                    if(arrayShopLongitude == null) {
                        arrayShopLongitude = new ArrayList<>();
                    }

                    arrayShopLongitude.add(locationUserLongitude);

                    final ArrayList<Double> arrayShopLatitudeFinal = arrayShopLatitude;
                    final ArrayList<Double> arrayShopLongitudeFinal = arrayShopLongitude;

                    //------------------------------------------------------------------------------
                    // Определяем минимальные и максимальные значения широты и долготы
                    // из всего перечня магазинов, отображаемых на карте в виде маркеров
                    Double minLatitude = FunctionsArray.getMinValueFromArray(arrayShopLatitudeFinal);
                    Double minLongitude = FunctionsArray.getMinValueFromArray(arrayShopLongitudeFinal);
                    Double maxLatitude = FunctionsArray.getMaxValueFromArray(arrayShopLatitudeFinal);
                    Double maxLongitude = FunctionsArray.getMaxValueFromArray(arrayShopLongitudeFinal);

                    final LatLng minLatLng = new LatLng(minLatitude, minLongitude);
                    final LatLng maxLatLng = new LatLng(maxLatitude, maxLongitude);

                    //------------------------------------------------------------------------------
                    // Включаем анимацию камеры
                    final LatLngBounds bounds = new LatLngBounds.Builder().include(minLatLng).include(maxLatLng).build();

                    this.getGoogleMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            if(ActivityGoogleMaps.this.getGoogleMap() != null) {
                                ActivityGoogleMaps.this.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                                // Если общее количество магазинов =1, то прокладываем маршрут до данного магазина
                                if (ActivityGoogleMaps.this.getArrayShopListId().size() == 1 && arrayShopLatitudeFinal.size() == 2 && arrayShopLongitudeFinal.size() == 2) {
                                    // Определяем координаты данного магазина
                                    Double currentShopLatitude = arrayShopLatitudeFinal.get(0);
                                    Double currentShopLongitude = arrayShopLongitudeFinal.get(0);

                                    LatLng currentShopLocation = new LatLng(currentShopLatitude, currentShopLongitude);

                                    // Прокладываем маршрут между двумя точками
                                    AsyncTaskMapCalculateRoute asyncTaskMapCalculateRoute = new AsyncTaskMapCalculateRoute(
                                            ActivityGoogleMaps.this,
                                            currentUserLocation,
                                            currentShopLocation
                                    );

                                    asyncTaskMapCalculateRoute.execute();
                                }
                            }
                        }
                    });

                    //------------------------------------------------------------------------------
                    // Добавляем в массив mArrayMarkerShop сведения о текущем пользователе
                    HashMap<String, String> arrayCurrentUserInfo = new HashMap<>();
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER, GlobalFlags.TAG_YES);

                    this.getArrayMarkerShop().put(markerCurrentUser.getId(), arrayCurrentUserInfo);

                    // Формируем адаптер для отображения информационного окна при клике по
                    // соответствующему маркеру на карте
                    this.getGoogleMap().setInfoWindowAdapter(
                            new AdapterGoogleMapsInfoWindowShop(
                                    this,
                                    this.getArrayMarkerShop(),
                                    this.getMapWrapperLayout()
                            )
                    );
                }
            }
        }
    }

    //==============================================================================================
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Настраиваем карты Google Map
        this.setGoogleMap(googleMap);
        this.getGoogleMap().getUiSettings().setAllGesturesEnabled(true);
        this.getGoogleMap().getUiSettings().setCompassEnabled(true);
        this.getGoogleMap().getUiSettings().setIndoorLevelPickerEnabled(true);
        this.getGoogleMap().getUiSettings().setZoomControlsEnabled(true);
        this.getGoogleMap().getUiSettings().setMapToolbarEnabled(true);
        this.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
        this.getGoogleMap().getUiSettings().setRotateGesturesEnabled(true);
        this.getGoogleMap().getUiSettings().setScrollGesturesEnabled(true);
        this.getGoogleMap().getUiSettings().setTiltGesturesEnabled(true);
        this.getGoogleMap().getUiSettings().setZoomGesturesEnabled(true);

        //------------------------------------------------------------------------------------------
        // Получаем ссылку на элемент-оболочку над картой GoogleMap
        this.setMapWrapperLayout((MapWrapperLayout) findViewById(R.id.mapWrapperLayout));

        if(this.getMapWrapperLayout() != null) {
            this.getMapWrapperLayout().init(googleMap, FunctionsScreen.getPixelsFromDp(this, 39 + 20));
        }

        //------------------------------------------------------------------------------------------
        // В зависимости от типа действия при создании карты выполняем соответствующие действия
        switch (this.getActionOnLoadMap()) {
            case GlobalFlags.ACTION_ONLOAD_MAP_SUBTYPE_SHOW_ONE_SHOP:
                // Считываем информацию о текущем магазине одежды
                ActivityGoogleMaps.this.startLoadCurrentShopInfoForMap();
                break;

            case GlobalFlags.ACTION_ONLOAD_MAP_SUBTYPE_SHOW_SOME_SHOP:
                // Выводим сообщение о загрузке информации о магазинах для текущей одежды
                Toast toastLoadShopInfo = Toast.makeText(this, getString(R.string.string_proccess_load_shop), Toast.LENGTH_SHORT);
                toastLoadShopInfo.setGravity(Gravity.CENTER, 0, 0);
                toastLoadShopInfo.show();

                // Загружаем список магазинов для текущей одежды
                MySQLGetShopListIdForDress mySQLGetShopListIdForDress = new MySQLGetShopListIdForDress(this, this.getCurrentDressId());
                mySQLGetShopListIdForDress.startLoadShopListIdForDress();

                break;
        }
    }
}
