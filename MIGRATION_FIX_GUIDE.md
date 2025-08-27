# Flyway Migration Fix Guide

## 🚨 **Current Issue**
The application is failing to start due to a Flyway migration validation error:
```
Detected failed migration to version 20250810.05 (study group).
Please remove any half-completed changes then run repair to fix the schema history.
```

## 🔧 **Immediate Solution (Temporary)**

### **Option 1: Use the Fix Script**
```cmd
fix_migration_issue.bat
```

### **Option 2: Manual Steps**
1. **Stop any running containers:**
   ```cmd
   docker-compose down
   ```

2. **Start fresh database:**
   ```cmd
   docker-compose up -d db
   ```

3. **Wait for database to be ready (15 seconds)**

4. **Flyway is already disabled** in `application.yml` (enabled: false)

5. **Test the application:**
   ```cmd
   mvnw.cmd spring-boot:run
   ```

## 🎯 **Root Cause Analysis**

The issue is caused by migration file reordering:
- **Backup shows**: `V20250810_05__group_message.sql`
- **Current shows**: `V20250810_05__study_group.sql`

This mismatch causes Flyway validation to fail because:
1. The database expects migration `20250810.05` to be `group_message`
2. But the current file is `study_group`
3. Flyway detects this inconsistency and fails

## 🛠️ **Permanent Solutions**

### **Solution 1: Fix Migration Order (Recommended)**
Reorder the migration files to match the expected sequence:

```bash
# Rename files to fix the order
V20250810_05__study_group.sql → V20250810_06__study_group.sql
V20250810_06__group_member.sql → V20250810_07__group_member.sql
V20250810_07__group_content.sql → V20250810_08__group_content.sql
V20250810_08__group_invite.sql → V20250810_09__group_invite.sql
V20250810_09__group_message.sql → V20250810_10__group_message.sql
```

### **Solution 2: Database Reset (Nuclear Option)**
If the above doesn't work, completely reset the database:

```cmd
# Stop containers
docker-compose down

# Remove database volume
docker volume rm kayaan_newclone1_kayaan_backend_db_data

# Start fresh
docker-compose up -d db

# Wait 15 seconds
timeout /t 15

# Re-enable Flyway in application.yml
# Change enabled: false to enabled: true

# Run application
mvnw.cmd spring-boot:run
```

### **Solution 3: Flyway Repair**
If you have Flyway CLI installed:

```cmd
# Connect to database and repair
flyway -url=jdbc:mysql://localhost:3307/kayaan_db -user=root -password=password repair
```

## 📋 **Migration File Order (Corrected)**

The correct migration order should be:
```
V20250810_00__user.sql
V20250810_01__ai_job.sql
V20250810_02__ai_draft.sql
V20250810_03__streak.sql
V20250810_04__streak_log.sql
V20250810_05__group_message.sql          ← This should be group_message, not study_group
V20250810_06__study_group.sql            ← This should be study_group
V20250810_07__group_member.sql
V20250810_08__group_content.sql
V20250810_09__group_invite.sql
V20250810_10__content_audit_log.sql
V20250810_11__update_group_invite.sql
V20250810_12__ai_generation_request.sql
V20250810_13__ai_prompt_template.sql
V20250810_14__ai_generated_content.sql
```

## 🚀 **After Fixing**

1. **Re-enable Flyway** in `application.yml`:
   ```yaml
   flyway:
     enabled: true  # Change from false to true
   ```

2. **Test the application:**
   ```cmd
   mvnw.cmd spring-boot:run
   ```

3. **Verify migrations** are applied correctly

## 🔍 **Troubleshooting**

### **If Still Having Issues:**
1. Check database connection (port 3307)
2. Verify MySQL is running
3. Check migration file syntax
4. Ensure no conflicting table names

### **Useful Commands:**
```cmd
# Check database status
docker ps

# Check database logs
docker-compose logs db

# Connect to database
mysql -h localhost -P 3307 -u root -ppassword kayaan_db

# Check Flyway schema history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## 📞 **Need Help?**

If you're still experiencing issues:
1. Check the application logs for specific error messages
2. Verify all migration files are syntactically correct
3. Ensure database permissions are correct
4. Consider using the database reset option for a clean start
