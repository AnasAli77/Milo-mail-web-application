package com.app.milobackend.services;

import com.app.milobackend.dtos.ContactDTO;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Contact;
import com.app.milobackend.repositories.ContactRepo;
import com.app.milobackend.repositories.UserRepo;
import com.app.milobackend.strategies.ContactSortByEmails;
import com.app.milobackend.strategies.ContactSortByName;
import com.app.milobackend.strategies.ContactSortWorker;
import com.app.milobackend.strategies.ContactSortingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return null;
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

    public List<ContactDTO> getSortedContacts(String sortBy) {

        List<Contact> contacts = contactRepo.findByUserEmail(getCurrentUserEmail());

        ContactSortWorker sortWorker = new ContactSortWorker();
        switch (sortBy) {
            case "name":
                sortWorker.setStrategy(new ContactSortByName());
                break;
            case "email":
                sortWorker.setStrategy(new ContactSortByEmails());
                break;
            default:
                return null;
        }
        List<Contact> sortedContacts = sortWorker.sort(contacts);
        return getContactDTOS(sortedContacts);
    }
    
    private boolean EmailsContain(String word, List<String> emails) {
        for (String email : emails) {
            if (email.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public List<ContactDTO> searchContacts(String search) {
        List<Contact> contacts = contactRepo.findByUserEmail(getCurrentUserEmail());

        List<Contact> contactsResults =  contacts.stream().filter(contact ->
            contact.getName().toLowerCase().contains(search.toLowerCase()) || EmailsContain(search, contact.getEmails())
        ).toList();

        return getContactDTOS(contactsResults);
    }

    private List<ContactDTO> getContactDTOS(List<Contact> contacts) {
        List<ContactDTO> contactDTOs = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setId(contact.getId());
            contactDTO.setName(contact.getName());
            contactDTO.setEmails(contact.getEmails());
            contactDTOs.add(contactDTO);
        }
        return contactDTOs;
    }
}
