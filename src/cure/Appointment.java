package cure;

import person.Patient;
import spa.Treatment;

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
