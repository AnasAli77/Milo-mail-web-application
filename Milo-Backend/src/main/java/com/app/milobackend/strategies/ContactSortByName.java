package com.app.milobackend.strategies;

import com.app.milobackend.models.Contact;
import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContactSortByName implements ContactSortingStrategy {
    public List<Contact> SortContacts(List<Contact> contacts) {
        List<Contact> copy = new ArrayList<Contact>(contacts);
        copy.sort(new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return copy;
    }
}
