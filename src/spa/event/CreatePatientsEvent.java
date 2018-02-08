package spa.event;

import java.time.ZonedDateTime;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

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
    	
    	ZonedDateTime time = this.spa.nextOpenDay(this.scheduledTime);
    	int inFlowMonth = (int)this.spa.getInflowMonth(time);
    	int nbrPatientWeek = this.spa.getNbPatientsOfWeek(time);
    	int nbrPatientToAdd = inFlowMonth - nbrPatientWeek;

    	while (nbrPatientToAdd > 0) {
    		boolean honesty;
    		double rand = Math.random() * 100d;    		
    		honesty = rand > 5;
    		
    		int startWeek = this.spa.dayToWeek(time);
    		int startYear = time.getYear();

    		Patient patient = new Patient(this.spa.getNewPatientId(), honesty, startYear, startWeek);
            List<Treatment> dailyTreatments = patient.getCure().getDailyTreatments();
    		LoggerWrap.Log(patient, "Patient " + patient.getId() + " created");
    		StringBuilder sb = new StringBuilder("Patient " + patient.getId() + " has treatments: ");
            for (Treatment treatment : dailyTreatments) {
                sb.append(treatment.name);
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length() - 1);
            LoggerWrap.Log(patient, sb.toString());

    		this.spa.addPatient(patient);
    		patient.initEvents(scheduler, this.spa);
            nbrPatientToAdd--;
    	}
    }
}
