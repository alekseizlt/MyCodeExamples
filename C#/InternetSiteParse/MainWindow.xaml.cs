using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.IO;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Net;
using System.Windows.Shapes;

//using mshtml;
using HtmlAgilityPack;

namespace InternetSiteParse
{
    /// <summary>
    /// Логика взаимодействия для MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        // URL-адрес сайта, с которого будем извлекать все возможные расширения файлов и их описания
        private string mainSiteURL = String.Empty;

        // Массив, хранящий информацию (URL-адрес, название на английском и название на русском) для групп расширений файлов
        private Dictionary<int, Dictionary<string, string>> arrayGroupFileExtensionsInfo = null;

        // Массив, содержащий информацию обо всех расширениях файлов, информация о которых была извлечена с сайта
        private Dictionary<int, Dictionary<string, string>> arrayFileExtensionsInfo = null;

        // Переменная, содержащая индекс текущей (обрабатываемой) категории расширений в массиве ArrayGroupFileExtensionsInfo
        private int currentGroupFileExtensionsIndex = 0;

        // Переменная, содержащая общее количество обрабатываемых расширений
        private int countFileExtensions = 0;

        // Путь к исполняемому файлу программы
        private string pathExecuteFileFolder = String.Empty;

        // Переменная, содержащая название предыдущего обработанного расширения файла
        private string prevFileExtensionName = String.Empty;

        // Объект типа BackgroundWorker, предназначенный для асинхронного выполнения за кулисами приложения
        // длительной операции по подготовке к обработке расширений файлов
        private BackgroundWorker backgroundWorkerInitialize = null;

        // Объект типа BackgroundWorker, предназначенный для асинхронного выполнения за кулисами приложения 
        // длительной операции непосредственно по обработке расширений файлов
        private BackgroundWorker backgroundWorkerProcess = null;

        // Объект типа BackgroundWorker, предназначенный для асинхронного выполнения за кулисами приложения 
        // длительной операции по сохранению в файлы информации об обработанных расширениях
        private BackgroundWorker backgroundWorkerSaveToFile = null;

        //====================================================================================
        // Метод для задания/считывания URL-адреса сайта, с которого будем извлекать 
        // все возможные расширения файлов и их описания
        private string MainSiteURL
        {
            get { return this.mainSiteURL; }
            set { this.mainSiteURL = value; }
        }

        //====================================================================================
        // Метод для задания/считывания массива, хранящего информацию 
        // (URL-адрес, название на английском и название на русском) для групп расширений файлов
        private Dictionary<int, Dictionary<string, string>> ArrayGroupFileExtensionsInfo
        {
            get { return this.arrayGroupFileExtensionsInfo; }
            set { this.arrayGroupFileExtensionsInfo = value; }
        }

        //====================================================================================
        // Метод для задания/считывания массива, содержащего информацию обо всех расширениях файлов,
        // информация о которых была извлечена с сайта
        private Dictionary<int, Dictionary<string, string>> ArrayFileExtensionsInfo
        {
            get { return this.arrayFileExtensionsInfo; }
            set { this.arrayFileExtensionsInfo = value; }
        }

        //====================================================================================
        // Метод для задания/считывания значения переменной, содержащей индекс текущей (обрабатываемой)
        // категории расширений в массиве ArrayGroupFileExtensionsInfo
        private int CurrentGroupFileExtensionsIndex
        {
            get { return this.currentGroupFileExtensionsIndex; }
            set { this.currentGroupFileExtensionsIndex = value; }
        }

        //====================================================================================
        // Метод для задания/считывания значения переменной, содержащей общее количество обрабатываемых расширений
        private int CountFileExtensions
        {
            get { return this.countFileExtensions; }
            set { this.countFileExtensions = value; }
        }

        //====================================================================================
        // Метод для задания/считывания значения переменной, содержащей путь к исполняемому файлу программы
        private string PathExecuteFileFolder
        {
            get { return this.pathExecuteFileFolder; }
            set { this.pathExecuteFileFolder = value; }
        }

        //====================================================================================
        // Метод для задания/считывания значения переменной, содержащей название предыдущего обработанного расширения файла
        private string PrevFileExtensionName
        {
            get { return this.prevFileExtensionName; }
            set { this.prevFileExtensionName = value; }
        }

        //========================================================================================
        // Метод для задания/считывания объекта типа BackgroundWorker, 
        // предназначенного для асинхронного выполнения за кулисами приложения
        // длительной операции по подготовке к обработке расширений файлов
        private BackgroundWorker BackgroundWorkerInitialize
        {
            get { return this.backgroundWorkerInitialize; }
            set { this.backgroundWorkerInitialize = value; }
        }

        //========================================================================================
        // Метод для задания/считывания объекта типа BackgroundWorker, 
        // предназначенного для асинхронного выполнения за кулисами приложения
        // длительной операции непосредственно по обработке расширений файлов
        private BackgroundWorker BackgroundWorkerProcess
        {
            get { return this.backgroundWorkerProcess; }
            set { this.backgroundWorkerProcess = value; }
        }

        //========================================================================================
        // Метод для задания/считывания объекта типа BackgroundWorker, 
        // предназначенный для асинхронного выполнения за кулисами приложения 
        // длительной операции по сохранению в файлы информации об обработанных расширениях
        private BackgroundWorker BackgroundWorkerSaveToFile
        {
            get { return this.backgroundWorkerSaveToFile; }
            set { this.backgroundWorkerSaveToFile = value; }
        }

