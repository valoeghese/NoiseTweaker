package tk.valoeghese.noisetweaker.noise;

public abstract class OctaveNoise implements INoise2D {
	
	protected INoise2D[] samplers;
	private double clamp;
	
	public OctaveNoise(int octaves) {
		samplers = new INoise2D[octaves];
		clamp = 1D / (1D - (1D / Math.pow(2, octaves)));
	}
	
	@Override
	public double noise(double x, double y) {
		double amplFreq = 0.5D;
		double result = 0;
		for (INoise2D sampler : samplers) {
			result += (amplFreq * sampler.noise(x / amplFreq, y / amplFreq));
			
			amplFreq *= 0.5D;
		}
		
		return result * clamp;
	}

}
