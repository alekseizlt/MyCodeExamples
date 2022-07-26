import 'dart:developer' as dev;
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_osm_plugin/flutter_osm_plugin.dart';

import 'package:flutter_attractions/classes/global_variables.dart';
import 'package:flutter_attractions/classes/http_get_post_request.dart';

enum StatusMap {loading, loaded, error}

class PageMap extends StatefulWidget {
  late final int _itemId;

  PageMap({Key? key, required int itemId}) : super(key: key) {
    _itemId = itemId;
  }

  @override
  State<PageMap> createState() => _PageMapState();
}

class _PageMapState extends State<PageMap> with OSMMixinObserver {
  late MapController mapController;
  StatusMap _mapStatus = StatusMap.loading;

  double? _itemLatitude;
  double? _itemLongitude;

  bool _isRemoteDataLoaded = false;
  bool _isRemoteDataLoadedSuccess = false;

  //============================================================================
  @override
  void initState() {
    super.initState();
    mapController = MapController(
      initMapWithUserPosition: true,
    );

    _mapStatus = StatusMap.loading;
  }

  //============================================================================
  @override
  Future<void> mapIsReady(bool isReady) async {
    if (isReady) {
      dev.log("Map is ready!");
    }
  }

  //============================================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text(GlobalVariables.titleMap)),
        body: OSMFlutter(
          controller: mapController,
          trackMyPosition: false,
          showContributorBadgeForOSM: true,
          showDefaultInfoWindow: false,
          showZoomController: true,
          mapIsLoading: Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: const <Widget>[
                CircularProgressIndicator(
                  color: Colors.blue
                ),
                SizedBox(height: 20.0),
                Text("Карта загружается..."),
              ],
            ),
          ),
          onMapIsReady: onMapIsReady,
          initZoom: 15,
          stepZoom: 1.0,
          markerOption: MarkerOption(
            defaultMarker: const MarkerIcon(
              icon: Icon(
                Icons.person_pin_circle,
                color: Colors.blue,
                size: 56,
              ),
            ),
          ),
          roadConfiguration: RoadConfiguration(
            startIcon: const MarkerIcon(
              icon: Icon(
                Icons.person,
                size: 64,
                color: Colors.brown,
              ),
            ),
            roadColor: Colors.yellowAccent,
          ),
          userLocationMarker: UserLocationMaker(
            personMarker: const MarkerIcon(
              icon: Icon(
                Icons.location_history_rounded,
                color: Colors.red,
                size: 48,
              ),
            ),
            directionArrowMarker: const MarkerIcon(
              icon: Icon(
                Icons.double_arrow,
                size: 48,
              ),
            ),
          )
        )
      );
  }

  //============================================================================
  @override
  void dispose() {
    mapController.dispose();
    super.dispose();
  }

  //============================================================================
  Future<void> onMapIsReady(bool isReady) async {
    if (isReady) {
      await mapIsInitialized();
    }
  }

  //============================================================================
  // Method to show Toast Notification
  void _showToast(BuildContext paramContext, String paramMessage) {
    final scaffold = ScaffoldMessenger.of(paramContext);
    scaffold.showSnackBar(
      SnackBar(
        content: Text(paramMessage),
        action: SnackBarAction(label: "Закрыть", onPressed: scaffold.hideCurrentSnackBar),
      ),
    );
  }

  //============================================================================
  Future<void> mapIsInitialized() async {
    // Get user position
    GeoPoint geoPointUser = await mapController.myLocation();
    dev.log("User location: ${geoPointUser.latitude}; ${geoPointUser.longitude}");



    // If must be shown only one shop on the map
    if (widget._itemId > 0) {
      // Load location of the current item from remote server
      await _getDataFromRemoteServer(geoPointUser);
    }
    else {
      // Add marker for current user on Map
      await mapController.addMarker(
          geoPointUser,
          markerIcon: const MarkerIcon(
            icon: Icon(
              Icons.location_history_rounded,
              color: Colors.green,
              size: 56,
            ),
          )
      );

      await mapController.goToLocation(geoPointUser);
    }
  }

  //============================================================================
  // Method to add marker for specific shop on the Map
  Future<void> _addMarkerToMapForItem(GeoPoint paramGeoPointUser) async {
    if (_itemLatitude != null && _itemLongitude != null) {
      GeoPoint geoPointShop = GeoPoint(latitude: _itemLatitude!, longitude: _itemLongitude!);

      // Draw road from user to shop
      RoadInfo roadInfo = await mapController.drawRoad(
        paramGeoPointUser,
        geoPointShop,
        roadType: RoadType.car,
        //intersectPoint: [paramGeoPointUser, geoPointShop],
        roadOption: const RoadOption(
          roadWidth: 10,
          roadColor: Colors.blue,
          showMarkerOfPOI: false,
          zoomInto: true,
        )
      );

      // Add marker for specific shop on Map
      await mapController.addMarker(
          geoPointShop,
          markerIcon: const MarkerIcon(
            icon: Icon(
              Icons.shop,
              color: Colors.lightBlue,
              size: 64,
            ),
          )
      );

      // Add marker for current user on Map
      await mapController.addMarker(
          paramGeoPointUser,
          markerIcon: const MarkerIcon(
            icon: Icon(
              Icons.location_history_rounded,
              color: Colors.green,
              size: 64,
            ),
          )
      );
    }
  }

  //============================================================================
  // Method to get data from remote server
  Future<void> _getDataFromRemoteServer(GeoPoint paramGeoPointUser) async {
    // Set some variables
    _isRemoteDataLoaded = false;
    _isRemoteDataLoadedSuccess = false;

    // Get data from remote server
    await HttpGetPostRequest.sendRequestPostBody(
        GlobalVariables.requestURL,
        <String, String> {
          GlobalVariables.requestActionDB: GlobalVariables.requestGetItemLocation,
          GlobalVariables.tagItemId: widget._itemId.toString()
        },
        (Map<String, dynamic> paramJsonData) {
          setState(() {
            // Set, that was received response from server
            _isRemoteDataLoaded = true;

            // Check, that request was finished successfully
            bool isResponseSuccess = false;

            if(paramJsonData.containsKey(GlobalVariables.tagItemLocationSuccess) && paramJsonData[GlobalVariables.tagItemLocationSuccess] != null) {
              int isResponseSuccessInt = paramJsonData[GlobalVariables.tagItemLocationSuccess] as int;

              if(isResponseSuccessInt == 1) {
                isResponseSuccess = true;
              }
            }

            if(isResponseSuccess) {
              if (paramJsonData.containsKey(GlobalVariables.tagItemLocation) && paramJsonData[GlobalVariables.tagItemLocation] != null) {
                _itemLatitude = paramJsonData[GlobalVariables.tagItemLocation][GlobalVariables.tagLatitude];
                _itemLongitude = paramJsonData[GlobalVariables.tagItemLocation][GlobalVariables.tagLongitude];

                // Set, that was received data from server successfully
                _isRemoteDataLoadedSuccess = true;

                // Add marker to Map for current item
                _addMarkerToMapForItem(paramGeoPointUser);
              }
            }
          });
        }
    );
  }
}
