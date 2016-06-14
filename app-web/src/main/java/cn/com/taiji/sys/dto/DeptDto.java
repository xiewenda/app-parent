package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.User;


/**
 * 机构单位dto
 * @author SunJingyan
 * @date 2014-04-21
 *
 */
public class DeptDto  implements Serializable{

	private static final long serialVersionUID = 8632051872528629002L;

	private String deptId;

	private String deptDesc;

	private Integer deptIndex;

	@NotEmpty(message = "机构名称不能为空")
	private String deptName;

	private String deptState;

	private String deptType;

	private String deptUrl;

	private String parentId;
	
	private String parentName;

	private List<User> users;
	
	private Date createTime;

	private String creatorId;
	
	private Date updateTime;

	private String updaterId;
	
	private Integer flag;

	private String remark;
	
//	private Dept parent = null;

//	private CodeDto codeDto;
	
	private List<Dept> children = new LinkedList<Dept>();
	
	private String token = "";
	
	
	
//	public Dept getParent() {
//		return parent;
//	}
//
//	public void setParent(Dept parent) {
//		this.parent = parent;
//	}

	public List<Dept> getChildren() {
		return children;
	}

	public void setChildren(List<Dept> children) {
		this.children = children;
	}

	public List<User> getUsers() {
		return users;
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

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptDesc() {
		return deptDesc;
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
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptState() {
		return deptState;
	}

	public void setDeptState(String deptState) {
		this.deptState = deptState;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptUrl() {
		return deptUrl;
	}

	public void setDeptUrl(String deptUrl) {
		this.deptUrl = deptUrl;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

//	public CodeDto getCodeDto() {
//		return codeDto;
//	}
//
//	public void setCodeDto(CodeDto codeDto) {
//		this.codeDto = codeDto;
//	}

	public void generateToken(String salt) {
		if (salt == null)
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(deptId)
					+ Long.toString(serialVersionUID));
		else
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(deptId) + salt);

	}

	public boolean tokenKeeped(String salt) {
		if (salt == null)
			return (DigestUtils.sha1Hex(StringUtils.trimToEmpty(deptId)
					+ Long.toString(serialVersionUID))).equals(token);
		else
			return DigestUtils.sha1Hex(StringUtils.trimToEmpty(deptId) + salt).equals(
					token);
	}
	
}
