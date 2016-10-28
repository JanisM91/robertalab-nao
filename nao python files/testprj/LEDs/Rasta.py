'''
Created on 20.10.2016

@author: jmohr
'''

from naoqi import ALProxy

led = ALProxy("AlLedsProxy","127.0.0.1",9559);

duration = 5

led.rasta(duration)