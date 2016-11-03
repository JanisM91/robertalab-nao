#Import
from naoqi import ALProxy
from naoqi import ALBroker
from optparse import OptionParser
import time

NAO_IP = "169.254.235.8"    #from robotconfiguration

#parse command line options
parser = OptionParser()
parser.add_option("--pip",help="Parent broker port. The IP address or your robot", dest="pip")
parser.add_option("--pport",help="Parent broker port. The port NAOqi is listening to",dest="pport",type="int")
parser.set_defaults(pip=NAO_IP,pport=9559)
(opts, args_) = parser.parse_args()
pip = opts.pip
pport = opts.pport

myBroker = ALBroker("myBroker","0.0.0.0",   #Listen to anyone
                    0,                      #find a free port and use it
                    pip,                    #parent broker ip
                    pport)                  #parent broker port

#Create ALProxy
#tts = ALProxy("ALTextToSpeech")
motion = ALProxy("ALMotion")
#posture = ALProxy("ALRobotPosture")
memory = ALProxy("ALMemory")
#tracker = ALProxy("ALTracker")
#mark = ALProxy("ALLandMarkDetection")
photo = ALProxy("ALPhotoCapture")
sonar = ALProxy("ALSonar")
tts = ALProxy("ALTextToSpeech")
led = ALProxy("ALLedsProxy")
video = ALProxy("ALVideoRecorder")


#Move

