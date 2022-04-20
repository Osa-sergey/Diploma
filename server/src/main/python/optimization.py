import sys
import numpy as np
from ortools.linear_solver import pywraplp
from conf import *
from db import *


def optimize(opt_id):
    con = create_connection(db_name, db_user, db_password, db_host, db_port)
    opt_info = get_optimization_info(opt_id, con)
    roadmap_id = opt_info[0][0]
    bs_number = opt_info[0][1]
    get_maintenance_matrix(roadmap_id, con)
    get_maintenance_matrix_number(roadmap_id, con)
    get_reachability_bs(roadmap_id, con)
    get_reachability_hq(roadmap_id, con)
    con.close()


def get_optimization_info(opt_id, con):
    res = execute_query_with_result(con,
                                    get_optimization_info_query,
                                    (opt_id,))
    return res


def get_maintenance_matrix(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_maintenance_matrix_query,
                                    (roadmap_id,))
    print(res)


def get_maintenance_matrix_number(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_maintenance_matrix_number_query,
                                    (roadmap_id,))
    print(res)


def get_reachability_bs(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_reachability_bs_query,
                                    (roadmap_id,))
    print(res)


def get_reachability_hq(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_reachability_hq_query,
                                    (roadmap_id,))
    print(res)


def set_optimization_status(status, opt_id, con):
    execute_query(con,
                  set_optimization_status_query,
                  (status, opt_id,))


if __name__ == "__main__":
    if len(sys.argv) == 2:
        optimization_id = sys.argv[1]
        optimize(optimization_id)
    else:
        print("Optimization_id not found")
