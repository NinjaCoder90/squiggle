package com.misterycrew.Application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class ClientPaneGameOverFX implements Serializable {

    private static final long serialVersionUID = -7729936764728832664L;

    public void startGameOverPane(Stage primaryStage, String winner) {

        ClientMainFX mainFX = new ClientMainFX();

        ImageView imageGameOver = new ImageView(new Image("game-over.png", 260, 260, false, false));

        Label theWinnerIs = new Label();
        theWinnerIs.setText("CONGRATULATIONS TO THE WINNER");
        theWinnerIs.getStyleClass().add("winner-Label");

        Label winners = new Label();
        winners.setText(winner.toUpperCase());
        winners.getStyleClass().add("winner-Label");

        VBox winnerLabelBox = new VBox();
        winnerLabelBox.getChildren().addAll(theWinnerIs,winners);
        winnerLabelBox.setSpacing(9);
        winnerLabelBox.setAlignment(Pos.CENTER);

        Button exit = new Button("EXIT");
        exit.getStyleClass().add("exit-Button");
        exit.setOnMousePressed(e -> {
            Platform.exit();
            System.exit(0);
        });

        Button tryAgain = new Button("TRY AGAIN");
        tryAgain.getStyleClass().add("tryAgain-Button");
        tryAgain.setOnMousePressed(e -> mainFX.start(primaryStage));

        HBox tryAgainExit = new HBox();
        tryAgainExit.getChildren().addAll(tryAgain,exit);
        tryAgainExit.setAlignment(Pos.CENTER);
        tryAgainExit.setSpacing(15);

        VBox gameOverSection = new VBox(imageGameOver,winnerLabelBox,tryAgainExit);
        gameOverSection.setAlignment(Pos.CENTER);
        gameOverSection.setSpacing(30);
        gameOverSection.setStyle("-fx-background-color: #ECEFF1;");

        Scene gameOverScene = new Scene(gameOverSection,1388,695);
        gameOverScene.getStylesheets().add("Style.css");
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }
}
