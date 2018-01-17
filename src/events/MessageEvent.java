package events;

import java.time.ZonedDateTime;

import logger.LogType;
import logger.Logger;

public class MessageEvent implements IEvent {
	
	private final ZonedDateTime eventTime;
	private final String message;

	public MessageEvent(ZonedDateTime eventTime, String message) {
		this.eventTime = eventTime;
		this.message = message;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return eventTime;
	}

	@Override
	public void process() {
		Logger.log(LogType.INFO, eventTime, message);
	}

}
