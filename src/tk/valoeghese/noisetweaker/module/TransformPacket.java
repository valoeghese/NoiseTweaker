package tk.valoeghese.noisetweaker.module;

public class TransformPacket {
	private final double[] data;
	private final String[] names;
	
	public TransformPacket(final double[] data, final String[] names) {
		this.data = data;
		this.names = names;
	}
	
	public double[] getData() {
		return data;
	}
	
	public String[] getNames() {
		return names;
	}
	
	public static final double[] concat(double[] arg0, double[] arg1) {
		double[] returns = new double[arg0.length + arg1.length];
		
		for (int i = 0; i < arg0.length; ++i) {
			returns[i] = arg0[i];
		}
		for (int i = 0; i < arg1.length; ++i) {
			returns[i + arg0.length] = arg1[i];
		}
		
		return returns;
	}

	public static final TransformPacket createExcludingFirst(int excluding, TransformPacket packet) {
		double[] inData = packet.getData();
		String[] inNames = packet.getNames();
		
		if (inData.length == excluding) {
			return NULL;
		}
		
		double[] resultData = new double[inData.length - excluding];
		String[] resultNames = new String[inNames.length - excluding];
		
		for (int i = 0; i < resultData.length; ++i) {
			resultData[i] = inData[i + excluding];
			resultNames[i] = inNames[i + excluding];
		}
		
		return new TransformPacket(resultData, resultNames);
	}
	
	public static final TransformPacket NULL = new TransformPacket(new double[] {}, new String[] {});
}
