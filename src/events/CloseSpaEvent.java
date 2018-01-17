package events;

import person.Patient;
import spa.SpaResort;
import spa.Treatment;

import java.util.ArrayList;
import java.util.List;

import engine.Engine;

import java.time.ZonedDateTime;

public class CloseSpaEvent implements IEvent {

    private final ZonedDateTime scheduledTime;
    private final SpaResort spa;
    
    public CloseSpaEvent(ZonedDateTime scheduledTime, SpaResort spa) {
        this.scheduledTime = scheduledTime;
        this.spa = spa;
    }
    
	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process() {
		Treatment[] treatments = spa.getTreatments();
		List<Patient> patientInTreatments = findPatientsInTreatments(treatments);
		// TODO MANAGERS
		while (!patientInTreatments.isEmpty()) {
			addEndTreatmentEvent(patientInTreatments.remove(patientInTreatments.size()));
		}
		
	}
	
	private List<Patient> findPatientsInTreatments(Treatment[] treatments){
		List<Patient> patientInTreatments = new ArrayList<>();
		for (int i = 0; i < treatments.length; i++){
			List<Patient> waitingQueue = treatments[i].getWaitingQueue();
			List<Patient> currentPatients = treatments[i].getCurrentPatients();
			if (!waitingQueue.isEmpty()) {
				patientInTreatments.addAll(waitingQueue);
			}
			if (!currentPatients.isEmpty()) {
				patientInTreatments.addAll(currentPatients);
			}
		}
		return patientInTreatments;
	}
	
	private void addEndTreatmentEvent(Patient patient) {
		IEvent endTreatEvent;
		endTreatEvent = new EndTreatmentEvent(scheduledTime, spa, patient);
		Engine.addEvent(endTreatEvent);
	}
}
