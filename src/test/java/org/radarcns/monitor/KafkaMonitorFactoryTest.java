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

package org.radarcns.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;
import org.radarcns.config.BatteryMonitorConfig;
import org.radarcns.config.ConfigRadar;
import org.radarcns.config.DisconnectMonitorConfig;
import org.radarcns.config.RadarBackendOptions;
import org.radarcns.config.RadarPropertyHandler;
import org.radarcns.config.RadarPropertyHandlerImpl;

public class KafkaMonitorFactoryTest {
    @Test
    public void createBatteryMonitor() throws Exception {
        String[] args = {"monitor", "battery"};
        RadarBackendOptions options = RadarBackendOptions.parse(args);
        ConfigRadar config = new ConfigRadar();
        BatteryMonitorConfig batteryConfig = new BatteryMonitorConfig();
        batteryConfig.setEmailAddress("test@localhost");
        batteryConfig.setLevel("LOW");
        batteryConfig.setEmailAddress("radarcns@hyve.net");
        batteryConfig.setEmailHost("localhost");
        batteryConfig.setEmailPort(25);
        batteryConfig.setEmailUser("someuser");
        config.setBatteryMonitor(batteryConfig);
        String tmpDir = Files.createTempDirectory(null).toAbsolutePath().toString();
        config.setPersistencePath(tmpDir);
        config.setSchemaRegistry(Collections.emptyList());
        config.setBroker(Collections.emptyList());

        File tmpConfig = File.createTempFile("radar", ".yml");
        config.store(tmpConfig);

        RadarPropertyHandler properties = new RadarPropertyHandlerImpl();
        properties.load(tmpConfig.getAbsolutePath());

        KafkaMonitor monitor = new KafkaMonitorFactory(options, properties).createMonitor();
        assertEquals(BatteryLevelMonitor.class, monitor.getClass());
        BatteryLevelMonitor batteryMonitor = (BatteryLevelMonitor) monitor;
        batteryMonitor.evaluateRecords(new ConsumerRecords<>(Collections.emptyMap()));
        assertTrue(new File(tmpDir, "battery_monitors_1.yml").isFile());
    }

    @Test
    public void createDisconnectMonitor() throws Exception {
        String[] args = {"monitor", "disconnect"};
        RadarBackendOptions options = RadarBackendOptions.parse(args);
        ConfigRadar config = new ConfigRadar();
        DisconnectMonitorConfig disconnectConfig = new DisconnectMonitorConfig();
        disconnectConfig.setEmailAddress("test@localhost");
        disconnectConfig.setTimeout(100L);
        String tmpDir = Files.createTempDirectory(null).toAbsolutePath().toString();
        config.setPersistencePath(tmpDir);
        config.setSchemaRegistry(Collections.emptyList());
        config.setBroker(Collections.emptyList());

        File tmpConfig = File.createTempFile("radar", ".yml");
        config.store(tmpConfig);

        RadarPropertyHandler properties = new RadarPropertyHandlerImpl();
        properties.load(tmpConfig.getAbsolutePath());

        KafkaMonitor monitor = new KafkaMonitorFactory(options, properties).createMonitor();
        assertEquals(DisconnectMonitor.class, monitor.getClass());
        DisconnectMonitor disconnectMonitor = (DisconnectMonitor) monitor;
        disconnectMonitor.evaluateRecords(new ConsumerRecords<>(Collections.emptyMap()));
        assertTrue(new File(tmpDir, "temperature_disconnect_1.yml").isFile());
    }
}