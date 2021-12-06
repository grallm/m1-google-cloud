package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTiny {
    public String name;
    public List<String> listFollowings;
    public int followings;
    public int followers;

    public UserTiny(String name, int followings, int followers) {
        this.name = name;
        this.listFollowings = new ArrayList<>();
        this.followings = followings;
        this.followers = followers;
    }

    public UserTiny(String name) {
        this.name = name;
        this.listFollowings = new ArrayList<>();
        this.followings = 0;
        this.followers = 0;
    }

    public UserTiny() {};
}
