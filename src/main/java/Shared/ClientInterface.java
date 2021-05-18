package Shared;

import javafx.scene.canvas.GraphicsContext;

import java.awt.event.MouseEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    public void messageFromServer(String message) throws RemoteException;

    public void drawingFromServer(Double x1, Double y1, double x, double y) throws RemoteException;

    public void updateUserList(String[] currentUsers) throws RemoteException;

}
