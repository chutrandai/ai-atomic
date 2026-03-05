# Hướng dẫn sử dụng Lombok

Để tránh các vấn đề về hiệu suất và lỗi Hibernate:
- **NÊN dùng**: `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Builder`.
- **KHÔNG dùng**: `@Data`, `@ToString`, `@EqualsAndHashCode` trên các class Entity JPA.
- **Lý do**: Tránh vòng lặp vô tận khi load lazy-loading fields và đảm bảo tính nhất quán của Set/Map.