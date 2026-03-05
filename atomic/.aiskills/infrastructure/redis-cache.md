# Cấu hình Redis & Caching

- **Strategy**: Cache-aside pattern.
- **Serialization**: Sử dụng JSON serializer thay vì JDK serializer mặc định.
- **TTL**: Luôn set Time-To-Live cho mọi key để tránh tràn bộ nhớ.