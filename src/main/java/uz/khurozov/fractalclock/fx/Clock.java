package uz.khurozov.fractalclock.fx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.time.LocalTime;

public class Clock extends AnchorPane {
    private final Timeline timer;
    private final StringProperty timeTextProperty;

    public Clock(
            DoubleBinding centerXProperty,
            DoubleBinding centerYProperty,
            DoubleBinding maxLength,
            ReadOnlyIntegerProperty depthProperty
    ) {
        SimpleDoubleProperty hourAngle = new SimpleDoubleProperty(0);
        SimpleDoubleProperty mad = new SimpleDoubleProperty(0);
        SimpleDoubleProperty sad = new SimpleDoubleProperty(0);

        DoubleBinding length = maxLength.multiply(0.2).divide(new DoubleBinding() {
            {
                super.bind(depthProperty);
            }

            @Override
            protected double computeValue() {
                return 1.0 - Math.pow(0.8, depthProperty.get()+1);
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(depthProperty);
            }

            @Override
            public void dispose() {
                super.unbind(depthProperty);
            }
        });

        IntegerBinding depth = depthProperty.add(0);

        timeTextProperty = new SimpleStringProperty("00:00:00.0");
        timer = new Timeline(new KeyFrame(
                Duration.millis(100),
                "timer",
                actionEvent -> {
                    LocalTime now = LocalTime.now();
                    int hours = now.getHour();
                    int minutes = now.getMinute();
                    int seconds = now.getSecond();
                    long millis = now.getNano() / 1000000L;

                    double hA = (hours % 12 + minutes / 60.0) * 30;
                    double mA = (minutes + seconds / 60.0) * 6;
                    double sA = (seconds + millis / 1000.0) * 6;
                    hourAngle.set(hA - 90);
                    mad.set(hA - mA);
                    sad.set(hA - sA);
                    
                    timeTextProperty.set(now.toString().substring(0, 10));
                }
        ));
        timer.setCycleCount(-1);

        new Hand(
                this,
                centerXProperty,
                centerYProperty,
                hourAngle.subtract(0),
                length.multiply(0.8),
                5.0,
                null,
                null,
                null,
                0
        );
        new Hand(
                this,
                centerXProperty,
                centerYProperty,
                hourAngle.subtract(mad),
                length.multiply(0.9),
                4.0,
                mad,
                sad,
                depth,
                0
        );
        new Hand(
                this,
                centerXProperty,
                centerYProperty,
                hourAngle.subtract(sad),
                length.multiply(1),
                3.5,
                mad,
                sad,
                depth,
                0
        );
    }

    public void play() {
        timer.play();
    }
    
    public ReadOnlyStringProperty timeTextProperty() {
        return timeTextProperty;
    }
}
