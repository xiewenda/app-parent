package cn.com.taiji.files;



import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;



/**
 * 文件处理器
 * @author SunJingyan
 * @date 2014年11月5日
 *
 */
@Controller
public class ExcelUtil {
	
	
	/**
	 * 表格内容单元格样式
	 * @param workbook
	 * @return
	 */
	public static CellStyle cellStyle(Workbook workbook)
	{
		//设置字体;  
        Font font = workbook.createFont();  
        //设置字体大小;  
        font.setFontHeightInPoints((short) 12); 
        //设置字体名字;  
        font.setFontName("Droid Sans Fallback"); 
        //设置样式;  
        CellStyle style = workbook.createCellStyle();
        //设置底边框;  
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        //设置底边框颜色;  
        style.setBottomBorderColor(HSSFColor.BLACK.index);  
        //设置左边框;  
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        //设置左边框颜色;  
        style.setLeftBorderColor(HSSFColor.BLACK.index);  
        //设置右边框;  
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        //设置右边框颜色;  
        style.setLeftBorderColor(HSSFColor.BLACK.index);  
       //设置顶边框;  
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        //设置顶边框颜色;  
        style.setTopBorderColor(HSSFColor.BLACK.index);  
        //在样式用应用设置的字体;  
        style.setFont(font);  
        //设置自动换行;  
        style.setWrapText(false);  
        //设置水平对齐的样式为居中对齐;  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
        //设置垂直对齐的样式为居中对齐;  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
        return style;  
	}
	
	/**
	 * 头部样式
	 * @param workbook
	 * @return
	 */
	public static CellStyle headCellStyle(Workbook workbook)
	{
		//设置字体;  
        Font font = workbook.createFont();  
        //设置字体大小;  
        font.setFontHeightInPoints((short) 12); 
        //设置字体名字;  
        font.setFontName("Droid Sans Fallback"); 
        //设置样式;  
        CellStyle style = workbook.createCellStyle();
        //在样式用应用设置的字体;  
        style.setFont(font);  
        //设置自动换行;  
        style.setWrapText(false);  
        //设置水平对齐的样式为居中对齐;  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
        //设置垂直对齐的样式为居中对齐;  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  
        return style;  
	}
	
}