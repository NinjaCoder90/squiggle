package com.misterycrew.Application.GameMechanic;

import com.misterycrew.Application.ClientPaneStartFX;
import com.misterycrew.Shared.ServerInterface;
import com.misterycrew.Shared.Users;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This class is used to assign
 * and keep trace of the points.
 */
public class Points implements Serializable {

    private static final long serialVersionUID = 4215060926591614406L;
    ClientPaneStartFX gameGui;

    /**
     * Constructor of the Points class.
     *
     * @param gameGui object class of ClientPaneStartFX.
     */
    public Points(ClientPaneStartFX gameGui) {
        this.gameGui = gameGui;
    }

    /**
     * This method is used to send to the server the
     * username and the score of the user, in order to store it for
     * choosing the winner at the end of the game.
     */
    private void sendScoreAndUsername() {
        try {
            gameGui.client.serverInterface.getScoreAndUsername(Users.getScore(), gameGui.getUserName().getText());
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to increment a variable
     * used to determine the amount of points that
     * the user might earn.
     * <p>
     * For further information see also: {@link ServerInterface#incrementPointsAmount()}
     */
    private void incrementPointsAmounts() {
        try {
            gameGui.client.serverInterface.incrementPointsAmount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Whenever a word is guessed, this method gives
     * points to the players (max.3) who guessed it.
     */
    public void validateGuessGivePoints() {
        if (gameGui.getLock() == 0 && !gameGui.getBtnDraw().isVisible()) {
            if (gameGui.getChatField().getText().compareToIgnoreCase(gameGui.getWordToGuessList().get(gameGui.count)) == 0) {
                gameGui.getChatField().setText("word guessed");
                if (gameGui.a == 0) {
                    Users.setScore(Users.getScore() + 49);
                } else if (gameGui.a == 1) {
                    Users.setScore(Users.getScore() + 39);
                } else if (gameGui.a == 2) {
                    Users.setScore(Users.getScore() + 33);
                }
                sendScoreAndUsername();
                gameGui.getScoreLabel().setText("Your Points: " + Users.getScore());
                gameGui.setLock(1);
                incrementPointsAmounts();
            }
        }
    }
}
