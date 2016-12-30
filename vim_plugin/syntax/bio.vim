" Vim syntax file
" Language: BIO language
" Maintainer: Robert Cebula
" Latest Revision: 13 July 2016

if exists("b:current_syntax")
  finish
endif

syn keyword basicLanguageKeywords end

syn match closePar ")" display contained
syn match openPar "(" display contained

syn match openBrace "{" display contained
syn match closeBrace "}" display contained

syn match openSquareBrace "\[" display contained
syn match closeSquareBrace "\]" display contained

syn match equal "=" display contained

syn match single_dollar "\$" display contained
syn match double_dollar "\$\$" display contained
syn match id "\$\?\$\?[_a-zA-Z\u0100-\uFFFF]" contains=single_dollar,double_dollar

syn match includeStmts "#INCLUDE\s*(" contains=openPar
syn match importStmts "#IMPORT\s*(" contains=openPar
syn match defineStmts "#DEFINE\s*(" contains=openPar

syn match def "def" display contained
syn match function "def\s\+[_a-zA-Z\u0100-\uFFFF][_0-9a-zA-Z\u0100-\uFFFF]*\s*(" contains=def,openPar
syn match functionCall "\(WHILE\|FOR\|IF\|CALL\|BREAK\|CONTINUE\|CONT\|DN\)\(\s\|\n\)*(" contains=openPar,int,float,none,bool,string

syn region defEndBlock start="def" end="end" fold transparent

syn keyword celTodo contained TODO TODELETE

syn match comment "@.*$" contains=celTodo

syn match stringEscape "\\[nrfbt\\]" display contained
syn region string start="\"" skip="\\\"" end="\"" contains=stringEscape

syn match comma "," display contained

" syn match none "\(\[\|\s\|=\|{\|\n\|,\|(\)\(none\|NONE\|None\)\(\s\|\n\|}\|,\|\|\])\)" display contains=openPar,closePar,comma,openBrace,closeBrace,equal,openSquareBrace,closeSquareBrace

syn match none "\<none\|None\|NONE\>" display

" syn match bool "\(\[\|\s\|\n\|=\|{\|,\|(\)\(true\|TRUE\|True\|false\|False\|FALSE\)\(\s\|\n\|}\|,\|)\|\]\)" display contains=openPar,closePar,comma,openBrace,closeBrace,equal,openSquareBrace,closeSquareBrace

syn match bool "\<true\|True\|TRUE\|false\|False\|FALSE\>" display

" syn match int "\(\[\|\s\|=\|{\|\n\|,\|(\)\(\([+-]\?[1-9][0-9]*\)\|0\)\(\s\|\n\|}\|,\|)\|\]\)" display contains=openPar,closePar,comma,openBrace,closeBrace,equal,openSquareBrace,closeSquareBrace

syn match int "\<\(\([+-]\?[1-9][0-9]*\)\|0\)\>" display

" syn match float "\(\[\|\s\|\n\|=\|{\|,\|(\)\(\([+-]\?[1-9][0-9]*\.[0-9]\+\)\|0\.[0-9]*\)\(\s\|\n\|}\|,\|)\|\]\)" display contains=openPar,closePar,comma,openBrace,closeBrace,equal,openSquareBrace,closeSquareBrace

syn match float "\<\(\([+-]\?[1-9][0-9]*\.[0-9]\+\)\|0\.[0-9]*\)\>" display

let b:current_syntax = "bio"

hi def link celTodo                  Todo
hi def link comment                  Comment
hi def link defEndBlock              Statement
hi def link basicLanguageKeywords    Keyword
hi def link def                      Keyword
hi def link string                   Constant
hi def link includeStmts             PreProc
hi def link importStmts              PreProc
hi def link defineStmts              PreProc
hi def link function                 Function
hi def link functionCall             Function
hi def link stringEscape             Special
hi def link none                     Constant
hi def link bool                     Constant
hi def link int                      Constant
hi def link float                    Constant
hi def link openBrace                Type
hi def link closeBrace               Type
hi def link single_dollar            Statement
hi def link double_dollar            Function
