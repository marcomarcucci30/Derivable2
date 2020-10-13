package milestone_one;

public class Metrics {
	
	private int idVersion;
	private String fileName;
	
	private int lineAdded = 0;
	private int lineDeleted = 0;
	private int churn = 0;
	private int chgSetSize = 0;
	
	private int maxLOC = 0;
	private float avgLOC = 0;
	private int maxChurn = 0;
	private float avgChurn = 0;
	
	private int maxChgSetSize = 0;
	private float avgChgSetSize = 0;
	
	private float count = 0;
	

	public Metrics(int idVersion) {
		setIdVersion(idVersion);
	}
	
	
	public void update(int lineAdded, int lineDeleted, int nFilesCommittedTogether) {
		float localCount = getCount();
		setCount(localCount + 1);
		localCount = getCount();
		
		//update LOC
		setLineAdded(getLineAdded()+lineAdded);
		if (getMaxLOC() < lineAdded) {
			setMaxLOC(lineAdded);
		}
		float localAvgLOC = getAvgLOC();
		float newAvgLOC = localAvgLOC * ((localCount-1)/localCount) + lineAdded * (1/localCount);
		setAvgLOC(newAvgLOC);
		
		//update Churn
		setChurn(getChurn() + (lineAdded-lineDeleted));
		if (getMaxChurn() < (lineAdded-lineDeleted)) {
			setMaxChurn(lineAdded-lineDeleted);
		}
		float localAavgChurn = getAvgChurn();
		float newAvgChurn = localAavgChurn * ((localCount-1)/localCount) + (lineAdded-lineDeleted) * (1/localCount);
		setAvgChurn(newAvgChurn);
		
		//update ChgSetSize
		setChgSetSize(getChgSetSize()+ (nFilesCommittedTogether - 1));
		if (getMaxChgSetSize() < (nFilesCommittedTogether - 1)) {
			setMaxChgSetSize((nFilesCommittedTogether - 1));
		}
		float localAvgChgSetSize = getAvgChgSetSize();
		float newAvgChgSetSize = localAvgChgSetSize * ((localCount-1)/localCount) + (nFilesCommittedTogether-1) * (1/localCount);
		setAvgChgSetSize(newAvgChgSetSize);
		
		
	}

	public static void main(String[] args) {
		// Nothing to do

	}

	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idVersion;
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
		Metrics other = (Metrics) obj;
		return idVersion == other.idVersion;
	}


	@Override
	public String toString() {
		return "Metrics [idVersion=" + idVersion + ", fileName=" + fileName + ", lineAdded=" + lineAdded
				+ ", lineDeleted=" + lineDeleted + ", churn=" + churn + ", chgSetSize=" + chgSetSize + ", maxLOC="
				+ maxLOC + ", avgLOC=" + avgLOC + ", maxChurn=" + maxChurn + ", avgChurn=" + avgChurn
				+ ", maxChgSetSize=" + maxChgSetSize + ", avgChgSetSize=" + avgChgSetSize + ", count=" + count + "]";
	}
	
	

	/**
	 * @return the idVersion
	 */
	public int getIdVersion() {
		return idVersion;
	}


	/**
	 * @param idVersion the idVersion to set
	 */
	public void setIdVersion(int idVersion) {
		this.idVersion = idVersion;
	}


	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}


	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * @return the lineAdded
	 */
	public int getLineAdded() {
		return lineAdded;
	}


	/**
	 * @param lineAdded the lineAdded to set
	 */
	public void setLineAdded(int lineAdded) {
		this.lineAdded = lineAdded;
	}


	/**
	 * @return the lineDeleted
	 */
	public int getLineDeleted() {
		return lineDeleted;
	}


	/**
	 * @param lineDeleted the lineDeleted to set
	 */
	public void setLineDeleted(int lineDeleted) {
		this.lineDeleted = lineDeleted;
	}


	/**
	 * @return the churn
	 */
	public int getChurn() {
		return churn;
	}


	/**
	 * @param churn the churn to set
	 */
	public void setChurn(int churn) {
		this.churn = churn;
	}


	/**
	 * @return the chgSetSize
	 */
	public int getChgSetSize() {
		return chgSetSize;
	}


	/**
	 * @param chgSetSize the chgSetSize to set
	 */
	public void setChgSetSize(int chgSetSize) {
		this.chgSetSize = chgSetSize;
	}


	/**
	 * @return the maxLOC
	 */
	public int getMaxLOC() {
		return maxLOC;
	}


	/**
	 * @param maxLOC the maxLOC to set
	 */
	public void setMaxLOC(int maxLOC) {
		this.maxLOC = maxLOC;
	}


	/**
	 * @return the avgLOC
	 */
	public float getAvgLOC() {
		return avgLOC;
	}


	/**
	 * @param avgLOC the avgLOC to set
	 */
	public void setAvgLOC(float avgLOC) {
		this.avgLOC = avgLOC;
	}


	/**
	 * @return the maxChurn
	 */
	public int getMaxChurn() {
		return maxChurn;
	}


	/**
	 * @param maxChurn the maxChurn to set
	 */
	public void setMaxChurn(int maxChurn) {
		this.maxChurn = maxChurn;
	}


	/**
	 * @return the avgChurn
	 */
	public float getAvgChurn() {
		return avgChurn;
	}


	/**
	 * @param avgChurn the avgChurn to set
	 */
	public void setAvgChurn(float avgChurn) {
		this.avgChurn = avgChurn;
	}


	/**
	 * @return the maxChgSetSize
	 */
	public int getMaxChgSetSize() {
		return maxChgSetSize;
	}


	/**
	 * @param maxChgSetSize the maxChgSetSize to set
	 */
	public void setMaxChgSetSize(int maxChgSetSize) {
		this.maxChgSetSize = maxChgSetSize;
	}


	/**
	 * @return the avgChgSetSize
	 */
	public float getAvgChgSetSize() {
		return avgChgSetSize;
	}


	/**
	 * @param avgChgSetSize the avgChgSetSize to set
	 */
	public void setAvgChgSetSize(float avgChgSetSize) {
		this.avgChgSetSize = avgChgSetSize;
	}


	/**
	 * @return the count
	 */
	public float getCount() {
		return count;
	}


	/**
	 * @param count the count to set
	 */
	public void setCount(float count) {
		this.count = count;
	}


}
