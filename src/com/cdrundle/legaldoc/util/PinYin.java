package com.cdrundle.legaldoc.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYin {

    private static final HanyuPinyinOutputFormat PINYIN_OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
    static {
    	PINYIN_OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
    	PINYIN_OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
    	PINYIN_OUTPUT_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);  
    }

    /**
     * 
     * @param str
     * @return
     *   返回汉字的全拼
     */
    public static String toPinYin(String str) {
    	return toPinYin(str, false);
    }
    
    /**
     * 
     * @param str
     * @return
     *   返回汉字拼音首字母
     */
    public static String toJianPin(String str) {
    	return toPinYin(str, true);
    }
    
    /**
     * 
     * @param str
     * @param onlyInitial
     *   是否只获取拼音首字母
     * @return
     *   返回汉字的拼音，其中包含的非汉字字符原样返回
     */
    private static String toPinYin(String str, boolean onlyInitial) {  
    	if(str == null || str.length() == 0) {
    		return str;
    	}    	
        char [] hanzi=new char[str.length()];  
        for(int i=0;i<str.length();i++){  
            hanzi[i]=str.charAt(i);  
        }  
         
        StringBuilder sb = new StringBuilder();
        try {  
            for (int i = 0; i < hanzi.length; i++) {
                String[] py = PinyinHelper.toHanyuPinyinStringArray(hanzi[i], PINYIN_OUTPUT_FORMAT);
                if(py != null && py.length >= 1 && py[0] != null && py[0].length() > 0) {
                	sb.append(onlyInitial ? py[0].substring(0, 1) : py[0]);
                }
                else {
                	sb.append(hanzi[i]);
                }
            }  
        }
        catch (BadHanyuPinyinOutputFormatCombination e) {  
            e.printStackTrace();  
        }  
  
        return sb.toString();  
    }  

}
