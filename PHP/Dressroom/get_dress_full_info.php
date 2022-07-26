<?php
	// Подключаем необходимые файлы
	require_once("include/define.php");
	require_once("db/db_connect_mysql.php");
	require_once("classes/class_dress.php");

	// Массив, представляющий собой JSON-ответ на запрос
	$response = array();
	
	$response["dress"] 			= null;
	$response["success"] 		= 0;
	$response["collection_id"] 	= 0;

	// Устанавливаем соединение с БД
	$db = new DB_CONNECT();
	$connection = $db->connect();

	// Считываем непосредственно информацию
	$tableDress = new TableDress();

	if($tableDress != null) {
		$response = $tableDress->GetDressFullInfo($connection, $_POST);
	}

	echo json_encode($response);
?>