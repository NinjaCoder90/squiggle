package Application.Shared;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used by the server program to keep
 * details of connected clients ordered
 **/
public class Users {

    public String name;
    public ClientInterface client;
    public int score;
    public List<Integer> list = new ArrayList<>();

    public Users(String name, ClientInterface client, int score){
        this.name = name;
        this.client = client;
        this.score = score;
    }

    public Users(){
    }

    public int getScore() {
        return score;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setScore(int score) {
        list.add(score);
        this.score = score;
    }

    public String getName(){
        return name;
    }

    public ClientInterface getClient(){
        return client;
    }

}