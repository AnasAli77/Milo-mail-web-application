export interface FilterRule {
  id?: number;
  criteriaType: 'SUBJECT' | 'SENDER';
  criteriaValue: string;
  actionType: 'MOVE_TO_FOLDER' | 'STAR' | 'MARK_READ';
  actionTarget?: string;
}
