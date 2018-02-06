package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.person.PersonState;
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
		if (patient.getPersonState() == PersonState.Out) {
			System.out.println("Escaped arrived treatment for patient: " + patient.getId());
			return;
		}

		boolean availableWaitingQueue = (this.treatment.getWaitingQueue().size() < this.treatment.getMaxPatientsWaiting());
		boolean availableWork = (this.treatment.getCurrentPatients().size() < this.treatment.getMaxPatientsWorking());
		if (availableWork) {
			this.patient.setPersonState(PersonState.Treatment);
			this.patient.setTreatment(this.treatment);
			this.treatment.addCurrentPatients(this.patient);
			this.patient.setStartTreatment(this.scheduledTime);
			LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + this.patient.getId() + " starts " + this.treatment.name);
			IEvent endTreatmentEvent;
			ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
			if (this.spa.getClosingHour(this.scheduledTime).isBefore(this.scheduledTime.plus(this.treatment.getDuration()).toLocalTime())) {
				time = time.with(this.spa.getClosingHour(this.scheduledTime));
			}
			endTreatmentEvent = new EndTreatmentEvent(getParent(), time, this.spa, this.patient);
			this.patient.nextEndTreatment = endTreatmentEvent;
			scheduler.postEvent(endTreatmentEvent);
		} else if (availableWaitingQueue) {
			this.patient.setPersonState(PersonState.WaitingQueue);
			this.patient.setTreatment(this.treatment);
			this.treatment.addWaitingQueuePatient(this.patient);
			this.patient.setStartWaiting(this.scheduledTime);
			LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + this.patient.getId() + " starts waiting");
		} else {
			IEvent searchEvent;
			searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(searchEvent);
		}
	}

}
