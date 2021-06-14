package ServerTests;

import com.misterycrew.Server.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
