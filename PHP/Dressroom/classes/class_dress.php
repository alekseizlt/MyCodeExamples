<?php
	// Подключаем необходимые файлы
	require_once("include/define.php");
	require_once("lib/functions.php");

	class TableDress {
		
		//=================================================================================================================
		// Функция для считывания информации об одежде по умолчанию для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GetDressDefault($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] = array();
			$response["success"] = 0;

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// Массив, хранящий для кого предназначены текущие вещи
				$array_dress_for_who = null;

				// Если передан параметр dress_for_who, определяющий для кого предназначены текущие вещи (одежда)
				$post_dress_for_who = null;

				if(isset($param_dress["for_who"])) {
					if($param_dress["for_who"] != null) {
						$post_dress_for_who = trim(urldecode(strval($param_dress["for_who"])));
					}
				}

				// Продолжаем дальше только при условии, что переданный параметр dress_for_who имеет допустимое значение
				// Возможные значения:
				// man 		  - одежда предназначена для мужчин
				// woman 	  - одежда предназначена для женщин
				// kid 		  - одежда предназначена для детей
				// all        - необходимо считать все категории одежды

				// Если конкретно выбрано, для кого предназначены текущие вещи
				if($post_dress_for_who == "man" || $post_dress_for_who == "woman" || $post_dress_for_who == "kid") {
					$array_dress_for_who = array();
					$array_dress_for_who[] = $post_dress_for_who;
				}
				// Иначе, если необходимо считать все категории одежды
				else if($post_dress_for_who == "all") {
					$array_dress_for_who 	= array();
					$array_dress_for_who[] 	= "man";
					$array_dress_for_who[] 	= "woman";
					$array_dress_for_who[] 	= "kid";
				}

				// Продолжаем дальнейшую обработку при условии, что правильно передан параметр post_dress_for_who
				if(($post_dress_for_who == "man" || $post_dress_for_who == "woman" || $post_dress_for_who == "kid" || $post_dress_for_who == "all") && is_array($array_dress_for_who) && count($array_dress_for_who) > 0) {
					// Определяем количество вещей (одежды), информацию о которых необходимо одновременно скачать из БД
					$post_count_dress_read_from_db = 15;

					if(isset($param_dress['count_dress_read_from_db'])) {
						if($param_dress['count_dress_read_from_db'] != null) {
							$post_count_dress_read_from_db = intval(trim(urldecode(strval($param_dress['count_dress_read_from_db']))));
						}
					}

					// Массив, содержащий возможные типы одежды
					$array_dress_type 	= array();
					$array_dress_type[] = "head";		// головные уборы
					$array_dress_type[] = "body";		// одежда, одеваемая на тело
					$array_dress_type[] = "leg";		// одежда, одеваемая на ноги
					$array_dress_type[] = "foot";		// обувь
					$array_dress_type[] = "accessory";	// аксессуары

					// В цикле считываем категории одежды для каждого, для кого она предназначена
					for($index_dress_for_who = 0; $index_dress_for_who < count($array_dress_for_who); $index_dress_for_who++) {
						// В цикле тут же перебираем все возможные типы одежды (головные уборы, обувь и т.д.)
						for($index_dress_type = 0; $index_dress_type < count($array_dress_type); $index_dress_type++) {
							// Параметры для запроса к БД
							$dress_for_who = $array_dress_for_who[$index_dress_for_who];	// для кого предназначены текущие вещи
							$dress_type    = $array_dress_type[$index_dress_type];			// тип текущих вещей (головные уборы, обувь и т.д.)
							$dress_default = 1;												// логическая переменная, указывающая на то, что данная одежда используется для просмотра по умолчанию
							$published	   = 1;												// логическая переменная, указывающая на то, что информация о текущей вещи опубликована

							$sql_select_dress_default = "SELECT `id`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `for_who`='".$array_dress_for_who[$index_dress_for_who]."' AND `type`='".$array_dress_type[$index_dress_type]."' AND `dress_default`=".strval($dress_default)." AND `published`=".strval($published)." ORDER BY `id`";

							$query_select_dress_default = mysqli_query($param_db, $sql_select_dress_default);
 
							// Если количество найденных строк <=0, то считываем информацию о первой вещи (одежде)
							// из общего массива вещей для текущего типа
							if(mysqli_num_rows($query_select_dress_default) <= 0) {
								$sql_select_dress_default = "SELECT `id`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `for_who`='".$array_dress_for_who[$index_dress_for_who]."' AND `type`='".$array_dress_type[$index_dress_type]."' AND `published`=".strval($published)." ORDER BY `id` LIMIT 1";

								$query_select_dress_default = mysqli_query($param_db, $sql_select_dress_default);
							}

				 			// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
							if(mysqli_num_rows($query_select_dress_default) > 0) {
								if(!is_array($response["dress"][$array_dress_for_who[$index_dress_for_who]])) {
									$response["dress"][$array_dress_for_who[$index_dress_for_who]] = array();
								}

								$response["dress"][$array_dress_for_who[$index_dress_for_who]][$array_dress_type[$index_dress_type]] = array();

								// В цикле считываем всю информацию о найденной одежде по умолчанию
								while($row_query_select_dress_default = mysqli_fetch_array($query_select_dress_default)) {
									$dress = array();
									$dress["id"] 	  	 	 = $row_query_select_dress_default["id"];									// id текущей вещи
									$dress["prev_id"] 	 	 = 0;																		// id предыдущей вещи с такими же параметрами
									$dress["next_id"] 	 	 = 0;																		// id следующей вещи с такими же параметрами
									$dress["catid"] 	 	 = $row_query_select_dress_default["catid"];								// id категории для текущей вещи
									$dress["category_title"] = "";																		// название категории для текуще вещи
									$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_default["title"]);		// название текущей вещи
									$dress["alias"] 	 	 = $row_query_select_dress_default["alias"];								// алиас названия текущей вещи
									$dress["brand_id"] 	 	 = $row_query_select_dress_default["brand_id"];								// id бренда для текущей вещи
									$dress["brand_title"] 	 = "";																		// название бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if($row_query_select_dress_default["image"] == null) {
										$dress["image"]   	   = null;
										$dress["image_width"]  = 0;
										$dress["image_height"] = 0;
									}
									else if(trim( $row_query_select_dress_default["image"] ) == "") {
										$dress["image"]   	   = null;
										$dress["image_width"]  = 0;
										$dress["image_height"] = 0;
									}
									else {
										$dress["image"]   	   = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_default["image"]);
										$dress["image_width"]  = $row_query_select_dress_default["image_width"];
										$dress["image_height"] = $row_query_select_dress_default["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if($row_query_select_dress_default["image_back"] == null) {
										$dress["image_back"] 		= null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else if(trim( $row_query_select_dress_default["image_back"] ) == "") {
										$dress["image_back"] 		= null;
										$dress["image_back_width"]  = 0;
										$dress["image_back_height"] = 0;
									}
									else
									{
										$dress["image_back"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_default["image_back"]);
										$dress["image_back_width"]  = $row_query_select_dress_default["image_back_width"];
										$dress["image_back_height"] = $row_query_select_dress_default["image_back_height"];
									}

									$dress["color"]			= $row_query_select_dress_default["color"];													// цвет текущей вещи
									$dress["style"]			= $row_query_select_dress_default["style"];													// стиль текущей вещи
									$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_default["short_description"]);		// краткое описание для текущей вещи
									$dress["description"] 	= iconv("cp1251", "utf-8", $row_query_select_dress_default["description"]);					// полное описание для текущей вещи
									$dress["hits"] 			= $row_query_select_dress_default["hits"];													// уровень популярности текущей вещи
									$dress["version"] 		= $row_query_select_dress_default["version"];												// версия информации о текущей вещи
									$dress["dress_default"] = $row_query_select_dress_default["dress_default"];											// флаг, указывающий на то, что текущая вещь является вещью, отображаемой по умолчанию
									$dress["dress_show_now"] = 1;																						// флаг, указывающий, что данная вещь будет отображена в первую очередь

									//------------------------------------------------------------------------------------------
									// Считываем название категории для текущей одежды
									$sql_select_category_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_default["catid"])." LIMIT 1";

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
									$sql_select_brand_info = "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_default["brand_id"]);

									$query_select_brand_info = mysqli_query($param_db, $sql_select_brand_info);
				 
							 		// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
									if(mysqli_num_rows($query_select_brand_info) > 0) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

										// Сохраняем название текущего бренда
										$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
									}

									//------------------------------------------------------------------------------------------
									// Создаем временный массив параметров одежды для считывания id следующей и предыдущей вещей
									$array_temp_dress_params = array();
									$array_temp_dress_params['id']		= $row_query_select_dress_default["id"];		// id текущей вещи
									$array_temp_dress_params['catid']	= $row_query_select_dress_default["catid"];		// id категории для текущей вещи
									$array_temp_dress_params['for_who'] = $array_dress_for_who[ $index_dress_for_who ];	// для кого предназначены текущие вещи
									$array_temp_dress_params['type'] 	= $array_dress_type[ $index_dress_type ];		// тип текущих вещей (головные уборы, обувь и т.д.)
									$array_temp_dress_params['color']	= $row_query_select_dress_default["color"];		// цвет текущей вещи
									$array_temp_dress_params['style']	= $row_query_select_dress_default["style"];		// стиль текущей вещи

									//------------------------------------------------------------------------------------------
									// Считываем id следующей и предыдущей вещей
									// id предыдущей записи
									$query_select_dress_id_prev = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`<".strval($array_temp_dress_params['id'])." ORDER BY `id` DESC LIMIT 1");

									if(mysqli_num_rows($query_select_dress_id_prev) > 0) {
										$row_query_select_dress_id_prev = mysqli_fetch_array($query_select_dress_id_prev);

										$dress["prev_id"] = $row_query_select_dress_id_prev['id'];
									}

									//------------------------------------------------------------------------------------------
									// id следующей записи
									$query_select_dress_id_next = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`>".strval($array_temp_dress_params['id'])." ORDER BY `id` LIMIT 1" );

									if(mysqli_num_rows($query_select_dress_id_next) > 0) {
										$row_query_select_dress_id_next = mysqli_fetch_array($query_select_dress_id_next);

										$dress["next_id"] = $row_query_select_dress_id_next['id'];
									}

									//------------------------------------------------------------------------------------------
									// Считываем информацию об одежде (в количестве post_count_dress_read_from_db/2), находящихся перед текущей одеждой
									$polovina_count_dress_read_from_db = intval(($post_count_dress_read_from_db - 1) / 2);

									$query_select_dress_info_prev = mysqli_query($param_db, "SELECT `id`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`<".strval($array_temp_dress_params['id'])." ORDER BY `id` DESC LIMIT ".strval($polovina_count_dress_read_from_db));

									// Предыдущих строк может оказаться меньще, чем $polovina_count_dress_read_from_db,
									// поэтому определяем насколько больше необходимо считать следующих строк
									$offset_count_next_dress_read_from_db = $polovina_count_dress_read_from_db - mysqli_num_rows($query_select_dress_info_prev);

									// Теперь считываем всю информацию о предыдущих вещах
									if(mysqli_num_rows($query_select_dress_info_prev) > 0) {
										while($row_query_select_dress_info_prev = mysqli_fetch_array($query_select_dress_info_prev)) {
											$dress_prev = array();
											$dress_prev["id"] 	  	 	  = $row_query_select_dress_info_prev["id"];								// id текущей вещи
											$dress_prev["prev_id"] 	 	  = 0;																		// id предыдущей вещи с такими же параметрами
											$dress_prev["next_id"] 	 	  = 0;																		// id следующей вещи с такими же параметрами
											$dress_prev["catid"] 	 	  = $row_query_select_dress_info_prev["catid"];								// id категории для текущей вещи
											$dress_prev["category_title"] = $dress["category_title"];												// название категории для текуще вещи
											$dress_prev["title"]   	 	  = iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["title"]);	// название текущей вещи
											$dress_prev["alias"] 	 	  = $row_query_select_dress_info_prev["alias"];								// алиас названия текущей вещи
											$dress_prev["brand_id"] 	  = $row_query_select_dress_info_prev["brand_id"];							// id бренда для текущей вещи
											$dress_prev["brand_title"] 	  = $dress["brand_title"];													// название бренда для текущей вещи

											// Ссылка на изображение для текущей вещи
											if($row_query_select_dress_info_prev["image"] == null) {
												$dress_prev["image"]   		= null;
												$dress_prev["image_width"]  = 0;
												$dress_prev["image_height"] = 0;
											}
											else if(trim($row_query_select_dress_info_prev["image"]) == "") {
												$dress_prev["image"]   		= null;
												$dress_prev["image_width"]  = 0;
												$dress_prev["image_height"] = 0;
											}
											else {
												$dress_prev["image"]   		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image"]);
												$dress_prev["image_width"]  = $row_query_select_dress_info_prev["image_width"];
												$dress_prev["image_height"] = $row_query_select_dress_info_prev["image_height"];
											}

											// Ссылка на изображение с обратной стороны для текущей вещи
											if($row_query_select_dress_info_prev["image_back"] == null) {
												$dress_prev["image_back"] 		 = null;
												$dress_prev["image_back_width"]  = 0;
												$dress_prev["image_back_height"] = 0;
											}
											else if(trim($row_query_select_dress_info_prev["image_back"]) == "") {
												$dress_prev["image_back"] 		 = null;
												$dress_prev["image_back_width"]  = 0;
												$dress_prev["image_back_height"] = 0;
											}
											else {
												$dress_prev["image_back"] 		 = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image_back"]);
												$dress_prev["image_back_width"]  = $row_query_select_dress_info_prev["image_back_width"];
												$dress_prev["image_back_height"] = $row_query_select_dress_info_prev["image_back_height"];
											}

											$dress_prev['color']			= $row_query_select_dress_info_prev["color"];												// цвет текущей вещи
											$dress_prev['style']			= $row_query_select_dress_info_prev["style"];												// стиль текущей вещи
											$dress_prev["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["short_description"]);		// краткое описание для текущей вещи
											$dress_prev["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["description"]);				// полное описание для текущей вещи
											$dress_prev["hits"] 			= $row_query_select_dress_info_prev["hits"];												// уровень популярности текущей вещи
											$dress_prev["version"] 			= $row_query_select_dress_info_prev["version"];												// версия информации о текущей вещи
											$dress_prev["dress_default"] 	= $row_query_select_dress_info_prev["dress_default"];										// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
											$dress_prev["dress_show_now"] 	= 0;																						// флаг, указывающий на то, что данная вещь НЕ будет отображена в первую очередь

											//------------------------------------------------------------------------------------------
											// Считываем id следующей и предыдущей вещей
											// id предыдущей записи
											$query_select_dress_id_prev = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`<".strval($row_query_select_dress_info_prev['id'])." ORDER BY `id` DESC LIMIT 1");

											if(mysqli_num_rows($query_select_dress_id_prev) > 0) {
												$row_query_select_dress_id_prev = mysqli_fetch_array($query_select_dress_id_prev);

												$dress_prev["prev_id"] = $row_query_select_dress_id_prev['id'];
											}

											// id следующей записи
											$query_select_dress_id_next = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`>".strval($row_query_select_dress_info_prev['id'])." ORDER BY `id` LIMIT 1");

											if(mysqli_num_rows($query_select_dress_id_next) > 0) {
												$row_query_select_dress_id_next = mysqli_fetch_array($query_select_dress_id_next);

												$dress_prev["next_id"] = $row_query_select_dress_id_next['id'];
											}

											// Помещаем сведения о текущей категории в общий массив
											array_push( $response["dress"][ $array_dress_for_who[ $index_dress_for_who ] ][ $array_dress_type[ $index_dress_type ] ], $dress_prev );
										}
									}

									//------------------------------------------------------------------------------------------
									// Помещаем сведения о текущей категории в общий массив
									array_push( $response["dress"][ $array_dress_for_who[ $index_dress_for_who ] ][ $array_dress_type[ $index_dress_type ] ], $dress );

									//------------------------------------------------------------------------------------------
									// Считываем информацию об одежде (в количестве polovina_count_dress_read_from_db + offset_count_next_dress_read_from_db), находящихся после текущей одеждой
									$query_select_dress_info_next = mysqli_query($param_db, "SELECT `id`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".$array_temp_dress_params['catid']." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`>".strval($array_temp_dress_params['id'])." ORDER BY `id` LIMIT ".($polovina_count_dress_read_from_db + $offset_count_next_dress_read_from_db));

									// Теперь считываем всю информацию о следующих вещах
									if(mysqli_num_rows($query_select_dress_info_next) > 0) {
										while($row_query_select_dress_info_next = mysqli_fetch_array($query_select_dress_info_next)) {
											$dress_next = array();
											$dress_next["id"] 	  	 	  = $row_query_select_dress_info_next["id"];								// id текущей вещи
											$dress_next["prev_id"] 	 	  = 0;																		// id предыдущей вещи с такими же параметрами
											$dress_next["next_id"] 	 	  = 0;																		// id следующей вещи с такими же параметрами
											$dress_next["catid"] 	 	  = $row_query_select_dress_info_next["catid"];								// id категории для текущей вещи
											$dress_next["category_title"] = $dress["category_title"];												// название категории для текуще вещи
											$dress_next["title"]   	 	  = iconv("cp1251", "utf-8", $row_query_select_dress_info_next["title"]);	// название текущей вещи
											$dress_next["alias"] 	 	  = $row_query_select_dress_info_next["alias"];								// алиас названия текущей вещи
											$dress_next["brand_id"] 	  = $row_query_select_dress_info_next["brand_id"];							// id бренда для текущей вещи
											$dress_next["brand_title"] 	  = $dress["brand_title"];													// название бренда для текущей вещи

											// Ссылка на изображение для текущей вещи
											if($row_query_select_dress_info_next["image"] == null) {
												$dress_next["image"]   		= null;
												$dress_next["image_width"]  = 0;
												$dress_next["image_height"] = 0;
											}
											else if(trim( $row_query_select_dress_info_next["image"] ) == "") {
												$dress_next["image"]   		= null;
												$dress_next["image_width"]  = 0;
												$dress_next["image_height"] = 0;
											}
											else {
												$dress_next["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image"]);
												$dress_next["image_width"]  = $row_query_select_dress_info_next["image_width"];
												$dress_next["image_height"] = $row_query_select_dress_info_next["image_height"];
											}

											// Ссылка на изображение с обратной стороны для текущей вещи
											if($row_query_select_dress_info_next["image_back"] == null) {
												$dress_next["image_back"] 		 = null;
												$dress_next["image_back_width"]  = 0;
												$dress_next["image_back_height"] = 0;
											}
											else if(trim( $row_query_select_dress_info_next["image_back"] ) == "") {
												$dress_next["image_back"] 		 = null;
												$dress_next["image_back_width"]  = 0;
												$dress_next["image_back_height"] = 0;
											}
											else {
												$dress_next["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image_back"]);
												$dress_next["image_back_width"]  = $row_query_select_dress_info_next["image_back_width"];
												$dress_next["image_back_height"] = $row_query_select_dress_info_next["image_back_height"];
											}

											$dress_next['color']		= $row_query_select_dress_info_next["color"];													// цвет текущей вещи
											$dress_next['style']		= $row_query_select_dress_info_next["style"];													// стиль текущей вещи
											$dress_next["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info_next["short_description"]);		// краткое описание для текущей вещи
											$dress_next["description"] 	= iconv("cp1251", "utf-8", $row_query_select_dress_info_next["description"]);					// полное описание для текущей вещи
											$dress_next["hits"] 		= $row_query_select_dress_info_next["hits"];													// уровень популярности текущей вещи
											$dress_next["version"] 		= $row_query_select_dress_info_next["version"];													// версия информации о текущей вещи
											$dress_next["dress_default"] = $row_query_select_dress_info_next["dress_default"];											// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
											$dress_next["dress_show_now"] = 0;																							// флаг, указывающий на то, что данная не будет отображена в первую очередь

											//------------------------------------------------------------------------------------------
											// Считываем id следующей и предыдущей вещей
											// id предыдущей записи
											$query_select_dress_id_prev = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`<".strval($row_query_select_dress_info_next['id'])." ORDER BY `id` DESC LIMIT 1");

											if(mysqli_num_rows($query_select_dress_id_prev) > 0) {
												$row_query_select_dress_id_prev = mysqli_fetch_array($query_select_dress_id_prev);

												$dress_next["prev_id"] = $row_query_select_dress_id_prev['id'];
											}

											// id следующей записи
											$query_select_dress_id_next = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params['catid'])." AND `for_who`='".$array_temp_dress_params['for_who']."' AND `type`='".$array_temp_dress_params['type']."' AND `color`='".$array_temp_dress_params['color']."' AND `style`='".$array_temp_dress_params['style']."' AND `id`>".strval($row_query_select_dress_info_next['id'])." ORDER BY `id` LIMIT 1");

											if(mysqli_num_rows($query_select_dress_id_next) > 0) {
												$row_query_select_dress_id_next = mysqli_fetch_array($query_select_dress_id_next);

												$dress_next["next_id"] = $row_query_select_dress_id_next['id'];
											}

											// Помещаем сведения о текущей категории в общий массив
											array_push( $response["dress"][ $array_dress_for_who[ $index_dress_for_who ] ][ $array_dress_type[ $index_dress_type ] ], $dress_next );
										}
									}
								}

								// Устанавливаем флаг, что хотя найдена информация хотя бы об одной одежде по умолчанию
								$response["success"] = 1;
							}
						}
					}
				}
			}
	
	    	return $response;
		}

		//=================================================================================================================
		// Функция для считывания информации об определенной одежде для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GetDressFullInfo($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] = null;
			$response["success"] = 0;
			$response["collection_id"] = 0;

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// id текущего пользователя
				$post_user_id = 0;

				if(isset($param_dress["user_id"])) {
					if($param_dress["user_id"] != null) {
						$post_user_id = intval(trim(urldecode(strval($param_dress["user_id"]))));
					}
				}

				//-------------------------------------------------------------------------------------------------
				// Формируем массив всех возможных типов одежды
				// У данного массива ключ - тип одежды, а значение - присутствует или нет данный тип одежды в текущей коллекции
				$array_dress_types 				= array();
				$array_dress_types["head"] 		= false;
				$array_dress_types["body"] 		= false;
				$array_dress_types["leg"] 		= false;
				$array_dress_types["foot"] 		= false;
				$array_dress_types["accessory"] = false;

				//-----------------------------------------------------------------------------------------------------
				// Список id одежды из типа "Головные уборы"
				$post_dress_id_for_head = "";

				if(isset($param_dress["head"])) {
					if($param_dress["head"] != null) {
						$array_dress_types["head"] = true;
						$post_dress_id_for_head = trim(urldecode(strval($param_dress["head"])));
					}
				}

				// Список id одежды из типа "Одежда для тела"
				$post_dress_id_for_body = "";

				if(isset($param_dress["body"])) {
					if($param_dress["body"] != null) {
						$array_dress_types["body"] = true;
						$post_dress_id_for_body = trim(urldecode(strval($param_dress["body"])));
					}
				}

				// Список id одежды из типа "Одежда для ног"
				$post_dress_id_for_leg = "";

				if(isset($param_dress["leg"])) {
					if($param_dress["leg"] != null) {
						$array_dress_types["leg"] = true;
						$post_dress_id_for_leg = trim(urldecode(strval($param_dress["leg"])));
					}
				}

				// Список id одежды из типа "Обувь"
				$post_dress_id_for_foot = "";

				if(isset($param_dress["foot"])) {
					if($param_dress["foot"] != null) {
						$array_dress_types["foot"] = true;
						$post_dress_id_for_foot = trim(urldecode(strval($param_dress["foot"])));
					}
				}

				// Список id одежды из типа "Аксессуары"
				$post_dress_id_for_accessory = "";

				if(isset($param_dress["accessory"])) {
					if($param_dress["accessory"] != null) {
						$array_dress_types["accessory"] = true;
						$post_dress_id_for_accessory = trim(urldecode(strval($param_dress["accessory"])));
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

				//-----------------------------------------------------------------------------------------------------
				// Массив, содержащий id всех представленных вещей
				$array_dress_id_show_now = array();
				
				//-----------------------------------------------------------------------------------------------------
				// В цикле считываем информацию для каждой вещи (одежды), id которой представлен в массиве $array_dress_id
				foreach($array_dress_types as $array_dress_types_key => $array_dress_types_value) {
					if($array_dress_types_value == true) {
						for($index_dress_id = 0; $index_dress_id < count($array_dress_id[$array_dress_types_key]); $index_dress_id++) {
							// Считываем всю информацию о текущей вещи из БД
							$query_select_dress_info = mysqli_query($param_db, "SELECT * FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".strval($array_dress_id[$array_dress_types_key][$index_dress_id])." LIMIT 1");
			 
				 			// Если количество найденных строк больше 1, то информация об одежде найдена
							if(mysqli_num_rows($query_select_dress_info) > 0) {
								// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
								$row_query_select_dress_info = mysqli_fetch_array($query_select_dress_info);
							
								// Заполняем массив информацией о текущей одежде
								$dress = array();
								$dress["id"] 				= $row_query_select_dress_info["id"];
								$dress["catid"] 			= $row_query_select_dress_info["catid"];
								$dress["category_title"] 	= "";
								$dress["title"] 			= iconv("cp1251", "utf-8", $row_query_select_dress_info["title"]);
								$dress["alias"] 			= $row_query_select_dress_info["alias"];
								$dress["for_who"] 			= $row_query_select_dress_info["for_who"];
								$dress["type"] 				= $row_query_select_dress_info["type"];
								$dress["brand_id"] 			= $row_query_select_dress_info["brand_id"];
								$dress["brand_title"] 		= "";

								// Превьюшка для текущей вещи (одежды)
								if($row_query_select_dress_info["thumb"] == null) {
									$dress["thumb"]   		= null;
									$dress["thumb_width"]   = 0;
									$dress["thumb_height"]  = 0;
								}
								else if(trim($row_query_select_dress_info["thumb"]) == "") {
									$dress["thumb"]   		= null;
									$dress["thumb_width"]   = 0;
									$dress["thumb_height"]  = 0;
								}
								else {
									$dress["thumb"]   		= str_replace("/", __SLASH__, $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["thumb"]);
									$dress["thumb_width"]   = $row_query_select_dress_info["thumb_width"];
									$dress["thumb_height"]  = $row_query_select_dress_info["thumb_height"];
								}

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
									$dress["image"]   = str_replace("/", __SLASH__, $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image"]);
									$dress["image_width"]   = $row_query_select_dress_info["image_width"];
									$dress["image_height"]  = $row_query_select_dress_info["image_height"];
								}

								// Ссылка на изображение для обратной стороны для текущей вещи
								if($row_query_select_dress_info["image_back"] == null) {
									$dress["image_back"]   		= null;
									$dress["image_back_width"]  = 0;
									$dress["image_back_height"] = 0;
								}
								else if(trim($row_query_select_dress_info["image_back"]) == "") {
									$dress["image_back"]   		= null;
									$dress["image_back_width"]  = 0;
									$dress["image_back_height"] = 0;
								}
								else {
									$dress["image_back"]   		= str_replace("/", __SLASH__, $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image_back"]);
									$dress["image_back_width"]  = $row_query_select_dress_info["image_back_width"];
									$dress["image_back_height"] = $row_query_select_dress_info["image_back_height"];
								}

								$dress["image_additional"]	= null;
								$dress["color"] 			= $row_query_select_dress_info["color"];
								$dress["style"] 			= $row_query_select_dress_info["style"];
								$dress["short_description"] = iconv("cp1251", "utf-8", str_replace("/", __SLASH__, $row_query_select_dress_info["short_description"]) );
								$dress["description"] 		= iconv("cp1251", "utf-8", str_replace("/", __SLASH__, $row_query_select_dress_info["description"]) );
								$dress["hits"] 				= $row_query_select_dress_info["hits"];
								$dress["version"] 			= $row_query_select_dress_info["version"];
								$dress["dress_default"] 	= $row_query_select_dress_info["dress_default"];

								//---------------------------------------------------------------------------------
								// Сохраняем id текущей одежды
								array_push($array_dress_id_show_now, $row_query_select_dress_info["id"]);

								//---------------------------------------------------------------------------------
								// Считываем название категории для текущей одежды
								$query_select_category_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_info["catid"])." LIMIT 1");
		 
					 			// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
								if(mysqli_num_rows($query_select_category_info) > 0) {
									// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
									$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

									// Сохраняем название текущей категории
									$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
								}

								//---------------------------------------------------------------------------------
								// Считываем название и изображение бренда для текущей одежды
								$query_select_brand_info = mysqli_query($param_db, "SELECT `title`, `image` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_info["brand_id"])." LIMIT 1");
			 
					 			// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
								if(mysqli_num_rows($query_select_brand_info) > 0) {
									// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
									$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

									// Сохраняем название текущего бренда
									$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);

									// Сохраняем ссылку на изображение для текущего бренда
									if($row_query_select_brand_info["image"] == null) {
										$dress["brand_image"] = null;
									}
									else if(trim($row_query_select_brand_info["image"]) == "") {
										$dress["brand_image"] = null;
									}
									else {
										$dress["brand_image"] = str_replace("/", __SLASH__, $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_brand_info["image"]);
									}
								}

								//---------------------------------------------------------------------------------
								// Считываем ссылки на дополнительные изображения для текущей вещи (одежды)
								$query_select_dress_image_additional_info = mysqli_query($param_db, "SELECT `image` FROM `".DB_TABLE_PREFIX."dressroom_dress_image` WHERE `dress_id`=".strval($row_query_select_dress_info["id"])." ORDER BY `id`" );

								// Строковая переменная, хранящая ссылки на дополнительные изображения для текущей вещи (одежды),
								// разделенные знаком "__PROBEL__"
								$string_dress_image_additional = "";

								// Если количество найденных строк больше 1, то информация о дополнительных изображениях
								// для текущей вещи (одежды) найдена
								if(mysqli_num_rows($query_select_dress_image_additional_info) > 0) {
									// В цикле обрабатываем все ссылки на дополнительные изображения для текущей вещи (одежды)
									while($row_query_select_dress_image_additional_info = mysqli_fetch_array($query_select_dress_image_additional_info)) {
										$string_dress_image_additional .= str_replace("/", __SLASH__, $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_image_additional_info["image"]).__PROBEL__;
									}

									// Удаляем из строки string_dress_image_additional последний символ "__PROBEL__"
									$string_dress_image_additional = substr($string_dress_image_additional, 0, strrpos($string_dress_image_additional, __PROBEL__ ));

									// Сохраняем ссылки на дополнительные изображения для текущей вещи (одежды)
									$dress["image_additional"] = $string_dress_image_additional;
								}

								//---------------------------------------------------------------------------------
								if($response["dress"] == null) {
									$response["dress"] = array();
								}

								if($response["dress"][$array_dress_types_key] == null) {
									$response["dress"][$array_dress_types_key] = array();
								}

								// Помещаем сведения о одежде в общий массив
								array_push($response["dress"][$array_dress_types_key], $dress);

								// Устанавливаем флаг, что хотя найдена информация хотя бы об одной одежде
								$response["success"] = 1;
							}
						}
					}
				}

				//-------------------------------------------------------------------------------------------------
				// Проверяем сохранен ли текущий набор одежды для текущего пользователя в качестве коллекции
				$current_collection_saved_id = CheckIsSaveCurrentCollectionForCurrentUser($param_db, $post_user_id, $array_dress_id_show_now);

				$response["collection_id"] = $current_collection_saved_id;
			}

			return $response;
		}

		//=================================================================================================================
		// Функция для считывания информации об одежде по умолчанию для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GoToDress($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] 			= null;
			$response["success"] 		= 0;
			$response["collection_id"] 	= 0;
			$response["message"] 		= "Информация об одежде отсутствует!";

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// Переменная, хранящая id текущей или крайней (первой или последней) одежды в зависимости от типа поддействия
				$post_dress_id = 0;

				// id текущего пользователя
				$post_user_id = 0;

				if(isset($param_dress["user_id"])) {
					if($param_dress["user_id"] != null) {
						$post_user_id = intval(trim(urldecode(strval($param_dress["user_id"]))));
					}
				}

				$post_action_go_to_dress_sybtype = 0;

				if(isset($param_dress["action_go_to_dress_sybtype"])) {
					if($param_dress["action_go_to_dress_sybtype"] != null) {
						$post_action_go_to_dress_sybtype = intval(trim(urldecode(strval($param_dress["action_go_to_dress_sybtype"]))));
					}
				}

				// Определяем количество вещей (одежды), информацию о которых необходимо одновременно скачать из БД
				$post_count_dress_read_from_db = 1;

				if(isset($param_dress["count_dress_read_from_db"])) {
					if($param_dress["count_dress_read_from_db"] != null) {
						$post_count_dress_read_from_db = intval(trim(urldecode(strval($param_dress["count_dress_read_from_db"]))));
					}
				}

				// Переменная, содержащая запрос к БД на считывание информации об одежде
				$sql_select_dress_info = null;

				// Массив, хранящий id всей одежды, входящей в состав текущего набора одежды
				$array_dress_id_in_current_collection = null;

				// Если текущее действие - КЛИК ПАЛЬЦЕМ ПО ИЗОБРАЖЕНИЮ ОДЕЖДЫ	
				if($post_action_go_to_dress_sybtype == 1) {
					// Если передан параметр dress_id, определяющий id одежды, информацию о которой необходимо считать
					$post_dress_id = 0;

					if(isset($param_dress["dress_id"])) {
						if($param_dress["dress_id"] != null) {
							$post_dress_id = intval(trim(urldecode(strval($param_dress["dress_id"]))));
						}
					}

					// Формируем запрос к БД на считывание информации об одежде
					$sql_select_dress_info = "SELECT `id`, `catid`, `title`, `alias`, `for_who`, `type`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".strval($post_dress_id)." LIMIT ".strval($post_count_dress_read_from_db);

					// Формируем массив, хранящий id всей одежды, входящей в состав текущего набора одежды
					$array_dress_id_in_current_collection = array();
					array_push($array_dress_id_in_current_collection, $post_dress_id);
				}
				// Иначе, если текущее действие - ЛИСТАНИЕ ОДЕЖДЫ ПАЛЬЦЕМ
				else if($post_action_go_to_dress_sybtype == 2) {
					// Если передан параметр dress_id, определяющий id крайней (первой или последней) одежды
					$post_dress_id = 0;

					if(isset($param_dress["dress_id"])) {
						if($param_dress["dress_id"] != null) {
							$post_dress_id = intval(trim(urldecode(strval($param_dress["dress_id"]))));
						}
					}

					// Параметр, определяющий направление листания
					$post_swipe_direction = 0;

					if(isset($param_dress["swipe_direction"])) {
						if($param_dress["swipe_direction"] != null) {
							$post_swipe_direction = intval(trim(urldecode(strval($param_dress["swipe_direction"]))));
						}
					}

					if($post_dress_id > 0) {
						// Переменные, содержащие параметры текущей одежды, id которой передан в качестве параметра
						$current_dress_catid   = null;		// id категории для текущей вещи
						$current_dress_for_who = null;		// для кого предназначены текущие вещи
						$current_dress_type    = null;		// тип текущих вещей (головные уборы, обувь и т.д.)
						$current_dress_color   = null;		// цвет текущей вещи
						$current_dress_style   = null;		// стиль текущей вещи

						// Считываем из БД параметры для текущей одежды, id которой передан в качестве параметра
						$query_select_current_dress_info = mysqli_query($param_db, "SELECT `catid`, `for_who`, `type`, `color`, `style` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `id`=".strval($post_dress_id)." LIMIT 1");

						// Если информация о данной вещи найдена
						if(mysqli_num_rows($query_select_current_dress_info) > 0) {
							$row_query_select_current_dress_info = mysqli_fetch_array($query_select_current_dress_info);

							// Извлекаем параметры для текущей одежды
							$current_dress_catid   = $row_query_select_current_dress_info["catid"];			// id категории для текущей вещи
							$current_dress_for_who = $row_query_select_current_dress_info["for_who"];		// для кого предназначены текущие вещи
							$current_dress_type    = $row_query_select_current_dress_info["type"];			// тип текущих вещей (головные уборы, обувь и т.д.)
							$current_dress_color   = $row_query_select_current_dress_info["color"];			// цвет текущей вещи
							$current_dress_style   = $row_query_select_current_dress_info["style"];			// стиль текущей вещи
						}

						//--------------------------------------------------------------------------------------------------
						// Формируем запрос к БД на считывание информации о предыдущей или последующей одежде в зависимости от направления листания
						$sql_select_dress_info = "SELECT `id`, `catid`, `title`, `alias`, `for_who`, `type`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE ";

						// Добавляем к запросу параметры текущей одежды

						// id категории для текущей вещи
						if($current_dress_catid != null) {
							$sql_select_dress_info .= "`catid`=".strval($current_dress_catid)." AND ";
						}

						// Для кого предназначены текущие вещи
						if($current_dress_for_who != null) {
							$sql_select_dress_info .= "`for_who`='".$current_dress_for_who."' AND ";
						}

						// Тип текущих вещей (головные уборы, обувь и т.д.)
						if($current_dress_type != null) {
							$sql_select_dress_info .= "`type`='".$current_dress_type."' AND ";
						}

						// Цвет текущей вещи
						if($current_dress_color != null) {
							$sql_select_dress_info .= "`color`='".$current_dress_color."' AND ";
						}

						// Стиль текущей вещи
						if($current_dress_style != null) {
							$sql_select_dress_info .= "`style`='".$current_dress_style."' AND ";
						}

						// Если направление листания - слева направо
						if($post_swipe_direction == 1) {
							// Считываем информацию о предыдущей одежде
							$sql_select_dress_info .= "`id`<".strval($post_dress_id)." ORDER BY `id` DESC LIMIT ".strval($post_count_dress_read_from_db);
						}
						// Иначе, если направление листания - справа налево
						else if($post_swipe_direction == 2) {
							// Считываем информацию о следующей одежде
							$sql_select_dress_info .= "`id`>".strval($post_dress_id)." ORDER BY `id` LIMIT ".strval($post_count_dress_read_from_db);
						}
					}
				}
				// Иначе, если текущий тип поддействия - СЧИТЫВАНИЕ ОДЕЖДЫ ДЛЯ ДРУГИХ ПАРАМЕТРОВ
				else if($post_action_go_to_dress_sybtype == 3) {
					// Если на сервер переданы соответствующие параметры
					$post_catid = 0;

					if(isset($param_dress["catid"])) {
						if($param_dress["catid"] != null) {
					 		$post_catid = intval(trim(urldecode(strval($param_dress["catid"]))));
					 	}
					}

					$post_for_who = null;

					if(isset($param_dress["for_who"])) {
						if($param_dress["for_who"] != null) {
							$post_for_who = trim(urldecode(strval($param_dress["for_who"])));
						}
					}

					$post_type = null;

					if(isset($param_dress["type"])) {
						if($param_dress["type"] != null) {
							$post_type = trim(urldecode(strval($param_dress["type"])));
						}
					}

					$post_color = null;

					if(isset($param_dress["color"])) {
						if($param_dress["color"] != null) {
							$post_color = trim(urldecode(strval($param_dress["color"])));
						}
					}

					$post_style = null;

					if(isset($param_dress["style"])) {
						if($param_dress["style"] != null) {
							$post_style = trim(urldecode(strval($param_dress["style"])));
						}
					}

					// Формируем запрос к БД на считывание информации об одежде
					$sql_select_dress_info = "SELECT `id`, `catid`, `title`, `alias`, `for_who`, `type`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($post_catid)." AND `for_who`='".$post_for_who."' AND `type`='".$post_type."' AND `color`='".$post_color."'";

					if($post_style != "no" && $post_style != "all") {
						$sql_select_dress_info .= " AND `style`='".$post_style."'";
					}

					$sql_select_dress_info .= " ORDER BY `id` LIMIT ".strval($post_count_dress_read_from_db);
				}
				// Иначе, если текущий тип поддействия - СЧИТЫВАНИЕ ОДЕЖДЫ ДЛЯ ДРУГОЙ КАТЕГОРИИ
				else if($post_action_go_to_dress_sybtype == 4) {
					$post_catid = 0;

					if(isset($param_dress["catid"])) {
						if($param_dress["catid"] != null) {
					 		$post_catid = intval(trim(urldecode(strval($param_dress["catid"]))));
					 	}
					}

					$post_for_who = null;

					if(isset($param_dress["for_who"])) {
						if($param_dress["for_who"] != null) {
							$post_for_who = trim(urldecode(strval($param_dress["for_who"])));
						}
					}

					$post_type = null;

					if(isset($param_dress["type"])) {
						if($param_dress["type"] != null) {
							$post_type = trim(urldecode(strval($param_dress["type"])));
						}
					}

					// Формируем запрос к БД на считывание информации об одежде
					$sql_select_dress_info = "SELECT `id`, `catid`, `title`, `alias`, `for_who`, `type`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($post_catid)." AND `for_who`='".$post_for_who."' AND `type`='".$post_type."' ORDER BY `id` LIMIT ".strval($post_count_dress_read_from_db);
				}

				//--------------------------------------------------------------------------------------------------
				// Формируем массив, хранящий id всей одежды, входящей в состав текущего набора одежды
				if($post_action_go_to_dress_sybtype == 3 || $post_action_go_to_dress_sybtype == 4) {
					// Формируем массив всех возможных типов одежды
					// У данного массива ключ - тип одежды, а значение - присутствует или нет данный тип одежды в текущей коллекции
					$array_dress_types 				= array();
					$array_dress_types["head"] 		= false;
					$array_dress_types["body"] 		= false;
					$array_dress_types["leg"] 		= false;
					$array_dress_types["foot"] 		= false;
					$array_dress_types["accessory"] = false;

					//-----------------------------------------------------------------------------------------------
					// Список id одежды из типа "Головные уборы"
					$post_dress_id_for_head = "";

					if(isset($param_dress["head"])) {
						if($param_dress["head"] != null) {
							$array_dress_types["head"] = true;
							$post_dress_id_for_head = trim(urldecode(strval($param_dress["head"])));
						}
					}

					// Список id одежды из типа "Одежда для тела"
					$post_dress_id_for_body = "";

					if(isset($param_dress["body"])) {
						if($param_dress["body"] != null) {
							$array_dress_types["body"] = true;
							$post_dress_id_for_body = trim(urldecode(strval($param_dress["body"])));
						}
					}

					// Список id одежды из типа "Одежда для ног"
					$post_dress_id_for_leg = "";

					if(isset($param_dress["leg"])) {
						if($param_dress["leg"] != null) {
							$array_dress_types["leg"] = true;
							$post_dress_id_for_leg = trim(urldecode(strval($param_dress["leg"])));
						}
					}

					// Список id одежды из типа "Обувь"
					$post_dress_id_for_foot = "";

					if(isset($param_dress["foot"])) {
						if($param_dress["foot"] != null) {
							$array_dress_types["foot"] = true;
							$post_dress_id_for_foot = trim(urldecode(strval($param_dress["foot"])));
						}
					}

					// Список id одежды из типа "Аксессуары"
					$post_dress_id_for_accessory = "";

					if(isset($param_dress["accessory"])) {
						if($param_dress["accessory"] != null) {
							$array_dress_types["accessory"] = true;
							$post_dress_id_for_accessory = trim(urldecode(strval($param_dress["accessory"])));
						}
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
				}

				//--------------------------------------------------------------------------------------------------
				// Если запрос к БД на считывание информации об одежде сформирован, то выполняем данный запрос
				if($sql_select_dress_info != null) {
					$query_select_dress_info = mysqli_query($param_db, $sql_select_dress_info);
				 
					// Если количество найденных строк больше 1, то информация о текущей одежде найдена
					if(mysqli_num_rows($query_select_dress_info) > 0) {
						// Порядковый номер текущей вещи (одежды)
						$dress_number = 0;

						while($row_query_select_dress_info = mysqli_fetch_array($query_select_dress_info)) {
							// Увеличиваем порядковый номер текущей вещи (одежды) на +1
							++ $dress_number;

							// Формируем массив, хранящий информацию о текущей вещи (одежде)
							$dress = array();
							$dress["id"] 	  	 	 = $row_query_select_dress_info["id"];										// id текущей вещи
							$dress["catid"] 	 	 = $row_query_select_dress_info["catid"];									// id категории для текущей вещи
							$dress["category_title"] = "";																		// название категории для текуще вещи
							$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_info["title"]);			// название текущей вещи
							$dress["alias"] 		 = $row_query_select_dress_info["alias"];									// алиас названия текущей вещи
							$dress["for_who"] 		 = $row_query_select_dress_info["for_who"];									// для кого предназначена текущая вещь (для мужчин, женщин или детей)
							$dress["type"] 	 		 = $row_query_select_dress_info["type"];									// тип текущей вещи (головной убор, обувь и т.д.)
							$dress["brand_id"] 		 = $row_query_select_dress_info["brand_id"];								// id бренда для текущей вещи
							$dress["brand_title"] 	 = "";																		// название бренда для текущей вещи

							// Ссылка на изображение для текущей вещи
							if($row_query_select_dress_info["image"] == null) {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else if(trim($row_query_select_dress_info["image"]) == "") {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else {
								$dress["image"] 	   = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info["image"]);
								$dress["image_width"]  = $row_query_select_dress_info["image_width"];
								$dress["image_height"] = $row_query_select_dress_info["image_height"];
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

							$dress["color"]				= $row_query_select_dress_info["color"];											// цвет текущей вещи
							$dress["style"]				= $row_query_select_dress_info["style"];											// стиль текущей вещи
							$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info["short_description"]);		// краткое описание для текущей вещи
							$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_info["description"]);			// полное описание для текущей вещи
							$dress["hits"] 				= $row_query_select_dress_info["hits"];												// уровень популярности текущей вещи
							$dress["version"] 			= $row_query_select_dress_info["version"];
							$dress["dress_default"] 	= $row_query_select_dress_info["dress_default"];

							//------------------------------------------------------------------------------------------
							// Устанавливаем флаг, определяющий будет ли отображена текущая вещь в первую очередь
							$dress["dress_show_now"] = 0;

							// Если тип действия - СЧИТЫВАНИЕ ИНФОРМАЦИИ О ВЕЩАХ ДЛЯ ДУРГИХ ПАРАМЕТРОВ ИЛИ ДРУГОЙ КАТЕГОРИИ
							if($post_action_go_to_dress_sybtype == 3 || $post_action_go_to_dress_sybtype == 4) {
								// Если порядковый номер текущей вещи (одежды) равен 1, то устанавливаем флаг, 
								// что текущая вещь будет отображена в первую очередь и сохраняем id текущей вещи
								if($dress_number == 1) {
									$dress["dress_show_now"] = 1;
									array_push($array_dress_id_in_current_collection, $row_query_select_dress_info["id"]);
								}
							}

							//------------------------------------------------------------------------------------------
							// Считываем название категории для текущей одежды
							$query_select_category_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_info["catid"])." LIMIT 1");
					 
					 		// Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
							if(mysqli_num_rows($query_select_category_info) > 0) {
								// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
								$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

								// Сохраняем название текущей категории
								$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
							}

							//------------------------------------------------------------------------------------------
							// Считываем название бренда для текущей одежды
							$query_select_brand_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_info["brand_id"]));
							 
						 	// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
							if(mysqli_num_rows($query_select_brand_info) > 0) {
								// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
								$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

								// Сохраняем название текущего бренда
								$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
							}

							//--------------------------------------------------------------------------------------------------------
							// Помещаем сведения о текущей категории в общий массив
							if($response["dress"] == null) {
								$response["dress"] = array();
							}

							array_push($response["dress"], $dress);
						}

						//----------------------------------------------------------------------------------------------------------
						// Проверяем сохранена ли текущая коллекция ранее для текущего пользователя
						$current_collection_saved_id = CheckIsSaveCurrentCollectionForCurrentUser($param_db, $post_user_id, $array_dress_id_in_current_collection);

						$response["collection_id"] = $current_collection_saved_id;
						
						//----------------------------------------------------------------------------------------------------------
						// Устанавливаем флаг, что хотя найдена информация о необходимой одежде
						$response["success"] = 1;
					}
				}
			}

			return $response;
		}

		//=================================================================================================================
		// Функция для считывания информации об одежде по умолчанию для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GoToDressDefault($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] 			= null;
			$response["success"] 		= 0;
			$response["collection_id"] 	= 0;
			$response["message"] 		= "Информация об одежде отсутствует!";

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// Определяем количество вещей (одежды), информацию о которых необходимо одновременно скачать из БД
				$post_count_dress_read_from_db = 5;

				if(isset($param_dress["count_dress_read_from_db"] ) ) {
					if($param_dress["count_dress_read_from_db"] != null) {
						$post_count_dress_read_from_db = intval(trim(urldecode(strval($param_dress["count_dress_read_from_db"]))));
					}
				}

				// Считываем id текущего пользователя
				$post_user_id = 0;
			
				if(isset($param_dress["user_id"])) {
					if($param_dress["user_id"] != null) {
						$post_user_id = intval(trim(urldecode(strval($param_dress["user_id"]))));
					}
				}

				// Задаем, для кого необходимо считать информацию об одежде
				$dress_for_who = "man";

				// Если передан параметр dress_for_who, определяющий для кого предназначены текущие вещи (одежда)
				if(isset($param_dress["for_who"])) {
					if($param_dress["for_who"] != null) {
						$post_dress_for_who = trim(urldecode(strval($param_dress["for_who"])));

						// Возможные параметр dress_for_who имеет допустимое значение
						// Возможные значения:
						// man 		  - одежда предназначена для мужчин
						// woman 	  - одежда предназначена для женщин
						// kid 		  - одежда предназначена для детей
						if($post_dress_for_who == "man" || $post_dress_for_who == "woman" || $post_dress_for_who == "kid") {
							$dress_for_who = $post_dress_for_who;
						}
					}
				}
	
				// Считываем информацию об одежде по умолчанию
				
				// Массив, содержащий возможные типы одежды
				$array_dress_type = array();
				$array_dress_type[] = "head";		// головные уборы
				$array_dress_type[] = "body";		// одежда, одеваемая на тело
				$array_dress_type[] = "leg";		// одежда, одеваемая на ноги
				$array_dress_type[] = "foot";		// обувь
				$array_dress_type[] = "accessory";	// аксессуары

				// Массив, хранящий id одежды, которая будет отображена в первую очередь
				$array_dress_id_show_now = array();

				// В цикле тут же перебираем все возможные типы одежды (головные уборы, обувь и т.д.)
				for($index_dress_type = 0; $index_dress_type < count( $array_dress_type ); $index_dress_type++) {
					// Параметры для запроса к БД
					$query_dress_for_who 	= $dress_for_who;								// для кого предназначены текущие вещи
					$query_dress_type    	= $array_dress_type[ $index_dress_type ];		// тип текущих вещей (головные уборы, обувь и т.д.)
					$query_dress_default 	= 1;											// логическая переменная, указывающая на то, что данная одежда используется для просмотра по умолчанию
					$query_dress_published	= 1;											// логическая переменная, указывающая на то, что информация о текущей вещи опубликована

					$query_select_dress_default = mysqli_query($param_db, "SELECT `id`, `catid`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `for_who`='".$query_dress_for_who."' AND `type`='".$query_dress_type."' AND `dress_default`=".strval($query_dress_default)." AND `published`=".strval($query_dress_published)." ORDER BY `id`");
			 
					// Если количество найденных строк <=0, то считываем информацию о первой вещи (одежде)
					// из общего массива вещей для текущего типа
					if(mysqli_num_rows($query_select_dress_default) <= 0) {
						$query_select_dress_default = mysqli_query($param_db, "SELECT `id`, `catid`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `for_who`='".$query_dress_for_who."' AND `type`='".$query_dress_type."' AND `published`=".strval($query_dress_published)." ORDER BY `id` LIMIT 1");
					}

					// Если количество найденных строк больше 1, то информация о необходимой одежде найдена
					if(mysqli_num_rows($query_select_dress_default) > 0) {
						$response["dress"][$query_dress_type] = array();

						// В цикле считываем всю информацию о найденной одежде по умолчанию
						while($row_query_select_dress_default = mysqli_fetch_array($query_select_dress_default)) {
							$dress = array();
							
							$dress["id"] 	  	 	 = $row_query_select_dress_default["id"];									// id текущей вещи
							$dress["for_who"] 		 = $query_dress_for_who;													// для кого предназначены текущие вещи
							$dress["type"] 			 = $query_dress_type;														// тип текущих вещей (головные уборы, обувь и т.д.)
							$dress["catid"] 	 	 = $row_query_select_dress_default["catid"];								// id категории для текущей вещи
							$dress["brand_id"] 	 	 = $row_query_select_dress_default["brand_id"];								// id бренда для текущей вещи
				
							// Ссылка на изображение для текущей вещи
							if($row_query_select_dress_default["image"] == null) {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else if(trim($row_query_select_dress_default["image"]) == "") {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else {
								$dress["image"]   	   = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_default["image"]);
								$dress["image_width"]  = $row_query_select_dress_default["image_width"];
								$dress["image_height"] = $row_query_select_dress_default["image_height"];
							}

							// Ссылка на изображение с обратной стороны для текущей вещи
							if($row_query_select_dress_default["image_back"] == null) {
								$dress["image_back"] 		= null;
								$dress["image_back_width"]  = 0;
								$dress["image_back_height"] = 0;
							}
							else if(trim($row_query_select_dress_default["image_back"] ) == "") {
								$dress["image_back"] 		= null;
								$dress["image_back_width"]  = 0;
								$dress["image_back_height"] = 0;
							}
							else {
								$dress["image_back"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_default["image_back"]);
								$dress["image_back_width"]  = $row_query_select_dress_default["image_back_width"];
								$dress["image_back_height"] = $row_query_select_dress_default["image_back_height"];
							}

							$dress["dress_default"] 	= $row_query_select_dress_default["dress_default"];										// флаг, указывающий является ли текущая вещь вещью, отображаемой по умолчанию
							$dress["dress_show_now"] 	= 1;																					// флаг, указывающий, что текущая вещь будет отображена в первую очередь

							// Запоминаем id текущей одежды
							array_push($array_dress_id_show_now, $row_query_select_dress_default["id"]);

							//------------------------------------------------------------------------------------------
							// Создаем временный массив параметров одежды для считывания id следующей и предыдущей вещей
							$array_temp_dress_params = array();
							$array_temp_dress_params["id"]		= $row_query_select_dress_default["id"];		// id текущей вещи
							$array_temp_dress_params["catid"]	= $row_query_select_dress_default["catid"];		// id категории для текущей вещи
							$array_temp_dress_params["for_who"] = $query_dress_for_who;							// для кого предназначены текущие вещи
							$array_temp_dress_params["type"] 	= $query_dress_type;							// тип текущих вещей (головные уборы, обувь и т.д.)
							$array_temp_dress_params["color"]	= $row_query_select_dress_default["color"];		// цвет текущей вещи
							$array_temp_dress_params["style"]	= $row_query_select_dress_default["style"];		// стиль текущей вещи

							//------------------------------------------------------------------------------------------
							// Считываем информацию об одежде (в количестве post_count_dress_read_from_db/2), находящихся перед текущей одеждой
							$polovina_count_dress_read_from_db = intval(($post_count_dress_read_from_db - 1) / 2);

							$query_select_dress_info_prev = mysqli_query($param_db, "SELECT `id`, `catid`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params["catid"])." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`<".strval($array_temp_dress_params["id"])." ORDER BY `id` DESC LIMIT ".$polovina_count_dress_read_from_db);

							// Предыдущих строк может оказаться меньще, чем $polovina_count_dress_read_from_db,
							// поэтому определяем насколько больше необходимо считать следующих строк
							$offset_count_next_dress_read_from_db = $polovina_count_dress_read_from_db - mysqli_num_rows($query_select_dress_info_prev);

							// Теперь считываем всю информацию о предыдущих вещах
							if(mysqli_num_rows($query_select_dress_info_prev) > 0) {
								// Массив, содержащий информацию об одежде, находящейся перед текущей одеждой
								$array_dress_prev_info = array();

								// Переменная, хранящая id предыдущей вещи, считанной последней
								$dress_prev_last_id = 0;

								while($row_query_select_dress_info_prev = mysqli_fetch_array($query_select_dress_info_prev)) {
									$dress_prev = array();
									
									$dress_prev["id"] 	  	 	  = $row_query_select_dress_info_prev["id"];								// id текущей вещи
									$dress_prev["for_who"] 		  = $array_temp_dress_params["for_who"];									// для кого предназначены текущие вещи
									$dress_prev["type"] 		  = $array_temp_dress_params["type"];										// тип текущих вещей (головные уборы, обувь и т.д.)
									$dress_prev["catid"] 	 	  = $row_query_select_dress_info_prev["catid"];								// id категории для текущей вещи
									$dress_prev["brand_id"] 	  = $row_query_select_dress_info_prev["brand_id"];							// id бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if($row_query_select_dress_info_prev["image"] == null) {
										$dress_prev["image"]   	   	= null;
										$dress_prev["image_width"]  = 0;
										$dress_prev["image_height"] = 0;
									}
									else if(trim($row_query_select_dress_info_prev["image"]) == "") {
										$dress_prev["image"]   		= null;
										$dress_prev["image_width"]  = 0;
										$dress_prev["image_height"] = 0;
									}
									else {
										$dress_prev["image"] 		= str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image"]);
										$dress_prev["image_width"]  = $row_query_select_dress_info_prev["image_width"];
										$dress_prev["image_height"] = $row_query_select_dress_info_prev["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if($row_query_select_dress_info_prev["image_back"] == null) {
										$dress_prev["image_back"] 		 = null;
										$dress_prev["image_back_width"]  = 0;
										$dress_prev["image_back_height"] = 0;
									}
									else if(trim($row_query_select_dress_info_prev["image_back"]) == "") {
										$dress_prev["image_back"] 		 = null;
										$dress_prev["image_back_width"]  = 0;
										$dress_prev["image_back_height"] = 0;
									}
									else {
										$dress_prev["image_back"] 		 = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image_back"]);
										$dress_prev["image_back_width"]  = $row_query_select_dress_info_prev["image_back_width"];
										$dress_prev["image_back_height"] = $row_query_select_dress_info_prev["image_back_height"];
									}

									$dress_prev["dress_default"] 	 = $row_query_select_dress_info_prev["dress_default"];										// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
									$dress_prev["dress_show_now"] 	 = 0;																						// флаг, указывающий, что текущая вещь НЕ будет отображена в первую очередь

									//-----------------------------------------------------------------------------------------
									// Запоминаем id текущей вещи
									$dress_prev_last_id = $row_query_select_dress_info_prev["id"];

									//------------------------------------------------------------------------------------------
									// Помещаем сведения о текущей категории в общий массив 
									if($array_dress_prev_info[ $query_dress_type ] == null) {
										$array_dress_prev_info[ $query_dress_type ] = array();
									}

									array_push($array_dress_prev_info[ $query_dress_type ], $dress_prev);
								}

								//--------------------------------------------------------------------------------------
								// Считываем id вещей, у которых id меньше, чем значение переменной $dress_prev_last_id
								$query_select_dress_info_from_first = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".strval($array_temp_dress_params["catid"])." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`<".strval($dress_prev_last_id)." ORDER BY `id`");

								// Если информация о необходимых вещах найдена
								if(mysqli_num_rows($query_select_dress_info_from_first) > 0) {
									while($row_query_select_dress_info_from_first = mysqli_fetch_array($query_select_dress_info_from_first)) {
										$dress_from_first = array();
										
										$dress_from_first["id"] = $row_query_select_dress_info_from_first["id"];		// id текущей вещи

										// Сохраняем информацию о данной вещи в возвращаемом массиве
										if( $response["dress"][ $array_temp_dress_params["type"] ] == null ) {
											$response["dress"][ $array_temp_dress_params["type"] ] = array();
										}

										array_push($response["dress"][ $array_temp_dress_params["type"] ], $dress_from_first);
									}
								}

								//--------------------------------------------------------------------------------------
								// В цикле (в обратном порядке) сохраняем информацию об одежде, находящейся перед текущей одеждой,
								// в возвращаемый массив
								foreach($array_dress_prev_info as $key_dress_prev_info => $value_dress_prev_info) {
									if( $response["dress"][ $key_dress_prev_info ] == null ) {
										$response["dress"][ $key_dress_prev_info ] = array();
									}

									// Так как из БД информация о вещах считывалась в обратном порядке
									for($index_dress = count( $value_dress_prev_info ) - 1; $index_dress >= 0; $index_dress--) {
										array_push( $response["dress"][ $key_dress_prev_info ], $value_dress_prev_info[ $index_dress ] );
									}
								}
							}

							//------------------------------------------------------------------------------------------
							// Помещаем сведения о текущей одежде в общий массив
							array_push($response["dress"][ $query_dress_type ], $dress);

							//------------------------------------------------------------------------------------------
							// Считываем информацию об одежде (в количестве polovina_count_dress_read_from_db + offset_count_next_dress_read_from_db), находящихся после текущей одеждой
							$query_select_dress_info_next = mysqli_query($param_db, "SELECT `id`, `catid`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".$array_temp_dress_params["catid"]." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`>".$array_temp_dress_params["id"]." ORDER BY `id` LIMIT ".($polovina_count_dress_read_from_db + $offset_count_next_dress_read_from_db));

							// Теперь считываем всю информацию о следующих вещах
							if(mysqli_num_rows($query_select_dress_info_next) > 0) {
								while ( $row_query_select_dress_info_next = mysqli_fetch_array($query_select_dress_info_next)) {
									$dress_next = array();
									
									$dress_next["id"] 	  	 	  = $row_query_select_dress_info_next["id"];								// id текущей вещи
									$dress_next["for_who"] 		  = $array_temp_dress_params["for_who"];									// для кого предназначены текущие вещи
									$dress_next["type"] 		  = $array_temp_dress_params["type"];										// тип текущих вещей (головные уборы, обувь и т.д.)
									$dress_next["catid"] 	 	  = $row_query_select_dress_info_next["catid"];								// id категории для текущей вещи
									$dress_next["brand_id"] 	  = $row_query_select_dress_info_next["brand_id"];							// id бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if($row_query_select_dress_info_next["image"] == null) {
										$dress_next["image"]   		= null;
										$dress_next["image_width"]  = 0;
										$dress_next["image_height"] = 0;
									}
									else if(trim($row_query_select_dress_info_next["image"]) == "") {
										$dress_next["image"]   		= null;
										$dress_next["image_width"]  = 0;
										$dress_next["image_height"] = 0;
									}
									else {
										$dress_next["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image"]);
										$dress_next["image_width"]  = $row_query_select_dress_info_next["image_width"];
										$dress_next["image_height"] = $row_query_select_dress_info_next["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if($row_query_select_dress_info_next["image_back"] == null) {
										$dress_next["image_back"] 		 = null;
										$dress_next["image_back_width"]  = 0;
										$dress_next["image_back_height"] = 0;
									}
									else if(trim($row_query_select_dress_info_next["image_back"]) == "") {
										$dress_next["image_back"] 		 = null;
										$dress_next["image_back_width"]  = 0;
										$dress_next["image_back_height"] = 0;
									}
									else {
										$dress_next["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image_back"]);
										$dress_next["image_back_width"]  = $row_query_select_dress_info_next["image_back_width"];
										$dress_next["image_back_height"] = $row_query_select_dress_info_next["image_back_height"];
									}

									$dress_next["dress_default"] 	 = $row_query_select_dress_info_next["dress_default"];										// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
									$dress_next["dress_show_now"] 	 = 0;																						// флаг, указывающий, что текущая вещь НЕ будет отображена в первую очередь

									//------------------------------------------------------------------------------------------
									// Помещаем сведения о текущей категории в общий массив
									array_push($response["dress"][ $query_dress_type ], $dress_next);
								}
							}
						}

						// Устанавливаем флаг, что хотя найдена информация хотя бы об одной одежде по умолчанию
						$response["success"] = 1;
					}
				}
						
				// Проверяем сохранен ли набор одежды, который будет отображен в первую очередь,
				// для текущего пользователя в качестве коллекции
				$response["collection_id"] = CheckIsSaveCurrentCollectionForCurrentUser($param_db, $post_user_id, $array_dress_id_show_now);
			}

			return $response;
		}

		//=================================================================================================================
		// Функция для считывания информации об одежде по умолчанию для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GoToDressLastView($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] 			= null;
			$response["success"] 		= 0;
			$response["collection_id"] 	= 0;
			$response["message"] 		= "Информация об одежде отсутствует!";

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// Считываем id текущего пользователя
				$post_user_id = 0;
					
				if(isset($param_dress["user_id"])) {
					if($param_dress["user_id"] != null) {
						$post_user_id = intval(trim(urldecode(strval($param_dress["user_id"]))));
					}
				}

				// Если передан параметр dress_id, определяющий id одежды, информацию о которой необходимо считать
				$post_dress_id = "";
				
				if(isset($param_dress["dress_id"])) {
					if($param_dress["dress_id"] != null) {
						$post_dress_id = trim(urldecode(strval($param_dress["dress_id"])));
					}
				}

				// Задаем, для кого необходимо считать информацию об одежде
				$post_dress_for_who = "man";

				// Если передан параметр dress_for_who, определяющий для кого предназначены текущие вещи (одежда)
				if(isset($param_dress["for_who"])) {
					if($param_dress["for_who"] != null) {
						$post_dress_for_who = trim(urldecode(strval($param_dress["for_who"])));
					}
				}

				// Продолжаем дальше только при условии, что переданный параметр dress_for_who имеет допустимое значение
				// Возможные значения:
				// man 		  - одежда предназначена для мужчин
				// woman 	  - одежда предназначена для женщин
				// kid 		  - одежда предназначена для детей

				// Продолжаем дальнейшую обработку при условии, что правильно передан параметр post_dress_for_who
				if(($post_dress_for_who == "man" || $post_dress_for_who == "woman" || $post_dress_for_who == "kid") && $post_dress_id != "") {
					// Определяем количество вещей (одежды), информацию о которых необходимо одновременно скачать из БД
					$post_count_dress_read_from_db = 5;

					if(isset($param_dress["count_dress_read_from_db"])) {
						if($param_dress["count_dress_read_from_db"] != null) {
							$post_count_dress_read_from_db = intval(trim(urldecode(strval($param_dress["count_dress_read_from_db"]))));
						}
					}
			
					// Массив, хранящий id одежды, которая будет отображена в первую очередь
					$array_dress_id_show_now = array();

					// В качестве id последней просматриваемой одежды может быть передано более одного id, 
					// которые разделены знаком тройного подчеркивания "___"
					$array_dress_id = explode("___", $post_dress_id);

					// Формируем запрос на считывание информации об одежде, id которой присутствуют в массиве array_dress_id
					$sql_select_dress_from_array_dress_id = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE ";

					for($index_dress_from_array_dress_id = 0; $index_dress_from_array_dress_id < count($array_dress_id); $index_dress_from_array_dress_id++) {
						$sql_select_dress_from_array_dress_id .= "`id`=".strval($array_dress_id[ $index_dress_from_array_dress_id ]);

						// Если текущий элемент - не последний
						if($index_dress_from_array_dress_id < count($array_dress_id) - 1) {
							$sql_select_dress_from_array_dress_id .= " OR ";
						}
					}

					$sql_select_dress_from_array_dress_id .= " AND `for_who`='".$post_dress_for_who."' AND `published`=1";

					$query_select_dress_from_array_dress_id = mysqli_query($param_db, $sql_select_dress_from_array_dress_id);
					
					// Если количество найденных строк совпадает с количеством элементов в масиве array_dress_id,
					// значит информация обо всех необходимых вещах найдена
					if(mysqli_num_rows($query_select_dress_from_array_dress_id) > 0) {
						// В цикле считываем всю информацию о найденной одежде
						while ($row_query_select_dress_from_array_dress_id = mysqli_fetch_array($query_select_dress_from_array_dress_id)) {
							$dress = array();

							$dress["id"] 	  	 	 = $row_query_select_dress_from_array_dress_id["id"];										// id текущей вещи
							$dress["catid"] 	 	 = $row_query_select_dress_from_array_dress_id["catid"];									// id категории для текущей вещи
							$dress["for_who"] 	 	 = $row_query_select_dress_from_array_dress_id["for_who"];									// для кого предназначены текущие вещи
							$dress["type"] 	 	 	 = $row_query_select_dress_from_array_dress_id["type"];										// тип текущих вещей (головные уборы, обувь и т.д.)
							$dress["category_title"] = "";																						// название категории для текуще вещи
							$dress["title"]   	 	 = iconv("cp1251", "utf-8", $row_query_select_dress_from_array_dress_id["title"]);			// название текущей вещи
							$dress["alias"] 	 	 = $row_query_select_dress_from_array_dress_id["alias"];									// алиас названия текущей вещи
							$dress["brand_id"] 	 	 = $row_query_select_dress_from_array_dress_id["brand_id"];									// id бренда для текущей вещи
							$dress["brand_title"] 	 = "";																						// название бренда для текущей вещи

							// Ссылка на изображение для текущей вещи
							if($row_query_select_dress_from_array_dress_id["image"] == null) {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else if(trim($row_query_select_dress_from_array_dress_id["image"]) == "") {
								$dress["image"]   	   = null;
								$dress["image_width"]  = 0;
								$dress["image_height"] = 0;
							}
							else {
								$dress["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_from_array_dress_id["image"]);
								$dress["image_width"]  = $row_query_select_dress_from_array_dress_id["image_width"];
								$dress["image_height"] = $row_query_select_dress_from_array_dress_id["image_height"];
							}

							// Ссылка на изображение с обратной стороны для текущей вещи
							if($row_query_select_dress_from_array_dress_id["image_back"] == null) {
								$dress["image_back"] 		= null;
								$dress["image_back_width"]  = 0;
								$dress["image_back_height"] = 0;
							}
							else if(trim( $row_query_select_dress_from_array_dress_id["image_back"]) == "") {
								$dress["image_back"] 		= null;
								$dress["image_back_width"]  = 0;
								$dress["image_back_height"] = 0;
							}
							else {
								$dress["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_from_array_dress_id["image_back"]);
								$dress["image_back_width"]  = $row_query_select_dress_from_array_dress_id["image_back_width"];
								$dress["image_back_height"] = $row_query_select_dress_from_array_dress_id["image_back_height"];
							}

							$dress["color"]				= $row_query_select_dress_from_array_dress_id["color"];												// цвет текущей вещи
							$dress["style"]				= $row_query_select_dress_from_array_dress_id["style"];												// стиль текущей вещи
							$dress["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_from_array_dress_id["short_description"]);		// краткое описание для текущей вещи
							$dress["description"] 		= iconv("cp1251", "utf-8", $row_query_select_dress_from_array_dress_id["description"]);				// полное описание для текущей вещи
							$dress["hits"] 				= $row_query_select_dress_from_array_dress_id["hits"];												// уровень популярности текущей вещи
							$dress["version"] 			= $row_query_select_dress_from_array_dress_id["version"];											// версия информации о текущей вещи
							$dress["dress_default"] 	= $row_query_select_dress_from_array_dress_id["dress_default"];										// флаг, указывающий является ли текущая вещь вещью, отображаемой по умолчанию
							$dress["dress_show_now"] 	= 1;																								// флаг, указывающий, что текущая вщь будет отображена в первую очередь
							$dress["info_is_full"]		= 1;																								// флаг, указывающий, что информация о текущей вещи является полной

							// Запоминаем id текущей одежды
							array_push($array_dress_id_show_now, $row_query_select_dress_from_array_dress_id["id"]);

							//------------------------------------------------------------------------------------------
							// Считываем название категории для текущей одежды
							$query_select_category_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."categories` WHERE `id`=".strval($row_query_select_dress_from_array_dress_id["catid"])." LIMIT 1");
		 
							 // Если количество найденных строк больше 1, то информация о текущей категории одежды найдена
							if(mysqli_num_rows($query_select_category_info) > 0) {
								// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
								$row_query_select_category_info = mysqli_fetch_array($query_select_category_info);

								// Сохраняем название текущей категории
								$dress["category_title"] = iconv("cp1251", "utf-8", $row_query_select_category_info['title']);
							}

							//------------------------------------------------------------------------------------------
							// Считываем название бренда для текущей одежды
							$query_select_brand_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".strval($row_query_select_dress_from_array_dress_id["brand_id"])." LIMIT 1");
				 
							 // Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
							if(mysqli_num_rows($query_select_brand_info) > 0) {
								// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
								$row_query_select_brand_info = mysqli_fetch_array($query_select_brand_info);

								// Сохраняем название текущего бренда
								$dress["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_brand_info['title']);
							}

							//------------------------------------------------------------------------------------------
							if( $response["dress"][ $row_query_select_dress_from_array_dress_id["type"] ] == null ) {
								$response["dress"][ $row_query_select_dress_from_array_dress_id["type"] ] = array();
							}

							//------------------------------------------------------------------------------------------
							// Создаем временный массив параметров одежды для считывания id следующей и предыдущей вещей
							$array_temp_dress_params = array();
							$array_temp_dress_params["id"]		= $row_query_select_dress_from_array_dress_id["id"];		// id текущей вещи
							$array_temp_dress_params["catid"]	= $row_query_select_dress_from_array_dress_id["catid"];		// id категории для текущей вещи
							$array_temp_dress_params["for_who"] = $row_query_select_dress_from_array_dress_id["for_who"];	// для кого предназначены текущие вещи
							$array_temp_dress_params["type"] 	= $row_query_select_dress_from_array_dress_id["type"];		// тип текущих вещей (головные уборы, обувь и т.д.)
							$array_temp_dress_params["color"]	= $row_query_select_dress_from_array_dress_id["color"];		// цвет текущей вещи
							$array_temp_dress_params["style"]	= $row_query_select_dress_from_array_dress_id["style"];		// стиль текущей вещи

							//------------------------------------------------------------------------------------------
							// Считываем информацию об одежде (в количестве post_count_dress_read_from_db/2), находящихся перед текущей одеждой
							$polovina_count_dress_read_from_db = intval( ( $post_count_dress_read_from_db - 1 ) / 2 );

							$query_select_dress_info_prev = mysqli_query($param_db, "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".$array_temp_dress_params["catid"]." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`<".$array_temp_dress_params["id"]." ORDER BY `id` DESC LIMIT ".$polovina_count_dress_read_from_db);

							// Предыдущих строк может оказаться меньще, чем $polovina_count_dress_read_from_db,
							// поэтому определяем насколько больше необходимо считать следующих строк
							$offset_count_next_dress_read_from_db = $polovina_count_dress_read_from_db - mysqli_num_rows($query_select_dress_info_prev);

							// Теперь считываем всю информацию о предыдущих вещах
							if( mysqli_num_rows($query_select_dress_info_prev) > 0 ) {
								// Массив, содержащий информацию об одежде, находящейся перед текущей одеждой
								$array_dress_prev_info = array();

								// Переменная, хранящая id предыдущей вещи, считанной последней
								$dress_prev_last_id = 0;

								while ( $row_query_select_dress_info_prev = mysqli_fetch_array( $query_select_dress_info_prev ) ) {
									$dress_prev = array();

									$dress_prev["id"] 	  	 	  = $row_query_select_dress_info_prev["id"];								// id текущей вещи
									$dress_prev["for_who"] 	 	  = $array_temp_dress_params["for_who"];									// для кого предназначены текущие вещи
									$dress_prev["type"] 	 	  = $array_temp_dress_params["type"];										// тип текущих вещей (головные уборы, обувь и т.д.)
									$dress_prev["catid"] 	 	  = $row_query_select_dress_info_prev["catid"];								// id категории для текущей вещи
									$dress_prev["category_title"] = $dress["category_title"];												// название категории для текуще вещи
									$dress_prev["title"]   	 	  = iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["title"]);	// название текущей вещи
									$dress_prev["alias"] 	 	  = $row_query_select_dress_info_prev["alias"];								// алиас названия текущей вещи
									$dress_prev["brand_id"] 	  = $row_query_select_dress_info_prev["brand_id"];							// id бренда для текущей вещи
									$dress_prev["brand_title"] 	  = "";																		// название бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if( $row_query_select_dress_info_prev["image"] == null ) {
										$dress_prev["image"]   		= null;
										$dress_prev["image_width"]  = 0;
										$dress_prev["image_height"] = 0;
									}
									else if( trim( $row_query_select_dress_info_prev["image"] ) == "" ) {
										$dress_prev["image"]   		= null;
										$dress_prev["image_width"]  = 0;
										$dress_prev["image_height"] = 0;
									}
									else {
										$dress_prev["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image"]);
										$dress_prev["image_width"]  = $row_query_select_dress_info_prev["image_width"];
										$dress_prev["image_height"] = $row_query_select_dress_info_prev["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if( $row_query_select_dress_info_prev["image_back"] == null ) {
										$dress_prev["image_back"] 		 = null;
										$dress_prev["image_back_width"]  = 0;
										$dress_prev["image_back_height"] = 0;
									}
									else if( trim( $row_query_select_dress_info_prev["image_back"] ) == "" ) {
										$dress_prev["image_back"] 		 = null;
										$dress_prev["image_back_width"]  = 0;
										$dress_prev["image_back_height"] = 0;
									}
									else {
										$dress_prev["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_prev["image_back"]);
										$dress_prev["image_back_width"]  = $row_query_select_dress_info_prev["image_back_width"];
										$dress_prev["image_back_height"] = $row_query_select_dress_info_prev["image_back_height"];
									}

									$dress_prev["color"]			 = $row_query_select_dress_info_prev["color"];												// цвет текущей вещи
									$dress_prev["style"]			 = $row_query_select_dress_info_prev["style"];												// стиль текущей вещи
									$dress_prev["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["short_description"]);		// краткое описание для текущей вещи
									$dress_prev["description"] 		 = iconv("cp1251", "utf-8", $row_query_select_dress_info_prev["description"]);				// полное описание для текущей вещи
									$dress_prev["hits"] 			 = $row_query_select_dress_info_prev["hits"];												// уровень популярности текущей вещи
									$dress_prev["version"] 			 = $row_query_select_dress_info_prev["version"];											// версия информации о текущей вещи
									$dress_prev["dress_default"] 	 = $row_query_select_dress_info_prev["dress_default"];										// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
									$dress_prev["dress_show_now"] 	 = 0;																						// флаг, указывающий, что текущая вещь НЕ будет отображена в первую очередь
									$dress_prev["info_is_full"]		 = 1;																						// флаг, указывающий, что информация о текущей вещи является полной

									//------------------------------------------------------------------------------------------
									// Считываем название бренда для текущей одежды
									$query_select_dress_prev_brand_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".$row_query_select_dress_info_prev["brand_id"]." LIMIT 1");
				 
									// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
									if ( mysqli_num_rows( $query_select_dress_prev_brand_info ) > 0 ) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_dress_prev_brand_info = mysqli_fetch_array( $query_select_dress_prev_brand_info );

										// Сохраняем название текущего бренда
										$dress_prev["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_dress_prev_brand_info['title']);
									}

									// Запоминаем id текущей вещи
									$dress_prev_last_id = $row_query_select_dress_info_prev["id"];

									//------------------------------------------------------------------------------------------
									// Помещаем сведения о текущей категории в общий массив
									if( $array_dress_prev_info[ $array_temp_dress_params["type"] ] == null ) {
										$array_dress_prev_info[ $array_temp_dress_params["type"] ] = array();
									}

									array_push( $array_dress_prev_info[ $array_temp_dress_params["type"] ], $dress_prev );
								}

								//--------------------------------------------------------------------------------------
								// Считываем id вещей, у которых id меньше, чем значение переменной $dress_prev_last_id
								$query_select_dress_info_from_first = mysqli_query($param_db, "SELECT `id` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".$array_temp_dress_params["catid"]." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`<".$dress_prev_last_id." ORDER BY `id`");

								// Если информация о необходимых вещах найдена
								if( mysqli_num_rows( $query_select_dress_info_from_first ) > 0 )	{
									while( $row_query_select_dress_info_from_first = mysqli_fetch_array( $query_select_dress_info_from_first ) ) {
										$dress_from_first = array();

										$dress_from_first["id"] 			= $row_query_select_dress_info_from_first["id"];		// id текущей вещи
										$dress_from_first["info_is_full"]	= 0;													// флаг, указывающий, что информация о текущей вещи НЕ является полной

										// Сохраняем информацию о данной вещи в возвращаемом массиве
										if( $response["dress"][ $array_temp_dress_params["type"] ] == null ) {
											$response["dress"][ $array_temp_dress_params["type"] ] = array();
										}

										array_push( $response["dress"][ $array_temp_dress_params["type"] ], $dress_from_first );
									}
								}

								//--------------------------------------------------------------------------------------
								// В цикле (в обратном порядке) сохраняем информацию об одежде, находящейся перед текущей одеждой,
								// в возвращаемый массив
								foreach ( $array_dress_prev_info as $key_dress_prev_info => $value_dress_prev_info ) {
									if( $response["dress"][ $key_dress_prev_info ] == null ) {
										$response["dress"][ $key_dress_prev_info ] = array();
									}

									// Так как из БД информация о вещах считывалась в обратном порядке
									for( $index_dress = count( $value_dress_prev_info ) - 1; $index_dress >= 0; $index_dress-- ) {
										array_push( $response["dress"][ $key_dress_prev_info ], $value_dress_prev_info[ $index_dress ] );
									}
								}
							}

							//------------------------------------------------------------------------------------------
							// Помещаем сведения о текущей категории в общий массив
							array_push( $response["dress"][ $row_query_select_dress_from_array_dress_id["type"] ], $dress );

							//------------------------------------------------------------------------------------------
							// Считываем информацию об одежде (в количестве polovina_count_dress_read_from_db + offset_count_next_dress_read_from_db), находящихся после текущей одеждой
							$query_select_dress_info_next = mysqli_query($param_db, "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE `catid`=".$array_temp_dress_params["catid"]." AND `for_who`='".$array_temp_dress_params["for_who"]."' AND `type`='".$array_temp_dress_params["type"]."' AND `color`='".$array_temp_dress_params["color"]."' AND `style`='".$array_temp_dress_params["style"]."' AND `id`>".$array_temp_dress_params["id"]." ORDER BY `id` LIMIT ".($polovina_count_dress_read_from_db + $offset_count_next_dress_read_from_db));

							// Теперь считываем всю информацию о следующих вещах
							if( mysqli_num_rows( $query_select_dress_info_next ) > 0 ) {
								while ( $row_query_select_dress_info_next = mysqli_fetch_array( $query_select_dress_info_next ) ) {
									$dress_next = array();

									$dress_next["id"] 	  	 	  = $row_query_select_dress_info_next["id"];								// id текущей вещи
									$dress_next["for_who"] 	 	  = $array_temp_dress_params["for_who"];									// для кого предназначены текущие вещи
									$dress_next["type"] 	 	  = $array_temp_dress_params["type"];										// тип текущих вещей (головные уборы, обувь и т.д.)
									$dress_next["catid"] 	 	  = $row_query_select_dress_info_next["catid"];								// id категории для текущей вещи
									$dress_next["category_title"] = $dress["category_title"];												// название категории для текуще вещи
									$dress_next["title"]   	 	  = iconv("cp1251", "utf-8", $row_query_select_dress_info_next["title"]);	// название текущей вещи
									$dress_next["alias"] 	 	  = $row_query_select_dress_info_next["alias"];								// алиас названия текущей вещи
									$dress_next["brand_id"] 	  = $row_query_select_dress_info_next["brand_id"];							// id бренда для текущей вещи
									$dress_next["brand_title"] 	  = "";																		// название бренда для текущей вещи

									// Ссылка на изображение для текущей вещи
									if( $row_query_select_dress_info_next["image"] == null ) {
										$dress_next["image"]   		= null;
										$dress_next["image_width"]  = 0;
										$dress_next["image_height"] = 0;
									}
									else if( trim( $row_query_select_dress_info_next["image"] ) == "" ) {
										$dress_next["image"]   		= null;
										$dress_next["image_width"]  = 0;
										$dress_next["image_height"] = 0;
									}
									else {
										$dress_next["image"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image"]);
										$dress_next["image_width"]  = $row_query_select_dress_info_next["image_width"];
										$dress_next["image_height"] = $row_query_select_dress_info_next["image_height"];
									}

									// Ссылка на изображение с обратной стороны для текущей вещи
									if( $row_query_select_dress_info_next["image_back"] == null ) {
										$dress_next["image_back"] 		 = null;
										$dress_next["image_back_width"]  = 0;
										$dress_next["image_back_height"] = 0;
									}
									else if( trim( $row_query_select_dress_info_next["image_back"] ) == "" ) {
										$dress_next["image_back"] 		 = null;
										$dress_next["image_back_width"]  = 0;
										$dress_next["image_back_height"] = 0;
									}
									else {
										$dress_next["image_back"] = str_replace("/", "__SLASH__", $server_protocol.$_SERVER['SERVER_NAME']."/".$row_query_select_dress_info_next["image_back"]);
										$dress_next["image_back_width"]  = $row_query_select_dress_info_next["image_back_width"];
										$dress_next["image_back_height"] = $row_query_select_dress_info_next["image_back_height"];
									}

									$dress_next["color"]			 = $row_query_select_dress_info_next["color"];												// цвет текущей вещи
									$dress_next["style"]			 = $row_query_select_dress_info_next["style"];												// стиль текущей вещи
									$dress_next["short_description"] = iconv("cp1251", "utf-8", $row_query_select_dress_info_next["short_description"]);		// краткое описание для текущей вещи
									$dress_next["description"] 		 = iconv("cp1251", "utf-8", $row_query_select_dress_info_next["description"]);				// полное описание для текущей вещи
									$dress_next["hits"] 			 = $row_query_select_dress_info_next["hits"];												// уровень популярности текущей вещи
									$dress_next["version"] 			 = $row_query_select_dress_info_next["version"];											// версия информации о текущей вещи
									$dress_next["dress_default"] 	 = $row_query_select_dress_info_next["dress_default"];										// флаг, указывающий является ли текущая вещь вещью ПО УМОЛЧАНИЮ
									$dress_next["dress_show_now"] 	 = 0;																						// флаг, указывающий, что текущая вещь НЕ будет отображена в первую очередь
									$dress_next["info_is_full"]		 = 1;																						// флаг, указывающий, что информация о текущей вещи является полной

									//------------------------------------------------------------------------------------------
									// Считываем название бренда для текущей одежды
									$query_select_dress_next_brand_info = mysqli_query($param_db, "SELECT `title` FROM `".DB_TABLE_PREFIX."dressroom_brand` WHERE `id`=".$row_query_select_dress_info_next["brand_id"]." LIMIT 1");
				 
									// Если количество найденных строк больше 1, то информация о текущем бренде одежды найдена
									if ( mysqli_num_rows( $query_select_dress_next_brand_info ) > 0 ) {
										// Так как в результате может содержаться только одна строка, то считываем из результата только первую строку
										$row_query_select_dress_next_brand_info = mysqli_fetch_array( $query_select_dress_next_brand_info );

										// Сохраняем название текущего бренда
										$dress_next["brand_title"] = iconv("cp1251", "utf-8", $row_query_select_dress_next_brand_info['title']);
									}

									//------------------------------------------------------------------------------------------
									// Считываем id следующей и предыдущей вещей
									// Помещаем сведения о текущей категории в общий массив
									if( $response["dress"][ $array_temp_dress_params["type"] ] == null ) {
										$response["dress"][ $array_temp_dress_params["type"] ] = array();
									}
									
									array_push( $response["dress"][ $array_temp_dress_params["type"] ], $dress_next );
								}
							}
						}

						// Устанавливаем флаг, что хотя найдена информация хотя бы об одной одежде по умолчанию
						$response["success"] = 1;

						// Проверяем сохранен ли набор одежды, который будет отображен в первую очередь,
						// для текущего пользователя в качестве коллекции
						$response["collection_id"] = CheckIsSaveCurrentCollectionForCurrentUser($param_db, $post_user_id, $array_dress_id_show_now);
					}
				}
			}

			return $response;
		}

		//=================================================================================================================
		// Функция для считывания информации об одежде по умолчанию для текущего пользователя
		// Передаваемые параметры
		// param_db 	- ссылка на подключение к БД
		// param_dress	- массив, содержащий дополнительные параметры
		public function GoToDressDefaultOrLastView($param_db, $param_dress) {
			// Определяем протокол сервера
			$server_protocol = GetServerProtocol();

			// Массив, представляющий собой JSON-ответ на запрос
			$response = array();

			$response["dress"] 			= null;
			$response["success"] 		= 0;
			$response["collection_id"] 	= 0;
			$response["message"] 		= "Информация об одежде отсутствует!";

			// Если переданы все параметры
			if($param_db != null && $param_dress != null) {
				// Логическая переменная, определяющая необходимо просматривать последнюю одежду или одежду по умолчанию
				// Значение true - последняя просматриваемая одежда
				// Значение false - одежда по умолчанию
				$is_dress_last_view = false;

				// Считываем id текущего пользователя
				$post_user_id = 0;
					
				if(isset($param_dress["user_id"])) {
					if($param_dress["user_id"] != null) {
						$post_user_id = intval(trim(urldecode(strval($param_dress["user_id"]))));
					}
				}

				// Если передан параметр dress_id, определяющий id одежды, информацию о которой необходимо считать
				$post_dress_id = "";
				
				if(isset($param_dress["dress_id"])) {
					if($param_dress["dress_id"] != null) {
						$post_dress_id = trim(urldecode(strval($param_dress["dress_id"])));
					}
				}

				// Задаем, для кого необходимо считать информацию об одежде
				$dress_for_who = "man";

				// Если передан параметр dress_for_who, определяющий для кого предназначены текущие вещи (одежда)
				if(isset($param_dress["for_who"])) {
					if($param_dress["for_who"] != null) {
						$post_dress_for_who = trim(urldecode(strval($param_dress["for_who"])));

						// Возможные параметр dress_for_who имеет допустимое значение
						// Возможные значения:
						// man 		  - одежда предназначена для мужчин
						// woman 	  - одежда предназначена для женщин
						// kid 		  - одежда предназначена для детей
						if($post_dress_for_who == "man" || $post_dress_for_who == "woman" || $post_dress_for_who == "kid") {
							$dress_for_who = $post_dress_for_who;
						}
					}
				}

				// В качестве id последней просматриваемой одежды может быть передано более одного id, 
				// которые разделены знаком тройного подчеркивания "___"
				$array_dress_id = explode("___", $post_dress_id);

				// Формируем запрос на считывание информации об одежде, id которой присутствуют в массиве array_dress_id
				$sql_select_dress_from_array_dress_id = "SELECT `id`, `for_who`, `type`, `catid`, `title`, `alias`, `brand_id`, `image`, `image_width`, `image_height`, `image_back`, `image_back_width`, `image_back_height`, `color`, `style`, `short_description`, `description`, `hits`, `version`, `dress_default` FROM `".DB_TABLE_PREFIX."dressroom_dress` WHERE ";

				for($index_dress_from_array_dress_id = 0; $index_dress_from_array_dress_id < count($array_dress_id); $index_dress_from_array_dress_id++) {
					// Если это первый элемент
					if($index_dress_from_array_dress_id == 0) {
						$sql_select_dress_from_array_dress_id .= "(";
					}
					
					$sql_select_dress_from_array_dress_id .= "`id`=".strval($array_dress_id[$index_dress_from_array_dress_id]);

					// Если текущий элемент - не последний
					if($index_dress_from_array_dress_id < count($array_dress_id) - 1) {
						$sql_select_dress_from_array_dress_id .= " OR ";
					}
					// Иначе
					else {
						$sql_select_dress_from_array_dress_id .= ") ";
					}
				}

				$sql_select_dress_from_array_dress_id .= " AND `for_who`='".$dress_for_who."' AND `published`=1";

				$query_select_dress_from_array_dress_id = mysqli_query($param_db, $sql_select_dress_from_array_dress_id);

				// Если количество найденных строк совпадает с количеством элементов в масиве array_dress_id,
				// значит информация обо всех необходимых вещах найдена
				if(mysqli_num_rows($query_select_dress_from_array_dress_id) == count($array_dress_id) && count($array_dress_id) > 0) {
					$is_dress_last_view = true;
				}
	
				// Если осуществляется просмотр последней просматриваемой одежды
				if($is_dress_last_view == true) {
					$response = $this->GoToDressLastView($param_db, $param_dress);
				}
				// Иначе, осуществляем просмотр одежды по умолчанию
				else {
					$response = $this->GoToDressDefault($param_db, $param_dress);
				}
			}

			return $response;
		}
	}
?>