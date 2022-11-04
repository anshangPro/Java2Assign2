package application.View;

import application.Controller.ClientController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class UserListWindow {
    private static Stage stageW;
    private static ListView<String> listView;
    private static HashMap<String, String> lookUpTable;
    private static String chosen;

    private static void display(ClientController client) {
        Stage stage = new Stage();
        stageW = stage;
        stage.initModality(Modality.WINDOW_MODAL);
        BorderPane borderPane = new BorderPane();

        VBox vbox = new VBox();
        List<String> userNameList = Collections.emptyList();
        // 把清单对象转换为JavaFX控件能够识别的数据对象
        ObservableList<String> obList = FXCollections.observableArrayList(userNameList);
        listView = new ListView<String>(obList);
        lookUpTable = new HashMap<>();
//        listView.setItems(obList);
        listView.setPrefSize(400, 180);
        Label label = new Label("这里查看选择的对手");
        label.setWrapText(true);


        Button refresh = new Button("refresh");
        refresh.setOnAction((event) -> client.getList());
        client.getList();
        Button invite = new Button("invite");
        invite.setOnAction((event -> {
            if (chosen != null) {
                client.invite(lookUpTable.get(chosen));
                AlertWindow.show("inviting");
            }
        }));


        vbox.getChildren().addAll(listView, label, refresh, invite);
        // 设置列表视图的选择监听器
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String old_str, String new_str) {
                String desc;
                if (listView.getSelectionModel() != null){
                     desc = String.format("您点了第%d项，名称是%s",
                            listView.getSelectionModel().getSelectedIndex(),
                            listView.getSelectionModel().getSelectedItem().toString());
                } else desc = "这里查看选择的对手";
                label.setText(desc);
                chosen = listView.getSelectionModel().getSelectedItem().toString();
            }
        });
        borderPane.setCenter(vbox);

        Scene scene = new Scene(borderPane, 420, 200);

        stage.setScene(scene);
        stage.setTitle("info");
        stage.setOnCloseRequest(event -> {
            client.stop();
        });
        stage.show();
    }

    public static void setList(HashMap<String, String> map) {
        Platform.runLater(() -> {
                List < String > nameList = new ArrayList<>(map.keySet());
        ObservableList<String> obList = FXCollections.observableArrayList(nameList);
        listView.setItems(obList);
        lookUpTable = map;
        });
    }

    public static void close() {
        Platform.runLater(() -> stageW.close());
    }

    public static void show(ClientController clientController) {
        if (stageW != null) {
            Platform.runLater(() -> stageW.close());
        }
        Platform.runLater(() -> display(clientController));
    }
}
