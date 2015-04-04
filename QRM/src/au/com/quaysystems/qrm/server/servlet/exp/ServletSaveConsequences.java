package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRiskConsequence;

@SuppressWarnings("serial")
@WebServlet (value = "/saveConsequences", asyncSupported = false)
public class ServletSaveConsequences extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<Double> preArray = new ArrayList<Double>();
		ArrayList<Double> postArray = new ArrayList<Double>();
		Double preProb = 0.0;
		Double postProb = 0.0;
		String preType = "au.com.quaysystems.qrm.util.probability.NullDistribution";
		String postType = "au.com.quaysystems.qrm.util.probability.NullDistribution";

		String consStr = null;
		Long consType = 2L;
		JSONObject input = null;

		// These session attributes are set when the user calls "calcContingency". By using saveConsequence
		// the contigency has changed, so the value is nulled out.
		request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
		request.getSession().setAttribute("riskContingencyPostMitCostResult", null);


		Long id = null;
		try {
			input = (JSONObject) parser.parse(request.getParameter("DATA"));

			try {
				id = Long.parseLong((String) input.get("internalID"));
			} catch (Exception e3) {
				try {
					id = (Long) input.get("internalID");
				} catch (Exception e) {
					id = null;
				}
			}

			if (id.longValue() == 0) {
				id = null;
			}

			try {
				consStr = (String) input.get("description");
			} catch (Exception e) {
				consStr = "Consequence Description";
			}
			try {
				consType = (Long) input.get("quantType");
			} catch (Exception e) {
				try {
					consType = Long.parseLong(((String) input.get("quantType")));
				} catch (Exception e1) {
					consType = 2L;
				}
			}

			JSONObject preMit1 = (JSONObject) input.get("preMitData1");

			try {
				preType = (String) preMit1.get("distType");
			} catch (Exception e2) {
				preType = "au.com.quaysystems.qrm.util.probability.NullDistribution";
			}

			try {
				preProb = Double.parseDouble((String) preMit1.get("probability"));
			} catch (Exception e) {
				try {
					preProb = ((Long) preMit1.get("probability")).doubleValue();
				} catch (Exception e1) {
				}
			}

			preArray.add(getDoubleJS(preMit1.get("mean")));
			preArray.add(getDoubleJS(preMit1.get("stdDev")));
			preArray.add(getDoubleJS(preMit1.get("min")));
			preArray.add(getDoubleJS(preMit1.get("most")));
			preArray.add(getDoubleJS(preMit1.get("max")));
			preArray.add(getDoubleJS(preMit1.get("lower")));
			preArray.add(getDoubleJS(preMit1.get("upper")));
			preArray.add(getDoubleJS(preMit1.get("simple")));

			JSONObject postMit1 = (JSONObject) input.get("postMitData1");

			try {
				postType = (String) postMit1.get("distType");
			} catch (Exception e2) {
				postType = "au.com.quaysystems.qrm.util.probability.NullDistribution";
			}

			try {
				postProb = Double.parseDouble((String) postMit1.get("probability"));
			} catch (Exception e) {
				try {
					postProb = ((Long) postMit1.get("probability")).doubleValue();
				} catch (Exception e1) {}
			}

			postArray.add(getDoubleJS(postMit1.get("mean")));
			postArray.add(getDoubleJS(postMit1.get("stdDev")));
			postArray.add(getDoubleJS(postMit1.get("min")));
			postArray.add(getDoubleJS(postMit1.get("most")));
			postArray.add(getDoubleJS(postMit1.get("max")));
			postArray.add(getDoubleJS(postMit1.get("lower")));
			postArray.add(getDoubleJS(postMit1.get("upper")));
			postArray.add(getDoubleJS(postMit1.get("simple")));

		} catch (Exception e) {
			// TODO: handle exception
		}

		try {

			ModelRiskConsequence c = new ModelRiskConsequence();
			c.setInternalID(id);
			c.setDescription(consStr);
			c.quantType = consType;
			c.setRiskID(Long.parseLong(request.getParameter("RISKID")));
			c.setCostDistributionParamsArray(preArray);
			c.setPostCostDistributionParamsArray(postArray);
			c.setCostDistributionType(preType);
			c.setRiskConsequenceProb(preProb);
			c.setPostCostDistributionType(postType);
			c.setPostRiskConsequenceProb(postProb);
			c.setQuantifiable(true);

			c.decompile();

			try {
				if (c.getCostDistributionType().contains("Discrete")){
					c.costDistributionParams = ((JSONArray) input.get("preMitData2")).toJSONString();
				}
			} catch (NullPointerException e) {}
			catch(Exception e){
				log.error("QRM Stack Trace", e);
			}
			try {
				if (c.getPostCostDistributionType().contains("Discrete")){
					c.postCostDistributionParams = ((JSONArray) input.get("postMitData2")).toJSONString();
				}
			} catch (NullPointerException e) {}
			catch(Exception e){
				log.error("QRM Stack Trace", e);
			}

			sess.clear();
			Transaction tx = sess.beginTransaction();
			sess.saveOrUpdate(c);
			tx.commit();
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}

		outputJSON(getRiskConsequences(Long.parseLong(stringMap.get("RISKID")), sess),response);

	}
}
