package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.entities.AccountStatus;

@RunWith(Enclosed.class)
public class AccountStatusTest {
	public static class Writing_To_The_Database {
		@Test
		public void itSeralizesActiveAsZero() {
			assertEquals(0, AccountStatus.ACTIVE.getValue());
		}
		
		@Test
		public void itSeralizesDeletedAsOne() {
			assertEquals(1, AccountStatus.DELETED.getValue());
		}
		
		@Test
		public void itSeralizesLockedAsTwo() {
			assertEquals(2, AccountStatus.LOCKED.getValue());
		}
		
		@Test
		public void itSeralizesArchivedAsThree() {
			assertEquals(3, AccountStatus.ARCHIVED.getValue());
		}
	}
	
	public static class Reading_From_The_Database {
		@Test
		public void itDeserializesZeroAsActive() throws Exception {
			assertEquals(AccountStatus.ACTIVE, AccountStatus.byValue(0));
		}
		
		@Test
		public void itDeserializesOneAsDeleted() throws Exception {
			assertEquals(AccountStatus.DELETED, AccountStatus.byValue(1));
		}
		
		@Test
		public void itDeserializesTwoAsLocked() throws Exception {
			assertEquals(AccountStatus.LOCKED, AccountStatus.byValue(2));
		}
		
		@Test
		public void itDeserializesThreeAsArchived() throws Exception {
			assertEquals(AccountStatus.ARCHIVED, AccountStatus.byValue(3));
		}
	}
	
	public static class Converting_Lists_Of_Statuses_To_Values {
		@Test
		public void itReturnsAListOfSeralizedStatuses() throws Exception {
			assertEquals(
				ImmutableList.of(0, 2),
				AccountStatus.toValues(
					ImmutableList.of(
						AccountStatus.ACTIVE, AccountStatus.LOCKED
					)
				)
			);
		}
	}
}
