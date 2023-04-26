package com.devcharles.piazzapanic.utility;

public class Difficulty {

  public enum Level {
    EASY,
    MEDIUM,
    HARD,
  }

  public static Difficulty createDifficulty(Level difficultyLevel) {
    Difficulty difficulty = new Difficulty();
    switch (difficultyLevel) {
      case EASY:
        difficulty.initialChefs = 3;
        difficulty.maxGroupSize = 1;
        difficulty.customerDelay = 35000;
        difficulty.customerPatience = 110000;
        break;
      case MEDIUM:
        difficulty.initialChefs = 2;
        difficulty.maxGroupSize = 2;
        difficulty.customerDelay = 30000;
        difficulty.customerPatience = 90000;
        break;
      case HARD:
        difficulty.initialChefs = 1;
        difficulty.maxGroupSize = 3;
        difficulty.customerDelay = 25000;
        difficulty.customerPatience = 70000;
        break;
    }
    return difficulty;
  }

  // TODO: manage initial number of chefs
  public int initialChefs;
  public int maxGroupSize;
  public int customerDelay;
  public int customerPatience;
}
