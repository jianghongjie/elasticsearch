package com.dachen.elasticsearch.handler;

import java.util.List;
import java.util.Map;

public class SearchResponse {
    private int totalSize;
    private Map<String,List<String>>data;
    public int getTotalSize() {
      return totalSize;
    }

    public void setTotalSize(int totalSize) {
      this.totalSize = totalSize;
    }

    public Map<String, List<String>> getData() {
      return data;
    }

    public void setData(Map<String, List<String>> data) {
      this.data = data;
    }

    
}
