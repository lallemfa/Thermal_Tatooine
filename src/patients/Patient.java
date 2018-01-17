package patients;

import java.time.ZonedDateTime;

import engine.Engine;

public class Patient {
	
	public int id;
	
	private boolean isFair;
	private Cure cure;
	
	// Constructors
	public Patient(int givenID){
		// Patients are believed to be honest if not defined.
		this(givenID, true);
	}
	
	public Patient(int givenID, boolean honesty){
		this.id 		= givenID;
		this.isFair 	= honesty;		
		this.cure = new Cure();
	}
	
	// Starting cure
	public void startingCure(){
		ZonedDateTime startTime = Engine.getCurrentTime();
		cure.start(startTime);
		// Test fin cure
	}
	
	// Print
	public String toString()
	{
		String msg = "___________________________\n" + 
					"Patient ID :\t" + this.id + "\n" +
	    			"Honesty :\t" + this.isFair + "\n" +
		    		"\n\tCure to do :\n" +
		    		this.cure.toString() +
		    		"\n";
		
	    return msg + "___________________________\n";
	}
}
