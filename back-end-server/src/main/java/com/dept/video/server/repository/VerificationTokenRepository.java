package com.dept.video.server.repository;

import com.dept.video.server.model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

    VerificationToken findByTokenAndType(String token, String type);

    VerificationToken findFirstByUserIdOrderByDateDesc(String userId);
}
