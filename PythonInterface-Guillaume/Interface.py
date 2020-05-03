import time
import serial

serPort = 'COM2'
baudRate = 9600

serial = serial.Serial(serPort, baudRate, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE,bytesize=serial.EIGHTBITS,timeout=0)

'''
Lecture des valeur du port serial
Print de la valeur recue
'''
def readValue() :
    line = serial.readline()   # read a '\n' terminated line
    value = int(line)
    if (value == 1) :
        print('OK \n')
    elif (value == 2) :
        print('Alerte \n')
    else :
        print('distance de : '+ value + ' cm \n')

'''
Envois de la valeur max a la sonde
'''
def sendMaxValue(value):
    print('envois de la valuer ' + value + ' a la pic comme valeur minimum \n')
    time.sleep(1)
    line = serial.writelines('!'+ value)
    return line

'''
Fonctione principale de gestion du programme
'''
def main():
    print("Entré la valeur minimum de distance de la sonde")
    value = input()
    sendMaxValue(value)
    print("Valeur" + value + "envoyée comme valeur minimum \n")

    try :
        while 1:
            if (serial.inWaiting() > 0):
                readValue()

            time.sleep(1)
    except KeyboardInterrupt :
        pass

    serial.close()


'''
Lancement du programme
'''
main()
