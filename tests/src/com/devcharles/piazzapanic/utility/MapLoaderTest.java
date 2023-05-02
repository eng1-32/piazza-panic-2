package com.devcharles.piazzapanic.utility;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.B2dBodyComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.TextureComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import box2dLight.RayHandler;
import java.util.Map;

import static org.junit.Assert.*;

import com.devcharles.piazzapanic.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author James Wild
 * @author Alistair Foggin
 */
@RunWith(GdxTestRunner.class)
public class MapLoaderTest {

  @Test
  public void buildFromObjectsTest() {
    World world = new World(new Vector2(0, 0), true);
    PooledEngine engine = new PooledEngine();
    AssetManager manager = new AssetManager();
    manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(manager);
    manager.finishLoading();

    EntityFactory factory = new EntityFactory(engine, world, manager);
    RayHandler rayhandler = new RayHandler(world);
    MapLoader mapLoader = new MapLoader("v2/endlessMap.tmx", null, factory, manager);
    mapLoader.buildFromObjects(rayhandler);
    assertEquals("Checks that 3 cook spawn points.", 3, mapLoader.getCookSpawns().size());
    Map<Integer, Map<Integer, Box2dLocation>> aiObj = mapLoader.getObjectives();
    assertTrue("Checks an ai objective with key 0 exists.", aiObj.containsKey(0));
    assertTrue("Checks an ai objective with key -1 exists.", aiObj.containsKey(-1));
    assertTrue("Checks an ai objective with key -2 exists.", aiObj.containsKey(-2));
    assertTrue("Checks an ai objective with key 0 has a slot 0 exists.",
        aiObj.get(0).containsKey(0));
  }

  @Test
  public void buildStationsTest() {
    World world = new World(new Vector2(0, 0), true);
    PooledEngine engine = new PooledEngine();
    AssetManager manager = new AssetManager();
    manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    manager.load("v2/endlessMap.tmx", TiledMap.class);
    manager.finishLoading();
    EntityFactory factory = new EntityFactory(engine, world, manager);
    MapLoader mapLoader = new MapLoader("v2/endlessMap.tmx", null, factory, manager);
    mapLoader.buildStations();
    ImmutableArray<Entity> entities = engine.getEntitiesFor(
        Family.all(B2dBodyComponent.class, TransformComponent.class,
            TextureComponent.class, StationComponent.class).get());
    int numSinks = 0;
    int numCuttingBoards = 0;
    int numGrills = 0;
    int numServe = 0;
    int numOvens = 0;
    int numIngredients = 0;
    int numBins = 0;
    for (Entity entity : entities) {
      switch (Mappers.station.get(entity).type) {
        case sink:
          numSinks++;
          break;
        case cutting_board:
          numCuttingBoards++;
          break;
        case grill:
          numGrills++;
          break;
        case serve:
          numServe++;
          break;
        case oven:
          numOvens++;
          break;
        case ingredient:
          numIngredients++;
          break;
        case bin:
          numBins++;
          break;
      }
    }
    assertEquals("Check that all 7 stations are built.", 25, entities.size());
    assertEquals("Checks sink stations exist.", 4, numSinks);
    assertEquals("Checks cutting boards station exist.", 2, numCuttingBoards);
    assertEquals("Checks grill stations exist.", 2, numGrills);
    assertEquals("Checks serve stations exist.", 2, numServe);
    assertEquals("Checks oven stations exist.", 2, numOvens);
    assertEquals("Checks bin station exist.", 1, numBins);
    assertEquals("Checks ingredient stations exist.", 12, numIngredients);
  }
}
