package radar.consumer.commit.synch;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nonnull;

import radar.consumer.ConsumerRadar;
import radar.utils.KafkaProperties;
import radar.utils.RadarConfig;

/**
 * Created by Francesco Nobilia on 29/09/2016.
 * Abstract class defining the behaviour of a Kafka Consumer that reads message At-Most-Once (AMO)
 * implementing a self commit policy
 */
public abstract class ConsumerAMO<K,V> extends ConsumerRadar{

    private final static Logger log = Logger.getLogger(ConsumerAMO.class);

    public RadarConfig config;

    private KafkaConsumer<K,V> consumer;
    private RadarConfig.PlatformTopics topics;
    private String clientID;

    private CountDownLatch shutdownLatch;

    public ConsumerAMO() {
        init(null,null,null);
    }

    public ConsumerAMO(String clientID) {
        init(clientID,null,null);
    }

    public ConsumerAMO(RadarConfig.PlatformTopics topics) {
        init(null,topics,null);
    }

    public ConsumerAMO(String clientID, RadarConfig.PlatformTopics topics) {
        init(clientID,topics,null);
    }

    public ConsumerAMO(RadarConfig.PlatformTopics topics, Properties properties) {
        init(null,topics,properties);
    }

    public ConsumerAMO(String clientID, RadarConfig.PlatformTopics topics, Properties properties) {
        init(clientID,topics,properties);
    }

    private void init(String clientID, RadarConfig.PlatformTopics topics, Properties properties){
        config = new RadarConfig();
        shutdownLatch = new CountDownLatch(1);

        properties = (properties == null) ? KafkaProperties.getSelfCommitConsumer(true,clientID) : properties;

        consumer = new KafkaConsumer(properties);

        this.topics = (topics == null) ? RadarConfig.PlatformTopics.all_in : topics;
    }

    /**
     * Must be implemented for customising consumer behaviour
     * @param record Kafka message currently consumed
     */
    public abstract void process(@Nonnull ConsumerRecord<K,V> record);

    /**
     * Commit the current partition state
     */
    private boolean doCommitSync() {
        try {
            consumer.commitSync();
            return true;
        } catch (CommitFailedException e) {
            log.error("Commit failed", e);
            return false;
        }
    }

    /**
     * Consume a list of Kafka Topics
     */
    public void run() {
        try {
            consumer.subscribe(config.getTopicList(topics));

            while (true) {
                ConsumerRecords<K,V> records = consumer.poll(Long.MAX_VALUE);
                if(doCommitSync()) {
                    records.forEach(record -> process(record));
                }
            }
        } catch (WakeupException e) {
            // ignore, we're closing
        } catch (SerializationException e) {
            log.error("Message cannot be serialised", e);
            //TODO
        }
        catch (Exception e) {
            log.error("Unexpected error", e);
        } finally {
            consumer.close();
            shutdownLatch.countDown();
        }
    }

    /**
     * Shutdown the consumer and force the commit.
     * Derived classes must override and call through to the super class's implementation of this
     * method.
     */
    public void shutdown() throws InterruptedException {
        consumer.wakeup();
        shutdownLatch.await();

        log.info("SHUTDOWN");
    }
}

