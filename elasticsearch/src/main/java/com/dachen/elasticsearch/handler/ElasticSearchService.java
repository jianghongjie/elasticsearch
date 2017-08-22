package com.dachen.elasticsearch.handler;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dachen.elasticsearch.action.ExistsAction;
import com.dachen.elasticsearch.action.SearchAction;
import com.dachen.elasticsearch.action.SearchResult;
import com.dachen.elasticsearch.handler.param.AbstractSearchParam;
import com.dachen.elasticsearch.model.BaseInfo;
import com.dachen.elasticsearch.util.JestClientUtils;
import com.dachen.elasticsearch.util.TypeDefine;

final class ElasticSearchService {
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
	
	static void createIndex(String index){
		createIndex(index,0,0);
	}
	/**
	 * 创建一个索引（表）
	 * @param index
	 * @param shards 分片数
	 * @param replicas 副本数
	 * @throws IOException
	 */
	/****************************************************************
			 "settings" : {
	  			"index" : {
	        		"creation_date" : "1461809437304",
	        		"number_of_shards" : "5",
	        		"number_of_replicas" : "1",
	        		"uuid" : "iCB237baRCalK2Juyc4UEg",
			        "version" : {
			          "created" : "2020099"
			        }
	  			}
			 }
	 ****************************************************************/
	static void createIndex(String index,int shards,int replicas) {
		if(StringUtils.isEmpty(index)){
			logger.error("index is null");
			return;
		}
		if(isExists(index,null)){
			return;
		}
		if(shards<=0 && replicas<=0){
			execute(new CreateIndex.Builder(index).build());
			return;
		}
		Map<String,Integer> settingMap = new HashMap<String,Integer>();
		/*
		 * 一个索引中含有的主分片(Primary Shard)的数量，默认值是5。在索引创建后这个值是不能被更改的。
		 */
		settingMap.put("number_of_shards", shards);//
		/*
		 * 每一个主分片关联的副本分片(Replica Shard)的数量，默认值是1。这个设置在任何时候都可以被修改。 
		 * 可以动态地通过update-index-settings API完成对副本分片数量的修改
		 * PUT /my_temp_index/_settings
				{
				    "number_of_replicas": 1
				}
		 */
		settingMap.put("number_of_replicas", replicas);//
		logger.info("create index {}",index);
		execute(new CreateIndex.Builder(index).settings(JSON.toJSONString(settingMap)).build());
	}
	
