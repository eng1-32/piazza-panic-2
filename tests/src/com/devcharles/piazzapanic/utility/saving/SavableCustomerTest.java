package com.devcharles.piazzapanic.utility.saving;

import static org.junit.Assert.*;

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
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.CookingComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableCustomerTest {

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
    Entity customer = factory.createCustomer(Vector2.Zero, FoodType.burger, false);
    Mappers.customer.get(customer).food = factory.createFood(FoodType.burger);
    Mappers.aiAgent.get(customer).slot = 1;
    Mappers.aiAgent.get(customer).currentObjective = 3;

    SavableCustomer savedCustomer = SavableCustomer.from(customer);
    assertEquals("The food order should be a burger", FoodType.burger, savedCustomer.order);
    assertEquals("The actual food should also be a burger", FoodType.burger,
        savedCustomer.food.foodComponent.type);
    assertEquals("The timer delay should be saved correctly.", 90000, savedCustomer.timer.delay);
    assertEquals("The transform x position should be 0", 0,
        savedCustomer.transformComponent.position.x, 0.001f);
    assertEquals("The transform y position should be 0", 0,
        savedCustomer.transformComponent.position.y, 0.001f);
    assertEquals("The slot should be 1", 1, savedCustomer.slot);
    assertEquals("The current objective should be 3", 3, savedCustomer.currentObjective);
  }

  @Test
  public void testToEntity() {
    SavableCustomer customer = new SavableCustomer();
    customer.transformComponent = new TransformComponent();
    customer.transformComponent.position.x = 1;
    customer.transformComponent.position.y = 2;
    customer.transformComponent.isMoving = true;

    customer.currentObjective = 3;
    customer.slot = 1;
    customer.order = FoodType.burger;

    customer.food = new SavableFood();
    customer.food.transformComponent = new TransformComponent();
    customer.food.foodComponent = new FoodComponent();
    customer.food.foodComponent.type = FoodType.burger;
    customer.food.processed = true;
    customer.food.hasCookingComponent = true;
    customer.food.cookingTimer = new SavableTimer();

    customer.timer = new SavableTimer();
    customer.timer.delay = 30000;

    Entity customerEntity = customer.toEntity(factory);
    TransformComponent transformComponent = Mappers.transform.get(customerEntity);
    assertNotNull("The transform should exist", transformComponent);
    assertEquals("The transform x position should be 1", 1, transformComponent.position.x, 0.001f);
    assertEquals("The transform y position should be 2", 2, transformComponent.position.y, 0.001f);
    assertTrue("The transform should be moving", transformComponent.isMoving);

    AIAgentComponent aiAgent = Mappers.aiAgent.get(customerEntity);
    assertNotNull("The ai agent component should exist", aiAgent);
    assertEquals("The current objective should be 3", 3, aiAgent.currentObjective);
    assertEquals("The slot should be 1", 1, aiAgent.slot);

    CustomerComponent customerComponent = Mappers.customer.get(customerEntity);
    assertNotNull("The customer component should exist", customerComponent);
    assertEquals("The order should be a burger", FoodType.burger, customerComponent.order);
    assertEquals("The customer timer should have the right delay", 30000, customer.timer.delay);

    assertNotNull("The customer should have food", customerComponent.food);
    FoodComponent foodComponent = Mappers.food.get(customerComponent.food);
    assertNotNull("The food should have a food component", foodComponent);
    assertEquals("The food should be a burger", FoodType.burger, foodComponent.type);
    assertNotNull("The food should have a transform",
        Mappers.transform.get(customerComponent.food));

    CookingComponent cookingComponent = Mappers.cooking.get(customerComponent.food);
    assertNotNull("The cooking component should exist on the food", cookingComponent);
    assertTrue("The food should be processed", cookingComponent.processed);
    assertNotNull("The food should have a cooking timer", cookingComponent.timer);
  }
}