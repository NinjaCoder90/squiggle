package com.misterycrew.Application;

import com.misterycrew.Application.GameMechanic.Points;
import com.misterycrew.Shared.ClientInterface;
import com.misterycrew.Shared.ServerInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private static final long serialVersionUID = 7468891722773409712L;
    ClientPaneStartFX gameGUI;
    private final String clientServiceName;
    private final String name;
    public ServerInterface serverInterface;
    protected boolean connectionProblem = false;

    /**
     * Constructor for the Client class.
     *
     * @param gameGUI  object of the ClientPaneStartFX class.
     * @param userName holding the username of the user.
     * @throws RemoteException if failed to export the object.
     */
    public Client(ClientPaneStartFX gameGUI, String userName) throws RemoteException {
        super();
        this.gameGUI = gameGUI;
        this.name = userName;
        this.clientServiceName = "ClientService_" + userName;
    }

    /**
     * Register our own listening service/interface
     * lookup the server RMI interface, then send our details.
     *
     * @throws RemoteException if failed to export the object.
     */
    public void startClient() throws RemoteException, ConnectionProblemsException {
        String hostName = "localhost";
        String serviceName = "distributedService";
        String[] details = new String[]{name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            serverInterface = (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        } catch (ConnectException | NotBoundException | MalformedURLException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "The server seems to be unavailable\nPlease try later");
            Optional<ButtonType> another = alert.showAndWait();
            if (another.isPresent() && another.get() == ButtonType.OK) {
                System.exit(0);
            }
            connectionProblem = true;
            throw new ConnectionProblemsException();
        }
        if (!connectionProblem) {
            registerWithServer(details);
            System.out.println("Client Listen RMI Server is running...\n");
        }
    }

    /**
     * This method is used to register the user joining the server,
     * by passing to the server this.ref which is the class object
     * as a remote object reference.
     *
     * @param details (String) array containing the username,hostname
     *                (which is "localhost") and the clientServiceName
     *                (which is just a string containing the "ClientService_" + username)
     */
    public void registerWithServer(String[] details) {
        try {
            serverInterface.passIDentity(this.ref);
            serverInterface.registerUsers(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to append to the textArea the message send by the user,
     * showing it to all the current users.
     *
     * @param username (String) variable holding the username of the user sending the message.
     * @param message  (String) variable holding the actual message to send.
     */
    @Override
    public void messageFromServer(String username, String message) {
        gameGUI.chatSection.appendText(username + message + "\n");
    }

    /**
     * This method is used to send the drawing as an object to all the current users.
     * From the user having the control to draw.
     *
     * @param x1    (double)
     * @param y1    (double)
     * @param x     (double)
     * @param y     (double)
     * @param color (String) variable holding the color.
     */
    @Override
    public void drawingFromServer(double x1, double y1, double x, double y, String color) {
        gameGUI.gc.strokeLine(x1, y1, x, y);
        gameGUI.gc.setStroke(Color.valueOf(color));
    }

    /**
     * This method is used to send the input of the eraser to all the current users.
     *
     * @param x     (double) coordinate x
     * @param y     (double) coordinate y
     * @param n     (int) coordinate n
     * @param m     (int) coordinate m
     * @param color String variable holding the color.
     */
    @Override
    public void clearFromServer(double x, double y, int n, int m, String color) {
        gameGUI.gc.fillOval(x, y, n, m);
        gameGUI.gc.setFill(Color.valueOf(color));
    }

    /**
     * This method is used to send to all the current users the clear Canvas.
     *
     * @param v     coordinate V
     * @param v1    coordinate V1
     * @param v2    coordinate V2
     * @param v3    coordinate V3
     * @param color variable holding the color.
     */
    @Override
    public void ClearCanvasFromServer(int v, int v1, int v2, int v3, String color) {
        gameGUI.gc.setFill(Color.WHITE);
        gameGUI.gc.fillRect(v, v1, v2, v3);
    }

    /**
     * This method is used to updated the actual label in the GUI, with the updated round.
     * Using {@link Platform#runLater(Runnable)} to update a GUI component from a non-GUI thread,
     * in which it will be handled by the GUI thread as soon as possible.
     *
     * @param round holding the updated round from the server.
     * @throws RemoteException if failed to export the object.
     */
    @Override
    public void sendRoundFromServer(int round) throws RemoteException {
        Platform.runLater(() -> gameGUI.roundsLabel.setText("Round " + round + " of 5"));
    }

    /**
     * This method is used to update the round to the upcoming users joining the server.
     *
     * @param round used to pass the updated round to the GUI.
     */
    @Override
    public void updateRoundFromServer(int round) {
        gameGUI.rnd = round;
    }

    /**
     * This method is used to reset to 0 the lock variable for the validateGuessGivePoints() method,
     * in order to give back the possibility to the user to guess the word and earn points again.
     * For further information see also: {@link Points#validateGuessGivePoints()} method.
     */
    @Override
    public void resetFromServer() {
        gameGUI.setLock(0);
        gameGUI.a = 0;
    }

    /**
     * This method is used to check if the user has the control,
     * ie have the draw button visible.
     * For further information see also: {@link ClientPaneStartFX#checkIfThisUserHasControl()} method.
     */
    @Override
    public void checkFromServer() {
        gameGUI.checkIfThisUserHasControl();
    }

    /**
     * This method is used to increment the word to guess form the list,
     * and then checking if the user has the control to give the complete word,
     * otherwise give only the first and last letter.
     * For further information see also: {@link ClientPaneStartFX#checkIfThisUserHasControl()} method.
     */
    @Override
    public void showNextWordToGuessFromServer() {
        gameGUI.count++;
        gameGUI.checkIfThisUserHasControl();
    }

    /**
     * This method is used to update the index of the word to guess to the other users.
     *
     * @param index variable containing the index updated from the server.
     */
    @Override
    public void updateIndexWordFromServer(int index) {
        gameGUI.count = index;
    }

    /**
     * This method is used to set the countdown of each round to notify the
     * user of how much time left has.
     *
     * @param timeline Integer variable used to update the countdown of the label of the GUI.
     */
    @Override
    public void setCountDownFromServer(int timeline) {
        Platform.runLater(() -> gameGUI.countDown.setText(String.valueOf(timeline)));
    }

    @Override
    public void updateCountDownVariableFromServer(int interval) {
        gameGUI.interval = interval;
    }

    /**
     * This method is used to pick a winner the last round of the game session,
     * by invoking the ClientPaneGameOverFX which then notify all the users who's the winner.
     * By pressing the "TRY AGAIN" button the user will be prompted to the primaryStage which is the START,
     * otherwise it will exit the game.
     *
     * @param winner winner.
     */
    @Override
    public void pickWinnerFromServer(String winner) {
        ClientPaneGameOverFX gameOverPane = new ClientPaneGameOverFX();
        Platform.runLater(() -> gameOverPane.startGameOverPane(gameGUI.primaryStage, winner));
    }

    /**
     * This method is used to disable for everyone the control.
     *
     * @param currentUsers array of Strings containing the names of the current users in the server.
     */
    @Override
    public void disableForEveryoneFromServer(String[] currentUsers) {
        enableDisableControl(false, true);
    }

    /**
     * This method is used to pick a random player each round,
     * and allow the control by enabling the buttons.
     */
    @Override
    public void pickPlayerToDrawFromServer() {
        enableDisableControl(true, false);
    }

    /**
     * Gives the control to draw to another user, if the current user
     * who has the control leaves the game.
     */
    @Override
    public void giveControlToOtherUserFromServer() {
        enableDisableControl(true, false);
    }

    /**
     * Updates the leaderboard for all the current users in the server.
     * if the user from the currentUsers array equals to the username passed in the textField
     * we append "You" in front of the name for better readability in the leaderboard.
     * Else we append just the username.
     *
     * @param currentUsers array containing the current users in the server.
     */
    @Override
    public void updateUserListFromServer(String[] currentUsers) {
        gameGUI.users.clear();
        gameGUI.users.setText("Leaderboard\n");
        for (String user : currentUsers) {
            if (user.equals(name)) {
                gameGUI.users.appendText("You " + user + "\n");
            } else {
                gameGUI.users.appendText(user + "\n");
            }
        }
    }

    /**
     * This method is used to enable or disable the utilities button
     * in order to allow or not the control of drawing to the users.
     *
     * @param enable  if true enables the visibility else
     *                disables the visibility of the buttons.
     * @param disable if true disables the button
     *                else enables the button.
     */
    private void enableDisableControl(boolean enable, boolean disable) {
        gameGUI.btnClear.setVisible(enable);
        gameGUI.btnDraw.setVisible(enable);
        gameGUI.btnColorRed.setVisible(enable);
        gameGUI.btnColorBlack.setVisible(enable);
        gameGUI.btnColorPurple.setVisible(enable);
        gameGUI.btnColorGreen.setVisible(enable);
        gameGUI.btnColorBlue.setVisible(enable);
        gameGUI.btnColorOrange.setVisible(enable);
        gameGUI.btnColorPink.setVisible(enable);
        gameGUI.btnColorYellow.setVisible(enable);
        gameGUI.clearCanvas.setVisible(enable);
        gameGUI.canvas.setDisable(disable);
    }

    /**
     * This method allows the Points class to know how many users
     * are guessing, in order to differentiate the given points.
     * By incrementing a variable in the same round so that the upcoming users will get
     * less points.
     */
    @Override
    public void incrementPointsAmountFromServer() {
        gameGUI.a += 1;
    }

    /**
     * This method is used to clear the chat section when the game ends.
     */
    @Override
    public void clearChatFromServer() {
        gameGUI.chatSection.clear();
    }
}
