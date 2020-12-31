package milestone_one;

import java.time.LocalDate;
import java.util.List;

public class ManageMetrics {
	
	private List<FileProject> files;
	private List<Version> versions;
	private static ManageMetrics manageMetrics = null;

	public ManageMetrics() {
		//empty
	}
	
	public static ManageMetrics getIstance() {
		if (manageMetrics == null) {
			manageMetrics = new ManageMetrics();
		}
		return manageMetrics;
	}
	
	//calculate LOC metric, Churn Metrics, ChgSetSize Metrics
	//each file has a list of metrics; one for each version of the file
	public void calculateMetrics() {
		boolean skipCommit = false;
		//<..,<dateCommit, idCommit>, ..>
		List<List<String>> listCommit = Command.getAllCommit();
		
		int versionCondidered = 1;
		for (int i=0; i<listCommit.size(); i++) {
			skipCommit = false;
			
			//check is the list is terminated
			if (i+1 == listCommit.size())
				return;
			
			String idCommitPrev = listCommit.get(i).get(1);
			LocalDate dateCommitPrev = LocalDate.parse(listCommit.get(i).get(0));
			
			String idCommitNext = listCommit.get(i+1).get(1);
			LocalDate dateCommitNext = LocalDate.parse(listCommit.get(i+1).get(0));
			
			
			int indexVersionCommitPrev = findCommitVersion(dateCommitPrev);
			int indexVersionCommitNext = findCommitVersion(dateCommitNext);
			//skip this commit
			if (indexVersionCommitPrev == 0) {
				skipCommit = true;
			}
			
			//check if the commit is the first commit in the version considered
			if (indexVersionCommitNext > versionCondidered) {
				skipCommit = true;
				//init a new version
				//update version considered
				versionCondidered = indexVersionCommitNext;			
				
			}
			
			if (skipCommit)
				//skip this commit for this version
				continue;
			
			//git query on two commit
			//<<add, delete, FileName>, ..>
			List<List<String>> listMetricFiles = Command.getCommitChange(idCommitPrev, idCommitNext);
			//iterate on all file found
			for (List<String> list : listMetricFiles) {
				//iterate on all files in project
				for (FileProject file : files) {
					updateMetrics(file, list, versionCondidered, listMetricFiles);
				}
			}			
		}
		
	}
	
	private void updateMetrics(FileProject file, List<String> list, int versionCondidered, List<List<String>> listMetricFiles) {
		if (file.equals(new FileProject(list.get(2)))) {
			//TOGLI QUA
			int indexMetrics = file.getMetrics().indexOf(new Metrics(versionCondidered));
			if (indexMetrics == -1  ) {
				//the metrics for the version considered doesn't exists
				//create the Metrics object and update metric
				Metrics metrics = new Metrics(versionCondidered);
				file.getMetrics().add(metrics);
				indexMetrics = file.getMetrics().indexOf(new Metrics(versionCondidered));
			}else {
				//the metrics for the version considered exist already, so update metric
			}
			
			//update the metrics in both cases
			file.getMetrics().get(indexMetrics).update(Integer.valueOf(list.get(0)), Integer.valueOf(list.get(1)), listMetricFiles.size());
		}else {
			//doesn't find files
		}
	}

	//find version of the specified commit
	private int findCommitVersion(LocalDate dateCommit) {
		
		//discard commit before first version
		if (dateCommit.isBefore(this.versions.get(0).getDate()))
			return 0;
		Version versionBeforeVersion = this.versions.get(0);
		for (Version version : this.versions) {
			//commit is in this version
			if (dateCommit.isBefore(version.getDate())) {
				return versionBeforeVersion.getIndex();
			}
			versionBeforeVersion = version;
		}
		return this.versions.get(this.versions.size()-1).getIndex();
		
	}
	

	

	public static void main(String[] args) {
		//empty
	}

	/**
	 * @return the files
	 */
	public List<FileProject> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<FileProject> files) {
		this.files = files;
	}

	/**
	 * @return the versions
	 */
	public List<Version> getVersions() {
		return versions;
	}

	/**
	 * @param versions the versions to set
	 */
	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

}
