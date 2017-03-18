package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by brsk on 2016-11-11.
 */
public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.38/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.0.38/android_login_api/register.php";

    //kursy
    public static final String DATA_URL = "http://192.168.0.38/android_login_api/feed.php?page=";
    public static final String DATA_URL_TOP5 = "http://192.168.0.38/android_login_api/feed_top5.php";
    public static final String URL_IMAGE = "http://192.168.0.38/android_login_api/";
    public static final String DATA_LESSONS_URL = "http://192.168.0.38/android_login_api/getLessons.php?course_id=";
    public static final String DATA_INSERT_COURSE_MEMBER = "http://192.168.0.38/android_login_api/insertCourseMember.php?course_id=";
    public static final String DATA_GET_BUTTON_INFO = "http://192.168.0.38/android_login_api/getButtonInfo.php?course_id=";
    //JSON TAGS
    public static final String TAG_IMAGE_URL = "image";
    public static final String TAG_NAME = "title";
    public static final String TAG_PUBLISHER = "price";
    public static final String TAG_ID = "id";
    public static final String TAG_SHORT_DESC = "short_description";

    public static final String TAG_LESSON_ID = "id";
    public static final String TAG_LESSON_lesson_description = "lesson_description";
    public static final String TAG_LESSON_lesson_title = "lesson_title";
    public static final String TAG_LESSON_video = "video";
    public static final String TAG_LESSON_free = "free";
    public static final String TAG_LESSON_is_enabled = "is_enabled";
    public static final String TAG_LESSON_image = "image";
    public static final String TAG_LESSON_course_title = "course_title";
    public static final String TAG_LESSON_course_price = "price";
    public static final String TAG_LESSON_course_description = "course_description";
    public static final String TAG_LESSON_category = "category";
    public static final String TAG_LESSON_bigimage = "big_image";




}