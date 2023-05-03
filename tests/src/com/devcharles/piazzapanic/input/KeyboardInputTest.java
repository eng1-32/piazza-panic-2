package com.devcharles.piazzapanic.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.Input.Keys;
import com.devcharles.piazzapanic.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matthew Fitzpatrick
 */
@RunWith(GdxTestRunner.class)
public class KeyboardInputTest {

  KeyboardInput kbInput = new KeyboardInput();

  @Test
  public void testKeyDown() {
    kbInput.keyDown(Keys.LEFT);
    assertTrue("If the left key is pressed, left should be set to true",
        kbInput.left);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.A);
    assertTrue("If the A key is pressed, left should be set to true",
        kbInput.left);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.RIGHT);
    assertTrue("If the right key is pressed, right should be set to true",
        kbInput.right);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.D);
    assertTrue("If the D key is pressed, right should be set to true",
        kbInput.right);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.UP);
    assertTrue("If the up key is pressed, up should be set to true",
        kbInput.up);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.W);
    assertTrue("If the w key is pressed, up should be set to true",
        kbInput.up);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.DOWN);
    assertTrue("If the down key is pressed, down should be set to true",
        kbInput.down);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.S);
    assertTrue("If the s key is pressed, down should be set to true",
        kbInput.down);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.F);
    assertTrue("If the f key is pressed, putDown should be set to true",
        kbInput.putDown);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.E);
    assertTrue("If the e key is pressed, changeCooks should be set to true",
        kbInput.changeCooks);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.R);
    assertTrue("If the r key is pressed, pickUp should be set to true",
        kbInput.pickUp);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.Q);
    assertTrue("If the q key is pressed, interact should be set to true",
        kbInput.interact);
    kbInput.clearInputs();
    kbInput.keyDown(Keys.H);
    assertTrue("If the h key is pressed, disableHud should be set to true",
        kbInput.disableHud);
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.H));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_1));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_2));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_3));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_4));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_5));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_6));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_7));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_8));
    kbInput.clearInputs();
    assertTrue("If a correct key is pressed, the function will return true",
        kbInput.keyDown(Keys.NUM_9));
    kbInput.clearInputs();
    assertFalse("If an incorrect key is pressed, the function will return false",
        kbInput.keyDown(Keys.Z));
  }

  @Test
  public void testKeyUp() {
    kbInput.left = true;
    kbInput.keyUp(Keys.LEFT);
    assertFalse("left should be set to false when the left key is not being pressed",
        kbInput.left);
    kbInput.left = true;
    kbInput.keyUp(Keys.A);
    assertFalse("left should be set to false when the a key is not being pressed",
        kbInput.left);
    kbInput.right = true;
    kbInput.keyUp(Keys.RIGHT);
    assertFalse("right should be set to false when the right key is not being pressed",
        kbInput.right);
    kbInput.right = true;
    kbInput.keyUp(Keys.D);
    assertFalse("right should be set to false when the d key is not being pressed",
        kbInput.right);
    kbInput.up = true;
    kbInput.keyUp(Keys.UP);
    assertFalse("up should be set to false when the up key is not being pressed",
        kbInput.up);
    kbInput.up = true;
    kbInput.keyUp(Keys.W);
    assertFalse("up should be set to false when the w key is not being pressed",
        kbInput.up);
    kbInput.down = true;
    kbInput.keyUp(Keys.DOWN);
    assertFalse("down should be set to false when the down key is not being pressed",
        kbInput.down);
    kbInput.down = true;
    kbInput.keyUp(Keys.S);
    assertFalse("down should be set to false when the s key is not being pressed",
        kbInput.down);
    kbInput.changeCooks = true;
    kbInput.keyUp(Keys.E);
    assertFalse("changeCooks should be set to false when the e key is not being pressed",
        kbInput.changeCooks);
    kbInput.putDown = true;
    kbInput.keyUp(Keys.F);
    assertFalse("putDown should be set to false when the f key is not being pressed",
        kbInput.putDown);
    kbInput.pickUp = true;
    kbInput.keyUp(Keys.R);
    assertFalse("pickUp should be set to false when the r key is not being pressed",
        kbInput.pickUp);
    kbInput.interact = true;
    kbInput.keyUp(Keys.Q);
    assertFalse("interact should be set to false when the q key is not being pressed",
        kbInput.interact);
    kbInput.disableHud = true;
    kbInput.keyUp(Keys.H);
    assertFalse("disableHud should be set to false when the h key is not being pressed",
        kbInput.disableHud);
    kbInput.swapNum = 1;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 1 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 2;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 2 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 3;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 3 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 4;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 4 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 5;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 5 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 6;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 6 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 7;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 7 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 8;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 8 key is not being pressed",
        0, kbInput.swapNum);
    kbInput.swapNum = 9;
    kbInput.keyUp(Keys.NUM_1);
    assertEquals("interact should be set to false when the 9 key is not being pressed",
        0, kbInput.swapNum);
    assertTrue("If a valid key is passed into the function, it should return true",
        kbInput.keyUp(Keys.Q));
    assertFalse("If an invalid key is passed into the function, it should return false",
        kbInput.keyUp(Keys.END));

  }
}
