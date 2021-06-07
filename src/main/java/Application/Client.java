package Application;

import Application.Shared.ClientInterface;
import Application.Shared.ServerInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class Client<a> extends UnicastRemoteObject implements ClientInterface {

    private static final long serialVersionUID = 7468891722773409712L;
    ClientPaneFX chatGUI;
    private final String clientServiceName;
    public final String name;
    public ServerInterface serverInterface;
    protected boolean connectionProblem = false;

    public Client(ClientPaneFX chatGUI,String userName) throws RemoteException{
        super();
        this.chatGUI = chatGUI;
        this.name = userName;
        this.clientServiceName = "ClientService_" + userName;
    }

    public void startClient() throws RemoteException {
        String hostName = "localhost";
        String[] details = new String[]{name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            String serviceName = "distributedService";
            serverInterface = (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        }
        catch (ConnectException | NotBoundException | MalformedURLException e) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR, "The server seems to be unavailable\nPlease try later");
            Optional<ButtonType> another = alert.showAndWait();
            if (another.isPresent() && another.get() == ButtonType.OK) {
                System.exit(0);
            }
            connectionProblem = true;
        }
        if(!connectionProblem){
            registerWithServer(details);
            System.out.println("Client Listen RMI Server is running...\n");
        }
    }

    /**
     * This method is used to register the user joining the server,
     * by passing to the server this.ref which is the class object
     * as a remote object reference.
     * @param details (String) array containing the username,hostname
     *                (which is "localhost") and the clientServiceName
     *                (which is just a string containing the "ClientService_" + username)
     */
    private void registerWithServer(String[] details) {
        try{
            serverInterface.passIDentity(this.ref);
            serverInterface.registerUsers(details);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method is used to append to the textArea the message send by the user,
     * showing it to all the current users.
     * @param username (String) variable holding the username of the user sending the message.
     * @param message (String) variable holding the actual message to send.
     */
    @Override
    public void messageFromServer(String username,String message) {
            chatGUI.chatSection.appendText(username + message + "\n");
    }

    /**
     * This method is used to send the drawing as an object to all the current users.
     * From the user having the control to draw.
     * @param x1 (double)
     * @param y1 (double)
     * @param x  (double)
     * @param y  (double)
     * @param color (String) variable holding the color.
     */
    @Override
    public void drawingFromServer(double x1, double y1, double x, double y, String color) {
        chatGUI.gc.strokeLine(x1,y1,x,y);
        chatGUI.gc.setStroke(Color.valueOf(color));
    }

    /**
     * This method is used to send the input of the eraser to all the current users.
     * @param x (double) coordinate x
     * @param y (double) coordinate y
     * @param n (int) coordinate n
     * @param m (int) coordinate m
     * @param color String variable holding the color.
     */
    @Override
    public void clearFromServer(double x, double y, int n, int m, String color) {
        chatGUI.gc.fillOval(x,y,n,m);
        chatGUI.gc.setFill(Color.valueOf(color));
    }

    /**
     * This method is used to send to all the current users the clear Canvas.
     * @param v coordinate V
     * @param v1 coordinate V1
     * @param v2 coordinate V2
     * @param v3 coordinate V3
     * @param color variable holding the color.
     */
    @Override
    public void ClearCanvasFromServer(int v, int v1, int v2, int v3, String color) {
        chatGUI.gc.setFill(Color.WHITE);
        chatGUI.gc.fillRect(v,v1,v2,v3);
    }

    /**
     * This method is used to updated the actual label in the GUI, with the updated round.
     * Using {@link Platform#runLater(Runnable)} to update a GUI component from a non-GUI thread,
     * in which it will be handled by the GUI thread as soon as possible.
     * @param round holding the updated round from the server.
     * @throws RemoteException if failed to export the object.
     */
    @Override
    public void sendRoundFromServer(int round) throws RemoteException {
       Platform.runLater(() -> chatGUI.roundsLabel.setText("Round " + round + " of 5"));
    }

    /**
     * This method is used to update the round to the upcoming users joining the server.
     * @param round used to pass the updated round to the GUI.
     */
    @Override
    public void updateRoundFromServer(int round) {
        chatGUI.rnd = round;
    }

    /**
     * This method is used to reset to 0 the lock variable for the validateGuessGivePoints() method,
     * in order to give back the possibility to the user to guess the word and earn points again.
     * For further information see also: {@link Application.GameMechanic.Points#validateGuessGivePoints()} method.
     */
    @Override
    public void resetFromServer() {
        chatGUI.lock = 0;
        chatGUI.a = 0;
    }

    /**
     * This method is used to check if the user has the control,
     * ie have the draw button visible.
     * For further information see also: {@link ClientPaneFX#checkIfThisUserHasControl()} method.
     */
    @Override
    public void checkFromServer() {
        chatGUI.checkIfThisUserHasControl();
    }

    /**
     * This method is used to increment the word to guess form the list,
     * and then checking if the user has the control to give the complete word,
     * otherwise give only the first and last letter.
     * For further information see also: {@link ClientPaneFX#checkIfThisUserHasControl()} method.
     */
    @Override
    public void showNextWordToGuessFromServer() {
        chatGUI.count++;
        chatGUI.checkIfThisUserHasControl();
    }

    /**
     * This method is used to update the index of the word to guess to the other users.
     * @param index variable containing the index updated from the server.
     */
    @Override
    public void updateIndexWordFromServer(int index) {
        chatGUI.count = index;
    }

    /**
     * This method is used to set the countdown of each round to notify the
     * user of how much time left has.
     * @param timeline Integer variable used to update the countdown of the label of the GUI.
     */
    @Override
    public void setCountDownFromServer(int timeline) {
        Platform.runLater(() -> chatGUI.countDown.setText(String.valueOf(timeline)));
    }

    @Override
    public void updateCountDownVariableFromServer(int interval){
        chatGUI.interval = interval;
    }

    /**
     * This method is used to pick a winner the last round of the game session,
     * uses an Alert to notify all the users who's the winner. By pressing OK
     * on the alert the user will be prompted to the primaryStage which is the START.
     * @param winner winner.
     */
    @Override
    public void pickWinnerFromServer(String winner) {
        Platform.runLater(() -> {
            chatGUI.alert.setTitle("GAME OVER");
            chatGUI.alert.setHeaderText("THE WINNER IS..?");
            chatGUI.alert.setContentText(winner + "\n\nWould you like to play again?");
            chatGUI.another = chatGUI.alert.showAndWait();
            if (chatGUI.another.isPresent() && chatGUI.another.get() == ButtonType.OK) {
                try {
                    chatGUI.start(chatGUI.primaryStage);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }else if (chatGUI.another.isPresent() && chatGUI.another.get() == ButtonType.CANCEL){
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * This method is used to disable for everyone the control.
     * @param currentUsers array of Strings containing the names of the current users in the server.
     */
    @Override
    public void disableForEveryoneFromServer(String[] currentUsers) {
        enableDisableControl(false,true);
    }

    /**
     * This method is used to pick a random player each round,
     * and allow the control by enabling the buttons.
     */
    @Override
    public void pickPlayerToDrawFromServer() {
        enableDisableControl(true,false);
    }

    /**
     * Gives the control to draw to another user, if the current user
     * who has the control leaves the game.
     */
    @Override
    public void giveControlToOtherUserFromServer() {
        enableDisableControl(true,false);
    }

    /**
     *  Updates the leaderboard for all the current users in the server.
     *  if the user from the currentUsers array equals to the username passed in the textField
     *  we append "You" in front of the name for better readability in the leaderboard.
     *  Else we append just the username.
     * @param currentUsers array containing the current users in the server.
     */
    @Override
    public void updateUserListFromServer(String[] currentUsers) {
        chatGUI.users.clear();
        chatGUI.users.setText("Leaderboard\n");
        for (String user : currentUsers){
            if (user.equals(name)) {
              chatGUI.users.appendText(user + " You" + "\n");
            }else {
              chatGUI.users.appendText(user + "\n");
            }
        }
    }

    /**
     * This method is used to enable or disable the utilities button
     * in order to allow or not the control of drawing to the users.
     * @param enable if true enables the visibility else
     *               disables the visibility of the buttons.
     * @param disable if true disables the button
     *                else enables the button.
     */
    private void enableDisableControl(boolean enable,boolean disable){
        chatGUI.btnClear.setVisible(enable);
        chatGUI.btnDraw.setVisible(enable);
        chatGUI.btnColorRed.setVisible(enable);
        chatGUI.btnColorBlack.setVisible(enable);
        chatGUI.btnColorPurple.setVisible(enable);
        chatGUI.btnColorGreen.setVisible(enable);
        chatGUI.btnColorBlue.setVisible(enable);
        chatGUI.btnColorOrange.setVisible(enable);
        chatGUI.btnColorPink.setVisible(enable);
        chatGUI.btnColorYellow.setVisible(enable);
        chatGUI.clearCanvas.setVisible(enable);
        chatGUI.canvas.setDisable(disable);
    }

    @Override
    public void incrementPointsAmountFromServer() {
       chatGUI.a += 1;
    }

}
