package au.com.quaysystems.qrm.server.servlet;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.CSVUploadInfo;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.QRMTXManager;

import com.thoughtworks.xstream.XStream;

public class RiskInstallerHelper {

	private static final Integer IGNORE = 0;
	private static final Integer APPEND = 1;
	private static final Integer REPLACE = 2;
	private ArrayList<String[]> itemsToImport;
	@SuppressWarnings("unused")
	private HashMap<Integer, String> actionColumns;
	private HashMap<String, Integer> actionColumnsReverse;
	private HashMap<String, String> configMap;
	private Integer riskKey; 
	private Boolean matchRisks;
	private Long projectID;
	private Long userID; 
	private HttpServletResponse response;
	private Connection conn;
	private Session sess;
	private QRMTXManager txmgr = new QRMTXManager();
	private HashMap<String, Double> probMap = new HashMap<String, Double>();
	private HashMap<String, Double> impactMap = new HashMap<String, Double>();
	HashSet<String> probSet = new HashSet<String>();
	HashSet<String> impactSet = new HashSet<String>();
	private List<String[]> allItems;
	private CSVUploadInfo info;
	private String[] probRankArray;
	private String[] impactRankArray;
	private ArrayList<ModelPerson> mgrCandidates;
	private ArrayList<ModelPerson> ownerCandidates;
	private final static HashMap<String, Integer> nWords = new HashMap<String, Integer>(30);
	public boolean impactSetNumeric = false;
	public boolean probSetNumeric = false;

	

	static{
		nWords.put("high", 1);
		nWords.put("likely", 1);
		nWords.put("medium", 1);
		nWords.put("possible", 1);
		nWords.put("possibility", 1);
		nWords.put("probably", 1);
		nWords.put("probable", 1);
		nWords.put("certain", 1);
		nWords.put("certainty", 1);
		nWords.put("low", 1);
		nWords.put("rare", 1);
		nWords.put("seldom", 1);
		nWords.put("unlikely", 1);
		nWords.put("very", 1);
		nWords.put("extremely", 1);
		nWords.put("absolute", 1);
		nWords.put("absolutely", 1);		
	}
	
