import time
import serial
import os
import tkinter as tk

serPort = 'COM2'
baudRate = 9600

serial = serial.Serial(serPort, baudRate)

'''
Lecture des valeur du port serial
Print de la valeur recue
'''
def readValue() :
    #os.system('cls')
    line = (serial.readline()).decode()   # read a '\n' terminated line
    line = line[:-1]
    if (line == '1') :
        print('OK')
        inputVal_text.set('OK')
    elif (line == '2') :
        print('Alerte')
        inputVal_text.set('Alerte')
    else :
        print('distance de : '+ line + ' cm')
        outputVal_text.set('distance de : '+ line + ' cm')
    serial.flush()

'''
Envois de la valeur minimal a la sonde
'''
def sendMinValue():
    print("bouton")
    minValue = inputMinVal.get()
    serial.writelines(('!' + minValue).encode())
    print('Valeur ' + minValue + ' envoyée comme valeur minimale')

'''
Lancement du programme
'''
window = tk.Tk()
window.title("Projet Elec")
window.geometry("400x200")

labelMinVal = tk.Label(window, text='Valeur d\'alarme :')
labelMinVal.pack()

inputMinVal = tk.Entry(window, textvariable=int, width=6)
inputMinVal.pack()

inputVal_text = tk.StringVar()
intputVal = tk.Label(window, textvariable=inputVal_text)
intputVal.pack()

sendBtn = tk.Button(window, text='send', command=sendMinValue)
sendBtn.pack()

outputVal_text = tk.StringVar()
outputVal = tk.Label(window, textvariable=outputVal_text)
outputVal.pack()

window.update()

try:
    print("Ouverture du port COM2")
    serial.close()
    serial.open()
    print("Port serial COM2 ouvert \n")

    time.sleep(2)
    try:
        #print("Appuyez sur une touche pour terminer")
        while 1:
            if (serial.inWaiting() > 0):
                readValue()
            window.update()
    except KeyboardInterrupt:
        pass
finally:
    serial.close()