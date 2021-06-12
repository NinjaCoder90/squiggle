package com.misterycrew.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    void messageFromServer(String username, String message) throws RemoteException;

    void drawingFromServer(double x1, double y1, double x, double y, String color) throws RemoteException;

    void clearFromServer(double x, double y, int n, int m, String color) throws RemoteException;

    void updateUserListFromServer(String[] currentUsers) throws RemoteException;

    void ClearCanvasFromServer(int v, int v1, int v2, int v3, String color) throws RemoteException;

    void sendRoundFromServer(int round) throws RemoteException;

    void updateRoundFromServer(int round) throws RemoteException;

    void resetFromServer() throws RemoteException;

    void checkFromServer() throws RemoteException;

    void showNextWordToGuessFromServer() throws RemoteException;

    void updateIndexWordFromServer(int index) throws RemoteException;

    void setCountDownFromServer(int timeline) throws RemoteException;

    void pickPlayerToDrawFromServer() throws RemoteException;

    void disableForEveryoneFromServer(String[] userList) throws RemoteException;

    void giveControlToOtherUserFromServer() throws RemoteException;

    void updateCountDownVariableFromServer(int interval) throws RemoteException;

    void pickWinnerFromServer(String winner) throws RemoteException;

    void incrementPointsAmountFromServer() throws RemoteException;

    void clearChatFromServer() throws RemoteException;
}
