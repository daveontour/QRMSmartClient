package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.servlet.exp.PopulateProject;

@SuppressWarnings("serial")
@WebServlet (value = "/normaliseProject", asyncSupported = false)
public class ServletNormaliseProject extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
			PopulateProject pp = new PopulateProject();
			pp.normaliseProject(request, response, sess, userID, stringMap, objMap, projectID);
		}
}
