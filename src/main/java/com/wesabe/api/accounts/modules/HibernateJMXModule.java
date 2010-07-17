package com.wesabe.api.accounts.modules;

import com.google.inject.AbstractModule;
import com.wesabe.api.accounts.stats.HibernateStatsReporter;

public class HibernateJMXModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HibernateStatsReporter.class).asEagerSingleton();
	}

}
