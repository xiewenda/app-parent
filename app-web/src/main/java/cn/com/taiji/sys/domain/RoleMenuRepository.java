package cn.com.taiji.sys.domain;


import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * 菜单角色表dao
 * @author SunJingyan
 * @date 2014年4月21日
 *
 */
public interface  RoleMenuRepository extends JpaRepository<RoleMenu, RoleMenuKeyPK>,
JpaSpecificationExecutor<RoleMenu>{
	@Query("select distinct(k.id.menuId) from RoleMenu k where k.id.roleId  in :roleIds")
	List<String> findMenuIdsByRoleIds(@Param("roleIds") Set roleIds);
	
	@Query("select k.id.menuId from RoleMenu k where k.id.roleId =:roleId")
	List<String> findMenuIdsByRoleId(@Param("roleId") String roleId);
	
	@Query("select k.id.roleId from RoleMenu k where k.id.menuId =:menuId")
	List<String> findRoleIdsByMenuId(@Param("menuId") String menuId);
	
	@Query("select k from RoleMenu k where k.id.menuId =:menuId")
	List<RoleMenu> findByMenuId(@Param("menuId") String menuId);
	
	@Query("select k from RoleMenu k where k.id.roleId =:roleId")
	List<RoleMenu> findByRoleId(@Param("roleId") String roleId);
	
	@Query("select k from RoleMenu k where k.id=:id")
	RoleMenu findByPk(@Param("id") RoleMenuPK id);
	
}
