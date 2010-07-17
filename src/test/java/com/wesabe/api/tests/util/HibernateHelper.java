package com.wesabe.api.tests.util;

import static org.mockito.Mockito.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

public class HibernateHelper {

	public static Transaction mockTransaction() {
		return mock(Transaction.class);
	}

	public static SessionFactory mockSessionFactory(org.hibernate.classic.Session session) {
		final SessionFactory sessionFactory = mock(SessionFactory.class);
		when(sessionFactory.openSession()).thenReturn(session);
		return sessionFactory;
	}

	public static Session mockSession(Transaction transaction) {
		final Session session = mock(Session.class);
		when(session.beginTransaction()).thenReturn(transaction);
		return session;
	}
	
}