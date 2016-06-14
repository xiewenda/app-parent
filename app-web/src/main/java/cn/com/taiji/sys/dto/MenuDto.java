package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import cn.com.taiji.sys.domain.Menu;
import cn.com.taiji.sys.domain.Role;

/**
 * 菜单dto类
 * @author  SunJingyan
 * @date  2014年6月10日
 */
public class MenuDto implements Serializable {
	
	private static final long serialVersionUID = 8867257601351189506L;

	private String menuId;

	private String controllerClass;

	private byte[] iconPath;

	private String menuDesc;

	private Integer menuIndex;

	private String menuName;

	private String menuUrl;

	private String parentId;
	
	private String parentName;

	private byte[] smallIconPath;

	private Date createTime;

	private String creatorId;
	
	private Date updateTime;

	private String updaterId;
	
	private Integer flag;

	private String remark;
	
//	private Menu parent = null;

	private List<Menu> children = new LinkedList<Menu>();

	private Set<Role> roles;
	
	private String token = "";

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getControllerClass() {
		return controllerClass;
	}

	public void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
	}

	public byte[] getIconPath() {
		return iconPath;
	}

	public void setIconPath(byte[] iconPath) {
		this.iconPath = iconPath;
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

	public String getMenuDesc() {
		return menuDesc;
	}

	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}

	public Integer getMenuIndex() {
		return menuIndex;
	}

	public void setMenuIndex(Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public byte[] getSmallIconPath() {
		return smallIconPath;
	}

	public void setSmallIconPath(byte[] smallIconPath) {
		this.smallIconPath = smallIconPath;
	}

//	public Menu getParent() {
//		return parent;
//	}
//
//	public void setParent(Menu parent) {
//		this.parent = parent;
//	}
//
	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void generateToken(String salt) {
		if (salt == null)
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(menuId)
					+ Long.toString(serialVersionUID));
		else
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(menuId) + salt);

	}

	public boolean tokenKeeped(String salt) {
		if (salt == null)
			return (DigestUtils.sha1Hex(StringUtils.trimToEmpty(menuId)
					+ Long.toString(serialVersionUID))).equals(token);
		else
			return DigestUtils.sha1Hex(StringUtils.trimToEmpty(menuId) + salt).equals(
					token);
	}

}
