package patients;


public class Patient {
	
	public int id;
	
	private boolean isFair;
	private Cure[] curesToDo;
	
	private int currentCure;
	
	// Constructors
	public Patient(int givenID)
	{
		// Patients are believed to be honest if not defined.
		this(givenID, true);
	}
	
	public Patient(int givenID, boolean honesty)
	{
		id 		= givenID;
		isFair 	= honesty;
		
		currentCure = 0;
		
		curesToDo = new Cure[3];
		curesToDo[0] = new Cure();
		curesToDo[1] = new Cure();
		curesToDo[2] = new Cure();
	}
	
	// Starting next cure
	public void startingCure()
	{
		if (currentCure <= 2)
		{
			curesToDo[currentCure].start(10);
			currentCure ++;
		} else {
			System.out.println("I have already done all my cures.");
		}
	}
	
	// Print
	public String toString()
	{
		String msg = "___________________________\n" + 
					"Patient ID :\t" + id + "\n" +
	    			"Honesty :\t" + isFair + "\n" +
		    		"\n\tCures to do :\n";
		
		for(int i = 0; i <=2; i++)
		{
			msg += "Cure #" + (i+1) + "\n";
			msg += curesToDo[i].toString();
			msg += "\n";
		}
		
	    return msg + "___________________________\n";
	}
}
