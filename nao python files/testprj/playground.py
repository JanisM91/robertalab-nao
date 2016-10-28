'''
Created on 21.10.2016

Project ()°,°)

@author: jmohr
'''

import qi

app = qi.Application()
session = qi.Session()
session.connect("tcp://nao.local:9559")
tts = session.service("ALTextToSpeech")
tts.say("Hello, World")

app.run()