import { inject, Injectable, signal } from '@angular/core';
import { Contact } from '../models/contact';
import { ApiContactService } from './api-contact-service';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  private api = inject(ApiContactService);

  // Signal exposed to components
  contacts = signal<Contact[]>([]);

  /**
   * Simulates fetching sorted/searched data from backend
   */
  refreshContacts(searchQuery: string = '', sortBy: string = 'name') {
    if (searchQuery.trim()) {
      this.api.searchContacts(searchQuery).subscribe(data => this.contacts.set(data));
    } else if (sortBy !== 'name') { // 'name' is default
      this.api.sortContacts(sortBy).subscribe(data => this.contacts.set(data));
    } else {
      this.api.getContacts().subscribe(data => this.contacts.set(data));
    }
  }

  addContact(contactData: { name: string, emails: string[] }) {
    // ID is 0 or null, backend generates it
    const newContact: Contact = { id: 0, name: contactData.name, emails: contactData.emails };

    this.api.addContact(newContact).subscribe(savedContact => {
      this.contacts.update(list => [...list, savedContact]);
    });
  }

  updateContact(id: number, contactData: { name: string, emails: string[] }) {
    const contactToUpdate: Contact = { id, name: contactData.name, emails: contactData.emails };
    this.api.editContact(contactToUpdate.id, contactToUpdate).subscribe(savedContact => {
      // Update the specific contact in the signal list
      this.contacts.update(list =>
        list.map(c => c.id === id ? savedContact : c)
      );
    });
  }

  deleteContact(id: number) {
    this.api.removeContact(id).subscribe(() => {
      // Remove the contact from the signal list
      this.contacts.update(list => list.filter(c => c.id !== id));
    });
  }
}