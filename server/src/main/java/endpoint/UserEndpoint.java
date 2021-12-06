package endpoint;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.ShardedCounter;
import entities.UserTiny;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(name = "instaCrash", version = "v1",
        audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class UserEndpoint {
    /**
     * Get all Users
     * http://localhost:8080/_ah/api/instaCrash/v1/user
     *
     * @return All users
     */
    @ApiMethod(name = "getAllUsers", path = "user", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> getAllUsers() {
        Query q = new Query("User");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(200));

        return results;
    }

    /**
     * Register a user, getting it's ID from Access Token
     * http://localhost:8080/_ah/api/instaCrash/v1/user/
     *
     * @param user User id
     * @return Created User
     */
    @ApiMethod(name = "addUser", path = "user", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity addUser(User user, UserTiny userTiny) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // If already exists, fetch
        Entity e = null;
        try {
            e = getUser(user.getId());
        } catch (Exception err) {
            e = new Entity("User", user.getId());

            e.setProperty("email", user.getEmail());
            e.setProperty("name", userTiny.name);
            e.setProperty("listFollowing", new ArrayList<String>());
            e.setProperty("followings", 0);
        }

        Date now = new Date();
        e.setProperty("lastConnected", now);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();

        return e;
    }

    /**
     * Get a User from its ID
     * http://localhost:8080/_ah/api/instaCrash/v1/user/123
     *
     * @param userId id of the User
     * @return User
     */
    @ApiMethod(path = "user/{userId}")
    public Entity getUser(@Named("userId") String userId) throws EntityNotFoundException {
        Key userKey = KeyFactory.createKey("User", userId);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        return datastore.get(userKey);
    }

    /**
     * Get all the users with specified name
     * http://localhost:8080/_ah/api/instaCrash/v1/user/name/ArKeid0s
     *
     * @param name name of the User
     * @return User
     */
    @ApiMethod(path = "user/name/{name}")
    public List<Entity> getUsersByName(@Named("name") String name) {
        Query q = new Query("User").setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, name));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));

        return results;
    }


    /**
     * Get the number of follow
     * http://localhost:8080/_ah/api/instaCrash/v1/user/name/ArKeid0s
     *
     * @param userId id of the User we want information of
     * @return User
     */
    @ApiMethod(path = "user/followersCount")
    public Entity getFollowersCount(@Named("userId") String userId) throws EntityNotFoundException {
        Query q = new Query("User").setFilter(new Query.FilterPredicate("listFollowing", Query.FilterOperator.IN, userId));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity e = datastore.prepare(q).asSingleEntity();
        Long totalFollowers = (Long) e.getProperty("count");

        Entity user = datastore.get(KeyFactory.createKey("User", userId));
        user.setProperty("followersCount", totalFollowers);

        return user;
    }

    //region FOLLOWS

    /**
     * Check if a user follows userFollowing
     * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/follow?user=malo.grall@gmail.com
     *
     * @param user          id of the User
     * @param userFollowing email of the suspected followed User
     * @return
     */
    @ApiMethod(path = "user/{userFollowing}/follow", httpMethod = ApiMethod.HttpMethod.GET)
    public Entity getIsFollowing(@Named("userId") String user, @Named("userFollowing") String userFollowing) throws UnauthorizedException {

        Entity userChecked = null;

        // Not connected
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        try {
            userChecked = getUser(user);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        if (userChecked != null) {
            ArrayList<String> listFollowing = (ArrayList<String>) userChecked.getProperty("listFollowing");
            if ( listFollowing != null && listFollowing.contains(userFollowing)) {
                return userChecked;
            } else {
                return null;
            }
        }

        return null;

		/* //OLD VERSION
		// Check if exists
		Query q = new Query("Follow").setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user))
									 .setFilter(new Query.FilterPredicate("following", Query.FilterOperator.EQUAL, userFollowing));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(1));

		// Return null if doesn't follow
		return results.size() > 0 ? results.get(0) : null;
		 */
    }

    /**
     * Follow a user, giving its user id
     * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/follow/follow?user=malo.grall@gmail.com
     *
     * @param user         user id of the User
     * @param userToFollow userId of the User to follow
     * @return
     */
    @ApiMethod(path = "user/{userToFollow}/follow", httpMethod = ApiMethod.HttpMethod.POST)
    public Entity follow(User user, @Named("userToFollow") String userToFollow) throws EntityNotFoundException, UnauthorizedException {

        // Not connected
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Check if user is registered
        Entity userChecked = getUser(user.getId());
        Entity userToFollowEntity = getUser(userToFollow);

        if (userChecked != null && getIsFollowing(user.getId(), userToFollow) != null && userChecked != userToFollowEntity) {
            ArrayList<String> listFollowing = (ArrayList<String>) userChecked.getProperty("listFollowing");


            if (listFollowing == null || listFollowing.isEmpty()) {
                listFollowing = new ArrayList<>();

            }
            listFollowing.add(userToFollow);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Transaction txn = datastore.beginTransaction();

            userChecked.setProperty("listFollowing", listFollowing);
            userChecked.setProperty("followings", listFollowing.size());

            datastore.put(userChecked);
            datastore.put(userToFollowEntity);
            txn.commit();
        }

        return userChecked;

/*
		// Add the follow entity to datastore
		Entity e = new Entity("Follow");
		e.setProperty("user", user.getId());
		e.setProperty("following", userToFollow);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();

		return e;
*/

    }

    /**
     * Unfollow a user, giving its user id
     * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/unfollow/unfollow?user=malo.grall@gmail.com
     *
     * @param user           user id of the User
     * @param userToUnfollow userId of the User to unfollow
     * @return
     */
    @ApiMethod(path = "user/{userToUnfollow}/unfollow", httpMethod = ApiMethod.HttpMethod.DELETE)
    public Entity unfollow(User user, @Named("userToUnfollow") String userToUnfollow) throws EntityNotFoundException, UnauthorizedException {
        // Not connected
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Check if user is registered
        Entity userChecked = getUser(user.getId());
        Entity userToUnfollowEntity = getUser(userToUnfollow);

        if (userChecked != null && getIsFollowing(user.getId(), userToUnfollow) != null && userChecked != userToUnfollowEntity) {
            ArrayList<String> listFollowing = (ArrayList<String>) userChecked.getProperty("listFollowing");

            if (listFollowing == null || listFollowing.isEmpty()) {
                listFollowing = new ArrayList<>();

            }
            listFollowing.remove(userToUnfollow);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Transaction txn = datastore.beginTransaction();

            userChecked.setProperty("listFollowing", listFollowing);
            userChecked.setProperty("followings",  listFollowing.size());

            datastore.put(userChecked);
            datastore.put(userToUnfollowEntity);
            txn.commit();
        }

        return userChecked;
    }
    //endregion

    /**
     * Return user list of posts
     * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/posts
     *
     * @param userId
     * @return
     */
    @ApiMethod(path = "user/{userId}/posts")
    public List<Entity> getUserPosts(@Named("userId") String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Get all posts
        Query q = new Query("Post").setFilter(new Query.FilterPredicate("ownerId", Query.FilterOperator.EQUAL, userId));
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));

        for (Entity e : results) {
            ShardedCounter sc = new ShardedCounter(e.getKey().getName());
            e.setProperty("likes", sc.getCount());
        }

        return results;
    }

    @ApiMethod(path = "user/fromToken")
    public Entity getUserFromToken(User user) throws EntityNotFoundException, UnauthorizedException {
        if (user == null) throw new UnauthorizedException("Invalid credentials");

        return getUser(user.getId());
    }
}