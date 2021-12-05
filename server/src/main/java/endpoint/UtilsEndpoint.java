package endpoint;


import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.Like;
import entities.Post;
import entities.ShardedCounter;
import entities.UserTiny;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        Entity e;
        DatastoreService datastore;
        Transaction txn;

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();
        LikeEndpoint likeEndpoint = new LikeEndpoint();

        List<User> userList = new ArrayList<>();
        List<UserTiny> tinyUserList = new ArrayList<>();
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
                Entity createdPost = postEndpoint.addPost(new Post(
                        Integer.toString(i),
                        "Bob" + i,
                        "https://img.20mn.fr/sIChN5W-TCG0VWSpGYJYLw/768x492_tous-trolls.jpg",
                        "Dans mon post numéro " + j + " je vais vous présenter ce super accident n=" + i + " sur fond de couché de soleil"
                ));
                list.add(createdPost);

                //un user like ses propres posts mais ducoup ça aide pas sur les shards
                list.add(likeEndpoint.likePost(new Like(
                        createdPost.getKey().getName(),
                        Integer.toString(i)
                )));

            }


        }

        Random r = new Random();
        int a;
        int b;

        for (int i = 1; i < 100; i++) {

            // Add the follow entity to datastore


            do {
                a = r.nextInt(100);
            } while (a == i);
            list.add(
                    userEndpoint.follow(Integer.toString(i), Integer.toString(a)));


            do {
                b = r.nextInt(100);
            } while (b == i || b == a);

            list.add(userEndpoint.follow(Integer.toString(i), Integer.toString(b)));
        }


        return list;
    }

    /**
     * @return List of Entity Follow linking every auto-generated User with 2 others
     */
    private List<Entity> generateFriends() throws UnauthorizedException, EntityNotFoundException {
        List<Entity> list = new ArrayList<>();
        Random r = new Random();
        int a;
        int b;

        UserEndpoint userEndpoint = new UserEndpoint();


        for (int i = 0; i < 100; i++) {

            // Add the follow entity to datastore


            do {
                a = r.nextInt(100);
            } while (a == i);
            list.add(userEndpoint.follow(KeyFactory.createKey(Integer.toString(i), "autoGen" + i + "@mail.mail").toString(), "autoGen" + a + "@mail.mail"));


            do {
                b = r.nextInt(100);
            } while (b == i || b == a);

            list.add(userEndpoint.follow("autoGen" + i + "@mail.mail", "autoGen" + b + "@mail.mail"));
        }
        return list;
    }

}
