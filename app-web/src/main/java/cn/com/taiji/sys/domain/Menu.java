package cn.com.taiji.sys.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Where;




/**
 * The persistent class for the menu database table.
 * 
 */
@Entity
@Table(name="menu")
@NamedQuery(name="Menu.findAll", query="SELECT m FROM Menu m")
@NamedQueries({
	@NamedQuery(name = "findMenuTree", query = "select o from Menu o left join fetch o.children"),
	@NamedQuery(name = "Menu.findRoots", query = "select o from Menu o where o.parent is null") })
public class Menu implements Serializable {
	
	private static final long serialVersionUID = 7381374907067127702L;

	@Id
	@Column(name="menu_id")
	private String menuId;

	@Column(name="controller_class")
	private String controllerClass;

	@Lob
	@Column(name="icon_path")
	private byte[] iconPath;

	@Column(name="menu_desc")
	private String menuDesc;

	@Column(name="menu_index")
	private Integer menuIndex;

	@Column(name="menu_name")
	private String menuName;

	@Column(name="menu_url")
	private String menuUrl;

//	@Column(name="parent_id")
//	private String parentId;

	@Lob
	@Column(name="small_icon_path")
	private byte[] smallIconPath;

	
	@ManyToOne(cascade = { CascadeType.REFRESH }, optional = false)
	//@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", insertable = true, updatable = true)
	private Menu parent = null;

	@OrderBy("menuIndex ASC")
	@Where(clause="flag = 1")
	//@OneToMany(fetch = FetchType.LAZY)
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id")
	private List<Menu> children = new LinkedList<Menu>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_menu", joinColumns = @JoinColumn(name = "menu_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time")
	private Date createTime;

	@Column(name="creator_id")
	private String creatorId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_time")
	private Date updateTime;

	@Column(name="updater_id")
	private String updaterId;
	
	private Integer flag;

	private String remark;
	
	public Menu() {
	}

	public String getMenuId() {
		return this.menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getControllerClass() {
		return this.controllerClass;
	}

	public void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
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

	public byte[] getIconPath() {
		return this.iconPath;
	}

	public void setIconPath(byte[] iconPath) {
		this.iconPath = iconPath;
	}

	public String getMenuDesc() {
		return this.menuDesc;
	}

	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}

	public Integer getMenuIndex() {
		return this.menuIndex;
	}

	public void setMenuIndex(Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	public String getMenuName() {
		return this.menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return this.menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

//	public String getParentId() {
//		return this.parentId;
//	}
//
//	public void setParentId(String parentId) {
//		this.parentId = parentId;
//	}

	public byte[] getSmallIconPath() {
		return this.smallIconPath;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void setSmallIconPath(byte[] smallIconPath) {
		this.smallIconPath = smallIconPath;
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

//	public Set<Role> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Set<Role> roles) {
//		this.roles = roles;
//	}

}