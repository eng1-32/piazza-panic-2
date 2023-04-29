package com.devcharles.piazzapanic.utility.saving;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableCustomerAISystemTest {

  PooledEngine engine;
  World world;
  EntityFactory factory;

  @Before
  public void setUp() {
    engine = new PooledEngine();
    world = new World(Vector2.Zero, true);
    AssetManager assetManager = new AssetManager();
    assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(assetManager);
    assetManager.finishLoading();
    factory = new EntityFactory(engine, world, assetManager);
  }


  @Test
  public void from() {
    Map<Integer, Map<Integer, Box2dLocation>> objectives = new HashMap<>();
    HashMap<Integer, Box2dLocation> destination = new HashMap<>();
    destination.put(0, new Box2dLocation());

    HashMap<Integer, Box2dLocation> start = new HashMap<>();
    start.put(0, new Box2dLocation());
    HashMap<Integer, Box2dLocation> queueStart = new HashMap<>();
    queueStart.put(0, new Box2dLocation());
    HashMap<Integer, Box2dLocation> queueEnd = new HashMap<>();
    queueEnd.put(0, new Box2dLocation());

    objectives.put(-2, start);
    objectives.put(-1, destination);
    objectives.put(0, queueStart);
    objectives.put(1, queueEnd);

    Integer[] reputationPoints = new Integer[]{3, 0};
    CustomerAISystem aiSystem = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    aiSystem.getSpawnTimer().setDelay(25000);
    engine.addSystem(aiSystem);
    engine.update(26f); // Spawn two customer groups
    engine.update(0f); // Actually process the entities

    SavableCustomerAISystem savableCustomerAISystem = SavableCustomerAISystem.from(aiSystem);
    assertEquals("There should be 2 customer groups", 2, savableCustomerAISystem.customers.size());
    assertEquals("There should be 2 total customers", 2, savableCustomerAISystem.totalCustomers);
    assertEquals("There should be 1 customer in the first group", 1,
        savableCustomerAISystem.customers.get(0).size());
    assertEquals("There should be 1 customer in the second group", 1,
        savableCustomerAISystem.customers.get(1).size());

    assertEquals("The timer should be 25 seconds", 25000, savableCustomerAISystem.spawnTimer.delay);

    assertTrue("The first objective should be taken",
        savableCustomerAISystem.objectiveTaken.get(0));
    assertTrue("The second objective should be taken",
        savableCustomerAISystem.objectiveTaken.get(1));

    assertEquals("There should be no queued customers", 0,
        savableCustomerAISystem.numQueuedCustomers);
    assertEquals("There should be patience modifier of 1", 1,
        savableCustomerAISystem.patienceModifier, 0.001f);
    assertEquals("There should be income modifier of 1", 1,
        savableCustomerAISystem.incomeModifier);
    assertFalse("It is no longer the first spawn", savableCustomerAISystem.firstSpawn);
  }
}