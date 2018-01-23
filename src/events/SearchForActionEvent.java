package events;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import person.Patient;
import person.PersonState;
import spa.SpaResort;
import spa.Treatment;

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
		Treatment choosenTreatment = selectNextTreatment();
		// TODO add event arrive in treatment with good time
	}
	
	private Treatment selectNextTreatment() {
		List<Treatment> dailyTreatments = this.patient.getCure().getDailyTreatments();
		PersonState state = this.patient.getState();
		Treatment choosenTreatment = null;
		
		if (state == PersonState.Treatment) {
			Treatment treatment = this.patient.getTreatment();
			Duration durationMoving = Duration.ofMinutes(100);
			
			for(int i=0; i < dailyTreatments.size(); i++) {
				Treatment tempTreatment = dailyTreatments.remove(dailyTreatments.size()-1);
				Duration duration = this.spa.distanceBetween(treatment, tempTreatment);
				if (duration.compareTo(durationMoving) < 0) {
					choosenTreatment = tempTreatment;
				}
			}
		} else {
			// TODO rest zone + corridors
		}
		return choosenTreatment;
	}
}
