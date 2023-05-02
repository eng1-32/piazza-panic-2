package com.devcharles.piazzapanic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.ItemComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.componentsystems.CarryItemsSystem;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.InventoryUpdateSystem;
import com.devcharles.piazzapanic.componentsystems.LightingSystem;
import com.devcharles.piazzapanic.componentsystems.PhysicsSystem;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.componentsystems.PowerUpSystem;
import com.devcharles.piazzapanic.componentsystems.RenderingSystem;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.Difficulty;
import com.devcharles.piazzapanic.utility.Difficulty.Level;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.saving.GameState;
import com.devcharles.piazzapanic.utility.saving.SavableCook;
import com.devcharles.piazzapanic.utility.saving.SavableFood;

/**
 * @author Alistair Foggin
 */
public class EndlessGameScreen extends BaseGameScreen {

  private Difficulty difficulty;

  public EndlessGameScreen(PiazzaPanic game, String mapPath, boolean loadSave,
      Difficulty difficulty) {
    super(game, mapPath);
    hud.setEndless(true);
    engine.addSystem(new PhysicsSystem(world));
    engine.addSystem(new RenderingSystem(mapLoader.map, game.batch, camera));
    engine.addSystem(new LightingSystem(rayhandler, camera));
    // This can be commented in during debugging.
    // engine.addSystem(new DebugRendererSystem(world, camera));
    engine.addSystem(new PlayerControlSystem(kbInput));
    engine.addSystem(new StationSystem(factory, reputationPointsAndMoney, hud));
    CustomerAISystem aiSystem =
        new CustomerAISystem(mapLoader.getObjectives(), world, factory, hud,
            reputationPointsAndMoney,
            true);
    engine.addSystem(aiSystem);
    engine.addSystem(new CarryItemsSystem());
    engine.addSystem(new InventoryUpdateSystem(hud));
    PowerUpSystem powerUpSystem = new PowerUpSystem();
    engine.addSystem(powerUpSystem);
    hud.initShop(powerUpSystem);

    this.difficulty = difficulty;

    if (loadSave) {
      FileHandle saveFile = Gdx.files.local(GameState.SAVE_LOCATION);
      Json json = new Json();
      GameState gameSave = json.fromJson(GameState.class, saveFile.readString());

      // Load stations
      for (String key : gameSave.getStations().keySet()) {
        Mappers.station.get(stationsMap.get(Integer.valueOf(key)))
            .copyValues(gameSave.getStations().get(key).toStationComponent(factory), engine);
      }

      // Load cooks
      for (int i = 0; i < gameSave.getCooks().size(); i++) {
        mapLoader.getCookSpawns().remove(i);
        SavableCook savedCook = gameSave.getCooks().get(i);
        Entity cook = factory.createCook((int) savedCook.transformComponent.position.x,
            (int) savedCook.transformComponent.position.y);

        ControllableComponent controllableComponent = Mappers.controllable.get(cook);
        for (SavableFood savableFood : gameSave.getCooks().get(i).foodStack) {
          Entity foodEntity = savableFood.toEntity(factory);
          ItemComponent itemComponent = engine.createComponent(ItemComponent.class);
          itemComponent.holderTransform = Mappers.transform.get(cook);
          foodEntity.add(itemComponent);
          controllableComponent.currentFood.push(foodEntity);
        }
        controllableComponent.speedModifier = savedCook.speedModifier;
        if (i == 0) {
          cook.add(engine.createComponent(PlayerComponent.class));
        }
      }

      reputationPointsAndMoney[0] = gameSave.getReputation();
      reputationPointsAndMoney[1] = gameSave.getMoney();

      // Load customerAISystem
      aiSystem.loadFromSave(gameSave.getCustomerAISystem());

      // Load powerUpSystem
      powerUpSystem.loadFromSave(gameSave.getPowerUpSystem());

      // Load difficulty
      this.difficulty = gameSave.getDifficulty();

      // Load hud save details
      hud.loadFromSave(gameSave);
    }

    if (this.difficulty == null) {
      this.difficulty = Difficulty.createDifficulty(Level.MEDIUM);
    }
    aiSystem.setDifficulty(this.difficulty);

    if (!loadSave) {
      for (int i = 0; i < this.difficulty.initialCooks; i++) {
        Vector2 position = mapLoader.getCookSpawns().get(i);
        Entity cook = factory.createCook((int) position.x, (int) position.y);
        if (i == 0) {
          cook.add(getEngine().createComponent(PlayerComponent.class));
        }
        mapLoader.getCookSpawns().remove(i);
      }
    }
  }

  public void spawnCook() {
    int id = -1;
    for (Integer i : mapLoader.getCookSpawns().keySet()) {
      id = i;
      Vector2 position = mapLoader.getCookSpawns().get(i);
      factory.createCook((int) position.x, (int) position.y);
      break;
    }
    mapLoader.getCookSpawns().remove(id);
  }

  public boolean canSpawnCook() {
    return mapLoader.getCookSpawns().size() > 0;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }
}
