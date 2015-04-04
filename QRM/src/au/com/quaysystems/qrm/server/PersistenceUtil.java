package au.com.quaysystems.qrm.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import au.com.quaysystems.qrm.dto.DTOMetricElement;
import au.com.quaysystems.qrm.dto.DTOObjectiveImpacted;
import au.com.quaysystems.qrm.dto.ModelAnalConfig;
import au.com.quaysystems.qrm.dto.ModelAuditComment;
import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelEmailRecord;
import au.com.quaysystems.qrm.dto.ModelImportTemplate;
import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentConsequence;
import au.com.quaysystems.qrm.dto.ModelIncidentObjective;
import au.com.quaysystems.qrm.dto.ModelIncidentRisk;
import au.com.quaysystems.qrm.dto.ModelIncidentUpdate;
import au.com.quaysystems.qrm.dto.ModelJobData;
import au.com.quaysystems.qrm.dto.ModelJobMonteData;
import au.com.quaysystems.qrm.dto.ModelJobMonteResult;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelJobQueueMgr;
import au.com.quaysystems.qrm.dto.ModelJobReportData;
import au.com.quaysystems.qrm.dto.ModelJobResult;
import au.com.quaysystems.qrm.dto.ModelMetric;
import au.com.quaysystems.qrm.dto.ModelMetricProject;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMitigationStepNoPerson;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelMultiLevelNoGen;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelObjectiveNoGen;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelRiskLiteExt;
import au.com.quaysystems.qrm.dto.ModelRiskLiteRelationship;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.dto.ModelToleranceDescriptors;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;
import au.com.quaysystems.qrm.dto.ModelWelcomeData;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class PersistenceUtil {

	private static final HashMap<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	private static SessionFactory qrmloginSF;

	private static Configuration repConfig = new Configuration()
	.setProperty("hibernate.c3p0.min_size", "1")
	.setProperty("hibernate.c3p0.max_size", "10")
	.setProperty("hibernate.c3p0.timeout", "300")
	.setProperty("hibernate.c3p0.max_statements", "50")
	.setProperty("hibernate.cache.use_second_level_cache",	"false")
	.setProperty("hibernate.connection.autoReconnectForPools", "true")
	.setProperty("hibernate.connection.autoReconnect", "true")
	.setProperty("hibernate.show_sql", "false")
	.addAnnotatedClass(ModelRiskProject.class)
	.addAnnotatedClass(ModelPerson.class)
	.addAnnotatedClass(ModelRisk.class)
	.addAnnotatedClass(ModelRiskLite.class)
	.addAnnotatedClass(ModelRiskLiteExt.class)
	.addAnnotatedClass(ModelRiskLiteBasic.class)
	.addAnnotatedClass(ModelRiskLiteRelationship.class)
	.addAnnotatedClass(ModelMultiLevel.class)
	.addAnnotatedClass(ModelMultiLevelNoGen.class)
	.addAnnotatedClass(ModelObjective.class)
	.addAnnotatedClass(ModelObjectiveNoGen.class)
	.addAnnotatedClass(ModelToleranceMatrix.class)
	.addAnnotatedClass(ModelRiskControl.class)
	.addAnnotatedClass(ModelMitigationStep.class)
	.addAnnotatedClass(ModelMitigationStepNoPerson.class)
	.addAnnotatedClass(ModelUpdateComment.class)
	.addAnnotatedClass(ModelRiskConsequence.class)
	.addAnnotatedClass(ModelQuantImpactType.class)
	.addAnnotatedClass(ModelAnalConfig.class)
	.addAnnotatedClass(ModelQRMReport.class)
	.addAnnotatedClass(ModelDataObjectAllocation.class)
	.addAnnotatedClass(ModelAuditComment.class)
	.addAnnotatedClass(ModelJobQueue.class)
	.addAnnotatedClass(ModelJobQueueMgr.class)
	.addAnnotatedClass(ModelJobData.class)
	.addAnnotatedClass(ModelJobResult.class)
	.addAnnotatedClass(ModelJobMonteResult.class)
	.addAnnotatedClass(ModelJobMonteData.class)
	.addAnnotatedClass(ModelJobReportData.class)
	.addAnnotatedClass(ModelReview.class)
	.addAnnotatedClass(ModelIncident.class)
	.addAnnotatedClass(ModelIncidentConsequence.class)
	.addAnnotatedClass(ModelWelcomeData.class)
	.addAnnotatedClass(DTOMetricElement.class)
	.addAnnotatedClass(ModelMetric.class)
	.addAnnotatedClass(ModelMetricProject.class)
	.addAnnotatedClass(ModelToleranceDescriptors.class)
	.addAnnotatedClass(DTOObjectiveImpacted.class)
	.addAnnotatedClass(ModelRepository.class)
	.addAnnotatedClass(ModelEmailRecord.class)
	.addAnnotatedClass(ReportProcessorData.class)
	.addAnnotatedClass(ModelIncidentUpdate.class)
	.addAnnotatedClass(ModelImportTemplate.class)
	.addAnnotatedClass(ModelScheduledJob.class)
	.addAnnotatedClass(ModelIncidentRisk.class)
	.addAnnotatedClass(ModelIncidentObjective.class);

	private static Configuration loginConfig= new Configuration()
	.setProperty("hibernate.c3p0.min_size", "1")
	.setProperty("hibernate.c3p0.max_size", "5")
	.setProperty("hibernate.c3p0.timeout", "300")
	.setProperty("hibernate.c3p0.max_statements", "50")
	.setProperty("hibernate.cache.use_second_level_cache",	"false")
	.setProperty("hibernate.connection.autoReconnectForPools", "true")
	.setProperty("hibernate.connection.autoReconnect", "true")
	.addAnnotatedClass(ModelRepository.class)
	.addAnnotatedClass(ModelScheduledJob.class);
	

	private static Properties props;
	private static String hostURLRoot;
	private static String hostUser;
	private static String hostPass;
	private static String hostDriverClass;
	private static String hibernateDialect;
	private static ComboPooledDataSource qrmloginCPDS;
	private static HashMap<String, ComboPooledDataSource> dataSourceMap= new  HashMap<String, ComboPooledDataSource>();
	private static boolean setInputCalled = false;
	public static String hostAndPort;


	@SuppressWarnings("unchecked")
	public static void setInput(Properties configProp, boolean scheduleJobs){

		if (setInputCalled){
			return;
		} else {
			setInputCalled = true;
		}
		
		if (props == null){

			props = configProp;

			hostURLRoot = props.getProperty("HOSTURLROOT");
			hostUser = props.getProperty("HOSTUSER");
			hostPass = props.getProperty("HOSTUSERPASS");
			hostDriverClass = props.getProperty("HOSTDRIVERCLASS");
			hibernateDialect = props.getProperty("HIBERNATEDIALECT");
			repConfig.setProperty("hibernate.dialect",hibernateDialect);

			Matcher m = Pattern.compile("jdbc:mysql://[a-z A-Z 0-9.]*:[0-9]*/").matcher(hostURLRoot);
			m.find();
			String temp = m.group();
			hostAndPort = temp.substring(13, temp.length() - 1);

			// Create the connection pool for the QRMLogin Database
			try {
				qrmloginCPDS = new ComboPooledDataSource();
				qrmloginCPDS.setAcquireRetryAttempts(5);
				qrmloginCPDS.setDriverClass(hostDriverClass);
				qrmloginCPDS.setUser(hostUser);
				qrmloginCPDS.setPassword(hostPass);
				qrmloginCPDS.setJdbcUrl(hostURLRoot);
				qrmloginCPDS.setIdleConnectionTestPeriod(51);
				qrmloginCPDS.setMaxIdleTime(1800);
				qrmloginCPDS.setMaxConnectionAge(7200);
				qrmloginCPDS.setAcquireIncrement(1);
				qrmloginCPDS.setDebugUnreturnedConnectionStackTraces(true);
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			loginConfig = loginConfig
					.setProperty("hibernate.connection.driver_class", hostDriverClass)
					.setProperty("hibernate.connection.url", hostURLRoot)
					.setProperty("hibernate.connection.username", hostUser)
					.setProperty("hibernate.connection.password", hostPass)
					.setProperty("hibernate.dialect",hibernateDialect);
			
			qrmloginSF = loginConfig.buildSessionFactory(new ServiceRegistryBuilder().applySettings(loginConfig.getProperties()).buildServiceRegistry());


			//Initialise all the Session Factories for each repository

			Session sess = qrmloginSF.openSession();
			List<ModelRepository> reps = (List<ModelRepository>)sess.createSQLQuery("SELECT * FROM repositories").addEntity(ModelRepository.class).list();
			sess.close();

			for (ModelRepository rep:reps){
				try {
					log.info("--->Initialising repository: "+rep.url);
					getSessionFactory(rep.url);

					if (scheduleJobs){

						Session sess2 = getSession(rep.url);
						// Get all the scheduled jobs for the repository
						List<ModelScheduledJob> jobs = (List<ModelScheduledJob>)sess2.createSQLQuery("SELECT * FROM schedjob")
								.addEntity(ModelScheduledJob.class).list();

						sess2.close();

						// Schedule the jobs
						for (ModelScheduledJob job:jobs){
							JobController.addJobAndScedule( new JobScheduler(job));
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static SessionFactory getSessionFactory(final String url) {

		SessionFactory sf = sessionFactoryMap.get(url);
		if (sf != null){
			return sf;
		}

		try {
			// Create the pooled data source for the particular URL
			ComboPooledDataSource pds = new ComboPooledDataSource();
			pds.setDriverClass(hostDriverClass);
			pds.setJdbcUrl(url);
			pds.setAcquireIncrement(1);
			pds.setIdleConnectionTestPeriod(53);
			pds.setMaxIdleTime(1800);
			pds.setMaxConnectionAge(7200);
			pds.setDebugUnreturnedConnectionStackTraces(true);

			dataSourceMap.put(url,pds);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
		

		repConfig = repConfig.setProperty("hibernate.connection.driver_class", hostDriverClass)
				.setProperty("hibernate.connection.url", url)
				.setProperty("hibernate.connection.username", hostUser)
				.setProperty("hibernate.connection.password", hostPass)
				.setProperty("hibernate.dialect",hibernateDialect);

		sf = repConfig.buildSessionFactory(new ServiceRegistryBuilder().applySettings(repConfig.getProperties()).buildServiceRegistry());

		sessionFactoryMap.put(url, sf);

		return sf;
	}

	public static Session getSimpleControlSession(){
		return qrmloginSF.openSession();
	}

	public static void resetSimpleControlSessionFactory(){


		if(qrmloginSF instanceof SessionFactoryImpl) {
			SessionFactoryImpl sf = (SessionFactoryImpl)qrmloginSF;
			ConnectionProvider conn = sf.getConnectionProvider();
			if(conn instanceof C3P0ConnectionProvider) { 
				((C3P0ConnectionProvider)conn).close(); 
			}
		}
		qrmloginSF.close();

		try {
			qrmloginCPDS.softResetAllUsers();
		} catch (SQLException e) {
			qrmloginCPDS.hardReset();
		}


		qrmloginSF = loginConfig.buildSessionFactory(new ServiceRegistryBuilder().applySettings(loginConfig.getProperties()).buildServiceRegistry());

	}

	public static void resetDBConnections(){

		try {
			qrmloginCPDS.softResetAllUsers();
		} catch (SQLException e) {
			qrmloginCPDS.hardReset();
		}

		resetSimpleControlSessionFactory();

		sessionFactoryMap.clear();
		for (ComboPooledDataSource ds:dataSourceMap.values()){
			ds.close();
		}
		dataSourceMap.clear();
	}

	public static Connection getConnection(final String url){

		ComboPooledDataSource ds = null;

		try {

			ds = dataSourceMap.get(url);

			if (ds == null){
				getSessionFactory(url);
				ds = dataSourceMap.get(url);
			}

			return ds.getConnection();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	public static long getRootProjectID(String url){

		Connection conn = null;
		try {
			conn = getConnection(url);
			ResultSet rs = conn.createStatement().executeQuery("select MIN(projectID) from riskproject");
			rs.first();
			return  rs.getLong(1);
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			return 0L;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}

	// If JDBC is used directly, rather than Hibernate, then the cache can become out of date. 
	// So after certain types of updates, the session factory needs to be removed so that it is recreated next 
	// time it is called.
	public static void removeSF(final String url) {

		try {

			SessionFactory factory = sessionFactoryMap.get(url);
			if(factory instanceof SessionFactoryImpl) {
				SessionFactoryImpl sf = (SessionFactoryImpl)factory;
				ConnectionProvider conn = sf.getConnectionProvider();
				if(conn instanceof C3P0ConnectionProvider) { 
					((C3P0ConnectionProvider)conn).close(); 
				}
			}

			factory.close();

			sessionFactoryMap.remove(url);

		} catch (Exception e) {
			resetDBConnections();
		}
	}
	public static Session getSession(final String url){
		return getSessionFactory(url).openSession();
	}
	public static ComboPooledDataSource getQRMLoginCPDS() {
		return qrmloginCPDS;
	}
	public static String getHostURLRoot() {
		return hostURLRoot;
	}
	public static String getHostUser() {
		return hostUser;
	}
	public static String getHostPass() {
		return hostPass;
	}
}