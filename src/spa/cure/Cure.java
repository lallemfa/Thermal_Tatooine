package spa.cure;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import engine.event.IEventScheduler;
import javafx.util.Pair;
import logger.IRecordableWrapper;
import spa.entity.Entity;
import spa.event.AppointmentTimeoutEvent;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Cure extends Entity implements IRecordableWrapper {

    private final double PROB_NB_TREATMENTS[] = {0.2, 0.35, 0.3, 0.15};

    private int startYear;
    private int startWeek;
    private List<Treatment> dailyTreatments;
    private List<Boolean> doneTreatments;
    private int maxPointsPerDay;
    private int maxPoints;
    private int currentPoints;
	private final Patient owner;
	private List<Pair<LocalTime, Duration>> appointmentTimes = new ArrayList<>();

	private String msg = "";

    public Cure(Patient patient, int startYear, int startWeek) {
    	super();
    	this.owner = patient;
    	this.startYear = startYear;
    	this.startWeek = startWeek;
    	this.currentPoints = 0;
    	this.maxPointsPerDay = 0;

        setTreatments();
        this.maxPoints = maxPointsPerDay * 5 * 3 * 3;
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
        this.doneTreatments = Arrays.asList(new Boolean[this.dailyTreatments.size()]);
        Collections.fill(this.doneTreatments, Boolean.FALSE);
    }
    
    public void findAppointments(IEventScheduler scheduler, SpaResort spa) {
    	for (Treatment treatment : this.dailyTreatments) {
    		if (treatment.isWithAppointment()) {
    			LocalTime time = treatment.getAppointmentTime(startYear, startWeek, appointmentTimes);
    			if (time == null) {
    			    continue;
                }
                appointmentTimes.add(new Pair<>(time, treatment.getDuration()));
                for (int w = 0; w < 3; w++) {
                    for (int y = 0; y < 3; y++) {
                        treatment.addAppointment(startYear + y, startWeek + w, time);
                        ZonedDateTime eventTime = spa.weekToDay(startYear + y, startWeek + w);
                        if (eventTime == null) {
                            continue;
                        }
                        eventTime = eventTime.with(time);
                        for (int k = 0; k < 5; k++) {
                            scheduler.postEvent(new AppointmentTimeoutEvent(this.owner, eventTime, spa, this.owner, treatment));
                            eventTime = eventTime.plusDays(1);
                        }
                    }
                }
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

	@Override
	public String[] getTitles() {
		return new String[] {"Classe", "Message"};
	}

	@Override
	public String[] getRecords() {
		return new String[] {this.getClass().getName(), this.msg};
	}

	@Override
	public String getClassement() {
		return "Cure";
	}

	@Override
	public void setMsg(String msg) {
		this.msg = msg;
		
	}
}
