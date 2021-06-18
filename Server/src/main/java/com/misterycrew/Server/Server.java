package com.misterycrew.Server;

import com.misterycrew.Shared.ClientInterface;
import com.misterycrew.Shared.ServerInterface;
import com.misterycrew.Shared.Users;

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

    private static int round = 1;
    private int interval = 91;
    private final String line = "<<=========================================>>\n";
    private static final long serialVersionUID = 1L;
    private static final CopyOnWriteArrayList<Users> usersList = new CopyOnWriteArrayList<>();
    private int indexWord = 0;
    private int oldValue;
    private final HashMap<String, Integer> map = new HashMap<>();
    private final List<String> list = new ArrayList<>();
    private int members;
    private final int totRounds = 5;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private int locked = 0;

    /**
     * Constructor for the Server class inheriting the super() class
     * used to create and export a new {@link UnicastRemoteObject} object using an anonymous port.
     *
     * @throws RemoteException if failed to export the object.
     */
    public Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws ServerFailedToStartException {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "distributedService";

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ServerInterface hello = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("RMI Server is running...");

        } catch (Exception e) {
            System.out.println("Server had problems starting");
            throw new ServerFailedToStartException();
        }
    }

    /**
     * Starts the RMI registry on port 1099 (default port).
     */
    public static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("The RMI Server is ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * getter used to retrieve the current amount of members in the game session.
     *
     * @return (Integer) variable holding the amount of members.
     */
    public int getMembers() {
        return members;
    }

    @Override
    public int returnCurrentUsers() {
        return usersList.size();
    }

    /**
     * This method is used to send the actual drawing to each of the
     * users in the server by calling the drawingFromServer() method from the client.
     * <p>
     * For further information see also: {@link ClientInterface#drawingFromServer(double, double, double, double, String)} method.
     *
     * @param x1    (double)
     * @param y1    (double)
     * @param x     (double)
     * @param y     (double)
     * @param color (String) variable holding the color.
     */
    @Override
    public void sendDrawing(Double x1, Double y1, double x, double y, String color) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().drawingFromServer(x1, y1, x, y, color)));
    }

    /**
     * This method is used to erase part of the drawing in the canvas to
     * the other users in the games session by the user who has the control enabled.
     * <p>
     * For further information see also: {@link ClientInterface#clearFromServer(double, double, int, int, String)} method.
     *
     * @param x     (double) coordinate x
     * @param y     (double) coordinate y
     * @param n     (int) coordinate n
     * @param m     (int) coordinate m
     * @param color String variable holding the color.
     */
    @Override
    public void sendClear(double x, double y, int n, int m, String color) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().clearFromServer(x, y, n, m, color)));
    }

    /**
     * This method is used to update for each of the users in the server the round
     * by calling the updateRoundFromServer() method.
     * <p>
     * For further information see also: {@link ClientInterface#updateRoundFromServer(int)} method.
     */
    @Override
    public void updateRound() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateRoundFromServer(round)));
    }

    /**
     * This method is used to send to each of the users in the server the
     * updated round, by calling the sendRoundFromServer() method.
     * <p>
     * For further information see also: {@link ClientInterface#sendRoundFromServer(int)} ()} method.
     *
     * @param round the actual round updated.
     */
    public void sendRound(int round) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().sendRoundFromServer(round)));
    }

    /**
     * This method is used to reset to 0 the lock variable
     * to each of the users in the server.
     * <p>
     * For further information see also: {@link ClientInterface#resetFromServer()} method.
     */
    private void resetIndexGivePointsMethod() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().resetFromServer()));
    }

    /**
     * This method is used by the Points class to
     */
    @Override
    public void incrementPointsAmount() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().incrementPointsAmountFromServer()));
    }

    /**
     * This method is used to send to every user in the server the
     * clearChatFromServer() method.
     *
     * For further information see also: {@link ClientInterface#clearChatFromServer()} method.
     */
    public void clearChat(){
        usersList.forEach(throwingConsumerWrapper(
                user -> user.getClient().clearChatFromServer()
        ));
    }

    /**
     * This method is used to set the timer for each user in the server,
     * and perform different actions each second per 90 seconds.
     */
    @Override
    public void setTimerGame() {
        scheduler.scheduleAtFixedRate(() -> {
            if (interval == 0) {
                sendClearCanvas(0, 0, 690, 620, "white");
                if (round == totRounds) {
                    pickWinner();
                    clearChat();
                    round = 1;
                    usersList.clear();
                    map.clear();
                    list.clear();
                    scheduler.shutdownNow();
                } else {
                    round++;
                    sendRound(round);
                    showNextWordToGuess();
                    if (round > 1 && returnCurrentUsers() > 1) {
                        pickPlayerToDraw();
                    }
                    checkIfThisUserHasControl();
                    resetIndexGivePointsMethod();
                }
                interval = 91;
            } else {
                interval--;
                usersList.forEach(throwingConsumerWrapper(
                        user -> user.getClient().setCountDownFromServer(interval)
                ));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * getter used to retrieve the interval variable.
     *
     * @return (int) variable count down number.
     */
    public int getInterval() {
        return interval;
    }

    /**
     * This method is used to update the count down variable for each user
     * in the server.
     */
    public void updateCountDownVariable() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateCountDownVariableFromServer(interval)));
    }

    /**
     * This method is used to disable for each user the control,
     * in this case we dont have to keep track of the previous user who had
     * the control to draw.
     * <p>
     * For further information see also: {@link ClientInterface#disableForEveryoneFromServer(String[])}
     */
    private void disableForEveryone() {
        usersList.forEach(throwingConsumerWrapper(user ->
                user.getClient().disableForEveryoneFromServer(getUserList())));
    }

    /**
     * This method is used to get the score and the username,
     * from the Points class, and then we put it into a {@link HashMap}.
     *
     * @param scoreUser the score of the user passed as a value to the HashMap.
     * @param nameUser  the username passed as a key to the HashMap.
     */
    @Override
    public void getScoreAndUsername(int scoreUser, String nameUser) {
        map.put(nameUser, scoreUser);
    }

    /**
     * This method is used to sort the map by value to get the user with most points.
     * therefore we get the user at index 0 ie the user with most points.
     */
    public void pickWinner() {
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(k -> list.add(k.getKey() + " with " + k.getValue()+ " points"));
        if (returnCurrentUsers() > 1) {
            usersList.forEach(throwingConsumerWrapper(user -> user.getClient().pickWinnerFromServer(list.get(0))));
        }
    }

    /**
     * This method is used to update the word index for each of the users in the server.
     * For further information see also: {@link ClientInterface#updateIndexWordFromServer(int)}
     * method.
     */
    @Override
    public void updateIndexWord() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateIndexWordFromServer(indexWord)));
    }

    /**
     * This method is used to
     * 1) update the index of the word to show
     * 2) Send to the other users the word.
     * For further information see also: {@link ClientInterface#showNextWordToGuessFromServer()} method.
     */
    private void showNextWordToGuess() {
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
     * <p>
     * disableForEveryone() -> used to disable the control to everyone,
     * in this way we dont have to check who has the
     * control i.e. (ability to draw).
     */
    private void pickPlayerToDraw() {
        disableForEveryone();
        Random rand = new Random();
        int next = rand.nextInt(usersList.size());

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

    /**
     * This method is used to clear the canvas to the other users in the games session
     * by the user who has the control enabled.
     * for further information see also: {@link ClientInterface#ClearCanvasFromServer(int, int, int, int, String)}
     * method.
     *
     * @param v     (Integer) coordinate V
     * @param v1    (Integer) coordinate V1
     * @param v2    (Integer) coordinate V2
     * @param v3    (Integer) coordinate V3
     * @param color (String) variable holding the color.
     */
    @Override
    public void sendClearCanvas(int v, int v1, int v2, int v3, String color) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().ClearCanvasFromServer(v, v1, v2, v3, color)));
    }

    /**
     * This method is used to update the leaderboard of each
     * user in the server.
     * for further information see also: {@link ClientInterface#updateUserListFromServer(String[])}
     */
    public void updateUserList() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().updateUserListFromServer(getUserList())));
    }

    /**
     * This method generates an array containing the names of
     * the current users in the server.
     *
     * @return an array of Strings containing the names.
     */
    public String[] getUserList() {
        String[] allUsers = new String[usersList.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = usersList.get(i).getName();
        }
        return allUsers;
    }

    /**
     * This method is used to send this object to the client,
     * for each user to check if the current user has the control,
     * ie have the utilities buttons (Button draw, Button clear ecc...) visible.
     * <p>
     * for further information see also: {@link ClientInterface#checkFromServer()}
     */
    public void checkIfThisUserHasControl() {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().checkFromServer()));
    }

    /**
     * This method is used when a user leaves the game session,
     * to give the control to the user at index 0 ie. the first user
     * in the list, if the list is >= usersList.size().
     * <p>
     * for further information see also: {@link ClientInterface#giveControlToOtherUserFromServer()}
     */
    private void giveControlToOtherUser() {
        try {
            if (returnCurrentUsers() >= 1) {
                usersList.get(0).getClient().giveControlToOtherUserFromServer();
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is called when a user leaves the game session.
     * By looping through the usersList and comparing the username
     * given as a parameter and the user from the list, then we just
     * print out on the server the "username" left the game session plus the Date.
     * Furthermore a check is being made to verify if the user has also
     * the control in that case we give the control to someone else.
     *
     * @param userName   (String) holding the username to check.
     * @param hasControl (boolean) which hols if the user has the control.
     */
    @Override
    public void leaveGame(String userName, boolean hasControl) {
        --members;
        for (Users user : usersList) {
            if (user.getName().equals(userName)) {
                System.out.println(line + userName + " left the game session\n" + new Date(System.currentTimeMillis()) + "\n" + line);
                usersList.remove(user);
                if (hasControl) {
                    giveControlToOtherUser();
                }
                updateUserList();
                checkIfThisUserHasControl();
                if (returnCurrentUsers() == 0){
                    locked = 1;
                }
                break;
            }
        }
    }

    /**
     * This method is used to update the game chat by sending for each user the
     * messageFromServer() method.
     * For further Information see also: {@link ClientInterface#messageFromServer(String, String)}
     * method.
     *
     * @param userName    (String) containing the username of the user.
     * @param chatMessage (String) containing the message to be send.
     */
    @Override
    public void updateChat(String userName, String chatMessage) {
        usersList.forEach(throwingConsumerWrapper(user -> user.getClient().messageFromServer(userName, ": " + chatMessage)));
    }

    /**
     * getter used to retrieve the total of rounds.
     *
     * @return (int) variable holding the total of rounds.
     */
    public int getTotRounds() {
        return totRounds;
    }

    /**
     * Getter used to retrieve the state of the lock (ie 0 or 1) for
     * the timer when all the user leave the game
     * session before the game is over. By setting it to 1
     * we make sure that if joins a new user in the same game session
     * it will not call again the setTimerGame() method on top of the
     * already existing one.
     *
     * @return (Integer) state of the lock 1 or 0
     */
    public int getLocked() {
        return locked;
    }

    /**
     * This method is used to register the new users joining the server,
     *
     * @param details (String) array holding the details of the user
     *                {username, hostname: "localhost", client Service Name}
     */
    @Override
    public void registerUsers(String[] details) {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the game session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'s RMI service : " + details[2]);
        ++members;
        int score = 0;
        try {
            ClientInterface nextClient = (ClientInterface) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
            usersList.add(new Users(details[0], nextClient, score));
            updateUserList();
            checkIfThisUserHasControl();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to just print out on the server the remote
     * reference of the remote object.
     *
     * @param ref represents the handle for a remote object.
     */
    @Override
    public void passIDentity(RemoteRef ref) {
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to handle the exceptions thrown by
     * the remote objects in a elegant way through lambdas used
     * in this class.
     *
     * @param throwingConsumer ThrowingConsumer is a functional interface that can be used to implement any generic
     *                         block of code.
     * @param <T>              generic type
     * @return (Consumer) is a functional interface; it takes an argument and returns nothing.
     */
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