package cn.com.taiji.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.Role;
import cn.com.taiji.sys.service.UserService;

/**
 * 该类的主要作用是为Spring Security提供一个经过用户认证后的UserDetails。 该UserDetails包括用户名、密码、是否可用、是否过期等信息。
 */
@Component
public class TaijiUserDetailServiceImpl implements UserDetailsService {
  @Autowired
  private UserService userService;

  // 登录验证
  public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
    /** 连接数据库根据登陆？？用户名称获得用户信息 */
    cn.com.taiji.sys.domain.User users = userService.findByLoginName(loginName);
    if (users == null) {
      throw new UsernameNotFoundException(loginName);
    } else {
      if (!"1".equals(users.getState())) {
        throw new UsernameNotFoundException("该用户处于锁定状态");
      }
    }
    Collection<GrantedAuthority> grantedAuths = obtionGrantedAuthorities(users);

    boolean enables = true; // 是否可用
    boolean accountNonExpired = true; // 是否过期
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;
    // 封装成spring security的user
    User userdetail = new User(users.getUserName(), users.getPassword(), enables, accountNonExpired,
        credentialsNonExpired, accountNonLocked, grantedAuths);
    System.out.println("验证从此地过了一遭");
    return userdetail;
  }

  public UserService getUserService() {
    return userService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  // 取得用户的权限
  private Set<GrantedAuthority> obtionGrantedAuthorities(cn.com.taiji.sys.domain.User users) {
    Set<GrantedAuthority> authSet = new HashSet<GrantedAuthority>();
    List<Menu> resources = new ArrayList<Menu>();
    // 获取用户角色
    Set<Role> roles = users.getRoles();

    // for(WafRole role : roles) {
    // Set<WafMenu> tempRes = role.getMenus();
    // for(WafMenu res : tempRes) {
    // resources.add(res);
    // }
    // }
    // for(WafMenu res : resources) {
    // authSet.add(new GrantedAuthorityImpl(res.getMenuname()+"----"));
    // }


    for (Role role : roles) {
      authSet.add(new SimpleGrantedAuthority(role.getRoleId()));
    }
    return authSet;
  }
}
