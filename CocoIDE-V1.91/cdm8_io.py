#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# Python3 and 2
from __future__ import absolute_import, division, print_function

# CdM8 IDE and emulator
# (c) M L Walters and A Shafarenko June-July 2018
# 

# July 2018, M L Walters
# V1.6 Seperate out IO Ports into (this!) seperate module:

try:
    # Python 3 tk
    import tkinter as tk
    from tkinter import ttk
    from tkinter import filedialog
    from tkinter import messagebox
    import tkinter.font as font
except:
    # Python 2 tk (runs but not exhaustively tested!)
    # Ames lib (sendfile.py) not python 2 compatible (urllib)
    import Tkinter as tk
    import ttk
    import tkFileDialog as filedialog
    import tkMessageBox as messagebox
    import tkFont as font

# Global vars
interruptVector = 0
interrupt = None

class IOport():
    """
    To create an IO port, inherit from IOport. Parameters parent, portno, portAdr
    and name to IOport.--init__()
    the New class should create the main display on self.IOdispwin
    The name of the new IO port (class) is the name shown in the IO port drop list
    New class shouls contain two mthods:
     __init__() # called when instantiating new port
     updatePort() # updates the port display. 
     Note:
            self.portIPvals = {} # dictionary of input port {adresss:value} pairs
            self.portOPvals = {} # dictionary of output port {address:value) pairs
        These are automatically updated and read by CocoIDE, your updatePort() method
        should just update the Port Display
            
    """
    # Should be shared between all child object ports
    global interrupt  # Interrupt signal, set to True to raise interrupt
    global interruptVector # Interrupt vector default
    def __init__(self, parent=None, portno=0, portAdr=0xf0, name="Untitled"):
        self.portno = portno
        self.parent = parent
        self.portAdr = portAdr+self.portno #default memory Address
        self.prevAdr=None
        self.name = name
        # Dictionaries to hold IP and OP values. Set to None in Super class if not used
        # Define more entries if more than one adr/port/IOs required etc.
        self.portIPvals = {} # default input port adresss:values
        self.portOPvals = {} # default output port addresses:values
        ## Create the Standard IO port container - Super Class needs only fill self.IOdispwin
        #  with GUI widgets etc.
        self.portFrame = tk.Frame(self.parent, width=358)
        #self.portFrame.grid(row=self.portno, sticky="ew")
        self.portFrame.pack(fill=tk.X, expand=1)
        #self.portFrame.columnconfigure(0, weight=1)
        
        # Title/header/standard widgets frame
        self.portheaderFrame = tk.Frame(self.portFrame, relief="raised", border=1)
        self.portheaderFrame.pack(side=tk.TOP, fill=tk.X, expand=1)
        
        # Port Address Entry Box 
        self.adrBoxtxt = tk.StringVar()
        self.adrBoxtxt.set("%02X" % self.portAdr)
        self.IOadrBox = tk.Entry(self.portheaderFrame, width=2, text=self.adrBoxtxt)
        self.IOadrBox.pack(side=tk.LEFT, fill=tk.X)
        self.IOadrBox.bind('<Return>', self.updatePortAdr)
        
         
        ## Delete port button?
        # Make the button smaller to look like a close button
        # size in pixels
        f = tk.Frame(self.portheaderFrame, height=15, width=15)
        f.pack_propagate(0) # Do not shrink
        f.pack(side=tk.RIGHT)
        self.portcheck = tk.IntVar()
        self.portcheck.set(0)
        self.delButton = tk.Button(f, text="X", command=self.removePort )# variable=self.portcheck)
        self.delButton.pack(fill=tk.BOTH, expand=1)# 
        
        # Make header look nice!
        self.spacer = tk.Label(self.portheaderFrame, width=12)
        self.spacer.pack(side=tk.LEFT, expand=0)
        
        # Configure a larger font for displays
        self.dispfont = font.nametofont("TkDefaultFont").copy()  # Get its font
        self.dispfont.config(size=int(self.dispfont["size"]*1.3), weight="bold")   # Modify font attributes
        
        # Port type/title
        self.title = tk.Label(self.portheaderFrame, text=self.name.ljust(30))
        self.title.pack(side=tk.LEFT, fill=tk.X, expand=1)
        
        # IO port GUI area/panel
        self.IOdispwin = tk.Frame(self.portFrame, height=15, relief="sunken")#,bg="green")
        self.IOdispwin.pack(side=tk.TOP, fill=tk.X, expand=1)
        #self.portno += 1
    
    def getIPadr(self):
        return self.portIPvals.keys()
        
    def getOPadr(self):
        return self.portOPvals.keys()
    
    def getIPval(self):
        return self.portIPvals.values()
    
    def setOPval(self, adr, val):
        if  adr in self.portOPvals.keys():
            #print("**", adr, val)
            self.portOPvals[adr]= val
            self.updatePort()
            
    def resetPort(self, event=None):
        for item in self.portOPvals.keys():
            self.portOPvals[item] = 0
        self.updatePort() 

    def updatePortAdr(self, event=None):
        #Check value returned
        error = None
        numstr = self.adrBoxtxt.get().upper()
        if len(numstr) == 2:
            for char in numstr:
                if char not in "0123456789ABCDEF":
                    error = True
        else: 
            error = True
        # if ok, update port address
        if not error:
            self.adrBoxtxt.set(numstr)
            numIPadrs = len(self.portIPvals)
            numOPadrs = len(self.portOPvals)
            self.portIPvals = {}#: del self.portIPvals[self.portAdr]
            self.portOPvals = {}#: del self.portOPvals[self.portAdr]
            self.portAdr = int(numstr, 16)
            for n in range(numIPadrs):
                #print("n=",n, self.portAdr+n)
                self.portIPvals[(self.portAdr+n)] = 0x00
            startAdr = self.portAdr
            for n in range(numOPadrs):
                #print("n=",n, self.portAdr+n)
                self.portOPvals[(self.portAdr+n)] = 0x00
            #print(self.portIPvals)# debug
            #print(self.portOPvals)# debug
        else:
            self.adrBoxtxt.set("%04X" % self.portAdr)
        self.updatePort()
    
    def _updatePort(self):
        self.parent.event_generate("<<updatePort>>", state=256)
        
    def removePort(self, event=None):
        self.portFrame.destroy() # Remove itself!
        # Generate event in Super class to clear up IOports list etc.???
        self.parent.event_generate("<<updatePort>>", state=str(self.portno))
    
    def updateFont(self):
        newsize = font.nametofont("TkDefaultFont")["size"]  # Get its font
        self.dispfont.config(size=int(newsize *1.3), weight="bold")
        

