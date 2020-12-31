package milestone_one;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageFile {
	
	private static final Logger log = Logger.getLogger(ManageFile.class.getName());
	private static String fileNotFoundException = "FileNotFoundException in ManageFile.";
	private static String iOException = "IOException in ManageFile.";
	private static Properties prop = ManageProperties.getInstance();
	private static String project = "PROJECT";
	
	

	public ManageFile() {
		//empty
	}
	
	/*Function tu use test list created */
	public static List<FileProject> retrieveFiles() throws IOException {
		log.info("Retrieve all files..");
		String filesString = "Test"+prop.getProperty(project);
		File file = new File(filesString);
	    FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,fileNotFoundException, e);
		}
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    String line = null;
	    List<FileProject> filesList = new ArrayList<>();
	    try {
			while ((line=bufferedReader.readLine()) != null) {
				String[] fileSplit = line.split(",");
				FileProject fileProject = new FileProject(fileSplit[0]);
				if (!fileSplit[1].equals("null")) {
					fileProject.setAddDate(LocalDate.parse(fileSplit[1]));
				}
				if (!fileSplit[2].equals("null")) {
					fileProject.setRemoveDate(LocalDate.parse(fileSplit[2]));
				}
				
				filesList.add(fileProject);
				
			}
		} catch (java.io.IOException e) {
			log.log(Level.SEVERE, iOException , e);
		}
	    finally {
	    	bufferedReader.close();
		}
	    
	    return filesList;
		
	}
	
	/*Create file test to speed up the program*/
	public static void createFileToTest(List<FileProject> filesList) {
		log.info("Create file to test...");
		PrintWriter writer = null;
		try {
			//create file
			writer = new PrintWriter(new File("Test"+prop.getProperty(project)));
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,"FileNotFoundException", e);
		}
		StringBuilder sb = new StringBuilder();
		
		for (FileProject fileProject : filesList) {
			sb.append(fileProject.getName());
			sb.append(",");
			sb.append(fileProject.getAddDate());
			sb.append(",");
			sb.append(fileProject.getRemoveDate());
			sb.append("\n");
		}
		
		if (writer == null) {
			throw new IllegalStateException("Object is null.");
		}
		//Write CSV file
		writer.write(sb.toString());
	    writer.close();
		
	}
	
	

	//Create a Version List from the specified csv file
	public static List<Version> readFile() throws IOException {
		List<Version> versions = new ArrayList<>();
		String filesString = prop.getProperty(project)+"VersionInfo.csv";
		File file = new File(filesString);
	    FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,fileNotFoundException, e);
		}
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    String line;
	    int count = 0;
	    try {
			while ((line = bufferedReader.readLine()) != null) {
				if (count!=0) {
					String[] atributesList = line.split(",");
					Version version = new Version();
					version.setIndex(Integer.parseInt(atributesList[0]));
					version.setId(atributesList[1]);
					version.setName(atributesList[2]);
					version.setDate(LocalDate.parse((atributesList[3].substring(0, 10))));
					versions.add(version);
				}
				count++;
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, iOException , e);
		}finally {
			bufferedReader.close();
		}
	   
	    return versions;
	}
	
	/*Retrieves all files (removed files too) in the project */
	public static List<FileProject> readFilesInProject() {
		
		log.info("Retrieve all files..");
		List<String> filesString = Command.getAllFiles();
		List<FileProject> filesList = new ArrayList<>();
		String line2 = null;
		try {
			for (String fileName : filesString) {
				if (fileName.isBlank()) {
					continue;
				}
				FileProject fileProject = new FileProject(fileName);
				List<BufferedReader> list = Command.getAddRemoveDate(fileName);
				//while lin etc..
				if((line2 = list.get(0).readLine())!=null) {
					fileProject.setAddDate(LocalDate.parse(line2.substring(0, 10)));
				}
				if((line2 = list.get(1).readLine())!=null) {
					fileProject.setRemoveDate(LocalDate.parse(line2.substring(0, 10)));
				}
				filesList.add(fileProject);
				list.get(0).close();
				list.get(1).close();
			}	
		} catch (IOException e) {
			log.log(Level.SEVERE,iOException, e);
		}
		return filesList;
	}
	
	public static void createCSVFile(List<FileProject> filesProjects, List<Version> versions) {
		
		log.info("Create Dataset...");
		
		StringBuilder sb = new StringBuilder();
		sb.append("Version");
		sb.append(",");
		sb.append("Filename");
		sb.append(",");
		sb.append("LOC_added");
		sb.append(",");
		sb.append("MAX_LOC_added");
		sb.append(",");
		sb.append("AVG_LOC_addded");
		sb.append(",");
		sb.append("Churn");
		sb.append(",");
		sb.append("MAX_Churn");
		sb.append(",");
		sb.append("AVG_Churn");
		sb.append(",");
		sb.append("ChgSetSize");
		sb.append(",");
		sb.append("MAX_ChgSetSize");
		sb.append(",");
		sb.append("AVG_ChgSetSize");
		sb.append(",");
		sb.append("Buggy");
		sb.append("\n");
		
		//iterar tutti i file per ogni versione possibile in versions
		for (int i= 0; i<versions.size(); i++) {
			
			//BOOKKEEPER e OPENJPA: se la data della versione è oltre metà del progetto non considerare la versione
			if ((versions.get(i).getDate().isAfter(LocalDate.parse("2014-02-03")) && prop.getProperty(project).equals("BOOKKEEPER"))
					|| (versions.get(i).getDate().isAfter(LocalDate.parse("2010-03-30")) && prop.getProperty(project).equals("OPENJPA"))) {
				continue;
			}
			
			
			
			//itero su tutti i file del progetto
			for (FileProject file : filesProjects) {
				validationFile(file, versions, i, sb);
			}
			
		}
		try (PrintWriter writer = new PrintWriter(new File(prop.getProperty(project)+"_Data_Set.csv"))){
			//create file
			//Write CSV file
			writer.write(sb.toString());
			
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,"FileNotFoundException", e);
		}
		
	}
	
	
	private static void validationFile(FileProject file, List<Version> versions, int i, StringBuilder sb) {
		if (file.getAddDate() == null) {
			return;
		}
		
		LocalDate nextVersionDate = null;
		LocalDate addFileDate = file.getAddDate();
		LocalDate removeFileDate = file.getRemoveDate();
		if (i<=(versions.size()-2)) {
			nextVersionDate = versions.get(i+1).getDate();
		}				
		LocalDate currentVersionDate = versions.get(i).getDate();
		
		//per prima cosa controllo che il file esisteva in quella versione
		//anche se viene eliminato nella stessa versione rimane sempre buggato
		if (i<=(versions.size()-2) && addFileDate.isBefore(nextVersionDate)  && (removeFileDate == null || removeFileDate.isAfter(currentVersionDate))) {
			//caso in cui il file  ESISTE, cioè la data di add del file è precedente alla versione successiva e la date di remove del file è successiva alla versione corrente
			if (file.getAffectsVersionsIndex().contains(versions.get(i).getIndex())) {
				//caso in cui il file era buggy in quella versione
				sb.append(versions.get(i).getIndex());
				sb.append(",");
				sb.append(file.getName());
				sb.append(",");
				appendMetrics(file, sb, versions.get(i).getIndex());
				sb.append("Yes");
				sb.append("\n");
			}else {
				//caso in cu file non buggy in questa det versione
				sb.append(versions.get(i).getIndex());
				sb.append(",");
				sb.append(file.getName());
				sb.append(",");
				appendMetrics(file, sb, versions.get(i).getIndex());
				sb.append("No");
				sb.append("\n");
			}
			
		}else if (i>(versions.size()-2)){
			validateFileInLastVersion(file, versions, i, sb, removeFileDate, currentVersionDate);
		}else {
			//file non esiste ancora
		}
	}
	
	private static void validateFileInLastVersion(FileProject file, List<Version> versions, int i, StringBuilder sb1, LocalDate removeFileDate, LocalDate currentVersionDate) {
		//caso in cui o siamo arrivati alla fine delle versions
		if (removeFileDate == null || removeFileDate.isAfter(currentVersionDate) || removeFileDate.isEqual(currentVersionDate)) {
			//devo controllare l'index
			if (file.getAffectsVersionsIndex().contains(versions.get(i).getIndex())) {
				//caso in cui il file era buggy in quella versione
				sb1.append(versions.get(i).getIndex());
				sb1.append(",");
				sb1.append(file.getName());
				sb1.append(",");
				appendMetrics(file, sb1, versions.get(i).getIndex());
				sb1.append("Yes");
				sb1.append("\n");
			}else {
				//caso in cu file non buggy in questa det versione
				sb1.append(versions.get(i).getIndex());
				sb1.append(",");
				sb1.append(file.getName());
				sb1.append(",");
				appendMetrics(file, sb1, versions.get(i).getIndex());
				sb1.append("No");
				sb1.append("\n");
			}
		}else {
			//file non esiste più 
		}
	}

	private static void appendMetrics(FileProject file, StringBuilder sb, int i) {		
		
		int metricsIndex = file.getMetrics().indexOf(new Metrics(i));

		//file in the version i never commited, so write 
		if (metricsIndex == -1) {
			for (int j = 0; j<9; j++) {
				sb.append(0);
				sb.append(",");
			}
			return;
		}
		Metrics metrics = file.getMetrics().get(metricsIndex);
		
		sb.append(metrics.getLineAdded());
		sb.append(",");
		sb.append(metrics.getMaxLOC());
		sb.append(",");
		sb.append(metrics.getAvgLOC());
		sb.append(",");
		sb.append(metrics.getChurn());
		sb.append(",");
		sb.append(metrics.getMaxChurn());
		sb.append(",");
		sb.append(metrics.getAvgChurn());
		sb.append(",");
		sb.append(metrics.getChgSetSize());
		sb.append(",");
		sb.append(metrics.getMaxChgSetSize());
		sb.append(",");
		sb.append(metrics.getAvgChgSetSize());
		sb.append(",");
		
	}
	
	

	public static void main(String[] args) {
		//empty
		List<FileProject> files = readFilesInProject();
		createFileToTest(files);
	}

	

}
