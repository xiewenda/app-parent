package cn.com.taiji.sys.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the dept_user database table.
 * 
 */
@Entity
@Table(name="dept_user")
@NamedQuery(name="DeptUser.findAll", query="SELECT d FROM DeptUser d")
public class DeptUser implements Serializable {
	
	private static final long serialVersionUID = 5680225687620614164L;
	@EmbeddedId
	private DeptUserPK id;

	public DeptUser() {
	}

	public DeptUserPK getId() {
		return this.id;
	}

	public void setId(DeptUserPK id) {
		this.id = id;
	}

}