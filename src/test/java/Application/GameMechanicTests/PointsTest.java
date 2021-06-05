package Application.GameMechanicTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Application.Shared.Users;

import static org.junit.jupiter.api.Assertions.*;

public class PointsTest {
    public Users user = new Users();
    @Test
    @DisplayName("Should be equals to 100")
    void ShouldBeEqualsTo100(){
        user.setScore(50);
        int score = user.getScore()+50;
        assertEquals(100, score);
    }

    @Test
    @DisplayName("Should not be equal to 175")
    void ShouldNotBeEqualsTo175(){
        user.setScore(0);
        int score = 0;
        for (int i = 0; i < 3; i++) {
            score = user.getScore()+50;
        }
        assertNotEquals(175, score);
    }
}
