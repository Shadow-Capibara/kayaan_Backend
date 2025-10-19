-- Create password_reset_token table for forgot password functionality
CREATE TABLE IF NOT EXISTS password_reset_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    reset_code VARCHAR(6) NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Foreign key constraint to user table
    CONSTRAINT fk_password_reset_user 
        FOREIGN KEY (user_id) REFERENCES _user(id) 
        ON DELETE CASCADE,
    
    -- Index for faster lookups
    INDEX idx_reset_code (reset_code),
    INDEX idx_email (email),
    INDEX idx_expires_at (expires_at)
);

