package com.devcharles.piazzapanic;

import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.componentsystems.CarryItemsSystem;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.InventoryUpdateSystem;
import com.devcharles.piazzapanic.componentsystems.LightingSystem;
import com.devcharles.piazzapanic.componentsystems.PhysicsSystem;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.componentsystems.RenderingSystem;

/**
 * @author Alistair Foggin
 */
public class ScenarioGameScreen extends BaseGameScreen {

  public ScenarioGameScreen(PiazzaPanic game, String mapPath, int maxCustomers) {
    super(game, mapPath);
    engine.addSystem(new PhysicsSystem(world));
    engine.addSystem(new RenderingSystem(mapLoader.map, game.batch, camera));
    engine.addSystem(new LightingSystem(rayhandler, camera));
    // This can be commented in during debugging.
    // engine.addSystem(new DebugRendererSystem(world, camera));
    engine.addSystem(new PlayerControlSystem(kbInput));
    engine.addSystem(new StationSystem(factory));
    CustomerAISystem aiSystem = new CustomerAISystem(mapLoader.getObjectives(), world, factory, hud,
        reputationPointsAndMoney, false);
    aiSystem.setMaxScenarioCustomers(maxCustomers);
    engine.addSystem(aiSystem);
    engine.addSystem(new CarryItemsSystem());
    engine.addSystem(new InventoryUpdateSystem(hud));
  }
}
