package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface ServerInterface extends Remote {

    public void updateChat(String userName, String chatMessage) throws RemoteException;

    public void registerUsers(String[] details) throws RemoteException;

    public void passIDentity(RemoteRef ref)throws RemoteException;
}