def taiChi():
    names = list()
    times = list()
    keys = list()

    names.append("HeadPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 8.95233e-08, -4.76838e-07, 8.89455e-08, 1.04976e-07, 0.331613, 0.314159, 9.19019e-08, -0.331613, 0.139626, -0.0872665, 0.139626, 0.383972, 0.558505, 0.383972, -0.331613, 0.139626, -0.0872665, 0.139626, 0.383972, 0, -0.190258])
    
    names.append("HeadYaw")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 8.42936e-08, 8.42938e-08, 8.42938e-08, -4.76838e-07, 0.314159, -0.296706, -1.18682, -0.279253, 0.20944, 1.5708, 0.20944, 0.139626, 0, -0.139626, 0.279253, -0.20944, -1.5708, -0.20944, -0.139626, 0, -0.00310993])

    names.append("LAnklePitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 43.4, 44.4, 46.2, 50])
    keys.append([1.00403e-07, 0, -0.303687, 0, 0, -0.647517, -0.610865, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -0.872665, -0.741765, 0, 1.00403e-07, 0.523599, 1.00403e-07, -0.555015, -0.654498, -1.0472, 0.033706])

    names.append("LAnkleRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 33.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0.0523599, 0.122173, 0.174533, -0.10472, -0.10472, 0.174533, -0.261799, 0.0628318, 0.1309, 0, 0, 0, 0.0872665, 0, -0.240855, -0.55676, -0.424115, -0.349066, 0, -0.349066, -0.312414, 0, -0.05058])

    names.append("LElbowRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 45.4, 46.2, 50])
    keys.append([0, -0.698132, -1.0472, 0, 0, -1.65806, -0.959931, -1.48353, -1.01229, -1.01229, 0, -1.01229, -1.01229, -0.890118, -0.855211, -1.11701, -0.855211, -1.25664, -0.855211, -0.855211, -0.994838, -1.4207, -0.38806])

    names.append("LElbowYaw")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 45.4, 46.2, 50])
    keys.append([-1.5708, -1.5708, -1.5708, -1.5708, -1.5708, -0.383972, 0, 0, 0, 0, 0, 0, 0, 0.20944, 0.191986, -0.418879, -0.418879, -0.0872665, -0.418879, 0.191986, -0.378736, -0.244346, -1.18276])

    names.append("LHand")
    times.append([3, 50])
    keys.append([0, 0.2984])

    names.append("LHipPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 0, -0.349066, 0, 0, -0.698132, -0.610865, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -0.872665, -0.741765, -0.122173, -0.872665, 0, -0.872665, -0.654498, -1.0472, 0.216335])

    names.append("LHipRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 33.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([-0.0523599, -0.122173, -0.174533, 0.10472, 0.10472, -0.174533, 0.174533, 0.420624, 0.528835, 0.610865, 0.610865, 0.610865, 0.349066, 0, -0.261799, 0.251327, 0.261799, 0.139626, 0.698132, 0.139626, -0.261799, 0, 0.0414601])

    names.append("LHipYawPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([-0.10821, -0.120428, -0.1309, -0.120428, -0.143117, -0.167552, -0.0994838, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.0680678, 0, -0.194775])

    names.append("LKneePitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 0, 0.698132, -9.9341e-08, -9.9341e-08, 1.39626, 1.22173, 2.0944, 2.0944, 2.0944, 2.0944, 2.0944, 2.1101, 1.74533, 1.48353, 0.122173, 1.74533, 0, 1.74533, 1.309, 2.0944, -0.0890141])

    names.append("LShoulderPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([1.5708, 1.91986, 2.0944, 1.5708, 0, 0.366519, 0.349066, 0.191986, -0.802851, -0.174533, -0.296706, -0.174533, 0.523599, 0.471239, 0.331613, -0.471239, 0.0698132, -0.0698132, 0.0698132, 0.331613, 1.69297, 1.52936])

    names.append("LShoulderRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0.174533, 0.349066, 0.174533, 0.174533, 0.174533, 0.698132, 0, 0.0872665, 0.174533, 0.401426, 1.15192, 0.401426, 0.401426, 0.174533, 0, 0.401426, 0, 0, 0, 0.20944, 0.942478, 0.107338])

    names.append("LWristYaw")
    times.append([3, 50])
    keys.append([-1.53589, 0.139552])

    names.append("RAnklePitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([1.00403e-07, 0, 0, 0, 0, -0.698132, -0.174533, 0, 0, 1.00403e-07, 0.523599, 1.00403e-07, -0.741765, -0.872665, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, 0.036858])

    names.append("RAnkleRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([-0.0523599, 0.1309, 0.438078, 0.10472, 0.10472, 0.294961, 0.621337, 0.785398, 0.74351, 0.436332, 0, 0.349066, 0.261799, 0, -0.174533, -0.174533, -0.0424667, -0.0225556, -0.0130542, -0.00206581, 0, 0.0291878])

    names.append("RElbowRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 45.4, 46.2, 50])
    keys.append([0, 0.698132, 1.0472, 2.57424e-07, 0, 1.23918, 1.64061, 0.0698132, 1.11701, 0.855211, 1.25664, 0.855211, 0.855211, 0.890118, 1.01229, 1.01229, 1.01229, 0.0349066, 1.01229, 1.01229, 1.13272, 1.36659, 0.395814])

    names.append("RElbowYaw")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 45.4, 46.2, 50])
    keys.append([1.5708, 1.5708, 1.5708, 1.5708, 1.5708, 0.191986, 0.349066, 1.5708, 0.418879, 0.418879, 0.0872665, 0.418879, -0.191986, -0.20944, 0, 0, 0, 0, 0, 0, 0.342085, 0.244346, 1.15966])

    names.append("RHand")
    times.append([3, 50])
    keys.append([0, 0.302])

    names.append("RHipPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 0, 0, 0, 0, -0.698132, -0.174533, -0.10472, -0.122173, -0.872665, 0, -0.872665, -0.741765, -0.872665, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, -1.0472, 0.214717])

    names.append("RHipRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0.0523599, -0.122173, -0.438078, -0.10472, -0.10472, -0.349066, -0.785398, -0.541052, -0.139626, -0.139626, -0.698132, -0.139626, 0.261799, 0, -0.349066, -0.539307, -0.610865, -0.610865, -0.610865, -0.532325, 0, -0.021434])

    names.append("RHipYawPitch")
    times.append([3, 5, 7, 9, 11, 13, 50])
    keys.append([-0.10821, -0.120428, -0.1309, -0.120428, -0.143117, -0.167552, -0.194775])

    names.append("RKneePitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([0, 0, 0, 0, 0, 1.39626, 0.349066, 0.122173, 0.122173, 1.74533, 0, 1.74533, 1.48353, 1.74533, 2.0944, 2.0944, 2.0944, 2.0944, 2.0944, 2.0944, 2.0944, -0.091998])

    names.append("RShoulderPitch")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([1.5708, 1.91986, 2.0944, 1.5708, 0, 0.174533, 0.610865, 1.0472, -0.471239, 0.0698132, -0.0698132, 0.0698132, 0.331613, 0.471239, 0.523599, -0.802851, -0.174533, -0.296706, -0.174533, 0.523599, 1.69297, 1.51563])

    names.append("RShoulderRoll")
    times.append([3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23.6, 26.2, 28.4, 30.4, 32.4, 34.4, 37, 39.6, 42.2, 44.4, 46.2, 50])
    keys.append([-0.174533, -0.174533, -0.349066, -0.174533, -0.174515, -0.0698132, -0.837758, -1.51844, -0.401426, 0, 0, 0, 0, -0.174533, -0.401426, -0.174533, -0.401426, -1.15192, -0.401426, -0.558505, -0.942478, -0.099752])

    names.append("RWristYaw")
    times.append([3, 50])
    keys.append([1.53589, 0.164096])

    motion.angleInterpolation(names, keys, times, True)
    
def wave():
    names = list()
    times = list()
    keys = list()

    names.append("HeadPitch")
    times.append([0.8, 1.56, 2.24, 2.8, 3.48, 4.6])
    keys.append([0.29602, -0.170316, -0.340591, -0.0598679, -0.193327, -0.01078])

    names.append("HeadYaw")
    times.append([0.8, 1.56, 2.24, 2.8, 3.48, 4.6])
    keys.append([-0.135034, -0.351328, -0.415757, -0.418823, -0.520068, -0.375872])

    names.append("LElbowRoll")
    times.append([0.72, 1.48, 2.16, 2.72, 3.4, 4.52])
    keys.append([-1.37902, -1.29005, -1.18267, -1.24863, -1.3192, -1.18421])

    names.append("LElbowYaw")
    times.append([0.72, 1.48, 2.16, 2.72, 3.4, 4.52])
    keys.append([-0.803859, -0.691876, -0.679603, -0.610574, -0.753235, -0.6704])

    names.append("LHand")
    times.append([1.48, 4.52])
    keys.append([0.238207, 0.240025])

    names.append("LShoulderPitch")
    times.append([0.72, 1.48, 2.16, 2.72, 3.4, 4.52])
    keys.append([1.11824, 0.928028, 0.9403, 0.862065, 0.897349, 0.842125])

    names.append("LShoulderRoll")
    times.append([0.72, 1.48, 2.16, 2.72, 3.4, 4.52])
    keys.append([0.363515, 0.226991, 0.20398, 0.217786, 0.248467, 0.226991])

    names.append("LWristYaw")
    times.append([1.48, 4.52])
    keys.append([0.147222, 0.11961])

    names.append("RElbowRoll")
    times.append([0.64, 1.4, 1.68, 2.08, 2.4, 2.64, 3.04, 3.32, 3.72, 4.44])
    keys.append([1.38524, 0.242414, 0.349066, 0.934249, 0.680678, 0.191986, 0.261799, 0.707216, 1.01927, 1.26559])

    names.append("RElbowYaw")
    times.append([0.64, 1.4, 2.08, 2.64, 3.32, 3.72, 4.44])
    keys.append([-0.312978, 0.564471, 0.391128, 0.348176, 0.381923, 0.977384, 0.826783])

    names.append("RHand")
    times.append([1.4, 3.32, 4.44])
    keys.append([0.853478, 0.854933, 0.425116])

    names.append("RShoulderPitch")
    times.append([0.64, 1.4, 2.08, 2.64, 3.32, 4.44])
    keys.append([0.247016, -1.17193, -1.0891, -1.26091, -1.14892, 1.02015])

    names.append("RShoulderRoll")
    times.append([0.64, 1.4, 2.08, 2.64, 3.32, 4.44])
    keys.append([-0.242414, -0.954191, -0.460242, -0.960325, -0.328317, -0.250085])

    names.append("RWristYaw")
    times.append([1.4, 3.32, 4.44])
    keys.append([-0.312978, -0.303775, 0.182504])

    motion.angleInterpolation(names, keys, times, True)

def WipeForehead():
    names = list()
    times = list()
    keys = list()

    names.append("HeadPitch")
    times.append([0.96, 1.68, 3.28, 3.96, 4.52, 5.08])
    keys.append([-0.0261199, 0.427944, 0.308291, 0.11194, -0.013848, 0.061318])

    names.append("HeadYaw")
    times.append([0.96, 1.68, 3.28, 3.96, 4.52, 5.08])
    keys.append([-0.234743, -0.622845, -0.113558, -0.00617796, -0.027654, -0.036858])

    names.append("LElbowRoll")
    times.append([0.8, 1.52, 3.12, 3.8, 4.36, 4.92])
    keys.append([-0.866668, -0.868202, -0.822183, -0.992455, -0.966378, -0.990923])

    names.append("LElbowYaw")
    times.append([0.8, 1.52, 3.12, 3.8, 4.36, 4.92])
    keys.append([-0.957257, -0.823801, -1.00788, -0.925044, -1.24412, -0.960325])

    names.append("LHand")
    times.append([1.52, 3.12, 3.8, 4.92])
    keys.append([0.132026, 0.132026, 0.132026, 0.132026])

    names.append("LShoulderPitch")
    times.append([0.8, 1.52, 3.12, 3.8, 4.36, 4.92])
    keys.append([0.863599, 0.858999, 0.888144, 0.929562, 1.017, 0.977116])

    names.append("LShoulderRoll")
    times.append([0.8, 1.52, 3.12, 3.8, 4.36, 4.92])
    keys.append([0.286815, 0.230059, 0.202446, 0.406468, 0.360449, 0.31903])

    names.append("LWristYaw")
    times.append([1.52, 3.12, 3.8, 4.92])
    keys.append([0.386526, 0.386526, 0.386526, 0.386526])

    names.append("RElbowRoll")
    times.append([0.64, 1.36, 2.96, 3.64, 4.2, 4.76])
    keys.append([1.28093, 1.39752, 1.57239, 1.24105, 1.22571, 0.840674])

    names.append("RElbowYaw")
    times.append([0.64, 1.36, 2.96, 3.64, 4.2, 4.76])
    keys.append([-0.128898, -0.285367, -0.15651, 0.754686, 1.17193, 0.677985])

    names.append("RHand")
    times.append([1.36, 2.96, 3.64, 4.76])
    keys.append([0.166571, 0.166208, 0.166571, 0.166208])

    names.append("RShoulderPitch")
    times.append([0.64, 1.36, 2.96, 3.64, 4.2, 4.76])
    keys.append([0.0767419, -0.59515, -0.866668, -0.613558, 0.584497, 0.882091])

    names.append("RShoulderRoll")
    times.append([0.64, 1.36, 2.96, 3.64, 4.2, 4.76])
    keys.append([-0.019984, -0.019984, -0.615176, -0.833004, -0.224006, -0.214801])

    names.append("RWristYaw")
    times.append([1.36, 2.96, 3.64, 4.76])
    keys.append([-0.058334, -0.0521979, -0.067538, -0.038392])

    motion.angleInterpolation(names, keys, times, True)

#Walk

#Sounds

#LEDs

def blink():
    led.fadeRGB("FaceLeds", 0xffffff);
    time.sleep(0.5);
    led.fadeRGB("FaceLeds", 0x000000);
    time.sleep(0.5);
    led.fadeRGB("FaceLeds", 0xffffff);

#Sensors

def sonar():
    #Subscribe to Sonars, launch them(at Hardware level) and start data acquisition
    sonar.subscribe("TestApplication")

    #Retrieve sonar data from ALMemory (distance in meters)
    memory.getData("Device/SubDeviceList/US/Right/Sensor/Value")

    #Unsubscribe from sonars and stop them (at Hardware level)
    sonar.unsubscribe("TestApplication")

def recordVideo():
    recordFolder = "/home/nao/recordings/cameras/"

    # 0 - 160*120  1 - 320*240  2 - 640*480
    video.setResolution(2)

    # 0 - Top  1 - Bottom
    video.setCameraID(0)

    video.setVideoFormat("MJPG")

    video.startRecording(recordFolder, "robertaVideo")

def takePicture():
    recordFolder = "/home/nao/recordings/cameras/"

    # 0 - 160*120  1 - 320*240  2 - 640*480  3 - 1280*960
    photo.setResolution(1)

    # 0 - Top  1 - Bottom
    photo.setCameraID(0)

    photo.setPictureFormat("jpg")

    photo.takePicture(recordFolder, "robertaPhoto")
