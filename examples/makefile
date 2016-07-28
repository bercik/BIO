src=$(wildcard *.bio)
src_bioc=$(src:.bio=.cbio)
obj=$(addprefix compiled/, $(src_bioc))

all: $(obj)

compiled/%.cbio: %.bio
	../impl/bioc/bioc $< -o $@


clean:
	rm $(obj)
