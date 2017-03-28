<?php 
 $course_id = $_GET['course_id']; 
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "select c.id, u.name ,c.text, c.created from users u 
			join lessons_comments c ON u.id = c.user_id
			join lessons l ON l.id = c.lesson_id
			where lesson_id = '$course_id' order by created DESC";
			

	 $result = mysqli_query($con,$sql); 

	 $res = array(); 
	 
	 while($row = mysqli_fetch_array($result)){
	 array_push($res, array(
	 "id"=>$row['id'],
	 "name"=>$row['name'],
	 "text"=>$row['text'],
	 "created"=>$row['created']
	 ));
	 }
	 

	 
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
