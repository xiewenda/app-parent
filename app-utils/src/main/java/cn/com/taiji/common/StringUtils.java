package cn.com.taiji.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * String 的工具类  其他的String也可以在这里扩展
 * @author xie
 * 20160519
 */
public class StringUtils {
	  /**
	   * 将日期类型转换为字符串 
	   * 如  Date 转换为 2016-05-20
	   * @param date
	   * @param format 格式
	   * @return String
	   */
      public static String DateFormatString(Date date,String format){
    	  if(date==null) return null;
    	  if(format==null || "".equals(format)) format = "yyyyMMdd";
    	  SimpleDateFormat sdf = new SimpleDateFormat(format);
    	  String str = null;
    	  try{
    	   str = sdf.format(date);
    	  }catch (Exception e){
    		  e.printStackTrace();
    		   throw new RuntimeException("日期转换出现异常!");
    	  }
    	  return str;
      }
      /**
       * 将字符串long型日期 转换为字符串日期 
       * 如 "1463734914003" 转换为 2016-05-20 05:03:58
       * @param str
       * @param format
       * @return
       */
     public static String StringLongformatStringDate(String str,String format){
    	   if(str==null || "".equals(str)) return null;
    	   Date date = new Date(Long.parseLong(str));
    	  return DateFormatString(date,format);
      }
     /**
      * 将字符串格式日期转换为date 
      * 如 2016-05-20 转换为日期Date 
      * @param str
      * @param format
      * @return
      */
     public static Date StringDateParseDate(String str , String format){
    	 if(str==null || "".equals(str)) return null;
    	 if(format==null || "".equals(format)) format = "yyyyMMdd";
    	 	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	 	Date date = null;
	   	  try{
	   		date = sdf.parse(str);
	   	  }catch (Exception e){
	   		   e.printStackTrace();
	   		   throw new RuntimeException("日期转换出现异常!");
	   	  }
   	  return date;
     }
     /**
      * 将字符串格式日期转换为Long 
      * 如 2016-05-20 05:03:58 转换为日期Long型 1463691838000
      * 最多转到秒数毫秒数默认补0
      * @param str
      * @param format
      * @return
      */
     public static Long StringDateParseLong(String str , String format){
    	 if(str==null || "".equals(str)) return null;
    	 if(format==null || "".equals(format)) format = "yyyyMMdd";
    	 	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	 	Date date = null;
	   	  try{
	   		date = sdf.parse(str);
	   	  }catch (Exception e){
	   		   e.printStackTrace();
	   		   throw new RuntimeException("日期转换出现异常!");
	   	  }
   	  return date.getTime();
     }
     public static boolean isNotEmpty(Object str){
    	 if(str!=null && !"".equals(str+""))return true;
    	 return false;
     }
     public static void main(String[] args) {
		System.out.println(StringUtils.DateFormatString(new Date(),"yyyy-MM-dd"));
	}
}
