package spa.treatment;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import engine.event.IEventScheduler;
import javafx.util.Pair;
import logger.IRecordableWrapper;
import spa.event.FailureEvent;
import spa.event.RepairEvent;
import spa.person.Patient;
import spa.resort.SpaResort;

public enum Treatment implements IRecordableWrapper {

	Filiformes		(0, "Jets filiformes", 			TreatmentType.Filiforme, 	"10:00:00", "13:00:00", false,
					4, 5, 30, false, 10,  28,  4, 2),
	Douches			(1, "Douches", 					TreatmentType.Douche, 		"07:15:00", "14:00:00", false,
					8, 10, 10, false,  8,  49,  2, 2),
	BainsAnciens	(2, "Bain à jets anciens", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", false,
					9, 20, 10,  true, 15,  35,  4, 2),
	SoinVisage		(3, "Soin du visage", 			TreatmentType.Visage, 		"07:15:00", "14:00:00", false,
					8, 10,  5, false,  5, 365, 40, 1),
	Etuves			(4, "Etuves", 					TreatmentType.Etuve, 		"07:15:00", "14:00:00", true,
					6, 15, 15,  true,  6,  21,  5, 3),
	BainsModernes	(5, "Bain à jets modernes", 	TreatmentType.Bain, 		"07:15:00", "14:00:00", false,
					8, 20, 15,  true, 10,  70, 10, 4),
	TerresChaudes	(6, "Terres Chaudes", 			TreatmentType.Terre, 		"07:15:00", "14:00:00", true,
					6, 20, 20,  true, 10,  61, 10, 3);

	
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

    private List<LocalTime> appointmentTimes;
	private HashMap<Integer, HashMap<Integer, List<Integer>>> appointments = new HashMap<>();

	private int durationWaitedPerDay = 0;
	private int patientsCuredPerDay = 0;
	private String msg = "";
	
	Treatment(int id, String name, TreatmentType type, String openHour, String closeHour, boolean withAppointment,
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
		this.repairMeanDuration			= repairMeanDuration;

		// Appointment Times
		if (withAppointment) {
			appointmentTimes = new ArrayList<>();
			LocalTime time = this.openHour.plus(Duration.ofMinutes(5));
			while (time.compareTo(this.closeHour) < 0) {
				appointmentTimes.add(time);
				time = time.plus(this.duration).plus(Duration.ofMinutes(5));
			}
		}
	}

    public Duration getDuration() {
		return this.duration;
	}
	
	public Boolean getOrganizedWaiting() {
		return this.isOrganizedWaiting;
	}
	
	public Boolean getBrokenState() {
		return this.broken;
	}
	
	public void setBrokenState(Boolean brokenState) {
		this.broken = brokenState;
	}

	public void clearQueues() {
		this.waitingQueue.clear();
		this.currentPatients.clear();
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
	
	public void addCurrentPatients(Patient patient, Duration durationWaited) {
		this.patientsCuredPerDay 	+= 1;
		this.durationWaitedPerDay 	+= durationWaited.getSeconds();
		this.currentPatients.add(patient);
	}
	
	public void addWaitingQueuePatient(Patient patient) {
		this.waitingQueue.add(patient);
	}
	
	public void removeCurrentPatients(Patient patient) {
		this.currentPatients.remove(patient);
	}
	
	public void removeWaitingQueuePatient(Patient patient) {
		this.waitingQueue.remove(patient);
	}

	public Patient popFirstInWaitingQueue() {
		if (this.waitingQueue.isEmpty()) {
			return null;
		}
		return this.waitingQueue.remove(0);
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
			currentTime = currentTime.with(openingTime).minusHours(1);
			scheduler.postEvent(new FailureEvent(this, currentTime, spa, this));
			
			
			// Repair
			Duration nbDaysToRepair = getDurationToRepair();
			currentTime = currentTime.plus(nbDaysToRepair);
			if (!spa.isOpen(currentTime)) {
				currentTime = spa.nextOpenDay(currentTime);
			}
			LocalTime closingTime = spa.getClosingHour(currentTime);
			currentTime = currentTime.with(closingTime).plusHours(1);
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

	public void addAppointment(int year, int week, LocalTime time) {
		createAppointmentNodeIfNotExists(year, week);
        int index = appointmentTimes.indexOf(time);
        appointments.get(year).get(week).set(index, appointments.get(year).get(week).get(index) + 1);
    }

	public LocalTime getAppointmentTime(int year, int week, List<Pair<LocalTime, Duration>> patientAppnts) {
		createAppointmentNodeIfNotExists(year, week);
        for (int i = 0; i < appointments.get(year).get(week).size(); i++) {
            if (appointments.get(year).get(week).get(i) < maxPatientsWorking) {
            	boolean available = true;
            	LocalTime startTime = appointmentTimes.get(i);
            	LocalTime endTime = startTime.plus(duration);
            	for (Pair<LocalTime, Duration> appts : patientAppnts) {
					if (!(appts.getKey().isBefore(startTime) && appts.getKey().plus(appts.getValue()).isBefore(startTime)) &&
						!(appts.getKey().isAfter(endTime) && appts.getKey().plus(appts.getValue()).isAfter(endTime))) {
						available = false;
					}
				}
				if (available) {
					return appointmentTimes.get(i);
				}
            }
        }
		return null;
	}

	private void createAppointmentNodeIfNotExists(int year, int week) {
		if (!appointments.containsKey(year)) {
			appointments.put(year, new HashMap<>());
		}
		if (!appointments.get(year).containsKey(week)) {
			List<Integer> emptyList = Arrays.asList(new Integer[appointmentTimes.size()]);
			Collections.fill(emptyList, 0);
			appointments.get(year).put(week, emptyList);
		}
	}
	
	public void setDurationWaitedPerDay(int durationWaitedPerDay) {
		this.durationWaitedPerDay = durationWaitedPerDay;
	}

	public void setPatientsCuredPerDay(int patientsCuredPerDay) {
		this.patientsCuredPerDay = patientsCuredPerDay;
	}

	// Next 3 methods for the Logger
	@Override
	public String[] getTitles() {
		return new String[] {"Classe", "Duration Waited", "Patients Cured", "isBroken", "Message"};
	}

	@Override
	public String[] getRecords() {
		return new String[] {this.getClass().getName(), (this.durationWaitedPerDay/60f) + "", this.patientsCuredPerDay + "", broken + "", this.msg};
	}

	@Override
	public String getClassement() {
		return "Treatment";
	}
	
	@Override
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
