src=$(wildcard *.bio)
src_bioc=$(src:.bio=.bioc)
obj=$(addprefix compiled/, $(src_bioc))

all: $(obj)

compiled/%.bioc: %.bio
	../impl/bioc/bioc $< -o $@


clean:
	rm $(obj)
