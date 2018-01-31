package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;

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
		Logger.Information(getParent(), "Process", "Spa opened");
	}

}
