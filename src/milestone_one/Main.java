package milestone_one;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.temporal.ChronoUnit;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class.getName());
	private static Properties prop = ManageProperties.getInstance();
	private static String fields = "fields";
	private static String releaseDate = "releaseDate";

	public Main() {
		//empty
	}
	public static JSONObject readJsonFromUrl(String url) throws IOException {
	      InputStream is = new URL(url).openStream();
	      try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
	         String jsonText = readAll(rd);
	         return new JSONObject(jsonText);
	       } finally {
	         is.close();
	       }
	   }
	
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException {
	      InputStream is = new URL(url).openStream();
	      try (BufferedReader rd = new BufferedReader(new InputStreamReader(is,  StandardCharsets.UTF_8))) {
	         String jsonText = readAll(rd);
	         return new JSONArray(jsonText);
	       } finally {
	         is.close();
	       }
	   }
	
	private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }
	
	//retrieves all fixVersions in the specified ticket
	private static void retrieveFixVersion(JSONArray issues, int i, Ticket ticket) {
		Integer fixVersionsLenght = issues.getJSONObject(i).getJSONObject(fields).getJSONArray("fixVersions").length();
    	for (int k = 0; k < fixVersionsLenght; k++) {
    		JSONObject fixVersionsJsonObject = issues.getJSONObject(i).getJSONObject(fields).getJSONArray("fixVersions").getJSONObject(k);
    		
    		//check resolution date
    		if (!fixVersionsJsonObject.has(releaseDate)) {
    			continue;
    		}
    		
    		Version fixVersion = new Version(fixVersionsJsonObject.get("id").toString(),
    				fixVersionsJsonObject.get("name").toString(), LocalDate.parse(fixVersionsJsonObject.get(releaseDate).toString()));    
    		ticket.addFixVersion(fixVersion);
		}
	}
	
	//retrieves all affectsVersions in the specified ticket
	private static void retrieveAffectsVersion(JSONArray issues, int i, Ticket ticket) {
		Integer affectsVersionsLenght = issues.getJSONObject(i).getJSONObject(fields).getJSONArray("versions").length();
		for (int k = 0; k < affectsVersionsLenght; k++) {
			JSONObject affectsVersionsJsonObject = issues.getJSONObject(i).getJSONObject(fields).getJSONArray("versions").getJSONObject(k);
			
			//check resolution date
			if (!affectsVersionsJsonObject.has(releaseDate)) {
				continue;
			}
			
			Version affectVersion = new Version(affectsVersionsJsonObject.get("id").toString(),
					affectsVersionsJsonObject.get("name").toString(), LocalDate.parse(affectsVersionsJsonObject.get(releaseDate).toString()));
			ticket.addAffectVersion(affectVersion);
		}
	}
	
	private static List<Ticket> retrieveTickets(LocalDate dateBegin, LocalDate median) throws IOException{
		
		List<Ticket> ticketsList = new ArrayList<>();
		Integer j = 0;
		   Integer i = 0;
		   Integer stop = 0;
		   Integer total = 1;
	      //Get JSON API for closed bugs w/ AV in the project
		   do {
			   
	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	         j = i + 1000;
	         String projName = ManageProperties.getInstance().getProperty("PROJECT");
			String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,fixVersions,created&startAt="
	                + i.toString() + "&maxResults=" + j.toString();
			
			JSONObject json = readJsonFromUrl(url);
	         JSONArray issues = json.getJSONArray("issues");
	         total = json.getInt("total");
	         //Create ticket
	         for (i = 0; i < issues.length() ; i++) {
	        	       	 
	        	 Ticket ticket = new Ticket();
	        	 String idTicket;
	        	 String nameTicket;
	        	 String resolutionDate;
	        	 LocalDate dateTicket;
	        	 
	        	
	        	dateTicket = LocalDate.parse(issues.getJSONObject(i).getJSONObject(fields).get("created").toString().substring(0, 10));
	        	//consider only the first half of the project
	        	if (dateTicket.isAfter(median) || dateTicket.isBefore(dateBegin)) {
	        		continue;
	        	}
		        ticket.setDate(dateTicket);
		        
	            //Iterate through each bug
	        	if (issues.getJSONObject(i).has("id")){
		        	idTicket = issues.getJSONObject(i).get("id").toString();
		        	ticket.setId(idTicket);
		        }
	        	if (issues.getJSONObject(i).has("key")){
	        		nameTicket = issues.getJSONObject(i).get("key").toString();
	        		ticket.setName(nameTicket);
	        	}
	        	
	        	
	        	
	        	resolutionDate = issues.getJSONObject(i).getJSONObject(fields).getString("resolutiondate").substring(0, 10);
	        	ticket.setResolutionDate(LocalDate.parse(resolutionDate));
	        	
	        	//retrieves all fixVersions in the specified ticket
	        	retrieveFixVersion(issues, i, ticket);
	        	
	        	
	        	//retrieves all affectsVersions in the specified ticket
	        	retrieveAffectsVersion(issues, i, ticket);
	        	
	        	
	        	ticket.selectLatestFixVersion();
	        	ticket.selectLatestAffectVersion();
	        	ticketsList.add(ticket);
	         }  
	         stop = stop + i;
	      } while (stop < total);
		   
		   return ticketsList;
	}
	

	private static void buggy(List<FileProject> projectList, Ticket ticket, List<Version> versionList) {
		String nameTicket = ticket.getName();
		List<String> commitList = Command.getCommit(nameTicket);
		for (String commit : commitList) {
			//retrieve all file of a specified commit
			List<String> filesList = Command.getFiles(commit);
			//itero su tutti i file del commit
			for (String fileCommit : filesList) {
				//itero su tutti i file del progetto
				for (FileProject fileProject : projectList) {
					if (fileProject.getName().equals(fileCommit)) {
						checkBuggyVersion(ticket, fileProject, versionList);
						break;
					}
				}
			}
		}
		
	}
	
	
	
	/*CONTROLLO SULLA DATA DI CREAZIONE DEL FILE
	 * 
	 * Controllo se il file è stato creato prima dell'IV, tra la IV e la FV oppure dopo la FV*/ 
	private static void checkBuggyVersion(Ticket ticket, FileProject fileProject, List<Version> versionList) {
		
		LocalDate addDate = fileProject.getAddDate();
		LocalDate removeDate = fileProject.getRemoveDate();
		LocalDate fixVersion = ticket.getFixVersions().get(0).getDate();
		LocalDate affectsVersion = ticket.getAffectsVersions().get(0).getDate();
		
		if (removeDate!= null && addDate.isAfter(removeDate)) {
			return;
		}
		
		
		//caso in cui la data del file è precedente all'IV del ticket
		if (addDate.isBefore(affectsVersion) || addDate.isEqual(affectsVersion)) {
			
			//check 
			eliminationDate(versionList, removeDate, fixVersion, affectsVersion, fileProject, ticket);
			
		}else if (addDate.isBefore(fixVersion)) {
			//caso in cui la data di add è precedenet alla fix ma successiva all'affects versions
			for (Version version : versionList) {
				if (addDate.isBefore(version.getDate())) {
					
					fileProject.updateAffectsVersionIndex(version.getIndex()-1, ticket.getFixVersions().get(0).getIndex());
					//Qui so che la data di add sta tra Iv e Fv ma come faccio a spere con precisione che la data di remove
					//sta dopo o uguale alla fix version??????
					
					break;
				}
			}
			
		}else {
			//file aggiunto dopo  la chiusura del ticket --> impossibile
			//oppure file aggiunto nella fix version e quindi non buggy
		}		
	}
	
	
	/*CONTROLLO SULLA DATA DI ELIMINAZIONE DEL FILE
	 * 
	 * */
	private static void eliminationDate(List<Version> versionList, LocalDate removeDate, LocalDate fixVersion, LocalDate affectsVersion, FileProject fileProject, Ticket ticket) {
		if (removeDate == null || removeDate.isAfter(fixVersion) || removeDate.isEqual(fixVersion)) {
			//se la data di remove non esiste o è dopo fix version considero tutte le affects version per il file.
			
			fileProject.updateAffectsVersionIndex(ticket.getAffectsVersions().get(0).getIndex(), ticket.getFixVersions().get(0).getIndex());
			
		}else if (removeDate.isAfter(affectsVersion)){
			//devo capire in quale versione è stato eliminato
			for (Version version : versionList) {
				//eliminato nella versione precedente a quella di version
				if (removeDate.isBefore(version.getDate())) {
					
					//eliminazione avvenuta dopo la chiusura del ticket
					if (version.getDate().isAfter(fixVersion) || version.getDate().isEqual(fixVersion)) {
						fileProject.updateAffectsVersionIndex(ticket.getAffectsVersions().get(0).getIndex(), ticket.getFixVersions().get(0).getIndex());
					}else {
						//eliminazione avvenuta prima della chiusura del ticket
						fileProject.updateAffectsVersionIndex(ticket.getAffectsVersions().get(0).getIndex(), version.getIndex() -1);
					}
					break;
				}
			}
		}else {
			//data di eliminazione precedente a affectsVersion--> impossibile
		}
	}
	
	
	
	public static void main(String[] args) throws IOException{
		String msg = null;
		
		long start = System.currentTimeMillis();
		
		//git clone/pull
		Command cmd = new Command();
		cmd.gitClone();
		
		//retrievs all file of the project
		//Se si vogliono aggiornare i file dal progetto settare updatesFiles = 1
		if (Integer.parseInt(prop.getProperty("updateFiles")) == 1) {
			ManageFile.main(null);
		}
		List<FileProject> filesProjects = ManageFile.retrieveFiles();
		
		
		List<String> lifeProject = cmd.lifeProject();
		LocalDate dateBegin = LocalDate.parse(lifeProject.get(0));
		LocalDate dateEnd = LocalDate.parse(lifeProject.get(1));
		long numDays = ChronoUnit.DAYS.between(dateBegin, dateEnd);
		//middle day of project
		LocalDate median = dateBegin.plusDays(numDays/2L);
		
		//create file csv
		log.info("Create File CSV which contains all versions..");
		GetReleaseInfo.main(null);
		
		//Create versions list of the the project form csv file
		List<Version> versions = ManageFile.readFile();
		Proportion.setVersions(versions);
		//get tickets of project froma jira
		List<Ticket> ticketsList = retrieveTickets(dateBegin,median);
		//ordino i ticket
		if (ticketsList.isEmpty())
			return;
		Collections.sort(ticketsList);
		
		
		int count = 0;
		for (Ticket ticket : ticketsList) {
			if (!ticket.getAffectsVersions().isEmpty()) {
				count++;
			}
			
		}
		msg = "Ticket with affect version ="+count+"\n";
		log.log(Level.INFO, msg);
		
		log.info("Calculate Proportion e Buggy...");
		
		
		//Proportion and buggy
		count=0;
		int countGood = 0;
		for (Ticket ticket : ticketsList) {
			//update proportion
			int err = Proportion.updateProportion(ticket);
			if (err == -1 || err==1) {
				count++;
				//non considero questo ticket per il calcolo delle classi buggy
				continue;
			}
			countGood++;
			
			//buggy file
			if (ticket.getAffectsVersions().get(0).getIndex() < ticket.getFixVersions().get(0).getIndex()) {
				buggy(filesProjects, ticket, versions);
			}
			
		}
		msg = "Ticket NON condiderati nel calcolo d Prop: "+count+", Ticket considerati in Prop: "+countGood;
		log.log(Level.INFO, msg);
		//calculate all metrics
		log.info("Calculate Metrics...");
		ManageMetrics manageMetrics = ManageMetrics.getIstance();
		manageMetrics.setFiles(filesProjects);
		manageMetrics.setVersions(versions);
		manageMetrics.calculateMetrics();
		
	
		
		ManageFile.createCSVFile(filesProjects, versions);
		
		log.info("Done!");
		
		long end = System.currentTimeMillis();
		String time = String.valueOf(end -start);
		log.info(time);
		
		
	}
	


}
