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
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class SavableCookTest {
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
    Entity cook = factory.createCook(0, 0);
    Entity food = factory.createFood(FoodType.buns);
    Mappers.controllable.get(cook).currentFood.pushItem(food, cook);
    SavableCook savedCook = SavableCook.from(cook);
    assertEquals("The speedModifier should be copied", savedCook.speedModifier, Mappers.controllable.get(cook).speedModifier, 0.001f);
    TransformComponent cookTransform = Mappers.transform.get(cook);
    assertEquals("The transform x position should be the same", cookTransform.position.x, savedCook.transformComponent.position.x, 0.01f);
    assertEquals("The transform y position should be the same", cookTransform.position.y, savedCook.transformComponent.position.y, 0.01f);
    assertEquals("The transform isMoving flag should be the same", cookTransform.isMoving, savedCook.transformComponent.isMoving);
    assertEquals("The saved food type should be a bun", FoodType.buns, savedCook.foodStack.getFirst().foodComponent.type);
  }
}