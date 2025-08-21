# Study Group Security System

## ภาพรวม

ระบบความปลอดภัยของ Study Group ถูกออกแบบมาเพื่อปกป้องข้อมูลและทรัพยากรในกลุ่มเรียน โดยใช้ระบบสิทธิ์แบบ Role-Based Access Control (RBAC) และการตรวจสอบสิทธิ์หลายชั้น

## ระบบสิทธิ์ (Permission System)

### GroupPermission Enum

```java
public enum GroupPermission {
    VIEW_GROUP,           // ดูข้อมูลกลุ่ม
    POST_CONTENT,         // โพสต์เนื้อหา
    EDIT_OWN_CONTENT,     // แก้ไขเนื้อหาของตัวเอง
    DELETE_OWN_CONTENT,   // ลบเนื้อหาของตัวเอง
    EDIT_ANY_CONTENT,     // แก้ไขเนื้อหาใดๆ ในกลุ่ม
    DELETE_ANY_CONTENT,   // ลบเนื้อหาใดๆ ในกลุ่ม
    MANAGE_MEMBERS,       // จัดการสมาชิก
    DELETE_GROUP,         // ลบกลุ่ม
    INVITE_MEMBERS,       // เชิญสมาชิกใหม่
    VIEW_ANALYTICS,       // ดูสถิติกลุ่ม
    MODERATE_CONTENT,     // ควบคุมเนื้อหา
    MANAGE_ROLES          // จัดการบทบาทของสมาชิก
}
```

### GroupRole Enum

```java
public enum GroupRole {
    OWNER,      // เจ้าของกลุ่ม - ทำได้ทุกอย่าง
    ADMIN,      // ผู้ดูแล - จัดการสมาชิกและเนื้อหา
    MODERATOR,  // ผู้ควบคุม - ควบคุมเนื้อหา
    MEMBER      // สมาชิกทั่วไป - โพสต์และดูเนื้อหา
}
```

## การตรวจสอบสิทธิ์

### GroupPermissionService

Service หลักสำหรับตรวจสอบสิทธิ์ในกลุ่ม:

```java
@Service
public class GroupPermissionService {
    
    // ตรวจสอบว่าผู้ใช้มีสิทธิ์ทำอะไรในกลุ่ม
    public boolean hasPermission(Long userId, Long groupId, GroupPermission permission);
    
    // ตรวจสอบบทบาทของผู้ใช้ในกลุ่ม
    public GroupRole getUserRole(Long userId, Long groupId);
    
    // ตรวจสอบสิทธิ์การเข้าถึงเนื้อหา
    public boolean canAccessContent(Long userId, Long contentId);
    
    // ตรวจสอบสิทธิ์การแก้ไขเนื้อหา
    public boolean canEditContent(Long userId, Long contentId);
    
    // ตรวจสอบสิทธิ์การลบเนื้อหา
    public boolean canDeleteContent(Long userId, Long contentId);
}
```

### ContentAccessControl

Component สำหรับควบคุมการเข้าถึงเนื้อหา:

```java
@Component
public class ContentAccessControl {
    
    // ตรวจสอบสิทธิ์การดูเนื้อหา
    public boolean canViewContent(Long userId, Long contentId);
    
    // ตรวจสอบสิทธิ์การแก้ไขเนื้อหา
    public boolean canEditContent(Long userId, Long contentId);
    
    // ตรวจสอบสิทธิ์การลบเนื้อหา
    public boolean canDeleteContent(Long userId, Long contentId);
    
    // ตรวจสอบสิทธิ์การอัปโหลดไฟล์
    public boolean canUploadFile(Long userId, Long groupId);
}
```

## ระบบรหัสเชิญ (Invite Code System)

### InviteCodeService

Service สำหรับจัดการรหัสเชิญที่ปลอดภัย:

```java
@Service
public class InviteCodeService {
    
    // สร้างรหัสเชิญที่ปลอดภัย
    public String generateSecureInviteCode();
    
    // ตรวจสอบความถูกต้องของรหัสเชิญ
    public InviteValidationResult validateInviteCode(String code);
    
    // ตรวจสอบวันหมดอายุ
    public boolean isInviteExpired(GroupInvite invite);
    
    // ตรวจสอบจำนวนครั้งที่ใช้
    public boolean isInviteUsageExceeded(GroupInvite invite);
}
```

### คุณสมบัติความปลอดภัย

- **รหัสเชิญแบบสุ่ม**: ใช้ SecureRandom สำหรับสร้างรหัส
- **วันหมดอายุ**: ตั้งวันหมดอายุของรหัสเชิญ
- **จำนวนครั้งที่ใช้**: จำกัดจำนวนครั้งที่ใช้รหัสเชิญ
- **การเพิกถอน**: สามารถเพิกถอนรหัสเชิญได้
- **IP Tracking**: เก็บ IP ของผู้สร้างรหัสเชิญ

## ระบบการยืนยัน (Action Confirmation System)

