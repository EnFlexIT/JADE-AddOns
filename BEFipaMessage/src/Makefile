ifeq (../Rules.mk,$(wildcard ../Rules.mk))
include ../Rules.mk
endif

subdirs=sonera examples

all clean doc batch dist:
	for i in $(subdirs); do (cd $$i; make $@); done
