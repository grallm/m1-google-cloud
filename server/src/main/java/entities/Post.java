package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Post {
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

    public Post(String ownerId, String owner, String image, String description) {
        this.ownerId = ownerId;
        this.owner = owner;
        this.image = image;
        this.description = description;
    }
}
