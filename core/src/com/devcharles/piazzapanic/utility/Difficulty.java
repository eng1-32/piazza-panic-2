package com.devcharles.piazzapanic.utility;

public class Difficulty {

  public enum Level {
    EASY,
    MEDIUM,
    HARD,
  }

  public Difficulty(Level difficultyLevel) {
    switch (difficultyLevel) {
      case EASY:
        initialChefs = 3;
        maxGroupSize = 1;
        customerDelay = 35000;
        customerPatience = 110000;
        break;
      case MEDIUM:
        initialChefs = 2;
        maxGroupSize = 2;
        customerDelay = 30000;
        customerPatience = 90000;
        break;
      case HARD:
        initialChefs = 1;
        maxGroupSize = 3;
        customerDelay = 25000;
        customerPatience = 70000;
        break;
    }
  }

  // TODO: manage initial number of chefs
  public int initialChefs;
  public int maxGroupSize;
  public int customerDelay;
  public int customerPatience;
}
