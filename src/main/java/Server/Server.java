package Server;

import Shared.ClientInterface;
import Shared.ServerInterface;
import Shared.Users;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements ServerInterface {

    private final String line = "<<---------------------------------------->>\n";
    private static final long serialVersionUID = 1L;
    public final Vector<Users> usersList;

    public Server() throws RemoteException {
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
    public void sendDrawing(Double x1, Double y1, double x, double y, String color) throws RemoteException {
        for (Users user : usersList) {
            try {
                user.getClient().drawingFromServer(x1, y1, x, y, color);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendClear(double x, double y, int n, int m, String color) throws RemoteException {
        for (Users user : usersList) {
            try {
                user.getClient().clearFromServer(x, y, n, m, color);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendClearCanvas(int v, int v1, int v2, int v3, String color) throws RemoteException {
        for (Users user : usersList) {
            try {
                user.getClient().clearFromServer(v, v1, v2, v3, color);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    public void updateUserList() {
        String[] currentUsers = getUserList();
        for(Users user : usersList){
            try {
                user.getClient().updateUserListFromServer(currentUsers);
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
    public void updateChat(String userName, String chatMessage) throws RemoteException {
        sendMessageToEverybody(userName + ": " + chatMessage);
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
            updateUserList();
        }
        catch(RemoteException | MalformedURLException | NotBoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void leaveChat(String userName) throws RemoteException {
        for(Users c : usersList){
            if(c.getName().equals(userName)){
                System.out.println(line + userName + " left the chat session" + "\n" + line);
                System.out.println(new Date(System.currentTimeMillis()));
                usersList.remove(c);
                updateUserList();
                break;
            }
        }
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

