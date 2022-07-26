import 'dart:developer' as dev;

import 'package:flutter/material.dart';
import 'package:flutter_attractions/classes/global_variables.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter_html/flutter_html.dart';

class AttractionItem extends StatelessWidget {
  late final int _itemId;
  late final String _imageUrl;
  late final String _title;
  late final String _description;

  //============================================================================
  AttractionItem({Key? key,
                  required int paramItemId,
                  required String paramTitle,
                  required String paramDescription,
                  String paramImageUrl = ''}) : super(key: key) {
    _itemId = paramItemId;
    _title = paramTitle;
    _description = paramDescription;
    _imageUrl = paramImageUrl;
  }

  //============================================================================
  static AttractionItem fromJson(Map<String, dynamic> paramJson) {
    int localItemId = 0;
    String? localImageUrl;
    String localTitle = "Title";
    String localDescription = "Description";

    if(paramJson.containsKey(GlobalVariables.tagItemId)) {
      localItemId = int.parse(paramJson[GlobalVariables.tagItemId]);
    }

    if(paramJson.containsKey(GlobalVariables.tagPreviewPicture)) {
      localImageUrl = paramJson[GlobalVariables.tagPreviewPicture];
    }

    if(paramJson.containsKey(GlobalVariables.tagName)) {
      localTitle = paramJson[GlobalVariables.tagName];
    }

    if(paramJson.containsKey(GlobalVariables.tagPreviewText)) {
      localDescription = paramJson[GlobalVariables.tagPreviewText];
    }

    if(localImageUrl == null) {
      return AttractionItem(
          paramItemId: localItemId,
          paramTitle: localTitle,
          paramDescription: localDescription
      );
    }

    return AttractionItem(
        paramItemId: localItemId,
        paramTitle: localTitle,
        paramDescription: localDescription,
        paramImageUrl: localImageUrl
    );
  }

  //============================================================================
  @override
  Widget build(BuildContext context) {
    if (_imageUrl.isNotEmpty) {
      return InkWell(
          onTap: () {
            Navigator.pushNamed(context, "/${GlobalVariables.routeDetailing}/$_itemId");
          },
          child: Container(
          color: Colors.lightBlueAccent,
          height: 120.0,
          margin: const EdgeInsets.only(top: 5.0, bottom: 5.0),
          child: Row(
            children: [
              CachedNetworkImage(
                imageUrl: _imageUrl,
                placeholder: (context, url) => const CircularProgressIndicator(),
                errorWidget: (context, url, error) => const Icon(Icons.error),
                width: 100.0,
                height: 100.0,
                fit: BoxFit.contain
              ),
              Expanded(
                child: Container(
                  padding: const EdgeInsets.all(5.0),
                  child: Column(
                    children: <Widget>[
                      Text(_title, style: const TextStyle(fontSize: 20.0), overflow: TextOverflow.ellipsis),
                      Expanded(child:  Html (data: _description))
                    ]
                  )
                )
              )
            ]
          )
        )
      );
    }

    return InkWell(
        onTap: () {
          Navigator.pushNamed(context, "/detailing/$_itemId");
        },
        child: Container(
          color: Colors.black12,
          height: 100.0,
          margin: const EdgeInsets.only(top: 5.0, bottom: 5.0),
          child: Row(
            children: [
              Expanded(
                child: Container(
                  padding: const EdgeInsets.all(5.0),
                  child: Column(
                    children: [
                      Text(_title, style: const TextStyle(fontSize: 20.0), overflow: TextOverflow.ellipsis),
                      Expanded(child:  Html (data: _description))
                    ]
                  )
                )
              )
            ]
          )
        )
    );
  }
}
