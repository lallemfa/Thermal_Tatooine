package spa.person;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.IEventScheduler;
import enstabretagne.base.logger.IRecordable;
import spa.cure.Cure;
import spa.event.AppointmentTimeoutEvent;
import spa.event.PatientArrivalEvent;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Patient extends Person implements IRecordable {

    boolean patient = true;
    private boolean isFair;

    private int startYear;
    private int startWeek;
    
    private Cure cure;
    private ZonedDateTime maxArrivingTime; // TODO

    private ZonedDateTime startTreatment; 
    private ZonedDateTime startWaiting; 
    
    private Duration waitedDuration;

    public Patient(int id, boolean isFair, int startYear, int startWeek) {
    	super();
        this.id = id;
        this.isFair = isFair;
        this.startYear = startYear;
        this.startWeek = startWeek;
        // TODO: compute random time for cure start
        this.cure = new Cure(this, startYear, startWeek);
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
    
	public void initEvents(IEventScheduler scheduler, SpaResort spa) {
		ZonedDateTime yearTime;
    	ZonedDateTime eventTime;
    	int startYear = this.startYear;
    	int startWeek = this.startWeek;
    	
    	for (int i = 0; i < 3; i++) {
    		int year = startYear + i;
        	for (int j = 0; j < 3; j++) {
        		int week = startWeek + j;
        		eventTime = spa.weekToDay(year, week);
            	for (int k = 0; k < 5; k++) {
            		scheduler.postEvent(new PatientArrivalEvent(this, eventTime, spa, this));
            		eventTime = eventTime.plusDays(1);
            	}
        	}
    	}		
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
