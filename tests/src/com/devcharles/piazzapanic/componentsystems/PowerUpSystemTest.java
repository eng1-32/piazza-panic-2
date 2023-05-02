package com.devcharles.piazzapanic.componentsystems;

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
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alistair Foggin
 */
@RunWith(GdxTestRunner.class)
public class PowerUpSystemTest {

  PooledEngine engine;
  EntityFactory factory;
  Integer[] reputationPoints;
  CustomerAISystem aiSystem;
  PowerUpSystem powerUpSystem;

  @Before
  public void setUp() {
    engine = new PooledEngine();
    Map<Integer, Map<Integer, Box2dLocation>> objectives = new HashMap<>();
    HashMap<Integer, Box2dLocation> destination = new HashMap<>();
    destination.put(0, new Box2dLocation());

    HashMap<Integer, Box2dLocation> start = new HashMap<>();
    start.put(0, new Box2dLocation());
    HashMap<Integer, Box2dLocation> queueStart = new HashMap<>();
    queueStart.put(0, new Box2dLocation());
    HashMap<Integer, Box2dLocation> queueMid = new HashMap<>();
    queueMid.put(0, new Box2dLocation());
    HashMap<Integer, Box2dLocation> queueEnd = new HashMap<>();
    queueEnd.put(0, new Box2dLocation());

    objectives.put(-2, start);
    objectives.put(-1, destination);
    objectives.put(0, queueStart);
    objectives.put(1, queueMid);
    objectives.put(2, queueEnd);

    World world = new World(Vector2.Zero, true);
    engine = new PooledEngine();
    AssetManager manager = new AssetManager();
    manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(manager);
    manager.finishLoading();
    factory = new EntityFactory(engine, world, manager);
    reputationPoints = new Integer[]{3, 0};
    aiSystem = new CustomerAISystem(objectives, world, factory, mock(Hud.class), reputationPoints,
        true);
    powerUpSystem = new PowerUpSystem();
    engine.addSystem(aiSystem);
    engine.addSystem(powerUpSystem);
  }

  @Test
  public void testAddSpeedUp() {
    Entity cook1 = factory.createCook(0, 0);
    Entity cook2 = factory.createCook(1, 1);

    assertEquals("The first cook should have a speed modifier of 1", 1,
        Mappers.controllable.get(cook1).speedModifier, 0.001f);
    assertEquals("The second cook should have a speed modifier of 1", 1,
        Mappers.controllable.get(cook2).speedModifier, 0.001f);

    powerUpSystem.addSpeedUp();
    assertEquals("The first cook should have a speed modifier of 1.2", 1.2f,
        Mappers.controllable.get(cook1).speedModifier, 0.001f);
    assertEquals("The second cook should have a speed modifier of 1.2", 1.2f,
        Mappers.controllable.get(cook2).speedModifier, 0.001f);
    powerUpSystem.addSpeedUp();
    powerUpSystem.addSpeedUp();
    powerUpSystem.addSpeedUp();
    powerUpSystem.addSpeedUp();
    float finalSpeed = Mappers.controllable.get(cook2).speedModifier;
    powerUpSystem.addSpeedUp();
    assertEquals("The first cook should not increase the speed modifier beyond the max", finalSpeed,
        Mappers.controllable.get(cook1).speedModifier, 0.001f);
    assertEquals("The second cook should increase the speed modifier beyond the max", finalSpeed,
        Mappers.controllable.get(cook2).speedModifier, 0.001f);
  }

  @Test
  public void testAddPrepSpeed() {
    Entity station = factory.createStation(0, StationType.cutting_board, Vector2.Zero, null, false);

    assertEquals("The station should have a prep speed modifier of 1", 1,
        Mappers.station.get(station).prepModifier, 0.001f);

    powerUpSystem.addPrepSpeed();
    assertEquals("The station should have a prep speed modifier of 1.2", 1.2f,
        Mappers.station.get(station).prepModifier, 0.001f);
    powerUpSystem.addPrepSpeed();
    powerUpSystem.addPrepSpeed();
    powerUpSystem.addPrepSpeed();
    powerUpSystem.addPrepSpeed();
    float finalSpeed = Mappers.station.get(station).prepModifier;
    powerUpSystem.addPrepSpeed();
    assertEquals("The station should not increase the prep speed modifier beyond the max",
        finalSpeed, Mappers.station.get(station).prepModifier, 0.001f);

    Entity cook = factory.createCook(0, 0);
    cook.add(engine.createComponent(PlayerComponent.class));
    Entity food = factory.createFood(FoodType.unformedPatty);
    Mappers.controllable.get(cook).currentFood.pushItem(food, cook);
    Mappers.player.get(cook).putDown = true;
    Mappers.station.get(station).interactingCook = cook;

    StationSystem stationSystem = new StationSystem(factory, reputationPoints, mock(Hud.class));
    engine.addSystem(stationSystem);
    engine.update(0.1f);
    assertEquals("The food cooking component timer should be less than 5000",
        (int) (5000 / finalSpeed), Mappers.cooking.get(food).timer.getDelay());
  }

