package cn.com.taiji.sys.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色用户表dao
 * @author SunJingyan
 * @date 2014年4月21日
 *
 */
public interface RoleUserRepository extends JpaRepository<RoleUser, RoleUserPK>{

	@Query("select k.id.userId from RoleUser k where k.id.roleId =:roleId")
	List<String> findUserIdsByRoleId(@Param("roleId") String roleId);
	
	@Query("select k.id.roleId from RoleUser k where k.id.userId =:userId")
	List<String> findRoleIdsByUserId(@Param("userId") String userId);
	
	@Query("select k from RoleUser k where k.id.userId =:userId")
	List<RoleUser> findByUserId(@Param("userId") String userId);
	
	@Query("select k from RoleUser k where k.id.roleId =:roleId")
	List<RoleUser> findByRoleId(@Param("roleId") String roleId);
	
	@Query("select k from RoleUser k where k.id=:id")
	RoleUser findByPk(@Param("id") RoleUserPK id);
	
	
}
