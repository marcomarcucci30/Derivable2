package milestone_two;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import milestone_one.ManageProperties;
import milestone_two.EnumContainer.*;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.*;

public class NonSoCheNomeDargli {
	
	private static Properties prop = ManageProperties.getInstance();
	private static final Logger log = Logger.getLogger(NonSoCheNomeDargli.class.getName());
	private static String project = "PROJECT";
	
	private static EnumContainer.Classifier classifier = null;
	private static EnumContainer.Feature feature = null;
	private static EnumContainer.Sampling sampling = null;
	
	private static Instances trainingSet = null;
	private static Instances testingSet = null;
	
	private static String Exception= "Exception";
	
	
	
	private static List<DataSetInstance> averageDataSetInstances = new ArrayList<>();
	static List<DataSetInstance> dataSetInstances = new ArrayList<>();
	private static int trainingRelease;
	

	public NonSoCheNomeDargli() {
		// Nothing to do
	}

	public static void main(String[] args) {
		ManageDataSet manageDataSet = new ManageDataSet();
		manageDataSet.setDataset(prop.getProperty(project)+"_Data_Set.csv");
		List<Integer> versionLineInfo = manageDataSet.retrieveVersionLineInfo();
		manageDataSet.csvToArff();
		
		//prova istances
		DataSource source = null;
		Instances dataset = null;
	
		try {
			source = new DataSource(prop.getProperty(project)+"_Data_Set.arff");
			dataset = source.getDataSet();
		} catch (Exception e) {
			log.log(Level.SEVERE, Exception , e);
		}
		
		
		/*size of vewrsionLineInfo is Number of release +1*/
		for (int i=0; i<versionLineInfo.size()-2; i++) {
			trainingRelease = i+1;
			//create Testing
			NonSoCheNomeDargli.trainingSet = new Instances(dataset, 0, versionLineInfo.get(i+1)-1);
			//create testing
			NonSoCheNomeDargli.testingSet = new Instances(dataset, versionLineInfo.get(i+1), versionLineInfo.get(i+2)-versionLineInfo.get(i+1));
			
			classifierSelection();
		}
		
		System.out.println("\nNumber of element in list: "+averageDataSetInstances.size());
		for (DataSetInstance dataSetInstance : averageDataSetInstances) {
			System.out.println("\n"+dataSetInstance.toString());
		}
		
		manageDataSet.createAverageDataset(averageDataSetInstances);
		manageDataSet.createDataset(dataSetInstances);
		
	}
	
	private static double majorityClass(Instances dataset, boolean choice) {
		int numInstances = dataset.numInstances();
		double yes=0;
		double no=0;
		int numAttr = dataset.numAttributes();
		for (int instIdx = 0; instIdx < numInstances; instIdx++) {
			Instance currInst = dataset.instance(instIdx);
			String buggyString = currInst.stringValue(numAttr-1);
			if (buggyString.equalsIgnoreCase("Yes")) {
				yes++;
			}else {
				no++;
			}
		}
		
		double numInstancesDouble = (double)dataset.numInstances();
		
		if (choice) {
			return yes/numInstancesDouble;
		}
		
		if (yes>no)
			return yes/numInstancesDouble;
		return no/numInstancesDouble;
	}
	
	
	private static void classifierSelection(){
		
		for (Classifier classifier : EnumContainer.Classifier.values()) {
			NonSoCheNomeDargli.classifier = classifier;
			featureSelection();
		}
		
	}

