package com.devcharles.piazzapanic.utility.saving;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.PowerUpSystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class GameStateTest {

  @Test
  public void testSetFromEngine() {
    PooledEngine engine = new PooledEngine();
    World world = new World(Vector2.Zero, true);
    AssetManager assetManager = new AssetManager();
    assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(assetManager);
    assetManager.finishLoading();
    EntityFactory factory = new EntityFactory(engine, world, assetManager);

    Entity stationEntity = factory.createStation(0, StationType.grill, Vector2.Zero,
        FoodType.grilledPatty, false);
    factory.createCook(0, 0);
    engine.addSystem(
        new CustomerAISystem(new HashMap<Integer, Map<Integer, Box2dLocation>>(), world, factory,
            mock(
                Hud.class), new Integer[]{3, 0}, true));
    engine.addSystem(new PowerUpSystem());

    GameState initialState = new GameState();
    initialState.setFromEngine(engine);
    assertEquals("A cook should be saved from the engine.", 1, initialState.getCooks().size());
    assertNotNull("A station should exist with a matching id.",
        initialState.getStations().get(String.valueOf(Mappers.station.get(stationEntity).id)));
    assertNotNull("The customerAISystem should be retrieved from the engine",
        initialState.getCustomerAISystem());
    assertNotNull("The powerUpSystem should be retrieved from the engine",
        initialState.getPowerUpSystem());
  }
}
