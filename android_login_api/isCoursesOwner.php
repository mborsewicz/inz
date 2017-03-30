<?php 
 $course_id = $_GET['course_id']; 
 $user_id = $_GET['user_id'];
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "select count(1) as `show` from courses where id = '$course_id' and user_id = '$user_id'";

	 $result = mysqli_query($con,$sql); 

	 $res = array(); 
	 
	 while($row = mysqli_fetch_array($result)){
	 array_push($res, array(
	 "show"=>$row['show'],
	 ));
	 }
	
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
