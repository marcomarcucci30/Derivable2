package milestone_two;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import milestone_one.ManageProperties;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class ManageDataSet {
	
	
	private static final Logger log = Logger.getLogger(ManageDataSet.class.getName());
	private static Properties prop = ManageProperties.getInstance();
	private static String fileNotFoundException = "FileNotFoundException in ManageFile.";
	private static String iOException = "IOException in ManageFile.";
	private String dataset = null;
	private static String project = "PROJECT";
	
	public ManageDataSet() {
		// empty
	}
	
	
	/*Retrieve number of istances for each version in the dataset.
	 * Number of Versions of the dataset must be <100*/
	public List<Integer> retrieveVersionLineInfo() {
		File file = new File(this.dataset);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,fileNotFoundException, e);
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = null;
		String prevLine = null;
		int lineNumber = 0;
		List<Integer> versionLineInfo = new ArrayList<>();
		boolean firstLine = true;
		
		try {
			while ((line=bufferedReader.readLine()) != null) {
				if (firstLine) {
					firstLine= false;
					continue;
				}
				lineNumber++;
				if (!line.substring(0, 2).equalsIgnoreCase(prevLine)) {
					versionLineInfo.add(lineNumber);
				}
				prevLine = line.substring(0,2);
			}
			versionLineInfo.add(lineNumber);
		} catch (java.io.IOException e) {
			log.log(Level.SEVERE, iOException , e);
		}finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return versionLineInfo;
	}
	
	/*Create file arff from csv*/
	public void csvToArff() {
		// load CSV
		Instances data = null;
	    CSVLoader loader = new CSVLoader();
	    try {
			loader.setSource(new File(this.dataset));
			data = loader.getDataSet();//get instances object
		} catch (IOException e) {
			log.log(Level.SEVERE, iOException , e);
		}
	    
	    // save ARFF
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(data);//set the dataset we want to convert
	    //and save as ARFF
	    try {
	    	saver.setFile(new File(prop.getProperty("PROJECT")+"_Data_Set.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	
	public void createAverageDataset(List<DataSetInstance> dataSetInstances) {
		StringBuilder sb = new StringBuilder();
		sb.append("Dataset, Classifier, Feature Selection, "
				+ "Balancing,TP, FP, TN, FN, Precision, Recall, ROC Area, Kappa\n");
		
		for (DataSetInstance dataSetInstance : dataSetInstances) {
			sb.append(dataSetInstance.toStringForAverageDataset());
		}
		
		try (PrintWriter writer = new PrintWriter(new File(prop.getProperty(project)+"_Average_Data_Set_Classifier.csv"))){
			//create file
			//Write CSV file
			writer.write(sb.toString());
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,"FileNotFoundException", e);
		}
	}
	
	public void createDataset(List<DataSetInstance> dataSetInstances) {
		StringBuilder sb = new StringBuilder();
		sb.append("Dataset, #TrainingRelease, %training, %Defective in training, %Defective in testing, Classifier, "
				+ "Feature Selection, Balancing, TP, FP, TN, FN, Precision, Recall, ROC Area, Kappa\n");
		
		for (DataSetInstance dataSetInstance : dataSetInstances) {
			sb.append(dataSetInstance.toStringForDataset());
		}
		
		try (PrintWriter writer = new PrintWriter(new File(prop.getProperty(project)+"_Data_Set_Classifier.csv"))){
			//create file
			//Write CSV file
			writer.write(sb.toString());
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,"FileNotFoundException", e);
		}
	}

	public static void main(String[] args) {
		String dataset = "Bookkeeper_Data_Set.csv";
		ManageDataSet manageDataSet = new ManageDataSet();
		manageDataSet.setDataset(dataset);
		manageDataSet.retrieveVersionLineInfo();
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

}
