/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.configure;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.lang.Opt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@DependsOn("dataSource")
@AllArgsConstructor
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@EnableConfigurationProperties({FlywayProperties.class, DataSourceProperties.class})
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
@Slf4j
public class DatabaseFlywayMigration {
    private final FlywayProperties flywayProperties;
    private final DataSourceProperties dataSourceProperties;

    /**
     * The reason why hikariDataSource is used here is because Dinky uses a Druid connection pool, which can cause some flyway SQL to be intercepted during execution, resulting in automatic upgrade failure
     * @return dataSource
     */
    private DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(dataSourceProperties.getUrl());
        hikariDataSource.setUsername(dataSourceProperties.getUsername());
        hikariDataSource.setPassword(dataSourceProperties.getPassword());
        Opt.ofBlankAble(dataSourceProperties.getDriverClassName()).ifPresent(hikariDataSource::setDriverClassName);
        return hikariDataSource;
    }

    @Bean
    public Flyway flyway(
            ObjectProvider<JavaMigration> javaMigrations,
            ObjectProvider<Callback> callbacks,
            ObjectProvider<FlywayConfigurationCustomizer> fluentConfigurationCustomizers) {
        FluentConfiguration fluentConfiguration = Flyway.configure();
        fluentConfiguration.dataSource(dataSource());

        fluentConfiguration.javaMigrations(javaMigrations.stream().toArray(JavaMigration[]::new));

        fluentConfiguration.callbacks(callbacks.orderedStream().toArray(Callback[]::new));
        fluentConfigurationCustomizers
                .orderedStream()
                .forEach((customizer) -> customizer.customize(fluentConfiguration));

        configureProperties(fluentConfiguration, flywayProperties);
        Flyway flyway = fluentConfiguration.load();
        // use flyway to migrate database schema
        executeMigrate(flyway);
        return flyway;
    }

    /**
     * execute migrate method
     * @param flyway flyway
     */
    private static void executeMigrate(Flyway flyway) {
        try {
            log.info("===========[Initialize flyway start]============");
            flyway.migrate();
            log.info("===========[Initialize flyway successfully ]============");

        } catch (FlywayException e) {
            flyway.repair();
            log.error("===========[Failed to initialize flyway]============");
            throw e;
        } finally {
            HikariDataSource dataSource =
                    (HikariDataSource) flyway.getConfiguration().getDataSource();
            dataSource.close();
            log.info("===========[Close dataSource of flyway  successfully]============");
        }
    }

    private void configureProperties(FluentConfiguration configuration, FlywayProperties properties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        String[] locations = properties.getLocations().toArray(new String[0]);
        map.from(locations).to(configuration::locations);
        map.from(properties.getEncoding()).to(configuration::encoding);
        map.from(properties.getConnectRetries()).to(configuration::connectRetries);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getLockRetryCount()).to(configuration::lockRetryCount);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getDefaultSchema()).to(configuration::defaultSchema);
        map.from(properties.getSchemas()).as(StringUtils::toStringArray).to(configuration::schemas);
        map.from(properties.isCreateSchemas()).to(configuration::createSchemas);
        map.from(properties.getTable()).to(configuration::table);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getTablespace()).to(configuration::tablespace);
        map.from(properties.getBaselineDescription()).to(configuration::baselineDescription);
        map.from(properties.getBaselineVersion()).to(configuration::baselineVersion);
        map.from(properties.getInstalledBy()).to(configuration::installedBy);
        map.from(properties.getPlaceholders()).to(configuration::placeholders);
        map.from(properties.getPlaceholderPrefix()).to(configuration::placeholderPrefix);
        map.from(properties.getPlaceholderSuffix()).to(configuration::placeholderSuffix);
        map.from(properties.isPlaceholderReplacement()).to(configuration::placeholderReplacement);
        map.from(properties.getSqlMigrationPrefix()).to(configuration::sqlMigrationPrefix);
        map.from(properties.getSqlMigrationSuffixes())
                .as(StringUtils::toStringArray)
                .to(configuration::sqlMigrationSuffixes);
        map.from(properties.getSqlMigrationSeparator()).to(configuration::sqlMigrationSeparator);
        map.from(properties.getRepeatableSqlMigrationPrefix()).to(configuration::repeatableSqlMigrationPrefix);
        map.from(properties.getTarget()).to(configuration::target);
        map.from(properties.isBaselineOnMigrate()).to(configuration::baselineOnMigrate);
        map.from(properties.isCleanDisabled()).to(configuration::cleanDisabled);
        map.from(properties.isCleanOnValidationError()).to(configuration::cleanOnValidationError);
        map.from(properties.isGroup()).to(configuration::group);
        map.from(properties.isMixed()).to(configuration::mixed);
        map.from(properties.isOutOfOrder()).to(configuration::outOfOrder);
        map.from(properties.isSkipDefaultCallbacks()).to(configuration::skipDefaultCallbacks);
        map.from(properties.isSkipDefaultResolvers()).to(configuration::skipDefaultResolvers);
        map.from(properties.isValidateOnMigrate()).to(configuration::validateOnMigrate);
        map.from(properties.getInitSqls())
                .whenNot(CollectionUtils::isEmpty)
                .as((initSqls) -> StringUtils.collectionToDelimitedString(initSqls, "\n"))
                .to(configuration::initSql);
        // Pro properties
        map.from(properties.getBatch()).to(configuration::batch);
        map.from(properties.getDryRunOutput()).to(configuration::dryRunOutput);
        map.from(properties.getErrorOverrides()).to(configuration::errorOverrides);
        map.from(properties.getLicenseKey()).to(configuration::licenseKey);
        map.from(properties.getOracleSqlplus()).to(configuration::oracleSqlplus);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getOracleSqlplusWarn()).to(configuration::oracleSqlplusWarn);
        map.from(properties.getStream()).to(configuration::stream);
        map.from(properties.getUndoSqlMigrationPrefix()).to(configuration::undoSqlMigrationPrefix);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getCherryPick()).to(configuration::cherryPick);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getJdbcProperties()).whenNot(Map::isEmpty).to(configuration::jdbcProperties);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getOracleKerberosCacheFile()).to(configuration::oracleKerberosCacheFile);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getOutputQueryResults()).to(configuration::outputQueryResults);
        // No method reference for compatibility with Flyway 6.x
        map.from(properties.getSkipExecutingMigrations()).to(configuration::skipExecutingMigrations);
        // No method reference for compatibility with Flyway < 7.8
        map.from(properties.getIgnoreMigrationPatterns())
                .whenNot(List::isEmpty)
                .to((ignoreMigrationPatterns) ->
                        configuration.ignoreMigrationPatterns(ignoreMigrationPatterns.toArray(new String[0])));
        // No method reference for compatibility with Flyway version < 7.9
        map.from(properties.getDetectEncoding()).to(configuration::detectEncoding);
    }
}
