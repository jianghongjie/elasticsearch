package com.dachen.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import com.dachen.elasticsearch.util.TypeDefine;

public class EsGroup extends BaseInfo{
	private String name;
//	private String introduction;
//	private int weight;
	private List<EsDiseaseType>expertise;
	
	public EsGroup(String bizId){
		this.setBizId(bizId);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public String getIntroduction() {
//		return introduction;
//	}
//	public void setIntroduction(String introduction) {
//		this.introduction = introduction;
//	}
//	public int getWeight() {
//		return weight;
//	}
//	public void setWeight(int weight) {
//		this.weight = weight;
//	}
	public List<EsDiseaseType> getExpertise() {
		return expertise;
	}
	public void setExpertise(List<EsDiseaseType> expertise) {
		this.expertise = expertise;
	}
	
	public EsGroup addExpertise(EsDiseaseType type){
		if(expertise==null){
			expertise = new ArrayList<EsDiseaseType>();
		}
		expertise.add(type);
		return this;
	}
	
	@Override
	public String index() {
		return TypeDefine.Constants.INDEX_HEALTH;
	}

	@Override
	public String type() {
		return TypeDefine.Constants.TYPE_GROUP;
	}
}
