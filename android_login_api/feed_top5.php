<?php 
 
 require_once 'include/DB_Connect.php';

 $db = new Db_Connect();
 $con = $db->connect();
 mysqli_set_charset($con, "utf8");
 
	 $sql = "SELECT id, title, price, image, short_description from courses limit 5";

	 $result = mysqli_query($con,$sql); 

	 $res = array(); 
	 
	 while($row = mysqli_fetch_array($result)){
	 array_push($res, array(
	 "id"=>$row['id'],
	 "title"=>$row['title'],
	 "price"=>$row['price'],
	 "image"=>$row['image'],
	 "short_description"=>$row['short_description']
	 ));
	 }
	 
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
