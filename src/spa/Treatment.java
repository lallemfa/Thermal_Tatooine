package spa;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import person.Patient;

public enum Treatment {

	TerresChaudes	(0, "Terres Chaudes", 			TreatmentType.Terre, 		"07:15:00", "14:00:00", true,
						6, 20, 20,  true, 10,  61, 10, 3),
	Filiformes		(1, "Jets filiformes", 		TreatmentType.Filiforme, 	"10:00:00", "13:00:00", true,
						4,  5, 30, false, 10,  28,  4, 2),
	Etuves			(2, "Etuves", 					TreatmentType.Etuve, 		"07:15:00", "14:00:00", true,
						6, 15, 15,  true,  6,  21,  5, 3),
	BainsModernes	(3, "Bain à jets modernes", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", true,
						8, 20, 15,  true, 10,  70, 10, 4),
	BainsAnciens	(4, "Bain à jets anciens", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", true,
						9, 20, 10,  true, 15,  35,  4, 2),
	Douches			(5, "Douches", 				TreatmentType.Douche, 		"07:15:00", "14:00:00", true,
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
	private final Duration maintenanceMeanDuration;
	
	private List<Patient> waitingQueue = new ArrayList<>();
	private List<Patient> currentPatients = new ArrayList<>();

	
	private Treatment(int id, String name, TreatmentType type, String openHour, String closeHour, boolean withAppointment,
			int maxPatientsWorking, int duration, int maxPoints, boolean isOrganizedWaiting,
			int maxPatientsWaiting, int failureMeanPerDay, int failureSTDD, int maintenanceMeanDuration) {
		
		this.id = id;
		
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
		this.maintenanceMeanDuration 	= Duration.ofDays(maintenanceMeanDuration);
	}
	
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
				"\t\tMaintenance Mean Duration (in days) -> " + maintenanceMeanDuration + "\n";
	}

	public Duration getDuration() {
		return duration;
	}

	public int getMaxPoints() {
		return maxPoints;
	}
	
	public List<Patient> getCurrentPatients() {
		return currentPatients;
	}
	
	public List<Patient> getWaitingQueue() {
		return waitingQueue;
	}
	
	public boolean isWithAppointment() {
		return this.withAppointment;
	}
	
	public void initEvents(ZonedDateTime startTime, ZonedDateTime endTime) {
		// TODO: failures and repairs
	}

	public ZonedDateTime getAppointmentTime(ZonedDateTime time) {
		// TODO: get appointment, time returned needs to be available for the next three weeks
		return null;
	}
}
