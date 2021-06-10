package com.misterycrew.Application.GameMechanic;

import com.misterycrew.Application.ClientPaneFX;
import com.misterycrew.Shared.Users;

import java.rmi.RemoteException;
import java.util.Objects;

public class Points {

    ClientPaneFX gameGui;

    public Points(ClientPaneFX gameGui) {
        this.gameGui = gameGui;
    }

    private void sendScoreAndUsername(){
        try {
            gameGui.client.serverInterface.getScoreAndUsername(Users.getScore(),gameGui.userName.getText());
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    private void incrementPointsAmounts(){
        try {
            gameGui.client.serverInterface.incrementPointsAmount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void validateGuessGivePoints() {
        if (gameGui.lock == 0 && !gameGui.btnDraw.isVisible()){
            if (gameGui.chatField.getText().compareToIgnoreCase(Objects.requireNonNull(gameGui.wordToGuessList.get(gameGui.count))) == 0){
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
                gameGui.lock = 1;
                incrementPointsAmounts();
            }
        }
    }
}
