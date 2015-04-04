package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Id;

public class ModelPersonLite implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3387546669084210798L;

	/** The email. */
	public String email;

	/** The stakeholderID. */
	@Id
	public long stakeholderID;

	/** The name. */
	public String name;
	
	public String compoundName;
	
	public boolean allowLogon;

	public ModelPersonLite() {
	}

}
