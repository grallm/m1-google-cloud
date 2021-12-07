package endpoint;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.ShardedCounter;


@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))

public class LikeEndpoint {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


    /**
     * Like a Post
     * http://localhost:8080/_ah/api/instaCrash/v1/like
     *
     * @return Liked Post
     */
    @ApiMethod(name = "likePost", path = "like/{postId}", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity likePost(@Named("postId") String postId, User user) throws UnauthorizedException, EntityNotFoundException {

        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Check if user registered
 //       UserEndpoint userEndpoint = new UserEndpoint();
 //       userEndpoint.getUser(user.getId());
        // Add like to Datastore
        Entity e = new Entity("Like", postId + ":" + user.getId());
        e.setProperty("postId", postId);
        e.setUnindexedProperty("userEmail", user.getId());

        try {
            //Check if post is already like
            datastore.get(KeyFactory.createKey("Like", postId + ":" + user.getId()));

        } catch (EntityNotFoundException exception) {
//        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            ShardedCounter sc = new ShardedCounter(postId);
            TransactionOptions options = TransactionOptions.Builder.withXG(true);
            Transaction txn = datastore.beginTransaction(options);
            datastore.put(txn, e);
            sc.increment(txn, datastore);
        }


        return e;
    }

    @ApiMethod(path = "like/{postId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void unlikePost(@Named("postId") String postId, User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        Key likeKey = KeyFactory.createKey("Like", postId + ':' + user.getId());
 //       DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//		Transaction txn = datastore.beginTransaction();
        datastore.delete(likeKey);
//		txn.commit();

        ShardedCounter sc = new ShardedCounter(postId);
        sc.decrement();
    }


    /**
     * Get nbCount of likes
     * http://localhost:8080/_ah/api/instaCrash/v1/like
     *
     * @param id Id of the post
     * @return Liked Post
     */
    @ApiMethod(name = "getLikes", path = "like/getLikesCount/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity getLikesCount(@Named("id") String id) throws EntityNotFoundException {
        Key postKey = KeyFactory.createKey("Post", id);

 //       DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity post = datastore.get(postKey);

        ShardedCounter sc = new ShardedCounter(id);
        post.setProperty("NbLikes", sc.getCount());

        return post;
    }

    /**
     * Check if a user likes a post
     *
     * @param postId
     * @param user
     * @return
     * @throws EntityNotFoundException
     * @throws UnauthorizedException
     */
    @ApiMethod(path = "like/{postId}", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity doesLike(@Named("postId") String postId, User user) throws EntityNotFoundException, UnauthorizedException {

        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        //	Find corresponding Like
        Key likeKey = KeyFactory.createKey("Like", postId + ':' + user.getId());
    //    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        return datastore.get(likeKey);
    }
}