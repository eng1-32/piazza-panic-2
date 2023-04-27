package com.devcharles.piazzapanic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.devcharles.piazzapanic.scene2d.Slideshow;
import com.devcharles.piazzapanic.utility.Difficulty;
import com.devcharles.piazzapanic.utility.Difficulty.Level;

/**
 * Main menu of the game, transitions the player to the Tutorial {@link Slideshow} on button press
 */
public class MainMenuScreen extends ApplicationAdapter implements Screen {

  final PiazzaPanic game;
  OrthographicCamera camera;
  private final Stage stage;
  private final Batch batch;
  private final Sprite sprite;

  public MainMenuScreen(final PiazzaPanic game) {

    this.game = game;
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 1280, 720);
    batch = new SpriteBatch();

    sprite = new Sprite(game.assetManager.get("mainMenuImage.png", Texture.class));
    sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    stage = new Stage(new ScreenViewport());

    Label.LabelStyle menuTitleStyle = new Label.LabelStyle();
    menuTitleStyle.font = game.assetManager.get("craftacular/raw/font-title-export.fnt",
        BitmapFont.class);

    Label title = new Label("Piazza Panic", menuTitleStyle);

    final Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    final Table scenarioTable = new Table();
    scenarioTable.center();
    scenarioTable.setFillParent(true);
    final Label customerLabel = new Label("Customers: 5", menuTitleStyle);
    final Slider customerSlider = new Slider(1, 9, 1, false, game.skin);
    customerSlider.setValue(5);
    TextButton startScenarioButton = new TextButton("Start", game.skin);
    TextButton scenarioBackBtn = new TextButton("Back", game.skin);
    customerSlider.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        customerLabel.setText(
            String.format("Customers: %d", (int) customerSlider.getValue()));
      }
    });
    startScenarioButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.setScreen(new Slideshow(game, Slideshow.Type.tutorial,
            new ScenarioGameScreen(game, null, (int) customerSlider.getValue())));
        dispose();
      }
    });
    scenarioBackBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        scenarioTable.remove();
        stage.addActor(root);
      }
    });
    scenarioTable.add(customerLabel).padBottom(60);
    scenarioTable.row();
    scenarioTable.add(customerSlider).width(370).padBottom(30);
    scenarioTable.row();
    scenarioTable.add(startScenarioButton).padBottom(60);
    scenarioTable.row();
    scenarioTable.add(scenarioBackBtn);
    scenarioTable.row();

    final Table endlessDifficulty = new Table();
    endlessDifficulty.center();
    endlessDifficulty.setFillParent(true);
    Label difficultyLabel = new Label("Difficulty", menuTitleStyle);
    TextButton easyBtn = new TextButton("Easy", game.skin);
    TextButton mediumBtn = new TextButton("Medium", game.skin);
    TextButton hardBtn = new TextButton("Hard", game.skin);
    TextButton backBtn = new TextButton("Back", game.skin);
    easyBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        startEndless(Level.EASY);
      }
    });

    mediumBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        startEndless(Level.MEDIUM);
      }
    });

    hardBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        startEndless(Level.HARD);
      }
    });

    backBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        endlessDifficulty.remove();
        stage.addActor(root);
      }
    });

    endlessDifficulty.add(difficultyLabel).padBottom(60);
    endlessDifficulty.row();
    endlessDifficulty.add(easyBtn).padBottom(30);
    endlessDifficulty.row();
    endlessDifficulty.add(mediumBtn).padBottom(30);
    endlessDifficulty.row();
    endlessDifficulty.add(hardBtn).padBottom(60);
    endlessDifficulty.row();
    endlessDifficulty.add(backBtn);

    TextButton startScenarioModeBtn = new TextButton("Start scenario mode", game.skin);
    TextButton startEndlessModeBtn = new TextButton("Start endless mode", game.skin);
    TextButton loadEndlessModeBtn = new TextButton("Load endless mode", game.skin);
    TextButton exitBtn = new TextButton("Exit game", game.skin);

    // Checks if button is clicked and if clicked goes onto the tutorial
    startScenarioModeBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        root.remove();
        stage.addActor(scenarioTable);
      }
    });
    // Checks if button is clicked and if clicked goes onto the tutorial
    startEndlessModeBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        root.remove();
        stage.addActor(endlessDifficulty);
      }
    });
    // Checks if button is clicked and if clicked goes onto the tutorial
    loadEndlessModeBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.setScreen(new Slideshow(game, Slideshow.Type.tutorial,
            new EndlessGameScreen(game, null, true, null)));
        dispose();
      }
    });
    exitBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        dispose();
        Gdx.app.exit();
      }
    });

    root.add(title).expandX().padBottom(120);
    root.row();
    root.add(startScenarioModeBtn).padBottom(30);
    root.row();
    root.add(startEndlessModeBtn).padBottom(30);
    root.row();
    root.add(loadEndlessModeBtn).padBottom(30);
    root.row();
    root.add(exitBtn);

  }

  public void startEndless(Difficulty.Level difficultyLevel) {
    game.setScreen(new Slideshow(game, Slideshow.Type.tutorial,
        new EndlessGameScreen(game, null, false, Difficulty.createDifficulty(difficultyLevel))));
    dispose();
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    // draws everything (dont change this order unless you know what youre doing)
    game.assetManager.update(16);
    batch.begin();
    sprite.draw(batch);
    batch.end();
    stage.act();
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void hide() {

  }

  public void dispose() {
    stage.dispose();
  }
}
