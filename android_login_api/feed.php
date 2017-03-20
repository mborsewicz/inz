<?php 
 
 //Getting the page number which is to be displayed  
 $page = $_GET['page']; 
 
 //Initially we show the data from 1st row that means the 0th row 
 $start = 0; 
 
 //Limit is 3 that means we will show 3 items at once
 $limit = 3; 
 
 //Importing the database connection 
 require_once 'include/DB_Connect.php';

 $db = new Db_Connect();
 $con = $db->connect();
 
 mysqli_set_charset($con, "utf8");
 
 //Counting the total item available in the database 
 $total = mysqli_num_rows(mysqli_query($con, "SELECT id from courses"));
 
 //We can go atmost to page number total/limit
 $page_limit = ceil($total/$limit); 
 
 //If the page number is more than the limit we cannot show anything 
 if($page<=$page_limit){
 
	 //Calculating start for every given page number 
	 $start = ($page - 1) * $limit; 
	 
	 //SQL query to fetch data of a range 
	 $sql = "SELECT id, title, price, image, short_description from courses limit $start, $limit";
	 
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
 }else{
            echo "over";
    }