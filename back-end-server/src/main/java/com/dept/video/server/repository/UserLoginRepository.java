package com.dept.video.server.repository;

import com.dept.video.server.model.UserLogin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserLoginRepository extends MongoRepository<UserLogin, String> {

    long countByUserIdAndFingerPrint_Hash(String userId, String hash);
}
