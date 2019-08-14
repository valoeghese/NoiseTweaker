package tk.valoeghese.noisetweaker.util;

public final class SimpleRandom {
	
	private long seed;
	private long localSeed;
	
	public SimpleRandom(long seed) {
		this.seed = seed;
		this.localSeed = seed;
	}
	
	public int random(double x, double y, int bound) {
		init(x, y);
		
		int result = (int) ((localSeed >> 24) % bound);
		if (result < 0) {
			result += (bound - 1);
		}
		
		return result;
	}
	
	public double randomDouble(double x, double y) {
		return (double) this.random(x, y, Integer.MAX_VALUE) / (double) Integer.MAX_VALUE;
	}
	
	public int random(int bound) {
		int result = (int) ((localSeed >> 24) % bound);
		if (result < 0) {
			result += (bound - 1);
		}
		
		localSeed += seed;
		localSeed *= 3412375462423L * localSeed + 834672456235L;
		
		return result;
	}
	
	public double randomDouble() {
		return (double) this.random(Integer.MAX_VALUE) / (double) Integer.MAX_VALUE;
	}
	
	public void init(double x, double y) {
		localSeed = seed;
		localSeed += (x * 72624D);
		localSeed *= 3412375462423L * localSeed + 834672456235L;
		localSeed += (y * 8963D);
		localSeed *= 3412375462423L * localSeed + 834672456235L;
	}
}
