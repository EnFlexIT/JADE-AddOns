
ifeq (../../../../Rules.mk,$(wildcard ../../../../Rules.mk))
include ../../../../Rules.mk
endif

SUBDIRS = 

.SUFFIXES :
.SUFFIXES : .java .class
.PHONY : ${SUBDIRS} doc clean install

all: ${SUBDIRS} ${CLASSES}

.java.class:
	$(JAVAC) +P $(JFLAGS) $<

doc:
	(cd .. && make doc)

clean:
	rm -fr doc
	rm -f *.class

pre: dummy
	rm -fr pre
	mkdir pre
	for i in *.java; do gcc -E -C -P -x c -nostdinc -DJADE $$i > pre/$$i; done

dummy:
