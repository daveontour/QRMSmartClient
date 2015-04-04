package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelWelcomeData;
import au.com.quaysystems.qrm.server.servlet.MetricEval;

@SuppressWarnings("serial")
@WebServlet (value = "/getOverviewData", asyncSupported = false)
public class ServletGetOverviewData extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		Boolean descendants = Boolean.parseBoolean(stringMap.get("DESCENDANTS"));
		if (descendants == null){
			descendants = false;
		}

		ModelRiskProject proj = getRiskProject(projectID,sess);

		List<ModelWelcomeData> data = new ArrayList<ModelWelcomeData>(); 

		ModelWelcomeData item0 = new ModelWelcomeData();
		item0.data = "<b>Project Title</b>";
		item0.value = proj.getProjectTitle();
		data.add(item0);

		ModelWelcomeData item1 = new ModelWelcomeData();
		item1.data = "<b>Project Description</b>";
		item1.value = proj.getProjectDescription();
		data.add(item1);

		ModelWelcomeData item2 = new ModelWelcomeData();
		item2.data = "<b>Project Risk Manager</b>";
		item2.value = getPerson(proj.getProjectRiskManagerID(),sess).name;
		data.add(item2);

		ModelWelcomeData item3 = new ModelWelcomeData();
		item3.data = "<b>Project Start Date</b>";
		item3.value = df.format(proj.projectStartDate);
		data.add(item3);

		ModelWelcomeData item4 = new ModelWelcomeData();
		item4.data = "<b>Project End Date</b>";
		item4.value = df.format(proj.getProjectEndDate());
		data.add(item4);

		ModelWelcomeData item5 = new ModelWelcomeData();
		item5.data = "<b>Project Code</b>";
		item5.value = proj.getProjectCode();
		data.add(item5);


		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID, projectID, descendants);

		ModelWelcomeData item6 = new ModelWelcomeData();
		item6.data = "<b>Number of Sub Projects</b>";
		item6.value = ""+me.getNumProjects();
		data.add(item6);

		ModelWelcomeData item8 = new ModelWelcomeData();
		item8.data = "<b>Percentage of Mitigation Plans</b>";
		item8.value = me.getPercentApproveMitPlanMetric()+"%";
		data.add(item8);

		ModelWelcomeData item7 = new ModelWelcomeData();
		item7.data = "<b>Total Number of Risks</b>";
		item7.value = ""+me.getNumRisks();
		data.add(item7);

		HashMap<Integer, Double> allocation = me.toleranceAllocation();

		ModelWelcomeData item9 = new ModelWelcomeData();
		item9.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Extreme</b>";
		item9.value = ""+allocation.get(5).intValue();
		data.add(item9);

		ModelWelcomeData item11 = new ModelWelcomeData();
		item11.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - High</b>";
		item11.value = ""+allocation.get(4).intValue();
		data.add(item11);

		ModelWelcomeData item12 = new ModelWelcomeData();
		item12.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Significant</b>";
		item12.value = ""+allocation.get(3).intValue();
		data.add(item12);

		ModelWelcomeData item13 = new ModelWelcomeData();
		item13.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Moderate</b>";
		item13.value = ""+allocation.get(2).intValue();
		data.add(item13);

		ModelWelcomeData item14 = new ModelWelcomeData();
		item14.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Low</b>";
		item14.value = ""+allocation.get(1).intValue();
		data.add(item14);

		//

		HashMap<String, Double> allocation2 = me.statusAllocation();

		ModelWelcomeData item15 = new ModelWelcomeData();
		item15.data = "<b>Number of Risks by Exposure Status</b>";
		item15.value = "";
		data.add(item15);

		ModelWelcomeData item16 = new ModelWelcomeData();
		item16.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Active</b>";
		item16.value = ""+allocation2.get("Active").intValue();
		data.add(item16);

		ModelWelcomeData item17 = new ModelWelcomeData();
		item17.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Pending</b>";
		item17.value = ""+allocation2.get("Pending").intValue();
		data.add(item17);

		ModelWelcomeData item18 = new ModelWelcomeData();
		item18.data = "<b>&nbsp;&nbsp;&nbsp;Number of Risks - Inactive</b>";
		item18.value = ""+allocation2.get("Inactive").intValue();
		data.add(item18);

		outputJSON(data,response);

	}
}
