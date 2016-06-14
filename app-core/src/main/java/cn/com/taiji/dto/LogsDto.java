package cn.com.taiji.dto;

import java.util.Date;

public class LogsDto {

		private String selectType;
		private String s_beginYear;
		private String s_endYear;
		private String currYear;
		private String s_beginMonth;
		private String s_endMonth;
		
		private String s_page;
		private String s_pageSize;
		private String total;
		
		private String title;
		private String year;
		private String timeType;
		private String starTime;
		private String endTime;
		//日志模块
		private String selectModelType;
		
		
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
		public String getSelectModelType() {
			return selectModelType;
		}
		public void setSelectModelType(String selectModelType) {
			this.selectModelType = selectModelType;
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
		public String getSelectType() {
			return selectType;
		}
		public void setSelectType(String selectType) {
			this.selectType = selectType;
		}
		public String getS_beginYear() {
			return s_beginYear;
		}
		public void setS_beginYear(String s_beginYear) {
			this.s_beginYear = s_beginYear;
		}
		public String getS_endYear() {
			return s_endYear;
		}
		public void setS_endYear(String s_endYear) {
			this.s_endYear = s_endYear;
		}
		public String getCurrYear() {
			return currYear;
		}
		public void setCurrYear(String currYear) {
			this.currYear = currYear;
		}
		public String getS_beginMonth() {
			return s_beginMonth;
		}
		public void setS_beginMonth(String s_beginMonth) {
			this.s_beginMonth = s_beginMonth;
		}
		public String getS_endMonth() {
			return s_endMonth;
		}
		public void setS_endMonth(String s_endMonth) {
			this.s_endMonth = s_endMonth;
		}
		public String getS_page() {
			return s_page;
		}
		public void setS_page(String s_page) {
			this.s_page = s_page;
		}
		public String getS_pageSize() {
			return s_pageSize;
		}
		public void setS_pageSize(String s_pageSize) {
			this.s_pageSize = s_pageSize;
		}
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public String getTimeType() {
			return timeType;
		}
		public void setTimeType(String timeType) {
			this.timeType = timeType;
		}
		public String getStarTime() {
			return starTime;
		}
		public void setStarTime(String starTime) {
			this.starTime = starTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		
		
}
