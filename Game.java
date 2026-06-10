/**

        * Game: Wavebound.io

        * Author: Jacky Lui

        * Date Created: May, 28, 2026

        * Date Last Modified: June 10, 2026

        */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.AnimationTimer;
import java.util.HashMap;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.Random;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;
import javafx.scene.image.ImageView;

public class Game extends Application {
	//Declaring variables for player
	int playerHealth = 100;
	int maxPlayerHealth = 100;
	double playerSpeed = 3.0;
	int playerXp = 0;
	int maxPlayerXp = 100;
	int playerLevel = 0;
	boolean playerLeveledUp = false;
	int enemiesDefeated = 0;
	int score = 0;
	Rectangle player;

	//Contains all movement keys
	HashMap<KeyCode, Boolean> keyState = new HashMap<>();
	//Contains all active enemies
	ArrayList<Enemy> enemiesList = new ArrayList<>();
	//Contains all projectiles active
	ArrayList<ProjectileAbility> projectileList = new ArrayList<>();

	//Time elapsed for game
	AnimationTimer timer;
	long startTime;
	long minutes;
	long seconds;
	Label time = new Label("0:00");

	//Spawnrate
	long lastSpawnTime;
	long lastBossSpawnTime;
	double spawnRate = 2 * 1e9;
	double bossSpawnRate = 2 * 60e9;
	int spawnLimit = 11;

	//Screen dimensions for the user
	Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
	double screenWidth = primaryScreenBounds.getWidth();
	double screenHeight = primaryScreenBounds.getHeight();

	//Map
	Pane game;
	Scene menuScene;

	//Healthbar
	Rectangle healthBar;
	Rectangle healthBackground;
	double healthBarMaxWdith = 200.0;
	Text healthNumber = new Text(playerHealth + "/ " + maxPlayerHealth);

	//Xpbar
	Rectangle xpBar;
	Rectangle xpBarBackground;
	double xpBarMaxWidth = 200.0;
	Text lvlText = new Text( "level " + playerLevel);

	//Enemies killed;
	Text defeatedText = new Text("Slain: " + enemiesDefeated);

	//Random number generator
	Random random = new Random();

	//Mouse Click
	double mouseClickX;
	double mouseClickY;
	boolean isMouseClicked;

	//Abilities (If stack is zero, player has not obtained it)
	int maxStack = 3;
	int lifeStealStack = 0;
	int boomerangStack = 0;
	int auraStack = 0;
	
	//All image patterns
	ImagePattern fireballPattern;
	ImagePattern boomerangPattern;
	ImagePattern normalSlimePattern;
	ImagePattern tankSlimePattern;
	ImagePattern fastSlimePattern;
	ImagePattern bossSlimePattern;
	ImagePattern auraPattern;

	//Cooldowns
	long lastDamageTime;
	long lastFireballTime;
	long lastAuraTime;
	double damageCooldown = 5 * 1e8;
	double weaponCooldown = 0.5 * 1e9;

	//Difficulty
	int easyMultiplyer = 1;
	int mediumMultiplyer = 2;
	int hardMultiplyer = 3;
	int difficulty;

	//Score
	int enemiesMultiplyer = 10;
	int timeMultiplyer = 5;
	int levelMultiplyer = 2;

	//knockback
	double enemyKnockback = 0.5;
	double playerKnockback = 45;

	//normal enemy statistics 
	int normalEnemyHealth = 40;
	int normalEnemyDamage = 10;
	double normalEnemySpeed = 1.0;
	int normalEnemyXp = 10;

	//fast enemy statisitcs
	int fastEnemyHealth = 25;
	int fastEnemyDamage = 5;
	double fastEnemySpeed = 2.0;
	int fastEnemyXp = 15;

	//tank enemy statisitcs
	int tankEnemyHealth = 70;
	int tankEnemyDamage = 7;
	double tankEnemySpeed = 0.5;
	int tankEnemyXp = 20;

	//boss enemy statisitcs
	int bossEnemyHealth = 300;
	int bossEnemyDamage = 20;
	double bossEnemySpeed = 0.3;
	int bossEnemyXp = 100;

	//Fireball statisitcs
	double fireballSpeed = 4.0;
	int fireballDamage = 25;
	double fireballCooldown = 0.5 * 1e9;

	//Boomerang statisitcs
	double boomerangSpeed = 3.0;
	int boomerangDamage = 20;
	double boomerangCooldown = 0.5 * 1e9;
	double boomerangReturnSpeed = 3.0;

	//Lifesteal statistics
	double lifestealChance = 0.1;
	int lifestealAmount = 10;
	double lifestealCooldown = 2 * 1e9;
	PassiveAbility lifeStealEnable;
	boolean lifeStealInitialized = false;

	//Aura statistics
	int auraDamage = 20;
	double auraCooldown = 1 * 1e9;
	PassiveAbility auraEnable;
	boolean auraInitialized = false;
	int auraRadiusMultiplyer = 10;

	public static void main(String[] args) {
		launch();
	}

