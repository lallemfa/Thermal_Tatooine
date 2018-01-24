package engine.event;

import java.time.ZonedDateTime;

public class EndEvent implements IEvent {

	private final ZonedDateTime scheduledTime;
	
	public EndEvent(ZonedDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process() {}
	
}
