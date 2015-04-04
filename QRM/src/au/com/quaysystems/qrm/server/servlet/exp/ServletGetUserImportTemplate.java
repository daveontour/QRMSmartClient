package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelImportTemplate;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getUserImportTemplate", asyncSupported = false)
public class ServletGetUserImportTemplate extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		@SuppressWarnings("unchecked")
		ArrayList<ModelImportTemplate> templates = new ArrayList<ModelImportTemplate>(sess.getNamedQuery("getUserTemplates").setLong("var_userid", userID).list());
		if (templates.size() == 0){
			ModelImportTemplate temp = new ModelImportTemplate();
			temp.setTemplateName("No Templates Found");
			templates.add(temp);
		}
		outputXML(new RESTTransportContainer(templates),response);


	}
}
