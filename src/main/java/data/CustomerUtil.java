package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import org.apache.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.serverless.Response;

/*
 * A utility class to perform ORM CRUD operations for Customer class.
 */
public class CustomerUtil {
	private static final Logger LOG = Logger.getLogger(CustomerUtil.class);

	@SuppressWarnings("unchecked")
	public static Customer findCustomerByName(String name, Context context) {
		if (name == null || name.isEmpty()) {
			return null;
		}

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		LOG.info("session open");
		try {
			Transaction tx = null;
			tx = session.beginTransaction();

			List<Customer> customers = session.createQuery("from data.Customer c where c.name = :name")
					.setParameter("name", name)
					.getResultList();

			tx.commit();

			if (customers != null && !customers.isEmpty()) {
				context.getLogger().log(
						"Found customer id =" + customers.get(0).getId() + ", name = " + customers.get(0).getName());
				return customers.get(0);
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	public static Customer addCustomer(String name, Context context) {
		if (name == null || name.isEmpty()) {
			return null;
		}

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		LOG.info("session open");
		try {
			session.beginTransaction();
			Customer customer = new Customer();
			customer.setName(name);

			session.save(customer);			
			session.getTransaction().commit();
			
			return customer;
		} catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
		return null;
	}

	public static String deleteCustomer(String name, Context context) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		LOG.info("session open");

		try {
			session.beginTransaction();
			
			List<Customer> customers = session.createQuery("from data.Customer c where c.name = :name")
					.setParameter("name", name)
					.getResultList();
			
			if (customers == null || customers.isEmpty()) {
				return "Cannot find this customer " + name;
			}
			
			session.delete(customers.get(0));
			session.getTransaction().commit();
			LOG.info("Customer " + name + " was removed");
			return "SUCCESS";
		} catch (HibernateException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {			
			session.close();
		}
		return "FAIL";
	}

}
