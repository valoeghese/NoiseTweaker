package tk.valoeghese.noisetweaker.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class NoiseTransformers {
	private NoiseTransformers() {
	}
	
	private static final Map<String, NoiseTransformParser> transformers = new HashMap<>();
	
	// get .tfm file
	public static final NoiseTransformParser getTransformer(String name) {
		return transformers.computeIfAbsent(name, n -> new NoiseTransformParser(new File("./transformer/".concat(name).concat(".tfm"))));
	}
}
