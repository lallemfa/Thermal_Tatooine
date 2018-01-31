package spa.event;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.person.Patient;
import spa.resort.SpaResort;

import java.time.ZonedDateTime;

public class CreatePatientsEvent extends Event implements IEvent {

    private ZonedDateTime scheduledTime;
    private SpaResort spa;

    public CreatePatientsEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa) {
        super(parent);
        this.scheduledTime = scheduledTime;
        this.spa = spa;
    }

    @Override
    public ZonedDateTime getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public void process(IEventScheduler scheduler) {
        // TODO: create patients
    }
}
