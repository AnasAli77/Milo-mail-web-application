package com.app.milobackend.strategies;

import com.app.milobackend.models.Contact;

import java.util.List;

public class ContactSortWorker {
    private ContactSortingStrategy strategy;

    public void setStrategy(ContactSortingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Contact> sort(List<Contact> contacts) {
        return this.strategy.SortContacts(contacts);
    }
}
