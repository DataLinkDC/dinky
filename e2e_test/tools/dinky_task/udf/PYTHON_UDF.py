from pyflink.table import ScalarFunction, DataTypes
from pyflink.table.udf import udf


class com(ScalarFunction):
    def __init__(self):
        pass

    def eval(self, variable):
        return str(variable)


python = udf(com(), result_type=DataTypes.STRING())
