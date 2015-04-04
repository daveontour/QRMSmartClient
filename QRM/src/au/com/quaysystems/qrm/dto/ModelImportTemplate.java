package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getUserTemplates", query = "SELECT * FROM importtemplate WHERE userID = :var_userid", resultClass = ModelImportTemplate.class)
})
@Entity
@Table(name = "importtemplate")
public class ModelImportTemplate implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6026125603486048504L;


	@Id
	@GeneratedValue
	public long templateID;
	public long	userID;
	public String templateName;
	public String template;


	public ModelImportTemplate() {
	}


	public long getTemplateID() {
		return templateID;
	}


	public void setTemplateID(long templateID) {
		this.templateID = templateID;
	}


	public long getUserID() {
		return userID;
	}


	public void setUserID(long userID) {
		this.userID = userID;
	}


	public String getTemplateName() {
		return templateName;
	}


	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}


	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
