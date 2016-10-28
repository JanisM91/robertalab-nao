'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

memoryProxy = ALProxy("AlMemory","192.168.178.1",9559)

#Subscribe to Sonars, launch them(at Hardware level) and start data acquisition
side = "left"

if side == "left":
    memoryProxy.getData("leftFootTotalWeight")
else:
    memoryProxy.getData("rightFootTotalWeight")