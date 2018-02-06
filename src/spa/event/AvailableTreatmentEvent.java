package spa.event;

import java.time.Duration;
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
			
			Duration durationWaited = Duration.between(nextPatient.getStartWaiting(), this.scheduledTime);

			nextPatient.addWaitedTime(durationWaited);
			
			IEvent endTreatmentEvent;
			ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
			if (this.spa.getClosingHour(this.scheduledTime).compareTo(this.scheduledTime.plus(this.treatment.getDuration()).toLocalTime()) < 0) {
				time = time.with(this.spa.getClosingHour(this.scheduledTime));
			}
			
			LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + nextPatient.getId() + " starts " + treatment.name);
			
			endTreatmentEvent = new EndTreatmentEvent(getParent(), time, this.spa, nextPatient);
			nextPatient.nextEndTreatment = endTreatmentEvent;
			scheduler.postEvent(endTreatmentEvent);
		}
	}
	
	private Patient findNextPatient() {
		Patient nextPatient = null;
		if (cheatWorks(this.treatment)) {
			Boolean findCheater = false;
			List<Patient> waitingQueue = this.treatment.getWaitingQueue();
			for (int i=0; i < waitingQueue.size(); i++) {
				if (!waitingQueue.get(i).getFairness()) {
					nextPatient = waitingQueue.remove(i);
					findCheater = true;
					break;
				}
			}	
			if (!findCheater) {
				nextPatient = this.treatment.getWaitingQueue().remove(0);
			}
		} else {
			nextPatient = this.treatment.getWaitingQueue().remove(0);
		}
		return nextPatient;
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
