/*
 * Copyright 2017 Kings College London and The Hyve
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

package org.radarcns.empatica.topic;

import org.radarcns.topic.OutputStreamGroup;
import org.radarcns.topic.StreamDefinition;

/**
 * Entire set of Empatica E4 SensorTopic
 */
public final class E4SensorStreams extends OutputStreamGroup {

    //All sensor topics
    private final StreamDefinition accelerationStream;
    private final StreamDefinition batteryLevelStream;
    private final StreamDefinition bloodVolumePulseStream;
    private final StreamDefinition electroDermalActivityStream;
    private final StreamDefinition interBeatIntervalStream;
    private final StreamDefinition sensorStatusStream;
    private final StreamDefinition temperatureStream;

    private static E4SensorStreams instance = new E4SensorStreams();

    static E4SensorStreams getInstance() {
        return instance;
    }

    private E4SensorStreams() {
        accelerationStream = createSensorStream(
                "android_empatica_e4_acceleration");
        batteryLevelStream = createSensorStream(
                "android_empatica_e4_battery_level");
        bloodVolumePulseStream = createSensorStream(
                "android_empatica_e4_blood_volume_pulse");
        electroDermalActivityStream = createSensorStream(
                "android_empatica_e4_electrodermal_activity");
        interBeatIntervalStream = createSensorStream(
                "android_empatica_e4_inter_beat_interval");
        sensorStatusStream = createSensorStream(
                "android_empatica_e4_sensor_status");
        temperatureStream = createSensorStream(
                "android_empatica_e4_temperature");
    }

    public StreamDefinition getAccelerationStream() {
        return accelerationStream;
    }

    public StreamDefinition getBatteryLevelStream() {
        return batteryLevelStream;
    }

    public StreamDefinition getBloodVolumePulseStream() {
        return bloodVolumePulseStream;
    }

    public StreamDefinition getElectroDermalActivityStream() {
        return electroDermalActivityStream;
    }

    public StreamDefinition getInterBeatIntervalStream() {
        return interBeatIntervalStream;
    }

    public StreamDefinition getSensorStatusStream() {
        return sensorStatusStream;
    }

    public StreamDefinition getTemperatureStream() {
        return temperatureStream;
    }

}