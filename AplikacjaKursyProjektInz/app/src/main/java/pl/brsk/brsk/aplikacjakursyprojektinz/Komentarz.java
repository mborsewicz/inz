package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by borse on 25.03.2017.
 */

public class Komentarz {


    private String id;
    private String text;
    private String name;
    private String created;


    //Getters and Setters
    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() { return text; }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCreated() { return created; }

    public void setCreated(String created) {
        this.created = created;
    }

}
