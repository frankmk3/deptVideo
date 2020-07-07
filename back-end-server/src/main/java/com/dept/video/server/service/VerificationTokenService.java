package com.dept.video.server.service;

import com.dept.video.server.common.IDGeneratorUtility;
import com.dept.video.server.model.VerificationToken;
import com.dept.video.server.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken getById(String id) {
        return verificationTokenRepository.findById(id).orElse(null);
    }

    public VerificationToken create(VerificationToken verificationToken) {
        IDGeneratorUtility.generateIdIfMissing(verificationToken);
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getValidTokenByIdAndUserAndType(String token, String type) {
        VerificationToken token1 = verificationTokenRepository.findByTokenAndType(token, type);
        VerificationToken token2 = null;
        if (token1 != null) {
            token2 = verificationTokenRepository.findFirstByUserIdOrderByDateDesc(token1.getUserId());
        }
        return token2 != null && token1.getId().equals(token2.getId()) && token2.getExpiryDate().after(new Date()) ? token2 : null;
    }

}