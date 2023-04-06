package com.devcharles.piazzapanic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.componentsystems.CarryItemsSystem;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.InventoryUpdateSystem;
import com.devcharles.piazzapanic.componentsystems.LightingSystem;
import com.devcharles.piazzapanic.componentsystems.PhysicsSystem;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.componentsystems.RenderingSystem;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.saving.GameState;
import com.devcharles.piazzapanic.utility.saving.SavableCook;
import com.devcharles.piazzapanic.utility.saving.SavableFood;

public class EndlessGameScreen extends BaseGameScreen {

  public EndlessGameScreen(PiazzaPanic game, String mapPath, boolean loadSave) {
    super(game, mapPath);
    hud.setEndless(true);
    engine.addSystem(new PhysicsSystem(world));
    engine.addSystem(new RenderingSystem(mapLoader.map, game.batch, camera));
    engine.addSystem(new LightingSystem(rayhandler, camera));
    // This can be commented in during debugging.
    // engine.addSystem(new DebugRendererSystem(world, camera));
    engine.addSystem(new PlayerControlSystem(kbInput));
    engine.addSystem(new StationSystem(kbInput, factory));
    CustomerAISystem aiSystem =
        new CustomerAISystem(mapLoader.getObjectives(), world, factory, hud, reputationPoints,
            true, 3);
    engine.addSystem(aiSystem);
    engine.addSystem(new CarryItemsSystem());
    engine.addSystem(new InventoryUpdateSystem(hud));

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
      ImmutableArray<Entity> cooks = engine.getEntitiesFor(
          Family.all(TransformComponent.class, ControllableComponent.class).get());
      for (int i = 0; i < gameSave.getCooks().size(); i++) {
        SavableCook savedCook = gameSave.getCooks().get(i);
        Entity cook = cooks.get(i);
        Body cookBody = Mappers.b2body.get(cook).body;
        Vector3 transformPosition = savedCook.transformComponent.position;
//        cookBody.getTransform().setPosition(new Vector2(transformPosition.x, transformPosition.y));
        Mappers.transform.get(cook).copyValues(savedCook.transformComponent);

        ControllableComponent controllableComponent = Mappers.controllable.get(cook);
        for (Entity entity : controllableComponent.currentFood) {
          engine.removeEntity(entity);
        }
        controllableComponent.currentFood.clear();
        for (SavableFood savableFood : gameSave.getCooks().get(i).foodStack) {
          controllableComponent.currentFood.push(savableFood.toEntity(factory));
        }
      }

      // Load customerAISystem
      aiSystem.loadFromSave(gameSave.getCustomerAISystem());
    }
  }
}
