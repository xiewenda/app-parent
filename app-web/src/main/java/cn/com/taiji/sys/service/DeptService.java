package cn.com.taiji.sys.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.DeptRepository;
import cn.com.taiji.sys.domain.DeptUser;
import cn.com.taiji.sys.domain.DeptUserPK;
import cn.com.taiji.sys.domain.DeptUserRepository;
import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.domain.UserRepository;
import cn.com.taiji.sys.dto.DeptDto;
import cn.com.taiji.sys.dto.UserDto;
import cn.com.taiji.sys.exception.BusinessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 机构Service
 * @author SunJingyan
 * @date 2014-05-10
 *
 */
@Service
public class DeptService {
	
	@Inject
	private DeptRepository deptRepository;
	
	@Inject
	private DeptUserRepository deptUserRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private ObjectMapper objectMapper;
	
	/**
	 * 查询机构信息
	 * @param searchParameters
	 *        查询参数的map集合
	 * @return
	 *       查询的结果,map类型
	 *       total:总条数
	 *       depts:查询结果list集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Map getPage(final Map searchParameters,String salt)
	{
		Map map = new HashMap();
		int page = 0;
		int pageSize = 10;
		Page<Dept> pageList;
//		Page<DeptDto> pageDtoList;
		if(searchParameters!=null && searchParameters.size()>0 && searchParameters.get("page")!=null)
		{
			page = Integer.parseInt(searchParameters.get("page").toString()) - 1;
		}
		if(searchParameters!=null && searchParameters.size()>0 && searchParameters.get("pageSize")!=null)
		{
			pageSize = Integer.parseInt(searchParameters.get("pageSize").toString());
		}
		if (pageSize < 1)
			pageSize = 1;
		if (pageSize > 100)
			pageSize = 100;
		List<Map> orderMaps = (List<Map>) searchParameters.get("sort");
		List<Order> orders = new ArrayList<Order>();
		if (orderMaps != null) {
			for (Map m : orderMaps) {
				if (m.get("field") == null)
					continue;
				String field = m.get("field").toString();
				if (!StringUtils.isEmpty(field)) {
					String dir = m.get("dir").toString();
					if ("DESC".equalsIgnoreCase(dir)) {
						orders.add(new Order(Direction.DESC, field));
					} else {
						orders.add(new Order(Direction.ASC, field));
					}
				}
			}
		}
		PageRequest pageable;
		if (orders.size() > 0) 
		{
			pageable = new PageRequest(page, pageSize, new Sort(orders));
		} 
		else 
		{
			Sort sort = new Sort(Direction.ASC,"deptIndex");
			pageable = new PageRequest(page, pageSize,sort);
		}
		Map filter = (Map) searchParameters.get("filter");
		if (filter != null) 
		{
//			String logic = filter.get("logic").toString();
			final List<Map> filters = (List<Map>) filter.get("filters");
			Specification<Dept> spec = new Specification<Dept>() 
			{
				@Override
				public Predicate toPredicate(Root<Dept> root,
						CriteriaQuery<?> query, CriteriaBuilder cb) 
				{
					List<Predicate> pl = new ArrayList<Predicate>();
					for (Map f : filters) 
					{
//						String operator = ((String) f.get("operator")).trim();
						String field = f.get("field").toString().trim();
						String value = f.get("value").toString().trim();
						if(value!=null && value.length()>0)
						{
							if ("deptName".equalsIgnoreCase(field)) 
							{
									pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} 
							else if ("deptType".equalsIgnoreCase(field)) 
							{
									pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} else if ("deptUrl".equalsIgnoreCase(field)) 
							{
									pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
						}
						
					}
					//查询出未删除的
					pl.add(cb.equal(root.<Integer> get("flag"),1));
					//只查询当前人员的所在机构和当前机构的字机构
					if(searchParameters.get("deptId") != null){
						String deptId = searchParameters.get("deptId").toString();
						pl.add(cb.or(cb.equal(root.get("deptId"), deptId),cb.equal(root.get("parent").get("deptId"), deptId)));
					}
					return cb.and(pl.toArray(new Predicate[0]));
				}
			};
			pageList =  deptRepository.findAll(spec, pageable);

		} 
		else 
		{
			Specification<Dept> spec = new Specification<Dept>() 
					{
						public Predicate toPredicate(Root<Dept> root,
								CriteriaQuery<?> query, CriteriaBuilder cb) 
						{
							List<Predicate> list = new ArrayList<Predicate>();
							//查询出未删除的
							list.add(cb.equal(root.<Integer> get("flag"),1));
							//只查询当前人员的所在机构和当前机构的字机构
							if(searchParameters.get("deptId") != null){
								String deptId = searchParameters.get("deptId").toString();
								list.add(cb.or(cb.equal(root.get("deptId"), deptId),cb.equal(root.get("parent").get("deptId"), deptId)));
							}
							return cb.and(list.toArray(new Predicate[0]));
						}
					};
			pageList = deptRepository.findAll(spec, pageable);

		}
		map.put("total", pageList.getTotalElements());
		List<DeptDto> dtos = new ArrayList<DeptDto>();
		List<Dept> list = pageList.getContent();
		if(list!=null && list.size()>0)
		{
			for(Dept d:list)
			{
				DeptDto dto = new DeptDto();
				BeanUtils.copyProperties(d, dto);
				if(d.getParent()!=null && d.getParent().getDeptId()!=null)
				{
//					dto.setParent(d.getParent());
					dto.setParentId(d.getParent().getDeptId());
					dto.setParentName(d.getParent().getDeptName());
				}
				dto.generateToken(salt);
				dtos.add(dto);
			}
		}
		map.put("depts", dtos);
		return map;
	}
	
	
	/**
	 * 添加机构信息
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void add(List<DeptDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(DeptDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					//新增
					Dept dept = new Dept();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setDeptId(id);
					dto.setFlag(1);//初始化
					dto.setCreatorId(userDetails.getUsername());
					dto.setUpdaterId(userDetails.getUsername());
					String createTimeStr = sdf.format(new Date());
					String updateTimeStr = sdf.format(new Date());
					Date createTime;
					Date updateTime;
					try {
						createTime = sdf.parse(createTimeStr);
						updateTime = sdf.parse(updateTimeStr);
						dto.setCreateTime(createTime);
						dto.setUpdateTime(updateTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					BeanUtils.copyProperties(dto, dept);
					if(dto.getParentId()!=null)
					{
						String parentId = dto.getParentId();
						Dept parent = this.deptRepository.findOne(parentId);
						dept.setParent(parent);
					}
					this.deptRepository.saveAndFlush(dept);
				
				}
			}
		}
	}
	
	/**
	 * 编辑机构信息
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void edit(List<DeptDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(DeptDto dto: list)
			{
				Dept dept = this.deptRepository.findOne(dto.getDeptId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(userDetails.getUsername());
				} catch (ParseException e) {
				}
				BeanUtils.copyProperties(dto, dept);
				if(dto.getParentId()!=null)
				{
					String parentId = dto.getParentId();
					Dept parent = this.deptRepository.findOne(parentId);
					dept.setParent(parent);
				}
				this.deptRepository.saveAndFlush(dept);
					
			}
		}
	}
	
	
	/**
	 * 添加和编辑机构信息-备份
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveDept(List<DeptDto> list)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(DeptDto d:list)
			{
				Dept dept;
				if(d!=null && d.getDeptId()!=null && !d.getDeptId().equals(""))
				{
					//更新
					dept = this.deptRepository.findOne(d.getDeptId());
					String updateTimeStr = sdf.format(new Date());
					Date updateTime;
					try {
						updateTime = sdf.parse(updateTimeStr);
						d.setUpdateTime(updateTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				else
				{
					//添加
					dept = new Dept();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					d.setDeptId(id);
				    d.setFlag(1);//初始化
					String createTimeStr = sdf.format(new Date());
					String updateTimeStr = sdf.format(new Date());
					Date createTime;
					Date updateTime;
					try {
						createTime = sdf.parse(createTimeStr);
						updateTime = sdf.parse(updateTimeStr);
						d.setCreateTime(createTime);
						d.setUpdateTime(updateTime);
					} catch (ParseException e) {
					}
				}
				BeanUtils.copyProperties(d, dept);
				if(d.getParentId()!=null)
				{
					String parentId = d.getParentId();
					Dept parent = this.deptRepository.findOne(parentId);
					dept.setParent(parent);
				}
				if(d.getDeptIndex()==null)
				{
					dept.setDeptIndex(0);
				}
				this.deptRepository.saveAndFlush(dept);
			}
		}
	}
	
	/**
	 * 多条删除
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteDept(List<DeptDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			for(DeptDto d:list)
			{
				if(d.getChildren()!=null && d.getChildren().size()>0)
				{
					throw new BusinessException("该机构下面有子机构单位，不能删除！");
				}
				else if(d.getUsers()!=null && d.getUsers().size()>0)
				{
					throw new BusinessException("该机构与部分用户关联，不能删除！");
				}
				else {
					String id = d.getDeptId();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String updateTimeStr = sdf.format(new Date());
					Date updateTime = null;
					try {
							updateTime = sdf.parse(updateTimeStr);
					} catch (ParseException e) {
					}
					this.deptRepository.updateFlag(id,updateTime,userDetails.getUsername());
				}
			}
		}
	}
	
	/**
	 * 启用(state设为“1”)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void enableDept(String ids)
	{
		if(ids!=null && ids.length()>0)
		{
			String[] deptIds = ids.split(",");
			if(deptIds!=null && deptIds.length>0)
			{
				for(String id:deptIds)
				{
					Dept dept = this.deptRepository.findOne(id);
					dept.setDeptState("1");
					this.deptRepository.saveAndFlush(dept);
				}
			}
		}
	}
	/**
	 * 禁用(state设为“0”)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void disableDept(String ids)
	{
		if(ids!=null && ids.length()>0)
		{
			String[] deptIds = ids.split(",");
			if(deptIds!=null && deptIds.length>0)
			{
				for(String id:deptIds)
				{
					Dept dept = this.deptRepository.findOne(id);
					dept.setDeptState("0");
					this.deptRepository.saveAndFlush(dept);
				}
			}
		}
	}
	
	/**
	 * 查询所有的机构
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<DeptDto> findAllDepts()
	{
		List<DeptDto> dtos = new ArrayList<DeptDto>();
//		List<Dept> depts = this.deptRepository.findAll();
		List<Dept> depts = this.deptRepository.findAllDepts();
		if(depts!=null && depts.size()>0)
		{
			for(Dept d:depts)
			{
				DeptDto dto = new DeptDto();
				BeanUtils.copyProperties(d, dto);
				if(d.getParent()!=null && d.getParent().getDeptId()!=null)
				{
//					dto.setParent(d.getParent());
					dto.setParentId(d.getParent().getDeptId());
					dto.setParentName(d.getParent().getDeptName());
				}
				dtos.add(dto);
			}
		}
		DeptDto nullDept = new DeptDto();
		nullDept.setDeptId(null);
		nullDept.setDeptName("无");
		dtos.add(nullDept);
		return dtos;
	}
//	public List<CodeDto> findAllDepts()
//	{
//		List<CodeDto> codes = new ArrayList<CodeDto>();
//		List<Dept> depts = this.deptRepository.findAll();
//		if(depts!=null && depts.size()>0)
//		{
//			for(Dept d:depts)
//			{
//				CodeDto dto = new CodeDto();
//				dto.setCode(d.getDeptId());
//				dto.setName(d.getDeptName());
//				codes.add(dto);
//			}
//		}
//		return codes;
//	}
	
	/**
	 * 查询出机构树的根
	 * @return
	 */
//	@Transactional(propagation=Propagation.SUPPORTS)
//	public Dept findTree()
//	{
//		List<Dept> list = this.deptRepository.findRoots();
//		if(list!=null && list.size()>0)
//		{
//			if(list.size()>1)
//			{
//				return list.get(0);
//			}
//			return list.get(0);
//		}
//		return null;
//	}
	
