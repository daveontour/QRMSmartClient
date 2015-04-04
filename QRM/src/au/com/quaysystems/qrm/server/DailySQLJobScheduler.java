package au.com.quaysystems.qrm.server;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.tiling.scheduling.Scheduler;
import org.tiling.scheduling.SchedulerTask;
public class DailySQLJobScheduler {
	private final Scheduler scheduler = new Scheduler();
	private int hourOfDay, minute;
	private int[] days = {1,2,3,4,5,6,7};
	private String sql;
	private String url;
	
	public DailySQLJobScheduler(int hour, int minute, String sql, String url) {
		this.hourOfDay = hour;
		this.minute = minute;
		this.sql = sql;
		this.url = url;

	}
	public void cancel(){
		scheduler.cancel();
	}
	public void start() {
		scheduler.schedule(new SchedulerTask() {
			public void run() {
				runJob();
			}
			private void runJob() {
				try {
					Connection conn = PersistenceUtil.getConnection(url);
					conn.createStatement().executeUpdate(sql);
					conn.close();
				} catch (SQLException e) {
					Logger.getLogger("au.com.quaysystems.qrm").error(e);
				}
			}
		}, new RestrictedDailyIterator(hourOfDay, minute, 0, days));
	}
}