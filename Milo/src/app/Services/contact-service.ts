import { Injectable, signal } from '@angular/core';
import { Contact } from '../models/contact';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  
  // Internal store to simulate backend database
  private _allContacts: Contact[] = [
    { id: 1, name: 'Alice Smith', emails: ['alice@example.com', 'alice.work@milo.com'] },
    { id: 2, name: 'Bob Jones', emails: ['bob@example.com'] },
    { id: 3, name: 'Charlie Day', emails: ['charlie@day.com'] }
  ];

  // Signal exposed to components
  contacts = signal<Contact[]>([...this._allContacts]);

  /**
   * Simulates fetching sorted/searched data from backend
   */
  refreshContacts(searchQuery: string = '', sortBy: string = 'name') {
    let result = [...this._allContacts];

    // 1. Backend Search Simulation
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      result = result.filter(c => 
        c.name.toLowerCase().includes(q) || 
        c.emails.some(email => email.toLowerCase().includes(q))
      );
    }

    // 2. Backend Sort Simulation
    result.sort((a, b) => {
      const fieldA = sortBy === 'name' ? a.name.toLowerCase() : a.emails[0].toLowerCase();
      const fieldB = sortBy === 'name' ? b.name.toLowerCase() : b.emails[0].toLowerCase();
      return fieldA.localeCompare(fieldB);
    });

    this.contacts.set(result);
  }

  addContact(contactData: {name: string, emails: string[]}) {
    const newContact: Contact = {
      id: Date.now(),
      name: contactData.name,
      emails: contactData.emails
    };
    this._allContacts.push(newContact);
    this.refreshContacts(); 
  }

  updateContact(id: number, contactData: {name: string, emails: string[]}) {
    this._allContacts = this._allContacts.map(c => 
      c.id === id ? { ...c, ...contactData } : c
    );
    this.refreshContacts();
  }

  deleteContact(id: number) {
    this._allContacts = this._allContacts.filter(c => c.id !== id);
    this.refreshContacts();
  }
}