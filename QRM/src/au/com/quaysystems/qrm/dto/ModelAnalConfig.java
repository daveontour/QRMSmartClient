package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "analysistools")
public class ModelAnalConfig {

	@Id
	@GeneratedValue
	public long id;
	public boolean bReverse;
	public String title;
	public String clazz;
	public String param1;
	public boolean b3D;
	public boolean bTol;
	public boolean bMatrix;
	public boolean bContext;
	public boolean bDescend;
	public boolean bNumElem;

	public ModelAnalConfig() {
	}

}