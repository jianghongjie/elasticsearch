package com.dachen.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.handler.param.SearchParam;
import com.dachen.elasticsearch.model.BaseInfo;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsDoctor;
import com.dachen.elasticsearch.util.DataFactory;
import com.dachen.elasticsearch.util.TypeDefine.Constants;


/**
 * Unit test for simple App.
 */
public class AppTest{
	/*************************************************************************************
	 * 1、删除索引：
	 * curl -XDELETE 'http://localhost:9200/health/'
	 *   
	 * 2、get index
	 * curl -XGET 'http://localhost:9200/health/?pretty' 
	 * curl -XGET 'http://localhost:9200/health/_settings,_mappings?pretty'
	 * curl -XGET 'http://localhost:9200/health/_mapping/group?pretty'
	 * 
	 * 2、Types or index Exists
	 * curl -XHEAD -i 'http://localhost:9200/health/group'
	 * curl -XHEAD -i 'http://localhost:9200/health'
	 * 
	 * 3、其他
	 * http://192.168.3.7:9200/health?pretty
	 * http://192.168.3.7:9200/health/_mapping?pretty
	 * http://192.168.3.7:9200/health/group/_search?pretty
	 * 
	 * _analyzer
	 * http://192.168.3.7:9200/_analyze?analyzer=ik_max_word&text=
	 * ik_max_word: 会将文本做最细粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合；
	 * ik_smart: 会做最粗粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”。
	 * standard:标准
	 ***************************************************************************************/
	public static void main(String[] args) throws IOException {
//		initData();
//		search("二叶");
//		search("住院医师");
//		search("SOMEBODY");
//		search("深圳市");
//		search("皮肤科");
		
//		searchMyIndex("Brown fox");
//		searchMyIndex("quick");// pets
		
//		updateDoc();
		search("感觉");
    }
	
	private static Object search(String searchKey){
		SearchParam searchParam = 
				new SearchParam.Builder(Constants.INDEX_HEALTH)
								.searchKey(searchKey)
								.type(new String[]{Constants.TYPE_DOCTOR,Constants.TYPE_GROUP})
								.resultFields(new String[]{"bizId","expertise.name"})
								.size(10).build();
		Object result =  ElasticSearchFactory.getInstance().searchAndReturnDocument(searchParam);
		System.out.println(searchKey+":"+JSON.toJSONString(result));
		return result;
	}
	
	private static void searchMyIndex(String searchKey){
		SearchParam searchParam = 
				new SearchParam.Builder("my_index")
								.searchKey(searchKey)
								.schema("multi_match")
								.type(new String[]{"my_type"})
								.size(10).build();
		Object result = ElasticSearchFactory.getInstance().searchAndReturnDocument(searchParam);
		System.out.println(searchKey+":"+JSON.toJSONString(result));
	}
	private static void initData()
	{
		ElasticSearchFactory  instance = ElasticSearchFactory.getInstance();

		System.out.println("insert document to type("+Constants.TYPE_GROUP+")");
		List<? extends BaseInfo>data = DataFactory.getInitGroupData();
		instance.batchSaveDocument(data);
		
		System.out.println("insert document to type("+Constants.TYPE_DOCTOR+")");
		List<? extends BaseInfo>docData = DataFactory.getInitDoctorData();
		instance.batchSaveDocument(docData);
	}
	
	private static void updateDoc(){
		 EsDoctor doctor = new EsDoctor("101894");
//		 doctor.setName("doctor2");
//		 doctor.setSkill("擅长医治各种二叶疾病3333");
//		 doctor.setDepartments("呼吸内科2");
//		 doctor.setDepartments("");
		 List<EsDiseaseType>expertise = new ArrayList<EsDiseaseType>();
		 expertise.add(new EsDiseaseType("全感觉性障碍膜炎",""));
		 expertise.add(new EsDiseaseType("职业性皮肤病",""));
		 expertise.add(new EsDiseaseType("上消化道大出血",""));
		 expertise.add(new EsDiseaseType("平足症",""));
		 doctor.setExpertise(expertise);
		 ElasticSearchFactory  instance = ElasticSearchFactory.getInstance();
		 instance.updateDocument(doctor);
		 System.out.println(JSON.toJSONString(doctor));
		 
//		 Map map = JSON.parseObject(JSON.toJSONString(doctor), Map.class);
	}
}
