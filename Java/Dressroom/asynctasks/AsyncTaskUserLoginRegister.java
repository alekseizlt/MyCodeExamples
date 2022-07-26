package ru.alexprogs.dressroom.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.alexprogs.dressroom.ActivityLoginRegister;
import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.UserDetails;
import ru.alexprogs.dressroom.httppostrequest.HttpGetPostRequest;
import ru.alexprogs.dressroom.lib.Functions;
import ru.alexprogs.dressroom.lib.FunctionsConnection;
import ru.alexprogs.dressroom.lib.FunctionsLog;
import ru.alexprogs.dressroom.lib.FunctionsString;

// Фоновый Async Task для авторизации/регистрации пользователя
public class AsyncTaskUserLoginRegister extends AsyncTask<String, Void, Integer> {

    // Свойства данного класса
    private Context mContext;                                       // контекст
    private ProgressDialog mProgressDialogUserLoginRegister;        // ссылка на модальное окно, отображающее процесс загрузки данных с сервера БД
    private int mAction;                                            // переменная, определяющая тип действия (авторизация или регистрация пользователя)

    // Параметры, введенные пользователем
    private String mUserLogin;              // логин пользователя
    private String mUserLoginType;          // тип учетной записи (внутренняя, вконтакте, facebook, twitter или google)
    private String mUserPassword;           // пароль пользователя
    private String mUserName;               // имя пользователя
    private String mUserSurname;            // фамилия пользователя
    private String mUserMail;               // адрес электронной почты пользователя
    private String mUserAvatarURL;          // ссылка на изображение (аватар) для текущего пользователя
    private String mUserForWho;             // для кого текущий пользователь предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
    private String mUserProfileURL;         // ссылка на профиль текущего профиля в социальной сети

    //==============================================================================================
    // Конструктор для случая авторизации пользователя через внутреннюю учетную запись
    // Передаваемые параметры
    // context - контекст
    // userLogin - логин пользователя
    // userPassword - пароль пользователя
    public AsyncTaskUserLoginRegister(Context context, String userLogin, String userPassword) {
        this.setContext(context);                                                           // задаем контекст
        this.setAction(GlobalFlags.ACTION_USER_LOGIN);                                      // задаем тип действия "Авторизация"
        this.setUserLogin(userLogin);                                                       // задаем логин пользователя
        this.setUserPassword(userPassword);                                                 // задаем пароль пользователя
        this.setUserLoginType(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL);      // задаем, что тип учетной записи - внутренняя
    }

    //==============================================================================================
    // Конструктор для случая авторизации пользователя через социальную сеть
    // Передаваемые параметры
    // context - контекст
    // socialNetworkType - тип социальной сети, через которую прошел авторизацию текущий пользователь
    // socialNetworkUserId - id пользователя в социальной сети, через которую прошел авторизацию текущий пользователь
    // userName - имя текущего пользователя
    // userSurname - фамилия текущего пользователя
    // userMail - адрес электронной почты для текущего пользователя
    // userAvatarURL - ссылка на изображение-аватар из социальной сети для текущего пользователя
    // userProfileURL - ссылка на профиль из социальной сети для текущего пользователя
    public AsyncTaskUserLoginRegister(Context context, String socialNetworkType, String socialNetworkUserId,
                                      String userName, String userSurname, String userMail, String userAvatarURL, String userProfileURL) {
        this.setContext(context);                                                           // задаем контекст
        this.setAction(GlobalFlags.ACTION_USER_LOGIN_SOCIAL);                               // задаем тип действия "Авторизация" через социальную сеть
        this.setUserLogin(socialNetworkUserId);                                             // задаем логин пользователя
        this.setUserLoginType(socialNetworkType);                                           // задаем тип учетной записи
        this.setUserName(userName);                                                         // задаем имя пользователя
        this.setUserSurname(userSurname);                                                   // задаем фамилию пользователя
        this.setUserMail(userMail);                                                         // задаем адрес электронной почты пользователя
        this.setUserAvatarURL(userAvatarURL);                                               // ссылка на изображение-аватар из социальной сети для текущего пользователя
        this.setUserForWho(GlobalFlags.TAG_DRESS_MAN);                                      // задаем, что текущий пользователь предпочитает просматривать по умолчанию вещи для мужчин
        this.setUserProfileURL(userProfileURL);                                             // ссылка на профиль из социальной сети для текущего пользователя
    }

