package com.wesabe.api.accounts.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * The status of an {@link Account}.
 * 
 * @author coda
 *
 */
public enum AccountStatus {
	/**
	 * A regular, active account.
	 */
	ACTIVE(0),
	
	/**
	 * An account which has been deleted.
	 */
	DELETED(1),
	
	/**
	 * An account which has been locked.
	 */
	LOCKED(2),
	
	/**
	 * An account which has been archived.
	 */
	ARCHIVED(3);
	
	private final int value;
	
	private AccountStatus(int value) {
		this.value = value;
	}
	
	/**
	 * The mapping value of the status instance, to be stored in the database.
	 * 
	 * @return a unique value representing the status
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Converts a mapping value to a status instance.
	 * 
	 * @param value the mapping value
	 * @return the corresponding status instance
	 * @see AccountStatus#getValue()
	 */
	public static AccountStatus byValue(int value) {
		return VALUE_LOOKUP.get(value);
	}
	
	/**
	 * Converts an array of {@link AccountStatus}es into their mapping values;
	 * 
	 * @param statuses
	 * @return an array of corresponding status mapping values
	 */
	public static List<Integer> toValues(Iterable<AccountStatus> statuses) {
		return ImmutableList.copyOf(Iterables.transform(statuses, VALUE_FUNCTION));
	}
	
	private static final Function<AccountStatus, Integer> VALUE_FUNCTION =
		new Function<AccountStatus, Integer>(){
			@Override
			public Integer apply(AccountStatus status) {
				return status.getValue();
			}
		};
	
	private static final Map<Integer, AccountStatus> VALUE_LOOKUP =
		Maps.uniqueIndex(
			Arrays.asList(AccountStatus.class.getEnumConstants()),
			VALUE_FUNCTION
		);
}