        //====================================================================================
        // Конструктор
        public MainWindow()
        {
            InitializeComponent();

            // Задаем URL-адрес сайта, с которого будем извлекать все возможные расширения файлов и их описания
            this.MainSiteURL = "http://...";

            // Заполняем массив, хранящий информацию 
            // (URL-адрес, название на английском и название на русском) для групп расширений файлов
            this.ArrayGroupFileExtensionsInfo = new Dictionary<int, Dictionary<string, string>>()
            {
                // Аудиофайлы
                { 0, new Dictionary<string, string>()
                                {
                                    { "key", "AudioFiles" },
                                    { "nameEnglish", "Audio Files" },
                                    { "nameRussian", "Аудио файлы" },
                                    { "url", "http://.../types/audio/" }
                                }
                },

                // Видеофайлы
                { 1, new Dictionary<string, string>()
                                {
                                    { "key", "VideoFiles" },
                                    { "nameEnglish", "Video Files" },
                                    { "nameRussian", "Видео файлы" },
                                    { "url", "http://.../types/video/" }
                                }
                },

                // Рисунки, изображения
                { 2, new Dictionary<string, string>()
                                {
                                    { "key", "DrawingsImages" },
                                    { "nameEnglish", "Drawings, Images" },
                                    { "nameRussian", "Рисунки, изображения" },
                                    { "url", "http://.../types/pictures/" }
                                }
                },

                // Растровые изображения
                { 3, new Dictionary<string, string>()
                                {
                                    { "key", "BitmapImages" },
                                    { "nameEnglish", "Bitmap Images" },
                                    { "nameRussian", "Растровые изображения" },
                                    { "url", "http://.../types/raster-images/" }
                                }
                },

                // Векторные изображения
                { 4, new Dictionary<string, string>()
                                {
                                    { "key", "VectorImages" },
                                    { "nameEnglish", "Vector Images" },
                                    { "nameRussian", "Векторные изображения" },
                                    { "url", "http://.../types/vector-images/" }
                                }
                },

                // 3D-модели, изображения
                { 5, new Dictionary<string, string>()
                                {
                                    { "key", "3DModelsImages" },
                                    { "nameEnglish", "3D-Models, Images" },
                                    { "nameRussian", "3D-модели, изображения" },
                                    { "url", "http://.../types/3d-images/" }
                                }
                },

                // CAD-файлы
                { 6, new Dictionary<string, string>()
                                {
                                    { "key", "CADFiles" },
                                    { "nameEnglish", "CAD Files" },
                                    { "nameRussian", "CAD-файлы" },
                                    { "url", "http://.../types/cad/" }
                                }
                },

                // Текст, документы
                { 7, new Dictionary<string, string>()
                                {
                                    { "key", "TextFiles" },
                                    { "nameEnglish", "Text Files, Documents" },
                                    { "nameRussian", "Текстовые файлы" },
                                    { "url", "http://.../types/text/" }
                                }
                },

                // Архивы, сжатые файлы
                { 8, new Dictionary<string, string>()
                                {
                                    { "key", "Archives" },
                                    { "nameEnglish", "Archives, Compressed Files" },
                                    { "nameRussian", "Архивы, сжатые файлы" },
                                    { "url", "http://.../types/archives/" }
                                }
                },

                // Исполняемые файлы
                { 9, new Dictionary<string, string>()
                                {
                                    { "key", "ExecutablesFiles" },
                                    { "nameEnglish", "Executables Files" },
                                    { "nameRussian", "Исполняемые файлы" },
                                    { "url", "http://.../types/executable/" }
                                }
                },

                // Интернет, web-файлы
                { 10, new Dictionary<string, string>()
                                {
                                    { "key", "Internet" },
                                    { "nameEnglish", "Internet, Web Files" },
                                    { "nameRussian", "Интернет, web-файлы" },
                                    { "url", "http://.../types/internet/" }
                                }
                },

                // Файлы игр
                { 11, new Dictionary<string, string>()
                                {
                                    { "key", "GameFiles" },
                                    { "nameEnglish", "Game Files" },
                                    { "nameRussian", "Файлы игр" },
                                    { "url", "http://.../types/games/" }
                                }
                },

                // Образы дисков
                { 12, new Dictionary<string, string>()
                                {
                                    { "key", "DiskImages" },
                                    { "nameEnglish", "Disk Images" },
                                    { "nameRussian", "Образы дисков" },
                                    { "url", "http://.../types/disc/" }
                                }
                },

                // Системные файлы
                { 13, new Dictionary<string, string>()
                                {
                                    { "key", "SystemFiles" },
                                    { "nameEnglish", "System Files" },
                                    { "nameRussian", "Системные файлы" },
                                    { "url", "http://.../types/system/" }
                                }
                },

                // Файлы шрифтов
                { 14, new Dictionary<string, string>()
                                {
                                    { "key", "FontFiles" },
                                    { "nameEnglish", "Font Files" },
                                    { "nameRussian", "Файлы шрифтов" },
                                    { "url", "http://.../types/fonts/" }
                                }
                },

                // Зашифрованные файлы
                { 15, new Dictionary<string, string>()
                                {
                                    { "key", "EncryptedFiles" },
                                    { "nameEnglish", "Encrypted Files" },
                                    { "nameRussian", "Зашифрованные файлы" },
                                    { "url", "http://.../types/encoded/" }
                                }
                },

                // Размеченные документы
                { 16, new Dictionary<string, string>()
                                {
                                    { "key", "MarkupDocuments" },
                                    { "nameEnglish", "MarkupDocuments" },
                                    { "nameRussian", "Размеченные документы" },
                                    { "url", "http://.../types/layouts/" }
                                }
                },

                // Файлы резервных копий
                { 17, new Dictionary<string, string>()
                                {
                                    { "key", "BackupFiles" },
                                    { "nameEnglish", "BackupFiles" },
                                    { "nameRussian", "Файлы резервных копий" },
                                    { "url", "http://.../types/backup/" }
                                }
                },

                // Файлы данных
                { 18, new Dictionary<string, string>()
                                {
                                    { "key", "DataFiles" },
                                    { "nameEnglish", "DataFiles" },
                                    { "nameRussian", "Файлы данных" },
                                    { "url", "http://.../types/data/" }
                                }
                },

                // Файлы баз данных
                { 19, new Dictionary<string, string>()
                                {
                                    { "key", "DatabaseFiles" },
                                    { "nameEnglish", "Database Files" },
                                    { "nameRussian", "Файлы баз данных" },
                                    { "url", "http://.../types/database/" }
                                }
                },

                // Скрипты, исходный код
                { 20, new Dictionary<string, string>()
                                {
                                    { "key", "ScriptsSourceCode" },
                                    { "nameEnglish", "Scripts, Source Code" },
                                    { "nameRussian", "Скрипты, исходный код" },
                                    { "url", "http://.../types/scripts/" }
                                }
                },

                // Подключаемые модули
                { 21, new Dictionary<string, string>()
                                {
                                    { "key", "Plugins" },
                                    { "nameEnglish", "Plugins" },
                                    { "nameRussian", "Подключаемые модули" },
                                    { "url", "http://.../types/plug-in/" }
                                }
                },

                // Файлы настроек
                { 22, new Dictionary<string, string>()
                                {
                                    { "key", "ConfigurationFiles" },
                                    { "nameEnglish", "Configuration Files" },
                                    { "nameRussian", "Файлы настроек" },
                                    { "url", "http://.../types/settings/" }
                                }
                },

                // Географические файлы, карты
                { 23, new Dictionary<string, string>()
                                {
                                    { "key", "GeographicFilesMaps" },
                                    { "nameEnglish", "Geographic Files, Maps" },
                                    { "nameRussian", "Географические файлы, карты" },
                                    { "url", "http://.../types/geo/" }
                                }
                },

                // Другие файлы
                { 24, new Dictionary<string, string>()
                                {
                                    { "key", "OtherFiles" },
                                    { "nameEnglish", "Other Files" },
                                    { "nameRussian", "Другие файлы" },
                                    { "url", "http://.../types/other/" }
                                }
                }
            };

            // Инициализируем массив, содержащий информацию обо всех расширениях файлов, информация о которых была извлечена с сайта
            this.ArrayFileExtensionsInfo = new Dictionary<int, Dictionary<string, string>>();

            //-------------------------------------------------------------------------------------------------------------------
            // Задаем параметры касаемо местоположения исполняемого файла программы
            // Путь к исполняемому файлу
            string pathExecuteFile = System.Reflection.Assembly.GetExecutingAssembly().Location;

            // Имя исполняемого файла
            // Делаем обрезку имени исполняемого файла по выражению ", Version", т.к. полное имя исполняемого файла
            // кроме непосредственно самого имени содержит еще и служебную информацию
            string nameExecuteFile = System.Reflection.Assembly.GetExecutingAssembly().FullName;
            nameExecuteFile = nameExecuteFile.Substring(0, nameExecuteFile.IndexOf(", Version"));

            // Убираем из пути к исполняемому файлу имя самого исполняемого файла и получаем таким образом путь к папке,
            // в которой находится исполняемый файл
            string pathExecuteFileFolder = pathExecuteFile.Substring(0, pathExecuteFile.LastIndexOf(nameExecuteFile));

            // Сохраняем путь к исполняемому файлу
            this.PathExecuteFileFolder = pathExecuteFileFolder;
        }

