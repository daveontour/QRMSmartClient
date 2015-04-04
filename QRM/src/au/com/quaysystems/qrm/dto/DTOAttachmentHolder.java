/*
 * 
 */
package au.com.quaysystems.qrm.dto;

// TODO: Auto-generated Javadoc

/**
 * The Class DTOAttachmentHolder.
 */
public class DTOAttachmentHolder {

	public String description;
	public String url;
	public String attachmentURL;
	public Long internalID;

	public DTOAttachmentHolder(final String d, final String u, final String a, Long internalID) {
		description = d;
		url = u;
		attachmentURL = a;
		this.internalID = internalID;
	}
}