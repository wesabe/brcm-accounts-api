package com.wesabe.api.accounts.stats;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.hibernate.SessionFactory;
import org.hibernate.jmx.StatisticsService;

import com.google.inject.Inject;

public class HibernateStatsReporter {
	
	@Inject
	public HibernateStatsReporter(SessionFactory sessionFactory, Logger logger) {
		final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		if (mbeanServer != null) {
			try {
				ObjectName on = new ObjectName("Hibernate:type=statistics,application=brcm");
				StatisticsService mBean = new StatisticsService();
				mBean.setStatisticsEnabled(true);
				mBean.setSessionFactory(sessionFactory);
				mbeanServer.registerMBean(mBean, on);
				logger.info("Hibernate JMX service enabled.");
			} catch (JMException e) {
				logger.log(Level.WARNING, "Unable to start JMX Hibernate service", e);
			}
		} else {
			logger.warning("No JMX service available -- Hibernate statistics will be unavailable.");
		}
	}
}
