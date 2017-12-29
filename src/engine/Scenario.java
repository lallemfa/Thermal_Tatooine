package engine;

public class Scenario {

	private String comment;
	
	// Constructors
	public Scenario()
	{
		comment = "Scenario without comment";
	}
	
	public Scenario(String remark)
	{
		comment = remark;
	}
	
	// Print
	public String toString()
	{
		return comment;
	}
}
