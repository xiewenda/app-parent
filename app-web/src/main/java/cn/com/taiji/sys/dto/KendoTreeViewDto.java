package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import cn.com.taiji.sys.domain.Code;
import cn.com.taiji.sys.domain.Dept;
import cn.com.taiji.sys.domain.Menu;

/**
 * 使用kendo TreeView控件来实现树
 * @author SunJingyan
 * @date 2014-04-21
 */
public class KendoTreeViewDto implements Serializable{

	private static final long serialVersionUID = -2837536739385846397L;

	/*节点ID*/
	private String id;
	
	/*节点名称*/
	private String text;
	
	/*是否展开*/
	private boolean expanded; 
	
	/*节点链接url*/
	private String href; 
	
	/*子节点集合*/
	private List<KendoTreeViewDto> items=new ArrayList();
	
	/*图标样式*/
	private String sprite;
	
	private boolean checked;

	/**
	 * 菜单转换成树(单个树)
	 * @param menu
	 */
	public KendoTreeViewDto(Menu menu) 
	{
		if(menu!=null)
		{
			if(menu.getMenuId()!=null && menu.getMenuId().length()>0)
			{
				this.setId(menu.getMenuId());
			}
			if(menu.getMenuName()!=null && menu.getMenuName().length()>0)
			{
				this.setText(menu.getMenuName());
			}
			if(menu.getMenuUrl()!=null && menu.getMenuUrl().length()>0)
			{
				this.setHref(menu.getMenuUrl());
			}
			if(!menu.getChildren().isEmpty()){
				for(Menu m : menu.getChildren())
				{
					this.items.add(new KendoTreeViewDto(m));
				}
			}
		}
		this.expanded = true;
		this.sprite = "folder";
	
	}
	
	/**
	 * 菜单转换成树(多个树)
	 * @param roots
	 */
	public List<KendoTreeViewDto> menuTree(List<Menu> roots) {
		List<KendoTreeViewDto> list = new ArrayList<KendoTreeViewDto>();
		if(!roots.isEmpty())
		{
			for(Menu menu: roots)
			{
				KendoTreeViewDto dto = new KendoTreeViewDto(menu);
				list.add(dto);
			}
		}
		return list;
	}
	
	/**
	 * 机构转换成树(单个树)
	 * @param dept
	 */
	public KendoTreeViewDto(Dept dept) {
		if(dept!=null)
		{
			if(dept.getDeptId()!=null && dept.getDeptId().length()>0)
			{
				this.setId(dept.getDeptId());
			}
			if(dept.getDeptName()!=null && dept.getDeptName().length()>0)
			{
				this.setText(dept.getDeptName());
			}
			if(dept.getDeptUrl()!=null && dept.getDeptUrl().length()>0)
			{
				this.setHref(dept.getDeptUrl());
			}
			if(!dept.getChildren().isEmpty())
			{
				for(Dept d :dept.getChildren())
				{
					this.items.add(new KendoTreeViewDto(d));
				}
			}
	   }
		this.expanded = true;

	}
	
	/**
	 * 机构转换成树(多个树)
	 * @param roots
	 */
	public List<KendoTreeViewDto> deptTree(List<Dept> roots) {
		List<KendoTreeViewDto> list = new ArrayList<KendoTreeViewDto>();
		if(!roots.isEmpty())
		{
			for(Dept dept: roots)
			{
				KendoTreeViewDto dto = new KendoTreeViewDto(dept);
				list.add(dto);
			}
		}
		return list;
	}
	
	/**
	 * 数据字典转换成树
	 * @param code
	 */
	public KendoTreeViewDto(CodeDto codeDto,String salt) {
		if(codeDto!=null)
		{
			if(codeDto.getId()!=null && codeDto.getId().length()>0)
			{
				this.setId(codeDto.getId());
			}
			if(codeDto.getCodeName()!=null && codeDto.getCodeName().length()>0)
			{
				this.setText(codeDto.getCodeName());
			}
			if(!codeDto.getCodes().isEmpty())
			{
				List<Code> codes = codeDto.getCodes();
				for(Code c: codes)
				{
					CodeDto dto = new CodeDto();
					BeanUtils.copyProperties(c, dto);
					dto.generateToken(salt);
					this.items.add(new KendoTreeViewDto(dto,salt));
				}
			}
		}
		this.expanded = true;
	}

	public KendoTreeViewDto() {
		super();
	}


	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public List<KendoTreeViewDto> getItems() {
		return items;
	}
	public void setItems(List<KendoTreeViewDto> items) {
		this.items = items;
	}
	public String getSprite() {
		return sprite;
	}
	public void setSprite(String sprite) {
		this.sprite = sprite;
	}
	
	

}
