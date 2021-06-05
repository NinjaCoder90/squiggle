package Application.GameMechanic;

import Application.ClientPaneFX;
import Application.Shared.ClientInterface;
import Application.Shared.Users;

import java.util.Objects;

public class Points {

    ClientPaneFX gameGui;

    public Points(ClientPaneFX gameGui) {
        this.gameGui = gameGui;
    }

    //POINTS:
    //1 --> 50pts
    //2 --> 40pts
    //3 --> 35pts
    //4,5,... --> 0pts
    public void validateGuessGivePoints() {
        if (gameGui.lock == 0 && !gameGui.btnDraw.isVisible()) {
            if (gameGui.chatField.getText().compareToIgnoreCase(Objects.requireNonNull(gameGui.wordToGuessList.get(gameGui.count))) == 0) {
                gameGui.chatField.setText("word guessed");
                Users.setScore(Users.getScore() + 50);
                gameGui.scoreLabel.setText("Your Points: " + Users.getScore());
                gameGui.lock = 1;
            }
        }
    }
}
