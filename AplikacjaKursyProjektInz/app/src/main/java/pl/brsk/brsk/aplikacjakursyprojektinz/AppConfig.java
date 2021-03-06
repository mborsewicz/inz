package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by brsk on 2016-11-11.
 */
public class AppConfig {

    public static String URL_LOGIN = "http://192.168.2.100/android_login_api/login.php";
    public static String URL_REGISTER = "http://192.168.2.100/android_login_api/register.php";
    public static  final  String DATA_UPLOAD_IMAGE = "http://192.168.2.100/android_login_api/upload_image.php";
    public static final String DATA_URL = "http://192.168.2.100/android_login_api/feed.php?page=";
    public static final String DATA_URL_TOP5 = "http://192.168.2.100/android_login_api/feed_top5.php";
    public static final String URL_IMAGE = "http://192.168.2.100/android_login_api/";
    public static final String DATA_LESSONS_URL = "http://192.168.2.100/android_login_api/getLessons.php?course_id=";
    public static final String DATA_INSERT_COURSE_MEMBER = "http://192.168.2.100/android_login_api/insertCourseMember.php?course_id=";
    public static final String DATA_GET_BUTTON_INFO = "http://192.168.2.100/android_login_api/getButtonInfo.php?course_id=";
    public static final String DATA_FILMY = "http://192.168.2.100/android_login_api/kursy/";
    public static final String DATA_INSERT_COURSE = "http://192.168.2.100/android_login_api/addCourse.php";
    public static final String DATA_INSERT_COMMENT = "http://192.168.2.100/android_login_api/addComment.php";
    public static final String DATA_LESSON = "http://192.168.2.100/android_login_api/getOneLesson.php?course_id=";
    public static final String DATA_STWORZONE = "http://192.168.2.100/android_login_api/stworzoneKursy.php?user_id=";
    public static final String DATA_KOMENTARZE = "http://192.168.2.100/android_login_api/getLessonComments.php?course_id=";
    public static final String DATA_IS_COURSE_OWNER = "http://192.168.2.100/android_login_api/isCoursesOwner.php?course_id=";
    public static final String DATA_UPLOAD_VIDEO = "http://192.168.2.100/android_login_api/upload.php";
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