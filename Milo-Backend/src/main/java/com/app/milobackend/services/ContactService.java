package com.app.milobackend.services;

import com.app.milobackend.models.Contact;
import com.app.milobackend.repositories.ContactRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null; // Or throw an exception
    }

    public Contact storeContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public void deleteContact(Long contact_id) {
        contactRepo.deleteById(contact_id);
    }

    public Contact updateContact(Long contact_id, Contact newContact) {
        Contact contact = contactRepo.findById(contact_id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setName(newContact.getName());
        contact.setEmails(newContact.getEmails());
        return contactRepo.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepo.findAll();
    }

    public void deleteAllContacts() {
        contactRepo.deleteAll();
    }
}
