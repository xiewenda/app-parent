package cn.com.taiji.sys.domain;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 机构单位dao
 * @author SunJingyan
 * @date 2014-04-21
 *
 */
public interface DeptRepository extends JpaRepository<Dept, String>,JpaSpecificationExecutor<Dept>,PagingAndSortingRepository<Dept,String>{
	
	/**
	 * 查询机构树
	 * @return
	 */
	@Query("select d from Dept d left join fetch d.children")
	List<Dept> findDeptTree();

	/**
	 * 查询树根
	 * @return
	 */
	@Query("select d from Dept d where d.parent.deptId = :parentID and d.flag=1 order by d.deptIndex")
	List<Dept> findRoots(@Param("parentID") String parentId);
	/**
	 * 查询树根
	 * @return
	 */
	@Query("select d from Dept d where d.parent is null and d.flag=1 order by d.deptIndex")
	List<Dept> findRoots();
	/**
	 * 标记为删除
	 * @param id
	 */
	@Modifying
	@Query("update Dept d set d.flag=0,d.updateTime=:updateTime,d.updaterId=:updaterId where d.deptId=:id")
	void updateFlag(@Param("id") String id,@Param("updateTime") Date updateTime,@Param("updaterId") String updaterId);
	
	/**
	 * 查询所有（未标记为删除的）
	 * @return
	 */
	@Query("select d from Dept d where d.flag=1")
	List<Dept> findAllDepts();
	
	/**
	 * 根据机构ID和角色ID查询用户集合
	 * @param deptId
	 * @param roleId
	 * @return
	 */
	@Query("select u.userId from Dept d JOIN d.users u JOIN u.roles r  where d.deptId = :deptId and r.roleId = :roleId")
	List<String> findUsersByDeptRole(@Param("deptId") String deptId,@Param("roleId") String roleId);

}
