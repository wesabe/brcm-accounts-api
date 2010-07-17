package com.wesabe.api.accounts.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.SessionFactory;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;

import com.google.inject.Inject;

@Path("/stats/hibernate/")
@Produces(MediaType.TEXT_HTML)
public class HibernateStatsResource {
	private final SessionFactory sessionFactory;
	
	@Inject
	public HibernateStatsResource(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@GET
	public String show() {
		final StringBuilder builder = new StringBuilder();
		builder.append(
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
			"		</thead>\n"
		);
		
		final Statistics statistics = sessionFactory.getStatistics();
		for (String query : statistics.getQueries()) {
			final QueryStatistics queryStatistics = statistics.getQueryStatistics(query);
			builder.append("		<tr>\n");
			builder.append("			<td style=\"text-align: left;\">").append(query).append("</td>\n");
			builder.append("			<td>").append(queryStatistics.getExecutionCount()).append("</td>\n");
			builder.append("			<td>").append(queryStatistics.getExecutionRowCount()).append("</td>\n");
			builder.append("			<td>").append(queryStatistics.getExecutionMinTime()).append("</td>\n");
			builder.append("			<td>").append(queryStatistics.getExecutionAvgTime()).append("</td>\n");
			builder.append("			<td>").append(queryStatistics.getExecutionMaxTime()).append("</td>\n");
			builder.append("		</tr>\n");
		}
		
		builder.append(
			"	</table>\n" +
			"</body>\n" +
			"</html>"
		);
		
		return builder.toString();
	}
}
