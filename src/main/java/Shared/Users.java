package Shared;

/**
 * A class used by the server program to keep
 * details of connected clients ordered
 *
 */
public class Users {

    public String name;
    public ClientInterface client;

    //constructor
    public Users(String name, ClientInterface client){
        this.name = name;
        this.client = client;
    }

    //getters and setters
    public String getName(){
        return name;
    }
    public ClientInterface getClient(){
        return client;
    }


}