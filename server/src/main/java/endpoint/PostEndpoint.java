package endpoint;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.datastore.*;
import entities.Post;
import entities.ShardedCounter;

import java.util.*;

@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class PostEndpoint {
    /**
     * Get all 20 last Posts
     * If access_token given gets user's timeline
     *
     * @param user If access_token given
     * @return All Posts
     */
    @ApiMethod(name = "getAllPosts", path = "post", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> getAllPosts(User user) throws EntityNotFoundException {
        List<Entity> results = new ArrayList<>();

        // Gets all posts if no user
        if (user == null) {
            Query q = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery pq = datastore.prepare(q);

            results = pq.asList(FetchOptions.Builder.withLimit(2000));

            /*
             * FOR TESTING number of likes
             */

            for (Entity e : results) {

                ShardedCounter sc = new ShardedCounter(e.getKey().getName());

                e.setProperty("likes", sc.getCount());

            }

        }

        /*else {
            results = getTimeLine(user);
        }*/

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
     * Add a Post
     * http://localhost:8080/_ah/api/instaCrash/v1/post
     *
     * @return Created Post
     */
    @ApiMethod(name = "addPost", path = "post", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity addPost(User user, Post post) throws BadRequestException {

        // Add post to Datastore
        Entity e = new Entity("Post", user.getId() + ":" + post.date);
        e.setProperty("ownerId", user.getId());
        e.setProperty("owner", post.owner);
        e.setProperty("url", post.image);
        e.setProperty("body", post.description);
        e.setProperty("date", post.date);
        e.setProperty("likes", 0);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();

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
    public ArrayList<Post> getTimeLine(User user) throws EntityNotFoundException {

        Query qFollowings = new Query("User").setFilter(new Query.FilterPredicate("listFollowing", Query.FilterOperator.EQUAL, user.getId()));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(qFollowings);
        List<String> listFollowing = new ArrayList<>();

        //Add following accounts ids to a list
        pq.asIterator().forEachRemaining(e -> {
            listFollowing.add(e.getKey().getName());
        });

        //If no followings return null
        if (listFollowing.isEmpty()) {
            //debug
            System.out.println("Empty followers");
            return null;
        }

        ArrayList<Post> posts = new ArrayList<>();
        for (String i : listFollowing) {
            System.out.println("Check Following post : " + i);

            for (Entity e : getUserPosts(i)) {
                System.out.println("--post found");
                posts.add(new Post(
                        (String) e.getProperty("ownerId"),
                        (String) e.getProperty("owner"),
                        (String) e.getProperty("url"),
                        (String) e.getProperty("body"),
                        (long) e.getProperty("date"),
                        (long) e.getProperty("likes")
                ));
            }
        }

        Collections.sort(posts, Comparator.comparingLong(Post::getDate)); // Ascending
        Collections.reverse(posts); // Descending

        return posts;


        /* //OLD VERSION WORKS ! But scaling is questionable
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        //get all followers of user
        Query qFollowers = new Query("Follow").setFilter(new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL, userId));

        PreparedQuery pq = datastore.prepare(qFollowers);
        List<Object> followList = new ArrayList<>();

        pq.asIterator().forEachRemaining(entity ->
        {
            followList.add(entity.getProperty("user"));
            System.out.println("Value :" + entity.getProperty("user"));
        });

        // If follows nobody, return null
        if (followList.isEmpty()) {
            System.out.println("Empty list");
            return null;
        }

        Query qFollowerPosts = new Query("Post").setFilter(new Query.FilterPredicate("owner", Query.FilterOperator.IN, followList));

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
