java -cp "./jsaDemo.jar;../lib/jsa.jar;../../../lib/JadeTools.jar;../../../lib/jade.jar" jade.Boot -nomtp -gui -name test DFAgent:demo.DFAgent sensor:demo.SensorAgent(0 DFAgent@test) bettersensor:demo.SensorAgent(1 DFAgent@test) bestsensor:demo.SensorAgent(2 DFAgent@test) display:demo.DisplayAgent(DFAgent@test) son:demo.ManAgent(son.txt display@test mother@test showkb) mother:demo.DemoAgent(mother.txt) daughter:demo.DemoAgent(daughter.txt)
