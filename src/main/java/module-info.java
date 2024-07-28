module assign3.gomokugame {
    requires javafx.controls;
    requires javafx.fxml;


    opens assign3.gomokugame to javafx.fxml;
    exports assign3.gomokugame;
}