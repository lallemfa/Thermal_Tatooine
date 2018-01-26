package spa.person;

import java.time.ZonedDateTime;

import enstabretagne.base.logger.IRecordable;
import spa.cure.Cure;
import spa.treatment.Treatment;

public class Patient extends Person implements IRecordable {

    boolean patient = true;
    private boolean isFair;

    private Cure cure;
    private ZonedDateTime maxArrivingTime; // TODO

    private ZonedDateTime startTreatment; // TODO

    public Patient(int id) {
        this(id, true);
    }

    public Patient(int id, boolean isFair) {
        this.id = id;
        this.isFair = isFair;
        // TODO: compute random time for cure start
        this.cure = new Cure(ZonedDateTime.now());
    }

    public Cure getCure() {
        return cure;
    }

    public ZonedDateTime getStartTreatment() {
        return startTreatment;
    }
    
    public void setStartTreatment(ZonedDateTime scheduledTime) {
    	this.startTreatment = scheduledTime;
    }

    public void addCurePoints(int points) {
        this.cure.addPoints(points);
    }
    
    public void setTreatment(Treatment treatment) {
    	this.treatment = treatment;
    }

    public String toString() {
        return "___________________________\n" +
            "Patient ID :\t" + this.id + "\n" +
            "Honesty :\t" + this.isFair + "\n" +
            "\n\tCure to do :\n" + this.cure.toString() + "\n" +
            "___________________________";
    }
    
    // Next 3 methods for the Logger
    
	@Override
	public String[] getTitles() {
		return new String[] {"Classe"};
	}

	@Override
	public String[] getRecords() {
		return new String[] {this.getClass().getName()};
	}

	@Override
	public String getClassement() {
		return "Treatment";
	}
}