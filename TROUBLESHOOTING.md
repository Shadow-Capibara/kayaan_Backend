# Troubleshooting Guide

## ✅ **Compilation Issues Fixed**

### 1. **Class Name Mismatch Error**
- **Problem**: `class AIGenerationRateLimitService is public, should be declared in a file named AIGenerationRateLimitService.java`
- **Solution**: ✅ Fixed - Renamed file from `RateLimitService.java` to `AIGenerationRateLimitService.java`

### 2. **Lombok @Slf4j Not Working**
- **Problem**: `cannot find symbol: variable log`
- **Solution**: ✅ Fixed - Replaced `@Slf4j` with manual logger declaration
- **Additional Fix**: Updated `pom.xml` with proper Lombok annotation processor configuration

## 🚀 **How to Run the Application**

### **Option 1: Use the Fixed Batch File**
```cmd
run_fixed.bat
```

### **Option 2: Manual Commands (in a new terminal)**
```cmd
# Test compilation
mvnw.cmd clean compile

# If successful, run the application
mvnw.cmd spring-boot:run
```

### **Option 3: PowerShell (if available)**
```powershell
.\mvnw clean compile
.\mvnw spring-boot:run
```

## 🔧 **Common Issues and Solutions**

### **1. Terminal Encoding Issues**
- **Problem**: Commands prefixed with Thai character "แ"
- **Solution**: Use the provided batch files or start a new terminal session

### **2. Database Connection Failed**
- **Problem**: MySQL connection error
- **Solution**: 
  ```cmd
  docker-compose up -d db
  # Wait 10-15 seconds for MySQL to be ready
  ```

### **3. Port Already in Use**
- **Problem**: Port 8080 is occupied
- **Solution**: 
  ```cmd
  # Find process using port 8080
  netstat -ano | findstr :8080
  # Kill the process
  taskkill /PID <PID> /F
  ```

### **4. Java Version Issues**
- **Problem**: Java version incompatible
- **Solution**: Ensure Java 17+ is installed
  ```cmd
  java -version
  ```

### **5. Maven Dependencies**
- **Problem**: Missing dependencies
- **Solution**: 
  ```cmd
  mvnw.cmd clean install
  ```

## 📋 **Prerequisites Checklist**

- [ ] Java 17 or higher installed
- [ ] Docker and Docker Compose running
- [ ] Port 8080 available
- [ ] Maven (optional, project includes Maven Wrapper)

## 🐛 **Debug Mode**

The application is configured with DEBUG logging. Check console output for detailed information about:
- Database connections
- Spring Security
- Request mappings
- Validation errors

## 📞 **If Still Having Issues**

1. **Check the logs**: Look for specific error messages in the console
2. **Verify database**: Ensure MySQL is running on port 3307
3. **Check environment**: Verify `.env` file exists and has correct values
4. **Clean build**: Run `mvnw.cmd clean install` to rebuild everything

## 🔍 **Useful Commands**

```cmd
# Check Java version
java -version

# Check Docker status
docker ps

# Check if port 8080 is free
netstat -ano | findstr :8080

# Clean and rebuild
mvnw.cmd clean install

# Run tests
mvnw.cmd test
```
