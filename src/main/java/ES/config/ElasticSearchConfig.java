package ES.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    // 获取yml中es的配置
    @Value("${spring.elasticsearch.ip}")
    String esIp;

    @Value("${spring.elasticsearch.port}")
    int esPort;

    @Value("${spring.elasticsearch.pool}")
    String esClusterPool;
    // 注册 rest高级客户端
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esIp,esPort,"http")
                )
        );
        return client;
    }
}

