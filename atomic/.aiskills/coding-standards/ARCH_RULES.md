### 8. Quy tắc về Xử lý Đồng thời (Concurrency Control)
- **Pessimistic Locking (Khóa bi quan):**
    - Áp dụng cho: Các giá trị cần độ chính xác tuyệt đối và có tần suất cập nhật cao (VD: Số dư tài khoản `balance`, số lượng tồn kho `stock`).
    - Cách làm: Sử dụng `@Lock(LockModeType.PESSIMISTIC_WRITE)` tại tầng Repository.
- **Optimistic Locking (Khóa lạc quan):**
    - Áp dụng cho: Các dữ liệu đọc nhiều, ít cập nhật (VD: Thông tin profile, cấu hình hệ thống, bài viết).
    - Cách làm: Sử dụng annotation `@Version` của Jakarta Persistence trong Entity.