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
import com.devcharles.piazzapanic.components.CookingComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableFoodTest {

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
    Entity food = factory.createFood(FoodType.onion);
    Mappers.transform.get(food).position.x = 1;
    Mappers.transform.get(food).position.y = 2;
    Mappers.transform.get(food).isMoving = true;
    food.add(engine.createComponent(CookingComponent.class));
    Mappers.cooking.get(food).processed = true;

    SavableFood savedFood = SavableFood.from(food);
    assertEquals("The transform component should be the same", Mappers.transform.get(food),
        savedFood.transformComponent);
    assertEquals("The food component should be the same", Mappers.food.get(food),
        savedFood.foodComponent);
    assertTrue("There should be a saved cooking component", savedFood.hasCookingComponent);
    assertTrue("The food should be processed", savedFood.processed);
    assertEquals("The timer should have the same delay", Mappers.cooking.get(food).timer.getDelay(),
        savedFood.cookingTimer.delay);
  }

  @Test
  public void testToEntity() {
    SavableFood savedFood = new SavableFood();
    savedFood.foodComponent = new FoodComponent();
    savedFood.foodComponent.type = FoodType.onion;

    savedFood.transformComponent = new TransformComponent();
    savedFood.transformComponent.position.x = 1;
    savedFood.transformComponent.position.y = 2;
    savedFood.transformComponent.isMoving = true;

    savedFood.hasCookingComponent = true;
    savedFood.processed = true;
    savedFood.cookingTimer = new SavableTimer();
    savedFood.cookingTimer.delay = 25000;
    Entity food = savedFood.toEntity(factory);

    assertEquals("The transform component should be the same",
        1, Mappers.transform.get(food).position.x, 0.001f);
    assertEquals("The transform component should be the same",
        2, Mappers.transform.get(food).position.y, 0.001f);
    assertTrue("The transform should be moving", Mappers.transform.get(food).isMoving);
    assertEquals("The food component should be the same type", FoodType.onion,
        savedFood.foodComponent.type);
    assertNotNull("There should be a saved cooking component", Mappers.cooking.get(food));
    assertTrue("The food should be processed", Mappers.cooking.get(food).processed);
    assertEquals("The timer should have the same delay", 25000,
        Mappers.cooking.get(food).timer.getDelay());
  }
}