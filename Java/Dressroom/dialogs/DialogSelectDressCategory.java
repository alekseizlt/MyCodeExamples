package ru.alexprogs.dressroom.dialogs;

import ru.alexprogs.dressroom.globalflags.GlobalFlags;
import ru.alexprogs.dressroom.R;
import ru.alexprogs.dressroom.db.DBMain;
import ru.alexprogs.dressroom.db.mysql.MySQLGoToDress;
import ru.alexprogs.dressroom.lib.FunctionsLog;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
* Класс для отображения всплывающего окна выбора категорий одежды,
* которые необходимо отобразить на виртуальном манекене
*/
public class DialogSelectDressCategory{

    // Свойства данного класса
    private Context mContext;           // контекст

    // Массивы (списки) элементов RadioButton, созданных динамически для групп категорий: head, leg и foot
    private ArrayList<RadioButton> mArrayRadioButtonCategoryDressHead;          // для головных уборов
    private ArrayList<RadioButton> mArrayRadioButtonCategoryDressLeg;           // для одежды на ноги
    private ArrayList<RadioButton> mArrayRadioButtonCategoryDressFoot;          // для обуви

    // Массив (список) элементов CheckBox, созданных динамически для группы категорий: body и accessory
    private ArrayList<CheckBox> mArrayCheckBoxCategoryDressBody;                // для одежды на тело
    private ArrayList<CheckBox> mArrayCheckBoxCategoryDressAccessory;           // для аксессуаров

    // Количество отмеченных галочкой элементов checkbox из списка категорий одежды для тела
    private int mCountCheckBoxCategoryDressBodyChecked;

    private AlertDialog.Builder mBuilderDialogSelectCategories;

    //==============================================================================================
    // Конструктор
    public DialogSelectDressCategory(Context сontext) {
        // Инициализируем свойства текущего класса
        this.setContext(сontext);                       // контекст

        // Устанавливаем количество отмеченных галочкой элементов checkbox из списка категорий одежды для тела равным 0
        this.setCountCheckBoxCategoryDressBodyChecked(0);
    }

    //==============================================================================================
    // Метод для считывания значения контекста
    public Context getContext() {
        return this.mContext;
    }

