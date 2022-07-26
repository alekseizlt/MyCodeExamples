package ru.alexprogs.dressroom.httppostrequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ru.alexprogs.dressroom.lib.FunctionsLog;

// Класс для формирования запросов на удаленные сервера и обработки ответов, полученных от этих
// удаленных серверов
public class HttpGetPostRequest {

    //==============================================================================================
    // Метод для отправки запросов на удаленные сервера и получения ответов, полученных от этих
    // удаленных серверов с использованием метода GET
    // Передаваемые параметры
    // requestURL - адрес сервера, на который необходимо отослать запрос
    public static String executeGetRequest(String requestURL) {
        URL url;                    // объект URL, представляющий собой адрес сервера, на который необходимо отослать запрос
        String response = "";       // строка запроса на удаленный сервер

        try {
            // Формируем объект URL, представляющий собой адрес сервера, на который необходимо отослать запрос
            url = new URL(requestURL);

            // Устанавливаем соединение с удаленным сервером
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            // Ожидаем ответ от удаленного сервера
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
            }
            else {
                response = "";
                FunctionsLog.logPrint("Error (executeGetRequest): " + String.valueOf(responseCode));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (executeGetRequest): " + exception.toString());
        }

        return response;
    }

    //==============================================================================================
    // Метод для отправки запросов на удаленные сервера и получения ответов, полученных от этих
    // удаленных серверов с использованием метода POST
    // Передаваемые параметры
    // requestURL - адрес сервера, на который необходимо отослать запрос
    // postDataParams - ассоциативный массив параметров, которые необходимо на удаленный сервер
    //                  при помощи метода POST
    public static String executePostRequest(String requestURL, HashMap<String, String> postDataParams) {
        URL url;                    // объект URL, представляющий собой адрес сервера, на который необходимо отослать запрос
        String response = "";       // строка запроса на удаленный сервер

        try {
            // Формируем объект URL, представляющий собой адрес сервера, на который необходимо отослать запрос
            url = new URL(requestURL);

            // Устанавливаем соединение с удаленным сервером
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            // Пересылаем на удаленный сервер методом POST необходимые параметры
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(HttpGetPostRequest.encodePostDataString(postDataParams));

            writer.flush();
            writer.close();
            outputStream.close();

            // Ожидаем ответ от удаленного сервера
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
            }
            else {
                response = "";
                FunctionsLog.logPrint("Error (executePostRequest): " + String.valueOf(responseCode));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (executePostRequest): " + exception.toString());
        }

        return response;
    }

    //==============================================================================================
    // Метод для преобразования массива параметров, пересылаемых на удаленный сервер методом POST, в строку
    private static String encodePostDataString(HashMap<String, String> postDataParams) {
        StringBuilder result = new StringBuilder();     // строка-результат выполнения функции
        boolean isPostDataParamFirst = true;            // логическая переменная, определяющая является ли текущий параметр первым

        try {
            for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                if (isPostDataParamFirst) {
                    isPostDataParamFirst = false;
                }
                else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");

                if(entry.getValue() == null) {
                    result.append("null");
                }
                else {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (encodePostDataString): " + exception.toString());
        }

        return result.toString();
    }
}
