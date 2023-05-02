package com.devcharles.piazzapanic.utility;

/**
 * @author Alistair Foggin
 */
public class Difficulty {

  public enum Level {
    EASY,
    MEDIUM,
    HARD,
  }

  /**
   * Create a difficulty level without needing constructor parameters, so it is serializable
   *
   * @param difficultyLevel The difficulty level
   * @return the difficulty settings for the specified level
   */
  public static Difficulty createDifficulty(Level difficultyLevel) {
    Difficulty difficulty = new Difficulty();
    switch (difficultyLevel) {
      case EASY:
        difficulty.initialCooks = 3;
        difficulty.maxGroupSize = 1;
        difficulty.customerDelay = 35000;
        difficulty.customerPatience = 110000;
        break;
      case MEDIUM:
        difficulty.initialCooks = 2;
        difficulty.maxGroupSize = 2;
        difficulty.customerDelay = 30000;
        difficulty.customerPatience = 90000;
        break;
      case HARD:
        difficulty.initialCooks = 1;
        difficulty.maxGroupSize = 3;
        difficulty.customerDelay = 25000;
        difficulty.customerPatience = 70000;
        break;
    }
    return difficulty;
  }

  // TODO: manage initial number of chefs
  public int initialCooks;
  public int maxGroupSize;
  public int customerDelay;
  public int customerPatience;
}
