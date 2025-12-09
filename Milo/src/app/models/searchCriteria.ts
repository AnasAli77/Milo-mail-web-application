// Definition for search criteria
export interface SearchCriteria {
  query?: string;       // General "Includes words"
  from?: string;
  to?: string;
  subject?: string;
  hasAttachment?: boolean;
  priority?: number; // Added
}