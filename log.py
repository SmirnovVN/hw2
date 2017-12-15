#!/usr/bin/python
import time
import random
import sys

lines = int(sys.argv[1])

with open('log.log','w') as f:
    for i in range(0, lines):
        tp = int(time.time()) + i*1000
        resp = random.randint(1, 10)
        value = random.randint(10, 100)
        f.write('%s,%s,%s\n' % (resp,tp,value))

