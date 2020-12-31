package milestone_one;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ticket implements Comparable<Ticket>{

	private String id;
	private String name;
	private LocalDate dateLastCommit;
	private LocalDate date;
	private LocalDate resolutionDate;
	private List<Version> affectsVersions;
	private List<Version> fixVersions;
	private Version openingVersion;
	
	/*Classe che rappresenta un un Ticket del progetto considerato*/
	
	public Ticket() {
		this.fixVersions = new ArrayList<>();
		this.affectsVersions = new ArrayList<>();
	}

	public static void main(String[] args) {
		//empty
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the affectsVersions
	 */
	public List<Version> getAffectsVersions() {
		return affectsVersions;
	}

	/**
	 * @param affectsVersions the affectsVersions to set
	 */
	public void setAffectsVersions(List<Version> affectsVersions) {
		this.affectsVersions = affectsVersions;
	}

	/**
	 * @return the fixVersions
	 */
	public List<Version> getFixVersions() {
		return fixVersions;
	}

	/**
	 * @param fixVersions the fixVersions to set
	 */
	public void setFixVersions(List<Version> fixVersions) {
		this.fixVersions = fixVersions;
	}
	
	public void addFixVersion(Version version) {
		this.fixVersions.add(version);
	}
	
	public void addAffectVersion(Version version) {
		this.affectsVersions.add(version);
	}
	
	public void selectLatestFixVersion() {
		if (this.fixVersions.isEmpty())
			return;
		Collections.sort(this.fixVersions);
		this.fixVersions.subList(1, this.fixVersions.size()).clear();
	}
	
	public void selectLatestAffectVersion() {
		if (this.affectsVersions.isEmpty())
			return;
		Collections.sort(this.affectsVersions);
		this.affectsVersions.subList(1, this.affectsVersions.size()).clear();
	}
	

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", name=" + name + ", date=" + date + ", affectsVersions=" + affectsVersions
				+ ", fixVersions=" + fixVersions + ", openingVersion=" + openingVersion + "]";
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void retrieveOpeningVersion(List<Version> versions) {
		for (Version version : versions) {
			if (this.date.isBefore(version.getDate())) {
				this.setOpeningVersion(version.copyVersion());
				break;
			}
		}
	}

	/**
	 * @return the openingVersion
	 */
	public Version getOpeningVersion() {
		return openingVersion;
	}

	/**
	 * @param openingVersion the openingVersion to set
	 */
	public void setOpeningVersion(Version openingVersion) {
		this.openingVersion = openingVersion;
	}

	/**
	 * @return the resolutionDate
	 */
	public LocalDate getResolutionDate() {
		return resolutionDate;
	}

	/**
	 * @param resolutionDate the resolutionDate to set
	 */
	public void setResolutionDate(LocalDate resolutionDate) {
		this.resolutionDate = resolutionDate;
	}

	/**
	 * @return the dateLastCommitString
	 */
	public LocalDate getDateLastCommit() {
		return dateLastCommit;
	}

	/**
	 * @param dateLastCommitString the dateLastCommitString to set
	 */
	public void setDateLastCommit(LocalDate dateLastCommit) {
		this.dateLastCommit = dateLastCommit;
	}

	@Override
	public int compareTo(Ticket obj) {
		if (this.date.isBefore(obj.getDate())) {
			return -1;
		}else if (this.date.isAfter(obj.getDate())) {
			return 1;
		}else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Ticket other = (Ticket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	

}
