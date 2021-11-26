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
                .addSort("lastConnected", Query.SortDirection.DESCENDING);

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

    /**
     * Get a User from its email
     * @param email email of the User
     * @return User
     */
    @ApiMethod(path = "user/{email}")
    public Entity getUser(@Named("email") String email) throws EntityNotFoundException {
        Key postKey = KeyFactory.createKey("User", email);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity user = datastore.get(postKey);

        return user;
    }

    /**
     * Check if a user follows another
     * @param user email of the User
     * @param follow email of the User which first one may follow
     * @return
     */
    @ApiMethod(path = "user/{follow}/follow")
    public Entity getFollows(@Named("user") String user, @Named("follow") String follow)
            throws UnauthorizedException
    {
        // Not connected
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Check if exists
        Query q = new Query("Follow")
                .setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user))
                .setFilter(new Query.FilterPredicate("follow", Query.FilterOperator.EQUAL, follow));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(1));

        // Return null if doesn't follow
        return results.size() > 0
                ? results.get(0)
                : null;
    }

    /**
     * Follow a user, giving its email
     * Unfollow if follow already
     * @param user email of the User
     * @param follow email of the User to follow
     * @return
     */
    @ApiMethod(
            path = "user/{follow}/follow",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public Entity follow(@Named("user") String user, @Named("follow") String follow)
            throws EntityNotFoundException, UnauthorizedException
    {
        // Not connected
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Add post to Datastore
        Entity e = new Entity("Follow");
        e.setProperty("user", user);
        e.setProperty("follow", follow);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();

        return e;
    }
}