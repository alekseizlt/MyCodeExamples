<?php
	// Подключаем необходимые файлы
	require_once("include/define.php");
	require_once("lib/functions.php");

	class TableDressCollection {

		//=========================================================================================
		// Функция для проверки сохранена ли текущая коллекция ранее для текущего пользователя
		// Передаваемые параметры
		// param_db 	 		 	- ссылка на подключение к БД
		// param_user_id 		 	- ID игрока, информацию для которого необходимо считать информацию
		// param_dress_collection 	- массив, содержащий дополнительные параметры
		public function CheckIsSaveCurrentCollectionForCurrentUser($param_db, $param_user_id, $param_dress_collection) {
			// Массив, представляющий собой JSON-ответ на запрос о считывании информации для текущего игрока
			$response = array();

			// Возвращаемый ответ
			$response["collection_id"] = 0;

			// Если все необходимые паарметры переданы
			if($param_db != null && $param_user_id != null && $param_dress_collection != null) {
				$user_id = intval(trim(urldecode(strval($param_user_id))));

				// Если передан параметр user_id, определяющий id пользователя, для которого необходимо считать информацию
				if($user_id > 0) {
					// Формируем массив всех возможных типов одежды
					// У данного массива ключ - тип одежды, а значение - присутствует или нет данный тип одежды в текущей коллекции
					$array_dress_types = array();
					$array_dress_types["head"] = false;
					$array_dress_types["body"] = false;
					$array_dress_types["leg"] = false;
					$array_dress_types["foot"] = false;
					$array_dress_types["accessory"] = false;

					//-----------------------------------------------------------------------------------------------
					// Список id одежды из типа "Головные уборы"
					$post_dress_id_for_head = "";

					if(isset($param_dress_collection["head"])) {
						$array_dress_types["head"] = true;
						$post_dress_id_for_head = trim(urldecode(strval($param_dress_collection["head"])));
					}

					// Список id одежды из типа "Одежда для тела"
					$post_dress_id_for_body = "";

					if(isset($param_dress_collection["body"])) {
						$array_dress_types["body"] = true;
						$post_dress_id_for_body = trim(urldecode(strval($param_dress_collection["body"])));
					}

					// Список id одежды из типа "Одежда для ног"
					$post_dress_id_for_leg = "";

					if(isset($param_dress_collection["leg"])) {
						$array_dress_types["leg"] = true;
						$post_dress_id_for_leg = trim(urldecode(strval($param_dress_collection["leg"])));
					}

					// Список id одежды из типа "Обувь"
					$post_dress_id_for_foot = "";

					if(isset($param_dress_collection["foot"])) {
						$array_dress_types["foot"] = true;
						$post_dress_id_for_foot = trim(urldecode(strval($param_dress_collection["foot"])));
					}

					// Список id одежды из типа "Аксессуары"
					$post_dress_id_for_accessory = "";

					if(isset($param_dress_collection["accessory"])) {
						$array_dress_types["accessory"] = true;
						$post_dress_id_for_accessory = trim(urldecode(strval($param_dress_collection["accessory"])));
					}

					//---------------------------------------------------------------------------------------------
					// В параметре dress_id может быть передано более одного id
					// При этом в качестве знака разделителя между id вещами используется знак тройного подчеркивания "___"
					// Формируем массив из id вещей
					$array_dress_id = array();

					// Список id одежды из типа "Головные уборы"
					if($post_dress_id_for_head != null && $post_dress_id_for_head != "" && $array_dress_types["head"] == true) {
						$array_dress_id["head"] = array();
						$array_dress_id["head"] = explode("___", $post_dress_id_for_head);
					}

					// Список id одежды из типа "Одежда для тела"
					if($post_dress_id_for_body != null && $post_dress_id_for_body != "" && $array_dress_types["body"] == true) {
						$array_dress_id["body"] = array();
						$array_dress_id["body"] = explode("___", $post_dress_id_for_body);
					}

					// Список id одежды из типа "Одежда для ног"
					if($post_dress_id_for_leg != null && $post_dress_id_for_leg != "" && $array_dress_types["leg"] == true) {
						$array_dress_id["leg"] = array();
						$array_dress_id["leg"] = explode("___", $post_dress_id_for_leg);
					}

					// Список id одежды из типа "Обувь"
					if($post_dress_id_for_foot != null && $post_dress_id_for_foot != "" && $array_dress_types["foot"] == true) {
						$array_dress_id["foot"] = array();
						$array_dress_id["foot"] = explode("___", $post_dress_id_for_foot);
					}

					// Список id одежды из типа "Аксессуары"
					if($post_dress_id_for_accessory != null && $post_dress_id_for_accessory != "" && $array_dress_types["accessory"] == true) {
						$array_dress_id["accessory"] = array();
						$array_dress_id["accessory"] = explode("___", $post_dress_id_for_accessory);
					}

					//---------------------------------------------------------------------------------------
					// Формируем общий массив, хранящий id вещей, переданных на сервер
					$array_dress_id_in_current_collection = array();

					foreach($array_dress_types as $array_dress_types_key => $array_dress_types_value) {
						if($array_dress_types_value == true) {
							for($index_dress_id = 0; $index_dress_id < count($array_dress_id[$array_dress_types_key]); $index_dress_id++) {
								array_push($array_dress_id_in_current_collection, $array_dress_id[$array_dress_types_key][$index_dress_id]);
							}
						}
					}

					//---------------------------------------------------------------------------------------
					// Проверяем сохранена ли текущая коллекция ранее для текущего пользователя

					// Считываем id всех коллекций для текущего пользователя
					$sql_select_collection_for_current_user = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

					$query_select_collection_for_current_user = mysqli_query($param_db, $sql_select_collection_for_current_user);

					if(mysqli_num_rows($query_select_collection_for_current_user) > 0) {
						$array_collection_id_for_current_user = array();

						while($row_query_select_collection_for_current_user = mysqli_fetch_array($query_select_collection_for_current_user)) {
							$array_collection_id_for_current_user[] = $row_query_select_collection_for_current_user["id"];
						}

						// Определяем количество вещей в текущем наборе одежды
						$dress_count_in_current_collection = count($array_dress_id_in_current_collection);

						// В цикле перебираем все коллекции одежды для текущего пользователя, 
						// находим те, в которой количество вещей равно dress_count_in_current_collection и
						// сравниваем id одежды, присутствующей в данной коллекции с id вещей, переданных на сервер
						for($index_collection = 0; $index_collection < count($array_collection_id_for_current_user); $index_collection++) {
							$sql_select_dress_for_current_collection = "SELECT `dress_id` FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($array_collection_id_for_current_user[$index_collection])." ORDER BY `id`";

							$query_select_dress_for_current_collection = mysqli_query($param_db, $sql_select_dress_for_current_collection);

							if(mysqli_num_rows($query_select_dress_for_current_collection) == $dress_count_in_current_collection) {
								// Массив, хранящий id вещей для данной коллекции
								$array_dress_id_in_current_collection_from_db = array();

								while($row_query_select_dress_for_current_collection = mysqli_fetch_array($query_select_dress_for_current_collection)) {
									$array_dress_id_in_current_collection_from_db[] = $row_query_select_dress_for_current_collection["dress_id"];
								}

								// Сравниваем элементы двух массивов
								$two_arrays_equals = true;

								for($i = 0; $i < count($array_dress_id_in_current_collection); $i++) {
									$is_current_value_in_both_arrays = false;

									for($j = 0; $j < count($array_dress_id_in_current_collection_from_db); $j++) {
										if($array_dress_id_in_current_collection[$i] == $array_dress_id_in_current_collection_from_db[$j]) {
											$is_current_value_in_both_arrays = true;
											break;
										}
									}

									// Если текущее значение не присутствует в обоих массивах
									if($is_current_value_in_both_arrays == false) {
										$two_arrays_equals = false;
										break;
									}
								}

								// Если два массива оказались одинаковыми
								if($two_arrays_equals == true) {
									// Сохраняем id текущего набора одежды
									$response["collection_id"] = $array_collection_id_for_current_user[$index_collection];
									break;
								}
							}
						}
					}
				}
			}

			return $response;
		}

		//=========================================================================================
		// Функция для считывания информации о наборах одежды для текущего пользователя
		// Передаваемые параметры
		// param_db 	 			- ссылка на подключение к БД
		// param_user_id 		 	- ID игрока, информацию для которого необходимо считать информацию
		// param_dress_collection 	- массив, содержащий дополнительные параметры
		public function GetDressCollection($param_db, $param_user_id, $param_dress_collection) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["collection"] = null;
			$response["dress"] 		= null;
			$response["user"] 		= null;
			$response["success"] 	= 0;
    		$response["message"] 	= "Информация о коллекциях одежды отсутствует!";

			// Массив, хранящий для кого предназначены текущие вещи
			$array_dress_for_who = null;

			// Если все необходимые паарметры переданы
			if($param_db != null && $param_user_id != null && $param_dress_collection != null) {
				$user_id = intval(trim(urldecode(strval($param_user_id))));

				// Если передан параметр user_id, определяющий id пользователя, для которого необходимо считать информацию
				if($user_id > 0) {
					// Определяем передан ли на сервер id категории, для которой необходимо считать инфо об одежде, 
					// входящей в состав наборов одежды для текущего пользователя
					$post_category_id = null;

					if(isset($param_dress_collection["catid"])) {
						if($param_dress_collection["catid"] != null) {
							$post_category_id = trim(urldecode(strval($param_dress_collection["catid"])));

							if($post_category_id == "null") {
								$post_category_id = null;
							}
						}
					}

					// Определяем количество вещей (одежды), информацию о которых необходимо одновременно скачать из БД
					$post_count_dress_read_from_db = 5;

					if(isset($param_dress_collection["count_dress_read_from_db"])) {
						if($param_dress_collection["count_dress_read_from_db"] != null) {
							$post_count_dress_read_from_db = intval(trim(urldecode(strval($param_dress_collection["count_dress_read_from_db"]))));
						}
					}

					// Если id категории, для которой необходимо считать инфо об одежде, 
					// входящей в состав наборов одежды для текущего пользователя
					if($post_category_id != null) {
						// Считываем id всех коллекций для текущего пользователя
						$sql_select_collection_for_current_user = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

						$query_select_collection_for_current_user = mysqli_query($param_db, $sql_select_collection_for_current_user);

						if(mysqli_num_rows($query_select_collection_for_current_user) > 0) {
							// Массив, хранящий id всех наборов одежды для текущего пользователя
							$array_collection_id_for_current_user = array();

							while($row_query_select_collection_for_current_user = mysqli_fetch_array($query_select_collection_for_current_user)) {
								// Сохраняем id текущего набора одежды
								array_push($array_collection_id_for_current_user, $row_query_select_collection_for_current_user["id"]);
							}

							//-------------------------------------------------------------------------------------
							// Теперь формируем массив, содержащий id всей одежды, входящей в состав всех коллекций
							// для текущего пользователя
							$array_dress_id_in_collection_for_current_user = array();

							// Перебираем в цикле все коллекции одежды для текущего пользователя
							for($index_collection = 0; $index_collection < count($array_collection_id_for_current_user); $index_collection++) {
								// Считываем id всей одежды, присутствующей в данном наборе одежды
								$sql_select_dress_for_current_collection = "SELECT `dress_id` FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($array_collection_id_for_current_user[$index_collection])." ORDER BY `id`";

								$query_select_dress_for_current_collection = mysqli_query($param_db, $sql_select_dress_for_current_collection);

								if(mysqli_num_rows($query_select_dress_for_current_collection) > 0) {
									while($row_query_select_dress_for_current_collection = mysqli_fetch_array($query_select_dress_for_current_collection)) {
										if(in_array($row_query_select_dress_for_current_collection["dress_id"], $array_dress_id_in_collection_for_current_user) == false) {
											array_push($array_dress_id_in_collection_for_current_user, $row_query_select_dress_for_current_collection["dress_id"]);
										}
									}
								}
							}

							//-------------------------------------------------------------------------------------
							// Теперь из массива, хранящего id для всей одежды для всех коллекций для текущего пользователя,
							// отфильтровываем только те вещи, которые относятся к искомой категории
							$array_dress_id_for_current_user = array();

							// Формируем запрос к БД
							$sql_dress_filter = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE ";

							for($index_dress = 0; $index_dress < count($array_dress_id_in_collection_for_current_user); $index_dress++) {
								if($index_dress == 0) {
									$sql_dress_filter .= "(";
								}

								$sql_dress_filter .= "`id`=".$array_dress_id_in_collection_for_current_user[$index_dress];

								if($index_dress < count($array_dress_id_in_collection_for_current_user) - 1) {
									$sql_dress_filter .= " OR ";
								}
								else if($index_dress == count($array_dress_id_in_collection_for_current_user) - 1) {
									$sql_dress_filter .= ")";
								}
							}

							$sql_dress_filter .= " AND `catid`=".$post_category_id." ORDER BY `id`";

							// Выполняем сформированный запрос к БД
							$query_dress_filter = mysqli_query($param_db, $sql_dress_filter);

							if(mysqli_num_rows($query_dress_filter) > 0) {
								while($row_query_dress_filter = mysqli_fetch_array($query_dress_filter)) {
									array_push($array_dress_id_for_current_user, $row_query_dress_filter["id"]);
								}
							}

							//-------------------------------------------------------------------------------------
							// Определяем количество вещей из массива $array_dress_id_for_current_user, 
							// информацию о которых необходимо считать из БД
							$last_index_dress = count($array_dress_id_for_current_user);

							if($post_count_dress_read_from_db < count($array_dress_id_for_current_user)) {
								$last_index_dress = $post_count_dress_read_from_db;
							}
				
							//-------------------------------------------------------------------------------------
							// Теперь считываем информацию о каждой одежде, id котрой представлено в массиве $array_dress_id_for_current_user
							for($index_dress = 0; $index_dress < $last_index_dress; $index_dress++) {
								// Формируем запрос для считывания информации о текущей одежде
								$sql_select_dress_info = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".strval($array_dress_id_for_current_user[$index_dress])." LIMIT 1";

								$query_select_dress_info = mysqli_query($param_db, $sql_select_dress_info);

								// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
								if(mysqli_num_rows($query_select_dress_info) > 0) {
									// Считываем всю информацию о найденной одежде
									$row_query_select_dress_info = mysqli_fetch_array($query_select_dress_info);
								
									$dress = array();
									$dress["id"] 	  	 	 = $row_query_select_dress_info["id"];										// id текущей вещи
									$dress["for_who"] 	 	 = $row_query_select_dress_info["for_who"];									// для кого предназначена текущая одежда (для мужчин, женщин или детей)
									$dress["type"] 	 	 	 = $row_query_select_dress_info["type"];									// тип текущей вещи (головной убор, обувь и т.д.)
									$dress["catid"] 	 	 = $row_query_select_dress_info["catid"];									// id категории для текущей вещи
									$dress["category_title"] = "";																		// название категории для текуще вещи
									$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_info["title"]);			// название текущей вещи
									$dress["alias"] 	 	 = $row_query_select_dress_info["alias"];									// алиас названия текущей вещи
									$dress["brand_id"] 	 	 = $row_query_select_dress_info["brand_id"];								// id бренда для текущей вещи
									$dress["brand_title"] 	 = "";																		// название бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if($row_query_select_dress_info["image"] == null) {
										$dress["image"]   		= null;
										$dress["image_width"]   = 0;
										$dress["image_height"]  = 0;
									}
									else if(trim($row_query_select_dress_info["image"]) == "") {
										$dress["image"]   		= null;
										$dress["image_width"]   = 0;
										$dress["image_height"]  = 0;
									}
									else {
										$dress["image"]   = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image"]);
										$dress["image_width"]   = $row_query_select_dress_info["image_width"];
										$dress["image_height"]  = $row_query_select_dress_info["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if($row_query_select_dress_info["image_back"] == null) {
										$dress["image_back"] 		= null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else if(trim($row_query_select_dress_info["image_back"]) == "") {
										$dress["image_back"] = null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else {
										$dress["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image_back"]);
										$dress["image_back_width"]  = $row_query_select_dress_info["image_back_width"];
										$dress["image_back_height"] = $row_query_select_dress_info["image_back_height"];
									}

									$dress['color']			= $row_query_select_dress_info["color"];												// цвет текущей вещи
									$dress['style']			= $row_query_select_dress_info["style"];												// стиль текущей вещи
									$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info["short_description"]);		// краткое описание для текущей вещи
									$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_info["description"]);			// полное описание для текущей вещи
									$dress["hits"] 			= $row_query_select_dress_info["hits"];													// уровень популярности текущей вещи
									$dress["version"] 		= $row_query_select_dress_info["version"];												// версия информации о текущей вещи
									$dress["dress_default"] = $row_query_select_dress_info["dress_default"];										// является ли текущая вещь вещью по умолчанию

									//------------------------------------------------------------------------------------------
									// Считываем название категории для текущей одежды
									$sql_select_category_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".$row_query_select_dress_info["catid"]." LIMIT 1";

									$query_select_category_info = mysqli_query($param_db, $sql_select_category_info);
		 
					 				// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
									if(mysqli_num_rows($query_select_category_info) > 0) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

										// Сохраняем название текущей категории
										$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
									}

									//------------------------------------------------------------------------------------------
									// Считываем название бренда для текущей одежды
									$sql_select_brand_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".$row_query_select_dress_info["brand_id"];

									$query_select_brand_info = mysqli_query($param_db, $sql_select_brand_info);
				 
					 				// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
									if(mysqli_num_rows($query_select_brand_info) > 0) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_brand_info = mysqli_fetch_array( $query_select_brand_info );

										// Сохраняем название текущего бренда
										$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
									}

									//------------------------------------------------------------------------------------------
									$response["success"] = 1;

									// Добавляем информацию о текущей одежде в общий массив
									if( $response["dress"] == null ) {
										$response["dress"] = array();
									}

									array_push($response["dress"], $dress);
								}
							}
						}
					}
					//------------------------------------------------------------------------------------------
					// Иначе, считываем информацию о наборах одежды по умолчанию
					else {
						// Считываем id всех коллекций для текущего пользователя
						$sql_select_collection_for_current_user = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id` LIMIT ".strval($post_count_dress_read_from_db);

						$query_select_collection_for_current_user = mysqli_query($param_db, $sql_select_collection_for_current_user);

						if(mysqli_num_rows($query_select_collection_for_current_user) > 0) {
							// Массив, хранящий id всех наборов одежды для текущего пользователя
							$array_collection_id_for_current_user = array();

							while($row_query_select_collection_for_current_user = mysqli_fetch_array($query_select_collection_for_current_user)) {
								// Сохраняем id текущего набора одежды
								array_push($array_collection_id_for_current_user, $row_query_select_collection_for_current_user["id"]);
							}

							// Теперь для каждого набора одежды считываем информацию непосредственно
							// об одежде, входящей в состав данного набора
							for($index_collection = 0; $index_collection < count($array_collection_id_for_current_user); $index_collection++) {
								// Массив, хранящий полную информацию о текущем наборе одежды
								$array_current_collection_full_info = array();

								// Считываем всю информацию о текущем наборе одежды
								$sql_select_current_collection_info = "SELECT * FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `id`=".strval($array_collection_id_for_current_user[$index_collection])." LIMIT 1";

								$query_select_current_collection_info = mysqli_query($param_db, $sql_select_current_collection_info);

								if(mysqli_num_rows($query_select_current_collection_info) > 0) {
									$row_query_select_current_collection_info = mysqli_fetch_array($query_select_current_collection_info);
				
									// Сохраняем всю информацию о текущем наборе одежды
									$array_current_collection_full_info["id"] 				 = $row_query_select_current_collection_info["id"];
									$array_current_collection_full_info["title"] 			 = iconv("cp1251", "utf-8", $row_query_select_current_collection_info["title"]);
									$array_current_collection_full_info["alias"] 			 = $row_query_select_current_collection_info["alias"];
									$array_current_collection_full_info["type"] 			 = $row_query_select_current_collection_info["type"];
									$array_current_collection_full_info["short_description"] = iconv("cp1251", "utf-8", $row_query_select_current_collection_info["short_description"]);
									$array_current_collection_full_info["description"] 		 = iconv("cp1251", "utf-8", $row_query_select_current_collection_info["description"]);
									$array_current_collection_full_info["version"] 			 = $row_query_select_current_collection_info["version"];
								}

								//------------------------------------------------------------------------------------------
								// Считываем id всей одежды, присутствующей в данном наборе одежды
								$sql_select_dress_for_current_collection = "SELECT `id`, `dress_id` FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($array_collection_id_for_current_user[$index_collection])." ORDER BY `id`";

								$query_select_dress_for_current_collection = mysqli_query($param_db, $sql_select_dress_for_current_collection);

								if(mysqli_num_rows($query_select_dress_for_current_collection) > 0) {
									// Массив, хранящий информацию обо всей одежде, входящей в состав данного набора 
									$array_dress_for_current_collection = null;

									// Массив, хранящий id записей в таблице
									$array_record_id_for_current_collection_dress = array();

									// Массив, хранящий id вещей для данной коллекции
									$array_dress_id_in_current_collection_from_db = array();

									while($row_query_select_dress_for_current_collection = mysqli_fetch_array($query_select_dress_for_current_collection)) {
										array_push($array_record_id_for_current_collection_dress, $row_query_select_dress_for_current_collection["id"]);
										array_push($array_dress_id_in_current_collection_from_db, $row_query_select_dress_for_current_collection["dress_id"]);
									}

									//-------------------------------------------------------------------------------------
									// Теперь в цикле считываем всю информацию о каждой одежде, присутствующей в данном наборе одежды
									for($index_dress_in_collection = 0; $index_dress_in_collection < count($array_dress_id_in_current_collection_from_db); $index_dress_in_collection++) {
										// Формируем запрос для считывания информации о текущей одежде
										$sql_select_dress_info = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".strval($array_dress_id_in_current_collection_from_db[$index_dress_in_collection])." LIMIT 1";

										$query_select_dress_info = mysqli_query($param_db, $sql_select_dress_info);

										// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
										if(mysqli_num_rows($query_select_dress_info) > 0) {
											// Считываем всю информацию о найденной одежде
											$row_query_select_dress_info = mysqli_fetch_array($query_select_dress_info);
								
											$dress = array();
											$dress["collection_id"]	 = $array_collection_id_for_current_user[ $index_collection ];			// id текущего набора одежды
											$dress["id"] 	  	 	 = $row_query_select_dress_info["id"];									// id текущей вещи
											$dress["for_who"] 	 	 = $row_query_select_dress_info["for_who"];								// для кого предназначена текущая одежда (для мужчин, женщин или детей)
											$dress["type"] 	 	 	 = $row_query_select_dress_info["type"];								// тип текущей вещи (головной убор, обувь и т.д.)
											$dress["catid"] 	 	 = $row_query_select_dress_info["catid"];								// id категории для текущей вещи
											$dress["category_title"] = "";																	// название категории для текуще вещи
											$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_info["title"]);		// название текущей вещи
											$dress["alias"] 	 	 = $row_query_select_dress_info["alias"];								// алиас названия текущей вещи
											$dress["brand_id"] 	 	 = $row_query_select_dress_info["brand_id"];							// id бренда для текущей вещи
											$dress["brand_title"] 	 = "";																	// название бренда для текущей вещи

											// Ссылка на изображение для текущей вещи
											if($row_query_select_dress_info["image"] == null) {
												$dress["image"]   		= null;
												$dress["image_width"]   = 0;
												$dress["image_height"]  = 0;
											}
											else if(trim($row_query_select_dress_info["image"]) == "") {
												$dress["image"]   		= null;
												$dress["image_width"]   = 0;
												$dress["image_height"]  = 0;
											}
											else {
												$dress["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image"]);
												$dress["image_width"]   = $row_query_select_dress_info["image_width"];
												$dress["image_height"]  = $row_query_select_dress_info["image_height"];
											}

											// Ссылка на изображение с обратной стороны для текущей вещи
											if($row_query_select_dress_info["image_back"] == null) {
												$dress["image_back"] 		= null;
												$dress["image_back_width"]  = 0;
												$dress["image_back_height"] = 0;
											}
											else if(trim($row_query_select_dress_info["image_back"]) == "") {
												$dress["image_back"] 		= null;
												$dress["image_back_width"]  = 0;
												$dress["image_back_height"] = 0;
											}
											else {
												$dress["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image_back"]);
												$dress["image_back_width"]  = $row_query_select_dress_info["image_back_width"];
												$dress["image_back_height"] = $row_query_select_dress_info["image_back_height"];
											}

											$dress['color']			= $row_query_select_dress_info["color"];												// цвет текущей вещи
											$dress['style']			= $row_query_select_dress_info["style"];												// стиль текущей вещи
											$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info["short_description"]);		// краткое описание для текущей вещи
											$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_info["description"]);			// полное описание для текущей вещи
											$dress["hits"] 			= $row_query_select_dress_info["hits"];													// уровень популярности текущей вещи
											$dress["version"] 		= $row_query_select_dress_info["version"];												// версия информации о текущей вещи
											$dress["dress_default"] = $row_query_select_dress_info["dress_default"];										// является ли текущая вещь вещью по умолчанию

											//------------------------------------------------------------------------------------------
											// Считываем название категории для текущей одежды
											$sql_select_category_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_info["catid"])." LIMIT 1";

											$query_select_category_info = mysqli_query($param_db, $sql_select_category_info);
					 
										 	// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
											if(mysqli_num_rows($query_select_category_info) > 0) {
												// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
												$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

												// Сохраняем название текущей категории
												$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
											}

											//------------------------------------------------------------------------------------------
											// Считываем название бренда для текущей одежды
											$sql_select_brand_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_info["brand_id"]);

											$query_select_brand_info = mysqli_query($param_db, $sql_select_brand_info);
							 
										 	// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
											if(mysqli_num_rows($query_select_brand_info) > 0) {
												// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
												$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

												// Сохраняем название текущего бренда
												$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
											}

											//------------------------------------------------------------------------------------------
											// Добавляем информацию о текущей одежде в общий массив
											if($array_dress_for_current_collection == null) {
												$array_dress_for_current_collection = array();
											}

											if($array_dress_for_current_collection[$dress["type"]] == null) {
												$array_dress_for_current_collection[$dress["type"]] = array();
											}

											array_push($array_dress_for_current_collection[$dress["type"]], $dress);
										}
									}

									$array_current_collection_full_info["dress"] = $array_dress_for_current_collection;
								}

								$response["success"] = 1;

								if( $response["collection"] == null ) {
									$response["collection"] = array();
								}

								array_push($response["collection"], $array_current_collection_full_info);
							}
						}
					}

					//-----------------------------------------------------------------------------------
					// Определяем количество коллекций (наборов одежды) для текущего пользователя
					$sql_select_user_collections_count = "SELECT `id` FROM`".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

					$query_select_user_collections_count = mysqli_query($param_db, $sql_select_user_collections_count);

					$user = array();
					$user["collections_count"] = mysqli_num_rows($query_select_user_collections_count);

					//-----------------------------------------------------------------------------------
					// Формируем массив, хранящий информацию о категориях и количестве одежды,
   					// входящей в состав избранных наборов одежды для текущего пользователя
   					$user["dress_in_collections"] = CreateArrayDressInUserCollections($param_db, $user_id);

   					$response["user"] = $user;
   				}
   			}

	    	return $response;
		}

		//=========================================================================================
		// Функция для сохранения информации о наборах одежды для текущего пользователя
		// Передаваемые параметры
		// param_db 	 			- ссылка на подключение к БД
		// param_user_id 		 	- ID игрока, информацию для которого необходимо считать информацию
		// param_dress_collection 	- массив, содержащий дополнительные параметры
		public function SaveDressCollection($param_db, $param_user_id, $param_dress_collection) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос 
			// о сохранении информации о новом наборе (коллекции) одежды для текущего пользователя
			$response = array();

			// Возвращаемый ответ
			$response["user"] 		= null;
			$response["collection"] = null;
			$response["success"] 	= 0;
			$response["result"] 	= 0;

			// Если передан необходимые параметры
			if($param_db != null && $param_user_id != null && $param_dress_collection != null) {
				$user_id = intval(trim(urldecode(strval($param_user_id))));

				// Если передан параметр user_id, определяющий id пользователя, для которого необходимо считать информацию
				if($user_id > 0) {
					// Определяем тип поддействия (сохранение или удаление информации о текущем наборе одежды)
					$action_collection_save_subtype = 0;

					if(isset($param_dress_collection["action_collection_save_subtype"])) {
						if($param_dress_collection["action_collection_save_subtype"] != null) {
							$action_collection_save_subtype = intval(trim(urldecode(strval($param_dress_collection["action_collection_save_subtype"]))));
						}
					}

					// Если тип поддействия - сохранение информации о текущем наборе одежды
					if($action_collection_save_subtype == 1) {
						// Тип текущей коллекции
						$post_collection_type = null;

						if(isset($param_dress_collection["type"])) {
							if($param_dress_collection["type"] != null) {
								$post_collection_type = trim(urldecode(strval($param_dress_collection["type"])));

								if($post_collection_type == null || $post_collection_type == "") {
									$post_collection_type = "collection";
								}
							}
						}

						// Формируем массив всех возможных типов одежды
						// У данного массива ключ - тип одежды, а значение - присутствует или нет данный тип одежды в текущей коллекции
						$array_dress_types = array();

						$array_dress_types["head"] 		= false;
						$array_dress_types["body"] 		= false;
						$array_dress_types["leg"] 		= false;
						$array_dress_types["foot"] 		= false;
						$array_dress_types["accessory"] = false;

						//-----------------------------------------------------------------------------------------------------
						// Список id одежды из типа "Головные уборы"
						$post_dress_id_for_head = "";

						if(isset($param_dress_collection["head"])) {
							if($param_dress_collection["head"] != null) {
								$array_dress_types["head"] = true;
								$post_dress_id_for_head = trim(urldecode(strval($param_dress_collection["head"])));
							}
						}

						// Список id одежды из типа "Одежда для тела"
						$post_dress_id_for_body = "";

						if(isset($param_dress_collection["body"])) {
							if($param_dress_collection["body"] != null) {
								$array_dress_types["body"] = true;
								$post_dress_id_for_body = trim(urldecode(strval($param_dress_collection["body"])));
							}
						}

						// Список id одежды из типа "Одежда для ног"
						$post_dress_id_for_leg = "";

						if(isset($param_dress_collection["leg"])) {
							if($param_dress_collection["leg"] != null) {
								$array_dress_types["leg"] = true;
								$post_dress_id_for_leg = trim(urldecode(strval($param_dress_collection["leg"])));
							}
						}

						// Список id одежды из типа "Обувь"
						$post_dress_id_for_foot = "";

						if(isset($param_dress_collection["foot"])) {
							if($param_dress_collection["foot"] != null) {
								$array_dress_types["foot"] = true;
								$post_dress_id_for_foot = trim(urldecode(strval($param_dress_collection["foot"])));
							}
						}

						// Список id одежды из типа "Аксессуары"
						$post_dress_id_for_accessory = "";

						if(isset($param_dress_collection["accessory"])) {
							if($param_dress_collection["accessory"] != null) {
								$array_dress_types["accessory"] = true;
								$post_dress_id_for_accessory = trim(urldecode(strval($param_dress_collection["accessory"])));
							}
						}

						//-----------------------------------------------------------------------------------------------------
						// В параметре dress_id может быть передано более одного id
						// При этом в качестве знака разделителя между id вещами используется знак тройного подчеркивания "___"
						// Формируем массив из id вещей
						$array_dress_id = array();

						// Список id одежды из типа "Головные уборы"
						if($post_dress_id_for_head != null && $post_dress_id_for_head != "" && $array_dress_types["head"] == true) {
							$array_dress_id["head"] = array();
							$array_dress_id["head"] = explode("___", $post_dress_id_for_head);
						}

						// Список id одежды из типа "Одежда для тела"
						if($post_dress_id_for_body != null && $post_dress_id_for_body != "" && $array_dress_types["body"] == true) {
							$array_dress_id["body"] = array();
							$array_dress_id["body"] = explode("___", $post_dress_id_for_body);
						}

						// Список id одежды из типа "Одежда для ног"
						if($post_dress_id_for_leg != null && $post_dress_id_for_leg != "" && $array_dress_types["leg"] == true) {
							$array_dress_id["leg"] = array();
							$array_dress_id["leg"] = explode("___", $post_dress_id_for_leg);
						}

						// Список id одежды из типа "Обувь"
						if($post_dress_id_for_foot != null && $post_dress_id_for_foot != "" && $array_dress_types["foot"] == true) {
							$array_dress_id["foot"] = array();
							$array_dress_id["foot"] = explode("___", $post_dress_id_for_foot);
						}

						// Список id одежды из типа "Аксессуары"
						if($post_dress_id_for_accessory != null && $post_dress_id_for_accessory != "" && $array_dress_types["accessory"] == true) {
							$array_dress_id["accessory"] = array();
							$array_dress_id["accessory"] = explode("___", $post_dress_id_for_accessory);
						}

						//---------------------------------------------------------------------------------
						// Формируем общий массив, хранящий id вещей, переданных на сервер
						$array_dress_id_in_current_collection = array();

						foreach($array_dress_types as $array_dress_types_key => $array_dress_types_value) {
							if($array_dress_types_value == true) {
								for($index_dress_id = 0; $index_dress_id < count($array_dress_id[$array_dress_types_key]); $index_dress_id++) {
									array_push($array_dress_id_in_current_collection, $array_dress_id[$array_dress_types_key][$index_dress_id]);
								}
							}
						}

						//---------------------------------------------------------------------------------
						// Проверяем сохранена ли текущая коллекция ранее для текущего пользователя
						$current_collection_saved_id = $this->CheckIsSaveCurrentCollectionForCurrentUser($user_id, $array_dress_id_in_current_collection);

						//---------------------------------------------------------------------------------
						// Если информация о текущем наборе одежды была ранее уже сохранена для текущего пользователя
						if($current_collection_saved_id > 0) {
							$collection = array();
							$collection["id"] = $current_collection_saved_id;

							$response["user"] 		= null;
							$response["collection"] = $collection;
							$response["success"] 	= 0;
							$response["result"]	 	= 2;
							$response["message"] 	= "Данный набор одежды уже ранее был добавлен в раздел 'Мои коллекции'!";
						}
						// Иначе, сохраняем текущий набор одежды, если он не был ранее сохранен для текущего пользователя
						else {
							// Сначала добавляем в БД информацию о новом наборе одежды
							$sql_insert_dress_collection = "INSERT INTO `".DB_TABLE_PREFIX."dressroom_collection` (`title`, `alias`, `type`, `short_description`, `description`, `version`, `user_id`) VALUES (NULL, NULL, '".$post_collection_type."', NULL, NULL, 1, '".strval($user_id)."')";

							$query_insert_dress_collection = mysqli_query($param_db, $sql_insert_dress_collection);

							// Определяем id для только что добавленной записи в БД о новом наборе одежды
		   					$collection_new_id = mysqli_insert_id($param_db);

		   					if($collection_new_id > 0) {
		   						// Возвращаемый массив, содержащий информацию о текущей добавленной коллекции
		   						$collection = array();
		   						$collection["id"] 		= $collection_new_id;
		   						$collection["type"] 	= $post_collection_type;
		   						$collection["dress_id"] = array();

								// В цикле сохраняем для текущего набора одежды вещи, id которых представлены в массиве $array_dress_id
								foreach($array_dress_types as $array_dress_types_key => $array_dress_types_value) {
									if($array_dress_types_value == true) {
										for($index_dress_id = 0; $index_dress_id < count($array_dress_id[$array_dress_types_key]); $index_dress_id++) {
											$sql_save_dress_to_collection = "INSERT INTO `".DB_TABLE_PREFIX."dressroom_collection_dress` (`collection_id`, `dress_id`) VALUES (".strval($collection_new_id).", ".strval($array_dress_id[$array_dress_types_key][$index_dress_id])."' )";

											$query_save_dress_to_collection = mysqli_query($param_db, $sql_save_dress_to_collection);
				 
											// Устанавливаем флаг, что хотя одна вещь сохранена для текущего набора одежды
											$response["success"] = 1;
											$response["result"] = 1;

											// Запоминаем id текущей вещи
		   									$dress = array();
											$dress["id"] = $array_dress_id[$array_dress_types_key][$index_dress_id];

											array_push($collection["dress_id"],  $dress);
										}
									}
								}

								//-----------------------------------------------------------------------------------
								// Передаем данные о данной сохраненной коллекции одежды
								$response["collection"] = $collection;

								//-----------------------------------------------------------------------------------
								// Определяем количество коллекций (наборов одежды) для текущего пользователя
								$sql_select_user_collections_count = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

								$query_select_user_collections_count = mysqli_query($param_db, $sql_select_user_collections_count);

								$user = array();
								$user["collections_count"] = mysqli_num_rows($query_select_user_collections_count);

								//-----------------------------------------------------------------------------------
								// Формируем массив, хранящий информацию о категориях и количестве одежды,
			   					// входящей в состав избранных наборов одежды для текущего пользователя
			   					$user["dress_in_collections"] = CreateArrayDressInUserCollections($param_db, $user_id);

			   					$response["user"] = $user;
							}
						}
					}
					// Иначе, если тип поддействия - удаление информации о текущем наборе одежды
					else if($action_collection_save_subtype == 2) {
						// Определяем id текущего набора одежды
						$post_collection_id = 0;

						if(isset($param_dress_collection["collection_id"])) {
							if($param_dress_collection["collection_id"] != null) {
								$post_collection_id = intval(trim(urldecode(strval($param_dress_collection["collection_id"]))));
							}
						}						

						// Выполняем последующие действия при условии, что корректно передан id текущего набора одежды
						if($post_collection_id > 0) {
							mysqli_query($param_db, "DELETE FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `id`=".strval($post_collection_id));
							mysqli_query($param_db, "DELETE FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($post_collection_id));

							$response["success"] = 1;
							$response["result"]  = 1;

							//-----------------------------------------------------------------------------------
							// Определяем количество коллекций (наборов одежды) для текущего пользователя
							$sql_select_user_collections_count = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

							$query_select_user_collections_count = mysqli_query($param_db, $sql_select_user_collections_count);

							$user = array();
							$user["collections_count"] = mysqli_num_rows($query_select_user_collections_count);

							//-----------------------------------------------------------------------------------
							// Формируем массив, хранящий информацию о категориях и количестве одежды,
			   				// входящей в состав избранных наборов одежды для текущего пользователя
			   				$user["dress_in_collections"] = CreateArrayDressInUserCollections($param_db, $user_id);

			   				$response["user"] = $user;
						}
					}
				}
			}

			return $response;
		}

		//=========================================================================================
		// Функция для считывания информации о наборах одежды для текущего пользователя
		// Передаваемые параметры
		// param_db 	 			- ссылка на подключение к БД
		// param_user_id 		 	- ID игрока, информацию для которого необходимо считать информацию
		// param_dress_collection 	- массив, содержащий дополнительные параметры
		public function SwipeDressCollection($param_db, $param_user_id, $param_dress_collection) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["collection"] = null;
			$response["dress"] 		= null;
			$response["success"] 	= 0;

			// Если переданы необходимые параметры
			if($param_db != null && $param_user_id != null && $param_dress_collection != null) {
				$user_id = intval(trim(urldecode(strval($param_user_id))));

				// Если передан параметр user_id, определяющий id пользователя, для которого необходимо считать информацию
				if($user_id > 0) {
					$post_collection_id = 0;

					if(isset($param_dress_collection["collection_id"])) {
						if($param_dress_collection["collection_id"] != null) {
							$post_collection_id = intval(trim(urldecode(strval($param_dress_collection["collection_id"]))));
						}
					}

					$post_swipe_direction = 0;

					if(isset($param_dress_collection["swipe_direction"])) {
						if($param_dress_collection["swipe_direction"] != null) {
							$post_swipe_direction = intval(trim(urldecode(strval($param_dress_collection["swipe_direction"]))));
						}
					}
			
					//------------------------------------------------------------------------------------------
					// Определяем передан ли на сервер id категории, для которой необходимо считать инфо об одежде, 
					// входящей в состав наборов одежды для текущего пользователя
					$post_category_id = null;

					if(isset($param_dress_collection["catid"])) {
						if($param_dress_collection["catid"] != null) {
							$post_category_id = trim(urldecode(strval($param_dress_collection["catid"])));

							if($post_category_id == "null") {
								$post_category_id = null;
							}
						}
					}

					//------------------------------------------------------------------------------------------
					// Если id категории, для которой необходимо считать инфо об одежде, 
					// входящей в состав наборов одежды для текущего пользователя
					if($post_category_id != null) {
						// Считываем id всех коллекций для текущего пользователя
						$sql_select_collection_for_current_user = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." ORDER BY `id`";

						$query_select_collection_for_current_user = mysqli_query($param_db, $sql_select_collection_for_current_user);

						if(mysqli_num_rows($query_select_collection_for_current_user) > 0) {
							// Массив, хранящий id всех наборов одежды для текущего пользователя
							$array_collection_id_for_current_user = array();

							while($row_query_select_collection_for_current_user = mysqli_fetch_array($query_select_collection_for_current_user)) {
								// Сохраняем id текущего набора одежды
								array_push($array_collection_id_for_current_user, $row_query_select_collection_for_current_user["id"]);
							}

							//-------------------------------------------------------------------------------------
							// Теперь формируем массив, содержащий id всей одежды, входящей в состав всех коллекций
							// для текущего пользователя
							$array_dress_id_in_collection_for_current_user = array();

							// Перебираем в цикле все коллекции одежды для текущего пользователя
							for($index_collection = 0; $index_collection < count($array_collection_id_for_current_user); $index_collection++) {
								// Считываем id всей одежды, присутствующей в данном наборе одежды
								$sql_select_dress_for_current_collection = "SELECT `dress_id` FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($array_collection_id_for_current_user[$index_collection])." ORDER BY `id`";

								$query_select_dress_for_current_collection = mysqli_query($param_db, $sql_select_dress_for_current_collection);

								if(mysqli_num_rows($query_select_dress_for_current_collection) > 0) {
									while($row_query_select_dress_for_current_collection = mysqli_fetch_array($query_select_dress_for_current_collection)) {
										if(in_array($row_query_select_dress_for_current_collection["dress_id"], $array_dress_id_in_collection_for_current_user) == false) {
											array_push($array_dress_id_in_collection_for_current_user, $row_query_select_dress_for_current_collection["dress_id"]);
										}
									}
								}
							}

							//-------------------------------------------------------------------------------------
							// Теперь из массива, хранящего id для всей одежды для всех коллекций для текущего пользователя,
							// отфильтровываем только те вещи, которые относятся к искомой категории
							$array_dress_id_for_current_user = array();

							// Формируем запрос к БД
							$sql_dress_filter = "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE ";

							for($index_dress = 0; $index_dress < count($array_dress_id_in_collection_for_current_user); $index_dress++) {
								if($index_dress == 0) {
									$sql_dress_filter .= "(";
								}

								$sql_dress_filter .= "`id`=".strval($array_dress_id_in_collection_for_current_user[$index_dress]);

								if($index_dress < count($array_dress_id_in_collection_for_current_user) - 1) {
									$sql_dress_filter .= " OR ";
								}

								if($index_dress == count($array_dress_id_in_collection_for_current_user) - 1) {
									$sql_dress_filter .= ")";
								}
							}

							$sql_dress_filter .= " AND `catid`=".strval($post_category_id)." ORDER BY `id`";

							// Выполняем сформированный запрос к БД
							$query_dress_filter = mysqli_query($param_db, $sql_dress_filter);

							if(mysqli_num_rows($query_dress_filter) > 0) {
								while($row_query_dress_filter = mysqli_fetch_array($query_dress_filter)) {
									array_push($array_dress_id_for_current_user, $row_query_dress_filter["id"]);
								}
							}

							//-------------------------------------------------------------------------------------
							// Определяем порядковый номер текущей одежды (в данном случае одежда совпадает с коллекцией)
							$position_current_dress = -1;

							for($index_dress = 0; $index_dress < count($array_dress_id_for_current_user); $index_dress++) {
								if($array_dress_id_for_current_user[$index_dress] == $post_collection_id) {
									$position_current_dress = $index_dress;
								}
							}

							//-------------------------------------------------------------------------------------
							// Определяем позицию в массиве той одежды, информацию о которой необходимо считать
							$position_require_dress = -1;

							if($position_current_dress >= 0) {
								// Если направление листания - слева направо
								if($post_swipe_direction == 1) {
									$position_require_dress = $position_current_dress - 1;
								}
								// Иначе, если направление листания - справа налево
								else if($post_swipe_direction == 2) {
									$position_require_dress = $position_current_dress + 1;
								}
							}

							//-------------------------------------------------------------------------------------
							// Считываем полную информацию о необходимой одежде
							if($position_require_dress >= 0 && $position_require_dress < count($array_dress_id_for_current_user)) {
								// Формируем запрос к БД
								$sql_select_require_dress_info = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".$array_dress_id_for_current_user[$position_require_dress]." LIMIT 1";

								$query_select_require_dress_info = mysqli_query($param_db, $sql_select_require_dress_info);

								// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
								if(mysqli_num_rows($query_select_require_dress_info) > 0) {
									// Считываем всю информацию о найденной одежде
									$row_query_select_require_dress_info = mysqli_fetch_array( $query_select_require_dress_info );
								
									$dress = array();
									$dress["id"] 	  	 	 = $row_query_select_require_dress_info["id"];										// id текущей вещи
									$dress["for_who"] 	 	 = $row_query_select_require_dress_info["for_who"];									// для кого предназначена текущая одежда (для мужчин, женщин или детей)
									$dress["type"] 	 	 	 = $row_query_select_require_dress_info["type"];									// тип текущей вещи (головной убор, обувь и т.д.)
									$dress["catid"] 	 	 = $row_query_select_require_dress_info["catid"];									// id категории для текущей вещи
									$dress["category_title"] = "";																				// название категории для текуще вещи
									$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_require_dress_info["title"]);			// название текущей вещи
									$dress["alias"] 	 	 = $row_query_select_require_dress_info["alias"];									// алиас названия текущей вещи
									$dress["brand_id"] 	 	 = $row_query_select_require_dress_info["brand_id"];								// id бренда для текущей вещи
									$dress["brand_title"] 	 = "";																				// название бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if($row_query_select_require_dress_info["image"] == null) {
										$dress["image"]   		= null;
										$dress["image_width"]   = 0;
										$dress["image_height"]  = 0;
									}
									else if(trim($row_query_select_require_dress_info["image"]) == "") {
										$dress["image"]   		= null;
										$dress["image_width"]   = 0;
										$dress["image_height"]  = 0;
									}
									else {
										$dress["image"]   		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_require_dress_info["image"]);
										$dress["image_width"]   = $row_query_select_require_dress_info["image_width"];
										$dress["image_height"]  = $row_query_select_require_dress_info["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if($row_query_select_require_dress_info["image_back"] == null) {
										$dress["image_back"] 		= null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else if(trim($row_query_select_require_dress_info["image_back"]) == "") {
										$dress["image_back"] = null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else {
										$dress["image_back"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_require_dress_info["image_back"]);
										$dress["image_back_width"]  = $row_query_select_require_dress_info["image_back_width"];
										$dress["image_back_height"] = $row_query_select_require_dress_info["image_back_height"];
									}

									$dress["color"]			= $row_query_select_require_dress_info["color"];												// цвет текущей вещи
									$dress["style"]			= $row_query_select_require_dress_info["style"];												// стиль текущей вещи
									$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_require_dress_info["short_description"]);		// краткое описание для текущей вещи
									$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_require_dress_info["description"]);			// полное описание для текущей вещи
									$dress["hits"] 			= $row_query_select_require_dress_info["hits"];													// уровень популярности текущей вещи
									$dress["version"] 		= $row_query_select_require_dress_info["version"];												// версия информации о текущей вещи
									$dress["dress_default"] = $row_query_select_require_dress_info["dress_default"];										// является ли текущая вещь вещью по умолчанию

									//------------------------------------------------------------------------------------------
									// Считываем название категории для текущей одежды
									$sql_select_category_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".$row_query_select_require_dress_info["catid"]." LIMIT 1";

									$query_select_category_info = mysqli_query($param_db, $sql_select_category_info);
		 
									// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
									if(mysqli_num_rows($query_select_category_info) > 0) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

										// Сохраняем название текущей категории
										$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
									}

									//------------------------------------------------------------------------------------------
									// Считываем название бренда для текущей одежды
									$sql_select_brand_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".$row_query_select_require_dress_info["brand_id"];

									$query_select_brand_info = mysqli_query($param_db, $sql_select_brand_info);
				 
									// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
									if(mysqli_num_rows($query_select_brand_info) > 0) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

										// Сохраняем название текущего бренда
										$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
									}

									//------------------------------------------------------------------------------------------
									$response["success"] = 1;

									// Добавляем информацию о текущей одежде в общий массив
									if($response["dress"] == null) {
										$response["dress"] = array();
									}

									array_push( $response["dress"], $dress);
								}
							}
						}
					}
					// Иначе, если считывается полностью вся коллекция одежды
					else {
						// Формируем запрос к БД для считывания информации о необходимой коллекции одежды
						// в зависимости от направления литсания
						$sql_select_collection_info = null;

						// Если направление листания - слева направо
						if($post_swipe_direction == 1) {
							// Считываем информацию о предыдущей коллекции одежды
							$sql_select_collection_info = "SELECT * FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." AND `id`<".strval($post_collection_id)." ORDER BY `id` DESC LIMIT 1";
						}
						// Иначе, если направление листания - справа налево
						else if($post_swipe_direction == 2) {
							// Считываем информацию о следующей коллекции одежды
							$sql_select_collection_info = "SELECT * FROM `".DB_TABLE_PREFIX."dressroom_collection` WHERE `user_id`=".strval($user_id)." AND `id`>".strval($post_collection_id)." ORDER BY `id` LIMIT 1";
						}

						//------------------------------------------------------------------------------------------
						// Если запрос к БД успешно сформирован
						if($sql_select_collection_info != null) {
							// Выполняем сформированный запрос к БД на считывание информации о необходимой коллекции одежды
							$query_select_collection_info = mysqli_query($param_db, $sql_select_collection_info);

							// Если информация о необходимой коллекции одежды успешно считана
							if(mysqli_num_rows($query_select_collection_info) > 0) {
								$row_query_select_collection_info = mysqli_fetch_array($query_select_collection_info);
					
								// Сохраняем информацию о необходимой коллекции одежды
								$collection = array();
								$collection["id"] 				 = $row_query_select_collection_info["id"];
								$collection["title"] 			 = iconv("cp1251", "utf-8", $row_query_select_collection_info["title"]);
								$collection["alias"] 			 = $row_query_select_collection_info["alias"];
								$collection["type"] 			 = $row_query_select_collection_info["type"];
								$collection["short_description"] = iconv("cp1251", "utf-8", $row_query_select_collection_info["short_description"]);
								$collection["description"] 		 = iconv("cp1251", "utf-8", $row_query_select_collection_info["description"]);
								$collection["version"] 			 = $row_query_select_collection_info["version"];
								$collection["dress"]			 = null;

								//------------------------------------------------------------------------------------------
								// Считываем id всей одежды, присутствующей в данной коллекции одежды
								$sql_select_dress_for_current_collection = "SELECT `dress_id` FROM `".DB_TABLE_PREFIX."dressroom_collection_dress` WHERE `collection_id`=".strval($collection["id"])." ORDER BY `id`";

								$query_select_dress_for_current_collection = mysqli_query($param_db, $sql_select_dress_for_current_collection);

								if(mysqli_num_rows($query_select_dress_for_current_collection) > 0) {
									// Массив, хранящий id вещей для данной коллекции
									$array_dress_id_in_current_collection_from_db = array();

									while($row_query_select_dress_for_current_collection = mysqli_fetch_array($query_select_dress_for_current_collection)) {
										array_push($array_dress_id_in_current_collection_from_db, $row_query_select_dress_for_current_collection["dress_id"]);
									}

									//-------------------------------------------------------------------------------------
									// Теперь в цикле считываем всю информацию о каждой одежде, присутствующей в данном наборе одежды
									for($index_dress_in_collection = 0; $index_dress_in_collection < count($array_dress_id_in_current_collection_from_db ); $index_dress_in_collection++) {
										// Формируем запрос для считывания информации о текущей одежде
										$sql_select_dress_info = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".$array_dress_id_in_current_collection_from_db[$index_dress_in_collection]." LIMIT 1";

										$query_select_dress_info = mysqli_query($param_db, $sql_select_dress_info);

										// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
										if(mysqli_num_rows($query_select_dress_info) > 0) {
											// Считываем всю информацию о найденной одежде
											$row_query_select_dress_info = mysqli_fetch_array($query_select_dress_info);
												
											$dress = array();
											$dress["id"] 	  	 	 = $row_query_select_dress_info["id"];										// id текущей вещи
											$dress["for_who"] 	 	 = $row_query_select_dress_info["for_who"];									// для кого предназначена текущая одежда (для мужчин, женщин или детей)
											$dress["type"] 	 	 	 = $row_query_select_dress_info["type"];									// тип текущей вещи (головной убор, обувь и т.д.)
											$dress["catid"] 	 	 = $row_query_select_dress_info["catid"];									// id категории для текущей вещи
											$dress["category_title"] = "";																		// название категории для текуще вещи
											$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_info["title"]);			// название текущей вещи
											$dress["alias"] 	 	 = $row_query_select_dress_info["alias"];									// алиас названия текущей вещи
											$dress["brand_id"] 	 	 = $row_query_select_dress_info["brand_id"];								// id бренда для текущей вещи
											$dress["brand_title"] 	 = "";																		// название бренда для текущей вещи

											// Ссылка на изображение для текущей вещи
											if($row_query_select_dress_info["image"] == null) {
												$dress["image"]   		= null;
												$dress["image_width"]   = 0;
												$dress["image_height"]  = 0;
											}
											else if(trim($row_query_select_dress_info["image"]) == "") {
												$dress["image"]   		= null;
												$dress["image_width"]   = 0;
												$dress["image_height"]  = 0;
											}
											else {
												$dress["image"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image"]);
												$dress["image_width"]   = $row_query_select_dress_info["image_width"];
												$dress["image_height"]  = $row_query_select_dress_info["image_height"];
											}

											// Ссылка на изображение с обратной стороны для текущей вещи
											if($row_query_select_dress_info["image_back"] == null) {
												$dress["image_back"] 		= null;
												$dress["image_back_width"]  = 0;
												$dress["image_back_height"] = 0;
											}
											else if(trim($row_query_select_dress_info["image_back"]) == "") {
												$dress["image_back"] 		= null;
												$dress["image_back_width"]  = 0;
												$dress["image_back_height"] = 0;
											}
											else {
												$dress["image_back"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image_back"]);
												$dress["image_back_width"]  = $row_query_select_dress_info["image_back_width"];
												$dress["image_back_height"] = $row_query_select_dress_info["image_back_height"];
											}

											$dress["color"]			= $row_query_select_dress_info["color"];												// цвет текущей вещи
											$dress["style"]			= $row_query_select_dress_info["style"];												// стиль текущей вещи
											$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info["short_description"]);		// краткое описание для текущей вещи
											$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_info["description"]);			// полное описание для текущей вещи
											$dress["hits"] 			= $row_query_select_dress_info["hits"];													// уровень популярности текущей вещи
											$dress["version"] 		= $row_query_select_dress_info["version"];												// версия информации о текущей вещи
											$dress["dress_default"] = $row_query_select_dress_info["dress_default"];										// является ли текущая вещь вещью по умолчанию

											//------------------------------------------------------------------------------------------
											// Считываем название категории для текущей одежды
											$sql_select_category_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_info["catid"])." LIMIT 1";

											$query_select_category_info = mysqli_query($param_db, $sql_select_category_info);
			 
								 			// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
											if(mysqli_num_rows($query_select_category_info) > 0) {
												// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
												$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

												// Сохраняем название текущей категории
												$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info["title"]);
											}

											//------------------------------------------------------------------------------------------
											// Считываем название бренда для текущей одежды
											$sql_select_brand_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_info["brand_id"]);

											$query_select_brand_info = mysqli_query($param_db, $sql_select_brand_info);
								 
											 // Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
											if(mysqli_num_rows( $query_select_brand_info ) > 0) {
												// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
												$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

												// Сохраняем название текущего бренда
												$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info["title"]);
											}

											$response["success"] = 1;

											//------------------------------------------------------------------------------------------
											// Добавляем информацию о текущей одежде в общий массив
											if($collection["dress"] == null) {
												$collection["dress"] = array();
											}

											if($collection["dress"][ $dress["type"] ] == null) {
												$collection["dress"][ $dress["type"] ] = array();
											}

											array_push($collection["dress"][$dress["type"]], $dress);
										}
									}
								}

								//------------------------------------------------------------------------------------------
								// Сохраняем считанную из БД информацию о текущей коллекции одежды в возвращаемом массиве
								if($response["collection"] == null) {
									$response["collection"] = array();
								}

								array_push($response["collection"], $collection);
							}
						}
					}
				}
			}

			return $response;
		}
	}
?>