
ifeq (../../Rules.mk,$(wildcard ../../Rules.mk))
include  ../../Rules.mk
endif

.SUFFIXES :
.SUFFIXES : .java .class
.PHONY : ${SUBDIRS} doc clean install

all: ${SUBDIRS} ${CLASSES} 

.java.class:
	$(JAVAC) $(JFLAGS) $<

doc:
	(cd .. && make doc)

clean:
	rm -fr doc
	rm -f *.class