### IO Ports (inherit from IOport)

class OP_LEDs_8x1(IOport):
    
    def __init__(self, parent=None, portno=0, portAdr=0xf0):
        self.name=self.__class__.__name__ # IO port name for title
        IOport.__init__(self, parent, portno, portAdr, self.name)
        self.portOPvals = {self.portAdr: 0x00}# Only outputs needed
        self.portIPvals={}
        # Create the port GUI
        self.opLabels=[]
        for n in range(8):
            self.opLabels.append(tk.Label(self.IOdispwin, text=str(n), width=2, 
                fg="white", bg="green", relief="sunken", border=3, padx=7))
            self.opLabels[-1].pack(side=tk.RIGHT, expand=1)
        #self.updatePort()
    
    def updatePort(self, event=None):
        # Check if address needs updating
        if self.portAdr != self.prevAdr:
            self.portOPvals = {self.portAdr:0x00}
            self.portIPvals = {}
            self.prevAdr=self.portAdr
            
        
        # Then update the port IO registers    
        newval= self.portOPvals[self.portAdr]
        for n in range(len(self.opLabels)):
            mask=0b00000001
            for bit in range(8):
                if newval & mask:
                    self.opLabels[bit].config(bg="yellow", fg="black")
                else:
                    self.opLabels[bit].config(bg="green", fg="white")
                mask = mask << 1
        IOport._updatePort(self) # Call back to update CocoIDE
