package application.View;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertWindow {

    private static Stage stageW;
    private static void display(String info) {
        Stage stage = new Stage();
        stageW = stage;
        stage.initModality(Modality.WINDOW_MODAL);
        Label label = new Label(info);
        label.setWrapText(true);
        label.setPadding(new Insets(5, 20, 5, 20));
        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 220, 70);

        stage.setScene(scene);
        stage.setTitle("info");
        stage.show();
    }

    public static void show(String info) {
        if (stageW != null) {
            Platform.runLater(() -> stageW.close());
        }
        Platform.runLater(() -> display(info));
    }
}
