import os
try:
    os.system("cd bin && java Server && cd ..")
except KeyboardInterrupt:
    print("quit")