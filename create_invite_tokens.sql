-- Create invite tokens for existing study groups
-- This script will generate invite codes for all existing groups that don't have invites

USE kayaan_db;

-- Function to generate random invite code (6 characters)
DELIMITER //
CREATE FUNCTION IF NOT EXISTS generate_invite_code() 
RETURNS VARCHAR(6)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE chars VARCHAR(36) DEFAULT 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    DECLARE result VARCHAR(6) DEFAULT '';
    DECLARE i INT DEFAULT 1;
    
    WHILE i <= 6 DO
        SET result = CONCAT(result, SUBSTRING(chars, FLOOR(1 + RAND() * 36), 1));
        SET i = i + 1;
    END WHILE;
    
    RETURN result;
END //
DELIMITER ;

-- Insert invite tokens for existing groups
INSERT INTO group_invite (
    group_id, 
    token, 
    invite_code,
    expires_at, 
    created_by, 
    created_at, 
    revoked, 
    is_active,
    max_uses,
    current_uses
)
SELECT 
    sg.groupid as group_id,
    generate_invite_code() as token,
    generate_invite_code() as invite_code,
    DATE_ADD(NOW(), INTERVAL 30 DAY) as expires_at,
    sg.owner_userid as created_by,
    NOW() as created_at,
    FALSE as revoked,
    TRUE as is_active,
    100 as max_uses,
    0 as current_uses
FROM study_group sg
WHERE sg.deleted_at IS NULL
AND NOT EXISTS (
    SELECT 1 FROM group_invite gi 
    WHERE gi.group_id = sg.groupid 
    AND gi.revoked = FALSE 
    AND gi.expires_at > NOW()
);

-- Show results
SELECT 
    'Created invite tokens for groups:' as message,
    COUNT(*) as total_invites_created
FROM group_invite 
WHERE created_at >= NOW() - INTERVAL 1 MINUTE;

-- Show all active invite codes
SELECT 
    gi.group_id,
    sg.name as group_name,
    gi.invite_code,
    gi.token,
    gi.expires_at,
    gi.created_at,
    gi.is_active
FROM group_invite gi
JOIN study_group sg ON gi.group_id = sg.groupid
WHERE gi.revoked = FALSE 
AND gi.expires_at > NOW()
AND gi.is_active = TRUE
ORDER BY gi.created_at DESC;

-- Clean up function
DROP FUNCTION IF EXISTS generate_invite_code;
