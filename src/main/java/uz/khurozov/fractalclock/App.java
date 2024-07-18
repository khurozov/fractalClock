package uz.khurozov.fractalclock;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uz.khurozov.fractalclock.fx.Clock;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label depthLabel = new Label("Depth");
        Spinner<Integer> depthSpinner = new Spinner<>(0, 7, 3);
        
        ToolBar toolBar = new ToolBar(depthLabel, depthSpinner);
        
        Clock clock = new Clock(
                stage.widthProperty().divide(2),
                stage.heightProperty().subtract(toolBar.heightProperty()).divide(2),
                (DoubleBinding) Bindings.min(stage.widthProperty(), stage.heightProperty().subtract(toolBar.heightProperty())).multiply(0.45),
                IntegerProperty.readOnlyIntegerProperty(depthSpinner.valueProperty())
        );

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(clock);
        mainPane.setTop(toolBar);
        stage.setScene(new Scene(mainPane, 600, 600));
        stage.setTitle("Fractal Clock");
        stage.initStyle(StageStyle.UTILITY);
        stage.show();

        clock.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}