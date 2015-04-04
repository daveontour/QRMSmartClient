package au.com.quaysystems.qrm.server.servlet;

import java.io.Serializable;
import java.util.Date;

import au.com.quaysystems.qrm.dto.ModelPerson;

@SuppressWarnings("serial")
public class SessionEntry implements Serializable {

	public ModelPerson person;
	public Date lastAccess;
	public String orgName;
	public String dbURL;
	public long numTransactions = 0;
	public String sessionID;
	public boolean sessionEnabled = true;
	public String dbUser;
	public String remoteHost;
	public String remoteAddr;
	public static int SESSIONTIMEOUT = 20;


	public final void disableSession() {
		sessionEnabled = false;
	}
	public final void enableSession() {
		sessionEnabled = true;
	}
	public final void setLastAccess(final Date date) {
		lastAccess = date;
	}
	public final Date getLastAccess() {
		return lastAccess;
	}
	public final long getNumTransactions() {
		return numTransactions;
	}
	public final void addTransactions() {
		numTransactions++;
	}
	public final void setUser(final ModelPerson p) {
		person = p;
	}
	public final ModelPerson getUser() {
		return person;
	}
	public final void setOrganisation(final String org) {
		orgName = org;
	}
	public final String getOrganisation() {
		return orgName;
	}
	public final String getDbURL() {
		return dbURL;
	}
	public final void setDbURL(final String dbURL) {
		this.dbURL = dbURL;
	}
	public final String getSessionID() {
		return sessionID;
	}
	public final void setSessionID(final String sessionID) {
		this.sessionID = sessionID;
	}

	public ModelPerson getPerson() {
		return person;
	}
	public void setPerson(ModelPerson person) {
		this.person = person;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public boolean isSessionEnabled() {
		return sessionEnabled;
	}
	public void setSessionEnabled(boolean sessionEnabled) {
		this.sessionEnabled = sessionEnabled;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public void setNumTransactions(long numTransactions) {
		this.numTransactions = numTransactions;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
}