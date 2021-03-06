package cn.joy.framework.kits;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
/**
 * 字符串操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class StringKit {
	public static boolean isEmpty(Object info) {
		if (info == null)
			return true;
		return info.toString().trim().length() == 0;
	}

	public static boolean isNotEmpty(Object info) {
		if (info == null)
			return false;
		return info.toString().trim().length() > 0;
	}

	public static String getString(Object s) {
		if (s == null)
			return "";
		return s.toString().trim();
	}
	
	public static String getString(Object s, String defaultValue) {
		if (isEmpty(s))
			return defaultValue;
		return s.toString().trim();
	}
	
	public static String getString(Object s, String... defaultValues) {
		if (isEmpty(s)){
			if(defaultValues!=null){
				for(String defaultValue:defaultValues){
					if(isNotEmpty(defaultValue))
						return defaultValue;
				}
			}
			return "";
		}else
			return s.toString().trim();
	}
	
	public static String trim(String str, String trim){
		return str.trim().replaceFirst("^"+trim, "").replaceFirst(trim+"$", "");
	}
	
	public static String rTrim(String str, String trim){
		return str.trim().replaceFirst(trim+"$", "");
	}
	
	public static String lTrim(String str, String trim){
		return str.trim().replaceFirst("^"+trim, "");
	}
	
	public static String capitalize(String s){
		 return new StringBuilder().append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	
	public static int getOccurIndexOf(String src, String find, int times) {
		if (src == null || src.length() == 0)
			return -1;
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
			if (o == times) 
				return index - 1;
		}
		return -1;
	}
	
	public static int getOccurCount(String src, String find) {
		if (src == null || src.length() == 0)
			return 0;
		int count = 0;
		int index = -1;
		int length = find.length();
		while ((index = src.indexOf(find, index)) > -1) {
			index += length;
			count++;
		}
		return count;
	}
	
	public static String joinCollection(Collection c, String delimiter){
		String result = "";
		StringBuilder s = new StringBuilder();
		if(c!=null){
			for(Object o:c){
				s.append(o).append(delimiter);
			}
			result = s.toString();
			if(result.endsWith(delimiter))
				result = result.substring(0, result.lastIndexOf(delimiter));
		}
		return result;
	}
	
	public static boolean contains4ByteChar(String source) throws Exception{
		byte[] t1 = source.getBytes("UTF-8");  
        for (int i = 0; i < t1.length;) {  
            byte tt = t1[i];  
            if (CharUtils.isAscii((char) tt)) {  
                byte[] ba = new byte[1];  
                ba[0] = tt;  
                i++;  
            }  
            if ((tt & 0xE0) == 0xC0) {  
                byte[] ba = new byte[2];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                i++;  
                i++;  
            }  
            if ((tt & 0xF0) == 0xE0) {  
                byte[] ba = new byte[3];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                i++;  
                i++;  
                i++;  
            }  
            if ((tt & 0xF8) == 0xF0) {  
                return true;
            }  
        } 
        return false;
	}
	
	public static String filter4ByteChar(String source) throws Exception{
		StringBuilder s = new StringBuilder();
		byte[] t1 = source.getBytes("UTF-8");  
        for (int i = 0; i < t1.length;) {  
            byte tt = t1[i];  
            if (CharUtils.isAscii((char) tt)) {  
                byte[] ba = new byte[1];  
                ba[0] = tt;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("1个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xE0) == 0xC0) {  
                byte[] ba = new byte[2];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("2个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xF0) == 0xE0) {  
                byte[] ba = new byte[3];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                i++;  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("3个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xF8) == 0xF0) {  
                byte[] ba = new byte[4];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                ba[3] = t1[i+3];  
                i++;  
                i++;  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("4个字节的字符:" + result);  
                s.append("(-)");
            }  
        }
        return s.toString();
	}
	
	public static boolean isTrue(Object obj){
		String str = getString(obj);
		return "true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str) || "y".equalsIgnoreCase(str) || "1".equalsIgnoreCase(str);
	}
	
	public static List<String> matchAll(String str, String regex){
		List<String> matches = new ArrayList();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while(matcher.find()){
			for(int i=1;i<=matcher.groupCount();i++){
				matches.add(matcher.group(i));
			}
		}
		return matches;
	}
	
	public static String matchOne(String str, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		if(matcher.find()){
			return matcher.group(1);
		}
		return "";
	}
	
	/**
     * 替换固定格式的字符串（支持正则表达式）
     */
    public static String replaceAll(String str, String regex, String replacement) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * 将驼峰风格替换为下划线风格
     */
    public static String camelhumpToUnderline(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }
    
    /**
     * 转为帕斯卡命名方式（如：FooBar）
     */
    public static String toPascalStyle(String str, String seperator) {
        return capitalize(toCamelhumpStyle(str, seperator));
    }

    /**
     * 转为驼峰命令方式（如：fooBar）
     */
    public static String toCamelhumpStyle(String str, String seperator) {
        return underlineToCamelhump(toUnderlineStyle(str, seperator));
    }

    /**
     * 转为下划线命名方式（如：foo_bar）
     */
    public static String toUnderlineStyle(String str, String seperator) {
        str = str.trim().toLowerCase();
        if (str.contains(seperator)) {
            str = str.replace(seperator, "_");
        }
        return str;
    }

    /**
     * 转为显示命名方式（如：Foo Bar）
     */
    public static String toDisplayStyle(String str, String seperator) {
        String displayName = "";
        str = str.trim().toLowerCase();
        if (str.contains(seperator)) {
            String[] words = str.split(seperator);
            for (String word : words) {
                displayName += capitalize(word) + " ";
            }
            displayName = displayName.trim();
        } else {
            displayName = capitalize(str);
        }
        return displayName;
    }
}
