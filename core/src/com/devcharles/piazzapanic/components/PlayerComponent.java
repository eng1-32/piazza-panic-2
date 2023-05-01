package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Andrey Samoilov
 */
public class PlayerComponent implements Component {

  public boolean putDown = false;
  public boolean pickUp = false;
  public boolean interact = false;
}
