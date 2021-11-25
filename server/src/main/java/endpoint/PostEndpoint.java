package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.datastore.*;
import entities.Post;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Api(
        name = "instaCrash",
        version = "v1",
        // audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        // clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace =
        @ApiNamespace(
                ownerDomain = "helloworld.example.com",
                ownerName = "helloworld.example.com",
                packagePath = ""
        )
)
public class PostEndpoint {
    /**
     * Get all Posts
     * @return All Posts
     */
    @ApiMethod(
            name = "getAllPosts",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Entity> getAllPosts() {
        Query q = new Query("Score").addSort("score", Query.SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
        return result;
    }

    /**
     * Add a Post
     * @return Created Post
     */
    @ApiMethod(
            name = "addPost",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Entity addPost(Post post) throws BadRequestException {
        // Validate given post
        // if (post.owner.trim().length() < 4) {
        //     throw new BadRequestException("Invalid Post");
        // }

        // Add post to Datastore
        Entity e = new Entity("Post");
        e.setProperty("owner", post.owner);
        e.setProperty("url", post.image);
        e.setProperty("body", post.description);
        e.setProperty("date", new Date());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();

        return e;
    }
}
