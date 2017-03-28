<?php 
 header('Content-Type: application/json');
 
 $user_id = $_POST['user_id'];
 $lesson_id = $_POST['lesson_id'];
 $text = $_POST['text'];
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "Insert into lessons_comments(`user_id`, `lesson_id`, `text`) values ('$user_id', '$lesson_id', '$text')";

	 
	 mysqli_query($con,$sql); 
	 
	 echo json_encode("Dodano", JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);