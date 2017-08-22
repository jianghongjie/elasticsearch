package com.dachen.elasticsearch.model;

import io.searchbox.annotations.JestId;

public abstract class BaseInfo{
	//为其快速建立索引值，然后使用ES的搜索API，将您的返回的对象转换Java对象,可以是插入对象的主键ID等,检索完成之后将其返回
	@JestId
	private String bizId;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	
	public abstract String index();
	
	public abstract String type();
}
