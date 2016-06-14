package cn.com.taiji.sys.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * The persistent class for the dept database table.
 * 
 */
@Entity
@Table(name="deptinfo")
@NamedQuery(name="Dept.findAll", query="SELECT d FROM Dept d")
@NamedQueries({
	@NamedQuery(name = "Dept.findDeptTree", query = "select d from Dept d left join fetch d.children"),
	@NamedQuery(name = "Dept.findRoots", query = "select d from Dept d where d.parent is null")
	})
public class Dept implements Serializable {
	

	private static final long serialVersionUID = 5119673746393145493L;

	@Id
	@Column(name="dept_id")
	private String deptId;

	@Column(name="dept_desc")
	private String deptDesc;

	@Column(name="dept_index")
	private Integer deptIndex;

	@Column(name="dept_name")
	private String deptName;

	@Column(name="dept_state")
	private String deptState;

	@Column(name="dept_type")
	private String deptType;

	@Column(name="dept_url")
	private String deptUrl;

//	@Column(name="parent_id")
//	private String parentId;

	//uni-directional many-to-many association to User
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
		name="dept_user"
		, joinColumns={
			@JoinColumn(name="dept_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="user_id")
			}
		)
	private List<User> users;
	
	@ManyToOne(cascade = { CascadeType.REFRESH },fetch=FetchType.EAGER)
	@JoinColumn(name = "parent_id", insertable = true, updatable = true)
	private Dept parent = null;

	@OrderBy("deptIndex asc")
	@Where(clause="flag = 1")
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH,CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id")
	private List<Dept> children = new LinkedList<Dept>();

	
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
	
	public Dept() {
	}

	
	@JsonIgnore
	public Dept getParent() {
		return parent;
	}


	public void setParent(Dept parent) {
		this.parent = parent;
	}


	@JsonIgnore
	public List<Dept> getChildren() {
		return children;
	}


	public void setChildren(List<Dept> children) {
		this.children = children;
	}


	public String getDeptId() {
		return this.deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
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


	public String getDeptDesc() {
		return this.deptDesc;
	}

	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}

	

	public Integer getDeptIndex() {
		return deptIndex;
	}


	public void setDeptIndex(Integer deptIndex) {
		this.deptIndex = deptIndex;
	}


	public String getDeptName() {
		return this.deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptState() {
		return this.deptState;
	}

	public void setDeptState(String deptState) {
		this.deptState = deptState;
	}

	public String getDeptType() {
		return this.deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptUrl() {
		return this.deptUrl;
	}

	public void setDeptUrl(String deptUrl) {
		this.deptUrl = deptUrl;
	}

//	public String getParentId() {
//		return this.parentId;
//	}
//
//	public void setParentId(String parentId) {
//		this.parentId = parentId;
//	}

	@JsonIgnore
	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}