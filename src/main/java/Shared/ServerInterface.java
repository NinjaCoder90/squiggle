package Shared;

import javafx.scene.canvas.GraphicsContext;

import java.awt.event.MouseEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface ServerInterface extends Remote {

    public void updateChat(String userName, String chatMessage) throws RemoteException;

    public void registerUsers(String[] details) throws RemoteException;

    public void leaveChat(String userName) throws RemoteException;

    public void passIDentity(RemoteRef ref) throws RemoteException;

    public void sendDrawing(Double x1, Double y1, double x, double y) throws RemoteException;

}