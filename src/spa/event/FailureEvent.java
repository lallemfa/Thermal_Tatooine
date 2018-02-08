package spa.event;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
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
		this.treatment.setBrokenState(true);
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Failure of treatment: " + this.treatment.name);
	}
}