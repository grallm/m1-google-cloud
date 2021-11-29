package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import entities.UserTiny;

import java.util.Date;
import java.util.List;

@Api(name = "instaCrash", version = "v1",
		audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class UserEndpoint
{
	/**
	 * Get all Users
	 * http://localhost:8080/_ah/api/instaCrash/v1/user
	 *
	 * @return All Posts
	 */
	@ApiMethod(name = "getAllUsers", path = "user", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> getAllUsers()
	{
		Query q = new Query("User");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
		
		return results;
	}
	
	/**
	 * Add a user
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/
	 *
	 * @return Created User
	 */
	@ApiMethod(name = "addUser", path = "user", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity addUser(UserTiny userTiny) throws BadRequestException
	{
		// Validate given post
		if (userTiny.email.trim().length() < 4)
		{
			throw new BadRequestException("Invalid User");
		}
		
		// Add post to Datastore
		Date now = new Date();
		Entity e = new Entity("User", userTiny.email);
		e.setProperty("email", userTiny.email);
		e.setProperty("name", userTiny.name);
		e.setProperty("lastConnected", now);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		
		return e;
	}
	
	/**
	 * Get a User from its email
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com
	 *
	 * @param email email of the User
	 * @return User
	 */
	@ApiMethod(path = "user/{email}")
	public Entity getUserByEmail(@Named("email") String email) throws EntityNotFoundException
	{
		Key postKey = KeyFactory.createKey("User", email);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity user = datastore.get(postKey);
		
		return user;
	}
	
	/**
	 * Get all the users with specified name
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/name/ArKeid0s
	 *
	 * @param name name of the User
	 * @return User
	 */
	@ApiMethod(path = "user/name/{name}")
	public List<Entity> getUsersByName(@Named("name") String name) throws EntityNotFoundException
	{
		Query q = new Query("User").setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
		
		return results;
	}
	
	/**
	 * Check if a user follows userFollowing
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/follow?user=malo.grall@gmail.com
	 *
	 * @param user          email of the User
	 * @param userFollowing email of the suspected followed User
	 * @return
	 */
	@ApiMethod(path = "user/{userFollowing}/follow", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity getIsFollowing(@Named("user") String user, @Named("userFollowing") String userFollowing) throws UnauthorizedException
	{
		// Not connected
		if (user == null)
		{
			throw new UnauthorizedException("Invalid credentials");
		}
		
		// Check if exists
		Query q = new Query("Follow").setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user))
									 .setFilter(new Query.FilterPredicate("following", Query.FilterOperator.EQUAL, userFollowing));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(1));
		
		// Return null if doesn't follow
		return results.size() > 0 ? results.get(0) : null;
	}
	
	/**
	 * Follow a user, giving its email
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/follow/follow?user=malo.grall@gmail.com
	 *
	 * @param user         email of the User
	 * @param userToFollow email of the User to follow
	 * @return
	 */
	@ApiMethod(path = "user/{userToFollow}/follow", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity follow(@Named("user") String user, @Named("userToFollow") String userToFollow) throws EntityNotFoundException, UnauthorizedException
	{
		// Not connected
		if (user == null)
		{
			throw new UnauthorizedException("Invalid credentials");
		}
		
		// Add the follow entity to datastore
		Entity e = new Entity("Follow");
		e.setProperty("user", user);
		e.setProperty("following", userToFollow);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		
		return e;
	}
	
	/**
	 * Unfollow a user, giving its email
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/unfollow/unfollow?user=malo.grall@gmail.com
	 *
	 * @param user           email of the User
	 * @param userToUnfollow email of the User to unfollow
	 * @return
	 */
	@ApiMethod(path = "user/{userToUnfollow}/unfollow", httpMethod = ApiMethod.HttpMethod.DELETE)
	public Entity unfollow(@Named("user") String user, @Named("userToUnfollow") String userToUnfollow) throws EntityNotFoundException, UnauthorizedException
	{
		Entity followingEntity = getIsFollowing(user, userToUnfollow);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.delete(txn, followingEntity.getKey());
		txn.commit();
		
		return followingEntity;
	}
	
	/**
	 * Return user list of posts
	 * http://localhost:8080/_ah/api/instaCrash/v1/user/matproz.gaming@gmail.com/posts
	 *
	 * @param email
	 * @return
	 */
	@ApiMethod(path = "user/{email}/posts")
	public List<Entity> getUserPosts(@Named("email") String email) throws EntityNotFoundException
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		// Get all posts
		Query q = new Query("Post").setFilter(new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL, email));
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
		
		return results;
	}
	
	
}