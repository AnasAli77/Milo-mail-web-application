import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ContactService } from '../../Services/contact-service';
import { Contact } from '../../models/contact';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './contacts.html',
  styleUrl: './contacts.css'
})
export class Contacts implements OnInit {
  contactService = inject(ContactService);
  fb = inject(FormBuilder);

  // Search & Sort State
  searchTerm = '';
  sortBy = 'name'; // 'name' or 'email'

  // Selection State
  selectedContact = signal<Contact | null>(null);

  // Modal State
  isModalOpen = false;
  editingContactId: number | null = null;
  contactForm: FormGroup;

  constructor() {
    this.contactForm = this.fb.group({
      name: ['', Validators.required],
      emails: this.fb.array([])
    });
  }

  ngOnInit() {
    // Initial load
    this.contactService.refreshContacts();
  }

  get emailControls() {
    return (this.contactForm.get('emails') as FormArray).controls;
  }

  // --- Search & Sort Actions ---
  onSearch() {
    this.contactService.refreshContacts(this.searchTerm, this.sortBy);
  }

  onSortChange() {
    this.contactService.refreshContacts(this.searchTerm, this.sortBy);
  }

  selectContact(contact: Contact) {
    this.selectedContact.set(contact);
  }

  // --- Modal & Form Actions ---
  openAddModal() {
    this.editingContactId = null;
    this.contactForm.reset();
    
    // Clear FormArray and add one initial email field
    const emailArray = this.contactForm.get('emails') as FormArray;
    emailArray.clear();
    this.addEmailField();
    
    this.isModalOpen = true;
  }

  openEditModal(contact: Contact) {
    this.editingContactId = contact.id;
    
    // Populate FormArray
    const emailArray = this.contactForm.get('emails') as FormArray;
    emailArray.clear();
    contact.emails.forEach(email => {
      emailArray.push(this.fb.control(email, [Validators.required, Validators.email]));
    });

    this.contactForm.patchValue({
      name: contact.name
    });

    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  addEmailField() {
    const emails = this.contactForm.get('emails') as FormArray;
    emails.push(this.fb.control('', [Validators.required, Validators.email]));
  }

  removeEmailField(index: number) {
    const emails = this.contactForm.get('emails') as FormArray;
    if (emails.length > 1) {
      emails.removeAt(index);
    }
  }

  saveContact() {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }

    const formVal = this.contactForm.value;
    
    // Filter out empty emails just in case
    const cleanEmails = formVal.emails.filter((e: string) => !!e);

    if (this.editingContactId) {
      this.contactService.updateContact(this.editingContactId, {
        name: formVal.name,
        emails: cleanEmails
      });
    } else {
      this.contactService.addContact({
        name: formVal.name,
        emails: cleanEmails
      });
    }

    this.closeModal();
  }

  deleteContact(id: number) {
    if(confirm('Are you sure you want to delete this contact?')) {
      this.contactService.deleteContact(id);
    }
  }
}