package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Andrey Samoilov
 */
public class AnimationComponent implements Component {

  public Animation<TextureRegion> animation = null;
}
