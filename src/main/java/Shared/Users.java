package Shared;

/**
 * A class used by the server program to keep
 * details of connected clients ordered
 **/
public class Users {

    public String name;
    public ClientInterface client;
    public static int score;

    public Users(String name, ClientInterface client, int score){
        this.name = name;
        this.client = client;
        Users.score = score;
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Users.score = score;
    }

    public String getName(){
        return name;
    }
    public ClientInterface getClient(){
        return client;
    }

}