	//Menu
	@Override
	public void start(Stage menu) {
		Text title = new Text("Wavebound.io"); //Game name
		title.setTextAlignment(TextAlignment.CENTER);
		title.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 100px; -fx-font-weight: bold; -fx-fill: white");
		DropShadow shadow = new DropShadow(); //Creates a shadow effect on text
		shadow.setOffsetX(4.0f);
		shadow.setOffsetY(4.0f);
		title.setEffect(shadow);
		Text text1 = new Text("Survive the Waves....");
		Text text2 = new Text("Choose Difficulty\n");
		text1.setStyle("-fx-font-family: 'Impact'; -fx-font-weight: bold; -fx-font-size: 25px; -fx-fill: LEMONCHIFFON");
		text2.setStyle("-fx-font-family: 'Impact'; -fx-font-weight: bold; -fx-font-size: 25px; -fx-fill: LEMONCHIFFON");

		//Button Style
		String buttonStyle = 
			"-fx-background-color: #2c276e; " +
		    "-fx-border-color: #2c2c2c; " +
		    "-fx-border-width: 4px; " +
		    "-fx-border-radius: 10px; " +
		    "-fx-background-radius: 14px; " +
		    "-fx-text-fill: white; " +
		    "-fx-font-family: 'Monospaced'; " +
		    "-fx-font-size: 16px; " +
		    "-fx-padding: 10px;" +
		    "-fx-pref-width: 200px; " +
		    "-fx-pref-height: 5px; " +
		    "-fx-text-alignment: center;";

		//Buttons to begin playing the game
		Button easy = new Button("[    Easy    ]");
		Button medium = new Button("[   Medium   ]");
		Button hard = new Button("[    Hard    ]");

		easy.setStyle(buttonStyle);
		medium.setStyle(buttonStyle);
		hard.setStyle(buttonStyle);
		
		VBox layout = new VBox(13);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(title, text1, text2, easy, medium, hard);

		//Determines if button is pressed and difficulty
		easy.setOnAction(event -> {
			difficulty = easyMultiplyer;
			gameStart(menu);
		});

		medium.setOnAction(event -> {
			difficulty = mediumMultiplyer;
			gameStart(menu);
		});

		hard.setOnAction(event -> {
			difficulty = hardMultiplyer;
			gameStart(menu);
		});

		//Creates a image for the background
		Image backgroundImage = new Image("/assets/background/menu.png", false);
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
		Rectangle background = new Rectangle(screenWidth, screenHeight);
		background.setCache(true);
		background.setFill(backgroundPattern);
		background.setMouseTransparent(true);
		StackPane main = new StackPane();
		main.getChildren().addAll(background, layout);

		Scene scene = new Scene(main, screenWidth, screenHeight);
		menuScene = scene;
		menu.setScene(menuScene);
		menu.setFullScreen(true);
		menu.show();
	}

