package pl.starchasers.mdpages

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer
import java.util.*
import javax.sql.DataSource


//@Configuration
//@EnableJpaRepositories(basePackages = ["pl.starchasers.mdpages"])
//@PropertySource("persistence-generic-entity.properties")
//@EnableTransactionManagement
//class TestConfiguration() {
//
//    fun sessionFactory(): LocalSessionFactoryBean {
//        val sessionFactory = LocalSessionFactoryBean()
//        sessionFactory.setDataSource(dataSource())
//        sessionFactory.setPackagesToScan("pl.starchasers.mdpages")
//        sessionFactory.hibernateProperties = hibernateProperties()
//        sessionFactory.afterPropertiesSet()
//        return sessionFactory
//    }
//
////    @Bean
//    fun dataSource(): DataSource {
////        val dataSource =
////        dataSource.setDriverClassName("org.h2.Driver")
////        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1")
////        dataSource.setUsername("sa")
////        dataSource.setPassword("sa")
////        return dataSource
//    val dataSourceConfig = HikariConfig()
//    dataSourceConfig.driverClassName = "org.h2.Driver"
//    dataSourceConfig.jdbcUrl = "jdbc:h2:mem:db"
//    dataSourceConfig.username = "sa"
//    dataSourceConfig.password = "sa"
//
//    return HikariDataSource(dataSourceConfig)
//
////        val dataSourceBuilder = DataSourceBuilder.create()
////        dataSourceBuilder.driverClassName("org.h2.Driver")
////        dataSourceBuilder.url("jdbc:h2:mem:db")
////        dataSourceBuilder.username("sa")
////        dataSourceBuilder.password("sa")
////        return dataSourceBuilder.build()
//    }
//
////    @Bean
//    fun transactionManager(): PlatformTransactionManager? {
//        val transactionManager = HibernateTransactionManager(sessionFactory().getObject()!!)
//        transactionManager.sessionFactory = sessionFactory().getObject()
//        return transactionManager
//    }
//
//    private fun hibernateProperties(): Properties {
//        val hibernateProperties = Properties()
//        hibernateProperties.setProperty(
//            "hibernate.hbm2ddl.auto", "create-drop"
//        )
//        hibernateProperties.setProperty(
//            "hibernate.dialect", "org.hibernate.dialect.H2Dialect"
//        )
//        return hibernateProperties
//    }
//
////    @Bean
////    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean? {
////        val em = LocalContainerEntityManagerFactoryBean()
////        em.dataSource = dataSource()
////        em.setPackagesToScan("pl.starchasers.mdpages")
////        val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
////        em.jpaVendorAdapter = vendorAdapter
////        em.setJpaProperties(hibernateProperties())
////        return em
////    }
//
////    @Bean
////    @Qualifier(value = "entityManager")
//    fun entityManager(entityManagerFactory: EntityManagerFactory): EntityManager? {
//        return entityManagerFactory.createEntityManager()
//    }
//
////    @Bean
//////    @Qualifier(value = "entityManager")
////    fun entityManager(sessionFactory: LocalSessionFactoryBean): EntityManager? {
////        return sessio
////    }
//}

//@TestConfiguration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = ["pl.starchasers.mdpages"],
//    transactionManagerRef = "jpaTransactionManager"
//)
//@EnableJpaAuditing
//@PropertySource(["classpath:persistence-postgresql.properties"])
//@ComponentScan("pl.starchasers.mdpages")
//class ApplicationConfig : TransactionManagementConfigurer {
////    @Autowired
////    private val env: Environment? = null
//
//    @Bean
//    fun entityManagerFactory(): LocalSessionFactoryBean {
//        val sessionFactory = LocalSessionFactoryBean()
//        sessionFactory.setDataSource(applicationDataSource())
//        sessionFactory.setPackagesToScan(*arrayOf("pl.starchasers.mdpages"))
//        sessionFactory.hibernateProperties = hibernateProperties()
//        return sessionFactory
//    }
//
////    @Bean
//    fun jpaEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
//        val emf = LocalContainerEntityManagerFactoryBean()
//        emf.dataSource = applicationDataSource()
//        emf.setPackagesToScan(*arrayOf("pl.starchasers.mdpages"))
//        val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
//        emf.jpaVendorAdapter = vendorAdapter
//        emf.setJpaProperties(hibernateProperties())
//        return emf
//    }
//
//    @Bean
//    fun applicationDataSource(): DataSource {
////        val dataSource = BasicDataSource()
////        dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("jdbc.driverClassName")))
////        dataSource.setUrl(Preconditions.checkNotNull(env.getProperty("jdbc.url")))
////        dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("jdbc.user")))
////        dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("jdbc.pass")))
////        return dataSource
//        val hikariConfig = HikariConfig()
//        hikariConfig.driverClassName = "org.mariadb.jdbc.Driver"
//        hikariConfig.jdbcUrl = "jdbc:mysql://localhost/mdpages-new"
//        hikariConfig.username = "root"
//        hikariConfig.password = ""
//
//        hikariConfig.maximumPoolSize = 5
//        hikariConfig.connectionTestQuery = "SELECT 1"
//        hikariConfig.poolName = "springHikariCP"
//
//        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true")
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250")
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048")
//        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true")
//
//        return HikariDataSource(hikariConfig)
//    }
//
//    @Bean
//    fun hibernateTransactionManager(): PlatformTransactionManager { // TODO: Really need this?
//        val transactionManager = HibernateTransactionManager()
//        transactionManager.sessionFactory = entityManagerFactory().getObject()
//        return transactionManager
//    }
//
//    @Bean
//    fun transactionManager(): PlatformTransactionManager { // TODO: Really need this?
//        val transactionManager =
//            JpaTransactionManager() // http://stackoverflow.com/questions/26562787/hibernateexception-couldnt-obtain-transaction-synchronized-session-for-current
//        transactionManager.entityManagerFactory = jpaEntityManagerFactory().getObject()
//        return transactionManager
//    }
//
////    @Bean
////    fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
////        return PersistenceExceptionTranslationPostProcessor()
////    }
//
//    private fun hibernateProperties(): Properties {
//        val hibernateProperties = Properties()
//        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create")
//        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect")
//        hibernateProperties.setProperty("hibernate.show_sql", "true")
//        hibernateProperties.setProperty("hibernate.format_sql", "true")
//        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
////        hibernateProperties.setProperty(
////            "hibernate.cache.region.factory_class",
////            "org.hibernate.cache.ehcache.EhCacheRegionFactory"
////        )
//        // Envers properties
////        hibernateProperties.setProperty(
////            "org.hibernate.envers.audit_table_suffix",
////            env.getProperty("envers.audit_table_suffix")
////        ) // TODO: Really need this?
//        return hibernateProperties
//    }
//
//    override fun annotationDrivenTransactionManager(): TransactionManager {
//        return transactionManager()
//    }
//}