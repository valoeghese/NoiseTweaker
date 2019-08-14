package tk.valoeghese.noisetweaker;

import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tk.valoeghese.noisetweaker.io.NoiseTransformParser;
import tk.valoeghese.noisetweaker.io.NoiseTransformers;
import tk.valoeghese.noisetweaker.module.TransformPacket;
import tk.valoeghese.noisetweaker.module.TransformVarStorage;

public class Main extends Application {

	public static final Random random = new Random();
	private static NoiseTransformParser main;
	
	public static void main(String[] args) {
		System.out.println("Creating main transformer from file");
		main = NoiseTransformers.getTransformer("main");
		System.out.println("Launching graphics process	");
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		final double width = 400;
		final double height = 400;

		Pane pane = new Pane();

		Canvas canvas = new Canvas(width, height);
		final GraphicsContext graphics = canvas.getGraphicsContext2D();

		// Draw
		draw(graphics, (int) width, (int) height);

		// Connect objects and add to stage

		pane.getChildren().add(canvas);
		Scene scene = new Scene(pane, width, height, Color.WHITESMOKE);

		stage.setTitle("Output");
		stage.setScene(scene);
		stage.show();
	}

	private void draw(GraphicsContext graphics, int width, int height) {
		PixelWriter pw = graphics.getPixelWriter();
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				TransformPacket initPacket = new TransformPacket(new double[] {x, y}, new String[] {"x", "y"});
				
				double[] rgb = main.transform(new TransformVarStorage(), initPacket).getData();
				pw.setColor(x, y, Color.color(rgb[0], rgb[1], rgb[2]));
			}
		}
	}

}
