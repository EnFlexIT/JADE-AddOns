### This file is included by all makefiles ###
#

#
# JADE classes (jade.jar) 
LIBJADE=../../lib/jade.jar

# Shouldn't need to change these
JAVADOC=javadoc
JAVAC=javac
JAVAC_OPTIONS=-deprecation -O 
AR = jar
ARFLAGS = cf
ZIP = jar
ZIPFLAGS = cMvf
ZIPEXT = zip

ROOTDIR = $(shell pwd)  
ROOTNAME = $(shell basename $(ROOTDIR))  

BATCH_BE=makebe.bat 
BATCH_BE_CLEAN=makebeclean.bat
BATCH_BE_DIST=makebedist.bat
BATCH_BE_LIB=makebelib.bat

# Do not edit anything below this line.
CLSDIR=$(PACKAGE_DIR)/classes
SRCDIR=$(PACKAGE_DIR)/src
LIBDIR=$(PACKAGE_DIR)/lib
CLASSES = $(patsubst %.java,%.class,$(wildcard *.java))
REAL_CLASSPATH=$(PACKAGE_DIR)/$(LIBJADE)
JAVADOCFLAGS=-classpath $(PACKAGE_DIR)/src:$(REAL_CLASSPATH)
JFLAGS=-classpath $(PACKAGE_DIR)/classes:$(REAL_CLASSPATH) $(JAVAC_OPTIONS)\
	-sourcepath $(SRCDIR) -d $(CLSDIR)
