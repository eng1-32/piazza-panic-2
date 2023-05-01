package com.devcharles.piazzapanic.utility.saving;

import com.devcharles.piazzapanic.utility.GdxTimer;

/**
 * @author Alistair Foggin
 */
public class SavableTimer {

  public int delay;
  public int elapsed;
  public boolean running;
  public boolean looping;

  /**
   * Copy the GdxTimerValues into a form without any constructor parameters
   *
   * @param gdxTimer the timer to copy values from
   * @return the serializable timer
   */
  public static SavableTimer from(GdxTimer gdxTimer) {
    SavableTimer timer = new SavableTimer();
    timer.delay = gdxTimer.getDelay();
    timer.elapsed = gdxTimer.getElapsed();
    timer.running = gdxTimer.isRunning();
    timer.looping = gdxTimer.isLooping();
    return timer;
  }

  /**
   * Convert this back to a GdxTimer
   *
   * @return the GdxTimer with functionality
   */
  public GdxTimer toGdxTimer() {
    GdxTimer newTimer = new GdxTimer(delay, running, looping);
    newTimer.setElapsed(elapsed);
    return newTimer;
  }
}
