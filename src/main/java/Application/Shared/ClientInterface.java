package Application.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    public void messageFromServer(String username, String message) throws RemoteException;

    public void drawingFromServer(double x1, double y1, double x, double y ,String color) throws RemoteException;

    public void clearFromServer(double x,double y, int n, int m, String color) throws RemoteException;

    public void updateUserListFromServer(String[] currentUsers) throws RemoteException;

    public void ClearCanvasFromServer(int v,int v1, int v2, int v3, String color) throws RemoteException;

    public void sendRoundFromServer(int round) throws RemoteException;

    public void updateRoundFromServer(int round) throws RemoteException;

    public void resetFromServer() throws RemoteException;

    public void checkFromServer() throws RemoteException;

    public void showNextWordToGuessFromServer() throws RemoteException;

    public void updateIndexWordFromServer(int index) throws RemoteException;

    public void setCountDownFromServer(int timeline) throws RemoteException;

    public void pickPlayerToDrawFromServer() throws RemoteException;

    public void disableForEveryoneFromServer(String[] userList) throws RemoteException;

    public void giveControlToOtherUserFromServer() throws RemoteException;

    public void updateCountDownVariableFromServer(int interval) throws RemoteException;

    public void pickWinnerFromServer(String userName, int largest) throws RemoteException;

}
