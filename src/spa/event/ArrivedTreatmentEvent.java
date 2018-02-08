package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
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
			return;
		}

		this.patient.setTreatment(this.treatment);
		boolean availableWaitingQueue = this.treatment.getWaitingQueue().size() < this.treatment.getMaxPatientsWaiting();
		boolean availableWork = this.treatment.getCurrentPatients().size() < this.treatment.getMaxPatientsWorking();
		if (availableWork) {
			this.patient.setPersonState(PersonState.Treatment);
			this.treatment.addCurrentPatients(this.patient);
			this.patient.setStartTreatment(this.scheduledTime);
			LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " starts " + this.treatment.name);
			ZonedDateTime time = this.scheduledTime.plus(this.treatment.getDuration());
			if (this.spa.getClosingHour(this.scheduledTime).isBefore(this.scheduledTime.plus(this.treatment.getDuration()).toLocalTime())) {
				time = time.with(this.spa.getClosingHour(this.scheduledTime));
			}
			IEvent endTreatmentEvent = new EndTreatmentEvent(this.patient, time, this.spa, this.patient);
			this.patient.nextEndTreatment = endTreatmentEvent;
			scheduler.postEvent(endTreatmentEvent);
		} else if (availableWaitingQueue) {
			this.patient.setPersonState(PersonState.WaitingQueue);
			this.treatment.addWaitingQueuePatient(this.patient);
			this.patient.setStartWaiting(this.scheduledTime);
			LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " starts waiting for " + this.treatment.name);
		} else {
            this.patient.nbRecursiveSearch++;
            if (this.patient.nbRecursiveSearch > 5) {
                this.patient.nbRecursiveSearch = 0;
                this.patient.setPersonState(PersonState.Rest);
                if (this.scheduledTime.plusHours(2).toLocalTime().isAfter(this.spa.getClosingHour(this.scheduledTime))) {
                    IEvent leaveEvent = new LeaveSpaEvent(this.patient, this.scheduledTime, this.spa, this.patient);
                    scheduler.postEvent(leaveEvent);
                } else {
                    LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " rests");
                    IEvent searchEvent = new SearchForActionEvent(this.patient, this.scheduledTime.plusHours(2), this.spa, this.patient);
                    scheduler.postEvent(searchEvent);
                    this.patient.nextMovingEvent = searchEvent;
                }
            } else {
                LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " arrived at " + this.treatment.name + " but no room left");
                IEvent searchEvent = new SearchForActionEvent(this.patient, this.scheduledTime, this.spa, this.patient);
                scheduler.postEvent(searchEvent);
                this.patient.nextMovingEvent = searchEvent;
            }
		}
	}

}
