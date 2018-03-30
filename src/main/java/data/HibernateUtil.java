package data;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static final Logger LOG = Logger.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory;

	//Use hibernate.cfg.xml configuration to create sessionFactory
	public static SessionFactory getSessionFactory() {
		if (null != sessionFactory) {
			return sessionFactory;
		}

		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (HibernateException e) {
			LOG.error("Initial SessionFactory creation failed." + e);
			throw new ExceptionInInitializerError(e);
		}
		return sessionFactory;
	}
}