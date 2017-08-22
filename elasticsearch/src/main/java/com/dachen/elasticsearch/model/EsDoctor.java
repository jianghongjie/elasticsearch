package com.dachen.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import com.dachen.elasticsearch.util.TypeDefine;

public class EsDoctor extends BaseInfo{
	
	private String name;
	
	private Integer status;
	
	private String departments;

	private String skill;
	private List<EsDiseaseType>expertise;
	
	private List<String>groupId;
	
	public EsDoctor(String bizId){
		this.setBizId(bizId);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public List<EsDiseaseType> getExpertise() {
		return expertise;
	}

	public void setExpertise(List<EsDiseaseType> expertise) {
		this.expertise = expertise;
	}
	
	public List<String> getGroupId() {
		return groupId;
	}

	public void setGroupId(List<String> groupId) {
		this.groupId = groupId;
	}
	

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public EsDoctor addExpertise(EsDiseaseType type){
		if(expertise==null){
			expertise = new ArrayList<EsDiseaseType>();
		}
		expertise.add(type);
		return this;
	}
	
	public EsDoctor addGroupId(String id){
		if(groupId==null){
			groupId = new ArrayList<String>();
		}
		groupId.add(id);
		return this;
	}
	
	
	@Override
	public String index() {
		return TypeDefine.Constants.INDEX_HEALTH;
	}

	@Override
	public String type() {
		return TypeDefine.Constants.TYPE_DOCTOR;
	}
}
