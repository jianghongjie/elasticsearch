package com.dachen.elasticsearch.handler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dachen.elasticsearch.handler.param.AbstractSearchParam;
import com.dachen.elasticsearch.model.BaseInfo;
import com.dachen.elasticsearch.util.TypeDefine;

public final class ElasticSearchFactory {
	
	private static ElasticSearchFactory INSTANCE;
	private static final Byte[] LOCKS = new Byte[0];  
	public static ElasticSearchFactory getInstance(){
		if(INSTANCE==null){
			synchronized (LOCKS){
				if(INSTANCE==null){
					INSTANCE = new ElasticSearchFactory();
				}
			}
		}
		return INSTANCE;
	}
	
	private ElasticSearchFactory(){
		indexInit();
	}
	
	private void indexInit(){
		List<String> indexList = TypeDefine.getIndexs();
		for(String index:indexList){
			ElasticSearchService.createIndex(index);
		}
		
		Map<String,String>typeMap=TypeDefine.getTypes();
		for(String type:typeMap.keySet()){
			ElasticSearchService.createIndexMapping(typeMap.get(type),type);
		}
	}
	
	/**
	 * 批量索引文档
	 * @param bizDataList
	 */
	public void batchSaveDocument(List<? extends BaseInfo>bizDataList) {
		if(bizDataList==null || bizDataList.size()<=0){
			return;
		}
		String index = bizDataList.get(0).index();
		String type = bizDataList.get(0).type();
		
		int pageSize = 100;
		int dataSize = bizDataList.size();
		if(dataSize<=pageSize){
			ElasticSearchService.batchSaveDocument(index,type, bizDataList);
		}else{
			int lastPageCount = dataSize%pageSize;
			int count = dataSize/pageSize;
			if(lastPageCount>0)
			{
				count++;
			}
			
			List<? extends BaseInfo>subList = null;
			int toIndex = 0;
			for(int i=0;i<count;i++)
			{
				toIndex =(i+1)*pageSize; 
				if(toIndex>dataSize){
					toIndex = dataSize;
				}
//				toIndex = Math.min((i+1)*pageSize, dataSize);
				subList = bizDataList.subList(i*pageSize, toIndex);
				ElasticSearchService.batchSaveDocument(index,type, subList);
			}
		}
		
	}
	
	/**
	 * 插入一个新的文档
	 * @param bizData
	 */
	public void insertDocument(BaseInfo bizData){
		if(bizData == null || StringUtils.isBlank(bizData.getBizId())){
			return;
		}
		ElasticSearchService.saveDocument(bizData.index(),bizData.type(),bizData);
	}
	
	/**
	 * 更新文档（可局部更新，值为空【=null】的不更新）
	 * @param bizData
	 */
	public void updateDocument(BaseInfo bizData){
		if(bizData == null || StringUtils.isBlank(bizData.getBizId())){
			return;
		}
		Field[]fields=bizData.getClass().getDeclaredFields();
		Map<String,Object>updataFieldValue = new HashMap<String,Object>();
		
		String fieldName = null;
		for(Field f:fields){
			fieldName = f.getName();
			try {
				PropertyDescriptor pd = new PropertyDescriptor(fieldName, bizData.getClass());
				Method get = pd.getReadMethod();
	            Object value = get.invoke(bizData);
//	            System.out.println("field:"+fieldName+"---value:"+value);
	            if(value!=null){
	            	updataFieldValue.put(fieldName, value);
	            }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ElasticSearchService.updataDocument(bizData.index(),bizData.type(),bizData.getBizId(), updataFieldValue);
//		ElasticSearchService.saveDocument(bizData.index(),bizData.type(),bizData);
	}
	/**
	 * 删除一个文档，只需要bizId
	 * @param bizData
	 */
	public void deleteDocument(BaseInfo bizData) {
		if(bizData == null){
			return;
		}
		ElasticSearchService.deleteDocuments(bizData.index(),bizData.type(),bizData.getBizId());
	}
	
	public void rebuildIndex(String index) {
		ElasticSearchService.deleteIndex(index);
		
		indexInit();
	}
	
	/**
	 * 该接口至返回业务数据的Id
	 * 返回数据按照类型分类（医生/集团）
	 * @param searchParam
	 * @return
	 */
	public Map<String,List<String>> searchAndReturnBizId(final AbstractSearchParam searchParam){
		return ElasticSearchService.searchAndReturnBizId(searchParam);
	}
	
	public SearchResponse searchAndReturnBizId2(final AbstractSearchParam searchParam){
        return ElasticSearchService.searchAndReturnBizId2(searchParam);
    }
	
	public List<String> boolSearchAndReturnBizId(final AbstractSearchParam searchParam){
		String[]types = searchParam.getType();
		Map<String,List<String>> result = ElasticSearchService.searchAndReturnBizId(searchParam);
		if(result==null){
			return null;
		}
		return result.get(types[0]);
	}
	
	/**
	 * 该接口至返回存储到es服务器上的所有字段信息
	 * 返回数据按照类型分类（医生/集团）
	 * @param searchParam
	 * @return
	 */
	public Map<String,List<Map<String,Object>>> searchAndReturnDocument(final AbstractSearchParam searchParam){
		return ElasticSearchService.searchAndReturnDocument(searchParam,searchParam.getResultFields());
	}
	
	public List<Map<String,Object>> boolSearchAndReturnDocument(final AbstractSearchParam searchParam){
		String[]types = searchParam.getType();
		Map<String,List<Map<String,Object>>>  result = ElasticSearchService.searchAndReturnDocument(searchParam,searchParam.getResultFields());
		if(result==null){
			return null;
		}
		return result.get(types[0]);
	}
}
