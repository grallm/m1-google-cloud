package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.*;

import java.util.List;

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
}
