package patients;

import java.time.ZonedDateTime;

import spa.Treatment;

public class Cure {
    private int startCure;
    private int endCure;
    Treatment[] dailyTreatments;
    private int maxPoints;
    private int curPoints;

    // Constructors
    public Cure() {
    }

    // Starting cure
    public void start(ZonedDateTime time) {
    }

    // Print
    public String toString() {
        if (startCure != -1) {
            return "Time\n" +
                    "\tStart -> " + startCure + "\n" +
                    "\tEnd   -> " + endCure + "\n" +
                    "Points\t" + curPoints + " | " + maxPoints + "\n";
        } else {
            return "Time\n" +
                    "\tStart -> Not started yet\n" +
                    "\tEnd   -> Not started yet\n";
        }

    }
}
