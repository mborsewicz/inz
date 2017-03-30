<?php 
 header('Content-Type: application/json');
 
 require_once 'include/DB_Connect.php';
 
 $db = new Db_Connect();
 
 $con = $db->connect();

	// Get image string posted from Android App
    $base=$_REQUEST['image'];
    $filename = $_REQUEST['filename'];
																								//TODO ID KURSU DODAC!!!!!!
    // Decode Image
    $binary=base64_decode($base);
    header('Content-Type: bitmap; charset=utf-8');
    // Images will be saved under 'www/imgupload/uplodedimages' folder
    $file = fopen('kursy/'.$filename, 'wb');  												//SCIEZKA KURSY/$IDKURSU!!!!!!!!!!!!!
    // Create File
    fwrite($file, $binary);
    fclose($file);
    echo 'Image upload complete, Please check your php file directory';