	/**
	 * 定义一个类型（映射配置）
	 * @param type
	 * @param mappingJson
	 * @throws IOException
	 */
	/**
	 *
			"properties":{
		        "id":{"type":"string","store":"yes"}  ---属性设置
		        "name":{---属性设置
		        	"type":"string",
		        	"store":"no",  ---该字段的原始值是否被写入索引，默认为no
		        	"index":"analyzed"  --analyzed/no,为no时，无法搜索该字段
		        }  
		    }
	 */
	static void createIndexMapping(String index,String type) {
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(index)){
			logger.error("index or type is null");
			return;
		}
		if(isExists(index,type)){
			return;
		}
		logger.info("create indexMapping {}.{}",index,type);
		String mappingJson = TypeDefine.getIndexMapping(type);
		PutMapping putMapping = new PutMapping.Builder(index,type,mappingJson).build();
		execute(putMapping);
	}
	
	static void batchSaveDocument(String index,String type,List<? extends BaseInfo>bizDataList) {
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(index)){
			logger.error("index or type is null");
			return;
		}
		Bulk.Builder builder = new Bulk.Builder()
									.defaultIndex(index)
									.defaultType(type)
									.setParameter(Parameters.REFRESH, true);
		for(BaseInfo data:bizDataList){
			String jsonData = JSON.toJSONString(data);
			if(!type.equalsIgnoreCase(data.type())){
				logger.error("the type is error,data is {}",jsonData);
				continue;
			}
			if(!index.equalsIgnoreCase(data.index())){
				logger.error("the index is error,data is {}",jsonData);
				continue;
			}
			builder.addAction(
					new Index.Builder(jsonData).id(data.getBizId()).build());
		}
		execute(builder.build());
	}
	
	/**
	 * 保存文档，根据Id，如果存在则更新，不存在则新增
	 * @param type
	 * @param bizData
	 */
	static void saveDocument(String indexName,String type,BaseInfo bizData) {
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(indexName)){
			logger.error("index or type is null");
			return;
		}
		if(bizData==null){
			logger.error("data is null");
			return;
		}
		String jsonData = JSON.toJSONString(bizData);
		logger.info("saveDocument(type={}) {}",type,jsonData);
		Index index = new Index.Builder(jsonData)
				.index(indexName)
				.type(type)
				.setParameter(Parameters.REFRESH, true)
				.id(bizData.getBizId())//提供请求参数设置属性，如路由、版本控制、操作等类型。
				.build();
		execute(index);
	}
	
	/**
	 * 更新文档（TODO）
	 * @param type
	 * @param id
	 * @param field
	 * @param value
	 * 
	 * {"script" : "ctx._source.name_of_new_field = \"value_of_new_field\""} 增加名称为name_of_new_field的列
	 * {"script" : "ctx._source.remove(\"name_of_new_field\")"}删除名称为name_of_new_field的列
	 */
	static void updataDocument(String index,String type,String bizId,Map<String,Object>updateFieldValue){
//		String script ="";
		Map<String,Object> docMap = new HashMap<String,Object>();
//		Map<String,Object> scriptMap = new HashMap<String,Object>();
//		for(String field:updataFieldValue.keySet()){
//			scriptMap.put(field, value);
//		}
		docMap.put("doc_as_upsert", true);//对应的Id不存在则插入
		docMap.put("doc", updateFieldValue);//对应的Id不存在则插入
		execute(new Update.Builder(JSON.toJSONString(docMap)).index(index).type(type).id(bizId).build());
	}
	
	static void deleteDocuments(String index,String type,String bizId) {
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(index) || StringUtils.isEmpty(bizId)){
			logger.error("index or type or bizId is null");
			return;
		}
		logger.info("deleteDocuments(type={}) {}",type,bizId);
		execute(new Delete.Builder(bizId).index(index).type(type).build());
	}
	
	static void deleteType(String index,String type) {
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(index)){
			logger.error("index or type is null");
			return;
		}
		logger.info("deleteType(type={}) {}",type);
		execute(new DeleteIndex.Builder(index).type(type).build());
	}
	
	static void deleteIndex(String index) {
		if(StringUtils.isEmpty(index)){
			logger.error("index is null");
			return;
		}
		logger.info("deleteIndex(index={}) {}",index);
//		execute(new DeleteIndex.Builder(index).type(type).build());
		execute(new DeleteIndex.Builder(index).build());
	}
	static SearchResponse searchAndReturnBizId2(final AbstractSearchParam searchParam){
	    SearchResponse response = new SearchResponse(); 
  	    List<String> returnField = new ArrayList<String>(1);
        returnField.add("bizId");
        SearchResult hitResult = getResult(searchParam,returnField);
        if(hitResult==null){
            return response;
        }
        if(!hitResult.isSucceeded()){
            logger.error(hitResult.getErrorMessage());
            return response;
        }
        List<String> resultList = null;
        Map<String,List<String>> result = null;
        
        List<SearchResult.Hit<Object, Void>> hits = hitResult.getHits(Object.class);
        if(hits!=null && hits.size()>0){
            result = new HashMap<String,List<String>>(4);
            for(SearchResult.Hit<Object, Void> hit:hits){
                if(hit==null){
                    continue;
                }
                resultList =  result.get(hit.type);
                if(resultList==null){
                    resultList = new ArrayList<String>(hits.size());
                    result.put(hit.type,resultList);
                }
                resultList.add(hit._id);
            }
        }
        Integer totalSize = hitResult.getTotal();
        response.setData(result);
        response.setTotalSize(totalSize==null?0:totalSize);
        return response;
	}
	static Map<String,List<String>> searchAndReturnBizId(final AbstractSearchParam searchParam){
	    SearchResponse response = searchAndReturnBizId2(searchParam);
		return response.getData();
	}
	
	static Map<String,List<Map<String,Object>>> searchAndReturnDocument(final AbstractSearchParam searchParam,final List<String> returnField){
		SearchResult hitResult = getResult(searchParam,returnField);
		if(hitResult==null){
			return null;
		}
		if(!hitResult.isSucceeded()){
			logger.error(hitResult.getErrorMessage());
			return null;
		}
		List<Map<String,Object>> resultList = null;
		Map<String,List<Map<String,Object>>> result = null;
		List<SearchResult.Hit<Map, Void>> hits = hitResult.getHits(Map.class);
		if(hits!=null && hits.size()>0){
			result = new HashMap<String,List<Map<String,Object>>>(4);
			for(SearchResult.Hit<Map, Void> hit:hits){
				if(hit==null){
					continue;
				}
				resultList =  result.get(hit.type);
				if(resultList==null){
					resultList = new ArrayList<Map<String,Object>>(hits.size());
					result.put(hit.type,resultList);
				}
				hit.source.putIfAbsent("bizId", hit._id);
				resultList.add(hit.source);
			}
		}
		return result;
	}
	
	static boolean isExists(String index,String type){
		ExistsAction.Builder builder = new ExistsAction.Builder().index(index);
		if(StringUtils.isNotEmpty(type)){
			builder.type(type);
		}
		JestResult result = execute(builder.build());
		return result.isSucceeded();
	}
	
	private static SearchResult getResult(final AbstractSearchParam searchParam,final List<String> returnField){
		String query = searchParam.buildQuery();
		if(StringUtils.isBlank(query)){
			return null;
		}
		logger.debug(query);
		SearchAction.Builder builder = new SearchAction.Builder(query)
					.addIndex(searchParam.getIndexName())// multiple index or types can be added.
					.resultFields(returnField)
					.setSearchType(searchParam.getSearchType());
		
		String[]types=searchParam.getType();
		if(types!=null){
			for(String type:types){
				builder.addType(type);
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("searchDocuments begin,param={} ",JSON.toJSONString(searchParam));
		}
		SearchResult hitResult = execute(builder.build());
		if(logger.isDebugEnabled()){
			logger.debug("searchDocuments end,result={} ",hitResult.getJsonString());
		}
		return hitResult;
	}
	protected static <T extends JestResult> T execute(Action<T> clientRequest){
		 try {
			 JestResult result = JestClientUtils.jestClient().execute(clientRequest);
			 if(!result.isSucceeded()){
				 logger.error(result.getErrorMessage());
			 }
			 return (T)result;
		 } catch (IOException e) {
			 logger.error(e.getMessage());
		 }
			//异步非阻塞IO
//			client.executeAsync(index, new JestResultHandler<JestResult>(){
//				public void completed(JestResult result) {
//					System.out.println("it's ok");
//				}
//				public void failed(Exception ex) {
//					System.out.println(ex.getMessage());
//				}
//			});
		 return null;
	}
}