	//Stage for the game itself
	public void gameStart(Stage gameStart) {
		//Loads all images at once
		fireballPattern = new ImagePattern(new Image("/assets/abilities/fireball.png"));
		boomerangPattern = new ImagePattern(new Image("/assets/abilities/boomerang.png"));
		auraPattern = new ImagePattern(new Image("/assets/abilities/aura.png"));
		normalSlimePattern = new ImagePattern(new Image("/assets/slimes/normal.png"));
		tankSlimePattern = new ImagePattern(new Image("/assets/slimes/tank.png"));
		fastSlimePattern = new ImagePattern(new Image("/assets/slimes/fast.png"));
		bossSlimePattern = new ImagePattern(new Image("/assets/slimes/boss.png"));

		//Creates background for the game
		game = new Pane();
		Image backgroundImage = new Image("/assets/background/background.png", false);
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
		Rectangle background = new Rectangle(screenWidth, screenHeight);
		background.setCache(true);
		background.setFill(backgroundPattern);
		game.getChildren().add(background);

		//Creates a player with an image 
		player = new Rectangle(60, 60, Color.TRANSPARENT);
		Image playerImage = new Image("/assets/player.png", false);
		ImagePattern playerPattern = new ImagePattern(playerImage);
		player.setFill(playerPattern);
		player.setCache(true);

		//Resets old values
		playerHealth = 100;
		playerXp = 0;
		enemiesDefeated = 0;
		score = 0;
		boomerangStack = 0;
		auraStack = 0;
		lifeStealStack = 0;
		playerLevel = 0;
		playerLeveledUp = false;
		auraInitialized = false;
		lifeStealInitialized = false;
		enemiesList.clear();
		projectileList.clear();
		keyState.clear();

		//Reset all UI
		healthNumber.setText(playerHealth + "/ " + maxPlayerHealth);
		lvlText.setText("level " + playerLevel);
		defeatedText.setText("Slain: " + enemiesDefeated);

		//Timer
		startTime = System.nanoTime();
		lastSpawnTime = System.nanoTime();
		lastBossSpawnTime = System.nanoTime();
		lastDamageTime = System.nanoTime();
		lastFireballTime = System.nanoTime();
		lastAuraTime = System.nanoTime();
		DropShadow shadow = new DropShadow(); //Creates a shadow effect on text
		shadow.setOffsetX(4.0f);
		shadow.setOffsetY(4.0f);
		time.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white");
		time.setEffect(shadow);

		//Healthbar
		healthBar = new Rectangle(healthBarMaxWdith, 20, Color.RED);
		healthBackground = new Rectangle(healthBarMaxWdith, 20, Color.BLACK);
		healthNumber.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: red;");
		healthBackground.setX(20);
		healthBackground.setY(20);
		healthBar.setX(20);
		healthBar.setY(20);
		healthNumber.setX(240);
		healthNumber.setY(37);
		healthBar.setWidth(healthBarMaxWdith);

		//Xpbar
		xpBar = new Rectangle(0, 20, Color.BLUE);
		xpBarBackground = new Rectangle(xpBarMaxWidth, 20, Color.BLACK);
		lvlText.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: blue;");
		xpBarBackground.setX(20);
		xpBarBackground.setY(50);
		xpBar.setX(20);
		xpBar.setY(50);
		lvlText.setX(240);
		lvlText.setY(67);
		xpBar.setWidth(0);

		//Enemies killed counter
		defeatedText.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");
		defeatedText.setY(100);
		defeatedText.setX(20);

		game.getChildren().addAll(player, time, healthBackground, healthBar, healthNumber, xpBarBackground, xpBar, lvlText, defeatedText);
		Scene scene = new Scene(game, screenWidth, screenHeight);
		gameStart.setScene(scene);
		game.requestFocus();
		gameStart.setFullScreen(true);
		gameStart.show();

		//Postions the player in the center of the screen and the timer in the top left corner
		player.setX((screenWidth / 2) - (player.getWidth() / 2));
		player.setY((screenHeight / 2) - (player.getHeight() / 2));
		time.setLayoutX((screenWidth / 2) - time.getWidth() / 2);
		time.setLayoutY(20);
		
		//Adds key pressed to hashmap
		game.setOnKeyPressed(event -> keyState.put(event.getCode(), true));
		
		game.setOnKeyReleased(event -> keyState.put(event.getCode(), false));

		//Adds Mouse click
		game.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) { 
				game.requestFocus();
				mouseClickX = event.getSceneX();
				mouseClickY = event.getSceneY();
				isMouseClicked = true;
			}
			});

		//Animation timer for smooth gameplay
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				movement(gameStart);
				timeElapsed(now);
				spawnEnemy(now);
				enemyDetection();
				playerDamageCheck(now);
				moveProjectile();
				projectileHitDetection(now);
				auraAbility(now);
				boomerangAbility(now);

				if (isMouseClicked) {
				if (now - lastFireballTime >= weaponCooldown) { //Cooldown for fireball
					projectileFireball(player.getX(), player.getY());
					lastFireballTime = now; //Resets cooldown
				}
					isMouseClicked = false;
				}

				if (playerHealth <= 0) { //Ends the game when health reaches zero
					end(gameStart);
					stop();
				}

				if (playerLeveledUp) { //Shows ability screen if the player has gained a level
					playerLeveledUp = false;
					if (!(auraStack == maxStack && boomerangStack == maxStack && lifeStealStack == maxStack)) {
						abilitiesMenu(game, scene);
					}
				}

				//Ensures UI elements are always at the front
				healthBackground.toFront();
				healthBar.toFront();
				healthNumber.toFront();
				xpBarBackground.toFront();
				xpBar.toFront();
				lvlText.toFront();
				defeatedText.toFront();
				time.toFront();
			}
		};
		timer.start();
	}

	//If player leveled up, abilities menu will show
	public void abilitiesMenu(Pane game, Scene gameScene) {
		timer.stop();

		StackPane abilitiesMenu = new StackPane();
		abilitiesMenu.setPrefSize(screenWidth, screenHeight);
		HBox layout = new HBox(30);
		layout.setAlignment(Pos.CENTER);

		//Creates visual for the ability card
		String cardStyle =
		    "-fx-background-color: #121212; " +
		    "-fx-border-color: #2c2c2c; " +
		    "-fx-border-width: 4px; " +
		    "-fx-border-radius: 10px; " +
		    "-fx-background-radius: 14px; " +
		    "-fx-text-fill: white; " +
		    "-fx-font-family: 'Monospaced'; " +
		    "-fx-font-size: 16px; " +
		    "-fx-padding: 25px;" +
		    "-fx-pref-width: 280px; " +
		    "-fx-pref-height: 450px; " +
		    "-fx-text-alignment: center;";
		
		//Makes the entire card a button
		Button lifeSteal = new Button("Life Steal\n\nGain health \nfrom killing slimes.\n\nLvl " + lifeStealStack + "/" + maxStack + "\n\nPassive");
		Button aura = new Button("Aura\n\nCreates a\ndamaging radius.\n\nLvl " + auraStack + "/" + maxStack + "\n\nActive");
		Button boomerang = new Button("Boomerang\n\nThrows a returning \nprojectile.\n\nLvl " + boomerangStack + "/" + maxStack + "\n\nPassive");

		//Determines if player has gotten the max stack of each abilitiy
		if (lifeStealStack == maxStack) {
			lifeSteal.setText("Life Steal\n\nGain health \nfrom killing slimes.\n\nMax level" + "\n\nPassive Ability");
		}

		if (auraStack == maxStack) {
			aura.setText("Aura\n\nCreates a\ndamaging raius.\n\nMax level" + "\n\nActive Ability");
		}

		if (boomerangStack == maxStack) {
			boomerang.setText("Boomerang\n\nThrows a returning\nprojectile.\n\nMax level" + "\n\nActive Ability");
		}

		//Loads images for the ability icon
		Image imageAura = new Image("/assets/abilities/aura.png", 64, 64, true, true);
		Image imageLifesteal = new Image("/assets/abilities/lifesteal.png", 64, 64, true, true);
		Image imageBoomerang = new Image("/assets/abilities/boomerang.png", 64, 64, true, true);
		lifeSteal.setGraphic(new ImageView(imageLifesteal));
		aura.setGraphic(new ImageView(imageAura));
		boomerang.setGraphic(new ImageView(imageBoomerang));
		aura.setContentDisplay(ContentDisplay.TOP);
		boomerang.setContentDisplay(ContentDisplay.TOP);
		lifeSteal.setContentDisplay(ContentDisplay.TOP);

		lifeSteal.setStyle(cardStyle);
		aura.setStyle(cardStyle);
		boomerang.setStyle(cardStyle);

		layout.getChildren().addAll(aura, lifeSteal, boomerang);
		StackPane.setAlignment(layout, Pos.CENTER);
		abilitiesMenu.getChildren().add(layout);
		abilitiesMenu.setStyle("-fx-background-color: #000000bd;");
		game.getChildren().add(abilitiesMenu);

		//Action event for each ability card (button)
		if (lifeStealStack != maxStack) {
			lifeSteal.setOnAction (event -> {
				game.getChildren().remove(abilitiesMenu);
				game.requestFocus();
				lifeStealStack++;
				timer.start();
			});
		}
		if (auraStack != maxStack) {
			aura.setOnAction (event -> {
				game.getChildren().remove(abilitiesMenu);
				game.requestFocus();
				auraStack++;
				timer.start();
			});
		}
		if (boomerangStack != maxStack) {
			boomerang.setOnAction(event -> {
				game.getChildren().remove(abilitiesMenu);
				game.requestFocus();

				boomerangStack++;
				timer.start();
			});
		}
	}

	//Will run if the player reaches zero health
	public void end (Stage end) {
		score = (int) (((minutes * timeMultiplyer) + (playerLevel * levelMultiplyer) + (enemiesDefeated * enemiesMultiplyer))) * difficulty;

		Text gameOver = new Text("Game Over");
		gameOver.setStyle("-fx-font: 80px 'Impact'; -fx-font-weight: bold; -fx-fill: red");
		Text scoreText = new Text("Score: " + score);
		scoreText.setStyle("-fx-font: 24px 'Impact'; -fx-font-weight: bold; -fx-fill: red;");
		Button button = new Button("Click to Play Again!");
		button.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 15px; -fx-fill: black");
		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(gameOver, scoreText, button);

		//Game over background
		Image backgroundImage = new Image("/assets/background/gameOver.png", false);
		ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
		Rectangle background = new Rectangle(screenWidth, screenHeight);
		background.setCache(true);
		background.setFill(backgroundPattern);
		background.setMouseTransparent(true);

		StackPane main = new StackPane();
		main.getChildren().addAll(background, layout);
		Scene scene = new Scene(main, screenWidth, screenHeight);
		end.setScene(scene);
		end.setFullScreen(true);
		end.show();
		//Button to restart game
		button.setOnAction(event -> gameStart(end));
	}

	//Runs if the escape key is pressed (if you want to leave the current game)
	public void exitMenu(Pane game, Stage gameStart) {
		timer.stop();

		String buttonStyle = 
			"-fx-background-color: #2c276e; " +
		    "-fx-border-color: #2c2c2c; " +
		    "-fx-border-width: 4px; " +
		    "-fx-border-radius: 10px; " +
		    "-fx-background-radius: 14px; " +
		    "-fx-text-fill: white; " +
		    "-fx-font-family: 'Monospaced'; " +
		    "-fx-font-size: 16px; " +
		    "-fx-padding: 10px;" +
		    "-fx-pref-width: 100px; " +
		    "-fx-pref-height: 40px; " +
		    "-fx-text-alignment: center;";

		Text prompt = new Text("Do you want to leave the game?");
		Button yes = new Button("Yes");
		Button no = new Button("No");
		yes.setStyle(buttonStyle);
		no.setStyle(buttonStyle);
		prompt.setStyle("-fx-font: 30px 'Impact'; -fx-font-weight: bold; -fx-fill: white");

		HBox button = new HBox(15, yes, no);
		button.setAlignment(Pos.CENTER);
		VBox menu = new VBox(30, prompt, button);
		menu.setAlignment(Pos.CENTER);
		StackPane layout = new StackPane(menu);
		layout.setPrefSize(screenWidth, screenHeight);
		StackPane.setAlignment(layout, Pos.CENTER);
		game.getChildren().add(layout);
		gameStart.setFullScreen(true);
		layout.setStyle("-fx-background-color: #000000bd;");

		//Will not restart game
		no.setOnAction(event -> {
			game.getChildren().remove(layout);
			timer.start();
			game.requestFocus();
		});

		//Will bring you back to home menu screen
		yes.setOnAction(event -> {
			game.getChildren().remove(layout);
			timer.start();
			gameStart.setScene(menuScene);
		});
	}

	//Determines how to move with WASD using KeyCode
	public void movement(Stage gameStart) {
		//If no key is detect a default false value will be mapped
		if (keyState.getOrDefault(KeyCode.W, false)) {
			player.setY(player.getY() - playerSpeed);
		}

		if (keyState.getOrDefault(KeyCode.S, false)) {
			player.setY(player.getY() + playerSpeed);
		}

		if (keyState.getOrDefault(KeyCode.A, false)) {
			player.setX(player.getX() - playerSpeed);
		}

		if (keyState.getOrDefault(KeyCode.D, false)) {
			player.setX(player.getX() + playerSpeed);
		}

		if (keyState.getOrDefault(KeyCode.ESCAPE, false)) {
			exitMenu(game, gameStart);
			keyState.put(KeyCode.ESCAPE, false);
		}

		//Bounderies for the game so player cannot leave map
		if(player.getX() < 0) {
			player.setX(0);
		}
		if(player.getX() > screenWidth - player.getWidth()) {
			player.setX(screenWidth - player.getWidth());
		}

		if(player.getY() < 0) {
			player.setY(0);
		}
		if(player.getY() > screenHeight - player.getHeight()) {
			player.setY(screenHeight - player.getHeight());
		}
	}

	//Timer for the game
	public void timeElapsed(long now) {
		double totalSeconds = (now - startTime) * 1e-9;
		minutes = (long) (totalSeconds / 60);
		seconds = (long) (totalSeconds % 60);
		String doubleDigitMinutes = "";
		String doubleDigitSeconds = "";
		if (seconds < 10) { //Allows for double digits when the seconds are one digit
			doubleDigitSeconds = "0";
		}
		if (minutes < 10) { //Allows for double digits when the minutes are one digit
			doubleDigitMinutes = "0";
		}
		time.setText( doubleDigitMinutes + minutes + " : " +  doubleDigitSeconds + seconds);
	}

	//Spawns in enemies
	public void spawnEnemy(long now) {
		if (enemiesList.size() < spawnLimit) {
		if (minutes >= 2) { //Spawns the boss after two minutes
			if (now - lastBossSpawnTime > bossSpawnRate) {
				Enemy newEnemy = spawnLocation("boss", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastBossSpawnTime = now; //Resets cooldown
			}
		}
		if (minutes >= 1) { //Spawns all type of enemies when timer reaches one minute
			if ((now - lastSpawnTime) > spawnRate) {
				Enemy newEnemy = spawnLocation(randomEnemy(), 0, 0); //uses randomEnemy and spawnLocation function
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now; 
			}
		}
		else {
			if ((now - lastSpawnTime) > spawnRate) { //Spawns only normal enemies
				Enemy newEnemy = spawnLocation("normal", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now;
			}
		}
	}
}

	//Pathfinds to players location
	public void enemyDetection() {
		for (int i = 0; i < enemiesList.size(); i++) { //Loops through all enemies
			Enemy currentEnemy = enemiesList.get(i);
			for (int j = i + 1; j < enemiesList.size(); j++) {
			Enemy secondEnemy = enemiesList.get(j);
			double enemyX = currentEnemy.enemyShape.getX(); //Gets the location of enemy
			double enemyY = currentEnemy.enemyShape.getY();
			double secondEnemyX = secondEnemy.enemyShape.getX();
			double secondEnemyY = secondEnemy.enemyShape.getY();
			
			//Ensures enemies do not collide and clip into each other
			if (checkCollisionRectangle(secondEnemy.enemyShape, currentEnemy.enemyShape)) {

				if (enemyY > secondEnemyY) {
					currentEnemy.enemyShape.setY(enemyY + enemyKnockback);
					secondEnemy.enemyShape.setY(secondEnemyY - enemyKnockback);
				}

				else if (enemyY < secondEnemyY) {
					currentEnemy.enemyShape.setY(enemyY - enemyKnockback);
					secondEnemy.enemyShape.setY(secondEnemyY + enemyKnockback);
				}

				if (enemyX > secondEnemyX) {
					currentEnemy.enemyShape.setX(enemyX + enemyKnockback);
					secondEnemy.enemyShape.setX(secondEnemyX - enemyKnockback);
				}

				else if (enemyX < secondEnemyX) {
					currentEnemy.enemyShape.setX(enemyX - enemyKnockback);
					secondEnemy.enemyShape.setX(secondEnemyX + enemyKnockback);
				}
			}
		}
	}
		for (int i = 0; i < enemiesList.size(); i++) { //Loops through all enemies
			Enemy currentEnemy = enemiesList.get(i);
			double enemyX = currentEnemy.enemyShape.getX(); //Gets the location of enemy
			double enemyY = currentEnemy.enemyShape.getY();
			double enemySpeed = currentEnemy.enemySpeed;
			//Ensures enemy does not clip into player (knockback)
			if (checkCollisionRectangle(player, currentEnemy.enemyShape)) {

				if (enemyY > player.getY()) {
					currentEnemy.enemyShape.setY(enemyY + playerKnockback);
				}

				else if (enemyY < player.getY()) {
					currentEnemy.enemyShape.setY(enemyY - playerKnockback);
				}

				if (enemyX > player.getX()) {
					currentEnemy.enemyShape.setX(enemyX + playerKnockback);
				}

				else if (enemyX < player.getX()) {
					currentEnemy.enemyShape.setX(enemyX - playerKnockback);
				}
			}

			//Pathfinding to player
			else {
				if (enemyY > player.getY()) {
					currentEnemy.enemyShape.setY(enemyY - enemySpeed);
				}

				else if (enemyY < player.getY()) {
					currentEnemy.enemyShape.setY(enemyY + enemySpeed);
				}

				if (enemyX > player.getX()) {
					currentEnemy.enemyShape.setX(enemyX - enemySpeed);
				}

				else if (enemyX < player.getX()) {
					currentEnemy.enemyShape.setX(enemyX + enemySpeed);
				}
			}
		}
	}

	//Checks if enemy is touching the player (Collision Detection rectangle)
	public boolean checkCollisionRectangle(Rectangle player, Rectangle enemy) {
		return (player.getBoundsInParent().intersects(enemy.getBoundsInParent())); //checks if player intersects with an enemy
	}

	//Checks if enemy is touching the player (Collision Detection for circle)
	public boolean checkCollisionCircle(Circle ability, Rectangle enemy) {
		return ability.getBoundsInParent().intersects(enemy.getBoundsInParent()); //checks if player intersects with an enemy
	}


	//creates a random spawn location for an enemy around the border of the screen
	public Enemy spawnLocation(String enemyType, double coordX, double coordY) {
		int possibility = random.nextInt(1, 5); //4 options (top, bottom, left, right)
		if (possibility == 1) { //spawns on the left border
			coordX = primaryScreenBounds.getMinX();
			coordY = random.nextDouble(primaryScreenBounds.getMaxY());
		}
		if (possibility == 2) { //spawns on the right border
			coordX = primaryScreenBounds.getMaxX();
			coordY = random.nextDouble(primaryScreenBounds.getMaxY());
		}
		if (possibility == 3) { //spanws on the top border
			coordX = random.nextDouble(primaryScreenBounds.getMaxX());
			coordY = primaryScreenBounds.getMinY();
		}
		if (possibility == 4) { //spawns on the bottom border
			coordX = random.nextDouble(primaryScreenBounds.getMaxX());
			coordY = primaryScreenBounds.getMaxY();
		}
		return new Enemy(enemyType, coordX, coordY); //returns random set of coordinates
	}

	//Generates random enemy
	public String randomEnemy() {
		String enemyType = "normal"; //First possibilty
		int possibility = random.nextInt( 3);
		if (possibility == 0) { //Second possibilty
			enemyType = "fast";
		}
		else if (possibility == 1) { //Third possibility
			enemyType = "tank";
		}
		return enemyType;
	}

	//Checks if player is taking damage and updates healthbar
	public void playerDamageCheck(long now) {
		if (now - lastDamageTime > damageCooldown) {
			for (int i = enemiesList.size() - 1; i >= 0; i--) { //loops backwards (prevents detecting removed enemy)
				Enemy currentEnemy = enemiesList.get(i); //gets currentEnemy with arrayList
				if (checkCollisionRectangle(player, currentEnemy.enemyShape)) { //Checks if enemy touches player
					lastDamageTime = now;
					playerHealth -= currentEnemy.enemyDamage;
					int newHealth = Math.max(0, playerHealth);
					healthBar.setWidth((newHealth / (double) maxPlayerHealth) * healthBarMaxWdith); //Changes healthbar visual
					healthNumber.setText(newHealth + "/ " + maxPlayerHealth);
				}
			}
		}
	}

	//Projectile damage check
	public void projectileHitDetection(long now) {
		for (int i = projectileList.size() - 1; i >= 0; i--) { //Iterates through array backwards to prevent crashing
			ProjectileAbility currentProjectile = projectileList.get(i);
			boolean projectileRemoved = false;  //If projectile is removed, everything is skipped

			for (int j = enemiesList.size() - 1; j >= 0 && !projectileRemoved; j--) { //Iternates through array backwards to prevent crashing
				Enemy currentEnemy = enemiesList.get(j);

				if (checkCollisionRectangle(currentProjectile.projectileShape, currentEnemy.enemyShape)) { //Determines if projectile hits an enemy
					if (currentProjectile.type.equals("boomerang")) {
						if (now - currentProjectile.lastHitTime >= currentProjectile.cooldown) {
							currentEnemy.enemyHealth -= currentProjectile.projectileDamage;
							currentProjectile.lastHitTime = now;
						}
					}

					if (currentProjectile.type.equals("fireball")) {
						currentEnemy.enemyHealth -= currentProjectile.projectileDamage;
						projectileRemoved = true;
						game.getChildren().remove(currentProjectile.projectileShape);
						projectileList.remove(i);
					}

					if (currentEnemy.enemyHealth <= 0) { //Lifesteal ability activiates when enemy health is zero
						lifeStealAbility();

						playerXp += currentEnemy.enemyXp;
						while (playerXp >= maxPlayerXp) { //Levels up player
							playerLevel++;
							playerLeveledUp = true;
							playerXp -= maxPlayerXp;
						}

						if (auraStack == maxStack && boomerangStack == maxStack && lifeStealStack == maxStack) { //Lvl text will display "Max level"
							lvlText.setText("Max level");
							xpBar.setWidth(xpBarMaxWidth);
						}
						else {
							lvlText.setText("level " + playerLevel);
							xpBar.setWidth((playerXp / (double) maxPlayerXp) * xpBarMaxWidth); //Changes xpBar visual
						}
						enemiesDefeated++;
						defeatedText.setText("Slain: " + enemiesDefeated);

						enemiesList.remove(j);
						game.getChildren().remove(currentEnemy.enemyShape);
					}
				}
			}
		}
	}

	//Fireball projectile
	public void projectileFireball(double x, double y) {
		ProjectileAbility fireball = new ProjectileAbility("fireball", x, y);
		double changeX = mouseClickX - x;
		double changeY = mouseClickY - y;
		double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //Pythagorean theorm to determine the distane
		if (distance == 0) { //Prevents code from dividing by zero
			distance = 1;
		}
		fireball.velocityX = ((changeX / distance) * fireball.projectileSpeed);
		fireball.velocityY = ((changeY / distance) * fireball.projectileSpeed); //Adds projectile speed to the projectile
		projectileList.add(fireball);
		game.getChildren().add(fireball.projectileShape);
	}

	//Move projectiles
	public void moveProjectile() { //Moves projectile to mouse click
		for (int i = projectileList.size() - 1; i >= 0; i--) {
			ProjectileAbility p = projectileList.get(i);

			//moves fireball
			if (p.type.equals( "fireball")) {
				p.projectileShape.setX(p.projectileShape.getX() + p.velocityX);
				p.projectileShape.setY(p.projectileShape.getY() + p.velocityY);
				double projectileY = p.projectileShape.getY();
				double projectileX = p.projectileShape.getX();

				//removes projectiles that pass the screen border
				if (projectileX > screenWidth || projectileX < 0 || projectileY > screenHeight || projectileY < 0 ) {
					projectileList.remove(i);
					game.getChildren().remove(p.projectileShape);
				}
			}

			//moves fireball
			if (p.type.equals ("boomerang")) {
				double targetX = player.getX() + player.getWidth() / 2;
				double targetY = player.getY() + player.getHeight() / 2;
				double boomerangX = p.projectileShape.getX();
				double boomerangY = p.projectileShape.getY();

				double changeX = targetX - boomerangX;
				double changeY = targetY - boomerangY;
				double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //Pythagorean theorm to determine the distance

				//Determins if boomerang is coming back or being thrown
				if (p.boomerangIsReturning) {
			
					if (distance < 15) { //Removes boomerang that goes inside the player when returning
						projectileList.remove(i);
						game.getChildren().remove(p.projectileShape);
						p.boomerangIsReturning = false;
					}
					else {
						p.projectileShape.setX(p.projectileShape.getX() + (changeX / distance) * boomerangReturnSpeed);
						p.projectileShape.setY(p.projectileShape.getY() + (changeY / distance) * boomerangReturnSpeed);
					}
				}

				else {
					p.projectileShape.setX(p.projectileShape.getX() + p.velocityX);
					p.projectileShape.setY(p.projectileShape.getY() + p.velocityY);
					//return projectile once it hits the border of the map
					if (p.projectileShape.getX() < 0 || p.projectileShape.getX() > screenWidth || p.projectileShape.getY() < 0 || p.projectileShape.getY() > screenHeight ) {
						p.boomerangIsReturning = true;
					}
				}
			}
		}
	}

	//boomerang projectile
	public void boomerangAbility(long now) {
		if (boomerangStack <= 0 || enemiesList.isEmpty()) { //Ensures boomerang ability will not activate if ability is not owned or no enemies
			return;
		}

		int activeBoomerangCount = 0;
		for (int i = 0; i < projectileList.size(); i++) { //Determines the boomerangs active on screen
			ProjectileAbility p = projectileList.get(i);
			if (p.type.equals("boomerang")) {
				activeBoomerangCount++;
			}
		}
		//Calculates amrount of boomerang on the map currently
		int boomerangToSpawn = boomerangStack - activeBoomerangCount;

		//Only creates the amount of boomerangs based on how many stack of the boomerang ability you have
		for (int i = 0; i < boomerangToSpawn; i++) {
			double startX = player.getX() + player.getWidth() / 2;
			double startY = player.getY() + player.getHeight() / 2;
			ProjectileAbility boomerang = new ProjectileAbility("boomerang", startX, startY);

			Enemy target = enemiesList.get(random.nextInt(enemiesList.size())); //gets random target from arrayList

			//Determining the distacne traveled (does not move booemrang itself)
			double targetX = target.enemyShape.getX() + target.enemyShape.getWidth() / 2;
			double targetY = target.enemyShape.getY() + target.enemyShape.getHeight() / 2;

			double changeX = targetX - startX;
			double changeY = targetY - startY;
			double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //Pythagorean theorm to determine the distane

			if (distance <= 0) { //Ensures code does not divide by zero
				distance = 1;
			}
			if (distance > 0) {
				boomerang.velocityX = ((changeX / distance) * boomerang.projectileSpeed);
				boomerang.velocityY = ((changeY / distance) * boomerang.projectileSpeed);
			} 
			else {
				boomerang.velocityX = boomerang.projectileSpeed;
				boomerang.velocityY = 0;
			}

			projectileList.add(boomerang);
			game.getChildren().add(boomerang.projectileShape);
		}
	}

	//Damage detection for aura
	public void auraAbility(long now) {
		if (auraStack <= 0) { //Ensures if ability is obtained by player
			return;
		}

		if (!auraInitialized) { //Creates aura
        auraEnable = new PassiveAbility("aura");
        game.getChildren().add(auraEnable.auraShape);
        auraInitialized = true;
    	}

		//Creates the aura around the player
		auraEnable.auraShape.setRadius(50.0 + (auraStack * auraRadiusMultiplyer));
		double auraMiddleX = player.getX() + (player.getWidth() / 2);
		double auraMiddleY = player.getY() + (player.getHeight() / 2);
		auraEnable.auraShape.setCenterX(auraMiddleX);
		auraEnable.auraShape.setCenterY(auraMiddleY);

		//Cooldown for aura ability
		if (now - lastAuraTime >= auraEnable.cooldown) {
			lastAuraTime = now;
			double auraDamage = auraEnable.damage;
			for (int i = enemiesList.size() - 1; i >= 0; i--) {
				Enemy currentEnemy = enemiesList.get(i);

				//Determine if enemy touches player
				if (checkCollisionCircle(auraEnable.auraShape, currentEnemy.enemyShape)) {
					currentEnemy.enemyHealth -= auraDamage;

					if (currentEnemy.enemyHealth <= 0) {
						lifeStealAbility(); //Activates lifesteal
						playerXp += currentEnemy.enemyXp;

						while (playerXp >= maxPlayerXp) { //Levels up player
							playerLevel++;
							playerLeveledUp = true;
							playerXp -= maxPlayerXp;
						}

						if (auraStack == maxStack && boomerangStack == maxStack && lifeStealStack == maxStack) { //Lvl text will display "Max level"
							lvlText.setText("Max level");
							xpBar.setWidth(xpBarMaxWidth);
						}
						else {
							lvlText.setText("level " + playerLevel);
    						xpBar.setWidth((playerXp / (double) maxPlayerXp) * xpBarMaxWidth);
						}
						enemiesDefeated++;
						defeatedText.setText("Slain: " + enemiesDefeated);

						enemiesList.remove(i);
						game.getChildren().remove(currentEnemy.enemyShape);
					}
				}
			}
		}
	}

	//Lifesteal Ability
	public void lifeStealAbility() {
		if (lifeStealStack > 0) { //lifesteal ability

			if (!lifeStealInitialized) {
       		 lifeStealEnable = new PassiveAbility("lifeSteal");
       		 lifeStealInitialized = true;
    	}
			//Calculates the chance a palyer has to gain health
			double chance = (lifeStealEnable.healChance * lifeStealStack) * maxPlayerHealth;

			if (random.nextInt(maxPlayerHealth) < chance) { //A chance for player to gain health
				playerHealth += lifeStealEnable.healAmount;

				if (playerHealth > maxPlayerHealth) {
					playerHealth = maxPlayerHealth;
				}
				healthBar.setWidth((playerHealth / (double) maxPlayerHealth) * healthBarMaxWdith);
				healthNumber.setText(playerHealth + "/ " + maxPlayerHealth);
			}
		}
	}

	//Class that contains projectile abilties
	public class ProjectileAbility {
		double projectileSpeed;
		int projectileDamage;
		Rectangle projectileShape;
		String type;
		double velocityX;
		double velocityY;
		double cooldown;
		boolean boomerangIsReturning;

		long lastHitTime = 0;

		ProjectileAbility (String projectileType, double coordX, double coordY) {
			type = projectileType;
			if (type.equals("fireball")) {
				//Stats for fireball
				projectileSpeed = fireballSpeed;
				projectileDamage = fireballDamage;
				cooldown = fireballCooldown;

				//Image for fireball
				projectileShape = new Rectangle(40, 40, Color.TRANSPARENT);
				projectileShape.setCache(true);
				projectileShape.setFill(fireballPattern);
			}

			if (type.equals("boomerang")) {
				//Stats for boomerang
				projectileSpeed = boomerangSpeed;
				projectileDamage = boomerangDamage;
				cooldown = boomerangCooldown;
				boomerangIsReturning = false;

				//Image for boomerang
				projectileShape = new Rectangle(30, 50, Color.TRANSPARENT);
				projectileShape.setCache(true);
				projectileShape.setFill(boomerangPattern);
			}
			projectileShape.setX(coordX);
			projectileShape.setY(coordY);
		}
	}

	//Class that contains all passive abilities
	public class PassiveAbility {
		double healAmount;
		double healChance;
		double cooldown;
		double damage;
		Rectangle lifeStealShape;
		Circle auraShape;
		String type;

		PassiveAbility (String PassiveAbilityType) {
			type = PassiveAbilityType;

			if (type.equals("lifeSteal")) {
				//Stats for lifesteal
				healChance = lifestealChance;
				healAmount = lifestealAmount;
				cooldown = lifestealCooldown;
			}

			if (type.equals("aura")) {
				//Stats for aura
				damage = auraDamage;
				cooldown = auraCooldown;

				//Image for aura
				auraShape = new Circle(50, Color.TRANSPARENT);
				auraShape.setCache(true);
				auraShape.setFill(auraPattern);
			}
		}
	}

	//Class that contains all different types of enemeies
	public class Enemy {
		int enemyHealth;
		double enemySpeed;
		int enemyDamage;
		int enemyXp;
		Rectangle enemyShape;
		String type;

		Enemy (String enemyType, double coordX, double coordY) {
			type = enemyType;
			if (type.equals("normal")) {
				//Stats for normal slime
				enemyHealth = normalEnemyHealth * difficulty;
				enemySpeed = normalEnemySpeed;
				enemyDamage = normalEnemyDamage;
				enemyXp = normalEnemyXp;

				//Image for normal slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(normalSlimePattern);
			}

			if (type.equals("tank")) {
				//Stats for tank slime
				enemyHealth = tankEnemyHealth * difficulty;
				enemySpeed = tankEnemySpeed;
				enemyDamage = tankEnemyDamage;
				enemyXp = tankEnemyXp;

				//Image for tank slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(tankSlimePattern);
			}

			if (type.equals("fast")) {
				//Stats for fast slime
				enemyHealth =  fastEnemyHealth * difficulty;
				enemySpeed = fastEnemySpeed;
				enemyDamage = fastEnemyDamage;
				enemyXp = fastEnemyXp;

				//Image for fast slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(fastSlimePattern);
			}

			if (type.equals("boss")) {
				//Stats for boss slime
				enemyHealth = bossEnemyHealth * difficulty;
				enemySpeed = bossEnemySpeed;
				enemyDamage = bossEnemyDamage;
				enemyXp = bossEnemyXp;

				//Image for boss slime
				enemyShape = new Rectangle(130, 130, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(bossSlimePattern);
			}
			
			//Sets the coordinates for the enemy
			enemyShape.setX(coordX);
			enemyShape.setY(coordY);
		}
	}
}