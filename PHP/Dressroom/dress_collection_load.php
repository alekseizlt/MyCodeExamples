<?php
	// Подключаем необходимые файлы
	require_once("include/define.php");
	require_once("db/db_connect_mysql.php");
	require_once("classes/class_dress_collection.php");

	// Массив, представляющий собой JSON-ответ на запрос
	$response = array();
	
	$response["collection"] = null;
	$response["dress"] 		= null;
	$response["user"] 		= null;
	$response["success"] 	= 0;
    $response["message"] 	= "Информация о коллекциях одежды отсутствует!";

	// Если передан параметр user_id, определяющий id пользователя, для которого необходимо считать информацию
	if (isset($_POST["user_id"])) {
		// Устанавливаем соединение с БД
		$db = new DB_CONNECT();
		$connection = $db->connect();

		// Считываем непосредственно информацию
		$tableDressCollection = new TableDressCollection();

		if($tableDressCollection != null) {
			$response = $tableDressCollection->GetDressCollection($connection, $_POST["user_id"], $_POST);
		}
	}

	echo json_encode($response);
?>