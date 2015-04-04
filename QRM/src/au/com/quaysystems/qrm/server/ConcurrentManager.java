package au.com.quaysystems.qrm.server;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelJobMonteData;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.server.montecarlo.MonteCarloProcessor;
import au.com.quaysystems.qrm.server.servlet.exp.ReportProcessor;

import com.thoughtworks.xstream.XStream;

@WebListener
public class ConcurrentManager implements ServletContextListener {

	private static final Properties configProp = new Properties();
	public static String QRMLocalBroker = null;
	public static ReportProcessor repProcessor;
	public static MonteCarloProcessor monteProcessor;
	public static QRMMailProcessor mailProcessor;

	public static WorkQueue reportQueue;
	public static WorkQueue monteQueue;
	public static WorkQueue emailQueue;

	static long delay = 10000;   // delay for 5 sec.
	static long ConnectionTestingPeriod = 10000;  // repeat every sec.


	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	protected static XStream xsXML = new XStream();

	public ConcurrentManager(){	}


	@SuppressWarnings("unchecked")
	public void contextInitialized(final ServletContextEvent se) {

		try {
			configProp.load(new FileInputStream(se.getServletContext().getRealPath("/QRM.properties")));
			configProp.put("REPORT_PATH", se.getServletContext().getRealPath("/reports").replace("\\", "/")+"\\");

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


		PersistenceUtil.setInput(configProp, true);

		String reportThreads = configProp.getProperty("NUM_REPORT_THREADS");
		if (reportThreads == null){
			reportThreads = QRMConstants.NUM_REPORT_THREADS;
		}
		String monteThreads = configProp.getProperty("NUM_MONTE_THREADS");
		if (monteThreads == null){
			monteThreads = QRMConstants.NUM_MONTE_THREADS;
		}
		String emailThreads = configProp.getProperty("NUM_EMAIL_THREADS");
		if (emailThreads == null){
			emailThreads = QRMConstants.NUM_EMAIL_THREADS;;
		}

		repProcessor = new ReportProcessor(configProp);
		monteProcessor = new MonteCarloProcessor();
		mailProcessor = new QRMMailProcessor(configProp);

		// This defines the queue which various jobs are submitted to
		// The queue is configured with the defined number of threads to 
		// execute the jobs as they appear on the queue

		reportQueue = new WorkQueue(Integer.parseInt(reportThreads));
		monteQueue = new WorkQueue(Integer.parseInt(monteThreads));
		emailQueue = new WorkQueue(Integer.parseInt(emailThreads));

		//Connect to the broker
		//This will fail initially, which will kick of a reconnect


				// Re queues the unprocessed monte carlo jobs
		
				try {
					for (ModelRepository rep:getRepositories()){
		
						Session sess = PersistenceUtil.getSession(rep.url);
			
						List<ModelJobMonteData> jobsData = (List<ModelJobMonteData>)sess.createCriteria(ModelJobMonteData.class).add(Restrictions.eq("processed", false)).list();
						for ( ModelJobMonteData jobData:jobsData){
		
							sess.beginTransaction();
							ModelJobQueue job = (ModelJobQueue)sess.get(ModelJobQueue.class, jobData.getJobID());
							job.setReadyToExecute(true);
							job.setState("Queued");
							sess.save(job);
							sess.getTransaction().commit();
		
		
							Map<String, Object> data = new HashMap<String, Object>();
							data.put("MonteJob", xsXML.toXML(jobData));
							data.put("JobQueue",  xsXML.toXML(job));
		
							try {
								QRMAsyncMessage message = new QRMAsyncMessage(QRMConstants.MONTE_MSG, data);
								message.send();
							} catch (Exception e) {
								log.error("QRM Stack Trace", e);
							}				
						}
		
						sess.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	private static List<ModelRepository> getRepositories(){
		Session sess = PersistenceUtil.getSimpleControlSession();
		@SuppressWarnings("unchecked")
		List<ModelRepository> reps = (List<ModelRepository>)sess.createSQLQuery("SELECT * FROM repositories")
		.addEntity(ModelRepository.class).list();
		sess.close();
		return reps;
	}


	public void contextDestroyed(final ServletContextEvent arg0) {}
}