"""
class IP_Keybd_7Bit(IOport):
    def __init__(self, parent=None, portno=0, portAdr=0xf0):
        self.name=self.__class__.__name__
        IOport.__init__(self, parent, portno, portAdr, self.name)
        self.portOPvals = {}
        self.portIPvals = {self.portAdr:0} # Only Input port needed
        
        # Great the port display
        # Vars
        self.prevIntvec = 0
        # tk vars
        self.intEnableVar = tk.IntVar()
        self.intEnableVar.set(0) 
        self.intVectorVar = tk.StringVar()
        self.intVectorVar.set("0")
        self.bufVar = tk.StringVar()
        self.bufVar.set("ABC")
        # widgets
        self.intLabel = tk.Label(self.IOdispwin, text="Interrupt=Bit7")
        self.intLabel.grid(row=0, column=0, sticky="s")
        self.intCheckbox = tk.Checkbutton(self.IOdispwin, variable=self.intEnableVar)
        self.intCheckbox.grid(row=0, column=1, sticky="w")
        self.vecLabel = tk.Label(self.IOdispwin, text="Int Vector=")
        self.vecLabel.grid(row=1, column=0)#, sticky="w")
        self.vectorEntry = tk.Entry(self.IOdispwin, textvariable=self.intVectorVar,
             width=1)
        self.vectorEntry.grid(row=1, column=1)#, sticky="w")
        self.vectorEntry.bind("<Return>", self.updateVec)
        tk.Label(self.IOdispwin, text="Buffer=16 Chars, Rdy=Bit7").grid(row=0, column=3)#, rowspan=2)
        tk.Label(self.IOdispwin, text="    ").grid(row=0, column=2, rowspan=2)
        self.bufEntry = tk.Entry(self.IOdispwin, text="",# validate="all", validatecommand=self.updatePort,
             width=17, font=self.dispfont, fg="white", bg="royal blue")
        self.bufEntry.bind("<KeyRelease>", self.updatePort)#
        self.bufEntry.grid(row=1, column=3)
        
    def updateVec(self, event=None):
        print("update vector")
             
        intvec = self.intVectorVar.get()
        if intvec[0] in "0123":
            self.prevIntvec = intvec[0]
            self.intVectorVar.set(intvec[0])
        else:
            self.intVectorVar.set(str(self.prevIntvec))
        return
        
    def updatePort(self, event=None):
        #print("keyb updatePort")
        # Check and update buffer and IP port value/char
        keybstring = ""
        if event: # Input string has changed, linit to 16 chars
            keybstring = self.bufEntry.get()
            print("*",keybstring)
            self.bufEntry.delete(0, tk.END)
            self.bufEntry.insert(0,keybstring[0:16])
        # Update Input port value (next char)
        
        if keybstring == "":
            self.portIPvals[self.portAdr] = 0 
        else: 
            self.portIPvals[self.portAdr] = ord(keybstring[0]) | 0b10000000   
            # Update interrupt state
            if self.intEnableVar.get() == 1:
                self.interrupt = True
                
            else:
                self.interrupt = False
        IOport._updatePort(self)# Call back to update CocoIDE
        return "break"    
"""            
                    
class IP_Buttons_8x1(IOport):
    def __init__(self, parent=None, portno=0, portAdr=0xf0):
        self.name=self.__class__.__name__
        IOport.__init__(self, parent, portno, portAdr, self.name)
        self.portOPvals = {}
        self.portIPvals = {self.portAdr:0} # Only Input port needed
        # Create the port GUI
        self.ipButtons=[]
        self.buttonNum = None
        for n in range(8):
            self.ipButtons.append(tk.Button(self.IOdispwin, text=str(7-n), width=1, 
                fg="white", bg="royal blue", highlightcolor="royal blue",
                relief="raised", border=3, padx=7,
                command=lambda x=(7-n) : self.updatePort(butt=x)))
            self.ipButtons[-1].pack(side=tk.LEFT, expand=1)
            #self.ipButtons[-1].config("char",str(n))
            self.btn =None
            for btn in self.ipButtons: 
                btn.bind("<Button-1>", self.updatePort)
                #btn.bind("<Return>", self.updatePort)
                btn.bind("<KeyRelease-Return>", self.updatePort)
        self.updatePort()
        
    def updatePort(self, event=None, butt=None ):
        # If port adr changed, update port address range
        #print(self.prevAdr, self.portAdr)
        if self.portAdr != self.prevAdr:
            self.portIPvals[self.portAdr] = 0x00
            self.prevAdr=self.portAdr
        
        # Update the IO port display and register values
        portval = 0
        
        #if event: print(event.keysym)#debug
        if event and not butt: 
            n = 7
            for btn in self.ipButtons:
                if btn == event.widget: # find which button was pressed
                    #print(event.widget, "Button ",n)
                    butt = n
                else:
                    n -= 1
            portval += 2**butt
        self.portIPvals[self.portAdr] = portval
        IOport._updatePort(self)
      
