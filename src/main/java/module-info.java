module uz.khurozov.fractalclock {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.httpserver;


    opens uz.khurozov.fractalclock to javafx.fxml;
    exports uz.khurozov.fractalclock;
    exports uz.khurozov.fractalclock.fx;
    opens uz.khurozov.fractalclock.fx to javafx.fxml;
}