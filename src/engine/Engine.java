package engine;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Engine {

    private final IEventScheduler scheduler;
    private ZonedDateTime currentTime;

    public Engine(IEventScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public IEventScheduler scheduler() {
        return scheduler;
    }

    public void simulateUntil(ZonedDateTime startTime, ZonedDateTime endTime) {
        currentTime = startTime;
        IEvent endEvent = new EndEvent(endTime);
        scheduler.postEvent(endEvent);
        IEvent currentEvent = scheduler.popNextEvent();
        while (currentEvent != null) {
            if (currentEvent.getScheduledTime().isBefore(currentTime)) {
                throw new IllegalStateException("Trying to simulate an event from the past");
            }
            currentTime = currentEvent.getScheduledTime();
            if (currentEvent == endEvent) break;
            currentEvent.process();
            currentEvent = scheduler.popNextEvent();
        }
    }

    public void simulateFor(ZonedDateTime startTime, Duration duration) {
        simulateUntil(startTime, startTime.plus(duration));
    }

}
