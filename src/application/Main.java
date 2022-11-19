package application;

import application.Controller.ClientController;
import application.Controller.Controller;
import application.View.LoginWindow;
import application.View.UserListWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
  private static Stage stage;
  private static Scene scene;

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();

      fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
      Pane root = fxmlLoader.load();
      Controller controller = fxmlLoader.getController();
      ClientController client = new ClientController("ClientA", controller);
      LoginWindow.show(client);
      controller.setClientController(client);
      primaryStage.setTitle("Tic Tac Toe");
      scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);

      Thread clientThread = new Thread(client);
      clientThread.start();
      stage = primaryStage;

      primaryStage.setOnCloseRequest(event -> {
        client.stop();
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void close() {
    Platform.runLater(() -> stage.hide());
  }

  public static void showGame() {
    if (stage != null) {
      Text text = new Text("Self: x\nOpposite: name\n");
      Platform.runLater(() -> {
        stage.show();
      });
    }
  }


  public static void main(String[] args) {
    launch(args);
  }
}
