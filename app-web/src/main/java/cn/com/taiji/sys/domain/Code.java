package cn.com.taiji.sys.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the code database table.
 * 
 */
@Entity
@Table(name="code")
@NamedQuery(name="Code.findAll", query="SELECT c FROM Code c")
public class Code implements Serializable {

	private static final long serialVersionUID = -98516053380975909L;

	@Id
	private String id;

	@Column(name="code_code")
	private String codeCode;

	@Column(name="code_index")
	private Integer codeIndex;
	
	private Integer category;

	@Column(name="code_name")
	private String codeName;

	@Column(name="code_type")
	private String codeType;

	@Column(name="code_type_name")
	private String codeTypeName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time")
	private Date createTime;

	@Column(name="creator_id")
	private String creatorId;

	private String remark;

	private Integer state;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="update_time")
	private Date updateTime;

	@Column(name="updater_id")
	private String updaterId;
	
	@Column(name="dept_id")
	private String deptId;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	private Code code;

	@OrderBy("codeIndex asc")
	@Where(clause="state = 1")
	@OneToMany(mappedBy="code" , cascade = { CascadeType.PERSIST, CascadeType.REFRESH,CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.EAGER)
	private List<Code> codes;

	public Code() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodeCode() {
		return this.codeCode;
	}

	public void setCodeCode(String codeCode) {
		this.codeCode = codeCode;
	}

	public Integer getCodeIndex() {
		return this.codeIndex;
	}

	public void setCodeIndex(Integer codeIndex) {
		this.codeIndex = codeIndex;
	}

	public String getCodeName() {
		return this.codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeType() {
		return this.codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getCodeTypeName() {
		return this.codeTypeName;
	}

	public void setCodeTypeName(String codeTypeName) {
		this.codeTypeName = codeTypeName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdaterId() {
		return this.updaterId;
	}

	public void setUpdaterId(String updaterId) {
		this.updaterId = updaterId;
	}
	
	

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Code getCode() {
		return this.code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public List<Code> getCodes() {
		return this.codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public Code addCode(Code code) {
		getCodes().add(code);
		code.setCode(this);

		return code;
	}

	public Code removeCode(Code code) {
		getCodes().remove(code);
		code.setCode(null);

		return code;
	}
	
	

}