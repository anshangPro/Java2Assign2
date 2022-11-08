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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginWindow {

    private static Stage stageW;

    private static void display(ClientController client) {
        Stage stage = new Stage();
        stageW = stage;
        stage.initModality(Modality.WINDOW_MODAL);
        GridPane gird = new GridPane();
        gird.setAlignment(Pos.CENTER);
        gird.setVgap(10);
        gird.setHgap(10);
        gird.setPadding(new Insets(25, 25, 25, 25));

        Text login = new Text("Please login");
        login.setFont(Font.font("Tahoma", 20));
        gird.add(login, 0, 0, 2, 1);

        Label name = new Label("name:");
        TextField nameField = new TextField();
        Label passwd = new Label("password:");
        PasswordField passwordField = new PasswordField();
        gird.add(name, 0, 1);
        gird.add(nameField, 1, 1);
        gird.add(passwd, 0, 2);
        gird.add(passwordField, 1, 2);

        Button signIn = new Button("Sign in");
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.BOTTOM_RIGHT);
        hb.getChildren().add(signIn);
        gird.add(hb, 1, 4);

        Button register = new Button("Register");
        HBox hb2 = new HBox(10);
        hb2.setAlignment(Pos.BOTTOM_RIGHT);
        hb2.getChildren().add(register);
        gird.add(hb2, 0, 4);

        signIn.setOnAction((event) -> client.login(nameField.getText(), passwordField.getText()));
        register.setOnAction((event) -> client.register(nameField.getText(), passwordField.getText()));

        Scene scene = new Scene(gird, 300, 170);

        stage.setScene(scene);
        stage.setTitle("info");
        stage.setOnCloseRequest(event -> {
            client.stop();
        });
        stage.show();
    }

    public static void close() {
        if (stageW != null) Platform.runLater(() -> stageW.close());
    }

    public static void show(ClientController clientController) {
        if (stageW != null) {
            Platform.runLater(() -> stageW.close());
        }
        Platform.runLater(() -> display(clientController));
    }
}
