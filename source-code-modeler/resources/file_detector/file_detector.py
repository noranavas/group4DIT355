import os

def list_files_in_dir(dir):
    files = os.listdir(dir)
    for f in files:
        name, ext = os.path.splitext(f)
        print(name, ext)

def print_file_content(file_name):
    with open(file_name, 'r') as _file:
        file_content = _file.read()
        print(file_content)

list_files_in_dir('.')
#print_file_content('CategoryController.java')
