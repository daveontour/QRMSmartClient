/*
 * 
 */
package au.com.quaysystems.qrm.server;

import java.util.ArrayList;
import java.util.List;

import au.com.quaysystems.qrm.dto.ModelQRMReport;

public class RESTTransportContainer {

	public int status;
	public int totalRows;
	public int startRow;
	public int endRow;
	public ArrayList<String> errors;
	@SuppressWarnings("rawtypes")
	public List data;

	@SuppressWarnings("rawtypes")
	public RESTTransportContainer(final int totalRows, final int startRow,
			final int endRow, final int status, final ArrayList data) {
		this.status = status;
		this.data = data;
		this.totalRows = totalRows;
		this.startRow = startRow;
		this.endRow = endRow;

		if (totalRows < endRow) {
			this.endRow = totalRows;
		}
	}

	@SuppressWarnings("rawtypes")
	public RESTTransportContainer(final List list) {
		this.status = 0;
		this.data = list;
		this.totalRows = list.size();
		this.startRow = 0;
		this.endRow = list.size() - 1;
	}

	public RESTTransportContainer(final ArrayList<ModelQRMReport> data) {
		this.status = 0;
		this.data = data;
		this.totalRows = data.size();
		this.startRow = 0;
		this.endRow = data.size() - 1;
	}

	public RESTTransportContainer(final int status) {
		this.status = status;
		this.data = null;
		this.totalRows = 0;
		this.startRow = 0;
		this.endRow = 0;
	}
}
