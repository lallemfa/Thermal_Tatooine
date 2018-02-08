package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.treatment.Treatment;

public class RepairEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Treatment treatment;

	public RepairEvent(Object parent, ZonedDateTime scheduledTime, Treatment treatment) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.treatment = treatment;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		this.treatment.setBrokenState(false);
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Repair of treatment: " + this.treatment.name);
	}
}
