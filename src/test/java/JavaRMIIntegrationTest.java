//import Application.Client;
//import Application.ClientPaneFX;
//import Application.Server.Server;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.rmi.RemoteException;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//public class JavaRMIIntegrationTest {
//
//    ClientPaneFX chatGUI;
//    Server server;
//    @Before
//    public void init() {
//        Server.startMain();
//    }
//    @Test
//    public void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
//        String expectedMessage = "Hello";
//        try {
//            Client client = new Client(chatGUI,"Marco");
//            assertEquals(server.updateChat("Marco","Hello"),
//                    client.messageFromServer("Marco","Hello"));
//        } catch (RemoteException e) {
//            fail("Exception Occurred: " + e);
//        }
//    }
//
//}