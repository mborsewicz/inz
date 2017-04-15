<?php 
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();
 
 /*$kurs_id = $_REQUEST['kurs_id'];
 $user_id = $_REQUEST['user_id'];
 $title = $_REQUEST['title'];
 $description = $_REQUEST['description'];
 
 mysqli_set_charset($con, "utf8");

 $sql = "INSERT INTO courses_sections(`course_id`) VALUES ('$kurs_id')";
 mysqli_query($con,$sql); 
 $id_sekcji = mysqli_insert_id($con); */

 if($_SERVER['REQUEST_METHOD']=='POST'){
 $file_name = $_FILES['myFile']['name'];
 $file_size = $_FILES['myFile']['size'];
 $file_type = $_FILES['myFile']['type'];
 $temp_name = $_FILES['myFile']['tmp_name'];
 
 $location = "kursy/";
 //$location = $_SERVER['DOCUMENT_ROOT']."/android_login_api/kursy/";
 //$location = 'http://192.168.2.100/android_login_api/';
 
 if(move_uploaded_file($temp_name, $location.$file_name))
 {
	 echo "192.168.2.100/android_login_api/kursy".$file_name;
	 }
 else {
	 echo "http://192.168.2.100/android_login_api/kursy".$file_name;
 }
 }else{
 echo "Error";
 }
 
 //$sql2 = "INSERT INTO lessons(`user_id`, `section_id`, `video`, `title`, `description`, `is_enabled`) VALUES ('$user_id','$id_sekcji', '$file_name', '$title', '$description', 1)";
 //mysqli_query($con,$sql2)