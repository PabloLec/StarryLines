#!/bin/bash

: '
Multiline comment
'

:'echo "This is a comment in bash"'

<<'###BLOCK-COMMENT'
here-document
###BLOCK-COMMENT

<<            ?????
You can also add spaces
?????

: <<'END_COMMENT'
That works too
END_COMMENT

echo "This is not a #comment" # This is a comment

# This is a comment
