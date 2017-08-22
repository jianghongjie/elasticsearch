package com.dachen.elasticsearch.action;

import io.searchbox.client.JestResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.RootAggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author cihat keser
 */
public class SearchResult extends JestResult {

    public static final String EXPLANATION_KEY = "_explanation";
    public static final String HIGHLIGHT_KEY = "highlight";
    public static final String SORT_KEY = "sort";
    public static final String[] PATH_TO_TOTAL = "hits/total".split("/");
    public static final String[] PATH_TO_MAX_SCORE = "hits/max_score".split("/");

    public SearchResult(SearchResult searchResult) {
        super(searchResult);
    }

    public SearchResult(Gson gson) {
        super(gson);
    }
    
    @Override
    protected <T> T createSourceObject(JsonElement source, Class<T> type) {
        T obj = null;
        try {
        	String jsonSource = source.toString();
//        	if(type == String.class){
//        		return jsonSource;
//        	}
        	obj = JSON.parseObject(jsonSource, type);
        }
        catch(Exception e){
        	
        }
        return obj;
    }
    
    public <T> Hit<T, Void> getFirstHit(Class<T> sourceType) {
        return getFirstHit(sourceType, Void.class);
    }

    public <T, K> Hit<T, K> getFirstHit(Class<T> sourceType, Class<K> explanationType) {
        Hit<T, K> hit = null;

        List<Hit<T, K>> hits = getHits(sourceType, explanationType, true);
        if (!hits.isEmpty()) {
            hit = hits.get(0);
        }

        return hit;
    }

    public <T> List<Hit<T, Void>> getHits(Class<T> sourceType) {
        return getHits(sourceType, Void.class);
    }

    public <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType) {
        return getHits(sourceType, explanationType, false);
    }

    protected <T, K> List<Hit<T, K>> getHits(Class<T> sourceType, Class<K> explanationType, boolean returnSingle) {
        List<Hit<T, K>> sourceList = new ArrayList<Hit<T, K>>();

        if (jsonObject != null) {
            String[] keys = getKeys();
            if (keys != null) { // keys would never be null in a standard search scenario (i.e.: unless search class is overwritten)
                String sourceKey = keys[keys.length - 1];
                JsonElement obj = jsonObject.get(keys[0]);
                for (int i = 1; i < keys.length - 1; i++) {
                    obj = ((JsonObject) obj).get(keys[i]);
                }

                if (obj.isJsonObject()) {
                    sourceList.add(extractHit(sourceType, explanationType, obj, sourceKey));
                } else if (obj.isJsonArray()) {
                    for (JsonElement hitElement : obj.getAsJsonArray()) {
                        sourceList.add(extractHit(sourceType, explanationType, hitElement, sourceKey));
                        if (returnSingle) break;
                    }
                }
            }
        }

        return sourceList;
    }

    protected <T, K> Hit<T, K> extractHit(Class<T> sourceType, Class<K> explanationType, JsonElement hitElement, String sourceKey) {
        Hit<T, K> hit = null;

        if (hitElement.isJsonObject()) {
            JsonObject hitObject = hitElement.getAsJsonObject();

            JsonElement id = hitObject.get("_id");
            String index = hitObject.get("_index").getAsString();
            String type = hitObject.get("_type").getAsString();
            
            Double score = null;
            if (hitObject.has("_score") && !hitObject.get("_score").isJsonNull()) {
            	score = hitObject.get("_score").getAsDouble();
            }
            JsonElement explanation = hitObject.get(EXPLANATION_KEY);
            Map<String, List<String>> highlight = extractHighlight(hitObject.getAsJsonObject(HIGHLIGHT_KEY));
            List<String> sort = extractSort(hitObject.getAsJsonArray(SORT_KEY));
            
            JsonObject source = hitObject.getAsJsonObject(sourceKey);
            hit = new Hit<T, K>(
                    sourceType,
                    source,
                    explanationType,
                    explanation,
                    highlight,
                    sort,
                    index,
                    type,
                    score,
                    id.getAsString()
            );
        }
        return hit;
    }

    protected List<String> extractSort(JsonArray sort) {
        if (sort == null) {
            return null;
        }

        List<String> retval = new ArrayList<String>(sort.size());
        for (JsonElement sortValue : sort) {
            retval.add(sortValue.isJsonNull() ? "" : sortValue.getAsString());
        }
        return retval;
    }

    protected Map<String, List<String>> extractHighlight(JsonObject highlight) {
        Map<String, List<String>> retval = null;

        if (highlight != null) {
            Set<Map.Entry<String, JsonElement>> highlightSet = highlight.entrySet();
            retval = new HashMap<String, List<String>>(highlightSet.size());

            for (Map.Entry<String, JsonElement> entry : highlightSet) {
                List<String> fragments = new ArrayList<String>();
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    fragments.add(element.getAsString());
                }
                retval.put(entry.getKey(), fragments);
            }
        }

        return retval;
    }

    public Integer getTotal() {
        Integer total = null;
        JsonElement obj = getPath(PATH_TO_TOTAL);
        if (obj != null) total = obj.getAsInt();
        return total;
    }

    public Float getMaxScore() {
        Float maxScore = null;
        JsonElement obj = getPath(PATH_TO_MAX_SCORE);
        if (obj != null) maxScore = obj.getAsFloat();
        return maxScore;
    }

    protected JsonElement getPath(String[] path) {
        JsonElement retval = null;
        if (jsonObject != null) {
            JsonElement obj = jsonObject;
            for (String component : path) {
                if (obj == null) break;
                obj = ((JsonObject) obj).get(component);
            }
            retval = obj;
        }
        return retval;
    }

    public MetricAggregation getAggregations() {
        final String rootAggrgationName = "aggs";
        if (jsonObject == null) return new RootAggregation(rootAggrgationName, new JsonObject());
        if (jsonObject.has("aggregations"))
            return new RootAggregation(rootAggrgationName, jsonObject.getAsJsonObject("aggregations"));
        if (jsonObject.has("aggs")) return new RootAggregation(rootAggrgationName, jsonObject.getAsJsonObject("aggs"));

        return new RootAggregation(rootAggrgationName, new JsonObject());
    }

    /**
     * Immutable class representing a search hit.
     *
     * @param <T> type of source
     * @param <K> type of explanation
     * @author cihat keser
     */
    public class Hit<T, K> {

        public final T source;
        public final K explanation;
        public final Map<String, List<String>> highlight;
        public final List<String> sort;
        public final String index;
        public final String type;
        public final String _id;
        public final Double score;

        public Hit(Class<T> sourceType, JsonElement source, Class<K> explanationType, JsonElement explanation,
                   Map<String, List<String>> highlight, List<String> sort, String index, String type, Double score,String _id) {
            if (source == null) {
                this.source = null;
//            } else if(sourceType==String.class){
//                this.source = source.toString();
            } else {
                this.source = createSourceObject(source, sourceType);
            }
            if (explanation == null) {
                this.explanation = null;
            } else {
                this.explanation = createSourceObject(explanation, explanationType);
            }
            this.highlight = highlight;
            this.sort = sort;

            this.index = index;
            this.type = type;
            this.score = score;
            this._id=_id;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(source)
                    .append(explanation)
                    .append(highlight)
                    .append(sort)
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

            Hit rhs = (Hit) obj;
            return new EqualsBuilder()
                    .append(source, rhs.source)
                    .append(explanation, rhs.explanation)
                    .append(highlight, rhs.highlight)
                    .append(sort, rhs.sort)
                    .isEquals();
        }
    }

}
