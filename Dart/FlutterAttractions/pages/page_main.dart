import 'package:flutter/material.dart';
import 'package:flutter_attractions/classes/global_variables.dart';
import 'package:flutter_attractions/resources/colors.dart';
import 'page_account.dart';
import 'page_main_list.dart';
import 'page_map.dart';

// StatefulWidget имеет состояние, с которым
// позже мы будем работать через функцию
// setState(VoidCallback fn);
// обратите внимание setState принимает другую функцию
class PageMain extends StatefulWidget {
  const PageMain({Key? key, required this.title}) : super(key: key);

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".
  final String title;

  // StatefulWidget должен возвращать класс, которые наследуется от State
  @override
  State<PageMain> createState() => _PageMainState();
}

class _PageMainState extends State<PageMain> {
  int _bottomNavigationBarSelectedIndex = 0;
  PageController pageController = PageController();

  //============================================================================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageView(
        controller: pageController,
        onPageChanged: (int paramIndex) {
          setState(() {
            _bottomNavigationBarSelectedIndex = paramIndex;
          });
        },
        physics: const NeverScrollableScrollPhysics(),
        children: <Widget> [
          const PageMainList(),
          PageMap(itemId: 0),
          const PageAccount()
        ]
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem> [
          BottomNavigationBarItem(icon: Icon(Icons.list), label: GlobalVariables.titleList),
          BottomNavigationBarItem(icon: Icon(Icons.map), label: GlobalVariables.titleMap)  ,
          BottomNavigationBarItem(icon: Icon(Icons.account_box_rounded), label: GlobalVariables.titleAccount)
        ],
        currentIndex: _bottomNavigationBarSelectedIndex,
        selectedItemColor: Colors.blue,
        unselectedItemColor: primaryBlack,
        onTap: onBottomNavigationBarTap
      ),
    );
  }

  //============================================================================
  // Метод, вызываемый при клике по одной из кнопок на нижней панели
  void onBottomNavigationBarTap(int paramIndex) {
    setState(() {
      _bottomNavigationBarSelectedIndex = paramIndex;
    });

    pageController.animateToPage(paramIndex,
      duration: const Duration(milliseconds: 500),
      curve: Curves.linear
    );
  }
}