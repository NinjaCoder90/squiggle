package Application.GameMechanic;

import Application.ClientPaneFX;
import Application.Shared.Users;

import java.rmi.RemoteException;
import java.util.Objects;

public class Points {

    ClientPaneFX gameGui;

    public Points(ClientPaneFX gameGui){
        this.gameGui = gameGui;
    }

    public void validateGuessGivePoints() {
        if (gameGui.lock == 0 && !gameGui.btnDraw.isVisible()){
            if (gameGui.chatField.getText().compareToIgnoreCase(Objects.requireNonNull(gameGui.wordToGuessList.get(gameGui.count))) == 0){
                gameGui.chatField.setText("word guessed");
                Users.setScore(Users.getScore() + 50);
                try {
                    gameGui.client.serverInterface.getScoreAndUsername(Users.getScore(),gameGui.userName.getText());
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
                gameGui.scoreLabel.setText("Your Points: " + Users.getScore());
                gameGui.lock = 1;
            }
        }
    }
}
