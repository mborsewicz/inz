<?php
 
require_once 'include/DB_Functions.php';

$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])) {
 
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
 
    // czy istnieje
    if ($db->isUserExisted($email)) {
        
        $response["error"] = TRUE;
        $response["error_msg"] = "Uzytkownik o podanych emailu istnieje " . $email;
        echo json_encode($response);
    } else {
        // tworzenie nowego uzytkownika
        $user = $db->storeUser($name, $email, $password);
        if ($user) {
            // success
            $response["error"] = FALSE;
            $response["uid"] = $user["unique_id"];
			$response["user"]["user_id"] = $user["id"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created"] = $user["created"];
            $response["user"]["modified"] = $user["modified"];
            echo json_encode($response);
        } else {
            
            $response["error"] = TRUE;
            $response["error_msg"] = "Nieznany blad!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Uzupelnij Imie, Email oraz Haslo!";
    echo json_encode($response);
}
?>