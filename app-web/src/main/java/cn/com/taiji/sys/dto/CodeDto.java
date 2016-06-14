package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;


import cn.com.taiji.sys.domain.Code;

/**
 * 数据字典dto
 * @author SunJingyan
 * @date 2014年8月27日
 *
 */
public class CodeDto implements Serializable{
	
	private static final long serialVersionUID = 7275727535778417507L;
	
	private String id;

	private String codeCode;

	private Integer codeIndex;
	
	private Integer category;

	private String codeName;

	private String codeType;

	private String codeTypeName;

	private Date createTime;

	private String creatorId;

	private String remark;

	private Integer state;

	private Date updateTime;

	private String updaterId;
	private String deptId;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
//	private Code code;
	/*子节点结合*/
	private List<Code> codes;
	
	private String token = "";
	
	/*父节点ID*/
	private String parentId;
	
//	private String parentCode;
	
	/*父节点代码中文名称*/
//	private String parentCodeName;

	public String getCodeCode() {
		return codeCode;
	}

	public void setCodeCode(String codeCode) {
		this.codeCode = codeCode;
	}

	public Integer getCodeIndex() {
		return codeIndex;
	}

	public void setCodeIndex(Integer codeIndex) {
		this.codeIndex = codeIndex;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getCodeTypeName() {
		return codeTypeName;
	}

	public void setCodeTypeName(String codeTypeName) {
		this.codeTypeName = codeTypeName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
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

//	public Code getCode() {
//		return code;
//	}
//
//	public void setCode(Code code) {
//		this.code = code;
//	}

	public List<Code> getCodes() {
		return codes;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public String getToken() {
		return token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

//	public String getParentCodeName() {
//		return parentCodeName;
//	}
//
//	public void setParentCodeName(String parentCodeName) {
//		this.parentCodeName = parentCodeName;
//	}
//
//	public String getParentCode() {
//		return parentCode;
//	}
//
//	public void setParentCode(String parentCode) {
//		this.parentCode = parentCode;
//	}

	public void generateToken(String salt) {
		if (salt == null)
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(id)
					+ Long.toString(serialVersionUID));
		else
			token = DigestUtils.sha1Hex(StringUtils.trimToEmpty(id) + salt);

	}

	public boolean tokenKeeped(String salt) {
		if (salt == null)
			return (DigestUtils.sha1Hex(StringUtils.trimToEmpty(id)
					+ Long.toString(serialVersionUID))).equals(token);
		else
			return DigestUtils.sha1Hex(StringUtils.trimToEmpty(id) + salt).equals(
					token);
	}
	
	

}
