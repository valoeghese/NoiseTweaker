package tk.valoeghese.noisetweaker.module;

import java.util.Random;

import tk.valoeghese.noisetweaker.io.NoiseTransformParser;
import tk.valoeghese.noisetweaker.noise.INoise2D;
import tk.valoeghese.noisetweaker.noise.OctaveOpenSimplexNoise;
import tk.valoeghese.noisetweaker.noise.OctaveSimpleGradientNoise;
import tk.valoeghese.noisetweaker.noise.OctaveValueNoise;
import tk.valoeghese.noisetweaker.util.SimpleRandom;

public abstract class NoiseProvider {
	
	private NoiseProvider() {
	}
	
	// 0 = value, 1 = simplegrad, 2 = opensimplex, 3 = random
	// if type doesn't exist return null
	public static final NoiseProvider of(byte type) {
		switch (type) {
		case 0:
			return VALUE;
		case 1:
			return SIMPLEGRAD;
		case 2:
			return OPENSIMPLEX;
		case 3:
			return RANDOM;
		default:
			return null;
		}
	}
	
	private static final NoiseProvider VALUE = new NoiseProvider() {
		@Override
		public double[] sample(NoiseTransformParser parent, TransformPacket packet) {
			double[] data = packet.getData();
			
			int octaves = data.length < 3 ? 8 : (int) (data[2]);
			INoise2D sampler = parent.VNOISE_CACHE.computeIfAbsent(octaves, octavecount -> new OctaveValueNoise(new Random(parent.index()), false, octavecount));
			
			return new double[] {sampler.noise(data[0], data[1])};
		}
	};
	
	private static final NoiseProvider SIMPLEGRAD = new NoiseProvider() {
		@Override
		public double[] sample(NoiseTransformParser parent, TransformPacket packet) {
			double[] data = packet.getData();
			
			int octaves = data.length < 3 ? 8 : (int) (data[2]);
			INoise2D sampler = parent.SGNOISE_CACHE.computeIfAbsent(octaves, octavecount -> new OctaveSimpleGradientNoise(new Random(parent.index()), octavecount));
			
			return new double[] {sampler.noise(data[0], data[1])};
		}
	};
	
	private static final NoiseProvider OPENSIMPLEX = new NoiseProvider() {
		@Override
		public double[] sample(NoiseTransformParser parent, TransformPacket packet) {
			double[] data = packet.getData();
			
			int octaves = data.length < 3 ? 8 : (int) (data[2]);
			INoise2D sampler = parent.OSNOISE_CACHE.computeIfAbsent(octaves, octavecount -> new OctaveOpenSimplexNoise(new Random(parent.index()), octavecount));
			
			return new double[] {sampler.noise(data[0], data[1])};
		}
	};
	
	private static final NoiseProvider RANDOM = new NoiseProvider() {
		@Override
		public double[] sample(NoiseTransformParser parent, TransformPacket packet) {
			double[] data = packet.getData();
			return new double[] {new SimpleRandom(parent.index()).randomDouble(data[0], data[1])};
		}
	};

	public abstract double[] sample(NoiseTransformParser parent, TransformPacket packet);
}
