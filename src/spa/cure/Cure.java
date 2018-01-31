package spa.cure;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.formula.functions.Days360;

import engine.event.IEventScheduler;
import spa.event.AppointmentTimeoutEvent;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Cure {

    private final double PROB_NB_TREATMENTS[] = {0.2, 0.35, 0.3, 0.15};
    private final double PROB_4_TREATMENTS = 0.35;
    private final double PROB_5_TREATMENTS = 0.3;
    private final double PROB_6_TREATMENTS = 0.15;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Treatment> dailyTreatments;
    private List<Appointment> appointments;
    private List<Boolean> doneTreatments;
    private int maxPointsPerDay;
    private int maxPoints;
    private int currentPoints;
	private final Patient owner;

    public Cure(Patient patient, ZonedDateTime start) {
    	this.owner = patient;
    	this.currentPoints = 0;
    	this.maxPointsPerDay = 0;
        setTreatments();
        //setAppointments();
        this.maxPoints = maxPointsPerDay * 5 * 3 * 3;
        // TODO: create events for patient arriving
        // TODO: calculate startDate/endDate
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
    
    // TODO TO CHECK
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
    
    private void setAppointments(ZonedDateTime time, IEventScheduler scheduler, SpaResort spa) {
    	ZonedDateTime yearTime = time;
    	ZonedDateTime eventTime = time;
    	
    	for (int i = 0; i<3; i++) {
    		yearTime = time.plusYears(i).with(DayOfWeek.MONDAY);
    		
    		if(!spa.isOpen(yearTime)) {
    			yearTime.plusWeeks(1);
    		}
    		while (!spa.isOpen(yearTime.plusWeeks(2))) {
    			yearTime.minusWeeks(1);
    		}
    		
        	for (int j = 0; j<3; j++) {
        		eventTime = yearTime.plusWeeks(j);
            	for (int k = 0; k<5; k++) {
            		eventTime = eventTime.plusDays(k);
            		scheduler.postEvent(new AppointmentTimeoutEvent(this, eventTime, spa, this.owner));
            	}
        	}
    	}
    }
    
    public void findAppointments(IEventScheduler scheduler, SpaResort spa) {
    	for (int i=0; i < this.dailyTreatments.size(); i++) {
    		Treatment treatment = this.dailyTreatments.get(i);
    		if (treatment.isWithAppointment()) {
    			LocalTime time = treatment.getAppointmentTime(this.startDate);
    			ZonedDateTime eventTime = this.startDate.with(time);
    			setAppointments(eventTime, scheduler, spa);
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

    public String toString() {
        if (startDate != null) {
            return "Time\n" +
                "\tStart -> " + startDate.toLocalDateTime() + "\n" +
                "\tEnd   -> " + endDate.toLocalDateTime() + "\n" +
                "Points\t" + currentPoints + " / " + maxPoints + "\n";
        } else {
            return "Time\n" +
                "\tStart -> Not started yet\n" +
                "\tEnd   -> Not started yet\n";
        }
    }
}
