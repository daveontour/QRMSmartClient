package au.com.quaysystems.qrm.server;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class QRMHibernateIntegrator implements Integrator {

	@Override
	public void disintegrate(SessionFactoryImplementor arg0,
			SessionFactoryServiceRegistry arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void integrate(Configuration cfg, SessionFactoryImplementor sf,
			SessionFactoryServiceRegistry sfr) {
		
		final EventListenerRegistry eventListenerRegistry = sfr.getService( EventListenerRegistry.class );

		eventListenerRegistry.appendListeners(EventType.POST_LOAD, new PostLoadEventListener());
		eventListenerRegistry.appendListeners(EventType.SAVE_UPDATE, new UpdateOrSaveEventListener());


	}

	@Override
	public void integrate(MetadataImplementor arg0,
			SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
		// TODO Auto-generated method stub

	}

}
