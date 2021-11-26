package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Transaction;
import entities.Like;

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
		
		return e;
	}
}
