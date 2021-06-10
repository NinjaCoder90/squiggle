package com.misterycrew.Application.GameMechanic;

import com.misterycrew.Application.ClientPaneFX;
import com.misterycrew.Shared.ClientInterface;
import com.misterycrew.Shared.ServerInterface;
import com.misterycrew.Shared.Users;

import java.rmi.RemoteException;

/**
 * This class is used to assign
 * and keep trace of the points.
 */
public class Points {

    ClientPaneFX gameGui;

    /**
     * Costructor of the Points class.
     * @param gameGui object class of ClientPaneFX.
     */
    public Points(ClientPaneFX gameGui) {
        this.gameGui = gameGui;
    }

    /**
     * This method is used to send to the server the
     * username and the score of the user, in order to store it for
     * choosing the winner at the end of the game.
     */
    private void sendScoreAndUsername(){
        try {
            gameGui.client.serverInterface.getScoreAndUsername(Users.getScore(),gameGui.userName.getText());
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to increment a variable
     * used to determine the amount of points that
     * the user might earn.
     *
     * For further information see also: {@link ServerInterface#incrementPointsAmount()}
     */
    private void incrementPointsAmounts(){
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
        if (gameGui.getLock() == 0 && !gameGui.btnDraw.isVisible()){
            if (gameGui.chatField.getText().compareToIgnoreCase(gameGui.wordToGuessList.get(gameGui.count)) == 0){
                gameGui.chatField.setText("word guessed");
                if (gameGui.a == 0) {
                    Users.setScore(Users.getScore() + 49);
                } else if (gameGui.a == 1) {
                    Users.setScore(Users.getScore() + 39);
                } else if (gameGui.a == 2){
                    Users.setScore(Users.getScore() + 33);
                }
                sendScoreAndUsername();
                gameGui.scoreLabel.setText("Your Points: " + Users.getScore());
                gameGui.setLock(1);
                incrementPointsAmounts();
            }
        }
    }
}
