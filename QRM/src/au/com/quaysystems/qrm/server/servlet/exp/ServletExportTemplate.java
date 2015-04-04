package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.QRMExportCategory;
import au.com.quaysystems.qrm.dto.QRMExportConsequence;
import au.com.quaysystems.qrm.dto.QRMExportObjective;
import au.com.quaysystems.qrm.dto.QRMExportProject;

import com.thoughtworks.xstream.XStream;

@SuppressWarnings("serial")
@WebServlet (value = "/exportTemplate", asyncSupported = false)
public class ServletExportTemplate extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		XStream xs = new XStream();
		xs.setMode(XStream.NO_REFERENCES);

		xs.alias("project", QRMExportProject.class);
		xs.alias("consequences", QRMExportConsequence.class);
		xs.alias("objectives", QRMExportObjective.class);
		xs.alias("categories", QRMExportCategory.class);
		xs.alias("risk", ModelRisk.class);
		xs.alias("control", ModelRiskControl.class);
		xs.alias("mitigationstep", ModelMitigationStep.class);


		xs.omitField(QRMExportProject.class, "assignID");
		xs.omitField(QRMExportObjective.class, "assignID");
		xs.omitField(QRMExportObjective.class, "assignParentID");
		xs.omitField(QRMExportConsequence.class, "assignID");
		xs.omitField(QRMExportConsequence.class, "assignParentID");

		response.setHeader("Content-Disposition", "attachment; filename=Project_Template.xml");
		response.setContentType("text/xml");
		try {
			xs.toXML(getTemplate(projectID,sess, userID, objMap),response.getOutputStream());
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}

	}
	@SuppressWarnings("unchecked")
	protected final QRMExportProject getTemplate(Long projectID, Session sess, Long userID, HashMap<Object, Object> objMap){

		try {
			QRMExportProject template = new QRMExportProject();
			ModelRiskProject project = getRiskProjectDetails(projectID, sess);

			for (ModelPerson person : getProjectRiskOwners(projectID, sess)){
				template.personMap.put(person.stakeholderID, person);
			}
			for (ModelPerson person : getProjectRiskManagers(projectID, sess)){
				template.personMap.put(person.stakeholderID, person);
			}
			for (ModelPerson person : getProjectUsers(projectID, sess)){
				template.personMap.put(person.stakeholderID, person);
			}

			/*
			 * Add the risks to the template
			 */
			if ((Boolean)objMap.get("RISKS")){
				Criteria crit0 = sess.createCriteria(ModelRiskLiteBasic.class);
				crit0.add( Restrictions.eq( "projectID", projectID ) );
				List<ModelRiskLiteBasic> rsks = crit0.list();

				for (ModelRiskLiteBasic risk:rsks){
					template.risks.add(getRisk(risk.riskID, userID, projectID, sess));
				}
			}

			template.owners = project.riskownersIDs;
			template.mgrs = project.riskmanagerIDs;
			template.users = project.riskusersIDs;
			template.exportID = projectID;
			template.projectTitle = project.projectTitle;
			template.projectDescription = project.projectDescription;
			template.projectCode = project.projectCode;

			for(ModelMultiLevel cat : project.getRiskCategorys()){
				QRMExportCategory catTemp = new QRMExportCategory(cat.description, cat.internalID);
				if (cat.sec != null) {
					for (ModelMultiLevel subcat : cat.sec) {
						catTemp.subCats.add(new QRMExportCategory(subcat.description, subcat.internalID));
					}
				}
				template.categories.add(catTemp);
			}
			for (ModelQuantImpactType type : project.getImpactTypes()){
				template.consequenceTypes.add(new QRMExportConsequence(type.getInternalID(),type.description, type.units, type.isCostCategroy()));
			}
			for (ModelObjective obj : project.getObjectives()){
				template.objectives.add(new QRMExportObjective(obj.objective, obj.objectiveID, obj.parentID));
			}

			Criteria crit = sess.createCriteria(ModelRiskProject.class);
			crit.add( Restrictions.eq( "parentID", projectID ) );
			List<ModelRiskProject> projs = crit.list();

			for (ModelRiskProject proj : projs){
				template.subProjects.add(getTemplate(proj.projectID, sess, userID, objMap));
			}

			return template;
		} catch (HibernateException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
}
