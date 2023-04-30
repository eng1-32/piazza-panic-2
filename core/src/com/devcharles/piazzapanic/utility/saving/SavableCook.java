package com.devcharles.piazzapanic.utility.saving;

import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.FoodStack;
import com.devcharles.piazzapanic.utility.Mappers;
import java.util.ArrayDeque;

/**
 * @author Alistair Foggin
 */
public class SavableCook {

  public TransformComponent transformComponent;
  public ArrayDeque<SavableFood> foodStack = new ArrayDeque<>();
  public float speedModifier = 1f;

  /**
   * Extract the necessary details from a cook entity
   *
   * @param cookEntity the entity to grab the information from
   * @return the new cook that is savable to json
   */
  public static SavableCook from(Entity cookEntity) {
    if (cookEntity == null) {
      return null;
    }
    SavableCook cook = new SavableCook();
    cook.transformComponent = Mappers.transform.get(cookEntity);
    ControllableComponent controllable = Mappers.controllable.get(cookEntity);
    cook.setSavableFoodStackFromEntities(controllable.currentFood);
    cook.speedModifier = controllable.speedModifier;
    return cook;
  }

  private void setSavableFoodStackFromEntities(FoodStack stack) {
    for (Entity foodEntity : stack) {
      foodStack.push(SavableFood.from(foodEntity));
    }
  }
}
