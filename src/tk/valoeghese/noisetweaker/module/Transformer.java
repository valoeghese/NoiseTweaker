package tk.valoeghese.noisetweaker.module;

import java.util.function.Function;

public interface Transformer {
	public void transform(TransformVarStorage data, Function<TransformVarStorage, TransformPacket> packetFunction);
}
