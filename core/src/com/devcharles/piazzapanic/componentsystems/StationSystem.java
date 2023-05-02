package com.devcharles.piazzapanic.componentsystems;

import com.devcharles.piazzapanic.scene2d.Hud;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.TintComponent;
import com.devcharles.piazzapanic.components.CookingComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station;
import com.devcharles.piazzapanic.utility.Station.StationType;

/**
 * This system manages player-station interaction and station food processing.
 *
 * @author Andrey Samoilov
 * @author Alistair Foggin
 * @author Matthew Fitzpatrick
 */
public class StationSystem extends IteratingSystem {


  EntityFactory factory;

  private TintComponent readyTint;
  private TintComponent burnedTint;
  private final Hud hud;
  private float tickAccumulator = 0;
  private final Integer[] reputationAndMoney;

  public StationSystem(EntityFactory factory, Integer[] reputationAndMoney, Hud hud) {
    super(Family.all(StationComponent.class).get());
    this.factory = factory;
    this.reputationAndMoney = reputationAndMoney;
    this.hud = hud;
  }

  @Override
  public void update(float deltaTime) {
    tickAccumulator += deltaTime;
    super.update(deltaTime);
    if (tickAccumulator > 0.5f) {
      tickAccumulator -= 0.5f;
    }
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    StationComponent station = Mappers.station.get(entity);

    stationTick(entity, station, deltaTime);

    if (station.interactingCook != null) {

      PlayerComponent player = Mappers.player.get(station.interactingCook);

      if (player == null) {
        return;
      }

      if (player.putDown && !station.isLocked) {
        player.putDown = false;

        ControllableComponent controllable = Mappers.controllable.get(station.interactingCook);

        switch (station.type) {
          case ingredient:
            controllable.currentFood.pushItem(factory.createFood(station.ingredient),
                station.interactingCook);
            break;
          case bin:
            processBin(controllable);
            break;

          case serve:
            processServe(station.interactingCook);
            break;

          default:
            processStation(controllable, station);
            break;
        }
      } else if (player.pickUp && !station.isLocked) {
        player.pickUp = false;

        ControllableComponent controllable = Mappers.controllable.get(station.interactingCook);

        switch (station.type) {
          case ingredient:
            controllable.currentFood.pushItem(factory.createFood(station.ingredient),
                station.interactingCook);
            break;
          case bin:
          case serve:
            break;
          default:
            stationPickup(entity, station, controllable);
            break;
        }
      } else if (player.interact) {
        player.interact = false;
        if (station.isLocked) {
          tryStationUnlock(station);
          player.pickUp = false;
          player.putDown = false;
        } else {
          interactStation(station);
        }
      }
    }
  }

  /**
   * Try and process the food from the player.
   */
  void processStation(ControllableComponent controllable, StationComponent station) {

    if (controllable.currentFood.isEmpty()) {
      return;
    }

    Gdx.app.log("putDown", Mappers.food.get(controllable.currentFood.peek()).type.name());

    FoodComponent food = Mappers.food.get(controllable.currentFood.peek());

    HashMap<FoodType, FoodType> recipes = Station.recipeMap.get(station.type);

    if (recipes == null) {
      return;
    }

    FoodType result = recipes.get(food.type);

    if (result == null) {
      return;
    }

    int foodIndex = station.food.indexOf(null);

    // If there is space on the station
    if (foodIndex != -1) {
      // Pop if off player stack
      // Store in station
      station.food.set(foodIndex, controllable.currentFood.pop());
    } else {
      return;
    }

    // success

    CookingComponent cooking = getEngine().createComponent(CookingComponent.class);
    if (food.type == FoodType.unformedPatty || food.type == FoodType.unformedDough) {
      cooking.timer.setDelay((int) (cooking.timer.getDelay() / station.prepModifier));
    } else if (food.type == FoodType.onion || food.type == FoodType.lettuce
        || food.type == FoodType.tomato || food.type == FoodType.cheese) {
      cooking.timer.setDelay((int) (cooking.timer.getDelay() / station.chopModifier));
    }

    if (station.type == StationType.oven) {
      cooking.timer.setDelay(15000);
      cooking.processed = true;
    }
    cooking.timer.start();

    station.food.get(foodIndex).add(cooking);

    Gdx.app.log("Food processed", String.format("%s turned into %s", food.type, result));

  }

  /**
   * Perform special action (flipping patties, etc.)
   *
   * @param station the station the action is being performed on.
   */
  void interactStation(StationComponent station) {
    for (Entity food : station.food) {
      if (food == null || !Mappers.cooking.has(food)) {
        continue;
      }

      CookingComponent cooking = Mappers.cooking.get(food);

      // Check if it's ready without ticking the timer
      boolean ready = cooking.timer.tick(0);

      if (ready && !cooking.processed) {
        food.remove(TintComponent.class);
        cooking.processed = true;
        cooking.timer.reset();
        return;
      }
    }
  }

  /**
   * Try to combine the ingredients at the top of the player's inventory stack (max 3) into a ready
   * meal.
   *
   * @param cook the cook whos inventory is being used for creating the food.
   */
  void processServe(Entity cook) {
    ControllableComponent controllable = Mappers.controllable.get(cook);

    if (controllable.currentFood.size() < 2) {
      return;
    }

    int count = 2;
    FoodType result = tryServe(controllable, count);

    if (result == null) {
      result = tryServe(controllable, ++count);
      if (result == null) {
        return;
      }
    }

    for (int i = 0; i < count; i++) {
      Entity e = controllable.currentFood.pop();
      getEngine().removeEntity(e);
    }

    controllable.currentFood.pushItem(factory.createFood(result), cook);

  }