	/**
	 * 根据ID查询机构
	 * @param id
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Dept findById(String id)
	{
		return this.deptRepository.findOne(id);
	}
	
	/**
	 * 根据deptId查询出关系表中所有的userId拼装成字符串
	 * @param deptId
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public String findUserIds(String deptId)
	{
		List<String> list=this.deptUserRepository.findUserIdsByDeptId(deptId);
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0)
		{
			for(String id:list)
			{
				sb.append(",").append(id);
			}
		}
		// 去掉首字符“,”
		if (sb.length() > 0 && sb.indexOf(",") == 0)
		{
			sb.replace(0, 1, "");
		}
		return sb.toString();
	}
	
	/**
	 * 根据机构id查询出用户集合---不用
	 * @param deptId
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<UserDto> findUsers(String deptId)
	{
		List<UserDto> users = new ArrayList<UserDto>();
		List<User> list = new ArrayList<User>();
		List<String> userIds = this.deptUserRepository.findUserIdsByDeptId(deptId);
		if(userIds!=null && userIds.size()>0)
		{
			for(String s:userIds)
			{
				User user = this.userRepository.findOne(s);
				list.add(user);
			}
		}
		if(list!=null && list.size()>0)
		{
			for(User u:list)
			{
				UserDto userDto = new UserDto();
				BeanUtils.copyProperties(u, userDto);
				users.add(userDto);
			}
		}
		return users;
	}
	
	/**
	 * 根据机构id查询出用户集合
	 * @param deptId
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<UserDto> findUsersById(String deptId)
	{
		List<UserDto> users = new ArrayList<UserDto>();
		List<User> list = new ArrayList<User>();
		if(deptId!=null && deptId.length()>0)
		{
			Dept dept = this.deptRepository.findOne(deptId);
			if(dept!=null && dept.getUsers()!=null && dept.getUsers().size()>0)
			{
				list = dept.getUsers();
				for(User u:list)
				{
					UserDto userDto = new UserDto();
					BeanUtils.copyProperties(u, userDto);
					users.add(userDto);
				}
			}
		}
		return users;
	}
	
	
	/**
	 * 根据机构id和角色Id查询出用户集合
	 * @param deptId
	 * @param roleId
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<String> findUsersByDeptRole(String deptId,String roleId)
	{
//		List<UserDto> users = new ArrayList<UserDto>();
		List<String> list = new ArrayList<String>();
		list = this.deptRepository.findUsersByDeptRole(deptId, roleId);
//		if(list.size()>0)
//		{
//			for(User u:list)
//			{
//				UserDto userDto = new UserDto();
//				BeanUtils.copyProperties(u, userDto);
//				users.add(userDto);
//			}
//		}
		return list;
	}
	
	/**
	 * 保存机构用户的分配
	 * @param userIds
	 * @param deptId
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deptUserSave(String userIds,String preUserIds,String deptId)
	{
		if(deptId!=null && deptId.length()>0)
		{
			String[] aryUser;
			String[] aryUserPre;
			if(userIds!=null && userIds.length()>0 && preUserIds!=null && preUserIds.length()==0)
			{
				//插入
				aryUser = userIds.split(",");
				for(String userId:aryUser)
				{
					if(userId!=null && userId.length()>0)
					{
						DeptUserPK pk = new DeptUserPK();
						pk.setDeptId(deptId);
						pk.setUserId(userId);
						DeptUser deptUser = new DeptUser();
						deptUser.setId(pk);
						this.deptUserRepository.saveAndFlush(deptUser);
					}
				}
			}
			if(userIds!=null && userIds.length()==0 && preUserIds!=null && preUserIds.length()>0)
			{
				aryUserPre = preUserIds.split(",");
				//删除
				for(String userIdPre:aryUserPre)
				{
					if(userIdPre!=null && userIdPre.length()>0)
					{
						DeptUserPK pk = new DeptUserPK();
						pk.setDeptId(deptId);
						pk.setUserId(userIdPre);
						DeptUser deptUser = new DeptUser();
						deptUser.setId(pk);
						this.deptUserRepository.delete(deptUser);
					}
				}
			}
			if(userIds!=null && userIds.length()>0 && preUserIds!=null && preUserIds.length()>0)
			{
				//新分配的ID数组
				aryUser = userIds.split(",");
				//原来已分配的ID数组
				aryUserPre = preUserIds.split(",");
				//需要插入的list
				List<String> userIdsList = new ArrayList<String>();
				for(int i=0;i<aryUser.length;i++)
				{
					userIdsList.add(aryUser[i]);
				}
				//旧的
				for(int j=0;j<aryUserPre.length;j++)
				{
					String userIdPre = aryUserPre[j];
					System.out.println("原来的id:"+userIdPre);
					//新的
					for(int i=0;i<aryUser.length;i++)
					{
						String userId = aryUser[i];
						System.out.println("新的id:"+userId);
						if(!userIdPre.equals(userId))
						{
							if(i!=(aryUser.length-1))
							{
								continue;
							}
							else
							{
								//删除关系表旧记录
								if(userIdPre!=null && userIdPre.length()>0)
								{
									DeptUserPK pk = new DeptUserPK();
									pk.setDeptId(deptId);
									pk.setUserId(userIdPre);
									DeptUser deptUser = new DeptUser();
									deptUser.setId(pk);
									this.deptUserRepository.delete(deptUser);
									break;
								}
							}
						}
						else if(userId.equals(userIdPre))
						{
							userIdsList.remove(userId);
							break;
						}
					}
					
				}
				//插入关系表记录
				if(userIdsList.size()>0)
				{
					for(String userId:userIdsList)
					{
						if(userId!=null && userId.length()>0)
						{
							DeptUserPK pk = new DeptUserPK();
							pk.setDeptId(deptId);
							pk.setUserId(userId);
							DeptUser deptUser = new DeptUser();
							deptUser.setId(pk);
							this.deptUserRepository.saveAndFlush(deptUser);
						}
					}
				}
			}
//			if(deptId!=null && deptId.length()>0 && aryUser!=null && aryUser.length>0)
//			{
//			
//				//先删除关系表中的记录
//				List<DeptUser> list = this.deptUserRepository.findByDeptId(deptId);
//				if(list!=null && list.size()>0)
//				{
//					for(DeptUser key:list)
//					{
//						this.deptUserRepository.delete(key);
//					}
//				}
//				//再分配
//				for(String userId:aryUser)
//				{
//					DeptUserPK pk = new DeptUserPK();
//					pk.setDeptId(deptId);
//					pk.setUserId(userId);
//					DeptUser userDeptKey = this.deptUserRepository.findByPk(pk);
//					//先查询关系表中关联的记录，如果没有则保存
//					if(userDeptKey==null)
//					{
//						userDeptKey = new DeptUser();
//						userDeptKey.setId(pk);
//						this.deptUserRepository.saveAndFlush(userDeptKey);
//					}
//				}
//			}
		}
		
	}
	
	/**
	 * 查询树根
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Dept> findByParentId()
	{
		return this.deptRepository.findRoots();
	}
	/**
	 * 查询树根
	 * @return
	 */
	public List<Dept> findByParentId(String parentId)
	{
		return this.deptRepository.findRoots(parentId);
	}
	/**
	 * 查询出更新后的数据
	 * @param list
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<DeptDto> findDepts(List<DeptDto> list,String salt)
	{
		List<DeptDto> newList = new ArrayList<DeptDto>();
		if(list!=null && list.size()>0)
		{
			for(DeptDto d:list)
			{
				String id = d.getDeptId();
				Dept dept = this.deptRepository.findOne(id);
				if(dept!=null)
				{
					DeptDto dto = new DeptDto();
					BeanUtils.copyProperties(dept, dto);
					if(dept.getParent()!=null && dept.getParent().getDeptId()!=null)
					{
//						dto.setParent(dept.getParent());
						dto.setParentId(dept.getParent().getDeptId());
						dto.setParentName(dept.getParent().getDeptName());
					}
					dto.generateToken(salt);
					newList.add(dto);
				}
			}
		}
		return newList;
	}
	/**
	 * 通过用户ID查询用户所在部门的信息
	 */
	public DeptDto findDeptByUserId(String userId){
		DeptDto dto = new DeptDto();
		List<DeptUser> deptUserList = deptUserRepository.findByUserId(userId);
		if(deptUserList.size()>0){
			Dept dept = findById(deptUserList.get(0).getId().getDeptId());
			if(dept != null){
				BeanUtils.copyProperties(dept, dto);
			}
		}
		return dto;
	}
}