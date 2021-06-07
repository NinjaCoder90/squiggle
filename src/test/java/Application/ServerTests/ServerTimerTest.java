package Application.ServerTests;

import Application.Server.Server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;

public class ServerTimerTest {

    @Test
    @DisplayName("Time should be decremented after 5 second")
    void timerShouldBeDecrementedTo5(){
        try {
            Server server = new Server();
            server.setTimerGame();
            Thread.sleep(5500);

            assertEquals(5, server.getInterval());
        } catch (RemoteException |
                InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Time should be decremented after 1 second")
    void timerShouldBeDecrementedTo2(){
        try {
            Server server = new Server();
            server.setTimerGame();
            Thread.sleep(2000);

            assertEquals( 9, server.getInterval());
        } catch (RemoteException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    @DisplayName("Round should be changed by 1")
    void roundShouldBeIncremented(){
        try {
            Server server = new Server();
            server.setTimerGame();
            Thread.sleep(22000);
            assertNotEquals( 1, server.getRound());
            assertEquals(2, server.getRound());

        } catch (RemoteException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }
}
