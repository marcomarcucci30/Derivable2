package milestone_two;

import milestone_two.EnumContainer.Classifier;
import milestone_two.EnumContainer.Feature;
import milestone_two.EnumContainer.Sampling;

public class DataSetInstance {
		
	private String dataset;
	private int trainingRelease;//n di training release
	private double trainingPercentage;//
	private double defectiveTrainingPercentage;
	private double defectiveTestingPercentage;
	private Classifier classifier;
	private Feature feature;
	private Sampling sampling;
	private double truePositive;
	private double falsePositive;
	private double trueNegative;
	private double falseNegative;
	private double precision;
	private double recall;
	private double rocArea;
	private double kappa;
	
	private double nRun = 0;

	public DataSetInstance() {
		// nothing to do
	}

	

	public DataSetInstance(String project2, int trainingRelease2, double trainingPercentage2, double defectiveTrainingPercentage2, double defectiveTestingPercentage2, Classifier classifier2,
			Feature feature2){
		setDataset(project2);
		setTrainingRelease(trainingRelease2);
		setTrainingPercentage(trainingPercentage2);
		setDefectiveTrainingPercentage(defectiveTrainingPercentage2);
		setDefectiveTestingPercentage(defectiveTestingPercentage2);
		setClassifier(classifier2);
		setFeature(feature2);
	}
	
	public void update(DataSetInstance dataSetInstance) {
		setnRun(getnRun()+1);
		
		setTruePositive(getTruePositive()*((getnRun()-1)/getnRun()) + (dataSetInstance.getTruePositive()*(1/getnRun())));
		setFalsePositive(getFalsePositive()*((getnRun()-1)/getnRun()) + (dataSetInstance.getFalsePositive()*(1/getnRun())));
		setTrueNegative(getTrueNegative()*((getnRun()-1)/getnRun()) + (dataSetInstance.getTrueNegative()*(1/getnRun())));
		setFalseNegative(getFalseNegative()*((getnRun()-1)/getnRun()) + (dataSetInstance.getFalseNegative()*(1/getnRun())));
		
		setPrecision(getPrecision()*((getnRun()-1)/getnRun()) + (dataSetInstance.getPrecision()*(1/getnRun())));
		setRecall(getRecall()*((getnRun()-1)/getnRun()) + (dataSetInstance.getRecall()*(1/getnRun())));
		setRocArea(getRocArea()*((getnRun()-1)/getnRun()) + (dataSetInstance.getRocArea()*(1/getnRun())));
		setKappa(getKappa()*((getnRun()-1)/getnRun()) + (dataSetInstance.getKappa()*(1/getnRun())));
	}

	public static void main(String[] args) {
		// nothing to do
	}

	/**
	 * @return the dataset
	 */
	public String getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the trainingRelease
	 */
	public int getTrainingRelease() {
		return trainingRelease;
	}

	/**
	 * @param trainingRelease the trainingRelease to set
	 */
	public void setTrainingRelease(int trainingRelease) {
		this.trainingRelease = trainingRelease;
	}

	/**
	 * @return the trainingPercentage
	 */
	public double getTrainingPercentage() {
		return trainingPercentage;
	}

	/**
	 * @param trainingPercentage the trainingPercentage to set
	 */
	public void setTrainingPercentage(double trainingPercentage) {
		this.trainingPercentage = trainingPercentage;
	}

	/**
	 * @return the defectiveTrainingPercentage
	 */
	public double getDefectiveTrainingPercentage() {
		return defectiveTrainingPercentage;
	}

	/**
	 * @param defectiveTrainingPercentage the defectiveTrainingPercentage to set
	 */
	public void setDefectiveTrainingPercentage(double defectiveTrainingPercentage) {
		this.defectiveTrainingPercentage = defectiveTrainingPercentage;
	}

	/**
	 * @return the defectiveTestingPercentage
	 */
	public double getDefectiveTestingPercentage() {
		return defectiveTestingPercentage;
	}

