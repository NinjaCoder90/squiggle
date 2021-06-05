package Application.SharedTests;

import Application.Shared.ClientInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import Application.Shared.Users;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("Should return the correct score")
    void ShouldReturnTheCorrectScore(){
        Users u1 = new Users();
        u1.setScore(50);
        assertEquals(50, u1.getScore());
    }

    @Test
    @DisplayName("Should return the correct user")
    void ShouldReturnTheCorrectUser(){
        ClientInterface client = null;
        Users u1 = new Users("Marco", client, 50);
        Users u2 = new Users("Luca", client, 100);

        assertEquals("Marco", u1.getName());
        assertEquals("Luca", u2.getName());
        assertNotEquals("Luca", u1.getName());
        assertNotEquals("Marco", u2.getName());
        assertEquals(50, u1.getScore());
        assertEquals(100, u2.getScore());
    }
}
