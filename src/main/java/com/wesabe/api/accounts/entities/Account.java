package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.wesabe.api.util.guid.GUID;
import com.wesabe.api.util.money.CurrencyCodeParser;
import com.wesabe.api.util.money.Money;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@Entity
@Table(name="accounts")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Account")
@NamedQueries({
	@NamedQuery(
	  name="com.wesabe.api.accounts.Account.findByAccountKeyAndRelativeId",
	  query="SELECT a FROM Account a" +
	  		" WHERE a.accountKey = :accountKey AND a.relativeId = :accountId"
	),
	@NamedQuery(
	  name="com.wesabe.api.accounts.Account.findAllByAccountKey",
	  query="SELECT a FROM Account a" +
	  		" WHERE a.accountKey = :accountKey AND a.status IN (:statuses)" +
	  		" ORDER BY a.relativeId ASC"
	)
})
public class Account {
	private static final CurrencyCodeParser CURRENCY_CODE_PARSER = new CurrencyCodeParser();

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column(nullable=false)
	private String name;

	@Column(name="currency")
	private String currencyCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();

	@Column(name="account_key", nullable=false, unique=false, length=64)
	private String accountKey;

	@Column(nullable=false)
	private int status = AccountStatus.ACTIVE.getValue();

	@Column(nullable=false, unique=true, length=64)
	private String guid = GUID.generateRandom(64).toString();

	@Column(name="id_for_user")
	private Integer relativeId;

	@Column(name="position")
	private Integer position = 0;

	@Transient
	private BigDecimal balance;

	@Column(nullable=false, name="account_type_id")
	private int accountTypeId = AccountType.UNKNOWN.getValue();

	@OneToMany(mappedBy="account")
	@BatchSize(size=10)
	private Set<AccountBalance> accountBalances = Sets.newHashSet();

	@OneToMany(mappedBy="account")
	@BatchSize(size=200)
	private Set<Txaction> txactions = Sets.newHashSet();

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="financial_inst_id", nullable=true)
	@NotFound(action=NotFoundAction.IGNORE)
	private FinancialInst financialInst;
	
	public Account() {
	}

	public Account(String name, Currency currency) {
		this.name = name;
		this.currencyCode = currency.getCurrencyCode();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setStatus(AccountStatus status) {
		this.status = status.getValue();
	}

	public AccountStatus getStatus() {
		return AccountStatus.byValue(status);
	}

	public Currency getCurrency() throws UnknownCurrencyCodeException {
		return CURRENCY_CODE_PARSER.parse(currencyCode);
	}

	public void setCurrency(Currency currency) {
		this.currencyCode = currency.getCurrencyCode();
	}

	public GUID getGuid() {
		return new GUID(guid);
	}

	public void setRelativeId(int relativeId) {
		this.relativeId = Integer.valueOf(relativeId);
	}

	public Integer getRelativeId() {
		return relativeId;
	}
	
	public FinancialInst getFinancialInst() {
		return financialInst;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Account) {
			Account that = (Account) obj;

			return Objects.equal(getAccountKey(), that.getAccountKey())
				&& Objects.equal(getCurrency(), that.getCurrency())
				&& Objects.equal(getGuid(), that.getGuid())
				&& Objects.equal(getId(), getId())
				&& Objects.equal(getName(), that.getName())
				&& Objects.equal(getRelativeId(), that.getRelativeId())
				&& Objects.equal(getStatus(), that.getStatus());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(
			accountKey, currencyCode, guid, id, name,
			relativeId, Integer.valueOf(status)
		);
	}

	public Money getBalance() {
		if (!canHaveBalance()) {
			return null;
		}

		if (hasCachedBalance()) {
			return new Money(balance, getCurrency());
		}

		return calculateBalance();
	}

	private boolean hasCachedBalance() {
		return balance != null;
	}

	public boolean hasBalance() {
		return canHaveBalance() && (getBalance() != null);
	}

	private boolean canHaveBalance() {
		return getAccountType().hasBalance();
	}

	private Money calculateBalance() {
		final AccountBalance accountBalance = getMostRecentAccountBalance();
		if (accountBalance == null) {
			// FIXME coda@wesabe.com -- Apr 28, 2009: We *really* don't expect getBalance() to return null.
			return null;
		}

		Money balance = accountBalance.getBalance();
		for (Txaction txaction : getTransactionsSince(accountBalance.getDate())) {
			balance = balance.add(txaction.getAmount());
		}

		this.balance = balance.getValue();
		return balance;
	}

	public DateTime getBalanceDate() {
		final AccountBalance accountBalance = getMostRecentAccountBalance();
		if (accountBalance == null) {
			return null;
		}

		return accountBalance.getDate();
	}

	public Set<Txaction> getTxactions() {
		return txactions;
	}

	private Set<Txaction> getTransactionsSince(final DateTime date) {
		return Sets.filter(getTxactions(), new Predicate<Txaction>() {
			@Override
			public boolean apply(final Txaction txaction) {
				return !txaction.isDeleted() &&
					   !txaction.isDisabled() &&
					   txaction.getDatePosted().isAfter(date);
			}
		});
	}

	private AccountBalance getMostRecentAccountBalance() {
		AccountBalance mostRecentAccountBalance = null;

		for (AccountBalance accountBalance : getAccountBalances()) {
			if ((mostRecentAccountBalance == null) ||
					((accountBalance.getDate() != null) &&
					 (mostRecentAccountBalance.getDate() != null) &&
					 mostRecentAccountBalance.getDate().isBefore(accountBalance.getDate()))) {
				mostRecentAccountBalance = accountBalance;
			}
		}

		return mostRecentAccountBalance;
	}

	public Set<AccountBalance> getAccountBalances() {
		return accountBalances;
	}

	public boolean isActive() {
		return getStatus().equals(AccountStatus.ACTIVE);
	}

	public boolean isArchived() {
		return getStatus().equals(AccountStatus.ARCHIVED);
	}

	public AccountType getAccountType() {
		return AccountType.byValue(accountTypeId);
	}

	public void setAccountType(AccountType accountType) {
		this.accountTypeId = accountType.getValue();
	}
	
	public Integer getPosition() {
		return position;
	}

	public static Account ofType(AccountType accountType) {
		Account account = new Account();
		account.setAccountType(accountType);
		return account;
	}

  // REVIEW: 2009-05-13 <andre@wesabe.com> -- This should probably check for the max
  // of [last balance date, last ssu job date, last transaction date] or Today if those
  // are in the future.
	public DateTime getLastActivityDate() {
		AccountBalance mostRecentAccountBalance = getMostRecentAccountBalance();

		if (mostRecentAccountBalance == null) {
			return null;
		}

		return mostRecentAccountBalance.getCreatedAt();
	}
}
