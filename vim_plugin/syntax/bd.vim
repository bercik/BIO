" Vim syntax file
" Language: BIO documentation file
" Maintainer: Robert Cebula
" Latest Revision: 13 July 2016

if exists("b:current_syntax")
  finish
endif

syn match keyword "!name\s\+"
syn match keyword "!param\s\+"
syn match keyword "!return\s\+"
syn match keyword "!error\s\+"
syn match keyword "!alias\s\+"
syn match keyword "!repeat\s\+"
syn match keyword "!desc\s\+"

let b:current_syntax = "bio"
