package person;

public class PatientsTest {

	public static void main(String[] args) {
		System.out.println("\t\tTEST ABOUT PATIENTS' INSTANCIATION");
		
		Patient ba = new Patient(0);
		Patient bi = new Patient(1, true);
		Patient bo = new Patient(2, false);
		
		System.out.println(ba);
		System.out.println(bi);
		System.out.println(bo);
		
		ba.startingCure();
		System.out.println(ba);
	}

}
