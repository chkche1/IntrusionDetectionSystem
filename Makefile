JCC = javac

JFLAGS = -g

default: IDSListener.class IDS.class

IDSListener.class: IDSListener.java 
	$(JCC) $(JFLAGS) IDSListener.java

IDS.class: IDS.java 
	$(JCC) $(JFLAGS) IDS.java

clean: 
	$(RM) *.class
