import time
import serial
import tkinter as tk

serPort = 'COM2'
baudRate = 9600

serial = serial.Serial(serPort, baudRate)

'''
Lecture des valeur du port serial
Print de la valeur recue dans le GUI
'''
def readValue() :
    line = (serial.readline()).decode()   # read a '\n' terminated line
    line = line[:-1]
    if (line == '1') :
        print('OK')
        inputVal_text.set('OK')
        intputVal.config(fg='green')
    elif (line == '2') :
        print('Alerte')
        inputVal_text.set('Alerte')
        intputVal.config(fg = 'red')
    else :
        print('distance de : '+ line + ' cm')
        outputVal_text.set('distance de : '+ line + ' cm')
    serial.flush()

'''
Envois de la valeur minimal a la sonde depuis l'entrée dans le GUI
'''
def sendMinValue():
    minValue = inputMinVal.get()
    if (int(minValue) < 10 ) :
        minValue = str(0) + minValue
    if (int(minValue) < 100 ) :
        minValue = str(0) + minValue
    serial.write(('!' + minValue + '\n').encode())  # envois du string sous forme !<Value>\n
    print('Valeur ' + minValue + ' envoyée comme valeur minimale')

'''
Lancement du programme et du GUI
'''
window = tk.Tk()
window.title("Projet Elec")
# définition des espaces d'affichages
paramFrame = tk.Frame(window)
outputFrame = tk.Frame(window)
# définition du label d'alarme
labelMinVal = tk.Label(paramFrame, text='Valeur d\'alarme :')
labelMinVal.pack()
# définition du champ d'insertion des données de distance minimal
inputMinVal = tk.Entry(paramFrame, textvariable=int, width=6)
inputMinVal.insert(0, 100)
inputMinVal.pack()
# définition du bouton d'envois de la donnée minimal
sendBtn = tk.Button(paramFrame, text='send', command=sendMinValue)
sendBtn.pack()
# définition de l'affchage de la valeur minimale
inputVal_text = tk.StringVar()
intputVal = tk.Label(outputFrame, textvariable=inputVal_text)
intputVal.pack()
# définition du champ de réception de valeur
outputVal_text = tk.StringVar()
outputVal = tk.Label(outputFrame, textvariable=outputVal_text)
outputVal.pack()
# mise en place des blocs d'affichage
paramFrame.pack(padx=1, pady=1)
outputFrame.pack(padx=20, pady=20)
# mise a jours de la window
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