	/**
	 * @param defectiveTestingPercentage the defectiveTestingPercentage to set
	 */
	public void setDefectiveTestingPercentage(double defectiveTestingPercentage) {
		this.defectiveTestingPercentage = defectiveTestingPercentage;
	}

	/**
	 * @return the classifier
	 */
	public Classifier getClassifier() {
		return classifier;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * @return the sampling
	 */
	public Sampling getSampling() {
		return sampling;
	}

	/**
	 * @param sampling the sampling to set
	 */
	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}

	/**
	 * @return the truePositive
	 */
	public double getTruePositive() {
		return truePositive;
	}

	/**
	 * @param truePositive the truePositive to set
	 */
	public void setTruePositive(double truePositive) {
		this.truePositive = truePositive;
	}

	/**
	 * @return the falsePositive
	 */
	public double getFalsePositive() {
		return falsePositive;
	}

	/**
	 * @param falsePositive the falsePositive to set
	 */
	public void setFalsePositive(double falsePositive) {
		this.falsePositive = falsePositive;
	}

	/**
	 * @return the trueNegative
	 */
	public double getTrueNegative() {
		return trueNegative;
	}

	/**
	 * @param trueNegative the trueNegative to set
	 */
	public void setTrueNegative(double trueNegative) {
		this.trueNegative = trueNegative;
	}

	/**
	 * @return the falseNegative
	 */
	public double getFalseNegative() {
		return falseNegative;
	}

	/**
	 * @param falseNegative the falseNegative to set
	 */
	public void setFalseNegative(double falseNegative) {
		this.falseNegative = falseNegative;
	}

	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}
	/**
	 * @return the rocArea
	 */
	public double getRocArea() {
		return rocArea;
	}

	/**
	 * @param rocArea the rocArea to set
	 */
	public void setRocArea(double rocArea) {
		this.rocArea = rocArea;
	}

	/**
	 * @return the kappa
	 */
	public double getKappa() {
		return kappa;
	}

	/**
	 * @param kappa the kappa to set
	 */
	public void setKappa(double kappa) {
		this.kappa = kappa;
	}

	public double getnRun() {
		return nRun;
	}

	public void setnRun(double nRun) {
		this.nRun = nRun;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
		result = prime * result + ((sampling == null) ? 0 : sampling.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSetInstance other = (DataSetInstance) obj;
		if (classifier != other.classifier)
			return false;
		if (dataset == null) {
			if (other.dataset != null)
				return false;
		} else if (!dataset.equals(other.dataset))
			return false;
		return (feature == other.feature && sampling == other.sampling);
			
		
	}

	@Override
	public String toString() {
		return "DataSetInstance [dataset=" + dataset + ", trainingRelease=" + trainingRelease + ", trainingPercentage="
				+ trainingPercentage + ", defectiveTrainingPercentage=" + defectiveTrainingPercentage +", defectiveTestingPercentage=" + defectiveTestingPercentage + ", classifier="
				+ classifier + ", feature=" + feature + ", sampling=" + sampling + ", truePositive=" + truePositive
				+ ", falsePositive=" + falsePositive + ", trueNegative=" + trueNegative + ", falseNegative="
				+ falseNegative + ", precision=" + precision + ", recall=" + recall + ", rocArea=" + rocArea
				+ ", kappa=" + kappa + ", nRun=" + nRun + "]";
	}
	
	
	public String toStringForAverageDataset() {
		return dataset + ", "+classifier + ", " + feature + ", " + sampling + ", " + truePositive+ ", " + falsePositive + 
				", " + trueNegative + ", "+ falseNegative + ", " + precision + ", " + recall + ", " + rocArea+ ", " 
				+ kappa +"\n";
	}
	
	public String toStringForDataset() {
		return dataset + ", " + trainingRelease + ", "+ trainingPercentage + ", "+defectiveTrainingPercentage+", " + defectiveTestingPercentage + 
				", "+ classifier + ", " + feature + ", " + sampling + ", " + truePositive+ ", " + falsePositive + 
				", " + trueNegative + ", "+ falseNegative + ", " + precision + ", " + recall + ", " + rocArea+ ", " 
				+ kappa + "\n";
	}

}
