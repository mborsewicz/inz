<?php 
 $course_id = $_GET['course_id']; 
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	mysqli_set_charset($con, "utf8");

	 $sql = "select l.id, l.description AS 'lesson_description', l.title AS 'lesson_title', l.video, l.free, l.is_enabled from lessons l
            join courses_sections cs ON l.section_id = cs.id
            join courses c ON cs.course_id = c.id
			join courses_categories cc ON cc.id = c.category_id
			where c.id = '$course_id'";
			

	 $result = mysqli_query($con,$sql); 

	 $res = array(); 
	 
	 while($row = mysqli_fetch_array($result)){
	 array_push($res, array(
	 "id"=>$row['id'],
	 "lesson_description"=>$row['lesson_description'],
	 "lesson_title"=>$row['lesson_title'],
	 "video"=>$row['video'],
	 "free"=>$row['free'],
	 "is_enabled"=>$row['is_enabled']
	 ));
	 }
	 

	 
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
