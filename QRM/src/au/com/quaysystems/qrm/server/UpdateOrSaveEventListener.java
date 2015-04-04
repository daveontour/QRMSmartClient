package au.com.quaysystems.qrm.server;



import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * This class defines all the special processing that has to be done on different classes 
 * when they have going to be saved
 */
@SuppressWarnings("serial") 
class UpdateOrSaveEventListener extends	DefaultSaveOrUpdateEventListener {

	public void onSaveOrUpdate(SaveOrUpdateEvent event) {

		if (event.getObject() instanceof ReportProcessorData){
			ReportProcessorData data = (ReportProcessorData)event.getObject();
			try {
				XStream xs = new XStream();
				data.jobStr = xs.toXML(data.job);
				data.taskParamMapStr = xs.toXML(data.taskParamMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (event.getObject() instanceof ModelScheduledJob){
			ModelScheduledJob data = (ModelScheduledJob)event.getObject();
			try {
				XStream xs = new XStream();
				data.taskParamMapStr = xs.toXML(data.taskParamMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		super.onSaveOrUpdate(event);
	}
}