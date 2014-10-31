package GraphDrawing;

public class Time {

	private static final int MINS = 0;
	private static final int HOURS = 22;
	private static final int DAYS = 20;
	private static final int MONTHS = 1;
	private static final int YEARS = 2012;

	public static int[] getTime(int minutes) {
		int hours = HOURS;
		int mins = MINS;
		int year = YEARS;
		int days = DAYS;
		int month = MONTHS;
		while(minutes >= 60) {
			minutes -= 60;
			hours++;
		}
		mins = minutes;
		while(hours >= 24) {
			hours -= 24;
			days++;
		}
		while(days > getDays(month)) {
			days -= getDays(month);
			month++;
		}
		while(month > 12) {
			month -= 12;
			year++;
		}
		int[] date = {mins, hours, days, month, year};
		return date;
	}

	private static int getDays(int month) {
		if(month == 2)
			return 28;
		if(month <= 7) {
			if(month % 2 == 0)
				return 30;
			else
				return 31;
		} else {
			if(month % 2 == 0)
				return 31;
		}
		return 30;
	}
}
