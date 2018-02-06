package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;

public class OpenSpaEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;

	public OpenSpaEvent(Object parent, ZonedDateTime scheduledTime) {
		super(parent);
		this.scheduledTime = scheduledTime;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override

	public void process(IEventScheduler scheduler) {
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Spa opens");
	}

}
