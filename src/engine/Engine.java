package engine;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.EndEvent;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.simulation.core.ISimulationDateProvider;

public class Engine implements ISimulationDateProvider {

    private static IEventScheduler mScheduler;
    private static ZonedDateTime mCurrentTime;
    
    public static void init(IEventScheduler scheduler) {
    	mScheduler = scheduler;
    }
    
    public static void addEvent(IEvent event) {
    	mScheduler.postEvent(event);
    }
    
    public static ZonedDateTime getCurrentTime() {
    	return mCurrentTime;
    }

    public static void simulateUntil(ZonedDateTime startTime, ZonedDateTime endTime) {
        mCurrentTime = startTime;
        IEvent endEvent = new EndEvent(endTime);
        mScheduler.postEvent(endEvent);
        IEvent currentEvent = mScheduler.popNextEvent();
        while (currentEvent != null) {
            if (currentEvent.getScheduledTime().isBefore(mCurrentTime)) {
                throw new IllegalStateException("Trying to simulate an event from the past");
            }
            mCurrentTime = currentEvent.getScheduledTime();
            currentEvent.process();           
            if (currentEvent == endEvent) break;
            currentEvent = mScheduler.popNextEvent();
        }
    }

    public static void simulateFor(ZonedDateTime startTime, Duration duration) {
        simulateUntil(startTime, startTime.plus(duration));
    }

	@Override
	public LogicalDateTime SimulationDate() {
		return new LogicalDateTime(mCurrentTime.toLocalDateTime());
	}

}
