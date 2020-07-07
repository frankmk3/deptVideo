package com.dept.video.server.service;

import com.dept.video.server.common.IDGeneratorUtility;
import com.dept.video.server.common.QueryUtil;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.model.Contact;
import com.dept.video.server.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private final MongoOperations mongoOperations;

    private final QueryUtil queryUtil;

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(MongoOperations mongoOperations, QueryUtil queryUtil, ContactRepository contactRepository) {
        this.mongoOperations = mongoOperations;
        this.queryUtil = queryUtil;
        this.contactRepository = contactRepository;
    }

    public Contact getById(String id) {
        return contactRepository.findById(id).orElse(null);
    }


    public Contact create(Contact contact) {
        contact.setId(IDGeneratorUtility.generateId(Contact.class));
        return contactRepository.save(contact);
    }

    public Contact update(Contact contact) {
        return contactRepository.save(contact);
    }

    public PaginatedResponse<Contact> getAll(String q, Optional<String[]> orders, Optional<String> fields, Pageable pageable) throws QueryException {
        Query query = queryUtil.buildQuery(q, null, pageable, orders, fields);

        List<Contact> content = mongoOperations.find(query, Contact.class);
        long count = mongoOperations.count(query, Contact.class);

        return queryUtil.getPaginatedResponse(content, count, pageable);
    }


}