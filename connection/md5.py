import hashlib

f = open("md5nao.txt","w")
f.write(hashlib.md5(generatedToken).hexdigest())
