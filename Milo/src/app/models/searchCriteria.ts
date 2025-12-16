export interface SearchCriteria {
  query?: string;
  from?: string;
  to?: string;
  subject?: string;
  hasAttachment?: boolean;
  priority?: number;
  day?: string;
  month?: string;
  year?: string;
}