class OP_Disp_16xChr(IOport):
    def __init__(self, parent=None, portno=0, portAdr=0xe0):
        self.name=self.__class__.__name__
        portno=0 # Always zero for this
        IOport.__init__(self, parent, portno, portAdr, self.name)
        # Create the port GUI
        self.portOPvals={}
        self.prevAdr = None
        for n in range(16):
            self.portOPvals[self.portAdr+n] = 0x00   
        self.adrRangeLabel = tk.Label(self.IOdispwin, text=" to\n  %02X" % (self.portAdr+15))
        self.adrRangeLabel.grid()
        spacer = tk.Label(self.IOdispwin, text="", width=2)
        spacer.grid(row=0, column=1)
        
        self.charLabels=[]
        for n in range(16):
            self.charLabels.append(tk.Label(self.IOdispwin, text="", width=1,# padx=1,
                font=self.dispfont, pady=3, border=3,relief="raised", bg="green", fg="white"))
            self.charLabels[-1].grid(row=0, column=(n+2), sticky="ns")
        self.updatePort() 
    
    def updatePort(self, event=None):
        
        # If port adr changed, update address range
        #print(self.prevAdr, self.portAdr)
        if self.portAdr != self.prevAdr:
            self.portOPvals = {}
            self.prevAdr=self.portAdr
            self.adrRangeLabel.config(text="to\n"+"%02X" % (self.portAdr+15))
            #
            for n in range(16): 
                self.portOPvals[self.portAdr+n] = 0x00
            IOport._updatePort(self) # Call back to update CocoIDE
        
        #Update port display label chars
        n = 0
        for key, val in self.portOPvals.items():
            #print("*",key, val)
            if val != 0:
                self.charLabels[n].config(text=chr(val))
            else:
                self.charLabels[n].config(text="")
            n += 1
        return
      
class OP_HexDisps_xN(IOport):
    
    def __init__(self, parent=None, portno=0, portAdr=0xf0):
        self.name=self.__class__.__name__ # IO port name for title
        IOport.__init__(self, parent, portno, portAdr, self.name)
        self.portOPvals = {}# Only outputs needed
        self.noPorts = tk.StringVar()
        self.noPorts.set("3")
        self.charLabels = []
        self.spacer = []
        self.prevPorts = None
        # Display
        self.adrRangeLabel = tk.Label(self.IOdispwin, text="to %02X " % (self.portAdr+int(self.noPorts.get())-1))
        self.adrRangeLabel.grid(row=0, column=0, columnspan=2, sticky="w")
        self.noPortsLabel = tk.Label(self.IOdispwin, text="N=")
        self.noPortsLabel.grid(row=1, column=0, sticky="e")
        self.noPortsEntry = tk.Entry(self.IOdispwin, text=self.noPorts, width = 1)
        self.noPortsEntry.bind("<Return>", self.updatePort)#self.portNupdate)
        self.noPortsEntry.grid(row=1, column=1, sticky="e")
        tk.Label(self.IOdispwin, text=" ", width=1).grid(row=0, column=2, rowspan=2)
        self.charLabels=[]
        #self.portNupdate()
        self.updatePort()
        
    def updatePort(self, event=None):
        # Called by CocoIE if OP Port address is written to or changed

        # Check if num of OP ports or addreses have changed
        noports = int(self.noPorts.get()[0])
        self.noPorts.set(str(noports)) # Just use first character
        if str(noports) in "123456" and noports != self.prevPorts:
            #print("Redraw Hex disp")# debug
            # redraw the display
            for disp in self.charLabels:
                disp.destroy()
            self.charLabels=[]
            for disp in self.spacer:
                disp.destroy()
            self.spacer = []
            for n in range(noports): 
                self.charLabels.append(tk.Label(self.IOdispwin, text="", width=1, padx=1,
                    font=self.dispfont, pady=3, border=3,relief="raised", bg="green", fg="white"))
                self.charLabels[-1].grid(row=0, column=(n*3+4), rowspan=2, sticky="nsew")
                self.charLabels.append(tk.Label(self.IOdispwin, text="", width=1, padx=1,
                    font=self.dispfont, pady=3, border=3,relief="raised", bg="green", fg="white"))
                self.charLabels[-1].grid(row=0, column=(n*3+5), rowspan=2, sticky="nsew")
                self.spacer.append(tk.Label(self.IOdispwin, text="", width=0, padx=3))
                self.spacer[-1].grid(row=0, column=(n*3+6),rowspan=2, sticky="nsew")
        else: # Bad value. not in range 1 to 6, or alpha char
            self.noPorts.set(self.prevPorts)
        
        # Check if OP addresses need updating
        numports = int(self.noPorts.get())
        if self.portAdr != self.prevAdr or numports != self.prevPorts:
            #print("Updating Adrs")#debug
            self.portOPvals={}
            self.adrRangeLabel.config(text="to %X" % (self.portAdr+numports-1))
            for n in range(numports):
                self.portOPvals[self.portAdr+n] = 0x00
            self.prevAdr=self.portAdr
            self.prevPorts = numports
        
        # Then update the port display
        labIndex = 0
        for n in range(len(self.portOPvals)):    
            newval = int(self.portOPvals[self.portAdr+n])
            self.charLabels[labIndex].config(text="%X" % (newval & 0b00001111))
            labIndex += 1
            self.charLabels[labIndex].config(text="%X" % (newval >> 4))
            labIndex += 1
        IOport._updatePort(self) # Call back to update CocoIDE/Emu

