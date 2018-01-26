package engine;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.EndEvent;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.simulation.core.ISimulationDateProvider;

public class Engine implements ISimulationDateProvider {

    private IEventScheduler scheduler;
    private ZonedDateTime currentTime;
    
    public Engine(IEventScheduler scheduler) {
        this.scheduler 		= scheduler;
        this.currentTime 	= null;
    }
    
    public ZonedDateTime getCurrentTime() {
        return currentTime;
    }

    public IEventScheduler getScheduler() {
        return scheduler;
    }

    public void simulateUntil(ZonedDateTime startTime, ZonedDateTime endTime) {
        currentTime = startTime;
        IEvent endEvent = new EndEvent(this, endTime);
        scheduler.postEvent(endEvent);
        IEvent currentEvent = scheduler.popNextEvent();
        while (currentEvent != null) {
            if (currentEvent.getScheduledTime().isBefore(currentTime)) {
                throw new IllegalStateException("Trying to simulate an event from the past");
            }
            currentTime = currentEvent.getScheduledTime();
            currentEvent.process(scheduler);
            if (currentEvent == endEvent) break;
            currentEvent = scheduler.popNextEvent();
        }
    }

    public void simulateFor(ZonedDateTime startTime, Duration duration) {
        simulateUntil(startTime, startTime.plus(duration));
    }

	@Override
	public LogicalDateTime SimulationDate() {
		LogicalDateTime time = new LogicalDateTime(getCurrentTime().toLocalDateTime());
		return time;
	}

}
