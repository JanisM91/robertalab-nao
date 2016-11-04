from naoqi import ALProxy

motion = ALProxy("ALMotion","127.0.0.1",9559)

#in meters and radians(pi/2 = 90 degrees)
x = 0.2
y = 0.2
theta = 1.5709

#will block until move task is finished
motion.moveTo(x,y,theta);
