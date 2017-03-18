package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by borse on 12.03.2017.
 */

public class Category {

    private String id;
    private String title;

    public Category(){}

    public Category(String id, String title){
        this.id = id;
        this.title = title;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }
}
