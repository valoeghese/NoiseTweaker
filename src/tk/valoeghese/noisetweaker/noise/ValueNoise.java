package tk.valoeghese.noisetweaker.noise;

import java.util.Random;

import tk.valoeghese.noisetweaker.util.MathsHelper;
import tk.valoeghese.noisetweaker.util.SimpleRandom;

public final class ValueNoise implements INoise2D {
	
	private final SimpleRandom rand;
	private final boolean fade;
	
	private double offsetX = 0;
	private double offsetY = 0;
	
	public ValueNoise(long seed, boolean fade) {
		rand = new SimpleRandom(seed);
		this.fade = fade;
	}
	
	public ValueNoise(Random random, boolean fade) {
		rand = new SimpleRandom(random.nextLong());
		offsetX = random.nextDouble() * 256;
		offsetY = random.nextDouble() * 256;
		
		this.fade = fade;
	}
	
	private double keyPoint(double x, double y) {
		return (2 * rand.randomDouble(x, y)) - 1;
	}
	
	@Override
	public double noise(double x, double y) {
		return noiseAt(x + offsetX, y + offsetY);
	}
	
	private double noiseAt(double x, double y) {
		double xFloor = MathsHelper.floor(x);
		double yFloor = MathsHelper.floor(y);
		
		double localX = x - xFloor;
		double localY = y - yFloor;
		
		if (fade) {
			localX = fade(localX);
			localY = fade(localY);
		}
		
		double NW = keyPoint(xFloor, yFloor + 1);
		double NE = keyPoint(xFloor + 1, yFloor + 1);
		double SW = keyPoint(xFloor, yFloor);
		double SE = keyPoint(xFloor + 1, yFloor);
		
		return MathsHelper.lerp(localY, 
				MathsHelper.lerp(localX, SW, SE),
				MathsHelper.lerp(localX, NW, NE));
	}

	private static double fade(double n) {
		return n * n * n * (n * (n * 6 - 15) + 10);
	}

}
