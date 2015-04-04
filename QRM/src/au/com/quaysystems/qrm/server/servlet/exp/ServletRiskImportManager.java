package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.bytecode.opencsv.CSVReader;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.server.PersistenceUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;


@WebServlet (value = "/importRisk", asyncSupported = false)
public class ServletRiskImportManager extends HttpServlet {

	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	private static Long fileSizeLimit = 20L; 
	private static Long oneMB = 1048576L;


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1387308835754413189L;
	protected XStream xsJSON = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	});

	@Override
	public final void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}

	@Override
	public final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}
	@Override
	public final void init() {
		log.info("Risk Import Manager Processor Started");
	}

	public final void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) {

		if (request.getParameter("uploadRisks") != null) {
			saveAttachment(request, response);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveAttachment(final HttpServletRequest request,	final HttpServletResponse response) {

		List<ModelRisk> risks = null;

		HashMap<String, String> map = new HashMap<String, String>();

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setFileCleaningTracker(new FileCleaningTracker());
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(fileSizeLimit*oneMB);

		Connection conn = null;

		long key = 0;

		try {
			InputStream uploadedStream = null;
			int sizeInBytes = 0;

			for (FileItem item : (List<FileItem>) upload.parseRequest(request)) {
				if (item.isFormField()) {
					map.put(item.getFieldName(), item.getString());
				} else {
					map.put("name", item.getName());
					sizeInBytes = new Long(item.getSize()).intValue();
					uploadedStream = item.getInputStream();
				}
			}

			conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			conn.setAutoCommit(false);

			String sql = "INSERT INTO attachment (contents, hostID, hostType) VALUES (?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setBinaryStream(1, uploadedStream,sizeInBytes);
			stmt.setString(2, "-1");
			stmt.setString(3, "TEMPFILE");
			stmt.executeUpdate();

			ResultSet keys = stmt.getGeneratedKeys();
			keys.next();
			key = keys.getLong(1);

			conn.commit();
			uploadedStream.close();


			ResultSet res = conn.createStatement().executeQuery("SELECT contents FROM attachment WHERE internalID = "+ key);
			res.first();
			InputStream stream = res.getBinaryStream("contents");

			if (map.get("name").contains(".xml")){
				XStream xsXML = new XStream() {
					protected MapperWrapper wrapMapper(MapperWrapper next) {
						return new MapperWrapper(next) {
							@SuppressWarnings("rawtypes")
							public boolean shouldSerializeMember(Class definedIn, String fieldName) {
								if (definedIn == Object.class) {
									return false;
								}
								return super.shouldSerializeMember(definedIn, fieldName);
							}
						};
					}
				};
				xsXML.alias("risk", ModelRisk.class);
				xsXML.alias("control", ModelRiskControl.class);
				xsXML.alias("mitigationstep", ModelMitigationStep.class);
				xsXML.alias("riskconsequence", ModelRiskConsequence.class);
				xsXML.setMode(XStream.NO_REFERENCES);

				risks = (List<ModelRisk>)xsXML.fromXML(stream);

				// Add risk ID in case none has been put in.
				
				JSONArray arr = new JSONArray();

				Long internalID = -1L;
				for (ModelRisk risk:risks){
//					risk.setInternalID(internalID--);
					
					JSONObject riskJS = new JSONObject();
					riskJS.put("riskID", risk.riskID);
					riskJS.put("title", risk.title);
					riskJS.put("description", risk.description);
					arr.add(riskJS);
				}

				System.out.println(xsXML.toXML(risks));
				PreparedStatement stmt2 = conn.prepareStatement("UPDATE attachment  SET contents = ? WHERE internalID = ?");
				stmt2.setString(1, xsXML.toXML(risks));
				stmt2.setLong(2, key);
				stmt2.executeUpdate();

				try {
					
					JSONObject controlJS = new JSONObject();
					controlJS.put("type", "XML");
					controlJS.put("key", key);
					controlJS.put("myArr", arr);
					
					System.out.println(controlJS.toJSONString());
					
					response.getWriter().write(controlJS.toJSONString());
					response.flushBuffer();

				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}



			} else if (map.get("name").contains(".csv")) {
				// Process a CSV file
				
				int columnCount = 0;
				JSONArray JSONArr = new JSONArray();

				try {
					CSVReader reader = new CSVReader(new InputStreamReader(stream));

					List<String[]> items = reader.readAll();
					columnCount = items.get(0).length;

					try {

						Integer index = 0;
						for (String[] entry: items){
							
							JSONObject jsObj = new JSONObject();
							
							jsObj.put("id", index.toString());
							index++;
							
							int colNum = 0;
							for(String element:entry){
								jsObj.put("column"+colNum++, element);
							}
							JSONArr.add(jsObj);

						}
						reader.close();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				try {
					
					JSONObject controlJS = new JSONObject();
					controlJS.put("type", "CSV");
					controlJS.put("colCount", columnCount);
					controlJS.put("key", key);
					controlJS.put("myArr", JSONArr);
					
					response.getWriter().write(controlJS.toJSONString());
					response.flushBuffer();

				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}

			} else {
				ServletOutputStream out = response.getOutputStream();
				out.print("FORMAT_ERROR");
				out.close();
			}

			stream.close();
			conn.close();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			try {
				ServletOutputStream out = response.getOutputStream();
				out.print("FORMAT_ERROR");
				out.close();
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}	
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
}
