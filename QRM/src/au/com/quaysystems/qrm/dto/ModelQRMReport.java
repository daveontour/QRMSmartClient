/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;


@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllReportsContext", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version, reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'PROJECT'"),
	@NamedNativeQuery(name = "getAllReportsRegister", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'REGISTER'"),
	@NamedNativeQuery(name = "getAllReportsRepository", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'REPOSITORY'"),
	@NamedNativeQuery(name = "getAllReportsIncident", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'INCIDENT'"),
	@NamedNativeQuery(name = "getAllReportsReview", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'REVIEW'"),
	@NamedNativeQuery(name = "getAllReportsRisk", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, detailConfigWindow, excelOnlyFormat FROM reports where reporttype = 'RISK'"),
	@NamedNativeQuery(name = "getAllReports", resultClass = ModelQRMReport.class, query = "SELECT null AS bodyText, null AS visitor, coreFile,reportFileName, internalID,reportName,  reportDescription,  id,  template, reporttype, version,  reqTemplateRoot, reporttype, detailConfigWindow, excelOnlyFormat FROM reports")
})


@Entity
@Table(name = "reports")
public class ModelQRMReport implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -445832930501717602L;
	@Id
	public long internalID;
	public String reportName;
	public String reportDescription;
	public String reportFileName;
	public Boolean coreFile;
	public String id;
	public Boolean template;
	public String reporttype;
	public String visitor;
	public String bodyText;
	public String version;
	public String reqTemplateRoot;
	
	public boolean detailConfigWindow = false;
	public boolean excelOnlyFormat = false;

	public final long getInternalID() {
		return internalID;
	}
	public final void setInternalID(final long internalID) {
		this.internalID = internalID;
	}
	public final String getReportName() {
		return reportName;
	}
	public final void setReportName(final String reportName) {
		this.reportName = reportName;
	}
	public final String getReportDescription() {
		return reportDescription;
	}
	public final void setReportDescription(final String reportDescription) {
		this.reportDescription = reportDescription;
	}
	public final String getId() {
		return id;
	}
	public final void setId(final String id) {
		this.id = id;
	}
	public final boolean isTemplate() {
		return template;
	}
	public final void setTemplate(final boolean template) {
		this.template = template;
	}
	public final String getReporttype() {
		return reporttype;
	}
	public final void setReporttype(final String reporttype) {
		this.reporttype = reporttype;
	}
	public final String getVisitor() {
		return visitor;
	}
	public final void setVisitork(final String visitor) {
		this.visitor = visitor;
	}
	public final String getBodyText() {
		return bodyText;
	}
	public final void setBodyText(final String bodyText) {
		this.bodyText = bodyText;
	}
	public final String getVersion() {
		return version;
	}
	public final void setVersion(final String version) {
		this.version = version;
	}
	public final String getReqTemplateRoot() {
		return reqTemplateRoot;
	}
	public final void setReqTemplateRoot(final String reqTemplateRoot) {
		this.reqTemplateRoot = reqTemplateRoot;
	}
	public final String getReportFileName() {
		return reportFileName;
	}
	public final void setReportFileName(final String reportFileName) {
		this.reportFileName = reportFileName;
	}
	public final Boolean getCoreFile() {
		return coreFile;
	}
	public final void setCoreFile(final Boolean coreFile) {
		this.coreFile = coreFile;
	}
	public final Boolean getTemplate() {
		return template;
	}
	public final void setTemplate(final Boolean template) {
		this.template = template;
	}
	public Boolean isDetailConfigWindow() {
		return detailConfigWindow;
	}
	public void setDetailConfigWindow(Boolean detailConfigWindow) {
		this.detailConfigWindow = detailConfigWindow;
	}
	public Boolean isExcelOnlyFormat() {
		return excelOnlyFormat;
	}
	public void setExcelOnlyFormat(Boolean excelOnlyFormat) {
		this.excelOnlyFormat = excelOnlyFormat;
	}
}
