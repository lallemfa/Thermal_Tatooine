package engine.event;

import java.time.ZonedDateTime;

import logger.LogType;
import logger.Logger;

public class MessageEvent implements IEvent {
	
	private final ZonedDateTime scheduledTime;
	private final String message;

	public MessageEvent(ZonedDateTime eventTime, String message) {
		this.scheduledTime = eventTime;
		this.message = message;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process() {
		Logger.log(LogType.INFO, scheduledTime, message);
	}

}
