package csci4511;

import csci4511.algorithms.Algorithm;
import csci4511.algorithms.heuristic.SimpleHeuristic;
import csci4511.algorithms.manual.ManualAlgorithm;
import csci4511.algorithms.random.RandomAlgorithm;
import csci4511.engine.ExecutionUtilities;
import csci4511.engine.data.Board;
import csci4511.engine.data.Country;
import csci4511.ui.DiplomacyUI;
import me.joshlarson.jlcommon.concurrency.Delay;
import me.joshlarson.jlcommon.control.SafeMain;
import me.joshlarson.jlcommon.log.Log;
import me.joshlarson.jlcommon.log.log_wrapper.ConsoleLogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Diplomacy {
	
	public static void main(String[] args) {
		SafeMain.main("diplomacy", Diplomacy::runTraining);
	}
	
	/*
	Turkey:		 0.94,  0.26, -0.93, -0.20, -0.32, -0.26, -0.67,  0.46,  0.69, -0.57,  0.17,  0.26,  0.02,  0.24
	England:	 1.06,  0.78, -0.72, -0.21,  0.33,  0.01, -0.03,  0.97, -0.05,  0.33,  0.49, -0.15, -0.29, -0.70
	Austria:	
	Russia:		
	Germany:	
	France:		
	Italy:		
	 */
	
	// Germany: 0.7883537936686507, 0.5669816179150046, -0.2957241494596424, 0.05922667445614062, -0.22627502227212262, -0.5024970476920462, -0.9685487780378496, -1.2142798046025092, -0.7289537161454374, 0.05915057876764081, 1.1078192885438924, 1.1641928347181476, -0.4412635372421468
	private static void run() {
		Log.addWrapper(new ConsoleLogWrapper());
		Board test = BoardFactory.createDefaultBoard();
		JFrame frame = DiplomacyUI.showBoard(test, new Dimension(1152, 965));
		EnumMap<Country, Algorithm> algorithms = new EnumMap<>(Country.class);
		for (Country country : Country.values()) {
			algorithms.put(country, new SimpleHeuristic(new double[]{0.62, -0.76, -0.02, 0.08, 0.23, 0.18, -0.55, 1.48, 0.01, -0.57, -0.19, -0.14, 0.62, -0.45, 0.63}));
		}
		algorithms.put(Country.TURKEY, new ManualAlgorithm());
		while (frame.isShowing()) {
			// Empty
			ExecutionUtilities.playIteration(test, algorithms::get);
			frame.repaint();
			if (!Delay.sleepMilli(1000))
				break;
		}
	}
	
	private static void runTraining() {
		int iterations = 250;
		int populationSize = 64;
		
		Log.addWrapper(new ConsoleLogWrapper());
		List<Organism> population = new ArrayList<>(populationSize);
		EnumMap<Country, Algorithm> algorithms = createAlgorithms();
		for (int test = 0; test < populationSize; test++)
			population.add(new Organism());
		
		BoardFactory factory = new BoardFactory();
		factory.start();
		Random random = new Random();
		for (int iter = 0; iter < iterations; iter++) {
			Log.t("Iteration %d  Top Performer: %.2f  Low Performer: %.2f  %s", iter, population.get(0).getScore(), population.get(populationSize-1).getScore(), population.get(0).algorithm);
			for (int i = 0; i < populationSize/10; i++) {
//				population.set(random.nextInt(populationSize), population.get(i*2).pair(population.get(i*2+1)));
				population.set(random.nextInt(populationSize), population.get(i).pair(null));
			}
			population.parallelStream().forEach(Organism::evaluate);
			Collections.sort(population);
			Organism best = population.get(0);
			if (iter % 10 == 0) {
				if (populationSize > 64) {
					for (int i = 0; i < populationSize/2; i++) {
						population.remove(populationSize-1-i);
					}
					populationSize /= 2;
				}
				if (best.getScore() >= 0.95) {
					for (Country country : Country.values()) {
						algorithms.put(country, new SimpleHeuristic(best.algorithm));
					}
				}
			}
		}
		factory.stop();
		
		Organism highest = population.get(0);
		Log.d("Weights: (perf=%.1f) %s", highest.getScore(), highest.algorithm);
	}
	
	private static EnumMap<Country, Algorithm> createAlgorithms() {
		EnumMap<Country, Algorithm> algorithms = new EnumMap<>(Country.class);
		for (Country country : Country.values()) {
			SimpleHeuristic algorithm = new SimpleHeuristic();
			algorithms.put(country, algorithm);
		}
		return algorithms;
	}
	
	private static class Organism implements Comparable<Organism> {
		
		private final SimpleHeuristic algorithm;
		private double score;
		
		public Organism() {
			this(new SimpleHeuristic());
		}
		
		private Organism(SimpleHeuristic algorithm) {
			this.algorithm = algorithm;
			this.score = 0;
		}
		
		public Organism pair(Organism o) {
			// Uncomment for GA
//			return new Organism(new SimpleHeuristic(algorithm, o.algorithm));
			
			double [] currentWeights = algorithm.getWeights();
			double [] weights = Arrays.copyOf(currentWeights, currentWeights.length);
			Random random = new Random();
			weights[random.nextInt(weights.length)] += random.nextBoolean() ? 0.1 : -0.1;
			return new Organism(new SimpleHeuristic(weights));
		}
		
		public double getScore() {
			return score;
		}
		
		public void evaluate() {
			RandomAlgorithm random = new RandomAlgorithm();
			Country country = Country.ENGLAND;
			double updatedScore = ExecutionUtilities.play(BoardFactory.createDefaultBoard(), c -> c == country ? algorithm : random, country, 10, 20);
			score = (score + updatedScore) / 2;
		}
		
		@Override
		public int compareTo(@NotNull Organism o) {
			return Double.compare(o.score, score);
		}
	}
	
}
