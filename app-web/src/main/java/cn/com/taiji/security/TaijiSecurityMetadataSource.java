package cn.com.taiji.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.Role;
import cn.com.taiji.sys.service.MenuService;
import cn.com.taiji.sys.service.RoleService;

/**
 * @description 资源源数据定义，将所有的资源和权限对应关系建立起来，即定义某一资源可以被哪些角色访问
 *  此类在初始化时，应该取到所有资源及其对应角色的定义
 */
@Component
public class TaijiSecurityMetadataSource implements
FilterInvocationSecurityMetadataSource {
	@Autowired
	private RoleService roleService;
	@Autowired
	private MenuService menuService;
	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}


	/* 保存资源和权限的对应关系 key-资源url value-权限 */
	private Map<String, Collection<ConfigAttribute>> resourceMap = null;



	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public TaijiSecurityMetadataSource() {
		//		loadResourcesDefine();
	}

	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	// 加载所有资源与权限的关系
	private void loadResourceDefine() {
		System.out.print("加载所有资源");
		if (resourceMap == null) {
			resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
			List<Role> roles = this.roleService.findAllRoles();
		if(!roles.isEmpty()){	
			for (Role role : roles) {
				
				// 以权限名封装为Spring的security Object
				for(Menu menu:role.getMenus()){
					Collection<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
					ConfigAttribute configAttribute = new SecurityConfig(role.getRoleId());
					configAttributes.add(configAttribute);
					resourceMap.put("/"+menu.getMenuUrl(), configAttributes);
				
				}
			}
			
		}
		}

//		Set<Entry<String, Collection<ConfigAttribute>>> resourceSet = resourceMap.entrySet();
//		Iterator<Entry<String, Collection<ConfigAttribute>>> iterator = resourceSet.iterator();
	}


	// 返回所请求资源所需要的权限
//	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
//		if (resourceMap == null) {
//			loadResourceDefine();
//		}
//		String url = ((FilterInvocation) object).getRequestUrl();
//		System.out.println("requestUrl is " + url);
//		int firstQuestionMarkIndex = url.indexOf("?");
//		if (firstQuestionMarkIndex != -1) {
//			url = url.substring(0, firstQuestionMarkIndex);
//		}
//		HttpServletRequest request = ((FilterInvocation) object).getRequest();
//		Iterator<String> ite = resourceMap.keySet().iterator();
//		while (ite.hasNext()) {
//			String resURL = ite.next();
//			if(!" ".equals(resURL)&&resURL!=null){
//				return resourceMap.get(resURL);
//				//	            RequestMatcher urlMatcher = new AntPathRequestMatcher(resURL);
//				//	            //这段代码表示数据库内的需要验证的资源URL与当前请求的URL相匹配时进行验证
//				//	            if (urlMatcher.matches(request)) {
//				//	                return resourceMap.get(resURL);
//				//	            }
//			}
//		}
//		//	        Collection<ConfigAttribute> returnCollection = new ArrayList<ConfigAttribute>(); 
//		//	        returnCollection.add(new SecurityConfig("ROLE_NO_USER")); 
//
//		return null;
//	}
	//返回所请求资源所需要的权限   
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {  
          
        String requestUrl = ((FilterInvocation) object).getRequestUrl();  
        if(resourceMap == null) {  
            loadResourceDefine();  
        }  
        return resourceMap.get(requestUrl);  
    } 


	public boolean supports(Class<?> arg0) {
		//loadResourcesDefine();.
		// TODO Auto-generated method stub
		return true;
	}

}
