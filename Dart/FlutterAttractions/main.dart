import 'package:flutter/material.dart';
import 'pages/page_main.dart';
import 'resources/colors.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Attractions',
      debugShowCheckedModeBanner: false,    // remove banner Debug
      theme: ThemeData(
        primarySwatch: primaryBlack,
      ),
      home: const PageMain(title: 'Attractions'),
    );
  }
}

