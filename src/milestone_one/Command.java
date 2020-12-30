package milestone_one;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Command {
	
	private  String ticket;
	private static final Logger log = Logger.getLogger(Command.class.getName());
	private static Properties prop = ManageProperties.getInstance();
	private static String excpetion = "Object is null";
	private static String gitC = "git -C";
	private static String ioException = "IOException in command";
	private String interruptedException = "InterruptedException in Command.";
	private static String pathDir = "..\\..\\" + prop.getProperty("PROJECT").toLowerCase() +"\\";
	private static String repoUrl = prop.getProperty("REPO_APACHE_PREFIX")+prop.getProperty("PROJECT").toLowerCase()+".git";

	public Command(String keyValue) {
		setTicket(keyValue);
	}
	
	public Command() {
		
	}
	
	private static void exception (Object o) {
		if (o == null) {
			throw new IllegalStateException(excpetion);
		}
	}
	
	//Update repository
	public void gitPull() {

		String command = gitC+" "+pathDir+" pull "+ repoUrl+"";
		Process p = null;
		try {
			//execute Command
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException e) {
			log.info(ioException );
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,interruptedException, e);
			Thread.currentThread().interrupt();
		}
		
		exception(p);
		
		if (p.exitValue() == 0) {
			log.info(prop.getProperty("UpdateComplete"));
		}else {
			log.info("Update repository not succesfully.");
		}
	}
	
	//Clone the repository in the specified directory
	public void gitClone() {
		
		String command = "git clone "+repoUrl+" "+pathDir;
		Process p = null;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
			log.info(prop.getProperty("infoGitCLone"));
			p.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,interruptedException, e);
			Thread.currentThread().interrupt();
		}
		exception(p);
		if (p.exitValue() != 0) {
			log.info(prop.getProperty("dirAlredyExist"));
			gitPull();
		}else {
			log.info(prop.getProperty("CloneComplete"));
		}
 	}
	
	//return the date of the first and last commit of the project
	public List<String> lifeProject() throws IOException {	
		//Command log commit prop.getProperty(directoryM2) "..\\..\\bookkeeper\\"
		
		String commandEndDate = gitC+" "+ pathDir +" log --pretty=format:\"%cd\" --date=iso-strict --max-count=1";
		String commandBeginDate = gitC+" "+ pathDir +" rev-list --reverse --max-parents=0 HEAD --pretty=format:\"%cd\" --date=iso-strict";
		
				
		Process pBegin = null;
		Process pEnd = null;
		List<String> date = new ArrayList<>();
		String dateBegin = null;
		String dateEnd = null;
		String line;
		try {
			//execute command
			pBegin = Runtime.getRuntime().exec(commandBeginDate);
			pEnd = Runtime.getRuntime().exec(commandEndDate);
			pBegin.waitFor();
			pEnd.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.info(interruptedException);
			Thread.currentThread().interrupt();
		}
		exception(pEnd);
		exception(pBegin);
		BufferedReader inputBegin = new BufferedReader(new InputStreamReader(pBegin.getInputStream()));
		BufferedReader inputEnd = new BufferedReader(new InputStreamReader(pEnd.getInputStream()));
		try {
			int count = 0;
			while ((line = inputBegin.readLine())!= null) {
				//extract date in form 'yyyy-mm'
				if (count==1) {
					dateBegin = line.substring(0,10);
					break;
				}
				count++;				
			}
			inputBegin.close();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		try {
			while ((line = inputEnd.readLine()) != null) {
				//extract date in form 'yyyy-mm'
				dateEnd = line.substring(0,10);				
			}
			inputEnd.close();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		date.add(dateBegin);
		date.add(dateEnd);
		return date;
	}
	
	//get date of the last commit
	public static String log(String ticket) {	
		//Command log commit
		String command = gitC+" "+ pathDir +" log --pretty=format:\"%cd\" "
				+ "--grep=" + ticket +" --date=iso-strict  --max-count=1";
		
		
		Process p = null;
		String date = null;
		String line;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,ioException, e);
			Thread.currentThread().interrupt();
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				//extract date in form 'yyyy-mm'
				date = line.substring(0,10);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		return date;
	}
	
	
	//retrieves all files from the project
	public static List<String> getAllFiles() {
		String command = gitC+" "+ pathDir +" --no-pager log --pretty=format:\"\" --name-only *.java";
		
		List<String> files = new ArrayList<>();
		String line = null;
		String prevLine = null;
		Process p = null;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null || prevLine != null ) {
				if (line != null) {
					files.add(line);
				}
				prevLine = line;
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		return new ArrayList<>(new LinkedHashSet<>(files));//remove the duplicated lines
	}
	
	public static List<BufferedReader> getAddRemoveDate(String filename) {
		String commandAdd = gitC+" "+ pathDir +" --no-pager log --diff-filter=A --pretty=format:\"%cd\" --date=iso-strict -- "+filename;
		String commanRemove = gitC+" "+ pathDir +" --no-pager log --diff-filter=D --pretty=format:\"%cd\" --date=iso-strict -- "+filename;
		
		Process pAdd = null;
		Process pRemove = null;
		try {
			//execute command
			pAdd = Runtime.getRuntime().exec(commandAdd);
			pRemove = Runtime.getRuntime().exec(commanRemove);
			pAdd.waitFor();
			pRemove.waitFor();
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE,ioException, e);
			Thread.currentThread().interrupt();
		}
		exception(pAdd);
		exception(pRemove);
		BufferedReader inputAdd = new BufferedReader(new InputStreamReader(pAdd.getInputStream()));
		BufferedReader inputRemove = new BufferedReader(new InputStreamReader(pRemove.getInputStream()));
		List<BufferedReader> list = new ArrayList<>();
		list.add(inputAdd);
		list.add(inputRemove);
		return list;
	}
	
	//get commit in a specified ticket
	public static List<String> getCommit(String nameTicket) {
		String command = gitC+" "+ pathDir +" --no-pager log --pretty=format:\"%H\" "
				+ "--grep=" + nameTicket +":";
		
		String line = null;
		List<String> commitList = new ArrayList<>();
		Process p = null;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				commitList.add(line);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		return commitList;
	}
	
	//retrieves all files changed in the specified commit
	public static List<String> getFiles(String commit) {
		String command = gitC+" "+ pathDir +" --no-pager diff-tree "
				+ "--no-commit-id --name-only -r " + commit+" *.java";
		
		String line = null;
		List<String> filesList = new ArrayList<>();
		Process p = null;
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				filesList.add(line);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		return filesList;
	}
	
	/*retrieves all commits in the project*/
	public static List<List<String>> getAllCommit(){
		String command = gitC+" "+ pathDir +" --no-pager log --pretty=format:\"%cs,%H\" --reverse";
		
		String line = null;
		Process p = null;
		List<List<String>> list = new ArrayList<>();
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		try {
			while ((line = input.readLine()) != null) {
				String[] l = line.split(",");
				List<String> commit = new ArrayList<>();
				//add date commit
				commit.add(l[0]);
				//add commit id
				commit.add(l[1]);
				
				list.add(commit);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		
		return list;
	}
	
	
	//return list of file c
	public static List<List<String>> getCommitChange(String idCommitPrev, String idCommitNext) {
		String command = gitC+" "+ pathDir +" --no-pager diff --numstat "+ idCommitPrev+ " "+ idCommitNext+ " *.java";
		
		String line2 = null;
		Process p = null;
		List<List<String>> list = new ArrayList<>();
		try {
			//execute command
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		exception(p);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		try {
			while ((line2 = input.readLine()) != null) {
				String[] l = line2.split("\t");
				List<String> metric = new ArrayList<>();
				metric.add(l[0]);
				metric.add(l[1]);
				metric.add(l[2]);
				
				list.add(metric);
			}
			input.close();	
		} catch (IOException e) {
			log.log(Level.SEVERE,ioException, e);
		}
		
		return list;
	}
	
	public void setTicket(String ticket){
		this.ticket = ticket;
	}
	public String getTicket() {
		return this.ticket;
	}

	public static void main(String[] args) {
		//empty
	}

}
