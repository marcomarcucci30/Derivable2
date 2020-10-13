package milestone_one;

import java.time.LocalDate;
import java.util.List;

public class Proportion {
	
	private static Integer prop = 0;
	private static Integer numTicket = 0;
	private static Proportion proportion;
	private static List<Version> versions;
	

	public Proportion() {
		// empty
	}
	//implemets singleton for Proportion
	public static Proportion getInstance() {
		if (proportion == null) {
			proportion = new Proportion();
		}
		return proportion;
	}
	
	private static Version iterateOnVersions(LocalDate date) {
		for (Version version : versions) {
			//stop when found fixVersion
			if (date.isBefore(version.getDate())  || date.isEqual(version.getDate())) {
				return version.copyVersion();
			}
		}
		return null;
	}
	
	private static void retrieveVersionIndex(Ticket ticket, int cases) {
		
		
		switch (cases) {
		//no fixVersion, yes commit
		case 0:
			Version version0 = iterateOnVersions(ticket.getDateLastCommit());
			ticket.getFixVersions().add(version0);
			break;
			
		//no FixVersion, no commit
		case 1:
			Version version1 = iterateOnVersions(ticket.getResolutionDate());
			ticket.getFixVersions().add(version1);
			break;
		
		//yes fixVersion
		case 2:
			Version version2 = iterateOnVersions(ticket.getFixVersions().get(0).getDate());
			if (version2 != null) {
				Integer index2 = version2.getIndex();
				ticket.getFixVersions().get(0).setIndex(index2);
			}
			break;
			
		//yes affecstVersion
		case 3:
			Version version3 = iterateOnVersions(ticket.getAffectsVersions().get(0).getDate());
			if(version3!= null) {
				Integer index3 = version3.getIndex();
				ticket.getAffectsVersions().get(0).setIndex(index3);
			}
			break;
			
		default:
			break;
		}
		
		
	}
	
	/*0, if file in ticket non buggy, -1 if ticket don't consider, 1 if the file in the ticket are buggy*/ 
	public static int updateProportion(Ticket ticket) {
		//if the ticket does not have a fixed version, the latter is retrieved 
		if (ticket.getFixVersions().isEmpty()) {
			//retrieve date of last
			String dateLastCommitString = Command.log(ticket.getName()+":");
			//if there isn't commit for the ticket, discard the ticket
			if (dateLastCommitString != null) {
				LocalDate dateLastCommit = LocalDate.parse(dateLastCommitString);
				ticket.setDateLastCommit(dateLastCommit);
				retrieveVersionIndex(ticket, 0);
			}else {
				retrieveVersionIndex(ticket, 1);
			}
		}else {		
			retrieveVersionIndex(ticket, 2);
		}
		
		
		//retrieve OV
		ticket.retrieveOpeningVersion(versions);
		double numTicketSupport = numTicket;
		double propSupport = prop;
		
		//if tickets does not have affects version, the latter is calculated by proportion
		if (ticket.getAffectsVersions().isEmpty()) {
			double fv = ticket.getFixVersions().get(0).getIndex();
			double ov = ticket.getOpeningVersion().getIndex();
			if (fv<ov) {
				return -1;
			}
			//using proportion
			Integer indexIV = (int) Math.round((fv-(fv-ov)*propSupport));
			int versionToAddIndex = versions.indexOf(new Version(indexIV));
			if (versionToAddIndex < 0)
				versionToAddIndex = 0;
			
			ticket.getAffectsVersions().add(versions.get(versionToAddIndex).copyVersion());

			if (indexIV == fv) {
				//Nessun file nel ticket sarà buggy
				return 1;
			}			
		}else {
			retrieveVersionIndex(ticket, 3);
			double fv = ticket.getFixVersions().get(0).getIndex();
			double iv = ticket.getAffectsVersions().get(0).getIndex();
			double ov = ticket.getOpeningVersion().getIndex();
			//update value of proportion
			
			Integer resultInteger = check(fv, ov, iv);
			if (resultInteger != null)
				return resultInteger;
			Integer proportionSupport = (int) Math.round( (fv-iv)/(fv-ov) );
			prop = (int) Math.round(propSupport*(numTicketSupport/(numTicketSupport+1)) + (double)proportionSupport*(1/(numTicketSupport+1)));
			numTicket++;
		}
		return 0;
		
	}
	
	private static Integer check(double fv, double ov, double iv) {
		if (fv<iv) {
			return -1;
		}
		if (fv<ov) {
			return 0;
		}	
		if (fv==iv) {
			//nessun file nel ticket è buggy
			return 1;
		}
		if (fv==ov) {
			//non usare cìnel proportion ma vedere quali file suno buggy
			return 0;
		}
		if (ov<iv) {
			return 0;
		}
		return null;
	}
	
	
	public static void main(String[] args){
		//empty
	}
	/**
	 * @return the prop
	 */
	public static Integer getProp() {
		return prop;
	}
	/**
	 * @param prop the prop to set
	 */
	public static void setProp(Integer prop) {
		Proportion.prop = prop;
	}
	/**
	 * @return the versions
	 */
	public static List<Version> getVersions() {
		return versions;
	}
	/**
	 * @param versions the versions to set
	 */
	public static void setVersions(List<Version> versions) {
		Proportion.versions = versions;
	}

}
