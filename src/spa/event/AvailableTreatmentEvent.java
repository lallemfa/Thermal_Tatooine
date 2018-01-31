package spa.event;

import java.time.ZonedDateTime;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.person.Patient;
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
		Patient nextPatient = findNextPatient();
		this.treatment.addCurrentPatients(nextPatient);
		nextPatient.setStartTreatment(this.scheduledTime);
		IEvent endTreatmentEvent;
		ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
		endTreatmentEvent = new EndTreatmentEvent(getParent(), time, this.spa, nextPatient);
		scheduler.postEvent(endTreatmentEvent);
	}
	
	private Patient findNextPatient() {
		Patient nextPatient = null;
		if (cheatWorks()) {
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
	
	private Boolean cheatWorks() {
		double rand = Math.random() * 40d;
		if (rand >= 20d) {
			rand = Math.random() * 9d;
			if (rand >= 8d) {
				return true;
			}
		}		
		return false;
	}

}
