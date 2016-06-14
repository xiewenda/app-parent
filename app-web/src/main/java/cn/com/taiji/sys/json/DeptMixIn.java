package cn.com.taiji.sys.json;



import java.util.List;

import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DeptMixIn {
	
	@JsonIgnore
	Dept parent;
	
	@JsonIgnore
	List<Dept> children;
	
	@JsonIgnore
	List<User> users;
	
}
