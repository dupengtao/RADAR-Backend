package org.radarcns.util;

import org.apache.kafka.streams.kstream.Windowed;
import org.radarcns.empaticaE4.EmpaticaE4Acceleration;
import org.radarcns.key.MeasurementKey;
import org.radarcns.key.WindowedKey;

public class RadarUtils implements RadarUtilities{
    protected RadarUtils(){}
    public WindowedKey getWindowed(Windowed<MeasurementKey> window){
        return new WindowedKey(window.key().getUserId(), window.key().getSourceId(),
                window.window().start(), window.window().end());
    }

    public double floatToDouble(float input){
        return Double.parseDouble(String.valueOf(input));
    }

    public double ibiToHR(float input){
        return 60d / floatToDouble(input);
    }

    public double[] accelerationToArray(EmpaticaE4Acceleration value){
        return new double[] {
                floatToDouble(value.getX()),
                floatToDouble(value.getY()),
                floatToDouble(value.getY())};
    }
}
