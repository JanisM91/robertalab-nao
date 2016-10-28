import hashlib

#generatedToken needs to be input
f = open("md5naotoken.txt","w")
f.write(hashlib.md5(generatedToken).hexdigest())
