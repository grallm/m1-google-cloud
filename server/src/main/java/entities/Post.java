package entities;


import java.util.Date;

public class Post implements Comparable<Post> {
    public String ownerId;

    /**
     * Owner name
     */
    public String owner;

    /**
     * Image URL
     */
    public String image;

    public String description;
    public Date date;

    public Post(String ownerId, String owner, String image, String description, Date date) {
        this.ownerId = ownerId;
        this.owner = owner;
        this.image = image;
        this.description = description;
        this.date = date;
    }

    /**
     * Allow comparison with other posts by date
     * @param p
     * @return
     */
    @Override
    public int compareTo(Post p) {
        if (date == null || p.date == null) {
            return 0;
        }
        return date.compareTo(p.date);
    }
}
