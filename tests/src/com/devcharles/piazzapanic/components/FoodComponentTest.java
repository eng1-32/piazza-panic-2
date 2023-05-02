package com.devcharles.piazzapanic.components;

import static org.junit.Assert.*;

import com.devcharles.piazzapanic.GdxTestRunner;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author James Wild
 */
@RunWith(GdxTestRunner.class)
public class FoodComponentTest {

  @Test
  public void getValueFromFoodType() {
    assertEquals(1, FoodType.unformedPatty.getValue());
    assertEquals(2, FoodType.formedPatty.getValue());
    assertEquals(3, FoodType.grilledPatty.getValue());
    assertEquals(4, FoodType.buns.getValue());
    assertEquals(5, FoodType.toastedBuns.getValue());
    assertEquals(6, FoodType.burger.getValue());
    assertEquals(7, FoodType.lettuce.getValue());
    assertEquals(8, FoodType.slicedLettuce.getValue());
    assertEquals(9, FoodType.tomato.getValue());
    assertEquals(10, FoodType.slicedTomato.getValue());
    assertEquals(11, FoodType.onion.getValue());
    assertEquals(12, FoodType.slicedOnion.getValue());
    assertEquals(13, FoodType.salad.getValue());
  }

  @Test
  public void getFoodTypeFromID() {
    assertEquals(FoodType.unformedPatty, FoodType.from(1));
    assertEquals(FoodType.formedPatty, FoodType.from(2));
    assertEquals(FoodType.grilledPatty, FoodType.from(3));
    assertEquals(FoodType.buns, FoodType.from(4));
    assertEquals(FoodType.toastedBuns, FoodType.from(5));
    assertEquals(FoodType.burger, FoodType.from(6));
    assertEquals(FoodType.lettuce, FoodType.from(7));
    assertEquals(FoodType.slicedLettuce, FoodType.from(8));
    assertEquals(FoodType.tomato, FoodType.from(9));
    assertEquals(FoodType.slicedTomato, FoodType.from(10));
    assertEquals(FoodType.onion, FoodType.from(11));
    assertEquals(FoodType.slicedOnion, FoodType.from(12));
    assertEquals(FoodType.salad, FoodType.from(13));
    assertEquals(FoodType.potato, FoodType.from(14));
    assertEquals(FoodType.cookedPotato, FoodType.from(15));
    assertEquals(FoodType.jacketPotato, FoodType.from(16));
    assertEquals(FoodType.butter, FoodType.from(17));
    assertEquals(FoodType.unformedDough, FoodType.from(18));
    assertEquals(FoodType.formedDough, FoodType.from(19));
    assertEquals(FoodType.doughTomatoBase, FoodType.from(20));
    assertEquals(FoodType.uncookedPizza, FoodType.from(21));
    assertEquals(FoodType.pizza, FoodType.from(22));
    assertEquals(FoodType.tomatoBase, FoodType.from(23));
    assertEquals(FoodType.cheese, FoodType.from(24));
    assertEquals(FoodType.slicedCheese, FoodType.from(25));
    assertEquals(FoodType.burnt, FoodType.from(26));
  }

  @Test
  public void getFoodTypeFromIDInvalid() {
    assertNull(FoodType.from(27));
    assertNull(FoodType.from(0));
    assertNull(FoodType.from(-1));
  }

}

