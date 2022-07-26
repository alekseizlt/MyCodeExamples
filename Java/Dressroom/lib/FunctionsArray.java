package ru.alexprogs.dressroom.lib;

import java.util.ArrayList;
import java.util.HashMap;

// Класс, содержащий функции для работы с массивами
public class FunctionsArray {

    //==============================================================================================
    // Функция для определения минимального значения в массиве
    // Передаваемые параметры
    // array - массив, в котором необходимо найти минимальное значение
    public static Double getMinValueFromArray(ArrayList<Double> array) {
        if(array == null) {
            return 0.0;
        }

        // Переменная, содержащая искомое минимальное значение
        // Сохраняем в данной переменной значение первого элемента из массива
        Double minValueFromArray = array.get(0);

        // В цикле перебираем все элементы массива array
        for (int indexItem = 0; indexItem < array.size(); indexItem++) {
            if(array.get(indexItem) < minValueFromArray) {
                minValueFromArray = array.get(indexItem);
            }
        }

        return minValueFromArray;
    }

    //==============================================================================================
    // Функция для определения минимального значения в массиве
    // Передаваемые параметры
    // array - массив, в котором необходимо найти минимальное значение
    // key - ключ, по которому в массиве array необходимо найти минимальное значение
    public static Double getMinValueFromArray(ArrayList<HashMap<String, String>> array, String key) {
        if(array == null || key == null) {
            return 0.0;
        }

        // Переменная, содержащая искомое минимальное значение
        // Сохраняем в данной переменной значение первого элемента из массива
        Double minValueFromArray = Double.valueOf(array.get(0).get(key));

        // В цикле перебираем все элементы массива array
        for (int indexItem = 0; indexItem < array.size(); indexItem++) {
            if(array.get(indexItem) != null) {
                if(array.get(indexItem).containsKey(key)) {
                    if(array.get(indexItem).get(key) != null) {
                        Double currentItemValue = Double.valueOf(array.get(indexItem).get(key));

                        if(currentItemValue < minValueFromArray) {
                            minValueFromArray = currentItemValue;
                        }
                    }
                }
            }
        }

        return minValueFromArray;
    }

    //==============================================================================================
    // Функция для определения максимального значения в массиве
    // Передаваемые параметры
    // array - массив, в котором необходимо найти максимальное значение
    public static Double getMaxValueFromArray(ArrayList<Double> array) {
        if(array == null) {
            return 0.0;
        }

        // Переменная, содержащая искомое максимальное значение
        Double maxValueFromArray = 0.0;

        // В цикле перебираем все элементы массива array
        for (int indexItem = 0; indexItem < array.size(); indexItem++) {
            if(array.get(indexItem) > maxValueFromArray) {
                maxValueFromArray = array.get(indexItem);
            }
        }

        return maxValueFromArray;
    }

    //==============================================================================================
    // Функция для определения максимального значения в массиве
    // Передаваемые параметры
    // array - массив, в котором необходимо найти максимальное значение
    // key - ключ, по которому в массиве array необходимо найти максимальное значение
    public static Double getMaxValueFromArray(ArrayList<HashMap<String, String>> array, String key) {
        if(array == null || key == null) {
            return 0.0;
        }

        // Переменная, содержащая искомое максимальное значение
        Double maxValueFromArray = 0.0;

        // В цикле перебираем все элементы массива array
        for (int indexItem = 0; indexItem < array.size(); indexItem++) {
            if(array.get(indexItem) != null) {
                if(array.get(indexItem).containsKey(key)) {
                    if(array.get(indexItem).get(key) != null) {
                        Double currentItemValue = Double.valueOf(array.get(indexItem).get(key));

                        if(currentItemValue > maxValueFromArray) {
                            maxValueFromArray = currentItemValue;
                        }
                    }
                }
            }
        }

        return maxValueFromArray;
    }
}
