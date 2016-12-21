package org.radarcns.empaticaE4.streams;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.radarcns.empaticaE4.EmpaticaE4BatteryLevel;
import org.radarcns.empaticaE4.topic.E4Topics;
import org.radarcns.key.MeasurementKey;
import org.radarcns.stream.aggregator.MasterAggregator;
import org.radarcns.stream.aggregator.SensorAggregator;
import org.radarcns.stream.collector.DoubleValueCollector;
import org.radarcns.topic.SensorTopic;
import org.radarcns.util.RadarSingletonFactory;
import org.radarcns.util.RadarUtilities;
import org.radarcns.util.RadarUtils;
import org.radarcns.util.serde.RadarSerdes;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Definition of Kafka Stream for aggregating data about Empatica E4 battery level
 */
public class E4BatteryLevel extends SensorAggregator<EmpaticaE4BatteryLevel> {

    private RadarUtilities utilities = RadarSingletonFactory.getRadarUtilities();

    public E4BatteryLevel(String clientID, int numThread, MasterAggregator master) throws IOException{
        super(E4Topics.getInstance().getSensorTopics().getBatteryLevelTopic(),clientID,numThread,master);
    }

    @Override
    protected void setStream(@Nonnull KStream<MeasurementKey, EmpaticaE4BatteryLevel> kstream,
                             @Nonnull SensorTopic<EmpaticaE4BatteryLevel> topic)
            throws IOException {
        kstream.groupByKey()
                .aggregate(
                    DoubleValueCollector::new,
                    (k, v, valueCollector) -> valueCollector.add(v.getBatteryLevel()),
                    TimeWindows.of(10 * 1000L),
                    RadarSerdes.getInstance().getDoubleCollector(),
                    topic.getStateStoreName())
                .toStream()
                .map((k,v) -> new KeyValue<>(utilities.getWindowed(k),v.convertInAvro()))
                .to(topic.getOutputTopic());
    }
}
