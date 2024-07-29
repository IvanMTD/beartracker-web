package ru.beartrack.web.services;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class ManticoreService {
    private final IndexApi indexApi;
    private final SearchApi searchApi;
    private final UtilsApi utilsApi;
    @Value("${manticore.prefix.table}")
    private String tablePrefix;

    @SneakyThrows
    public void addDocument(String tableName, Map<String,Object> document) {
        InsertDocumentRequest documentRequest = new InsertDocumentRequest();
        documentRequest.index(tablePrefix + "_" + tableName).doc(document);
        SuccessResponse successResponse = indexApi.insert(documentRequest);
        log.info("add document success response: {}",successResponse);
    }

    @SneakyThrows
    public void updateDocument(String tableName, Map<String,Object> document) {
        List<Object> queryResult = sqlQuery("select * from " + tablePrefix + "_" + tableName + " where match('@uuid " + document.get("uuid") + "')");
        if(queryResult.size() == 1){
            Object result = queryResult.get(0);
            Map<String, Object> mainSourceMap = (LinkedHashMap<String,Object>)result;
            List<Object> sourceList = (ArrayList<Object>) mainSourceMap.get("data");
            if(sourceList.size() == 1){
                Object data = sourceList.get(0);
                LinkedHashMap<String,Object> source =  (LinkedHashMap<String,Object>)data;
                long sourceId = Long.parseLong(source.get("id").toString());
                InsertDocumentRequest replaceRequest = new InsertDocumentRequest();
                replaceRequest.index(tablePrefix + "_" + tableName).id(sourceId).setDoc(document);
                SuccessResponse successResponse = indexApi.replace(replaceRequest);
                log.info("update document success response: {}",successResponse);
            }
        }
    }

    @SneakyThrows
    public void deleteDocument(String tableName, UUID uuid){
        List<Object> queryResult = sqlQuery("select * from " + tablePrefix + "_" + tableName + " where match('@uuid " + uuid + "')");
        if(queryResult.size() == 1){
            Object result = queryResult.get(0);
            Map<String, Object> mainSourceMap = (LinkedHashMap<String,Object>)result;
            List<Object> sourceList = (ArrayList<Object>) mainSourceMap.get("data");
            if(sourceList.size() == 1){
                Object data = sourceList.get(0);
                LinkedHashMap<String,Object> source =  (LinkedHashMap<String,Object>)data;
                long sourceId = Long.parseLong(source.get("id").toString());
                DeleteDocumentRequest deleteRequest = new DeleteDocumentRequest();
                deleteRequest.index(tablePrefix + "_" + tableName).setId(sourceId);
                DeleteResponse deleteResponse = indexApi.delete(deleteRequest);
                log.info("delete document delete response: {}", deleteResponse);
            }
        }
    }

    @SneakyThrows
    public Flux<UUID> searchDocument(String tableName, String searchQuery){
        if(!searchQuery.equals("")){
            log.info("incoming query is {}",searchQuery);
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setIndex(tablePrefix + "_" + tableName);
            QueryFilter queryFilter = new QueryFilter();
            queryFilter.setQueryString(searchQuery);
            searchRequest.setQuery(queryFilter);
            SearchResponse response = searchApi.search(searchRequest);
            List<Object> hits = response.getHits().getHits();
            return Flux.fromIterable(hits).flatMapSequential(hit -> {
                log.info("found hit: {}",hit);
                Map<String, Object> mainSource = (LinkedHashMap<String,Object>)hit;
                Map<String, Object> postMap = (LinkedHashMap<String,Object>)mainSource.get("_source");
                UUID uuid = UUID.fromString(postMap.get("uuid").toString());
                return Mono.just(uuid);
            });
        }else{
            return Flux.empty();
        }
    }

    @SneakyThrows
    private List<Object> sqlQuery(String command){
        return utilsApi.sql(command,true);
    }

    @SneakyThrows
    public void dropIndex(String name) {
        String tableName = tablePrefix + "_" + name;
        String dropTableQuery = String.format("DROP TABLE IF EXISTS %s", tableName);
        utilsApi.sql(dropTableQuery, true);
    }

    @SneakyThrows
    public void createIndex(String name) {
        String tableName = tablePrefix + "_" + name;
        String createIndexQuery =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid TEXT," +
                "title TEXT," +
                "notation TEXT," +
                "content TEXT," +
                "metaTitle TEXT," +
                "metaDescription TEXT," +
                "metaKeywords TEXT" +
                ") TYPE='rt' morphology='stem_enru, libstemmer_ru' html_strip = '1'";
        utilsApi.sql(createIndexQuery, true);
    }
}