	public RiskInstallerHelper(
			List<String[]> items, ArrayList<String[]> itemsToImport,
			HashMap<Integer, String> actionColumns,
			HashMap<String, Integer> actionColumnsReverse,
			HashMap<String, String> configMap,
			Integer riskKey, 
			Long projectID,
			Long userID, 
			Boolean matchRisks,
			HttpServletResponse response,
			Connection conn,
			Session sess) {
		
		this.allItems = items;
		this.itemsToImport = itemsToImport;
		this.actionColumns = actionColumns;
		this.actionColumnsReverse = actionColumnsReverse;
		this.configMap = configMap;
		this.riskKey = riskKey;
		this.projectID = projectID;
		this.userID = userID;
		this.response = response;
		this.conn = conn;
		this.matchRisks = matchRisks;
		this.sess = sess;

		
		normalizeProb();
		normalizeImpact();
		
		if (probSetNumeric){
			for(String val : probSet){
				String valstr = StringUtils.replace(val, "#", ".");
				probMap.put(valstr, Double.parseDouble(valstr));
			}
		}
		if (impactSetNumeric){
			for(String val : impactSet){
				String valstr = StringUtils.replace(val, "#", ".");
				impactMap.put(valstr, Double.parseDouble(valstr));
			}
		}		
		this.info = new CSVUploadInfo(itemsToImport, actionColumns, actionColumnsReverse, configMap, riskKey, projectID, userID, matchRisks,probSet, impactSet);
		
	}
	public RiskInstallerHelper(String probRankStr, String impactRankStr,
			Long key, Long long1, HttpServletResponse response,
			Connection sessionConnection, Session session) {
		
		try{
			this.probRankArray = probRankStr.split("##");
		} catch (Exception e){}
		try{
			this.impactRankArray = impactRankStr.split("##");		
		} catch (Exception e){}
		
		this.conn = sessionConnection;

		XStream xs = new XStream();

		try {
			ResultSet res = conn.createStatement().executeQuery("SELECT contents FROM attachment WHERE internalID = "+ key);
			res.first();
			InputStream stream = res.getBinaryStream("contents");
			
			info = (CSVUploadInfo) xs.fromXML(stream);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.itemsToImport = info.itemsToImport;
		this.actionColumns = info.actionColumns;
		this.actionColumnsReverse = info.actionColumnsReverse;
		this.configMap = info.configMap;
		this.riskKey = info.riskKey;
		this.projectID = info.projectID;
		this.userID = info.userID;
		this.response = response;
		this.matchRisks = info.matchRisks;
		this.sess = session;
		this.probSet = info.probSet;
		this.impactSet = info.impactSet;
		
		ModelToleranceMatrix matrix = txmgr.getProjectMatrix(projectID, session);
		Integer maxImpact = matrix.getMaxImpact();
		Integer maxProb = matrix.getMaxProb();
		
		if (this.probRankArray != null){
			for (int i = 0; i < this.probRankArray.length; i++){
				probMap.put(probRankArray[i], Math.max(new Double(maxProb-i),0.5));
			}
		}

		if (this.impactRankArray != null){
			for (int i = 0; i < this.impactRankArray.length; i++){
				impactMap.put(impactRankArray[i], Math.max(new Double(maxImpact-i), 0.5));
			}
		}
	}
	private void normalizeProb() {
		
		Integer inherentProbNum = actionColumnsReverse.get("inherentProb");
		Integer treatedProbNum = actionColumnsReverse.get("treatedProb");
		
		
		if (inherentProbNum == null && treatedProbNum == null ){
			return;
		}

		boolean preNum = checkAllNumeric(inherentProbNum);
		boolean postNum = checkAllNumeric(treatedProbNum);
		
		if (preNum && postNum){
			this.probSetNumeric = true;
		} 
		
		//gather the various descriptions.
		boolean firstItem = true;
		for (String[] item : this.allItems){
			
			if (firstItem){
				firstItem = false;
				continue;
			}
			
			if (inherentProbNum != null){

				String val = item[inherentProbNum].toLowerCase().trim().replaceAll("\\W", "#").replaceAll("[^A-Za-z0-9#]", "");
				String[] compound = val.split("#");
				if (compound.length == 1){
					if (isValidDescription(val)){
						probSet.add(correct(val));
					}
				} else {
					if (isValidDescription(compound[0]) && isValidDescription(compound[1])){
						probSet.add(correct(compound[0])+"#"+correct(compound[1]));
					}
					
				}
			}

			
			if (treatedProbNum != null){
				String val = item[treatedProbNum].toLowerCase().trim().replaceAll("\\W", "#").replaceAll("[^A-Za-z0-9#]", "");
				String[] compound = val.split("#");
				if (compound.length == 1){
					if (isValidDescription(val)){
						probSet.add(correct(val));
					}
				} else {
					if (isValidDescription(compound[0]) && isValidDescription(compound[1])){
						probSet.add(correct(compound[0])+"#"+correct(compound[1]));
					}
					
				}
			}
		}
	}
	private boolean isValidDescription(String val){
		
		if (StringUtils.isNumeric(val)){
			return true;
		}
		if (!StringUtils.isAsciiPrintable(val) || val.length() > 20 || !StringUtils.isNotBlank(val)){
			return false;
		}

		return true;
	}
	private void normalizeImpact() {
		Integer inherentImpactNum = actionColumnsReverse.get("inherentImpact");
		Integer treatedImpactNum = actionColumnsReverse.get("treatedImpact");
		
		if (inherentImpactNum == null && treatedImpactNum == null ){
			return;
		}
		
		boolean preNum = checkAllNumeric(inherentImpactNum);
		boolean postNum = checkAllNumeric(treatedImpactNum);
		
		if (preNum && postNum){
			this.impactSetNumeric = true;
		}

		//gather the various descriptions.
		boolean firstItem = true;
		for (String[] item : this.allItems){
			
			if (firstItem){
				firstItem = false;
				continue;
			}
			
			if (inherentImpactNum != null){

				String val = item[inherentImpactNum].toLowerCase().trim().replaceAll("\\W", "#").replaceAll("[^A-Za-z0-9#]", "");
				String[] compound = val.split("#");
				if (compound.length == 1){
					if (isValidDescription(val)){
						impactSet.add(correct(val));
					}
				} else {
					if (isValidDescription(compound[0]) && isValidDescription(compound[1])){
						impactSet.add(correct(compound[0])+"#"+correct(compound[1]));
					}
					
				}
			}

			
			if (treatedImpactNum != null){
				String val = item[treatedImpactNum].toLowerCase().trim().replaceAll("\\W", "#").replaceAll("[^A-Za-z0-9#]", "");
				String[] compound = val.split("#");
				if (compound.length == 1){
					if (isValidDescription(val)){
						impactSet.add(correct(val));
					}
				} else {
					if (isValidDescription(compound[0]) && isValidDescription(compound[1])){
						impactSet.add(correct(compound[0])+"#"+correct(compound[1]));
					}
					
				}
			}
		}
		
		
	}
	public void execute(){
		
		ModelToleranceMatrix matrix = txmgr.getProjectMatrix(projectID, sess);
		Integer maxImpact = matrix.getMaxImpact();
		Integer maxProb = matrix.getMaxProb();
		
		mgrCandidates = (ArrayList<ModelPerson>)txmgr.getProjectRiskManagers(projectID, sess);
		ownerCandidates = (ArrayList<ModelPerson>)txmgr.getProjectRiskOwners(projectID, sess);
		

		for (String[] item : itemsToImport){
			if (matchRisks){
				findAndUpdateSingleRiskImportCSV(item, maxProb,maxImpact);
			} else {
				installSingleRiskCSV(item, maxProb,maxImpact);
			}
		}
	}
	
	private String translateFieldToValue(String[] item,	String field){
		
		Integer columnNum = actionColumnsReverse.get(field);
		
		if (columnNum != null){
			return item[columnNum];
		} else {
			return null;
		}
	}
	private Boolean getBooleanFieldValue(String[] item,	String field){
		
		Integer columnNum = actionColumnsReverse.get(field);
		
		if (columnNum == null){
			return false;
		} else {
			String val = item[columnNum].toLowerCase();
			
			if (val.contains("true")
				||	val.contains("yes")
				||	val.contains("1")
				||	val.contains("y")
			){
				return true;
			} else {
				return false;
			}
		}
	}
	private Integer getColumnNumber(String field){
		return actionColumnsReverse.get(field);
	}
	private Integer getFieldAction(String field){
		
		Integer colNum = getColumnNumber(field);
		if (colNum == null){
			return IGNORE;
		}
		
		String action = configMap.get("action"+colNum);
		if (action.contains("append")){
			return APPEND;
		}
		if (action.contains("replace")){
			return REPLACE;
		}
		return IGNORE;
	}
	private Long getIDBestPersonMatch(ArrayList<ModelPerson> candidates, String pers){
		
		if (pers == null){
			return userID;
		}
		
		ModelPerson best = null;
		int bestScore = Integer.MAX_VALUE;
		
		for (ModelPerson test:candidates){
			int score =  StringUtils.getLevenshteinDistance(test.name.toLowerCase(), pers.toLowerCase());
			if (score < bestScore){
				bestScore = score;
				best = test;
			}
		}
		
		if (bestScore < 4){
			return best.getStakeholderID();
		} else {
			return userID;
		}
	}
	private Long getIDBestCategoryMatch(ArrayList<ModelMultiLevel> candidates, String cat, boolean prim, long primID){
		
		if (cat == null){
			return null;
		}
		
		ModelMultiLevel best = null;
		int bestScore = Integer.MAX_VALUE;

		for (ModelMultiLevel test:candidates){
			if (prim){
				int score =  StringUtils.getLevenshteinDistance(test.getName().toLowerCase(), cat.toLowerCase());
				if (score < bestScore){
					bestScore = score;
					best = test;
				}
			} else {
				if (test.internalID.equals(primID)){
					for (ModelMultiLevel testSec:test.sec){
						int score =  StringUtils.getLevenshteinDistance(testSec.getName().toLowerCase(), cat.toLowerCase());
						if (score < bestScore){
							bestScore = score;
							best = test;
						}
					}
				}
			}
		}
		
		if (bestScore < 4){
			return best.internalID;
		} else {
			return null;
		}
	}
	private void installSingleRiskCSV(String[] item, Integer maxProb, Integer maxImpact) {

		try {
			ModelRiskLiteBasic risk = new ModelRiskLiteBasic();

			String title = (String)translateFieldToValue(item,  "title");
			if (title == null){
				title = "Please Enter Risk Title";
			}
			risk.setTitle(title);
			risk.setDescription(translateFieldToValue(item,  "description"));
			risk.setCause(translateFieldToValue(item,  "cause"));
			risk.setConsequences(translateFieldToValue(item, "consequences"));
			risk.setProjectID(projectID);
			risk.setMitPlanSummary(translateFieldToValue(item, "mitPlanSummary"));
			risk.setMitPlanSummaryUpdate(translateFieldToValue(item, "mitPlanSummaryUpdate"));
			risk.setImpact(translateFieldToValue(item, "impact"));
			risk.externalID = translateFieldToValue(item, "externalID");
			
			risk.setManager1ID(getIDBestPersonMatch(mgrCandidates, translateFieldToValue(item, "managerID")));
			risk.setOwnerID(getIDBestPersonMatch(ownerCandidates, translateFieldToValue(item, "ownerID")));
			
			risk.matrixID = 0L;
			risk.securityLevel = 0;
			
			String estContingency = translateFieldToValue(item,  "estimatedContingency");
			if (estContingency == null){
				risk.estimatedContingencey = 0.0;
			}
			if (estContingency instanceof String){
				try {
					risk.estimatedContingencey = Double.parseDouble((String)estContingency);
				} catch (Exception e) {
					risk.estimatedContingencey = 0.0;
				}
			}

			risk.setInherentProb(probMap.get(correct(translateFieldToValue(item,  "inherentProb"))));
			risk.setTreatedProb(probMap.get(correct(translateFieldToValue(item,  "treatedProb"))));
			risk.setInherentImpact(impactMap.get(correct(translateFieldToValue(item,  "inherentImpact"))));
			risk.setTreatedImpact(impactMap.get(correct(translateFieldToValue(item,  "treatedImpact"))));
			
			if (risk.getInherentProb() == null){
				risk.setInherentProb(new Double(maxProb));
			}
			if (risk.getInherentImpact() == null){
				risk.setInherentImpact(new Double(maxImpact));
			}
			if (risk.getTreatedProb() == null){
				risk.setTreatedProb(new Double(1.5));
			}
			if (risk.getTreatedImpact() == null){
				risk.setTreatedImpact(new Double(1.5));
			}
			sess.save(risk);
			sess.refresh(risk);
			
			sess.beginTransaction();
			ModelRisk riskRec = txmgr.getRisk(risk.riskID, 1L, risk.projectID, sess);
			
			try {
				Long primCatID = getIDBestCategoryMatch((ArrayList<ModelMultiLevel>)txmgr.getProjectCategorys(projectID, sess), translateFieldToValue(item, "primCat"), true, 0L);
				if (primCatID != null){
					riskRec.setPrimCatID(primCatID);
					Long secCatID = getIDBestCategoryMatch((ArrayList<ModelMultiLevel>)txmgr.getProjectCategorys(projectID, sess),translateFieldToValue(item, "secCat"), false, primCatID);
					if (secCatID != null){
						riskRec.setPrimCatID(secCatID);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			riskRec.setImpCost(getBooleanFieldValue(item, "isImpCost"));
			riskRec.setImpEnvironment(getBooleanFieldValue(item, "isImpEnvironment"));
			riskRec.setImpReputation(getBooleanFieldValue(item, "isImpReputation"));
			riskRec.setImpSafety(getBooleanFieldValue(item, "isImpSafety"));
			riskRec.setImpSpec(getBooleanFieldValue(item, "isImpSpec"));
			riskRec.setImpTime(getBooleanFieldValue(item, "isImpTime"));

			sess.save(riskRec);
			sess.getTransaction().commit();
			
			String mitPlan = translateFieldToValue(item,  "mitigationPlan");
			if (mitPlan != null){
				ModelMitigationStep stepNew = new ModelMitigationStep();
				stepNew.setPersonResponsibleID(risk.manager1ID);
				stepNew.setProjectID(projectID);
				stepNew.percentComplete = 0.0;
				stepNew.setEstimatedCost(0.0);
				stepNew.description = mitPlan;
				stepNew.response = false;
				stepNew.setRiskID(risk.riskID);
				sess.beginTransaction();
				sess.save(stepNew);
				sess.getTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	private void findAndUpdateSingleRiskImportCSV(String[] item, Integer maxProb, Integer maxImpact) {
		
		ModelRiskLiteBasic risk = null;
		
		switch (riskKey){
		case 0:
			risk = (ModelRiskLiteBasic)sess.createCriteria(ModelRiskLiteBasic.class)
			.add(Restrictions.eq("riskID", translateFieldToValue(item, "riskID")))
			.add(Restrictions.eq("projectID", projectID))
			.uniqueResult();

			break;
		case 1:
			risk = (ModelRiskLiteBasic)sess.createCriteria(ModelRiskLiteBasic.class)
			.add(Restrictions.eq("riskProjectCode", translateFieldToValue(item, "riskProjectCode")))
			.add(Restrictions.eq("projectID", projectID))
			.uniqueResult();

			break;
		case 2:
			risk = (ModelRiskLiteBasic)sess.createCriteria(ModelRiskLiteBasic.class)
			.add(Restrictions.eq("externalID", translateFieldToValue(item, "externalID")))
			.add(Restrictions.eq("projectID", projectID))
			.uniqueResult();
			break;
		}
		
		if (risk == null){
			installSingleRiskCSV(item, maxProb, maxImpact);
			return;
		}

		risk.description = determineUpdateValue(item, "description", risk.description );
		risk.title = determineUpdateValue(item, "title", risk.title );
		risk.impact = determineUpdateValue(item, "impact", risk.impact );
		risk.mitPlanSummary = determineUpdateValue(item, "mitPlanSummary", risk.mitPlanSummary );
		risk.mitPlanSummaryUpdate = determineUpdateValue(item, "mitPlanSummary", risk.mitPlanSummaryUpdate );
		risk.cause = determineUpdateValue(item, "cause", risk.cause );
		risk.consequences = determineUpdateValue(item, "consequences", risk.consequences );
		
		if (translateFieldToValue(item,  "inherentProb")!=null){
			risk.setInherentProb(probMap.get(correct(translateFieldToValue(item,  "inherentProb"))));
		} else {
			risk.setInherentProb(new Double(maxProb));
			
		}
		if (translateFieldToValue(item,  "treatedProb")!=null){
			risk.setTreatedProb(probMap.get(correct(translateFieldToValue(item,  "treatedProb"))));
		} else {
			risk.setTreatedProb(new Double (1.5));
		}
		if (translateFieldToValue(item,  "inherentImpact")!=null){
			risk.setInherentImpact(impactMap.get(correct(translateFieldToValue(item,  "inherentImpact"))));
		} else {
			risk.setInherentImpact(new Double (maxImpact));
		}
		if (translateFieldToValue(item,  "treatedImpact")!=null){
			risk.setTreatedImpact(impactMap.get(correct(translateFieldToValue(item,  "treatedImpact"))));
		} else {
			risk.setTreatedImpact(new Double (1.5));			
		}

		sess.beginTransaction();
		sess.save(risk);
		sess.getTransaction().commit();
	}
	private String determineUpdateValue(String[] item,	String field, String riskValue) {
		
		String value= translateFieldToValue(item,  field);
		int action = getFieldAction(field);
		
		if (action ==IGNORE){
			return riskValue;
		}
		if (action == REPLACE || value.startsWith(riskValue)){
			return value;
		}
		if (action == APPEND){
			return riskValue.concat(value);
		}
		
		return null;
	}
	private final ArrayList<String> edits(String word) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i < word.length(); ++i) result.add(word.substring(0, i) + word.substring(i+1));
		for(int i=0; i < word.length()-1; ++i) result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
		for(int i=0; i < word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
		for(int i=0; i <= word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
		return result;
	}
	public final String correct(String word) {
		if (word == null){
			return null;
		}
		try {
			if (Double.parseDouble(word) != 0){
				return word;
			}
		} catch (NumberFormatException e) {
			// OK To Continue
		}
		if (word.equals("v")) word = "very";
		if(nWords.containsKey(word)) return word;
		ArrayList<String> list = edits(word);
		HashMap<Integer, String> candidates = new HashMap<Integer, String>();
		for(String s : list) if(nWords.containsKey(s)) candidates.put(nWords.get(s),s);
		if(candidates.size() > 0) return candidates.get(Collections.max(candidates.keySet()));
		for(String s : list) for(String w : edits(s)) if(nWords.containsKey(w)) candidates.put(nWords.get(w),w);
		return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
	}
	public boolean checkProbAndImpact() {
		
		Integer inherentProbNum = actionColumnsReverse.get("inherentProb");
		Integer treatedProbNum = actionColumnsReverse.get("treatedProb");
		Integer inherentImpactNum = actionColumnsReverse.get("inherentImpact");
		Integer treatedImpactNum = actionColumnsReverse.get("treatedImpact");
		
		if ( (inherentProbNum != null && !checkAllNumeric(inherentProbNum)) || 
				(treatedProbNum != null && !checkAllNumeric(treatedProbNum))|| 
				(inherentImpactNum != null && !checkAllNumeric(inherentImpactNum))|| 
				(treatedImpactNum != null&& !checkAllNumeric(treatedImpactNum))){
			return true;
		} else {
			return false;
		}
	}
	private boolean checkAllNumeric(Integer col){
		
		if (col == null){
			return true;
		}
		
		boolean firstRow = false;
		for (String[] item : itemsToImport){
			if (firstRow){
				continue;
			}

			String val = item[col];
			try {
				if (Double.parseDouble(val) != 0){
					firstRow = true;
					continue;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	public void promptProbAndImpact() {
		
		// Save all the stuff for when the user returns
		
		XStream xs = new XStream();
		xs.toXML(this.info);
		
		String sql = "INSERT INTO attachment (contents, hostID, hostType) VALUES (?,?,?)";
		long key = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1,xs.toXML(this.info) );
			stmt.setString(2, "-1");
			stmt.setString(3, "TEMPFILE");
			stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
			keys.next();
			key = keys.getLong(1);

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Prepares the JS for prompting the user for prob/impact mao
		try {
			
			try {
				ServletOutputStream out = response.getOutputStream();

				String js = "var probValMap = [";
				for (String prob : probSet){
					js = js+"{\"probDesc\":\""+prob+"\"},";
				}
				if (!probSet.isEmpty()){
					js = js.substring(0, js.length()-1);
				}
				js = js+"];";
				
				js = js+"var impactValMap = [";
				for (String impact : impactSet){
					js = js+"{\"impactDesc\":\""+impact+"\"},";
				}
				if (!impactSet.isEmpty()){
					js = js.substring(0, js.length()-1);
				}
				js = js+"];";				
				
				out.println(js+"try{ parent.promptForProbAndImpactMap(probValMap, impactValMap, "+key+");} catch(e){alert(e)}");			
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
