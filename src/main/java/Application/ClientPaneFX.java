package Application;

import Server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;

import Shared.Users;

import static java.util.stream.Collectors.toList;

public class ClientPaneFX extends Application {

    private static final long serialVersionUID = 1L;
    private Label wordToGuess;
    public Label roundsLabel = new Label(),scoreLabel = new Label();
    public final Canvas canvas = new Canvas(690, 620);
    protected GraphicsContext gc = canvas.getGraphicsContext2D();
    public ToggleButton btnDraw = new ToggleButton();
    public ToggleButton btnClear = new ToggleButton();
    protected String color = "black";
    private Double x1 = null, y1 = null;
    private Client client;
    private Stage primaryStage;
    public Button clearCanvas = new Button("CLEAR CANVAS");
    public Button btnColorRed = new Button();
    public Button btnColorBlack = new Button();
    public Button btnColorGreen = new Button();
    public Button btnColorPurple = new Button();
    public Button btnColorPink = new Button();
    public Button btnColorOrange  = new Button();
    public Button btnColorBlue = new Button();
    public Button btnColorYellow = new Button();
    protected TextArea chatSection = new TextArea();
    protected TextArea users = new TextArea();
    private final TextField chatField = new TextField();
    private final Label labelSystemInfo = new Label("JavaFX " + System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");
    private TextField userName;
    public int rnd, i = 0, count = 0;
    private List<String> wordToGuessList;


    @Override
    public void start(Stage primaryStage) throws FileNotFoundException{

        this.primaryStage = primaryStage;

        labelSystemInfo.getStyleClass().add("system-info");

        ImageView scrawlLogoView = new ImageView(new Image(new FileInputStream("src/main/resources/img.png")));
        scrawlLogoView.getStyleClass().add("logo-View");
        scrawlLogoView.setPreserveRatio(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "",ButtonType.OK);
        Hyperlink howToPlay = new Hyperlink("How to Play");
        howToPlay.getStyleClass().add("link-howToPlay");
        howToPlay.setOnMouseClicked(e ->  alert.show());

        Button start = new Button("PLAY!");

        Label showErrorUsername = new Label("username already exist");
        showErrorUsername.setVisible(false);

        userName = new TextField();
        userName.setPromptText("Enter your name");
        userName.getStyleClass().add("username-textField");
        userName.setOnKeyTyped(e -> start.setDisable(userName.getText().length() <= 0));

        start.getStyleClass().add("btn-start");
        start.setDisable(true);
        start.setOnMouseClicked(this::secondStage);
        /*start.setOnMouseClicked(e-> {try {
            client = new Client(this, userName.getText().replaceAll("\\W+","_"));
            client.startClient();
            if (client.serverInterface.checkIfUsernameExist(userName.getText())) {
                start.setDisable(true);
            }else {
                System.out.println("hey");
                //getConnected();
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }});*/

        VBox startSection = new VBox();
        startSection.getChildren().addAll(scrawlLogoView,howToPlay,userName,start,labelSystemInfo);
        startSection.getStyleClass().add("startSection");

        Scene scene = new Scene(startSection);
        scene.getStylesheets().add("Style.css");

        onCloseStageEvent();
        primaryStage.getIcons().add(new Image(new File("src/main/resources/favicon.png").toURI().toString()));//ICON from FlatIcon By Freepik
        primaryStage.setTitle("Scrawl Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void onCloseStageEvent(){
        primaryStage.setOnCloseRequest(e -> {
            if(client != null){
                try {
                    client.serverInterface.leaveGame(userName.getText());
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
            Platform.exit();
            System.exit(0);
        });
    }

    private void secondStage(MouseEvent mouseEvent) {

        wordToGuess = new Label();
        //wordToGuess.setText(showWordToGuess());
        wordToGuess.getStyleClass().add("wordToGuess-label");

        users.getStyleClass().add("users-textarea");
        users.setEditable(false);

        chatSection.getStyleClass().add("chatSection-textarea");
        chatSection.setEditable(false);
        chatSection.setWrapText(true);

        chatField.getStyleClass().add("chatField-textField");
        chatField.setMaxWidth(260);
        chatField.setPromptText("Type your guess here...");
        chatField.setOnKeyPressed(this::sendMessage);

        VBox rightPane = new VBox();
        rightPane.getStyleClass().add("rightPane-style");
        rightPane.getChildren().addAll(chatSection,chatField);

        HBox marginRightPane = new HBox();
        marginRightPane.getChildren().addAll(rightPane);
        HBox.setMargin(rightPane, new Insets(50, 25, 25, 25));

        scoreLabel.setText("Points: 0");
        scoreLabel.getStyleClass().add("scoreLabel-style");

        VBox marginCanvas = new VBox();
        marginCanvas.getChildren().addAll(wordToGuess,canvas);
        marginCanvas.getStyleClass().add("marginCanvas-style");

        VBox leftPane = new VBox();
        leftPane.getChildren().addAll(scoreLabel,roundsLabel);
        leftPane.getStyleClass().add("leftPane-style");
        leftPane.setAlignment(Pos.CENTER);

        HBox marginLeftPane = new HBox();
        marginLeftPane.getChildren().addAll(leftPane);
        HBox.setMargin(leftPane, new Insets(50, 25, 25, 25));

        final ToggleGroup options = new ToggleGroup();

        ImageView viewEraseImage = new ImageView(new Image("eraser.png"));
        viewEraseImage.setFitHeight(30);
        viewEraseImage.setPreserveRatio(true);

        btnClear.setGraphic(viewEraseImage);
        btnClear.getStyleClass().add("btnClear");
        btnClear.setToggleGroup(options);
        btnClear.setTooltip(new Tooltip("ERASER \nErase part of the picture\nand replace it with the background color."));
        btnClear.setVisible(false);

        ImageView viewDrawImage = new ImageView(new Image("pencil_64px.png"));
        viewDrawImage.setFitHeight(30);
        viewDrawImage.setPreserveRatio(true);

        btnDraw.setGraphic(viewDrawImage);
        btnDraw.getStyleClass().add("btnClear");
        btnDraw.setToggleGroup(options);
        btnDraw.setTooltip(new Tooltip("PENCIL\nDraw a free form line with the\nselected color."));
        btnDraw.setVisible(false);

        HBox utilitiesBtnSection = new HBox();
        utilitiesBtnSection.getStyleClass().add("utility-btnSection");
        utilitiesBtnSection.getChildren().addAll(btnDraw,btnClear);

        HBox colorSection1 = new HBox();
        colorSection1.getStyleClass().add("colorSection1-style");

        btnColorRed.getStyleClass().add("btn-Red");
        btnColorRed.setOnAction(this::processColorChange);
        btnColorRed.setVisible(false);

        btnColorBlack.getStyleClass().add("btn-Black");
        btnColorBlack.setOnAction(this::processColorChange);
        btnColorBlack.setVisible(false);

        btnColorBlue.getStyleClass().add("btn-Blue");
        btnColorBlue.setOnAction(this::processColorChange);
        btnColorBlue.setVisible(false);

        btnColorPink.getStyleClass().add("btn-Pink");
        btnColorPink.setOnAction(this::processColorChange);
        btnColorPink.setVisible(false);

        colorSection1.getChildren().addAll(btnColorRed, btnColorBlack, btnColorBlue,btnColorPink);

        HBox colorSection2 = new HBox();
        colorSection2.getStyleClass().add("colorSection1-style");

        btnColorYellow.getStyleClass().add("btn-Yellow");
        btnColorYellow.setOnAction(this::processColorChange);
        btnColorYellow.setVisible(false);

        btnColorOrange = new Button();
        btnColorOrange.setStyle("");
        btnColorOrange.getStyleClass().add("btn-Orange");
        btnColorOrange.setOnAction(this::processColorChange);
        btnColorOrange.setVisible(false);

        btnColorGreen = new Button();
        btnColorGreen.getStyleClass().add("btn-Green");
        btnColorGreen.setOnAction(this::processColorChange);
        btnColorGreen.setVisible(false);

        btnColorPurple = new Button();
        btnColorPurple.getStyleClass().add("btn-Purple");
        btnColorPurple.setOnAction(this::processColorChange);
        btnColorPurple.setVisible(false);

        colorSection2.getChildren().addAll(btnColorYellow,btnColorOrange,btnColorGreen,btnColorPurple);

        clearCanvas.setPadding(new Insets(9, 9, 9, 9));
        clearCanvas.getStyleClass().add("btn-ClearCanvas");
        clearCanvas.setOnMouseClicked(this::clearCanvas);
        clearCanvas.setVisible(false);

        leftPane.getChildren().addAll(users,utilitiesBtnSection,colorSection1,colorSection2,clearCanvas);
        leftPane.setSpacing(25.0);

        BoxBlur blur = new BoxBlur();
        blur.setWidth(3);
        blur.setHeight(3);
        blur.setIterations(6);

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 690, 620);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(8);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.setEffect(blur);

        canvas.setOnMouseDragged(this::cursorDragged);
        canvas.setOnMouseClicked(this::cursorClick);
        canvas.setOnMouseReleased(this::cursorReleased);
        canvas.setDisable(true);

        BorderPane rootPane = new BorderPane();
        rootPane.setLeft(marginLeftPane);
        rootPane.setCenter(marginCanvas);
        rootPane.setRight(marginRightPane);

        getConnected();
        checkIfThisUserHasControl();
        updateRoundLabel();

        roundsLabel.getStyleClass().add("roundsLabel-style");

        Scene secondStage = new Scene(rootPane);
        secondStage.getStylesheets().add("Style.css");
        rootPane.setStyle("-fx-background-color: #ECEFF1;");
        //#263238 --> black theme
        //#ECEFF1 --> white theme

        primaryStage.setScene(secondStage);
        ScrollBar scrollBar = (ScrollBar) chatSection.lookup(".scroll-bar:vertical");
        scrollBar.setDisable(true);
    }
/*
    private void validateGuess() {

    }

    private void chooseWinner() {

    }

    private int validateGuessGivePoints() {
        validateGuess();
        return 0;
    }

    private void pickPlayerToDraw() {

    }
*/

    public void updateRoundLabel(){
        try {
            if (client.serverInterface.returnCurrentUsers() == 1) {
                client.serverInterface.setTimerGame();
            }
            client.serverInterface.updateRound();
            roundsLabel.setText("Round " + rnd + " of 3");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void processColorChange(ActionEvent event) {
        btnDraw.setSelected(true);
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
       try {
           gc.setFill(Color.WHITE);
           gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
           client.serverInterface.sendClearCanvas(0,0,690,620,"white");
       }catch(RemoteException e){
           e.printStackTrace();
       }
    }

    private void cursorReleased(MouseEvent mouseEvent) {
        x1 = null;
        y1 = null;
    }

    private void cursorClick(MouseEvent mouseEvent) {
        try {
            if (btnDraw.isSelected()) {
                gc.setStroke(Color.valueOf(color));
                gc.strokeLine(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY());
                client.serverInterface.sendDrawing(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY(), color);

            } else if (btnClear.isSelected()) {
                gc.setFill(Color.WHITE);
                gc.fillOval(mouseEvent.getX(), mouseEvent.getY(), 10, 10);
                client.serverInterface.sendClear(mouseEvent.getX(), mouseEvent.getY(), 10, 10, "white");
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    //Then we know he has the control and we give him the full word.
    // in the other case (for users that dont have the control)
    // we give them the word with missing letters.
    //if userType == true --> get wordsOnlyPlayerCanSee
    //if userType == false --> get wordsEveryoneCanSee
    public void checkIfThisUserHasControl(){
        try {
            if (btnDraw.isVisible()) {
                Platform.runLater(() -> wordToGuess.setText(showWordToGuess(true)));
            }else {
                client.serverInterface.updateIndexWord();
                Platform.runLater(() -> wordToGuess.setText(showWordToGuess(false)));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        /*
        if (btnDraw.isVisible()) {
            Platform.runLater(() -> wordToGuess.setText(showWordToGuess(true)));
        }else {
            Platform.runLater(() -> wordToGuess.setText(showWordToGuess(false)));
        }*/
    }

    private String showWordToGuess(boolean userType){
        Path path = Path.of("src/main/resources/WordsToGuess.txt");
        String wordToGuessString = "";
        String wrd = "";

        try {
            wordToGuessList = Files.lines(path).collect(toList());
            //count++;
            for (int i = 0; i < wordToGuessList.get(count).length(); i++) {
                wrd = wordToGuessList.get(count);
                wordToGuessString = wrd.charAt(0) + StringUtils.repeat("_", (i-1)) + wrd.charAt(wrd.length()-1);
            }

            return (userType ? wordToGuessList.get(count)  : wordToGuessString);

        }catch (Exception exception){
            return null;
        }
    }

    private void validateGuessGivePoints() {
        if (i == 0){
            if (chatField.getText().compareToIgnoreCase(Objects.requireNonNull(wordToGuessList.get(count))) == 0){
                Users.setScore(Users.getScore() + 10);
                scoreLabel.setText("Points: " + Users.getScore());
                i = 1;
            }
        }
    }

    private void cursorDragged(MouseEvent mouseEvent){
       try{
           if (btnDraw.isSelected()) {
            if ((x1 == null && y1 == null)) {
                x1 = mouseEvent.getX();
                y1 = mouseEvent.getY();
            }
            gc.setStroke(Color.valueOf(color));
            gc.strokeLine(x1, y1, mouseEvent.getX(), mouseEvent.getY());
            x1 = mouseEvent.getX();
            y1 = mouseEvent.getY();
               client.serverInterface.sendDrawing(x1, y1, mouseEvent.getX(), mouseEvent.getY(), color);

            } else if (btnClear.isSelected()) {
            gc.setFill(Color.WHITE);
            gc.fillOval(mouseEvent.getX(),mouseEvent.getY(),10, 10 );
               client.serverInterface.sendClear(mouseEvent.getX(), mouseEvent.getY(), 10, 10, "white");
            }
       }catch (RemoteException e){
           e.printStackTrace();
       }
    }

    private void sendMessage(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ENTER){
            validateGuessGivePoints();
            try {
                client.serverInterface.updateChat(userName.getText(), chatField.getText());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            chatField.clear();
        }
    }

    private void getConnected(){
        try {
            client = new Client(this, userName.getText().replaceAll("\\W+","_"));
            client.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[]args){
        launch(args);
    }
}
