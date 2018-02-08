package engine.event;

import java.time.ZonedDateTime;

import logger.IRecordableWrapper;
import logger.LoggerWrap;

public class MessageEvent extends Event implements IEvent {
	
	private final ZonedDateTime scheduledTime;
	private final String message;

	public MessageEvent(Object parent, ZonedDateTime eventTime, String message) {
		super(parent);
		this.scheduledTime = eventTime;
		this.message = message;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
		
	public void process(IEventScheduler scheduler) {
		LoggerWrap.Log((IRecordableWrapper) getParent(), message);
	}

}
