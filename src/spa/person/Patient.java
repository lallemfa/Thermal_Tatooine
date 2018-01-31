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

    public Patient(IEventScheduler scheduler, int id) {
        this(id, true);
    }

    public Patient(int id, boolean isFair) {
    	super();
        this.id = id;
        this.isFair = isFair;
        // TODO: compute random time for cure start
        this.cure = new Cure(this, ZonedDateTime.now());
        super.endConstructor();
        super.addChildren(this.cure);
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
