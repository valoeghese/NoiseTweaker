package tk.valoeghese.noisetweaker.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.valoeghese.noisetweaker.module.NoiseProvider;
import tk.valoeghese.noisetweaker.module.TransformPacket;
import tk.valoeghese.noisetweaker.module.TransformVarStorage;
import tk.valoeghese.noisetweaker.module.Transformer;
import tk.valoeghese.noisetweaker.noise.OctaveOpenSimplexNoise;
import tk.valoeghese.noisetweaker.noise.OctaveSimpleGradientNoise;
import tk.valoeghese.noisetweaker.noise.OctaveValueNoise;

public class NoiseTransformParser {

	private final String[] in;
	private final String[] out;

	private static long globalindex = 0;

	private final long index;

	// map octave count to noise for each transform parser. this reduces load time.
	public final Map<Integer, OctaveOpenSimplexNoise> OSNOISE_CACHE = new HashMap<>();
	public final Map<Integer, OctaveValueNoise> VNOISE_CACHE = new HashMap<>();
	public final Map<Integer, OctaveSimpleGradientNoise> SGNOISE_CACHE = new HashMap<>();

	private final List<Transformer> sequence = new ArrayList<>();
	private List<String> prepared = new ArrayList<>();

	public NoiseTransformParser(File file) {
		index = globalindex++;

		try(FileReader reader = new FileReader(file)) {
			char[] chars = new char[Short.MAX_VALUE];
			reader.read(chars);

			String data = String.valueOf(chars).trim();

			byte readMode = 0; // 0 = head, 1 = main, 2 = in, 3 = out, 4 = load, 
			// 5 = VarName, 6 = VarValue, 7 = prepare, 8 = gennoise
			// 9 = loadif
			byte readMeta = 0; // var manipulation: 0 = set, 1 = add, 2 = sub, 3 = mul, 4 = div
			// noise: 0 = value, 1 = simplegradient, 2 = opensimplex, 3 = simplerandom
			// conditionals: 0 = eq (equal), 1 = greater, 2 = less, 3 = greatereq, 4 = lesseq, 5 = noteq
			List<String> inList = new ArrayList<>();
			List<String> outList = new ArrayList<>();

			for (String line : data.split("\n")) {
				line = line.trim();

				final char[] lineData = line.toCharArray();

				StringBuilder buffer = new StringBuilder();
				String tempBufferStringPrev = new String(); // for varname stuff

				for (int i = 0; i < lineData.length; ++i) {
					char c = lineData[i];
					if (readMode == 0) { // header
						if (i == lineData.length - 1) {
							buffer.append(c);
							if (buffer.toString().trim().equals("start")) {
								readMode = 1;
								buffer = new StringBuilder();
								continue;
							}
						}
						switch (c) {
						case ' ':
							String bufferString = buffer.toString().trim();
							if (bufferString.equals("#")) {
								continue;
							} else if (bufferString.equals("in")) {
								readMode = 2;
							} else if (bufferString.equals("out")) {
								readMode = 3;
							}
							buffer = new StringBuilder();
							break;
						default:
							buffer.append(c);
							break;
						}
					} else if (readMode == 1) { // main
						switch (c) {
						case ' ':
							String bufferString = buffer.toString().trim();

							if (bufferString.equals("#")) {
								continue;
							} else if (bufferString.equals("prepare")) {
								readMode = 7;
							} else if (bufferString.equals("load")) {
								readMode = 4;
								// VAR MANIPULATION =================
							} else if (bufferString.equals("set")) {
								readMode = 5;
								readMeta = 0;
							} else if (bufferString.equals("add")) {
								readMode = 5;
								readMeta = 1;
							} else if (bufferString.equals("sub")) {
								readMode = 5;
								readMeta = 2;
							} else if (bufferString.equals("mul")) {
								readMode = 5;
								readMeta = 3;
							} else if (bufferString.equals("div")) {
								readMode = 5;
								readMeta = 4;
								// NOISE ===========================
							} else if (bufferString.equals("gennoise_v")) {
								readMode = 8;
								readMeta = 0;
							} else if (bufferString.equals("gennoise_g")) {
								readMode = 8;
								readMeta = 1;
							} else if (bufferString.equals("gennoise_s")) {
								readMode = 8;
								readMeta = 2;
							} else if (bufferString.equals("gennoise")) {
								readMode = 8;
								readMeta = 3;
								// CONDITIONAL LOAD ================
							} else if (bufferString.equals("loadif_e")) {
								readMode = 9;
								readMeta = 0;
							} else if (bufferString.equals("loadif_g")) {
								readMode = 9;
								readMeta = 1;
							} else if (bufferString.equals("loadif_l")) {
								readMode = 9;
								readMeta = 2;
							} else if (bufferString.equals("loadif_ge")) {
								readMode = 9;
								readMeta = 3;
							} else if (bufferString.equals("loadif_le")) {
								readMode = 9;
								readMeta = 4;
							} else if (bufferString.equals("loadif_ne")) {
								readMode = 9;
								readMeta = 5;
							}
							buffer = new StringBuilder();
							break;
						default:
							buffer.append(c);
							break;
						}
					} else if (readMode == 2) { // in
						if (c == ' ') {
							throw new RuntimeException("Invalid space in input declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();
							inList.add(bufferString);
							readMode = 0;
							buffer = new StringBuilder();
						}
					} else if (readMode == 3) { // out
						if (c == ' ') {
							throw new RuntimeException("Invalid space in output declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();
							outList.add(bufferString);
							readMode = 0;
							buffer = new StringBuilder();
						}
					} else if (readMode == 4) { // load
						if (c == ' ') {
							throw new RuntimeException("Invalid space in load declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();
							sequence.add((vars, packetFn) -> {
								vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), packetFn.apply(vars)));
								prepared = new ArrayList<>();
							});
							readMode = 1;
							buffer = new StringBuilder();
						}
					} else if (readMode == 5) { // varname

						if (i == lineData.length - 1) {
							throw new RuntimeException("Invalid end of line in varname declaration for var manipulation!");
						} else if (c == ' ') {
							String bufferString = buffer.toString().trim();
							tempBufferStringPrev = bufferString;
							readMode = 6;
							buffer = new StringBuilder();
						} else {
							buffer.append(c);
						}
					} else if (readMode == 6) { // var manipulation
						if (c == ' ') {
							throw new RuntimeException("Invalid space in var manipulation!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();

							try {
								double bufferVal = Double.valueOf(bufferString).doubleValue();

								final String varName = tempBufferStringPrev;

								if (readMeta == 0) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, bufferVal));
								} else if (readMeta == 1) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) + bufferVal));
								} else if (readMeta == 2) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) - bufferVal));
								} else if (readMeta == 3) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) * bufferVal));
								} else if (readMeta == 4) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) / bufferVal));
								}
							} catch (NumberFormatException e) {
								final String varName = tempBufferStringPrev;
								if (readMeta == 0) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.get(bufferString)));
								} else if (readMeta == 1) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) + vars.data.get(bufferString)));
								} else if (readMeta == 2) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) - vars.data.get(bufferString)));
								} else if (readMeta == 3) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) * vars.data.get(bufferString)));
								} else if (readMeta == 4) {
									sequence.add((vars, packetFunc) -> vars.data.put(varName, vars.data.getOrDefault(varName, 0.0D) / vars.data.get(bufferString)));
								}
							}

							readMode = 1; // readMeta will always be initialised before stuff is called that uses it so no need to set it
							tempBufferStringPrev = new String();
							buffer = new StringBuilder();
						}
					} else if (readMode == 7) { // prepare
						if (c == ' ') {
							throw new RuntimeException("Invalid space in prepare declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();
							sequence.add((vars, packetFunc) -> prepared.add(bufferString));
							readMode = 1;
							buffer = new StringBuilder();
						}
					} else if (readMode == 8) { // gennoise
						if (c == ' ') {
							throw new RuntimeException("Invalid space in gennoise<?> declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();
							final NoiseProvider provider = NoiseProvider.of(readMeta);
							sequence.add((vars, packetFn) -> {
								vars.readPacket(new TransformPacket(
										provider.sample(this, packetFn.apply(vars)),
										new String[] {bufferString}));
								prepared = new ArrayList<>();
							});

							readMode = 1;
							buffer = new StringBuilder();
						}
					} else if (readMode == 9) { // loadifs
						if (c == ' ') {
							throw new RuntimeException("Invalid space in loadifs declaration!");
						}
						buffer.append(c);
						if (i == lineData.length - 1) {
							String bufferString = buffer.toString().trim();

							switch (readMeta) {
							case 0: // ==
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] == packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							case 1: // >
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] > packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							case 2: // <
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] < packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							case 3: // >=
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] >= packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							case 4: // <=
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] <= packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							case 5: // !=
								sequence.add((vars, packetFn) -> {
									TransformPacket packet = packetFn.apply(vars);
									double[] packetData = packetFn.apply(vars).getData();
									if (packetData[0] != packetData[1]) {
										vars.readPacket(NoiseTransformers.getTransformer(bufferString).transform(new TransformVarStorage(), TransformPacket.createExcludingFirst(2, packet)));
									}
									prepared = new ArrayList<>();
								});
								break;
							}

							readMode = 1;
							buffer = new StringBuilder();
						}
					}
				}
			}

			in = inList.toArray(new String[0]);
			out = outList.toArray(new String[0]);

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public TransformPacket transform(final TransformVarStorage vars, TransformPacket in) {
		// read vars from in
		final double[] inData = in.getData();

		int i = 0;

		for (String input : this.in) {
			vars.data.put(input, inData[i]);
			++i;
		}

		// tranform
		this.sequence.forEach(command -> command.transform(vars, this::createFromPrepared));

		// return. used for main
		return this.createOut(vars);
	}

	private TransformPacket createFromPrepared(final TransformVarStorage vars) {
		double[] protoOut = new double[this.prepared.size()];

		int i = 0;
		for (String outVar : this.prepared) {
			protoOut[i] = vars.data.get(outVar);
			++i;
		}

		return new TransformPacket(protoOut, this.out);
	}

	private TransformPacket createOut(final TransformVarStorage vars) {
		double[] protoOut = new double[this.out.length];

		int i = 0;
		for (String outVar : this.out) {
			protoOut[i] = vars.data.get(outVar);
			++i;
		}

		return new TransformPacket(protoOut, this.out);
	}

	public long index() {
		return index;
	}
}
