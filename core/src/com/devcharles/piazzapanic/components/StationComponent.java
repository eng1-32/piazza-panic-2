package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Engine;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.Station.StationType;

/**
 * @author Andrey Samoilov
 * @author Ross Holmes
 * @author Alistair Foggin
 */
public class StationComponent implements Component {

  public int id = 0;
  public float prepModifier = 1f;
  public float chopModifier = 1f;
  public Entity interactingCook = null;
  public StationType type;
  public ArrayList<Entity> food = new ArrayList<>(
      Arrays.asList(new Entity[]{null, null, null, null}));
  public FoodType ingredient = null;

  /**
   * Copy values from another station component into this one
   *
   * @param otherStation the station to copy the parameters from
   * @param engine       the engine that contains the entities within this component
   * @author Alistair Foggin
   */
  public void copyValues(StationComponent otherStation, Engine engine) {
    id = otherStation.id;
    interactingCook = otherStation.interactingCook;
    type = otherStation.type;
    for (int i = 0; i < food.size(); i++) {
      Entity foodEntity = food.get(i);
      if (foodEntity != null) {
        engine.removeEntity(foodEntity);
      }
      food.set(i, otherStation.food.get(i));
    }
    ingredient = otherStation.ingredient;
  }
}
