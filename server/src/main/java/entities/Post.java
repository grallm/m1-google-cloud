package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Post {
    public String owner;

    /**
     * Image URL
     */
    public String image;

    public String description;
}
