package JavaFX;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientPaneFX extends Application {

    private Label coord,wordToGuess;
    private final Canvas canvas = new Canvas(690, 620);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private RadioButton rb1,rb2;
    private String color = "black";
    private Double x1 = null;
    private Double y1 = null;
    private Button btnColorRed,btnColorBlack,btnColorGreen,btnColorPurple,btnColorPink,btnColorOrange,btnColorBlue;
    private TextArea users,chatSection;
    private TextField chatField;

    private void updateCoord(double x, double y) {
        coord.setText("(" + x + "," + y + ")");
    }


    public void start(Stage primaryStage) {

        var javaVersion = System.getProperty("java.version");
        var javafxVersion = System.getProperty("javafx.version");
        Label label = new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        label.setStyle("-fx-font-weight: bold; -fx-font-family: 'JetBrains Mono NL'; -fx-font-size: 15;");

        wordToGuess = new Label();
        wordToGuess.setText(showWordToGuess());
        wordToGuess.setPrefHeight(50);
        wordToGuess.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-font-family: 'JetBrains Mono Medium';");

        users = new TextArea("LEADERBOARD");
        users.setEditable(false);
        users.setPrefHeight(290);
        users.setMaxWidth(250);
        users.setStyle("-fx-control-inner-background: #2C3030;-fx-text-fill: #ffff;-fx-font-size: 14; -fx-font-family: 'JetBrains Mono Medium';");

        HBox bottom = new HBox();
        bottom.setStyle("-fx-background-color: #171A1A;");
        bottom.setAlignment(Pos.CENTER);
        final ToggleGroup options = new ToggleGroup();
        BorderPane borderPane = new BorderPane();

        chatSection = new TextArea();
        chatSection.setStyle("-fx-control-inner-background: #171A1A; -fx-highlight-fill: #171A1A; -fx-highlight-text-fill: #ffff; -fx-text-fill: #ffff;" +
                             "-fx-font-size: 14; -fx-font-family: 'JetBrains Mono Medium';");
        chatSection.setEditable(false);
        chatSection.setPrefHeight(569);
        chatSection.setPrefWidth(280);

        chatField = new TextField();
        chatField.setStyle("-fx-background-color: #2C3030; -fx-text-fill: #ffff;");
        chatField.setPrefHeight(51);
        chatField.setPromptText("Type your guess here...");
        chatField.setOnKeyPressed(this::sendMessage);

        VBox rightSection = new VBox();
        rightSection.getChildren().addAll(chatSection,chatField);

        HBox marginRightSection = new HBox();
        marginRightSection.getChildren().add(rightSection);
        HBox.setMargin(rightSection, new Insets(60, 20, 39, 30));

        VBox marginCanvas = new VBox();
        marginCanvas.getChildren().addAll(wordToGuess,canvas,label);
        marginCanvas.setAlignment(Pos.CENTER);
        marginCanvas.setSpacing(10);

        VBox sidePane = new VBox();
        sidePane.setStyle("-fx-background-color: #171A1A;");
        sidePane.setAlignment(Pos.CENTER);
        HBox marginSideSection = new HBox();
        marginSideSection.getChildren().addAll(sidePane);
        HBox.setMargin(sidePane, new Insets(60, 30, 39, 20));

        rb1 = new RadioButton("DRAW");
        rb1.setStyle("-fx-text-fill: #ffff;");
        rb1.setToggleGroup(options);
        rb1.setSelected(true);

        rb2  = new RadioButton("ERASE");
        rb2.setStyle("-fx-text-fill: #ffff;");
        rb2.setToggleGroup(options);
        HBox radioButtonSection = new HBox();
        radioButtonSection.setSpacing(45.0);
        radioButtonSection.setAlignment(Pos.CENTER);
        radioButtonSection.getChildren().addAll(rb1,rb2);

        HBox colorSection1 = new HBox();

        btnColorRed = new Button();
        btnColorRed.setStyle("-fx-background-color: #d00000;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorRed.setOnAction(this::processColorChange);

        btnColorBlack = new Button();
        btnColorBlack.setStyle("-fx-background-color: #000000;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorBlack.setOnAction(this::processColorChange);

        btnColorBlue = new Button();
        btnColorBlue.setStyle("-fx-background-color: #1440ee;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorBlue.setOnAction(this::processColorChange);

        btnColorPink = new Button();
        btnColorPink.setStyle("-fx-background-color: #af09bd;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorPink.setOnAction(this::processColorChange);

        colorSection1.setSpacing(15.0);
        colorSection1.setAlignment(Pos.CENTER);
        colorSection1.getChildren().addAll(btnColorRed, btnColorBlack, btnColorBlue,btnColorPink);

        HBox colorSection2 = new HBox();

        Button btnColorYellow = new Button();
        btnColorYellow.setStyle("-fx-background-color: #f6ff00; -fx-pref-height: 30; -fx-pref-width: 30");
        btnColorYellow.setOnAction(this::processColorChange);

        btnColorOrange = new Button();
        btnColorOrange.setStyle("-fx-background-color: #ff9b42; -fx-pref-height: 30; -fx-pref-width: 30");
        btnColorOrange.setOnAction(this::processColorChange);

        btnColorGreen = new Button();
        btnColorGreen.setStyle("-fx-background-color: #057955;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorGreen.setOnAction(this::processColorChange);

        btnColorPurple = new Button();
        btnColorPurple.setStyle("-fx-background-color: #460fe7;-fx-pref-height: 30; -fx-pref-width: 30");
        btnColorPurple.setOnAction(this::processColorChange);

        colorSection2.setSpacing(15.0);
        colorSection2.setAlignment(Pos.CENTER);
        colorSection2.getChildren().addAll(btnColorYellow,btnColorOrange,btnColorGreen,btnColorPurple);

        Button clear = new Button("CLEAR CANVAS");
        clear.setPadding(new Insets(8, 8, 8, 8));
        clear.setStyle("-fx-background-color: #404747; -fx-text-fill: #ffff;");
        clear.setOnMouseClicked(this::clearCanvas);

        Label curColor = new Label("COLOR: BLACK");
        curColor.setStyle("-fx-text-fill: #ffff;");

        coord = new Label("(x, y)");
        coord.setStyle("-fx-text-fill: #ffff; -fx-min-width: 87");

        sidePane.getChildren().addAll(users,coord,radioButtonSection,colorSection1,colorSection2,clear);
        sidePane.setPrefWidth(280);
        sidePane.setMaxHeight(685);
        sidePane.setSpacing(25.0);

        Pane center = new Pane();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 690, 620);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(8);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(3);
        blur.setHeight(3);
        blur.setIterations(6);
        gc.setEffect(blur);

        canvas.setOnMouseMoved(this::cursorMoved);
        canvas.setOnMouseDragged(this::cursorDragged);
        canvas.setOnMouseClicked(this::cursorClick);
        canvas.setOnMouseExited(this::cursorExit);
        canvas.setOnMouseReleased(this::cursorReleased);

        center.getChildren().add(marginCanvas);
        borderPane.setLeft(marginSideSection);
        borderPane.setTop(bottom);
        borderPane.setCenter(center);
        borderPane.setRight(marginRightSection);
        Scene scene = new Scene(borderPane);
        primaryStage.getIcons().add(new Image(new File("src/main/java/images/pencil_512px.png").toURI().toString()));//ICON from FlatIcon By Freepik
        primaryStage.setTitle("scrawl Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void sendMessage(KeyEvent keyEvent) {
        List<String> message = new ArrayList<>();
        if (keyEvent.getCode() == KeyCode.ENTER){
            chatSection.appendText(chatField.getText() + "\n");
            chatField.clear();
        }
    }

    public void processColorChange(ActionEvent event) {
       if (event.getSource().equals(btnColorGreen)){
            color = "Green";
       }else if (event.getSource().equals(btnColorBlack)){
            color = "Black";
       }else if (event.getSource().equals(btnColorOrange)){
            color = "Orange";
       }else if (event.getSource().equals(btnColorBlue)){
            color = "Blue";
       }else if (event.getSource().equals(btnColorRed)){
            color = "Red";
       }else if(event.getSource().equals(btnColorPurple)) {
            color = "Purple";
       }else if(event.getSource().equals(btnColorPink)) {
           color = "Pink";
       }else
           color = "Yellow";
    }

    private void clearCanvas(MouseEvent mouseEvent) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 690, 620);
    }

    private void cursorReleased(MouseEvent mouseEvent) {
        x1 = null;
        y1 = null;
    }

    private void cursorExit(MouseEvent mouseEvent) {
        updateCoord(0, 0);
    }

    private void cursorClick(MouseEvent mouseEvent) {
        if (rb1.isSelected()) {
            gc.setStroke(Color.valueOf(color));
            gc.strokeLine(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY());
        } else if (rb2.isSelected()) {
            gc.setFill(Color.WHITE);
            gc.fillOval(mouseEvent.getX(),mouseEvent.getY(),10, 10);
        }
    }

    private String showWordToGuess(){
        Scanner scan;
        List<String> wordsEveryoneCanSee = new ArrayList<>();
       //List<String> wordsOnlyThePlayerCanSee = new ArrayList<>();
        String  wrd = "" ,wordEveryoneCanSee= "";
        try {

            scan = new Scanner(new File("src/main/java/WordsToGuess.txt"));
            while (scan.hasNext()){
               wordsEveryoneCanSee.add(scan.nextLine());
              // wordsOnlyThePlayerCanSee.add(scan.nextLine());
            }
            for (int i = 0; i < wordsEveryoneCanSee.get(i).length(); i++) {
                    wrd = wordsEveryoneCanSee.get(i);
                    wordEveryoneCanSee = wrd.charAt(0) + StringUtils.repeat("_", (i-1)) + wrd.charAt(wrd.length()-1);
            }
            scan.close();
            return wordEveryoneCanSee.toUpperCase();

        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    private void cursorDragged(MouseEvent mouseEvent) {
        if (rb1.isSelected()) {
            if ((x1 == null && y1 == null)) {
                x1 = mouseEvent.getX();
                y1 = mouseEvent.getY();
            }
            gc.setStroke(Color.valueOf(color));
            gc.strokeLine(x1, y1, mouseEvent.getX(), mouseEvent.getY());
            x1 = mouseEvent.getX();
            y1 = mouseEvent.getY();
        } else if (rb2.isSelected()) {
            gc.setFill(Color.WHITE);
            gc.fillOval(mouseEvent.getX(),mouseEvent.getY(),10, 10 );
        }
        updateCoord(mouseEvent.getX(), mouseEvent.getY());
    }

    private void cursorMoved(MouseEvent mouseEvent) {
        updateCoord(mouseEvent.getX(), mouseEvent.getY());
    }

    public static void main (String[]args){
            launch(args);
    }

}