        //====================================================================================
        // Метод, возникающий при нажатии на кнопке "Запуск"
        private void buttonRun_Click(object sender, RoutedEventArgs e)
        {
            // Делаем недоступной кнопку "Запуск", чтобы пользователь еще раз не запустил текущую операцию
            this.buttonRun.IsEnabled = false;

            // Выводим в лог соответствующие сообшения
            this.listBoxLog.Items.Add("Выполняется подсчет количества обрабатываемых расширений файлов...");

            // Выводим название текущей операции
            this.textBlockCurrentOperation.Text = "подсчет количества обрабатываемых расширений файлов";

            //-------------------------------------------------------------------------------------------
            // Получаем доступ к объекту типа BackgroundWorker, предназначенный для асинхронного выполнения
            // за кулисами приложения длительной операции по подготовке к обработке расширений файлов
            this.BackgroundWorkerInitialize = (BackgroundWorker)this.FindResource("backgroundWorkerInitialize");

            this.BackgroundWorkerInitialize.WorkerReportsProgress = true;         // включаем возможность отображения хода выполнения длительной задачи
            this.BackgroundWorkerInitialize.WorkerSupportsCancellation = true;    // включаем возможность отмены выполнения дилтельной операции

            // Запускаем выполнение длительной операции
            this.BackgroundWorkerInitialize.RunWorkerAsync();
        }

        //====================================================================================
        // Метод, возникающий при нажатии на кнопке "Стоп"
        private void buttonStop_Click(object sender, RoutedEventArgs e)
        {
            // Делаем активной (доступной) кнопку "Запуск"
            this.buttonRun.IsEnabled = true;

            // Запрашиваем отмену длительно выполняющейся задачи
            if (this.BackgroundWorkerInitialize.WorkerSupportsCancellation == true)
                this.BackgroundWorkerInitialize.CancelAsync();

            if (this.BackgroundWorkerProcess.WorkerSupportsCancellation == true)
                this.BackgroundWorkerProcess.CancelAsync();

            if (this.BackgroundWorkerSaveToFile.WorkerSupportsCancellation == true)
                this.BackgroundWorkerSaveToFile.CancelAsync();
        }

        //========================================================================================
        // Метод для запуска длительно выполняющейся задачи по подготовке к обработке расширений файлов
        private void BackgroundWorkerInitialize_DoWork(object sender, DoWorkEventArgs e)
        {
            // Переменная, хранящая процент выполнения длительной операции
            int percentProgress = 0;

            // В цикле обрабатываем все категории расширений
            for (int currentGroupFileExtensions = 0; currentGroupFileExtensions < this.ArrayGroupFileExtensionsInfo.Count; currentGroupFileExtensions++)
            {
                // Осуществляем парсинг страницы, если текущая страница содержит какой-нибудь текст
                // Считываем документ текущей страницы
                HtmlDocument documentGroupFileExtensions = new HtmlDocument();
                WebClient webClientGroupFileExtensions = new WebClient();

                documentGroupFileExtensions.LoadHtml(webClientGroupFileExtensions.DownloadString(this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["url"]));

                // Извлекаем HTML-разметку текущей парсируемой страницы
                string innerHTML = documentGroupFileExtensions.DocumentNode.InnerHtml.Trim();

                // Если текущая страница (документ) содержит какой-нибудь текст
                if (innerHTML != null && innerHTML != String.Empty && innerHTML != "")
                {
                    // Определяем процент выполнения длительной операции
                    percentProgress = Convert.ToInt32((currentGroupFileExtensions + 1) * 100 / this.ArrayGroupFileExtensionsInfo.Count);

                    this.backgroundWorkerInitialize.ReportProgress(percentProgress);

                    // Получаем доступ к таблице, содержащей список всех расширений для текущей категории
                    // Получаем список всех таблиц на данной странице
                    var tables = documentGroupFileExtensions.DocumentNode.SelectNodes("//table");

                    // В цикле перебираем все найденные таблицы на странице в поисках необходимой
                    // Необходимая содержит атрибут class="tb1"
                    foreach (var currentTable in tables)
                    {
                        // Если текущая таблица является искомой
                        if (currentTable.Attributes.Contains("class") == true)
                        {
                            if (currentTable.Attributes["class"].Value.Trim() == "tbl")
                            {
                                // Получаем массив всех ячеек (элементов <td>) из искомой таблицы
                                var tdFromTableWithFileExtensions = currentTable.SelectNodes("//td");
                                
                                // В цикле обрабатываем все ячейки данной таблицы
                                // Искомые ссылки на страницу описания соответствующего расширения находятся в ячейке с атрибутом class="ex"
                                // В соседней ячейке в этой же строке тоже находится ссылка на эту же страницу описания этого же расширения
                                // Поэтому сначала находим ячейку с атрибутом class="ex", а потом переходим по нужной ссылке, чтобы
                                // исключить дублирование информации
                                foreach (var currentTD in tdFromTableWithFileExtensions)
                                {
                                    // Если текущая ячейка является искомой
                                    if (currentTD.Attributes.Contains("class") == true)
                                    {
                                        if (currentTD.Attributes["class"].Value.Trim() == "ex")
                                        {
                                            // Получаем доступ к необходимой ссылке (элементу <a>) на страницу описания соответствуюещго расширения
                                            var linkFileExtension = currentTD.Element("a");

                                            // Извлекаем название текущего расширения
                                            string currentFileExtensionName = linkFileExtension.InnerText.Trim();

                                            // Так как это искомая ячейка со ссылкой на описание расширения, то увеличиваем общее количество расширений на +1
                                            // При условии, что текущее расширение не совпадает с предыдущим, так как одно и тоже расширение
                                            // может иметь несколько описаний, поэтому могут встречаться повторения расширений
                                            if (this.PrevFileExtensionName.IndexOf(currentFileExtensionName) == -1)
                                            {
                                                this.CountFileExtensions += 1;

                                                // Сохраняем название текущего расширения в качестве названия предыдущего расширения
                                                this.PrevFileExtensionName = currentFileExtensionName;
                                            }
                                        }
                                    }
                                }

                                // Завершаем выполнение цикла
                                break;
                            }
                        }
                    }
                }
            }

            //---------------------------------------------------------------------------------------
            // Запускаем метод по обработке порядковых номеров обрабатываемых почтовых серверов
            // Возвращаемое значение - ассоциативный массив следующих логических переменных
            //                         returnArrayUpdateServerNumber["result"] - логическая переменная, 
            //                         показывающая успешно (значение true) или не успешно (значение false) прошло 
            //                         изменение порядковых номеров обрабатываемых почтовых серверов
            //                         returnArrayUpdateServerNumber["showErrorMessage"] - логическая переменная, 
            //                         определяющая отображать (значение true) или не отображать (значение false)
            //                         сообщение об ошибке, возникшей в процессе выполнения данной функции
            Dictionary<string, bool> returnArray = new Dictionary<string, bool>();

            // Возвращаем результат
            e.Result = returnArray;
        }

