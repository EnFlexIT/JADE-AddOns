#DEBUG = -DEBUG_PARSER=true

all:SLParser.class SLUnparser.class

SLParser.class:SLParser.jj
	javacc -NOSTATIC $(DEBUG) SLParser.jj
	jjdoc SLParser.jj
	javac  -source 1.4 *.java

SLUnparser.class:SLUnparser.java
	javac  *.java


