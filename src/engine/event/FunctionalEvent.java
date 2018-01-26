package engine.event;

import java.time.ZonedDateTime;

public class FunctionalEvent<T> extends Event implements IEvent {

    private final ZonedDateTime scheduledTime;
    private final IEventAction action;


    public FunctionalEvent(Object parent, ZonedDateTime scheduledTime, IEventAction action) {
    	super(parent);
        this.scheduledTime 	= scheduledTime;
        this.action 		= action;
    }

    @Override
    public ZonedDateTime getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public void process(IEventScheduler scheduler) {
        action.run();
    }
    
    @Override
    public String toString() {
        return "Event created by : " + getParent().getClass().getName();
    }

}
