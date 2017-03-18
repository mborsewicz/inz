package pl.brsk.brsk.aplikacjakursyprojektinz;

/**
 * Created by brsk on 2016-12-10.
 */
public class Kurs {

    private String id;
    private String image;
    private String title;
    private String price;
    private String shortDescription;

    //Getters and Setters
    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortDescription(){return shortDescription;}

    public void setShortDescription(String shortDescription){ this.shortDescription = shortDescription;}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
