package tk.valoeghese.noisetweaker.module;

import java.util.HashMap;
import java.util.Map;

public class TransformVarStorage {
	public final Map<String, Double> data = new HashMap<>();

	public void readPacket(TransformPacket transform) {
		int i = 0;
		double[] tDat = transform.getData();
		
		for (String name : transform.getNames()) {
			data.put(name, tDat[i]);
			++i;
		}
	}
	
}
