"""This is a
module description
And its multiline
"""

_ = print("""
    This is not a comment
    """)

a = 1 # This is a comment

def foo():
    """This is a function docstring
    And it is multiline"""
    return 1

# This is a comment

def bar():
    """This is a function docstring"""
    return 2

def baz():
    '''Docstring with single quotes'''
    return 3

def qux():
    '''Multi-line
    single-quotes
    '''
    return 4

b = "# Don't remove me" # But remove me