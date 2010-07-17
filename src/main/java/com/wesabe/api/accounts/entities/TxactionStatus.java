package com.wesabe.api.accounts.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public enum TxactionStatus {
	ACTIVE(0),
	DELETED(1),
	ARCHIVED(3),
	DISABLED(5);
	
	private final int value;
	
	private TxactionStatus(int value) {
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
	 * @see TxactionStatus#getValue()
	 */
	public static TxactionStatus byValue(int value) {
		return VALUE_LOOKUP.get(value);
	}
	
	/**
	 * Converts an array of {@link AccountStatus}es into their mapping values;
	 * 
	 * @param statuses
	 * @return an array of corresponding status mapping values
	 */
	public static List<Integer> toValues(Iterable<TxactionStatus> statuses) {
		return ImmutableList.copyOf(Iterables.transform(statuses, VALUE_FUNCTION));
	}
	
	private static final Function<TxactionStatus, Integer> VALUE_FUNCTION =
		new Function<TxactionStatus, Integer>(){
			@Override
			public Integer apply(TxactionStatus status) {
				return status.getValue();
			}
		};
	
	private static final Map<Integer, TxactionStatus> VALUE_LOOKUP =
		Maps.uniqueIndex(
			Arrays.asList(TxactionStatus.class.getEnumConstants()),
			VALUE_FUNCTION
		);
}
