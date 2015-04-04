/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllRiskProjects", resultClass = ModelRiskProject.class, query = "SELECT  * FROM riskproject  WHERE projectID >0"),
	@NamedNativeQuery(name = "getAllRiskProjectsForUserLite", resultClass = ModelRiskProject.class, query = "SELECT DISTINCT * FROM riskproject  WHERE projectID > 0 AND projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = :user_id  UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = :user_id UNION SELECT projectID from projectowners WHERE stakeholderID = :user_id))"),
	@NamedNativeQuery(name = "getAllMgmtRiskProjectsForUserLite", resultClass = ModelRiskProject.class, query = "SELECT * FROM riskproject where projectID > 0 AND projectID IN (SELECT subprojectID FROM subprojects WHERE projectID IN (select projectID from riskproject where projectRiskManagerID = :userID)) UNION SELECT * from riskproject where projectRiskManagerID = :userID  AND projectID > 0"),
	@NamedNativeQuery(name = "getRiskProject", resultClass = ModelRiskProject.class, query = "SELECT * FROM riskproject  WHERE projectID = :projectID "),
	@NamedNativeQuery(name = "checkRiskProjectCode", resultClass = ModelRiskProject.class, query = "SELECT * FROM riskproject  WHERE projectID != :projectID AND projectCode = :projectCode")
})
@Entity
@Table(name = "riskproject")
public class ModelRiskProject implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6470627815955949734L;

	@Id
	public Long projectID;
	public Date dateEntered;
	public Date dateUpdated;
	public Long projectRiskManagerID;
	public Long parentID;
	public long tabsToUse;
	public Boolean singlePhase;
	public Boolean useAdvancedConsequences;
	public Boolean useAdvancedLiklihood;
	public String projectCode;
	public String projectDescription;
	public Date projectEndDate;
	public Date projectStartDate;
	public String projectTitle;
	public Long riskIndex;
	public long securityMask = 0;

	/** The minimum security level for the project risks **/
	public int minimumSecurityLevel = 0;

	/** The matrix. */
	@Transient
	public ModelToleranceMatrix matrix;

	/** The controls eff. */
	@Transient
	protected ArrayList<ModelControlEffectiveness> controlsEff;

	/** The impact types. */
	@Transient
	protected ArrayList<ModelQuantImpactType> impactTypes;

	/** The objectives. */
	@Transient
	protected ArrayList<ModelObjective> objectives;

	/** The reviews. */
	@Transient
	protected ArrayList<ModelReview> reviews;

	/** The risk categorys. */
	@Transient
	protected ArrayList<ModelMultiLevel> riskCategorys;

	/** The project risk manager. */
	@Transient
	protected ModelPerson projectRiskManager;

	/** The risk manager. */
	@Transient
	protected String riskManager;

	/** The riskmanagers. */
	@Transient
	public List<ModelPerson> riskmanagers;

	/** The riskowners. */
	@Transient
	public List<ModelPerson> riskowners;

	/** The riskmanager i ds. */
	@Transient
	public ArrayList<Long> riskmanagerIDs = new ArrayList<Long>();

	/** The riskowners i ds. */
	@Transient
	public ArrayList<Long> riskownersIDs = new ArrayList<Long>();

	/** The riskusers i ds. */
	@Transient
	public ArrayList<Long> riskusersIDs = new ArrayList<Long>();

	/** The project controllers. */
	@Transient
	protected ArrayList<Long> projectControllers;

	/** The project designates. */
	@Transient
	protected ArrayList<Long> projectDesignates;

	/** The risks. */
	@Transient
	protected ArrayList<Long> risks;

	/** The riskusers. */
	@Transient
	protected ArrayList<ModelPerson> riskusers;

	/** The tol actions. */
	@Transient
	protected ArrayList<ModelToleranceDescriptors> tolActions;

	/** The tolernacematrixs. */
	@Transient
	public ArrayList<ModelToleranceMatrix> tolernacematrixs;

	/** The matrix i ds. */
	@Transient
	public ArrayList<Long> matrixIDs = new ArrayList<Long>();
	
	@Transient
	public String extraStuff;

	/**
	 * Instantiates a new model risk project.
	 */
	public ModelRiskProject() {
	}

	/**
	 * Gets the controls eff.
	 * 
	 * @return the controlsEff
	 */
	public final ArrayList<ModelControlEffectiveness> getControlsEff() {
		if (controlsEff == null) {
			controlsEff = new ArrayList<ModelControlEffectiveness>();
		}
		return controlsEff;
	}

	/**
	 * Sets the controls eff.
	 * 
	 * @param controlsEff
	 *            the controlsEff to set
	 */
	public final void setControlsEff(
			final ArrayList<ModelControlEffectiveness> controlsEff) {
		this.controlsEff = controlsEff;
	}

	/**
	 * Gets the impact types.
	 * 
	 * @return the dateEntered
	 */

	/**
	 * @return the impactTypes
	 */
	public final ArrayList<ModelQuantImpactType> getImpactTypes() {
		if (impactTypes == null) {
			impactTypes = new ArrayList<ModelQuantImpactType>();
		}
		return impactTypes;
	}

	/**
	 * Sets the impact types.
	 * 
	 * @param impactTypes
	 *            the impactTypes to set
	 */
	public final void setImpactTypes(
			final ArrayList<ModelQuantImpactType> impactTypes) {
		this.impactTypes = (impactTypes);
	}

	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final Long getInternalID() {
		return projectID;
	}

	/**
	 * Sets the internal id.
	 * 
	 * @param internalID
	 *            the internalID to set
	 */

	public final void setInternalID(final Long internalID) {
		this.projectID = internalID;
	}

	/**
	 * Gets the manager internal id.
	 * 
	 * @return the managerInternalID
	 */
	public final Long getManagerInternalID() {
		return projectRiskManagerID;
	}

	/**
	 * Sets the manager internal id.
	 * 
	 * @param managerInternalID
	 *            the managerInternalID to set
	 */
	public final void setManagerInternalID(final Long managerInternalID) {
		this.projectRiskManagerID = managerInternalID;
	}

	/**
	 * Gets the objectives.
	 * 
	 * @return the objectives
	 */
	public final ArrayList<ModelObjective> getObjectives() {
		if (objectives == null) {
			objectives = new ArrayList<ModelObjective>();
		}
		return objectives;
	}

	/**
	 * Sets the objectives.
	 * 
	 * @param objectives
	 *            the objectives to set
	 */
	public final void setObjectives(final ArrayList<ModelObjective> objectives) {
		this.objectives = (objectives);
	}

	/**
	 * Gets the parent id.
	 * 
	 * @return the parentID
	 */
	public final Long getParentID() {
		return parentID;
	}

	/**
	 * Sets the parent id.
	 * 
	 * @param parentID
	 *            the parentID to set
	 */
	public final void setParentID(final Long parentID) {
		this.parentID = parentID;
	}

	/**
	 * Gets the project code.
	 * 
	 * @return the projectCode
	 */
	public final String getProjectCode() {
		return projectCode;
	}

	/**
	 * Sets the project code.
	 * 
	 * @param projectCode
	 *            the projectCode to set
	 */
	public final void setProjectCode(final String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * Gets the project description.
	 * 
	 * @return the projectDescription
	 */
	public final String getProjectDescription() {
		return projectDescription;
	}

	/**
	 * Sets the project description.
	 * 
	 * @param projectDescription
	 *            the projectDescription to set
	 */
	public final void setProjectDescription(final String projectDescription) {
		this.projectDescription = projectDescription;
	}

	/**
	 * Gets the project risk manager.
	 * 
	 * @return the projectRiskManager
	 */
	public final ModelPerson getProjectRiskManager() {
		return projectRiskManager;
	}

	/**
	 * Sets the project risk manager.
	 * 
	 * @param projectRiskManager
	 *            the projectRiskManager to set
	 */
	public final void setProjectRiskManager(final ModelPerson projectRiskManager) {
		this.projectRiskManager = projectRiskManager;
	}

	/**
	 * Gets the date entered.
	 * 
	 * @return the date entered
	 */
	public final Date getDateEntered() {
		return dateEntered;
	}

	/**
	 * Sets the date entered.
	 * 
	 * @param dateEntered
	 *            the new date entered
	 */
	public final void setDateEntered(final Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	/**
	 * Gets the date updated.
	 * 
	 * @return the date updated
	 */
	public final Date getDateUpdated() {
		return dateUpdated;
	}

	/**
	 * Sets the date updated.
	 * 
	 * @param dateUpdated
	 *            the new date updated
	 */
	public final void setDateUpdated(final Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	/**
	 * Gets the project end date.
	 * 
	 * @return the project end date
	 */
	public final Date getProjectEndDate() {
		return projectEndDate;
	}

	/**
	 * Sets the project end date.
	 * 
	 * @param projectEndDate
	 *            the new project end date
	 */
	public final void setProjectEndDate(final Date projectEndDate) {
		this.projectEndDate = projectEndDate;
	}

	/**
	 * Gets the project start date.
	 * 
	 * @return the project start date
	 */
	public final Date getProjectStartDate() {
		return projectStartDate;
	}

	/**
	 * Sets the project start date.
	 * 
	 * @param projectStartDate
	 *            the new project start date
	 */
	public final void setProjectStartDate(final Date projectStartDate) {
		this.projectStartDate = projectStartDate;
	}

	/**
	 * Gets the project title.
	 * 
	 * @return the projectTitle
	 */
	public final String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Sets the project title.
	 * 
	 * @param projectTitle
	 *            the projectTitle to set
	 */
	public final void setProjectTitle(final String projectTitle) {
		this.projectTitle = projectTitle;
	}

	/**
	 * Gets the reviews.
	 * 
	 * @return the reviews
	 */
	public final ArrayList<ModelReview> getReviews() {
		if (reviews == null) {
			reviews = new ArrayList<ModelReview>();
		}
		return reviews;
	}

	/**
	 * Sets the reviews.
	 * 
	 * @param reviews
	 *            the reviews to set
	 */
	public final void setReviews(final ArrayList<ModelReview> reviews) {
		this.reviews = (reviews);
	}

	/**
	 * Gets the risk categorys.
	 * 
	 * @return the riskCategorys
	 */
	public final ArrayList<ModelMultiLevel> getRiskCategorys() {
		if (riskCategorys == null) {
			riskCategorys = new ArrayList<ModelMultiLevel>();
		}
		return riskCategorys;
	}

	/**
	 * Sets the risk categorys.
	 * 
	 * @param riskCategorys
	 *            the riskCategorys to set
	 */
	public final void setRiskCategorys(
			final ArrayList<ModelMultiLevel> riskCategorys) {
		this.riskCategorys = (riskCategorys);
	}

	/**
	 * Gets the risk index.
	 * 
	 * @return the riskIndex
	 */
	public final Long getRiskIndex() {
		return riskIndex;
	}

	/**
	 * Sets the risk index.
	 * 
	 * @param riskIndex
	 *            the riskIndex to set
	 */
	public final void setRiskIndex(final Long riskIndex) {
		this.riskIndex = riskIndex;
	}

	/**
	 * Gets the risk manager.
	 * 
	 * @return the riskManager
	 */
	public final String getRiskManager() {
		return riskManager;
	}

	/**
	 * Sets the risk manager.
	 * 
	 * @param riskManager
	 *            the riskManager to set
	 */
	public final void setRiskManager(final String riskManager) {
		this.riskManager = riskManager;
	}

	/**
	 * Gets the riskmanagers.
	 * 
	 * @return the riskmanagers
	 */
	public final ArrayList<ModelPerson> getRiskmanagers() {
		if (riskmanagers == null) {
			riskmanagers = new ArrayList<ModelPerson>();
		}
		return (ArrayList<ModelPerson>) (riskmanagers);
	}

	/**
	 * Sets the riskmanagers.
	 * 
	 * @param riskmanagers
	 *            the riskmanagers to set
	 */
	public final void setRiskmanagers(final ArrayList<ModelPerson> riskmanagers) {
		this.riskmanagers = (riskmanagers);
	}

	/**
	 * Gets the riskowners.
	 * 
	 * @return the riskowners
	 */
	public final ArrayList<ModelPerson> getRiskowners() {
		if (riskowners == null) {
			riskowners = new ArrayList<ModelPerson>();
		}
		return (ArrayList<ModelPerson>) riskowners;
	}

	/**
	 * Sets the riskowners.
	 * 
	 * @param riskowners
	 *            the riskowners to set
	 */
	public final void setRiskowners(final ArrayList<ModelPerson> riskowners) {
		this.riskowners = (riskowners);
	}

	/**
	 * Gets the risks.
	 * 
	 * @return the risks
	 */
	public final ArrayList<Long> getRisks() {
		if (risks == null) {
			risks = new ArrayList<Long>();
		}
		return risks;
	}

	/**
	 * Sets the risks.
	 * 
	 * @param risks
	 *            the risks to set
	 */
	public final void setRisks(final ArrayList<Long> risks) {
		this.risks = (risks);
	}

	/**
	 * Gets the riskusers.
	 * 
	 * @return the riskusers
	 */
	public final ArrayList<ModelPerson> getRiskusers() {
		if (riskusers == null) {
			riskusers = new ArrayList<ModelPerson>();
		}
		return riskusers;
	}

	/**
	 * Sets the riskusers.
	 * 
	 * @param riskusers
	 *            the riskusers to set
	 */
	public final void setRiskusers(final ArrayList<ModelPerson> riskusers) {
		this.riskusers = (riskusers);
	}

	/**
	 * Gets the tabs.
	 * 
	 * @return the secPRMExportOnly
	 */
	/**
	 * @return the tabs
	 */
	public final long getTabs() {
		return tabsToUse;
	}

	/**
	 * Sets the tabs.
	 * 
	 * @param tabs
	 *            the tabs to set
	 */
	public final void setTabs(final long tabs) {
		this.tabsToUse = tabs;
	}

	/**
	 * Gets the tol actions.
	 * 
	 * @return the tolActions
	 */
	public final ArrayList<ModelToleranceDescriptors> getTolActions() {
		if (tolActions == null) {
			tolActions = new ArrayList<ModelToleranceDescriptors>();
		}
		return tolActions;
	}

	/**
	 * Sets the tol actions.
	 * 
	 * @param tolActions
	 *            the tolActions to set
	 */
	public final void setTolActions(
			final ArrayList<ModelToleranceDescriptors> tolActions) {
		this.tolActions = (tolActions);
	}

	/**
	 * Gets the tolernacematrixs.
	 * 
	 * @return the tolernacematrixs
	 */
	public final ArrayList<ModelToleranceMatrix> getTolernacematrixs() {
		if (tolernacematrixs == null) {
			tolernacematrixs = new ArrayList<ModelToleranceMatrix>();
		}
		return tolernacematrixs;
	}

	/**
	 * Sets the tolernacematrixs.
	 * 
	 * @param tolernacematrixs
	 *            the tolernacematrixs to set
	 */
	public final void setTolernacematrixs(
			final ArrayList<ModelToleranceMatrix> tolernacematrixs) {
		this.tolernacematrixs = (tolernacematrixs);
	}

	/**
	 * Gets the project controllers.
	 * 
	 * @return the project controllers
	 */
	public final ArrayList<Long> getProjectControllers() {
		return projectControllers;
	}

	/**
	 * Sets the project controllers.
	 * 
	 * @param projectManagers
	 *            the new project controllers
	 */
	public final void setProjectControllers(
			final ArrayList<Long> projectManagers) {
		this.projectControllers = projectManagers;
	}

	/**
	 * Gets the project designates.
	 * 
	 * @return the project designates
	 */
	public final ArrayList<Long> getProjectDesignates() {
		return projectDesignates;
	}

	/**
	 * Sets the project designates.
	 * 
	 * @param projectDesignates
	 *            the new project designates
	 */
	public final void setProjectDesignates(
			final ArrayList<Long> projectDesignates) {
		this.projectDesignates = projectDesignates;
	}

	/**
	 * Sets the security.
	 * 
	 * @param visitor
	 *            the new security
	 */
	public final void setSecurity(final long mask) {
		securityMask |= mask;
	}

	/**
	 * Unset security.
	 * 
	 * @param visitor
	 *            the visitor
	 */
	public final void unsetSecurity(final long mask) {
		securityMask &= ~mask;
	}

	/**
	 * Check security.
	 * 
	 * @param visitor
	 *            the visitor
	 * 
	 * @return true, if successful
	 */
	public final boolean checkSecurity(final long mask) {
		return ((securityMask & mask) > 0);
	}

	/**
	 * Gets the security visitor.
	 * 
	 * @return the security visitor
	 */
	public final long getSecurityMask() {
		return securityMask;
	}

	/**
	 * Sets the security visitor.
	 * 
	 * @param securityMask
	 *            the new security visitor
	 */
	public final void setSecurityMask(final long securityMask) {
		this.securityMask = securityMask;
	}

	/**
	 * Gets the projectID.
	 * 
	 * @return the projectID
	 */
	public final Long getProjectID() {
		return projectID;
	}

	/**
	 * Sets the projectID.
	 * 
	 * @param projectId
	 *            the new projectID
	 */
	public final void setProjectID(final Long projectId) {
		projectID = projectId;
	}


	public final Long getProjectRiskManagerID() {
		return projectRiskManagerID;
	}


	public final void setProjectRiskManagerID(final Long projectRiskManagerId) {
		projectRiskManagerID = projectRiskManagerId;
	}


	public final long getTabsToUse() {
		return tabsToUse;
	}

	public final void setTabsToUse(final long tabsToUse) {
		this.tabsToUse = tabsToUse;
	}

	/**
	 * Gets the matrix.
	 * 
	 * @return the matrix
	 */
	public final ModelToleranceMatrix getMatrix() {
		return matrix;
	}

	/**
	 * Sets the matrix.
	 * 
	 * @param matrix
	 *            the new matrix
	 */
	public final void setMatrix(final ModelToleranceMatrix matrix) {
		this.matrix = matrix;
	}

	/**
	 * Gets the riskmanager i ds.
	 * 
	 * @return the riskmanager i ds
	 */
	public final ArrayList<Long> getRiskmanagerIDs() {
		return riskmanagerIDs;
	}

	/**
	 * Sets the riskmanager i ds.
	 * 
	 * @param riskmanagerIDs
	 *            the new riskmanager i ds
	 */
	public final void setRiskmanagerIDs(final ArrayList<Long> riskmanagerIDs) {
		this.riskmanagerIDs = riskmanagerIDs;
	}

	/**
	 * Gets the riskowners i ds.
	 * 
	 * @return the riskowners i ds
	 */
	public final ArrayList<Long> getRiskownersIDs() {
		return riskownersIDs;
	}

	/**
	 * Sets the riskowners i ds.
	 * 
	 * @param riskownersIDs
	 *            the new riskowners i ds
	 */
	public final void setRiskownersIDs(final ArrayList<Long> riskownersIDs) {
		this.riskownersIDs = riskownersIDs;
	}

	/**
	 * Gets the riskusers i ds.
	 * 
	 * @return the riskusers i ds
	 */
	public final ArrayList<Long> getRiskusersIDs() {
		return riskusersIDs;
	}

	/**
	 * Sets the riskusers i ds.
	 * 
	 * @param riskusersIDs
	 *            the new riskusers i ds
	 */
	public final void setRiskusersIDs(final ArrayList<Long> riskusersIDs) {
		this.riskusersIDs = riskusersIDs;
	}

	/**
	 * Gets the matrix i ds.
	 * 
	 * @return the matrix i ds
	 */
	public final ArrayList<Long> getMatrixIDs() {
		return matrixIDs;
	}

	/**
	 * Sets the matrix i ds.
	 * 
	 * @param matrixIDs
	 *            the new matrix i ds
	 */
	public final void setMatrixIDs(final ArrayList<Long> matrixIDs) {
		this.matrixIDs = matrixIDs;
	}

	/**
	 * Sets the riskmanagers.
	 * 
	 * @param riskmanagers
	 *            the new riskmanagers
	 */
	public final void setRiskmanagers(final List<ModelPerson> riskmanagers) {
		this.riskmanagers = riskmanagers;
	}

	/**
	 * Sets the riskowners.
	 * 
	 * @param riskowners
	 *            the new riskowners
	 */
	public final void setRiskowners(final List<ModelPerson> riskowners) {
		this.riskowners = riskowners;
	}

	public int getMinimumSecurityLevel() {
		return minimumSecurityLevel;
	}

	public void setMinimumSecurityLevel(final int minimumSecurityLevel) {
		this.minimumSecurityLevel = minimumSecurityLevel;
	}

	public Boolean getSinglePhase() {
		return singlePhase;
	}

	public void setSinglePhase(Boolean singlePhase) {
		this.singlePhase = singlePhase;
	}

	public Boolean getUseAdvancedConsequences() {
		return useAdvancedConsequences;
	}

	public void setUseAdvancedConsequences(Boolean useAdvancedConsequences) {
		this.useAdvancedConsequences = useAdvancedConsequences;
	}

	public Boolean getUseAdvancedLiklihood() {
		return useAdvancedLiklihood;
	}

	public void setUseAdvancedLiklihood(Boolean useAdvancedLiklihood) {
		this.useAdvancedLiklihood = useAdvancedLiklihood;
	}
}
