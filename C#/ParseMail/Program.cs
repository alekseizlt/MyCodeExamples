using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Text.RegularExpressions;

using Pop3;
using ParseMail.Lib;

using MySql.Data;

namespace ParseMail
{
    class Program
    {
        static void Main(string[] args)
        {
            //-------------------------------------------------------------------------------------------------------------------
            // Двухмерный массив, хранящий информацию о введенных пользователям данных для разных почтовых серверов
            List<Dictionary<string, string>> arrayMailServers = new List<Dictionary<string, string>>();

            // Задаем полную информацию обо всех обрабатываемых почтовых серверах
            // Оперативные сводки
            arrayMailServers.Add(new Dictionary<string, string>() {
                    { "ServerMailAddress", "" },        // адрес текущего почтового сервера
                    { "ServerMailUser", "" },           // имя пользователя для подключения к текущему почтовому серверу
                    { "ServerMailPassword", "" },       // пароль пользователя для подключения к текущему почтовому серверу
                    { "ServerDBAddress", "" },          // адрес сервера БД для текущего почтового сервера
                    { "ServerDBUser", "" },             // имя пользователя для подключения к БД для текущего почтового сервера
                    { "ServerDBPassword", "" },         // пароль пользователя для подключения к БД для текущего почтового сервера
                    { "TypeDB", "mysql" },              // тип используемой БД для текущего почтового сервера
                    { "DBName", "" },                   // имя используемой БД для текущего почтового сервера
                    { "PrefixTypeSvodka", "" }          // используемый в качестве префикса тип сводки
                }
            );
            
            //-------------------------------------------------------------------------------------------------------------------
            // Массив, хранящий логические значения, указывающие на то, делать ли запись событий в БД для текущего почтового сервера
            bool[] arrayIsWriteDataToDB = new bool[arrayMailServers.Count];

            //-------------------------------------------------------------------------------------------------------------------
            // В цикле формируем строку подключения для каждого сервера БД для каждого почтового сервера
            // в зависимости от типа используемой БД для текущего почтового сервера
            for( int currentMailServer = 0; currentMailServer < arrayMailServers.Count; currentMailServer++ )
            {
                // Если тип используемой БД для текущего почтового сервера - mysql
                if( arrayMailServers[currentMailServer]["TypeDB"] == "mysql" )
                    arrayMailServers[currentMailServer].Add("StringDBConnection", "Data source=" + arrayMailServers[currentMailServer]["ServerDBAddress"] + ";UserId=" + arrayMailServers[currentMailServer]["ServerDBUser"] + ";Password=" + arrayMailServers[currentMailServer]["ServerDBPassword"] + ";database=" + arrayMailServers[currentMailServer]["DBName"] + ";");

                arrayIsWriteDataToDB[currentMailServer] = false;
            }

            //-------------------------------------------------------------------------------------------------------------------
            // Задаем параметры касаемо местоположения исполняемого файла программы
            // Путь к исполняемому файлу
            Pop3Statics.PathExecuteFile = System.Reflection.Assembly.GetExecutingAssembly().Location;

            // Имя исполняемого файла
            // Делаем обрезку имени исполняемого файла по выражению ", Version", т.к. полное имя исполняемого файла
            // кроме непосредственно самого имени содержит еще и служебную информацию
            Pop3Statics.NameExecuteFile = System.Reflection.Assembly.GetExecutingAssembly().FullName;
            Pop3Statics.NameExecuteFile = Pop3Statics.NameExecuteFile.Substring(0, Pop3Statics.NameExecuteFile.IndexOf(", Version"));

            // Убираем из пути к исполняемому файлу имя самого исполняемого файла и получаем таким образом путь к папке,
            // в которой находится исполняемый файл
            Pop3Statics.PathExecuteFileFolder = Pop3Statics.PathExecuteFile.Substring(0, Pop3Statics.PathExecuteFile.LastIndexOf(Pop3Statics.NameExecuteFile));

            //-------------------------------------------------------------------------------------------------------------------
            // Логическая переменная, указывающая делать (значение true) ли запись в БД о загруженных сводках или нет (значение false)
            // По умолчанию - значение false
            // Определяем в цикле задан ли данный параметр
            for (int i = 0; i < args.Length; i++)
            {
                // Если параметр задан, что надо делать запись в БД
                if (args[i] == "/writetodb")
                    arrayIsWriteDataToDB[0] = true;
            }

            //-------------------------------------------------------------------------------------------------------------------
            // Логическая переменная, определяющая будет ли производится запись операций в лог-файл
            // По умолчанию - значение true
            Pop3Statics.IsWriteDataToLog = true;

            // Определяем в цикле задан ли параметр, запрещающий запись в лог-файл
            for (int i = 0; i < args.Length; i++)
            {
                if (args[i] == "/nowritetolog")
                    Pop3Statics.IsWriteDataToLog = false;
            }

            //-------------------------------------------------------------------------------------------------------------------
            // Путь к файлу настроек
            Pop3Statics.IniFile = new INIFile(Pop3Statics.PathExecuteFileFolder + "settings.ini");

            //-------------------------------------------------------------------------------------------------------------------
            // Ассоциативный массив, имеющий следующие члены:
            // nowDate - текущая дата в формате ДД.ММ.ГГГГ
            // nowTime - текущее время в формате ЧЧ:ММ:СС
            Pop3Statics.ArrayNowDateTime = LibDateTime.getNowDateTime();
            
            //-------------------------------------------------------------------------------------------------------------------
            // В цикле обрабатываем все почтовые сервера
            for (int currentMailServer = 0; currentMailServer < arrayMailServers.Count; currentMailServer++)
            {
                // Считываем текущий тип обрабатываемых сводок
                Pop3Statics.CurrentTypeSvodka = arrayMailServers[currentMailServer]["PrefixTypeSvodka"];

                //-------------------------------------------------------------------------------------------------------------------
                // Задаем путь к корневому каталогу egw
                Pop3Statics.DataFolder = @"";
//              Pop3Statics.DataFolder = @"C:\Documents";

                // Задаем путь к каталогу с лог-файлами
                Pop3Statics.LogFolder = @"";

                //-------------------------------------------------------------------------------------------------------------------
                // Различные локальные переменные
                // Переменная, хранящая дату (в формате mktime) последнего обработанного письма со сводкой
                double lastLetterDateTimeUnix = 0;

                // Переменная, хранящая дату последнего обработанного письма со сводкой в строковом формате
                // ДД.ММ.ГГГГ
                // По умолчанию считаем за дату последнего обработанного письма - начало эпохи Unix
                string lastLetterDateTimeString = "01.01.1970";

                // Переменная, которая предназначена для сохранения даты (в формате mktime) последнего 
                // обработанного письма со сводкой за текущий заход
                double currentLastLetterDateTimeUnix = 0;

                // Порядковый номер обрабатываемого почтового письма из данного почтового ящика
                int numberMessage = 0;

                // Порядковый номер обрабатываемого файлового вложения в текущем почтовом письме
                int numberAttachmentInCurrentMessage = 0;

                //-------------------------------------------------------------------------------------------------------------------
                // Создаем новый лог файл для каждого дня, если запись в лог-файл разрешена
                // Имя лог-файла имеет следующий вид: log_parse_mail_[Тип сводки]_[текущая дата в формате ДД.ММ.ГГГГ]

                // Переменная, хранящая название текущего лог-файла
                string logFileName = String.Empty;

                // Переменная, хранящая полный путь к текущему лог-файлу (включая имя файла с расширением)
                string logFilePath = String.Empty;
                
                // Если запись в лог-файл разрешена
                if(Pop3Statics.IsWriteDataToLog)
                {
                    logFileName = "log_parse_mail_" + Pop3Statics.CurrentTypeSvodka + "_" + LibDateTime.getNowDateTime()["nowDate"] + ".txt";
                    logFilePath = Pop3Statics.LogFolder + @"\" + logFileName;

                    // Если директории, в которой хранятся лог-файлы, не существует, то создаем ее
                    if (!Directory.Exists(Pop3Statics.LogFolder))
                        Directory.CreateDirectory(Pop3Statics.LogFolder);
                    
                    // Теперь непосредственно создаем новый лог-файл для текущего дня, если разрешена запись в лог-файл
                    // Если он существует, то осуществляем запись в конец файла
                    try
                    {
                        Pop3Statics.LogFileWrite = new StreamWriter(logFilePath, true);
                    }
                    catch (UnauthorizedAccessException unauthorizedAccessException)
                    {
                        // Выводим соответствующие сообщения на экран
                        Console.WriteLine("Error: Ошибка доступа к лог-файлу " + logFileName + "!\r\nВозможно текущий лог-файл занят другим приложением!");
                        Console.WriteLine("Error: \r\n" + unauthorizedAccessException.ToString() + "\r\n");
                        Console.WriteLine("Попытка создания другого лог-файла...");

                        // Счетчик, предназначенный для создания другого лог-файла
                        int numberLogFile = 1;

                        logFileName = "log_parse_mail_" + Pop3Statics.CurrentTypeSvodka + "_" + LibDateTime.getNowDateTime()["nowDate"] + "(" + numberLogFile.ToString() + ").txt";
                        logFilePath = Pop3Statics.LogFolder + @"\" + logFileName;

                        // Выполняем цикл до тех пор, пока данного файла logFileName не будет существовать
                        while (File.Exists(logFilePath))
                        {
                            // Увеличиваем порядковый номер лог-файла на 1
                            ++numberLogFile;

                            logFileName = "log_parse_mail_" + Pop3Statics.CurrentTypeSvodka + "_" + LibDateTime.getNowDateTime()["nowDate"] + "(" + numberLogFile.ToString() + ").txt";
                            logFilePath = Pop3Statics.LogFolder + @"\" + logFileName;
                        }

                        // Сохраняем имя текущего рабочего лог-файла
                        Pop3Statics.LogFilename = logFileName;

                        // Открываем для записи новый лог-файл
                        Pop3Statics.LogFileWrite = new StreamWriter(logFilePath, true);

                        // Выводим сообщение на экран о том, в какой лог-файл производим запись событий
                        Console.WriteLine("Успех! Успешно создан другой лог-файл!");
                        Console.WriteLine("Запись событий будет производиться в лог-файл '" + logFileName + "', расположенный по следующему пути:\r\n" + logFilePath);
                    }
                    
                    //-------------------------------------------------------------------------------------------------------------------
                    // Делаем начальные записи в открытый лог-файл
                    FunctionsFile.WriteLineToLogFile(Pop3Statics.LogFileWrite, Pop3Statics.LogFilename, "", true);
                    FunctionsFile.WriteLineToLogFile(Pop3Statics.LogFileWrite, Pop3Statics.LogFilename, "======================================================================================================================", true);
                    FunctionsFile.WriteLineToLogFile(Pop3Statics.LogFileWrite, Pop3Statics.LogFilename, "Текущая дата:  " + LibDateTime.getNowDateTime()["nowDate"], true);
                    FunctionsFile.WriteLineToLogFile(Pop3Statics.LogFileWrite, Pop3Statics.LogFilename, "Текущее время: " + LibDateTime.getNowDateTime()["nowTime"], true);
                }

                //-------------------------------------------------------------------------------------------------------------------
                // Устанавливаем соединение с БД, если выставлен параметр, разрешающий запись в БД
                // Переменная, хранящая идентификатор текущего подключения к БД MySQL
                MySql.Data.MySqlClient.MySqlConnection mySQLConnection = null;

                // Переменная, хранящая текущую выполняемую команду к БД
                MySql.Data.MySqlClient.MySqlCommand mySQLCommand = null;

                // Переменная-объект, предназначенная для извлечения данных из БД
                MySql.Data.MySqlClient.MySqlDataReader mySqlDataReader;

                // Если разрешена запись в БД, то счиываем из данной БД дату последней обработанной оперативной сводки
                if (arrayIsWriteDataToDB[currentMailServer] != null && arrayIsWriteDataToDB[currentMailServer] == true)
                {
                    mySQLConnection = new MySql.Data.MySqlClient.MySqlConnection(arrayMailServers[currentMailServer]["StringDBConnection"]);

                    mySQLCommand = new MySql.Data.MySqlClient.MySqlCommand();
                    mySQLCommand.Connection = mySQLConnection;

                    try
                    {
                        mySQLConnection.Open();
                    }
                    catch (MySql.Data.MySqlClient.MySqlException mySqlException)
                    {
                        // Выводим соответствующие сообщения на экран и делаем записи в лог-файл, если запись в лог-файл разрешена
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Ошибка подключения к серверу БД " + '"' + arrayMailServers[currentMailServer]["ServerDBAddress"] + '"' + " для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + "!!!");
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: \r\n" + mySqlException.ToString() + "\r\n");
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Ошибка! Письма для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + " не были обработаны!");

                        // Если это не последний почтовый сервер из списка обрабатываемых
                        if( currentMailServer < arrayMailServers.Count - 1 )
                            Functions.WriteMessage(Pop3Statics.LogFilename, "Переход к обработке писем для следующего почтового сервера: " + '"' + arrayMailServers[currentMailServer + 1]["ServerMailAddress"] + '"');

                        // Завершаем обработку для данного почтового сервера и переходим к обработке следующего почтового сервера
                        continue;
                    }

                    //-------------------------------------------------------------------------------------------------------
                    // Считываем из БД дату (в формате mktime) последнего обработанного письма со сводкой
                    try
                    {
                        mySQLCommand.CommandText = Pop3Statics.SQLGetLastLetterDateTimeUnix;

                        // Считываем данные из БД
                        mySqlDataReader = mySQLCommand.ExecuteReader();

                        // Если в БД присутствуют данные, удовлетворяющие условиям выборки, то считываем дату последнего письма
                        // Иначе будем обрабатывать все письма из почтового ящика
                        if (mySqlDataReader.HasRows)
                        {
                            mySqlDataReader.Read();

                            // Если дата последнего обработанного письма со сводкой, хранящаяся в БД, отлична от значения NULL
                            if (Convert.ToBoolean(mySqlDataReader["param_value"]))
                            {
                                lastLetterDateTimeUnix = Convert.ToDouble(mySqlDataReader["param_value"]);

                                // Сохраняем считанное значение в классе Pop3Statics
                                Pop3Statics.LastLetterDateTimeUnix = lastLetterDateTimeUnix;

                                // Переводим дату последнего обработанного письма со сводкой в строковый формат ДД.ММ.ГГГГ
                                lastLetterDateTimeString = LibDateTime.ConvertDate(LibDateTime.ConvertFromUnixTimestamp(Pop3Statics.LastLetterDateTimeUnix));
                            }
                        }

                        mySqlDataReader.Close();

                        // Закрываем соединение с БД
                        mySQLConnection.Close();
                    }
                    catch (MySql.Data.MySqlClient.MySqlException mySqlException)
                    {
                        // Выводим соответствующие сообщения на экран и делае запись в ло-файл, если запись в лог-файл разрешена
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Ошибка считывания даты последнего обработанного письма со сводками для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + "!");
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: \r\n" + mySqlException.ToString() + "\r\n");

                        // Устанавливаем значения по умолчанию для даты последней обработанной сводки, т.е. будем обрабатывать все письма
                        lastLetterDateTimeString = "01.01.1970";
                        Pop3Statics.LastLetterDateTimeUnix = lastLetterDateTimeUnix = 0;
                    }
                }
                // Иначе, если параметр, разрешающий запись данных в БД, установлен в значение false, то
                // пробуем считать дату последнего загруженного письма из файла настроек, находящегося в той же папке, что и программа
                else
                {
                    // Считываем дату последней обработанной сводки (формат ДД.ММ.ГГГГ)
                    string lastLetterDate = Pop3Statics.IniFile.INIReadValue("MAIN", "LastLetterDate_" + Pop3Statics.CurrentTypeSvodka.ToUpper());

                    // Проверяем выше считанную дату на соответствие необходимому формату
                    string patternDate = @"[0-9]{2}\.[0-9]{2}\.[0-9]{4}";
                        
                    // Если формат соответствует требуемому
                    if (lastLetterDate != null && lastLetterDate != String.Empty && Regex.IsMatch(lastLetterDate, patternDate) && lastLetterDate.Length == 10)
                    {
                        // Запоминаем в переменной нормальное представление даты последней обработанной сводки
                        lastLetterDateTimeString = lastLetterDate;

                        // Разбиваем номинальное представление даты последней обработанной сводки на составные
                        // части: число, месяц и год
                        string[] arrayDate = lastLetterDate.Split('.');

                        // Преобразуем дату из нормального представления в количество секунд, прошедших с начала эпохи Unix
                        // И сохраняем полученное значение в классе Pop3Statics
                        Pop3Statics.LastLetterDateTimeUnix = LibDateTime.ConvertToUnixTimestamp(new DateTime(Convert.ToInt32(arrayDate[2]), Convert.ToInt32(arrayDate[1]), Convert.ToInt32(arrayDate[0]), 0, 0, 0, 0));
                    }
                    // Иначе, если не соответствует
                    else
                    {
                        // Устанавливаем значения по умолчанию для даты последней обработанной сводки, т.е. будем обрабатывать все письма
                        lastLetterDateTimeString = "01.01.1970";

                        // Сохраняем в классе Pop3Statics (в качестве даты последней обработанной сводки) значение 0
                        Pop3Statics.LastLetterDateTimeUnix = 0;
                    }
                }

