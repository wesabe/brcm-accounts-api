package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.wesabe.api.accounts.entities.TxactionStatus;

@RunWith(Enclosed.class)
public class TxactionStatusTest {
	public static class Writing_To_The_Database {
		@Test
		public void itSeralizesActiveAsZero() {
			assertEquals(0, TxactionStatus.ACTIVE.getValue());
		}
		
		@Test
		public void itSeralizesDeletedAsOne() {
			assertEquals(1, TxactionStatus.DELETED.getValue());
		}
		
		@Test
		public void itSeralizesArchivedAsThree() {
			assertEquals(3, TxactionStatus.ARCHIVED.getValue());
		}
		
		@Test
		public void itSerializesDisabledAsFive() throws Exception {
			assertEquals(5, TxactionStatus.DISABLED.getValue());
		}
	}
	
	public static class Reading_From_The_Database {
		@Test
		public void itDeserializesZeroAsActive() throws Exception {
			assertEquals(TxactionStatus.ACTIVE, TxactionStatus.byValue(0));
		}
		
		@Test
		public void itDeserializesOneAsDeleted() throws Exception {
			assertEquals(TxactionStatus.DELETED, TxactionStatus.byValue(1));
		}
		
		@Test
		public void itDeserializesThreeAsArchived() throws Exception {
			assertEquals(TxactionStatus.ARCHIVED, TxactionStatus.byValue(3));
		}
		
		@Test
		public void itDeserializesFiveAsDisabled() throws Exception {
			assertEquals(TxactionStatus.DISABLED, TxactionStatus.byValue(5));
		}
	}
	
	public static class Converting_Lists_Of_Statuses_To_Values {
		@Test
		public void itReturnsAListOfSeralizedStatuses() throws Exception {
			assertEquals(
				ImmutableList.of(0, 1),
				TxactionStatus.toValues(
					ImmutableList.of(
						TxactionStatus.ACTIVE, TxactionStatus.DELETED
					)
				)
			);
		}
	}
}
