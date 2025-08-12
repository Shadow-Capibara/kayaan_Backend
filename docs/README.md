# Kayaan Backend Documentation

## 📚 Documentation Structure

```
docs/
├── README.md                           # ไฟล์นี้
├── STRUCTURE.md                        # Documentation structure guide
└── features/                           # Features documentation
    └── study-group/                    # Study Group feature
        ├── README.md                   # Overview & Quick Start
        ├── api/                        # API documentation
        │   ├── README.md               # API overview
        │   └── STUDY_GROUP_API.md      # Complete API reference
        ├── testing/                    # Testing documentation
        │   ├── README.md               # Testing guide
        │   ├── curl_examples.md        # Curl examples
        │   └── test_study_group.sh     # Automated test script
        └── integration/                # Integration guides
            ├── README.md               # Integration overview
            └── FRONTEND_INTEGRATION_CHECKLIST.md
```

## 🚀 Quick Navigation

### Features
- **[Study Group](./features/study-group/README.md)** - Complete study group management system

### Development
- **[API Documentation](./features/study-group/api/)** - API reference and examples
- **[Testing Guide](./features/study-group/testing/)** - How to test APIs
- **[Frontend Integration](./features/study-group/integration/)** - Integration guides

## 📋 Available Features

### ✅ Completed
- **Study Group** - Full CRUD operations, file sharing, member management

### 🔄 In Progress
- Authentication & Authorization
- Avatar Management
- Theme System

### 📅 Planned
- Real-time Chat
- Notification System
- Analytics Dashboard

## 🛠 Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Supabase account

### Quick Start
```bash
# Clone repository
git clone <repository-url>
cd kayaan_Backend

# Setup environment
cp env.example .env
# Edit .env with your configuration

# Run application
./mvnw spring-boot:run
```

## 📖 Documentation Guidelines

### Adding New Features
1. Create feature folder: `docs/features/<feature-name>/`
2. Add subfolders: `api/`, `testing/`, `integration/`
3. Create README.md with overview
4. Add API documentation
5. Add testing examples
6. Add integration guides

### Documentation Standards
- Use clear, concise language
- Include code examples
- Add error handling examples
- Include testing instructions
- Provide integration guides

## 🔗 Useful Links

- [Backend Repository](../README.md)
- [Documentation Structure](./STRUCTURE.md)
- [Study Group Feature](./features/study-group/README.md)
- [API Documentation](./features/study-group/api/)
- [Testing Guide](./features/study-group/testing/)
- [Integration Guide](./features/study-group/integration/)

## 📞 Support

For questions or issues:
1. Check feature-specific documentation
2. Review API documentation
3. Run test scripts
4. Contact development team

---

**Happy coding! 🎉**
