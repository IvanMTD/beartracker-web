package ru.beartrack.web.configuration;

import com.manticoresearch.client.ApiClient;
import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ManticoreConfiguration {
    @Value("${manticore.server}")
    private String manticoreServer;

    @Bean
    public ApiClient manticoreApiClient() {
        ApiClient apiClient = com.manticoresearch.client.Configuration.getDefaultApiClient();
        apiClient.setBasePath(manticoreServer);
        return apiClient;
    }

    @Bean
    public IndexApi indexApi(){
        return new IndexApi(manticoreApiClient());
    }

    @Bean
    public SearchApi searchApi(){
        return new SearchApi(manticoreApiClient());
    }

    @Bean
    public UtilsApi utilsApi(){
        return new UtilsApi(manticoreApiClient());
    }
}
