package endpoint;


import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.Post;
import entities.Test;
import entities.UserTiny;

import java.util.*;

@Api(
        name = "instaCrash",
        version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace =
        @ApiNamespace(
                ownerDomain = "tinycrash.ew.r.appspot.com",
                ownerName = "tinycrash.ew.r.appspot.com",
                packagePath = ""
        )
)
public class UtilsEndpoint {
    @ApiMethod(path = "utils/test", httpMethod = ApiMethod.HttpMethod.GET)
    public Test testing() {
        return new Test("test");
    }

    /**
     * Populate the database with fake account that have posted 3 times
     * http://localhost:8080/_ah/api/instaCrash/v1/utils/populate
     *
     * @return List of Entity created
     */
    @ApiMethod(name = "populate", path = "utils/populate", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> populate() throws EntityNotFoundException, UnauthorizedException, BadRequestException {

        List<Entity> list = generateUserAndPosts(3);
        return list;

    }

    /**
     * @param nbPostPerUser number of post that will be generated per user
     * @return list of User and their nbPostPerUser Posts
     */
    private List<Entity> generateUserAndPosts(int nbPostPerUser) throws UnauthorizedException, BadRequestException, EntityNotFoundException {

        List<Entity> list = new ArrayList<>();

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();
        LikeEndpoint likeEndpoint = new LikeEndpoint();

        Random r = new Random();

        List<User> userList = new ArrayList<>();
        List<Entity> postList = new ArrayList<>();
        User user;
        UserTiny userTiny;

        for (int i = 1; i < 101; i++) {
            // Add User to Datastore

            user = new User(Integer.toString(i), "autoGen" + i + "@mail.mail");
            userTiny = new UserTiny("Bob" + i);

            userList.add(user);

            list.add(userEndpoint.addUser(
                    user,
                    userTiny
            ));

            for (int j = 0; j < nbPostPerUser; j++) {
                // Add post to Datastore

                Entity createdPost = postEndpoint.addPost(
                        user,
                        new Post(
                            Integer.toString(i),
                            userTiny.name,
                            "https://img.20mn.fr/sIChN5W-TCG0VWSpGYJYLw/768x492_tous-trolls.jpg",
                            "Dans mon post numéro " + j + " je vais vous présenter ce super accident n=" + i + " sur fond de couché de soleil",
                            new Date().getTime(), 0
                        )
                );


             /*   Entity createdPost = postEndpoint.addPost(new Post(
                        Integer.toString(i),
                        "Bob" + i,
                        "https://img.20mn.fr/sIChN5W-TCG0VWSpGYJYLw/768x492_tous-trolls.jpg",
                        "Dans mon post numéro " + j + " je vais vous présenter ce super accident n=" + i + " sur fond de couché de soleil"
                ));*/
                postList.add(createdPost);
                list.add(createdPost);
            }
        }


        ArrayList<Integer> randomLikes;

        //Random likes :
        for (int i = 0; i < 100; i++) {
            randomLikes = new ArrayList<>();
            for (int j = 1; j < postList.size() - 1; j++) {
                randomLikes.add(j);
            }
            Collections.shuffle(randomLikes);

            for (int k = 1; k < r.nextInt(postList.size() - 1); k++) {

                list.add(likeEndpoint.likePost(
                        postList.get(randomLikes.get(k)).getKey().getName(),
                        new User(Integer.toString(k), "autoGen" + i + "@mail.mail")
                ));
            }
        }

        int a;
        List<Integer> intList;

        for (int i = 0; i < 100; i++) {

            // Add the follow entity to datastore
            intList = new ArrayList<>();
            for (int k = 0; k < r.nextInt(50); k++) {

                do {
                    a = r.nextInt(100);
                } while (a == i && !intList.contains(a));
                intList.add(a);

                userEndpoint.follow(userList.get(i), Integer.toString(a));

            }
        }
        return list;
    }


}