    //==============================================================================================
    // Метод для задания значения контекста
    public void setContext(Context context) {
        this.mContext = context;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Головные уборы"
    private ArrayList<RadioButton> getArrayRadioButtonCategoryDressHead() {
        return this.mArrayRadioButtonCategoryDressHead;
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Головные уборы"
    private void setArrayRadioButtonCategoryDressHead(ArrayList<RadioButton> arrayRadioButtonCategoryDressHead) {
        this.mArrayRadioButtonCategoryDressHead = arrayRadioButtonCategoryDressHead;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Одежда для ног"
    private ArrayList<RadioButton> getArrayRadioButtonCategoryDressLeg() {
        return this.mArrayRadioButtonCategoryDressLeg;
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Одежда для ног"
    private void setArrayRadioButtonCategoryDressLeg(ArrayList<RadioButton> arrayRadioButtonCategoryDressLeg) {
        this.mArrayRadioButtonCategoryDressLeg = arrayRadioButtonCategoryDressLeg;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Обувь"
    private ArrayList<RadioButton> getArrayRadioButtonCategoryDressFoot() {
        return this.mArrayRadioButtonCategoryDressFoot;
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов RadioButton, созданных динамически
    // для группы категорий "Обувь"
    private void setArrayRadioButtonCategoryDressFoot(ArrayList<RadioButton> arrayRadioButtonCategoryDressFoot) {
        this.mArrayRadioButtonCategoryDressFoot = arrayRadioButtonCategoryDressFoot;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов RadioButton, созданных динамически
    private ArrayList<RadioButton> getArrayRadioButtonCategoryDress(String dressType) {
        switch (dressType) {
            case GlobalFlags.TAG_DRESS_HEAD:
                return this.getArrayRadioButtonCategoryDressHead();
            case GlobalFlags.TAG_DRESS_LEG:
                return this.getArrayRadioButtonCategoryDressLeg();
            case GlobalFlags.TAG_DRESS_FOOT:
                return this.getArrayRadioButtonCategoryDressFoot();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов RadioButton, созданных динамически
    private void setArrayRadioButtonCategoryDress(String dressType, ArrayList<RadioButton> arrayRadioButtonCategoryDress) {
        switch (dressType) {
            case GlobalFlags.TAG_DRESS_HEAD:
                this.setArrayRadioButtonCategoryDressHead(arrayRadioButtonCategoryDress);
                break;
            case GlobalFlags.TAG_DRESS_LEG:
                this.setArrayRadioButtonCategoryDressLeg(arrayRadioButtonCategoryDress);
                break;
            case GlobalFlags.TAG_DRESS_FOOT:
                this.setArrayRadioButtonCategoryDressFoot(arrayRadioButtonCategoryDress);
                break;
        }
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов CheckBox, созданных динамически
    // для группы категорий "Одежда для тела"
    private ArrayList<CheckBox> getArrayCheckBoxCategoryDressBody() {
        return this.mArrayCheckBoxCategoryDressBody;
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов CheckBox, созданных динамически
    // для группы категорий "Одежда для тела"
    private void setArrayCheckBoxCategoryDressBody(ArrayList<CheckBox> arrayCheckBoxCategoryDressBody) {
        this.mArrayCheckBoxCategoryDressBody = arrayCheckBoxCategoryDressBody;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов CheckBox, созданных динамически
    // для группы категорий "Аксессуары"
    private ArrayList<CheckBox> getArrayCheckBoxCategoryDressAccessory() {
        return this.mArrayCheckBoxCategoryDressAccessory;
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов CheckBox, созданных динамически
    // для группы категорий "Аксессуары"
    private void setArrayCheckBoxCategoryDressAccessory(ArrayList<CheckBox> arrayCheckBoxCategoryDressAccessory) {
        this.mArrayCheckBoxCategoryDressAccessory = arrayCheckBoxCategoryDressAccessory;
    }

    //==============================================================================================
    // Метод для считывания массива (списка) элементов CheckBox, созданных динамически
    private ArrayList<CheckBox> getArrayCheckBoxCategoryDress(String dressType) {
        switch (dressType) {
            case GlobalFlags.TAG_DRESS_BODY:
                return this.getArrayCheckBoxCategoryDressBody();
            case GlobalFlags.TAG_DRESS_ACCESSORY:
                return this.getArrayCheckBoxCategoryDressAccessory();
            default:
                return null;
        }
    }

    //==============================================================================================
    // Метод для задания массива (списка) элементов CheckBox, созданных динамически
    private void setArrayCheckBoxCategoryDress(String dressType, ArrayList<CheckBox> arrayCheckBoxCategoryDress) {
        switch (dressType) {
            case GlobalFlags.TAG_DRESS_BODY:
                this.setArrayCheckBoxCategoryDressBody(arrayCheckBoxCategoryDress);
                break;
            case GlobalFlags.TAG_DRESS_ACCESSORY:
                this.setArrayCheckBoxCategoryDressAccessory(arrayCheckBoxCategoryDress);
                break;
        }
    }

    //==============================================================================================
    // Метод для считывания значения количества отмеченных галочкой элементов checkbox
    // из списка категорий одежды для тела
    private int getCountCheckBoxCategoryDressBodyChecked() {
        return this.mCountCheckBoxCategoryDressBodyChecked;
    }

    //==============================================================================================
    // Метод для задания значения количества отмеченных галочкой элементов checkbox
    // из списка категорий одежды для тела
    private void setCountCheckBoxCategoryDressBodyChecked(int countCheckBoxCategoryDressBodyChecked) {
        this.mCountCheckBoxCategoryDressBodyChecked = countCheckBoxCategoryDressBodyChecked;
    }

    //==============================================================================================
    // Метод для считывания
    private AlertDialog.Builder getBuilderDialogSelectCategories() {
        return this.mBuilderDialogSelectCategories;
    }

    //==============================================================================================
    // Метод для задания
    private void setBuilderDialogSelectCategories(AlertDialog.Builder builderDialogSelectCategories) {
        this.mBuilderDialogSelectCategories = builderDialogSelectCategories;
    }

    //==============================================================================================
    // Метод создания текущего диалогового окна выбора категорий одежды
    public void create() {
        this.setBuilderDialogSelectCategories(new AlertDialog.Builder(this.getContext()));

        // Устанавливаем заголовок всплывающего окна
        this.getBuilderDialogSelectCategories().setTitle(R.string.string_title_dialog_select_categories);

        LayoutInflater inflaterDialogSelectCategories = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Создаем view из dialog_dynamic_content.xml
        View viewDialogSelectCategories = inflaterDialogSelectCategories.inflate(R.layout.dialog_dynamic_content, null);

        // Формируем выпадающий список категорий
        // Результат выполнения функции формирования контента (двухуровневого списка выбора категорий одежды)
        // всплывающего окна выбора категорий одежды
        Boolean resultCreateListDressCategories = createListDressCategoriesUseDynamicContent(viewDialogSelectCategories);

        // В зависимости от результата выполнения функции формирования двухуровневого
        // списка выбора категорий отображаем всплывающее диалоговое окно с соответствующем
        // выпадающим контентом

        // Если двухуровневый список выбора категорий был успешно СФОРМИРОВАН
        if (resultCreateListDressCategories.equals(true) && viewDialogSelectCategories != null) {
            // Устанавливаем view, полученный из dialog_dynamic_content.xml, как содержимое тела диалога
            this.getBuilderDialogSelectCategories().setView(viewDialogSelectCategories);

            // Устанавливаем кнопку "OK"
            this.getBuilderDialogSelectCategories().setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Формируем массив выбранных (отмеченных галочкой) категорий одежды
                    ArrayList<HashMap<String, String>> arrayCheckedCategory = new ArrayList<>();

                    //------------------------------------------------------------------------------
                    // Проверяем категории одежды для группы категорий "Головные уборы"
                    ArrayList<RadioButton> arrayRadioButtonCategoryDressHead = DialogSelectDressCategory.this.getArrayRadioButtonCategoryDress(GlobalFlags.TAG_DRESS_HEAD);

                    if(arrayRadioButtonCategoryDressHead != null) {
                        for (int i = 0; i < arrayRadioButtonCategoryDressHead.size(); i++) {
                            if(arrayRadioButtonCategoryDressHead.get(i).isChecked() && arrayRadioButtonCategoryDressHead.get(i).getTag() != null) {
                                // Считываем id для текущего элемента RadioButton
                                HashMap<String, String> currentRadioButtonTag = (HashMap<String, String>) arrayRadioButtonCategoryDressHead.get(i).getTag();

                                // Считываем id категории из тега текущего элемента RadioButton
                                if(currentRadioButtonTag.containsKey(GlobalFlags.TAG_ID) && currentRadioButtonTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    HashMap<String, String> currentCheckedCategory = new HashMap<>();

                                    currentCheckedCategory.put(GlobalFlags.TAG_ID, currentRadioButtonTag.get(GlobalFlags.TAG_ID));
                                    currentCheckedCategory.put(GlobalFlags.TAG_TYPE, currentRadioButtonTag.get(GlobalFlags.TAG_TYPE));

                                    arrayCheckedCategory.add(currentCheckedCategory);
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Проверяем категории одежды для группы категорий "Одежда для тела"
                    ArrayList<CheckBox> arrayCheckBoxCategoryDressBody = DialogSelectDressCategory.this.getArrayCheckBoxCategoryDress(GlobalFlags.TAG_DRESS_BODY);

                    if(arrayCheckBoxCategoryDressBody != null) {
                        for (int i = 0; i < arrayCheckBoxCategoryDressBody.size(); i++) {
                            if(arrayCheckBoxCategoryDressBody.get(i).isChecked() && arrayCheckBoxCategoryDressBody.get(i).getTag() != null) {
                                // Считываем id для текущего элемента CheckBox
                                HashMap<String, String> currentCheckBoxTag = (HashMap<String, String>) arrayCheckBoxCategoryDressBody.get(i).getTag();

                                // Считываем id категории из тега текущего элемента CheckBox
                                if(currentCheckBoxTag.containsKey(GlobalFlags.TAG_ID) && currentCheckBoxTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    HashMap<String, String> currentCheckedCategory = new HashMap<>();

                                    currentCheckedCategory.put(GlobalFlags.TAG_ID, currentCheckBoxTag.get(GlobalFlags.TAG_ID));
                                    currentCheckedCategory.put(GlobalFlags.TAG_TYPE, currentCheckBoxTag.get(GlobalFlags.TAG_TYPE));

                                    arrayCheckedCategory.add(currentCheckedCategory);
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Проверяем категории одежды для группы категорий "Одежда для ног"
                    ArrayList<RadioButton> arrayRadioButtonCategoryDressLeg = DialogSelectDressCategory.this.getArrayRadioButtonCategoryDress(GlobalFlags.TAG_DRESS_LEG);

                    if(arrayRadioButtonCategoryDressLeg != null) {
                        for (int i = 0; i < arrayRadioButtonCategoryDressLeg.size(); i++) {
                            if(arrayRadioButtonCategoryDressLeg.get(i).isChecked() && arrayRadioButtonCategoryDressLeg.get(i).getTag() != null) {
                                // Считываем id для текущего элемента RadioButton
                                HashMap<String, String> currentRadioButtonTag = (HashMap<String, String>) arrayRadioButtonCategoryDressLeg.get(i).getTag();

                                // Считываем id категории из тега текущего элемента RadioButton
                                if(currentRadioButtonTag.containsKey(GlobalFlags.TAG_ID) && currentRadioButtonTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    HashMap<String, String> currentCheckedCategory = new HashMap<>();

                                    currentCheckedCategory.put(GlobalFlags.TAG_ID, currentRadioButtonTag.get(GlobalFlags.TAG_ID));
                                    currentCheckedCategory.put(GlobalFlags.TAG_TYPE, currentRadioButtonTag.get(GlobalFlags.TAG_TYPE));

                                    arrayCheckedCategory.add(currentCheckedCategory);
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Проверяем категории одежды для группы категорий "Обувь"
                    ArrayList<RadioButton> arrayRadioButtonCategoryDressFoot = DialogSelectDressCategory.this.getArrayRadioButtonCategoryDress(GlobalFlags.TAG_DRESS_FOOT);

                    if(arrayRadioButtonCategoryDressFoot != null) {
                        for (int i = 0; i < arrayRadioButtonCategoryDressFoot.size(); i++) {
                            if(arrayRadioButtonCategoryDressFoot.get(i).isChecked() && arrayRadioButtonCategoryDressFoot.get(i).getTag() != null) {
                                // Считываем id для текущего элемента RadioButton
                                HashMap<String, String> currentRadioButtonTag = (HashMap<String, String>) arrayRadioButtonCategoryDressFoot.get(i).getTag();

                                // Считываем id категории из тега текущего элемента RadioButton
                                if(currentRadioButtonTag.containsKey(GlobalFlags.TAG_ID) && currentRadioButtonTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    HashMap<String, String> currentCheckedCategory = new HashMap<>();

                                    currentCheckedCategory.put(GlobalFlags.TAG_ID, currentRadioButtonTag.get(GlobalFlags.TAG_ID));
                                    currentCheckedCategory.put(GlobalFlags.TAG_TYPE, currentRadioButtonTag.get(GlobalFlags.TAG_TYPE));

                                    arrayCheckedCategory.add(currentCheckedCategory);
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // Проверяем категории одежды для группы категорий "Аксессуары"
                    ArrayList<CheckBox> arrayCheckBoxCategoryDressAccessory = DialogSelectDressCategory.this.getArrayCheckBoxCategoryDress(GlobalFlags.TAG_DRESS_ACCESSORY);

                    if(arrayCheckBoxCategoryDressAccessory != null) {
                        for (int i = 0; i < arrayCheckBoxCategoryDressAccessory.size(); i++) {
                            if(arrayCheckBoxCategoryDressAccessory.get(i).isChecked() && arrayCheckBoxCategoryDressAccessory.get(i).getTag() != null) {
                                // Считываем id для текущего элемента CheckBox
                                HashMap<String, String> currentCheckBoxTag = (HashMap<String, String>) arrayCheckBoxCategoryDressAccessory.get(i).getTag();

                                // Считываем id категории из тега текущего элемента CheckBox
                                if(currentCheckBoxTag.containsKey(GlobalFlags.TAG_ID) && currentCheckBoxTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    HashMap<String, String> currentCheckedCategory = new HashMap<>();

                                    currentCheckedCategory.put(GlobalFlags.TAG_ID, currentCheckBoxTag.get(GlobalFlags.TAG_ID));
                                    currentCheckedCategory.put(GlobalFlags.TAG_TYPE, currentCheckBoxTag.get(GlobalFlags.TAG_TYPE));

                                    arrayCheckedCategory.add(currentCheckedCategory);
                                }
                            }
                        }
                    }

                    //------------------------------------------------------------------------------
                    // В цикле получаем информацию для одежды для отмеченных категорий
                    for(int indexCheckedCategory = 0; indexCheckedCategory < arrayCheckedCategory.size(); indexCheckedCategory++) {
                        MySQLGoToDress mySQLGoToDress = new MySQLGoToDress(DialogSelectDressCategory.this.getContext());

                        mySQLGoToDress.startGoToDress(GlobalFlags.ACTION_NO,
                                arrayCheckedCategory.get(indexCheckedCategory).get(GlobalFlags.TAG_ID),
                                arrayCheckedCategory.get(indexCheckedCategory).get(GlobalFlags.TAG_TYPE),
                                null);
                    }
                }
            });

            // Кнопка "Отмена" формируемого диалогового окна выбора категорий одежды
            this.getBuilderDialogSelectCategories().setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
        }
        // Иначе, считаем, что в ходе создания двухуровневого списка выбор категорий
        // произошла ошибка
        else {
            // Создаем view из page_error.xml
            View viewDialogSelectCategoriesError = inflaterDialogSelectCategories.inflate(R.layout.page_error, null);

            // Устанавливаем view, полученный из page_error.xml, как содержимое тела диалога
            this.getBuilderDialogSelectCategories().setView(viewDialogSelectCategoriesError);

            // Выводим соответствующее сообщение об ошибке во всплывающем окне выбора категорий одежды
            TextView textViewError = (TextView) viewDialogSelectCategoriesError.findViewById(R.id.textViewError);

            if(textViewError != null) {
                textViewError.setText(R.string.string_no_dress_categories);
            }

            // Устанавливаем нейтральную кнопку "Закрыть"
            this.getBuilderDialogSelectCategories().setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
        }

        // Создаем диалоговое окно выбора категорий одежды
        this.getBuilderDialogSelectCategories().create();
    }

    //==============================================================================================
    // Метод отображения созданного текущего диалогового окна выбора категорий одежды
    public void show() {
        try {
            // Отображаем диалоговое окно выбора категорий одежды
            if(this.getBuilderDialogSelectCategories() != null) {
                this.getBuilderDialogSelectCategories().show();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error Dialog Select Dress Category Show: " + exception.toString());
        }
    }

    //==============================================================================================
    // Метод для создания двухуровневого списка категорий одежды
    // Возвращаемое значение
    // result - логическая переменная, принимающая значение true в случае успешного создания
    //          двухуровневого списка категорий одежды и значение false, соответственно, в случае
    //          НЕ успешного создания двухуровневого списка категорий одежды
    private Boolean createListDressCategoriesUseDynamicContent(View view) {
        // Считываем многомерный массив, хранящий список категорий
        HashMap<String, ArrayList<HashMap<String, String>>> listCategoriesDress = DBMain.getListCategoriesDress(GlobalFlags.getDressForWho());

        // Если многомерный массив, хранящий список категорий, пуст, то возвращаем значение false
        // в качестве результата выполнения функции
        if(listCategoriesDress == null)
            return false;
        else if(listCategoriesDress.size() == 0)
            return false;

        try {
            // В цикле перебираем все возможные группы категорий одежды
            for(int indexTagDressType = 0; indexTagDressType < GlobalFlags.getArrayTagDressType().size(); indexTagDressType++) {
                // Считываем текущий тег, определяющий типы одежды (головные уборы, обувь и т.д.)
                String CURRENT_TAG_DRESS_TYPE = GlobalFlags.getArrayTagDressType().get(indexTagDressType);

                // Если в глобальном массиве, содержащем сведения о категориях одежды,
                // присутствуют сведения о категориях для текущей группы одежды
                if(listCategoriesDress.containsKey(CURRENT_TAG_DRESS_TYPE)) {
                    // Инициализируем массив (список) элементов RadioButton или CheckBox, созданных динамически
                    // для соответствующей группы категорий
                    switch(CURRENT_TAG_DRESS_TYPE) {
                        case GlobalFlags.TAG_DRESS_HEAD:
                        case GlobalFlags.TAG_DRESS_LEG:
                        case GlobalFlags.TAG_DRESS_FOOT:
                            this.setArrayRadioButtonCategoryDress(CURRENT_TAG_DRESS_TYPE, new ArrayList<RadioButton>());
                            break;
                        case GlobalFlags.TAG_DRESS_BODY:
                        case GlobalFlags.TAG_DRESS_ACCESSORY:
                            this.setArrayCheckBoxCategoryDress(CURRENT_TAG_DRESS_TYPE, new ArrayList<CheckBox>());
                            break;
                    }

                    //------------------------------------------------------------------------------
                    // Формируем новый LinearLayout для заголовка текущей группы
                    LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayoutCurrentGroup = new LinearLayout(this.getContext());
                    linearLayoutCurrentGroup.setGravity(Gravity.CENTER_VERTICAL);
                    linearLayoutCurrentGroup.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutCurrentGroup.setClickable(true);
                    linearLayoutCurrentGroup.setBackgroundResource(R.drawable.background_expandablelistview_group);

                    // Задаем теги для текущего LinearLayout
                    HashMap<String, Object> linearLayoutCurrentGroupTag = new HashMap<>();
                    linearLayoutCurrentGroupTag.put(GlobalFlags.TAG_TYPE, CURRENT_TAG_DRESS_TYPE);                            // устанавливаем тег, определяющий тип текущей группы категорий (например, head)
                    linearLayoutCurrentGroupTag.put(GlobalFlags.TAG_GROUP_IS_EXPANDED, GlobalFlags.GROUP_IS_EXPANDED_YES);    // устанавливаем тег, определяющий, что текущая группа категорий развернута

                    linearLayoutCurrentGroup.setTag(linearLayoutCurrentGroupTag);

                    // Устанавливаем обработчик щелчка мышью по данной группе категорий
                    linearLayoutCurrentGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Получаем ссылку на родительский элемент View для текущего элемента View
                            View parentView = (View) view.getParent();

                            // Если у данного элемента задан тег
                            if (view.getTag() != null) {
                                // Считываем тег для текущей группы категорий
                                HashMap<String, Object> linearLayoutCurrentGroupTag = (HashMap<String, Object>) view.getTag();

                                // Определяем тип текущей группы категорий
                                String dressType = null;

                                if (linearLayoutCurrentGroupTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                    dressType = (String) linearLayoutCurrentGroupTag.get(GlobalFlags.TAG_TYPE);
                                }

                                // Определяем свернута или развернута текущая группа
                                int currentGroupIsExpanded = GlobalFlags.GROUP_IS_EXPANDED_YES;

                                if (linearLayoutCurrentGroupTag.containsKey(GlobalFlags.TAG_GROUP_IS_EXPANDED)) {
                                    currentGroupIsExpanded = (int) linearLayoutCurrentGroupTag.get(GlobalFlags.TAG_GROUP_IS_EXPANDED);
                                }

                                //------------------------------------------------------------------
                                // Формируем ссылку на соответстсвующий ImageView и контейнер LinearLayout
                                // дочерних элементов для текущей группы
                                ImageView imageView = null;
                                LinearLayout linearLayoutChildrens = null;

                                if(dressType != null) {
                                    switch (dressType) {
                                        case GlobalFlags.TAG_DRESS_HEAD:
                                            imageView = (ImageView) view.findViewById(R.id.imageViewCategoryDressGroupHead);
                                            linearLayoutChildrens = (LinearLayout) parentView.findViewById(R.id.linearLayoutCategoryDressGroupHeadChildrens);

                                            break;
                                        case GlobalFlags.TAG_DRESS_BODY:
                                            imageView = (ImageView) view.findViewById(R.id.imageViewCategoryDressGroupBody);
                                            linearLayoutChildrens = (LinearLayout) parentView.findViewById(R.id.linearLayoutCategoryDressGroupBodyChildrens);

                                            break;
                                        case GlobalFlags.TAG_DRESS_LEG:
                                            imageView = (ImageView) view.findViewById(R.id.imageViewCategoryDressGroupLeg);
                                            linearLayoutChildrens = (LinearLayout) parentView.findViewById(R.id.linearLayoutCategoryDressGroupLegChildrens);

                                            break;
                                        case GlobalFlags.TAG_DRESS_FOOT:
                                            imageView = (ImageView) view.findViewById(R.id.imageViewCategoryDressGroupFoot);
                                            linearLayoutChildrens = (LinearLayout) parentView.findViewById(R.id.linearLayoutCategoryDressGroupFootChildrens);

                                            break;
                                        case GlobalFlags.TAG_DRESS_ACCESSORY:
                                            imageView = (ImageView) view.findViewById(R.id.imageViewCategoryDressGroupAccessory);
                                            linearLayoutChildrens = (LinearLayout) parentView.findViewById(R.id.linearLayoutCategoryDressGroupAccessoryChildrens);

                                            break;
                                    }
                                }

                                // Если в настоящий момент группа свернута
                                if (currentGroupIsExpanded == GlobalFlags.GROUP_IS_EXPANDED_NO) {

                                    // Меняем изображение в соответствующем ImageView на стрелку вниз
                                    if (imageView != null)
                                        imageView.setImageResource(R.drawable.arrowbottom);

                                    // Отображаем контейнер LinearLayout дочерних элементов для текущей группы
                                    if (linearLayoutChildrens != null) {
                                        linearLayoutChildrens.setVisibility(LinearLayout.VISIBLE);
                                    }

                                    // Делаем отметку, что теперь данная группа развернута
                                    linearLayoutCurrentGroupTag.remove(GlobalFlags.TAG_GROUP_IS_EXPANDED);
                                    linearLayoutCurrentGroupTag.put(GlobalFlags.TAG_GROUP_IS_EXPANDED, GlobalFlags.GROUP_IS_EXPANDED_YES);

                                    view.setTag(linearLayoutCurrentGroupTag);
                                }
                                // Иначе, если в текущий момент данная группа развернута
                                else if (currentGroupIsExpanded == GlobalFlags.GROUP_IS_EXPANDED_YES) {

                                    // Скрываем контейнер LinearLayout дочерних элементов для текущей группы
                                    if (linearLayoutChildrens != null) {
                                        linearLayoutChildrens.setVisibility(LinearLayout.GONE);
                                    }

                                    // Меняем изображение в соответствующем ImageView на стрелку вправо
                                    if (imageView != null)
                                        imageView.setImageResource(R.drawable.arrowright);

                                    // Делаем отметку, что теперь данная группа свернута
                                    linearLayoutCurrentGroupTag.remove(GlobalFlags.TAG_GROUP_IS_EXPANDED);
                                    linearLayoutCurrentGroupTag.put(GlobalFlags.TAG_GROUP_IS_EXPANDED, GlobalFlags.GROUP_IS_EXPANDED_NO);

                                    view.setTag(linearLayoutCurrentGroupTag);
                                }
                            }
                        }
                    });

                    //------------------------------------------------------------------------------
                    // Формируем ImageView для заголовка текущей группы
                    LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    ImageView imageViewCurrentGroup = new ImageView(this.getContext());
                    imageViewCurrentGroup.setImageResource(R.drawable.arrowbottom);
                    imageViewCurrentGroup.setScaleType(ImageView.ScaleType.FIT_XY);

                    //------------------------------------------------------------------------------
                    // Формируем TextView для заголовка текущей группы
                    LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (45 * GlobalFlags.DpToPx));

                    TextView textViewCurrentGroup = new TextView(this.getContext());
                    textViewCurrentGroup.setGravity(Gravity.CENTER_VERTICAL);
                    textViewCurrentGroup.setPadding((int) (8 * GlobalFlags.DpToPx), 0, 0, 0);
                    textViewCurrentGroup.setTextColor(ContextCompat.getColor(this.getContext(), R.color.color_black));
                    textViewCurrentGroup.setTypeface(null, Typeface.BOLD);
                    textViewCurrentGroup.setText(GlobalFlags.getListCategoriesDressGroupPossibly().get(CURRENT_TAG_DRESS_TYPE));

                    //------------------------------------------------------------------------------
                    // Задаем id для вновь созданных элементов View
                    switch (CURRENT_TAG_DRESS_TYPE) {
                        case GlobalFlags.TAG_DRESS_HEAD:
                            linearLayoutCurrentGroup.setId(R.id.linearLayoutCategoryDressGroupHead);
                            imageViewCurrentGroup.setId(R.id.imageViewCategoryDressGroupHead);
                            textViewCurrentGroup.setId(R.id.textViewCategoryDressGroupHead);

                            break;
                        case GlobalFlags.TAG_DRESS_BODY:
                            linearLayoutCurrentGroup.setId(R.id.linearLayoutCategoryDressGroupBody);
                            imageViewCurrentGroup.setId(R.id.imageViewCategoryDressGroupBody);
                            textViewCurrentGroup.setId(R.id.textViewCategoryDressGroupBody);

                            break;
                        case GlobalFlags.TAG_DRESS_LEG:
                            linearLayoutCurrentGroup.setId(R.id.linearLayoutCategoryDressGroupLeg);
                            imageViewCurrentGroup.setId(R.id.imageViewCategoryDressGroupLeg);
                            textViewCurrentGroup.setId(R.id.textViewCategoryDressGroupLeg);

                            break;
                        case GlobalFlags.TAG_DRESS_FOOT:
                            linearLayoutCurrentGroup.setId(R.id.linearLayoutCategoryDressGroupFoot);
                            imageViewCurrentGroup.setId(R.id.imageViewCategoryDressGroupFoot);
                            textViewCurrentGroup.setId(R.id.textViewCategoryDressGroupFoot);

                            break;
                        case GlobalFlags.TAG_DRESS_ACCESSORY:
                            linearLayoutCurrentGroup.setId(R.id.linearLayoutCategoryDressGroupAccessory);
                            imageViewCurrentGroup.setId(R.id.imageViewCategoryDressGroupAccessory);
                            textViewCurrentGroup.setId(R.id.textViewCategoryDressGroupAccessory);

                            break;
                    }

                    linearLayoutCurrentGroup.addView(imageViewCurrentGroup, imageViewParams);
                    linearLayoutCurrentGroup.addView(textViewCurrentGroup, textViewParams);

                    LinearLayout mainLinearLayoutDialogDynamicContent = (LinearLayout) view.findViewById(R.id.mainLinearLayoutDialogDynamicContent);

                    if(mainLinearLayoutDialogDynamicContent != null) {
                        mainLinearLayoutDialogDynamicContent.addView(linearLayoutCurrentGroup, linearLayoutParams);
                    }

                    //==============================================================================
                    // Теперь формируем список дочерних элементов для каждой группы

                    // Создаем контейнер для текущих дочерних элементов для текущей группы
                    LinearLayout linearLayoutCurrentGroupChildrens = new LinearLayout(this.getContext());
                    linearLayoutCurrentGroupChildrens.setGravity(Gravity.CENTER_VERTICAL);
                    linearLayoutCurrentGroupChildrens.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutCurrentGroupChildrens.setVisibility(LinearLayout.VISIBLE);

                    // Задаем id для вновь созданного элемента linearLayoutCurrentGroupChildrens
                    switch (CURRENT_TAG_DRESS_TYPE) {
                        case GlobalFlags.TAG_DRESS_HEAD:
                            linearLayoutCurrentGroupChildrens.setId(R.id.linearLayoutCategoryDressGroupHeadChildrens);
                            break;
                        case GlobalFlags.TAG_DRESS_BODY:
                            linearLayoutCurrentGroupChildrens.setId(R.id.linearLayoutCategoryDressGroupBodyChildrens);
                            break;
                        case GlobalFlags.TAG_DRESS_LEG:
                            linearLayoutCurrentGroupChildrens.setId(R.id.linearLayoutCategoryDressGroupLegChildrens);
                            break;
                        case GlobalFlags.TAG_DRESS_FOOT:
                            linearLayoutCurrentGroupChildrens.setId(R.id.linearLayoutCategoryDressGroupFootChildrens);
                            break;
                        case GlobalFlags.TAG_DRESS_ACCESSORY:
                            linearLayoutCurrentGroupChildrens.setId(R.id.linearLayoutCategoryDressGroupAccessoryChildrens);
                            break;
                    }

                    //------------------------------------------------------------------------------
                    // Массив, содержащий сведения о дочерних элементах-категориях
                    // для текущей группы категорий одежды
                    ArrayList<HashMap<String, String>> listCategoriesDressForCurrentGroup = listCategoriesDress.get(CURRENT_TAG_DRESS_TYPE);

                    //------------------------------------------------------------------------------
                    // В цикле разбираем все категории одежды для текущей группы
                    for (int indexListCategoriesDressForCurrentGroup = 0; indexListCategoriesDressForCurrentGroup < listCategoriesDressForCurrentGroup.size(); indexListCategoriesDressForCurrentGroup++) {
                        // Массив дополнительных параметров для текущей категории (элемента списка)
                        HashMap<String, String> currentChildrenTag = new HashMap<>();

                        // Извлекаем всю информацию о текущей категории
                        // id категории для текущей одежде
                        String currentDressCategoryId = "0";

                        if(listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).containsKey(GlobalFlags.TAG_ID)) {
                            currentDressCategoryId = listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).get(GlobalFlags.TAG_ID);
                        }

                        // Название текущей категории
                        String currentDressCategoryTitle = "";

                        if(listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).containsKey(GlobalFlags.TAG_TITLE)) {
                            currentDressCategoryTitle = listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).get(GlobalFlags.TAG_TITLE);
                        }

                        // Количество вещей (одежды) для текущего пользователя
                        String currentDressCategoryDressCount = "0";

                        if(listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).containsKey(GlobalFlags.TAG_DRESS_COUNT)) {
                            currentDressCategoryDressCount = listCategoriesDressForCurrentGroup.get(indexListCategoriesDressForCurrentGroup).get(GlobalFlags.TAG_DRESS_COUNT);
                        }

                        // Проверяем присутствует ли в данный момент на виртуальном манекене одежда из текущей категории
                        Boolean isCategoryDressInGlobalArray = DBMain.checkCategoryDressInGlobalArray(Integer.parseInt(currentDressCategoryId), GlobalFlags.getDressForWho(), CURRENT_TAG_DRESS_TYPE);

                        // Формируем массив дополнительных параметров (тег) для данной категории
                        currentChildrenTag.put(GlobalFlags.TAG_ID, currentDressCategoryId);         // id текущей категории
                        currentChildrenTag.put(GlobalFlags.TAG_TYPE, CURRENT_TAG_DRESS_TYPE);       // тип текущей группы категорий (головные уборы, обувь и т.д.)

                        // Формируем элемент выпадающего списка выбора категорий для текущей категории
                        LayoutInflater inflaterDialogSelectCategories = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        // Если текущая группа - это "Головные уборы", "Одежда на ноги" или "Обувь",
                        // то категории одежды будут иметь вид radiobutton
                        if (CURRENT_TAG_DRESS_TYPE.equals(GlobalFlags.TAG_DRESS_HEAD) || CURRENT_TAG_DRESS_TYPE.equals(GlobalFlags.TAG_DRESS_LEG) || CURRENT_TAG_DRESS_TYPE.equals(GlobalFlags.TAG_DRESS_FOOT)) {
                            // Формируем элемент RadioButton для текущего пункта меню
                            View viewCurrentElement = inflaterDialogSelectCategories.inflate(R.layout.expandablelistview_item_radiobutton, null);

                            // Получаем ссылку на радиокнопку
                            RadioButton radioButtonCurrentElement = (RadioButton) viewCurrentElement.findViewById(R.id.radioButton);

                            // Задаем текст и тег для текущего пункта меню
                            if(radioButtonCurrentElement != null) {
                                radioButtonCurrentElement.setText(currentDressCategoryTitle);
                                radioButtonCurrentElement.setTag(currentChildrenTag);

                                // Если количество вещей для текущей категории >0
                                // и одежда из текущей категории еще не присутствует на виртуальном манекене
                                if(Integer.parseInt(currentDressCategoryDressCount) > 0 && isCategoryDressInGlobalArray.equals(false)) {
                                    // Устанавливаем обработчик выделения данного элемента RadioButton
                                    radioButtonCurrentElement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            // Извлекаем тег для текущего элемента RadioButton
                                            if (buttonView.getTag() != null) {
                                                HashMap<String, String> currentCategoryDressTag = (HashMap<String, String>) buttonView.getTag();

                                                // Извлекаем из тега для текущего элемента RadioButton
                                                // тип одежды для текущей категории (головные уборы, обувь и т.д.)
                                                if (currentCategoryDressTag.containsKey(GlobalFlags.TAG_TYPE)) {
                                                    String currentCategoryDressType = currentCategoryDressTag.get(GlobalFlags.TAG_TYPE);

                                                    // Если данный элемент выделен, то снимаем отметки с остальных
                                                    // элементов RadioButton из этой же группы категорий одежды
                                                    ArrayList<RadioButton> arrayRadioButtonCategoryDress = DialogSelectDressCategory.this.getArrayRadioButtonCategoryDress(currentCategoryDressType);

                                                    if (isChecked && arrayRadioButtonCategoryDress != null) {
                                                        for (int indexRadioButton = 0; indexRadioButton < arrayRadioButtonCategoryDress.size(); indexRadioButton++) {
                                                            // Если перебираемый элемент RadioButton из массива
                                                            // не совпадает с текущим элементом RadioButton, то снимаем с него выделение
                                                            if (arrayRadioButtonCategoryDress.get(indexRadioButton) != buttonView) {
                                                                arrayRadioButtonCategoryDress.get(indexRadioButton).setChecked(false);
                                                            }
                                                        }

                                                        // Обновляем глобальный массив
                                                        DialogSelectDressCategory.this.setArrayRadioButtonCategoryDress(currentCategoryDressType, arrayRadioButtonCategoryDress);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    //--------------------------------------------------------------
                                    // Добавляем данный элемент RadioButton в соответствующий глобальный массив
                                    // при условии, что количество вещей для текущей категории >0
                                    ArrayList<RadioButton> arrayRadioButtonCategoryDress = this.getArrayRadioButtonCategoryDress(CURRENT_TAG_DRESS_TYPE);

                                    if(arrayRadioButtonCategoryDress != null) {
                                        arrayRadioButtonCategoryDress.add(radioButtonCurrentElement);

                                        this.setArrayRadioButtonCategoryDress(CURRENT_TAG_DRESS_TYPE, arrayRadioButtonCategoryDress);
                                    }
                                }
                                // Иначе, делаем недоступным соответствующий элемент RadioButton
                                else {
                                    radioButtonCurrentElement.setEnabled(false);
                                }

                                //------------------------------------------------------------------
                                // Получаем ссылку на TextView для вывода дополнительного сообщения
                                TextView textViewMessage = (TextView) viewCurrentElement.findViewById(R.id.textViewMessage);

                                if(textViewMessage != null) {
                                    // Если отсутствуют вещи (одежда) для текущей категории
                                    if(Integer.parseInt(currentDressCategoryDressCount) <= 0) {
                                        textViewMessage.setText(R.string.string_category_no_dress);
                                        textViewMessage.setVisibility(View.VISIBLE);
                                    }
                                    // Иначе, если одежда из текущей категории уже присутствует на виртуальном манекене
                                    else if(isCategoryDressInGlobalArray.equals(true)) {
                                        textViewMessage.setText(R.string.string_category_use);
                                        textViewMessage.setVisibility(View.VISIBLE);
                                    }
                                }

                                //------------------------------------------------------------------
                                // Получаем ссылку на элемент TextView, отображающий количество одежды для текущей категории
                                TextView textViewDressCount = (TextView) viewCurrentElement.findViewById(R.id.textViewDressCount);

                                if(textViewDressCount != null) {
                                    textViewDressCount.setText(currentDressCategoryDressCount);
                                }
                            }

                            // Добавляем текущий дочерний элемент в общий список дочерних элементов
                            linearLayoutCurrentGroupChildrens.addView(viewCurrentElement);
                        }
                        // Иначе, если категории одежды будут иметь вид checkbox
                        else {
                            // Формируем элемент CheckBox для текущего пункта меню
                            View viewCurrentElement = inflaterDialogSelectCategories.inflate(R.layout.expandablelistview_item_checkbox, null);

                            // Получаем ссылку на элемент CheckBox
                            CheckBox checkBoxCurrentElement = (CheckBox) viewCurrentElement.findViewById(R.id.checkBox);

                            // Задаем текст и тег для текущего пункта меню
                            if(checkBoxCurrentElement != null) {
                                checkBoxCurrentElement.setText(currentDressCategoryTitle);
                                checkBoxCurrentElement.setTag(currentChildrenTag);

                                // И если присутствует хотя бы одна вещь (одежда) для текущей категории
                                // и одежда из текущей категории еще не присутствует на виртуальном манекене
                                if(Integer.parseInt(currentDressCategoryDressCount) > 0 && isCategoryDressInGlobalArray.equals(false)) {
                                    // Если текущая группа "Одежда для тела", то создаем для каждого элемента
                                    // из текущей группы обработчик выделения, запрещающий выделять более 3-х
                                    // пунктов (категорий одежды) одновременно
                                    // А также создаем для него id, равным его порядковому номеру в текущей группе
                                    if (CURRENT_TAG_DRESS_TYPE.equals(GlobalFlags.TAG_DRESS_BODY)) {
                                        // Обработчик выделения
                                        checkBoxCurrentElement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                // Если текущий элемент checkbox переходит в состояние "отмечен"
                                                if (isChecked) {
                                                    // Увеличиваем общее количество отмеченных элементов checkbox в группе "Для тела" на +1
                                                    ++DialogSelectDressCategory.this.mCountCheckBoxCategoryDressBodyChecked;

                                                    // Если общее количество отмеченных элементов checkbox достигло числа 3,
                                                    // то делаем остальные элементы checkbox в группе "Для тела" делаем недоступными
                                                    ArrayList<CheckBox> arrayCheckBoxCategoryDressBody = DialogSelectDressCategory.this.getArrayCheckBoxCategoryDressBody();

                                                    if (arrayCheckBoxCategoryDressBody != null && DialogSelectDressCategory.this.getCountCheckBoxCategoryDressBodyChecked() >= 3) {
                                                        // В цикле перебираем все дочерние элементы checkbox для группы "Одежда для тела"
                                                        for (int indexCheckBox = 0; indexCheckBox < arrayCheckBoxCategoryDressBody.size(); indexCheckBox++) {
                                                            // Если перебираемый элемент CheckBox из массива не отмечен, то делаем его НЕДОСТУПНЫМ
                                                            if (!arrayCheckBoxCategoryDressBody.get(indexCheckBox).isChecked()) {
                                                                arrayCheckBoxCategoryDressBody.get(indexCheckBox).setEnabled(false);
                                                            }
                                                        }

                                                        // Обновляем глобальный массив
                                                        DialogSelectDressCategory.this.setArrayCheckBoxCategoryDressBody(arrayCheckBoxCategoryDressBody);

                                                        // Выводим сообщение, что превышен лимит в 3 отмеченных checkbox'а
                                                        Toast toastCheckedDressBodyMoreThanThree = Toast.makeText(DialogSelectDressCategory.this.getContext(), R.string.string_checked_dress_body_more_than_three, Toast.LENGTH_LONG);
                                                        toastCheckedDressBodyMoreThanThree.setGravity(Gravity.CENTER, 0, 0);
                                                        toastCheckedDressBodyMoreThanThree.show();
                                                    }
                                                }
                                                // Иначе, если текущий элемент checkbox переходит в состояние "отмечен"
                                                else {
                                                    // Если количество отмеченных элементов checkbox все еще больше 0
                                                    if (DialogSelectDressCategory.this.getCountCheckBoxCategoryDressBodyChecked() > 0) {
                                                        // Уменьшаем общее количество отмеченных элементов checkbox в группе "Для тела" на -1
                                                        --DialogSelectDressCategory.this.mCountCheckBoxCategoryDressBodyChecked;

                                                        // Если общее количество отмеченных элементов checkbox стало меньше числа 3,
                                                        // то делаем все элементы checkbox в группе "Для тела" ДОСТУПНЫМИ
                                                        ArrayList<CheckBox> arrayCheckBoxCategoryDressBody = DialogSelectDressCategory.this.getArrayCheckBoxCategoryDressBody();

                                                        if (arrayCheckBoxCategoryDressBody != null && DialogSelectDressCategory.this.getCountCheckBoxCategoryDressBodyChecked() < 3) {
                                                            // В цикле перебираем все дочерние элементы checkbox для группы "Одежда для тела"
                                                            for (int indexCheckBox = 0; indexCheckBox < arrayCheckBoxCategoryDressBody.size(); indexCheckBox++) {
                                                                // Делаем все элементы CheckBox из массива ДОСТУПНЫМИ
                                                                arrayCheckBoxCategoryDressBody.get(indexCheckBox).setEnabled(true);
                                                            }

                                                            // Обновляем глобальный массив
                                                            DialogSelectDressCategory.this.setArrayCheckBoxCategoryDressBody(arrayCheckBoxCategoryDressBody);
                                                        }
                                                    }
                                                }
                                            }
                                        });

                                        // Добавляем данный элемент CheckBox в соответствующий глобальный массив
                                        // при условии, что количество вещей для текущей категории >0
                                        if (this.getArrayCheckBoxCategoryDressBody() == null) {
                                            this.setArrayCheckBoxCategoryDressBody(new ArrayList<CheckBox>());
                                        }

                                        this.getArrayCheckBoxCategoryDressBody().add(checkBoxCurrentElement);
                                    }
                                }
                                // Иначе, делаем недоступным соответствующий элемент RadioButton
                                else {
                                    checkBoxCurrentElement.setEnabled(false);
                                }

                                //------------------------------------------------------------------
                                // Получаем ссылку на TextView для вывода дополнительного сообщения
                                TextView textViewMessage = (TextView) viewCurrentElement.findViewById(R.id.textViewMessage);

                                if (textViewMessage != null) {
                                    // Если отсутствуют вещи (одежда) для текущей категории
                                    if (Integer.parseInt(currentDressCategoryDressCount) <= 0) {
                                        textViewMessage.setText(R.string.string_category_no_dress);
                                        textViewMessage.setVisibility(View.VISIBLE);
                                    }
                                    // Иначе, если одежда из текущей категории уже присутствует на виртуальном манекене
                                    else if(isCategoryDressInGlobalArray.equals(true)) {
                                        textViewMessage.setText(R.string.string_category_use);
                                        textViewMessage.setVisibility(View.VISIBLE);
                                    }
                                }

                                //------------------------------------------------------------------
                                // Получаем ссылку на элемент TextView, отображающий количество одежды для текущей категории
                                TextView textViewDressCount = (TextView) viewCurrentElement.findViewById(R.id.textViewDressCount);

                                if (textViewDressCount != null) {
                                    textViewDressCount.setText(currentDressCategoryDressCount);
                                }
                            }

                            // Добавляем текущий дочерний элемент в общий список дочерних элементов
                            linearLayoutCurrentGroupChildrens.addView(viewCurrentElement);
                        }
                    }

                    if (mainLinearLayoutDialogDynamicContent == null)
                        mainLinearLayoutDialogDynamicContent = (LinearLayout) view.findViewById(R.id.mainLinearLayoutDialogDynamicContent);

                    mainLinearLayoutDialogDynamicContent.addView(linearLayoutCurrentGroupChildrens, linearLayoutParams);
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
            FunctionsLog.logPrint("Error (createListDressCategoriesUseDynamicContent): " + exception.toString());

            return false;
        }

        return true;
    }
}
