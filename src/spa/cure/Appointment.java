package spa.cure;

import spa.person.Patient;
import spa.treatment.Treatment;

import java.time.ZonedDateTime;

public class Appointment {

    private Treatment treatment;
    private Patient patient;
    private ZonedDateTime time;

    public Appointment(Treatment treatment, Patient patient, ZonedDateTime time) {
        this.treatment = treatment;
        this.patient = patient;
        this.time = time;
    }

}
