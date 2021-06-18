package com.misterycrew.Server;

public class ServerFailedToStartException extends Exception{

    @Override
    public String getMessage() {
        return "ERROR: Server could not start!";
    }
}