        //========================================================================================
        // Метод для запуска длительно выполняющейся задачи по обработке расширений файлов
        private void BackgroundWorkerProcess_DoWork(object sender, DoWorkEventArgs e)
        {
            // Переменная, хранящая процент выполнения длительной операции
            int percentProgress = 0;

            // Порядковый номер текущего обрабатываемого расширения
            int currentFileExtensionsNum = 0;

            // В цикле обрабатываем все категории расширений
            for (int currentGroupFileExtensions = 18; currentGroupFileExtensions < 25; currentGroupFileExtensions++)
            {
                // Осуществляем парсинг страницы, если текущая страница содержит какой-нибудь текст
                // Считываем документ текущей страницы
                HtmlDocument documentGroupFileExtensions = new HtmlDocument();
                WebClient webClientGroupFileExtensions = new WebClient();

                documentGroupFileExtensions.LoadHtml(webClientGroupFileExtensions.DownloadString(this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["url"]));

                // Извлекаем HTML-разметку текущей парсируемой страницы
                string innerHTML = documentGroupFileExtensions.DocumentNode.InnerHtml.Trim();
                
                // Если текущая страница (документ) содержит какой-нибудь текст
                if (innerHTML != null && innerHTML != String.Empty && innerHTML != "")
                {
                    this.backgroundWorkerProcess.ReportProgress(percentProgress, new Dictionary<string, object>()
                            {
                                { "logMessage", "\n==========================================================================\nНачалась обработка расширений категории '" + this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["nameRussian"] + "'...\n"},
                                { "currentGroupFileExtensionsIndex", currentGroupFileExtensions },   // порядковый номер текущей (обрабатываемой) категории расширений
                                { "currentFileExtensionsIndex", currentFileExtensionsNum }           // порядковый номер текущего обрабатываемого расширения
                            }
                    );
                    
                    // Получаем доступ к таблице, содержащей список всех расширений для текущей категории
                    // Получаем список всех таблиц на данной странице
                    var tables = documentGroupFileExtensions.DocumentNode.SelectNodes("//table");
                    
                    // В цикле перебираем все найденные таблицы на странице в поисках необходимой
                    // Необходимая содержит атрибут class="tb1"
                    foreach (var currentTable in tables)
                    {
                        // Если текущая таблица является искомой
                        if (currentTable.Attributes.Contains("class") == true)
                        {
                            if (currentTable.Attributes["class"].Value.Trim() == "tbl")
                            {
                                // Получаем массив всех ячеек (элементов <td>) из искомой таблицы
                                var tdFromTableWithFileExtensions = currentTable.SelectNodes("//td");

                                // В цикле обрабатываем все ячейки данной таблицы
                                // Искомые ссылки на страницу описания соответствующего расширения находятся в ячейке с атрибутом class="ex"
                                // В соседней ячейке в этой же строке тоже находится ссылка на эту же страницу описания этого же расширения
                                // Поэтому сначала находим ячейку с атрибутом class="ex", а потом переходим по нужной ссылке, чтобы
                                // исключить дублирование информации
                                foreach (var currentTD in tdFromTableWithFileExtensions)
                                {
                                    // Если текущая ячейка является искомой
                                    if (currentTD.Attributes.Contains("class") == true)
                                    {
                                        if (currentTD.Attributes["class"].Value.Trim() == "ex")
                                        {
                                            // Получаем доступ к необходимой ссылке (элементу <a>) на страницу описания соответствуюещго расширения
                                            var linkFileExtension = currentTD.Element("a");

                                            // Извлекаем название текущего расширения
                                            string currentFileExtensionName = linkFileExtension.InnerText.Trim();

                                            // Описания текущего расширения файла
                                            string currentFileExtensionDescriptionRUS = String.Empty;   // на русском
                                            string currentFileExtensionDescriptionENG = String.Empty;   // на английском

                                            // Проверяем, чтобы текущее расширение не совпадало с предыдущим, так как одно и тоже расширение
                                            // может иметь несколько описаний, поэтому могут встречаться повторения расширений
                                            if (this.PrevFileExtensionName.IndexOf(currentFileExtensionName) == -1)
                                            {
                                                this.CountFileExtensions += 1;

                                                // Если текущий элемент <a> содержит атрибут href (ссылку на страницу), то идем дальше
                                                if (linkFileExtension.Attributes.Contains("href") == true)
                                                {
                                                    // Признаком того, что текущая ссылка указывает на страницу описания какого-либо расширения,
                                                    // является присутствие в адресе ссылки (атрибут href) слова types
                                                    if (linkFileExtension.Attributes["href"].Value.Trim().IndexOf("types") != -1)
                                                    {
                                                        // Загружаем страницу для текущего разрешения и парсим ее
                                                        HtmlDocument documentFileExtension = new HtmlDocument();
                                                        WebClient webClientFileExtension = new WebClient();

                                                        // Если возникла ошибка при загрузке страницы описания текущего расширения,
                                                        // то переходим к обработке следующего расширения
                                                        try
                                                        {
                                                            documentFileExtension.LoadHtml(webClientFileExtension.DownloadString(this.MainSiteURL + linkFileExtension.Attributes["href"].Value));
                                                        }
                                                        catch(Exception exception)
                                                        {
//                                                          this.listBoxLog.Items.Add(exception.ToString());

                                                            continue;
                                                        }

                                                        // Извлекаем HTML-разметку текущей парсируемой страницы, содержащей описание текущего расширения файла
                                                        string innerHTMLFileExtension = documentFileExtension.DocumentNode.InnerHtml.Trim();

                                                        // Если текущая страница (документ), содержащая описание текущего расширения файла содержит какой-нибудь текст
                                                        if (innerHTMLFileExtension != null && innerHTMLFileExtension != String.Empty && innerHTMLFileExtension != "")
                                                        {
                                                            // Получаем доступ к таблице, содержащей полное описание для текущего расширения файла
                                                            // Получаем список всех таблиц на данной странице
                                                            var tablesFileExtension = documentFileExtension.DocumentNode.SelectNodes("//table");

                                                            // В цикле перебираем все найденные таблицы на странице в поисках необходимой
                                                            // Необходимая содержит атрибут class="desc"
                                                            foreach (var currentTableFileExtension in tablesFileExtension)
                                                            {
                                                                // Если текущая таблица является искомой
                                                                if (currentTableFileExtension.Attributes.Contains("class") == true)
                                                                {
                                                                    if (currentTableFileExtension.Attributes["class"].Value.Trim() == "desc")
                                                                    {
                                                                        // Получаем массив всех ячеек (элементов <td>) из искомой таблицы
                                                                        var tdsFromTableFileExtension = currentTableFileExtension.SelectNodes("//td");

                                                                        // В цикле обрабатываем все ячейки из данной таблицы
                                                                        // Вторая ячейка содержит описание расширения НА РУССКОМ
                                                                        // Четвертая ячейка содержит описание расширения НА АНГЛИЙСКОМ
                                                                        // Шестая ячейка содержит ТИП для текущего расширения
                                                                        // Переменная, содержащая порядковый номер ячейки из таблицы, содержащий описание текущего расширения
                                                                        int numTDInTableFileExtension = 0;

                                                                        // Значение ссылки на категорию, найденной на странице описания текущего расширения
                                                                        string linkGroupFileExtensionValue = String.Empty;

                                                                        foreach (var currentTDFromTableFileExtension in tdsFromTableFileExtension)
                                                                        {
                                                                            // Переходим к следующей ячейке в таблице (увеличиваем порядковый номер numTDInTableFileExtension)
                                                                            ++numTDInTableFileExtension;

                                                                            // Если это вторая ячейка, содержащая описание текущего расширения на русском
                                                                            if (numTDInTableFileExtension == 2)
                                                                            {
                                                                                currentFileExtensionDescriptionRUS = currentTDFromTableFileExtension.InnerText.Trim();
                                                                            }
                                                                            // Иначе, елси это четвертая ячейка, содержащая описание текущего расширения на русском
                                                                            else if (numTDInTableFileExtension == 4)
                                                                            {
                                                                                currentFileExtensionDescriptionENG = currentTDFromTableFileExtension.InnerText.Trim();
                                                                            }
                                                                            // Считываем тип (шестая ячейка), для которого приведены описания обрабатываемого расширения,
                                                                            // т.к. одно и тоже расширение может относиться к разным типам
                                                                            // Соответственно на странице описания расширения может присутствовать несоклько таблиц 
                                                                            // с атрибутом class="desc"
                                                                            else if (numTDInTableFileExtension == 6)
                                                                            {
                                                                                // Получаем доступ к необходимой ссылке (элементу <a>) на страницу описания соответствуюещго расширения
                                                                                var linkGroupFileExtension = currentTDFromTableFileExtension.SelectSingleNode("//a");

                                                                                // Сохраняем значение ссылки на группу для группы, найденной на странице описания текущего расширения
                                                                                linkGroupFileExtensionValue = linkGroupFileExtension.Attributes["href"].Value.Trim();
                                                                            }
                                                                        }

                                                                        // Если изначально считанная категория для данного расширения совпадает с категорией,
                                                                        // считанной на странице описания данного расширения, то завершаем обработку текущего расширения
                                                                        if (this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["url"].IndexOf(linkGroupFileExtensionValue) != -1)
                                                                            break;
                                                                    }
                                                                }
                                                            }

                                                            // Сохраняем информацию об обработанном расширении в соответствующий массив
                                                            this.ArrayFileExtensionsInfo.Add(currentFileExtensionsNum, new Dictionary<string, string>() 
                                                                                {
                                                                                    { "numGroup", currentGroupFileExtensions.ToString() },
                                                                                    { "nameGroup", this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["key"] },
                                                                                    { "nameGroupRus", this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["nameRussian"] },
                                                                                    { "nameExtension", currentFileExtensionName },
                                                                                    { "descEnglish", currentFileExtensionDescriptionENG },
                                                                                    { "descRussian", currentFileExtensionDescriptionRUS }
                                                                                }
                                                            );

                                                            // Увеличиваем порядковый номер текущего (обработанного) расширения на +1
                                                            ++currentFileExtensionsNum;

                                                            // Выводим сообщение об успешной обработке текущего расширения
                                                            // Определяем процент выполнения длительной операции
                                                            percentProgress = Convert.ToInt32((currentFileExtensionsNum + 1) * 100 / this.CountFileExtensions);

                                                            this.backgroundWorkerProcess.ReportProgress(percentProgress, new Dictionary<string, object>()
                                                                                {
                                                                                    { "logMessage", "Расширение '" + currentFileExtensionName + "' из категории '" + this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["nameRussian"] + "' УСПЕШНО ОБРАБОТАНО!!!"},
                                                                                    { "currentGroupFileExtensionsIndex", currentGroupFileExtensions },   // порядковый номер текущей (обрабатываемой) категории расширений
                                                                                    { "currentFileExtensionsIndex", currentFileExtensionsNum }           // порядковый номер текущего обрабатываемого расширения
                                                                                }
                                                            );

                                                            // Сохраняем название текущего расширения в качестве названия предыдущего расширения
                                                            this.PrevFileExtensionName = currentFileExtensionName;
                                                        }
                                                    }
                                                }
                                                // Иначе переходим к обработке следующего расширения
                                                else
                                                    continue;
                                            }
                                        }
                                    }
                                }
                                                   
                                // Завершаем выполнение цикла
                                break;
                            }
                        }
                    }
                    
                    // Выводим сообщение окончания обработки данной категории расширений
                    this.backgroundWorkerProcess.ReportProgress(percentProgress, new Dictionary<string, object>()
                            {
                                { "logMessage", "\nОбработка расширений категории '" + this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["nameRussian"] + "' УСПЕШНО ЗАВЕРШЕНА!!!\n\n"},
                                { "currentGroupFileExtensionsIndex", currentGroupFileExtensions },   // порядковый номер текущей (обрабатываемой) категории расширений
                                { "currentFileExtensionsIndex", currentFileExtensionsNum }           // порядковый номер текущего обрабатываемого расширения
                            }
                    );                
                }
                else
                    this.backgroundWorkerProcess.ReportProgress(percentProgress, new Dictionary<string, object>()
                            {
                                { "logMessage", "\nПри обработке категории '" + this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensions]["nameRussian"] + "' ВОЗНИКЛА ОШИБКА!!!\n\n"},
                                { "currentGroupFileExtensionsIndex", currentGroupFileExtensions },   // порядковый номер текущей (обрабатываемой) категории расширений
                                { "currentFileExtensionsIndex", currentFileExtensionsNum }           // порядковый номер текущего обрабатываемого расширения
                            }
                    );
            }

