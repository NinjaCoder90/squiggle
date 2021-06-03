package Application.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface ServerInterface extends Remote {

    public void updateChat(String userName, String chatMessage) throws RemoteException;

    public void registerUsers(String[] details) throws RemoteException;

    public void leaveGame(String userName) throws RemoteException;

    public void passIDentity(RemoteRef ref) throws RemoteException;

    public void sendDrawing(Double x1, Double y1, double x, double y, String color) throws RemoteException;

    public void sendClear(double x,double y, int n, int m, String color) throws RemoteException;

    public void sendClearCanvas(int v,int v1, int v2, int v3, String color) throws RemoteException;

    public boolean checkIfUsernameExist(String username) throws RemoteException;

    public int returnCurrentUsers() throws RemoteException;

    public void updateRound() throws RemoteException;

    public void setTimerGame() throws RemoteException;

    public void updateIndexWord() throws RemoteException;

}