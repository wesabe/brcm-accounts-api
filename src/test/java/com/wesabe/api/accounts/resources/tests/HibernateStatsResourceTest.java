package com.wesabe.api.accounts.resources.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.resources.HibernateStatsResource;
import com.wesabe.api.tests.util.MockResourceContext;

@RunWith(Enclosed.class)
public class HibernateStatsResourceTest {
	public static class Generating_Hibernate_Statistics {
		private MockResourceContext context;
		private Statistics statistics;
		private QueryStatistics queryStats;
		private HibernateStatsResource resource;

		@Before
		public void setup() throws Exception {
			this.context = new MockResourceContext();

			this.queryStats = mock(QueryStatistics.class);
			when(queryStats.getExecutionAvgTime()).thenReturn(1L);
			when(queryStats.getExecutionCount()).thenReturn(2L);
			when(queryStats.getExecutionMaxTime()).thenReturn(3L);
			when(queryStats.getExecutionMinTime()).thenReturn(4L);
			when(queryStats.getExecutionRowCount()).thenReturn(5L);

			this.statistics = mock(Statistics.class);
			when(statistics.getQueryStatistics("SELECT o FROM Object o")).thenReturn(queryStats);
			when(statistics.getQueries()).thenReturn(new String[] { "SELECT o FROM Object o" });

			when(context.getSessionFactory().getStatistics()).thenReturn(statistics);

			this.resource = context.getInstance(HibernateStatsResource.class);
		}

		@Test
		public void itRendersHibernateStatisticsAsHtml() throws Exception {
			assertThat(resource.show(), is(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
				"	\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
				"\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
				"<head>\n" +
				"	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
				"	<title>Hibernate Statistics</title>\n" +
				"	<style lang=\"text/css\">\n" +
				"		td { border-bottom: 1px solid black; }\n" +
				"	</style>\n" +
				"</head>\n" +
				"<body>\n" +
				"	<h1>Hibernate Statistics</h1>\n" +
				"	<h2>Queries</h2>\n" +
				"	<table style=\"width:50em; font-family: 'Consolas', monospace; text-align: right;\">\n" +
				"		<thead>\n" +
				"			<tr>\n" +
				"				<th style=\"text-align: left;\">Query</th>\n" +
				"				<th>total queries</th>\n" +
				"				<th>total rows</th>\n" +
				"				<th>min (ms)</th>\n" +
				"				<th>mean (ms)</th>\n" +
				"				<th>max (ms)</th>\n" +
				"			</tr>\n" +
				"		</thead>\n" +
				"		<tr>\n" +
				"			<td style=\"text-align: left;\">SELECT o FROM Object o</td>\n" +
				"			<td>2</td>\n" +
				"			<td>5</td>\n" +
				"			<td>4</td>\n" +
				"			<td>1</td>\n" +
				"			<td>3</td>\n" +
				"		</tr>\n" +
				"	</table>\n" +
				"</body>\n" +
				"</html>"
			));
		}
	}
}
