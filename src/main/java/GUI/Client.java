package GUI;

import Shared.ClientInterface;
import Shared.ServerInterface;
import javafx.scene.paint.Color;

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
        this.clientServiceName = "ClientService_" + userName;
    }


    public void startClient() throws RemoteException {
        String hostName = "localhost";
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            String serviceName = "distributedService";
            serverInterface = (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        }
        catch (ConnectException | NotBoundException | MalformedURLException e) {
            //Alert alert = new Alert(Alert.AlertType.ERROR, "The server seems to be unavailable\nPlease try later", ButtonType.OK);
            //alert.show();
            connectionProblem = true;
            e.printStackTrace();
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
            e.getCause();
        }
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {
        chatGUI.chatSection.appendText(message);
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
        chatGUI.gc.fillRect(v,v1,v2,v3);
        chatGUI.gc.setFill(Color.valueOf(color));
    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
            chatGUI.setClientPanel(currentUsers);
    }

}
