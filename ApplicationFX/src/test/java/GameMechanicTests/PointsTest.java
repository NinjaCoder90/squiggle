package GameMechanicTests;

import com.misterycrew.Shared.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class PointsTest {

    @Test
    @DisplayName("Should return the correct score")
    void ShouldReturnTheCorrectScore(){
        Users u1 = new Users("Marco", 50);
        Users.setScore(50);
        assertEquals(50, u1.getOverloadScore());
    }

    @Test
    @DisplayName("Should return the correct user and score")
    void ShouldReturnTheCorrectUserAndScore(){
        Users u1 = new Users("Marco", 50);
        Users u2 = new Users("Luca",100);

        assertEquals("Marco", u1.getOverloadName());
        assertEquals("Luca", u2.getOverloadName());

        assertNotEquals("Luca", u1.getOverloadName());
        assertNotEquals("Marco", u2.getOverloadName());

        assertEquals(50, u1.getOverloadScore());
        assertEquals(100, u2.getOverloadScore());
    }
}
