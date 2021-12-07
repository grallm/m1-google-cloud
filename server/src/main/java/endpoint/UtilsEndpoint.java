package endpoint;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.Post;
import entities.Test;
import entities.UserTiny;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
                                // Adding minutes to time to have multiple dates
                                new Date().getTime() + (long) i * j * 60 * 1000, 0
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
                        userList.get(i)
                ));
            }
        }

        int a;
        List<Integer> intList;

        for (int i = 0; i < 100; i++) {

            // Add the follow entity to datastore
            intList = new ArrayList<>();
            for (int k = 1; k < r.nextInt(50) + 1; k++) {

                do {
                    a = r.nextInt(99) + 1;
                } while (a == i && intList.contains(a));
                intList.add(a);

                userEndpoint.follow(userList.get(i), Integer.toString(a));

            }
        }
        return list;
    }


    @ApiMethod(path = "utils/deleteallUser", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity delete() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


        Query mydeleteq = new Query("User");
        PreparedQuery pq = datastore.prepare(mydeleteq);
        for (
                Entity result : pq.asIterable()) {
            datastore.delete(result.getKey());
        }

        return new Entity("test");

    }

    @ApiMethod(path = "utils/deleteallPost", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity deletePost() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


        Query mydeleteq = new Query("Post");
        PreparedQuery pq = datastore.prepare(mydeleteq);
        for (
                Entity result : pq.asIterable()) {
            datastore.delete(result.getKey());
        }

        return new Entity("test");
    }

    @ApiMethod(path = "utils/deleteallLikes", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity deleteLikes() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


        Query mydeleteq = new Query("Like");
        PreparedQuery pq = datastore.prepare(mydeleteq);
        for (
                Entity result : pq.asIterable()) {
            datastore.delete(result.getKey());
        }

        return new Entity("test");

    }


    @ApiMethod(name = "timeLineTests", path = "utils/timeLineTests/{nbUsers}", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity timeLineTest10(@Named("nbUsers") int nbUsers) throws UnauthorizedException, EntityNotFoundException, BadRequestException {

//        Double testCreatePost1 = averageCreatePost(30, 10);

        Double testGetTimeLine1 = averageGetTimeLine(30, nbUsers);


        System.out.println("--Averge for 30 tests :");
//        System.out.println("---Creating a post with 10 followers : " + testCreatePost1 + " milliseconds");
        System.out.println("---Getting the timeLine with " + nbUsers +" follows : " + testGetTimeLine1 + " milliseconds");

        Entity ret = new Entity("Test");
        ret.setProperty("Time", (testGetTimeLine1));

        return ret;

    }


    @ApiMethod(name = "populateForLikes", path = "utils/populateForLikes/{start}/{nbLikes}", httpMethod = ApiMethod.HttpMethod.GET)
    public User populateForTimeTests(@Named("nbLikes") int nbLikes, @Named("start") int start) throws UnauthorizedException, EntityNotFoundException, BadRequestException {

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();
        LikeEndpoint likeEndpoint = new LikeEndpoint();

        //Generating tests accounts
        List<User> usersTest10 = new ArrayList<>();
        User user = new User(Integer.toString(0), "testingAccount" + 0 + "@mail.mail");
        UserTiny userTiny;
        for (int i = start; i < nbLikes; i++) {

            user = new User(Integer.toString(i), "testingAccount" + i + "@mail.mail");
            userTiny = new UserTiny("Test" + i);


            try {
                userEndpoint.addUser(
                        user,
                        userTiny
                );

                usersTest10.add(user);

            } catch (DatastoreTimeoutException exception) {

                System.out.println("Timeout error, skipping this one");

            }
        }
        return user;
    }


    @ApiMethod(name = "howManyLikes", path = "utils/likesInSeconds/{nbLikes}", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity likesPerSecond(@Named("nbLikes") int nbLikes) throws UnauthorizedException, EntityNotFoundException, BadRequestException {

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();
        LikeEndpoint likeEndpoint = new LikeEndpoint();

//        //Generating tests accounts
//        List<User> usersTest10 = new ArrayList<>();
        User user;
        UserTiny userTiny;
//        for (int i = 0; i < nbLikes; i++) {
//
//            user = new User(Integer.toString(i), "testingAccount" + i + "@mail.mail");
//            userTiny = new UserTiny("Test" + i);
//
//
//            try {
//                userEndpoint.addUser(
//                        user,
//                        userTiny
//                );
//
//                usersTest10.add(user);
//
//            } catch (DatastoreTimeoutException exception) {
//
//                System.out.println("Timeout error, skipping this one");
//
//            }
//        }

        //The user that will post
        user = new User("TestedUser", "TestShowAccount@mail.mail");
        userTiny = new UserTiny("TestShow");
        Entity testedUser = userEndpoint.addUser(
                user,
                userTiny
        );

        Post tmp;

        tmp = new Post(user.getId(), userTiny.name, "http://example.org", "short desc", new Date().getTime(), 0);
        String postId = (postEndpoint.addPost(user, tmp).getKey().getName());


        long startCount;
        long stopCount;
        User spam;


        startCount = System.currentTimeMillis();
        for (int i = 0; i < nbLikes; i++) {
            spam = new User(Integer.toString(i), "testingAccount" + i + "@mail.mail");
            likeEndpoint.likePost(postId, spam);
        }
        stopCount = System.currentTimeMillis();


        System.out.println("--- Like flood :" + (stopCount - startCount) + "milliseconds");
        System.out.println("--- Number of likes in one second : " + (( nbLikes * 1000) / (stopCount - startCount)));

        Entity ret = new Entity("Test");
        ret.setProperty("Time_Total", (stopCount - startCount));
        ret.setProperty("Likes_per_second", ((nbLikes * 1000) / (stopCount - startCount)));
        ret.setProperty("NumberOfLikes", nbLikes);


        return ret;

    }


    private Long generateTests(int numberOfFollowers) throws UnauthorizedException, EntityNotFoundException, BadRequestException {

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();


        /**
         * Tests time to post a message, with 10 followers
         */

        //Generating tests accounts
        List<User> usersTest10 = new ArrayList<>();
        User user;
        UserTiny userTiny;
        for (int i = 0; i < numberOfFollowers; i++) {

            user = new User(Integer.toString(i), "testingAccount" + i + "@mail.mail");
            userTiny = new UserTiny("Test" + i);

            usersTest10.add(user);

            userEndpoint.addUser(
                    user,
                    userTiny
            );
        }

        //The user that will post
        user = new User("TestedUser", "TestShowAccount@mail.mail");
        userTiny = new UserTiny("TestShow");
        Entity testedUser = userEndpoint.addUser(
                user,
                userTiny
        );


        //making the 10 User follow the user that will post
        for (User user1 : usersTest10) {
            userEndpoint.follow(user1, user.getId());
        }

        //creating the post
        Post post = new Post(user.getId(), userTiny.name, "http://example.org", "short desc", new Date().getTime(), 0);

        long timeRequestStart;
        long timeRequestFinish;
        //starting time measure
        timeRequestStart = System.currentTimeMillis();
        postEndpoint.addPost(user, post);
        timeRequestFinish = System.currentTimeMillis();

        long timeTest = timeRequestFinish - timeRequestStart;
        System.out.println("--- Time to create one post with " + usersTest10.size() + " followers : ");
        System.out.println("----- In seconds : " + TimeUnit.MILLISECONDS.toSeconds(timeTest));
        System.out.println("----- In miliseconds : " + timeTest);


        return timeTest;

    }

    private Double averageCreatePost(int nbTests, int nbUsers) throws EntityNotFoundException, BadRequestException, UnauthorizedException {


        List<Long> averageTestPost = new ArrayList<>();


        for (int i = 0; i < nbTests; i++) {

            averageTestPost.add(generateTests(nbUsers));
        }

        return averageTestPost.stream().mapToLong(val -> val).average().getAsDouble();


    }

    private Double averageGetTimeLine(int nbTests, int nbUsers) throws EntityNotFoundException, BadRequestException, UnauthorizedException {


        List<Long> averageTestPost = new ArrayList<>();


        for (int i = 0; i < nbTests; i++) {

            averageTestPost.add(testTimeLine(nbUsers));
        }

        return averageTestPost.stream().mapToLong(val -> val).average().getAsDouble();


    }

    private Long testTimeLine(int numberOfFollowers) throws UnauthorizedException, EntityNotFoundException, BadRequestException {

        UserEndpoint userEndpoint = new UserEndpoint();
        PostEndpoint postEndpoint = new PostEndpoint();


        /**
         * Tests time to post a message, with 10 followers
         */

        //Generating tests accounts
        List<User> usersTest10 = new ArrayList<>();
        List<UserTiny> usersTinyTest10 = new ArrayList<>();
        User user;
        UserTiny userTiny;

        Date date;

        for (int i = 0; i < numberOfFollowers; i++) {

            date = new Date();
            user = new User(Integer.toString(i) + date.getTime(), "testingAccountAgain" + i + "@mail.mail");
            userTiny = new UserTiny("Test" + i);

            usersTest10.add(user);
            usersTinyTest10.add(userTiny);

            userEndpoint.addUser(
                    user,
                    userTiny
            );
        }

        //The user that will post
        date = new Date();
        user = new User("OverTestedUser" + date.getTime(), "TestShowAccount@mail.mail");
        userTiny = new UserTiny("TestShow");
        Entity testedUser = userEndpoint.addUser(
                user,
                userTiny
        );

        //Making the users follow our TestUser, and posting one post
        for (User user1 : usersTest10) {
            userEndpoint.follow(user, user1.getId());
            //creating the posts
            Post post = new Post(user1.getId(), "Test" + usersTest10.indexOf(user1), "http://example.org", "short desc", new Date().getTime(), 0);

            //starting time measure
            postEndpoint.addPost(user1, post);
        }


        long timeRequestStart;
        long timeRequestFinish;
        timeRequestStart = System.currentTimeMillis();

        postEndpoint.getTimeLine(user);

        timeRequestFinish = System.currentTimeMillis();

        long timeTest = timeRequestFinish - timeRequestStart;
        System.out.println("--- Time to get TimeLine with " + usersTest10.size() + " follows : ");
        System.out.println("----- In seconds : " + TimeUnit.MILLISECONDS.toSeconds(timeTest));
        System.out.println("----- In miliseconds : " + timeTest);


        return timeTest;

    }

}
