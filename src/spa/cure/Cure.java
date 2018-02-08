package spa.cure;

import java.time.DayOfWeek;
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
    private List<Float> doneTreatments;
    private int currentPointsThisYear;
    private int currentPointsThisDay;
    private int currentPoints;
	private final Patient owner;
	private List<Pair<LocalTime, Duration>> appointmentTimes = new ArrayList<>();

	private int maxPointsPerDay;
	private int maxPointsPerYear;
	
	private String msg = "";

    public Cure(Patient owner, int startYear, int startWeek) {
    	super();
    	this.owner = owner;
    	this.startYear = startYear;
    	this.startWeek = startWeek;
    	
    	this.currentPointsThisYear 	= 0;
    	this.currentPointsThisDay 	= 0;
    	this.currentPoints 			= 0;

        setTreatments();
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
            this.maxPointsPerDay += treatment.getMaxPoints();
            allTreatments.removeIf(t -> t.type == treatment.type);
        }
        this.doneTreatments = Arrays.asList(new Float[this.dailyTreatments.size()]);
        Collections.fill(this.doneTreatments, 0f);
    }
    
    public void findAppointments(IEventScheduler scheduler, SpaResort spa) {
    	this.maxPointsPerYear = this.maxPointsPerDay * spa.getOpeningDays().size() * 3;
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
                        for (DayOfWeek dow : spa.getOpeningDays()) {
                            eventTime = eventTime.with(dow);
                            scheduler.postEvent(new AppointmentTimeoutEvent(this.owner, eventTime, spa, this.owner, treatment));
                        }
                    }
                }
    		}
    	}
    }

    public LocalTime getEarliestAppointment() {
        LocalTime time = LocalTime.MAX;
        for (Pair<LocalTime, Duration> appointmentTime : this.appointmentTimes) {
            if (appointmentTime.getKey().isBefore(time)) {
                time = appointmentTime.getKey();
            }
        }
        return time;
    }

    public List<Treatment> getDailyTreatments() {
        return this.dailyTreatments;
    }
    
    public List<Float> getDoneTreatments() {
        return this.doneTreatments;
    }
    
    public void resetDoneTreatments() {
        Collections.fill(doneTreatments, 0f);
    }
    
    public int getMaxPointsPerDay() {
		return this.maxPointsPerDay;
	}

    public int getMaxPointsPerYear() {
		return this.maxPointsPerYear;
	}
    
	public void setDoneTreatments(Treatment treatment, float ratio) {
    	for (int i = 0; i < this.dailyTreatments.size(); ++i) {
            if (this.dailyTreatments.get(i) == treatment) {
            	this.doneTreatments.set(i, Math.min(Math.max(0f, ratio), 1f));
            }
        }
    }

    public boolean hasAppointmentsAfter(LocalTime time) {
        for (Pair<LocalTime, Duration> appointmentTime : this.appointmentTimes) {
            if (appointmentTime.getKey().isAfter(time)) {
                return true;
            }
        }
        return false;
    }

    public float getDoneTreatmentRatio(Treatment treatment) {
        for (int i = 0; i < this.dailyTreatments.size(); ++i) {
            if (this.dailyTreatments.get(i) == treatment) {
                return this.doneTreatments.get(i);
            }
        }
        return 0f;
    }


    public void addPoints(int points) {
        this.currentPoints 			+= points;
        this.currentPointsThisDay 	+= points;
        this.currentPointsThisYear 	+= points;
    }

    public int getPoints() {
        return this.currentPoints;
    }

    public int getPointsThisDay() {
        return this.currentPointsThisDay;
    }
    
    public int getPointsThisYear() {
        return this.currentPointsThisYear;
    }

    public void resetPointsThisDay() {
        this.currentPointsThisDay = 0;
    }
    
    public void resetPointsByYear() {
    	this.currentPointsThisYear = 0;
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
