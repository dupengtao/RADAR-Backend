/*
 * Copyright 2017 The Hyve and King's College London
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

package org.radarcns.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.radarcns.topic.KafkaTopic;

/**
 * Implementation of a {@link StreamGroup}. Override to create specific streams for a given
 * device: use the {@link #createStream(String, String)} to create an internal stream and
 * {@link #createSensorStream(String)} to create a sensor stream.
 *
 * <p>To access the streams, create getter functions or use the {@link #getStreamDefinition(String)}
 * method.
 */
public class GeneralStreamGroup implements StreamGroup {
    public static final String OUTPUT_LABEL = "_output";

    private final Map<String, Collection<StreamDefinition>> topicMap;
    private final Set<String> topicNames;

    public GeneralStreamGroup() {
        topicMap = new HashMap<>();
        topicNames = new TreeSet<>();
    }

    /**
     * Create a stream from input to output topic. By using this method, {@link #getTopicNames()}
     * and {@link #getStreamDefinition(String)} automatically get updated.
     * @param input input topic name
     * @param output output topic name
     * @return stream definition.
     */
    protected Collection<StreamDefinition> createStream(String input, String output) {
        return createStream(input, output, 0L);
    }

    /**
     * Create a stream from input to output topic. By using this method, {@link #getTopicNames()}
     * and {@link #getStreamDefinition(String)} automatically get updated.
     * @param input input topic name
     * @param output output topic name
     * @param window time windows size in milliseconds, 0 if none.
     * @return stream definition.
     */
    protected Collection<StreamDefinition> createStream(String input, String output, long window) {
        Collection<StreamDefinition> ret = Collections.singleton(
                new StreamDefinition(new KafkaTopic(input), new KafkaTopic(output), window));
        topicMap.put(input, ret);
        topicNames.add(input);
        topicNames.add(output);
        return ret;
    }

    /**
     * Create a sensor stream from input topic to a "[input]_output" topic. By using this method,
     * {@link #getTopicNames()} and {@link #getStreamDefinition(String)} automatically get updated.
     * @param input input topic name
     * @return sensor stream definition
     */
    protected Collection<StreamDefinition> createSensorStream(String input) {
        return createStream(input, input + OUTPUT_LABEL, 0L);
    }

    /**
     * Create a set of sensor streams, for each of the RADAR standard time frames. An input topic
     * {@code my_input} will create, e.g., {@code my_input_10sec}, {@code my_input_10min} output
     * topics.
     * @param input topic to stream from
     * @return stream definitions to stream
     */
    protected Collection<StreamDefinition> createWindowedSensorStream(String input) {
        return createWindowedSensorStream(input, input);
    }

    /**
     * Create a set of sensor streams, for each of the RADAR standard time frames. An input topic
     * {@code my_input} with output base {@code my_output} will create, e.g.,
     * {@code my_output_10sec}, {@code my_output_10min} output topics.
     * @param input topic to stream from
     * @param outputBase base topic name to stream to
     * @return stream definitions to stream
     */
    protected Collection<StreamDefinition> createWindowedSensorStream(String input,
            String outputBase) {

        topicNames.add(input);
        Collection<StreamDefinition> streams = Arrays.stream(TimeWindowMetadata.values())
                .map(w -> new StreamDefinition(
                        new KafkaTopic(input), new KafkaTopic(w.getTopicLabel(outputBase)),
                        w.getIntervalInMilliSec(), getCommitIntervalForTimeWindow(w)))
                .collect(Collectors.toList());

        topicNames.addAll(streams.stream()
                .map(t -> t.getOutputTopic().getName())
                .collect(Collectors.toList()));

        topicMap.merge(input, streams, (v1, v2) -> {
            Set<StreamDefinition> newSet = new TreeSet<>(v1);
            newSet.addAll(v2);
            return newSet;
        });

        return streams;
    }

    public void addTopicName(String topicName) {
        this.topicNames.add(topicName);
    }

    public void addTopicNames(Collection<String> topicNames) {
        this.topicNames.addAll(topicNames);
    }

    public long getCommitIntervalForTimeWindow(TimeWindowMetadata metadata) {
        switch (metadata) {
            case ONE_DAY:
                return CommitInterval.COMMIT_INTERVAL_FOR_ONE_DAY.getCommitInterval();
            case ONE_MIN:
                return CommitInterval.COMMIT_INTERVAL_FOR_ONE_MIN.getCommitInterval();
            case TEN_MIN:
                return CommitInterval.COMMIT_INTERVAL_FOR_TEN_MIN.getCommitInterval();
            case ONE_HOUR:
                return CommitInterval.COMMIT_INTERVAL_FOR_ONE_HOUR.getCommitInterval();
            case ONE_WEEK:
                return CommitInterval.COMMIT_INTERVAL_FOR_ONE_WEEK.getCommitInterval();
            case TEN_SECOND:
                return CommitInterval.COMMIT_INTERVAL_FOR_TEN_SECOND.getCommitInterval();
            default:
                return CommitInterval.COMMIT_INTERVAL_DEFAULT.getCommitInterval();
        }
    }

    @Override
    public Collection<StreamDefinition> getStreamDefinition(String inputTopic) {
        Collection<StreamDefinition> topic = topicMap.get(inputTopic);
        if (topic == null) {
            throw new IllegalArgumentException("Topic " + inputTopic + " unknown");
        }
        return topic;
    }

    @Override
    public List<String> getTopicNames() {
        List<String> topicList = new ArrayList<>(topicNames);
        topicList.sort(String.CASE_INSENSITIVE_ORDER);
        return topicList;
    }

    public enum CommitInterval {
        COMMIT_INTERVAL_FOR_TEN_SECOND(10_000L),
        COMMIT_INTERVAL_FOR_ONE_MIN(30_000L),
        COMMIT_INTERVAL_FOR_TEN_MIN(300_000L),
        COMMIT_INTERVAL_FOR_ONE_HOUR(1800_000L),
        COMMIT_INTERVAL_FOR_ONE_DAY(7200_000L),
        COMMIT_INTERVAL_FOR_ONE_WEEK(10800_000L),
        COMMIT_INTERVAL_DEFAULT(30_000L);

        private final long commitInterval;


        CommitInterval(long commitInterval) {
            this.commitInterval = commitInterval;
        }

        public long getCommitInterval() {
            return commitInterval;
        }
    }
}