### ActionConfirmationService

Service สำหรับการยืนยันการกระทำสำคัญ:

```java
@Service
public class ActionConfirmationService {
    
    // สร้างการยืนยันสำหรับการกระทำสำคัญ
    public ConfirmationToken createConfirmation(Long userId, ConfirmationAction action, Map<String, Object> params);
    
    // ตรวจสอบการยืนยัน
    public boolean validateConfirmation(String token, ConfirmationAction action);
    
    // ยืนยันการลบสมาชิก
    public boolean confirmMemberRemoval(Long userId, Long memberId, String confirmationToken);
    
    // ยืนยันการลบกลุ่ม
    public boolean confirmGroupDeletion(Long userId, Long groupId, String confirmationToken);
}
```

### การกระทำที่ต้องยืนยัน

- **ลบสมาชิก**: ต้องยืนยันก่อนลบสมาชิกออกจากกลุ่ม
- **ลบกลุ่ม**: ต้องยืนยันก่อนลบกลุ่มทั้งหมด
- **ออกจากกลุ่ม**: ต้องยืนยันก่อนออกจากกลุ่ม
- **ลบเนื้อหา**: ต้องยืนยันก่อนลบเนื้อหาสำคัญ
- **เพิกถอนรหัสเชิญ**: ต้องยืนยันก่อนเพิกถอน

## ระบบจำกัดการใช้งาน (Rate Limiting)

### RateLimitService

Service สำหรับจำกัดการใช้งานระบบ:

```java
@Service
public class RateLimitService {
    
    // จำกัดการสร้างกลุ่ม: 5 กลุ่มต่อวัน
    public boolean canCreateGroup(Long userId);
    
    // จำกัดการโพสต์เนื้อหา: 50 โพสต์ต่อวัน
    public boolean canPostContent(Long userId, Long groupId);
    
    // จำกัดการเชิญสมาชิก: 20 ครั้งต่อวัน
    public boolean canInviteMembers(Long userId, Long groupId);
    
    // จำกัดการส่งข้อความ: 100 ข้อความต่อวัน
    public boolean canSendMessage(Long userId, Long groupId);
}
```

### ขีดจำกัดการใช้งาน

| การกระทำ | ขีดจำกัด | หน่วย |
|---------|---------|-------|
| สร้างกลุ่ม | 5 | กลุ่มต่อวัน |
| โพสต์เนื้อหา | 50 | โพสต์ต่อวัน |
| เชิญสมาชิก | 20 | ครั้งต่อวัน |
| ส่งข้อความ | 100 | ข้อความต่อวัน |
| อัปโหลดไฟล์ | 50 | ไฟล์ต่อวัน |
| ลบเนื้อหา | 20 | ครั้งต่อวัน |
| แก้ไขเนื้อหา | 30 | ครั้งต่อวัน |

## การตรวจสอบและติดตาม (Audit & Monitoring)

### ContentAuditLog

Entity สำหรับเก็บประวัติการเข้าถึงเนื้อหา:

```java
@Entity
@Table(name = "content_audit_log")
public class ContentAuditLog {
    private Long id;
    private Long userId;
    private Long contentId;
    private Long groupId;
    private String action; // CREATE, UPDATE, DELETE, VIEW, DOWNLOAD
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private String details;
    private Boolean success;
}
```

### การติดตามที่ครอบคลุม

- **การเข้าถึงเนื้อหา**: บันทึกทุกครั้งที่มีการดูเนื้อหา
- **การเปลี่ยนแปลง**: บันทึกการสร้าง แก้ไข ลบเนื้อหา
- **การอัปโหลด**: บันทึกการอัปโหลดไฟล์
- **การดาวน์โหลด**: บันทึกการดาวน์โหลดไฟล์
- **IP Address**: เก็บ IP ของผู้ใช้
- **User Agent**: เก็บข้อมูล browser/device

## การจัดการ Exception

### SecurityException Classes

```java
// Base class สำหรับ security exceptions
public abstract class SecurityException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;
}

// Specific exceptions
public class GroupAccessDeniedException extends SecurityException;
public class ContentAccessDeniedException extends SecurityException;
public class InvalidInviteCodeException extends SecurityException;
public class ActionConfirmationRequiredException extends SecurityException;
public class RateLimitExceededException extends SecurityException;
```

### HTTP Status Codes

| Exception | HTTP Status | คำอธิบาย |
|-----------|-------------|----------|
| GroupAccessDeniedException | 403 Forbidden | ไม่มีสิทธิ์เข้าถึงกลุ่ม |
| ContentAccessDeniedException | 403 Forbidden | ไม่มีสิทธิ์เข้าถึงเนื้อหา |
| InvalidInviteCodeException | 400 Bad Request | รหัสเชิญไม่ถูกต้อง |
| ActionConfirmationRequiredException | 428 Precondition Required | ต้องการการยืนยัน |
| RateLimitExceededException | 429 Too Many Requests | ใช้งานเกินขีดจำกัด |

