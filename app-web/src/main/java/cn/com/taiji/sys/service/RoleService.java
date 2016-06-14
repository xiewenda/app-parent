package cn.com.taiji.sys.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.MenuRepository;
import cn.com.taiji.sys.domain.Role;
import cn.com.taiji.sys.domain.RoleMenu;
import cn.com.taiji.sys.domain.RoleMenuPK;
import cn.com.taiji.sys.domain.RoleMenuRepository;
import cn.com.taiji.sys.domain.RoleRepository;
import cn.com.taiji.sys.domain.RoleUser;
import cn.com.taiji.sys.domain.RoleUserPK;
import cn.com.taiji.sys.domain.RoleUserRepository;
import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.domain.UserRepository;
import cn.com.taiji.sys.dto.MenuDto;
import cn.com.taiji.sys.dto.RoleDto;
import cn.com.taiji.sys.dto.UserDto;
import cn.com.taiji.sys.exception.BusinessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 角色Service
 * @author SunJingyan
 * @date 2014-05-26
 *
 */
@Service
public class RoleService {
	
	private static final Logger log = LoggerFactory.getLogger(RoleService.class);
	
	@Inject
	private RoleRepository roleRepository;

	@Inject
	private RoleUserRepository roleUserRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private RoleMenuRepository roleMenuRepository;
	
	@Inject
	private MenuRepository menuRepository;
	
	@Inject
	private ObjectMapper objectMapper;

