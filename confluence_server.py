import os
import json
from mcp.server.fastmcp import FastMCP
from atlassian import Confluence

# Khởi tạo FastMCP server
mcp = FastMCP("Confluence Reader")

# Lấy cấu hình từ môi trường (được truyền từ file JSON cấu hình của Antigravity)
URL = os.environ.get("CONFLUENCE_URL")
EMAIL = os.environ.get("CONFLUENCE_EMAIL")
TOKEN = os.environ.get("CONFLUENCE_API_TOKEN")


# Khởi tạo kết nối Confluence
conf = Confluence(
    url=URL,
    token=TOKEN,
    cloud=False  # Đặt là False nếu dùng Confluence Server/Data Center (ServiceHub)
)


@mcp.tool()
def search_menu_by_name(title: str, space: str = None):
    """Tìm kiếm ID của một trang (Menu) dựa trên tiêu đề."""
    results = conf.get_all_pages_by_label(title)  # Hoặc dùng cql bên dưới
    # Sử dụng CQL để tìm chính xác hơn
    cql = f'title ~ "{title}"'
    if space:
        cql += f' and space = "{space}"'

    search_results = conf.cql(cql)
    return search_results.get('results', [])


@mcp.tool()
def list_child_pages(parent_page_id: str):
    """Liệt kê tất cả các trang con của một Menu (Parent ID)."""
    children = conf.get_child_pages(parent_page_id)
    return [{"id": c['id'], "title": c['title']} for c in children]


@mcp.tool()
def read_page_content(page_id: str):
    """Đọc nội dung chi tiết của một trang dựa trên ID."""
    page = conf.get_page_by_id(page_id, expand='body.storage')
    title = page['title']
    # Confluence trả về HTML, Agent sẽ tự xử lý hoặc bạn có thể dùng BeautifulSoup để strip tag
    content = page['body']['storage']['value']
    return f"Title: {title}\n\nContent:\n{content}"


if __name__ == "__main__":
    mcp.run()
