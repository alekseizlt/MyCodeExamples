package ru.alexprogs.dressroom.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Collection;
import java.util.HashMap;

import ru.alexprogs.dressroom.R;

/**
* Статический класс, содержащий различные функции для работы со строками
*/
public class FunctionsString {

    //==============================================================================================
    // Функция преобразования слов с русскими буквами (киррилицей) в имя записанное по-русски но английскими буквами
    public static String transformationNameRusToEngrus( String input_rus_name, Boolean need_reverse_transformation )
    {
        // Параметр $need_reverse_transformation говорит о том, будет ли необходимость обратного пребразования имени в русское

        String temp = input_rus_name;
        String letter = "";
        String output = "";

        // Побуквенно разбираем данное русское имя
        for( int i = 0; i < temp.length(); i++ )
        {
            //--------------------------------------------------------------------------------------------------------------
            //Маленькие буквы
            if(temp.charAt(i) == 'а' && getCharCode(temp.charAt(i)) == 1072)		letter = "a";
            else if(temp.charAt(i) == 'б' && getCharCode(temp.charAt(i)) == 1073)	letter = "b";
            else if(temp.charAt(i) == 'в' && getCharCode(temp.charAt(i)) == 1074)	letter = "v";
            else if(temp.charAt(i) == 'г' && getCharCode(temp.charAt(i)) == 1075)	letter = "g";
            else if(temp.charAt(i) == 'д' && getCharCode(temp.charAt(i)) == 1076)	letter = "d";
            else if(temp.charAt(i) == 'е' && getCharCode(temp.charAt(i)) == 1077)	letter = "ye";
            else if(temp.charAt(i) == 'ж' && getCharCode(temp.charAt(i)) == 1078)	letter = "zh";
            else if(temp.charAt(i) == 'з' && getCharCode(temp.charAt(i)) == 1079)	letter = "z";
            else if(temp.charAt(i) == 'и' && getCharCode(temp.charAt(i)) == 1080)	letter = "i";
            else if(temp.charAt(i) == 'й' && getCharCode(temp.charAt(i)) == 1081)	letter = "jy";
            else if(temp.charAt(i) == 'к' && getCharCode(temp.charAt(i)) == 1082)	letter = "k";
            else if(temp.charAt(i) == 'л' && getCharCode(temp.charAt(i)) == 1083)	letter = "l";
            else if(temp.charAt(i) == 'м' && getCharCode(temp.charAt(i)) == 1084)	letter = "m";
            else if(temp.charAt(i) == 'н' && getCharCode(temp.charAt(i)) == 1085)	letter = "n";
            else if(temp.charAt(i) == 'о' && getCharCode(temp.charAt(i)) == 1086)	letter = "o";
            else if(temp.charAt(i) == 'п' && getCharCode(temp.charAt(i)) == 1087)	letter = "p";
            else if(temp.charAt(i) == 'р' && getCharCode(temp.charAt(i)) == 1088)	letter = "r";
            else if(temp.charAt(i) == 'с' && getCharCode(temp.charAt(i)) == 1089)	letter = "s";
            else if(temp.charAt(i) == 'т' && getCharCode(temp.charAt(i)) == 1090)	letter = "t";
            else if(temp.charAt(i) == 'у' && getCharCode(temp.charAt(i)) == 1091)	letter = "u";
            else if(temp.charAt(i) == 'ф' && getCharCode(temp.charAt(i)) == 1092)	letter = "f";
            else if(temp.charAt(i) == 'х' && getCharCode(temp.charAt(i)) == 1093)	letter = "kh";
            else if(temp.charAt(i) == 'ц' && getCharCode(temp.charAt(i)) == 1094)	letter = "ts";
            else if(temp.charAt(i) == 'ч' && getCharCode(temp.charAt(i)) == 1095)	letter = "ch";
            else if(temp.charAt(i) == 'ш' && getCharCode(temp.charAt(i)) == 1096)	letter = "sh";
            else if(temp.charAt(i) == 'щ' && getCharCode(temp.charAt(i)) == 1097)	letter = "shch";
            else if(temp.charAt(i) == 'ъ' && getCharCode(temp.charAt(i)) == 1098)	letter = "jt";
            else if(temp.charAt(i) == 'ы' && getCharCode(temp.charAt(i)) == 1099)	letter = "y";
            else if(temp.charAt(i) == 'ь' && getCharCode(temp.charAt(i)) == 1100)	letter = "jm";
            else if(temp.charAt(i) == 'э' && getCharCode(temp.charAt(i)) == 1101)	letter = "e";
            else if(temp.charAt(i) == 'ю' && getCharCode(temp.charAt(i)) == 1102)	letter = "yu";
            else if(temp.charAt(i) == 'я' && getCharCode(temp.charAt(i)) == 1103)	letter = "ya";

                //--------------------------------------------------------------------------------------------------------------
                //Заглавные буквы
            else if(temp.charAt(i) == 'А' && getCharCode(temp.charAt(i)) == 1040)	letter = "A";
            else if(temp.charAt(i) == 'Б' && getCharCode(temp.charAt(i)) == 1041)	letter = "B";
            else if(temp.charAt(i) == 'В' && getCharCode(temp.charAt(i)) == 1042)	letter = "V";
            else if(temp.charAt(i) == 'Г' && getCharCode(temp.charAt(i)) == 1043)	letter = "G";
            else if(temp.charAt(i) == 'Д' && getCharCode(temp.charAt(i)) == 1044)	letter = "D";
            else if(temp.charAt(i) == 'Е' && getCharCode(temp.charAt(i)) == 1045)	letter = "YE";
            else if(temp.charAt(i) == 'Ж' && getCharCode(temp.charAt(i)) == 1046)	letter = "ZH";
            else if(temp.charAt(i) == 'З' && getCharCode(temp.charAt(i)) == 1047)	letter = "Z";
            else if(temp.charAt(i) == 'И' && getCharCode(temp.charAt(i)) == 1048)	letter = "I";
            else if(temp.charAt(i) == 'Й' && getCharCode(temp.charAt(i)) == 1049)	letter = "JY";
            else if(temp.charAt(i) == 'К' && getCharCode(temp.charAt(i)) == 1050)	letter = "K";
            else if(temp.charAt(i) == 'Л' && getCharCode(temp.charAt(i)) == 1051)	letter = "L";
            else if(temp.charAt(i) == 'М' && getCharCode(temp.charAt(i)) == 1052)	letter = "M";
            else if(temp.charAt(i) == 'Н' && getCharCode(temp.charAt(i)) == 1053)	letter = "N";
            else if(temp.charAt(i) == 'О' && getCharCode(temp.charAt(i)) == 1054)	letter = "O";
            else if(temp.charAt(i) == 'П' && getCharCode(temp.charAt(i)) == 1055)	letter = "P";
            else if(temp.charAt(i) == 'Р' && getCharCode(temp.charAt(i)) == 1056)	letter = "R";
            else if(temp.charAt(i) == 'С' && getCharCode(temp.charAt(i)) == 1057)	letter = "S";
            else if(temp.charAt(i) == 'Т' && getCharCode(temp.charAt(i)) == 1058)	letter = "T";
            else if(temp.charAt(i) == 'У' && getCharCode(temp.charAt(i)) == 1059)	letter = "U";
            else if(temp.charAt(i) == 'Ф' && getCharCode(temp.charAt(i)) == 1060)	letter = "F";
            else if(temp.charAt(i) == 'Х' && getCharCode(temp.charAt(i)) == 1061)	letter = "KH";
            else if(temp.charAt(i) == 'Ц' && getCharCode(temp.charAt(i)) == 1062)	letter = "TS";
            else if(temp.charAt(i) == 'Ч' && getCharCode(temp.charAt(i)) == 1063)	letter = "CH";
            else if(temp.charAt(i) == 'Ш' && getCharCode(temp.charAt(i)) == 1064)	letter = "SH";
            else if(temp.charAt(i) == 'Щ' && getCharCode(temp.charAt(i)) == 1065)	letter = "SHCH";
            else if(temp.charAt(i) == 'Ъ' && getCharCode(temp.charAt(i)) == 1066)	letter = "JT";
            else if(temp.charAt(i) == 'Ы' && getCharCode(temp.charAt(i)) == 1067)	letter = "Y";
            else if(temp.charAt(i) == 'Ь' && getCharCode(temp.charAt(i)) == 1068)	letter = "JM";
            else if(temp.charAt(i) == 'Э' && getCharCode(temp.charAt(i)) == 1069)	letter = "E";
            else if(temp.charAt(i) == 'Ю' && getCharCode(temp.charAt(i)) == 1070)	letter = "YU";
            else if(temp.charAt(i) == 'Я' && getCharCode(temp.charAt(i)) == 1071)	letter = "YA";
            else if(getCharCode(temp.charAt(i)) == 32)				letter = "_";			// символ пробела
                // символы вопроса, восклицательный знак, знакт препинания
            else if( (getCharCode(temp.charAt(i)) > 32 && getCharCode(temp.charAt(i)) < 45) || (getCharCode(temp.charAt(i)) > 57 && getCharCode(temp.charAt(i)) < 65) || (getCharCode(temp.charAt(i)) > 125 && getCharCode(temp.charAt(i)) < 130) || (getCharCode(temp.charAt(i)) > 131 && getCharCode(temp.charAt(i)) < 150) || (getCharCode(temp.charAt(i)) > 151 && getCharCode(temp.charAt(i)) < 192) )
                letter = "";
            else
                letter = String.valueOf(temp.charAt(i));

            // Добавляем преобразованный символ к возвращаемому имени
            output = output + letter;
        }

        return output;
    }