	private static void featureSelection() {
		for (Feature feature : EnumContainer.Feature.values()) {
			NonSoCheNomeDargli.feature = feature;
			Instances training = new Instances(trainingSet);
			Instances testing = new Instances(testingSet);
			
			switch (feature) {
			case NO_FEATURES_SELECTION:
				int numAttrFilteredNoFS = training.numAttributes();
				training.setClassIndex(numAttrFilteredNoFS-1);
				testing.setClassIndex(numAttrFilteredNoFS-1);
				samplingSelection(training, testing) ;
				break;
				
			case BEST_FIRST:
				//create AttributeSelection object
				AttributeSelection filter = new AttributeSelection();
				//create evaluator and search algorithm objects
				CfsSubsetEval eval = new CfsSubsetEval();
				GreedyStepwise search = new GreedyStepwise();
				//set the algorithm to search backward
				search.setSearchBackwards(true);
				//set the filter to use the evaluator and search algorithm
				filter.setEvaluator(eval);
				filter.setSearch(search);
				//specify the dataset
				Instances filteredTraining = null;
				Instances filteredTesting = null;
				try {
					filter.setInputFormat(training);
					//apply
					filteredTraining = Filter.useFilter(training, filter);
					
					//evaluation with filtered
					int numAttrFiltered = filteredTraining.numAttributes();
					//TODO IL SETCLASSINDEX FORSE CONVIEN FARLO ALLA FINE
					filteredTraining.setClassIndex(numAttrFiltered - 1);
					filteredTesting = Filter.useFilter(testing, filter);
					filteredTesting.setClassIndex(numAttrFiltered - 1);
				} catch (Exception e) {
					log.log(Level.SEVERE, Exception , e);
				}
				
				samplingSelection(filteredTraining, filteredTesting);
				
				break;

			default:
				break;
			}
			
			
			//TODO	valuare il classificatore con (classifier, feature, saapling) per lo specifico
			//run di walk-forward.
			
			
		}
	}

