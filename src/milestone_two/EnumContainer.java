package milestone_two;

public class EnumContainer {
	
	static enum Classifier {NAIVE_BAYES, RANDOM_FOREST, IBK};
	static enum Feature {NO_FEATURES_SELECTION, BEST_FIRST};
	static enum Sampling {NO_SAMPLING, OVER_SAMPLING, UNDER_SAMPLING, SMOTE};

	public EnumContainer() {
		// Nothing to do
	}

	public static void main(String[] args) {
		//nothing to do
		System.out.println(Classifier.NAIVE_BAYES);
	}

}
