package ES.Common;

import ES.Document.ConceptDoc;
import ES.Document.InstitutionDoc;
import ES.Document.VenueDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.IDN;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Service
public class EsUtileService {

    @Autowired
    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("116.204.69.134", 9200, "http")));

    /**
     * 创建索引
     *
     */
    public boolean createIndex(String indexName) {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            //log.info("创建索引 response 值为： {}", response.toString());
            System.out.println("create index:"+indexName);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断索引是否存在
     *
     */
    public boolean existIndex(String indexName) {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除索引
     *
     */
    public boolean deleteIndex(String indexName) {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            //log.info("删除索引{}，返回结果为{}", indexName, delete.isAcknowledged());
            return delete.isAcknowledged();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据id删除文档
     *
     */
    public boolean deleteDocById(String indexName, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            //log.info("删除索引{}中id为{}的文档，返回结果为{}", indexName, id, deleteResponse.status().toString());
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 批量插入数据
     *
     */
    public boolean multiAddDoc(String indexName, List<JSONObject> list) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            list.forEach(doc -> {
                String source = JSON.toJSONString(doc);
                IndexRequest indexRequest = new IndexRequest(indexName);
                indexRequest.source(source, XContentType.JSON);
                bulkRequest.add(indexRequest);
            });
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            //log.info("向索引{}中批量插入数据的结果为{}", indexName, !bulkResponse.hasFailures());
            return !bulkResponse.hasFailures();
        }catch (Exception e) {
            //log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 更新文档
     *
     */
    public boolean updateDoc(String indexName, String docId, JSONObject jsonObject) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, docId).doc(JSON.toJSONString(jsonObject), XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            int total = updateResponse.getShardInfo().getTotal();
            //log.info("更新文档的影响数量为{}",total);
            return total > 0;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据id查询文档
     */
    public JSONObject queryDocById(String indexName, String docId) {
        JSONObject jsonObject = new JSONObject();
        try {
            GetRequest getRequest = new GetRequest(indexName, docId);
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            jsonObject = (JSONObject) JSONObject.toJSON(getResponse.getSource());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通用条件查询，map类型的参数都为空时，默认查询全部
     *
     */
    public PageResult<JSONObject> conditionSearch(String indexName, Integer pageNum, Integer pageSize, String highName, Map<String, Object> andMap, Map<String, Object> orMap, Map<String, Object> dimAndMap, Map<String, Object> dimOrMap) throws IOException, IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 索引不存在时不报错
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        //构造搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = buildMultiQuery(andMap, orMap, dimAndMap, dimOrMap, null, null);
        sourceBuilder.query(boolQueryBuilder);
        //高亮处理
        if (!StringUtils.isEmpty(highName)) {
            buildHighlight(sourceBuilder, highName);
        }
        //分页处理
        buildPageLimit(sourceBuilder, pageNum, pageSize);
        //超时设置
        sourceBuilder.timeout(TimeValue.timeValueSeconds(60));
        searchRequest.source(sourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        List<JSONObject> resultList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            //原始查询结果数据
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //高亮处理
            if (!StringUtils.isEmpty(highName)) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightField = highlightFields.get(highName);
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    StringBuilder value = new StringBuilder();
                    for (Text text : fragments) {
                        value.append(text);
                    }
                    sourceAsMap.put(highName, value.toString());
                }
            }
            JSONObject jsonObject =  JSONObject.parseObject(JSONObject.toJSONString(sourceAsMap));
            resultList.add(jsonObject);
        }

        long total = searchHits.getTotalHits().value;
        PageResult<JSONObject> pageResult = new PageResult<>();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);
        pageResult.setList(resultList);
        pageResult.setTotalPage(total==0?0: (int) (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1));

        return pageResult;
    }

    /**
     * 高级搜索，map类型的参数都为空时，默认查询全部
     *
     */
    public PageResult<JSONObject> advancedSearch(String indexName, Integer pageNum, Integer pageSize, String highName, Map<String, Object> andMap, Map<String, Object> orMap, Map<String, Object> notMap, Map<String, Object> dimAndMap, Map<String, Object> dimOrMap, Map<String, Object> dimNotMap,Timestamp from,Timestamp to) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 索引不存在时不报错
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        //构造搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = buildMultiQuery(andMap, orMap, notMap, dimAndMap, dimOrMap, dimNotMap);
        //构造时间限制
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("Pdate").from(from).to(to));
        sourceBuilder.query(boolQueryBuilder);
        //高亮处理
        if (!StringUtils.isEmpty(highName)) {
            buildHighlight(sourceBuilder, highName);
        }
        //分页处理
        buildPageLimit(sourceBuilder, pageNum, pageSize);
        //超时设置
        sourceBuilder.timeout(TimeValue.timeValueSeconds(60));
        searchRequest.source(sourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        List<JSONObject> resultList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            //原始查询结果数据
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //高亮处理
            if (!StringUtils.isEmpty(highName)) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightField = highlightFields.get(highName);
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    StringBuilder value = new StringBuilder();
                    for (Text text : fragments) {
                        value.append(text);
                    }
                    sourceAsMap.put(highName, value.toString());
                }
            }
            JSONObject jsonObject =  JSONObject.parseObject(JSONObject.toJSONString(sourceAsMap));
            resultList.add(jsonObject);
        }

        long total = searchHits.getTotalHits().value;
        PageResult<JSONObject> pageResult = new PageResult<>();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);
        pageResult.setList(resultList);
        pageResult.setTotalPage(total==0?0: (int) (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1));

        return pageResult;
    }

    /**
     * 默认搜索，map类型的参数都为空时，默认查询全部
     *
     */
    public PageResult<JSONObject> defaultSearch(String indexName, Integer pageNum, Integer pageSize, String highName, Map<String, Object> andMap, Map<String, Object> orMap, Map<String, Object> notMap, Map<String, Object> dimAndMap, Map<String, Object> dimOrMap, Map<String, Object> dimNotMap, Timestamp from,Timestamp to) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        // 索引不存在时不报错
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        //构造搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = buildMultiQuery(andMap, orMap, notMap, dimAndMap, dimOrMap, dimNotMap);
        //构造时间限制
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("Pdate").from(from).to(to));

        sourceBuilder.query(boolQueryBuilder);
        //高亮处理
        if (!StringUtils.isEmpty(highName)) {
            buildHighlight(sourceBuilder, highName);
        }
        //分页处理
        buildPageLimit(sourceBuilder, pageNum, pageSize);
        //超时设置
        sourceBuilder.timeout(TimeValue.timeValueSeconds(60));
        searchRequest.source(sourceBuilder);

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        List<JSONObject> resultList = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            //原始查询结果数据
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //高亮处理
            if (!StringUtils.isEmpty(highName)) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightField = highlightFields.get(highName);
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    StringBuilder value = new StringBuilder();
                    for (Text text : fragments) {
                        value.append(text);
                    }
                    sourceAsMap.put(highName, value.toString());
                }
            }
            JSONObject jsonObject =  JSONObject.parseObject(JSONObject.toJSONString(sourceAsMap));
            resultList.add(jsonObject);
        }

        long total = searchHits.getTotalHits().value;
        PageResult<JSONObject> pageResult = new PageResult<>();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);
        pageResult.setList(resultList);
        pageResult.setTotalPage(total==0?0: (int) (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1));

        return pageResult;
    }

    /**
     * 构造多条件查询
     *
     */
    public BoolQueryBuilder buildMultiQuery(Map<String, Object> andMap, Map<String, Object> orMap, Map<String, Object> notMap, Map<String, Object> dimAndMap, Map<String, Object> dimOrMap, Map<String, Object> dimNotMap) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //该值为true时搜索全部
        boolean searchAllFlag = true;
        //精确查询，and
        if (!CollectionUtils.isEmpty(andMap)) {
            for (Map.Entry<String, Object> entry : andMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(matchQueryBuilder);
            }
            searchAllFlag = false;
        }
        //精确查询，or
        if (!CollectionUtils.isEmpty(orMap)) {
            for (Map.Entry<String, Object> entry : orMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.should(matchQueryBuilder);
            }
            searchAllFlag = false;
        }
        //精确查询，not
        if (!CollectionUtils.isEmpty(notMap)) {
            for (Map.Entry<String, Object> entry : notMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.mustNot(matchQueryBuilder);
            }
            searchAllFlag = false;
        }
        //模糊查询，and
        if (!CollectionUtils.isEmpty(dimAndMap)) {
            for (Map.Entry<String, Object> entry : dimAndMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey()+".keyword", "*" + entry.getValue() + "*");
                boolQueryBuilder.must(wildcardQueryBuilder);
            }
            searchAllFlag = false;
        }
        //模糊查询，or
        if (!CollectionUtils.isEmpty(dimOrMap)) {
            for (Map.Entry<String, Object> entry : dimOrMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey()+".keyword", "*" + entry.getValue() + "*");
                boolQueryBuilder.should(wildcardQueryBuilder);
            }
            searchAllFlag = false;
        }
        //模糊查询，not
        if (!CollectionUtils.isEmpty(dimOrMap)) {
            for (Map.Entry<String, Object> entry : dimNotMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey()+".keyword", "*" + entry.getValue() + "*");
                boolQueryBuilder.mustNot(wildcardQueryBuilder);
            }
            searchAllFlag = false;
        }
        if (searchAllFlag) {
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            boolQueryBuilder.must(matchAllQueryBuilder);
        }

        return boolQueryBuilder;
    }

    /**
     * 构建高亮字段
     *
     */
    public void buildHighlight(SearchSourceBuilder sourceBuilder, String highName) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field(highName);
        //多个高亮显示
        highlightBuilder.requireFieldMatch(false);
        //高亮标签前缀
        highlightBuilder.preTags("<span style='color:red'>");
        //高亮标签后缀
        highlightBuilder.postTags("</span>");

        sourceBuilder.highlighter(highlightBuilder);
    }

    /**
     * 构造分页
     */
    public void buildPageLimit(SearchSourceBuilder sourceBuilder, Integer pageNum, Integer pageSize) {
        if (sourceBuilder!=null && !StringUtils.isEmpty(pageNum) && !StringUtils.isEmpty(pageSize)) {
            sourceBuilder.from(pageSize * (pageNum-1) );
            sourceBuilder.size(pageSize);
        }
    }

    public void addDoc(String indexName, WorkDoc workDoc) {
        IndexRequest request = new IndexRequest(indexName).id(workDoc.getPID()).source(JSONObject.toJSONString(workDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            System.out.println("add doc "+workDoc.getPID()+" success.");
        } catch (IOException e) {
//            System.out.println("add doc "+workDoc.getPID()+" failed.");
            System.out.println(e);
        }
    }

    public void addDoc(String indexName, InstitutionDoc institutionDoc) {
        IndexRequest request = new IndexRequest(indexName).id(institutionDoc.getIID()).source(JSONObject.toJSONString(institutionDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            System.out.println("add doc "+institutionDoc.getIID()+" success.");
        } catch (IOException e) {
//            System.out.println("add doc "+institutionDoc.getIID()+" failed.");
            System.out.println(e);
        }
    }

    /**
     * 将新的概念放入ES中。
     * @param indexName 索引名称，应该为concepts。
     * @param conceptDoc 概念Doc实例。
     */
    public void addDoc(String indexName, ConceptDoc conceptDoc) {
        IndexRequest request = new IndexRequest(indexName)
                .id(conceptDoc.getCID())
                .source(JSONObject.toJSONString(conceptDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Successfully added new concept: " + conceptDoc.getCID() + " with name " + conceptDoc.getCname());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将新的出版物放入ES中。
     * @param indexName 索引名称，应该为concepts。
     * @param venueDoc 出版物Doc实例。
     */
    public void addDoc(String indexName, VenueDoc venueDoc) {
        IndexRequest request = new IndexRequest(indexName)
                .id(venueDoc.getVID())
                .source(JSONObject.toJSONString(venueDoc), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Successfully added new concept: " + venueDoc.getVID() + " with name " + venueDoc.getVfullname());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDuplicateDoc(String indexName, String IDName, String ID) {
        CountRequest countRequest = new CountRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(IDName, ID));
        countRequest.source(sourceBuilder);
        try {
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            int count = (int) countResponse.getCount();
            System.out.println("Duplicate check: " + count);
            if (count > 1) {
                // 清除多余的Doc
                DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
                deleteByQueryRequest.setMaxDocs(count - 1);
                deleteByQueryRequest.setQuery(QueryBuilders.matchQuery(IDName, ID));
                BulkByScrollResponse bulkResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest,
                        RequestOptions.DEFAULT);
                System.out.println(bulkResponse.getTotal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Object> getVInfo(String vid) {
        ArrayList<Object> ret = new ArrayList<>();
        JSONObject venueDoc = queryDocById("venue", vid);
        JSONArray vabbrnames = venueDoc.getJSONArray("valtername");
        String s = vabbrnames.getString(0);
        for(int i=0; i<vabbrnames.size(); i++)
        {
            String temp = vabbrnames.getString(i);
            if(temp.length()<s.length())
            {
                s = temp;
            }
        }
        if(s.length()<=10)
        {
            ret.add(s);
        }
        else
        {
            ret.add(null);
        }
        JSONArray vcitesAccumulate = venueDoc.getJSONArray("vcitesAccumulate");
        int citeNum = -1;
        if(vcitesAccumulate.size()>=3)
        {
            citeNum = Integer.parseInt(vcitesAccumulate.getString(2));
        }
        else if(vcitesAccumulate.size()>=2)
        {
            citeNum = Integer.parseInt(vcitesAccumulate.getString(1));
        }
        else if(vcitesAccumulate.size()>=1)
        {
            citeNum = Integer.parseInt(vcitesAccumulate.getString(0));
        }
        ret.add(citeNum);
        return ret;
    }
}


