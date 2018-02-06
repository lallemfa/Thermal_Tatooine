package spa.event;

import java.time.ZonedDateTime;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class AvailableTreatmentEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Treatment treatment;
	private SpaResort spa;

	public AvailableTreatmentEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Treatment treatment) {
		super(parent);
		this.spa = spa;
		this.scheduledTime = scheduledTime;
		this.treatment = treatment;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		if (!this.treatment.getWaitingQueue().isEmpty()) {
			Patient nextPatient = findNextPatient();
			this.treatment.addCurrentPatients(nextPatient);
			nextPatient.setStartTreatment(this.scheduledTime);
			nextPatient.setPersonState(PersonState.Treatment);
			IEvent endTreatmentEvent;
			ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
			if (this.spa.getClosingHour(this.scheduledTime).isBefore(this.scheduledTime.plus(this.treatment.getDuration()).toLocalTime())) {
				time = time.with(this.spa.getClosingHour(this.scheduledTime));
			}
			
			LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + nextPatient.getId() + " starts " + treatment.name);
			
			endTreatmentEvent = new EndTreatmentEvent(getParent(), time, this.spa, nextPatient);
			nextPatient.nextEndTreatment = endTreatmentEvent;
			scheduler.postEvent(endTreatmentEvent);
		}
	}
	
	private Patient findNextPatient() {
		if (cheatWorks(this.treatment)) {
			List<Patient> waitingQueue = this.treatment.getWaitingQueue();
			for (Patient patient : waitingQueue) {
				if (!patient.getFairness()) {
					this.treatment.removeWaitingQueuePatient(patient);
					return patient;
				}
			}
		}
		return this.treatment.popFirstInWaitingQueue();
	}
	
	private Boolean cheatWorks(Treatment treatment) {
		double waitingTime;
		double freq;
		if (treatment.getOrganizedWaiting()) {
			waitingTime = 20d;
			freq = 10d;
		} else {
			waitingTime = 10d;
			freq = 4d;
		}
		double rand = Math.random() * 40d;
		if (rand >= waitingTime) {
			rand = Math.random() * freq;
			if (rand <= 1d) {
				return true;
			}
		}		
		return false;
	}

}
