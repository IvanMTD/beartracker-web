package ru.beartrack.web.services;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.DeleteDocumentRequest;
import com.manticoresearch.client.model.InsertDocumentRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

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
        indexApi.insert(documentRequest);
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
                indexApi.replace(replaceRequest);
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
                indexApi.delete(deleteRequest);
            }
        }
    }

    /*@SneakyThrows
    public Flux<UUID> searchDocument(String tableName, String searchQuery){
        if(!searchQuery.equals("")){

        }else{
            return Flux.empty();
        }
    }*/

    @SneakyThrows
    private List<Object> sqlQuery(String command){
        return utilsApi.sql(command,true);
    }

    @SneakyThrows
    public void createIndex(String name) {
        String tableName = tablePrefix + "_" + name;
        String createIndexQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "uuid UUID," +
                        "title TEXT FULLTEXT," +
                        "notation TEXT FULLTEXT," +
                        "content TEXT FULLTEXT," +
                        "metaTitle TEXT FULLTEXT," +
                        "metaDescription TEXT FULLTEXT," +
                        "metaKeywords TEXT FULLTEXT," +
                        ") TYPE='rt';", tableName);
        utilsApi.sql(createIndexQuery, true);
    }
}
