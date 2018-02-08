package spa.person;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

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

    private ZonedDateTime startTreatment; 
    private ZonedDateTime startWaiting; 
    
    private Duration waitedDuration = Duration.ZERO;

    public IEvent nextEndTreatment;
    public IEvent nextMovingEvent;
    public int nbRecursiveSearch = 0;
    
    private String msg = "";

    public Patient(int id, boolean isFair, int startYear, int startWeek) {
    	super();
        this.id = id;
        this.isFair = isFair;
        this.startYear = startYear;
        this.startWeek = startWeek;
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
    
    public void resetPointsByDay() {
    	this.cure.resetPointsThisDay();
    }
    
    public void resetPointsByYear() {
    	this.cure.resetPointsByYear();
    }
    
    public void addWaitedTime(Duration duration) {
        this.waitedDuration = this.waitedDuration.plus(duration);
    }
    
    public void setTreatment(Treatment treatment) {
    	this.treatment = treatment;
    }
    
	public void initEvents(IEventScheduler scheduler, SpaResort spa) {
        this.cure.findAppointments(scheduler, spa);

    	for (int i = 0; i < 3; i++) {
    		int year = this.startYear + i;
        	for (int j = 0; j < 3; j++) {
        		int week = this.startWeek + j;
                ZonedDateTime eventTime = spa.weekToDay(year, week);
        		if (eventTime == null) {
        		    continue;
                }
                LocalTime minSpaTime = spa.getOpeningHour(eventTime).plusMinutes(10);
                LocalTime maxSpaTime = spa.getOpeningHour(eventTime).plusHours(3);
                LocalTime maxTime = cure.getEarliestAppointment().minusMinutes(10);
                if (maxTime.isAfter(maxSpaTime)) {
                    maxTime = maxSpaTime;
                }
                for (DayOfWeek dow : spa.getOpeningDays()) {
        		    Duration delta = Duration.between(minSpaTime, maxTime);
        		    long random = (long)Math.floor(Math.random() * delta.toMinutes());
        		    LocalTime arrivalTime = minSpaTime.plusMinutes(random);
                    eventTime = eventTime.with(dow).with(arrivalTime);
                    scheduler.postEvent(new PatientArrivalEvent(this, eventTime, spa, this));
                }
        	}
    	}
	}
    
    // Next 3 methods for the Logger
	@Override
	public String[] getTitles() {
		return new String[] {"Id", "Duration waited", "Total Points Earned",
							"Points Earned / Day", "Points Earned / Year",
							"Max Points/Day", "Max Points/Year",
							"Treatments done", "Treatments to do", "Message"};
	}

	@Override
	public String[] getRecords() {
		int doneTreatments = this.cure.getDoneTreatments().stream().filter(t -> t == 1f).collect(Collectors.toList()).size();
		return new String[] {String.valueOf(this.id), this.waitedDuration.toMinutes() + "", this.cure.getPoints() + "",
							this.cure.getPointsThisDay() + "", this.cure.getPointsThisYear() + "",
							this.cure.getMaxPointsPerDay() + "", this.cure.getMaxPointsPerYear() + "",
							doneTreatments + "", this.cure.getDailyTreatments().size() + "", this.msg};
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
