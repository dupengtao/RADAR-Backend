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

import org.apache.kafka.streams.kstream.KStream;
import org.radarcns.config.KafkaProperty;
import org.radarcns.kafka.AggregateKey;
import org.radarcns.kafka.ObservationKey;
import org.radarcns.passive.empatica.EmpaticaE4BatteryLevel;
import org.radarcns.stream.StreamDefinition;
import org.radarcns.stream.StreamMaster;
import org.radarcns.stream.StreamWorker;
import org.radarcns.stream.aggregator.DoubleAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Kafka Stream for aggregating data about Empatica E4 battery level.
 */
public class E4BatteryLevelStream extends StreamWorker<ObservationKey, EmpaticaE4BatteryLevel> {
    private static final Logger logger = LoggerFactory.getLogger(E4BatteryLevelStream.class);

    public E4BatteryLevelStream(Collection<StreamDefinition> definitions, int numThread,
            StreamMaster master, KafkaProperty kafkaProperties) {
        super(definitions, numThread, master, kafkaProperties, logger);
    }

    @Override
    protected KStream<AggregateKey, DoubleAggregation> implementStream(StreamDefinition definition,
            @Nonnull KStream<ObservationKey, EmpaticaE4BatteryLevel> kstream) {
        return aggregateFloat(definition, kstream, EmpaticaE4BatteryLevel::getBatteryLevel);
    }
}