                // Выводим соответствующие сообшения на экран и делаем запись в лог-файл, если запись в лог-файл разрешена
                if (lastLetterDateTimeString == "01.01.1970")
                    Functions.WriteMessage(Pop3Statics.LogFilename, "Это первая обработка писем со сводками для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + "!");
                else
                    Functions.WriteMessage(Pop3Statics.LogFilename, "Дата последнего обработанного письма со сводками для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + ": " + lastLetterDateTimeString);

                Functions.WriteMessage(Pop3Statics.LogFilename, "Начинается обработка писем для почтового сервера: " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"' + "...\r\n");
                Functions.WriteMessage(Pop3Statics.LogFilename, "Учетная запись: " + '"' + arrayMailServers[currentMailServer]["ServerMailUser"] + '"' + "\r\n");

                //-------------------------------------------------------------------------------------------------------
                try
                {
                    // Создаем клиента Pop3
                    Pop3Client emailClient = new Pop3Client(arrayMailServers[currentMailServer]["ServerMailAddress"], arrayMailServers[currentMailServer]["ServerMailUser"], arrayMailServers[currentMailServer]["ServerMailPassword"], arrayMailServers[currentMailServer]["ServerDBAddress"], arrayMailServers[currentMailServer]["ServerDBUser"], arrayMailServers[currentMailServer]["ServerDBPassword"], arrayMailServers[currentMailServer]["DBName"], arrayIsWriteDataToDB[currentMailServer], arrayMailServers[currentMailServer]["TypeDB"]);
                        
                    // Устанавливаем соединение с почтовым сервером и проходим процедуру авторизации на данном сервере
                    bool resultOpenInbox = emailClient.OpenInbox();

                    // Если содинение с даным почтовым сервером НЕ УСТАНОВЛЕНО и текущий почтовый сервер НЕ ПОСЛЕДНИЙ из списка,
                    // то переходим к обработке писем следующего почтового сервера по списку
                    if (resultOpenInbox != true)
                    {
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Ошибка! Письма для почтового сервера " + '"' + arrayMailServers[currentMailServer]["ServerDBAddress"] + '"' + " не были обработаны!");
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Переход к обработке писем для следующего почтового сервера: " + '"' + arrayMailServers[currentMailServer + 1]["ServerDBAddress"] + '"' + " для учетной записи: " + '"' + arrayMailServers[currentMailServer + 1]["ServerDBUser"] + '"');

                        continue;
                    }
                        
                    // В цикле перебираем письма из данного почтового ящика
                    while (emailClient.NextEmail())
                    {
                        // Увеличиваем порядковый номер обрабатываемого почтового письма
                        ++numberMessage;
                            
                        // При каждом новом обрабатываемом почтовом письме сбрасываем счетчик файловых вложений
                        numberAttachmentInCurrentMessage = 0;

                        // Преобразуем дату получения  письма в формат ДД.ММ.ГГГГ
                        // Переводим порядковый номер месяца отправки текущего письма в строковое двухзнаковое представление
                        string month = Functions.Convert1To2Symbols(Array.IndexOf(Pop3Statics.MonthsNameEn, emailClient.CurrentMessage.Date["month"]) + 1);

                        string date = emailClient.CurrentMessage.Date["0day"] + "." + month + "." + emailClient.CurrentMessage.Date["year"];

                        // Выводим соответствующие сообшения на экран т делаем запись в лог-файл, елси запись в лог-файл разрешена
                        Functions.WriteMessage(Pop3Statics.LogFilename, "\r\n-------------------------------------------------------------------------------");
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Почтовый сервер: " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"');
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Учетная запись: " + '"' + arrayMailServers[currentMailServer]["ServerMailUser"] + '"');
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Письмо №" + numberMessage);
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Тема письма: " + emailClient.CurrentMessage.Subject);
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Дата получения письма: " + date);

                        // Если текущее письмо имеет содержимое смешанного типа как текст, так и вложенные файлы
                        // А также данное письмо имеет пометку о том, что оно должно быть обработано
                        // Письма обрабатываются только в том случае, если дата их получения является более позднее,
                        // чем дата последнего обработанного письма в прошлый раз, которая была сохранена в БД или записана в файл настроек
                        if (emailClient.CurrentMessage.IsMultipart && emailClient.CurrentMessage.IsCurrentMessageMustProcess)
                        {
                            // Выводим на экран и делаем запись в лог-файл сообщение о количестве файлов, вложенных в текущее письмо
                            Functions.WriteMessage(Pop3Statics.LogFilename, "Количество файловых вложений в текущем письме: " + emailClient.CurrentMessage.CountAttachments);

                            IEnumerator enumerator = emailClient.CurrentMessage.MultipartEnumerator;

                            while (enumerator.MoveNext())
                            {
                                Pop3Component multipart = (Pop3Component)enumerator.Current;
                                    
                                // Если в данном письме обнаружено файловое вложение
                                if (multipart.IsAttachment)
                                {
                                    // Увеличиваем порядковый номер файлового вложения
                                    ++numberAttachmentInCurrentMessage;

                                    //-------------------------------------------------------------------------------------------------------
                                    // Осуществляем декодирование вложенных файлов и их сохранение на жесткий диск
                                    // При этом данная функция возвращает имя файла, под которым он был сохранен на жестком диске
                                    string filename = multipart.DecodeData(emailClient.CurrentMessage.Subject, emailClient.CurrentMessage.CountAttachments);

                                    // Выводим соответствующие сообшения на экран и делаем запись в лог-файл, если запись в лог-файл разрешена
                                    Functions.WriteMessage(Pop3Statics.LogFilename, "Имя файлового вложения №" + numberAttachmentInCurrentMessage + ": " + multipart.Filename);
                                    Functions.WriteMessage(Pop3Statics.LogFilename, "Сохранен на жестком диске под именем: " + filename);

                                    //-------------------------------------------------------------------------------------------------------
                                    // Добавляем информацию о новой сводке в БД, если разрешена запись в БД
                                    if (arrayIsWriteDataToDB[currentMailServer] != null && arrayIsWriteDataToDB[currentMailServer] == true)
                                    {
                                        try
                                        {
                                            //-------------------------------------------------------------------------------------------------------
                                            // Открываем соединение с БД
                                            mySQLConnection.Open();

                                            //-------------------------------------------------------------------------------------------------------
                                            // Добавляем информацию о новой сводке в таблицу egw_svodka_... 

                                            // Очищаем параметры
                                            mySQLCommand.Parameters.Clear();

                                            // Необходимые параметры (столбцы в таблице egw_svodka_...)
                                            mySQLCommand.Parameters.AddWithValue("@name", filename);
                                            mySQLCommand.Parameters.AddWithValue("@date", date);
                                            mySQLCommand.Parameters.AddWithValue("@time_date_mktime", Convert.ToDouble(emailClient.CurrentMessage.Date["mktime"]));

                                            // Проверяем присутствует ли информация о данной сводке в таблице egw_svodka_...
                                            mySQLCommand.CommandText = Pop3Statics.SQLCheckSvodka;

                                            // Считываем данные из БД
                                            mySqlDataReader = mySQLCommand.ExecuteReader();

                                            // Если в БД присутствуют данные, удовлетворяющие условиям выборки, то обновляем 
                                            // информацию в таблице egw_svodka_... о данной сводке
                                            if (mySqlDataReader.HasRows)
                                            {
                                                // Считываем id записи в таблице egw_svodka_..., соответствующей данной сводке
                                                mySqlDataReader.Read();

                                                mySQLCommand.Parameters.AddWithValue("@id", mySqlDataReader["id"]);

                                                mySqlDataReader.Close();

                                                mySQLCommand.CommandText = Pop3Statics.SQLUpdateSvodka;
                                                mySQLCommand.ExecuteNonQuery();
                                            }
                                            // Иначе добавляем информацию о данной сводке в таблицу egw_svodka_...
                                            else
                                            {
                                                mySqlDataReader.Close();

                                                mySQLCommand.CommandText = Pop3Statics.SQLAddSvodka;
                                                mySQLCommand.ExecuteNonQuery();
                                            }

                                            // Очищаем параметры, необходимые для вставки данных в БД
                                            mySQLCommand.Parameters.Clear();

                                            //-------------------------------------------------------------------------------------------------------
                                            // Добавляем информацию о новой сводке в таблицу egw_svodka_..._log

                                            // Вычисляем время в формате mktime для текущего времени и даты
                                            double now_mktime = LibDateTime.ConvertToUnixTimestamp(DateTime.Now);

                                            // Необходимые параметры (столбцы в таблице egw_svodka_...)
                                            mySQLCommand.Parameters.AddWithValue("@name", filename);
                                            mySQLCommand.Parameters.AddWithValue("@time", LibDateTime.getNowDateTime()["nowTime"]);
                                            mySQLCommand.Parameters.AddWithValue("@date", LibDateTime.getNowDateTime()["nowDate"]);
                                            mySQLCommand.Parameters.AddWithValue("@time_date_mktime", now_mktime);

                                            // Проверяем присутствует ли информация о данной сводке в таблице egw_svodka_..._log
                                            mySQLCommand.CommandText = Pop3Statics.SQLCheckSvodkaLog;

                                            // Считываем данные из БД
                                            mySqlDataReader = mySQLCommand.ExecuteReader();

                                            // Если в БД присутствуют данные, удовлетворяющие условиям выборки, то обновляем 
                                            // информацию в таблице egw_svodka_..._log о данной сводке
                                            if (mySqlDataReader.HasRows)
                                            {
                                                // Считываем id записи в таблице egw_svodka_..._log, соответствующей данной сводке
                                                mySqlDataReader.Read();

                                                mySQLCommand.Parameters.AddWithValue("@id", mySqlDataReader["id"]);

                                                mySqlDataReader.Close();

                                                mySQLCommand.CommandText = Pop3Statics.SQLUpdateSvodkaLog;
                                                mySQLCommand.ExecuteNonQuery();
                                            }
                                            // Иначе добавляем информацию о данной сводке в таблицу egw_svodka_..._log
                                            else
                                            {
                                                mySqlDataReader.Close();

                                                mySQLCommand.CommandText = Pop3Statics.SQLAddSvodkaLog;
                                                mySQLCommand.ExecuteNonQuery();
                                            }

                                            // Очищаем параметры, необходимые для вставки данных в БД
                                            mySQLCommand.Parameters.Clear();

                                            //-------------------------------------------------------------------------------------------------------
                                            // Закрываем соединение с БД
                                            mySQLConnection.Close();
                                        }
                                        // В случае ошибки добавления информации о новой сводке в БД
                                        catch (MySql.Data.MySqlClient.MySqlException mySqlException)
                                        {
                                            // Выводим соответствующие сообщения на экран и делаем записи в лог-файл, если запись в лог-файл разрешена
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Ошибка добавления сведений о новой сводке в БД на сервер " + '"' + arrayMailServers[currentMailServer]["ServerDBAddress"] + '"' + "!");
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Почтовый сервер: " + '"' + arrayMailServers[currentMailServer]["ServerMailAddress"] + '"');
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Учетная запись: " + '"' + arrayMailServers[currentMailServer]["ServerMailUser"] + '"');
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Письмо №" + numberMessage);
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Тема письма: " + emailClient.CurrentMessage.Subject);
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error: Дата получения письма: " + date);
                                            Functions.WriteMessage(Pop3Statics.LogFilename, "Error:\r\n" + mySqlException.ToString() + "\r\n");
                                        }
                                    }
                                }
                            }
                        }
                        // Иначе, если данное почтовое письмо не должно быть обработано, т.к. оно
                        // было обработано ранее, то выводим об этом сообщение на экран
                        else if (emailClient.CurrentMessage.IsMultipart && !emailClient.CurrentMessage.IsCurrentMessageMustProcess)
                        {
                            // Выводим сообщение на экран и делаем запись в лог-файл, если запись в лог-файл разрешена
                            if(emailClient.CurrentMessage.IsCurrentMessageHaveError == true)
                                Functions.WriteMessage(Pop3Statics.LogFilename, "Текущее письмо пропущено, так как оно содержит ошибочные данные!!\r\n");
                            else
                                Functions.WriteMessage(Pop3Statics.LogFilename, "Текущее письмо со сводками было обработано ранее!\r\n");
                        }
                        // Иначе, если текущее почтовое письмо не содержит никаких файловых вложений
                        else
                        {
                            // Выводим сообщение на экран и делаем запись в лог-файл, если запись в лог-файл разрешена
                            Functions.WriteMessage(Pop3Statics.LogFilename, "Данное письмо не содержит файловых вложений!\r\n");
                        }

                        // Сохраняем дату получения данного письма (вдруг оно последнее в очереди обработки) в том случае,
                        // если дата получения данного письма больше по величине, чем дата получения предыдущего письма
                        // Последнее полученное письмо имеет максимальное значение даты получения среди всех остальных
                        // полученных писем
                        if( Convert.ToDouble(emailClient.CurrentMessage.Date["mktime"]) - currentLastLetterDateTimeUnix > 0 )
                            currentLastLetterDateTimeUnix = Convert.ToDouble(emailClient.CurrentMessage.Date["mktime"]);
                    }

                    //-------------------------------------------------------------------------------------------------------
                    // Обновляем информацию о дате последнего обработанного письма со сводкой в БД и закрываем соединение в БД,
                    // если разрешена запись в БД
                    if (arrayIsWriteDataToDB[currentMailServer] != null && arrayIsWriteDataToDB[currentMailServer] == true)
                    {
                        // Открываем соединение с БД
                        mySQLConnection.Open();

                        // Обновляем информацию о дате последнего обработанного письма со сводкой в БД
                        mySQLCommand.Parameters.AddWithValue("@svodka_last_date", currentLastLetterDateTimeUnix);
                        mySQLCommand.CommandText = Pop3Statics.SQLUpdateLastLetterDateTimeUnix;
                        mySQLCommand.ExecuteNonQuery();

                        // Закрываем соединение с БД
                        mySQLConnection.Close();
                    }

                    // Закрываем соединение с текущим почтовым сервером
                    emailClient.CloseConnection();

                    // Делаем соответствующие записи на экран и в лог-файл, если запись в лог-файл разрешена
                    Functions.WriteMessage(Pop3Statics.LogFilename, "\r\nОбработка писем завершена!\r\n");
                        
                    // Делаем запись о дате последнего обработанного письма в файл настроек
                    Pop3Statics.IniFile.INIWriteValue("MAIN", "LastLetterDate_" + Pop3Statics.CurrentTypeSvodka.ToUpper(), LibDateTime.ConvertDate(LibDateTime.ConvertFromUnixTimestamp(currentLastLetterDateTimeUnix)));
                }
                catch (Exception exception)
                {
                        // Выводим соответствующие сообщения на экран и делаем записи в лог-файл, если запись в лог-файл разрешена
                        Functions.WriteMessage(Pop3Statics.LogFilename, "Error:\r\n" + exception.ToString() + "\r\n");
                }

                // Закрываем текущий созданный лог-файл, если запись в лог-файл разрешена
                if (Pop3Statics.IsWriteDataToLog)
                    FunctionsFile.CloseLogFileWrite(Pop3Statics.LogFileWrite);
            }

//            Console.WriteLine("Для выхода нажмите любую клавишу ...");
//            Console.ReadKey();
        }
    }
}
