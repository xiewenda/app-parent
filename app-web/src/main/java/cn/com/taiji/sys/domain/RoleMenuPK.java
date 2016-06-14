package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the role_menu database table.
 * 
 */
@Embeddable
public class RoleMenuPK implements Serializable {

	private static final long serialVersionUID = 4040666385792166495L;

	@Column(name="menu_id", insertable=false, updatable=false)
	private String menuId;

	@Column(name="role_id", insertable=false, updatable=false)
	private String roleId;

	public RoleMenuPK() {
	}
	public String getMenuId() {
		return this.menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RoleMenuPK)) {
			return false;
		}
		RoleMenuPK castOther = (RoleMenuPK)other;
		return 
			this.menuId.equals(castOther.menuId)
			&& this.roleId.equals(castOther.roleId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.menuId.hashCode();
		hash = hash * prime + this.roleId.hashCode();
		
		return hash;
	}
}