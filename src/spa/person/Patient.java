package spa.person;

import java.time.ZonedDateTime;

import spa.cure.Cure;

public class Patient extends Person {

    boolean patient = true;
    private boolean isFair;

    private Cure cure;
    private ZonedDateTime maxArrivingTime; // TODO

    private ZonedDateTime startTreatment; // TODO

    public Patient(int id) {
        this(id, true);
    }

    public Patient(int id, boolean isFair) {
        this.id = id;
        this.isFair = isFair;
        // TODO: compute random time for cure start
        this.cure = new Cure(ZonedDateTime.now());
    }

    public Cure getCure() {
        return cure;
    }

    public ZonedDateTime getStartTreatment() {
        return startTreatment;
    }

    public void addCurePoints(int points) {
        this.cure.addPoints(points);
    }

    public String toString() {
        return "___________________________\n" +
            "Patient ID :\t" + this.id + "\n" +
            "Honesty :\t" + this.isFair + "\n" +
            "\n\tCure to do :\n" + this.cure.toString() + "\n" +
            "___________________________";
    }
}
