package tk.valoeghese.noisetweaker.noise;

import java.util.Random;

import tk.valoeghese.noisetweaker.util.MathsHelper;
import tk.valoeghese.noisetweaker.util.SimpleRandom;

public final class SimpleGradientNoise implements INoise2D {

	private double offsetX;
	private double offsetY;
	
	private static final double SQRT_2 = Math.sqrt(2);

	private static final double[][] GRADS = {
			{-1, -1}, {1, -1}, {-1, 1}, {1, 1},
			{0, -SQRT_2}, {SQRT_2, 0}, {-SQRT_2, 0}, {0, SQRT_2}
			};
	private static final int GRADS_LENGTH;
	private final SimpleRandom rand;
	
	public SimpleGradientNoise(Random random) {
		offsetX = random.nextDouble() * 256D;
		offsetY = random.nextDouble() * 256D;
		
		rand = new SimpleRandom(random.nextLong());
	}

	private double[] randomGradAt(double x, double y) {
		int returnLoc = rand.random(x, y, GRADS_LENGTH);
		
		return GRADS[returnLoc];
	}

	private static double dot2d(final double[] vec1, final double[] vec2) {
		if (vec1.length != 2 || vec2.length != 2) {
			throw new RuntimeException("Invalid vector array length for 2D vector dot product!");
		}

		return (vec1[0] * vec2[0]) + (vec1[1] * vec2[1]);
	}

	@Override
	public double noise(double x, double y) {
		return noiseAt(x + offsetX, y + offsetY);
	}

	private double noiseAt(double x, double y) {
		double floorX = MathsHelper.floor(x);
		double floorY = MathsHelper.floor(y);

		// local and faded x/y
		double localX = x - floorX;
		double localY = y - floorY;
		double fadeX = fade(localX);
		double fadeY = fade(localY);
		
		// distance gradients
		double[] distanceGradNW = new double[] {fadeX, fadeY - 1};
		double[] distanceGradNE = new double[] {fadeX - 1, fadeY - 1};
		double[] distanceGradSW = new double[] {fadeX, fadeY};
		double[] distanceGradSE = new double[] {fadeX - 1, fadeY};

		// dot products
		double dotNW = dot2d(randomGradAt(floorX, floorY + 1), distanceGradNW);
		double dotNE = dot2d(randomGradAt(floorX + 1, floorY + 1), distanceGradNE);
		double dotSW = dot2d(randomGradAt(floorX, floorY), distanceGradSW);
		double dotSE = dot2d(randomGradAt(floorX + 1, floorY), distanceGradSE);

		// bilinear interpolation
		return MathsHelper.lerp(fadeY,
				MathsHelper.lerp(fadeX, dotSW, dotSE), 
				MathsHelper.lerp(fadeX, dotNW, dotNE)
				);
	}

	private static double fade(double n) {
		return n * n * n * (n * (n * 6 - 15) + 10);
	}
	
	static {
		GRADS_LENGTH = GRADS.length;
	}
}
