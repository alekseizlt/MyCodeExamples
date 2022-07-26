import 'dart:developer' as dev;

import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:url_launcher/url_launcher.dart';

import '../classes/global_variables.dart';
import '../classes/http_get_post_request.dart';

import 'page_map.dart';

class PageDetailedInfo extends StatefulWidget {
  late final String _itemId;

  PageDetailedInfo({Key? key, required String itemId}) : super(key: key) {
    _itemId = itemId;
  }

  //============================================================================
  // StatefulWidget must return class, which extended from State
  @override
  State<PageDetailedInfo> createState() => _PagePageDetailedInfo();
}

class _PagePageDetailedInfo extends State<PageDetailedInfo> {
  Map<String, dynamic>? _mapDetailedInfo;

  String? _imageUrl;
  String _title = "";
  String _description = "";
  String? _url;

  bool _isRemoteDataLoaded = false;
  bool _isRemoteDataLoadedSuccess = false;

  //============================================================================
  @override
  void initState() {
    super.initState();

    // Get date from server
    _getDataFromRemoteServer();
  }

  //============================================================================
  // Method to fill info about this item
  void _fillItemInfoFields() {
    if(_mapDetailedInfo != null) {
      if (_mapDetailedInfo!.containsKey(GlobalVariables.tagDetailPicture)) {
        _imageUrl = _mapDetailedInfo![GlobalVariables.tagDetailPicture];
      }

      if (_mapDetailedInfo!.containsKey(GlobalVariables.tagName)) {
        _title = _mapDetailedInfo![GlobalVariables.tagName];
      }

      if (_mapDetailedInfo!.containsKey(GlobalVariables.tagDetailText)) {
        _description = _mapDetailedInfo![GlobalVariables.tagDetailText];
      }

      if (_mapDetailedInfo!.containsKey(GlobalVariables.tagURL)) {
        _url = _mapDetailedInfo![GlobalVariables.tagURL];
      }
    }
  }

