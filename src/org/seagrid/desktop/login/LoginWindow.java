package org.seagrid.desktop.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.airavata.model.workspace.Project;

import java.io.IOException;

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        primaryStage.setTitle("SEAGrid Desktop Client - Login");
        primaryStage.setScene(new Scene(root, 300, 125));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void displayAndWait() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        primaryStage.setTitle("SEAGrid Desktop Client - Login");
        primaryStage.setScene(new Scene(root, 300, 125));
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
