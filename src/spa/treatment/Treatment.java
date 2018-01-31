package spa.treatment;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import engine.event.IEventScheduler;
import engine.event.MessageEvent;
import enstabretagne.base.logger.IRecordable;
import spa.event.FailureEvent;
import spa.event.RepairEvent;
import spa.person.Patient;
import spa.resort.SpaResort;

public enum Treatment implements IRecordable {

	TerresChaudes	(0, "Terres Chaudes", 			TreatmentType.Terre, 		"07:15:00", "14:00:00", true,
						6, 20, 20,  true, 10,  61, 10, 3),
	Filiformes		(1, "Jets filiformes", 			TreatmentType.Filiforme, 	"10:00:00", "13:00:00", true,
						4,  5, 30, false, 10,  28,  4, 2),
	Etuves			(2, "Etuves", 					TreatmentType.Etuve, 		"07:15:00", "14:00:00", true,
						6, 15, 15,  true,  6,  21,  5, 3),
	BainsModernes	(3, "Bain à jets modernes", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", true,
						8, 20, 15,  true, 10,  70, 10, 4),
	BainsAnciens	(4, "Bain à jets anciens", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", true,
						9, 20, 10,  true, 15,  35,  4, 2),
	Douches			(5, "Douches", 					TreatmentType.Douche, 		"07:15:00", "14:00:00", true,
						8, 10, 10, false,  8,  49,  2, 2),
	SoinVisage		(6, "Soin du visage", 			TreatmentType.Visage, 		"07:15:00", "14:00:00", true,
						8, 10,  5, false,  5, 365, 40, 1);
	
	public final String name;
	public final TreatmentType type;
	
	public final int id;
	
	private final LocalTime openHour;
	private final LocalTime closeHour;
	
	private final boolean withAppointment;
	private final int maxPatientsWorking;

	private final Duration duration;
	private final int maxPoints;
	private final boolean isOrganizedWaiting;
	private final int maxPatientsWaiting;
	private final int failureSTDD;
	private final int failureMeanPerDay;
	private final int repairMeanDuration;
	
	private boolean broken;
	
	private List<Patient> waitingQueue = new ArrayList<>();
	private List<Patient> currentPatients = new ArrayList<>();

	
	private Treatment(int id, String name, TreatmentType type, String openHour, String closeHour, boolean withAppointment,
			int maxPatientsWorking, int duration, int maxPoints, boolean isOrganizedWaiting,
			int maxPatientsWaiting, int failureMeanPerDay, int failureSTDD, int repairMeanDuration) {
		
		this.id = id;
		this.broken = false;
		this.name = name;
		this.type = type;
		
		// Timetable
		this.openHour 	= LocalTime.parse(openHour);
		this.closeHour 	= LocalTime.parse(closeHour);
		
		// Mechanics
		this.duration 	= Duration.ofMinutes(duration);
		this.maxPoints 	= maxPoints;
		
		this.withAppointment = withAppointment;
		this.isOrganizedWaiting = isOrganizedWaiting;
		this.maxPatientsWorking = maxPatientsWorking;
		this.maxPatientsWaiting = maxPatientsWaiting;
		
		// Failures
		this.failureSTDD 				= failureSTDD;
		this.failureMeanPerDay			= failureMeanPerDay;
		this.repairMeanDuration = repairMeanDuration;
	}
/*	
	public String toString() {
		return name + " | Type: " + type + "\n" +
				"\tOpen from " + openHour + " to " + closeHour + "\n" +
				"\tTreatment lasts " + duration + " minutes and gives " + maxPoints + " points if fulfilled\n" + 
				"\tWith Rendez-Vous ? " + withAppointment + "\n" +
				"\t\tMax number of patients working -> " + maxPatientsWorking + "\n" +
				"\tOrganized waiting ? " + isOrganizedWaiting + "\n" +
				"\t\tMax number of patients waiting -> " + maxPatientsWaiting + "\n" +
				"\tFailures :\n" + 
				"\t\tFailure mean (in days)              -> " + failureMeanPerDay + "\n" +
				"\t\tStandard Deviation (in days)        -> " + failureSTDD + "\n" +
				"\t\tMaintenance Mean Duration (in days) -> " + repairMeanDuration + "\n";
	}
*/
	public Duration getDuration() {
		return this.duration;
	}
	
	public Boolean getBrokenState() {
		return this.broken;
	}
	
	public void setBrokenState(Boolean brokenState) {
		this.broken = brokenState;
	}

	public int getMaxPoints() {
		return this.maxPoints;
	}
	
	public int getMaxPatientsWaiting() {
		return this.maxPatientsWaiting;
	}

	public int getMaxPatientsWorking() {
		return this.maxPatientsWorking;
	}
	
	public List<Patient> getCurrentPatients() {
		return this.currentPatients;
	}
	
	public List<Patient> getWaitingQueue() {
		return this.waitingQueue;
	}
	
	public void addCurrentPatients(Patient patient) {
		this.currentPatients.add(patient);
	}
	
	public void addWaitingQueuePatient(Patient patient) {
		this.waitingQueue.add(patient);
	}
	
	public void removeCurrentPatients(Patient patient) {
		for (int i = 0; i < this.currentPatients.size(); ++i) {
            if (this.currentPatients.get(i) == patient) {
            	this.currentPatients.remove(i);
            }
        }
	}
	
	public void removeWaitingQueuePatient(Patient patient) {
		for (int i = 0; i < this.waitingQueue.size(); ++i) {
            if (this.waitingQueue.get(i) == patient) {
            	this.waitingQueue.remove(i);
            }
        }
	}
	
	public boolean isWithAppointment() {
		return this.withAppointment;
	}
	
	public void initEvents(IEventScheduler scheduler, SpaResort spa, ZonedDateTime startTime, ZonedDateTime endTime) {
		ZonedDateTime currentTime = ZonedDateTime.of(startTime.toLocalDateTime(), startTime.getZone());
		currentTime = spa.nextOpenDay(currentTime);
		while (currentTime.compareTo(endTime) < 0) {
			// Failure
			Duration nbDaysToFailure = getDurationToNextFailure();
			currentTime = currentTime.plus(nbDaysToFailure);
			if (!spa.isOpen(currentTime)) {
				currentTime = spa.nextOpenDay(currentTime);
			}
			LocalTime openingTime = spa.getOpeningHour(currentTime);
			currentTime = currentTime.with(openingTime);
			scheduler.postEvent(new FailureEvent(this, currentTime, spa, this));

			// Repair
			Duration nbDaysToRepair = getDurationToRepair();
			currentTime = currentTime.plus(nbDaysToRepair);
			if (!spa.isOpen(currentTime)) {
				currentTime = spa.nextOpenDay(currentTime);
			}
			LocalTime closingTime = spa.getClosingHour(currentTime);
			currentTime = currentTime.with(closingTime);
			scheduler.postEvent(new RepairEvent(this, currentTime, this));
			currentTime = currentTime.plusDays(1);
		}
	}

	private Duration getDurationToNextFailure() {
		Random random = new Random();
		double nbDaysToFailure = - failureMeanPerDay * Math.log(1 - random.nextDouble());
		return Duration.ofDays(Math.round(nbDaysToFailure));
	}

	private Duration getDurationToRepair() {
		Random random = new Random();
		double nbDaysToRepair = - repairMeanDuration * Math.log(1 - random.nextDouble());
		return Duration.ofDays(Math.round(nbDaysToRepair));
	}

	public LocalTime getAppointmentTime(ZonedDateTime time) {
		// TODO: get appointment, time returned needs to be available for the next three weeks
		return null;
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
