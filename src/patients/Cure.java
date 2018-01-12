package patients;

public class Cure {
    private int startCure;
    private int endCure;
    // Treatment[] dueTreatments
    private int maxPoints;
    private int curPoints;

    // Constructors
    public Cure() {
        startCure = -1;
        endCure = -1;

        maxPoints = 0;
        curPoints = 0;
    }

    // Starting cure
    public void start(int time) {
        startCure = time;
        endCure = time + 3;
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