## การตั้งค่า Security

### GroupSecurityConfig

```java
@Configuration
@EnableWebSecurity
public class GroupSecurityConfig {
    
    @Bean
    public SecurityFilterChain groupSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/study-groups/**").authenticated()
                .requestMatchers("/api/group-members/**").authenticated()
                .requestMatchers("/api/group-content/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
```

### CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // อนุญาต origin จาก frontend
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:3000",      // React dev server
        "https://kayaan-frontend.vercel.app" // Production frontend
    ));
    
    // อนุญาต HTTP methods
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    ));
    
    // อนุญาต headers
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"
    ));
    
    return source;
}
```

## การใช้งานใน Service Layer

### ตัวอย่างการใช้งานใน StudyGroupService

```java
@Service
public class StudyGroupServiceImpl implements StudyGroupService {
    
    @Autowired
    private GroupPermissionService permissionService;
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @Override
    public StudyGroupResponse createGroup(CreateGroupRequest request, Long userId) {
        // ตรวจสอบสิทธิ์การสร้างกลุ่ม
        if (!permissionService.hasPermission(userId, null, GroupPermission.VIEW_GROUP)) {
            throw new GroupAccessDeniedException("User cannot create groups");
        }
        
        // ตรวจสอบ rate limit
        if (!rateLimitService.canCreateGroup(userId)) {
            throw new RateLimitExceededException("Rate limit exceeded for group creation", 
                "CREATE_GROUP", LocalDateTime.now().plusDays(1));
        }
        
        // ... existing logic
    }
}
```

### ตัวอย่างการใช้งานใน GroupContentService

```java
@Service
public class GroupContentServiceImpl implements GroupContentService {
    
    @Autowired
    private ContentAccessControl accessControl;
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @Override
    public ResourceResponse createContent(UploadResourceCompleteRequest request, Long userId) {
        // ตรวจสอบสิทธิ์การอัปโหลด
        if (!accessControl.canUploadFile(userId, request.getGroupId())) {
            throw new ContentAccessDeniedException("User cannot upload content to this group");
        }
        
        // ตรวจสอบ rate limit
        if (!rateLimitService.canUploadFile(userId, request.getGroupId())) {
            throw new RateLimitExceededException("Rate limit exceeded for content upload", 
                "UPLOAD_FILE", LocalDateTime.now().plusDays(1));
        }
        
        // ... existing logic
    }
}
```

## การทดสอบระบบความปลอดภัย

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class GroupPermissionServiceTest {
    
    @Mock
    private GroupMemberRepository groupMemberRepository;
    
    @InjectMocks
    private GroupPermissionService permissionService;
    
    @Test
    void hasPermission_WhenUserIsOwner_ShouldReturnTrue() {
        // Given
        Long userId = 1L;
        Long groupId = 1L;
        GroupMember member = GroupMember.builder()
            .role("OWNER")
            .build();
        
        when(groupMemberRepository.findByUserIdAndGroupId(userId, groupId))
            .thenReturn(Optional.of(member));
        
        // When
        boolean result = permissionService.hasPermission(userId, groupId, GroupPermission.DELETE_GROUP);
        
        // Then
        assertTrue(result);
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase
class GroupSecurityIntegrationTest {
    
    @Autowired
    private GroupPermissionService permissionService;
    
    @Test
    void testGroupPermissionFlow() {
        // Test complete permission flow
        // ... test implementation
    }
}
```

## การปรับปรุงและบำรุงรักษา

### การอัปเดตระบบ

1. **ตรวจสอบ Logs**: ดู ContentAuditLog เป็นประจำ
2. **อัปเดต Permissions**: เพิ่ม/ลบ permissions ตามความต้องการ
3. **ปรับ Rate Limits**: ปรับขีดจำกัดตามการใช้งานจริง
4. **Security Audits**: ตรวจสอบระบบความปลอดภัยเป็นประจำ

### การ Monitor

- **Rate Limit Alerts**: แจ้งเตือนเมื่อมีการใช้งานเกินขีดจำกัด
- **Permission Changes**: ติดตามการเปลี่ยนแปลงสิทธิ์
- **Failed Access Attempts**: ติดตามการเข้าถึงที่ล้มเหลว
- **Suspicious Activities**: ตรวจจับกิจกรรมที่น่าสงสัย

## สรุป

ระบบความปลอดภัยของ Study Group ถูกออกแบบมาให้:

✅ **ครอบคลุม**: ปกป้องทุกส่วนของระบบ  
✅ **ยืดหยุ่น**: ปรับแต่งได้ตามความต้องการ  
✅ **ติดตามได้**: บันทึกทุกการกระทำ  
✅ **ใช้งานง่าย**: API ที่เข้าใจง่าย  
✅ **ประสิทธิภาพสูง**: ใช้ caching และ indexing  

ระบบนี้พร้อมใช้งานและสามารถปรับปรุงเพิ่มเติมได้ตามความต้องการในอนาคต
