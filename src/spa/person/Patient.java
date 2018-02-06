package spa.person;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import spa.cure.Cure;
import spa.event.PatientArrivalEvent;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Patient extends Person implements IRecordableWrapper {

    boolean patient = true;
    private boolean isFair;

    private int startYear;
    private int startWeek;
    
    private Cure cure;
    private ZonedDateTime maxArrivingTime; // TODO

    private ZonedDateTime startTreatment; 
    private ZonedDateTime startWaiting; 
    
    private Duration waitedDuration = Duration.ZERO;

    public IEvent nextEndTreatment;
    
    private String msg = "";

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
    
    public void addWaitedTime(Duration duration) {
        this.waitedDuration = this.waitedDuration.plus(duration);
    }
    
    public void setTreatment(Treatment treatment) {
    	this.treatment = treatment;
    }
    
	public void initEvents(IEventScheduler scheduler, SpaResort spa) {
    	ZonedDateTime eventTime;
    	
    	for (int i = 0; i < 3; i++) {
    		int year = this.startYear + i;
        	for (int j = 0; j < 3; j++) {
        		int week = this.startWeek + j;
        		eventTime = spa.weekToDay(year, week);
        		if (eventTime == null) {
        		    continue;
                }
            	for (int k = 0; k < 5; k++) {
            	    eventTime = eventTime.withHour(7).withMinute(15);
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
		return new String[] {"Classe", "Id", "Duration waited", "Message"};
	}

	@Override
	public String[] getRecords() {
		return new String[] {this.getClass().getName(), String.valueOf(this.id), this.waitedDuration + "", this.msg};
	}

	@Override
	public String getClassement() {
		return "Patient";
	}
	
	@Override
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
