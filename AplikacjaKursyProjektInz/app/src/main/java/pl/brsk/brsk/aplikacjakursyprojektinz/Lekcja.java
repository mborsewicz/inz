package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by brsk on 2017-01-27.
 */
public class Lekcja {

    private String id;
    private String description;
    private String title;
    private String video;
    private String free;
    private String is_enabled;
    private String bigImage;

    //Getters and Setters
    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() { return video; }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFree() { return free; }

    public void setFree(String free) {
        this.free = free;
    }

    public String getIs_enabled() { return is_enabled; }

    public void setIs_enabled(String is_enabled) {
        this.is_enabled = is_enabled;
    }

    public String getBigImage(){ return bigImage; }

    public void setBigImage(String bigImage) { this.bigImage = bigImage; }
}


//l.id, l.description AS 'lesson_description', l.title AS 'lesson_title', l.video, l.free, l.is_enabled