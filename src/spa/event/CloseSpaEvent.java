package spa.event;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class CloseSpaEvent extends Event implements IEvent {

    private final ZonedDateTime scheduledTime;
    private final SpaResort spa;
    
    public CloseSpaEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa) {
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
		List<Patient> patients = spa.getPatients().stream()
				.filter(p -> p.getPersonState() == PersonState.WaitingQueue || p.getPersonState() == PersonState.Moving)
				.collect(Collectors.toList());
		for (Patient patient : patients) {
			patient.getTreatment().removeWaitingQueuePatient(patient);
			patient.setTreatment(null);
			patient.setPersonState(PersonState.Out);
			IEvent leaveEvent = new LeaveSpaEvent(patient, this.scheduledTime, this.spa, patient);
			scheduler.postEvent(leaveEvent);
		}
		for (Treatment treatment : this.spa.getTreatments()) {
			LoggerWrap.Log((IRecordableWrapper) treatment, "Spa closes");
			treatment.setDurationWaitedPerDay(0);
			treatment.setPatientsCuredPerDay(0);
		}
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Spa closes");
	}
}
