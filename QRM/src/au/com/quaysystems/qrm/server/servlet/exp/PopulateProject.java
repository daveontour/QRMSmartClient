package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.DTOObjectiveImpacted;
import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentConsequence;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

import com.ibm.icu.util.Calendar;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import de.svenjacobs.loremipsum.LoremIpsum;

public class PopulateProject  {

	private final SimpleDateFormat df2 = new SimpleDateFormat("MMM");
	private final Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	private final QRMTXManager txmgr = new QRMTXManager();
	private final ComboPooledDataSource qrmloginCPDS;

	public PopulateProject(){
		qrmloginCPDS = PersistenceUtil.getQRMLoginCPDS();		
	}


	private static void closeAll(final ResultSet resultSet,
			final Statement statement, final Connection connection) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
	}
	private long getRepositoryID(String url){

		Connection conn = null;
		try {
			conn = qrmloginCPDS.getConnection();
			String sql = "SELECT repID FROM repositories WHERE url = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			long repID = rs.getLong("repID");
			closeAll(rs,stmt, conn);

			return (repID);
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}
		return -1L;
	}

	@SuppressWarnings("unchecked")
	private Long[] getRootProjectID(Session sess){

		ArrayList<ModelRiskProject> projects = new ArrayList<ModelRiskProject>(sess.getNamedQuery("getAllRiskProjects").list());

		Long id = Long.MAX_VALUE;
		Long userID = null;

		for(ModelRiskProject project:projects){
			if (project.projectID < id){
				id = project.projectID;
				userID = project.projectRiskManagerID;
			}
		}

		return new Long[]{id,userID};
	}





	@SuppressWarnings("unchecked")
	public void normaliseProject(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		int maxNumControlsPerRisk = Integer.parseInt(request.getParameter("maxNumControlsPerRisk"));
		int maxNumberObjectivesPerRisk = Integer.parseInt(request.getParameter("maxNumberObjectivesPerRisk"));
		int maxNumberMitigationStepsPerRisk = Integer.parseInt(request.getParameter("maxNumberMitigationStepsPerRisk"));
		int maxNumberCommentsPerRisk = Integer.parseInt(request.getParameter("maxNumberCommentsPerRisk"));
		int maxNumberConsequences = Integer.parseInt(request.getParameter("maxNumberConsequences"));
		int numRisks = Integer.parseInt(request.getParameter("numRisks"));

		boolean createProjectStructure = Boolean.parseBoolean(request.getParameter("createProjectStructure"));
		boolean createTestUsers = Boolean.parseBoolean(request.getParameter("createTestUsers"));

		Random rand = new Random();

		//Need to set the DB session associated with the thread to a session attached to the concerned repository

		try {
			sess = PersistenceUtil.getSession(request.getParameter("repURL"));
			sess.setCacheMode(CacheMode.IGNORE);
			sess.setFlushMode(FlushMode.ALWAYS);
		} catch (Exception e2) {
			sess= null;
			return;
		}

		createSampleStructure(createProjectStructure, createTestUsers, request, sess);

		ArrayList<ModelRiskProject> projects = new ArrayList<ModelRiskProject>(sess.getNamedQuery("getAllRiskProjects").list());

		LoremIpsum ipsum = new LoremIpsum();

		HashMap<Long,ModelRiskProject> mapProj = new HashMap<Long,ModelRiskProject>(); 
		HashMap<Long,List<ModelPerson>> mapMgrs = new HashMap<Long,List<ModelPerson>>(); 
		HashMap<Long,List<ModelPerson>> mapOwns = new HashMap<Long,List<ModelPerson>>(); 
		HashMap<Long,ModelToleranceMatrix> mapMat = new HashMap<Long,ModelToleranceMatrix>(); 
		HashMap<Long,ArrayList<ModelMultiLevel>> mapCat = new HashMap<Long,ArrayList<ModelMultiLevel>>(); 
		HashMap<Long,ArrayList<Long>> mapSummRisk = new HashMap<Long,ArrayList<Long>>();
		ArrayList<Long> allRisks  = new ArrayList<Long>(); 

		for (int i = 0; i< numRisks; i++){

			log.info("Creating Sample Risk "+i);

			ModelRiskProject proj = projects.get(rand.nextInt(projects.size()));
			mapProj.put(proj.projectID, proj);

			List<ModelPerson> mgrs =  mapMgrs.get(proj.projectID);
			if (mgrs == null){
				mgrs = txmgr.getProjectRiskManagers(proj.projectID,sess);
				mapMgrs.put(proj.projectID, mgrs);
			}
			List<ModelPerson> owns =  mapOwns.get(proj.projectID);
			if (owns == null){
				owns = txmgr.getProjectRiskOwners(proj.projectID,sess);
				mapOwns.put(proj.projectID, owns);
			}
			ModelToleranceMatrix mat =  mapMat.get(proj.projectID);
			if (mat == null){
				mat = txmgr.getProjectMatrix(proj.projectID, sess);
				mapMat.put(proj.projectID,mat);
			}
			ArrayList<ModelMultiLevel> cats =  mapCat.get(proj.projectID);
			if (cats == null){
				cats = new ArrayList<ModelMultiLevel>(txmgr.getProjectCategorys(proj.projectID, sess));
				mapCat.put(proj.projectID, cats);
			}
			ArrayList<Long> summ =  mapSummRisk.get(proj.projectID);
			if (summ == null){
				summ = new ArrayList<Long>();
				mapSummRisk.put(proj.projectID, summ);
			}


			try {
				Connection conn = PersistenceUtil.getConnection(request.getParameter("repURL"));
				String sql = null;
				if (cats.size() > 0){
					sql = "INSERT INTO risk (title, description, cause, projectID, matrixID, manager1ID, ownerID, consequences, impSafety, impSpec,impCost,impTime, impReputation, impEnvironment, treated," +
							"treatmentAvoidance,treatmentReduction, treatmentRetention,treatmentTransfer, summaryRisk," +
							"inherentImpact, treatedImpact, inherentProb, treatedProb,  mitPlanSummary, mitPlanSummaryUpdate,primCatID) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO risk (title, description, cause, projectID, matrixID, manager1ID, ownerID, consequences, impSafety, impSpec,impCost,impTime, impReputation, impEnvironment, treated," +
							"treatmentAvoidance,treatmentReduction, treatmentRetention,treatmentTransfer, summaryRisk," +
							"inherentImpact, treatedImpact,inherentProb, treatedProb, mitPlanSummary, mitPlanSummaryUpdate) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				}
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1,ipsum.getWords(5+rand.nextInt(25), rand.nextInt(50)));
				stmt.setString(2,ipsum.getWords(rand.nextInt(200), rand.nextInt(50)));
				stmt.setString(3,ipsum.getWords(rand.nextInt(200), rand.nextInt(50)));
				stmt.setLong(4,proj.projectID);
				stmt.setLong(5,mat.matrixID);
				stmt.setLong(6,mgrs.get(rand.nextInt(mgrs.size())).stakeholderID);
				stmt.setLong(7,owns.get(rand.nextInt(owns.size())).stakeholderID);
				stmt.setString(8,ipsum.getWords(rand.nextInt(200), rand.nextInt(49)));
				stmt.setBoolean(9, rand.nextBoolean());
				stmt.setBoolean(10, rand.nextBoolean());
				stmt.setBoolean(11, rand.nextBoolean());
				stmt.setBoolean(12, rand.nextBoolean());
				stmt.setBoolean(13, rand.nextBoolean());
				stmt.setBoolean(14, rand.nextBoolean());
				stmt.setBoolean(15, rand.nextBoolean());
				stmt.setBoolean(16, rand.nextBoolean());
				stmt.setBoolean(17, rand.nextBoolean());
				stmt.setBoolean(18, rand.nextBoolean());
				stmt.setBoolean(19, rand.nextBoolean());
				boolean sumRisk = (rand.nextDouble()<0.15)?true:false;
				stmt.setBoolean(20, sumRisk);
				double imp = Math.min(mat.maxImpact+1-rand.nextDouble(),1.5+rand.nextInt(mat.maxImpact)+rand.nextDouble()/2);
				double prob = Math.min(mat.maxProb+1-rand.nextDouble(),1.5+rand.nextInt(mat.maxProb)+rand.nextDouble()/2);
				stmt.setDouble(21, imp);
				stmt.setDouble(22, Math.max(rand.nextDouble()*imp,1.0+rand.nextDouble()));
				stmt.setDouble(23, prob);
				stmt.setDouble(24, Math.max(rand.nextDouble()*prob,1.0+rand.nextDouble()));
				stmt.setString(25, ipsum.getWords(5+rand.nextInt(150), rand.nextInt(30)));
				stmt.setString(26, ipsum.getWords(5+rand.nextInt(100), rand.nextInt(20)));
				
				if (cats.size() > 0){
					ModelMultiLevel cat = cats.get(rand.nextInt(cats.size()));
					stmt.setLong(27, cat.internalID);
				}
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				rs.first();
				allRisks.add(rs.getLong(1));


				if (sumRisk)summ.add(rs.getLong(1));


				closeAll(null, stmt, conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e);
			} 
		}

		sess.close();


		String url = request.getParameter("repURL");
		PersistenceUtil.removeSF(url);
		sess = PersistenceUtil.getSession(url);

		long repID = getRepositoryID(url);
		ArrayList<Long> repUsers = new ArrayList<Long>();
		try {
			Connection conn = qrmloginCPDS.getConnection();
			String sql = "SELECT * FROM userrepository WHERE repID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, repID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				repUsers.add(rs.getLong("stakeholderID"));
			}
			closeAll(rs,stmt, conn);
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}


		for (ModelRiskProject project:projects){

			Long id = project.getProjectID();


			timeshiftAndPopulateProject(project.getProjectTitle(), id, url, mapMat.get(id), mapSummRisk.get(id),
					maxNumControlsPerRisk,  
					maxNumberObjectivesPerRisk,  
					maxNumberMitigationStepsPerRisk,  
					maxNumberCommentsPerRisk,
					maxNumberConsequences, 
					sess,
					repID,
					repUsers);
		}
		Thread th = new  Thread(new CreateIncidents(ipsum,  allRisks,  url,  projects));
		th.run();

		sess.close();
	}


	private void timeshiftAndPopulateProject(String projTitle, long projectID, String url,ModelToleranceMatrix mat, ArrayList<Long> sumRisks, int maxNumControlsPerRisk, int maxNumberObjectivesPerRisk, int maxNumberMitigationStepsPerRisk, int maxNumberCommentsPerRisk, int maxNumberConsequences, Session sess, long repID, ArrayList<Long> repUsers){

		log.info("Time Shifting project "+projectID);

		long now = new Date().getTime();
		int beginOffsetDays;
		int projectLengthDays;
		long projectBegin;
		long projectEnd;

		Random rand = new Random();

		beginOffsetDays = rand.nextInt(180)*(rand.nextBoolean()?-1:0);
		projectBegin = now+beginOffsetDays*QRMConstants.DAYSMSEC;
		projectLengthDays = 360+rand.nextInt(360);
		projectEnd = projectBegin+(projectLengthDays*QRMConstants.DAYSMSEC);

		Connection conn = PersistenceUtil.getConnection(url);
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e2) {
			log.error("QRM Stack Trace", e2);
		}

		try {
			String sql = "UPDATE risk SET " +
					"  dateIDRev = null, dateIDApp = null, idIDRev = null, idIDApp = null," +
					"  dateEvalRev = null, dateEvalApp = null, idEvalRev = null, idEvalApp = null," +
					"  dateMitPrep = null, dateMitRev = null, dateMitApp = null, idMitPrep = null, idMitRev = null, idMitApp = null" +
					"  WHERE projectID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, projectID);
			stmt.executeUpdate();
			closeAll(null, stmt, null);
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} 

		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE riskproject SET projectStartDate = ?, projectEndDate = ?");
			ps.setDate(1, new java.sql.Date(projectBegin));
			ps.setDate(2, new java.sql.Date(projectEnd));
			ps.executeUpdate();
			closeAll(null, ps, null);
		} catch (SQLException e3) {
			log.error(e3);
		}

		LoremIpsum ipsum = new LoremIpsum();

		List<ModelRisk> risks = txmgr.getAllProjectRisks(1L, projectID, false, sess);
		ArrayList<ModelObjective> objs = (ArrayList<ModelObjective>)txmgr.getProjectObjectives(projectID, sess);
		ModelRiskProject project = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID);

		int j = 0;

		ArrayList<Long> types = new ArrayList<Long>();
		for (ModelQuantImpactType type : txmgr.getProjectQuantTypes(project.projectID, sess)){
			// Type -1 is used for cost summary
			// Type 2 is unspecified contingency allowace
			// Type 3 is Non quantifiable 
			// So all of the above are excluded
			if (type.typeID != -1 && type.typeID != 2 && type.typeID != 3){
				types.add(type.typeID);
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int startMonth = cal.get(Calendar.MONTH);
		int startYear = cal.get(Calendar.YEAR);
		int startDay = cal.get(Calendar.DAY_OF_MONTH);

		ArrayList<Long> reviewIDs = new ArrayList<Long>();

		if (projectID%3 == 0){  //just so not too many are created
			// Create some reviews for the project
			for (int i = startMonth+2; i < startMonth+12; i+=6){

				cal.set(Calendar.MONTH,(i<11)?i:i-11);
				cal.set(Calendar.YEAR, (i<11)?startYear:startYear+1);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(26, startDay));

				ModelReview rev = new ModelReview();
				rev.setTitle("Half Yearly Review ("+df2.format(cal.getTime())+") of all risks: "+projTitle);
				rev.setScheduledDate(cal.getTime());
				rev.setProjectID(projectID);

				Transaction tx = sess.beginTransaction();
				reviewIDs.add((Long)sess.save(rev));
				tx.commit();
			}

			cal.setTime(new Date());
			startMonth = cal.get(Calendar.MONTH);
			startYear = cal.get(Calendar.YEAR);
			startDay = cal.get(Calendar.DAY_OF_MONTH);

			// Create some reviews for the project

			cal.setTime(new Date());
			startMonth = cal.get(Calendar.MONTH);
			startYear = cal.get(Calendar.YEAR);
			startDay = cal.get(Calendar.DAY_OF_MONTH);

			for (int i = 1; i < 2; i++){

				int month = cal.get(Calendar.MONTH)-6*i;

				if (month < 0){
					month = 11+month;
					cal.set(Calendar.YEAR, startYear-1);
				}
				cal.set(Calendar.MONTH,month);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(26, startDay));

				ModelReview rev = new ModelReview();
				rev.setTitle("Half Yearly ("+df2.format(cal.getTime())+") of all risks: "+projTitle);
				rev.setScheduledDate(cal.getTime());
				rev.setActualDate(cal.getTime());
				rev.reviewComplete = true;
				rev.setProjectID(projectID);
				rev.setReviewComments(ipsum.getParagraphs(1+rand.nextInt(3)));

				Transaction tx = sess.beginTransaction();
				reviewIDs.add((Long)sess.save(rev));
				tx.commit();
			}
		}
		for (ModelRisk risk:risks){

			log.info("Populating Sample Risk "+j++);

			//Attach the risk to the project review
			try {
				Statement st = conn.createStatement();
				for (Long revID:reviewIDs){
					String sql = "INSERT INTO reviewrisk (reviewID, riskID) VALUES ("+revID+","+risk.riskID+")";
					st.addBatch(sql);
				}
				st.executeBatch();
			} catch (SQLException e1) {
				log.error("QRM Stack Trace", e1);
			}

			try {

				if (rand.nextDouble() < 0.7 && sumRisks.size() > 0 && !risk.isSummaryRisk()){
					if (sumRisks.size() == 1 && rand.nextBoolean()){
						sumRisks.get(0);
					} else {
//						risk.parentSummaryRisk = sumRisks.get(rand.nextInt(sumRisks.size()));
						
						

						// Change to the way parent /child risk  maintained
						Statement st = conn.createStatement();
//						st.executeUpdate("INSERT INTO riskrisk (parentID, childID) VALUES ("+risk.parentSummaryRisk+","+risk.riskID+")");
						st.executeUpdate("INSERT INTO riskrisk (parentID, childID) VALUES ("+sumRisks.get(rand.nextInt(sumRisks.size()))+","+risk.riskID+")");

					}
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}

			Transaction tx = sess.beginTransaction();

			int riskBeginOffsetDays = rand.nextInt((int)(projectLengthDays/2));
			risk.setBeginExposure(new Date(project.getProjectStartDate().getTime()+riskBeginOffsetDays*QRMConstants.DAYSMSEC));

			int riskLengthDays = Math.max(rand.nextInt(projectLengthDays-riskBeginOffsetDays), 30);
			risk.setEndExposure(new Date(risk.getBeginExposure().getTime()+riskLengthDays*QRMConstants.DAYSMSEC));


			risk.setLikeType(1);
			risk.setLikeT(365.0);
			risk.setLikeAlpha(rand.nextDouble()*(1.5*365/riskLengthDays));
			risk.setLikeProb(0.0);

			risk.setLikePostType(1);
			risk.setLikePostT(365.0);
			risk.setLikePostAlpha(risk.getLikeAlpha()* rand.nextDouble());
			risk.setLikePostProb(0.0);

			ProbUtil.setDynamicProb(project,risk, mat, true, true);
			ProbUtil.setDynamicProb(project,risk, mat, false, true);

			sess.saveOrUpdate(risk);
			tx.commit();

			try {
				conn.setAutoCommit(false);
				Statement stmt= conn.createStatement();

				// Audit Comments
				if (rand.nextDouble() < 0.9){
					stmt.addBatch("INSERT INTO auditcomments (  riskID, enteredByID, comment,identification, review) VALUES ("+risk.getInternalID()+","+risk.getManager1ID()+",'"+ipsum.getWords(rand.nextInt(200), rand.nextInt(50))+"', true, true)");
					if (rand.nextDouble() < 0.7){
						stmt.addBatch("INSERT INTO auditcomments (  riskID, enteredByID, comment,identification, approval) VALUES ("+risk.getInternalID()+","+risk.getOwnerID()+",'"+ipsum.getWords(rand.nextInt(200), rand.nextInt(50))+"', true, true)");
						stmt.addBatch("INSERT INTO auditcomments (  riskID, enteredByID, comment,evaluation, review) VALUES ("+risk.getInternalID()+","+risk.getManager1ID()+",'"+ipsum.getWords(rand.nextInt(200), rand.nextInt(50))+"', true, true)");
						if (rand.nextBoolean()){
							stmt.addBatch("INSERT INTO auditcomments (  riskID, enteredByID, comment,evaluation, approval) VALUES ("+risk.getInternalID()+","+risk.getOwnerID()+",'"+ipsum.getWords(rand.nextInt(200), rand.nextInt(50))+"', true, true)");
						}
					}
				}

				// Add a random number of comments
				if(maxNumberCommentsPerRisk > 0){
					for (int i=0; i<= rand.nextInt(maxNumberCommentsPerRisk);i++){
						try {
							conn.setAutoCommit(true);
							stmt.addBatch("INSERT INTO auditcomments (riskID, enteredByID, comment) VALUES ("+risk.getInternalID()+","+repUsers.get(rand.nextInt(repUsers.size()))+",'"+ipsum.getWords(rand.nextInt(200), rand.nextInt(50))+"')");
						} catch (SQLException e) {
							log.error("QRM Stack Trace", e);
						}
					}
				}
				// Add some consequences
				if (maxNumberConsequences > 0){
					if (types.size() > 0){
						for (int i=0; i<= rand.nextInt(maxNumberConsequences);i++){
							try {
								int x1 = 50-(int)(50*rand.nextDouble());
								int x2 = 50-(int)(50*rand.nextDouble());
								String y = 500-rand.nextInt(500)+":45.0:0.0:0.0:0.0:0.0:";
								stmt.addBatch("INSERT INTO riskconsequence (riskID, quantifiable, quantType, description," +
										"riskConsequenceProb, costDistributionType, costDistributionParams," +
										"postRiskConsequenceProb, postCostDistributionType, postCostDistributionParams) " +
										"VALUES ("+risk.riskID+",true,"+types.get(rand.nextInt(types.size()))+",'"+ipsum.getWords(4+rand.nextInt(5), rand.nextInt(50))+"',"+x1+",'au.com.quaysystems.qrm.util.probability.NormalDistribution','"+500+rand.nextInt(500)+":45.0:0.0:0.0:0.0:0.0:"+"',"+x2+",'au.com.quaysystems.qrm.util.probability.NormalDistribution','"+y+"')");
							} catch (SQLException e) {
								log.error("QRM Stack Trace", e);
							}
						}
					}
				}

				if (maxNumControlsPerRisk > 0){
					for (int i=0; i<= rand.nextInt(maxNumControlsPerRisk);i++){
						int x = rand.nextInt(4)+1;
						stmt.addBatch("INSERT INTO riskcontrols (control,effectiveness,riskID,contribution) VALUES ('"+ipsum.getWords(rand.nextInt(20), rand.nextInt(50))+"',"+x+","+risk.getInternalID()+",'"+ipsum.getWords(rand.nextInt(5), rand.nextInt(50))+"')");
					}
				}

				stmt.executeBatch();

			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}

			// Add a random number of impacted objectives
			if (maxNumberObjectivesPerRisk > 0){
				if (objs.size() > 0){
					sess.beginTransaction();
					for (int i=0; i<= rand.nextInt(maxNumberObjectivesPerRisk);i++){
						Long obj = objs.get(rand.nextInt(objs.size())).getInternalID();
						if (obj != 1) sess.saveOrUpdate(new DTOObjectiveImpacted(risk.getInternalID(), obj));
					}
					sess.getTransaction().commit();
				}
			}

			// Add a random number of mitigation steps
			if (maxNumberMitigationStepsPerRisk > 0){
				for (int i=0; i<= 2*rand.nextInt(maxNumberMitigationStepsPerRisk);i++){
					try {
						String sql = "INSERT INTO mitigationstep (projectID, riskID, description, startDate, endDate, personID, estCost, percentComplete, response) VALUES " +
								"(?,?,?,?,?,?,?,?,?)";
						PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
						stmt.setLong(1, projectID);
						stmt.setLong(2, risk.getInternalID());
						stmt.setString(3, ipsum.getWords(rand.nextInt(100), rand.nextInt(49)));
						stmt.setDate(4, new java.sql.Date(project.projectStartDate.getTime()+rand.nextInt(riskBeginOffsetDays+riskLengthDays)*QRMConstants.DAYSMSEC));
						stmt.setDate(5, new java.sql.Date(project.projectStartDate.getTime()+rand.nextInt(riskBeginOffsetDays+riskLengthDays)*QRMConstants.DAYSMSEC));
						stmt.setLong(6, repUsers.get(rand.nextInt(repUsers.size())));
						stmt.setDouble(7, new Double(rand.nextInt(10)*1000).intValue());
						stmt.setDouble(8, rand.nextInt(100));
						stmt.setBoolean(9, rand.nextBoolean());
						stmt.executeUpdate();

						ResultSet rs = stmt.getGeneratedKeys();
						rs.first();
						int mitStepID = rs.getInt(1);
						for (int ji=0; ji<= rand.nextInt(4);ji++){
							try {									
								sess.beginTransaction();
								sess.save(new ModelUpdateComment(mitStepID, "MITIGATION", ipsum.getWords(rand.nextInt(30), rand.nextInt(15)),repUsers.get(rand.nextInt(repUsers.size()))));
								sess.getTransaction().commit();
							} catch (Exception e) {
								log.error(e);
							}
						}
						rs.close();
					} catch (SQLException e) {
						log.error(e);
					}
				}
			}
		}
		closeAll(null, null, conn);
	}

	private void createSampleStructure(boolean createProjectStructure, boolean createTestUsers, HttpServletRequest request, Session sess){

		Connection conn = PersistenceUtil.getConnection(request.getParameter("repURL"));
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e1) {
			log.error("QRM Stack Trace", e1);
		}

		Long repID = getRepositoryID(request.getParameter("repURL"));

		ArrayList<Long> userIDs = new ArrayList<Long>();
		ArrayList<Long> projectIDs = new ArrayList<Long>();


		if (createTestUsers){
			try {
				Connection conn2 = qrmloginCPDS.getConnection();
				conn2.setAutoCommit(true);
				userIDs.add(createUser(conn2, "Dave Burton","dave@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Kellie Brothers","fgdfggd@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Katrina House","katrina@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Tom Dent","vprahflp@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Fionna Troy","qvmhzrpw@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Sridip Lang","sdffrpw@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Tom Stevens","hfnizeqg@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Mary Jane Kamroc","tjzlzcnw@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Charles Pearl","okffpyvb@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "James Junior","pdjwhznp@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Caroline Potter","gafxxerx@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "Katie Magenta","icozdgrx@emailaddress.email"+repID, "pass", repID,request));
				userIDs.add(createUser(conn2, "David Smith","xfhaejnp@emailaddress.email"+repID, "pass", repID,request));
				conn2.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}

		Long[] root = getRootProjectID(sess);
		Long rootID = root[0];
		Long userID = root[1];


		projectIDs.add(rootID);
		Long itProjectID = null;

		//createDummyProject returns a id which is used as the parent ID for future calls
		if (createProjectStructure){
			try {
				projectIDs.add(createDummyProject(conn, rootID,"Highest level of corporate risk managed by the board risk management sub committee","Board of Directors","BOD",userID,1));
				projectIDs.add(createDummyProject(conn, rootID,"Risks required to be managed by a corporate executive","Corporate Management","CXO",userID,1));
				projectIDs.add(createDummyProject(conn, rootID,"Corporate Information Technology and communications operations","Information Technology","IT",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(3),"IT system and network operations","IT Operations","ITOP",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(4),"On site IT operations","Onsite Operations","ITON",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(4),"External hosted operations","Remote Hosted","ITOFF",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(3),"Corporate IT Architecture","IT Architecture","ITAR",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(3),"IT customer support","IT Customer Support","ITCS",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(3),"Management of IT service vendors","Vendor Operations","ITVE",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Corporate Finance Department","Finance","FIN",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(10),"Accounts","Accounting","ACC",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(10),"Finacial Accounts","Financial Accounting","ACCF",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(10),"Management Accounting","Management Accounting","ACCM",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(10),"Budget Operations","Budgetting","BUDG",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Corporate Marketing","Marketing","MARK",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Research and Development","Research and Development","RND",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Human Resources","Human Resources","HR",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(17),"Employee Health and Welfare","Health and Welfare","HRWEL",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(17),"Employee Compennsation and Benefits","Compennsation and Benefits","HRCOM",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(17),"HR Recruiting","Recruiting","HRREC",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Manufacturing","Manufacturing","MAN",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Customer Service","Customer Service","CS",userID,0));
				projectIDs.add(createDummyProject(conn, rootID,"Sales","Sales","SALE",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(23),"Direct Sales","Direct Sales","SALED",userID,0));
				projectIDs.add(createDummyProject(conn, projectIDs.get(23),"Web Sales","Web Sales","SALEW",userID,0));
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}

			log.info("Creating Objectives");

			itProjectID = projectIDs.get(3);
			long rootObjID = 1L;

			try {
				long planObjID = createObjective(conn, itProjectID, "Effective Planning and Organisation", rootObjID ); 
				createObjective(conn, itProjectID, "Define Strategic IT Plan", planObjID ); 
				createObjective(conn, itProjectID, "Define the Information Architecture", planObjID ); 
				createObjective(conn, itProjectID, "Determine technical Direction", planObjID ); 
				createObjective(conn, itProjectID, "Define IT Processes, Organisation and Relationships", planObjID ); 
				createObjective(conn, itProjectID, "Manage IT Investment", planObjID ); 
				createObjective(conn, itProjectID, "Communicate Management Aim and Directions", planObjID ); 
				createObjective(conn, itProjectID, "Manage IT Human Resources", planObjID ); 
				createObjective(conn, itProjectID, "Manage Quality", planObjID ); 
				createObjective(conn, itProjectID, "Assess and Manage IT Risk", planObjID ); 
				createObjective(conn, itProjectID, "Manage Projects", planObjID ); 
				createObjective(conn, itProjectID, "Effective Acquisition and Implementation", rootObjID ); 
				createObjective(conn, itProjectID, "Effective Delivery and Support", rootObjID ); 
				createObjective(conn, itProjectID, "Effective Monitoring and Evaluation", rootObjID ); 
			} catch (Exception e){
				log.error("QRM Stack Trace", e);
			}

			log.info("Creating IT Project Quant Types");


			try {
				createQuantType("Loss of Data", "MB", false, itProjectID, sess);
				createQuantType("Loss of external network connection", "minutes", false, itProjectID, sess);
				createQuantType("Loss of IT systems access", "minutes", false, itProjectID, sess);
				createQuantType("Replacement cost of equipment", "dollars", true, itProjectID, sess);
				createQuantType("Security Breach", "Number of Breeches", false, itProjectID, sess);
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}

		try {
			String sqlOwner = "INSERT INTO projectowners (projectID,stakeholderID) VALUES ";
			String sqlManagers = "INSERT INTO projectriskmanagers (projectID,stakeholderID) VALUES ";
			if (createTestUsers){
				for (Long projID:projectIDs){
					for (int i = 0; i < userIDs.size(); i++){
						sqlManagers = sqlManagers+" ("+projID+","+userIDs.get(i)+"),";
						if (i < 7) {
							sqlOwner = sqlOwner+" ("+projID+","+userIDs.get(i)+"),";
						}
					}
				}
			}
			sqlOwner = sqlOwner.substring(0, sqlOwner.lastIndexOf(','));
			sqlManagers = sqlManagers.substring(0, sqlManagers.lastIndexOf(','));
			conn.createStatement().executeUpdate(sqlOwner);
			conn.createStatement().executeUpdate(sqlManagers);
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
		closeAll(null,null,conn);
	}

	private void createQuantType(String desc, String units, boolean cost, Long projectID, Session sess){

		sess.beginTransaction();

		ModelQuantImpactType type = new ModelQuantImpactType();

		type.description = desc;
		type.units = units;
		type.costCategroy = cost;
		type.projectID = projectID;
		sess.saveOrUpdate(type);

		sess.getTransaction().commit();
	}

	private long createObjective(Connection conn, Long projectID, String objective, Long parentID) throws SQLException{

		// Call the database procedure to create the objective 
		CallableStatement cstmt = conn.prepareCall("{call insertObjective(?,?,?,?)}");
		cstmt.setLong(1, projectID);
		cstmt.setString(2, objective);
		cstmt.setLong(3, parentID);
		cstmt.registerOutParameter(4, Types.NUMERIC);
		cstmt.execute();

		return cstmt.getLong(4);
	}

	private long createUser(Connection conn, String name, String email, String pass, long repID, HttpServletRequest request) throws SQLException{

		// conn that is passed is to the cpds
		// Database stored procedure to create user
		CallableStatement cs = conn.prepareCall("call createUser(?,?,?,?)");
		cs.setString(1, name);
		cs.setString(2, pass);
		cs.setString(3, email);
		cs.registerOutParameter(4, Types.BIGINT);
		cs.execute();

		long userID = cs.getLong(4);

		if (userID < 0) throw new SQLException();

		// Add the user to trhe repository
		Connection conn2 = PersistenceUtil.getConnection(request.getParameter("repURL"));

		PreparedStatement ps = conn2.prepareStatement("INSERT INTO userrepository (stakeholderID, repID) VALUES (?,?)");
		ps.setLong(1, userID);
		ps.setLong(2, repID);
		ps.execute();
		conn2.close();

		return userID;
	}

	private long createDummyProject(Connection conn, long parentID, String desc, String title, String code, long userID, int seclevel) throws SQLException{

		log.info("Creating Sample Project "+title);
		CallableStatement cstmt = conn.prepareCall("{call addProject(?,?,?,?,?,?,?,?,?)}");

		cstmt.setLong(1, parentID);
		cstmt.setString(2, desc);
		cstmt.setString(3, title);
		cstmt.setString(4, code);
		cstmt.setLong(5, userID);
		cstmt.setDate(6, new java.sql.Date(new Date().getTime()));
		cstmt.setDate(7, new java.sql.Date(new Date().getTime()));
		cstmt.setInt(8, seclevel);
		cstmt.registerOutParameter(9, java.sql.Types.BIGINT);

		cstmt.executeUpdate();
		return cstmt.getLong(9);	
	}

	private class CreateIncidents implements Runnable {

		private LoremIpsum ipsum;
		private Session sess;
		private ArrayList<Long> allRisks;
		private Connection conn;
		private ArrayList<ModelRiskProject> projects;

		public CreateIncidents(LoremIpsum ipsum, ArrayList<Long> allRisks, String url, ArrayList<ModelRiskProject> projects) {
			super();
			this.ipsum = ipsum;
			this.allRisks = allRisks;
			this.projects = projects;
			
			sess = PersistenceUtil.getSession(url);
			conn = PersistenceUtil.getConnection(url);
		}

		public void run() {
			
			Random rand = new Random();
			
			for (ModelRiskProject project: projects){
				int numIncidents = 5+new Random().nextInt(10);
				Long mgrID = projects.get(rand.nextInt(projects.size())).projectRiskManagerID;
				createIncidents(ipsum, numIncidents, project.projectID, mgrID);
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@SuppressWarnings("unchecked")
		private void createIncidents(LoremIpsum ipsum,int numIncidents, Long projectID, Long mgrID){

			Random random = new Random();
			Calendar cal =  Calendar.getInstance();
			int startMonth = cal.get(Calendar.MONTH);
			System.out.println("--> Creating "+numIncidents+" incidents");

			for (int idx=0; idx < numIncidents; idx++){
				try {

					Statement stmt= conn.createStatement();

					ModelIncident incident = new ModelIncident();
					incident.projectID = projectID;
					incident.setDateIncident(cal.getTime());
					incident.setReportedByID(mgrID);
					incident.setReportedByStr(txmgr.getPerson(mgrID, sess).name);
					incident.notifyStakeHoldersEntered = incident.notifyStakeHoldersUpdate = incident.stakeHolderNotified = false;

					incident.setTitle(ipsum.getWords(4+random.nextInt(20),random.nextInt(50)));

					cal.set(Calendar.DAY_OF_MONTH, random.nextInt(26));
					cal.set(Calendar.MONTH, random.nextInt(11));
					if (cal.get(Calendar.MONTH) >= startMonth){
						cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
					}

					incident.setDescription(ipsum.getWords(20+random.nextInt(100)));
					incident.setLessonsLearnt(ipsum.getParagraphs(2+random.nextInt(5)));
					incident.setControls(ipsum.getWords(20+random.nextInt(100)));
					incident.severity = random.nextInt(4)+1;

					incident.bCauses = random.nextBoolean();
					incident.bControl = random.nextBoolean();
					incident.bIdentified = random.nextBoolean();
					incident.bMitigated = random.nextBoolean();
					incident.bRated = random.nextBoolean();
					incident.bReviews = random.nextBoolean();
					incident.bActive = random.nextBoolean();
					incident.bIssue = random.nextBoolean();

					incident.impCost = random.nextBoolean();
					incident.impTime = random.nextBoolean();
					incident.impSpec = random.nextBoolean();
					incident.impEnviron = random.nextBoolean();
					incident.impSafety = random.nextBoolean();
					incident.impReputation = random.nextBoolean();

					sess.beginTransaction();
					Long id = (Long) sess.save(incident);
					sess.getTransaction().commit();

					Long idL = Long.MAX_VALUE;
					for(ModelRiskProject project:(List<ModelRiskProject>)sess.getNamedQuery("getAllRiskProjects").list()){
						if (project.projectID < idL){
							idL = project.projectID;
						}
					}

					ArrayList<ModelObjective> objs = (ArrayList<ModelObjective>)txmgr.getProjectObjectives(idL, sess);

					try {
						int numObj = random.nextInt(objs.size());

						for (int i = 0; i<numObj; i++) {
							Long objectiveID = objs.get(random.nextInt(objs.size()-1)).objectiveID;
							if (objectiveID > 0) {
								stmt.addBatch("INSERT INTO incidentobjective (incidentID, objectiveID) VALUES ("+id+", "+objectiveID+")");
							}
						}
					} catch (Exception e) {
						log.error(e);
					}

					ArrayList<ModelQuantImpactType> cons = txmgr.getProjectQuantTypes(idL, sess);

					try {
						int numCons = random.nextInt(cons.size());
						for (int i = 0; i<numCons*2; i++) {
							ModelIncidentConsequence consq = new ModelIncidentConsequence();
							consq.value = new Double(new Double(5000*random.nextDouble()).intValue());
							consq.description = ipsum.getWords(random.nextInt(30), random.nextInt(50));
							consq.typeID = cons.get(random.nextInt(numCons)).typeID;
							consq.incidentID = id;
							sess.save(consq);
						}
					} catch (Exception e) {
						log.error(e);
					}

					try {
						int numRisks = Math.min(allRisks.size(), random.nextInt(25)+1);
						for (int i = 0; i< numRisks; i++) {
							stmt.addBatch("INSERT INTO incidentrisk (incidentID, riskID) VALUES ("+id+","+allRisks.get(random.nextInt(allRisks.size()-1))+")");
						}
					} catch (Exception e) {
						log.error(e);
					}

					stmt.executeBatch();

				} catch (Exception e) {
					log.error(e);
				} finally {
				}
			}
		}
	}
}