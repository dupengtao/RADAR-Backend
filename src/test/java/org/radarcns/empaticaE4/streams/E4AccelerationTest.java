//package org.radarcns.empaticaE4.streams;
//
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.KStreamBuilder;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.mockito.Mockito;
//import org.mortbay.util.IO;
//import org.radarcns.config.ConfigRadar;
//import org.radarcns.config.PropertiesRadar;
//import org.radarcns.empaticaE4.EmpaticaE4Acceleration;
//import org.radarcns.empaticaE4.topic.E4SensorTopics;
//import org.radarcns.empaticaE4.topic.E4Topics;
//import org.radarcns.key.MeasurementKey;
//import org.radarcns.stream.aggregator.AggregatorWorker;
//import org.radarcns.stream.aggregator.MasterAggregator;
//import org.radarcns.topic.SensorTopic;
//
//import java.io.IOException;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * Created by nivethika on 21-12-16.
// */
//public class E4AccelerationTest {
//
//    private E4Acceleration e4Acceleration;
//    private int numOfThreads = 4;
//    private String clientID = "someclinet";
//    @Rule
//    public ExpectedException exception = ExpectedException.none();
//
//
//    @Before
//    public void setUp() throws IOException, Exception{
//        PropertiesRadar.load("radar.yml");
//        MasterAggregator aggregator = mock(MasterAggregator.class);
//        this.e4Acceleration= new E4Acceleration(clientID, numOfThreads, aggregator);
//    }
//
//    @Test
//    public void setStream() throws IOException{
////        ConfigRadar propertiesRadar = mock(ConfigRadar.class);
////        AggregatorWorker worker = mock(AggregatorWorker.class);
////        when(PropertiesRadar.getInstance()).thenReturn(propertiesRadar);
//
////        KStreamBuilder streamBuilder = new KStreamBuilder();
////        KStream<MeasurementKey, EmpaticaE4Acceleration> stream = streamBuilder.stream("E4Acceleration");
////        SensorTopic sensorTopic = E4Topics.getInstance().getSensorTopics().getAccelerationTopic();
////        e4Acceleration.setStream(stream, sensorTopic);
//
//    }
//}
