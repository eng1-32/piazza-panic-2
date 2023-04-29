package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import com.devcharles.piazzapanic.utility.saving.SavableCustomer;
import com.devcharles.piazzapanic.utility.saving.SavableCustomerAISystem;
import com.devcharles.piazzapanic.utility.saving.SavableTimer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class CustomerAISystemTest {

  Map<Integer, Map<Integer, Box2dLocation>> objectives;
  World world;
  PooledEngine engine;
  AssetManager manager;
  EntityFactory factory;
  Integer[] reputationPoints;

  @Before
  public void setup() {
    objectives = new HashMap<>();
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

    reputationPoints = new Integer[] {3, 0};

    world = new World(Vector2.Zero, true);
    engine = new PooledEngine();
    manager = new AssetManager();
    manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    PiazzaPanic.loadAssets(manager);
    manager.finishLoading();
    factory = new EntityFactory(engine, world, manager);
  }


  @Test
  public void testUpdate() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        new Integer[]{3}, false);
    engine.addSystem(system);

    assertEquals("There should be no customers to start with.", 0, system.customers.size());
    engine.update(1f);
    assertEquals("There should be a customer on the first frame.", 1, system.customers.size());
    engine.update(29f);
    assertEquals("There should still be 1 customer as the timer is not bigger than the delay.", 1,
        system.customers.size());
    engine.update(0.001f);
    assertEquals("There should be 2 customers on the very next frame past the limit.", 2,
        system.customers.size());
  }

  @Test
  public void testEndlessQuickerSpawns() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        new Integer[]{3}, true);
    engine.addSystem(system);

    int initialDelay = system.spawnTimer.getDelay();
    engine.update(1f);
    assertEquals("The first delay should be the same as the default.", initialDelay,
        system.spawnTimer.getDelay());
    engine.update(29.001f);
    assertTrue("The second and subsequent delays should be less than the default.",
        system.spawnTimer.getDelay() < initialDelay);
  }

  @Test
  public void testProcessEntityLoseReputation() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(system);

    engine.update(1f);
    system.processEntity(system.customers.get(0).get(0), 91f);
    assertEquals("Reputation should decrease after over 90 seconds.", 2,
        reputationPoints[0].intValue());
  }

  @Test
  public void testProcessEntityExitsWithFood() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(system);

    engine.update(1f);
    Entity customer = system.customers.get(0).get(0);
    TransformComponent transformComponent = Mappers.transform.get(customer);
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    transformComponent.position.x = 1f;
    customerComponent.food = factory.createFood(FoodType.burger);
    system.processEntity(system.customers.get(0).get(0), 1f);

    assertEquals("When the customer is to the right of the objective with food, it should be gone",
        0, engine.getEntities().size());
  }

  @Test
  public void testProcessEntityChefInteractValidFood() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(system);

    engine.update(1f);
    Entity customer = system.customers.get(0).get(0);
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    customerComponent.interactingCook = factory.createCook(0, 0);
    customerComponent.interactingCook.add(engine.createComponent(PlayerComponent.class));
    ControllableComponent cookComponent = Mappers.controllable.get(
        customerComponent.interactingCook);
    PlayerComponent playerComponent = Mappers.player.get(customerComponent.interactingCook);
    playerComponent.putDown = true;

    Entity food = factory.createFood(customerComponent.order);
    cookComponent.currentFood.pushItem(food, customerComponent.interactingCook);

    assertNull("Customer should not have food.", customerComponent.food);
    assertEquals("Cook should have a recipe.", food, cookComponent.currentFood.peek());
    system.processEntity(customer, 1f);
    assertEquals("Customer should now have the recipe.", food, customerComponent.food);
    assertTrue("Cook should no longer have a recipe.", cookComponent.currentFood.isEmpty());
    assertFalse("Player should not be putting down.", playerComponent.putDown);
  }

  @Test
  public void testProcessEntityChefInteractNoPlayerComponent() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(system);

    engine.update(1f);
    Entity customer = system.customers.get(0).get(0);
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    customerComponent.interactingCook = factory.createCook(0, 0);

    ControllableComponent cookComponent = Mappers.controllable.get(
        customerComponent.interactingCook);
    Entity food = factory.createFood(customerComponent.order);
    cookComponent.currentFood.pushItem(food, customerComponent.interactingCook);

    system.processEntity(customer, 1f);
    assertEquals("The chef should not give an item if it is not the player.", 1,
        cookComponent.currentFood.size());
  }

  @Test
  public void testProcessEntityChefInteractInvalidFood() {
    CustomerAISystem system = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(system);

    engine.update(1f);
    Entity customer = system.customers.get(0).get(0);
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    customerComponent.interactingCook = factory.createCook(0, 0);
    customerComponent.interactingCook.add(engine.createComponent(PlayerComponent.class));
    PlayerComponent playerComponent = Mappers.player.get(customerComponent.interactingCook);
    playerComponent.putDown = true;

    ControllableComponent cookComponent = Mappers.controllable.get(
        customerComponent.interactingCook);
    Entity food = factory.createFood(FoodType.tomato);
    cookComponent.currentFood.pushItem(food, customerComponent.interactingCook);

    system.processEntity(customer, 1f);
    assertTrue("The food should be removed.",
        cookComponent.currentFood.isEmpty());
    assertFalse("Food should no longer exist.", engine.getEntities().contains(food, true));
    assertNull("The customer should not have the food.", customerComponent.food);
  }

  @Test
  public void testDestroyCustomerValid() {
    CustomerAISystem system = new CustomerAISystem(objectives, world,
        factory, mock(Hud.class), new Integer[]{}, false);
    engine.addSystem(system);
    Entity customer = factory.createCustomer(Vector2.Zero, null);
    Entity food = factory.createFood(FoodType.from(1));
    customer.getComponent(CustomerComponent.class).food = food;

    assertTrue("Food should exist in the ECS engine.", engine.getEntities().contains(food, true));
    assertTrue("Customer should exist in the ECS engine.",
        engine.getEntities().contains(customer, true));
    assertEquals("Customer should have a physical representation", 1, world.getBodyCount());

    system.destroyCustomer(customer);

    assertFalse("Food should no longer exist in the ECS engine.",
        engine.getEntities().contains(food, true));
    assertFalse("Customer should no longer exist in the ECS engine.",
        engine.getEntities().contains(customer, true));
    assertEquals("Customer should not have a physical representation", 0, world.getBodyCount());
  }

  @Test(expected = NullPointerException.class)
  public void testDestroyCustomerInvalidEntity() {
    CustomerAISystem system = new CustomerAISystem(objectives, world,
        factory, mock(Hud.class), new Integer[]{}, false);
    engine.addSystem(system);
    Entity customer = new Entity();

    system.destroyCustomer(customer);
  }

  @Test(expected = NullPointerException.class)
  public void testDestroyCustomerNull() {
    CustomerAISystem system = new CustomerAISystem(objectives, world,
        factory, mock(Hud.class), new Integer[]{}, false);
    engine.addSystem(system);

    system.destroyCustomer(null);
  }

  @Test
  public void testMakeItGoThere() {
    CustomerAISystem system = new CustomerAISystem(objectives, world,
        factory, mock(Hud.class), new Integer[]{}, false);
    engine.addSystem(system);
    Entity customer = factory.createCustomer(Vector2.Zero, null);
    AIAgentComponent aiAgentComponent = Mappers.aiAgent.get(customer);

    assertNull("There should be no steering behaviour.",
        aiAgentComponent.steeringBody.getSteeringBehavior());

    system.makeItGoThere(aiAgentComponent, -1);
    SteeringBehavior<Vector2> oldBehaviour = aiAgentComponent.steeringBody.getSteeringBehavior();
    assertNotNull("There should be steering behaviour.", oldBehaviour);

    system.makeItGoThere(aiAgentComponent, 0);
    assertNotEquals("The steering behaviour should be different.", oldBehaviour,
        aiAgentComponent.steeringBody.getSteeringBehavior());
  }

  @Test
  public void testFulfillOrder() {
    CustomerAISystem system = new CustomerAISystem(objectives, world,
        factory, mock(Hud.class), reputationPoints, false);
    engine.addSystem(system);
    Entity customer = factory.createCustomer(Vector2.Zero, null);
    Entity food = factory.createFood(FoodType.from(1));
    CustomerComponent customerComponent = Mappers.customer.get(customer);
    customerComponent.order = FoodType.burger;

    ArrayList<Entity> group = new ArrayList<>();
    group.add(customer);
    system.customers.add(group);
    system.fulfillOrder(customer, customerComponent, food);
    assertEquals("There should be no more customers.", 0, system.customers.size());
    assertFalse("Timer should be stopped.", customerComponent.timer.isRunning());
    assertEquals("Customer's food should be the same as the food above.", food,
        customerComponent.food);
  }

  @Test
  public void loadFromSave() {

    CustomerAISystem aiSystem = new CustomerAISystem(objectives, world, factory, mock(Hud.class),
        reputationPoints, false);
    engine.addSystem(aiSystem);

    SavableCustomerAISystem savableCustomerAISystem = new SavableCustomerAISystem();
    savableCustomerAISystem.objectiveTaken = new HashMap<>();
    savableCustomerAISystem.objectiveTaken.put(0, true);
    savableCustomerAISystem.objectiveTaken.put(1, true);

    savableCustomerAISystem.spawnTimer = new SavableTimer();
    savableCustomerAISystem.spawnTimer.delay = 25000;
    savableCustomerAISystem.spawnTimer.elapsed = 5000;
    savableCustomerAISystem.spawnTimer.running = true;

    savableCustomerAISystem.totalCustomers = 2;
    savableCustomerAISystem.firstSpawn = false;
    savableCustomerAISystem.numQueuedCustomers = 1;
    savableCustomerAISystem.patienceModifier = 1.1f;
    savableCustomerAISystem.incomeModifier = 2;

    savableCustomerAISystem.customers = new ArrayList<>();
    ArrayList<SavableCustomer> firstGroup = new ArrayList<>();
    SavableCustomer firstCustomer = new SavableCustomer();
    firstCustomer.currentObjective = 0;
    firstCustomer.order = FoodType.burger;
    firstCustomer.transformComponent = new TransformComponent();
    firstCustomer.timer = new SavableTimer();
    firstCustomer.timer.delay = 25000;
    firstGroup.add(firstCustomer);
    savableCustomerAISystem.customers.add(firstGroup);
    ArrayList<SavableCustomer> secondGroup = new ArrayList<>();
    SavableCustomer secondCustomer = new SavableCustomer();
    secondCustomer.currentObjective = 1;
    secondCustomer.order = FoodType.salad;
    secondCustomer.transformComponent = new TransformComponent();
    secondCustomer.timer = new SavableTimer();
    secondCustomer.timer.delay = 25000;
    secondGroup.add(secondCustomer);
    savableCustomerAISystem.customers.add(secondGroup);

    aiSystem.loadFromSave(savableCustomerAISystem);

    assertEquals("There should be 2 customer groups", 2, aiSystem.customers.size());
    assertEquals("There should be 2 total customers", 2, aiSystem.getTotalCustomers());
    assertEquals("There should be 1 customer in the first group", 1,
        aiSystem.customers.get(0).size());
    assertEquals("The first customer should want a burger", FoodType.burger,
        Mappers.customer.get(aiSystem.customers.get(0).get(0)).order);
    assertEquals("The first customer should go to objective 0", 0,
        Mappers.aiAgent.get(aiSystem.customers.get(0).get(0)).currentObjective);
    assertEquals("There should be 1 customer in the second group", 1,
        aiSystem.customers.get(1).size());
    assertEquals("The second customer should want a salad", FoodType.salad,
        Mappers.customer.get(aiSystem.customers.get(1).get(0)).order);
    assertEquals("The second customer should go to objective 1", 1,
        Mappers.aiAgent.get(aiSystem.customers.get(1).get(0)).currentObjective);

    assertEquals("The timer should be 25 seconds", 25000, aiSystem.getSpawnTimer().getDelay());
    assertEquals("The timer should have 5 elapsed seconds", 5000, aiSystem.getSpawnTimer().getElapsed());

    assertTrue("The first objective should be taken",
        aiSystem.getObjectiveTaken().get(0));
    assertTrue("The second objective should be taken",
        aiSystem.getObjectiveTaken().get(0));

    assertEquals("There should be one queued customers", 1,
        aiSystem.getNumQueuedCustomers());
    assertEquals("There should be patience modifier of 1.1", 1.1f,
        aiSystem.getPatienceModifier(), 0.001f);
    assertEquals("There should be income modifier of 2", 2,
        aiSystem.getIncomeModifier());
    assertFalse("It is no longer the first spawn", aiSystem.isFirstSpawn());

    engine.update(0.1f);
    assertEquals("There should be a third customer", 3, aiSystem.customers.size());
  }
}