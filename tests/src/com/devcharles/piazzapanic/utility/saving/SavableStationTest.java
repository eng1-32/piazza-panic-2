package com.devcharles.piazzapanic.utility.saving;

import static org.junit.Assert.*;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableStationTest {

  PooledEngine engine;
  EntityFactory factory;

  @Before
  public void setUp() {
    engine = new PooledEngine();
    World world = new World(Vector2.Zero, true);
    AssetManager assetManager = new AssetManager();
    assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(assetManager);
    assetManager.finishLoading();
    factory = new EntityFactory(engine, world, assetManager);
  }

  @Test
  public void testFrom() {
    StationComponent stationComponent = new StationComponent();
    stationComponent.id = 5;
    stationComponent.type = StationType.grill;
    stationComponent.prepModifier = 1.1f;
    stationComponent.chopModifier = 1.1f;
    stationComponent.food = new ArrayList<>();
    stationComponent.food.add(factory.createFood(FoodType.grilledPatty));
    stationComponent.food.add(null);
    stationComponent.food.add(null);
    stationComponent.food.add(null);
    stationComponent.ingredient = FoodType.buns;

    SavableStation savableStation = SavableStation.from(stationComponent);
    assertEquals("The ids should match", 5, savableStation.id);
    assertEquals("The type should be a grill", StationType.grill, savableStation.type);
    assertEquals("The prep modifier should be 1.1", 1.1f, savableStation.prepModifier, 0.0001f);
    assertEquals("The chop modifier should be 1.1", 1.1f, savableStation.chopModifier, 0.0001f);
    assertEquals("There should be 4 food slots", 4, savableStation.food.size());
    assertNotNull("There should be food on the first slot", savableStation.food.get(0));
    assertEquals("The food should be a grilled patty", FoodType.grilledPatty,
        savableStation.food.get(0).foodComponent.type);
    assertNull("There should be no food on the second slot", savableStation.food.get(1));
    assertNull("There should be no food on the third slot", savableStation.food.get(2));
    assertNull("There should be no food on the fourth slot", savableStation.food.get(3));
    assertEquals("The ingredient should be a bun", FoodType.buns, savableStation.ingredient);
  }

  @Test
  public void testToStationComponent() {
    SavableStation savableStation = new SavableStation();
    savableStation.id = 5;
    savableStation.type = StationType.grill;
    savableStation.prepModifier = 1.1f;
    savableStation.chopModifier = 1.1f;
    SavableFood food = new SavableFood();
    food.transformComponent = new TransformComponent();
    food.foodComponent = new FoodComponent();
    food.foodComponent.type = FoodType.grilledPatty;
    savableStation.food = new ArrayList<>();
    savableStation.food.add(food);
    savableStation.food.add(null);
    savableStation.food.add(null);
    savableStation.food.add(null);
    savableStation.ingredient = FoodType.buns;

    StationComponent stationComponent = savableStation.toStationComponent(factory);
    assertEquals("The ids should match", 5, stationComponent.id);
    assertEquals("The type should be a grill", StationType.grill, stationComponent.type);
    assertEquals("The prep modifier should be 1.1", 1.1f, stationComponent.prepModifier, 0.0001f);
    assertEquals("The chop modifier should be 1.1", 1.1f, stationComponent.chopModifier, 0.0001f);
    assertEquals("There should be 4 food slots", 4, stationComponent.food.size());
    assertNotNull("There should be food on the first slot", stationComponent.food.get(0));
    assertEquals("The food should be a grilled patty", FoodType.grilledPatty,
        Mappers.food.get(stationComponent.food.get(0)).type);
    assertNull("There should be no food on the second slot", stationComponent.food.get(1));
    assertNull("There should be no food on the third slot", stationComponent.food.get(2));
    assertNull("There should be no food on the fourth slot", stationComponent.food.get(3));
    assertEquals("The ingredient should be a bun", FoodType.buns, stationComponent.ingredient);
  }
}