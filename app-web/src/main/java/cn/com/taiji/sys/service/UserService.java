package cn.com.taiji.sys.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import cn.com.taiji.sys.domain.DeptUser;
import cn.com.taiji.sys.domain.User;
import cn.com.taiji.sys.domain.UserRepository;
import cn.com.taiji.sys.dto.UserDto;
import cn.com.taiji.sys.exception.BusinessException;


/**
 * 用户Service
 * 
 * @author SunJingyan
 * @date 2014-05-20
 *
 */
@Service
public class UserService {

  @Inject
  private UserRepository userRepository;


  public UserRepository getUserRepository() {
    return userRepository;
  }

  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * 查询用户信息
   * 
   * @param searchParameters 查询参数的map集合
   * @return 查询的结果,map类型 total:总条数 users:查询结果list集合
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public Map getPage(final Map searchParameters, String salt) {
    Map map = new HashMap();
    int page = 0;
    int pageSize = 10;
    Page<User> pageList;
    // Page<UserDto> pageDtoList;
    if (searchParameters != null && searchParameters.size() > 0
        && searchParameters.get("page") != null) {
      page = Integer.parseInt(searchParameters.get("page").toString()) - 1;
    }
    if (searchParameters != null && searchParameters.size() > 0
        && searchParameters.get("pageSize") != null) {
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
    if (orders.size() > 0) {
      pageable = new PageRequest(page, pageSize, new Sort(orders));
    } else {
      Sort s = new Sort(Direction.ASC, "userIndex");
      pageable = new PageRequest(page, pageSize, s);
    }
    Map filter = (Map) searchParameters.get("filter");
    if (filter != null) {
      // String logic = filter.get("logic").toString();
      final List<Map> filters = (List<Map>) filter.get("filters");
      Specification<User> spec = new Specification<User>() {
        @Override
        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
          List<Predicate> pl = new ArrayList<Predicate>();
          for (Map f : filters) {
            // String operator = ((String) f.get("operator")).trim();
            String field = f.get("field").toString().trim();
            String value = f.get("value").toString().trim();
            if (value != null && value.length() > 0) {
              if ("loginName".equalsIgnoreCase(field)) {
                pl.add(cb.like(root.<String>get(field), "%" + value + "%"));
              } else if ("userName".equalsIgnoreCase(field)) {
                pl.add(cb.like(root.<String>get(field), "%" + value + "%"));
              } else if ("email".equalsIgnoreCase(field)) {
                pl.add(cb.like(root.<String>get(field), "%" + value + "%"));
              }
            }
          }
          // 查询出未删除的
          pl.add(cb.equal(root.<Integer>get("flag"), 1));
          // 按部门查询用户
          if (searchParameters.get("userIds") != null) {
            String userIds = searchParameters.get("userIds").toString();
            List<String> userIdList = new ArrayList<String>();
            if (userIds != null) {
              userIdList = Arrays.asList(userIds.split(","));
              pl.add(root.get("userId").in(userIdList));
            }
          }
          return cb.and(pl.toArray(new Predicate[0]));
        }
      };
      pageList = userRepository.findAll(spec, pageable);

    } else {
      Specification<User> spec = new Specification<User>() {
        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
          List<Predicate> list = new ArrayList<Predicate>();
          // 查询出未删除的
          list.add(cb.equal(root.<Integer>get("flag"), 1));
          // 按部门查询用户
          if (searchParameters.get("userIds") != null) {
            String userIds = searchParameters.get("userIds").toString();
            List<String> userIdList = new ArrayList<String>();
            if (userIds != null) {
              userIdList = Arrays.asList(userIds.split(","));
              list.add(root.get("userId").in(userIdList));
            }
          }
          return cb.and(list.toArray(new Predicate[0]));
        }
      };
      pageList = userRepository.findAll(spec, pageable);

    }
    map.put("total", pageList.getTotalElements());
    List<UserDto> dtos = new ArrayList<UserDto>();
    List<User> list = pageList.getContent();
    if (list != null && list.size() > 0) {
      for (User d : list) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(d, dto);
        dto.generateToken(salt);
        dtos.add(dto);
      }
    }
    map.put("users", dtos);
    return map;
  }

  /**
   * 添加用户信息
   * 
   * @param list
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void add(List<UserDto> list, UserDetails userDetails) {
    if (list != null && list.size() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (UserDto dto : list) {
        if (StringUtils.isEmpty(dto.getToken())) {
          if (!findByName(dto.getLoginName(), dto.getUserId())) {
            throw new BusinessException("用户名[" + dto.getLoginName() + "]已存在！");
          }
          // 新增
          User user = new User();
          String id = UUID.randomUUID().toString().replaceAll("-", "");
          dto.setUserId(id);
          dto.setFlag(1);// 初始化
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
          }
          BeanUtils.copyProperties(dto, user);
          this.userRepository.saveAndFlush(user);
        }
      }
    }
  }

  /**
   * 编辑用户信息
   * 
   * @param list
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void edit(List<UserDto> list, UserDetails userDetails) {
    if (list != null && list.size() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (UserDto dto : list) {
        if (!findByName(dto.getLoginName(), dto.getUserId())) {
          throw new BusinessException("用户名[" + dto.getLoginName() + "]已存在！");
        }
        User user = this.userRepository.findOne(dto.getUserId());
        String updateTimeStr = sdf.format(new Date());
        Date updateTime;
        try {
          updateTime = sdf.parse(updateTimeStr);
          dto.setUpdateTime(updateTime);
          dto.setUpdaterId(userDetails.getUsername());
        } catch (ParseException e) {
        }
        BeanUtils.copyProperties(dto, user);
        this.userRepository.saveAndFlush(user);

      }
    }
  }

  /**
   * 添加和编辑用户信息-备份
   * 
   * @param list
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void saveUser(List<UserDto> list) {
    if (list != null && list.size() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (UserDto d : list) {
        User user;
        if (d != null && d.getUserId() != null && !d.getUserId().equals("")) {
          // 修改
          user = this.userRepository.findOne(d.getUserId());
          String updateTimeStr = sdf.format(new Date());
          Date updateTime;
          try {
            updateTime = sdf.parse(updateTimeStr);
            d.setUpdateTime(updateTime);
          } catch (ParseException e) {
            e.printStackTrace();
          }
        } else {
          // 新增
          user = new User();
          String id = UUID.randomUUID().toString().replaceAll("-", "");
          d.setUserId(id);
          d.setFlag(1);// 初始化
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
        BeanUtils.copyProperties(d, user);
        this.userRepository.saveAndFlush(user);
      }
    }
  }


  /**
   * 多条删除
   * 
   * @param list
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteUser(List<UserDto> list, UserDetails userDetails) {
    if (list != null && list.size() > 0) {
      for (UserDto d : list) {
        if (d.getRoles() != null && d.getRoles().size() > 0) {
          throw new BusinessException("该用户与角色关联，不能删除！");
        } else if (d.getDepts() != null && d.getDepts().size() > 0) {
          throw new BusinessException("该用户与机构单位关联，不能删除！");
        } else {
          String id = d.getUserId();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String updateTimeStr = sdf.format(new Date());
          Date updateTime = null;
          try {
            updateTime = sdf.parse(updateTimeStr);
          } catch (ParseException e) {
          }
          this.userRepository.updateFlag(id, updateTime, userDetails.getUsername());
        }
      }
    }
  }

  /**
   * 查询出所有的用户集合
   * 
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public List<UserDto> findAllUsers() {
    // List<User> AllUsers = this.userRepository.findAll();
    List<User> AllUsers = this.userRepository.findAllUsers();
    List<UserDto> list = new ArrayList<UserDto>();
    if (AllUsers != null && AllUsers.size() > 0) {
      for (User u : AllUsers) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(u, dto);
        list.add(dto);
      }
    }
    // if(list!=null && list.size()>0)
    // {
    // for(UserDto dto:list)
    // {
    // for(UserDto selectDto:selectedUsers)
    // {
    // if(dto.getUserId().equals(selectDto.getUserId()))
    // {
    // dto.setSelected(true);
    // }
    // }
    // }
    // }
    return list;
  }

  /**
   * 登录查询
   * 
   * @param loginName
   * @param password
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public User login(String loginName, String password) {
    return this.userRepository.login(loginName, password);
  }

  /**
   * 根据用户名查询用户
   * 
   * @param loginName
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public User findByLoginName(String loginName) {
    User user = this.userRepository.findByLoginName(loginName);
    return user;
  }

  /**
   * 修改密码
   * 
   * @param userId
   * @param password
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void savePwd(String userId, String password) {
    password = DigestUtils.sha256Hex(password);
    this.userRepository.updatePwd(userId, password);
  }

  /**
   * 查询出更新后的数据
   * 
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public List<UserDto> findUsers(List<UserDto> list, String salt) {
    List<UserDto> newList = new ArrayList<UserDto>();
    if (list != null && list.size() > 0) {
      for (UserDto d : list) {
        String id = d.getUserId();
        User user = this.userRepository.findOne(id);
        if (user != null) {
          UserDto dto = new UserDto();
          BeanUtils.copyProperties(user, dto);
          dto.generateToken(salt);
          newList.add(dto);
        }
      }
    }
    return newList;
  }

  /**
   * 根据用户名查询
   * 
   * @param
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public boolean findByName(String loginName, String id) {
    List<User> users = new ArrayList<User>();
    if (loginName != null && loginName.length() > 0) {
      users = this.userRepository.findByUserName(loginName, id);
      if (users.size() > 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * 根据用户名查询
   * 
   * @param
   * @return
   */
  @Transactional(propagation = Propagation.SUPPORTS)
  public User findById(String id) {
    // //UserDto uDto = new UserDto();
    // if(id!=null &&!"".equals(id)){
    // User user = this.userRepository.findByUserID(id);
    // if(user != null){
    // BeanUtils.copyProperties(user, uDto);
    // }else{
    // System.out.println("user信息有误");
    // }
    // }
    // return uDto;
    User user = this.userRepository.findByUserID(id);
    return user;
  }
}
