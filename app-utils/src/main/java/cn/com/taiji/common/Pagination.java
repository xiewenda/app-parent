package cn.com.taiji.common;

import java.util.List;

public class Pagination<T> {
	public Pagination() {
	}
	/*
	 * 每页显示数据的条数
	 */
	private int pageSize;
	/*
	 * 页面开始时
	 */
	private int pageStartNo;
	/*
	 * 当前页 页码
	 */
	private int pageCurrentNum;
	/*
	 * 总条数
	 */
	private long pageTotal;
	/*
	 * 总页数
	 */
	private int pageCount;
	/*
	 * 当前页的结果集
	 */
	private List<T> pageResult;
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageStartNo() {
		return pageStartNo;
	}
	public void setPageStartNo(int pageStartNo) {
		this.pageStartNo = pageStartNo;
	}
	public int getPageCurrentNum() {
		return pageCurrentNum;
	}
	public void setPageCurrentNum(int pageCurrentNum) {
		this.pageCurrentNum = pageCurrentNum;
	}


	public long getPageTotal() {
		return pageTotal;
	}
	public void setPageTotal(long pageTotal) {
		this.pageTotal = pageTotal;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public List<T> getPageResult() { 
		return pageResult;
	}
	public void setPageResult(List<T> pageResult) {
		this.pageResult = pageResult;
	}
	
	
}
