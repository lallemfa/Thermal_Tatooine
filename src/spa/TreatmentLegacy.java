package spa;

public class TreatmentLegacy {
	private String name;
	
	private int startHour;
	private int endHour;
	
	private boolean isFixedHours;
	
	private boolean isOrganizedWaiting;
	private int maxWaitingPatients;
	// Manager manager
	
	// Constructors
	public TreatmentLegacy(String givenName, int start, int end, boolean fixedHours, boolean organizedWaiting, int maxWaiting)
	{
		name = givenName;
		
		startHour 	= start;
		endHour 	= end;
		
		isFixedHours = fixedHours;
		
		isOrganizedWaiting = organizedWaiting;
		maxWaitingPatients = maxWaiting;
	}
	
	// Print
	@Override
	public String toString()
	{
		String msg = "\t" + name + "\n" +
					"Opening hours -> " + startHour + " to " + endHour + "\n" +
					"Fixed Hours ?\t" + isFixedHours + "\n" +
					"Organized Waiting ?\t" + isOrganizedWaiting + "\n" +
					"Size of queue : " + maxWaitingPatients + "\n";
		
		return msg;
	}
}