package com.app.milobackend.strategies;

import com.app.milobackend.models.Contact;

import java.util.List;

public interface ContactSortingStrategy {
    List<Contact> SortContacts(List<Contact> contacts);
}
