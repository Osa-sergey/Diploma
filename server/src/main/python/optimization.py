import math
import sys

import numpy as np

from conf import *
from db import *
from solve_model import *


def optimize(opt_id):
    con = create_connection(db_name, db_user, db_password, db_host, db_port)
    opt_info = get_optimization_info(opt_id, con)
    roadmap_id = opt_info[0][0]
    bs_number = opt_info[0][1]
    maintenance_matrix = get_maintenance_matrix(roadmap_id, con)
    maintenance_matrix_number = get_maintenance_matrix_number(roadmap_id, con)
    reachability_bs = get_reachability_bs(roadmap_id, con)
    reachability_hq = get_reachability_hq(roadmap_id, con)
    solve(bs_number, maintenance_matrix, maintenance_matrix_number, reachability_bs, reachability_hq, roadmap_id, con)
    con.close()


def get_pos_points_coord(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_pos_points_coord_query,
                                    (roadmap_id, 60))
    return res


def get_interest_points_coord(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_interest_points_coord_query,
                                    (roadmap_id,))
    return res


def get_optimization_info(opt_id, con):
    res = execute_query_with_result(con,
                                    get_optimization_info_query,
                                    (opt_id,))
    return res


def get_maintenance_matrix(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_maintenance_matrix_query,
                                    (roadmap_id,))
    number_of_pos_points = execute_query_with_result(con,
                                                     number_of_pos_points_query,
                                                     (roadmap_id,))
    len_pos_points = number_of_pos_points[0][0]
    number_of_interest_points = int(len(res) / len_pos_points)
    array = np.empty((0, number_of_interest_points), int)
    for i in range(len_pos_points):
        arr = []
        for j in range(number_of_interest_points):
            arr.append(res[i * number_of_interest_points + j][2])
        array = np.append(array, np.array([arr]), axis=0)
    return array


def get_maintenance_matrix_number(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_maintenance_matrix_number_query,
                                    (roadmap_id,))
    array = np.empty((0, 1), int)
    for i in range(len(res)):
        array = np.append(array, np.array([[res[i][1]]]), axis=0)
    return array


def get_reachability_bs(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_reachability_bs_query,
                                    (roadmap_id,))
    number_of_pos_points = int(math.sqrt(len(res)))
    array = np.empty((0, number_of_pos_points), int)
    for i in range(number_of_pos_points):
        arr = []
        for j in range(number_of_pos_points):
            arr.append(res[i * number_of_pos_points + j][2])
        array = np.append(array, np.array([arr]), axis=0)
    return array


def get_reachability_hq(roadmap_id, con):
    res = execute_query_with_result(con,
                                    get_reachability_hq_query,
                                    (roadmap_id,))
    array = np.empty((1, 0), int)
    for i in range(len(res)):
        array = np.append(array, np.array([[res[i][1]]]), axis=1)
    return array


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