  /**
   * Attempt to create food from a set of ingredients.
   *
   * @param count number of ingredients to combine
   */
  FoodType tryServe(ControllableComponent controllable, int count) {
    Set<FoodType> ingredients = new HashSet<>();
    FoodType[] twoIngredients = {FoodType.grilledPatty, FoodType.toastedBuns,
        FoodType.cookedPotato, FoodType.slicedCheese};
    FoodType[] threeIngredients = {FoodType.slicedLettuce, FoodType.slicedTomato,
        FoodType.slicedOnion, FoodType.formedDough, FoodType.tomato, FoodType.cheese};
    int i = 0;
    for (Entity foodEntity : controllable.currentFood) {
      if (i >= count) {
        break;
      }
      FoodComponent check = Mappers.food.get(foodEntity);
      if (count == 2) {
        if (!check.getIsBurned() && Arrays.asList(twoIngredients).contains(check.type)) {
          ingredients.add(Mappers.food.get(foodEntity).type);
        }
      } else if (count == 3) {
        if (!check.getIsBurned() && Arrays.asList(threeIngredients).contains(check.type)) {
          ingredients.add(Mappers.food.get(foodEntity).type);
        }
      }

      i++;
    }

    return Station.serveRecipes.get(ingredients);
  }

  /**
   * Destroy the top food in the inventory of a cook.
   */
  void processBin(ControllableComponent controllable) {
    if (controllable.currentFood.isEmpty()) {
      return;
    }

    Entity e = controllable.currentFood.pop();
    getEngine().removeEntity(e);
  }

  /**
   * Pick up ready food from a station
   */
  void stationPickup(Entity station, StationComponent stationComponent,
      ControllableComponent controllable) {
    for (Entity foodEntity : stationComponent.food) {
      if (foodEntity != null) {
        FoodComponent foodComponent = Mappers.food.get(foodEntity);
        FoodType[] compare = Station.recipeMap.get(stationComponent.type).values()
            .toArray(new FoodType[0]);
        if (Arrays.asList(compare).contains(foodComponent.type) || foodComponent.getIsBurned()) {
          if (controllable.currentFood.pushItem(foodEntity, stationComponent.interactingCook)) {
            stationComponent.food.set(stationComponent.food.indexOf(foodEntity), null);
            foodEntity.remove(CookingComponent.class);
            Mappers.transform.get(foodEntity).scale.set(1, 1);
            Gdx.app.log("Picked up", Mappers.food.get(foodEntity).type.toString());
          }
          return;
        }

        if (Mappers.tint.has(station)) {
          station.remove(TintComponent.class);
        }
      }
    }
  }

  /**
   * Cook the food in the station. This progresses the timer in the food being cooked in the
   * station.
   *
   * @param station
   * @param stationComponent
   * @param deltaTime
   */
  void stationTick(Entity station, StationComponent stationComponent, float deltaTime) {
    if (stationComponent.type == StationType.cutting_board
        && stationComponent.interactingCook == null) {
      return;
    }

    boolean stationHasToggledTint = false;
    for (Entity foodEntity : stationComponent.food) {

      if (foodEntity == null || !Mappers.cooking.has(foodEntity)) {
        continue;
      }

      CookingComponent cooking = Mappers.cooking.get(foodEntity);
      FoodComponent foodComponent = Mappers.food.get(foodEntity);
      FoodType[] compare = Station.recipeMap.get(stationComponent.type).values()
          .toArray(new FoodType[0]);

      boolean ready = cooking.timer.tick(deltaTime);

      if (ready && cooking.processed && stationComponent.type == StationType.oven) {
        if (tickAccumulator > 0.5f) {
          if (!stationHasToggledTint) {
            stationHasToggledTint = true;
            System.out.println("tint toggled");
            if (!Mappers.tint.has(station)) {
              station.add(readyTint);
            } else {
              station.remove(TintComponent.class);
            }
          }
        }
      }
      if (ready && cooking.processed && !Arrays.asList(compare).contains(foodComponent.type)) {
        // Process the food into it's next form
        foodComponent.type = Station.recipeMap.get(stationComponent.type).get(foodComponent.type);
        Mappers.texture.get(foodEntity).region = EntityFactory.getFoodTexture(foodComponent.type);
        Gdx.app.log("Food ready", foodComponent.type.name());
      } else if (ready && !cooking.processed) {

        if (tickAccumulator > 0.5f) {

          if (!Mappers.tint.has(foodEntity)) {
            foodEntity.add(readyTint);
          } else {
            foodEntity.remove(TintComponent.class);
          }
        }

      }
      if (stationComponent.type != StationType.cutting_board) {
        hasBurned(foodEntity, cooking, foodComponent);
      }
    }
  }

  void hasBurned(Entity foodEntity, CookingComponent cooking, FoodComponent food) {
    if (cooking.timer.getElapsed() > (cooking.timer.getDelay() * 2.5)) {
      food.setBurned(true);
      cooking.timer.stop();
      cooking.timer.reset();
      foodEntity.remove(CookingComponent.class);
      foodEntity.add(burnedTint);
    }
  }

  void tryStationUnlock(StationComponent stationComponent) {
    if (reputationAndMoney[1] >= 50) {
      stationComponent.isLocked = false;
      reputationAndMoney[1] -= 50;
      hud.updateShop();
    }
  }

  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    readyTint = getEngine().createComponent(TintComponent.class);
    readyTint.tint = Color.ORANGE;
    burnedTint = getEngine().createComponent(TintComponent.class);
    burnedTint.tint = Color.BLACK;
  }

}
