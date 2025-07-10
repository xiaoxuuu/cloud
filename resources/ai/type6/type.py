import csv

# 定义需要补全的六个类型
required_types = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX"}

# 读取CSV文件并生成SQL插入语句
with open('data.csv', mode='r', newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)

    for row in reader:
        company_code = row['company_code']
        existing_types = set(row['type'].split(','))

        # 找出缺失的类型
        missing_types = required_types - existing_types

        # 生成SQL插入语句
        for part_type in missing_types:
            sql = (
                f"INSERT INTO `financial_ai`.`ai_company_parse` "
                f"(`id`, `effective_date`, `part_type`, `company_code`, `summary`, `create_time`, `update_time`) "
                f"VALUES (2, NULL, '{part_type}', '{company_code}', '##### 此数据暂未解析', "
                f"'2025-06-18 01:43:13.000', '2025-06-18 01:44:16.212');"
            )
            print(sql)
