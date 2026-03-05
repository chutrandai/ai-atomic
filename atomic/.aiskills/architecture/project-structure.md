# Cấu trúc Thư mục Dự án (Project Structure)

Dự án tuân thủ mô hình **Clean Architecture / Hexagonal Architecture** tại gói `com.vnpay.awesomeproject`.

### Quy tắc phân lớp:
- **common/**: Tiện ích dùng chung (exception, util, base dto).
- **domain/**: Lõi nghiệp vụ. Chứa `model` (Entities), `service` (Domain logic), và `repository` (Interfaces). Không phụ thuộc framework.
- **application/**: Điều hướng Use Cases. Chứa `service` (Thực thi logic), `mapper` (MapStruct), và `port`.
- **infrastructure/**: Triển khai kỹ thuật.
    - `persistence/`: JPA/Hibernate/Oracle/Elasticsearch entities & repos.
    - `messaging/`: Kafka producers/consumers.
    - `external/`: RestTemplate gọi API bên thứ 3.
    - `config/`: Bean config (Security, Redis, Virtual Threads).
- **web/`: Giao tiếp API. Chia theo `modulename`, chứa `controller`, `request`, và `response`.