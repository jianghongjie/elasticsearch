package com.dachen.elasticsearch.handler.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoolSearchParam extends AbstractSearchParam{

	private List<Map<String,Object>> filter;
	
	/**
	 * 默认使用最大分查询
	 */
	private boolean useDisMax= true;
	
	protected BoolSearchParam(Builder builder) {
		super(builder);
		this.filter = builder.filter;
		this.useDisMax = builder.useDisMax; 
	}
	
	protected Map<String,Object> getSubQuery()
    {
		Map<String,Object> bool = new HashMap<String,Object>(2);
    	if(filter!=null){
    		if(filter.size()==1){
    			bool.put("filter", filter.get(0));
    		}else{
    			bool.put("filter", filter);
    		}
    	}
    	List<Map<String,Object>> matchList = getMatchList();
    	if(matchList!=null && matchList.size()>0){
    		if(useDisMax)//filter and must
    		{
    			Map<String,Object> disMaxMap=new HashMap<String,Object>(1);
    			Map<String,Object> queries=new HashMap<String,Object>(1);
    			queries.put("queries", matchList);
    			queries.put("tie_breaker", 0.3);
    			
    			disMaxMap.put("dis_max", queries);
    			bool.put("must", disMaxMap);
    		}
    		else//filter and should
    		{
    			bool.put("should", matchList);
    			bool.put("minimum_should_match", 1);//should中至少一个匹配
    		}
    	}
    	Map<String,Object> subQuery = new HashMap<String,Object>(1);
    	subQuery.put("bool", bool);
		return subQuery;
    }
	
	private List<Map<String,Object>> getMatchList(){
		if(this.searchFields!=null && this.searchFields.size()>0)
		{
    		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(searchFields.size());
    		for(String field: this.searchFields){
    			Map<String,Object> condition = new HashMap<String,Object>(1);
    			Map<String,Object> match = new HashMap<String,Object>(1);
    			condition.put(field, this.searchKey);
    			match.put("match", condition);
    			matchList.add(match);
    		}
    		return matchList;
		}
		return null;
	}
	
	@Deprecated 
	private Map<String,Object> getSubQueryByFiltered()
    {
    	Map<String,Object> filtered = new HashMap<String,Object>(2);
    	if(filter!=null){
    		filtered.put("filter", filter);
    		
    	}
    	List<Map<String,Object>> matchList = getMatchList();
    	if(matchList!=null && matchList.size()>0){
    		Map<String,Object> disMaxMap=new HashMap<String,Object>(1);
			Map<String,Object> queries=new HashMap<String,Object>(1);
			queries.put("queries", matchList);
			queries.put("tie_breaker", 0.3);
			
			disMaxMap.put("dis_max", queries);
			filtered.put("query", disMaxMap);
    	}
    	Map<String,Object> subQuery = new HashMap<String,Object>(1);
    	subQuery.put("filtered", filtered);
		return subQuery;
    }
	
	public static class Builder extends AbstractSearchParam.Builder<BoolSearchParam,Builder>{

		private List<Map<String,Object>> filter=new ArrayList<Map<String,Object>>(2); 
		
		private boolean useDisMax= false;
		public Builder(String indexName) {
			super(indexName);
		}
		
		public Builder type(String...type) {
			if(type!=null && type.length>=1){
				super.type(type[0]);
			}
			return this;
		}
		
		public Builder useDisMax(boolean useDisMax) {
			this.useDisMax = useDisMax;
			return this;
		}
		
		public Builder addFilter(String key,Object value){
			Map<String,Object> condition = new HashMap<String,Object>(1);
			Map<String,Object> term = new HashMap<String,Object>(1);
			condition.put(key, value);
			term.put("term", condition);
			filter.add(term);
			return this;
		}
		
		public BoolSearchParam build() {
			 schema("bool");
			 return new BoolSearchParam(this);
		}
	} 
}
