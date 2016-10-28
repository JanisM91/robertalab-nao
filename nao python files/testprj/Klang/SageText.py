'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

tts = ALProxy("ALTextToSpeech","192.168.178.1",9559)

text = "Hallo!"
language = "German"

tts.say(text,language)