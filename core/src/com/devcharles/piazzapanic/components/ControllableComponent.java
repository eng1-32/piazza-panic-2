package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.devcharles.piazzapanic.utility.FoodStack;

/**
 * @author Andrey Samoilov
 * @author Alistair Foggin
 */
public class ControllableComponent implements Component {

  public FoodStack currentFood = new FoodStack();
  public float speedModifier = 1f;
}
