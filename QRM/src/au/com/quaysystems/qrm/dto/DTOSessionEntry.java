package au.com.quaysystems.qrm.dto;

import java.util.Date;

import au.com.quaysystems.qrm.server.servlet.SessionEntry;

public class DTOSessionEntry {
	
	public String name;
	public String email;
	public Long userID;
	public Date lastAccess;
	public String orgName;
	public long numTransactions = 0;
	public String sessionID;
	public boolean sessionEnabled = true;
	public String dbUser;
	public String remoteHost;
	public String remoteAddr;

	
	public DTOSessionEntry(SessionEntry session) {
		this.name =session.person.name;
		this.email = session.person.email;
		this.userID = session.person.stakeholderID;
		this.lastAccess = session.lastAccess;
		this.orgName = session.orgName;
		this.numTransactions = session.numTransactions;
		this.sessionID = session.sessionID;
		this.sessionEnabled = session.sessionEnabled;
		this.dbUser = session.dbUser;
		this.remoteAddr = session.remoteAddr;
		this.remoteHost = session.remoteHost;

	}

}
