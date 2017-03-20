<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$response = array("error" => FALSE);
 
if (isset($_POST['email']) && isset($_POST['password'])) {

    $email = $_POST['email'];
    $password = $_POST['password'];
 
    $user = $db->getUserByEmailAndPassword($email, $password);
 
    if ($user != false) {
 
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
        $response["error_msg"] = "Haslo/Email bledne. Sproboj ponownie!";
        echo json_encode($response);
    }
} else {

    $response["error"] = TRUE;
    $response["error_msg"] = "Wpisz haslo/email!";
    echo json_encode($response);
}
?>