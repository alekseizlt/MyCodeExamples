import 'package:flutter/material.dart';
import 'package:flutter_attractions/classes/global_variables.dart';

import 'page_list_attractions.dart';
import 'page_detailed_info.dart';
import 'page_map.dart';

class PageMainList extends StatefulWidget {
  const PageMainList({Key? key}) : super(key: key);

  @override
  State<PageMainList> createState() => _PageMainListState();
}

class _PageMainListState extends State<PageMainList> {

  //============================================================================
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: '/',
      routes: {
        '/': (BuildContext context) => const PageListAttractions(),
        "/${GlobalVariables.routeDetailing}": (BuildContext context) => PageDetailedInfo(itemId: '0'),
        "/${GlobalVariables.routeMapping}": (BuildContext context) => PageMap(itemId: 0)
      },
      onGenerateRoute: (RouteSettings routeSettings) {
        if(routeSettings.name != null) {
          List<String> path = routeSettings.name!.split('/');

          if(path[1] == GlobalVariables.routeDetailing) {
            return MaterialPageRoute(
                builder: (context) => PageDetailedInfo(itemId: path[2]),
                settings: routeSettings
            );
          }
          else if(path[1] == GlobalVariables.routeMapping) {
            return MaterialPageRoute(
                builder: (context) => PageMap(itemId: int.parse(path[2])),
                settings: routeSettings
            );
          }
        }
      }
    );
  }
}