            //---------------------------------------------------------------------------------------
            // Запускаем метод по обработке порядковых номеров обрабатываемых почтовых серверов
            // Возвращаемое значение - ассоциативный массив следующих логических переменных
            //                         returnArrayUpdateServerNumber["result"] - логическая переменная, 
            //                         показывающая успешно (значение true) или не успешно (значение false) прошло 
            //                         изменение порядковых номеров обрабатываемых почтовых серверов
            //                         returnArrayUpdateServerNumber["showErrorMessage"] - логическая переменная, 
            //                         определяющая отображать (значение true) или не отображать (значение false)
            //                         сообщение об ошибке, возникшей в процессе выполнения данной функции
            Dictionary<string, bool> returnArray = new Dictionary<string, bool>();

            // Возвращаем результат
            e.Result = returnArray;
        }

        //========================================================================================
        // Метод для запуска длительно выполняющейся задачи по сохранению в файлы информации об обработанных расширениях
        private void BackgroundWorkerSaveToFile_DoWork(object sender, DoWorkEventArgs e)
        {
            // Переменная, хранящая процент выполнения длительной операции
            int percentProgress = 0;

            // Открываем файлы для записи в них информации об обработанных расширениях
            FileStream fileStreamENG      = new FileStream(this.PathExecuteFileFolder + "ResourcesENG.txt", FileMode.Create);       // для английского языка
            FileStream fileStreamRUS      = new FileStream(this.PathExecuteFileFolder + "ResourcesRUS.txt", FileMode.Create);       // для русского языка
            FileStream fileStreamTreeView = new FileStream(this.PathExecuteFileFolder + "ResourcesTreeView.txt", FileMode.Create);  // для формирования дерева

            StreamWriter streamWriterENG = new StreamWriter(fileStreamENG, Encoding.UTF8);
            StreamWriter streamWriterRUS = new StreamWriter(fileStreamRUS, Encoding.UTF8);
            StreamWriter streamWriterTreeView = new StreamWriter(fileStreamTreeView, Encoding.UTF8);

            // В цикле обрабатываем все расширения файлов
            for (int currentFileExtension = 0; currentFileExtension < this.ArrayFileExtensionsInfo.Count; currentFileExtension++)
            {
                // Делаем запись информации о текущем расширении в файл для английского языка
                streamWriterENG.WriteLine("  <data name=" + '"' + "TreeViewFileExtensionsItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroup"] + "SubItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"].ToUpper() + "Header" + '"' + " xml:space=" + '"' + "preserve" + '"' + ">");
                streamWriterENG.WriteLine("    <value>(" + this.ArrayFileExtensionsInfo[currentFileExtension]["descEnglish"] + ")</value>");
                streamWriterENG.WriteLine("    <comment>Расширение файла " + '"' + "." + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"] + '"' + " из категории " + '"' + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroupRus"] + '"' + "</comment>");
                streamWriterENG.WriteLine("  </data>");

                // Делаем запись информации о текущем расширении в файл для русского языка
                streamWriterRUS.WriteLine("  <data name=" + '"' + "TreeViewFileExtensionsItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroup"] + "SubItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"].ToUpper() + "Header" + '"' + " xml:space=" + '"' + "preserve" + '"' + ">");
                streamWriterRUS.WriteLine("    <value>(" + this.ArrayFileExtensionsInfo[currentFileExtension]["descRussian"] + ")</value>");
                streamWriterRUS.WriteLine("    <comment>Расширение файла " + '"' + "." + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"] + '"' + " из категории " + '"' + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroupRus"] + '"' + "</comment>");
                streamWriterRUS.WriteLine("  </data>");

                // Делаем запись в файл, содержащий функцию для формирования дерева
                streamWriterTreeView.WriteLine("new TreeViewItemWithCheckBoxViewModel(" + '"' + '.' + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"] + '"' + ", Properties.Resources.TreeViewFileExtensionsItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroup"] + "SubItem" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"].ToUpper() + "Header),");

                // Выводим сообщение об успешном сохранении информации о текущем расширении в файл
                // Определяем процент выполнения длительной операции
                percentProgress = Convert.ToInt32((currentFileExtension + 1) * 100 / this.ArrayFileExtensionsInfo.Count);

                this.backgroundWorkerSaveToFile.ReportProgress(percentProgress, new Dictionary<string, object>()
                        {
                            { "logMessage", " Информация о расширении '" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameExtension"] + "' из категории '" + this.ArrayFileExtensionsInfo[currentFileExtension]["nameGroupRus"] + "' УСПЕШНО СОХРАНЕНА В ФАЙЛЫ 'ResourcesENG.txt' и 'ResourcesRUS.txt'!!!"},
                            { "currentGroupFileExtensionsIndex", this.ArrayFileExtensionsInfo[currentFileExtension]["numGroup"] },   // порядковый номер текущей (обрабатываемой) категории расширений
                            { "currentFileExtensionsIndex", currentFileExtension }                                                   // порядковый номер текущего обрабатываемого расширения
                        }
                );
            }

            //---------------------------------------------------------------------------------------
            // Запускаем метод по обработке порядковых номеров обрабатываемых почтовых серверов
            // Возвращаемое значение - ассоциативный массив следующих логических переменных
            //                         returnArrayUpdateServerNumber["result"] - логическая переменная, 
            //                         показывающая успешно (значение true) или не успешно (значение false) прошло 
            //                         изменение порядковых номеров обрабатываемых почтовых серверов
            //                         returnArrayUpdateServerNumber["showErrorMessage"] - логическая переменная, 
            //                         определяющая отображать (значение true) или не отображать (значение false)
            //                         сообщение об ошибке, возникшей в процессе выполнения данной функции
            Dictionary<string, object> returnArray = new Dictionary<string, object>() 
            {
                { "fileStreamENG", fileStreamENG},
                { "fileStreamRUS", fileStreamRUS},
                { "fileStreamTreeView", fileStreamTreeView},
                { "streamWriterENG", streamWriterENG},
                { "streamWriterRUS", streamWriterRUS},
                { "streamWriterTreeView", streamWriterTreeView}
            };

            // Возвращаем результат
            e.Result = returnArray;
        }

        //========================================================================================
        // Метод, вызываемый по окончании асинхронного выполнения за кулисами приложения 
        // длительной операции непосредственно по подготовке к обработке расширений файлов
        private void BackgroundWorkerInitialize_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            // Если возникла ошибка в процессе выполнения длительной операции, то выводим
            // соответствующее сообщение
            if (e.Error != null)
            {
                // Ошибка была сгенерирована обработчиком события DoWork
                System.Windows.MessageBox.Show(e.Error.Message, "ERROR!!!");
            }
            // Иначе, если не возникала никакая ошибка
            else
            {
                // Считываем результат выполнения длительной операции
                Dictionary<string, bool> returnArray = e.Result as Dictionary<string, bool>;

                this.PrevFileExtensionName = String.Empty;

                // Выводим соответствующие сообщения в лог
                this.listBoxLog.Items.Add("\nПодсчет количества обрабатываемых расширений файлов закончен!");
                this.listBoxLog.Items.Add("\nНайдено " + this.ArrayGroupFileExtensionsInfo.Count + " категорий, содержащих " + this.CountFileExtensions + " расширений файлов!\n");

                // Выводим название текущей операции
                this.textBlockCurrentOperation.Text = "парсинг расширений файлов";

                // Выводим общее количество обрабатываемых категорий и расширений
                this.textBlockCategoryTotal.Text = this.ArrayGroupFileExtensionsInfo.Count.ToString();
                this.textBlockExtensionTotal.Text = this.CountFileExtensions.ToString();

                //-------------------------------------------------------------------------------------------
                // Получаем доступ к объекту типа BackgroundWorker, предназначенный для асинхронного выполнения
                // за кулисами приложения длительной операции непосредственно по обработке расширений файлов
                this.BackgroundWorkerProcess = (BackgroundWorker)this.FindResource("backgroundWorkerProcess");

                this.BackgroundWorkerProcess.WorkerReportsProgress = true;         // включаем возможность отображения хода выполнения длительной задачи
                this.BackgroundWorkerProcess.WorkerSupportsCancellation = true;    // включаем возможность отмены выполнения дилтельной операции

                // Запускаем выполнение длительной операции
                this.BackgroundWorkerProcess.RunWorkerAsync();
            }
        }

        //========================================================================================
        // Метод, вызываемый по окончании асинхронного выполнения за кулисами приложения 
        // длительной операции непосредственно по обработке расширений файлов
        private void BackgroundWorkerProcess_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            // Если возникла ошибка в процессе выполнения длительной операции, то выводим
            // соответствующее сообщение
            if (e.Error != null)
            {
                // Ошибка была сгенерирована обработчиком события DoWork
                System.Windows.MessageBox.Show(e.Error.Message, "ERROR!!!");
            }
            // Иначе, если не возникала никакая ошибка
            else
            {
                // Считываем результат выполнения длительной операции
                Dictionary<string, bool> returnArray = e.Result as Dictionary<string, bool>;

                // Выводим название текущей операции
                this.textBlockCurrentOperation.Text = "сохранение расширений в файлы";

                //-------------------------------------------------------------------------------------------
                // Получаем доступ к объекту типа BackgroundWorker, предназначенный для асинхронного выполнения
                // за кулисами приложения длительной операции по сохранению в файлы информации об обработанных расширениях
                this.BackgroundWorkerSaveToFile = (BackgroundWorker)this.FindResource("backgroundWorkerSaveToFile");

                this.BackgroundWorkerSaveToFile.WorkerReportsProgress = true;         // включаем возможность отображения хода выполнения длительной задачи
                this.BackgroundWorkerSaveToFile.WorkerSupportsCancellation = true;    // включаем возможность отмены выполнения дилтельной операции

                // Запускаем выполнение длительной операции
                this.BackgroundWorkerSaveToFile.RunWorkerAsync();
            }
        }

        //========================================================================================
        // Метод, вызываемый по окончании асинхронного выполнения за кулисами приложения 
        // длительной операции по сохранению в файлы информации об обработанных расширениях
        private void BackgroundWorkerSaveToFile_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            // Если возникла ошибка в процессе выполнения длительной операции, то выводим
            // соответствующее сообщение
            if (e.Error != null)
            {
                // Ошибка была сгенерирована обработчиком события DoWork
                System.Windows.MessageBox.Show(e.Error.Message, "ERROR!!!");
            }
            // Иначе, если не возникала никакая ошибка
            else
            {
                // Считываем результат выполнения длительной операции
                Dictionary<string, object> returnArray = e.Result as Dictionary<string, object>;

                // Закрываем файлы для записи
                FileStream fileStreamENG = returnArray["fileStreamENG"] as FileStream;
                FileStream fileStreamRUS = returnArray["fileStreamRUS"] as FileStream;
                FileStream fileStreamTreeView = returnArray["fileStreamTreeView"] as FileStream;
                StreamWriter streamWriterENG = returnArray["streamWriterENG"] as StreamWriter;
                StreamWriter streamWriterRUS = returnArray["streamWriterRUS"] as StreamWriter;
                StreamWriter streamWriterTreeView = returnArray["streamWriterTreeView"] as StreamWriter;

                streamWriterENG.Flush();
                streamWriterENG.Close();
                fileStreamENG.Close();

                streamWriterRUS.Flush();
                streamWriterRUS.Close();
                fileStreamRUS.Close();

                streamWriterTreeView.Flush();
                streamWriterTreeView.Close();
                fileStreamTreeView.Close();

                // Делаем активной (доступной) кнопку "Запуск"
                this.buttonRun.IsEnabled = true;

                // Выводим сообщение о завершении обработки расширений
                MessageBox.Show("Обработка расширений файлов УСПЕШНО ЗАВЕРШЕНА!!!");
            }
        }

        //========================================================================================
        // Метод для отслеживания продвижения выполнения длительной операции
        // по подготовке к обработке расширений файлов
        private void BackgroundWorkerInitialize_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            // Считываем процент выполнения длительной операции

            // Для прогресс-бара, отображающего процесс выполнения текущей операции
            this.progressBarCurrentOperation.Value = e.ProgressPercentage;
            this.textBlockPercentageCurrentOperation.Text = e.ProgressPercentage.ToString();

            // Для прогресс-бара, отображающего общий пргресс выполнения всех операций
            this.progressBarTotalProgress.Value = e.ProgressPercentage / 3;
            this.textBlockPercentageTotalProgress.Text = (e.ProgressPercentage / 3).ToString();
        }

        //========================================================================================
        // Метод для отслеживания продвижения выполнения длительной операции
        // непосредственно по обработке расширений файлов
        private void BackgroundWorkerProcess_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            // Считываем процент выполнения длительной операции

            // Для прогресс-бара, отображающего процесс выполнения текущей операции
            this.progressBarCurrentOperation.Value = e.ProgressPercentage;
            this.textBlockPercentageCurrentOperation.Text = e.ProgressPercentage.ToString();

            // Для прогресс-бара, отображающего общий пргресс выполнения всех операций
            this.progressBarTotalProgress.Value = (100 + e.ProgressPercentage) / 3;
            this.textBlockPercentageTotalProgress.Text = ((100 + e.ProgressPercentage) / 3).ToString();

            // Считываем дополнительные параметры
            Dictionary<string, object> backgroundWorkerParameters = e.UserState as Dictionary<string, object>;

            string logMessage = backgroundWorkerParameters["logMessage"].ToString();                                                // новое сообщение, отображаемое в логе
            int currentGroupFileExtensionsIndex = Convert.ToInt32(backgroundWorkerParameters["currentGroupFileExtensionsIndex"]);   // порядковый номер текущей (обрабатываемой) категории расширений
            int currentFileExtensionsIndex = Convert.ToInt32(backgroundWorkerParameters["currentFileExtensionsIndex"]);             // порядковый номер текущего (обрабатываемого) расширения

            // Отображаем порядковый номер текущей (обрабатываемой) категории расширений файлов
            this.textBlockCategoryCurrent.Text = (currentGroupFileExtensionsIndex + 1).ToString();

            // Отображаем порядковый номер текущего (обрабатываемого) расширения файла
            this.textBlockExtensionCurrent.Text = (currentFileExtensionsIndex + 1).ToString();

            // Отображаем новое сообщение в логе
            this.listBoxLog.Items.Add(logMessage);

            // Автоматически перелистываем лог в самый низ
            this.listBoxLog.ScrollIntoView(this.listBoxLog.Items[this.listBoxLog.Items.Count - 1]);

            //----------------------------------------------------------------------------------------------------------------------
            // Загружаем в браузер страницу с текущей (обрабатываемой) категорией расширений,
            // если она отличается от предыдущей
            if (this.MainWebBrowser.Source == null)
            {
                this.MainWebBrowser.Navigate(this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensionsIndex]["url"]);
            }
            else
            {
                // Считываем адрес текущей страницы, загруженной в браузер
                string urlMainWebBrowser = this.MainWebBrowser.Source.ToString();

                if ((currentGroupFileExtensionsIndex < this.ArrayGroupFileExtensionsInfo.Count - 1) && (urlMainWebBrowser.IndexOf(this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensionsIndex]["url"]) == -1))
                    this.MainWebBrowser.Navigate(this.ArrayGroupFileExtensionsInfo[currentGroupFileExtensionsIndex]["url"]);
            }
        }

        //========================================================================================
        // Метод для отслеживания продвижения выполнения длительной операции
        // непосредственно по сохранению в файлы информации об обработанных расширениях
        private void BackgroundWorkerSaveToFile_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            // Считываем процент выполнения длительной операции

            // Для прогресс-бара, отображающего процесс выполнения текущей операции
            this.progressBarCurrentOperation.Value = e.ProgressPercentage;
            this.textBlockPercentageCurrentOperation.Text = e.ProgressPercentage.ToString();

            // Для прогресс-бара, отображающего общий пргресс выполнения всех операций
            this.progressBarTotalProgress.Value = (200 + e.ProgressPercentage) / 3;
            this.textBlockPercentageTotalProgress.Text = ((200 + e.ProgressPercentage) / 3).ToString();

            // Считываем дополнительные параметры
            Dictionary<string, object> backgroundWorkerParameters = e.UserState as Dictionary<string, object>;

            string logMessage = backgroundWorkerParameters["logMessage"].ToString();                                                // новое сообщение, отображаемое в логе
            int currentGroupFileExtensionsIndex = Convert.ToInt32(backgroundWorkerParameters["currentGroupFileExtensionsIndex"]);   // порядковый номер текущей (обрабатываемой) категории расширений
            int currentFileExtensionsIndex = Convert.ToInt32(backgroundWorkerParameters["currentFileExtensionsIndex"]);             // порядковый номер текущего (обрабатываемого) расширения

            // Отображаем порядковый номер текущей (обрабатываемой) категории расширений файлов
            this.textBlockCategoryCurrent.Text = (currentGroupFileExtensionsIndex + 1).ToString();

            // Отображаем порядковый номер текущего (обрабатываемого) расширения файла
            this.textBlockExtensionCurrent.Text = (currentFileExtensionsIndex + 1).ToString();

            // Отображаем новое сообщение в логе
            this.listBoxLog.Items.Add(logMessage);

            // Автоматически перелистываем лог в самый низ
            this.listBoxLog.ScrollIntoView(this.listBoxLog.Items[this.listBoxLog.Items.Count - 1]);
        }
    }
}
