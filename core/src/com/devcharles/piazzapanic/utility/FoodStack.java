package com.devcharles.piazzapanic.utility;

import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import java.util.ArrayDeque;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.components.ItemComponent;
import java.util.ArrayList;

public class FoodStack extends ArrayList<Entity> {

  private Engine engine;

  public void init(Engine e) {
    engine = e;
  }

  public final int capacity = 12;

  /**
   * Put a new food into inventory, use this instead of {@code FoodStack.push(Entity food)} as it
   * binds the item location to the player and maintains a maximum inventory size of 12.
   *
   * @param food
   * @param cook
   * @return
   */
  public boolean pushItem(Entity food, Entity cook) {
    if (this.size() < capacity) {
      ItemComponent item = engine.createComponent(ItemComponent.class);
      item.holderTransform = Mappers.transform.get(cook);
      food.add(item);
      this.push(food);
      return true;
    }
    return false;
  }

  /**
   * Used internally, please use {@code FoodStack.pushItem(Entity food)} instead.
   */
  public void push(Entity food) {
    this.add(0, food);
    setVisibility(this.size(), null);
  }


  /* (non-Javadoc)
   * Recreation of the pop function
   * @see java.util.ArrayDeque#pop()
   */
  public Entity pop() {
    Entity e = this.get(0);
    this.remove(0);
    e.remove(ItemComponent.class);
    setVisibility(this.size(), e);
    return e;
  }

  protected void setVisibility(int size, Entity e) {
    if (size > 1) {
      for (Entity food : this) {
        Mappers.transform.get(food).isHidden = true;
      }
    } else if (size == 1) {
      Mappers.transform.get(this.peek()).isHidden = false;
    }

    if (e != null) {
      Mappers.transform.get(e).isHidden = false;
    }
  }

  public Entity peek(){
    return this.get(0);
  }

  public void move(int index){
    if(this.size() > 1 && index < this.size()){
      Entity temp = this.get(index);
      this.remove(index);
      this.add(0, temp);
    }
  }

}
