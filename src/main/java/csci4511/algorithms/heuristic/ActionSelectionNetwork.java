package csci4511.algorithms.heuristic;

import java.util.Arrays;
import java.util.Random;

public class ActionSelectionNetwork {
	
	private static final int INPUT_DATA = 16;
	
	private static double MUTATION_RATE = 0.1;
	private static double MUTATION_AMOUNT = 1 / 8.0;
	
	private final double [] weights;
	private String weightStr;
	
	public ActionSelectionNetwork() {
		Random random = new Random();
		this.weights = new double[INPUT_DATA];
		
		for (int i = 0; i < INPUT_DATA; i++) {
			weights[i] = ((int) ((random.nextDouble() * 2 - 1) * 100)) / 100.0;
		}
	}
	
	public ActionSelectionNetwork(ActionSelectionNetwork copy) {
		this.weights = Arrays.copyOf(copy.weights, INPUT_DATA);
	}
	
	public ActionSelectionNetwork(ActionSelectionNetwork left, ActionSelectionNetwork right) {
		Random random = new Random();
		this.weights = new double[INPUT_DATA];
		
		int swap = random.nextInt(weights.length);
		boolean keepLeft = random.nextBoolean();
		for (int i = 0; i < INPUT_DATA; i++) {
			if (keepLeft)
				weights[i] = left.weights[i];
			else
				weights[i] = right.weights[i];
			
			if (i == swap)
				keepLeft = !keepLeft;
		}
		
		// Mutation
		for (int i = 0; i < weights.length; i++) {
			if (random.nextDouble() <= MUTATION_RATE)
				weights[i] += random.nextBoolean() ? MUTATION_AMOUNT : -MUTATION_AMOUNT;
		}
	}
	
	public double [] getWeights() {
		return weights;
	}
	
	public void setWeights(double [] weights) {
		System.arraycopy(weights, 0, this.weights, 0, INPUT_DATA);
	}
	
	public double calculate(double [] inputs) {
		double sum = 0;
		for (int i = 0; i < INPUT_DATA; i++) {
			sum += inputs[i] * weights[i];
		}
		return sum;
	}
	
	@Override
	public String toString() {
		if (weightStr == null) {
			StringBuilder str = new StringBuilder("[");
			for (int i = 0; i < INPUT_DATA; i++) {
				str.append(String.format("%.2f", weights[i]));
				if (i+1 < INPUT_DATA)
					str.append(", ");
			}
			str.append(']');
			weightStr = str.toString();
		}
		return "Network["+weightStr+"]";
	}
	
}
