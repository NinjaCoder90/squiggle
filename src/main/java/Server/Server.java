package Server;

import Application.Client;
import Shared.ClientInterface;
import Shared.ServerInterface;
import Shared.Users;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements ServerInterface {

    private final String line = "<<=========================================>>\n";
    private static final long serialVersionUID = 1L;
    public final Vector<Users> usersList;
    String[] detail;

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
    public int returnCurrentUsers() throws RemoteException{
        int size = 0;
        for (Users user : usersList) {
            size++;
        }
        return size;
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
    public void sendRound(int round) throws RemoteException{
        for (Users user : usersList) {
            try {
                user.getClient().sendRoundFromServer(round);
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

    public String[] getUserList() {
        // generate an array of current users
        String[] allUsers = new String[usersList.size()];
        for(int i = 0; i < allUsers.length; i++){
            allUsers[i] = usersList.elementAt(i).getName();
        }
        return allUsers;
    }

    @Override
    public void leaveGame(String userName) throws RemoteException {
        for(Users user : usersList){
            if(user.getName().equals(userName)){
                System.out.println(line + userName + " left the game session\n" + new Date(System.currentTimeMillis()) + "\n" + line);
                usersList.remove(user);
                updateUserList();
                break;
            }
        }
    }

    @Override
    public void enableDrawing(String name) throws RemoteException{
          /*if (detail[0].equals(user.getName())) {
              usersList.elementAt(0).getClient().enableDrawingFromSever();
          }*/
    }

    @Override
    public void updateChat(String userName, String chatMessage) throws RemoteException {
        sendMessageToEverybody(userName , ": " + chatMessage);
    }

    private void sendMessageToEverybody(String username, String newMessage) {
        for (Users user : usersList) {
            try {
                user.getClient().messageFromServer(username, newMessage);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean checkIfUsernameExist(String username) throws RemoteException{
        String[] currentUsers = getUserList();
        for (int i = 0; i < getUserList().length; i++) {
            if (currentUsers[i].equals(username) ) {
                return true;
            }
        }
       /* for (String user : getUserList()) {
            if (user.equals(username)) {
                return true;
            }
        }*/
        return false;
    }

    @Override
    public void registerUsers(String[] details) throws RemoteException {
            System.out.println(new Date(System.currentTimeMillis()));
            System.out.println(details[0] + " has joined the game session");
            System.out.println(details[0] + "'s hostname : " + details[1]);
            System.out.println(details[0] + "'s RMI service : " + details[2]);

            try {
                ClientInterface nextClient = (ClientInterface) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
                usersList.addElement(new Users(details[0], nextClient));
                updateUserList();
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                e.printStackTrace();
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

