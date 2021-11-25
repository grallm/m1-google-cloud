package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.*;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.QueryResults;

import javax.inject.Named;
import java.util.ArrayList;
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

    @ApiMethod(
            name = "GQuery",
            path = "gquery",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Entity> gQuery(@Named("query") String query) {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        // [START newQuery]
        com.google.cloud.datastore.Query<?> glquery = com.google.cloud.datastore.Query.newGqlQueryBuilder(query).build();
        QueryResults<?> results = datastore.run(glquery);

        List<Entity> list = new ArrayList<>();
        Entity ent;
        while(results.hasNext()){
            ent = new Entity("result");
            ent.setProperty("result", results.next());
            list.add(ent);
        }

        // Use results
        // [END newQuery]

        return list;
    }
}
