package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the dept_user database table.
 * 
 */
@Embeddable
public class DeptUserPK implements Serializable {

	
	private static final long serialVersionUID = -1286091259170974853L;

	@Column(name="user_id", insertable=false, updatable=false)
	private String userId;

	@Column(name="dept_id", insertable=false, updatable=false)
	private String deptId;

	public DeptUserPK() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDeptId() {
		return this.deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DeptUserPK)) {
			return false;
		}
		DeptUserPK castOther = (DeptUserPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.deptId.equals(castOther.deptId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.deptId.hashCode();
		
		return hash;
	}
}