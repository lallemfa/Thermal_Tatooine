package spa.event;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.LogType;
import logger.Logger;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

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
	public void process(IEventScheduler scheduler) {
		Treatment[] treatments = spa.getTreatments();
		List<Patient> patientInTreatments = findPatientsInTreatments(treatments);
		// TODO MANAGERS
		while (!patientInTreatments.isEmpty()) {
			addEndTreatmentEvent(scheduler, patientInTreatments.remove(patientInTreatments.size()));
		}
		Logger.log(LogType.INFO, this.scheduledTime, "Spa closes");
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
	
	private void addEndTreatmentEvent(IEventScheduler scheduler, Patient patient) {
		IEvent endTreatEvent;
		endTreatEvent = new EndTreatmentEvent(this.scheduledTime, this.spa, patient);
		scheduler.postEvent(endTreatEvent);
	}
}
