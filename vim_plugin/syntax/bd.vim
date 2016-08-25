" Vim syntax file
" Language: BD documentation file
" Maintainer: Robert Cebula
" Latest Revision: 25 August 2016

if exists("b:current_syntax")
  finish
endif

syn match keyword "!name\s\+"
syn match keyword "!param\s\+"
syn match keyword "!return\s\+"
syn match keyword "!error\s\+"
syn match keyword "!alias\s\+"
syn match keyword "!repeat\s\+"
syn match keyword "!optional\s\+"
syn match keyword "!desc\s\+"

syn match Function "^!optional\s*$"

let b:current_syntax = "bd"
