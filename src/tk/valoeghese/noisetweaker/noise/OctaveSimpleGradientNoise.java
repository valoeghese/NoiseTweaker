package tk.valoeghese.noisetweaker.noise;

import java.util.Random;

public final class OctaveSimpleGradientNoise extends OctaveNoise {
	
	public OctaveSimpleGradientNoise(Random rand, int octaves) {
		super(octaves);
		
		for (int i = 0; i < octaves; ++i) {
			samplers[i] = new SimpleGradientNoise(rand);
		}
	}

}
