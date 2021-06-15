package com.misterycrew.Application;

import com.misterycrew.Application.GameMechanic.Points;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class ClientPaneStartFX extends Application implements Serializable {


    private static final long serialVersionUID = -5997589403994982301L;
    protected Label roundsLabel = new Label(), countDown = new Label();
    protected Label scoreLabel = new Label();
    protected final Canvas canvas = new Canvas(690, 620);
    protected GraphicsContext gc = canvas.getGraphicsContext2D();
    protected ToggleButton btnDraw = new ToggleButton();
    protected ToggleButton btnClear = new ToggleButton();
    protected String color = "black";
    private Double x1 = null, y1 = null;
    public Client client;
    protected Stage primaryStage;
    protected Button clearCanvas = new Button("CLEAR CANVAS");
    protected Button btnColorRed = new Button(), btnColorBlack = new Button(), btnColorGreen = new Button();
    protected Button btnColorPurple = new Button(), btnColorPink = new Button(), btnColorOrange = new Button();
    protected Button btnColorBlue = new Button(), btnColorYellow = new Button();
    protected TextArea chatSection = new TextArea(), users = new TextArea();
    protected final TextField chatField = new TextField();
    protected TextField userName = new TextField();
    protected int rnd;
    public int count = 0;
    private int lock = 0;
    protected List<String> wordToGuessList;
    protected int interval;
    public int a = 0;
    private final Button start = new Button("PLAY!");
    Alert alertToManyMembers = new Alert(Alert.AlertType.INFORMATION);
    private final Label wordToGuess = new Label();

    /**
     * This method is the start method of the Application class.
     * At this primaryStage you are asked to enter your username.
     *
     * @param primaryStage Stage
     */
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        ImageView scrawlLogoView = new ImageView(new Image("Image-Start.png", 450, 150, false, false));
        scrawlLogoView.getStyleClass().add("logo-View");

        Alert howToPlayAlert = new Alert(Alert.AlertType.INFORMATION);
        howToPlayAlert.setContentText("How to Play. \nWhen its your turn to draw, you will have to " +
                "draw a word shown to you in the top of the application in 80 seconds, alternatively when somebody " +
                "else is drawing you have to type your guess into the chat to gain points, be quick, the earlier " +
                "you guess a word the more points you get!");

        Button howToPlay = new Button("HOW TO PLAY");
        howToPlay.getStyleClass().add("button-howToPlay");
        howToPlay.setOnMouseClicked(e -> howToPlayAlert.show());

        userName.setPromptText("Enter your name");
        userName.getStyleClass().add("username-textField");
        userName.setOnKeyTyped(e -> start.setDisable(userName.getText().length() == 0));

        start.setDisable(true);
        start.setOnMousePressed(this::checkIfToManyMembers);
        start.getStyleClass().add("btn-start");

        VBox startBox = new VBox();
        startBox.getChildren().addAll(howToPlay, userName, start);
        startBox.setAlignment(Pos.CENTER);
        startBox.setSpacing(11);

        VBox startSection = new VBox();
        startSection.getChildren().addAll(scrawlLogoView, startBox);
        startSection.getStyleClass().add("startSection");

        Scene scene = new Scene(startSection, 1388, 695);
        scene.getStylesheets().add("Style.css");

        onCloseStageEvent();
        primaryStage.getIcons().add(new Image(new File("src/main/resources/favicon.png").toURI().toString()));
        primaryStage.setTitle("Scrawl Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * This method is used to notify the server that the user
     * might want to leave the game session.
     */
    void onCloseStageEvent() {
        primaryStage.setOnCloseRequest(e -> {
            if (client != null) {
                try {
                    client.serverInterface.leaveGame(userName.getText(), btnDraw.isVisible());
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * This is the second Stage in which it will occur when the user
     * presses the START button.
     *
     * @param mouseEvent MouseEvent
     */
    private void secondStage(MouseEvent mouseEvent) {

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
        rightPane.getChildren().addAll(chatSection, chatField);

        HBox marginRightPane = new HBox();
        marginRightPane.getChildren().addAll(rightPane);
        HBox.setMargin(rightPane, new Insets(50, 25, 25, 25));

        ImageView imagePoints = new ImageView(new Image("star.png", 25, 25, false, false));

        scoreLabel.setText("Your Points: 0");
        scoreLabel.getStyleClass().add("scoreLabel-style");

        HBox pointsSection = new HBox();
        pointsSection.getChildren().addAll(imagePoints, scoreLabel);
        pointsSection.setAlignment(Pos.CENTER);
        pointsSection.setSpacing(26);

        ImageView imageTime = new ImageView(new Image("deadline.png", 25, 25, false, false));

        HBox timeSection = new HBox();
        timeSection.getChildren().addAll(imageTime, countDown, roundsLabel);
        roundsLabel.getStyleClass().add("roundsLabel-style");
        countDown.getStyleClass().add("roundsLabel-style");
        timeSection.setAlignment(Pos.CENTER);
        timeSection.setSpacing(10);

        VBox marginCanvas = new VBox();
        marginCanvas.getChildren().addAll(wordToGuess, canvas);
        marginCanvas.getStyleClass().add("marginCanvas-style");

        VBox leftPane = new VBox();
        leftPane.getChildren().addAll(pointsSection, timeSection);
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
        utilitiesBtnSection.getChildren().addAll(btnDraw, btnClear);

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

        colorSection1.getChildren().addAll(btnColorRed, btnColorBlack, btnColorBlue, btnColorPink);

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

        colorSection2.getChildren().addAll(btnColorYellow, btnColorOrange, btnColorGreen, btnColorPurple);

        clearCanvas.setPadding(new Insets(9, 9, 9, 9));
        clearCanvas.getStyleClass().add("btn-ClearCanvas");
        clearCanvas.setOnMouseClicked(this::clearCanvas);
        clearCanvas.setVisible(false);

        leftPane.getChildren().addAll(users, utilitiesBtnSection, colorSection1, colorSection2, clearCanvas);
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

        enableDrawingForFirstUser();
        checkIfThisUserHasControl();
        updateRoundLabel();
        setCountDownLabel();

        Scene secondStage = new Scene(rootPane);
        secondStage.getStylesheets().add("Style.css");
        rootPane.setStyle("-fx-background-color: #ECEFF1;");

        primaryStage.setScene(secondStage);
        ScrollBar scrollBar = (ScrollBar) chatSection.lookup(".scroll-bar:vertical");
        scrollBar.setDisable(true);
    }

    /**
     * Getter used to retrieve the List of Strings used to show
     * the words to guess.
     *
     * @return (List < String >) holding the list with the words to be guessed.
     */
    public List<String> getWordToGuessList() {
        return wordToGuessList;
    }

    /**
     * Getter used to retrieve the Username.
     *
     * @return (ToggleButton) holding the username of the user.
     */
    public TextField getUserName() {
        return userName;
    }

    /**
     * Getter used to retrieve the TextField used to write the message.
     *
     * @return (TextField) holding the message to be send.
     */
    public TextField getChatField() {
        return chatField;
    }

    /**
     * Getter used to retrieve the Button used to draw.
     *
     * @return (ToggleButton).
     */
    public ToggleButton getBtnDraw() {
        return btnDraw;
    }

    /**
     * Getter used to retrieve the score Label.
     *
     * @return (Label) holding the score.
     */
    public Label getScoreLabel() {
        return scoreLabel;
    }

    /**
     * Getter used to retrieve the lock state.
     *
     * @return (Integer) holding the state of the lock.
     */
    public int getLock() {
        return lock;
    }

    /**
     * Setter used to set the lock.
     *
     * @param lock (Integer) used to pass the changed value of
     *             the lock variable.
     */
    public void setLock(int lock) {
        this.lock = lock;
    }

    /**
     * This method is used in order to verify that the users
     * currently in the game session are not grater than the rounds
     * (!members > 5).
     * <p>
     * Note: we use leaveGame method in this case just to make sure
     * that the server doesnt register the user when the start button is pressed.
     *
     * @param mouseEvent input when the start button is pressed
     */
    private void checkIfToManyMembers(MouseEvent mouseEvent) {
        try {
            getConnected();
            if (client.serverInterface.getMembers() >= client.serverInterface.getTotRounds()) {
                alertToManyMembers.setContentText("To many players, please wait until " +
                        "someone lives the game.");
                alertToManyMembers.show();
                client.serverInterface.leaveGame(userName.getText(), btnDraw.isVisible());
            } else {
                secondStage(mouseEvent);
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to set the countdown label to the updated
     * variable if the user is not the first one joining the server.
     * (This method is made because: The first user joining the server will be
     * the one starting the timer so the other users will have to wait until next
     * round to see the updated round and countdown)
     */
    private void setCountDownLabel() {
        try {
            if (client.serverInterface.returnCurrentUsers() != 1) {
                client.serverInterface.updateCountDownVariable();
                countDown.setText(String.valueOf(interval));
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to enable the control to draw to the first user
     * only, because afterwards from the server it will randomly pick a user
     * to draw.
     */
    private void enableDrawingForFirstUser() {
        try {
            if (client.serverInterface.returnCurrentUsers() == 1) {
                btnClear.setVisible(true);
                btnDraw.setVisible(true);
                btnColorRed.setVisible(true);
                btnColorBlack.setVisible(true);
                btnColorPurple.setVisible(true);
                btnColorGreen.setVisible(true);
                btnColorBlue.setVisible(true);
                btnColorOrange.setVisible(true);
                btnColorPink.setVisible(true);
                btnColorYellow.setVisible(true);
                clearCanvas.setVisible(true);
                canvas.setDisable(false);
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to:
     * 1) Initiate the timer if, is the first user.
     * 2) If is not the first user it will set the
     * current round.
     */
    private void updateRoundLabel() {
        try {
            if (client.serverInterface.returnCurrentUsers() == 1) {
                client.serverInterface.setTimerGame();
            }
            client.serverInterface.updateRound();
            roundsLabel.setText("Round " + rnd + " of 5");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to change the color of the pen,
     * used to draw.
     *
     * @param event mouse input event
     */
    public void processColorChange(ActionEvent event) {
        btnDraw.setSelected(true);
        if (event.getSource().equals(btnColorGreen)) {
            color = "Green";
        } else if (event.getSource().equals(btnColorBlack)) {
            color = "Black";
        } else if (event.getSource().equals(btnColorOrange)) {
            color = "Orange";
        } else if (event.getSource().equals(btnColorBlue)) {
            color = "Blue";
        } else if (event.getSource().equals(btnColorRed)) {
            color = "Red";
        } else if (event.getSource().equals(btnColorPurple)) {
            color = "Purple";
        } else if (event.getSource().equals(btnColorPink)) {
            color = "Pink";
        } else
            color = "Yellow";
    }

    /**
     * This method clears the canvas by applying the white color on the canvas.
     *
     * @param mouseEvent when the clear canvas button is pressed.
     */
    private void clearCanvas(MouseEvent mouseEvent) {
        try {
            gc.setFill(Color.WHITE);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            client.serverInterface.sendClearCanvas(0, 0, 690, 620, "white");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to reset to null the x and y coordinates
     * when the mouse event is released.
     *
     * @param mouseEvent on mouse released.
     */
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
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to set the word to guess based on the
     * type of user. First we check if the user has the control
     * in this case we set the complete word, in the other case
     * we give them the word with missing letters.
     * <p>
     * if userType == true --> complete word.
     * if userType == false --> word with missing letters.
     */
    public void checkIfThisUserHasControl() {
        try {
            if (btnDraw.isVisible()) {
                Platform.runLater(() -> wordToGuess.setText(showWordToGuess(true)));
            } else {
                client.serverInterface.updateIndexWord();
                Platform.runLater(() -> wordToGuess.setText(showWordToGuess(false)));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to read from a .txt file and show the
     * word based on the userType.
     *
     * @param userType boolean variable holding the type of user.
     * @return String containing the word.
     */
    public String showWordToGuess(boolean userType) {
        Path path = Path.of("src/main/resources/WordsToGuess.txt");
        String wordToGuessString = "";
        String wrd;

        try {
            wordToGuessList = Files.lines(path).collect(toList());
            for (int i = 0; i < wordToGuessList.get(count).length(); i++) {
                wrd = wordToGuessList.get(count);
                wordToGuessString = wrd.charAt(0) + StringUtils.repeat("_", (i - 1)) + wrd.charAt(wrd.length() - 1);
            }
            return (userType ? wordToGuessList.get(count).toUpperCase() : wordToGuessString.toUpperCase());
        } catch (Exception exception) {
            return null;
        }
    }

    private void cursorDragged(MouseEvent mouseEvent) {
        try {
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
                gc.fillOval(mouseEvent.getX(), mouseEvent.getY(), 10, 10);
                client.serverInterface.sendClear(mouseEvent.getX(), mouseEvent.getY(), 10, 10, "white");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to send the message to the server, by
     * getting the text of the username TextField which holds who is
     * sending the message and the chatField which holds the actual message.
     * Finally broadcast it to all the current users.
     *
     * @param keyEvent key pressed to send the message.
     */
    private void sendMessage(KeyEvent keyEvent) {
        Points points = new Points(this);

        if (keyEvent.getCode() == KeyCode.ENTER) {

            points.validateGuessGivePoints();

            try {
                if (lock == 1 && StringUtils.containsIgnoreCase(chatField.getText(),getWordToGuessList().get(count))){
                    chatSection.appendText("word already guessed\n");
                } else {
                    client.serverInterface.updateChat(userName.getText(), chatField.getText());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            chatField.clear();
        }
    }

    /**
     * This method is used to connect to the client,
     * by passing th object of this class and the username of
     * the user that wants to play.
     * Note: using the regular expression "\W+" we avoid having
     * malformed usernames (ex. spaces,non word characters).
     */
    private void getConnected() {
        try {
            client = new Client(this, userName.getText().replaceAll("\\W+", "_"));
            client.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
