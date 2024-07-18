package uz.khurozov.fractalclock.fx;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

class Hand extends Line {
    private Hand mChild;
    private Hand sChild;

    public Hand(
            AnchorPane pane,
            DoubleBinding startXProp,
            DoubleBinding startYProp,
            DoubleBinding curAngleProp,
            DoubleBinding lengthProp,
            double width,
            DoubleProperty minutesAngleDiffProp,
            DoubleProperty secondsAngleDiffProp,
            IntegerBinding depthProp,
            int curDepth
    ) {
        pane.getChildren().add(this);
        this.setStrokeWidth(width);
        this.startXProperty().bind(startXProp);
        this.startYProperty().bind(startYProp);

        DoubleBinding endXProp = new DoubleBinding() {
            {
                super.bind(startXProp, curAngleProp, lengthProp);
            }

            @Override
            protected double computeValue() {
                return startXProp.get() + getXLen(curAngleProp.get(), lengthProp.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(startXProp, curAngleProp, lengthProp);
            }

            @Override
            public void dispose() {
                super.unbind(startXProp, curAngleProp, lengthProp);
            }
        };
        DoubleBinding endYProp = new DoubleBinding() {

            {
                super.bind(startYProp, curAngleProp, lengthProp);
            }

            @Override
            protected double computeValue() {
                return startYProp.get() + getYLen(curAngleProp.get(), lengthProp.get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(startYProp, curAngleProp, lengthProp);
            }

            @Override
            public void dispose() {
                super.unbind(startYProp, curAngleProp, lengthProp);
            }
        };
        this.endXProperty().bind(endXProp);
        this.endYProperty().bind(endYProp);

        if (depthProp != null) {
            depthProp.addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (newValue.intValue() < curDepth) {
                        pane.getChildren().remove(Hand.this);
                        depthProp.removeListener(this);
                        Hand.this.setVisible(false);
                    } else if (newValue.intValue() > curDepth && (mChild == null || !mChild.isVisible() || sChild == null || !sChild.isVisible())) {
                        addChildren(
                                pane,
                                endXProp,
                                endYProp,
                                curAngleProp,
                                lengthProp,
                                width,
                                minutesAngleDiffProp,
                                secondsAngleDiffProp,
                                depthProp,
                                curDepth
                        );
                    }
                }
            });
            addChildren(
                    pane,
                    endXProp,
                    endYProp,
                    curAngleProp,
                    lengthProp,
                    width,
                    minutesAngleDiffProp,
                    secondsAngleDiffProp,
                    depthProp,
                    curDepth
            );
        }
    }

    private void addChildren(
            AnchorPane pane,
            DoubleBinding endXProp,
            DoubleBinding endYProp,
            DoubleBinding curAngleProp,
            DoubleBinding lengthProp,
            double width,
            DoubleProperty minutesAngleDiffProp,
            DoubleProperty secondsAngleDiffProp,
            IntegerBinding depthProp,
            int curDepth
    ) {
        if (depthProp.get() > curDepth) {
            mChild = new Hand(
                    pane,
                    endXProp,
                    endYProp,
                    curAngleProp.subtract(minutesAngleDiffProp),
                    lengthProp.multiply(0.8),
                    width * 0.8,
                    minutesAngleDiffProp,
                    secondsAngleDiffProp,
                    depthProp,
                    curDepth+1
            );
            sChild = new Hand(
                    pane,
                    endXProp,
                    endYProp,
                    curAngleProp.subtract(secondsAngleDiffProp),
                    lengthProp.multiply(0.8),
                    width * 0.7,
                    minutesAngleDiffProp,
                    secondsAngleDiffProp,
                    depthProp,
                    curDepth+1
            );
        }
    }

    private static double getXLen(double angleDeg, double length) {
        return length * Math.cos(Math.toRadians(angleDeg));
    }

    private static double getYLen(double angleDeg, double length) {
        return length * Math.sin(Math.toRadians(angleDeg));
    }
}
