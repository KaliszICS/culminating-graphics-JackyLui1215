/**

        * Game: Wavebound.io

        * Author: Jacky

        * Date Created: May,28 2026

        * Date Last Modified: June 8, 2026

        */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.AnimationTimer;
import java.util.HashMap;
import java.util.Random;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

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

	//Contains active enemies on the map
	ArrayList<Enemy> enemiesList = new ArrayList<>();

	//Contains all active projectiles on the map
	ArrayList<ProjectileAbility> projectileList = new ArrayList<>();

    //Spawn rate
	long lastSpawnTime;
	long lastBossSpawnTime;
    int spawnRate = 2;

	//Cooldowns
	long lastDamageTime;
	long lastAuraTime;
	long lastFireballTime;
	long weaponCooldown;
	long damageCooldown = (long) (5 * 1e8);

	//Timer
    AnimationTimer timer;
	long startTime;
	long minutes;
	long seconds;
	Label time = new Label("0:00");

	//Healthbar
	Rectangle healthBar;
	Rectangle healthBackground;
	double healthBarMaxWdith = 200.0;
	Text healthNumber = new Text(playerHealth + "/ 100");

	//Xpbar
	Rectangle xpBar;
	Rectangle xpBarBackground;
	double xpBarMaxWidth = 200.0;
	Text lvlText = new Text( "level " + playerLevel);

	//Abilities (If stack is zero, player has not obtained it)
	int maxStack = 3;
	int lifeStealStack = 0;
	int boomerangStack = 0;
	int auraStack = 0;
	PassiveAbility auraEnable;
	PassiveAbility lifeStealEnable;
	boolean auraInitialized = false;
	boolean lifeStealInitialized = false;

	//Enemies killed;
	Text defeatedText = new Text("Slain: " + enemiesDefeated);

    //Random number generator
    Random random = new Random();

	//Determines screen dimensions of the user
	Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
	double screenWidth = primaryScreenBounds.getWidth();
	double screenHeight = primaryScreenBounds.getHeight();

	//Mouse Click 
	double mouseClickX;
	double mouseClickY;
	boolean isMouseClicked;

	//All images
	ImagePattern fireballPattern;
	ImagePattern boomerangPattern;
	ImagePattern normalSlimePattern;
	ImagePattern tankSlimePattern;
	ImagePattern fastSlimePattern;
	ImagePattern bossSlimePattern;
	ImagePattern lifeStealPattern;
	ImagePattern auraPattern;

	//Map
	Pane game;

	//Difficulty
	int difficulty;
	int easyMultiplyer = 1;
	int mediumMultiplyer = 2;
	int hardMultiplyer = 3;

	public static void main(String[] args) {
		launch();
	}
    
    //Menu screen of the game
	@Override
	public void start(Stage menu) {
		Text title = new Text("Wavebound.io");
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

		//Determins if a button is pressed
		//Determines if button is pressed
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
        Image backgroundImage = new Image("/assets/menu.png", false);
        ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
        Rectangle background = new Rectangle(screenWidth, screenHeight);
        background.setCache(true);
        background.setFill(backgroundPattern);
        background.setMouseTransparent(true);
        StackPane main = new StackPane();
        main.getChildren().addAll(background, layout);

		//Shows menu
		Scene scene = new Scene(main, screenWidth, screenHeight);
		menu.setScene(scene);
		menu.setFullScreen(true);
		menu.show();
	}

	//Stage for the game itself
	public void gameStart(Stage gameStart) {
		//Loads all images at once
		fireballPattern = new ImagePattern(new Image("/assets/fireball.png"));
		boomerangPattern = new ImagePattern(new Image("/assets/boomerang.png"));
		normalSlimePattern = new ImagePattern(new Image("/assets/slimes/normal.png"));
		tankSlimePattern = new ImagePattern(new Image("/assets/slimes/tank.png"));
		fastSlimePattern = new ImagePattern(new Image("/assets/slimes/fast.png"));
		bossSlimePattern = new ImagePattern(new Image("/assets/slimes/boss.png"));
		lifeStealPattern = new ImagePattern(new Image("/assets/lifesteal.png"));
		auraPattern = new ImagePattern(new Image("/assets/aura.png"));
		
		//Creates background for the game
		game = new Pane();
		Image backgroundImage = new Image("/assets/background.png", false);
        ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
        Rectangle background = new Rectangle(screenWidth, screenHeight);
        background.setCache(true);
        background.setFill(backgroundPattern);
        game.getChildren().add(background);

		//Creates player with an image
		player = new Rectangle(60, 60, Color.TRANSPARENT);
		Image playerImage = new Image("/assets/Player.png", false);
		ImagePattern playerPattern = new ImagePattern(playerImage);
		player.setFill(playerPattern);
		player.setCache(true);
		
		//Reset old values
		playerHealth = 100;
		playerXp = 0;
		enemiesDefeated = 0;
		score = 0;
		enemiesList.clear();
		healthNumber.setText(playerHealth + "/100");

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

		//Enemies killed counter
		defeatedText.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");
		defeatedText.setY(100);
		defeatedText.setX(20);

		//Time elapsed of the game
		startTime = System.nanoTime();
        lastSpawnTime = System.nanoTime();
		lastBossSpawnTime = System.nanoTime();
		lastDamageTime = System.nanoTime();
		lastAuraTime = System.nanoTime();
		lastFireballTime = System.nanoTime();
		
		DropShadow shadow = new DropShadow(); //Creates a shadow effect on text
		shadow.setOffsetX(4.0f);
		shadow.setOffsetY(4.0f);
		time.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white");
		time.setEffect(shadow);

		game.getChildren().addAll(player, time, healthBackground, healthBar, healthNumber, xpBarBackground, xpBar, lvlText, defeatedText);
		Scene scene = new Scene(game, screenWidth, screenHeight);
		gameStart.setScene(scene);
		gameStart.setFullScreen(true);
		gameStart.show();
		game.requestFocus();

		//Positions player in the center of the screen
		player.setX((screenWidth / 2) - (player.getWidth() / 2));
		player.setY((screenHeight / 2) - (player.getHeight() / 2));
		time.setLayoutX((screenWidth / 2) - time.getWidth() / 2);
		time.setLayoutY(20);

		//Ensures no null expcetion happens from trying to get keystate when not assigned.
		keyState.put(KeyCode.W, false);
		keyState.put(KeyCode.A, false);
		keyState.put(KeyCode.S, false);
		keyState.put(KeyCode.D, false);
		//Adds key pressed to hashmap.
		scene.setOnKeyPressed(event -> keyState.put(event.getCode(), true));
		scene.setOnKeyReleased(event -> keyState.put(event.getCode(), false));

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
				movement();
				timeElapsed(now);
                spawnEnemy(now);
				enemyDetection();
				playerDamageCheck(now);
				moveProjectile();
				projectileHitDetection(now);
				auraAbility(now);
				boomerangAbility(now);

				//Adds Mouse click
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

	//Will run if the player reaches zero health
	public void end (Stage end) {
		score = (int) ((minutes * 5) + (seconds + 1) + (enemiesDefeated * 10));
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
        Image backgroundImage = new Image("/assets/gameOver.png", false);
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
		button.setOnAction(event -> gameStart(end));
	}

	//Determines how to move with WASD using KeyCode
	public void movement() {
		if (keyState.get(KeyCode.W)) {
			player.setY(player.getY() - playerSpeed);
		}

		if (keyState.get(KeyCode.S)) {
			player.setY(player.getY() + playerSpeed);
		}

		if (keyState.get(KeyCode.A)) {
			player.setX(player.getX() - playerSpeed);
		}

		if (keyState.get(KeyCode.D)) {
			player.setX(player.getX() + playerSpeed);
		}
		//Bounderies for the game to prevent player for exiting
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
		if (minutes < 10) { //Allows for double digits when the seconds are one digit
			doubleDigitMinutes = "0";
		}
		time.setText( doubleDigitMinutes + minutes + " : " +  doubleDigitSeconds + seconds);
	}

    //Spawns enemy with a cooldown
    public void spawnEnemy(long now) {
		if (minutes >= 1) { //spawns all types of enemies except boss
			if ((now - lastSpawnTime) > spawnRate * 1e9) {
				Enemy newEnemy = spawnLocation(randomEnemy(), 0, 0); //uses randomEnemy function
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now;
			}
		if (minutes >= 2) { //will spawn boss after two minutes with intervals of two minutes
			if ((now -lastSpawnTime) > spawnRate * 60e9) {
				Enemy newEnemy = spawnLocation("boss", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
			}
		}
		}
		else {
			if ((now - lastSpawnTime) > spawnRate * 1e9) { //spawns only normal enemies
				Enemy newEnemy = spawnLocation("normal", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now;
			}
		}
        if(minutes == 5 && seconds == 0) { //when the time reaches 5 minutes, the boss wil spawn
            Enemy newEnemy = spawnLocation("normal", 0, 0);
            enemiesList.add(newEnemy);
            game.getChildren().add(newEnemy.enemyShape);
        }
	}

	//Checks if enemy is touching the player (Collision Detection)
	public boolean checkCollisionRectangle(Rectangle player, Rectangle enemy) {
		return (player.getBoundsInParent().intersects(enemy.getBoundsInParent())); //checks if player intersects with an enemy
	}

	//Checks if enemy is touching the player (Collision Detection)
	public boolean checkCollisionCircle(Circle ability, Rectangle enemy) {
		return ability.getBoundsInParent().intersects(enemy.getBoundsInParent()); //checks if player intersects with an enemy
	}


	//Pathfinds to players location
	public void enemyDetection() {
		for (int i = 0; i < enemiesList.size(); i++) { //loops through all enemies
			Enemy currentEnemy = enemiesList.get(i); //gets one enemy from the entire list
			double enemyX = currentEnemy.enemyShape.getX();
			double enemyY = currentEnemy.enemyShape.getY();
			double enemySpeed = currentEnemy.enemySpeed;
			//Knockback for enemy so enemy does not clip into player
			if (checkCollisionRectangle(player, currentEnemy.enemyShape)) {
				double knockback = 45.0;

				if (enemyY > player.getY()) {
				currentEnemy.enemyShape.setY(enemyY + knockback);
			}

			else if (enemyY < player.getY()) {
				currentEnemy.enemyShape.setY(enemyY - knockback);
			}

			if (enemyX > player.getX()) {
				currentEnemy.enemyShape.setX(enemyX + knockback);
			}

			else if (enemyX < player.getX()) {
				currentEnemy.enemyShape.setX(enemyX - knockback);
			}
			//Pathfinding to player
			}
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

    //creates a random spawn location for an enemy around the border of the screen
	public Enemy spawnLocation(String enemyType, double coordX, double coordY) {
		int possibility = random.nextInt(1, 5);
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
		return new Enemy(enemyType, coordX, coordY);
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
			for (int i = enemiesList.size() - 1; i >= 0; i--) {
				Enemy currentEnemy = enemiesList.get(i); //gets currentEnemy with arrayList
				if (checkCollisionRectangle(player, currentEnemy.enemyShape)) { //Checks if enemy touches player
					playerHealth -= currentEnemy.enemyDamage;
					int newHealth = Math.max(0, playerHealth);
					healthBar.setWidth((newHealth / 100.0) * healthBarMaxWdith); //Changes healthbar visual
					healthNumber.setText(playerHealth + "/100");
				}
			}
		}
	}

	//Projectile damage check
	public void projectileHitDetection(long now) {
		for (int i = projectileList.size() - 1; i >= 0; i--) { //iterates through array backwards to prevent crashing
			ProjectileAbility currentProjectile = projectileList.get(i);
			boolean projectileRemoved = false;  //If projectile is removed, everything is skipped

			for (int j = enemiesList.size() - 1; j >= 0 && !projectileRemoved; j--) { //iternates through array backwards to prevent crashing
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
    						xpBar.setWidth((playerXp / (double) maxPlayerXp) * xpBarMaxWidth);
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
		double changeX = mouseClickX - x; //finds the x component
		double changeY = mouseClickY - y; //finds the y component
		double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //pythagorean theorm to determine the distance 
		fireball.velocityX = ((changeX / distance) * fireball.projectileSpeed); //adds projectile speed to the projectile
		fireball.velocityY = ((changeY / distance) * fireball.projectileSpeed);
		projectileList.add(fireball);
		game.getChildren().add(fireball.projectileShape);
		}

		//Move projectiles
	//Move projectiles
	public void moveProjectile() { //Moves projectile to mouse click
		for (int i = projectileList.size() - 1; i >= 0; i--) {
			ProjectileAbility p = projectileList.get(i);

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
					double returnSpeed = 3.0;
					if (distance < 15) {
						projectileList.remove(i);
						game.getChildren().remove(p.projectileShape);
						p.boomerangIsReturning = false;
					}
					else {
						p.projectileShape.setX(p.projectileShape.getX() + (changeX / distance) * returnSpeed);
						p.projectileShape.setY(p.projectileShape.getY() + (changeY / distance) * returnSpeed);
					}
				}

				else {
					p.projectileShape.setX(p.projectileShape.getX() + p.velocityX);
					p.projectileShape.setY(p.projectileShape.getY() + p.velocityY);
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

		int boomerangToSpawn = boomerangStack - activeBoomerangCount;

		//Only creates the amount of boomerangs based on how many stack of the boomerang ability you have
		for (int i = 0; i < boomerangToSpawn; i++) {
			double startX = player.getX() + player.getWidth() / 2;
			double startY = player.getY() + player.getHeight() / 2;
			ProjectileAbility boomerang = new ProjectileAbility("boomerang", startX, startY);

			Enemy target = enemiesList.get(random.nextInt(enemiesList.size())); //gets random target from arrayList

			double targetX = target.enemyShape.getX() + target.enemyShape.getWidth() / 2;
			double targetY = target.enemyShape.getY() + target.enemyShape.getHeight() / 2;

			double changeX = targetX - startX;
			double changeY = targetY - startY;
			double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //Pythagorean theorm to determine the distane

			if (distance == 0) { //Ensures code does not divide by zero
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
		if (auraStack <= 0) { //Ensures if ability is owned
			return;
		}

		if (!auraInitialized) {
        auraEnable = new PassiveAbility("aura");
        game.getChildren().add(auraEnable.auraShape);
        auraInitialized = true;
    	}

		//Creates the aura around the player
		auraEnable.auraShape.setRadius(50.0 + (auraStack * 10));
		double auraMiddleX = player.getX() + (player.getWidth() / 2);
		double auraMiddleY = player.getY() + (player.getHeight() / 2);
		auraEnable.auraShape.setCenterX(auraMiddleX);
		auraEnable.auraShape.setCenterY(auraMiddleY);

		//Cooldown for aura ability
		if (now - lastAuraTime >= auraEnable.cooldown) {
			lastAuraTime = now;
			double auraDamage = auraEnable.auraDamage;
			for (int i = enemiesList.size() - 1; i >= 0; i--) {
				Enemy currentEnemy = enemiesList.get(i);

				if (checkCollisionCircle(auraEnable.auraShape, currentEnemy.enemyShape)) {
					currentEnemy.enemyHealth -= auraDamage;

					if (currentEnemy.enemyHealth <= 0) {
						lifeStealAbility();
						playerXp += currentEnemy.enemyXp;

						if (auraStack == maxStack && boomerangStack == maxStack && lifeStealStack == maxStack) { //Sees if player is at max level
							lvlText.setText("Max level");
						}

						while (playerXp >= maxPlayerXp) {
							playerLevel++;
							playerLeveledUp = true;
							playerXp -= maxPlayerXp;
							lvlText.setText("level " + playerLevel);
						}

						xpBar.setWidth((playerXp / (double) maxPlayerXp) * xpBarMaxWidth);
						enemiesList.remove(i);
						game.getChildren().remove(currentEnemy.enemyShape);
						enemiesDefeated++;
						defeatedText.setText("Slain: " + enemiesDefeated);
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

			double chance = (lifeStealEnable.healChance * lifeStealStack) * maxPlayerHealth;

			if (random.nextInt(maxPlayerHealth) < chance) { //A chance for player to gain health
				playerHealth += lifeStealEnable.healAmount;

				if (playerHealth > maxPlayerHealth) {
					playerHealth = maxPlayerHealth;
				}
				healthBar.setWidth((playerHealth / 100.0) * healthBarMaxWdith);
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
				projectileSpeed = 4.0;
				projectileDamage = 25;
				cooldown = 0.5 * 1e9;

				//Image for fireball
				projectileShape = new Rectangle(40, 40, Color.TRANSPARENT);
				projectileShape.setCache(true);
				projectileShape.setFill(fireballPattern);
			}

			if (type.equals("boomerang")) {
				//Stats for boomerang
				projectileSpeed = 3.0;
				projectileDamage = 20;
				cooldown = 0.5 * 1e9;
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
		double auraDamage;
		Rectangle lifeStealShape;
		Circle auraShape;
		String type;

		PassiveAbility (String PassiveAbilityType) {
			type = PassiveAbilityType;

			if (type.equals("lifeSteal")) {
				//Stats for lifesteal
				healChance = 0.5;
				healAmount = 10;
				cooldown = 2 * 1e9;

				//Image for lifeSteal
				lifeStealShape = new Rectangle(10, 20, Color.TRANSPARENT);
				lifeStealShape.setCache(true);
				lifeStealShape.setFill(lifeStealPattern);
			}

			if (type.equals("aura")) {
				//Stats for aura
				auraDamage = 20;
				cooldown = 0.5 * 1e9;

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
				enemyHealth = 25 * difficulty;
				enemySpeed = 1.0;
				enemyDamage = 10;
				enemyXp = 10;

				//Image for normal slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(normalSlimePattern);
			}

			if (type.equals("tank")) {
				//Stats for tank slime
				enemyHealth = 75 * difficulty;
				enemySpeed = 0.5;
				enemyDamage = 5;
				enemyXp = 20;

				//Image for tank slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(tankSlimePattern);
			}

			if (type.equals("fast")) {
				//Stats for fast slime
				enemyHealth = 15 * difficulty;
				enemySpeed = 2.0;
				enemyDamage = 15;
				enemyXp = 15;

				//Image for fast slime
				enemyShape = new Rectangle(70, 70, Color.TRANSPARENT);
				enemyShape.setCache(true);
				enemyShape.setFill(fastSlimePattern);
			}

			if (type.equals("boss")) {
				//Stats for boss slime
				enemyHealth = 500 * difficulty;
				enemySpeed = 0.1;
				enemyDamage = 20;
				enemyXp = 50;

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