class OP_DecDisps_2xN(IOport):
    
    def __init__(self, parent=None, portno=0, portAdr=0xf0):
        self.name=self.__class__.__name__ # IO port name for title
        IOport.__init__(self, parent, portno, portAdr, self.name)
        self.portOPvals = {}# Only outputs needed
        self.noPorts = tk.StringVar()
        self.noPorts.set("3")
        self.charLabels = []
        self.spacer = []
        self.prevPorts = None
        # Display
        self.adrRangeLabel = tk.Label(self.IOdispwin, text="to %02X " % (self.portAdr+int(self.noPorts.get())-1))
        self.adrRangeLabel.grid(row=0, column=0, columnspan=2, sticky="w")
        self.noPortsLabel = tk.Label(self.IOdispwin, text="N=")
        self.noPortsLabel.grid(row=1, column=0, sticky="e")
        self.noPortsEntry = tk.Entry(self.IOdispwin, text=self.noPorts, width = 1)
        self.noPortsEntry.bind("<Return>", self.updatePort)#self.portNupdate)
        self.noPortsEntry.grid(row=1, column=1, sticky="e")
        tk.Label(self.IOdispwin, text=" ", width=1).grid(row=0, column=2, rowspan=2)
        self.charLabels=[]
        #self.portNupdate()
        self.updatePort()
        
    def updatePort(self, event=None):
        # Called by CocoIE if OP Port address is written to or changed
        # Check if num of OP ports or addreses have changed
        noports = int(self.noPorts.get()[0])
        self.noPorts.set(str(noports)) # Just use first character
        if str(noports) in "123456" and noports != self.prevPorts:
            #print("Redraw Hex disp")# debug
            # redraw the display
            for disp in self.charLabels:
                disp.destroy()
            self.charLabels=[]
            for disp in self.spacer:
                disp.destroy()
            self.spacer = []
            for n in range(noports): 
                self.charLabels.append(tk.Label(self.IOdispwin, text="", width=1, padx=3,
                    font=self.dispfont, pady=3, border=3,relief="raised", bg="green", fg="white"))
                self.charLabels[-1].grid(row=0, column=(n*3+4), rowspan=2, sticky="nsew")
                self.charLabels.append(tk.Label(self.IOdispwin, text="", width=1, padx=3,
                    font=self.dispfont, pady=3, border=3,relief="raised", bg="green", fg="white"))
                self.charLabels[-1].grid(row=0, column=(n*3+5), rowspan=2, sticky="nsew")
                #self.spacer.append(tk.Label(self.IOdispwin, text="", width=1))
                #elf.spacer[-1].grid(row=0, column=(n*3+6),rowspan=2, sticky="nsew")
        else: # Bad value. not in range 1 to 6, or alpha char
            self.noPorts.set(self.prevPorts)
        
        # Check if OP addresses need updating
        numports = int(self.noPorts.get())
        if self.portAdr != self.prevAdr or numports != self.prevPorts:
            #print("Updating Adrs")#debug
            self.portOPvals={}
            self.adrRangeLabel.config(text="to %02X" % (self.portAdr+numports-1))
            for n in range(numports):
                self.portOPvals[self.portAdr+n] = 0x00
            self.prevAdr=self.portAdr
            self.prevPorts = numports
            

        # Then update the port display
        labIndex = 0
        for n in range(len(self.portOPvals)):    
            newval = int(self.portOPvals[self.portAdr+n])
            nibtxt = "0123456789.-+*/="[newval & 0b00001111]
            self.charLabels[labIndex].config(text=nibtxt)
            nibtxt = "0123456789.-+*/="[newval >> 4]
            labIndex += 1
            self.charLabels[labIndex].config(text=nibtxt)
            labIndex += 1
        IOport._updatePort(self) # Call back to update CocoIDE/Emu 
        
if __name__ == "__main__":
    # test
    myapp = tk.Tk()
    #Buttons_8x1(myapp)
    #LEDs_8x1(myapp)
    #Disp_16xChr(myapp)
    #HexDisps_xN(myapp)
    IP_Keybd_7Bit(myapp)
    myapp.mainloop()
