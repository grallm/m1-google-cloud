package endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.api.datastore.*;
import entities.UserTiny;

import java.util.Date;
import java.util.List;
@Api(
        name = "instaCrash",
        version = "v1",
        // audiences = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        // clientIds = "616939906371-cnpc0ocrc71ae3hbann3glgiktfgregk.apps.googleusercontent.com",
        namespace =
        @ApiNamespace(
                ownerDomain = "tinycrash.ew.r.appspot.com",
                ownerName = "tinycrash.ew.r.appspot.com",
                packagePath = ""
        )
)
public class UserEndpoint {
    /**
     * Get all Users
     * @return All Posts
     */
    @ApiMethod(
            name = "getAllUsers",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Entity> getAllUsers() {
        Query q = new Query("User")
                .addSort("date", Query.SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));

        return results;
    }

    /**
     * Add a user
     * @return Created User
     */
    @ApiMethod(
            name = "addUser",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Entity addUser(UserTiny userTiny) throws BadRequestException {
        // Validate given post
        if (userTiny.email.trim().length() < 4) {
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
}
