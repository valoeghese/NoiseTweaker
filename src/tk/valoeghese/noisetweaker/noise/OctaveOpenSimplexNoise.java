package tk.valoeghese.noisetweaker.noise;

import java.util.Random;

public final class OctaveOpenSimplexNoise extends OctaveNoise {
	
	public OctaveOpenSimplexNoise(Random rand, int octaves) {
		super(octaves);
		
		for (int i = 0; i < octaves; ++i) {
			samplers[i] = new OpenSimplexNoise(rand);
		}
	}

}
