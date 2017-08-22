package com.dachen.elasticsearch.util;

import java.util.ArrayList;
import java.util.List;

import com.dachen.elasticsearch.model.BaseInfo;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsDoctor;
import com.dachen.elasticsearch.model.EsGroup;

public class DataFactory {
   /* public static List<String> getInitJsonData(){
        List<String> list = new ArrayList<String>();
        String data1  = JSON.toJSONString(new Medicine(1,"银花 感冒 颗粒","功能主治：银花感冒颗粒 ，头痛,清热，解表，利咽。"));
        String data2  = JSON.toJSONString(new Medicine(2,"感冒  止咳糖浆","功能主治：感冒止咳糖浆,解表清热，止咳化痰。"));
        String data3  = JSON.toJSONString(new Medicine(3,"感冒灵颗粒","功能主治：解热镇痛。头痛 ,清热。"));
        String data4  = JSON.toJSONString(new Medicine(4,"感冒  灵胶囊","功能主治：银花感冒颗粒 ，头痛,清热，解表，利咽。"));
        String data5  = JSON.toJSONString(new Medicine(5,"仁和 感冒 颗粒","功能主治：疏风清热，宣肺止咳,解表清热，止咳化痰。"));
        list.add(data1);
        list.add(data2);
        list.add(data3);
        list.add(data4);
        list.add(data5);
        return list;
    }*/
    
    public static List<? extends BaseInfo> getInitGroupData(){
        List<BaseInfo> list = new ArrayList<BaseInfo>();
        EsGroup group = new EsGroup("55e56d1fb522250b853ae87f");
        group.setBizId("55e56d1fb522250b853ae87f");
        group.setName("康哲医生集团");
//        group.setIntroduction("康哲市场部");
//        group.setWeight(1);
        
        group.addExpertise(new EsDiseaseType("全葡萄膜炎","")) 
        	 .addExpertise(new EsDiseaseType("职业性皮肤病",""))
        	 .addExpertise(new EsDiseaseType("二叶主动脉瓣",""));
        list.add(group);
        
        group = new EsGroup("56d3ab07b522252589d80950");
        group.setName("石义良集团");
//        group.setIntroduction("玄关健康是个年轻、有活力、理性带着一点闷骚的感性dsad");
//        group.setWeight(2);
        
        group.addExpertise(new EsDiseaseType("全感觉性障碍膜炎","")) 
        	 .addExpertise(new EsDiseaseType("职业性皮肤病","")) 
        	 .addExpertise(new EsDiseaseType("上消化道大出血",""))
        	 .addExpertise(new EsDiseaseType("平足症",""));
        list.add(group);
        return list;
    }
   
    public static List<? extends BaseInfo> getInitDoctorData(){
        List<BaseInfo> list = new ArrayList<BaseInfo>();
        
        List<EsDiseaseType>expertise = new ArrayList<EsDiseaseType>();
        expertise.add(new EsDiseaseType("全感觉性障碍膜炎",""));
        expertise.add(new EsDiseaseType("职业性皮肤病",""));
        expertise.add(new EsDiseaseType("上消化道大出血",""));
        expertise.add(new EsDiseaseType("平足症",""));
        
        EsDoctor doctor = new EsDoctor("101893");
        doctor.setName("doctor");
        doctor.setSkill("擅长医治各种二叶疾病");
//        doctor.setIntroduction("somebody");
//        doctor.setHospital("深圳市龙岗区第二人民医院(布吉镇人民医院)");
        doctor.setDepartments("呼吸内科");
//        doctor.setTitle("住院医师");
        doctor.setExpertise(expertise);
        list.add(doctor);
        return list;
    }
    
}
