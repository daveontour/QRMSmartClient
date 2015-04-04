/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getPerson", resultClass = ModelPerson.class, query = "SELECT STAKEHOLDERS.* FROM stakeholders WHERE stakeholderID = :pid AND active > 0"),
	@NamedNativeQuery(name = "getPersonByName", resultClass = ModelPerson.class, query = "SELECT STAKEHOLDERS.* FROM stakeholders WHERE name = :name AND active > 0"),
	@NamedNativeQuery(name = "getProjectControllers", query = "SELECT DISTINCT stakeholders.* FROM stakeholders INNER JOIN stakholder_rights ON stakholder_rights.stakeholderID = stakeholders.stakeholderID WHERE stakholder_rights.projectID = :pid AND stakholder_rights.create_projects > 0 AND stakeholders.stakeholderID > 0", resultClass = ModelPerson.class),
	@NamedNativeQuery(name = "getProjectRiskManagers",resultClass = ModelPerson.class, query = "SELECT  DISTINCT stakeholders.* FROM stakeholders INNER JOIN projectriskmanagers ON projectriskmanagers.stakeholderID = stakeholders.stakeholderID WHERE projectriskmanagers.projectID = :projectID AND stakeholders.stakeholderID > 0"),
	@NamedNativeQuery(name = "getProjectRiskOwners",resultClass = ModelPerson.class, query = "SELECT DISTINCT stakeholders.* FROM stakeholders INNER JOIN projectowners ON projectowners.stakeholderID = stakeholders.stakeholderID WHERE projectowners.projectID = :projectID AND stakeholders.stakeholderID > 0"),
	@NamedNativeQuery(name = "getProjectRiskUsers",resultClass = ModelPerson.class, query = "SELECT DISTINCT stakeholders.* FROM stakeholders INNER JOIN projectusers ON projectusers.stakeholderID = stakeholders.stakeholderID WHERE projectusers.projectID = :projectID AND stakeholders.stakeholderID > 0"),
	@NamedNativeQuery(name = "getProjectDesignates", query = "SELECT DISTINCT stakeholders.stakeholderID FROM stakeholders INNER JOIN stakholder_rights ON stakholder_rights.stakeholderID = stakeholders.stakeholderID WHERE stakholder_rights.projectID = :pid AND stakholder_rights.project_designate > 0 AND stakeholders.stakeholderID > 0", resultClass = ModelPerson.class),
	@NamedNativeQuery(name = "getAllRepPersonLite",resultClass = ModelPerson.class, query = "SELECT DISTINCT stakeholders.* FROM stakeholders JOIN userrepository ON stakeholders.stakeholderID = userrepository.stakeholderID")
})
@Entity
@Table(name = "stakeholders")
public class ModelPerson implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3387546669084210798L;

	public boolean active;
	public boolean allowUserMgmt;
	public boolean allowlogon;
	public String email;
	/** The stakeholderID. */
	@Id
	public long stakeholderID;

	@Column(name = "lastlogon")
	public Date lastLogon;
	public String name;
	/** The password. */
	@Transient
	public String password;
	public Long emailmsgtypes;

	/** The riskmanager. */
	@Transient
	protected boolean riskmanager;

	/** The riskowner. */
	@Transient
	protected boolean riskowner;

	/**
	 * Instantiates a new model person.
	 */
	public ModelPerson() {
	}

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 * 
	 * @param value
	 *            the new active
	 */
	public final void setActive(final boolean value) {
		this.active = value;
	}

	/**
	 * Checks if is allow user management.
	 * 
	 * @return true, if is allow user management
	 */
	public final boolean isAllowUserManagement() {
		return allowUserMgmt;
	}

	/**
	 * Sets the allow user management.
	 * 
	 * @param value
	 *            the new allow user management
	 */
	public final void setAllowUserManagement(final boolean value) {
		this.allowUserMgmt = value;
	}

	/**
	 * Checks if is allowlogon.
	 * 
	 * @return true, if is allowlogon
	 */
	public final boolean isAllowlogon() {
		return allowlogon;
	}

	/**
	 * Sets the allowlogon.
	 * 
	 * @param value
	 *            the new allowlogon
	 */
	public final void setAllowlogon(final boolean value) {
		this.allowlogon = value;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public final String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param value
	 *            the new email
	 */
	public final void setEmail(final String value) {
		this.email = value;
	}

	/**
	 * Gets the stakeholderID.
	 * 
	 * @return the stakeholderID
	 */
	public final Long getStakeholderID() {
		return stakeholderID;
	}

	/**
	 * Sets the internal id.
	 * 
	 * @param value
	 *            the new internal id
	 */
	public final void setInternalID(final Long value) {
		this.stakeholderID = value;
	}


	public final Date getLastLogon() {
		return lastLogon;
	}


	public final void setLastLogon(final Date value) {
		this.lastLogon = value;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param value
	 *            the new name
	 */
	public final void setName(final String value) {
		this.name = value;
	}


	/**
	 * Sets the password.
	 * 
	 * @param value
	 *            the new password
	 */
	public final void setPassword(final String value) {
		this.password = value;
	}

	/**
	 * Checks if is riskmanager.
	 * 
	 * @return true, if is riskmanager
	 */
	public final boolean isRiskmanager() {
		return riskmanager;
	}

	/**
	 * Sets the riskmanager.
	 * 
	 * @param value
	 *            the new riskmanager
	 */
	public final void setRiskmanager(final boolean value) {
		this.riskmanager = value;
	}

	/**
	 * Checks if is riskowner.
	 * 
	 * @return true, if is riskowner
	 */
	public final boolean isRiskowner() {
		return riskowner;
	}

	/**
	 * Sets the riskowner.
	 * 
	 * @param value
	 *            the new riskowner
	 */
	public final void setRiskowner(final boolean value) {
		this.riskowner = value;
	}
	/**
	 * Gets the emailmsgtypes.
	 * 
	 * @return the emailMessageTypes
	 */
	public final long getEmailmsgtypes() {
		return emailmsgtypes;
	}

	/**
	 * Sets the email message types.
	 * 
	 * @param emailMessageTypes
	 *            the emailMessageTypes to set
	 */

	public final void setEmailMessageTypes(final long emailMessageTypes) {
		this.emailmsgtypes = emailMessageTypes;
	}
}
