package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the role_menu database table.
 * 
 */
@Entity
@Table(name="role_menu")
@NamedQuery(name="RoleMenu.findAll", query="SELECT r FROM RoleMenu r")
public class RoleMenu implements Serializable {

	private static final long serialVersionUID = -3204190138251678123L;
	@EmbeddedId
	private RoleMenuPK id;

	public RoleMenu() {
	}

	public RoleMenuPK getId() {
		return this.id;
	}

	public void setId(RoleMenuPK id) {
		this.id = id;
	}

}