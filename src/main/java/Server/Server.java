package Server;

import Shared.ClientInterface;
import Shared.ServerInterface;
import Shared.Users;
import javafx.scene.canvas.GraphicsContext;

import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private final String line = "--------------------------------------\n";
    private static final long serialVersionUID = 1L;
    private final Vector<Users> usersList;

    protected Server() throws RemoteException {
        super();
        usersList = new Vector<>(10,1);
    }

    public static void main(String[] args) {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "distributedService";

        if (args.length == 2){
            hostName = args[0];
            serviceName = args[1];
        }
        try {
            ServerInterface hello = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("RMI Server is running...");
        }catch (Exception e){
            System.out.println("Server had problems starting");
        }
    }

    private static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("The RMI Server is ready");
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
    @Override
    public void sendDrawing(Double x1, Double y1, double x, double y) throws RemoteException {
        sendDrawingToEverybody(x1,y1,x,y);
    }

    private void sendDrawingToEverybody(Double x1, Double y1, double x, double y){
        for (Users user : usersList) {
            try {
                user.getClient().drawingFromServer(x1, y1, x, y);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateChat(String userName, String chatMessage) throws RemoteException {
        sendMessageToEverybody(userName + " : " + chatMessage + "\n");
    }

    private void sendMessageToEverybody(String newMessage) {
        for (Users user : usersList) {
            try {
                user.getClient().messageFromServer(newMessage);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerUsers(String[] details) throws RemoteException {

        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);

        try{
            ClientInterface nextClient = ( ClientInterface )Naming.lookup("rmi://" + details[1] + "/" + details[2]);

            usersList.addElement(new Users(details[0], nextClient));

            nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");

            sendMessageToEverybody("[Server] : " + details[0] + " has joined the group.\n");

            updateUserList();
        }
        catch(RemoteException | MalformedURLException | NotBoundException e){
            //e.printStackTrace();
            e.getCause();
        }
    }

    @Override
    public void leaveChat(String userName) throws RemoteException {
        for(Users c : usersList){
            if(c.getName().equals(userName)){
                System.out.println(line + userName + " left the chat session");
                System.out.println(new Date(System.currentTimeMillis()));
                usersList.remove(c);
                break;
            }
        }
        if(!usersList.isEmpty()){
            updateUserList();
        }
    }

    private void updateUserList() {
        String[] currentUsers = getUserList();
        for(Users c : usersList){
            try {
                c.getClient().updateUserList(currentUsers);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] getUserList() {
        // generate an array of current users
        String[] allUsers = new String[usersList.size()];
        for(int i = 0; i < allUsers.length; i++){
            allUsers[i] = usersList.elementAt(i).getName();
        }
        return allUsers;
    }

    @Override
    public void passIDentity(RemoteRef ref) throws RemoteException {
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

