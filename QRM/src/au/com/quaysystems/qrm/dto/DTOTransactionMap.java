package au.com.quaysystems.qrm.dto;

public class DTOTransactionMap {
	public String method;
	public Long count;
	
	public DTOTransactionMap(String method, Long count) {
		super();
		this.method = method;
		this.count = count;
	}
}
