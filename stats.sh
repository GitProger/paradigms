#!/usr/bin/bash

cloc . --match-d=".*-solutions" --not-match-d="(examples|base)" \
                                --not-match-f="Test(er)?.java$"