    //==============================================================================================
    // Конструктор для случая регистрации пользователя
    // Передаваемые параметры
    // context - контекст
    // userLogin - логин пользователя
    // userPassword - пароль пользователя
    // userName - имя текущего пользователя
    // userSurname - фамилия текущего пользователя
    // userMail - адрес электронной почты для текущего пользователя
    // userForWho - для кого текущий пользователь предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
    public AsyncTaskUserLoginRegister(Context context, String userLogin, String userPassword, String userName, String userSurname, String userMail, String userForWho) {
        this.setContext(context);                                                           // задаем контекст
        this.setAction(GlobalFlags.ACTION_USER_REGISTRATION);                               // задаем тип действия "Регистрация"
        this.setUserLogin(userLogin);                                                       // задаем логин пользователя
        this.setUserLoginType(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL);      // задаем, что тип учетной записи - внутренняя
        this.setUserPassword(userPassword);                                                 // задаем пароль пользователя
        this.setUserName(userName);                                                         // задаем имя пользователя
        this.setUserSurname(userSurname);                                                   // задаем фамилию пользователя
        this.setUserMail(userMail);                                                         // задаем адрес электронной почты пользователя
        this.setUserForWho(userForWho);                                                     // для кого текущий пользователь предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
    }

    //==============================================================================================
    // Метод для считывания контекста
    private Context getContext() {
        return this.mContext;
    }

    //==============================================================================================
    // Метод для задания контекста
    private void setContext(Context context) {
        this.mContext = context;
    }

    //==============================================================================================
    // Метод для считывания ссылки на модальное окно, отображающее процесс загрузки данных с сервера БД
    private ProgressDialog getProgressDialogUserLoginRegister() {
        return this.mProgressDialogUserLoginRegister;
    }

    //==============================================================================================
    // Метод для задания ссылки на модальное окно, отображающее процесс загрузки данных с сервера БД
    private void setProgressDialogUserLoginRegister(ProgressDialog progressDialogUserLoginRegister) {
        this.mProgressDialogUserLoginRegister = progressDialogUserLoginRegister;
    }

    //==============================================================================================
    // Метод для считывания переменной, определяющей тип действия (авторизация или регистрация пользователя)
    private int getAction() {
        return this.mAction;
    }

    //==============================================================================================
    // Метод для задания переменной, определяющей тип действия (авторизация или регистрация пользователя)
    private void setAction(int action) {
        this.mAction = action;
    }

    //==============================================================================================
    // Метод для считывания логина пользоватедя
    private String getUserLogin() {
        return this.mUserLogin;
    }

    //==============================================================================================
    // Метод для задания логина пользоватедя
    private void setUserLogin(String userLogin) {
        this.mUserLogin = userLogin;
    }

    //==============================================================================================
    // Метод для считывания типа учетной записи пользоватедя
    private String getUserLoginType() {
        return this.mUserLoginType;
    }

    //==============================================================================================
    // Метод для задания типа учетной записи пользоватедя
    private void setUserLoginType(String userLoginType) {
        userLoginType = userLoginType.toLowerCase().trim();

        // Если переданное в текущую функцию значение типа учетной записи пользоватедя
        // не совпадает ни с одним из разрешенных, то устанавливаем в качестве
        // значения по умолчанию - значение "internal"
        if(!userLoginType.equals(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL) &&
           !userLoginType.equals(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_VKONTAKTE) &&
           !userLoginType.equals(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_FACEBOOK) &&
           !userLoginType.equals(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_TWITTER) &&
           !userLoginType.equals(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_GOOGLE)) {
            this.mUserLoginType = GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL;
        }
        // Иначе
        else {
            this.mUserLoginType = userLoginType;
        }
    }

    //==============================================================================================
    // Метод для считывания пароля пользоватедя
    private String getUserPassword() {
        return this.mUserPassword;
    }

    //==============================================================================================
    // Метод для задания пароля пользоватедя
    private void setUserPassword(String userPassword) {
        this.mUserPassword = userPassword;
    }

    //==============================================================================================
    // Метод для считывания имени пользоватедя
    private String getUserName() {
        return this.mUserName;
    }

    //==============================================================================================
    // Метод для задания имени пользоватедя
    private void setUserName(String userName) {
        this.mUserName = userName;
    }

