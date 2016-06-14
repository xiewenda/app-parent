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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.taiji.sys.domain.Code;
import cn.com.taiji.sys.domain.CodeRepository;
import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.dto.CodeDto;
import cn.com.taiji.sys.exception.BusinessException;

/**
 * 数据字典Service
 * @author  SunJingyan
 * @date  2014年6月11日
 */
@Service
public class CodeService 
{

	@Inject
	private CodeRepository codeRepository;
	
	@Inject
	private ObjectMapper objectMapper;
	@Autowired  
	private HttpSession session;
	
	/**
	 * 查询单级数据字典信息
	 * @param searchParameters
	 *        查询参数的map集合
	 * @return
	 *       查询的结果,map类型
	 *       total:总条数
	 *       codes:查询结果list集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Map getPageSingle(final Map searchParameters,String salt) {
		Map map = new HashMap();
		int page = 0;
		int pageSize = 10;
		Page<Code> pageList;
//		Page<CodeDto> pageDtoList;
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
			Sort sort = new Sort(Direction.ASC,"codeIndex");
			pageable = new PageRequest(page, pageSize,sort);
		}
		Map filter = (Map) searchParameters.get("filter");
		if (filter != null) 
		{
//			String logic = filter.get("logic").toString();
			final List<Map> filters = (List<Map>) filter.get("filters");
			Specification<Code> spec = new Specification<Code>() 
			{
				@Override
				public Predicate toPredicate(Root<Code> root,
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
							if ("codeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} 
							else if ("codeType".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeTypeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeCode".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
						}
					}
					//查询出未删除的
					pl.add(cb.equal(root.<Integer> get("state"),1));
					//查询出单级的
					pl.add(cb.equal(root.<Integer> get("category"),0));
					//查询出本部门的
					if(searchParameters.get("deptId") != null){
						String deptId = searchParameters.get("deptId").toString();
						pl.add(cb.equal(root.get("deptId"), deptId));
					}
					return cb.and(pl.toArray(new Predicate[0]));
				}
			};
			pageList =  codeRepository.findAll(spec, pageable);

		} 
		else 
		{
			Specification<Code> spec = new Specification<Code>() 
					{
						@Override
						public Predicate toPredicate(Root<Code> root,
								CriteriaQuery<?> query, CriteriaBuilder cb) 
						{
							List<Predicate> pl = new ArrayList<Predicate>();
							//查询出未删除的
							pl.add(cb.equal(root.<Integer> get("state"),1));
							//查询出单级的
							pl.add(cb.equal(root.<Integer> get("category"),0));
							//查询出本部门的
							if(searchParameters.get("deptId") != null){
								String deptId = searchParameters.get("deptId").toString();
								pl.add(cb.equal(root.get("deptId"), deptId));
							}
							return cb.and(pl.toArray(new Predicate[0]));
						}
					};
			pageList = codeRepository.findAll(spec,pageable);

		}
		map.put("total", pageList.getTotalElements());
		List<CodeDto> dtos = new ArrayList<CodeDto>();
		List<Code> list = pageList.getContent();
		if(list!=null && list.size()>0)
		{
			for(Code d:list)
			{
				CodeDto dto = new CodeDto();
				BeanUtils.copyProperties(d, dto);
				dto.generateToken(salt);
				dtos.add(dto);
			}
		}
		map.put("codes", dtos);
		return map;
	}
	
	
	/**
	 * 查询多级数据字典信息
	 * @param searchParameters
	 *        查询参数的map集合
	 * @return
	 *       查询的结果,map类型
	 *       total:总条数
	 *       codes:查询结果list集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Map getPageMulti(final Map searchParameters,String salt) {
		Map map = new HashMap();
		int page = 0;
		int pageSize = 10;
		Page<Code> pageList;
//		Page<CodeDto> pageDtoList;
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
			Sort sort = new Sort(Direction.ASC,"codeIndex");
			pageable = new PageRequest(page, pageSize,sort);
		}
		Map filter = (Map) searchParameters.get("filter");
		if (filter != null) 
		{
//			String logic = filter.get("logic").toString();
			final List<Map> filters = (List<Map>) filter.get("filters");
			Specification<Code> spec = new Specification<Code>() 
			{
				@Override
				public Predicate toPredicate(Root<Code> root,
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
							if ("codeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} 
							else if ("codeType".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeTypeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeCode".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
						}
					}
					//查询出未删除的
					pl.add(cb.equal(root.<Integer> get("state"),1));
					//查询出多级的
					pl.add(cb.equal(root.<Integer> get("category"),1));
					//查询出父节点
					pl.add(cb.isNull(root.<Code>get("code")));
					//查询出本部门的
					//只查询当前人员的所在机构和当前机构的字机构
					if(searchParameters.get("deptId") != null){
						String deptId = searchParameters.get("deptId").toString();
						pl.add(cb.equal(root.get("deptId"), deptId));
					}
//					Join<Code, Code> join = root.join("code");
//					pl.add(cb.equal(join.get("code").get("id"),null));
					return cb.and(pl.toArray(new Predicate[0]));
				}
			};
			pageList =  codeRepository.findAll(spec, pageable);

		} 
		else 
		{
			Specification<Code> spec = new Specification<Code>() 
					{
						@Override
						public Predicate toPredicate(Root<Code> root,
								CriteriaQuery<?> query, CriteriaBuilder cb) 
						{
							List<Predicate> pl = new ArrayList<Predicate>();
							//查询出未删除的
							pl.add(cb.equal(root.<Integer> get("state"),1));
							//查询出多级的
							pl.add(cb.equal(root.<Integer> get("category"),1));
							//查询出父节点
							pl.add(cb.isNull(root.<Code>get("code")));
							//查询出本部门的
							if(searchParameters.get("deptId") != null){
								String deptId = searchParameters.get("deptId").toString();
								pl.add(cb.equal(root.get("deptId"), deptId));
							}
//							Join<Code, Code> join = root.join("codes");
//							pl.add(cb.equal(join.get("code").get("id"),null));
							return cb.and(pl.toArray(new Predicate[0]));
						}
					};
			pageList = codeRepository.findAll(spec,pageable);

		}
		map.put("total", pageList.getTotalElements());
		List<CodeDto> dtos = new ArrayList<CodeDto>();
		List<Code> list = pageList.getContent();
		if(list!=null && list.size()>0)
		{
			for(Code d:list)
			{
				CodeDto dto = new CodeDto();
				BeanUtils.copyProperties(d, dto);
				dto.generateToken(salt);
				if(d.getCode()!=null && StringUtils.isNotEmpty(d.getCode().getId()))
				{
//					Code parent = this.codeRepository.findOne(d.getCode().getId());
//					if(parent!=null)
//					{
//						String parentName = parent.getCodeName();
//						dto.setParentCodeName(parentName);
//					}
					dto.setParentId(d.getCode().getId());
				}
				dtos.add(dto);
			}
		}
		map.put("codes", dtos);
		return map;
	}
	
	/**
	 * 查询数据字典信息-备份
	 * @param searchParameters
	 *        查询参数的map集合
	 * @return
	 *       查询的结果,map类型
	 *       total:总条数
	 *       codes:查询结果list集合
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Map getPage(final Map searchParameters,String salt) {
		Map map = new HashMap();
		int page = 0;
		int pageSize = 10;
		Page<Code> pageList;
//		Page<CodeDto> pageDtoList;
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
			Sort sort = new Sort(Direction.ASC,"codeIndex");
			pageable = new PageRequest(page, pageSize,sort);
		}
		Map filter = (Map) searchParameters.get("filter");
		if (filter != null) 
		{
//			String logic = filter.get("logic").toString();
			final List<Map> filters = (List<Map>) filter.get("filters");
			Specification<Code> spec = new Specification<Code>() 
			{
				@Override
				public Predicate toPredicate(Root<Code> root,
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
							if ("codeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							} 
							else if ("codeType".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeTypeName".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
							else if ("codeCode".equalsIgnoreCase(field)) 
							{
								pl.add(cb.like(root.<String> get(field),"%"+ value + "%"));
							}
						}
					}
					//查询出未删除的
					pl.add(cb.equal(root.<Integer> get("state"),1));
					return cb.and(pl.toArray(new Predicate[0]));
				}
			};
			pageList =  codeRepository.findAll(spec, pageable);

		} 
		else 
		{
			Specification<Code> spec = new Specification<Code>() 
					{
						@Override
						public Predicate toPredicate(Root<Code> root,
								CriteriaQuery<?> query, CriteriaBuilder cb) 
						{
							List<Predicate> pl = new ArrayList<Predicate>();
							//查询出未删除的
							pl.add(cb.equal(root.<Integer> get("state"),1));
							return cb.and(pl.toArray(new Predicate[0]));
						}
					};
			pageList = codeRepository.findAll(spec,pageable);

		}
		map.put("total", pageList.getTotalElements());
		List<CodeDto> dtos = new ArrayList<CodeDto>();
		List<Code> list = pageList.getContent();
		if(list!=null && list.size()>0)
		{
			for(Code d:list)
			{
				CodeDto dto = new CodeDto();
				BeanUtils.copyProperties(d, dto);
				dto.generateToken(salt);
				if(d.getCode()!=null && StringUtils.isNotEmpty(d.getCode().getId()))
				{
					dto.setParentId(d.getCode().getId());
				}
				dtos.add(dto);
			}
		}
		map.put("codes", dtos);
		return map;
	}
	
	/**
	 * 添加单级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addSingle(List<CodeDto> list)
	{
		User user=(User)session.getAttribute("user");
		String deptId = "";
		if(user != null){
			Set<Dept> set = user.getDepts();
			for(Dept dept : set){
				deptId = dept.getDeptId();
			}
		}
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(CodeDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
					{
						throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
					}
					//新增
				    Code code = new Code();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setId(id);
					dto.setState(1);//初始化
					dto.setCategory(0);//单级为0，多级为1
					if(user!=null){
						dto.setCreatorId(user.getUserId());
						dto.setUpdaterId(user.getUserId());
					}
					dto.setDeptId(deptId);
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
					BeanUtils.copyProperties(dto, code);
					this.codeRepository.saveAndFlush(code);
				}
			}
		}
	}
	
	/**
	 * 编辑单级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void editSingle(List<CodeDto> list)
	{
		User user=(User)session.getAttribute("user");
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(CodeDto dto: list)
			{
				if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
				{
					throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
				}
				Code code = this.codeRepository.findOne(dto.getId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(user.getUserId());
				} catch (ParseException e) {
						e.printStackTrace();
				}
				BeanUtils.copyProperties(dto, code);
				
				this.codeRepository.saveAndFlush(code);
					
			}
		}
	}
	
	/**
	 * 添加多级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addMulti(List<CodeDto> list)
	{
		User user=(User)session.getAttribute("user");
		String deptId = "";
		if(user != null){
			Set<Dept> set = user.getDepts();
			for(Dept dept : set){
				deptId = dept.getDeptId();
			}
		}
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(CodeDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
					{
						throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
					}
					//新增
				    Code code = new Code();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setId(id);
					dto.setState(1);//初始化
					dto.setCategory(1);//多级为1，单级为0
					if(user!=null){
						dto.setCreatorId(user.getUserId());
						dto.setUpdaterId(user.getUserId());
					}
					dto.setDeptId(deptId);
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
					}
					BeanUtils.copyProperties(dto, code);
					if(dto.getParentId()!=null && dto.getParentId().length()>0)
					{
						Code parent = this.codeRepository.findOne(dto.getParentId());
						code.setCode(parent);
					}
					this.codeRepository.saveAndFlush(code);
				}
			}
		}
	}
	
	/**
	 * 编辑多级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void editMulti(List<CodeDto> list)
	{
		User user=(User)session.getAttribute("user");
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(CodeDto dto: list)
			{
				if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
				{
					throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
				}
				Code code = this.codeRepository.findOne(dto.getId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(user.getUserId());
				} catch (ParseException e) {
				}
				BeanUtils.copyProperties(dto, code);
				
				if(dto.getParentId()!=null && dto.getParentId().length()>0)
				{
					Code parent = this.codeRepository.findOne(dto.getParentId());
					code.setCode(parent);
				}
				this.codeRepository.saveAndFlush(code);
					
			}
		}
	}
	
	/**
	 * 添加数据字典-备份
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void add(List<CodeDto> list)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for(CodeDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					//新增
				    Code code = new Code();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setId(id);
					dto.setState(1);//初始化
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
					BeanUtils.copyProperties(dto, code);
					if(dto.getParentId()!=null && dto.getParentId().length()>0)
					{
						Code parent = this.codeRepository.findOne(dto.getParentId());
						code.setCode(parent);
					}
					this.codeRepository.saveAndFlush(code);
				}
			}
		}
	}
	
	/**
	 * 编辑数据字典-备份
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void edit(List<CodeDto> list)
	{
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for(CodeDto dto: list)
			{
				Code code = this.codeRepository.findOne(dto.getId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(userDetails.getUsername());
				} catch (ParseException e) {
						e.printStackTrace();
				}
				BeanUtils.copyProperties(dto, code);
				
				if(dto.getParentId()!=null && dto.getParentId().length()>0)
				{
					Code parent = this.codeRepository.findOne(dto.getParentId());
					code.setCode(parent);
				}
				this.codeRepository.saveAndFlush(code);
					
			}
		}
	}
	
	/**
	 * 查询出更新后的数据
	 * @param list
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<CodeDto> findCodes(List<CodeDto> list,String salt)
	{
		List<CodeDto> newList = new ArrayList<CodeDto>();
		if(list!=null && list.size()>0)
		{
			for(CodeDto d:list)
			{
				String id = d.getId();
				Code code = this.codeRepository.findOne(id);
				if(code!=null)
				{
					CodeDto dto = new CodeDto();
					BeanUtils.copyProperties(code, dto);
					if(StringUtils.isNotEmpty(d.getParentId()))
					{
//						Code parent = this.codeRepository.findOne(d.getParentCode());
//						if(parent!=null)
//						{
//							String parentName = parent.getCodeName();
//							dto.setParentCodeName(parentName);
//						}
						dto.setParentId(d.getParentId());
					}
					dto.generateToken(salt);
					newList.add(dto);
				}
			}
		}
		return newList;
	}
	
	/**
	 * 多条删除
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteCode(List<CodeDto> list,UserDetails userDetails)
	{
		if(list!=null && list.size()>0)
		{
			for(CodeDto d:list)
			{
				if(d.getCodes()!=null && d.getCodes().size()>0)
				{
					throw new BusinessException("该条记录下面含有子元素集合，不能删除！");
				}
				String id = d.getId();
				//标记为已删除-0,未删除-1
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String updateTimeStr = sdf.format(new Date());
				Date updateTime = null;
				try {
					updateTime = sdf.parse(updateTimeStr);
				} catch (ParseException e) {
				}
			    this.codeRepository.updateState(id,updateTime,userDetails.getUsername());
			}
				
		}
	}
	
	/**
	 * 根据ID查询数据字典
	 * @param id
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public CodeDto findDtoById(String id,String salt)
	{
		CodeDto dto = new CodeDto();
		if(StringUtils.isNotEmpty(id))
		{
			Code code = this.codeRepository.findOne(id);
			if(code!=null)
			{
				BeanUtils.copyProperties(code, dto);
				dto.generateToken(salt);
			}
		}
		return dto;
	}
	
	/**
	 * 根据ID查询数据字典
	 * @param id
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public Code findById(String id)
	{
		Code code = null;
		if(id!=null && id.length()>0)
		{
			code = this.codeRepository.findOne(id);
		}
		return code;
	}
	
	/**
	 * 查询出所有的数据字典集合
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<CodeDto> findAllCodes()
	{
//		List<Code> AllCodes = this.codeRepository.findAll();
		List<Code> AllCodes = this.codeRepository.findAllCodes();
		List<CodeDto> list = new ArrayList<CodeDto>();
		if(AllCodes!=null && AllCodes.size()>0)
		{
			for(Code u:AllCodes)
			{
				CodeDto dto = new CodeDto();
				BeanUtils.copyProperties(u, dto);
				if(u.getCode()!=null && StringUtils.isNotEmpty(u.getCode().getId()))
				{
//					Code parent = this.codeRepository.findOne(u.getCode().getId());
//					if(parent!=null)
//					{
//						String parentName = parent.getCodeName();
//						dto.setParentCodeName(parentName);
//					}
					dto.setParentId(u.getCode().getId());
				}
				list.add(dto);
			}
		}
		return list;
	}
	
	/**
	 * 查询出所有子节点集合
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<CodeDto> findAllChildCodes(String parentCode,String salt)
	{
		List<CodeDto> list = new ArrayList<CodeDto>();
		if(parentCode!=null && parentCode.length()>0)
		{
			Code parent = this.codeRepository.findOne(parentCode);
			if(parent!=null)
			{
				List<Code> AllCodes = this.codeRepository.findAllChildCodes(parent);
				if(AllCodes!=null && AllCodes.size()>0)
				{
					for(Code u:AllCodes)
					{
						CodeDto dto = new CodeDto();
						BeanUtils.copyProperties(u, dto);
						if(u.getCode()!=null && StringUtils.isNotEmpty(u.getCode().getId()))
						{
							Code code = this.codeRepository.findOne(u.getCode().getId());
							dto.setParentId(u.getCode().getId());
						}
						dto.generateToken(salt);
						list.add(dto);
					}
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 根据codeType查询出codeCode
	 * @param codeType
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public String findCodeByType(String codeType)
	{
		List<String> list =  this.codeRepository.findCodeByType(codeType);
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据业务代码查询
	 * @param codeCode
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public boolean findByCodeCode(String codeCode,String id)
	{
		Code code = null;
		if(codeCode!=null && codeCode.length()>0)
		{
			code = this.codeRepository.findByCodeCode(codeCode,id);
			if(code!=null)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * 根据类型和index 查询对应的code
	 * @param codeType
	 * @param index
	 * @return
	 */
	public CodeDto findOneCodeByTypeAndIndex(String codeType,Integer index){
		Code code = codeRepository.findOneCodeByCodeTypeAndCodeIndex(codeType, index);
		CodeDto cdto = new CodeDto();
		if(code != null){
			BeanUtils.copyProperties(code, cdto);
		}
		return cdto;
	}
	/**
	 * 根据类型 查询对应的code
	 * @param codeType
	 * @return
	 */
	public List<CodeDto> findListCodeByType(String codeType){
		List<CodeDto> cDtoList = new ArrayList<CodeDto>();
		List<Code> codeList = codeRepository.findCodeByCodeType(codeType);
		if(codeList != null && codeList.size()>0){
			for(Code code:codeList){				
				CodeDto cdto = new CodeDto();
				BeanUtils.copyProperties(code, cdto);
				cDtoList.add(cdto);
			}
		}else{
			return null;
		}
		return cDtoList;
	}
	/**
	 * 添加子节点
	 * @param list
	 * @param parentCode
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addTreeNode(List<CodeDto> list,String parentCode)
	{
		User user=(User)session.getAttribute("user");
		String deptId = "";
		if(user != null){
			Set<Dept> set = user.getDepts();
			for(Dept dept : set){
				deptId = dept.getDeptId();
			}
		}
		Code parent = null;
		if(parentCode!=null && parentCode.length()>0)
		{
			parent = this.codeRepository.findOne(parentCode);
		}
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for(CodeDto dto: list)
			{
				if(StringUtils.isEmpty(dto.getToken()))
				{
					if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
					{
						throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
					}
					//新增
				    Code code = new Code();
					String id = UUID.randomUUID().toString().replaceAll("-", "");
					dto.setId(id);
					dto.setState(1);//初始化
					dto.setCategory(1);//初始化为多级
					if(user!=null){
						dto.setCreatorId(user.getUserId());
						dto.setUpdaterId(user.getUserId());
					}
					dto.setDeptId(deptId);
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
					BeanUtils.copyProperties(dto, code);
					code.setCode(parent);
					this.codeRepository.saveAndFlush(code);
				}
			}
		}
	}
	
	/**
	 * 编辑子节点
	 * @param list
	 * @param parentCode
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void editTreeNode(List<CodeDto> list,String parentCode)
	{
		User user=(User)session.getAttribute("user");
		Code parent = null;
		if(parentCode!=null && parentCode.length()>0)
		{
			parent = this.codeRepository.findOne(parentCode);
		}
		if(list!=null && list.size()>0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for(CodeDto dto: list)
			{
				if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
				{
					throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
				}
				Code code = this.codeRepository.findOne(dto.getId());
				String updateTimeStr = sdf.format(new Date());
				Date updateTime;
				try {
						updateTime = sdf.parse(updateTimeStr);
						dto.setUpdateTime(updateTime);
						dto.setUpdaterId(user.getUserId());
				} catch (ParseException e) {
						e.printStackTrace();
				}
				BeanUtils.copyProperties(dto, code);
				code.setCode(parent);
				this.codeRepository.saveAndFlush(code);
					
			}
		}
	}
	
	/**
	 * 删除树的子节点
	 * @param ids
	 */
	@Transactional(propagation= Propagation.REQUIRED)
	public void deleteTreeNodes(String ids,UserDetails userDetails)
	{
		System.out.println("ids:"+ids);
		if(!StringUtils.isEmpty(ids))
		{
			String[] array = ids.split(",");
			if(array.length>0)
			{
				for(String id: array)
				{
					Code code = this.codeRepository.findOne(id);
					if(code!=null && code.getCodes()!=null && code.getCodes().size()>0)
					{
						String codeName = code.getCodeName(); 
						throw new BusinessException("删除失败："+codeName+"含有子节点！");
					}
					else if(code!=null)
					{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String updateTimeStr = sdf.format(new Date());
						Date updateTime = null;
						try {
							updateTime = sdf.parse(updateTimeStr);
						} catch (ParseException e) {
						}
						this.codeRepository.deleteTreeNode(id,updateTime,userDetails.getUsername());
					}
				}
			}
		}
	}
	
