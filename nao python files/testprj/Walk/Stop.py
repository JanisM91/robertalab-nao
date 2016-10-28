'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

motion = ALProxy("ALMotion","192.168.178.1",9559)

motion.stopMove();