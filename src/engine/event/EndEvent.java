package engine.event;

import java.time.ZonedDateTime;

public class EndEvent extends Event implements IEvent {

	
	private final ZonedDateTime scheduledTime;
	
	public EndEvent(Object parent, ZonedDateTime scheduledTime) {
		super(parent);
		this.scheduledTime 	= scheduledTime;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {}

	
}
