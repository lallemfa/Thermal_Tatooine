package engine;

import java.time.Duration;
import java.time.ZonedDateTime;

import events.EndEvent;
import events.IEvent;
import events.IEventScheduler;

public class Engine {

    private static IEventScheduler mScheduler;
    
    public static void init(IEventScheduler scheduler) {
    	mScheduler = scheduler;
    }
    
    public static void addEvent(IEvent event) {
    	mScheduler.postEvent(event);
    }
//    public Engine(IEventScheduler scheduler) {
//        this.scheduler = scheduler;
//    }
//
//    public IEventScheduler scheduler() {
//        return scheduler;
//    }

    public static void simulateUntil(ZonedDateTime startTime, ZonedDateTime endTime) {
        ZonedDateTime currentTime = startTime;
        IEvent endEvent = new EndEvent(endTime);
        mScheduler.postEvent(endEvent);
        IEvent currentEvent = mScheduler.popNextEvent();
        while (currentEvent != null) {
            if (currentEvent.getScheduledTime().isBefore(currentTime)) {
                throw new IllegalStateException("Trying to simulate an event from the past");
            }
            currentTime = currentEvent.getScheduledTime();
            currentEvent.process();           
            if (currentEvent == endEvent) break;
            currentEvent = mScheduler.popNextEvent();
        }
    }

    public static void simulateFor(ZonedDateTime startTime, Duration duration) {
        simulateUntil(startTime, startTime.plus(duration));
    }

}
