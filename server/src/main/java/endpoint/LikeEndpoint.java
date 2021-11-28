package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.*;
import entities.Like;
import entities.ShardedCounter;

@Api(name = "instaCrash", version = "v1",
		// audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		// clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
		namespace = @ApiNamespace(ownerDomain = "tinycrash.ew.r.appspot.com", ownerName = "tinycrash.ew.r.appspot.com", packagePath = ""))
public class LikeEndpoint
{
	/**
	 * Like a Post
	 * http://localhost:8080/_ah/api/instaCrash/v1/like
	 *
	 * @return Liked Post
	 */
	@ApiMethod(name = "likePost", path = "like", httpMethod = ApiMethod.HttpMethod.POST)
	public Entity likePost(Like like)
	{
		// Add like to Datastore
		Entity e = new Entity("Like");
		e.setProperty("postId", like.postId);
		e.setProperty("userEmail", like.userEmail);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();

		ShardedCounter sc = new ShardedCounter(like.postId);
		sc.increment();



		
		return e;
	}



	/**
	 * Get nbCount of likes
	 * http://localhost:8080/_ah/api/instaCrash/v1/like
	 * @param id Id of the post
	 * @return Liked Post
	 */
	@ApiMethod(name = "getLikes", path = "like/getLikesCount/{id}", httpMethod = ApiMethod.HttpMethod.GET)
	public Entity getLikesCount(@Named("id") String id) throws EntityNotFoundException {
		Key postKey = KeyFactory.createKey("Post", id);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity post = datastore.get(postKey);

		ShardedCounter sc = new ShardedCounter(id);
		post.setProperty("NbLikes", sc.getCount());

		return post;
	}
}
