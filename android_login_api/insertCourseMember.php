<?php 
 $course_id = $_GET['course_id'];
 $user_id = $_GET['user_id'];
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "Insert into courses_members(`course_id`, `user_id`) values ('$course_id', '$user_id')";

	 mysqli_query($con,$sql); 
	  
	 $res = array(); 
	 
	 
	 array_push($res, array(
	 "dodano"
	 ));
	 
	 

	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);