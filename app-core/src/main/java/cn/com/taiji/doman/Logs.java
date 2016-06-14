package cn.com.taiji.doman;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name="SYSLOG")
public class Logs {
    @Id
    @GeneratedValue(generator="syslog")
    @GenericGenerator(name="syslog",strategy="uuid.hex")
	private String id;
	/**
	 * 操作人
	 */
	private String createByUser;
	/**
	 * 操作人Ip地址
	 */
	private String requestIp;
	/**
	 * 操作时间
	 */
	private Date createTime;
	/**
	 * 操作说明
	 */
	private String description;
	/**
	 * 操作模块类型
	 */
	private String domainType;
	/**
	 * 操作状态
	 */
	private String status;
	/**
	 * 备注
	 */
	private String remark;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateByUser() {
		return createByUser;
	}
	public void setCreateByUser(String createByUser) {
		this.createByUser = createByUser;
	}
	public String getRequestIp() {
		return requestIp;
	}
	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
