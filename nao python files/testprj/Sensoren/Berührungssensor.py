'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

memoryProxy = ALProxy("AlMemory","192.168.178.1",9559)

position = "Hand"
side = "Links"

if position == "Hand":
    if side == "Links":
        memoryProxy.getData("HandLeftBackTouched")
    elif side == "Rechts":
        memoryProxy.getData("HandRightBackTouched")
elif position == "Bumper":
    if side == "Links":
        memoryProxy.getData("LeftBumperPressed")
    elif side == "Rechts":
        memoryProxy.getData("RightBumperPressed")