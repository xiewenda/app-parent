package cn.com.taiji.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.service.UserService;

/**
 * 带验证码校验功能的用户名、密码认证过滤器
 * 
 * 支持不输入验证码；支持验证码忽略大小写。
 * 
 * 
 */
public class TaijiUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter  {
	
	public static final String VALIDATE_CODE = "j_captcha";
	public static final String USERNAME = "j_username";
	public static final String PASSWORD = "j_password";
	
	private boolean postOnly = true;
	private boolean allowEmptyValidateCode = false;
	
//	@Autowired
//	private WafUserService userService;
	
	@Autowired
	private UserService userService;
	
	
//	public WafUserService getUserService() {
//		return userService;
//	}
//
//	public void setUserService(WafUserService userService) {
//		this.userService = userService;
//	}
	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: "+ request.getMethod());
		}
		String loginname = obtainUsername(request);
		String password = obtainPassword(request);
		password = password.length() > 30 ? password : DigestUtils.sha256Hex(password);
		// 验证用户账号与密码是否对应
		loginname = loginname.trim();
		User user = userService.login(loginname,password);
		   HttpSession session = request.getSession(true);  
		   session.setAttribute("user", user);
		if (user == null) {
			throw new AuthenticationServiceException("登录名称或密码不正确，请重新输入");
		}
		//	checkValidateCode(request);
		// UsernamePasswordAuthenticationToken实现 Authentication
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginname, password);
		// Place the last username attempted into HttpSession for views

		// 允许子类设置详细属性
		setDetails(request, authRequest);
		// 运行UserDetailsService的loadUserByUsername 再次封装Authentication
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 
	 * <li>比较session中的验证码和用户输入的验证码是否相等</li>
	 * 
	 */
	protected void checkValidateCode(HttpServletRequest request) {
		String sessionValidateCode = obtainSessionValidateCode(request);
		String validateCodeParameter = obtainValidateCodeParameter(request);
		if (StringUtils.isEmpty(validateCodeParameter)
				|| !sessionValidateCode.equalsIgnoreCase(validateCodeParameter)) {
			throw new AuthenticationServiceException("验证码错误，请重新输入！");
		}
	}

	private String obtainValidateCodeParameter(HttpServletRequest request) {
		return request.getParameter(VALIDATE_CODE);
	}

	protected String obtainSessionValidateCode(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(VALIDATE_CODE);
		return null == obj ? "" : obj.toString();
	}

	public boolean isPostOnly() {
		return postOnly;
	}

	@Override
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public static String getValidateCode() {
		return VALIDATE_CODE;
	}

	public static String getUsername() {
		return USERNAME;
	}

	public static String getPassword() {
		return PASSWORD;
	}

	public boolean isAllowEmptyValidateCode() {
		return allowEmptyValidateCode;
	}

	public void setAllowEmptyValidateCode(boolean allowEmptyValidateCode) {
		this.allowEmptyValidateCode = allowEmptyValidateCode;
	}

}
