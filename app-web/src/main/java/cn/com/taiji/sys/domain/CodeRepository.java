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
 * 数据字典dao
 * @author SunJingyan
 * @date 2014年8月27日
 *
 */
public interface CodeRepository  extends JpaRepository<Code, String>,JpaSpecificationExecutor<Code>,PagingAndSortingRepository<Code,String>{

	/**
	 * 根据代码类型查询出业务代码
	 * @param codeType
	 * @return
	 */
	@Query("select c.codeCode from Code  c where c.codeType=:codeType") 
	List<String> findCodeByType(@Param("codeType") String codeType);
	
	/**
	 * 标记为删除
	 * @param id
	 */
	@Modifying
	@Query("update Code c set c.state=0,c.updateTime=:updateTime,c.updaterId=:updaterId where c.id=:id ")
	void updateState(@Param("id") String id,@Param("updateTime") Date updateTime,@Param("updaterId") String updaterId);
	
	/**
	 * 删除树节点
	 * @param id
	 */
	@Modifying
	@Query("update Code c set c.state=0,c.updateTime=:updateTime,c.updaterId=:updaterId where c.id= :id")
	void deleteTreeNode(@Param("id") String id,@Param("updateTime") Date updateTime,@Param("updaterId") String updaterId);
	
	/**
	 * 查询出未删除的所有数据字典记录集合
	 * @return
	 */
	@Query("select c from Code c where c.state=1") 
	List<Code> findAllCodes();
	
	/**
	 * 根据codeCode查询出记录
	 * @param codeCode
	 * @return
	 */
	@Query("select c from Code c where c.codeCode = :codeCode and c.id!= :id and c.state=1")
	Code findByCodeCode(@Param("codeCode") String codeCode,@Param("id") String id);
	/**
	 * 查询出子节点集合
	 * @param code
	 * @return
	 */
	@Query("select c from Code c where c.code = :parent and c.deptId=:deptId and c.state=1")
	List<Code> findAllChildCodesByDeptId(@Param("parent") Code code,@Param("deptId") String deptId);
	/**
	 * 查询统一机构下创建的人
	 * @param code
	 * @return
	 */
	@Query("select c from Code c where c.code = :parent and c.state=1")
	List<Code> findAllChildCodes(@Param("parent") Code code);
	/**
	 * 通过code类型和index 查询出对应的Code
	 * @param codeType
	 * @param codeIndex
	 * @return
	 */
	@Query("select c from Code c where c.codeType = :codeType and c.state=1 and c.codeIndex = :codeIndex")
	Code findOneCodeByCodeTypeAndCodeIndex(@Param("codeType") String codeType,@Param("codeIndex") Integer codeIndex);
	
	@Query("select c from Code c where c.code = null and c.category = 1 and c.state=1")
	List<Code> findRoots();
	
	@Query("select c from Code c where c.code = null and c.id=:dataId and c.category = 1 and c.state=1")
	List<Code> findRoots(@Param("dataId") String dataId);
	/**
	 * 通过code类型 查询出对应的Code
	 * @param codeType
	 * @return
	 */
	@Query("select c from Code c where c.codeType = :codeType and c.state=1 order by c.codeIndex")
	List<Code> findCodeByCodeType(@Param("codeType") String codeType);
	/**
	 * 属性录入页面 添加数据字典 查询出所有单级和多级父级的数据字典
	 * @param deptId
	 * @return
	 */
	@Query("select c  from Code c where c.code is null and c.deptId = :deptId")
	List<Code> findCodeBydeptId(@Param("deptId") String deptId);
}
