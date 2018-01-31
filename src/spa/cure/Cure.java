package spa.cure;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.cure.Appointment;
import spa.entity.Entity;
import spa.event.AppointmentTimeoutEvent;
import spa.event.EndTreatmentEvent;
import spa.treatment.Treatment;

public class Cure extends Entity {

    private final double PROB_NB_TREATMENTS[] = {0.2, 0.35, 0.3, 0.15};

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Treatment> dailyTreatments;
    private List<Appointment> appointments;
    private List<Boolean> doneTreatments;
    private int maxPointsPerDay;
    private int maxPoints;
    private int currentPoints;

    public Cure(IEventScheduler scheduler, ZonedDateTime start) {
    	super(scheduler);
        currentPoints = 0;
        maxPointsPerDay = 0;
        setTreatments();
        setAppointments();
        maxPoints = maxPointsPerDay * 5 * 3 * 3;
        // TODO: create events for patient arriving
        // TODO: calculate startDate/endDate
        super.endConstructor();
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
            maxPointsPerDay += treatment.getMaxPoints();
        }
        
        this.doneTreatments = new ArrayList<Boolean>(this.dailyTreatments.size());
        Collections.fill(this.doneTreatments, Boolean.FALSE);
    }

    private void setAppointments() {
    	for (int i=0; i < dailyTreatments.size(); i++) {
    		Treatment treatment = dailyTreatments.get(i);
    		if (treatment.isWithAppointment()) {
    			IEvent appointmentEvent;
    			LocalTime time = treatment.getAppointmentTime(this.startDate);
    			//appointmentEvent = new AppointmentTimeoutEvent(this, time, this.spa, this.patient);
    			//scheduler.postEvent(appointmentEvent);
    		}
    	}
        // TODO: set appointments for treatments
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

    public List<Treatment> getDailyTreatments() {
        return dailyTreatments;
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