  //============================================================================
  // функция build, строит иерархию виджетов
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text(GlobalVariables.titleDetailedInfo)),
        body: _createBodyWidget()
    );
  }

  //============================================================================
  // Method to return widget for Image
  Widget _createWidgetImage() {
    if (_imageUrl != null && _imageUrl!.isNotEmpty) {
      return CachedNetworkImage(
          imageUrl: _imageUrl!,
          placeholder: (context, url) => const CircularProgressIndicator(),
          errorWidget: (context, url, error) => const Icon(Icons.error),
          fit: BoxFit.contain
      );
    }

    return const SizedBox(height: 20.0);
  }

  //============================================================================
  // Method to create widget content
  Widget _createBodyWidget() {
    if(!_isRemoteDataLoaded) {
      return Center(
          child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: const <Widget>[
                CircularProgressIndicator(
                    color: Colors.blue
                ),
                SizedBox(height: 20.0),
                Text(GlobalVariables.messageLoadingData)
              ]
          )
      );
    }

    if(!_isRemoteDataLoadedSuccess) {
      return Center(
          child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                const Text(GlobalVariables.messageErrorGetDataFromRemoteServer, textAlign: TextAlign.center),
                const SizedBox(height: 20.0),
                ElevatedButton(
                    onPressed: () {
                      _getDataFromRemoteServer();
                    },
                    child: const Text("ПОВТОРИТЬ", textAlign: TextAlign.center)
                )
              ]
          )
      );
    }

    return Padding(
        padding: const EdgeInsets.all(16.0),
        child: SingleChildScrollView(
            child: Column(
                children: <Widget>[
                  _createWidgetImage(),
                  const SizedBox(height: 10.0),
                  Table(
                      border: const TableBorder(
                          horizontalInside: BorderSide(width: 0, color: Colors.white),
                          verticalInside: BorderSide(width: 0, color: Colors.white)
                      ),
                      columnWidths: const <int, TableColumnWidth> {
                        0: FixedColumnWidth(80),
                        1: FlexColumnWidth()
                      },
                      defaultVerticalAlignment: TableCellVerticalAlignment.middle,
                      children: <TableRow>[
                        TableRow(
                            children: <TableCell>[
                              const TableCell(
                                  child: Text("Название:")
                              ),
                              TableCell(
                                  child: Text(_title, style: const TextStyle(fontSize: 15.0), overflow: TextOverflow.ellipsis)
                              )
                            ]
                        ),

                        const TableRow(
                            children: <TableCell>[
                              TableCell(
                                  child: SizedBox(height: 10.0)
                              ),
                              TableCell(
                                  child: SizedBox(height: 10.0)
                              )
                            ]
                        ),

                        TableRow(
                            children: <TableCell>[
                              const TableCell(
                                  child: Text("Описание:")
                              ),
                              TableCell(
                                  child: Html(data: _description)
                              )
                            ]
                        )
                      ]
                  ),
                  const SizedBox(height: 10.0),
                  Row(
                    children: <Widget>[
                      const Expanded(
                          flex: 1,
                          child: SizedBox(width: 1.0)
                      ),

                      Expanded(
                          flex: 3,
                          child: ElevatedButton(
                              onPressed: () {
                                _openUrl(context);
                              },
                              child: const Text("Открыть сайт", textAlign: TextAlign.center)
                          )
                      ),

                      const Expanded(
                          flex: 2,
                          child: SizedBox(width: 1.0)
                      ),

                      Expanded(
                          flex: 3,
                          child: ElevatedButton(
                              onPressed: () {
                                Navigator.pushNamed(context, "/${GlobalVariables.routeMapping}/${widget._itemId}");
                              },
                              child: const Text("На карте", textAlign: TextAlign.center)
                          )
                      ),

                      const Expanded(
                          flex: 1,
                          child: SizedBox(width: 1.0)
                      )
                    ],
                  )
                ]
            )
        )
    );
  }

  //============================================================================
  // Method to get data from remote server
  Future<void> _getDataFromRemoteServer() async {
    // Set some variables
    _isRemoteDataLoaded = false;
    _isRemoteDataLoadedSuccess = false;

    // Get data from remote server
    await HttpGetPostRequest.sendRequestPostBody(
        GlobalVariables.requestURL,
        <String, String> {
          GlobalVariables.requestActionDB: GlobalVariables.requestGetDetailedInfo,
          GlobalVariables.tagItemId: widget._itemId.toString()
        },
        (Map<String, dynamic> paramJsonData) {
          setState(() {
            // Set, that was received response from server
            _isRemoteDataLoaded = true;

            // Check, that request was finished successfully
            bool isResponseSuccess = false;

            if(paramJsonData.containsKey(GlobalVariables.tagDetailedInfoSuccess) && paramJsonData[GlobalVariables.tagDetailedInfoSuccess] != null) {
              int isResponseSuccessInt = paramJsonData[GlobalVariables.tagDetailedInfoSuccess] as int;

              if(isResponseSuccessInt == 1) {
                isResponseSuccess = true;
              }
            }

            if(isResponseSuccess) {
              if (paramJsonData.containsKey(GlobalVariables.tagDetailedInfo) && paramJsonData[GlobalVariables.tagDetailedInfo] != null) {
                _mapDetailedInfo = paramJsonData[GlobalVariables.tagDetailedInfo];

                // Set, that was received data from server successfully
                _isRemoteDataLoadedSuccess = true;

                // Fill info fields about this item
                _fillItemInfoFields();
              }
            }
          });
        }
    );
  }

  //============================================================================
  // Method to open URL
  Future<void> _openUrl(BuildContext paramContext) async {
    if(_url == null) {
      _showToast(paramContext, "Ошибка! Не удалось открыть сайт!");
    }

    if (!await launchUrl(Uri.parse(_url!), mode: LaunchMode.externalApplication)) {
      _showToast(paramContext, "Ошибка! Не удалось открыть сайт!");
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
}
