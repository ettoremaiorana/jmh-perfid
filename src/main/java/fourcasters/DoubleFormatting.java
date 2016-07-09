package fourcasters;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import fourcasters.perfid.SpeedyDoubleFormatter;

public class DoubleFormatting {

    
    @State(Scope.Benchmark)
    public static class StringBufferState {
    	final StringBuilder sb = new StringBuilder(1024);
    }

    @State(Scope.Benchmark)
    public static class ClassicDecimalFormat {
    	final DecimalFormat df = new DecimalFormat();
    }

    @State(Scope.Benchmark)
    public static class SamplesState {
    	final long rangeMin = -1024*1024*1024*1023;
    	final long rangeMax = rangeMin*-1;
    	final int SIZE = 30000;
    	final Random r = new Random(System.currentTimeMillis());
    	final double[] records = new double[SIZE];

    	public SamplesState() {
    		for (int i = 0; i < SIZE; i++) {
    			double d = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    			records[i] = d;
    		}
    	}
    }

	@Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
	public void speedyTest(SamplesState ss, StringBufferState state, Blackhole bh) {
		for (double d : ss.records) {
			SpeedyDoubleFormatter.append(state.sb, d);
			bh.consume(state.sb.toString());
			state.sb.setLength(0);
		}
	}

	@Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
	public void superslow(SamplesState ss, ClassicDecimalFormat state, Blackhole bh) {
		for (double d : ss.records) {
			bh.consume(state.df.format(d));
		}
	}
}
