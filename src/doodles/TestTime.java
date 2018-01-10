package doodles;

import java.time.LocalDateTime;

public class TestTime {

	public static void main(String[] args)
	{
		
		LocalDateTime now 		= LocalDateTime.now();
		LocalDateTime eventTime = now.plusMinutes(10);
		
		for(int i = 0; i <= 15; i++)
		{
			now = now.plusMinutes(1);
			
			if(now.compareTo(eventTime) == 0)
			{
				System.out.println("An event occured !!!");
			} else {
				System.out.println("It is " + now + " and all is quiet.");
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("\n\n\n");
		
		Scenario[] scenarios = {new Scenario(), new Scenario("Less patients"), new Scenario()};
		
		SimTime simTime = new SimTime("01/01/2025 15:04", "01/01/2030 15:04", scenarios);
		
		System.out.println(simTime);
	}

}
