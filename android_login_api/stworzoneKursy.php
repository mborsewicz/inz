<?php 
   
 $user_id = $_GET['user_id']; 
 require_once 'include/DB_Connect.php';

 $db = new Db_Connect();
 $con = $db->connect();
 
 mysqli_set_charset($con, "utf8");
 

	 
	 //SQL query to fetch data of a range 
	 $sql = "SELECT id, title, price, image, short_description from courses where user_id = '$user_id'";
	 
	 //Getting result 
	 $result = mysqli_query($con,$sql); 
	 
	 //Adding results to an array 
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
	 //Displaying the array in json format 
	 echo json_encode($res, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_ERROR_UTF8);
	 
	 