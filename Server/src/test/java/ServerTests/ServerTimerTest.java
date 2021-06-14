package ServerTests;

import com.misterycrew.Server.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ServerTimerTest {

    @Test
    @DisplayName("Time should be decremented after 5 second")
    void timerShouldBeDecrementedTo5(){
        try {
            Server server = new Server();
            server.setTimerGame();
            Thread.sleep(2500);

            assertEquals(88, server.getInterval());
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

            assertEquals( 89, server.getInterval());
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
