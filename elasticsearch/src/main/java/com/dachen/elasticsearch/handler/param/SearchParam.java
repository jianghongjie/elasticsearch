package com.dachen.elasticsearch.handler.param;

import java.util.HashMap;
import java.util.Map;


public class SearchParam extends AbstractSearchParam{
	
	protected SearchParam(Builder builder){
		super(builder);
	}
	
	protected Map<String,Object> getSubQuery(){
		Map<String,Object> setting = new HashMap<String,Object>(4);
    	Map<String,Object> match = new HashMap<String,Object>(1);
		
    	setting.put("query", searchKey);//搜索关键词
    	setting.put("fields", searchFields);//搜索范围
		
    	setting.put("tie_breaker", 0.3);
    	if("query_string".equalsIgnoreCase(schema)){
    		/*
    		 * 最大分查询，个人感觉使用最大分查询更合理，这会让包含有我们寻找的关键字有更高的权重，而不是在不同的字段中重复出现的相同单词。
    		 * 如果不指定则采用布尔查询来构造查询
    		 */
    		setting.put("use_dis_max", true);
    		
    		/**
    		 * query
    		 * default_field
    		 * default_operator(or)
    		 */
    		match.put("query_string", setting);//
    	}
    	else if("multi_match".equalsIgnoreCase(schema)){
    		match.put("multi_match", setting);//
//    		match.put("type", "best_fields");//默认使用dis_max
    	}else{
    		match.put("match_all", new HashMap<String,Object>());//
    	}
		return match;
	} 
	
	public static class Builder extends AbstractSearchParam.Builder<SearchParam,Builder>
	{
		
		public Builder(String indexName) {
			super(indexName);
		}
		
		public SearchParam build() {
			 return new SearchParam(this);
		}
	}
}
