package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTiny {
    public String name;
    public List<String> following;

    public UserTiny(String name) {
        this.name = name;
        this.following = new ArrayList<>();
    }
    public UserTiny() {};
}
