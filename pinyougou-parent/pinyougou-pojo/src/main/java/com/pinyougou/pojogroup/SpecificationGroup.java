package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

public class SpecificationGroup implements Serializable {
	private TbSpecification specification;
	private List<TbSpecificationOption> specificationOptionList;
	public SpecificationGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SpecificationGroup(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
		super();
		this.specification = specification;
		this.specificationOptionList = specificationOptionList;
	}
	public TbSpecification getSpecification() {
		return specification;
	}
	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}
	public List<TbSpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}
	public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}
	@Override
	public String toString() {
		return "SpecificationGroup [specification=" + specification + ", specificationOptionList="
				+ specificationOptionList + "]";
	}
	
	
	
	
}
