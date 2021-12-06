package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTiny {
    public String name;
    public List<String> listFollowings;
    public int followings;

    public UserTiny(String name, int followings) {
        this.name = name;
        this.listFollowings = new ArrayList<>();
        this.followings = followings;
    }
    public UserTiny() {};
}
