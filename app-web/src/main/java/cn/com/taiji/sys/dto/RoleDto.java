
package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 角色dto
 * @author SunJingyan
 * @date 2014年4月21日
 *
 */
public class RoleDto implements Serializable {

	private static final long serialVersionUID = 8089520763539561510L;

	private String roleId;
	
	private String parentId;
	
	private String roleDesc;
	
	private Integer roleIndex;
	
	private String roleName;
	
	private Integer showUsers;
	
	private List<Menu> menus;
	
	private Set<User> users;
	
	private Date createTime;

	private String creatorId;
	
	private Date updateTime;

	private String updaterId;
	
	private Integer flag;

	private String remark;
	
	private String token = "";
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdaterId() {
		return updaterId;
	}

	public void setUpdaterId(String updaterId) {
		this.updaterId = updaterId;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Integer getRoleIndex() {
		return roleIndex;
	}

	public void setRoleIndex(Integer roleIndex) {
		this.roleIndex = roleIndex;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getShowUsers() {
		return showUsers;
	}

	public void setShowUsers(Integer showUsers) {
		this.showUsers = showUsers;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public void generateToken(String salt) {
		if (salt == null)
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(roleId)
					+ Long.toString(serialVersionUID));
		else
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(roleId) + salt);

	}

	public boolean tokenKeeped(String salt) {
		if (salt == null)
			return (DigestUtils.sha1Hex(StringUtils.trimToEmpty(roleId)
					+ Long.toString(serialVersionUID))).equals(token);
		else
			return DigestUtils.sha1Hex(StringUtils.trimToEmpty(roleId) + salt).equals(
					token);
	}

	@Override
	public String toString() {
		return "RoleDto roleid="+roleId+"rolename="+roleName+"parentroleid="+parentId+"roledescription="+roleDesc+"roleindex="+roleIndex+"";
	}
}