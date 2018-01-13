package spa;

import java.time.DayOfWeek;
import java.time.Duration;
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
	float getInflowMonth(Month month);
	float getInflowMonth(ZonedDateTime time);
	
	Duration distanceBetween(Treatment treatment1, Treatment treatment2);
	
	boolean isOpen(ZonedDateTime time);
	
	void initEvents(ZonedDateTime startTime, ZonedDateTime endTime);
	
}
