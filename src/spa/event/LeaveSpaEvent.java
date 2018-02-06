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

public class LeaveSpaEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Patient patient;

	public LeaveSpaEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.patient = patient;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + this.patient.getId() + " leaves Spa");
		this.patient.setPersonState(PersonState.Out);
	}
}
