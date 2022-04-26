from ortools.sat.python import cp_model
import numpy as np


class VarArraySolutionPrinter(cp_model.CpSolverSolutionCallback):
    """Print intermediate solutions."""

    def __init__(self, variables):
        cp_model.CpSolverSolutionCallback.__init__(self)
        self.__variables = variables
        self.__solution_count = 0

    def on_solution_callback(self):
        self.__solution_count += 1
        for v in self.__variables:
            print('%s=%i' % (v, self.Value(v)), end=' ')
        print()

    def solution_count(self):
        return self.__solution_count

def solve(bs_number, maintenance_matrix, maintenance_matrix_number, reachability_bs, reachability_hq):
    # model
    maintenance_matrix = np.array([[1, 1, 0, 1],
                                   [0, 0, 1, 0],
                                   [0, 0, 0, 1]])
    maintenance_matrix_number = np.array([[3],
                                          [1],
                                          [1]])
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
    # for i in range(y_len):
    #     model.Add(cp_model.LinearExpr.WeightedSum(y, reachability_bs[i]) >= y[i] - 1)
    # model.Add(cp_model.LinearExpr.WeightedSum(y, reachability_hq[0]) >= 1)
    model.Add(cp_model.LinearExpr.Sum(y) <= 3)

    solver = cp_model.CpSolver()
    solution_printer = VarArraySolutionPrinter(x)
    solver.parameters.enumerate_all_solutions = True
    # target
    model.Maximize(sum(x))
    solver.parameters.max_time_in_seconds = 1800
    # Solve.
    status = solver.Solve(model, solution_printer)

    print('Status = %s' % solver.StatusName(status))
    for i in range(y_len):
        print('pos_point_%i = %i' % (i, solver.Value(y[i])))
    for i in range(x_len):
        print('interest_point_%i = %i' % (i, solver.Value(x[i])))
    print(f'Maximum of objective function: {solver.ObjectiveValue()}\n')

    print('\nStatistics')
    print(f'  status   : {solver.StatusName(status)}')
    print(f'  conflicts: {solver.NumConflicts()}')
    print(f'  branches : {solver.NumBranches()}')
    print(f'  wall time: {solver.WallTime()} s')

