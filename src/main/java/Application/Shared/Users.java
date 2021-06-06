package Application.Shared;

import java.util.HashMap;

/**
 * A class used by the server program to keep
 * details of connected clients ordered
 **/
public class Users {

    public String name;
    public String overloadName;
    public int overloadScore;
    public ClientInterface client;
    public static int score;
    public static HashMap<String,Integer> map = new HashMap<>();

    public Users(String name, ClientInterface client, int score){
        this.name = name;
        this.client = client;
        Users.score = score;
    }

    public Users(String name, int score){
        this.overloadName = name;
        this.overloadScore = score;
    }

    public String getOverloadName() {
        return overloadName;
    }

    public void setOverloadName(String overloadName) {
        this.overloadName = overloadName;
    }

    public int getOverloadScore() {
        return overloadScore;
    }

    public void setOverloadScore(int overloadScore) {
        this.overloadScore = overloadScore;
    }

//    public int getScore() {
//        return score;
//    }
//
//    public List<Integer> getList() {
//        return list;
//    }
//
//    public void setScore(int score) {
//        list.add(score);
//        this.score = score;
//    }
    public static int getScore() {
        return score;
    }

    public static HashMap<String,Integer> getMap() {
        return map;
    }

    public static void setScore(int score) {
//        map.put(name,Users.score);
        Users.score = score;
    }

    public String getName(){
        return name;
    }

    public ClientInterface getClient(){
        return client;
    }

}