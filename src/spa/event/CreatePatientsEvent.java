package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
import spa.resort.SpaResort;

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
    	int inFlowMonth = (int)this.spa.getInflowMonth(this.scheduledTime);
    	int nbrPatientWeek = this.spa.getNbPatientsOfWeek(this.scheduledTime);
    	int nbrPatientToAdd = inFlowMonth - nbrPatientWeek;
    	while (nbrPatientToAdd > 0) {
    		boolean honesty;
    		double rand = Math.random() * 100d;    		
    		honesty = (rand > 5) ? true : false;
    		
    		int startWeek = this.spa.dayToWeek(scheduledTime);
    		int startYear = scheduledTime.getYear();
    		Patient patient = new Patient(this.spa.getNewPatientId(), honesty, startYear, startWeek);
    		Logger.Information(getParent(), "Process", "Patient " + patient.getId() + " created");
    		this.spa.addPatient(patient);
    		patient.initEvents(scheduler, this.spa);
    	}
    }
}
