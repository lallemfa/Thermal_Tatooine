package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import engine.event.IEvent;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class SearchForActionEvent implements IEvent {

	private ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;

	public SearchForActionEvent(ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
        this.scheduledTime = scheduledTime;
        this.patient = patient;
        this.spa = spa;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process() {
		Duration duration;
		PersonState state = this.patient.getState();
		Treatment choosenTreatment = selectNextTreatment(state);
		if (state != PersonState.Treatment) {
			duration = this.spa.getMaxDistanceDuration();
		}
		// TODO rest zone + corridors
		// TODO add event arrive in treatment with good time
	}
	
	private Treatment selectNextTreatment(PersonState state) {
		List<Treatment> dailyTreatments = this.patient.getCure().getDailyTreatments();
		
		Treatment choosenTreatment = null;
		Duration durationMoving = Duration.ofMinutes(100);
		
		for(int i=0; i < dailyTreatments.size(); i++) {
			Treatment tempTreatment = dailyTreatments.remove(dailyTreatments.size()-1);
			if (!tempTreatment.isWithAppointment() && state != PersonState.Treatment) {
				return tempTreatment;
			}
			if (!tempTreatment.isWithAppointment() && state == PersonState.Treatment) {
				Treatment treatment = this.patient.getTreatment();
				Duration duration = this.spa.distanceBetween(treatment, tempTreatment);
				if (duration.compareTo(durationMoving) < 0) {
					choosenTreatment = tempTreatment;
				}
			}
		}
		return choosenTreatment;
	}
}
