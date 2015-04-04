package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.simple.parser.JSONParser;

import au.com.quaysystems.qrm.dto.DTOAnalysisConfigElements;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.RESTTransportContainer;
import au.com.quaysystems.qrm.server.servlet.SessionControl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public abstract class QRMRPCServletRepMgr extends HttpServlet {

	public static long numCalls = 0;
	protected static final Long oneMB = 1048576L;
	private final static long serialVersionUID = -7408422743586821213L;
	public static int SESSIONTIMEOUT = 20;
	protected boolean disableNewSessions = false;
	protected int limitSessions = 20;
	protected XStream xsXML = new XStream();
	protected SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected XStream xsJSON = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	});

	protected JSONParser parser = new JSONParser();

	protected Logger log;
	protected String servlet;
	protected ServletConfig sc;

	public void doPost(final HttpServletRequest request, final HttpServletResponse response)  {
		doGet(request, response);
	}
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)  {


		Long userID;
		try {
			userID = SessionControl.sessionMap.get(request.getSession().getId()).person.getStakeholderID();
		} catch (Exception e1) {
			userID = null;
		}
		Long projectID;
		Long riskID;
		HashMap<String, String> stringMap = new HashMap<>();
		HashMap<Object, Object> objMap = new HashMap<>();

		log.info(servlet);

		for (Object key : request.getParameterMap().keySet()) {

			stringMap.put((String) key,	request.getParameter(key.toString()));

			if (request.getParameter(key.toString()).equalsIgnoreCase("true")) {
				objMap.put(key, new Boolean(true));
			} else if (request.getParameter(key.toString()).equalsIgnoreCase("false")) {
				objMap.put(key, new Boolean(false));
			} else {
				objMap.put(key,	request.getParameter(key.toString()));
			}

		}

		try {
			projectID = Long.parseLong(stringMap.get("PROJECTID"));
		} catch (Exception e3) {
			projectID = null;
		}

		try {
			riskID = Long.parseLong(stringMap.get("RISKID"));
		} catch (Exception e3) {
			riskID = null;
		}

		response.setHeader("Cache-Control", "no-cache");

		try  {
			execute( request, response, null, userID, stringMap, objMap, projectID, riskID);
		} catch (Exception e) {
			log.error( e);
		} 
	}

	public void getStats(HttpServletResponse response){
		try {
			response.getWriter().print(numCalls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void resetStats(HttpServletResponse response){
		try {
			numCalls = 0;
			response.getWriter().print("OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	abstract void execute(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID);

	@Override
	public void init(final ServletConfig sc) {

		this.sc = sc;

		log = Logger.getLogger("au.com.quaysystems.qrm");

		String[] servletStr = sc.getServletName().split("\\.");
		servlet = servletStr[servletStr.length-1];
		log.info("Starting "+ servlet);


		formatUTC.setCalendar(Calendar.getInstance(new SimpleTimeZone(0, "UTC")));

		xsXML.alias("response", RESTTransportContainer.class);

		xsXML.alias("record", DTOAnalysisConfigElements.class);
		xsXML.alias("record", ModelRiskProject.class);
		xsXML.alias("record", ModelRiskLite.class);
		xsXML.alias("record", ModelObjective.class);
		xsXML.alias("record", ModelQRMReport.class);
		xsXML.alias("record", ModelReview.class);

		xsXML.omitField(ModelRiskLite.class,"cause");
		xsXML.omitField(ModelRiskLite.class,"consequences");
		xsXML.omitField(ModelRiskLite.class,"extObject");

		xsJSON.alias("person", ModelPerson.class);
		xsJSON.alias("project", ModelRiskProject.class);
		xsJSON.alias("matrix", ModelToleranceMatrix.class);
		xsJSON.alias("risk", ModelRiskLite.class);
		xsJSON.alias("risk", ModelRisk.class);
		xsJSON.alias("mitplanstep", ModelMitigationStep.class);
		xsJSON.alias("riskcontrol", ModelRiskControl.class);
		xsJSON.alias("data", RESTTransportContainer.class);

		xsJSON.omitField(ModelJobQueue.class, "jobJdbcURL");

		// For security reasons
		xsJSON.omitField(ModelRepository.class, "orgID");


		try {
			super.init(sc);
		} catch (ServletException e) {
			log.error("QRM Stack Trace", e);
		}

	}

	protected final void outputJSON(final Object obj, HttpServletResponse res) {
		try {
			res.getWriter().println(xsJSON.toXML(obj));
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputXML(final Object obj, HttpServletResponse res) {
		try {
			res.getWriter().println(xsXML.toXML(obj));
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected static void closeAll(final ResultSet resultSet,
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
	protected Long getLongJS(final Object obj) {
		Long val = null;
		try {
			if (obj instanceof Long) {
				val = ((Long) obj);
			}
			if (obj instanceof String) {
				val = Long.parseLong(((String) obj));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			val = null;
		}
		return val;
	}
	protected long getRepositoryID(String url){

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			String sql = "SELECT repID FROM repositories WHERE url = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			long repID = rs.getLong("repID");
			closeAll(rs,stmt, conn);

			return (repID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}
		return -1L;
	}
}
