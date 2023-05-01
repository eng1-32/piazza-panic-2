package com.devcharles.piazzapanic.utility.saving;

import static org.junit.Assert.*;

import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.utility.GdxTimer;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableTimerTest {

  @Test
  public void testFrom() {
    GdxTimer timer = new GdxTimer(25000, true, false);
    timer.tick(1.50f);
    SavableTimer savableTimer = SavableTimer.from(timer);
    assertEquals("The delay should be as expected", 25000, savableTimer.delay);
    assertEquals("The elapsed time should be as expected", 1500, savableTimer.elapsed);
    assertTrue("The timer should be running", savableTimer.running);
    assertFalse("The timer should not be looping", savableTimer.looping);
  }

  @Test
  public void testToGdxTimer() {
    SavableTimer savableTimer = new SavableTimer();
    savableTimer.delay = 25000;
    savableTimer.elapsed = 1500;
    savableTimer.running = true;
    savableTimer.looping = false;
    GdxTimer timer = savableTimer.toGdxTimer();
    assertEquals("The delay should be as expected", 25000, timer.getDelay());
    assertEquals("The elapsed time should be as expected", 1500, timer.getElapsed());
    assertTrue("The timer should be running", timer.isRunning());
    assertFalse("The timer should not be looping", timer.isLooping());
  }
}