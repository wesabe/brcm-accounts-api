package com.wesabe.api.util.auth.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.util.auth.WesabeUser;

@RunWith(Enclosed.class)
public class WesabeUserTest {
	public static class A_Wesabe_User {
		private final WesabeUser user = WesabeUser.create("409", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc");
		
		@Test
		public void itHasAName() throws Exception {
			assertThat(user.getName(), is("409"));
		}
		
		@Test
		public void itHasAUserId() throws Exception {
			assertThat(user.getUserId(), is(409));
		}
		
		@Test
		public void itHasAnAccountKey() throws Exception {
			assertThat(user.getAccountKey(), is("47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc"));
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(user.toString(), is("WesabeUser [userId=409, accountKey=47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc]"));
		}
		
		@Test
		public void itHasTheSameHashCodeAsAnEqualWesabeUser() throws Exception {
			final WesabeUser other = WesabeUser.create("409", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc");
			assertThat(user.hashCode(), is(other.hashCode()));
		}
		
		@Test
		public void itHasADifferentHashCodeThanAWesabeUserWithADifferentUserId() throws Exception {
			final WesabeUser other = WesabeUser.create("408", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc");
			assertThat(user.hashCode(), is(not(other.hashCode())));
		}
		
		@Test
		public void itHasADifferentHashCodeThanAWesabeUserWithADifferentAccountKey() throws Exception {
			final WesabeUser other = WesabeUser.create("409", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abd");
			assertThat(user.hashCode(), is(not(other.hashCode())));
		}
		
		@Test
		public void itIsEqualToEqualWesabeUser() throws Exception {
			final WesabeUser other = WesabeUser.create("409", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc");
			assertThat(user.equals(other), is(true));
		}
		
		@Test
		public void itIsNotEqualToAWesabeUserWithADifferentUserId() throws Exception {
			final WesabeUser other = WesabeUser.create("408", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abc");
			assertThat(user.equals(other), is(false));
		}
		
		@Test
		public void itIsNotEqualToAWesabeUserWithADifferentAccountKey() throws Exception {
			final WesabeUser other = WesabeUser.create("409", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abd");
			assertThat(user.equals(other), is(false));
		}
		
		@Test
		public void itIsNotEqualToAnObjectWhichIsNotAWesabeUser() throws Exception {
			assertThat(user.equals("WHAT"), is(false));
		}
	}
	
	public static class Credentials_With_A_Bad_User_Id {
		@Test
		public void itDoesNotExist() throws Exception {
			assertThat(WesabeUser.create("f09", "47dfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abd"), is(nullValue()));
		}
	}
	
	public static class Credentials_With_A_Bad_Account_Key {
		@Test
		public void itDoesNotExist() throws Exception {
			assertThat(WesabeUser.create("9", "4gdfae9288abf3d5d2252abfb0bd6ac9662637d646e6df9d5d274bc336e27abd"), is(nullValue()));
		}
	}
}
