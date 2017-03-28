<?php 
 header('Content-Type: application/json');
 
 $user_id = $_POST['user_id'];
 $category_id = $_POST['category_id'];
 $title = $_POST['title'];
 $price= $_POST['price'];
 $short_description = $_POST['short_description'];
 $description = $_POST['description'];
 
 $response = array();
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "Insert into courses(`user_id`, `category_id`, `title`, `price`, `short_description`, `description`) values ('$user_id', '$category_id', '$title', '$price', '$short_description', '$description')";

	 
	 mysqli_query($con,$sql); 
	 $id_kursu =  mysqli_insert_id($con);
	 $response["id_kursu"] = $id_kursu;
	 
	 $sql2 = "Insert into courses_sections(`course_id`) values ('$id_kursu')";
	 mysqli_query($con,$sql2);
	 $id_sekcji =  mysqli_insert_id($con);
	 $response["id_sekcji"] = $id_sekcji;

	 mkdir('kursy\\'.$id_kursu, 0777, true);
	 

	 echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);