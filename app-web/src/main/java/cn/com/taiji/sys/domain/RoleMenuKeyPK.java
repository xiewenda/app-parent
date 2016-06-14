package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the userdeptkey database table.
 * 
 */
@Embeddable
public class RoleMenuKeyPK implements Serializable {

	private static final long serialVersionUID = -1727990758469653268L;

	@Column(name="roleId", insertable=false, updatable=false)
	private String roleId;

	@Column(name="menuId", insertable=false, updatable=false)
	private String menuId;

	public RoleMenuKeyPK() {
	}


	public String getRoleId() {
		return roleId;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}


	public String getMenuId() {
		return menuId;
	}


	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RoleMenuKeyPK)) {
			return false;
		}
		RoleMenuKeyPK castOther = (RoleMenuKeyPK)other;
		return 
			this.roleId.equals(castOther.roleId)
			&& this.menuId.equals(castOther.menuId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.roleId.hashCode();
		hash = hash * prime + this.menuId.hashCode();
		
		return hash;
	}
}