import os
import oracledb
from mcp.server.fastmcp import FastMCP

mcp = FastMCP("Oracle-Local-Bridge")


@mcp.tool()
def get_oracle_table_schema(table_name: str) -> str:
    """Truy vấn cấu trúc bảng từ Oracle DB sử dụng biến môi trường."""

    # Đọc thông tin từ biến môi trường do MCP cung cấp
    db_user = os.getenv("ORACLE_USER")
    db_password = os.getenv("ORACLE_PASSWORD")
    db_dsn = os.getenv("ORACLE_DSN")

    try:
        # Kết nối sử dụng thông tin từ file config
        conn = oracledb.connect(
            user=db_user,
            password=db_password,
            dsn=db_dsn
        )
        cursor = conn.cursor()

        sql = f"""
        SELECT column_name, data_type, data_length, nullable 
        FROM user_tab_columns 
        WHERE table_name = '{table_name.upper()}'
        ORDER BY column_id
        """
        cursor.execute(sql)
        columns = cursor.fetchall()

        if not columns:
            return f"Không tìm thấy bảng {table_name}."

        result = [f"Col: {c[0]}, Type: {c[1]}, Len: {c[2]}, Nullable: {c[3]}" for c in columns]

        cursor.close()
        conn.close()
        return "\n".join(result)
    except Exception as e:
        return f"Lỗi kết nối Oracle: {str(e)}"


if __name__ == "__main__":
    mcp.run()