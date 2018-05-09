package com.akaxin.common.test;

import com.akaxin.common.crypto.AESCrypto;

public class AESTest {
	public static void main(String[] args) {

		System.out.println(AESCrypto.generate256TSKey().length);

		System.out.println(AESCrypto.generateTSKey().length);
		
		System.out.println(AESCrypto.generateTSKey("11231231").length);
	}
}
