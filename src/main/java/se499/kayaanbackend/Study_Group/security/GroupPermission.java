package se499.kayaanbackend.Study_Group.security;

/**
 * Enum สำหรับสิทธิ์ต่างๆ ที่ผู้ใช้สามารถมีในกลุ่มเรียน
 */
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
