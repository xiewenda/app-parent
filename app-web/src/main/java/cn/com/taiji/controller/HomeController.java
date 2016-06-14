package cn.com.taiji.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bjca.sso.bean.UserTicket;
import com.bjca.sso.processor.TicketManager;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.Role;
import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.dto.KendoTreeViewDto;
import cn.com.taiji.sys.service.MenuService;
import cn.com.taiji.sys.service.RoleService;
import cn.com.taiji.sys.service.UserService;

@Controller
public class HomeController {
	@Inject
	private RoleService roleService;
	@Inject
	private MenuService menuService;
	@Autowired
	protected AuthenticationManager authenticationManager;
	@Autowired
	private UserService userService;
	/**
	 * 主页
	 * @return
	 */
	@RequestMapping("/system_frame")
	public String homePage(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttri){
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
		String loginTurn = (String)request.getSession(true).getAttribute("loginTurn");
//正式部署时，下面代码放开
//		if("zjwsy".equals(loginTurn)){
			return "redirect:./systemFrameTurn";
//		}else if("zxfwsq".equals(loginTurn)){
//			redirectAttri.addAttribute("syFlag", "sy");
//			return "redirect:./zixun/xjpage";
//		}else{
//			return "redirect:http://172.24.49.97/";
//		}
	}
	/**
	 * 办公系统首页
	 * @param request
	 * @param response
	 * @param redirectAttri
	 * @return
	 */
	@RequestMapping("/systemFrameTurn")
	public String systemFramePage(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttri){
		User user = (User)request.getSession(true).getAttribute("user");
		System.out.println(user);
		if(user == null){
			return "redirect:http://172.24.49.97:8080/ZJWInterface?loginTurn=zjwsy";
		}else{
			response.setHeader("X-Frame-Options", "SAMEORIGIN");
		}
		//日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		request.setAttribute("now", df.format(new Date()));
		//周几
		 String weekString = "";
        final String dayNames[] = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekString = dayNames[dayOfWeek - 1];
		request.setAttribute("week", weekString);
		if(user != null){
			 Set<Role> roles = user.getRoles();
			 for(Role role :roles){
				 if("ROLE_JXW".equals(role.getRoleDesc())){
					 return "system_frame0317";
				 }
			 }
		}
		return "system_frame";
	}
	/**
	 * 办公系统首页
	 * @param request
	 * @param response
	 * @param redirectAttri
	 * @return
	 */
	@RequestMapping("/adminLogin")
	public String adminLogin(HttpServletResponse response){
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
		return "loginAdmin";
	}
	/**
	 * 内网单点登录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/calogin")
	public String cas(Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
			//服务器证书
			String BJCA_SERVER_CERT = request.getParameter("BJCA_SERVER_CERT");
			//票据
			String BJCA_TICKET = request.getParameter("BJCA_TICKET");
			//票据类型
			String BJCA_TICKET_TYPE = request.getParameter("BJCA_TICKET_TYPE");
			System.out.println("BJCA_SERVER_CERT==========" + BJCA_SERVER_CERT);// 处理票据信息
			System.out.println("BJCA_TICKET==========" + BJCA_TICKET);// 处理票据信息
			System.out.println("BJCA_TICKET_TYPE==========" + BJCA_TICKET_TYPE);// 处理票据信息
			TicketManager ticketmag = new TicketManager();
			//验证签名和解密
			UserTicket userticket = ticketmag.getTicket(BJCA_TICKET,BJCA_TICKET_TYPE, BJCA_SERVER_CERT);
			String s_role = "";
			if(userticket!=null){
				String username = userticket.getUserName();
				String userid = userticket.getUserUniqueID();
				String departid = userticket.getUserDepartCode();
				//取角色信息
				Hashtable roles = userticket.getUserRoles();
				if (roles != null && roles.size() > 0) {
					int index = 1;
					Enumeration e = roles.keys();
					Enumeration e2 = roles.elements();
					for (; e.hasMoreElements();) {
						String rolecode = (String) e.nextElement();
						String rolename = (String) e2.nextElement();
						if (rolename.indexOf("?") != -1) {
							rolename = new String(rolename.getBytes("UTF-8"),"ISO-8859-1");
						}
						if (index == 1) {
							s_role = rolecode;
						} else {
							s_role = s_role + "," + rolecode;
						}
						index++;
						System.out.println("s_role======="+s_role);
						System.out.println("rolename======="+rolename);
					}
				}
				System.out.println("username======="+username);
				System.out.println("userid======="+userid);
				System.out.println("departid======="+departid);
				request.getSession().setAttribute("username",username);
				request.getSession().setAttribute("userid",userid);
				request.getSession().setAttribute("departid",departid);
				request.getSession().setAttribute("roles",s_role);
				//获取用户信息并判断是否存在
				User user = new User();
				user = userService.findById(userid);
				 if (user != null ) {
					  request.getSession().setAttribute("user",user);
					  UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getLoginName(), DigestUtils.sha256Hex("123456"));
					  try {
							token.setDetails(new WebAuthenticationDetails(request));
							Authentication authenticatedUser = authenticationManager.authenticate(token);
							SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
							request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.getContext());
						} catch (AuthenticationException e) {
							System.out.println("Authentication failed: " + e.getMessage());
							return "404";
						}
					  return "redirect:systemFrameTurn";// 跳转到自动登陆页面
				 }else {
					  model.addAttribute("errorMessage","用户未同步！");
					  return "thymeleaf/sso_error";
					}
			}else {
				model.addAttribute("errorMessage","单点出错，票据为空！");
				return "thymeleaf/sso_error";
			}
	}
	@RequestMapping("/menu-tree-byrole")
	public Model index(Model model)
	{
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<String> roles = AuthorityUtils.authorityListToSet(userDetails.getAuthorities());
		List<Menu> menus =new ArrayList<Menu>();
		if(roles.contains("ROLE_ADMIN")){
			menus = menuService.findRootTree();
		}else{
			menus=roleService.findMenusByRoleIds(roles);
		}
		//使用kendoui的kendoTreeView实现
		KendoTreeViewDto treeViewDto = new KendoTreeViewDto();
		List<KendoTreeViewDto> list = treeViewDto.menuTree(menus);
		model.addAttribute("menuTree", list);
		return model;
	}
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String home(Model model,HttpServletResponse response) {
		response.setHeader("X-Frame-Options", "SAMEORIGIN");
		return "index";
	}
	// for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied(Principal user) {
		ModelAndView model = new ModelAndView();
		if (user != null) {
			model.addObject("msg", "Hi " + user.getName() 
					+ ", you do not have permission to access this page!");
		} else {
			model.addObject("msg", 
					"You do not have permission to access this page!");
		}

		model.setViewName("403");
		return model;

	}

	@RequestMapping(value = "/image", method = RequestMethod.GET)
	public String image(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",0);
		BufferedImage img = new BufferedImage(68, 22,  

				BufferedImage.TYPE_INT_RGB);  

		// 得到该图片的绘图对象    

		Graphics g = img.getGraphics();  

		Random r = new Random();  

		Color c = new Color(200, 150, 255);  

		g.setColor(c);  

		// 填充整个图片的颜色    

		g.fillRect(0, 0, 68, 22);  

		// 向图片中输出数字和字母    

		StringBuffer sb = new StringBuffer();  

		char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();  

		int index, len = ch.length;  

		for (int i = 0; i < 4; i++) {  
			index = r.nextInt(len);  
			g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt  
					(255)));  
			g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));  
			// 输出的  字体和大小                      
			g.drawString("" + ch[index], (i * 15) + 3, 18);  
			//写什么数字，在图片 的什么位置画    
			sb.append(ch[index]);  
		}  
		request.getSession().setAttribute("j_captcha", sb.toString());  
		try {
			ImageIO.write(img, "JPEG", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return null;
	}
	/**
	 * excel导出
	 * @param userAgent
	 * @param request
	 * @param response
	 */
//	@RequestMapping("/file-to-excel")
//	public void FileToExcel(@RequestHeader("User-Agent") String userAgent,HttpServletRequest request,HttpServletResponse response)
//	{
//		response.reset();
//		ServletOutputStream os = null;
//		try {
//			os = response.getOutputStream();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
//		response.reset();   
//		String fileName = "评审中心劳务费及专家费明细表2014-4.xls";
//		response.setCharacterEncoding("UTF-8");
//		boolean isIe = userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1; // trident for IE 11
//		try 
//		{
//			if (isIe)
//			{
//				response.setHeader("Content-Disposition","attachment; filename=\""+ URLEncoder.encode(fileName, "utf-8") + "\"");
//			} 
//			else 
//			{
//				response.setHeader("Content-Disposition","attachment; filename=\""+ MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");
//			}
//		} catch (Exception e) {
//		}
//		response.setContentType("application/msexcel");
//		String templatePath=getClass().getResource("/").getFile().toString()+File.separator+"excelTemplates/评审中心劳务费及专家费明细表模板2014-4.xls";  
//		Workbook workbook = null;
//		try {
//			workbook = new XSSFWorkbook(new FileInputStream(templatePath));
//		} catch (Exception ex) {
//			try {
//				workbook = new HSSFWorkbook(new FileInputStream(templatePath));
//			} catch (IOException e) {
//			}
//		}
//		Sheet sheet = workbook.getSheetAt(0);
//		//设置头部
//		Row headRow = sheet.createRow(3);
//		CellStyle headCellStyle = ExcelUtil.headCellStyle(workbook);
//		for(int col= 0; col<10; col++)
//		{
//			headRow.createCell(col);
//		}
//		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(3,3,0,3);
//		sheet.addMergedRegion(cellRangeAddress1);
//		CellRangeAddress cellRangeAddress2 = new CellRangeAddress(3,3,4,8);
//		sheet.addMergedRegion(cellRangeAddress2);
//		sheet.getRow(3).getCell(0).setCellValue("项目(部门)：专家委xxxxxxxxxxxxxx项目");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
//		String dateStr = sdf.format(new Date());
//		sheet.getRow(3).getCell(4).setCellValue(dateStr);
//		sheet.getRow(3).getCell(4).setCellStyle(headCellStyle);
//		sheet.getRow(3).getCell(9).setCellValue("单位:元");
//		sheet.getRow(3).getCell(9).setCellStyle(headCellStyle);
//		CellStyle cellStyle = ExcelUtil.cellStyle(workbook);
//		//生成10条数据
//		int length = 10;
//		Double salary = null;
//		Double tax = null;
//		Double heji_salary = new Double(0);
//		Double heji_tax = new Double(0);
//		Double heji_he = new Double(0);
//		//创建行,单元格
//		for(int rowNo = 5; rowNo < 5+length; rowNo++)
//		{
//			Row row = sheet.getRow(rowNo);
//			if(row==null)
//			{
//				row = sheet.createRow(rowNo);
//				row.setHeightInPoints((float)34.02);
//				for(int col= 0; col<10; col++)
//				{
//					row.createCell(col);
//					row.getCell(col).setCellStyle(cellStyle);
//				}
//			}
//			//设置单元格值
//			row.getCell(0).setCellValue(rowNo-4);
//			row.getCell(1).setCellValue("张瑞");
//			row.getCell(2).setCellValue("副教授");
//			row.getCell(3).setCellValue("130984195112123911");
//			salary = new Double(10000.10);
//			row.getCell(4).setCellValue(salary);
//			tax = new Double(500.23);
//			row.getCell(5).setCellValue(tax);
//			row.getCell(6).setCellValue(salary-tax);
//			row.getCell(7).setCellValue("中国工商银行");
//			row.getCell(8).setCellValue("6210870490864567");
//			row.getCell(9).setCellValue(row.getCell(1).getStringCellValue());
//			heji_salary += salary;
//			heji_tax += tax;
//		}
//		heji_he = heji_salary - heji_tax;
//		//合计
//		Row hejiRow = sheet.createRow(5+length);
//		hejiRow.setHeightInPoints((float)34.02);
//		for(int col= 0; col<10; col++)
//		{
//			hejiRow.createCell(col);
//			hejiRow.getCell(col).setCellStyle(cellStyle);
//		}
//		hejiRow.getCell(0).setCellValue("合计");
//		hejiRow.getCell(4).setCellValue(heji_salary);
//		hejiRow.getCell(5).setCellValue(heji_tax);
//		hejiRow.getCell(6).setCellValue(heji_he);
//		try {
//			workbook.write(os);
//			os.flush();
//			os.close();
//		} catch (IOException e) {
//		}  
//
//	}
//	/**
//	 * 注意dataMap里存放的数据Key值要与模板中的参数相对应
//	 * 
//	 * @param dataMap
//	 */
//	private static Map getData() {
//		Map<String, Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("meetingname", "测试用例");
//		List<Experts> _experts = new ArrayList<Experts>();
//		Experts e1 = new Experts();
//		e1.setSeq("1");
//		e1.setEname("刘钊");
//		e1.setPost("开发");
//		_experts.add(e1);
//		e1 = new Experts();
//		e1.setSeq("2");
//		e1.setEname("刘招");
//		e1.setPost("实施");
//		_experts.add(e1);
//		e1 = new Experts();
//		e1.setSeq("3");
//		e1.setEname("刘照");
//		e1.setPost("售前");
//		_experts.add(e1);
//		dataMap.put("elist", _experts);
//
//
//
//		List<Depts> _dept = new ArrayList<Depts>();
//		Depts d1 = new Depts();
//		d1.setSeq("1");
//		d1.setDname("张三");
//		d1.setDeptname("太极");
//		_dept.add(d1);
//		d1 = new Depts();
//		d1.setSeq("2");
//		d1.setDname("李四");
//		d1.setDeptname("百度");
//		_dept.add(d1);
//		d1 = new Depts();
//		d1.setSeq("3");
//		d1.setDname("王五");
//		d1.setDeptname("搜狐");
//		_dept.add(d1);
//		dataMap.put("dlist", _dept);
//		return dataMap;
//
//	}
//	@RequestMapping(value="/expWord")
//	public  void expWord(@RequestHeader("User-Agent") String userAgent,HttpServletRequest request,HttpServletResponse response) {
//		DocumentHandler h = new DocumentHandler();
//		String pathname= h.createDoc(getData());
//		String rootPath=getClass().getResource("/").getFile().toString();  
//		if(pathname!=null)
//		{
//			//路径
//			File file = new File(rootPath+pathname);
//			long fileLength = file.length();
//			response.setContentType("text/plain; charset=UTF-8");
//			response.setContentLength((int) fileLength);
//			response.setCharacterEncoding("UTF-8");
//			boolean isIe = userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1; // trident for IE 11
//			try 
//			{
//				if (isIe)
//				{
//					response.setHeader("Content-Disposition","attachment; filename=\""+ URLEncoder.encode("测试.doc", "utf-8") + "\"");
//				} 
//				else 
//				{
//					response.setHeader("Content-Disposition","attachment; filename=\""+ MimeUtility.encodeWord("测试.doc", "utf-8", "Q") + "\"");
//				}
//			} catch (Exception e) {
//			}
//			InputStream fis = null;
//			try {
//				fis = new BufferedInputStream(new FileInputStream(rootPath+pathname));
//				byte[] buffer = new byte[fis.available()];
//				fis.read(buffer); 
//				fis.close(); 
//				OutputStream toClient = new BufferedOutputStream(response.getOutputStream()); 
//				toClient.write(buffer); 
//				toClient.flush(); 
//				toClient.close();
//			} catch (FileNotFoundException e) {
//			} catch (IOException e) {
//			} 
//		}
//
//	}
		/**
		 * CA证书和非证书用户集成登录
		 * @param request
		 * @param response
		 * @return
		 */
//	    @RequestMapping("/integrationLogin")
//		public String integrationLogin(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttr) throws Exception {
//			request.setCharacterEncoding("GBK");
//			boolean loginFlag = false;
//			String credenceType = request.getParameter("credenceType");
//			if (credenceType == null) {
//				String error = "客户端登录凭证验证失败！";
//				//返回错误页面
//				request.setAttribute("errorMsg", error);
//				System.out.println("登录失败");
//				return "redirect:http://172.24.49.97/";
//			}
//			//非证书用户登录
//			if (credenceType.equals("COMMON")) {
//				this.noCertLogin(request, response);
//				//证书用户登录
//			} else if (credenceType.equals("CERTIFICATE")) {
//				certLogin(request, response);
//			}
//			if(loginFlag){
//				return "redirect:http://172.24.49.97/";
//			}else{
//				System.out.println("登录失败");
//				return "redirect:http://172.24.49.97/";
//			}
//			
//		}
//		public boolean noCertLogin(HttpServletRequest request, HttpServletResponse response){
//			String loginName = request.getParameter("loginName");
//			String loginPwd = request.getParameter("password");
//			loginName = loginName.trim();
//			User user = userService.login(loginName, DigestUtils.sha256Hex(loginPwd));
//			HttpSession session = request.getSession(true);  
//			session.setAttribute("user", user);
//			if (user == null) {
//				String error = "登录名称或密码不正确，请重新输入";
//				session.setAttribute("errorMsg", error);
//				return false;
//				//throw new AuthenticationServiceException("登录名称或密码不正确，请重新输入");
//			}
//			return true;
//		}
		/**
		 * 证书验证方法
		 * @param request
		 * @param response
		 * @return
		 */
//		public boolean certLogin(HttpServletRequest request, HttpServletResponse response){
//			HttpSession session = request.getSession();
//			String loginName = null;
//			String loginPwd = null;
//			String ranStr = (String) session.getAttribute("Random");
//			String clientCert = request.getParameter("secX_UserCert");
//			String UserSignedData = request.getParameter("secX_SignedData");
//			String ContainerName = request.getParameter("secX_ContainerName");
//			SecurityEngineDeal se = SecurityEngineDeal.getInstance();;
////			try {
////				Object safeObj = null;
////				if (context != null) {
////					safeObj = context.getAttribute("SYSTEM_SECURE");
////				}
////				if (safeObj == null) {
////					se = SecurityEngineDeal.getInstance();
////					if (se != null) {
////						context.setAttribute("SYSTEM_SECURE", se);
////					}
////				} else {
////					se = (SecurityEngineDeal) safeObj;
////				}
////			} catch (Exception e) {
////				e.printStackTrace();
////				String error = "服务器证书配置错误，请您与系统管理员联系！";
////				doReturnError(request, response, error);
////				return;
////			}
//			boolean vstatus = false;
//			try {
//				boolean retValue = se.Certificate_IsValid(clientCert);
//				String signcert = se.SignedDataByP7_Verify(ranStr, UserSignedData);
//				if (signcert == null) {
//					// 验证签名失败
//					String error = "验证签名失败！";
//					vstatus = false;
//					session.setAttribute("errorMsg", error);
//				//	this.doReturnError(request, response, error);
//					return false;
//				}
//				if (retValue) {
//					vstatus = true;
//					System.out.println("客户端证书验证成功！");
//					session.setAttribute("ContainerName", ContainerName);
//					// 获得登陆用户ID
//					String uniqIdOid = "2.16.840.1.113732.2";
//					String uniqueIdStr = se.Certificate_GetExtInfo(clientCert,uniqIdOid);
//	                if(uniqueIdStr.contains("JJ")){
//	                	uniqueIdStr = se.Certificate_GetExtInfo(clientCert,"1.2.86.11.7.1.8");
//	                }
////					String pin = se.Certificate_GetInfo(clientCert, 2);
////					session.setAttribute("UniqueID", uniqueIdStr);
////					loginName = uniqueIdStr;
////					loginPwd = "qwewr232sd==";
//	                User user = userService.findById(uniqueIdStr);
//	                session.setAttribute("user", user);
//	                if(user == null){
//	                	String error = "用户不存在！";
//						session.setAttribute("errorMsg", error);
//						return false;
//	                }
//				} else {
//					vstatus = false;
//					String error = "客户端证书验证失败！";
//					session.setAttribute("errorMsg", error);
//					//	this.doReturnError(request, response, error);
//					return false;
//				}
//			} catch (Exception ex) {
//				vstatus = false;
//				String error = "客户端证书验证失败！";
//				session.setAttribute("errorMsg", error);
//				//	this.doReturnError(request, response, error);
//				return false;
//			}
//			if (vstatus) {
//				// 门户系统在统一认证平台中的系统编码；
//				String infoCode = "yjmh";
//				LoginTicketManager ltm = new LoginTicketManager();
//				LoginTicket lt = ltm.getLoginTicket(request,loginName, loginPwd, infoCode);
//				java.lang.String value = null;
//				java.lang.String departCode = null;
//				java.lang.String departName = null;
//				java.lang.String userName = null;
//				value = lt.getUserUniqueID();
//				if (value.length() > 1) {
//					userName = lt.getUserName();
//					departCode = lt.getDepartCode();
//					departName = lt.getDepartName();
//					if (departCode == null || departCode.equals("null"))
//						departCode = "";
//					if (departName == null || departName.equals("null"))
//						departName = "";
//				}
//				System.out.println("登录后得到32位码===============" + value);
//				System.out.println("登录后得到用户姓名==============" + userName);
//				System.out.println("登录后得到机构编码===============" + departCode);
//				System.out.println("登录后得到机构名称===============" + departName);
//				if (value != null && value.equals("0")) {
//					String error = "用户不存在，请您确认后再发送！";
//					session.setAttribute("errorMsg", error);
//					return false;
//				} else if (value != null && value.equals("1")) {
//					String error = "用户密码错误，请您确认后再发送！";
//					session.setAttribute("errorMsg", error);
//					//	this.doReturnError(request, response, error);
//					return false;
//				} else if (value != null && value.equals("2")) {
//					String error = "用户账号已被冻结，请您确认后再发送！";
//					session.setAttribute("errorMsg", error);
//					//	this.doReturnError(request, response, error);
//					return false;
//				}else {
//					session.setAttribute("com.bjca.uams.SSOId", value);
//				}
////				RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
////				if (rd == null) {
////					String error = path + "访问地址不存在，请您确认后再发送！";
////					this.doReturnError(request, response, error);
////					return ;
////				}
//			//	rd.forward(request, response);
//				return true;
//			} else {
//				String error = "客户端证书验证失败！";
//				session.setAttribute("errorMsg", error);
//				//	this.doReturnError(request, response, error);
//				return false;
//			}
//		//	return true;
//		}
//		 public boolean certLogin(HttpServletRequest request, HttpServletResponse response) {
//				response.setHeader("Pragma", "No-cache");
//				response.setHeader("Cache-Control", "no-cache");
//				response.setDateHeader("Expires", 0);
//				HttpSession session = request.getSession();
//				String ranStr = (String) session.getAttribute("Random");
//				SecurityEngineDeal sed = null;
//				try {
//					sed = SecurityEngineDeal.getInstance("SecXV3Default");
//				} catch (JDOMException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InitException e) {
//					e.printStackTrace();
//				} catch (AppNotfoundException e) {
//					e.printStackTrace();
//				}
//				//获得登陆用户cert
//				String clientCert = request.getParameter("UserCert");
//				String UserSignedData = request.getParameter("UserSignedData");
//				String ContainerName = request.getParameter("ContainerName");
//				String certPub="";
//				try {
//					certPub = sed.getCertInfo(clientCert, 8);
//				} catch (Base64Exception e1) {
//					e1.printStackTrace();
//				} catch (GetCertInfoException e1) {
//					e1.printStackTrace();
//				}
//				//验证服务器证书有效期
//				java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("yyyy/MM/dd");
//				String dateNow = date.format(new Date());
//				try {
//					Date datenow = date.parse(dateNow);
//				} catch (ParseException e1) {
//					e1.printStackTrace();
//					System.out.println("获取证书有效期开始时间失败:" + e1.getMessage());
//				}
//				String servercert = null;
//				try {
//					servercert = sed.getServerCertificate();
//				} catch (Base64Exception e1) {
//					e1.printStackTrace();
//					System.out.println("获取证书有效期失败:" + e1.getMessage());
//					session.setAttribute("errorMsg", "获取证书有效期失败:" + e1.getMessage());
//					return false;
//				}
//				String servercertdate = null;
//				try {
//					servercertdate = sed.getCertInfo(servercert, 12);
//				} catch (Base64Exception e1) {
//					e1.printStackTrace();
//					System.out.println("获取证书有效期截止时间失败:" + e1.getMessage());
//					session.setAttribute("errorMsg", "获取证书有效期截止时间失败:" + e1.getMessage());
//					return false;
//				} catch (GetCertInfoException e1) {
//					e1.printStackTrace();
//					System.out.println("获取证书有效期截止时间失败:" + e1.getMessage());
//					session.setAttribute("errorMsg", "获取证书有效期截止时间失败:" + e1.getMessage());
//					return false;
//				}
//				try {
//					Date scertdate = date.parse(servercertdate);
//				} catch (ParseException e1) {
//					e1.printStackTrace();
//					System.out.println("获取证书有效期失败:" + e1.getMessage());
//				}
//				//验证客户端证书
//				try {
//					int retValue = sed.validateCert(clientCert);
//					if (retValue == 1) {
//						session.setAttribute("ContainerName", ContainerName);
//						String uniqueIdStr = "";
//						String certSnStr = "qwewr232sd==";
//						try {
//							uniqueIdStr = sed.getCertInfo(clientCert, 17);
//						} catch (Exception e) {
//							System.out.println("客户端数字证书验证失败:" + e.getMessage());
//						}
//						//用户名
//						session.setAttribute("UniqueID", uniqueIdStr);
//						String uniqueId = "";
//						try {
//							//获得登陆用户ID
//							uniqueId = sed.getCertInfoByOid(clientCert,"2.16.840.1.113732.2");
//							if(uniqueId.contains("JJ")){
//								uniqueId = sed.getCertInfoByOid(clientCert,"1.2.86.11.7.1.8");
//							}
//							session.setAttribute("UniqueID", uniqueId);
//						} catch (Exception e) {
//							System.out.println("客户端数字证书验证失败:" + e.getMessage());
//						}
//
//						System.out.println("CA数字证书认证登录！");
//						System.out.println("用户名：");
//						System.out.println(uniqueIdStr);
//						System.out.println("证书颁发者(颁发者通用名): ");
//						System.out.println(certPub);
//						System.out.println("证书唯一标识(备用主题通用名)：");
//						System.out.println(uniqueId);
//						System.out.println("开始CA认证登录！\n");
//						User user = userService.findById((String) session.getAttribute("UniqueID"));
//		                session.setAttribute("user", user);
//		                if(user == null){
//		                	String error = "用户不存在！";
//							session.setAttribute("errorMsg", error);
//							return false;
//		                }
//					} else {
//						System.out.println("客户端证书验证失败！");
//						if (retValue == -1) {
//							System.out.println("登录证书的根不被信任！");
//						} else if (retValue == -2) {
//							System.out.println("登录证书超过有效期！");
//						} else if (retValue == -3) {
//							System.out.println("登录证书为作废证书！");
//						} else if (retValue == -4) {
//							System.out.println("登录证书被临时冻结！");
//						}
//					}
//				} catch (Exception ex) {
//					System.out.println("客户端证书验证失败:" + ex.getMessage());
//				}
//
//				//验证客户端签名
//				try {
//					if (sed.verifySignedData(clientCert, ranStr, UserSignedData)) {
//
//					} else {
//						System.out.println("验证客户端签名错误！");
//					}
//				} catch (Exception e) {
//					System.out.println("验证客户端签名错误:" + e.getMessage());
//				}
//			    return true;
//			  }
}
