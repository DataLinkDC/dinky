# -*- coding: utf-8 -*-
import importlib
import sys
import platform
import pyflink

# import ast
# from pathlib import Path
# from typing import Callable, AnyStr
# notFuncName: list = ["__call__", "eval"]
# udfNameList: list = []
# is_udf: Callable[[AnyStr], bool] = lambda func: isinstance(getattr(importlib.import_module(path.stem), func),
#                                                            pyflink.table.udf.UserDefinedFunctionWrapper)
#
# # filePath: str = sys.argv[1]
# # if str is None:
# #     raise Exception("请输入文件路径")
# # path = Path(filePath)
# # if path.parent.name != "":
# #     sys.path.append(path.parent.__str__())
# with open(filePath, 'r', encoding='utf8') as f:
#     tree = ast.parse(f.read())
#
# for node in ast.walk(tree):
#     if isinstance(node, ast.FunctionDef):
#         try:
#             if node.args.args[0].arg == "self":
#                 continue
#         except Exception as e:
#             pass
#         if notFuncName.__contains__(node.name):
#             continue
#
#         if not is_udf(node.name):
#             continue
#         udfNameList.append(node.name)
#
#     try:
#         if isinstance(node.targets[0], ast.Name) and is_udf(node.targets[0].id):
#             udfNameList.append(node.targets[0].id)
#     except Exception as e:
#         pass


import os

if len(sys.argv) < 2:
    raise Exception("Please enter the file path")

project_path: str = sys.argv[1]

udf_name_list = set()


def list_modules(root_dir):
    """返回给定目录下所有模块和子模块的名称"""
    modules = []
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith(".py"):
                p_ = project_path.replace(os.sep, ".")
                module_name = os.path.splitext(os.path.join(dirpath, filename))[0].replace(os.sep, ".").replace(
                    p_ + ".", "")
                modules.append(module_name.replace(root_dir, ""))
    return modules


if __name__ == '__main__':
    modules = list_modules(project_path)

    sys.path.append(project_path)
    for module_name in modules:
        try:
            module = importlib.import_module(module_name)
            for m in dir(module):
                if isinstance(getattr(module, m), pyflink.table.udf.UserDefinedFunctionWrapper):
                    udf_name_list.add(module.__name__ + "." + m)

        except Exception as e:
            pass

    print(str.join(",", udf_name_list))

# import pkgutil
# for _, module_name, _ in pkgutil.walk_packages([""]):
#     if module_name == os.path.splitext(os.path.basename(__file__))[0]:
#         continue
#     try:
#         print(module_name)
#         module = importlib.import_module(module_name)
#         for m in dir(module):
#             if isinstance(getattr(module, m), pyflink.table.udf.UserDefinedFunctionWrapper):
#                 udfNameList.add(module.__name__ + "." + m)
#
#     except Exception as e:
#         pass
#
# print(str.join(",", udfNameList))
