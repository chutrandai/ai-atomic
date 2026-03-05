import oracledb

conn = oracledb.connect(
    user="GLAMORPSTEST",
    password="glamorpstest",
    dsn="10.22.19.64:1521/DVTTT1"
)
cursor = conn.cursor()

sql = """
SELECT column_name, data_type, data_length, nullable 
FROM user_tab_columns 
WHERE table_name = 'BO_USER'
ORDER BY column_id
"""
cursor.execute(sql)
columns = cursor.fetchall()

for c in columns:
    print(f"Col: {c[0]}, Type: {c[1]}, Len: {c[2]}, Nullable: {c[3]}")

cursor.close()
conn.close()
