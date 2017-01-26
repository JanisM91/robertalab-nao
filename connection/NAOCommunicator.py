#Import ALProxy
import httplib, urllib, requests, json, time, logging
from random import choice
#from hal import Hal
#h = Hal()

serverAdress = "https://lab.open-roberta.org/"
serverPushAddress = serverAdress + "/rest/pushcmd"
serverDownloadAddress = serverAdress + "/rest/download"
serverupdateAddress = serverAdress + "/rest/update"

#Sends a push request to the open roberta server for registration or keeping the connection alive. This will be hold by the server for approximately 10
#seconds and then answered.
def _pushRequest(cmd, params):
	if cmd == 'register':
		params['cmd'] = cmd
		response = requests.post(serverPushAddress, json=params, timeout = 30)
		data = response.json()
		print data
		return data			
	elif cmd == 'download':
		params['cmd'] = 'push'
		logging.warning("Sending push request to /rest/download")
		response = requests.post(serverDownloadAddress, params, timeout = 20)
		print(response.headers)
		data = response.content().decode('utf-8')
		print data
		return data
	elif cmd == 'push':
		params['cmd'] = cmd
		response = requests.post(serverPushAddress, json=params, timeout = 20)
		data = response.json()
		print data
		return data
	
# Downloads a user program from the server as binary.
def storeCode(response):
	logging.warning("Storing code")
	fileName = response.headers['Filename']
	code = response.read().decode('utf-8')
	print(code)
	with open(fileName, 'w') as prog:
		prog.write(code)
	return code

#def runProgram():
	
def generateToken():
    # note: we intentionally leave '01' and 'IO' out since they can be confused
    # when entering the code
    chars = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ'
    return ''.join(choice(chars) for i in range(8))	
		
def updateCustomServerAddress(customServerAdress):
	serverAdress = customServerAdress
	serverpushAddress = serverAdress + "/rest/pushcmd"
	serverDownloadAddress = serverAdress + "/rest/download"
	serverupdateAddress = serverAdress + "/rest/update"

#def getFilename():

def main():
	registered = False
	#generate a new token at every call
	token = generateToken()
	logging.warning("Token generated: " + token)
	#h.say("Der Token lautet: " + token)
	#set the JsonObject with information about the robot for registration
	params = {'firmwarename': "lejos", 'cmd': "register", 'token': token, "brickname":"EV3", "nepoexitvalue":0 }

	try:
		response = _pushRequest('register', params)
		if response['cmd'] == 'repeat':
			registered = True
			logging.warning("Connection established. Token accepted. Repeating...")
			while(True):
				response = _pushRequest('push', params)
				logging.warning('...')
				if response['cmd'] == 'download':
					logging.warning("Download command received. Storing and running code.")
					binaryCode = _pushRequest('download', params)
					code = storeCode(response)
					break
		elif response['cmd'] == 'abort':
			print("token mismatch or connection timed out")
			params['token'] = generateToken()
			time.sleep(1.0)
		else:
			print("Error!")	
	except Exception as e:
		print(e)
		
if __name__ == "__main__":
    main()