    //==============================================================================================
    // Метод для считывания фамилии пользоватедя
    private String getUserSurname() {
        return this.mUserSurname;
    }

    //==============================================================================================
    // Метод для задания фамилии пользоватедя
    private void setUserSurname(String userSurname) {
        this.mUserSurname = userSurname;
    }

    //==============================================================================================
    // Метод для считывания адреса электронной почты пользоватедя
    private String getUserMail() {
        return this.mUserMail;
    }

    //==============================================================================================
    // Метод для задания адреса электронной почты пользоватедя
    private void setUserMail(String userMail) {
        this.mUserMail = userMail;
    }

    //==============================================================================================
    // Метод для считывания ссылки на изображение для текущего пользователя
    public String getUserAvatarURL() {
        return this.mUserAvatarURL;
    }

    //==============================================================================================
    // Метод для задания ссылки на изображение для текущего пользователя
    public void setUserAvatarURL(String userAvatarURL) {
        this.mUserAvatarURL = userAvatarURL;
    }

    //==============================================================================================
    // Метод для считывания значения переменной, определяющей для кого текущий пользователь
    // предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
    private String getUserForWho() {
        return this.mUserForWho;
    }

    //==============================================================================================
    // Метод для задания значения переменной, определяющей для кого текущий пользователь
    // предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
    private void setUserForWho(String userForWho) {
        userForWho = userForWho.toLowerCase().trim();

        // Возможные значения: для мужчин, для женщин или для детей
        // Если переданное значение не соответствует ни одному из возможных,
        // то задаем в качестве значения по умолчанию - для мужчин
        if(!userForWho.equals(GlobalFlags.TAG_DRESS_MAN) && !userForWho.equals(GlobalFlags.TAG_DRESS_WOMAN) && !userForWho.equals(GlobalFlags.TAG_DRESS_KID)) {
            this.mUserForWho = GlobalFlags.TAG_DRESS_MAN;
        }
        else {
            this.mUserForWho = userForWho;
        }
    }

    //==============================================================================================
    // Метод для считывания ссылки на профиль текущего пользователя в социальной сети
    private String getUserProfileURL() {
        return this.mUserProfileURL;
    }

    //==============================================================================================
    // Метод для задания ссылки на профиль текущего пользователя в социальной сети
    private void setUserProfileURL(String userProfileURL) {
        this.mUserProfileURL = userProfileURL;
    }

