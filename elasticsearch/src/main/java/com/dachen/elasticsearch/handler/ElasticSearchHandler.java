package com.dachen.elasticsearch.handler;


public class ElasticSearchHandler {

	/*private Client client;

	private static ElasticSearchHandler instance = new ElasticSearchHandler();
    
	public static ElasticSearchHandler getInstance()
	{
		return instance;
	}
	private ElasticSearchHandler(){
        //集群连接超时设置
          
              
            client = new TransportClient(settings);
         
    	Settings settings = Settings.builder()
    			.put("client.transport.ping_timeout", "10s")
//    			.put("client.transport.sniff", true)  
                .put("client", true)  
                .put("data",false)  
//                .put("clusterName","elasticsearch")  
    			.build();
    	InetAddress address=null;
		try {
//			address = InetAddress.getByAddress(ipAddress.getBytes());
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        client = TransportClient.builder()
        		.settings(settings)
        		.build()
        		.addTransportAddress(new InetSocketTransportAddress(address, 9300));
    }
    
    
    *//**
     * 建立索引,索引建立好之后,会在elasticsearch\data\elasticsearch\nodes\0创建所以你看
     * @param indexName  为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param indexType  Type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
     * @param jsondata     json格式的数据集合
     * 
     * @return
     *//*
	public void createIndexResponse(String indexname, String type, List<String> jsondata){
		 //创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
        for(String data:jsondata)
        {
        	IndexResponse response = requestBuilder
            		.setSource(data)
            		.execute()
            		.actionGet();
        }    
	}
    public void createIndexResponse(String indexname, String type, Map<String,String> jsondata){
        //创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
        for(String id:jsondata.keySet())
        {
        	IndexResponse response = requestBuilder.setId(id)
            		.setSource(jsondata.get(id))
            		.execute()
            		.actionGet();
        }     
         
    }
    
    *//**
     * 创建索引
     * @param client
     * @param jsondata
     * @return
     *//*
    public IndexResponse createIndexResponse(String indexname, String type,String id,String jsondata){
    	 IndexResponse response = client.prepareIndex(indexname, type,id)
    	            .setSource(jsondata)
    	            .execute()
    	            .actionGet();
    	        return response;
    }
    
    public IndexResponse createIndexResponse(String indexname, String type,String jsondata){
        IndexResponse response = client.prepareIndex(indexname, type)
		            .setSource(jsondata)
		            .execute()
		            .actionGet();
        return response;
    }
    
    *//**
     * 执行搜索
     * @param queryBuilder
     * @param indexname
     * @param type
     * @return
     *//*
    public List<String> search(QueryBuilder queryBuilder,SearchParam param)
    {
    	SearchRequestBuilder builder = client.prepareSearch(param.getIndexName())
    			.setTypes(param.getType())
    			.setSearchType(param.getSearchType())
    			.setFrom(param.getFrom())
    			.setSize(param.getSize());  
    	
    	builder.setQuery(queryBuilder);
    	
        SearchResponse searchResponse = builder.execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        List<String> list = new ArrayList<String>();
        
        if(searchHists.length>0){
            for(SearchHit hit:searchHists){
                list.add(hit.getSourceAsString());
            }
        }
        return list;
    }*/
    
}
