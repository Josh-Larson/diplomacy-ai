package csci4511;

import csci4511.algorithms.Algorithm;
import csci4511.algorithms.UnitMonteCarlo;
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
import me.joshlarson.jlcommon.log.log_wrapper.FileLogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Diplomacy {
	
	public static void main(String[] args) {
		SafeMain.main("diplomacy", Diplomacy::runRanking);
	}
	
	/*
	Turkey:  (perf=0.62) Network[[0.76, 0.69, -0.07, 0.15, 0.20, -0.16, -0.16, 0.89, 0.56, 0.55, -0.88, -0.28, 0.25, 0.12]]
	England: (perf=0.58) Network[[0.98, 0.29, -0.38, 0.10, -0.59, -0.31, -0.56, 0.76, -0.17, -0.48, 0.65, -0.06, 1.11, 0.60]]
	Austria: (perf=0.82) Network[[0.79, 0.40, -0.20, 0.73, 0.37, -0.56, -1.08, 0.54, 0.57, -0.89, -0.36, 0.30, 0.61, 0.75]]
	Russia:  (perf=0.96) Network[[0.96, -0.82, -0.73, 0.20, -0.05, 0.29, -0.55, 0.96, -0.15, -0.65, -0.36, -0.86, 0.92, 0.18]]
	Germany: (perf=0.80) Network[[0.37, 0.09, -0.49, -0.72, 0.89, -0.11, -0.51, 0.45, -0.23, -0.78, -0.49, -0.06, 1.51, -0.93]]
	France:  (perf=0.78) Network[[0.46, -0.44, 0.21, 0.47, 0.44, -0.40, -0.49, 0.85, 0.77, 0.20, -0.55, -0.11, 0.97, -0.34]]
	Italy:   (perf=0.69) Network[[1.06, 0.33, -0.13, 0.11, 0.69, -0.11, -0.37, 0.95, 0.21, -0.09, -0.99, -0.12, 0.46, -0.60]]
	 */
	
	private static void run() {
		Log.addWrapper(new ConsoleLogWrapper());
		Board test = BoardFactory.createDefaultBoard();
		JFrame frame = DiplomacyUI.showBoard(test, new Dimension(1152, 965));
		EnumMap<Country, Algorithm> algorithms = new EnumMap<>(Country.class);
		algorithms.put(Country.ENGLAND, new SimpleHeuristic(new double[]{0.87, 0.29, 0.22, -0.09, 0.08, 0.1, -0.41, 0.6, 0.91, -0.09, 0.04, -0.2, 0.6, 0.3}));
		algorithms.put(Country.FRANCE, new SimpleHeuristic(new double[]{0.03, 0.17, -0.04, 0.54, -0.41, -1.04, -0.59, 1.08, -0.04, 0.76, 0.94, -0.14, 0.96, -0.96}));
		algorithms.put(Country.GERMANY, new SimpleHeuristic(new double[]{0.51, -0.71, -0.66, 0.17, -0.56, 0.39, -0.65, 0.92, 0.62, -0.13, 0.43, 0.24, 0.51, -0.67}));
		algorithms.put(Country.RUSSIA, new SimpleHeuristic(new double[]{0.83, 0.03, -0.55, 0.95, -0.13, 0.19, -0.55, 0.65, 1.39, 0.15, 0.77, 0, 0.19, -1.03}));
		algorithms.put(Country.ITALY, new SimpleHeuristic(new double[]{0.56, 0.66, -0.21, 0.43, -0.24, -0.51, -0.23, 0.54, 0.94, 0.76, 0, -0.09, 0.41, 0.25}));
		algorithms.put(Country.AUSTRIA, new SimpleHeuristic(new double[]{0.56, -1.04, -0.57, 0.21, 0.01, 0.02, -0.52, 0.75, -0.03, 0.73, 0.42, 0.05, 0.63, 0.93}));
		algorithms.put(Country.TURKEY, new SimpleHeuristic(new double[]{0.53, 0.14, -0.19, -0.36, 0.3, 0.14, 0, 0.98, 0.04, 0.08, -0.06, -0.09, 0.33, -0.39}));
		while (frame.isShowing()) {
			// Empty
			ExecutionUtilities.playIteration(test, algorithms::get);
			frame.repaint();
			if (!Delay.sleepMilli(1000))
				break;
		}
	}
	
	private static void runRanking() {
		EnumMap<Country, Algorithm> algorithms = new EnumMap<>(Country.class);
		algorithms.put(Country.ENGLAND, new SimpleHeuristic(new double[]{0.87, 0.29, 0.22, -0.09, 0.08, 0.1, -0.41, 0.6, 0.91, -0.09, 0.04, -0.2, 0.6, 0.3}));
		algorithms.put(Country.FRANCE, new SimpleHeuristic(new double[]{0.03, 0.17, -0.04, 0.54, -0.41, -1.04, -0.59, 1.08, -0.04, 0.76, 0.94, -0.14, 0.96, -0.96}));
		algorithms.put(Country.GERMANY, new SimpleHeuristic(new double[]{0.51, -0.71, -0.66, 0.17, -0.56, 0.39, -0.65, 0.92, 0.62, -0.13, 0.43, 0.24, 0.51, -0.67}));
		algorithms.put(Country.RUSSIA, new SimpleHeuristic(new double[]{0.83, 0.03, -0.55, 0.95, -0.13, 0.19, -0.55, 0.65, 1.39, 0.15, 0.77, 0, 0.19, -1.03}));
		algorithms.put(Country.ITALY, new SimpleHeuristic(new double[]{0.56, 0.66, -0.21, 0.43, -0.24, -0.51, -0.23, 0.54, 0.94, 0.76, 0, -0.09, 0.41, 0.25}));
		algorithms.put(Country.AUSTRIA, new SimpleHeuristic(new double[]{0.56, -1.04, -0.57, 0.21, 0.01, 0.02, -0.52, 0.75, -0.03, 0.73, 0.42, 0.05, 0.63, 0.93}));
		algorithms.put(Country.TURKEY, new SimpleHeuristic(new double[]{0.53, 0.14, -0.19, -0.36, 0.3, 0.14, 0, 0.98, 0.04, 0.08, -0.06, -0.09, 0.33, -0.39}));
		
		List<Country> countries = Arrays.asList(Country.values());
		countries.parallelStream().forEach(country -> {
			System.out.println("Starting " + country);
			EnumMap<Country, Integer> wins = new EnumMap<>(Country.class);
			UnitMonteCarlo monteCarlo = new UnitMonteCarlo();
			Function<Country, Algorithm> algorithmSelection = c -> (c == country ? monteCarlo : algorithms.get(c));
			for (int i = 0; i < 100; i++) {
				Board board = BoardFactory.createDefaultBoard();
				EnumMap<Country, Integer> results = ExecutionUtilities.play(board, algorithmSelection, 50);
				for (Country c : countries)
					wins.merge(c, results.get(c), (p, n) -> p+n);
//				System.out.println(results);
			}
			System.out.println("Final for "+country+": " + wins);
		});
	}
	
	private static void runTraining() {
		Log.addWrapper(new ConsoleLogWrapper());
		Log.addWrapper(new FileLogWrapper(new File("log.txt")));
		for (Country country : Country.values()) {
			runTraining(country);
		}
	}
	
	private static void runTraining(Country country) {
		int iterations = 256;
		int populationSize = 256;
		
		List<Organism> population = new ArrayList<>(populationSize);
		for (int test = 0; test < populationSize; test++)
			population.add(new Organism(country));
		
		Random random = new Random();
		for (int iter = 0; iter < iterations; iter++) {
			population.parallelStream().forEach(Organism::evaluate);
			Collections.sort(population);
//			Log.t("Iteration %d  Top Performer: %.2f  Low Performer: %.2f  %s", iter, population.get(0).getScore(), population.get(populationSize-1).getScore(), population.get(0).algorithm);
			{
				String weights = population.get(0).algorithm.toString();
				weights = weights.replace("Network[[", "").replace("]]", "").replace(" ", "");
				System.out.printf("%s\t%d\t%.2f\t%.2f\t%s%n", country, iter, population.get(0).getScore(), population.get(populationSize-1).getScore(), weights);
			}
			{
				int childrenCount = populationSize/10;
				for (int i = 0; i < childrenCount; i++) {
					population.set(random.nextInt(populationSize-childrenCount)+childrenCount, population.get(i).createChild());
				}
			}
			if (iter % 10 == 0 && populationSize > 64) {
				for (int i = 0; i < populationSize/2; i++) {
					population.remove(populationSize-1-i);
				}
				populationSize /= 2;
			}
		}
		
		Organism highest = population.get(0);
//		Log.d("Weights: (perf=%.2f) %s", highest.getScore(), highest.algorithm);
	}
	
	private static class Organism implements Comparable<Organism> {
		
		private static final RandomAlgorithm RANDOM = new RandomAlgorithm();
		
		private final SimpleHeuristic algorithm;
		private final Country country;
		private double score;
		
		public Organism(Country country) {
			this(country, new SimpleHeuristic());
		}
		
		private Organism(Country country, SimpleHeuristic algorithm) {
			this.algorithm = algorithm;
			this.country = country;
			this.score = 0;
		}
		
		public Organism createChild() {
			double [] currentWeights = algorithm.getWeights();
			double [] weights = Arrays.copyOf(currentWeights, currentWeights.length);
			Random random = new Random();
			weights[random.nextInt(weights.length)] += random.nextBoolean() ? 0.1 : -0.1;
			return new Organism(country, new SimpleHeuristic(weights));
		}
		
		public double getScore() {
			return score;
		}
		
		public void evaluate() {
			double updatedScore = ExecutionUtilities.play(BoardFactory.createDefaultBoard(), this::getAlgorithm, country, 10, 20);
			score = (score + updatedScore) / 2;
		}
		
		@Override
		public int compareTo(@NotNull Organism o) {
			return Double.compare(o.score, score);
		}
		
		private Algorithm getAlgorithm(Country country) {
			if (country == this.country)
				return algorithm;
			return RANDOM;
		}
	}
	
}
