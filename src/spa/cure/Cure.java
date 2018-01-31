package spa.cure;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import engine.event.IEventScheduler;
import spa.entity.Entity;
import spa.event.AppointmentTimeoutEvent;
import spa.event.PatientArrivalEvent;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Cure extends Entity {

    private final double PROB_NB_TREATMENTS[] = {0.2, 0.35, 0.3, 0.15};

    private int startYear;
    private int startWeek;
    private List<Treatment> dailyTreatments;
    private List<Boolean> doneTreatments;
    private int maxPointsPerDay;
    private int maxPoints;
    private int currentPoints;
	private final Patient owner;

    public Cure(Patient patient, int startYear, int startWeek) {
    	super();
    	this.owner = patient;
    	this.startYear = startYear;
    	this.startWeek = startWeek;
    	this.currentPoints = 0;
    	this.maxPointsPerDay = 0;

        setTreatments();
        this.maxPoints = maxPointsPerDay * 5 * 3 * 3;
        // TODO: create events for patient arriving
        // TODO: calculate startDate/endDate
        super.endConstructor();
    }

    private int getNbDailyTreatments() {
        double random = Math.random();
        for (int i = 0; i < PROB_NB_TREATMENTS.length; i++) {
            if (random < PROB_NB_TREATMENTS[i]) {
                return 3 + i;
            }
            random -= PROB_NB_TREATMENTS[i];
        }
        return 0;
    }

    public boolean hasCureDuringWeek(int year, int week) {
        return Math.abs(week - startWeek) < 3 && Math.abs(year - startYear) < 3;
    }
    
    private void setTreatments() {
        List<Treatment> allTreatments = new ArrayList<>(Arrays.asList(Treatment.values()));
        int nbTreatments = getNbDailyTreatments();
        this.dailyTreatments = new ArrayList<>();
        for (int i = 0; i < nbTreatments; i++) {
            int randomIndex = (int) Math.floor(allTreatments.size() * Math.random());
            Treatment treatment = allTreatments.remove(randomIndex);
            this.dailyTreatments.add(treatment);
            allTreatments.removeIf(t -> t.type == treatment.type);
            this.maxPointsPerDay += treatment.getMaxPoints();
        }
        this.doneTreatments = new ArrayList<Boolean>(this.dailyTreatments.size());
        Collections.fill(this.doneTreatments, Boolean.FALSE);
    }
    
    private void setAppointmentEvents(int startYear, int startWeek, IEventScheduler scheduler, SpaResort spa, Treatment treatment) {
    	ZonedDateTime yearTime;
    	ZonedDateTime eventTime;

    	for (int i = 0; i < 3; i++) {
    		int year = this.startYear + i;
        	for (int j = 0; j < 3; j++) {
        		int week = this.startWeek + j;
        		eventTime = spa.weekToDay(year, week);
            	for (int k = 0; k < 5; k++) {
            		scheduler.postEvent(new AppointmentTimeoutEvent(this, eventTime, spa, this.owner, treatment));
            		eventTime = eventTime.plusDays(1);
            	}
        	}
    	}		
    }
    
    public void findAppointments(IEventScheduler scheduler, SpaResort spa) {
    	for (Treatment treatment : this.dailyTreatments) {
    		if (treatment.isWithAppointment()) {
    			LocalTime time = treatment.getAppointmentTime(startYear, startWeek);
                for (int w = 0; w < 3; w++) {
                    for (int y = 0; y < 3; y++) {
                        treatment.addAppointment(startYear + y, startWeek + w, time);
                    }
                }
    			setAppointmentEvents(startYear, startWeek, scheduler, spa, treatment);
    		}
    	}
    }

    public List<Treatment> getDailyTreatments() {
        return this.dailyTreatments;
    }
    
    public List<Boolean> getDoneTreatments() {
        return this.doneTreatments;
    }
    
    public void resetDoneTreatments() {
        Collections.fill(doneTreatments, Boolean.FALSE);
    }
    
    public void setDoneTreatments(Treatment treatment) {
    	for (int i = 0; i < this.dailyTreatments.size(); ++i) {
            if (this.dailyTreatments.get(i) == treatment) {
            	this.doneTreatments.set(i, Boolean.TRUE);
            }
        }        
    }


    public void addPoints(int points) {
        this.currentPoints += points;
    }

    public int getPoints() {
        return this.currentPoints;
    }
}
