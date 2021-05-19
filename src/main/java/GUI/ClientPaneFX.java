package GUI;

import Shared.Users;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientPaneFX extends Application {

    private static final long serialVersionUID = 1L;
    private Label wordToGuess;
    private final Canvas canvas = new Canvas(690, 620);
    protected GraphicsContext gc = canvas.getGraphicsContext2D();
    private ToggleButton btnDraw,btnClear;
    protected String color = "black";
    private String name;
    private Double x1 = null, y1 = null;
    private Client chatClient;
    private Stage primaryStage;
    private Button btnColorRed,btnColorBlack,btnColorGreen,btnColorPurple,btnColorPink,btnColorOrange,btnColorBlue;
    protected TextArea chatSection, users;
    private final TextField chatField = new TextField();
    private final Label labelSystemInfo = new Label("JavaFX " + System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");
    private TextField userName;


    @Override
    public void start(Stage primaryStage) throws FileNotFoundException{

        this.primaryStage = primaryStage;

        labelSystemInfo.setStyle("-fx-font-family: 'JetBrains Mono NL'; -fx-font-size: 14; -fx-text-fill: #242c37;");

        ImageView scrawlLogoView = new ImageView(new Image(new FileInputStream("src/main/resources/img.png")));
        scrawlLogoView.setStyle("-fx-fit-to-width: 300;-fx-fit-to-height: 355");
        scrawlLogoView.setPreserveRatio(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "",ButtonType.OK);
        Hyperlink howToPlay = new Hyperlink("How to Play");
        howToPlay.setStyle("-fx-font-size: 18;");
        howToPlay.setOnMouseClicked(e ->  alert.show());

        userName = new TextField();
        userName.setStyle("-fx-max-width: 330;-fx-pref-height: 35;-fx-border-radius: 6 6 6 6; -fx-background-radius: 6 6 6 6;");
        userName.setPromptText("Enter your name");

        Button start = new Button("PLAY!");
        start.setStyle("-fx-background-color: #5cb85c; -fx-font-weight: bold; -fx-pref-width: 330; -fx-text-fill: #fff; -fx-pref-height: 40; -fx-font-size: 16;-fx-font-family: 'JetBrains Mono NL';" +
                        "-fx-border-radius: 6 6 6 6; -fx-background-radius: 6 6 6 6;");
        start.setOnMouseEntered(e -> start.setStyle("-fx-background-color: #449D44; -fx-font-weight: bold; -fx-text-fill: #fff;-fx-pref-width: 330; " +
                        "-fx-pref-height: 40; -fx-font-size: 16;-fx-font-family: 'JetBrains Mono NL';-fx-border-radius: 6 6 6 6; -fx-background-radius: 6 6 6 6;"));
        start.setOnMouseExited(e -> start.setStyle("-fx-background-color: #5cb85c;-fx-font-weight: bold; -fx-text-fill: #fff;-fx-pref-width: 330;" +
                         " -fx-pref-height: 40; -fx-font-size: 16;-fx-font-family: 'JetBrains Mono NL';-fx-border-radius: 6 6 6 6; -fx-background-radius: 6 6 6 6;"));
        start.setOnMouseClicked(this::secondStage);

        VBox startSection = new VBox();
        startSection.getChildren().addAll(scrawlLogoView,howToPlay,userName,start,labelSystemInfo);
        startSection.setStyle("-fx-spacing: 10; -fx-pref-height: 700;-fx-pref-width: 1350; -fx-alignment: center");
        Scene scene = new Scene(startSection);

        onCloseStageEvent();
        primaryStage.getIcons().add(new Image(new File("src/main/resources/favicon.png").toURI().toString()));//ICON from FlatIcon By Freepik
        primaryStage.setTitle("Scrawl Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void onCloseStageEvent(){
        primaryStage.setOnCloseRequest(e -> {
            if(chatClient != null){
                try {
                    chatClient.serverInterface.leaveChat(name);
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
            System.exit(0);
        });
    }

    private void secondStage(MouseEvent mouseEvent) {

        try {
            name = userName.getText();
            getConnected(name);
        } catch (RemoteException e) {
            e.getMessage();
        }

        labelSystemInfo.setStyle("-fx-font-family: 'JetBrains Mono NL'; -fx-font-size: 12; -fx-text-fill: #242c37;");

        wordToGuess = new Label();
        wordToGuess.setText(showWordToGuess());
        wordToGuess.setStyle("-fx-pref-height: 25;-fx-text-fill: #242c37;-fx-font-weight: bold; -fx-font-size: 18; -fx-font-family: 'JetBrains Mono Medium';");

        users = new TextArea();
        users.setText("Leaderboard\n");
        users.setStyle("-fx-alignment: center;-fx-max-width: 250; -fx-pref-height: 290;-fx-background-color: #242c37;-fx-control-inner-background: #242c37;-fx-font-weight: bold; " +
                        "-fx-text-fill: #fff;-fx-font-size: 14; -fx-font-family: 'JetBrains Mono Medium';" +
                        "-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;" +
                        "-fx-focus-color: transparent;-fx-padding: 10");
        users.setEditable(false);

        chatSection = new TextArea();
        chatSection.setStyle("-fx-pref-height: 540;-fx-pref-width: 280;-fx-background-color: #151a21; -fx-control-inner-background: #151a21;-fx-text-box-border: transparent; -fx-text-fill: #fff;" +
                "-fx-faint-focus-color: transparent;-fx-focus-color: transparent;" +
                "-fx-font-size: 14; -fx-font-family: 'JetBrains Mono Medium';-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8; -fx-padding: 10;");
        chatSection.setEditable(false);


        chatField.setStyle("-fx-pref-height: 40;-fx-background-color: #242c37; -fx-text-fill: #ffff;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        chatField.setMaxWidth(240);
        chatField.setPromptText("Type your guess here...");
        chatField.setOnKeyPressed(this::sendMessage);

        VBox rightPane = new VBox();
        rightPane.setStyle("-fx-pref-height: 620;-fx-alignment: center;-fx-background-color: #171A1A;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        rightPane.getChildren().addAll(chatSection,chatField);
        HBox marginRightPane = new HBox();
        marginRightPane.getChildren().addAll(rightPane);
        HBox.setMargin(rightPane, new Insets(40, 20, 30, 30));

        VBox marginCanvas = new VBox();
        marginCanvas.getChildren().addAll(wordToGuess,canvas,labelSystemInfo);
        marginCanvas.setStyle("-fx-alignment: center;-fx-spacing: 7;");

        VBox leftPane = new VBox();
        leftPane.setStyle("-fx-background-color: #151a21;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        leftPane.setAlignment(Pos.CENTER);

        HBox marginLeftPane = new HBox();
        marginLeftPane.getChildren().addAll(leftPane);
        HBox.setMargin(leftPane, new Insets(40, 30, 30, 20));

        final ToggleGroup options = new ToggleGroup();

        ImageView imageview = new ImageView(new Image(new File("src/main/resources/eraser.png").toURI().toString()));
        imageview.setFitHeight(30);
        imageview.setFitWidth(30);
        btnClear = new ToggleButton();
        btnClear.setGraphic(imageview);
        btnClear.setStyle("-fx-background-color: #242c37;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        btnClear.setOnMouseEntered(e -> btnClear.setStyle("-fx-background-color: #3c4458;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        btnClear.setOnMouseExited(e -> btnClear.setStyle("-fx-background-color: #242c37;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        btnClear.setToggleGroup(options);
        btnClear.setTooltip(new Tooltip("ERASER \nErase part of the picture\nand replace it with the background color."));

        ImageView imageviewDraw = new ImageView(new Image(new File("src/main/resources/pencil_64px.png").toURI().toString()));
        imageviewDraw.setFitHeight(30);
        imageviewDraw.setFitWidth(30);
        btnDraw = new ToggleButton();
        btnDraw.setGraphic(imageviewDraw);
        btnDraw.setStyle("-fx-background-color: #242c37;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        btnDraw.setOnMouseEntered(e -> btnDraw.setStyle("-fx-background-color: #3c4458;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        btnDraw.setOnMouseExited(e -> btnDraw.setStyle("-fx-background-color: #242c37;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        btnDraw.setToggleGroup(options);
        btnDraw.setTooltip(new Tooltip("PENCIL\nDraw a free form line with the\nselected color."));

        HBox utilitiesBtnSection = new HBox();
        utilitiesBtnSection.setStyle("-fx-alignment: center;-fx-spacing: 20");
        utilitiesBtnSection.getChildren().addAll(btnDraw,btnClear);

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

        colorSection1.setStyle("-fx-spacing: 10;-fx-alignment: center");
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

        colorSection2.setStyle("-fx-spacing: 10;-fx-alignment: center");
        colorSection2.getChildren().addAll(btnColorYellow,btnColorOrange,btnColorGreen,btnColorPurple);

        Button clearCanvas = new Button("CLEAR CANVAS");
        clearCanvas.setPadding(new Insets(8, 8, 8, 8));
        clearCanvas.setStyle("-fx-background-color: #242c37;-fx-font-weight: bold; -fx-text-fill: #fff;-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;");
        clearCanvas.setOnMouseEntered(e -> clearCanvas.setStyle("-fx-background-color: #3c4458;-fx-font-weight: bold; -fx-text-fill: #fff; -fx-font-family: 'JetBrains Mono NL';-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        clearCanvas.setOnMouseExited(e -> clearCanvas.setStyle("-fx-background-color: #242c37;-fx-font-weight: bold; -fx-text-fill: #fff; -fx-font-family: 'JetBrains Mono NL';-fx-border-radius: 8 8 8 8; -fx-background-radius: 8 8 8 8;"));
        clearCanvas.setOnMouseClicked(this::clearCanvas);

        Label curColor = new Label("COLOR: BLACK");
        curColor.setStyle("-fx-text-fill: #ffff;");

        Label coord = new Label("(x, y)");
        coord.setStyle("-fx-text-fill: #ffff; -fx-min-width: 87");

        leftPane.getChildren().addAll(users,utilitiesBtnSection,colorSection1,colorSection2,clearCanvas);
        leftPane.setPrefWidth(280);
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

        BorderPane rootPane = new BorderPane();
        rootPane.setLeft(marginLeftPane);
        rootPane.setCenter(marginCanvas);
        rootPane.setRight(marginRightPane);

        Scene secondStage = new Scene(rootPane);
        primaryStage.setScene(secondStage);
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
           gc.fillRect(0, 0, 695, 625);
           chatClient.serverInterface.sendClearCanvas(0,0,695,625,"white");
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
                chatClient.serverInterface.sendDrawing(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY(), color);

            } else if (btnClear.isSelected()) {
                gc.setFill(Color.WHITE);
                gc.fillOval(mouseEvent.getX(), mouseEvent.getY(), 10, 10);
                chatClient.serverInterface.sendClear(mouseEvent.getX(), mouseEvent.getY(), 10, 10, "white");
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private String showWordToGuess(){
        Scanner scan;
        List<String> wordsEveryoneCanSee = new ArrayList<>();
        //List<String> wordsOnlyThePlayerCanSee = new ArrayList<>();
        String  wrd ,wordEveryoneCanSee= "";
        //Path path = Path.of("src/main/resources/WordsToGuess.txt");
        try {

            scan = new Scanner(new File("src/main/resources/WordsToGuess.txt"));
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
            return null;
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
            chatClient.serverInterface.sendDrawing(x1, y1, mouseEvent.getX(), mouseEvent.getY(), color);

            } else if (btnClear.isSelected()) {
            gc.setFill(Color.WHITE);
            gc.fillOval(mouseEvent.getX(),mouseEvent.getY(),10, 10 );
            chatClient.serverInterface.sendClear(mouseEvent.getX(), mouseEvent.getY(), 10, 10, "white");
            }
       }catch (RemoteException e){
           e.printStackTrace();
       }
    }

    private void sendMessage(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ENTER){
            try {
                chatClient.serverInterface.updateChat(name, chatField.getText());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            chatField.clear();
        }
    }

    private void getConnected(String userName) throws RemoteException{
        /*
         *remove whitespace and non word characters to avoid malformed url
        */
        String cleanedUserName = userName.replaceAll("\\W+","_");
        try {
            chatClient = new Client(this, cleanedUserName);
            chatClient.startClient();
        } catch (RemoteException e) {
            e.getMessage();
        }
    }


    public void setClientPanel(String[] currClients) {
        for (String user : currClients){
            users.appendText(user);
        }
    }

    public static void main (String[]args){
        launch(args);
    }
}
