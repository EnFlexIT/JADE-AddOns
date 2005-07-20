#DEBUG = -DEBUG_PARSER=true
JAVACC_BIN = $(HOME)/JavaCC-3.2/bin

all:SLParser.class SLUnparser.class

SLParser.class:SLParser.jj
	$(JAVACC_BIN)/javacc -NOSTATIC $(DEBUG) SLParser.jj
	$(JAVACC_BIN)/jjdoc SLParser.jj
	javac  *.java

SLUnparser.class:SLUnparser.java
	javac  *.java


