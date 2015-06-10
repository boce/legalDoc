package com.cdrundle.legaldoc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
	public static String getMd5Str(String password) throws NoSuchAlgorithmException
	{
		// md5 encrypt
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		byte[] domain = md5.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		// converting domain to String
		for (int i = 0; i < domain.length; i++)
		{
			if (Integer.toHexString(0xFF & domain[i]).length() == 1)
			{
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & domain[i]));
			} else
			{
				md5StrBuff.append(Integer.toHexString(0xFF & domain[i]));
			}
		}
		return md5StrBuff.toString();
	}
}
