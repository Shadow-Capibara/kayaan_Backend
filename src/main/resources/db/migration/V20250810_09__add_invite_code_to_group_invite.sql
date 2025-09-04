-- Migration: Add invite_code field to group_invite table
-- This field will be used for the new invite code system

ALTER TABLE group_invite 
ADD COLUMN invite_code VARCHAR(8) UNIQUE,
ADD COLUMN max_uses INT DEFAULT NULL,
ADD COLUMN current_uses INT DEFAULT 0,
ADD COLUMN created_by_ip VARCHAR(45) DEFAULT NULL,
ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Add index for better performance
CREATE INDEX idx_group_invite_invite_code ON group_invite(invite_code);
CREATE INDEX idx_group_invite_group_id_active ON group_invite(group_id, is_active);
