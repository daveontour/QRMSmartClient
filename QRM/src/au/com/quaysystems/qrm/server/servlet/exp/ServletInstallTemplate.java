package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.QRMExportCategory;
import au.com.quaysystems.qrm.dto.QRMExportConsequence;
import au.com.quaysystems.qrm.dto.QRMExportObjective;
import au.com.quaysystems.qrm.dto.QRMExportProject;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.servlet.SessionControl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@SuppressWarnings("serial")
@WebServlet (value = "/installTemplate", asyncSupported = false)
public class ServletInstallTemplate extends QRMRPCServlet {

	protected static final Long fileSizeLimit = 20L; 


	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			XStream xs = new XStream() {
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
			xs.setMode(XStream.NO_REFERENCES);
			xs.alias("risk", ModelRisk.class);
			xs.alias("control", ModelRiskControl.class);
			xs.alias("mitigationstep", ModelMitigationStep.class);
			xs.alias("project", QRMExportProject.class);
			xs.alias("consequences", QRMExportConsequence.class);
			xs.alias("objectives", QRMExportObjective.class);
			xs.alias("categories", QRMExportCategory.class);


			boolean isMultipart = ServletFileUpload.isMultipartContent(request);

			if (isMultipart) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setFileCleaningTracker(new FileCleaningTracker());
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setSizeMax(fileSizeLimit*oneMB);

				try {
					HashMap<String, String> map = new HashMap<String, String>();
					InputStream uploadedStream = null;

					for (FileItem item : (List<FileItem>) upload.parseRequest(request)) {
						if (item.isFormField()) {
							map.put(item.getFieldName(), item.getString());
						} else {
							uploadedStream = item.getInputStream();
						}
					}

					QRMExportProject template = (QRMExportProject)xs.fromXML(uploadedStream);
					Long userID2 = SessionControl.sessionMap.get(request.getSession().getId()).person.getStakeholderID();
					Long rootParentID = Long.parseLong(map.get("projectID"));

					try { 
						ServletOutputStream out = response.getOutputStream();
						String message = installTemplateInternal(template, template, rootParentID, userID2, Boolean.parseBoolean(map.get("inludeTemplateRoot")),Boolean.parseBoolean(map.get("inludeRisks")),Boolean.parseBoolean(map.get("inheritStakeholders")), request, sess, response, projectID);
						out.println("<html><body><script>parent.isc.say('"+message+"');parent.waitingOnTemplateUploadWindow.hide();parent.getUserMgrProjects();</script></body></html>");
						return;
					} catch (IOException e) {
						log.error("QRM Stack Trace", e);
					}
				} catch (SizeLimitExceededException e){
					try {
						ServletOutputStream out = response.getOutputStream();
						out.println("<html><body><script>parent.isc.say('Upload Failed. File size limit exceeded ( "+fileSizeLimit+"MB )');try{ parent.waitingOnTemplateUploadWindow.hide();parent.getUserMgrProjects();} catch(e){}</script></body></html>");			
					} catch (IOException e1) {
						log.error("QRM Stack Trace", e1);
					}
					return;			
				} catch (Exception e) {
					try {
						log.error("QRM Stack Trace", e);
						response.getOutputStream().println("<html><body><script>parent.isc.say('Unable to install template. Not a QRM Project Template');parent.waitingOnTemplateUploadWindow.hide();parent.getUserMgrProjects();</script></body></html>");
					} catch (IOException e1) {
						log.error("QRM Stack Trace", e1);
					}
				}
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			try {
				response.getOutputStream().println("<html><body><script>alert('Unable to install template');parent.getUserMgrProjects();</script></body></html>");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e1);
			}
		}

	}
	
	protected String  installTemplateInternal(QRMExportProject template, QRMExportProject templateRoot, Long rootProjectID, Long userID, boolean includeTemplateRoot, boolean includeRisks, boolean inheritStakeholders, HttpServletRequest request, Session sess, HttpServletResponse response, Long projectID){

		try {

			Date startDate = new Date();
			Date endDate = new Date(new Date().getTime() + 365 * 24 * 60 * 60 * 1000);


			// This is to allow a complete template to be attached to a 
			// newly created project - which already has a root project created.

			if (includeTemplateRoot) {
				log.info(">>>>>>>>Installing Project "+ template.projectTitle+"<<<<<<<<<<<");

				Connection conn = getSessionConnection(request);
				ResultSet rs = null;
				// Prevent same project code being used.
				do {
					PreparedStatement ps = conn.prepareStatement("select projectCode from riskproject where projectCode = ?");
					ps.setString(1, template.projectCode);
					rs = ps.executeQuery();
					if (rs.first()){
						template.projectCode = template.projectCode+"X";
						if (template.projectCode.length() > 6){
							String st = "ABCDEFGHIJKLMNOPQRSTUVWQYZ";
							Random rand = new Random();
							rand.setSeed(new Date().getTime());

							String code = template.projectCode.substring(0,2);
							int idx = rand.nextInt(26);
							code = code + st.substring(idx, idx + 1);
							idx = rand.nextInt(26);
							code = code + st.substring(idx, idx + 1);
							idx = rand.nextInt(26);
							code = code + st.substring(idx, idx + 1);

							template.projectCode = code;
						}	
					}
				} while( rs.first()); 
				rs.close();
				// Prevent same project title being used.
				do {
					PreparedStatement ps = conn.prepareStatement("select projectTitle from riskproject where projectTitle = ?");
					ps.setString(1, template.projectTitle);
					rs = ps.executeQuery();
					if (rs.first()){
						template.projectTitle = template.projectTitle+"*";
					}
				} while( rs.first()); 
				rs.close();

				CallableStatement cstmt = null;
				try {
					cstmt = conn.prepareCall("{call addProject(?,?,?,?,?,?,?,?,?)}");
					cstmt.setLong(1, rootProjectID);
					cstmt.setString(2, template.projectDescription);
					cstmt.setString(3, template.projectTitle);
					cstmt.setString(4, template.projectCode);
					cstmt.setLong(5, userID);
					cstmt.setDate(6, new java.sql.Date(startDate.getTime()));
					cstmt.setDate(7, new java.sql.Date(endDate.getTime()));
					cstmt.setInt(8, 0);
					cstmt.registerOutParameter(9, java.sql.Types.BIGINT);
					cstmt.execute();
					template.assignID = cstmt.getLong(9);

					closeAll(null, cstmt, null);
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				} finally {
					closeAll(null,null,conn);
				}
			} else {
				template.assignID = rootProjectID;
			}

			// Add Stakeholder. If stakeholder already exists (emails and name match) existing stakeholder ID
			// is returned, otherwise new user created with password "password"
			Connection conn =  PersistenceUtil.getQRMLoginCPDS().getConnection();
			try {
				String url = (String)request.getSession().getAttribute("session.url");
				Long repID = getRepositoryID(url);
				for (ModelPerson person:template.personMap.values()){
					try {					
						Long newID = (person.stakeholderID != 1)?createUser(conn, person.name, person.email, "password", repID, url):1L;
						if (newID > 0){
							person.stakeholderID = newID;
						} else if (person.stakeholderID != 1){
							person.stakeholderID = userID;
						} 
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			} finally {
				closeAll(null,null,conn);
			}

			ArrayList<ModelQuantImpactType> projectTypes = getProjectQuantTypes(template.assignID,  sess);
			HashMap<String, ModelQuantImpactType> projTypeMap = new HashMap<String, ModelQuantImpactType>();		
			for(ModelQuantImpactType type : projectTypes){
				projTypeMap.put(type.description, type);
			}

			sess.beginTransaction();
			for (QRMExportConsequence cons: template.consequenceTypes){

				ModelQuantImpactType typeExist = projTypeMap.get(cons.description); 
				if(typeExist != null && typeExist.units.equals(cons.units) && typeExist.generation <= 0){
					cons.assignID = typeExist.getInternalID();
				} else {
					ModelQuantImpactType type = new ModelQuantImpactType();
					type.description = cons.description;
					type.units = cons.units;
					type.costCategroy = cons.costType;
					type.projectID = template.assignID;
					type.generation = -1;

					cons.assignID = (Long)sess.save(type);
				}
			}
			sess.getTransaction().commit();

			List<ModelMultiLevel> projectCats = getProjectCategorys(template.assignID,  sess);
			HashMap<String, ModelMultiLevel> projCatMap = new HashMap<String, ModelMultiLevel>();		
			for(ModelMultiLevel cat : projectCats){
				projCatMap.put(cat.description, cat);
			}

			for(QRMExportCategory cat:template.categories){
				ModelMultiLevel catExist = projCatMap.get(cat.description);
				Long primID = null;

				if(catExist != null && catExist.generation <= 0){
					primID = catExist.internalID;
					cat.assignID = primID;

					HashMap<String, ModelMultiLevel> subCatMap = new HashMap<String, ModelMultiLevel>();

					if (catExist.sec != null){
						for(ModelMultiLevel subCat : catExist.sec){
							subCatMap.put(subCat.description, subCat);
						}
					}

					if (cat.subCats != null) {
						sess.beginTransaction();

						for (QRMExportCategory subcat : cat.subCats) {
							ModelMultiLevel catSubExist = subCatMap.get(subcat.description);
							if (catSubExist != null && catExist.generation <= 0) {
								subcat.assignID = catSubExist.internalID;
							} else {
								ModelMultiLevel catSaveSub = new ModelMultiLevel();
								catSaveSub.setDescription(subcat.description);
								catSaveSub.setContextID(template.assignID);
								catSaveSub.setParentID(primID);
								catSaveSub.generation = -1;
								subcat.assignID = (Long)sess.save(catSaveSub);
							}
						}
						sess.getTransaction().commit();
					}
				} else {
					sess.beginTransaction();
					ModelMultiLevel catSave = new ModelMultiLevel();
					catSave.setDescription(cat.description);
					catSave.setContextID(template.assignID);
					catSave.setParentID(1L);
					catSave.generation = -1;
					primID = (Long) sess.save(catSave);
					sess.getTransaction().commit();
					cat.assignID = primID;
					if (cat.subCats != null) {
						sess.beginTransaction();
						for (QRMExportCategory subcat : cat.subCats) {
							ModelMultiLevel catSaveSub = new ModelMultiLevel();
							catSaveSub.setDescription(subcat.description);
							catSaveSub.setContextID(template.assignID);
							catSaveSub.setParentID(primID);
							subcat.assignID = (Long)sess.save(catSaveSub);
						}
						sess.getTransaction().commit();
					}
				}
			}

			conn = getSessionConnection(request);
			ArrayList<ModelObjective> projObjs = getProjectObjectives(template.assignID, sess);
			HashMap<String, ModelObjective> objMap = new HashMap<String, ModelObjective>();
			for (ModelObjective obj:projObjs){
				objMap.put(obj.objective, obj);
			}
			try {
				for (QRMExportObjective objective:template.objectives){

					ModelObjective objExists = objMap.get(objective.objective);

					if (objExists != null && objExists.generation <= 0){

						objective.assignParentID = templateRoot.findNewObjectiveParentID(objective.exportParentID);
						if (objective.assignParentID == null){
							objective.assignParentID = 1L;
						}

						objective.assignID = objExists.objectiveID;

					} else {

						objective.assignParentID = templateRoot.findNewObjectiveParentID(objective.exportParentID);
						if (objective.assignParentID == null){
							objective.assignParentID = 1L;
						}
						CallableStatement cstmt = conn.prepareCall("{call insertObjective(?,?,?,?)}");

						cstmt.setLong(1, template.assignID);
						cstmt.setString(2, objective.objective);
						cstmt.setLong(3, objective.assignParentID);
						cstmt.registerOutParameter(4, Types.NUMERIC);

						cstmt.execute();

						objective.assignID =  cstmt.getLong(4);
					}
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} finally {
				closeAll(null,null,conn);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return "Template Installation Failed";
		} finally {
			//			PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));
		}

		/*
		 * Owners, Managers and Users
		 */
		try {
			Connection conn = getSessionConnection(request);
			conn.setAutoCommit(true);
			PreparedStatement st = conn.prepareStatement("DELETE FROM projectowners WHERE projectID = ?");
			if (!inheritStakeholders){
				st.setLong(1, template.assignID);
				st.executeUpdate();
			}

			try {
				PreparedStatement sto = conn.prepareStatement("INSERT INTO projectowners (projectID, stakeholderID) VALUES (?,?)");
				for (Long id : template.owners) {
					sto.setLong(1, template.assignID);
					sto.setLong(2, template.personMap.get(id).stakeholderID);
					try {
						sto.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
						try {
							// addUnSyncedStakeholderToRepository(objJS);
							// sto.executeUpdate();
						} catch (Exception e1) {
							log.error("QRM Stack Trace", e1);
						}
					}
				}

				st = conn.prepareStatement("DELETE FROM projectriskmanagers WHERE projectID = ?");
				if (!inheritStakeholders){
					st.setLong(1, template.assignID);
					st.executeUpdate();
				}
				PreparedStatement stm = conn.prepareStatement("INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (?,?)");
				for (Long id : template.mgrs) {
					stm.setLong(1, template.assignID);
					stm.setLong(2, template.personMap.get(id).stakeholderID);
					try {
						stm.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}

				st = conn.prepareStatement("DELETE FROM projectusers WHERE projectID = ?");
				if (!inheritStakeholders){
					st.setLong(1, template.assignID);
					st.executeUpdate();
				}
				PreparedStatement stu = conn.prepareStatement("INSERT INTO projectusers (projectID, stakeholderID) VALUES (?,?)");
				for (Long id:template.users) {
					stu.setLong(1, template.assignID);
					stu.setLong(2, template.personMap.get(id).stakeholderID);
					try {
						stu.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
				closeAll(null, st, conn);
				closeAll(null, sto, null);
				closeAll(null, stu, null);
				closeAll(null, stm, null);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		/*
		 * Install any risks
		 */
		if (includeRisks){
			for (ModelRisk risk:template.risks){
				try {
					risk.setProjectID(template.assignID);
					installSingleRisk(risk, template, userID,  projectID,  sess,  response);
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}			
			}
		}

		for (QRMExportProject temp : template.subProjects){
			installTemplateInternal(temp, templateRoot,template.assignID, userID, true, includeRisks, inheritStakeholders, request, sess,  response, projectID);
		}
		return "Template Installed";
	}
	protected long createUser(Connection conn, String name, String email, String pass, long repID, String url) throws SQLException{

		// Database stored procedure to create user
		// conn is to cpds

		long userID = -200L;
		CallableStatement cs;
		try {
			cs = conn.prepareCall("call createUser(?,?,?,?)");
			cs.setString(1, name);
			cs.setString(2, pass);
			cs.setString(3, email);
			cs.registerOutParameter(4, Types.BIGINT);
			cs.execute();
			userID = cs.getLong(4);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


		if (userID < 0) return userID;

		Connection conn2 = PersistenceUtil.getConnection(url);

		PreparedStatement ps0 = conn2.prepareStatement("SELECT * FROM userrepository where stakeholderID = ? AND repID = ?");
		ps0.setLong(1, userID);
		ps0.setLong(2, repID);

		ResultSet rs = ps0.executeQuery();
		if (rs.first()){
			conn2.close();
			return userID;
		}

		PreparedStatement ps = conn2.prepareStatement("INSERT INTO userrepository (stakeholderID, repID) VALUES (?,?)");
		ps.setLong(1, userID);
		ps.setLong(2, repID);
		ps.execute();
		conn2.close();

		return userID;
	}

}
