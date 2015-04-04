/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * The Class DTOMetricElement.
 */
@Entity
public class DTOMetricElement {

	/** The picture. */
	@Id
	public String picture;

	/** The metric id. */
	public String metricID;

	/**
	 * Instantiates a new dTO metric rowElement.
	 * 
	 * @param metricID
	 *            the metric id
	 * @param userID
	 *            the user id
	 * @param projectID
	 *            the project id
	 * @param desc
	 *            the desc
	 */
	
	public DTOMetricElement(){}
	public DTOMetricElement(final Long metricID, final Long userID,
			final Long projectID, final boolean desc) {
		picture = "?metricID=" + metricID + "&userID=" + userID + "&projectID="
				+ projectID + "&desc=" + desc + "&nocache="
				+ new Date().getTime();
		this.metricID = "QRMMetricID" + metricID;
	}
}