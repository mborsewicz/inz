<?php 
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

 	 $sql = "select * from courses_categories";
	mysqli_set_charset($con, "utf8");
	
	 $result = mysqli_query($con,$sql); 

	 $res = array(); 

	 $result = mysqli_query($con,$sql); 
	 
	 while($row = mysqli_fetch_array($result)){
	 array_push($res, array(
	 "id"=>$row['id'],
	 "title"=>$row['title'],
	 ));
	 }
	 

	 
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
