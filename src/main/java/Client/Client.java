package Client;

import GUI.ClientPaneFX;
import Shared.ClientInterface;
import Shared.ServerInterface;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private static final long serialVersionUID = 7468891722773409712L;
    ClientPaneFX chatGUI;
    private final String clientServiceName;
    private final String name;
    public ServerInterface serverInterface;
    protected boolean connectionProblem = false;


    public Client(ClientPaneFX chatGUI,String userName) throws RemoteException{
        super();
        this.chatGUI = chatGUI;
        this.name = userName;
        this.clientServiceName = "ClientListenService_" + userName;
    }


    public void startClient() throws RemoteException{
        String hostName = "localhost";
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            String serviceName = "distributedService";
            serverInterface = (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        }
        catch (ConnectException e) {
            //Alert alert = new Alert(Alert.AlertType.ERROR, "The server seems to be unavailable\nPlease try later", ButtonType.OK);
            //alert.show();
            connectionProblem = true;
            e.printStackTrace();
        }catch(NotBoundException | MalformedURLException me){
            connectionProblem = true;
            me.printStackTrace();
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
        }
        catch(Exception e){
            //e.printStackTrace();
            e.getCause();
        }
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {
       //System.out.println(message);
        chatGUI.chatSection.appendText(message);
    }

    @Override
    public void drawingFromServer(Double x1, Double y1, double x, double y) throws RemoteException {
        chatGUI.gc.strokeLine(x1,y1,x,y);
        chatGUI.gc.setFill(Color.BLACK);
    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        for (String user : currentUsers) {
            chatGUI.users.appendText(user);
        }
    }
}
