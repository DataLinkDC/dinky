import hashlib
import importlib
import os
import sys
import uuid
import zipfile
import appdirs
import shutil

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

if len(sys.argv) < 2:
    raise Exception("Please enter the file path")

project_path: str = sys.argv[1]

udf_name_list = set()


def get_file_md5(path):
    """
    获取文件内容的MD5值
    :param path: 文件所在路径
    :return:
    """
    with open(path, 'rb') as file:
        data = file.read()

    diff_check = hashlib.md5()
    diff_check.update(data)
    md5_code = diff_check.hexdigest()
    return md5_code


def list_modules(root_dir):
    """返回给定目录下所有模块和子模块的名称"""
    modules = []
    if os.path.isdir(root_dir):
        sys.path.append(project_path)
        for dirpath, _, filenames in os.walk(root_dir):
            for filename in filenames:
                parse_file(dirpath, filename, modules, root_dir)
    else:
        file_dir = os.path.dirname(root_dir)
        sys.path.append(file_dir)
        parse_file(file_dir, root_dir, modules, file_dir)
        if project_path.endswith(".py"):
            sys.path.append(file_dir)
        elif project_path.endswith(".zip"):
            tmp_dir = appdirs.user_cache_dir()
            file = zipfile.ZipFile(project_path)
            unzip_file_path = os.path.normpath(tmp_dir + "/dinky/udf_parse/" + get_file_md5(project_path))
            if os.path.exists(unzip_file_path):
                shutil.rmtree(unzip_file_path)
            file.extractall(unzip_file_path)
            sys.path.append(unzip_file_path)
            for dirpath, _, filenames in os.walk(unzip_file_path):
                for filename in filenames:
                    parse_file(dirpath, filename, modules, unzip_file_path)
            file.close()
    return modules


def parse_file(dirpath, filename, modules, root_dir):
    root_dir = os.path.normpath(root_dir)
    if filename.endswith(".py"):
        p_ = root_dir.replace(os.sep, ".")
        module_name = os.path.splitext(os.path.join(dirpath, filename))[0].replace(os.sep, ".").replace(
            p_ + ".", "")
        modules.append(module_name.replace(root_dir, ""))


if __name__ == '__main__':
    modules = list_modules(project_path)

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
