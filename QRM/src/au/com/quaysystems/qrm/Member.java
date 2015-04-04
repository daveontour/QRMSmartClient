package au.com.quaysystems.qrm;

import java.util.LinkedList;
import java.util.Queue;

public class Member {
	public boolean expired = false;
	public String sessionID;
	public Long riskID;
	public Long userID;
	public Long lastRequest;
	public Boolean browserSession = true;
	public Queue<String> queue = new LinkedList<String>();
	
}