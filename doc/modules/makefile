src=$(wildcard *.bd)
src_html=$(src:.bd=.html)
obj=$(addprefix html/, $(src_html))

all: index

index: $(obj)
	html/mergedoc.py

html/%.html: %.bd
	./biodoc.py $< $@

clean:
	rm $(obj) html/index.html
