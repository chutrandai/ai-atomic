# Phong cách Phân lớp (Layered Style)

### Luồng dữ liệu (Data Flow):
Controller -> Application Service -> Domain Service/Repository -> Infrastructure Implementation.

### Quy tắc bất biến:
- **Dependencies**: Lớp bên trong (Domain) không được biết về lớp bên ngoài (Infrastructure).
- **Data Transfer**:
    - Web layer dùng **Records** cho Request/Response.
    - Application layer dùng MapStruct để chuyển đổi giữa DTO và Entity.
    - Domain layer chỉ làm việc với Business Entities.