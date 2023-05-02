package com.devcharles.piazzapanic.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

/**
 * @author Andrey Samoilov
 * @author Matthew Fitzpatrick
 */
public class KeyboardInput implements InputProcessor {

  public boolean left, right, up, down;

  public boolean changeCooks;

  public boolean putDown;

  public boolean pickUp;

  public boolean interact;

  public boolean disableHud;
  public boolean swap;
  public int swapNum;

  public void clearInputs() {
    left = false;
    right = false;
    up = false;
    down = false;
    changeCooks = false;
    putDown = false;
    pickUp = false;
    interact = false;
    disableHud = false;
    swap = false;
    swapNum = 0;
  }

  @Override
  public boolean keyDown(int keycode) {
    boolean processed = true;
    switch (keycode) {
      case Keys.LEFT:
      case Keys.A:
        left = true;
        break;
      case Keys.RIGHT:
      case Keys.D:
        right = true;
        break;
      case Keys.UP:
      case Keys.W:
        up = true;
        break;
      case Keys.DOWN:
      case Keys.S:
        down = true;
        break;
      case Keys.F:
        putDown = true;
        break;
      case Keys.E:
        changeCooks = true;
        break;
      case Keys.R:
        pickUp = true;
        break;
      case Keys.Q:
        interact = true;
        break;
      case Keys.H:
        disableHud = true;
        break;
      case Keys.NUM_1:
        swap = true;
        swapNum = 0;
        break;
      case Keys.NUM_2:
        swap = true;
        swapNum = 1;
        break;
      case Keys.NUM_3:
        swap = true;
        swapNum = 2;
        break;
      case Keys.NUM_4:
        swap = true;
        swapNum = 3;
        break;
      case Keys.NUM_5:
        swap = true;
        swapNum = 4;
        break;
      case Keys.NUM_6:
        swap = true;
        swapNum = 5;
        break;
      case Keys.NUM_7:
        swap = true;
        swapNum = 6;
        break;
      case Keys.NUM_8:
        swap = true;
        swapNum = 7;
        break;
      case Keys.NUM_9:
        swap = true;
        swapNum = 8;
        break;
      case Keys.NUM_0:
        swap = true;
        swapNum = 9;
        break;
      default:
        processed = false;
    }
    return processed;
  }

  @Override
  public boolean keyUp(int keycode) {
    boolean processed = true;
    switch (keycode) {
      case Keys.LEFT:
      case Keys.A:
        left = false;
        break;
      case Keys.RIGHT:
      case Keys.D:
        right = false;
        break;
      case Keys.UP:
      case Keys.W:
        up = false;
        break;
      case Keys.DOWN:
      case Keys.S:
        down = false;
        break;
      case Keys.E:
        changeCooks = false;
        break;
      case Keys.F:
        putDown = false;
        break;
      case Keys.R:
        pickUp = false;
        break;
      case Keys.Q:
        interact = false;
        break;
      case Keys.H:
        disableHud = false;
        break;
      case Keys.NUM_1:
      case Keys.NUM_2:
      case Keys.NUM_3:
      case Keys.NUM_4:
      case Keys.NUM_5:
      case Keys.NUM_6:
      case Keys.NUM_7:
      case Keys.NUM_8:
      case Keys.NUM_9:
        swap = false;
        swapNum = 0;
        break;
      default:
        processed = false;
    }
    return processed;
  }

  @Override
  public boolean keyTyped(char character) {
    boolean processed = true;
    switch (character) {
      default:
        processed = false;
    }
    return processed;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }

}
