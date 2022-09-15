package pmt.scripts.mutation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mutation_score {
	public static Mutation_score mine = new Mutation_score();

	public static void main(String[] args) throws IOException, NumberFormatException {

		List<measurement> ju2jmh_measurements = extract_measurements(new FileReader("ju2jmh.csv"));

		List<measurement> jmh_measurements = extract_measurements(new FileReader("jmh.csv"));
		List<String> allMutants_jmh = extract_all_mutants(jmh_measurements);

		List<measurement> junit_measurements = extract_measurements(new FileReader("junit.csv"));

		List<measurement> autojmh_measurements = extract_measurements(new FileReader("autojmh.csv"));
		List<String> allMutants = extract_all_mutants(autojmh_measurements);


		List<String> killed_1_ju2jmh = new ArrayList<>();
		List<String> killed_5_ju2jmh = new ArrayList<>();
		List<String> killed_10_ju2jmh = new ArrayList<>();

		List<String> killed_1_jmh = new ArrayList<>();
		List<String> killed_5_jmh = new ArrayList<>();
		List<String> killed_10_jmh = new ArrayList<>();

		List<String> killed_1_junit = new ArrayList<>();
		List<String> killed_5_junit = new ArrayList<>();
		List<String> killed_10_junit = new ArrayList<>();

		List<String> killed_1_autojmh = new ArrayList<>();
		List<String> killed_5_autojmh = new ArrayList<>();
		List<String> killed_10_autojmh = new ArrayList<>();

		killed_1_ju2jmh.addAll(extract_killed_mutants(ju2jmh_measurements, 1.0));
		killed_5_ju2jmh.addAll(extract_killed_mutants(ju2jmh_measurements, 5.0));
		killed_10_ju2jmh.addAll(extract_killed_mutants(ju2jmh_measurements, 10.0));

		killed_1_jmh.addAll(extract_killed_mutants(jmh_measurements, 1.0));
		killed_5_jmh.addAll(extract_killed_mutants(jmh_measurements, 5.0));
		killed_10_jmh.addAll(extract_killed_mutants(jmh_measurements, 10.0));

		killed_1_junit.addAll(extract_killed_mutants(junit_measurements, 1.0));
		killed_5_junit.addAll(extract_killed_mutants(junit_measurements, 5.0));
		killed_10_junit.addAll(extract_killed_mutants(junit_measurements, 10.0));

		killed_1_autojmh.addAll(extract_killed_mutants(autojmh_measurements, 1.0));
		killed_5_autojmh.addAll(extract_killed_mutants(autojmh_measurements, 5.0));
		killed_10_autojmh.addAll(extract_killed_mutants(autojmh_measurements, 10.0));

		print_mutation_score(killed_1_jmh,killed_5_jmh,killed_10_jmh,allMutants_jmh,"jmh");
		print_mutation_score(killed_1_ju2jmh,killed_5_ju2jmh,killed_10_ju2jmh,allMutants,"ju2jmh");
		print_mutation_score(killed_1_junit,killed_5_junit,killed_10_junit,allMutants,"junit");
		print_mutation_score(killed_1_autojmh,killed_5_autojmh,killed_10_autojmh,allMutants,"autojmh");
	}


	private static void print_mutation_score(List<String> killed_1, List<String> killed_5,
			List<String> killed_10, List<String> allMutants, String type) {

		double tmp1 = (double)killed_1.size() / (double)allMutants.size();
		double tmp2 = (double)killed_5.size() / (double)allMutants.size();
		double tmp3 = (double)killed_10.size() / (double)allMutants.size();
		System.out.println(type+":  "+tmp1 + " ," + tmp2 + " ," + tmp3);

	}

	private static List<String> extract_all_mutants(List<measurement> autojmh_measurements) {
		List<String> allMutants = new ArrayList<>();
		autojmh_measurements.forEach(a -> {
			if(!allMutants.contains(a.mutant))
			allMutants.add(a.mutant);
		});
		return allMutants;
	}

	private static Collection<? extends String> extract_killed_mutants(List<measurement> measurements, double i) {
		List<String> extracted = new ArrayList<>();
		for (int j = 0; j < measurements.size(); j++) {
			measurement myMeasurement = measurements.get(j);
			double max_urci = 1.0 - (i / 100.0);
			if (myMeasurement.upper_rci <= max_urci) {
				String mutant = myMeasurement.mutant.toString();
				if (!extracted.contains(mutant)) {
					extracted.add(mutant);
				}
			}
		}
		return extracted;
	}

	private void writeFiles(List<measurement> measurements, String type) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (measurement myMeasurement : measurements) {
			sb.append(myMeasurement.bench).append(',').append(myMeasurement.upper_rci).append(',')
					.append(myMeasurement.mutant);

			sb.append("\n");

		}
		BufferedWriter br = new BufferedWriter(new FileWriter("/Users/massi/Desktop/result_mutant_" + type + ".csv"));

		try {
			br.write(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		br.close();
	}

	public static List<measurement> extract_measurements(FileReader fr) throws NumberFormatException, IOException {
		List<measurement> measurements = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(fr)) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				String name = values[0];

				measurement myMeasurement = mine.new measurement(name, values[68], Double.parseDouble(values[67]));
				measurements.add(myMeasurement);
			}
		}
		return measurements;
	}

	class bench {
		public String name = "";
		public String values = "";

		public bench(String name, String values) {
			this.name = name;
			this.values = values;
		}
	}

	class mutant {
		public String name = "";
		public String values = "";

		public mutant(String name, String values) {
			this.name = name;
			this.values = values;
		}
	}

	class measurement {
		public String bench = "";
		public String mutant = "";
		public double upper_rci = 0.0;

		public measurement(String bench, String mutant, double upper_rci) {
			this.bench = bench;
			this.mutant = mutant;
			this.upper_rci = upper_rci;
		}
	}

}
