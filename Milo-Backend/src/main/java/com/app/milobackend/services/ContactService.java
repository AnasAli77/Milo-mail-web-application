package com.app.milobackend.services;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Contact;
import com.app.milobackend.repositories.ContactRepo;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserRepo userRepo;

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null; // Or throw an exception
    }

    public Contact storeContact(Contact contact) {
        String userEmail = getCurrentUserEmail();
        ClientUser currentUser = userRepo.findByEmail(userEmail);
        contact.setUser(currentUser);
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
        return contactRepo.findByUserEmail(getCurrentUserEmail());
    }

    public void deleteAllContacts() {
        contactRepo.deleteAll();
    }
}