    //==============================================================================================
    // Перед началом фонового потока
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(this.getContext() != null) {
            this.setProgressDialogUserLoginRegister(new ProgressDialog(this.getContext()));

            switch (this.getAction()) {
                case GlobalFlags.ACTION_USER_LOGIN:
                case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                    this.getProgressDialogUserLoginRegister().setMessage(this.getContext().getResources().getString(R.string.string_title_progressdialog_login));
                    break;
                case GlobalFlags.ACTION_USER_REGISTRATION:
                    this.getProgressDialogUserLoginRegister().setMessage(this.getContext().getResources().getString(R.string.string_title_progressdialog_registration));
                    break;
            }

            this.getProgressDialogUserLoginRegister().setIndeterminate(false);
            this.getProgressDialogUserLoginRegister().setCancelable(false);
            this.getProgressDialogUserLoginRegister().show();
        }
    }

    //==============================================================================================
    // Осуществляем процесс авторизации/регистрации пользователя
    protected Integer doInBackground(String... args) {
        // Возвращаемая переменная
        Integer isLoginRegistration = 0;

        //------------------------------------------------------------------------------------------
        // Проверяем наличие Интернет-соединения
        Boolean isInternetConnection = FunctionsConnection.isInternetConnection();

        // Если Интернет-соединение отсутствует
        if(isInternetConnection.equals(false)) {
            return GlobalFlags.FLAG_NO_INTERNET_CONNECTION;
        }

        //------------------------------------------------------------------------------------------
        // Массив параметров, передаваемых на сервер
        HashMap<String, String> postDataParams = new HashMap<>();

        switch(this.getAction()) {
            case GlobalFlags.ACTION_USER_LOGIN:
                postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_USER_LOGIN);
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_LOGIN, this.getUserLogin());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_PASSWORD, this.getUserPassword());
                break;
            case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_USER_LOGIN_SOCIAL);
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE, this.getUserLoginType());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_LOGIN, this.getUserLogin());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_NAME, this.getUserName());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_SURNAME, this.getUserSurname());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_MAIL, this.getUserMail());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_IMAGE, this.getUserAvatarURL());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_PROFILE_URL, this.getUserProfileURL());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_FOR_WHO, this.getUserForWho());
                break;
            case GlobalFlags.ACTION_USER_REGISTRATION:
                postDataParams.put(GlobalFlags.TAG_ACTION_DB, GlobalFlags.TAG_ACTION_DB_USER_REGISTRATION);
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE, this.getUserLoginType());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_LOGIN, this.getUserLogin());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_PASSWORD, this.getUserPassword());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_NAME, this.getUserName());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_SURNAME, this.getUserSurname());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_MAIL, this.getUserMail());
                postDataParams.put(GlobalFlags.TAG_USER_DETAILS_FOR_WHO, this.getUserForWho());
                break;
        }

        // Пересылаем данные на сервер
        String requestResult = HttpGetPostRequest.executePostRequest(GlobalFlags.TAG_URL, postDataParams);

        //------------------------------------------------------------------------------------------
        // Парсим строку в JSON объект
        JSONObject jSONObject = null;

        try {
            jSONObject = new JSONObject(requestResult);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("JSON Parser Error (User Login Registration): " + exception.toString());

            // Передаем в качестве результата сообщение, что возникла ошибка в процессе авторизации/регистрации
            switch (this.getAction()) {
                case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                    return GlobalFlags.USER_LOGIN_ERROR;
                case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                    return GlobalFlags.USER_REGISTRATION_ERROR;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (User Login Registration): " + exception.toString());

            // Передаем в качестве результата сообщение, что возникла ошибка в процессе авторизации/регистрации
            switch (this.getAction()) {
                case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                    return GlobalFlags.USER_LOGIN_ERROR;
                case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                    return GlobalFlags.USER_REGISTRATION_ERROR;
            }
        }

        //------------------------------------------------------------------------------------------
        // Разбираем ответ от сервера
        try {
            // Получаем SUCCESS тег для проверки статуса ответа сервера
            int success = jSONObject.getInt(GlobalFlags.TAG_SUCCESS);

            // Получаем RESULT тег для проверки статуса ответа сервера
            Integer result = jSONObject.getInt(GlobalFlags.TAG_RESULT);

            // Если операция авторизации/регистрации пользователя выполнена успешно
            if (success == 1) {
                // Получаем JSON объект для текущего авторизуемого/регистрируемого пользователя
                if( !jSONObject.isNull(GlobalFlags.TAG_USER) ) {
                    JSONObject jSONCurrentUser = jSONObject.getJSONObject(GlobalFlags.TAG_USER);

                    // id текущего пользователя
                    String currentUserId = "0";

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_ID)) {
                        currentUserId = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_ID);
                    }

                    // id группы для текущего пользователя
                    String currentUserGroupId = "0";

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_GROUP_ID)) {
                        currentUserGroupId = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_GROUP_ID);
                    }

                    // Имя текущего пользователя
                    String currentUserName = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_NAME)) {
                        currentUserName = FunctionsString.jsonDecode(jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_NAME));
                    }

                    // Фамилия текущего пользователя
                    String currentUserSurname = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_SURNAME)) {
                        currentUserSurname = FunctionsString.jsonDecode(jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_SURNAME));
                    }

                    // Логин текущего пользователя
                    String currentUserLogin = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_LOGIN)) {
                        currentUserLogin = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_LOGIN);
                    }

                    // Тип учетной записи для текущего пользователя
                    String currentUserLoginType = GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE_VALUE_INTERNAL;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE)) {
                        currentUserLoginType = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE);
                    }

                    // Адрес электронной почты для текущего пользователя
                    String currentUserMail = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_MAIL)) {
                        currentUserMail = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_MAIL);
                    }

                    // Ссылка на изображение для текущего пользователя
                    String currentUserImage = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_IMAGE)) {
                        currentUserImage = FunctionsString.jsonDecode(jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_IMAGE));
                    }

                    // Токен для текущего пользователя
                    String currentUserToken = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_TOKEN)) {
                        currentUserToken = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_TOKEN);
                    }

                    // Ссылка на профиль в социальной сети для текущего пользователя
                    String currentUserProfileURL = null;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_PROFILE_URL)) {
                        currentUserProfileURL = FunctionsString.jsonDecode(jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_PROFILE_URL));
                    }

                    // Для кого текущий пользователь предпочитает просматривать по умолчанию вещи (для мужчин, женщин или детей)
                    String currentUserForWho = GlobalFlags.TAG_DRESS_MAN;

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_FOR_WHO)) {
                        currentUserForWho = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_FOR_WHO);
                    }

                    // Количество коллекция одежды для текущего пользователя
                    String currentUserCountCollections = "0";

                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS)) {
                        currentUserCountCollections = jSONCurrentUser.getString(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS);
                    }

                    //------------------------------------------------------------------------------
                    // Сохраняем всю информацию о текущем пользователе в соответствующем классе
                    HashMap<String, Object> arrayCurrentUserInfo = new HashMap<>();

                    // Добавляем каждый елемент (параметр текущего пользователя) в HashMap ключ => значение
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_ID, Integer.parseInt(currentUserId));                 // id текущего пользователя в удаленной БД
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_GROUP_ID, Integer.parseInt(currentUserGroupId));      // id группы для текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_NAME, currentUserName);                               // имя текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_SURNAME, currentUserSurname);                         // фамилия текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_LOGIN, currentUserLogin);                             // логин текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_LOGIN_TYPE, currentUserLoginType);                    // тип учетной записи текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_MAIL, currentUserMail);                               // адрес электронной почты для текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_IMAGE, currentUserImage);                             // ссылка на изображение для текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_TOKEN, currentUserToken);                             // токен для текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_PROFILE_URL, currentUserProfileURL);                  // ссылка на профиль в социальной сети для текущего пользователя
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_FOR_WHO, currentUserForWho);
                    arrayCurrentUserInfo.put(GlobalFlags.TAG_USER_DETAILS_COUNT_COLLECTIONS, Integer.parseInt(currentUserCountCollections));

                    // Устанавливаем глобальную переменную, определяющую для кого (мужчин, женщин или детей)
                    // желает просматривать одежду текущий пользователь
                    if(currentUserForWho != null) {
                        if (currentUserForWho.equals(GlobalFlags.TAG_DRESS_MAN) || currentUserForWho.equals(GlobalFlags.TAG_DRESS_WOMAN) || currentUserForWho.equals(GlobalFlags.TAG_DRESS_KID)) {
                            GlobalFlags.setDressForWho(Functions.dressForWhoStringToInt(currentUserForWho));
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Сохраняем все данные о текущем пользователе в сответствующем классе
                    UserDetails.setUserDetails(arrayCurrentUserInfo);

                    //--------------------------------------------------------------------------
                    // Формируем массив, хранящий информацию о категориях и количестве одежды,
                    // входящей в состав избранных наборов одежды для текущего пользователя
                    if (!jSONCurrentUser.isNull(GlobalFlags.TAG_USER_DRESS_IN_COLLECTIONS)) {
                        JSONArray jSONArrayDressInCollections = jSONCurrentUser.getJSONArray(GlobalFlags.TAG_USER_DRESS_IN_COLLECTIONS);

                        // Массив, хранящий информацию для всех категорий одежды,
                        // входящей в состав избранных наборов одежды для текущего пользователя
                        ArrayList<HashMap<String, String>> arrayDressInUserCollections = null;

                        // В цикле разбираем каждую категорию одежды
                        for(int indexCategory = 0; indexCategory < jSONArrayDressInCollections.length(); indexCategory++) {
                            // Считываем объект, хранящий информацию о текущей категории
                            JSONObject jSONObjectCurrentCategory = jSONArrayDressInCollections.getJSONObject(indexCategory);

                            // Разбираем данные о текущей категории

                            // id текущей категории
                            String currentCategoryId = "0";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_CATID)) {
                                currentCategoryId = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_CATID);
                            }

                            // Название текущей категории
                            String currentCategoryTitle = "";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_TITLE)) {
                                currentCategoryTitle = FunctionsString.jsonDecode(jSONObjectCurrentCategory.getString(GlobalFlags.TAG_TITLE));
                            }

                            // Алиас для названия текущей категории
                            String currentCategoryAlias = null;

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_ALIAS)) {
                                currentCategoryAlias = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_ALIAS);
                            }

                            // Тип одежды для текущей категории
                            String currentCategoryType = GlobalFlags.TAG_DRESS_HEAD;

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_TYPE)) {
                                currentCategoryType = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_TYPE);
                            }

                            // Количество одежды для текущей категории
                            String currentCategoryDressCount = "0";

                            if (!jSONObjectCurrentCategory.isNull(GlobalFlags.TAG_DRESS_COUNT)) {
                                currentCategoryDressCount = jSONObjectCurrentCategory.getString(GlobalFlags.TAG_DRESS_COUNT);
                            }

                            //----------------------------------------------------------------------
                            // Сохраняем данные о текщей категории
                            HashMap<String, String> mapCurrentCategoryInfo = new HashMap<>();
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_ID, currentCategoryId);
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_CATID, currentCategoryId);                   // id текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_TITLE, currentCategoryTitle);                // название текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_ALIAS, currentCategoryAlias);                // алиас для названия текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_TYPE, currentCategoryType);                  // тип одежды для текущей категории
                            mapCurrentCategoryInfo.put(GlobalFlags.TAG_DRESS_COUNT, currentCategoryDressCount);     // количество одежды для текущей категории

                            if(arrayDressInUserCollections == null) {
                                arrayDressInUserCollections = new ArrayList<>();
                            }

                            arrayDressInUserCollections.add(mapCurrentCategoryInfo);
                        }

                        //--------------------------------------------------------------------------
                        // Сохраняем массив arrayDressInUserCollections в глобальном массиве
                        UserDetails.setArrayDressInUserCollections(arrayDressInUserCollections);
                    }

                    //------------------------------------------------------------------------------
                    // Возвращаем результат
                    switch (this.getAction()) {
                        case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                        case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                            isLoginRegistration = GlobalFlags.USER_LOGIN_SUCCESS;
                            break;
                        case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                            isLoginRegistration = GlobalFlags.USER_REGISTRATION_SUCCESS;
                            break;
                    }
                }
                else {
                    // Передаем в качестве результата сообщение, что возникла ошибка в процессе авторизации/регистрации
                    switch (this.getAction()) {
                        case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                        case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                            isLoginRegistration = GlobalFlags.USER_LOGIN_ERROR;
                            break;
                        case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                            isLoginRegistration = GlobalFlags.USER_REGISTRATION_ERROR;
                            break;
                    }
                }
            }
            // Иначе, определяем какая возникла ошибка
            else {
                isLoginRegistration = result;
            }
        }
        catch (JSONException exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("JSON Parser Error (User Login Registration): " + exception.toString());

            // Передаем в качестве результата сообщение, что возникла ошибка в процессе авторизации/регистрации
            switch (this.getAction()) {
                case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                    return GlobalFlags.USER_LOGIN_ERROR;
                case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                    return GlobalFlags.USER_REGISTRATION_ERROR;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (User Login Registration): " + exception.toString());

            // Передаем в качестве результата сообщение, что возникла ошибка в процессе авторизации/регистрации
            switch (this.getAction()) {
                case GlobalFlags.ACTION_USER_LOGIN:             // при авторизации пользователя
                case GlobalFlags.ACTION_USER_LOGIN_SOCIAL:
                    return GlobalFlags.USER_LOGIN_ERROR;
                case GlobalFlags.ACTION_USER_REGISTRATION:      // при регистрации пользователя
                    return GlobalFlags.USER_REGISTRATION_ERROR;
            }
        }

        return isLoginRegistration;
    }

    //----------------------------------------------------------------------------------------------
    // После завершения фоновой задачи закрываем прогресс-диалог
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        //------------------------------------------------------------------------------------------
        // Скрываем диалоговое окно
        if(this.getProgressDialogUserLoginRegister() != null) {
            this.getProgressDialogUserLoginRegister().dismiss();
        }

        //------------------------------------------------------------------------------------------
        // Очищаем поле с паролем пользователя
        this.setUserPassword(null);

        //------------------------------------------------------------------------------------------
        // В зависимости от полученного результата выполняем соответствующие действия
        try {
            switch (result) {
                case GlobalFlags.FLAG_NO_INTERNET_CONNECTION:
                    UserDetails.setIsUserLogged(false);

                    // Выводим уведомление о том, что отсутствует Интернет-соединение
                    Toast toastNoInternetConnection = Toast.makeText(this.getContext(), R.string.string_no_internet_connection_toast, Toast.LENGTH_SHORT);
                    toastNoInternetConnection.setGravity(Gravity.CENTER, 0, 0);
                    toastNoInternetConnection.show();

                    break;
                case GlobalFlags.USER_REGISTRATION_ERROR:       // если возникла ошибка при регистрации пользователя
                    UserDetails.setIsUserLogged(false);

                    // Выводим сообщение о возникновении ОШИБКИ при регистрации пользователя
                    Toast toastUserRegistrationError = Toast.makeText(this.getContext(), R.string.string_user_registration_error, Toast.LENGTH_SHORT);
                    toastUserRegistrationError.setGravity(Gravity.CENTER, 0, 0);
                    toastUserRegistrationError.show();

                    break;

                case GlobalFlags.USER_REGISTRATION_SUCCESS:     // в случае успешной регистрации текущего пользователя
                    UserDetails.setIsUserLogged(true);

                    // Выводим сообщение об успешной регистрации пользователя
                    Toast toastUserRegistrationSuccess = Toast.makeText(this.getContext(), R.string.string_user_registration_success, Toast.LENGTH_SHORT);
                    toastUserRegistrationSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastUserRegistrationSuccess.show();

                    // Закрываем окно авторизации/регистрации
                    if(this.getContext() != null) {
                        if (this.getContext().getClass().toString().contains("ActivityLoginRegister")) {
                            ((ActivityLoginRegister) this.getContext()).finish();
                        }
                    }

                    break;

                case GlobalFlags.USER_REGISTRATION_SAME_LOGIN:  // если при регистрации было обнаружено, что пользователь с таким же логином уже существует
                    UserDetails.setIsUserLogged(false);

                    // Устанавливаем фокус в поле ввода логина регистрируемого пользователя
                    if(this.getContext() != null) {
                        if (this.getContext().getClass().toString().contains("ActivityLoginRegister")) {
                            ((ActivityLoginRegister) this.getContext()).editTextUserLoginNew.requestFocus();
                        }
                    }

                    // Выводим соответствующее сообщение
                    Toast toastUserRegistrationSameLogin = Toast.makeText(this.getContext(), R.string.string_user_registration_same_login, Toast.LENGTH_SHORT);
                    toastUserRegistrationSameLogin.setGravity(Gravity.CENTER, 0, 0);
                    toastUserRegistrationSameLogin.show();

                    break;

                case GlobalFlags.USER_REGISTRATION_SAME_MAIL:   // если при регистрации было обнаружено, что пользователь с таким же адресом электронной почты уже существует
                    UserDetails.setIsUserLogged(false);

                    // Устанавливаем фокус в поле ввода адреса электронной почты регистрируемого пользователя
                    if(this.getContext() != null) {
                        if (this.getContext().getClass().toString().contains("ActivityLoginRegister")) {
                            ((ActivityLoginRegister) this.getContext()).editTextUserMail.requestFocus();
                        }
                    }

                    // Выводим соответствующее сообщение
                    Toast toastUserRegistrationSameMail = Toast.makeText(this.getContext(), R.string.string_user_registration_same_mail, Toast.LENGTH_SHORT);
                    toastUserRegistrationSameMail.setGravity(Gravity.CENTER, 0, 0);
                    toastUserRegistrationSameMail.show();

                    break;

                case GlobalFlags.USER_LOGIN_ERROR:              // если возникла ошибка при авторизации пользователя
                    UserDetails.setIsUserLogged(false);

                    // Выводим сообщение о возникновении ОШИБКИ при авторизации пользователя
                    Toast toastUserLoginError = Toast.makeText(this.getContext(), R.string.string_user_login_incorrect, Toast.LENGTH_SHORT);
                    toastUserLoginError.setGravity(Gravity.CENTER, 0, 0);
                    toastUserLoginError.show();

                    break;

                case GlobalFlags.USER_LOGIN_SUCCESS:            // если авторизация пользователя прошла успешно
                    UserDetails.setIsUserLogged(true);

                    // Выводим сообщение об успешной авторизации пользователя
                    Toast toastUserLoginSuccess = Toast.makeText(this.getContext(), R.string.string_user_login_success, Toast.LENGTH_SHORT);
                    toastUserLoginSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastUserLoginSuccess.show();

                    // Закрываем окно авторизации/регистрации
                    if(this.getContext() != null) {
                        if (this.getContext().getClass().toString().contains("ActivityLoginRegister")) {
                            ((ActivityLoginRegister) this.getContext()).finish();
                        }
                    }

                    break;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Post Execute Error (User Login Registration): " + exception.toString());
        }
    }
}
