import psycopg2 as ps
from psycopg2 import OperationalError

get_maintenance_matrix_query = """select pos_point_id, interest_point_id, is_maintenance from server_main.maintenance
where roadmap_id = %s
order by pos_point_id desc, interest_point_id desc
"""

get_maintenance_matrix_number_query = """select pos_point_id, number from server_main.maintenance_number
where roadmap_id = %s
order by pos_point_id desc
"""

get_reachability_bs_query = """select pos_point_id_1, pos_point_id_2, is_reachable from server_main.reachability_bs
where roadmap_id = %s
order by pos_point_id_1 desc, pos_point_id_2 desc 
"""

get_reachability_hq_query = """select pos_point_id, is_reachable from server_main.reachability_hq
where roadmap_id = %s
order by pos_point_id desc
"""

get_optimization_info_query = """select roadmap_id, bs_number from server_main.optimizations
where id = %s
"""

set_optimization_status_query = """update server_main.optimizations SET (status) = (%s)
where id = %s
"""


def create_connection(name, user, password, host, port):
    connection = None
    try:
        connection = ps.connect(
            database=name,
            user=user,
            password=password,
            host=host,
            port=port,
        )
    except OperationalError as e:
        print(f"Connection error '{e}'")
    return connection


def execute_query(connection, query, params):
    connection.autocommit = True
    cursor = connection.cursor()
    try:
        cursor.execute(query, params)
        cursor.close()
    except BaseException as e:
        print(f"The error '{e}' occurred")


def execute_query_with_result(connection, query, params):
    connection.autocommit = True
    cursor = connection.cursor()
    try:
        cursor.execute(query, params)
        result = cursor.fetchall()
        cursor.close()
        return result
    except BaseException as e:
        print(f"The error '{e}' occurred")
        return ()
