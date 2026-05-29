import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class Game extends Application {
    //declaring variables for player
    int health = 100;
    double speed = 3.0;
    int xp = 0;
    Rectangle player;
    //Contains all movement keys
    HashMap<KeyCode, Boolean> keyState = new HashMap<>();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage menu) {
        Text title = new Text("Wavebound.io");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: bold;");
        Text text = new Text("Survive the Waves...");
        text.setStyle("-fx-font-size: 25px;");

        //Buttons to choose difficulty
        Button button = new Button("Click to Play!");
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, text, button);
        //Determins which difficulty is pressed
        button.setOnAction(event -> gameStart(menu));
        //Shows menu
        Scene scene = new Scene(layout, 600, 600);
        menu.setTitle("Game Menu");
        menu.setScene(scene);
        menu.setFullScreen(true);
        menu.show();
    }
    //Stage for the gmae itself
    public void gameStart(Stage gameStart) {
        //Creates player
        StackPane game = new StackPane();
        player = new Rectangle(35, 35, Color.RED);
        game.getChildren().add(player);
        Scene scene = new Scene(game, 800, 600);
        gameStart.setScene(scene);
        gameStart.setFullScreen(true);
        gameStart.show();
        game.requestFocus();
    
        //Ensures no null expcetion happens from trying to get keystate when not assigned.
        keyState.put(KeyCode.W, false);
        keyState.put(KeyCode.A, false);
        keyState.put(KeyCode.S, false);
        keyState.put(KeyCode.D, false);
        //Adds key pressed to hashmap.
        scene.setOnKeyPressed(event -> keyState.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keyState.put(event.getCode(), false));
        //Animation timer for smooth gameplay
        AnimationTimer timer = new AnimationTimer() {
        @Override 
        public void handle(long now) {
             movement();
        }
    };
    timer.start();
    }

    //Determines how to move with WASD using KeyCode
    public void movement() {
    if (keyState.get(KeyCode.W) == true) {
        player.setTranslateY(player.getTranslateY() - speed);
    }

    if (keyState.get(KeyCode.S) == true) {
        player.setTranslateY(player.getTranslateY() + speed);
    }

    if (keyState.get(KeyCode.A) == true) {
        player.setTranslateX(player.getTranslateX() - speed);
    }

    if (keyState.get(KeyCode.D) == true) {
        player.setTranslateX(player.getTranslateX() + speed);
    }

}
}