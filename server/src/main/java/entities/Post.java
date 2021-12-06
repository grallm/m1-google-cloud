package entities;


import java.util.Date;

public class Post implements Comparable<Post> {
    public String ownerId;

    /**
     * Owner name
     */
    public String owner;

    /**
     * Image URL or Base64
     */
    public String image;

    public String description;
    public long date;

    public long likes;

    public Post(String ownerId, String owner, String image, String description, long date, long likes) {
        this.ownerId = ownerId;
        this.owner = owner;
        this.image = image;
        this.description = description;
        this.date = date;
        this.likes = likes;
    }

    public long getDate(){
        return date;
    }

    @Override
    public int compareTo(Post o) {
        return 0;
    }
}
