/*
 * Copyright 2017 King's College London and The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.stream.empatica;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.radarcns.stream.StreamDefinition;
import org.radarcns.stream.TimeWindowMetadata;

/**
 * Created by nivethika on 21-12-16.
 */
public class E4StreamsTest {
    private E4Streams e4Streams;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        this.e4Streams = E4Streams.getInstance();
    }

    @Test
    public void getTopic() {
        List<String> topicNames = Arrays.asList(
                "android_empatica_e4_acceleration",
                "android_empatica_e4_battery_level",
                "android_empatica_e4_blood_volume_pulse",
                "android_empatica_e4_electrodermal_activity",
                "android_empatica_e4_inter_beat_interval",
                "android_empatica_e4_temperature");

        for(String topicName : topicNames) {
            Collection<StreamDefinition> topic = this.e4Streams.getStreamDefinition(topicName);
            assertEquals(topic.iterator().next().getInputTopic().getName(), topicName);
        }
    }

    @Test
    public void getInternalTopic() {
        StreamDefinition topic = this.e4Streams.getStreamDefinition("android_empatica_e4_inter_beat_interval").iterator().next();
        assertEquals("android_empatica_e4_inter_beat_interval", topic.getInputTopic().getName());
        assertEquals("android_empatica_e4_heart_rate_10min", topic.getOutputTopic().getName());
        assertEquals("From-android_empatica_e4_inter_beat_interval-To-android_empatica_e4_heart_rate_10min", topic.getStateStoreName());
    }

    @Test
    public void getInvalidTopic() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Topic something unknown");
        this.e4Streams.getStreamDefinition("something");
    }

    @Test
    public void getTopicNames() {
        System.out.println(this.e4Streams.getTopicNames());
        assertEquals(8 * TimeWindowMetadata.values().length,
                this.e4Streams.getTopicNames().size()); // sort removes the redundant
    }
}
