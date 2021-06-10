package com.misterycrew.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface ServerInterface extends Remote {

    void updateChat(String userName, String chatMessage) throws RemoteException;

    void registerUsers(String[] details) throws RemoteException;

    void leaveGame(String userName, boolean hasControl) throws RemoteException;

    void passIDentity(RemoteRef ref) throws RemoteException;

    void sendDrawing(Double x1, Double y1, double x, double y, String color) throws RemoteException;

    void sendClear(double x, double y, int n, int m, String color) throws RemoteException;

    void sendClearCanvas(int v, int v1, int v2, int v3, String color) throws RemoteException;

    int returnCurrentUsers() throws RemoteException;

    void updateRound() throws RemoteException;

    void setTimerGame() throws RemoteException;

    void updateIndexWord() throws RemoteException;

    void updateCountDownVariable() throws RemoteException;

    void getScoreAndUsername(int scoreUser, String nameUser) throws RemoteException;

    void incrementPointsAmount() throws RemoteException;

    int getMembers() throws RemoteException;

    int getTotRounds() throws RemoteException;
}