	/**
	 * 修改树的节点
	 * @param codeDto
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void editCode(CodeDto codeDto)
	{
		if(!findByCodeCode(codeDto.getCodeCode(),codeDto.getId()))
		{
			throw new BusinessException("业务代码["+codeDto.getCodeCode()+"]已存在！");
		}
		Code code = this.codeRepository.findOne(codeDto.getId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String updateTimeStr = sdf.format(new Date());
		Date updateTime;
		try {
				updateTime = sdf.parse(updateTimeStr);
//				codeDto.setUpdateTime(updateTime);
//				codeDto.setUpdaterId(userDetails.getUsername());
				code.setUpdateTime(updateTime);
				code.setUpdaterId(userDetails.getUsername());
				code.setCreateTime(code.getCreateTime());
				code.setCreatorId(code.getCreatorId());
		} catch (ParseException e) {
				e.printStackTrace();
		}
//		BeanUtils.copyProperties(codeDto, code);
		code.setCodeCode(codeDto.getCodeCode());
		code.setCodeIndex(codeDto.getCodeIndex());
		code.setCodeName(codeDto.getCodeName());
		code.setCodeType(codeDto.getCodeType());
		if(StringUtils.isNotEmpty(codeDto.getCodeTypeName()))
		{
			code.setCodeTypeName(codeDto.getCodeTypeName());
		}
		this.codeRepository.saveAndFlush(code);
	}

	/**
	 * 查询出所有子节点集合
	 * @return
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<CodeDto> findAllChildCodes(String parentCode)
	{
		List<CodeDto> list = new ArrayList<CodeDto>();
		if(parentCode!=null && parentCode.length()>0)
		{
			Code parent = this.codeRepository.findOne(parentCode);
			if(parent!=null)
			{
				List<Code> AllCodes = this.codeRepository.findAllChildCodes(parent);
				if(AllCodes!=null && AllCodes.size()>0)
				{
					for(Code u:AllCodes){
						CodeDto dto = new CodeDto();
						BeanUtils.copyProperties(u, dto);
						if(u.getCode()!=null && StringUtils.isNotEmpty(u.getCode().getId()))
						{
							dto.setParentId(u.getCode().getId());
						}
						list.add(dto);
					}
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 计算树的层级
	 * @param id
	 * @return
	 */
	public Integer getTreeLayer(String id)
	{
		List<Integer> countList = new ArrayList<Integer>();
		int count = 0;
		countLayers(id, count, countList);
		int max = 0;
		if(countList.size()>0)
		{
			max = countList.get(0);
			for(Integer i: countList)
			{
				if(i>max)
				{
					max = i;
				}
			}
		}
		return max;
	}
	
