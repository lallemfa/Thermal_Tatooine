package spa.resort;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

public interface ISpaResort {
	
	List<Month> getOpeningMonths();
	List<DayOfWeek> getOpeningDays();
	
	LocalTime getOpeningHour(ZonedDateTime time);
	LocalTime getClosingHour(ZonedDateTime time);
	
	float[] getInflowMonth();
	
	boolean isOpen(ZonedDateTime time);
	
	void initEvents(ZonedDateTime startTime, ZonedDateTime endTime);
	
}
