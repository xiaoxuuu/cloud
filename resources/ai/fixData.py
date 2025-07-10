from zipfile import error

import psycopg2
import requests
from datetime import datetime

# 数据库连接配置
db_config = {
    'host': '42.193.103.61',
    'port': 8201,
    'user': 'postgres',
    'password': 'GxS3aitpbKX9jx_yXw5ZvCkd',
    'dbname': 'd_api'  # 替换为实际数据库名
}

# 高德地图API配置
amap_api_key = 'f21d89b2cc53360a2641cfe053781e3d'  # 替换为你的实际API密钥
search_url = 'https://restapi.amap.com/v3/place/text?parameters'

def extract_city_from_address(address):
    """ 从地址中提取城市名，支持市和州 """
    import re
    # 使用正则表达式匹配省和市/州
    match = re.search(r'(?:\S+?省)?(\S+?[市州])', address)
    if match:
        return match.group(1)  # 提取市/州信息
    # 匹配失败时抛出异常
    raise ValueError(f"无法从地址中提取城市: {address}")

def get_poi_info(name, address):
    """ 使用高德地图API搜索POI信息 """
    city = ''
    if address:
        city = extract_city_from_address(address)

    params = {
        'key': amap_api_key,
        'keywords': name,
        'city': city,
        'citylimit': 'true',
        'offset': 10,
        'page': 1,
        'extensions': 'all'
    }

    response = requests.get(search_url, params=params)
    result = response.json()


    if result['status'] == '1':
        # if int(result['count']) != 1:
        #     return None
        poi = result['pois'][0]  # 取第一个结果
        return {
            'location': poi['location'].split(','),
            'tag': poi['tag'],
            'rating': poi['biz_ext']['rating'],
            'cost': poi['biz_ext']['cost'],
            'adcode': poi['adcode'],
            'amap_poi_id': poi['id']
        }
    else:
        return None


def update_database_with_poi():
    """ 从数据库获取数据并用高德地图API更新POI信息 """
    conn = psycopg2.connect(**db_config)
    cursor = conn.cursor()

    try:
        # 查询所有需要处理的名字
        select_query = "SELECT id AS food_id, address AS name, address FROM t_point WHERE state = 'A'"
        cursor.execute(select_query)

        for food_id, name, address in cursor.fetchall():
            print(f"Processing: {name}")
            poi_info = get_poi_info(name, address)
            if poi_info  is None:
                cursor.execute("UPDATE t_point SET state = 'P' WHERE id = %s", (food_id,))
                conn.commit()  # 提交事务
                print(f"存在重复数据: {name}")
                continue

            if poi_info:
                location = poi_info['location']
                tag = poi_info['tag']
                rating = poi_info['rating']
                cost = poi_info['cost']
                adcode = poi_info['adcode']
                amap_poi_id = poi_info['amap_poi_id']

                update_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

                # 更新数据库
                update_query = """
                               UPDATE t_point
                               SET longitude        = %s,
                                   latitude         = %s,
                                   amap_update_time = %s,
                                   amap_tag         = %s,
                                   amap_rating      = %s,
                                   amap_cost        = %s,
                                   address_code     = %s,
                                   amap_poi_id      = %s,
                                   state      = 'P'
                               WHERE id = %s; \
                               """
                cursor.execute(update_query, (
                    float(location[0]),  # x坐标（经度）
                    float(location[1]),  # y坐标（纬度）
                    update_time,
                    tag,
                    rating,
                    cost,
                    adcode,
                    amap_poi_id,
                    food_id
                ))
                conn.commit()  # 提交事务

    except Exception as e:
        print(f"Error occurred: {e}")
        conn.rollback()  # 出错回滚
    finally:
        cursor.close()
        conn.close()


if __name__ == '__main__':
    update_database_with_poi()
