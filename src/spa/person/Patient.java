package spa.person;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.IEventScheduler;
import enstabretagne.base.logger.IRecordable;
import spa.cure.Cure;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Patient extends Person implements IRecordable {

    boolean patient = true;
    private boolean isFair;

    private Cure cure;
    private ZonedDateTime maxArrivingTime; // TODO

    private ZonedDateTime startTreatment; 
    private ZonedDateTime startWaiting; 
    
    private Duration waitedDuration;

    public Patient(int id) {
        this(id, true);
    }

    public Patient(int id, boolean isFair) {
        this.id = id;
        this.isFair = isFair;
        // TODO: compute random time for cure start
        this.cure = new Cure(this, ZonedDateTime.now());
    }
    
    public Boolean getFairness() {
    	return this.isFair;
    }

    public Cure getCure() {
        return cure;
    }

    public ZonedDateTime getStartTreatment() {
        return this.startTreatment;
    }
    
    public void setStartTreatment(ZonedDateTime scheduledTime) {
    	this.startTreatment = scheduledTime;
    }
    
    public ZonedDateTime getStartWaiting() {
        return this.startWaiting;
    }
    
    public void setStartWaiting(ZonedDateTime scheduledTime) {
    	this.startWaiting = scheduledTime;
    }

    public void addCurePoints(int points) {
        this.cure.addPoints(points);
    }
    
    public void setTreatment(Treatment treatment) {
    	this.treatment = treatment;
    }
/*
    public String toString() {
        return "___________________________\n" +
            "Patient ID :\t" + this.id + "\n" +
            "Honesty :\t" + this.isFair + "\n" +
            "\n\tCure to do :\n" + this.cure.toString() + "\n" +
            "___________________________";
    }
*/
    
	void initEvents(IEventScheduler scheduler, SpaResort spa) {
		this.cure.findAppointments(scheduler, spa);
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