	/**
	 * 查询角色信息
	 * @param searchParameters
	 *        查询参数的map集合
	 * @return
	 *       查询的结果,map类型
	 *       total:总条数
	 *       roles:查询结果list集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Map getPage(final Map searchParameters,String salt) {
		Map map = new HashMap();
		int page = 0;
		int pageSize = 10;
		Page<Role> pageList;
//		Page<RoleDto> pageDtoList;
		//页码
		if(searchParameters!=null && searchParameters.size()>0 && searchParameters.get("page")!=null)
		{
			page = Integer.parseInt(searchParameters.get("page").toString()) - 1;
		}
		//每页显示的条数
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
		//排序字段不为空
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
			Sort s = new Sort(Direction.ASC,"roleIndex");
			pageable = new PageRequest(page, pageSize,s);
		}
		Map filter = (Map) searchParameters.get("filter");
		//查询条件不为空
		if (filter != null) 
		{
//			String logic = filter.get("logic").toString();
			final List<Map> filters = (List<Map>) filter.get("filters");
			Specification<Role> spec = new Specification<Role>() 
			{
				@Override
				public Predicate toPredicate(Root<Role> root,
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
							if ("roleIndex".equalsIgnoreCase(field)) 
							{
								pl.add(cb.equal(root.<String> get(field),value));
							} 
							else if ("roleName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} else if ("roleDesc".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
						}
						
					}
					//查询出未删除的
					pl.add(cb.equal(root.<Integer> get("flag"),1));
					return cb.and(pl.toArray(new Predicate[0]));
				}
			};
			pageList =  roleRepository.findAll(spec, pageable);

		} 
		//查询条件为空
		else 
		{
			Specification<Role> spec = new Specification<Role>() 
					{
						public Predicate toPredicate(Root<Role> root,
								CriteriaQuery<?> query, CriteriaBuilder cb) 
						{
							List<Predicate> list = new ArrayList<Predicate>();
							//查询出未删除的
							list.add(cb.equal(root.<Integer> get("flag"),1));
							return cb.and(list.toArray(new Predicate[0]));
						}
					};
			pageList = roleRepository.findAll(spec, pageable);

		}
		map.put("total", pageList.getTotalElements());
		List<RoleDto> dtos = new ArrayList<RoleDto>();
		List<Role> list = pageList.getContent();
		if(list!=null && list.size()>0)
		{
			for(Role d:list)
			{
				RoleDto dto = new RoleDto();
				BeanUtils.copyProperties(d, dto);
				dto.generateToken(salt);
				dtos.add(dto);
			}
		}
		map.put("roles", dtos);
		return map;
	}
	
	/**
	 * 添加角色信息
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void add(List<RoleDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(RoleDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					if(!findByName(dto.getRoleName(),dto.getRoleId()))
					{
						throw new BusinessException("角色名["+dto.getRoleName()+"]已存在！");
					}
					//新增
					Role role = new Role();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setRoleId(id);
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
					BeanUtils.copyProperties(dto, role);
					this.roleRepository.saveAndFlush(role);
				}
			}
		}
	}
	
	/**
	 * 编辑角色信息
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void edit(List<RoleDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(RoleDto dto: list)
			{
				if(!findByName(dto.getRoleName(),dto.getRoleId()))
				{
					throw new BusinessException("角色名["+dto.getRoleName()+"]已存在！");
				}
				Role role = this.roleRepository.findOne(dto.getRoleId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(userDetails.getUsername());
				} catch (ParseException e) {
				}
				BeanUtils.copyProperties(dto, role);
				this.roleRepository.saveAndFlush(role);
					
			}
		}
	}
	
	
	/**
	 * 添加和编辑角色信息-备份
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveRole(List<RoleDto> list)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(RoleDto d:list)
			{
				Role role;
				if(d!=null && d.getRoleId()!=null && !d.getRoleId().equals(""))
				{
					//更新
					role = this.roleRepository.findOne(d.getRoleId());
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
					//新增
					role = new Role();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					d.setRoleId(id);
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
						e.printStackTrace();
					}
				}
				BeanUtils.copyProperties(d, role);
				if(d.getRoleIndex()==null)
				{
					role.setRoleIndex(0);
				}
				this.roleRepository.saveAndFlush(role);
			}
		}
	}
	
	/**
	 * 多条删除
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteRole(List<RoleDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			for(RoleDto d:list)
			{
				if(d.getUsers()!=null && d.getUsers().size()>0)
				{
					throw new BusinessException("该角色与用户关联，不能删除！");
				}
				else if(d.getMenus()!=null && d.getMenus().size()>0)
				{
					throw new BusinessException("该角色与菜单关联，不能删除！");
				}
				else{
					String id = d.getRoleId();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String updateTimeStr = sdf.format(new Date());
					Date updateTime = null;
					try {
							updateTime = sdf.parse(updateTimeStr);
					} catch (ParseException e) {
					}
					this.roleRepository.updateFlag(id,updateTime,userDetails.getUsername());
				}
			}
		}
	}
	
	/**
	 * 查询所有的角色
	 * @return
	 */
//	public List<CodeDto> findAllRoles()
//	{
//		List<CodeDto> codes = new ArrayList<CodeDto>();
//		List<Role> roles = this.roleRepository.findAll();
//		if(roles!=null && roles.size()>0)
//		{
//			for(Role d:roles)
//			{
//				CodeDto dto = new CodeDto();
//				dto.setCode(d.getRoleId());
//				dto.setName(d.getRoleName());
//				codes.add(dto);
//			}
//		}
//		return codes;
//	}
	
