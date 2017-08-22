package com.dachen.elasticsearch.action;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestResult;

import com.google.gson.Gson;

public class ExistsAction extends AbstractAction<JestResult>{

	protected ExistsAction(Builder builder) {
        super(builder);
        this.indexName = builder.index;
        this.typeName = builder.type;
        setURI(buildURI());
     }
	
	public JestResult createNewElasticSearchResult(String responseBody,
			int statusCode, String reasonPhrase, Gson gson) {
		return createNewElasticSearchResult(new JestResult(gson), responseBody, statusCode, reasonPhrase, gson);
	}

	@Override
	public String getRestMethodName() {
		return "HEAD";
	}
	public static class Builder extends AbstractAction.Builder<ExistsAction, Builder>{
		private String index;
        private String type;
        public Builder(){
        	
        }
        public Builder index(String defaultIndex) {
            this.index = defaultIndex;
            return this;
        }

        public Builder type(String defaultType) {
            this.type = defaultType;
            return this;
        }

		@Override
		public ExistsAction build() {
			return new ExistsAction(this);
		}
	}
}
