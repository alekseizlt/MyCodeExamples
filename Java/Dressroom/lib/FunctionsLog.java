package ru.alexprogs.dressroom.lib;

import android.util.Log;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;

/**
* Класс, содержащий различные функции для работы с логом
*/
public class FunctionsLog {

    //==============================================================================================
    // Метод для вывода в лог информации о занимаемой приложением памяти
    public static void logMemory() {
        FunctionsLog.logPrint(String.format("Total memory = %s KB", (int) (Runtime.getRuntime().totalMemory() / 1024)));
    }

    //==============================================================================================
    // Метод для вывода сообщения, переданного в качестве параметра, в лог
    // Передаваемые параметры
    // message - текст сообщения, которое необходимо вывести в лог
    public static void logPrint(String message) {
        Log.d(GlobalFlags.TAG_LOG, message);
    }
}
