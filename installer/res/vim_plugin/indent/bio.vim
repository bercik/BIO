" Vim indent file
" Language: BIO language
" Maintainer: Robert Cebula
" Latest Revision: 13 July 2016

if exists("b:did_indent")
   finish
endif
let b:did_indent = 1

setlocal indentexpr=BIOIndent()

if exists("*BIOIndent")
   finish
endif

function! BIOIndent()
   let line = getline(v:lnum)
   let previousNum = prevnonblank(v:lnum - 1)
   let previous = getline(previousNum)

   if previous =~ "(" && previous !~ ")" && line !~ ")"
      return indent(previousNum) + &tabstop
   endif

   if previous =~ "def.*"
      return indent(previousNum) + &tabstop
   endif

   return indent(previousNum)
endfunction
