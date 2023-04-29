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
import com.devcharles.piazzapanic.componentsystems.PowerUpSystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavablePowerUpSystemTest {

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
  public void testFrom() {
    PowerUpSystem powerUpSystem = new PowerUpSystem();
    engine.addSystem(powerUpSystem);
    engine.addSystem(new CustomerAISystem(new HashMap<Integer, Map<Integer, Box2dLocation>>(), world, factory, mock(
        Hud.class), new Integer[] {3, 0}, false));
    powerUpSystem.addPatience();
    powerUpSystem.addSalePrice();
    powerUpSystem.addChopSpeed();
    powerUpSystem.addPrepSpeed();
    powerUpSystem.addSpeedUp();

    SavablePowerUpSystem savablePowerUpSystem = SavablePowerUpSystem.from(powerUpSystem);
    assertEquals("There should be 1 chop speed powerup", 1, savablePowerUpSystem.numChopSpeed);
    assertEquals("There should be 1 prep spead powerup",1, savablePowerUpSystem.numPrepSpeed);
    assertEquals("There should be 1 patience powerup",1, savablePowerUpSystem.numPatienceIncrease);
    assertEquals("There should be 1 movement spead powerup",1, savablePowerUpSystem.numSpeedUp);
    assertEquals("There should be 1 sale price powerup",1, savablePowerUpSystem.numSalePrice);
  }
}