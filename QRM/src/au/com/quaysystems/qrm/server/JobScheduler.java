package au.com.quaysystems.qrm.server;
import java.util.ArrayList;

import org.tiling.scheduling.Scheduler;
import org.tiling.scheduling.SchedulerTask;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;
public class JobScheduler {
	private final Scheduler scheduler = new Scheduler();
	private int hourOfDay, minute;
	private int[] days;
	public  final Long jobID;
	private ModelScheduledJob job;
	
	public JobScheduler(ModelScheduledJob job) {
		this.hourOfDay = Integer.parseInt(job.timeStr.split(":")[0]);
		this.minute = Integer.parseInt(job.timeStr.split(":")[1]);
		ArrayList<Integer> dayList = new ArrayList<Integer>(); 
		if (job.Mon) dayList.add(1);
		if (job.Tue) dayList.add(2);
		if (job.Wed) dayList.add(3);
		if (job.Thu) dayList.add(4);
		if (job.Fri) dayList.add(5);
		if (job.Sat) dayList.add(6);
		if (job.Sun) dayList.add(7);
		
		this.days = new int[dayList.size()];
		
		int  idx = 0;
		for(Integer i:dayList){
			this.days[idx++] = i;
		}
		this.job = job;
		this.jobID = job.internalID;
	}
	public void cancel(){
		scheduler.cancel();
	}
	public  ModelScheduledJob getJobDetail(){
		return this.job;
	}
	public void start() {
		scheduler.schedule(new SchedulerTask() {
			public void run() {
				runJob();
			}
			private void runJob() {
				JobController.processReportJob(job);
			}
		}, new RestrictedDailyIterator(hourOfDay, minute, 0, days));
	}
}