package entities;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sources :
 *  - https://download.huihoo.com/google/gdgdevkit/DVD1/developers.google.com/appengine/articles/sharding_counters.html
 *  - https://github.com/GoogleCloudPlatform/appengine-sharded-counters-java
 *
 * This method allow high scalability counters, but the downfall is that the Datastore became non readable by humans (It add between INITIAL_SHARD and MAX_SHARD kinds per counter)
 *
 * There is almost no change to the initial code
 */
public class ShardedCounter {

    /**
     * Convenience class which contains constants related to a named sharded
     * counter. The counter name provided in the constructor is used as the
     * entity key.
     */
    private static final class Counter {
        /**
         * Entity kind representing a named sharded counter.
         */
        private static final String KIND = "Counter";


        /**
         * Property to store the number of shards in a given {@value #KIND}
         * named sharded counter.
         */
        private static final String SHARD_COUNT = "shard_count";
    }

    /**
     * Convenience class which contains constants related to the counter shards.
     * The shard number (as a String) is used as the entity key.
     */
    private static final class CounterShard {
        /**
         * Entity kind prefix, which is concatenated with the counter name to
         * form the final entity kind, which represents counter shards.
         */
        private static final String KIND_PREFIX = "CounterShard_";

        /**
         * Property to store the current count within a counter shard.
         */
        private static final String COUNT = "count";
    }

    /**
     * DatastoreService object for Datastore access.
     */
    private static final DatastoreService DS = DatastoreServiceFactory
            .getDatastoreService();

    /**
     * Default number of shards.
     */
    private static final int INITIAL_SHARDS = 5;

    /**
     * this holds the possible number of shards that can be created dynamically
     * and then stop , since our dataset total is around 500 per second, we don't
     * need many shards incase the problem of splitting occurs, so we don't have
     * many dublicates
     */
    private static final int MAX_SHARDS = 15;

    /**
     * Cache duration for memcache.
     */
    private static final int CACHE_PERIOD = 60;
    /**
     * The name of this counter.
     */
    private final String counterName;


    /**
     * A random number generating, for distributing writes across shards.
     */
    private final Random generator = new Random();

    /**
     * The counter shard kind for this counter.
     */
    private String kind;

    /**
     * Memcache service object for Memcache access.
     */
//    private final MemcacheService mc = MemcacheServiceFactory
//            .getMemcacheService();

    /**
     * A logger object.
     */
    private static final Logger LOG = Logger.getLogger(ShardedCounter.class
            .getName());

    /**
     * Constructor which creates a sharded counter using the provided counter
     * name.
     *
     * @param name
     *            name of the sharded counter
     */
    public ShardedCounter(final String name) {
        counterName = name;
        kind = CounterShard.KIND_PREFIX + counterName;
    }

    /**
     * Increase the number of shards for a given sharded counter. Will never
     * decrease the number of shards.
     *
     * @param count
     *            Number of new shards to build and store
     */
    public final void addShards(final int count) {
        Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
        incrementPropertyTx(counterKey, Counter.SHARD_COUNT, count,
                INITIAL_SHARDS + count);
    }

    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    public final long getCount() {
//        Long value = (Long) mc.get(kind);
//        if (value != null) {
//            return value;
//        }

        long sum = 0;
        Query query = new Query(kind);
        for (Entity shard : DS.prepare(query).asIterable()) {
            sum += (Long) shard.getProperty(CounterShard.COUNT);
        }
//        mc.put(kind, sum, Expiration.byDeltaSeconds(CACHE_PERIOD),
//                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return sum;
    }
    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    public final long getCount(DatastoreService datastoreService) {
//        Long value = (Long) mc.get(kind);
//        if (value != null) {
//            return value;
//        }

        long sum = 0;
        Query query = new Query(kind);
        for (Entity shard : datastoreService.prepare(query).asIterable()) {
            sum += (Long) shard.getProperty(CounterShard.COUNT);
        }
//        mc.put(kind, sum, Expiration.byDeltaSeconds(CACHE_PERIOD),
//                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return sum;
    }

    /**
     * Increment the value of this sharded counter.
     */
    public final void increment() {
        // Find how many shards are in this counter.
        int numShards = getShardCount();

        // Choose the shard randomly from the available shards.
        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        incrementPropertyTx(shardKey, CounterShard.COUNT, 1, 1);
//        mc.increment(kind, 1);
    }

    /**
     * Increment the value of this sharded counter.
     */
    public final void increment(Transaction tc, DatastoreService datastoreService) {
        // Find how many shards are in this counter.
        int numShards = getShardCount();

        // Choose the shard randomly from the available shards.
        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        incrementPropertyTx(shardKey, CounterShard.COUNT, 1, 1, tc, datastoreService);
//        mc.increment(kind, 1);
    }