	private static void samplingSelection(Instances training, Instances testing) {
		for (Sampling sampling : EnumContainer.Sampling.values()) {
			NonSoCheNomeDargli.sampling = sampling;
			FilteredClassifier fc = new FilteredClassifier();
			/*Instances filteredTraining = training;
			Instances filteredTesting = testing;*/
			
			Instances filteredTraining = new Instances(training);
			Instances filteredTesting = new Instances(testing);
			switch (sampling) {
			case NO_SAMPLING:
				evaluate(filteredTraining, filteredTesting, null);
				break;
			case OVER_SAMPLING:
				String zeta = String.valueOf(2.0*majorityClass(filteredTraining, false));
				Resample  resample = new Resample();
				String[] optsOver = new String[]{"-B", "1.0", "-Z", zeta, "-no-replacement"};
				try {
					resample.setOptions(optsOver);
					// TODO CAPIRE SE CI VA O MENO
					resample.setInputFormat(training);
				} catch (Exception e) {
					log.log(Level.SEVERE, Exception , e);
				}
								
				fc.setFilter(resample);
				
				evaluate(filteredTraining, filteredTesting, fc);
				break;
			case UNDER_SAMPLING:
				SpreadSubsample  spreadSubsample = new SpreadSubsample();
				String[] optsUnder = new String[]{ "-M", "1.0"};
				try {
					spreadSubsample.setOptions(optsUnder);
				} catch (Exception e) {
					log.log(Level.SEVERE, Exception , e);
				}
				fc.setFilter(spreadSubsample);
				evaluate(filteredTraining, filteredTesting, fc);
				break;
			case SMOTE:
				SMOTE smote = new SMOTE();
				try {
					smote.setInputFormat(filteredTraining);
				} catch (Exception e) {
					log.log(Level.SEVERE, Exception , e);
				}
				fc.setFilter(smote);
				evaluate(filteredTraining, filteredTesting, fc);
				break;
			default:
				break;
			}
		}
	}
	
	
	private static void evaluate(Instances training, Instances testing, FilteredClassifier fc) {
		System.out.println("Instances Testing: "+testing.numInstances());
		weka.classifiers.Classifier classifierWeka = null;
		switch (classifier) {
		case NAIVE_BAYES:
			classifierWeka = new NaiveBayes();
			break;
		case RANDOM_FOREST:
			classifierWeka = new RandomForest();
			break;
		case IBK:
			classifierWeka = new IBk();
			break;
		default:
			break;
		}
		Evaluation evaluation = null;
		if (fc != null) {
			
			fc.setClassifier(classifierWeka);
			
			try {
				fc.buildClassifier(training);
				evaluation = new Evaluation(testing);
				evaluation.evaluateModel(fc, testing);
			} catch (Exception e) {
				//System.out.println(training.toString());
				System.out.println("Classifier: "+classifier+", Feature: "+feature+", Sampling: "+sampling);
				log.log(Level.SEVERE, Exception , e);
				return;
			}
		}
		else {
			try {
				if (Objects.isNull(classifierWeka)) {
					return;
				}
				classifierWeka.buildClassifier(training);
				evaluation = new Evaluation(testing);
				evaluation.evaluateModel(classifierWeka, testing);
			} catch (Exception e) {
				log.log(Level.SEVERE, Exception , e);
			}
		}
		if (Objects.isNull(evaluation)) {
			return;
		}
		System.out.println("Classifier: "+classifier+", Feature: "+feature+", Sampling: "+sampling+", number of training release: "+trainingRelease);
		System.out.println("Kappa: "+evaluation.kappa()+", Recall: "+evaluation.recall(1));
		
		
		
		System.out.println(evaluation.confusionMatrix()[0][0]+", "+evaluation.confusionMatrix()[0][1]+", "+evaluation.confusionMatrix()[1][0]+
				", "+evaluation.confusionMatrix()[1][1]+", "+evaluation.numTruePositives(0)+", "+ evaluation.numFalsePositives(0)+", "+ 
				evaluation.trueNegativeRate(1)+", "+evaluation.falseNegativeRate(1));
		
		System.out.println(evaluation.correct());
		System.out.println(evaluation.incorrect());
		System.out.println(evaluation.precision(1));
		
		try {
			System.out.println(evaluation.toClassDetailsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("num of instances: "+ evaluation.numInstances()+", correct: "+evaluation.correct()+", uncorrect: "+evaluation.incorrect()+
				", percentageIncorrect: "+evaluation.pctCorrect()+", percentageIncorrect: "+evaluation.pctIncorrect());
		
		System.out.println(evaluation.numTruePositives(0));
		System.out.println(evaluation.numFalsePositives(0));
		
		double sizeDataset = NonSoCheNomeDargli.trainingSet.size() + NonSoCheNomeDargli.testingSet.size();
		double trainingPercentage = NonSoCheNomeDargli.trainingSet.size() / sizeDataset;
		
		double defectiveTrainingPercentage = majorityClass(training, true);
		double defectiveTestingPercentage = majorityClass(testing, true);
		
		DataSetInstance dataSetInstance = new DataSetInstance(prop.getProperty(project), trainingRelease, trainingPercentage, defectiveTrainingPercentage, 
				defectiveTestingPercentage, classifier, feature, sampling, evaluation.numTruePositives(0), evaluation.numFalsePositives(0), 
				evaluation.numTruePositives(1),evaluation.numFalsePositives(1), evaluation.precision(1), evaluation.recall(1), 
				evaluation.areaUnderROC(1), evaluation.kappa());
		
		DataSetInstance dataSetInstance2 = new DataSetInstance(prop.getProperty(project), trainingRelease, trainingPercentage, defectiveTrainingPercentage, 
				defectiveTestingPercentage, classifier, feature, sampling, evaluation.numTruePositives(0), evaluation.numFalsePositives(0), 
				evaluation.numTruePositives(1),evaluation.numFalsePositives(1), evaluation.precision(1), evaluation.recall(1), 
				evaluation.areaUnderROC(1), evaluation.kappa());
		
		dataSetInstances.add(dataSetInstance2);
		
		if (!averageDataSetInstances.contains(dataSetInstance)) {
			averageDataSetInstances.add(dataSetInstance);
			dataSetInstance.setnRun(dataSetInstance.getnRun()+1);
			return;
		}
		
		int indexInstance = averageDataSetInstances.indexOf(dataSetInstance);
		averageDataSetInstances.get(indexInstance).update(dataSetInstance);
		
		
	}

	/**
	 * @return the classifier
	 */
	public EnumContainer.Classifier getClassifier() {
		return classifier;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(EnumContainer.Classifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * @return the feature
	 */
	public EnumContainer.Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(EnumContainer.Feature feature) {
		this.feature = feature;
	}

	/**
	 * @return the sampling
	 */
	public EnumContainer.Sampling getSampling() {
		return sampling;
	}

	/**
	 * @param sampling the sampling to set
	 */
	public void setSampling(EnumContainer.Sampling sampling) {
		this.sampling = sampling;
	}

}
