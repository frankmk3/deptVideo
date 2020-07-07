package com.dept.video.server.controller;

import com.dept.video.server.common.Constants;
import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.Response;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.model.Contact;
import com.dept.video.server.model.User;
import com.dept.video.server.service.ContactService;
import com.dept.video.server.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.dept.video.server.common.Constants.*;

/**
 * Controller class to manage Contacts information
 */
@Slf4j
@Controller
@RequestMapping("/contacts")
public class ContactController {

    private static final String LABEL = "Contact";
    private final ContactService contactService;
    private final UserService userService;

    private final MessagesUtility messagesUtility;

    private final Constants constants;

    @Autowired
    public ContactController(ContactService contactService, UserService userService, MessagesUtility messagesUtility, Constants constants) {
        this.contactService = contactService;
        this.userService = userService;
        this.messagesUtility = messagesUtility;
        this.constants = constants;
    }

    /**
     * Get the stored contacts information
     *
     * @return a paged response with status 200 and the resultant entity collection.
     * In case of bad query parameter, 400. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get contacts", tags = LABEL)
    @ApiImplicitParams(value = {@ApiImplicitParam(name = AUTHORIZATION_TOKEN,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity getContacts(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "order", required = false) final String[] order,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "page", required = false, defaultValue = "-1") final int page,
            @RequestParam(name = "size", required = false, defaultValue = "-1") final int size
    ) {
        ResponseEntity responseEntity;
        final int requestPage = page < 0 ? constants.getPaginatorPage() : page;
        final int requestSize = size <= 0 ? constants.getPaginatorSize() : size;
        final Pageable pageable = PageRequest.of(requestPage, requestSize);

        try {
            final PaginatedResponse<Contact> contacts = contactService.getAll(
                    q,
                    Optional.ofNullable(order),
                    Optional.ofNullable(fields),
                    pageable);
            responseEntity = new ResponseEntity(contacts, HttpStatus.OK);
        } catch (QueryException e) {
            responseEntity = new ResponseEntity(Response.builder().
                    status(HttpStatus.BAD_REQUEST.value()).
                    message(messagesUtility.getMessage("bad.request")).build(), HttpStatus.BAD_REQUEST);
            log.error(e.getMessage(), e);
        }
        return responseEntity;
    }

    /**
     * Get a contact by contact id
     *
     * @return a response with status 200 and the resultant entity. In case of bad contact id, 404. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get a contact", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/{contactId:.+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getContact(@PathVariable(name = "contactId") final String contactId) {
        ResponseEntity responseEntity;
        final Contact contact = contactService.getById(contactId);
        if (contact != null) {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            final User user = userService.getById(auth.getName());
            if (user.isAdmin() || user.getId().equals(contact.getUserId())) {
                responseEntity = new ResponseEntity(contact, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity(Response.builder().
                        status(HttpStatus.UNAUTHORIZED.value()).
                        message(messagesUtility.getMessage("wrong.input.data")).build()
                        , HttpStatus.UNAUTHORIZED);
            }
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * Create contact information
     *
     * @return a response with status 201 and the resultant entity. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Create contact", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createContact(@RequestBody Contact contact) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        contact.setUserId(auth.getName());
        return new ResponseEntity(contactService.create(contact), HttpStatus.CREATED);
    }

    /**
     * Update contact information
     *
     * @return a response with status 200 and the resultant entity. In case wrong contact id, 404. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Update contact", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @PutMapping("/{contactId:.+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateContact(@PathVariable(name = "contactId") final String contactId, @RequestBody Contact contact) {
        ResponseEntity responseEntity;
        final Contact current = contactService.getById(contactId);
        if (current != null) {
            responseEntity = new ResponseEntity(contactService.update(contact), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("wrong.input.data")).build()
                    , HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}