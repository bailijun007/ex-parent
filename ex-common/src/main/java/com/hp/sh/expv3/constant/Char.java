package com.hp.sh.expv3.constant;

import java.util.regex.Pattern;

public class Char {

	private static final String SPECIAL = "#@&";
	
	private static final String identifier = "[a-zA-Z_0-9]+";
	
	public static boolean isIdentifier(String str){
		return Pattern.matches(identifier, str);
	}
	
	public static void main(String[] args) {
		System.out.println(Char.isIdentifier("8795462-uygjhi89o"));
	}
	
}
