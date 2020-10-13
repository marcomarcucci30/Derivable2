package milestone_one;

import java.time.LocalDate;
import java.util.List;

public class Version implements Comparable<Version>{

	private Integer index;
	private String id;
	private String name;
	private LocalDate date;
	private List<Version> versions;//Version list of a specific csv file
	
	
	public Version() {
		// empty
	}

	public Version(String id, String name, LocalDate date) {
		setId(id);
		setName(name);
		setDate(date);
	}

	public Version(Integer indexIV) {
		setIndex(indexIV);
	}

	public Version(Integer index2, String id2, String name2, LocalDate date2, List<Version> versions2) {
		setIndex(index2);
		setId(id2);
		setName(name2);
		setDate(date2);
		setVersions(versions2);
	}

	public static void main(String[] args) {
		//empty
	}
	
	public Version copyVersion() {
		return new Version(getIndex(),getId(), getName(), getDate(), getVersions());
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
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

	@Override
	public String toString() {
		return "Version [index=" + index + ", id=" + id + ", name=" + name + ", date=" + date + "]";
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

	@Override
	public int compareTo(Version o) {
		if (this.date.isBefore(o.getDate())) {
			return -1;
		}else if (this.date.isAfter(o.getDate())) {
			return 1;
		}else {
			return 0;
		}
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		Version other = (Version) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index)) {
			return false;
		}
		return true;
	}

	

}
