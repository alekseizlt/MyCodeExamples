import 'dart:async';
import 'dart:convert';
import 'dart:developer' as dev;

import 'package:flutter_attractions/classes/global_variables.dart';
import 'package:http/http.dart' as http;

class HttpGetPostRequest {

  //============================================================================
  // Method for sending Get request to Web Server
  static Future sendRequestGet(String paramUrl) async {
    try {
      final http.Response response = await http.get(Uri.parse(paramUrl));

      // If the server returned a response code 200, then parse the JSON
      if(response.statusCode == GlobalVariables.httpCodeSuccess) {
        dev.log("Success in function HttpGetPostRequest.sendRequestGet(): Server return code ${response.statusCode}");
      }
      else {
        // If the server did not return a 201 CREATED response, then throw an exception
        dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestGet(): Server return code ${response.statusCode}");
      }
    } catch(error) {
      dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestGet(): $error");
      throw Exception("Error in function HttpGetPostRequest.sendRequestGet(): $error");
    }
  }//_sendRequestGet

  //============================================================================
  static Future sendRequestPostBody(String paramUrl, Map<String, String> paramBody, Function paramCallback) async {
    try {
      final http.Response response = await http.post(
          Uri.parse(paramUrl),
          body: paramBody
      );

      // If the server returned a response code 200, then parse the JSON
      if(response.statusCode == GlobalVariables.httpCodeSuccess) {
        // Call callback
        paramCallback(jsonDecode(response.body));
      }
      else {
        dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestPostBody(): Server return code ${response.statusCode}");
      }
    } catch (error) {
      dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestPostBody(): $error");
      throw Exception("Error in function HttpGetPostRequest.sendRequestPostBody(): $error");
    }
  }//_sendRequestPost

  //============================================================================
  static Future sendRequestPostBodyHeaders(String paramUrl, Map<String, String> paramBody) async {
    try {
      final http.Response response = await http.post(
        Uri.parse(paramUrl),
        headers: <String, String> {
          'Content-Type': 'application/json; charset=UTF-8'
        },
        body: jsonEncode(paramBody)
      );

      // If the server returned a response code 200, then parse the JSON
      if(response.statusCode == GlobalVariables.httpCodeSuccess) {
        dev.log("Success in function HttpGetPostRequest.sendRequestPostBodyHeaders(): Server return code ${response.statusCode}");
      }
      else {
        dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestPostBodyHeaders(): Server return code ${response.statusCode}");
      }
    } catch (error) {
      dev.log("ERROR: Error in function HttpGetPostRequest.sendRequestPostBodyHeaders(): $error");
      throw Exception("Error in function HttpGetPostRequest.sendRequestPostBodyHeaders(): $error");
    }
  }
}
