import hashlib

f = open("md5nao.txt","w")
#check path
f.write(hashlib.md5(open("/~/Roberta.py", 'rb').read()).hexdigest())
