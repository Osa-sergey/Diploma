from ortools.sat.python import cp_model
from optimization import get_pos_points_coord, get_interest_points_coord


def solve(bs_number, maintenance_matrix, maintenance_matrix_number, reachability_bs, reachability_hq, roadmap_id, con):
    # model initialization
    model = cp_model.CpModel()
    x_len = len(maintenance_matrix[0])
    y_len = len(maintenance_matrix)
    # variables
    x = []
    for i in range(x_len):
        x.append(model.NewIntVar(0, 1, 'interest_point_%i' % i))
    y = []
    for i in range(y_len):
        y.append(model.NewIntVar(0, 1, 'pos_point_%i' % i))

    # constraints
    for i in range(y_len):
        model.Add(cp_model.LinearExpr.WeightedSum(x, maintenance_matrix[i]) >= maintenance_matrix_number[i][0] * y[i])
    for i in range(x_len):
        model.Add(cp_model.LinearExpr.WeightedSum(y, maintenance_matrix[:, i]) >= x[i])
    for i in range(y_len):
        model.Add(cp_model.LinearExpr.WeightedSum(y, reachability_bs[i]) >= y[i] - 1)
    model.Add(cp_model.LinearExpr.WeightedSum(y, reachability_hq[0]) >= 1)
    model.Add(cp_model.LinearExpr.Sum(y) <= bs_number)

    solver = cp_model.CpSolver()
    solver.parameters.enumerate_all_solutions = True
    # target
    model.Maximize(cp_model.LinearExpr.Sum(x))
    solver.parameters.max_time_in_seconds = 1800
    # Solve.
    status = solver.Solve(model)
    # results
    pos_points_coord = get_pos_points_coord(roadmap_id, con)
    interest_points_coord = get_interest_points_coord(roadmap_id, con)
    print('Status = %s' % solver.StatusName(status))
    res = ""
    for i in range(y_len):
        if solver.Value(y[i]) == 1:
            res += get_formatted_point(pos_points_coord[i], "pos")
    for i in range(x_len):
        if solver.Value(x[i]) == 1:
            res += get_formatted_point(interest_points_coord[i], "interest")
    print(res)
    print(f'Maximum of objective function: {solver.ObjectiveValue()}\n')
    print('\nStatistics')
    print(f'  status   : {solver.StatusName(status)}')
    print(f'  conflicts: {solver.NumConflicts()}')
    print(f'  branches : {solver.NumBranches()}')
    print(f'  wall time: {solver.WallTime()} s')


def get_formatted_point(point, type):
    formatted_point = f"<node id='{point[0]}' action='modify' version='1' visible='true' lat='{point[1]}' lon='{point[2]}'"
    if type == "pos":
        formatted_point += " />\n"
    else:
        formatted_point += ">\n    <tag k='INTEREST_POINT' v='true' />\n</node>\n"
    return formatted_point
