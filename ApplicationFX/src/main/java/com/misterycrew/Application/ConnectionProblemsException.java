
package com.misterycrew.Application;

public class ConnectionProblemsException extends Exception{

    @Override
    public String getMessage() {
        return "ERROR: The client had some problems of connection try later";
    }
}
