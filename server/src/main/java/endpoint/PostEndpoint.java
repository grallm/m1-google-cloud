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
import entities.ShardedCounter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class PostEndpoint {
    /**
     * Get all 20 last Posts
     *
     * @return All Posts
     */
    @ApiMethod(name = "getAllPosts", path = "post", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> getAllPosts() throws EntityNotFoundException {
        List<Entity> results = new ArrayList<>();

        // Gets all posts
        Query q = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        results = pq.asList(FetchOptions.Builder.withLimit(2000));

        /*
         * Number of likes
         */
        for (Entity e : results) {

            ShardedCounter sc = new ShardedCounter(e.getKey().getName());

            e.setProperty("likes", sc.getCount());

        }

        return results;
    }

    /**
     * Return the 5 last post of a user
     *
     * @param userId
     * @return
     * @throws EntityNotFoundException
     */
    @ApiMethod(path = "post/getUserPosts/{userId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> getUserPosts(@Named("userId") String userId) throws EntityNotFoundException {

        List<Entity> results;

        Query q = new Query("Post")
                .setFilter(new Query.FilterPredicate("ownerId", Query.FilterOperator.EQUAL, userId))
                .addSort("date", Query.SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        results = pq.asList(FetchOptions.Builder.withLimit(5));

        results.iterator().forEachRemaining(entity -> {
            ShardedCounter sc = new ShardedCounter(entity.getKey().getName());
            entity.setProperty("likes", sc.getCount());
        });

        return results;

    }

    /**
     * Add a Post. Image being Base64 file
     * http://localhost:8080/_ah/api/instaCrash/v1/post
     *
     * @return Created Post
     */
    @ApiMethod(name = "addPost", path = "post", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity addPostFile (User user, Post post) throws BadRequestException, UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        UploadEndpoint uep = new UploadEndpoint();
        // Change image string from File to URL
        post.image = uep.uploadFile(post.image, post.ownerId + ":" + post.date);

        return addPost(user, post);
    }

    /**
     * Add a post
     * Methoding taking image as an URL
     * @param user
     * @param post
     * @return
     * @throws BadRequestException
     */
    public Entity addPost(User user, Post post) throws BadRequestException {
        ShardedCounter sc = new ShardedCounter("Post:" + user.getId());


        // Add post to Datastore
        Entity e = new Entity("Post", user.getId() + ":" + (sc.getCount() + 1));
        e.setProperty("ownerId", user.getId());
        e.setProperty("owner", post.owner);
        e.setProperty("image", post.image);
        e.setProperty("body", post.description);
        e.setProperty("date", post.date);
        e.setProperty("likes", 0);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();

        sc.increment();


        return e;
    }

    /**
     * Get a Post from its ID
     * http://localhost:8080/_ah/api/instaCrash/v1/post/{ID}
     *
     * @param id ID of the post
     * @return Post
     */
    @ApiMethod(path = "post/{id}")
    public Entity getPost(@Named("id") String id) throws EntityNotFoundException {
        Key postKey = KeyFactory.createKey("Post", id);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity post = datastore.get(postKey);

        ShardedCounter sc = new ShardedCounter(post.getKey().getName());
        post.setProperty("likes", sc.getCount());

        return post;
    }

    /**
     * Get all the users that likes the post
     * http://localhost:8080/_ah/api/instaCrash/v1/post/{id}/likes
     *
     * @param id The Post_ID
     * @return
     */
    @ApiMethod(path = "post/{id}/likes")
    public List<Entity> getListUserLike(@Named("id") String id) {
        Query q = new Query("Like").setFilter(new Query.FilterPredicate("postId", Query.FilterOperator.EQUAL, id));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
        List<Entity> users = new ArrayList<>();

        for (Entity likes : results) {
            Query q2 = new Query("User").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, likes.getProperty("userEmail")));
            PreparedQuery pq2 = datastore.prepare(q2);

            users.add(pq2.asList(FetchOptions.Builder.withLimit(1)).get(0));
        }

        return users;
    }


    /**
     * Get the timeline of a user
     *
     * @param user Connected user
     * @return Timeline posts
     */
    @ApiMethod(path = "post/timeLine")
    public List<Entity> getTimeLine(User user) throws EntityNotFoundException {

//        System.out.println("--- " + KeyFactory.createKey("User", user.getId().toString()).getName());
//        System.out.println("--- " + user.getId());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity userEntity = datastore.get(KeyFactory.createKey("User", user.getId()));
        List<String> listFollowing = (List<String>) userEntity.getProperty("listFollowing");

        //If no followings return null
        if (listFollowing == null || listFollowing.isEmpty()) {
            //debug
            System.out.println("Empty followers");
            return new ArrayList<>();
        }

        Key key;
        ShardedCounter sc;
        Entity e = null;
        List<Entity> result = new ArrayList<>();

        Date date;
        Instant now = Instant.now();
        // ajoute le dernier post de chaque personne follow à la liste
        boolean flag;
        for (String needYourPosts : listFollowing) {
            flag = true;
            sc = new ShardedCounter("Post:" + needYourPosts);
            long i = sc.getCount();

            while (flag && i > 0) {

                key = KeyFactory.createKey("Post", needYourPosts + ":" + i);

                i--;

                try {

                    e = datastore.get(key);
                    date =  new Date((long) e.getProperty("date"));

                    if (date.toInstant().isAfter(now.minus(1, ChronoUnit.DAYS))) {

                        result.add(e);

                    } else {
                        flag = false;
                    }
                } catch (EntityNotFoundException exception) {
                    //TODO : Handle this
                    System.out.println("---- Not found : " + key.getName());
                    flag = false;
                }
            }

        }


        //This cost a LOT, need to improve
        result.sort(Comparator.comparing(entity -> (new Date((long)entity.getProperty("date")))));

        List<Entity> toReturn = new ArrayList<>();
        if (result.size() > 20)
            toReturn = result.subList(0, 19);
        else if (result.size() > 0)
            toReturn = result.subList(0, result.size() - 1);
        else System.out.println("------ AUCUNE TL");

        System.out.println(result.size());

        return toReturn;

        //Post Key("Post", userId + i);
        // i = postNumber
        //each time a post is added : Add with a number
/*
        Query qPosts = new Query("Post")
                .setFilter(new Query.FilterPredicate("ownerId", Query.FilterOperator.IN, listFollowing))
                .addSort("date", Query.SortDirection.DESCENDING);
        PreparedQuery pq = datastore.prepare(qPosts);

        return pq.asList(FetchOptions.Builder.withLimit(20));


 */

//
//        for (String i : listFollowing) {
//            //   System.out.println("Check Following post : " + i);
//
//            for (Entity e : getUserPosts(i)) {
////                System.out.println("--post found");
//                posts.add(new Post(
//                        (String) e.getProperty("ownerId"),
//                        (String) e.getProperty("owner"),
//                        (String) e.getProperty("image"),
//                        (String) e.getProperty("body"),
//                        (long) e.getProperty("date"),
//                        (long) e.getProperty("likes")
//                ));
//            }
//        }
//
//        posts.sort(Comparator.comparingLong(Post::getDate)); // Ascending
//        Collections.reverse(posts); // Descending
//
//        return posts;

/*
         //OLD VERSION WORKS ! But scaling is questionable
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        //get all followers of user
        Query qFollowers = new Query("Follow").setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getId()));

        PreparedQuery pq = datastore.prepare(qFollowers);
        List<Object> followList = new ArrayList<>();

        pq.asIterator().forEachRemaining(entity ->
        {
            followList.add(entity.getProperty("following"));
           // System.out.println("Value :" + entity.getProperty("following"));
        });

        // If follows nobody, return null
        if (followList.isEmpty()) {
            System.out.println("Empty list");
            return null;
        }

        Query qFollowerPosts = new Query("Post").setFilter(new Query.FilterPredicate("ownerId", Query.FilterOperator.IN, followList));

        pq = datastore.prepare(qFollowerPosts);

        return pq.asList(FetchOptions.Builder.withLimit(20));
*/

    }

}

/**
 * Architecture idea:
 * Add list of follow in user
 * Foreach followers get last 10 posts and add them to temp list and reorder by date attribute
 * Display temp list as timeline
 */
