package com.dachen.elasticsearch.action;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

public class SearchAction extends AbstractAction<SearchResult>{
	 private boolean resultFields=false;
	 
	 private String query;
	 private List<Sort> sortList = new LinkedList<Sort>();

	 protected SearchAction(Builder builder) {
        super(builder);

        this.query = builder.query;
        this.sortList = builder.sortList;
        if( builder.resultFields!=null &&  builder.resultFields.size()>0){
        	this.resultFields = true;
        }
        setURI(buildURI());
     }

     public SearchResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
         return createNewElasticSearchResult(new SearchResult(gson), responseBody, statusCode, reasonPhrase, gson);
     }

     public String getIndex() {
         return this.indexName;
     }
 
     public String getType() {
         return this.typeName;
     }

     @Override
     protected String buildURI() {
         return super.buildURI() + "/_search";
     }

     @Override
	 public String getPathToResult() {
		 if(resultFields){
			 return "hits/hits/fields";
		 }
		 return "hits/hits/_source";
	 }

     @Override
     public String getRestMethodName() {
         return "POST";
     }

     @SuppressWarnings("unchecked")
     @Override
     public String getData(Gson gson) {
        String data;
        if (sortList.isEmpty()) {
            data = query;
        } else {
            List<Map<String, Object>> sortMaps = new ArrayList<Map<String, Object>>(sortList.size());
            for (Sort sort : sortList) {
                sortMaps.add(sort.toMap());
            }

            Map rootJson = gson.fromJson(query, Map.class);
            rootJson.put("sort", sortMaps);
            data = gson.toJson(rootJson);
        }
        return data;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(query)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        SearchAction rhs = (SearchAction) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .append(sortList, rhs.sortList)
                .isEquals();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<SearchAction, Builder> {
        private String query;
        
        private List<String> resultFields;
		 
        private List<Sort> sortList = new LinkedList<Sort>();

        public Builder(String query) {
            this.query = query;
        }

        public Builder setSearchType(String searchType) {
            return setParameter(Parameters.SEARCH_TYPE, searchType);
        }

        public Builder addSort(Sort sort) {
            sortList.add(sort);
            return this;
        }

        public Builder addSort(Collection<Sort> sorts) {
            sortList.addAll(sorts);
            return this;
        }
        public Builder resultFields(List<String> resultFields) {
			this.resultFields = resultFields;
			return this;
		}

        @Override
        public SearchAction build() {
            return new SearchAction(this);
        }
        
    }
}
