package events;

import java.time.ZonedDateTime;

public class EndEvent implements IEvent {

	private final ZonedDateTime endTime;
	
	public EndEvent(ZonedDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return endTime;
	}

	@Override
	public void process() {}
	
}