    /**
     * Decrement the value of this sharded counter.
     */
    public final void decrement() {
        // Find how many shards are in this counter.
        int numShards = getShardCount();

        // Choose the shard randomly from the available shards.
        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        decrementPropertyTx(shardKey, CounterShard.COUNT, 1, 1);
//
//        mc.put(kind, getCount(), Expiration.byDeltaSeconds(CACHE_PERIOD),
//                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

    }

    /**
     * Get the number of shards in this counter.
     *
     * @return shard count
     */
    private int getShardCount() {
        try {
            Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
            Entity counter = DS.get(counterKey);
            Long shardCount = (Long) counter.getProperty(Counter.SHARD_COUNT);
            return shardCount.intValue();
        } catch (EntityNotFoundException ignore) {
            return INITIAL_SHARDS;
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString());
            return INITIAL_SHARDS;
        }
    }

    /**
     * Increment datastore property value inside a transaction. If the entity
     * with the provided key does not exist, instead create an entity with the
     * supplied initial property value.
     *
     * @param key
     *            the entity key to update or create
     * @param prop
     *            the property name to be incremented
     * @param increment
     *            the amount by which to increment
     * @param initialValue
     *            the value to use if the entity does not exist
     */
    private void incrementPropertyTx(final Key key, final String prop,
                                     final long increment, final long initialValue) {


        Transaction tx = DS.beginTransaction();
        Entity thing;
        long value;
        try {
            try {
                thing = DS.get(tx, key);
                value = (Long) thing.getProperty(prop) + increment;
            } catch (EntityNotFoundException e) {
                thing = new Entity(key);
                value = initialValue;
            }
            thing.setUnindexedProperty(prop, value);
            DS.put(tx, thing);
            tx.commit();
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING,
                    "You may need more shards. Consider adding more shards.");
            LOG.log(Level.WARNING, e.toString(), e);

            if(getShardCount() <= MAX_SHARDS){
                LOG.log(Level.INFO,
                        "Doubling Shade, for this process");
                addShards(getShardCount()); //double the shade by adding the number of shade again
            }

        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString(), e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    /**
     * Increment datastore property value inside a transaction. If the entity
     * with the provided key does not exist, instead create an entity with the
     * supplied initial property value.
     *
     * @param key
     *            the entity key to update or create
     * @param prop
     *            the property name to be incremented
     * @param increment
     *            the amount by which to increment
     * @param initialValue
     *            the value to use if the entity does not exist
     */
    private void incrementPropertyTx(final Key key, final String prop,
                                     final long increment, final long initialValue, Transaction tc, DatastoreService datastoreService) {

        Entity thing;
        long value;
        Random ramd = new Random();
        int txnId = ramd.nextInt();
        try {
            try {
                thing = datastoreService.get(key);
                value = (Long) thing.getProperty(prop) + increment;
            } catch (EntityNotFoundException e) {
                thing = new Entity(key);
                value = initialValue;
            }
            thing.setUnindexedProperty(prop, value);
            datastoreService.put(tc, thing);
            System.out.println("Commit  txnId : " + txnId);
            tc.commit();
            System.out.println("After Commit  txnId : " + txnId);
            // Block finally block
            return;
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING,
                    "You may need more shards. Consider adding more shards." + " txnId : " + txnId);
            LOG.log(Level.WARNING, e.toString(), e);

            if(getShardCount() <= MAX_SHARDS){
                LOG.log(Level.INFO,
                        "Doubling Shade, for this process");
                addShards(getShardCount()); //double the shade by adding the number of shards again
            }

        } catch (Exception e) {
            LOG.log(Level.WARNING, " txnId : " + txnId, e);
        } finally {
            System.out.println("Rollback txnId : " + txnId + ", count " + getShardCount());
            if (tc.isActive()) {
                tc.rollback();
            }
        }
    }



    /**
     * Decrement datastore property value inside a transaction. If the entity
     * with the provided key does not exist, instead create an entity with the
     * supplied initial property value.
     *
     * @param key
     *            the entity key to update or create
     * @param prop
     *            the property name to be decremented
     * @param decrement
     *            the amount by which to decrement
     * @param initialValue
     *            the value to use if the entity does not exist
     */
    private void decrementPropertyTx(final Key key, final String prop,
                                     final long decrement, final long initialValue) {

        Transaction tx = DS.beginTransaction();
        Entity thing;
        long value;
        try {
            try {
                thing = DS.get(tx, key);
                value = (Long) thing.getProperty(prop) - decrement;
            } catch (EntityNotFoundException e) {
                thing = new Entity(key);
                value = 0;
            }
            thing.setUnindexedProperty(prop, value);
            DS.put(tx, thing);
            tx.commit();
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING,
                    "You may need more shards. Consider adding more shards.");
            LOG.log(Level.WARNING, e.toString(), e);

            if(getShardCount() <= MAX_SHARDS){
                LOG.log(Level.INFO,
                        "Doubling Shade, for this process");
                addShards(getShardCount()); //double the shade by adding the number of shade again
            }

        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString(), e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
}