	/**
	 * 计算树的层级
	 * @param id
	 * @param count
	 * @param countList
	 * @return
	 */
	public Integer countLayers(String id,Integer count,List<Integer> countList)
	{
		if(countList==null)
		{
			countList = new ArrayList<Integer>();
		}
		if(count==null)
		{
			count = 0;
		}
		if(id!=null && id.length()>0)
		{
			Code parent = this.codeRepository.findOne(id);
			if(parent!=null && parent.getCodes()!=null && parent.getCodes().size()>0)
			{
				count++;
				List<Code> children = parent.getCodes();
				for(int i=0;i<children.size();i++)
				{
					Code code = children.get(i);
					if(code!=null && code.getId()!=null && code.getId().length()>0)
					{
						countList.add(countLayers(code.getId(), count,countList));
					}
				}
			}
		 }
		return count;
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Code> findRoots()
	{
		List<Code> list = this.codeRepository.findRoots();
		return list;
	}
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Code> findRoots(String dataId)
	{
		List<Code> list = this.codeRepository.findRoots(dataId);
		return list;
	}
	/**
	 * 添加多级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addMulti(String codeId,CodeDto dto)
	{
		User user=(User)session.getAttribute("user");
		String deptId = "";
		if(user != null){
			Set<Dept> set = user.getDepts();
			for(Dept dept : set){
				deptId = dept.getDeptId();
			}
		}
		if(codeId!=null && codeId.length()>0)
		{
			Code parent = this.codeRepository.findOne(codeId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
			{
				throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
			}
			//新增
			Code code = new Code();
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			dto.setId(id);
		    dto.setState(1);//初始化
		    dto.setCategory(1);//单级为0，多级为1
		    if(user!=null){
				dto.setCreatorId(user.getUserId());
				dto.setUpdaterId(user.getUserId());
			}
		    dto.setDeptId(deptId);
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
			BeanUtils.copyProperties(dto, code);
			code.setCode(parent);
			this.codeRepository.saveAndFlush(code);
		}
		
	}
	
	/**
	 * 添加单级数据字典
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addSingle(CodeDto dto){
			User user=(User)session.getAttribute("user");
			String deptId = "";
			if(user != null){
				Set<Dept> set = user.getDepts();
				for(Dept dept : set){
					deptId = dept.getDeptId();
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(!findByCodeCode(dto.getCodeCode(),dto.getId()))
			{
				throw new BusinessException("业务代码["+dto.getCodeCode()+"]已存在！");
			}
			//新增
			Code code = new Code();
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			dto.setId(id);
		    dto.setState(1);//初始化
		    dto.setCategory(0);//单级为0，多级为1
		    if(user!=null){
				dto.setCreatorId(user.getUserId());
				dto.setUpdaterId(user.getUserId());
			}
		    dto.setDeptId(deptId);
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
			BeanUtils.copyProperties(dto, code);
			this.codeRepository.saveAndFlush(code);
	}
	
}
