package com.dachen.elasticsearch.handler.param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.elasticsearch.util.TypeDefine;

public abstract class AbstractSearchParam {
	protected String indexName;
	 
	protected String[]type;
	
	protected int from=0;
	
	protected int size=10;
	
	protected String searchType="query_then_fetch";
	
	protected String schema = "query_string";
	
	protected String searchKey;
	
	protected List<String> resultFields;
	
	protected List<String> searchFields;
	
	public AbstractSearchParam(){
		
	}
	
	@SuppressWarnings("unchecked")
	public AbstractSearchParam(Builder builder) {
		this.indexName = builder.indexName;
		this.type = builder.type;
		this.searchKey = builder.searchKey;
		this.searchType=builder.searchType;
		this.schema = builder.schema;
		this.from = builder.from;
		this.size =builder.size;
		
		if(type==null || type.length==0){
			type = TypeDefine.getSearchTypes();
		}
		if(StringUtils.isEmpty(indexName)){
			indexName = TypeDefine.getSearchIndex();
		}
		
		if(builder.resultFields!=null && builder.resultFields.length>0){
			this.resultFields = java.util.Arrays.asList(builder.resultFields);
		}
		
		if(builder.searchFields!=null && builder.searchFields.length>0){
			this.searchFields = java.util.Arrays.asList(builder.searchFields);
		}else{
			this.searchFields = TypeDefine.getSearchFields(type);
		}
	}
	
	public String getIndexName() {
		return indexName;
	}

	public String[] getType() {
		return type;
	}

	public String getSearchType() {
		return searchType;
	}

	public List<String> getResultFields() {
		return resultFields;
	}
	
	public String buildQuery(){
		if(StringUtils.isBlank(searchKey)){
			return null;
		}
    	Map<String,Object> query = new HashMap<String,Object>(4);
    	query.put("query", getSubQuery());//
		//选择需要返回的字段
		if(resultFields!=null && resultFields.size()>0){
			query.put("fields", resultFields);
		}
		query.put("from", from);
		query.put("size", size);
//		query.put("explain",true);//
//		query.put("version", true);//Returns a version for each search hit
		return JSON.toJSONString(query);
	}
	
	
	protected abstract Map<String,Object> getSubQuery();
	
	
	@SuppressWarnings("unchecked")
	protected static abstract class Builder<T extends AbstractSearchParam, K>
	{
		/**
		 * 对应关系型数据库的db
		 */
		private String indexName;
		/**
		 * 对应关系型数据库的table,支持多类型查询
		 */
		private String[]type;
		
		private int from=0;
		
		private int size=10;
		
		/**
		 * dfs_query_then_fetch/dfsQueryThenFetch
		 * dfs_query_and_fetch/dfsQueryAndFetch
		 * query_then_fetch/queryThenFetch
		 * query_and_fetch/queryAndFetch
		 */
		private String searchType="query_then_fetch";
		/**
		 * query_string / multi_match
		 */
		private String schema = "query_string";
		
		private String searchKey;
		
		private String[]resultFields=null;
		
		private String[]searchFields = null;
		
		public Builder(String indexName) {
			this.indexName = indexName;
		}

		public K type(String...type) {
			this.type = type;
			return (K)this;
		}

		public K from(int from) {
			this.from = from;
			return (K)this;
		}

		public K size(int size) {
			this.size = size;
			return (K)this;
		}
		public K searchKey(String searchKey){
			this.searchKey = searchKey;
			return (K)this;
		}
		
		
		public K searchType(String searchType) {
			this.searchType = searchType;
			return (K)this;
		}
		
		public K schema(String schema) {
			this.schema = schema;
			return (K)this;
		}
		public K searchFields(String[]searchFields){
			this.searchFields = searchFields;
			return (K)this;
		}
		
		public K resultFields(String[]resultFields){
			this.resultFields = resultFields;
			return (K)this;
		}
		
		public abstract T build();
	}
}
