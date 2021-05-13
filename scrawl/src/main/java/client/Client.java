package client;

import shared.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface {

    protected Client() throws RemoteException {
        super();
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {

    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {

    }
}
