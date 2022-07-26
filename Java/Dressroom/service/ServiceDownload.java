package ru.alexprogs.dressroom.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.alexprogs.dressroom.lib.FunctionsLog;

// Служба для обновления данных, хранящихся в локальной БД
public class ServiceDownload extends Service {

    // Свойства данного класса
    private static Context mContext;                // контекст
    private ExecutorService mExecutorService;
    private static final String TAG_TIME_LAST_SERVICE_EXECUTE = "time_last_service_execute";

    //==============================================================================================
    // Конструктор
    public ServiceDownload() {
    }

    //==============================================================================================
    // Метод для считывания контекста
    private static Context getContext() {
        return ServiceDownload.mContext;
    }

    //==============================================================================================
    // Метод для задания контекста
    public static void setContext(Context context) {
        ServiceDownload.mContext = context;
    }

    //==============================================================================================
    // Метод для считывания объекта ExecutorService
    private ExecutorService getExecutorService() {
        return this.mExecutorService;
    }

    //==============================================================================================
    // Метод для задания объекта ExecutorService
    private void setExecutorService(ExecutorService executorService) {
        this.mExecutorService = executorService;
    }

    //==============================================================================================
    // Метод, вызываемый при создании текущей службы
    @Override
    public void onCreate() {
        super.onCreate();

        //------------------------------------------------------------------------------------------
        // Задаем объект ExecutorService
        this.setExecutorService(Executors.newFixedThreadPool(1));
    }

    //==============================================================================================
    // Метод, вызываемый при запуске текущей службы
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Запускаем метод обновления данных в локальной БД
        ClassDownloadData classDownloadData = new ClassDownloadData(startId);

        if(this.getExecutorService() != null) {
            this.getExecutorService().execute(classDownloadData);
        }

        // Восстанавливаем данную служюу в случае ее внезапного уничтожения и снова выполняем
        // незавершенные операции
        return START_REDELIVER_INTENT;
    }

    //==============================================================================================
    // Метод, вызываемый при уничтожении текущей службы
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //==============================================================================================
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //==============================================================================================
    // Класс, предназначенный для считывания данных с удаленной БД в отдельном потоке
    class ClassDownloadData implements Runnable {

        // Свойства данного класса
        private int mStartId;

        //------------------------------------------------------------------------------------------
        // Конструктор
        // Передаваемые параметры
        // startId - счетчик запуска текущей службы
        public ClassDownloadData(int startId) {
            this.setStartId(startId);
        }

        //------------------------------------------------------------------------------------------
        // Метод для считывания значения переменной mStartId
        private int getStartId() {
            return this.mStartId;
        }

        //------------------------------------------------------------------------------------------
        // Метод для задания значения переменной mStartId
        private void setStartId(int startId) {
            this.mStartId = startId;
        }

        //------------------------------------------------------------------------------------------
        // Главный метод Run
        public void run() {
            // Делаем задержку в 2 секунды, чтобы отбросить 2-ой запрос, который поступает к текущему
            // широковещательному слушателю примерно через 100 мс после первого запроса
            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
                FunctionsLog.logPrint("Error Time Sleep (BroadcastReceiver): " + exception);
            }

            //--------------------------------------------------------------------------------------
            // Считываем дату последнего запуска текущей службы в миллисекундах
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ServiceDownload.getContext());
            long timeLastServiceExecute = sharedPreferences.getLong(ServiceDownload.TAG_TIME_LAST_SERVICE_EXECUTE, 0);

            // Определяем текущую дату в миллисекундах
            long timeCurrent = System.currentTimeMillis();
            FunctionsLog.logPrint("timeLastServiceExecute:" + timeLastServiceExecute);
            FunctionsLog.logPrint("timeCurrent:" + timeCurrent);
            // Если прошло более 1 дня, то запускаем операцию обновления данных, представленных в локальной БД
            if(timeCurrent - timeLastServiceExecute > 10000) {


                //----------------------------------------------------------------------------------
                // Сохраняем текущее время запуска данной службы
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putLong(ServiceDownload.TAG_TIME_LAST_SERVICE_EXECUTE, timeCurrent);
                sharedPreferencesEditor.apply();
            }

            //--------------------------------------------------------------------------------------
            // После окончания выполнения всех операций завершаем выполнение текущей службы
            this.stop();
        }

        //------------------------------------------------------------------------------------------
        // Метод для остановки выполнения службы
        private void stop() {
            // Делаем пометку, что следующий вызов широковещательного слушателя для запуска
            // текущей службы является первым
            BroadcastReceiverService.setIsFirstConnect(true);

            // Устанавливаем работу текущей службы
            stopSelf(this.getStartId());
        }
    }
}
