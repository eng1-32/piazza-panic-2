package com.devcharles.piazzapanic.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;

/**
 * @author Ross Holmes
 * @author Andrey Samoilov
 */
public class FoodComponent implements Component {

  public FoodType type;
  Boolean isBurned = false;

  public enum FoodType {
    // These ids correspond to the order of the food in the sprite!
    unformedPatty(1),
    formedPatty(2),
    grilledPatty(3),
    buns(4),
    toastedBuns(5),
    burger(6),
    lettuce(7),
    slicedLettuce(8),
    tomato(9),
    slicedTomato(10),
    onion(11),
    slicedOnion(12),
    salad(13),
    potato(14),
    cookedPotato(15),
    jacketPotato(16),
    butter(17),
    unformedDough(18),
    formedDough(19),
    doughTomatoBase(20),
    uncookedPizza(21),
    pizza(22),
    tomatoBase(23),
    cheese(24),
    slicedCheese(25),
    burnt(26);


    private final int value;

    FoodType(int id) {
      this.value = id;
    }

    public int getValue() {
      return value;
    }

    private static final Map<Integer, FoodType> _map = new HashMap<>();

    static {
      for (FoodType stationType : FoodType.values()) {
        _map.put(stationType.value, stationType);
      }
    }

    /**
     * Get type from id
     *
     * @param value id value
     * @return Enum type
     */
    public static FoodType from(int value) {
      return _map.get(value);
    }
  }

  public void setBurned(Boolean value) {
    isBurned = value;
  }

  public Boolean getIsBurned() {
    return isBurned;
  }

}
