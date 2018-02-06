package spa.event;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class FailureEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private SpaResort spa;
	private Treatment treatment;

	public FailureEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Treatment treatment) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.treatment = treatment;
        this.spa = spa;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		Logger.Information(getParent(), "Process", "Failure of treatment: " + this.treatment.name);
		this.treatment.setBrokenState(true);
		List<Patient> patientInTreatments = findPatientsInTreatment();
		// TODO MANAGERS
		while (!patientInTreatments.isEmpty()) {
			addEndTreatmentEvent(scheduler, patientInTreatments.remove(patientInTreatments.size() - 1));
		}
	}
	
	private List<Patient> findPatientsInTreatment(){
		List<Patient> patientInTreatments = new ArrayList<>();
		List<Patient> waitingQueue = this.treatment.getWaitingQueue();
		List<Patient> currentPatients = this.treatment.getCurrentPatients();
		if (!waitingQueue.isEmpty()) {
			patientInTreatments.addAll(waitingQueue);
		}
		if (!currentPatients.isEmpty()) {
			patientInTreatments.addAll(currentPatients);
		}
		return patientInTreatments;
	}
	
	private void addEndTreatmentEvent(IEventScheduler scheduler, Patient patient) {
		IEvent endTreatEvent;
		endTreatEvent = new EndTreatmentEvent(getParent(), this.scheduledTime, this.spa, patient);
		scheduler.postEvent(endTreatEvent);
	}
}