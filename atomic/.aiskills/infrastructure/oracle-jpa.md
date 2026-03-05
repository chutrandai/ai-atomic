# Cấu hình Oracle & JPA/Hibernate

- **ID Generation**: Luôn dùng **Snowflake ID**. Không dùng Identity/Sequence của DB.
- **Audit**: Mọi bảng phải có `created_at`, `created_by`, `updated_at`, `updated_by`.
- **Soft Delete**: Sử dụng field `is_deleted` hoặc `status`.
- **Migration**: Chỉ sử dụng Flyway/Liquibase. `ddl-auto` phải là `validate`.
- **Documentation**: Mọi `@Column` phải có Javadoc tiếng Việt mô tả nghiệp vụ.