  @Test
  public void testAddChopSpeed() {
    Entity station = factory.createStation(0, StationType.cutting_board, Vector2.Zero, null, false);

    assertEquals("The station should have a chop speed modifier of 1", 1,
        Mappers.station.get(station).chopModifier, 0.001f);

    powerUpSystem.addChopSpeed();
    assertEquals("The station should have a chop speed modifier of 1.2", 1.2f,
        Mappers.station.get(station).chopModifier, 0.001f);
    powerUpSystem.addChopSpeed();
    powerUpSystem.addChopSpeed();
    powerUpSystem.addChopSpeed();
    powerUpSystem.addChopSpeed();
    float finalSpeed = Mappers.station.get(station).chopModifier;
    powerUpSystem.addChopSpeed();
    assertEquals("The station should not increase the chop speed modifier beyond the max",
        finalSpeed, Mappers.station.get(station).chopModifier, 0.001f);

    Entity cook = factory.createCook(0, 0);
    cook.add(engine.createComponent(PlayerComponent.class));
    Entity food = factory.createFood(FoodType.onion);
    Mappers.controllable.get(cook).currentFood.pushItem(food, cook);
    Mappers.player.get(cook).putDown = true;
    Mappers.station.get(station).interactingCook = cook;

    StationSystem stationSystem = new StationSystem(factory, reputationPoints, mock(Hud.class));
    engine.addSystem(stationSystem);
    engine.update(0.1f);
    assertEquals("The food cooking component timer should be less than 5000",
        (int) (5000 / finalSpeed), Mappers.cooking.get(food).timer.getDelay());
  }

  @Test
  public void testAddSalePrice() {
    assertEquals("The station should have an income modifier of 1", 1,
        aiSystem.getIncomeModifier());

    powerUpSystem.addSalePrice();
    assertEquals("The station should have an income modifier of 2", 2,
        aiSystem.getIncomeModifier());
    powerUpSystem.addSalePrice();
    powerUpSystem.addSalePrice();
    powerUpSystem.addSalePrice();
    powerUpSystem.addSalePrice();
    assertEquals("The station should have a the income modifier of 6",
        6, aiSystem.getIncomeModifier(), 0.001f);
    powerUpSystem.addSalePrice();
    assertEquals("The station should not increase the income modifier beyond the max",
        6, aiSystem.getIncomeModifier(), 0.001f);

    Entity cook = factory.createCook(0, 0);
    cook.add(engine.createComponent(PlayerComponent.class));
    engine.update(0.1f);
    Entity customer = aiSystem.customers.get(0).get(0);
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    Entity food = factory.createFood(customerComponent.order);
    Mappers.controllable.get(cook).currentFood.pushItem(food, cook);
    Mappers.player.get(cook).putDown = true;
    customerComponent.interactingCook = cook;
    assertEquals("The total money should be 0 before the transaction", 0,
        (int) reputationPoints[1]);
    engine.update(0.1f);
    assertEquals("The total money should be 14 before the transaction", 14,
        (int) reputationPoints[1]);
  }

  @Test
  public void testAddPatience() {
    assertEquals("The station should have a patience modifier of 1", 1f,
        aiSystem.getPatienceModifier(), 0.001f);
    engine.update(0.1f);
    Entity customer1 = aiSystem.customers.get(0).get(0);
    assertEquals("The customer patience should be 90000", 90000,
        Mappers.customer.get(customer1).timer.getDelay());

    powerUpSystem.addPatience();
    assertEquals("The station should have a patience modifier of 1.2", 1.2f,
        aiSystem.getPatienceModifier(), 0.001f);
    powerUpSystem.addPatience();
    powerUpSystem.addPatience();
    powerUpSystem.addPatience();
    powerUpSystem.addPatience();
    float finalPatience = aiSystem.getPatienceModifier();
    powerUpSystem.addPatience();
    assertEquals("The station should not increase the patience modifier beyond the max",
        finalPatience, aiSystem.getPatienceModifier(), 0.001f);

    assertTrue("The customer patience should be more than 90000",
        Mappers.customer.get(customer1).timer.getDelay() > 90000);
    engine.update(30);
    Entity customer2 = aiSystem.customers.get(1).get(0);
    assertTrue("The customer patience should be more than 90000",
        Mappers.customer.get(customer2).timer.getDelay() > 90000);
  }
}