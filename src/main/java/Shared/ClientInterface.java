package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    public void messageFromServer(String username, String message) throws RemoteException;

    public void drawingFromServer(Double x1, Double y1, double x, double y ,String color) throws RemoteException;

    public void clearFromServer(double x,double y, int n, int m, String color) throws RemoteException;

    public void updateUserListFromServer(String[] currentUsers) throws RemoteException;

    public void ClearCanvasFromServer(int v,int v1, int v2, int v3, String color) throws RemoteException;

    public void enableDrawingFromSever() throws RemoteException;

    public void sendRoundFromServer(int round) throws RemoteException;

    public void updateRoundFromServer(int round) throws RemoteException;

}
