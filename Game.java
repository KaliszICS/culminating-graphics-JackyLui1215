/**

        * Game: Wavebound.io

        * Author: Jacky

        * Date Created: May,28 2026

        * Date Last Modified: June 3, 2026

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
	double playerSpeed = 3.0;
	int playerXp = 0;
	int playerLevel = 0;
	boolean playerLeveledUp = false;
	int enemiesDefeated = 0;
	int score = 0;
	Rectangle player;

	//Contains all movement keys
	HashMap<KeyCode, Boolean> keyState = new HashMap<>();

	//Contains active enemies on the map
	ArrayList<enemy> enemiesList = new ArrayList<>();

	//Contains all active projectiles on the map
	ArrayList<projectileAbility> projectileList = new ArrayList<>();

    //Spawn rate
	long lastSpawnTime;
	long lastBossSpawnTime;
    int spawnRate = 2;

	//Cooldowns
	long lastDamageTime;
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

	//Map
	Pane game;

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
		Text text = new Text("Survive the Waves...");
		text.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 25px; -fx-fill: white");

		//Buttons to play the game
		Button button = new Button("Click to Play!");
		button.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 15px; -fx-fill: black");
		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(title, text, button);
		//Determins if a button is pressed
		button.setOnAction(event -> gameStart(menu));

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
		//Adds Mouse click
		scene.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) { //Runs if left click of mouse is clicked
				mouseClickX = event.getSceneX();
				mouseClickY = event.getSceneY();
				projectileFireball(player.getX(), player.getY()); //Creates the projectile
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
				projectileHitDetection();
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
				enemy newEnemy = spawnLocation(randomEnemy(), 0, 0); //uses randomEnemy function
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now;
			}
		if (minutes >= 2) { //will spawn boss after two minutes with intervals of two minutes
			if ((now -lastSpawnTime) > spawnRate * 60e9) {
				enemy newEnemy = spawnLocation("boss", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
			}
		}
		}
		else {
			if ((now - lastSpawnTime) > spawnRate * 1e9) { //spawns only normal enemies
				enemy newEnemy = spawnLocation("normal", 0, 0);
				enemiesList.add(newEnemy);
				game.getChildren().add(newEnemy.enemyShape);
				lastSpawnTime = now;
			}
		}
        if(minutes == 5 && seconds == 0) { //when the time reaches 5 minutes, the boss wil spawn
            enemy newEnemy = spawnLocation("normal", 0, 0);
            enemiesList.add(newEnemy);
            game.getChildren().add(newEnemy.enemyShape);
        }
	}

	//Checks if enemy is touching the player (Collision Detection)
	public boolean checkCollision(Rectangle player, Rectangle enemy) {
		return (player.getBoundsInParent().intersects(enemy.getBoundsInParent())); //checks if player intersects with an enemy
	}


	//Pathfinds to players location
	public void enemyDetection() {
		for (int i = 0; i < enemiesList.size(); i++) { //loops through all enemies
			enemy currentEnemy = enemiesList.get(i); //gets one enemy from the entire list
			double enemyX = currentEnemy.enemyShape.getX();
			double enemyY = currentEnemy.enemyShape.getY();
			double enemySpeed = currentEnemy.enemySpeed;
			//Knockback for enemy so enemy does not clip into player
			if (checkCollision(player, currentEnemy.enemyShape)) {
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
	public enemy spawnLocation(String enemyType, double coordX, double coordY) {
		 random = new Random(); //creates a random spawn location with java's random class
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
		return new enemy(enemyType, coordX, coordY);
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
				enemy currentEnemy = enemiesList.get(i); //gets currentEnemy with arrayList
				if (checkCollision(player, currentEnemy.enemyShape)) { //Checks if enemy touches player
					playerHealth -= currentEnemy.enemyDamage;
					int newHealth = Math.max(0, playerHealth);
					healthBar.setWidth((newHealth / 100.0) * healthBarMaxWdith); //Changes healthbar visual
					healthNumber.setText(playerHealth + "/100");
				}
			}
		}
	}

	//Projectile damage check
	public void projectileHitDetection() {
		for (int i = projectileList.size() - 1; i >= 0; i--) { //iterates through array backwards to prevent crashing
			projectileAbility currentProjectile = projectileList.get(i);
			boolean projectileRemoved = false;  //If projectile is removed, everything is skipped

			for (int j = enemiesList.size() - 1; j >= 0 && !projectileRemoved; j--) { //iternates through array backwards to prevent crashing
				enemy currentEnemy = enemiesList.get(j);

				if (checkCollision(currentProjectile.projectileShape, currentEnemy.enemyShape)) { //determines if projectile hits an enemy
					currentEnemy.enemyHealth -= currentProjectile.projectileDamage;
					projectileRemoved = true;
					game.getChildren().remove(currentProjectile.projectileShape);
					projectileList.remove(i);

						playerXp += currentEnemy.enemyXp;
						while (playerXp >= 100) { //Levels up player
							playerLevel++;
							playerLeveledUp = true;
							lvlText.setText("level " + playerLevel);
							playerXp -= 100;
							xpBar.setWidth(0);
						}

						xpBar.setWidth((playerXp / 100.0) * xpBarMaxWidth); //Changes xpBar visual
						defeatedText.setText("Slain: " + enemiesDefeated);

						enemiesList.remove(j);
						game.getChildren().remove(currentEnemy.enemyShape);
						enemiesDefeated++;
					}
				}
			}
		}

	//Fireball projectile
	public void projectileFireball(double x, double y) {
		projectileAbility fireball = new projectileAbility("fireball", x, y);
		double changeX = mouseClickX - x; //finds the x component
		double changeY = mouseClickY - y; //finds the y component
		double distance = Math.sqrt(Math.pow(changeX, 2) + Math.pow(changeY, 2)); //pythagorean theorm to determine the distance 
		fireball.velocityX = ((changeX / distance) * fireball.projectileSpeed); //adds projectile speed to the projectile
		fireball.velocityY = ((changeY / distance) * fireball.projectileSpeed);
		projectileList.add(fireball);
		game.getChildren().add(fireball.projectileShape);
		}

		//Move projectiles
	public void moveProjectile() { //Moves projectile to mouse click
		for (int i = projectileList.size() - 1; i >= 0; i--) {
			projectileAbility p = projectileList.get(i);

			if (p.type.equals( "fireball")) {
				p.projectileShape.setX(p.projectileShape.getX() + p.velocityX);
				p.projectileShape.setY(p.projectileShape.getY() + p.velocityY);
				double projectileY = p.projectileShape.getY();
				double projectileX = p.projectileShape.getX();
				//removes porjectiles that pass the screen border
				if (projectileX > screenWidth || projectileX < 0 || projectileY > screenHeight || projectileY < 0 ) {
					projectileList.remove(i);
					game.getChildren().remove(p.projectileShape);
				}
			}
		}
	}

	//Class that contains abilties
	public class projectileAbility {
		double projectileSpeed;
		int projectileDamage;
		Rectangle projectileShape;
		String type;
		double velocityX;
		double velocityY;

		projectileAbility (String projectileType, double coordX, double coordY) {
			type = projectileType;
			if (type.equals("fireball")) {
				projectileSpeed = 4.0;
				projectileDamage = 25;

				//Image for projectile
                Image image = new Image("/assets/fireball.png", false);
                ImagePattern imagePattern = new ImagePattern(image);
				projectileShape = new Rectangle(20, 20, Color.TRANSPARENT);
                projectileShape.setCache(true);
                projectileShape.setFill(imagePattern);
			}
				projectileShape.setX(coordX);
				projectileShape.setY(coordY);
		}
	}

	//Class for all different types of enemeies
	public class enemy {
		int enemyHealth;
		double enemySpeed;
		int enemyDamage;
		int enemyXp;
		Rectangle enemyShape;
		String type;

		enemy (String enemyType, double coordX, double coordY) {
			type = enemyType;
			if (type.equals("normal")) {
				enemyHealth = 50;
				enemySpeed = 1.0;
				enemyDamage = 10;
				//Image for normal slime
                Image image = new Image("/assets/Slimes/normal.png", false);
                ImagePattern imagePattern = new ImagePattern(image);
				enemyShape = new Rectangle(50, 50, Color.TRANSPARENT);
                enemyShape.setCache(true); //reduces resources used (performance increase)
                enemyShape.setFill(imagePattern);
			}

			if (type.equals("tank")) {
				enemyHealth = 100;
				enemySpeed = 0.5;
				enemyDamage = 5;
				//Image for tank slime
				Image image = new Image("/assets/Slimes/tank.png", false);
                ImagePattern imagePattern = new ImagePattern(image);
				enemyShape = new Rectangle(50, 50, Color.TRANSPARENT);
                enemyShape.setCache(true);
                enemyShape.setFill(imagePattern);
			}

			if (type.equals("fast")) {
				enemyHealth = 25;
				enemySpeed = 2.0;
				enemyDamage = 15;
				//Image for fast slime
				Image image = new Image("/assets/Slimes/fast.png", false);
                ImagePattern imagePattern = new ImagePattern(image);
				enemyShape = new Rectangle(50, 50, Color.TRANSPARENT);
                enemyShape.setCache(true);
                enemyShape.setFill(imagePattern);
			}

			if (type.equals("boss")) {
				enemyHealth = 200;
				enemySpeed = 0.1;
				enemyDamage = 20;
				//Image for boss slime
				Image image = new Image("/assets/Slimes/boss.png", false);
                ImagePattern imagePattern = new ImagePattern(image);
				enemyShape = new Rectangle(90, 90, Color.TRANSPARENT);
                enemyShape.setCache(true);
                enemyShape.setFill(imagePattern);
			}
			//Sets the coordinates for the enemies
			enemyShape.setX(coordX);
			enemyShape.setY(coordY);
		}
	}

}