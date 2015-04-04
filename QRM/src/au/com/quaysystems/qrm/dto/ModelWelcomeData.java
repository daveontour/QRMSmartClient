package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getWelcomeData", callable = true, query = "call getWelcomeData(:projectID )", resultClass = ModelWelcomeData.class)
})
@Entity
public class ModelWelcomeData  implements Serializable {

	private static final long serialVersionUID = -4444030131075899963L;

	@Id
	@GeneratedValue
	public Long id;
	public String addInfo;
	public Long internalID;
	public Date date;
	public String element;
	public String name;
	@Transient
	public String data;
	@Transient
	public String value;
	public ModelWelcomeData(){}
}
