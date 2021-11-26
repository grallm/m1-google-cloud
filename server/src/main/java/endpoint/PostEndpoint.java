package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.datastore.*;
import entities.Post;

import java.util.Date;
import java.util.List;

@Api(name = "instaCrash", version = "v1",
		// audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		// clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class PostEndpoint
{
	/**
	 * Get all 20 last Posts
	 * http://localhost:8080/_ah/api/instaCrash/v1/post
	 *
	 * @return All Posts
	 */
	@ApiMethod(name = "getAllPosts", path = "post", httpMethod = ApiMethod.HttpMethod.GET)
	public List<Entity> getAllPosts()
	{
		Query q = new Query("Post").addSort("date", Query.SortDirection.DESCENDING);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
		
		return results;
	}
	
	/**
	 * Add a Post
	 * http://localhost:8080/_ah/api/instaCrash/v1/post
	 *
	 * @return Created Post
	 */
	@ApiMethod(name = "addPost", path = "post", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity addPost(Post post) throws BadRequestException
	{
		// Validate given post
		if (post.owner.trim().length() < 4)
		{
			throw new BadRequestException("Invalid Post");
		}
		
		// Add post to Datastore
		Date now = new Date();
		Entity e = new Entity("Post", post.owner + ":" + now.getTime());
		e.setProperty("owner", post.owner);
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
	public Entity getPost(@Named("id") String id) throws EntityNotFoundException
	{
		Key postKey = KeyFactory.createKey("Post", id);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity post = datastore.get(postKey);
		
		return post;
	}
}
