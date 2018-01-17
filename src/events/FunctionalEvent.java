package events;

import java.time.ZonedDateTime;

public class FunctionalEvent<T> implements IEvent {

    private final T owner;
    private final ZonedDateTime scheduledTime;
    private final IEventAction action;


    public FunctionalEvent(T owner, ZonedDateTime scheduledTime, IEventAction action) {
        this.owner = owner;
        this.scheduledTime = scheduledTime;
        this.action = action;
    }

    @Override
    public ZonedDateTime getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public void process() {
        action.run();
    }
    
    @Override
    public String toString() {
        return "Event created by : " + owner.toString();
    }



}
