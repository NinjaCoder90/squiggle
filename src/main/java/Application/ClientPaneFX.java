package Application;

import javafx.application.Application;
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
import java.rmi.RemoteException;
import java.util.*;

public class ClientPaneFX extends Application {

    private static final long serialVersionUID = 1L;
    private Label wordToGuess;
    private final Canvas canvas = new Canvas(690, 620);
    protected GraphicsContext gc = canvas.getGraphicsContext2D();
    private ToggleButton btnDraw,btnClear;
    protected String color = "black";
    protected String name;
    private Double x1 = null, y1 = null;
    private Client client;
    private Stage primaryStage;
    private Button btnColorRed,btnColorBlack,btnColorGreen,btnColorPurple,btnColorPink,btnColorOrange,btnColorBlue;
    protected TextArea chatSection = new TextArea();
    protected TextArea users = new TextArea();
    private final TextField chatField = new TextField();
    private final Label labelSystemInfo = new Label("JavaFX " + System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");
    private TextField userName;


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

        userName = new TextField();
        userName.setPromptText("Enter your name");
        userName.getStyleClass().add("username-textField");

        Button start = new Button("PLAY!");
        start.getStyleClass().add("btn-start");
        start.setOnMouseClicked(this::secondStage);

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
                    client.serverInterface.leaveChat(name);

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
            e.printStackTrace();
        }

        wordToGuess = new Label();
        wordToGuess.setText(showWordToGuess());
        wordToGuess.getStyleClass().add("wordToGuess-label");

        users.getStyleClass().add("users-textarea");
        users.setEditable(false);

        chatSection.getStyleClass().add("chatSection-textarea");
        chatSection.setEditable(false);
        chatSection.setWrapText(true);

        chatField.getStyleClass().add("chatField-textField");
        chatField.setMaxWidth(240);
        chatField.setPromptText("Type your guess here...");
        chatField.setOnKeyPressed(this::sendMessage);

        VBox rightPane = new VBox();
        rightPane.getStyleClass().add("rightPane-style");
        rightPane.getChildren().addAll(chatSection,chatField);

        HBox marginRightPane = new HBox();
        marginRightPane.getChildren().addAll(rightPane);
        HBox.setMargin(rightPane, new Insets(40, 20, 30, 30));

        VBox marginCanvas = new VBox();
        marginCanvas.getChildren().addAll(wordToGuess,canvas,labelSystemInfo);
        marginCanvas.getStyleClass().add("marginCanvas-style");

        VBox leftPane = new VBox();
        leftPane.getStyleClass().add("leftPane-style");
        leftPane.setAlignment(Pos.CENTER);

        HBox marginLeftPane = new HBox();
        marginLeftPane.getChildren().addAll(leftPane);
        HBox.setMargin(leftPane, new Insets(40, 30, 30, 20));

        final ToggleGroup options = new ToggleGroup();

        ImageView viewEraseImage = new ImageView(new Image("eraser.png"));
        viewEraseImage.setFitHeight(25);
        viewEraseImage.setPreserveRatio(true);
        btnClear = new ToggleButton();
        btnClear.setPrefSize(25, 25);
        btnClear.setGraphic(viewEraseImage);
        btnClear.getStyleClass().add("btnClear");
        btnClear.setToggleGroup(options);
        btnClear.setTooltip(new Tooltip("ERASER \nErase part of the picture\nand replace it with the background color."));

        ImageView viewDrawImage = new ImageView(new Image("favicon.png"));
        viewDrawImage.setFitHeight(25);
        viewDrawImage.setPreserveRatio(true);
        btnDraw = new ToggleButton();
        btnDraw.setPrefSize(25, 25);
        btnDraw.setGraphic(viewDrawImage);
        btnDraw.getStyleClass().add("btnClear");
        btnDraw.setToggleGroup(options);
        btnDraw.setTooltip(new Tooltip("PENCIL\nDraw a free form line with the\nselected color."));

        HBox utilitiesBtnSection = new HBox();
        utilitiesBtnSection.getStyleClass().add("utility-btnSection");
        utilitiesBtnSection.getChildren().addAll(btnDraw,btnClear);

        HBox colorSection1 = new HBox();
        colorSection1.getStyleClass().add("colorSection1-style");

        btnColorRed = new Button();
        btnColorRed.getStyleClass().add("btn-Red");
        btnColorRed.setOnAction(this::processColorChange);

        btnColorBlack = new Button();
        btnColorBlack.getStyleClass().add("btn-Black");
        btnColorBlack.setOnAction(this::processColorChange);

        btnColorBlue = new Button();
        btnColorBlue.getStyleClass().add("btn-Blue");
        btnColorBlue.setOnAction(this::processColorChange);

        btnColorPink = new Button();
        btnColorPink.getStyleClass().add("btn-Pink");
        btnColorPink.setOnAction(this::processColorChange);

        colorSection1.getChildren().addAll(btnColorRed, btnColorBlack, btnColorBlue,btnColorPink);

        HBox colorSection2 = new HBox();
        colorSection2.getStyleClass().add("colorSection1-style");

        Button btnColorYellow = new Button();
        btnColorYellow.getStyleClass().add("btn-Yellow");
        btnColorYellow.setOnAction(this::processColorChange);

        btnColorOrange = new Button();
        btnColorOrange.setStyle("");
        btnColorOrange.getStyleClass().add("btn-Orange");
        btnColorOrange.setOnAction(this::processColorChange);

        btnColorGreen = new Button();
        btnColorGreen.getStyleClass().add("btn-Green");
        btnColorGreen.setOnAction(this::processColorChange);

        btnColorPurple = new Button();
        btnColorPurple.getStyleClass().add("btn-Purple");
        btnColorPurple.setOnAction(this::processColorChange);

        colorSection2.getChildren().addAll(btnColorYellow,btnColorOrange,btnColorGreen,btnColorPurple);

        Button clearCanvas = new Button("CLEAR CANVAS");
        clearCanvas.setPadding(new Insets(8, 8, 8, 8));
        clearCanvas.getStyleClass().add("btn-ClearCanvas");
        clearCanvas.setOnMouseClicked(this::clearCanvas);

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
        secondStage.getStylesheets().add("Style.css");

        primaryStage.setScene(secondStage);

        ScrollBar scrollBar = (ScrollBar) chatSection.lookup(".scroll-bar:vertical");
        scrollBar.setDisable(true);

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
           client.serverInterface.sendClearCanvas(0,0,695,625,"white");
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
            try {
                client.serverInterface.updateChat(name, chatField.getText());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            chatField.clear();
        }
    }

    private void getConnected(String userName) throws RemoteException{
        try {
            //remove whitespace and non word characters to avoid malformed url
            client = new Client(this, userName.replaceAll("\\W+","_"));
            client.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[]args){
        launch(args);
    }
}
