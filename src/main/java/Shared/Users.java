package Shared;

/**
 * A class used by the server program to keep
 * details of connected clients ordered
 **/
public class Users {

    public String name;
    public ClientInterface client;

    public Users(String name, ClientInterface client){
        this.name = name;
        this.client = client;
    }

    public String getName(){
        return name;
    }
    public ClientInterface getClient(){
        return client;
    }

}