    //=======================================================================================================================
    // Функция преобразования слов, написанных по-русски но английскими буквами, в слова, написанные русскими буквами (киррилицей)
    public static String transformationNameEngrusToRus( String input_engrus_name )
    {
        String temp = input_engrus_name;
        String letter = "";
        String output = "";

        // Побуквенно разбираем данное русско-английское имя
        for( int i = 0; i < temp.length(); i++ )
        {
            //--------------------------------------------------------------------------------------------------------------
            // Маленькие буквы
            if(temp.charAt(i) == 'a' && getCharCode(temp.charAt(i)) == 97)		    letter = "а";
            else if(temp.charAt(i) == 'b' && getCharCode(temp.charAt(i)) == 98)	    letter = "б";
            else if(temp.charAt(i) == 'v' && getCharCode(temp.charAt(i)) == 118)	letter = "в";
            else if(temp.charAt(i) == 'g' && getCharCode(temp.charAt(i)) == 103)	letter = "г";
            else if(temp.charAt(i) == 'd' && getCharCode(temp.charAt(i)) == 100)	letter = "д";
            else if(temp.charAt(i) == 'y' && getCharCode(temp.charAt(i)) == 121)
            {
                // Проверяем не последний ли это символ в имени
                if(i < temp.length() - 1)
                {
                    if(temp.charAt(i + 1) == 'e' && getCharCode(temp.charAt(i + 1)) == 101)
                    {
                        letter = "е";
                        i++;
                    }
                    else if(temp.charAt(i + 1) == 'u' && getCharCode(temp.charAt(i + 1)) == 117)
                    {
                        letter = "ю";
                        i++;
                    }
                    else if(temp.charAt(i + 1) == 'a' && getCharCode(temp.charAt(i + 1)) == 97)
                    {
                        letter = "я";
                        i++;
                    }
                    else
                        letter = "ы";
                }
                else
                    letter = "ы";
            }
            else if(temp.charAt(i) == 'z' && getCharCode(temp.charAt(i)) == 122)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'h' && getCharCode(temp.charAt(i + 1)) == 104 )
                {
                    letter = "ж";
                    i++;
                }
                else
                    letter = "з";
            }
            else if(temp.charAt(i) == 'i' && getCharCode(temp.charAt(i)) == 105)	letter = "и";
            else if(temp.charAt(i) == 'k' && getCharCode(temp.charAt(i)) == 107)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'h' && getCharCode(temp.charAt(i + 1)) == 104 )
                {
                    letter = "х";
                    i++;
                }
                else
                    letter = "к";
            }
            else if(temp.charAt(i) == 'l' && getCharCode(temp.charAt(i)) == 108)	letter = "л";
            else if(temp.charAt(i) == 'm' && getCharCode(temp.charAt(i)) == 109)	letter = "м";
            else if(temp.charAt(i) == 'n' && getCharCode(temp.charAt(i)) == 110)	letter = "н";
            else if(temp.charAt(i) == 'o' && getCharCode(temp.charAt(i)) == 111)	letter = "о";
            else if(temp.charAt(i) == 'p' && getCharCode(temp.charAt(i)) == 112)	letter = "п";
            else if(temp.charAt(i) == 'r' && getCharCode(temp.charAt(i)) == 114)	letter = "р";
            else if(temp.charAt(i) == 's' && getCharCode(temp.charAt(i)) == 115)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'h' && getCharCode(temp.charAt(i + 1)) == 104 )
                {
                    if( (i < temp.length() - 2) && temp.charAt(i + 2) == 'c' && getCharCode(temp.charAt(i + 2)) == 99 )
                    {
                        if( (i < temp.length() - 3) && temp.charAt(i + 3) == 'h' && getCharCode(temp.charAt(i + 3)) == 104 )
                        {
                            letter = "щ";
                            i += 3;
                        }
                    }
                    else
                    {
                        letter = "ш";
                        i++;
                    }
                }
                else
                    letter = "с";
            }
            else if(temp.charAt(i) == 't' && getCharCode(temp.charAt(i)) == 116)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 's' && getCharCode(temp.charAt(i + 1)) == 115 )
                {
                    letter = "ц";
                    i++;
                }
                else
                    letter = "т";
            }
            else if(temp.charAt(i) == 'u' && getCharCode(temp.charAt(i)) == 117)	letter = "у";
            else if(temp.charAt(i) == 'f' && getCharCode(temp.charAt(i)) == 102)	letter = "ф";
            else if(temp.charAt(i) == 'c' && getCharCode(temp.charAt(i)) == 99)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'h' && getCharCode(temp.charAt(i + 1)) == 104 )
                {
                    letter = "ч";
                    i++;
                }
            }
            else if(temp.charAt(i) == 'j' && getCharCode(temp.charAt(i)) == 106)
            {
                if( (i < temp.length() - 1) &&  temp.charAt(i + 1) == 't' && getCharCode(temp.charAt(i + 1)) == 116 )
                {
                    letter = "ъ";
                    i++;
                }
                else if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'm' && getCharCode(temp.charAt(i + 1)) == 109 )
                {
                    letter = "ь";
                    i++;
                }
                else if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'y' && getCharCode(temp.charAt(i + 1)) == 121 )
                {
                    letter = "й";
                    i++;
                }
            }
            else if(temp.charAt(i) == 'e' && getCharCode(temp.charAt(i)) == 101)	letter = "э";

            //--------------------------------------------------------------------------------------------------------------
            // Заглавные буквы
            else if(temp.charAt(i) == 'A' && getCharCode(temp.charAt(i)) == 65)	    letter = "А";
            else if(temp.charAt(i) == 'B' && getCharCode(temp.charAt(i)) == 66)	    letter = "Б";
            else if(temp.charAt(i) == 'V' && getCharCode(temp.charAt(i)) == 86)	    letter = "В";
            else if(temp.charAt(i) == 'G' && getCharCode(temp.charAt(i)) == 71)	    letter = "Г";
            else if(temp.charAt(i) == 'D' && getCharCode(temp.charAt(i)) == 68)	    letter = "Д";
            else if(temp.charAt(i) == 'Y' && getCharCode(temp.charAt(i)) == 89)
            {
                if(i < temp.length() - 1)
                {
                    if(temp.charAt(i + 1) == 'E' && getCharCode(temp.charAt(i + 1)) == 69)
                    {
                        letter = "Е";
                        i++;
                    }
                    else if(temp.charAt(i + 1) == 'U' && getCharCode(temp.charAt(i + 1)) == 85)
                    {
                        letter = "Ю";
                        i++;
                    }
                    else if(temp.charAt(i + 1) == 'A' && getCharCode(temp.charAt(i + 1)) == 65)
                    {
                        letter = "Я";
                        i++;
                    }
                    else
                        letter = "Ы";
                }
                else
                    letter = "Ы";
            }
            else if(temp.charAt(i) == 'Z' && getCharCode(temp.charAt(i)) == 90)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'H' && getCharCode(temp.charAt(i + 1)) == 72 )
                {
                    letter = "Ж";
                    i++;
                }
                else
                    letter = "З";
            }
            else if(temp.charAt(i) == 'I' && getCharCode(temp.charAt(i)) == 73)	    letter = "И";
            else if(temp.charAt(i) == 'K' && getCharCode(temp.charAt(i)) == 75)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'H' && getCharCode(temp.charAt(i + 1)) == 72 )
                {
                    letter = "Х";
                    i++;
                }
                else
                    letter = "К";
            }
            else if(temp.charAt(i) == 'L' && getCharCode(temp.charAt(i)) == 76)	    letter = "Л";
            else if(temp.charAt(i) == 'M' && getCharCode(temp.charAt(i)) == 77)	    letter = "М";
            else if(temp.charAt(i) == 'N' && getCharCode(temp.charAt(i)) == 78)	    letter = "Н";
            else if(temp.charAt(i) == 'O' && getCharCode(temp.charAt(i)) == 79)	    letter = "О";
            else if(temp.charAt(i) == 'P' && getCharCode(temp.charAt(i)) == 80)	    letter = "П";
            else if(temp.charAt(i) == 'R' && getCharCode(temp.charAt(i)) == 82)	    letter = "Р";
            else if(temp.charAt(i) == 'S' && getCharCode(temp.charAt(i)) == 83)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'H' && getCharCode(temp.charAt(i + 1)) == 72 )
                {
                    if( (i < temp.length() - 2) && temp.charAt(i + 2) == 'C' && getCharCode(temp.charAt(i + 2)) == 67 )
                    {
                        if( (i < temp.length() - 3) && temp.charAt(i + 3) == 'H' && getCharCode(temp.charAt(i + 3)) == 72 )
                        {
                            letter = "Щ";
                            i += 3;
                        }
                    }
                    else
                    {
                        letter = "Ш";
                        i++;
                    }
                }
                else
                    letter = "С";
            }
            else if(temp.charAt(i) == 'T' && getCharCode(temp.charAt(i)) == 84)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'S' && getCharCode(temp.charAt(i + 1)) == 83 )
                {
                    letter = "Ц";
                    i++;
                }
                else
                    letter = "Т";
            }
            else if(temp.charAt(i) == 'U' && getCharCode(temp.charAt(i)) == 85)	    letter = "У";
            else if(temp.charAt(i) == 'F' && getCharCode(temp.charAt(i)) == 70)	    letter = "Ф";
            else if(temp.charAt(i) == 'C' && getCharCode(temp.charAt(i)) == 67)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'H' && getCharCode(temp.charAt(i + 1)) == 72 )
                {
                    letter = "Ч";
                    i++;
                }
            }
            else if(temp.charAt(i) == 'J' && getCharCode(temp.charAt(i)) == 74)
            {
                if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'T' && getCharCode(temp.charAt(i + 1)) == 84 )
                {
                    letter = "Ъ";
                    i++;
                }
                else if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'M' && getCharCode(temp.charAt(i + 1)) == 77 )
                {
                    letter = "Ь";
                    i++;
                }
                else if( (i < temp.length() - 1) && temp.charAt(i + 1) == 'Y' && getCharCode(temp.charAt(i + 1)) == 89 )
                {
                    letter = "Й";
                    i++;
                }
            }
            else if(temp.charAt(i) == 'E' && getCharCode(temp.charAt(i)) == 69)	    letter = "Э";

            // Добавляем преобразованный символ к возвращаемому имени
            output = output + letter;
        }

        return output;
    }

    //==============================================================================================
    // Функция получения кода символа
    public static Integer getCharCode (Character character)
    {
        Integer code = (int) character;

        return code;
    }

    //==============================================================================================
    // Функция декодирования строки полученной, как результат php-функции json_encode
    public static String jsonDecode(String jsonEncodeString) {
        HashMap<String, String> arrayJSONEncodeLetters = new HashMap<String, String> ();

        arrayJSONEncodeLetters.put("\u0430", "а");
        arrayJSONEncodeLetters.put("\u0410", "А");
        arrayJSONEncodeLetters.put("\u0431", "б");
        arrayJSONEncodeLetters.put("\u0411", "Б");
        arrayJSONEncodeLetters.put("\u0432", "в");
        arrayJSONEncodeLetters.put("\u0412", "В");
        arrayJSONEncodeLetters.put("\u0433", "г");
        arrayJSONEncodeLetters.put("\u0413", "Г");
        arrayJSONEncodeLetters.put("\u0434", "д");
        arrayJSONEncodeLetters.put("\u0414", "Д");
        arrayJSONEncodeLetters.put("\u0435", "е");
        arrayJSONEncodeLetters.put("\u0415", "Е");
        arrayJSONEncodeLetters.put("\u0451", "ё");
        arrayJSONEncodeLetters.put("\u0401", "Ё");
        arrayJSONEncodeLetters.put("\u0436", "ж");
        arrayJSONEncodeLetters.put("\u0416", "Ж");
        arrayJSONEncodeLetters.put("\u0437", "з");
        arrayJSONEncodeLetters.put("\u0417", "З");
        arrayJSONEncodeLetters.put("\u0438", "и");
        arrayJSONEncodeLetters.put("\u0418", "И");
        arrayJSONEncodeLetters.put("\u0439", "й");
        arrayJSONEncodeLetters.put("\u0419", "Й");
        arrayJSONEncodeLetters.put("\u0457", "ї");
        arrayJSONEncodeLetters.put("\u0407", "Ї");
        arrayJSONEncodeLetters.put("\u0456", "і");
        arrayJSONEncodeLetters.put("\u0406", "І");
        arrayJSONEncodeLetters.put("\u043a", "к");
        arrayJSONEncodeLetters.put("\u041a", "К");
        arrayJSONEncodeLetters.put("\u043b", "л");
        arrayJSONEncodeLetters.put("\u041b", "Л");
        arrayJSONEncodeLetters.put("\u043c", "м");
        arrayJSONEncodeLetters.put("\u041c", "М");
        arrayJSONEncodeLetters.put("\u043d", "н");
        arrayJSONEncodeLetters.put("\u041d", "Н");
        arrayJSONEncodeLetters.put("\u043e", "о");
        arrayJSONEncodeLetters.put("\u041e", "О");
        arrayJSONEncodeLetters.put("\u043f", "п");
        arrayJSONEncodeLetters.put("\u041f", "П");
        arrayJSONEncodeLetters.put("\u0440", "р");
        arrayJSONEncodeLetters.put("\u0420", "Р");
        arrayJSONEncodeLetters.put("\u0441", "с");
        arrayJSONEncodeLetters.put("\u0421", "С");
        arrayJSONEncodeLetters.put("\u0442", "т");
        arrayJSONEncodeLetters.put("\u0422", "Т");
        arrayJSONEncodeLetters.put("\u0443", "у");
        arrayJSONEncodeLetters.put("\u0423", "У");
        arrayJSONEncodeLetters.put("\u0444", "ф");
        arrayJSONEncodeLetters.put("\u0424", "Ф");
        arrayJSONEncodeLetters.put("\u0445", "х");
        arrayJSONEncodeLetters.put("\u0425", "Х");
        arrayJSONEncodeLetters.put("\u0446", "ц");
        arrayJSONEncodeLetters.put("\u0426", "Ц");
        arrayJSONEncodeLetters.put("\u0447", "ч");
        arrayJSONEncodeLetters.put("\u0427", "Ч");
        arrayJSONEncodeLetters.put("\u0448", "ш");
        arrayJSONEncodeLetters.put("\u0428", "Ш");
        arrayJSONEncodeLetters.put("\u0449", "щ");
        arrayJSONEncodeLetters.put("\u0429", "Щ");
        arrayJSONEncodeLetters.put("\u044a", "ъ");
        arrayJSONEncodeLetters.put("\u042a", "Ъ");
        arrayJSONEncodeLetters.put("\u044b", "ы");
        arrayJSONEncodeLetters.put("\u042b", "Ы");
        arrayJSONEncodeLetters.put("\u044c", "ь");
        arrayJSONEncodeLetters.put("\u042c", "Ь");
        arrayJSONEncodeLetters.put("\u044d", "э");
        arrayJSONEncodeLetters.put("\u042d", "Э");
        arrayJSONEncodeLetters.put("\u044e", "ю");
        arrayJSONEncodeLetters.put("\u042e", "Ю");
        arrayJSONEncodeLetters.put("\u044f", "я");
        arrayJSONEncodeLetters.put("\u042f", "Я");
//        arrayJSONEncodeLetters.put("\/", "/");
        arrayJSONEncodeLetters.put("__SLASH__", "/");
        arrayJSONEncodeLetters.put("\r", "");
//        arrayJSONEncodeLetters.put("\n", "<br />");
        arrayJSONEncodeLetters.put("\t", "");

        // Извлекаем набор ключей из вышеуказанного массива
        Collection<String> arrayJSONEncodeLettersKeyCollection = arrayJSONEncodeLetters.keySet();

        // Строка, возвращаемая как результат выполнения текущей функции
        String resultJsonDecodeString = jsonEncodeString;

        // В цикле разбираем строку, полученную, как результат php-функции json_encode
        for (String arrayJSONEncodeLettersKey : arrayJSONEncodeLettersKeyCollection) {
            resultJsonDecodeString = resultJsonDecodeString.replace(arrayJSONEncodeLettersKey, arrayJSONEncodeLetters.get(arrayJSONEncodeLettersKey));
        }

        return resultJsonDecodeString;
    }
}
