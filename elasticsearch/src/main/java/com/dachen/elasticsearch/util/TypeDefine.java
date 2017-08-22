package com.dachen.elasticsearch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeDefine {
	
	public static class Constants{
		public static final String INDEX_HEALTH ="health";
		
		public static final String TYPE_GROUP ="group";
		public static final String TYPE_DOCTOR ="doctor";
		public static final String TYPE_DISEASE ="disease";
//		public static final String TYPE_MEDICINE ="medicine";
	}
	public static List<String> getIndexs(){
		List<String> indexList = new ArrayList<String>(1);
		indexList.add(Constants.INDEX_HEALTH);
		return indexList;
	}
	public static Map<String,String> getTypes(){
		Map<String,String>types=new HashMap<String,String>();
		types.put(Constants.TYPE_GROUP, Constants.INDEX_HEALTH);
		types.put(Constants.TYPE_DOCTOR, Constants.INDEX_HEALTH);
		types.put(Constants.TYPE_DISEASE, Constants.INDEX_HEALTH);
	    return types;
	}
	
	public static String getIndexMapping(String type){
	   return FileUtils.getJson(type+".json");
	}
	
	public static String  getSearchIndex(){
		return Constants.INDEX_HEALTH;
	}
	public static String[] getSearchTypes(){
		return new String[]{Constants.TYPE_DOCTOR,Constants.TYPE_GROUP};
	}
	public static List<String> getSearchFields(String...types){
		Set<String>fields = new HashSet<String>(8);
		for(String type:types){
			 if(Constants.TYPE_GROUP.equalsIgnoreCase(type)){
				 fields.add("name");
				 fields.add("expertise.diseaseName");
				 fields.add("expertise.diseaseAlias");
				 fields.add("expertise.diseaseRemark");
//				 fields.add("introduction");
			 }else if(Constants.TYPE_DOCTOR.equalsIgnoreCase(type)){
				 fields.add("name");
				 fields.add("expertise.diseaseName");
				 fields.add("expertise.diseaseAlias");
				 fields.add("expertise.diseaseRemark");
				 fields.add("skill");
//				 fields.add("introduction");
//				 fields.add("title");
//				 fields.add("hospital");
				 fields.add("departments");
			 }else if(Constants.TYPE_DISEASE.equalsIgnoreCase(type)){
				 fields.add("diseaseName");
				 fields.add("diseaseAlias");
				 fields.add("diseaseRemark");
			 }else{
//				 FIELDS.ADD("TITLE");
//				 FIELDS.ADD("BODY");
			 }
		 }
		 return new ArrayList<String>(fields);
	 }
}
