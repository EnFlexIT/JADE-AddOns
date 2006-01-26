@set target=%1

@if ""%1"" == """" @set target=j2se

@echo Running the JSA demo in %target% environment

@java -cp "../%target%/demo/jsaDemo.jar;../%target%/lib/jsa.jar;../../../lib/JadeTools.jar;../../../lib/jade.jar" jade.Boot -nomtp -gui -name test dfagent:demo.DFAgent sensor:demo.SensorAgent(0 dfagent@test) bettersensor:demo.SensorAgent(1 dfagent@test) bestsensor:demo.SensorAgent(2 dfagent@test) display:demo.DisplayAgent(dfagent@test) son:demo.ManAgent(son.txt display@test mother@test showkb) mother:demo.DemoAgent(mother.txt) daughter:demo.DemoAgent(daughter.txt)
