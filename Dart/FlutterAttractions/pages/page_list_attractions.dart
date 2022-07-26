import 'dart:developer' as dev;

import 'package:flutter/material.dart';

import '../classes/global_variables.dart';
import '../classes/attraction_item.dart';
import '../classes/http_get_post_request.dart';

class PageListAttractions extends StatefulWidget {
  const PageListAttractions({Key? key}) : super(key: key);

  //============================================================================
  // StatefulWidget must return class, which extended from State
  @override
  State<PageListAttractions> createState() => _PageListAttractionsState();
}

class _PageListAttractionsState extends State<PageListAttractions> {
  List<dynamic>? _listAttractionItem;
  bool _isRemoteDataLoaded = false;
  bool _isRemoteDataLoadedSuccess = false;

  //============================================================================
  @override
  void initState() {
    super.initState();

    // Get date list from server
    _getDataFromRemoteServer();
  }

  //============================================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text(GlobalVariables.titleList)),
      body: _createBodyWidget()
    );
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

    return ListView.builder(
        itemCount: _listAttractionItem == null ? 0 : _listAttractionItem!.length,
        itemBuilder: (BuildContext context, int index) {
          return AttractionItem.fromJson(_listAttractionItem![index] as Map<String, dynamic>);
        }
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
          GlobalVariables.requestActionDB: GlobalVariables.requestGetListAttractions
        },
        (Map<String, dynamic> paramJsonData) {
          setState(() {
            // Set, that was received response from server
            _isRemoteDataLoaded = true;

            // Check, that request was finished successfully
            bool isResponseSuccess = false;

            if(paramJsonData.containsKey(GlobalVariables.tagListAttractionsSuccess) && paramJsonData[GlobalVariables.tagListAttractionsSuccess] != null) {
              int isResponseSuccessInt = paramJsonData[GlobalVariables.tagListAttractionsSuccess] as int;

              if(isResponseSuccessInt == 1) {
                isResponseSuccess = true;
              }
            }

            if(isResponseSuccess) {
              if (paramJsonData.containsKey(GlobalVariables.tagListAttractions) && paramJsonData[GlobalVariables.tagListAttractions] != null) {
                _listAttractionItem = paramJsonData[GlobalVariables.tagListAttractions];

                // Set, that was received data from server successfully
                _isRemoteDataLoadedSuccess = true;
              }
            }
          });
        }
    );
  }
}
