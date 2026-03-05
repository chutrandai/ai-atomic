# Tiêu chuẩn Jakarta EE 11 & Java 21

### Java 21 Features:
- **Virtual Threads**: Bật `spring.threads.virtual.enabled=true`. Ưu tiên cho các tác vụ I/O bound.
- **Records**: Sử dụng cho tất cả DTOs để đảm bảo tính immutability.
- **Pattern Matching**: Dùng cho `switch` và `instanceof` để code sạch hơn.

### Jakarta Persistence & Validation:
- Sử dụng namespace `jakarta.persistence.*` thay vì `javax`.
- Sử dụng `@Valid` và `jakarta.validation.constraints` trên Request Records.