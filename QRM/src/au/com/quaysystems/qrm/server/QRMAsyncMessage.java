package au.com.quaysystems.qrm.server;

import java.util.HashMap;
import java.util.Map;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

import com.thoughtworks.xstream.XStream;

public class QRMAsyncMessage implements Runnable{

	private String CHANNEL;

	private HashMap<String, Object> dataMap;

	public QRMAsyncMessage(ReportProcessorData job){
		dataMap = new HashMap<String, Object>();
		dataMap.put("job", job);
		CHANNEL = QRMConstants.REPORT_MSG;		
	}

	public QRMAsyncMessage(String channel,  Object obj, boolean bXs){
		if(bXs){
			obj = new XStream().toXML(obj);
		} 
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", obj);
		init(channel,data);
	}

	public QRMAsyncMessage(String channel,  Object obj){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", obj);
		init(channel,data);
	}

	public QRMAsyncMessage(String channel,  Map<String, Object> data ){
		init(channel,data);
	}

	private void init(String channel,  Map<String, Object> data ){
		this.CHANNEL = channel;
		if (data != null){
			this.dataMap = new HashMap<String, Object>(data);
		}
	}

	/*
	 * Assign a thread of execution to execute this task so that the request is executed
	 * asynchronously to the UI
	 */
	public void send(){

		// Add the message (this object) to the message queue for execution
		// The execute method of each of the queues places this runnable on the queue. 
		// The queue assigns a thread of execution as soon as one is available

		if (CHANNEL.endsWith(QRMConstants.REPORT_MSG)){
			ConcurrentManager.reportQueue.execute(this);
		}
		if (CHANNEL.endsWith(QRMConstants.EMAIL_MSG)){
			ConcurrentManager.emailQueue.execute(this);
		}
		if (CHANNEL.endsWith(QRMConstants.ADMINEMAIL_MSG)){
			ConcurrentManager.emailQueue.execute(this);
		}
		if (CHANNEL.endsWith(QRMConstants.MONTE_MSG)){
			ConcurrentManager.monteQueue.execute(this);
		}
	}

	@Override
	public void run() {

		// The run() method is called by the PoolThread which is assigned to execute this runnable

		if (CHANNEL.endsWith(QRMConstants.REPORT_MSG)){
			ConcurrentManager.repProcessor.deliver(dataMap);
		}
		if (CHANNEL.endsWith(QRMConstants.EMAIL_MSG)){
			ConcurrentManager.mailProcessor.deliver(dataMap);
		}
		if (CHANNEL.endsWith(QRMConstants.ADMINEMAIL_MSG)){
			ConcurrentManager.mailProcessor.deliverAdminEmail(dataMap);
		}
		if (CHANNEL.endsWith(QRMConstants.MONTE_MSG)){
			ConcurrentManager.monteProcessor.deliver(dataMap);
		}
	}
}
