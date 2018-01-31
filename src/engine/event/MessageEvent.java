package engine.event;

import java.time.ZonedDateTime;

import enstabretagne.base.logger.Logger;
import logger.LogType;
import logger.NoJokeItIsTheBestOneSoFarLogger;

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
		NoJokeItIsTheBestOneSoFarLogger.log(LogType.INFO, scheduledTime, message);
		Logger.Information(getParent(), "Process", message);
	}

}
