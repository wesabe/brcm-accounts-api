package com.wesabe.api.accounts;

import com.codahale.shore.Shore;

public class Runner {
	public static void main(String[] args) throws Exception {
		Shore.run(new Configuration(), args);
	}
}