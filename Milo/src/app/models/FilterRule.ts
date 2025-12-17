// export interface FilterRule {
//   id?: number,
//   criteria: Map<String, String>, // e.g., "subject" : "boss", "sender", "madara@gmail.com"

//   actionType: String,    // e.g., "MOVE", "STAR", "READ"
//   actionTarget?: String,  // e.g., Folder ID (for move), or null
// }

export interface FilterRule {
  id?: number;
  // Criteria
  sender?: string;
  recipient?: string;
  subject?: string;
  body?: string; // "Includes words"
  priority?: string;
  date?: string; // Format: YYYY-MM-DD
  hasAttachment?: boolean;

  // Actions
  moveToFolderId?: string; // ID of the folder to move to
  markAsRead?: boolean;
  star?: boolean;
}