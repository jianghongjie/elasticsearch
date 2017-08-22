package com.dachen.elasticsearch.model;

import com.dachen.elasticsearch.util.TypeDefine;

public class EsDiseaseType extends BaseInfo{
	private String diseaseName;

	private String diseaseAlias;
	
	private String diseaseRemark;
	
	public EsDiseaseType(String name,String remark){
		this.diseaseName = name;
		this.diseaseRemark = remark;
	}
	
	public EsDiseaseType(String name,String alias,String remark){
		this.diseaseName = name;
		this.diseaseAlias = alias;
		this.diseaseRemark = remark;
	}
	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String name) {
		this.diseaseName = name;
	}
	public String getDiseaseRemark() {
		return diseaseRemark;
	}
	public void setDiseaseRemark(String remark) {
		this.diseaseRemark = remark;
	}
	public String getDiseaseAlias() {
		return diseaseAlias;
	}
	public void setDiseaseAlias(String alias) {
		this.diseaseAlias = alias;
	}

	@Override
	public String index() {
		return TypeDefine.Constants.INDEX_HEALTH;
	}

	@Override
	public String type() {
		return TypeDefine.Constants.TYPE_DISEASE;
	}
}
