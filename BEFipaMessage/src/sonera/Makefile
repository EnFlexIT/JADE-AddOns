ifeq (../../Rules.mk,$(wildcard ../../Rules.mk))
include ../../Rules.mk
endif

subdirs=fipa

all clean doc batch dist:
	for i in $(subdirs); do (cd $$i; make $@); done
