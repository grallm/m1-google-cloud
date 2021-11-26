package endpoint;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
public class UtilsEndpoint {


    /**
     * Populate the database with fake account that have posted 3 times
     * http://localhost:8080/_ah/api/instaCrash/v1/utils/populate
     *
     * @return List of Entity created
     */
    @ApiMethod(name = "populate", path="utils/populate", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Entity> populate() throws EntityNotFoundException, UnauthorizedException {

        List<Entity> list = generateUserAndPosts(3);
        list.addAll(generateFriends());
        return list;

    }

    /**
     * @param nbPostPerUser number of post that will be generated per user
     * @return list of User and their nbPostPerUser Posts
     */
    private List<Entity> generateUserAndPosts(int nbPostPerUser) {

        List<Entity> list = new ArrayList<>();
        Date now;
        Entity e;
        DatastoreService datastore;
        Transaction txn;
        for (int i = 0; i < 100; i++) {
            // Add post to Datastore
            now = new Date();
            e = new Entity("User", "autoGen" + i + "@mail.mail");
            e.setProperty("email", "autoGen" + i + "@mail.mail");
            e.setProperty("name", "Bob" + i);
            e.setProperty("lastConnected", now);

            datastore = DatastoreServiceFactory.getDatastoreService();
            txn = datastore.beginTransaction();
            datastore.put(e);
            txn.commit();

            list.add(e);

            for (int j = 0; j < nbPostPerUser; j++) {
                // Add post to Datastore
                now = new Date();
                e = new Entity("Post", "autoGen" + i + "@mail.mail" + ":" + now.getTime());
                e.setProperty("owner", "autoGen" + i + "@mail.mail");
                e.setProperty("url", "https://Nicecrash" + i + "/" + j);
                e.setProperty("body", "Dans mon post numéro " + j + " je vais vous présenter ce super accident n=" + i + " sur fond de couché de soleil");
                e.setProperty("date", now);

                datastore = DatastoreServiceFactory.getDatastoreService();
                txn = datastore.beginTransaction();
                datastore.put(e);
                txn.commit();

                list.add(e);
            }
        }


        return list;
    }

    /**
     *
     * @return List of Entity Follow linking every auto-generated User with 2 others
     */
    private List<Entity> generateFriends() {
        List<Entity> list = new ArrayList<>();
        Date now;
        Entity e;
        DatastoreService datastore;
        Transaction txn;
        Random r = new Random();
        int a;
        int b;
        for (int i = 0; i < 100; i++) {

            // Add the follow entity to datastore
            e = new Entity("Follow");
            e.setProperty("user", "autoGen" + i + "@mail.mail");
            do {
                a = r.nextInt(100);
            } while (a == i);
            e.setProperty("following", "autoGen" + a + "@mail.mail");

            datastore = DatastoreServiceFactory.getDatastoreService();
            txn = datastore.beginTransaction();
            datastore.put(e);
            txn.commit();

            list.add(e);

            // Add the follow entity to datastore
            e = new Entity("Follow");
            e.setProperty("user", "autoGen" + i + "@mail.mail");
            do {
                b = r.nextInt(100);
            } while (b == i || b == a);
            e.setProperty("following", "autoGen" + b + "@mail.mail");

            datastore = DatastoreServiceFactory.getDatastoreService();
            txn = datastore.beginTransaction();
            datastore.put(e);
            txn.commit();

            list.add(e);
        }
        return list;
    }


}
