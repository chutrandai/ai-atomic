import os
import psycopg2
from mcp.server.fastmcp import FastMCP

# Khởi tạo FastMCP server
mcp = FastMCP("Postgres-Local-Bridge")


@mcp.tool()
def get_postgresql_table_schema(table_name: str) -> str:
    """Truy vấn cấu trúc bảng từ PostgreSQL sử dụng biến môi trường."""

    # Đọc thông tin từ biến môi trường
    # Định dạng DSN cho Postgres thường là: postgresql://user:password@host:port/dbname
    db_url = os.getenv("POSTGRES_URL")

    # Hoặc đọc rời rạc nếu bạn cấu hình riêng lẻ:
    db_user = os.getenv("POSTGRES_USER")
    db_password = os.getenv("POSTGRES_PASSWORD")
    db_host = os.getenv("POSTGRES_HOST", "localhost")
    db_port = os.getenv("POSTGRES_PORT", "5432")
    db_name = os.getenv("POSTGRES_DB")

    conn = None
    try:
        # Ưu tiên kết nối qua URL nếu có, nếu không dùng tham số rời
        if db_url:
            conn = psycopg2.connect(db_url)
        else:
            conn = psycopg2.connect(
                user=db_user,
                password=db_password,
                host=db_host,
                port=db_port,
                database=db_name
            )

        cursor = conn.cursor()

        # SQL truy vấn schema chuẩn ISO trong PostgreSQL
        sql = """
              SELECT column_name, data_type, character_maximum_length, is_nullable
              FROM information_schema.columns
              WHERE table_name = %s
              ORDER BY ordinal_position; \
              """

        # Sử dụng tham số %s để tránh SQL Injection (Postgres driver style)
        cursor.execute(sql, (table_name.lower(),))
        columns = cursor.fetchall()

        if not columns:
            return f"Không tìm thấy bảng '{table_name}' trong database."

        result = [
            f"Col: {c[0]}, Type: {c[1]}, MaxLen: {c[2] if c[2] else 'N/A'}, Nullable: {c[3]}"
            for c in columns
        ]

        cursor.close()
        return "\n".join(result)

    except Exception as e:
        return f"Lỗi kết nối PostgreSQL: {str(e)}"
    finally:
        if conn:
            conn.close()


if __name__ == "__main__":
    # Đảm bảo bạn đã cài đặt: pip install psycopg2-binary
    mcp.run()
