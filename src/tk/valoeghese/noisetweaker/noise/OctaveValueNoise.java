package tk.valoeghese.noisetweaker.noise;

import java.util.Random;

public final class OctaveValueNoise extends OctaveNoise {
	
	public OctaveValueNoise(Random rand, boolean fade, int octaves) {
		super(octaves);
		
		for (int i = 0; i < octaves; ++i) {
			samplers[i] = new ValueNoise(rand, fade);
		}
	}

}
