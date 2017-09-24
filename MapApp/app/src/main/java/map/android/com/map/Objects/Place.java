package map.android.com.map.Objects;

import java.io.Serializable;

/**
 * Created by suzan on 30/07/17.
 */

public class Place implements Serializable{

    private String id;
    private String name;
    private String desc;
    private String image;
    private Location location;
    private String rating;
    private String noOfRatings;
    private String longDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }


    public void setNoOfRatings(String noOfRatings) {
        this.noOfRatings = noOfRatings;
    }

    public String getNoOfRatings() {
        return noOfRatings;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }
}
