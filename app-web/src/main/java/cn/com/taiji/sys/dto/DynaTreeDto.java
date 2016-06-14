package cn.com.taiji.sys.dto;

import java.util.ArrayList;
import java.util.List;

import cn.com.taiji.sys.domain.Code;
import cn.com.taiji.sys.domain.Dept;

/**
 * DynaTreeDto
 * @author SunJingyan
 * @date 2014年12月14日
 *
 */
public class DynaTreeDto {


	private String title;
	private String key;
	private boolean isFolder;
	private String href; 
	private List children=new ArrayList();

	/**
	 * 机构转换成树(多个树)
	 * @param roots
	 * @author SunJingyan
	 */
	public List<DynaTreeDto> deptTree(List<Dept> roots)
	{
		List<DynaTreeDto> list = new ArrayList<DynaTreeDto>();
		if(!roots.isEmpty())
		{
			for(Dept dept: roots)
			{
				DynaTreeDto dto = new DynaTreeDto(dept);
				list.add(dto);
			}
		}
		return list;
	}
	
	/**
	 * 机构转换成树(单个树)
	 * @param dept
	 * @author SunJingyan
	 */
	public DynaTreeDto(Dept dept) 
	{
		if(dept!=null)
		{
			if(dept.getDeptId()!=null && dept.getDeptId().length()>0)
			{
				this.setKey(dept.getDeptId());
			}
			if(dept.getDeptName()!=null && dept.getDeptName().length()>0)
			{
				this.setTitle(dept.getDeptName());
			}
			if(dept.getDeptUrl()!=null && dept.getDeptUrl().length()>0)
			{
				this.setHref(dept.getDeptUrl());
			}
			this.setFolder(!dept.getChildren().isEmpty());
			if(!dept.getChildren().isEmpty())
			{
				for(Dept d :dept.getChildren())
				{
					this.children.add(new DynaTreeDto(d));
				}
			}
	   }
	}

	/**
	 * 数据字典转换成树(多个树)
	 * @param roots
	 * @author SunJingyan
	 */
	public List<DynaTreeDto> codeTree(List<Code> roots)
	{
		List<DynaTreeDto> list = new ArrayList<DynaTreeDto>();
		if(!roots.isEmpty())
		{
			for(Code code: roots)
			{
				DynaTreeDto dto = new DynaTreeDto(code);
				list.add(dto);
			}
		}
		return list;
	}
	
	/**
	 * 数据字典转换成树(单个树)
	 * @param dept
	 * @author SunJingyan
	 */
	public DynaTreeDto(Code code) 
	{
		if(code!=null)
		{
			if(code.getId()!=null && code.getId().length()>0)
			{
				this.setKey(code.getId());
			}
			if(code.getCodeName()!=null && code.getCodeName().length()>0)
			{
				this.setTitle(code.getCodeName());
			}
			this.setFolder(!code.getCodes().isEmpty());
			if(!code.getCodes().isEmpty())
			{
				for(Code c :code.getCodes())
				{
					this.children.add(new DynaTreeDto(c));
				}
			}
	   }
	}
	
	public DynaTreeDto() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}

	

}
