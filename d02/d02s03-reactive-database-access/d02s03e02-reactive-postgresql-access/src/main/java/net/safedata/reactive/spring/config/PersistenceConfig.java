package net.safedata.reactive.spring.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories("net.safedata.reactive.spring.domain.repository")
public class PersistenceConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.datasource.name}")
    private String databaseName;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Override
    public PostgresqlConnectionFactory connectionFactory() {
        final PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                                                                                          .host("localhost")
                                                                                          .port(5432)
                                                                                          .database(databaseName)
                                                                                          .username(userName)
                                                                                          .password(password)
                                                                                          .build();

        return new PostgresqlConnectionFactory(config);
    }
}
