package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.Role;

/**
 * 用户dto
 * @author SunJingyan
*  @date 2014-04-21
 *
 */
public class UserDto  implements Serializable{
	
	private static final long serialVersionUID = 1170018455276020707L;

	private String userId;
	
	private Integer userIndex;

//	@Email(message="Email格式不正确！")
	private String email;
	
	private String phoneNum;
	
//	@NotEmpty(message="登陆名不能为空！")
//	@Size(min=6,max=20)
	private String loginName;

//	@NotEmpty(message="密码不能为空！")
	private String password;

	private String state;
	
	private String userName;
	
	private Date createTime;

	private String creatorId;
	
	private Date updateTime;

	private String updaterId;
	
	private Integer flag;

	private String remark;
	
	private Set<Role> roles;
	
	private Set<Dept> depts;
	
	private String token = "";
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getUserIndex() {
		return userIndex;
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

	public void setUserIndex(Integer userIndex) {
		this.userIndex = userIndex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Dept> getDepts() {
		return depts;
	}

	public void setDepts(Set<Dept> depts) {
		this.depts = depts;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void generateToken(String salt) {
		if (salt == null)
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(userId)
					+ Long.toString(serialVersionUID));
		else
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(userId) + salt);

	}

	public boolean tokenKeeped(String salt) {
		if (salt == null)
			return (DigestUtils.sha1Hex(StringUtils.trimToEmpty(userId)
					+ Long.toString(serialVersionUID))).equals(token);
		else
			return DigestUtils.sha1Hex(StringUtils.trimToEmpty(userId) + salt).equals(
					token);
	}
	

}
