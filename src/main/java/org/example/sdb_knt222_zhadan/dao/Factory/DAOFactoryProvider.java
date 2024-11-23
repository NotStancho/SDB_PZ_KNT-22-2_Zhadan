package org.example.sdb_knt222_zhadan.dao.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DAOFactoryProvider {

    private final MySQLDAOFactory mySQLDAOFactory;
    private final MongoDBDAOFactory mongoDBDAOFactory;

    @Autowired
    public DAOFactoryProvider(MySQLDAOFactory mySQLDAOFactory, MongoDBDAOFactory mongoDBDAOFactory) {
        this.mySQLDAOFactory = mySQLDAOFactory;
        this.mongoDBDAOFactory = mongoDBDAOFactory;
    }

    @Bean(name = "mySQLFactory")
    public DAOFactory getMySQLDAOFactory() {
        return mySQLDAOFactory;
    }

    @Bean(name = "mongoDBFactory")
    public DAOFactory getMongoDBDAOFactory() {
        return mongoDBDAOFactory;
    }
}
