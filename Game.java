/**

        * Game: Wavebound.io

        * Author: Jacky

        * Date Created: May,28 2026

        * Date Last Modified: May 30, 2026

        */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.AnimationTimer;
import java.util.HashMap;
import javafx.scene.control.Label;
import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class Game extends Application {
	//declaring variables for player
	int playerHealth = 100;
	double playerSpeed = 3.0;
	int xp = 0;
	int enemiesDefeated = 0;
	int score = 0;
	Rectangle player;
	//Contains all movement keys
	HashMap<KeyCode, Boolean> keyState = new HashMap<>();
	ArrayList<enemy> enemiesList = new ArrayList<>();

	//timer
    AnimationTimer timer;
	long startTime;
	long minutes;
	long seconds;
	Label time = new Label("0:00");

	//Determines screen dimensions of the user
	Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
	double screenWidth = primaryScreenBounds.getWidth();
	double screenHeight = primaryScreenBounds.getHeight();

	//map
	Pane game;

	public static void main(String[] args) {
		launch();
	}
    
    //menu screen of the game
	@Override
	public void start(Stage menu) {
		Text title = new Text("Wavebound.io");
		title.setTextAlignment(TextAlignment.CENTER);
		title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold;");
		Text text = new Text("Survive the Waves...");
		text.setStyle("-fx-font-size: 25px;");

		//Buttons to play the game
		Button button = new Button("Click to Play!");
		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(title, text, button);
		//Determins which difficulty is pressed
		button.setOnAction(event -> gameStart(menu));
		//Shows menu
        StackPane main = new StackPane();
        main.getChildren().addAll(layout);
        Scene scene = new Scene(main, screenWidth, screenHeight);
		menu.setScene(scene);
		menu.setFullScreen(true);
		menu.show();
	}

	//Stage for the game itself
	public void gameStart(Stage gameStart) {
		//Creates background
		game = new Pane();

		//Creates player
		player = new Rectangle(60, 60, Color.BLUE);
		//resets old values
		playerHealth = 100;
		xp = 0;
		enemiesDefeated = 0;
		score = 0;
		enemiesList.clear();

		//Time elapsed of the game
		startTime = System.nanoTime();
		time.setStyle("-fx-font-size: 24px; -fx-text-fill: black; -fx-font-weight: bold;");

		game.getChildren().addAll(player, time);
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
		//Animation timer for smooth gameplay
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				movement();
				timeElapsed(now);
				}
		};
		timer.start();
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
		//bounderies for the game to prevent player for exiting
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

	//timer for the game
	public void timeElapsed(long now) {
		double totalSeconds = (now - startTime) * 1e-9;
		minutes = (long) (totalSeconds / 60);
		seconds = (long) (totalSeconds % 60);
		String doubleDigitMinutes = "";
		String doubleDigitSeconds = "";
		if (seconds < 10) {
			doubleDigitSeconds = "0";
		}
		if (minutes < 10) {
			doubleDigitMinutes = "0";
		}
		time.setText( doubleDigitMinutes + minutes + " : " +  doubleDigitSeconds + seconds);
	}

	//class for all different types of enemeies
	public class enemy {
		int enemyHealth;
		double enemySpeed;
		int enemyDamage;
		Rectangle enemyShape;
		String type;

		enemy (String enemyType, double coordX, double coordY) {
			type = enemyType;
			if (type.equals("normal")) {
				enemyHealth = 50;
				enemySpeed = 1.0;
				enemyDamage = 10;
                enemyShape = new Rectangle(80, 80, Color.GREEN);
			}

			if (type.equals("tank")) {
				enemyHealth = 100;
				enemySpeed = 0.5;
				enemyDamage = 5;
                enemyShape = new Rectangle(80, 80, Color.YELLOW);
			}

			if (type.equals("fast")) {
				enemyHealth = 25;
				enemySpeed = 2.0;
				enemyDamage = 15;
                enemyShape = new Rectangle(80, 80, Color.BLUE);
			}

			if (type.equals("boss")) {
				enemyHealth = 200;
				enemySpeed = 0.1;
				enemyDamage = 20;
                enemyShape = new Rectangle(80, 80, Color.RED);
			}
			//sets the coordinates for the enemies
			enemyShape.setX(coordX);
			enemyShape.setY(coordY);
		}
	}

}