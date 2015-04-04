package au.com.quaysystems.qrm.server;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.tiling.scheduling.ScheduleIterator;

public class RestrictedDailyIterator implements ScheduleIterator {

    private final int[] days;
    private final Calendar calendar = Calendar.getInstance();

    public RestrictedDailyIterator(int hourOfDay, int minute, int second, int day) {
        this(hourOfDay, minute, second, new int[]{day});
    }
    
    public RestrictedDailyIterator(int hourOfDay, int minute, int second, int[] days) {
        this(hourOfDay, minute, second, days, new Date());
    }

    public RestrictedDailyIterator(int hourOfDay, int minute, int second, int[] days, Date date) {

        this.days = (int[]) days.clone();
        Arrays.sort(this.days);

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        if (!calendar.getTime().before(date)) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    public Date next() {
        do {
            calendar.add(Calendar.DATE, 1);
        } while (Arrays.binarySearch(days, calendar.get(Calendar.DAY_OF_WEEK)) < 0);
        return calendar.getTime();
    }
}