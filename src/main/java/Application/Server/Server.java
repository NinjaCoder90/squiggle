package Application.Server;

import Application.Shared.ClientInterface;
import Application.Shared.ServerInterface;
import Application.Shared.Users;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Server extends UnicastRemoteObject implements ServerInterface {

    public static int round = 0;
    private int interval = 21;
    private final String line = "<<=========================================>>\n";
    private static final long serialVersionUID = 1L;
    private static final CopyOnWriteArrayList<Users> usersList = new CopyOnWriteArrayList<>();
    private int indexWord = 0;
    List<Integer> list = new ArrayList<>();
    public int next;
    int oldValue;

    /**
     * Constructor for the Server class inheriting the super() class
     * used to create and export a new {@link UnicastRemoteObject} object using an anonymous port.
     * @throws RemoteException if failed to export the object.
     */
    public Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "distributedService";

        if (args.length == 2){
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ServerInterface hello = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("RMI Server is running...");

        }catch (Exception e){
            System.out.println("Server had problems starting");
        }
    }

    private static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("The RMI Server is ready");
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public int returnCurrentUsers() throws RemoteException{
        return usersList.size();
    }

    @Override
    public void sendDrawing(Double x1, Double y1, double x, double y, String color) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().drawingFromServer(x1, y1, x, y, color)));
    }

    @Override
    public void sendClear(double x, double y, int n, int m, String color) throws RemoteException {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().clearFromServer(x, y, n, m, color)));
    }

    @Override
    public void updateRound(){
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateRoundFromServer(round)));
    }

    public static void sendRound(int round){
        usersList.forEach(throwingConsumerWrapper(user ->  user.getClient().sendRoundFromServer(round)));
    }

    private static void resetIndexGivePointsMethod(){
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().resetFromServer()));
    }

    @Override
    public void setTimerGame() throws RemoteException {
        ScheduledExecutorService timerGame = Executors.newScheduledThreadPool(5);
        timerGame.scheduleAtFixedRate(() -> {
            round++;
            if (round == 5) {
//                chooseWinner();
//                round = 0;
                timerGame.shutdownNow();
            }
            System.out.println("ciao sono anora ATTIVO");
            //setCountDown();
            sendRound(round);
            showNextWordToGuess();
            if (round > 1){
                pickPlayerToDraw();
            }
            checkIfThisUserHasControl();
            resetIndexGivePointsMethod();
        }, 0, 20, TimeUnit.SECONDS);
    }

    private void disableForEveryone() {
        String[] currentUsers = getUserList();
        usersList.forEach(throwingConsumerWrapper(user ->
                user.getClient().disableForEveryoneFromServer(currentUsers)));
    }

    private void chooseWinner(){
//       usersList.forEach(throwingConsumerWrapper(user -> user ));
    }

    public void setCountDown(){
        ScheduledExecutorService countDown = Executors.newSingleThreadScheduledExecutor();
        countDown.scheduleAtFixedRate(() -> {
            if (interval == 0) {
                interval = 21;
                countDown.shutdownNow();
            }else {
                usersList.forEach(throwingConsumerWrapper(user -> user.getClient().setCountDownFromServer(--interval)));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void updateIndexWord() throws RemoteException{
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateIndexWordFromServer(indexWord)));
    }

    private void showNextWordToGuess(){
        indexWord++;
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().showNextWordToGuessFromServer()));
    }

    /**
     * This method is used to pick after the first round a random player
     * to draw, using the {@link Random} class to generate a pseudo random
     * Integer, then checking with a while loop if the oldValue equals to
     * the random value if true it will generate another random number
     * until the condition becomes false ie. (next != oldValue),
     * if false it will store the random number generated in the oldValue variable
     * and proceed to get the user at that index from the usersList
     * and give him the control.
     *
     * disableForEveryone() -> used to disable the control to everyone,
     *                         in this way we dont have to check who has the
     *                         control i.e. (ability to draw).
     *
     */
    private void pickPlayerToDraw() {
        disableForEveryone();
        Random rand = new Random();
        next = rand.nextInt(usersList.size());

        while (next == oldValue) {
            next = rand.nextInt(usersList.size());
        }
        oldValue = next;
        try {
            Users user = usersList.get(next);
            user.getClient().pickPlayerToDrawFromServer();
        } catch (RemoteException exception) {
                exception.printStackTrace();
        }
    }

    @Override
    public void sendClearCanvas(int v, int v1, int v2, int v3, String color) throws RemoteException{
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().ClearCanvasFromServer(v, v1, v2, v3, color)));
    }

    public void updateUserList() {
        String[] currentUsers = getUserList();
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateUserListFromServer(currentUsers)));
    }

    /**
     * This method generates an array containing the names of
     * the current users in the server.
     * @return an array of Strings containing the names.
     */
    public String[] getUserList() {
        String[] allUsers = new String[usersList.size()];
        for(int i = 0; i < allUsers.length; i++){
            allUsers[i] = usersList.get(i).getName();
        }
        return allUsers;
    }

    /**
     * This method is used to send this object to the client,
     * for each user to check if the current user has the control,
     * ie have the utilities buttons (Button draw, Button clear ecc...) visible.
     */
    public void checkIfThisUserHasControl(){
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().checkFromServer()));
    }

    private void giveControlToOtherUser(Users user,String username){
//      usersList.forEach(throwingConsumerWrapper(users -> users.getClient().giveControlToOtherUserFromServer(currentUsers)));

        try {
            user.getClient().giveControlToOtherUserFromServer(
                    Arrays.stream(getUserList()).findFirst().orElse(null),getUserList());
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void leaveGame(String userName) throws RemoteException {
        for(Users user : usersList){
            if(user.getName().equals(userName)){
                System.out.println(line + userName + " left the game session\n" + new Date(System.currentTimeMillis()) + "\n" + line);
                usersList.remove(user);
                updateUserList();
                giveControlToOtherUser(user,userName);
                checkIfThisUserHasControl();
                break;
            }
        }
    }

    @Override
    public void updateChat(String userName, String chatMessage) throws RemoteException {
        usersList.forEach(throwingConsumerWrapper(user ->
                user.getClient().messageFromServer(userName, ": " + chatMessage)));
    }

    @Override
    public boolean checkIfUsernameExist(String username) throws RemoteException{
       /* for (String user : getUserList()) {
            if (user.equals(username)) {
                return true;
            }
        }*/
        return false;
    }

    /**
     * This method is used to register the new users joining the server,
     *
     * @param details (String) array holding the details of the user
     *                {username, hostname: "localhost", client Service Name}
     * @throws RemoteException if failed to export the object.
     */
    @Override
    public void registerUsers(String[] details) throws RemoteException {
            System.out.println(new Date(System.currentTimeMillis()));
            System.out.println(details[0] + " has joined the game session");
            System.out.println(details[0] + "'s hostname : " + details[1]);
            System.out.println(details[0] + "'s RMI service : " + details[2]);
            int score = 0;
            try {
                ClientInterface nextClient = (ClientInterface) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
                usersList.add(new Users(details[0], nextClient, score));
                updateUserList();
                checkIfThisUserHasControl();
            }catch (RemoteException | MalformedURLException | NotBoundException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void passIDentity(RemoteRef ref) throws RemoteException {
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}