ifeq (../../../Rules.mk,$(wildcard ../../../Rules.mk))
include ../../../Rules.mk
endif

subdirs=util acl 

all clean:
	for i in $(subdirs); do (cd $$i; make $@); done

doc:
	if [ ! -d api ]; then mkdir api; fi
	$(JAVADOC) "-J-Djava.preprocessor=$(JAVADOCPRE)" \
		$(JAVADOCFLAGS) -d api acl/*.java util/*.java

