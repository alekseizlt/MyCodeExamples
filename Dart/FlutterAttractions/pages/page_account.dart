import 'package:flutter/material.dart';
import '../classes/global_variables.dart';

class PageAccount extends StatefulWidget {
  const PageAccount({Key? key}) : super(key: key);

  // StatefulWidget должен возвращать класс, которые наследуется от State
  @override
  State<PageAccount> createState() => _PageAccountState();
}

class _PageAccountState extends State<PageAccount> {
  // Параметр key со значение _formKey – данная константа позволит обращаться
  // из дочерних элементов к функционалу формы для проверки данных,
  // сохранения или сброса значений.
  final _formKey = GlobalKey<FormState>();
  TypeGender _gender = TypeGender.male;
  bool _agreement = false;

  //============================================================================
  // функция build, строит иерархию виджетов
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text(GlobalVariables.titleAccount)),
        body: Container(
          padding: const EdgeInsets.all(10.0),
          child: SingleChildScrollView(
            child: Form(
              key: _formKey,
              child: Column(
                children: <Widget>[
                  const Text("Имя пользователя:", style: TextStyle(fontSize: 20.0)),
                  TextFormField(validator: (String? value) {
                    if (value == null || value.isEmpty) {
                      return "Пожалуйста, введите имя пользователя";
                    }
                    return null;
                  }),
                  const SizedBox(height: 20.0),

                  const Text("Пароль пользователя:", style: TextStyle(fontSize: 20.0)),
                  TextFormField(validator: (String? value) {
                    if (value == null || value.isEmpty) {
                      return "Пожалуйста, введите пароль пользователя";
                    }
                    return null;
                  }),
                  const SizedBox(height: 20.0),

                  const Text("Контактный E-mail:", style: TextStyle(fontSize: 20.0)),
                  TextFormField(validator: (String? value) {
                    if (value == null || value.isEmpty) {
                      return "Пожалуйста введите свой Email";
                    }

                    String patternMail = "[a-zA-Z0-9+.\\_\\%-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+";
                    RegExp regExp = RegExp(patternMail);

                    if (regExp.hasMatch(value)) {
                      return null;
                    }

                    return "Это не E-mail";
                  }),
                  const SizedBox(height: 20.0),

                  const Text("Ваш пол:", style: TextStyle(fontSize: 20.0),),
                  RadioListTile(
                    title: const Text("Мужской"),
                    value: TypeGender.male,
                    groupValue: _gender,
                    onChanged: onChangedGender,
                  ),
                  RadioListTile(
                    title: const Text("Женский"),
                    value: TypeGender.female,
                    groupValue: _gender,
                    onChanged: onChangedGender,
                  ),
                  const SizedBox(height: 20.0),

                  ElevatedButton(
                    child: const Text("Вход"),
                    onPressed: () {
                      if(_formKey.currentState!.validate()) {
                        String messageText;
                        Color messageColor = Colors.red;

                        if(_agreement == false) {
                          messageText = "Необходимо принять условия соглашения";
                        }
                        else {
                          messageText = "Форма успешно заполнена";
                          messageColor = Colors.green;
                        }

                        ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text(messageText), backgroundColor: messageColor)
                        );
                      }
                    }
                  ),
                  const SizedBox(height: 20.0),

                  CheckboxListTile(
                      value: _agreement,
                      title: Text("Я ознакомлен" + (_gender == TypeGender.male ? '' : 'а') +
                                  ' с документом "Согласие на обработку персональных данных" ' +
                                  'и даю согласие на обработку моих персональных данных ' +
                                  'в соответствии с требованиями ' +
                                  '"Федерального закона О персональных данных № 152-ФЗ".'
                             ),
                      onChanged: (bool? value) => setState(() {
                        value = value ?? false;
                        _agreement = value!;
                      })
                  ),
                ]
              )
            )
          )
        )
    );
  }

  //============================================================================
  // Метод, вызываемый при задании пола пользователя
  void onChangedGender(TypeGender? paramTypeGender) {
    setState(() {
      paramTypeGender = paramTypeGender ?? TypeGender.male;
      _gender = paramTypeGender!;
    });
  }
}
