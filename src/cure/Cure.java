package cure;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cure.Appointment;
import spa.Treatment;

public class Cure {

    private final double PROB_NB_TREATMENTS[] = {0.2, 0.35, 0.3, 0.15};
    private final double PROB_4_TREATMENTS = 0.35;
    private final double PROB_5_TREATMENTS = 0.3;
    private final double PROB_6_TREATMENTS = 0.15;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private List<Treatment> dailyTreatments;
    private List<Appointment> appointments;
    private int maxPointsPerDay;
    private int maxPoints;
    private int currentPoints;

    public Cure(ZonedDateTime start) {
        currentPoints = 0;
        maxPointsPerDay = 0;
        setTreatments();
        setAppointments();
        maxPoints = maxPointsPerDay * 5 * 3 * 3;
        // TODO: create events for patient arriving
        // TODO: calculate startDate/endDate
    }

    private void setTreatments() {
        List<Treatment> allTreatments = new ArrayList<>(Arrays.asList(Treatment.values()));
        int nbTreatments = getNbDailyTreatments();
        this.dailyTreatments = new ArrayList<>();
        for (int i = 0; i < nbTreatments; i++) {
            int randomIndex = (int) Math.floor(allTreatments.size() * Math.random());
            Treatment treatment = allTreatments.remove(randomIndex);
            this.dailyTreatments.add(treatment);
            allTreatments.removeIf(t -> t.type == treatment.type);
            maxPointsPerDay += treatment.getMaxPoints();
        }
    }

    private void setAppointments() {
        // TODO: set appointments for treatments
    }

    private int getNbDailyTreatments() {
        double random = Math.random();
        for (int i = 0; i < PROB_NB_TREATMENTS.length; i++) {
            if (random < PROB_NB_TREATMENTS[i]) {
                return 3 + i;
            }
            random -= PROB_NB_TREATMENTS[i];
        }
        return 0;
    }

    public List<Treatment> getDailyTreatments() {
        return dailyTreatments;
    }

    public void addPoints(int points) {
        this.currentPoints += points;
    }

    public int getPoints() {
        return this.currentPoints;
    }

    public String toString() {
        if (startDate != null) {
            return "Time\n" +
                "\tStart -> " + startDate.toLocalDateTime() + "\n" +
                "\tEnd   -> " + endDate.toLocalDateTime() + "\n" +
                "Points\t" + currentPoints + " / " + maxPoints + "\n";
        } else {
            return "Time\n" +
                "\tStart -> Not started yet\n" +
                "\tEnd   -> Not started yet\n";
        }
    }
}
