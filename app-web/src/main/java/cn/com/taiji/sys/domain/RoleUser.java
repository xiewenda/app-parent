package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the role_user database table.
 * 
 */
@Entity
@Table(name="role_user")
@NamedQuery(name="RoleUser.findAll", query="SELECT r FROM RoleUser r")
public class RoleUser implements Serializable {

	private static final long serialVersionUID = -5549613676858069574L;
	@EmbeddedId
	private RoleUserPK id;

	public RoleUser() {
	}

	public RoleUserPK getId() {
		return this.id;
	}

	public void setId(RoleUserPK id) {
		this.id = id;
	}

}