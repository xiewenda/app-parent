package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the role_user database table.
 * 
 */
@Embeddable
public class RoleUserPK implements Serializable {

	private static final long serialVersionUID = -6773592495408197838L;

	@Column(name="role_id", insertable=false, updatable=false)
	private String roleId;

	@Column(name="user_id", insertable=false, updatable=false)
	private String userId;

	public RoleUserPK() {
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RoleUserPK)) {
			return false;
		}
		RoleUserPK castOther = (RoleUserPK)other;
		return 
			this.roleId.equals(castOther.roleId)
			&& this.userId.equals(castOther.userId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.roleId.hashCode();
		hash = hash * prime + this.userId.hashCode();
		
		return hash;
	}
}