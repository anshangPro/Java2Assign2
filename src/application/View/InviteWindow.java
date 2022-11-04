package application.View;

import application.Controller.ClientController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InviteWindow {
    private static Stage stageW;

    private static void display(ClientController client, String invitor, String uuid) {
        Stage stage = new Stage();
        stageW = stage;
        stage.initModality(Modality.WINDOW_MODAL);
        GridPane gird = new GridPane();
        gird.setAlignment(Pos.CENTER);
        gird.setVgap(10);
        gird.setHgap(10);
        gird.setPadding(new Insets(25, 25, 25, 25));

        Text login = new Text(String.format("Invitation from %s", invitor));
        login.setFont(Font.font("Tahoma", 20));
        gird.add(login, 0, 0, 2, 1);

        Button accept = new Button("Accept");
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.BOTTOM_RIGHT);
        hb.getChildren().add(accept);
        gird.add(hb, 1, 3);

        Button reject = new Button("Reject");
        HBox hb2 = new HBox(10);
        hb2.setAlignment(Pos.BOTTOM_RIGHT);
        hb2.getChildren().add(reject);
        gird.add(hb2, 0, 3);

        accept.setOnAction((event) -> {
            client.accept(uuid);
            close();
        });
        reject.setOnAction((event) -> {
            client.reject(uuid);
            close();
        });

        Scene scene = new Scene(gird, 300, 170);

        stage.setScene(scene);
        stage.setTitle("info");
        stage.setOnCloseRequest(event -> {
            client.reject(uuid);
        });
        stage.show();
    }

    public static void close() {
        Platform.runLater(() -> stageW.close());
    }

    public static void show(ClientController clientController, String invitor, String uuid) {
        if (stageW != null) {
            Platform.runLater(() -> stageW.close());
        }
        AlertWindow.close();
        Platform.runLater(() -> display(clientController, invitor, uuid));
    }
}
