package tk.valoeghese.noisetweaker.util;

public final class MathsHelper {
	private MathsHelper() {
	}
	
	public static double lerp(double t, double n0, double n1) {
		return n0 + t * (n1 - n0);
	}
	
	public static double floor(double n) {
		int i = (int) n;
		return n < (double) i ? i - 1 : i;
	}
	
	public static double ceil(double n) {
		int i = (int) n;
		return n > (double) i ? i + 1 : i;
	}
}
