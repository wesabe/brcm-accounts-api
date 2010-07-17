package com.wesabe.api.tests.util;

import static org.mockito.Mockito.*;

import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wesabe.api.accounts.analytics.IntervalSummarizer;
import com.wesabe.api.accounts.analytics.NetWorthSummarizer;
import com.wesabe.api.accounts.analytics.TagHierarchyBuilder;
import com.wesabe.api.accounts.analytics.TagSummarizer;
import com.wesabe.api.accounts.analytics.TxactionListBuilder;
import com.wesabe.api.accounts.analytics.TxactionListBuilderProvider;
import com.wesabe.api.accounts.dao.AccountDAO;
import com.wesabe.api.accounts.dao.TxactionDAO;
import com.wesabe.api.accounts.presenters.AccountListPresenter;
import com.wesabe.api.accounts.presenters.IntervalSummaryPresenter;
import com.wesabe.api.accounts.presenters.NetWorthSummaryPresenter;
import com.wesabe.api.accounts.presenters.TagHierarchyPresenter;
import com.wesabe.api.accounts.presenters.TagSummaryPresenter;
import com.wesabe.api.accounts.presenters.TxactionListPresenter;
import com.wesabe.api.util.auth.WesabeUser;

public class MockResourceContext {
	private SessionFactory sessionFactory;
	private AccountDAO accountDAO;
	private TxactionDAO txactionDAO;
	private IntervalSummarizer intervalSummarizer;
	private IntervalSummaryPresenter intervalSummaryPresenter;
	private TagSummarizer tagSummarizer;
	private TagSummaryPresenter tagSummaryPresenter;
	private TxactionListBuilder txactionListBuilder;
	private TxactionListBuilderProvider txactionListBuilderProvider;
	private TxactionListPresenter txactionListPresenter;
	private AccountListPresenter accountListPresenter;
	private TagHierarchyBuilder tagHierarchyBuilder;
	private TagHierarchyPresenter tagHierarchyPresenter;
	private NetWorthSummarizer netWorthSummarizer;
	private NetWorthSummaryPresenter netWorthSummaryPresenter;
	private WesabeUser user;
	private Injector injector;
	
	public MockResourceContext() {
		this.sessionFactory = mock(SessionFactory.class);
		
		this.accountDAO = mock(AccountDAO.class);
		this.txactionDAO = mock(TxactionDAO.class);
		this.intervalSummarizer = mock(IntervalSummarizer.class);
		this.intervalSummaryPresenter = mock(IntervalSummaryPresenter.class);
		this.tagSummarizer = mock(TagSummarizer.class);
		this.tagSummaryPresenter = mock(TagSummaryPresenter.class);
		
		this.txactionListBuilder = mock(TxactionListBuilder.class);
		this.txactionListBuilderProvider = mock(TxactionListBuilderProvider.class);
		when(txactionListBuilderProvider.get()).thenReturn(txactionListBuilder);
		this.txactionListPresenter = mock(TxactionListPresenter.class);
		
		this.accountListPresenter = mock(AccountListPresenter.class);
		
		this.tagHierarchyBuilder = mock(TagHierarchyBuilder.class);
		this.tagHierarchyPresenter = mock(TagHierarchyPresenter.class);
		
		this.netWorthSummarizer = mock(NetWorthSummarizer.class);
		this.netWorthSummaryPresenter = mock(NetWorthSummaryPresenter.class);
		
		this.user = mock(WesabeUser.class);
		when(user.getUserId()).thenReturn(49);
		when(user.getAccountKey()).thenReturn("0123456789");
		
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(SessionFactory.class).toInstance(sessionFactory);
				bind(AccountDAO.class).toInstance(accountDAO);
				bind(TxactionDAO.class).toInstance(txactionDAO);
				bind(IntervalSummarizer.class).toInstance(intervalSummarizer);
				bind(IntervalSummaryPresenter.class).toInstance(intervalSummaryPresenter);
				bind(TagSummarizer.class).toInstance(tagSummarizer);
				bind(TagSummaryPresenter.class).toInstance(tagSummaryPresenter);
				bind(TxactionListBuilder.class).toProvider(txactionListBuilderProvider);
				bind(TxactionListPresenter.class).toInstance(txactionListPresenter);
				bind(AccountListPresenter.class).toInstance(accountListPresenter);
				bind(TagHierarchyBuilder.class).toInstance(tagHierarchyBuilder);
				bind(TagHierarchyPresenter.class).toInstance(tagHierarchyPresenter);
				bind(NetWorthSummarizer.class).toInstance(netWorthSummarizer);
				bind(NetWorthSummaryPresenter.class).toInstance(netWorthSummaryPresenter);
			}
		});
	}
	
	public <T> T getInstance(Class<T> klass) {
		return injector.getInstance(klass);
	}

	public WesabeUser getUser() {
		return user;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	public TxactionDAO getTxactionDAO() {
		return txactionDAO;
	}
	
	public IntervalSummarizer getIntervalSummarizer() {
		return intervalSummarizer;
	}
	
	public IntervalSummaryPresenter getIntervalSummaryPresenter() {
		return intervalSummaryPresenter;
	}
	
	public TagSummarizer getTagSummarizer() {
		return tagSummarizer;
	}
	
	public TagSummaryPresenter getTagSummaryPresenter() {
		return tagSummaryPresenter;
	}
	
	public TxactionListBuilder getTxactionListBuilder() {
		return txactionListBuilder;
	}
	
	public TxactionListBuilderProvider getTxactionListBuilderProvider() {
		return txactionListBuilderProvider;
	}
	
	public TxactionListPresenter getTxactionListPresenter() {
		return txactionListPresenter;
	}

	public AccountListPresenter getAccountListPresenter() {
		return accountListPresenter;
	}
	
	public TagHierarchyBuilder getTagHierarchyBuilder() {
		return tagHierarchyBuilder;
	}
	
	public TagHierarchyPresenter getTagHierarchyPresenter() {
		return tagHierarchyPresenter;
	}
	
	public NetWorthSummarizer getNetWorthSummarizer() {
		return netWorthSummarizer;
	}
	
	public NetWorthSummaryPresenter getNetWorthSummaryPresenter() {
		return netWorthSummaryPresenter;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}