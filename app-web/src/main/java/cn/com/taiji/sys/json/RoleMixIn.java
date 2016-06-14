package cn.com.taiji.sys.json;



import java.util.List;
import java.util.Set;

import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RoleMixIn {
	
	@JsonIgnore
	List<Menu> menus;
	
	@JsonIgnore
	Set<User> users;
	
}
