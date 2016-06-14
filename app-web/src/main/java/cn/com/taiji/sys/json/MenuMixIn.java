package cn.com.taiji.sys.json;



import java.util.List;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.Role;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MenuMixIn {
	
	@JsonIgnore
	Menu parent;
	
	@JsonIgnore
	List<Menu> children;
	
	@JsonIgnore
	List<Role> roles;
}
