package cn.com.taiji.sys.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 机构用户关系表dao
 * @author SunJingyan
 * @date 2014-04-21
 *
 */
public interface DeptUserRepository extends JpaRepository<DeptUser, DeptUserPK>{

	/**
	 * 根据机构用户关系表ID查询
	 * @param id
	 * @return
	 */
	@Query("select k from DeptUser k where k.id=:id")
	DeptUser findByPk(@Param("id") DeptUserPK id);
	
	/**
	 * 根据机构ID查询用户ID字符串集合
	 * @param deptId
	 * @return
	 */
	@Query("select k.id.userId from DeptUser k where k.id.deptId =:deptId")
	List<String> findUserIdsByDeptId(@Param("deptId") String deptId);
	
	/**
	 * 根据用户ID查询机构ID字符串集合
	 * @param userId
	 * @return
	 */
	@Query("select k.id.deptId from DeptUser k where k.id.userId =:userId")
	List<String> findDeptIdsByUserId(@Param("userId") String userId);
	
	/**
	 * *根据用户ID查询
	 * @param userId
	 * @return
	 */
	@Query("select k from DeptUser k where k.id.userId =:userId")
	List<DeptUser> findByUserId(@Param("userId") String userId);
	
	/**
	 * 根据机构ID查询
	 * @param deptId
	 * @return
	 */
	@Query("select k from DeptUser k where k.id.deptId =:deptId")
	List<DeptUser> findByDeptId(@Param("deptId") String deptId);
	
	//@Modifying/*
	//@Query("delete DeptUser k where k.id.deptId =?1")
	//void deleteByDeptId(@Param("deptId") String deptId)*/;
	
	
}
