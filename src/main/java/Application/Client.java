package Application;

import Server.Server;
import Shared.ClientInterface;
import Shared.ServerInterface;
import Shared.Users;
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
import java.util.Arrays;
import java.util.Optional;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private static final long serialVersionUID = 7468891722773409712L;
    ClientPaneFX chatGUI ;
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
    public String getName() {
        return name;
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
        }
        System.out.println("Client Listen RMI Server is running...\n");
    }

    private void registerWithServer(String[] details) {
        try{
            serverInterface.passIDentity(this.ref);
            serverInterface.registerUsers(details);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void messageFromServer(String username,String message) throws RemoteException {
            chatGUI.chatSection.appendText(username + message + "\n");
    }

    @Override
    public void drawingFromServer(Double x1, Double y1, double x, double y, String color) throws RemoteException {
        chatGUI.gc.strokeLine(x1,y1,x,y);
        chatGUI.gc.setStroke(Color.valueOf(color));
    }

    @Override
    public void clearFromServer(double x, double y, int n, int m, String color) throws RemoteException {
        chatGUI.gc.fillOval(x,y,n,m);
        chatGUI.gc.setFill(Color.valueOf(color));
    }

    @Override
    public void ClearCanvasFromServer(int v, int v1, int v2, int v3, String color) throws RemoteException {
        chatGUI.gc.setFill(Color.WHITE);
        chatGUI.gc.fillRect(v,v1,v2,v3);
    }

    @Override
    public void enableDrawingFromSever() throws RemoteException {
        chatGUI.btnColorRed.setVisible(true);
    }

    @Override
    public void sendRoundFromServer(int round) throws RemoteException {
        Platform.runLater(() -> chatGUI.roundsLabel.setText("Round " + round + " of 3"));
    }

    @Override
    public void updateUserListFromServer(String[] currentUsers) throws RemoteException {

        if (name.equals(currentUsers[0])) {
            chatGUI.btnClear.setVisible(true);
            chatGUI.btnDraw.setVisible(true);
            chatGUI.btnColorRed.setVisible(true);
            chatGUI.btnColorBlack.setVisible(true);
            chatGUI.btnColorPurple.setVisible(true);
            chatGUI.btnColorGreen.setVisible(true);
            chatGUI.btnColorBlue.setVisible(true);
            chatGUI.btnColorOrange.setVisible(true);
            chatGUI.btnColorPink.setVisible(true);
            chatGUI.btnColorYellow.setVisible(true);
            chatGUI.clearCanvas.setVisible(true);
            chatGUI.canvas.setDisable(false);
        }

        chatGUI.users.clear();
        chatGUI.users.setText("Leaderboard\n");
       for (String user : currentUsers){
           chatGUI.users.appendText(user + "\n");
       }
    }
}
