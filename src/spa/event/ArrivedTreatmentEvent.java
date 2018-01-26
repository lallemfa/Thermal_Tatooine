package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class ArrivedTreatmentEvent extends Event implements IEvent {
	
	private ZonedDateTime scheduledTime;
	private Patient patient;
	private Treatment treatment;
	private SpaResort spa;

	public ArrivedTreatmentEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Treatment treatment, Patient patient) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.patient = patient;
        this.treatment = treatment;
        this.spa = spa;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		boolean availableWaitingQueue = (this.treatment.getWaitingQueue().size() < this.treatment.getMaxPatientsWaiting());
		boolean availableWork = (this.treatment.getCurrentPatients().size() < this.treatment.getMaxPatientsWorking());
		if (availableWork) {
			this.treatment.addCurrentPatients(this.patient);
			this.patient.setStartTreatment(this.scheduledTime);
			IEvent endTreatmentEvent;
			ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
			endTreatmentEvent = new EndTreatmentEvent(getParent(), time, this.spa, this.patient);
			scheduler.postEvent(endTreatmentEvent);
		} else if (availableWaitingQueue) {
			// TODO Put in waiting list and start wating time in patient
		} else {
			IEvent searchEvent;
			searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(searchEvent);
		}
	}

}