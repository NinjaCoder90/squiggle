//package Application;
//
//import Application.Server.Server;
//
//import java.rmi.NotBoundException;
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//
//import org.junit.Test;
//import org.junit.Before;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//public class JavaRMIIntegrationTest {
//    private Server messengerService;
//    @Before
//    public void init() {
//        try {
//            messengerService = new Server();
//            messengerService.createStubAndBind();
//        } catch (RemoteException e) {
//            fail("Exception Occurred: " + e);
//        }
//    }
//    @Test
//    public void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
//        try {
//            Registry registry = LocateRegistry.getRegistry();
//            MessengerService server = (MessengerService) registry.lookup("MessengerService");
//            String responseMessage = server.sendMessage("Client Message");
//            String expectedMessage = "Server Message";
//            assertEquals(responseMessage, expectedMessage);
//        } catch (RemoteException | NotBoundException e) {
//            fail("Exception Occurred: " + e);
//        }
//    }
//}
