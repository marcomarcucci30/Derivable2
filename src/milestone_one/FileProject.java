package milestone_one;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*Classe che rappresenta un file .java del progetto considerato*/

public class FileProject {

	private String name;
	private LocalDate addDate = null;
	private LocalDate removeDate = null;
	private int indexBegin = 0;
	private int indexEnd = 0;
	
	//indicate buggy versions of the file
	private List<Integer> affectsVersionsIndex = new ArrayList<>();
	
	private List<Metrics> metrics = new ArrayList<>();
	
	
	public FileProject(String name, LocalDate adDate, LocalDate removeDate) {
	}
	public FileProject(String name) {
		setName(name);
	}
	
	
	public void updateAffectsVersionIndex(Integer iv, Integer fv) {
		for (int i=iv; i<fv; i++) {
			if (!getAffectsVersionsIndex().contains(i)) {
				getAffectsVersionsIndex().add(i);
			}
		}
		Collections.sort(getAffectsVersionsIndex());
	}

	public static void main(String[] args) {
		//empty
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the addDate
	 */
	public LocalDate getAddDate() {
		return addDate;
	}

	/**
	 * @param addDate the addDate to set
	 */
	public void setAddDate(LocalDate addDate) {
		this.addDate = addDate;
	}

	/**
	 * @return the removeDate
	 */
	public LocalDate getRemoveDate() {
		return removeDate;
	}

	/**
	 * @param removeDate the removeDate to set
	 */
	public void setRemoveDate(LocalDate removeDate) {
		this.removeDate = removeDate;
	}
	@Override
	public String toString() {
		return "FileProject [name=" + name + ", addDate=" + addDate + ", removeDate=" + removeDate + ", indexBegin="
				+ indexBegin + ", indexEnd=" + indexEnd + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		FileProject other = (FileProject) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	/**
	 * @return the indexBegin
	 */
	public int getIndexBegin() {
		return indexBegin;
	}
	/**
	 * @param indexBegin the indexBegin to set
	 */
	public void setIndexBegin(int indexBegin) {
		this.indexBegin = indexBegin;
	}
	/**
	 * @return the indexEnd
	 */
	public int getIndexEnd() {
		return indexEnd;
	}
	/**
	 * @param indexEnd the indexEnd to set
	 */
	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}
	/**
	 * @return the metrics
	 */
	public List<Metrics> getMetrics() {
		return metrics;
	}
	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(List<Metrics> metrics) {
		this.metrics = metrics;
	}
	/**
	 * @return the affectsVersionsIndex
	 */
	public List<Integer> getAffectsVersionsIndex() {
		return affectsVersionsIndex;
	}
	/**
	 * @param affectsVersionsIndex the affectsVersionsIndex to set
	 */
	public void setAffectsVersionsIndex(List<Integer> affectsVersionsIndex) {
		this.affectsVersionsIndex = affectsVersionsIndex;
	}
	
	
	
	

}
