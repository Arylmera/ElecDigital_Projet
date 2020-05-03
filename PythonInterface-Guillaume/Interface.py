import time
import serial
#from termcolor import colored, cprint

serPort = 'COM2'
baudRate = 9600

serial = serial.Serial(serPort, baudRate)

'''
Lecture des valeur du port serial
Print de la valeur recue
'''
def readValue() :
    line = (serial.readline()).decode()   # read a '\n' terminated line
    line = line[:-1]
    if (line == '1') :
        print('OK')
        #cprint("OK", 'green')
    elif (line == '2') :
        print('Alerte')
        #cprint("Alerte", 'red')
    else :
        print('distance de : '+ line + ' cm')
    serial.flush()


'''
Lancement du programme
'''
try:
    print("Ouverture du port COM2")
    serial.close()
    serial.open()
    print("Port serial COM2 ouvert \n")
    print("Entré la valeur minimum d'alerte pour la sonde")
    minValue = input()
    print("Envois de "+ minValue +" comme valeur minimum")
    serial.writelines( ('!'+ minValue).encode() )

    time.sleep(2)
    try:
        print("Appuyez sur une touche pour terminer")
        while 1:
            if (serial.inWaiting() > 0):
                readValue()

            time.sleep(0.1)
    except KeyboardInterrupt:
        pass
finally:
    serial.close()