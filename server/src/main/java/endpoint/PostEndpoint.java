package endpoint;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.datastore.*;
import entities.Post;

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
        List<Entity> results;

        // Gets all posts if no user
        if (user == null) {
            Query q = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery pq = datastore.prepare(q);

            results = pq.asList(FetchOptions.Builder.withLimit(20));

            /*
             * FOR TESTING number of likes
             */
            /*
            for (Entity e : results) {
                ShardedCounter sc = new ShardedCounter(e.getKey().getName());

                e.setProperty("likes", sc.getCount());

            }
            */
        } else {
            results = getTimeLine(user.getId());
        }

        return results;
    }

    /**
     * Add a Post
     * http://localhost:8080/_ah/api/instaCrash/v1/post
     *
     * @return Created Post
     */
    @ApiMethod(name = "addPost", path = "post", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity addPost(Post post) throws BadRequestException {
        // Validate given post
        if (post.owner.trim().length() < 4) {
            throw new BadRequestException("Invalid Post");
        }

        // Add post to Datastore
        Date now = new Date();
        Entity e = new Entity("Post", post.ownerId + ":" + now.getTime());
        e.setProperty("owner", post.owner);
        e.setProperty("ownerId", post.ownerId);
        e.setProperty("url", post.image);
        e.setProperty("body", post.description);
        e.setProperty("date", now);

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
     * @param userId ID of the user
     * @return Timeline posts
     */
    @ApiMethod(path = "post/timeLine/{userId}")
    public List<Entity> getTimeLine(@Named("userId") String userId) throws EntityNotFoundException {

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
    }
}
/**
 * Architecture idea:
 * Add list of follow in user
 * Foreach followers get last 10 posts and add them to temp list and reorder by date attribute
 * Display temp list as timeline
 */
