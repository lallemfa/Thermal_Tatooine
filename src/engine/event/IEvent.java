package engine.event;

import java.time.ZonedDateTime;

public interface IEvent extends Comparable<IEvent> {
	
	ZonedDateTime getScheduledTime();

	void process(IEventScheduler scheduler);
	
	@Override
	default int compareTo(IEvent other) {
		return this.getScheduledTime().compareTo(other.getScheduledTime());
	}
	
}
