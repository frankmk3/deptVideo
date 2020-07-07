package com.dept.video.server.repository;

import com.dept.video.server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

    User findByIdAndEnabledIsTrue(String id);

    @Query("{'$and':[ {_id:'?0'},{'$or':[{'name':{$ne:null}}, {'enabled':true}]}]}")
    User findByIdAndEnabledIsTrueOrNameNotNull(String id);

    User findOneById(String id);

    User findOneByIdAndSource(String id, String source);

    @Query(value = "{'?0':{$exists: true}}", count = true)
    long countByClientId(String client);
}