	/**
	 * 根据ID查询角色
	 * @param id
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Role findById(String id)
	{
		return this.roleRepository.findOne(id);
	}
	
	/**
	 * 根据roleId查询出关系表中所有的userId拼装成字符串
	 * @param roleId
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public String findUserIdsByRoleId(String roleId)
	{
		List<String> list=this.roleUserRepository.findUserIdsByRoleId(roleId);
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
	 * 根据角色id查询出映射的用户集合
	 * @param roleId
	 *        用户选择的角色id
	 * @return
	 *        映射的用户集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<UserDto> findUsersByRoleId(String roleId)
	{
		List<UserDto> users = new ArrayList<UserDto>();
		List<User> list = new ArrayList<User>();
		List<String> userIds = this.roleUserRepository.findUserIdsByRoleId(roleId);
		if(userIds!=null && userIds.size()>0)
		{
			for(String s:userIds)
			{
				User user = this.userRepository.findOne(s);
				if(user!=null)
				{
					list.add(user);
				}
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
	 * 保存角色用户的分配
	 * @param userIds
	 *        分配的用户的id字符串，以逗号分隔
	 * @param roleId
	 *        用户选定的角色id
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveRoleUsers(String userIds,String preUserIds,String roleId)
	{
		if(roleId!=null && roleId.length()>0)
		{
			String[] aryUser;
			String[] aryUserPre;
			if(userIds!=null && userIds.length()>0 && preUserIds!=null && preUserIds.length()==0)
			{
				//新分配的ID数组
			    aryUser = userIds.split(",");
			    for(String userId:aryUser)
				{
					if(userId!=null && userId.length()>0)
					{
						RoleUserPK pk = new RoleUserPK();
						pk.setRoleId(roleId);
						pk.setUserId(userId);
						RoleUser roleUser = new RoleUser();
						roleUser.setId(pk);
						this.roleUserRepository.saveAndFlush(roleUser);
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
						RoleUserPK pk = new RoleUserPK();
						pk.setRoleId(roleId);
						pk.setUserId(userIdPre);
						RoleUser roleUser = new RoleUser();
						roleUser.setId(pk);
						this.roleUserRepository.delete(roleUser);
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
									RoleUserPK pk = new RoleUserPK();
									pk.setRoleId(roleId);
									pk.setUserId(userIdPre);
									RoleUser roleUser = new RoleUser();
									roleUser.setId(pk);
									this.roleUserRepository.delete(roleUser);
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
							RoleUserPK pk = new RoleUserPK();
							pk.setRoleId(roleId);
							pk.setUserId(userId);
							RoleUser roleUser = new RoleUser();
							roleUser.setId(pk);
							this.roleUserRepository.saveAndFlush(roleUser);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 根据roleId查询出关系表中所有的菜单Id
	 * @param roleId
	 *        用户选择的角色id
	 * @return
	 *        映射的菜单id的字符串，以逗号分隔
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public String findMenuIdsByRoleId(String roleId)
	{
		List<String> list=this.roleMenuRepository.findMenuIdsByRoleId(roleId);
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
	 * 根据角色id查询出映射的菜单集合
	 * @param roleId
	 *        用户选择的角色id
	 * @return
	 *        映射的菜单dto
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<MenuDto> findMenus(String roleId)
	{
		List<MenuDto> menus = new ArrayList<MenuDto>();
		List<Menu> list = new ArrayList<Menu>();
		List<String> menuIds = this.roleMenuRepository.findMenuIdsByRoleId(roleId);
		if(menuIds!=null && menuIds.size()>0)
		{
			for(String s:menuIds)
			{
				Menu menu = this.menuRepository.findOne(s);
				if(menu!=null)
				{
					list.add(menu);
				}
			}
		}
		if(list!=null && list.size()>0)
		{
			for(Menu u:list)
			{
				MenuDto menuDto = new MenuDto();
				BeanUtils.copyProperties(u, menuDto);
				menus.add(menuDto);
			}
		}
		return menus;
	}
	
	/**
	 * 保存角色菜单的分配
	 * @param menuIds
	 *        选择的菜单Id的字符串，以逗号分隔的
	 * @param roleId
	 *        选择的角色Id
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveRoleMenus(String menuIds,String preMenuIds,String roleId)
	{
		if(roleId!=null && roleId.length()>0)
		{
			String[] aryMenuPre;
			String[] aryMenu;
			if(menuIds!=null && menuIds.length()>0 && preMenuIds!=null && preMenuIds.length()==0)
			{
				    //插入关系表记录
				    aryMenu = menuIds.split(",");
					for(String menuId:aryMenu)
					{
						if(menuId!=null && menuId.length()>0)
						{
							RoleMenuPK pk = new RoleMenuPK();
							pk.setRoleId(roleId);
							pk.setMenuId(menuId);
							RoleMenu roleMenu = new RoleMenu();
							roleMenu.setId(pk);
							this.roleMenuRepository.saveAndFlush(roleMenu);
						}
					}
			}
			if(menuIds!=null && menuIds.length()==0 && preMenuIds!=null && preMenuIds.length()>0)
			{
				//删除关系
				aryMenuPre = preMenuIds.split(",");
				for(String menuId:aryMenuPre)
				{
					if(menuId!=null && menuId.length()>0)
					{
						RoleMenuPK pk = new RoleMenuPK();
						pk.setRoleId(roleId);
						pk.setMenuId(menuId);
						RoleMenu roleMenu = new RoleMenu();
						roleMenu.setId(pk);
						this.roleMenuRepository.delete(roleMenu);
					}
				}
				
			}
			if(menuIds!=null && menuIds.length()>0 && preMenuIds!=null && preMenuIds.length()>0)
			{
				//新分配的ID数组
			    aryMenu = menuIds.split(",");
				//原来已分配的ID数组
				aryMenuPre = preMenuIds.split(",");
				//需要插入的list
				List<String> menuIdsList = new ArrayList<String>();
				for(int i=0;i<aryMenu.length;i++)
				{
					menuIdsList.add(aryMenu[i]);
				}
				try {
					String s = objectMapper.writeValueAsString(menuIdsList);
					System.out.println("s:"+s);
				} catch (JsonProcessingException e) {
				}
				//旧的
				for(int j=0;j<aryMenuPre.length;j++)
				{
					String menuIdPre = aryMenuPre[j];
					System.out.println("原来的id:"+menuIdPre);
					//新的
					for(int i=0;i<aryMenu.length;i++)
					{
						String menuId = aryMenu[i];
						System.out.println("新的id:"+menuId);
						if(!menuIdPre.equals(menuId))
						{
							if(i!=(aryMenu.length-1))
							{
								continue;
							}
							else
							{
								//删除关系表旧记录
								if(menuIdPre!=null && menuIdPre.length()>0)
								{
									RoleMenuPK pk = new RoleMenuPK();
									pk.setRoleId(roleId);
									pk.setMenuId(menuIdPre);
									RoleMenu roleMenu = new RoleMenu();
									roleMenu.setId(pk);
									this.roleMenuRepository.delete(roleMenu);
									break;
								}
							}
						}
						else if(menuId.equals(menuIdPre))
						{
							menuIdsList.remove(menuId);
							break;
						}
					}
					
				}
				//插入关系表记录
				if(menuIdsList.size()>0)
				{
					for(String menuId:menuIdsList)
					{
						if(menuId!=null && menuId.length()>0)
						{
							RoleMenuPK pk = new RoleMenuPK();
							pk.setRoleId(roleId);
							pk.setMenuId(menuId);
							RoleMenu roleMenu = new RoleMenu();
							roleMenu.setId(pk);
							this.roleMenuRepository.saveAndFlush(roleMenu);
						}
					}
				}
			}
				
				
				
		}
	}

	/**
	 * 查询所有
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Role> findAllRoles() {
		return roleRepository.findAllRoles();
	}
	
	/**
	 * 查询出更新后的数据
	 * @param list
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<RoleDto> findRoles(List<RoleDto> list,String salt)
	{
		List<RoleDto> newList = new ArrayList<RoleDto>();
		if(list!=null && list.size()>0)
		{
			for(RoleDto d:list)
			{
				String id = d.getRoleId();
				Role role = this.roleRepository.findOne(id);
				if(role!=null)
				{
					RoleDto dto = new RoleDto();
					BeanUtils.copyProperties(role, dto);
					dto.generateToken(salt);
					newList.add(dto);
				}
			}
		}
		return newList;
	}
	/**
	 * 根据角色id查询出映射的菜单集合
	 * @param roleId
	 *        用户选择的角色id
	 * @return
	 *        映射的菜单dto
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Menu> findMenusByRoleIds(Set roleIds)
	{
		List<Menu> list = new ArrayList<Menu>();
		List<String> menuIds = this.roleMenuRepository.findMenuIdsByRoleIds(roleIds);
		if(menuIds!=null && menuIds.size()>0)
		{
			for(String s:menuIds)
			{
				Menu menu = this.menuRepository.findOne(s);
				if(menu!=null)
				{
					list.add(menu);
				}
			}
		}
		
		return list;
	}
	/**
	 * 根据角色名查询
	 * @param 
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public boolean findByName(String roleName,String id)
	{
		List<Role> roles = new ArrayList<Role>();
		if(roleName!=null && roleName.length()>0)
		{
			roles = this.roleRepository.findByRoleName(roleName, id);
			if(roles.size()>0)
			{
				return false;
			}
		}
		return true;
	}
	
}
