package com.app.milobackend.controllers;

import com.app.milobackend.dtos.ContactDTO;
import com.app.milobackend.models.Contact;
import com.app.milobackend.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/add")
    public ResponseEntity<Contact> save(@RequestBody ContactDTO contact) {
        System.out.println("Received Contact: " + contact.toString());
        Contact incomingContact = new Contact();

        incomingContact.setName(contact.getName());
        incomingContact.setEmails(contact.getEmails());

        Contact newContact = contactService.storeContact(incomingContact);

        return ResponseEntity.ok(newContact);
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAll() {
        contactService.deleteAllContacts();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{contact_id}")
    public ResponseEntity<Void> delete(@PathVariable("contact_id") Long contact_id) {
        contactService.deleteContact(contact_id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get")
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ContactDTO contact) {
        Contact incomingContact = new Contact();
        incomingContact.setName(contact.getName());
        incomingContact.setEmails(contact.getEmails());

        Contact updatedContact;
        try {
            updatedContact = contactService.updateContact(id, incomingContact);
            return ResponseEntity.ok(updatedContact);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Resource Not Found");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
