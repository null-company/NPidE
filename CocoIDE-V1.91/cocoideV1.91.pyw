#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# Python3 and 2
from __future__ import absolute_import, division, print_function

# Coco De Mere 8 IDE and emulator
# (c) M L Walters June-July 2018
# Code/Modules based on cocoemu.py, cocas.py and cocol.py
    # V1 By Prof. Alex Shaferenko.  July 2016
    # V1.1. GUI modifications by M L Walters, August/Sept 2015
    # V1.2. Fixed bug in app.reload() where memory not displaying correctly

# CocoIDE V0.4 Radical Overhaul of GUI M.L.Walters July-Oct 2016
# V0.5 -    V0.8 Updates to GUI for Win and MAC versions. minor bugs fixed
# V0.9 -    Updates and fixes.(also in cocas.py V2.3)
# V0.91 -   Initial Save error bug fixed (only affects cocoide.pyw)
# v0.92 -   Adjusts default font to fit screen better!
#           Improved Watch display of binary strings
#           Saved breakpoints if assembly list not changed.
#           Editor now supports additional keyboard shortcuts:
#               Ctrl+Left Arrow – Move cursor to beginning of previous word.
#               Ctrl+Right Arrow – Move cursor to beginning of next word
#               Ctrl+Backspace – Delete previous word.
#               Ctrl+Delete – Delete next word.
#              Selecting Text. All of the above shortcuts can be combined
#                   with the Shift key to select text.
#               Ctrl+A – Select all text.
#               Ctrl+C, Ctrl+Insert – Copy selected text.
#               Ctrl+X, Shift+Delete – Cut selected text.
#               Ctrl+V, Shift+Insert – Paste text at cursor.
#               Ctrl+Z – Undo.
#               Ctrl+Y – Redo.
#           Note: Mac users Use the Option key instead of the Ctrl key?
#           Included IF to Ames system (if sendfile.py is in program directory)
#           Adjusts to screen resolution automatically for small screens.
#           For large screens, option to set presenter mode (fills screen). Also
#           good for smaller laptop/notebook screens. Can be set in cdm8_xxx.py file
#
# V0.93 -   Now able to save Memory Image file (for loading into Logisim
#           Use Meta comments #$<option> to format Memory Watch labels in Watch
#           Window:
#            #$str = String
#            #$dec = Signed Decimal
#            #$hex = Hexadecimal
#            #$bin = Binary
#           "Run From" dropdown menu to select entry point (picks up any label
#           beginning with _ e.g. _Start: or _Start> )
#           Disables editing on AMES meta comment lines containing "#!" while
#           Ames is running. E.g. _Q1> #! cannot be edited
#           Added auto-indent feature to editor
# V0.94 -   Fixed tab bug! Now lines up correctly
#           Able to use command line options: <filename> and
#           -p for presenter screenmode (overides setting in cdm8_asm.py file)
# V0.941    Fixed Mac bug for Compile/Save Image buttons???
# V0.942    Tidied up runFrom options re. reset and clearing memory etc.
#           Also changed Exit (save, cancel) options. More intuitive.
# V0.95     Distribution version for deployment
# V0.951    Changesd AMES exit behaviour to stop saving the file twice
#           Fixed #! behaviour (16/1/17)
# V0.96     Added Ames Logisim download/open/close capability for BPT
# V0.97     Passed final tests on LAb PCs with Ames extensions
# V0.971    Save pointer display
# V0.98     Ames control keys improved filtering!
#           Changed circ save file name (._circ.circ). Allows student to Save As?
# V0.982    Fixed OSX/Mac save problem??
# V0.983    If program to large for memory - Error message implemented
# V0.991    Nasty Macro compile error fixed in cocas.py (V2.4)
# V0.992    Cut/Paste fixed bug if Ames is running!
# V0.993    Fixed CDM8 compiler problem in cocas.py and this file. Note, emulator not working
#           with new stack instructions yet.
# V0.994    Put Emulator class in seperate file/module
# V1.0      Remove Logisim Ames extensions - now to be coded in Logisim by D Bowes.
# V1.1      Fixed bugs in AMES I/F
# V1.2      Changed AMES to allow post Timeout load/submit.
# V1.3      AMES changes tested in labs and from home.
# V1.4      Support for saving object file and Cocol linker GUI added
# V1.41     Ames "New" bug fixed
# V1.5      In development. AMES Python2/3 compatibility fixed, 
#           Mousewheel bug fixed, EditorCodelist textsize bug fixed (Windows 10 problem!)
# V1.52     Various improvements to Editor and GUI.
#           Also most of Steve's bugs/suggestions inc. Selfmodifying code warning.
#           Implemented Harvard Arch option.
# V1.53     Bug fixes/Testing, 
#           GUI improvements - Layout, Search and goto Line functions.
# V1.6      Implement IO Panel(/Plugins?):
#           Basic IO: Buttons, LEDs, Hex/Dec displays and 16 Alha Char Display.
# V1.7      Bug fixes, Editor text bindings (Tab ->text block, Ctrl+Tab <- unTab.
#           Keyboard, terminal, 
# V1.8      Paged memory + interrupts. To do gRobotIF, LogisimIF, graphics??,

title = 'CocoIDE V1.91'  # Should be updated to reflect version

try:
    # Python 3 tk
    import tkinter as tk
    from tkinter import ttk
    from tkinter import filedialog
    from tkinter import messagebox
    import tkinter.font as font
except:
    # Python 2 tk (runs but not exhaustively tested!)
    import Tkinter as tk
    import ttk
    import tkFileDialog as filedialog
    import tkMessageBox as messagebox
    import tkFont as font

import argparse
import random
random.seed()
import time
import os
import sys
import signal
import io
from sys import platform
import collections as colls
import atexit
import codecs
import copy
import pyclbr


# Language hightlight/syntax definitions
#from cdm8_asm_config import *
import cdm8_asm as cf
# Compiler/linker
import cocas
#import cocol
import cdm8_emu
import cdm8_io

# Get list of IOport classes

#ioPorts = pyclbr.readmodule('cdm8_io')
#del ioPorts["IOport"] # Do not need Super class name
#print(ioPorts)#debug
#ioPortnames = [ x.name for x in ioPorts.values()]
#print(ioPortnames)# debug

try:

    import sendfile as ames
    amesSession = True
    #print("Ames found")
except ImportError:
    print("No AMES library!")# debug
    amesSession = False
    #raise # debug

class CreateToolTip(object):
    """
    Create a tooltip for any given widget
    tk_ToolTip_class101.py
        gives a Tkinter widget a tooltip as the mouse is above the widget
        tested with Python27 and Python34  by  vegaseat  09sep2014
        www.daniweb.com/programming/software-development/code/484591/a-tooltip-class-for-tkinter
        Modified to include a delay time by Victor Zaccardo, 25mar16
        Modified by M L Walters July 2016
    """
    def __init__(self, widget, text='widget info', waittime=500):
        self.waittime = waittime    #miliseconds
        self.wraplength = 180   #pixels
        self.widget = widget
        self.text = text
        self.widget.bind("<Enter>", self.enter)
        self.widget.bind("<Leave>", self.leave)
        self.widget.bind("<ButtonPress>", self.leave)
        self.id = None
        self.tw = None

    def enter(self, event=None):
        self.schedule()

    def leave(self, event=None):
        self.unschedule()
        self.hidetip()

    def schedule(self):
        self.unschedule()
        self.id = self.widget.after(self.waittime, self.showtip)

    def unschedule(self):
        id = self.id
        self.id = None
        if id:
            self.widget.after_cancel(id)

    def showtip(self, event=None):
        x = y = 0
        x, y, cx, cy = self.widget.bbox("insert")
        x += self.widget.winfo_rootx() - 70
        y += self.widget.winfo_rooty() - 40
        # creates a toplevel window
        self.tw = tk.Toplevel(self.widget)
        # Leaves only the label and removes the app window
        self.tw.wm_overrideredirect(True)
        self.tw.wm_geometry("+%d+%d" % (x, y))
        label = tk.Label(self.tw,  justify='left',
                       background="#ffffff", relief='solid', borderwidth=1,
                       wraplength = self.wraplength)
        # Get memory cell content
        memValHex = self.widget["text"]
        memValDec = int(memValHex, 16)
        if memValDec >= 32 and memValDec < 127:
            memValStr = "'"+chr(memValDec)+"'"
        elif memValDec == 0:
            memValStr = "NUL"
        else:
            memValStr = "..."

        if memValDec==0:
            memValBin="00000000"
        else:
            memValBin = format(memValDec,"08b")
        if memValDec<128:
            #signed int
            memValDecS=256+memValDec
        else:
            memValDecS = memValDec
        memValDec = format(memValDecS-256,"+04d")+" %03d" % memValDec
        self.text = "0x"+memValHex+"\n'"+memValStr+"'\n"+memValDec+"\n"+memValBin
        label.config(text=self.text)
        label.pack(ipadx=1)

    def hidetip(self):
        tw = self.tw
        self.tw= None
        if tw:
            tw.destroy()


class VerticalScrolledFrame(tk.Frame):
    """A pure Tkinter scrollable frame that actually works!
    * Use the 'interior' attribute to place widgets inside the scrollable frame
    * Construct and pack/place/grid normally
    * This frame only allows vertical scrolling
    """
    def __init__(self, parent, *args, **kw):
        tk.Frame.__init__(self, parent, *args, **kw)            

        # create a canvas object and a vertical scrollbar for scrolling it
        vscrollbar = tk.Scrollbar(self, orient=tk.VERTICAL)
        vscrollbar.pack(fill=tk.Y, side=tk.RIGHT, expand=tk.FALSE)
        canvas = tk.Canvas(self, bd=0, highlightthickness=0,
                        yscrollcommand=vscrollbar.set)
        canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=tk.TRUE)
        vscrollbar.config(command=canvas.yview)

        # reset the view
        canvas.xview_moveto(0)
        canvas.yview_moveto(0)

        # create a frame inside the canvas which will be scrolled with it
        self.interior = tk.Frame(canvas)
        self.interior_id = canvas.create_window(0, 0, window=self.interior,
                                           anchor=tk.NW)
        
        # track changes to the canvas and frame width and sync them,
        # also updating the scrollbar
        def _configure_interior(event):
            # update the scrollbars to match the size of the inner frame
            size = (self.interior.winfo_reqwidth(), self.interior.winfo_reqheight())
            canvas.config(scrollregion="0 0 %s %s" % size)
            if self.interior.winfo_reqwidth() != canvas.winfo_width():
                # update the canvas's width to fit the inner frame
                canvas.config(width=self.interior.winfo_reqwidth())
        self.interior.bind('<Configure>', _configure_interior)

        def _configure_IOcanvas(event):
            if self.interior.winfo_reqwidth() != canvas.winfo_width():
                # update the inner frame's width to fill the canvas
                canvas.itemconfigure(self.interior_id, width=canvas.winfo_width())
        canvas.bind('<Configure>', _configure_IOcanvas)    
  

class CocoIDE(tk.Frame):

    def __init__(self, Emulator=None, master=None, filename=None, name='cocoide'):
        global amesSession # Flag for exam/test session capability
        tk.Frame.__init__(self, master=master, name=name)
        self.master.resizable(width=True, height=True)
        self.grid_rowconfigure(1, weight=1)     # For mainPanel to fill the window Horiz
        self.grid_columnconfigure(0, weight=1)  # For mainPanel to fill the window Vert??
        self.pack(fill=tk.BOTH, expand=1)#expand=1,
        self.TITLE = title
        self.master.title(self.TITLE)
        self.master.protocol("WM_DELETE_WINDOW",self.close_window) # Overide default quit
        self.master.geometry("1200x600")
        self.master.config(cursor="watch") 
        
        ## Make global the CDM8 emulator
        self.Emu=Emulator
        self.Emu.parent = self # allows for callbacks to CocoIDE
        self.bind("<<checkInPorts>>", self.inputPortHandler)
        # Useful CDM8 (self.Emu) attributes/defaults
        # Emu.CVZN = ob0000 # SP Flags
        # Emu.memory = [n][0]*256 where n = pages
        # Emu.PC 
        # Emu.regs=[0,0,0,0]) # Registers (r0 to r3)
        # Emu.SP = 0 # Stack Pointer

        ## Class-wide variables
        self.file_name = "Untitled"
        self.file_path = None ##??
        self.changed = False
        self.labelList=[]
        self.hidden=True
        self.memArray=[]
        self.running=False
        self.prevPC=0
        self.prevSP = 255
        self.bpTagNames = []
        self.runDict = colls.OrderedDict()
        self.cliptext = ""
        self.runDict["00:"] = 0x00
        self.watches = [] # List of lists for watches and watch labels etc.
        self.cdm8ver=4 # CDM8 Instruction set Version. Default = 4?
        self.memChanged = []
        self.startIndex = "1.0"
        self.prevStr = ""
        self.pageDisp = False # No memory pages shown as default (simple display)
        self.bgColour = None # to restore bg colour when AMES exits.
        ## Interupt defaults
        self.interrupt = False 
        self.intVector = 0
        self.bind("<<genInterrupt>>", self.interruptHandler)
        
        ## IO Ports 
        # Get list of Port names from cdm8_io module
        self.ioPorts = pyclbr.readmodule('cdm8_io')
        del self.ioPorts["IOport"] # Do not need Super class name
        #print(self.ioPorts)#debug
        self.ioPortnames = [ x.name for x in self.ioPorts.values()]
        self.ioPortnames.sort()
        # List to hold dynamically instantiated IOPorts, tk objects and attributes
        self.IOPorts = [] 

        ## Fonts
        # Scale text font size to screen size, unless not configured
        if self.winfo_screenwidth() > 1200 and cf.screenScaleMode == False:
            self.textsize=10
        else:
            screenheight=self.winfo_screenheight()/100
            screenwidth = self.winfo_screenwidth()/150
            #print (screenwidth, screenheight)#debug
            if screenwidth > screenheight:
                self.textsize = int(screenheight)
            else:
                self.textsize = int(screenwidth)
        #print("textsize=", self.textsize)
        
        ## Configure fonts
        self.defaultfont = font.nametofont("TkDefaultFont")
        self.defaultfont.configure(size=self.textsize)
        self.option_add("*Font", self.defaultfont)
        
        # Set default font for all widgets, etc.
        if cf.basefont:
            self.option_add("*Font", cf.basefont)
        else:
            self.option_add("*Font", self.defaultfont)
        
        # Save the default fixed font and set to size=self.textsize
        self.defaulttxtfont = font.Font(font="TkFixedFont")
        #self.editfont=font.Font(font="TkFixedFont")
        self.defaulttxtfont.configure(size=self.textsize)
        
        # Create bold and smaller versions of the default text window font
        self.boldfont = font.Font(font="TkFixedFont")#self.asstxt['font'])
        self.boldfont.config(weight='bold', size=self.textsize)
        self.smallfont = font.Font(font="TkFixedFont")#self.asstxt['font'])
        self.smallfont.config(size=self.textsize-2)
        self.smallboldfont = font.Font(font="TkFixedFont")#self.asstxt['font'])
        self.smallboldfont.config(size=self.textsize-2, weight="bold")
        
        # Ames stuff
        self.amesRunning = False#True
        self.homeDir = os.path.expanduser("~")
        self.timeleft = 0
        self.endtime = None
        self.testcount = None # None for normal AMES operation.
        self.arch=["vn"] * 8 # default = "vn" = Von Neuman, "hv" = Harvard
        ## Local variables
        #editorWidth = 50
        
        #### GUI Window Display
        ### Menus
        self.menubar = tk.Menu(self)
        if platform == "darwin":
            comkey = "Command-"
        else:
            comkey = "Ctrl+"
        # File menu, and add it to the menu bar
        self.filemenu = tk.Menu(self.menubar, tearoff=0)
        self.filemenu.add_command(label="New", command=self.file_new, accelerator=comkey+"n")
        self.filemenu.add_command(label="Open", command=self.file_open, accelerator=comkey+"o")
        self.filemenu.add_command(label="Save", command=self.file_save, accelerator=comkey+"s")
        self.filemenu.add_command(label="SaveAs", command=self.file_save_as, accelerator=comkey+"S")
        self.filemenu.add_separator()
        self.filemenu.add_command(label="Quit", command=self.close_window, accelerator=comkey+"q")#self.quit)
        self.menubar.add_cascade(label="File", menu=self.filemenu, accelerator=comkey+"f")
        
        # Edit menu
        self.editmenu = tk.Menu(self.menubar, tearoff=0)
        self.editmenu.add_command(label="Undo", command=self.undo, accelerator=comkey+"z")
        self.editmenu.add_command(label="Redo", command=self.redo, accelerator=comkey+"y")
        self.editmenu.add_command(label="Cut", command=self.cut, accelerator=comkey+"t")
        self.editmenu.add_command(label="Copy", command=self.copy, accelerator=comkey+"c")
        self.editmenu.add_command(label="Paste", command=self.paste, accelerator=comkey+"p")
        self.editmenu.add_separator()
        self.txtmenu = tk.Menu(self.editmenu, tearoff=0)
        self.txtmenu.add_radiobutton(label="V Large", command=lambda: self.changeTextSize(22))
        self.txtmenu.add_radiobutton(label="Large", command=lambda: self.changeTextSize(12))
        self.txtmenu.add_radiobutton(label="Default", command = lambda: self.changeTextSize(10))
        self.txtmenu.add_radiobutton(label="Small", command = lambda: self.changeTextSize(8))
        self.menubar.add_cascade(label="Edit", menu=self.editmenu)
        self.editmenu.add_cascade(label="Text Size", menu=self.txtmenu)
        
        # Emulator menu
        self.emumenu = tk.Menu(self.menubar, tearoff=0)
        self.emumenu.add_command(label="Compile/Reset", command=self.compileText)
        self.emumenu.add_command(label="Run", command=self.runProg)
        self.emumenu.add_command(label="Stop", command=self.runProg)
        self.emumenu.add_command(label="Toggle BP", command=self.toggleBP)
        self.emumenu.add_command(label="Save Image", command=self.saveImage)
        self.emumenu.add_command(label="Save Object File", command=self.saveObjFile)
        self.emumenu.add_command(label="Cocol CDM8 Linker", command=self.cocolnk)
        self.emumenu.add_separator()
        self.emumenu.add_command(label="Arch = Von Neuman ", command=self.toggleArch)
        self.emumenu.add_command(label="Paged Memory     ", command=self.toggleMemPageDisp)
        self.emumenu.add_command(label="Shadow SPs      ✔", command=self.setShadowSP)
        self.menubar.add_cascade(label="CDM8", menu=self.emumenu)
        # Help Menu
        self.helpmenu = tk.Menu(self.menubar, tearoff=0)
        self.helpmenu.add_command(label="Manual", command=self.helpwin)
        self.menubar.add_cascade(label="Help", menu=self.helpmenu)
        # Display the menu
        self.master.config(menu=self.menubar)
        
        ### Buttonbar
        buttonBar = tk.Frame(self, name="buttonbar", height=40)#, border=1)#, bg="red")
        buttonBar.grid(row=0,column=0, columnspan =4, sticky="ew")

        ## Shortcut buttons
        # Editor
        self.editButtons = tk.Frame(buttonBar)
        self.editButtons.pack(side=tk.LEFT, fill=tk.BOTH)
        self.newButton = tk.Button(self.editButtons, text="New", command=self.file_new)
        self.newButton.grid(row=0, column=0, rowspan=1, sticky="news")
        self.openButton = tk.Button(self.editButtons, text="Open", command=self.file_open)
        self.openButton.grid(row=0, column=1, rowspan=1,sticky="ns")
        self.saveButton = tk.Button(self.editButtons, text="Save", command=self.file_save)
        self.saveButton.grid(row=0, column=2, rowspan=1, sticky="ns")
        
        self.saveAsButton = tk.Button(self.editButtons, text="SaveAs", command=self.file_save_as)
        self.saveAsButton.grid(row=0, column=3, rowspan=1, columnspan=2, sticky="nws")
        self.exitButton = tk.Button(self.editButtons, text="Quit", height=3, command=self.close_window)
        self.exitButton.grid(row=0, column=5, rowspan=2, sticky="ns")
        
        self.searchBox = tk.Entry(self.editButtons, width=8)
        self.searchBox.bind("<Return>", self.searchText)
        self.searchBox.bind("<Button-3>", self.searchText)
        tk.Button(self.editButtons, text="Search", command=self.searchText).grid(row=1, column=0, sticky="ew")
        self.searchBox.grid(row=1, column=1, sticky="ew", columnspan=2)
        
        self.lineBox = tk.Entry(self.editButtons, width=3)
        self.lineBox.bind("<Return>", self.gotoLine)
        self.lineBox.bind("<Button-3>", self.gotoLine)
        tk.Button(self.editButtons, text="Line", command=self.gotoLine).grid(row=1, column=3, sticky="w")
        self.lineBox.grid(row=1, column=4, sticky="ew")
        # Spacer
        tk.Label(self.editButtons, text=" ", width=1).grid(row=1, column=6)

        ## CDM8 Emulator Buttons
        # Cludge for Mac button display!!!
        if platform == "darwin":
            self.saveImageButton = tk.Button(buttonBar, text="Save Image ",
                height=3, command=self.saveImage)
        else:
            self.saveImageButton = tk.Button(buttonBar, text="Save Mem\nImage ", 
                height=3, command=self.saveImage)
        
        self.saveImageButton.pack(side=tk.RIGHT)
        self.spacer=tk.Label(buttonBar, text="", width=2)
        self.spacer.pack(side=tk.RIGHT)
        self.speedScale = tk.Scale(buttonBar, from_=3, to=0, orient=tk.HORIZONTAL,
            showvalue=0, width=18, label="Step <--> Fast")
        self.speedScale.set(1)
        self.speedScale.pack(side=tk.RIGHT)
        self.runStopButton = tk.Button(buttonBar, text="Run ", height=3, padx=6,
            command=self.runProg)
        self.runStopButton.pack(side=tk.RIGHT)

        self.runFromFrame = tk.Frame(buttonBar)
        self.runFromFrame.pack(side=tk.RIGHT)
        self.runFromLabel = tk.Label(self.runFromFrame, text="Run From")
        self.runFromLabel.pack(side=tk.TOP)
        self.runFrom = tk.StringVar()
        self.runFrom.set("00:")
        
        # Create Style for RunSelect dropdown select
        style = ttk.Style()
        style.map('TCombobox', fieldbackground=[('readonly','white')])
        style.map('TCombobox', selectbackground=[('readonly', 'white')])
        style.map('TCombobox', selectforeground=[('readonly', 'black')])
        
        self.runEPSelect = ttk.Combobox(self.runFromFrame, textvariable=self.runFrom,
            state='readonly', width=6, foreground="black", background="white")
        self.runEPSelect.bind('<<ComboboxSelected>>',self.initPC) # reset Program Counter
        self.runEPSelect['values'] = ['00:']
        self.runEPSelect.current(0)
        self.runEPSelect.pack(side=tk.TOP)
        
        # Cludge for Mac/OSX
        if platform == "darwin":
            compileButton = tk.Button(buttonBar, text="Compile/Reset", command=self.compileText, height=3, ipadx=6)
        else:
            compileButton = tk.Button(buttonBar, text="Compile\nReset", command=self.compileText, height=3, padx=6)
        compileButton.pack(side=tk.RIGHT)
        
        # Staus/Run time warning/Error pane
        self.statusMsg=tk.Label(buttonBar, text="", width=20, fg="red", bg="white", 
            height=3,  relief="sunken", border=3, )#int(self.textsize)*2)padx=8,
        self.statusMsg.pack(side=tk.RIGHT, fill=tk.BOTH, expand=1)
        
        # AMES extensions
        if amesSession:
            #self.spacer0=tk.Label(buttonBar, text="", width=1)
            #self.spacer0.pack(side=tk.LEFT)
            self.amesFrame=tk.Frame(buttonBar, relief="flat", padx=6)
            self.amesFrame.pack(side=tk.LEFT, expand=0, fill=tk.Y)
            self.amesButton = tk.Button(self.amesFrame, text="Start ", command=self.amesStart)#, height=2)
            self.amesButton.grid(row = 1, column=0, sticky="w")#, rowspan=1)
            self.pinVar = tk.IntVar()
            self.pinVar.set("")
            self.pinEntry = tk.Entry(self.amesFrame, show="*", text=self.pinVar, width = 4)
            self.pinEntry.grid(row=1, column=1, padx=2, sticky="w")
            self.pinEntry.bind("<Return>", self.amesStart)
            self.pinLabel = tk.Label(self.amesFrame, text = "AMES          PIN?",  anchor="w", width=18)
            self.pinLabel.grid(row=0, column=0, columnspan=2, sticky="ws")#, pady=3)
            #self.amesStatus = tk.Label(self.amesFrame, text="",  height=3, 
            #    width=25, fg="red", padx=6)#, wrap=tk.WORD)width=25,
            #self.amesStatus.grid(row=0, column=2, rowspan=2, sticky="w")
        
        ### Create mainPanel under buttonBAr
        mainPanel = tk.Frame(self, name='cocoidewin')#, bg="yellow")
        mainPanel.grid(row=1,column=0, sticky="nsew")
        mainPanel.rowconfigure(0, weight=1)     # Allow text and mcode windows to scale vertically
        mainPanel.columnconfigure(1, weight=1)  # Allow text and watch windows to scale horizontally
        
        ## Create the assembly code editor panel
        # Text editor with scrollbar and syntax highlighting
        
        # Text Line Number pane
        self.lntext = tk.Text(mainPanel, width = 4,
                padx = 4, highlightthickness = 0,
                takefocus = 0, bd = 0, background = 'lightgrey',
                foreground = 'black', font=self.boldfont,
                yscrollcommand=self.yscroll3)
        self.lntext.grid(row=0, column=0, sticky="ns")
        self.lntext.config(state="disabled")
        self.lntext.bind('<MouseWheel>', lambda e: "break")
        
        # Text editor window
        self.asstxt = tk.Text(mainPanel, wrap=tk.NONE,font=self.defaulttxtfont,
                                undo=True, yscrollcommand=self.yscroll1,
                                autoseparators=True, maxundo=-1, width=40)#, height=10,) height=editorHeight,
        self.asstxt.grid(row=0, column=1, sticky="nsew")
        # Scroll bars
        self.vscroll = ttk.Scrollbar(mainPanel, orient=tk.VERTICAL, command=self.yview)
        self.vscroll.grid(row=0, column=2, sticky="ns")
        txtHscroll = ttk.Scrollbar(mainPanel, orient=tk.HORIZONTAL, command=self.asstxt.xview)
        txtHscroll.grid(row=1, column=0,columnspan=2, sticky="ew")
        self.asstxt.config(xscrollcommand=txtHscroll.set)
        
        # Editor - other configurations
        self.asstxt.edit_separator()
        #print("**\n",self.asstxt.bindtags())#debug
        
        # Editor formatting options
        tab_width = self.defaulttxtfont.measure('OOOO')  # compute desired width of tabs
        self.asstxt.config(font=self.boldfont, tabs=tab_width, tabstyle="wordprocessor")#"1.0c 2.0c 3.0c")#tab_width,))

        ## Create the machine code memory list display panel
        mcode_frame=ttk.Frame(mainPanel)#, width=60)
        mcode_frame.grid(row=0, column=3, sticky="nsew")#pack(fill=tk.Y, expand=1, side=tk.LEFT)
        self.mcode_list=tk.Text(mcode_frame, yscrollcommand=self.yscroll2, width=25,
                                wrap=tk.NONE, font=self.defaulttxtfont)#, height=editorHeight)
        mcodeHscroll = ttk.Scrollbar(mainPanel, orient=tk.HORIZONTAL, command=self.mcode_list.xview)
        mcodeHscroll.grid(row=1, column=3, sticky="ew")
        self.mcode_list.config(xscrollcommand=mcodeHscroll.set)                        
                                
        self.mcode_list.bind("<Key>", lambda e: "break") # Disable editing
        self.mcode_list.pack(fill=tk.BOTH, expand=1)

        ## Bind editor keys
        self.bindKeys()


        ## Bind mcode keys
        self.mcode_list.bind("<Control-T>", self.toggleBP)
        self.mcode_list.bind("<Control-t>", self.toggleBP)
        #if platform == "darwin":
        self.mcode_list.bind("<Command-T>", self.toggleBP)
        self.mcode_list.bind("<Command-t>", self.toggleBP)


        # Bind keys for highlighting
        self.asstxt.bind("<Key>", self.keydisable)
        self.asstxt.bind('<KeyRelease>', self.highlighter)

        ## Bind mouse events
        
        # Disable Mouse Wheel button (2)
        self.asstxt.bind("<ButtonRelease-2>", lambda e: "break")
        #self.asstxt.bind("<Double-Button-2>", lambda e: "break")
        self.asstxt.bind("<Double-ButtonRelease-2>", lambda e: "break")
        #self.asstxt.bind("<Triple-Button-2>", lambda e: "break")
        self.asstxt.bind("<Triple-ButtonRelease-2>", lambda e: "break")
        self.mcode_list.bind("<ButtonRelease-2>", lambda e: "break")
        #self.asstxt.bind("<Double-Button-2>", lambda e: "break")
        self.mcode_list.bind("<Double-ButtonRelease-2>", lambda e: "break")
        #self.asstxt.bind("<Triple-Button-2>", lambda e: "break")
        self.mcode_list.bind("<Triple-ButtonRelease-2>", lambda e: "break")
        
        # Pop up edit menu
        self.asstxt.bind("<Button-3>",self.popmenu)
        
        # Toggle BreakPoint
        self.mcode_list.bind("<Button-3>", self.toggleBP)# Confusing?
        self.mcode_list.bind('<Double-Button-1>', self.toggleBP)

        ## Create and add Watch panel
        watchPanel = tk.Frame(mainPanel, name="watchpanel")#, bg="blue")#,  height=80)#,  background="red") #width=570,
        watchPanel.grid(row=2, column=0, columnspan=3, sticky="nsew")#pack(side=tk.LEFT, fill=tk.BOTH)
        watchPanel.columnconfigure(0, weight=1)
        #watchPanel.columnconfigure(1, weight=1) 
        # And contents
        watchTitle = tk.Label(watchPanel, text="Memory Watches", relief="raised",  padx=0, font=self.boldfont)#, width=51)
        watchTitle.grid(row=0, column=0, columnspan=2, sticky="ew")#pack(side=tk.TOP, fill=tk.X)

        watchHeader = tk.Label(watchPanel, text="Adr:  Label:                  Content ")
        watchHeader.grid(row=1, column=0, columnspan=2, sticky="w")#pack(side=tk.TOP, fill=tk.X, anchor="w")
        
        self.watchList = tk.Text(watchPanel, height=9, bg="white", font=self.boldfont,  wrap=tk.NONE)#,width=editorWidth-2, text='data: OE: "Hello there"')
        self.watchList.bind("<Key>", lambda e: "break") # Disable editing
        self.watchList.grid(row=2, column=0, columnspan=2, sticky="nsew")
        watchScrollY = tk.Scrollbar(watchPanel, orient=tk.VERTICAL, command=self.watchList.yview)
        self.watchList.config(yscrollcommand=watchScrollY.set)
        watchScrollY.grid(row=2, column=1, sticky="nse")
        watchScrollX = tk.Scrollbar(watchPanel, orient=tk.HORIZONTAL, command=self.watchList.xview)
        self.watchList.config(xscrollcommand=watchScrollX.set)
        watchScrollX.grid(row=3, column=0, columnspan=2, sticky="sew")

        # Disable Mouse Wheel button (2)
        self.watchList.bind("<Button-2>", lambda e: "break")
        self.watchList.bind("<ButtonRelease-2>", lambda e: "break")
        self.watchList.bind("<Double-ButtonRelease-2>", lambda e: "break")
        self.watchList.bind("<Double-Button-2>", lambda e: "break")
        self.watchList.bind("<Triple-Button-2>", lambda e: "break")
        self.watchList.bind("<Triple-ButtonRelease-2>", lambda e: "break")
        
        
        self.mcode_list.bind("<Double-ButtonRelease-2>", lambda e: "break")
        #self.asstxt.bind("<Triple-Button-2>", lambda e: "break")
        self.mcode_list.bind("<Triple-ButtonRelease-2>", lambda e: "break")
        
        
        ## Create and add Register panel
        self.regPanel = tk.Frame(mainPanel, name="regpanel",  height=80, padx=0, relief="sunken", bd=2)#,  background="blue") #width=300,
        self.regPanel.grid(row=2, column=3, sticky="nsew")#pack(side=tk.LEFT, fill=tk.Y)
        regTitle = tk.Label(self.regPanel, text="Registers", font=self.boldfont)
        regTitle.grid(row=0, column=1, columnspan=2)
        
        # Program counter
        self.pcLab = tk.Label(self.regPanel, text="^ PC  ", width=6, font=self.boldfont, padx=3)
        self.pcLab.grid(row=1, column=0, sticky="w")#, columnspan=2)
        self.pcLabVal = tk.Label(self.regPanel, text="00",width=6, bg=cf.PCcolour, relief="sunken", padx=3, font=self.defaulttxtfont)
        self.pcLabVal.grid(row=2, column=0, sticky="w")

        # PS register (CVZN etc.)
        
        self.CVZN_Lab = tk.Label(self.regPanel, text="PS: I Page CVZN",width=15, font=self.boldfont)
        self.CVZN_Lab.grid(row=1, column=1, columnspan=2)#,sticky="e")#, columnspan=2)
        self.CVZN_Val = tk.Label(self.regPanel, text="0 000 0000", bg="white", width=15, relief="sunken", font=self.defaulttxtfont)
        self.CVZN_Val.grid(row=2, column=1, columnspan=2)#, sticky="e")#, columnspan=2)

        # Stack Pointer
        self.spLab = tk.Label(self.regPanel, text=" SP  ",width=7, font=self.boldfont)
        self.spLab.grid(row=1, column=3)#, columnspan=2)
        self.spVal = tk.Label(self.regPanel, text="00", bg=cf.SPcolour, width=7, relief="sunken", font=self.defaulttxtfont)
        self.spVal.grid(row=2, column=3)#, columnspan=2)


        spacer1= tk.Label(self.regPanel, text="")#, height=1)
        spacer1.grid(row=3, column=0)
        #Registers
        self.regLabs = [0]*4
        self.regHexs = [0]*4
        self.regStrs = [0]*4
        self.regDecs = [0]*4
        self.regBins = [0]*4
        for index in range(4):
            self.regLabs[index] = tk.Label(self.regPanel, text="r"+str(index), width=8, padx=5, fg="blue", font=self.boldfont)
            self.regLabs[index].grid(row=4, column=index, sticky="n")
            self.regHexs[index] = tk.Label(self.regPanel, text="0x00", width=8, bg="white", relief="sunken",font=self.defaulttxtfont)
            self.regHexs[index].grid(row=5, column=index, sticky="n")
            self.regStrs[index] = tk.Label(self.regPanel, text="NUL", width=8, bg="white", relief="sunken",font=self.defaulttxtfont)
            self.regStrs[index].grid(row=6, column=index, sticky="n")
            self.regDecs[index] = tk.Label(self.regPanel, text="+000 000", width=8, bg="white", relief="sunken",font=self.defaulttxtfont)
            self.regDecs[index].grid(row=7, column=index, sticky="n")
            self.regBins[index] = tk.Label(self.regPanel, text="00000000", width=8, bg="white", relief="sunken",font=self.defaulttxtfont)
            self.regBins[index].grid(row=8, column=index, sticky="n")

        ### Create the machine panel
        self.mcPanel = tk.Frame(mainPanel)#, bg="blue")
        #mcPanel.pack(side=tk.RIGHT, fill=tk.Y)#, expand=1)
        self.mcPanel.grid(row=0, column=5, sticky="nsew", rowspan=5)#, columnspan=4)
        self.mcPanel.grid_rowconfigure(index=4, weight=1)
        
        ## Memory Page select frame
        self.memPageFrame = tk.Frame(self.mcPanel)
        self.memPageFrame.grid(row=0, column=0, sticky="ew")
        self.memPageFrame.grid_remove()
        self.memPageVar = tk.IntVar()
        self.memPageVar.set(0)
        self.pageLabel = tk.Label(self.memPageFrame, text="Memory: Page:")
        self.pageLabel.pack(side=tk.LEFT)
        self.pageRadButtons = []
        for n in range(8):
            self.pageRadButtons.append(tk.Radiobutton(self.memPageFrame, text=str(n),
                variable=self.memPageVar, value=n, indicatoron=0, width=3, padx=4,
                command= lambda : self.dispAllMemory(page=self.memPageVar.get())))
            self.pageRadButtons[-1].pack(side=tk.LEFT) 
        
        ## Create the Advanced notebook memory panel
        self.emuNb = ttk.Notebook(self.mcPanel, name='notebook')#, width=420)
        # extend bindings to top level window allowing
        #   CTRL+TAB - cycles thru tabs
        #   SHIFT+CTRL+TAB - previous tab
        #   ALT+K - select tab using mnemonic (K = underlined letter)
        self.emuNb.enable_traversal()
        self.emuNb.grid(row=1, column=0)#, sticky="nsew")#(columnspan=2,
        #self.emuNb.pack(side=tk.TOP, expand=0, padx=5, pady=2)# 

        # Create machine memory frame(s) and add to notebook panel
        self.mem_frame=[[],[]]
        for n in range(len(self.mem_frame)):
            self.mem_frame[n] = ttk.Frame(self.emuNb)
            self.emuNb.add(self.mem_frame[n])
    
        self.setArch(self.arch[0], page=0) 
        
        # Also populates mem panel(s) and tooltips

        ## Add I/O Port display area to mcPanel
        # I/O Title Header
        self.IOHeader = tk.Frame(self.mcPanel, relief="raised", borderwidth=1)
        #self.IOHeader.pack(side=tk.TOP, fill=tk.X, padx=5, pady=3)
        self.IOHeader.grid(row=2, column=0)
        self.IOAdrLabel =  tk.Label(self.IOHeader, text="Addr.")
        self.IOAdrLabel.pack(side=tk.LEFT)
        self.IOLabel = tk.Label(self.IOHeader, font=self.boldfont, text="               I/O Ports")
        self.IOLabel.pack(side=tk.LEFT)
        # IO ADD/DEL IO port Buttons
        self.portVar = tk.StringVar()
        self.portVar.set(self.ioPortnames[0])
        self.addPortSelect = ttk.Combobox(self.IOHeader, textvariable=self.portVar,
            state='readonly', width=15)
        self.addPortSelect.pack(side=tk.RIGHT)
        self.addPortSelect['values']= self.ioPortnames
        self.addPortSelect.bind('<<ComboboxSelected>>', self.addport)
        self.addPortSelect.bind('<Return>', self.addport)
        self.portVarLabel = tk.Button(self.IOHeader, text="+", command=self.addport, width=1)
        self.portVarLabel.pack(side=tk.RIGHT)
        
        ## I/O Panel with canvas and scrollbar
        # Based on code by novel-yet-trivial/VerticalScrolledFrame.py, 2017
        self.IOPanel = tk.Frame(self.mcPanel)#, bg="red")#.pack(fill=tk.BOTH, expand=1)
        #self.IOPanel.pack(side=tk.TOP, fill=tk.BOTH, expand=1, padx=5)#, pady=1)
        self.IOPanel.grid(row=4, column=0, sticky="nsew")
        self.IOscroll = ttk.Scrollbar(self.IOPanel, orient=tk.VERTICAL)#, command=self.IOcanvas.yview)
        self.IOscroll.pack(side=tk.RIGHT, fill=tk.Y, expand=0)#row=0, column=2, sticky="ns")
        self.IOcanvas = tk.Canvas(self.IOPanel, yscrollcommand=self.IOscroll.set)# bg="yellow")
        self.IOcanvas.pack(side=tk.TOP, fill=tk.BOTH, padx=2, pady=2, expand=1)
        self.IOscroll.config(command=self.IOcanvas.yview)
        self.IOcanvas.yview_moveto(0)
        #self.IOcanvas.xview_moveto(0)#?? 
        
        ## Create a window on the canvas for the IOFrame 
        self.IOFrame = tk.Frame(self.IOcanvas)#, bg="purple")
        self.IOFrame_win = self.IOcanvas.create_window(0, 0, window=self.IOFrame,anchor=tk.NW)
        
        ## Various event bindings to update IOFrame ands GUI correctly
        # Track changes to the canvas and frame widths
        # Sync them, and update the scrollbar
        # Based on code by novel-yet-trivial/VerticalScrolledFrame.py, 2017
        def _configure_IOFrame(event=None):
            # Update the scrollbars to match the size of the inner frame
            size = (self.IOFrame.winfo_reqwidth(), self.IOFrame.winfo_reqheight())
            self.IOcanvas.config(scrollregion="0 0 %s %s" % size)
            if self.IOFrame.winfo_reqwidth() != self.IOcanvas.winfo_width():
                # update the canvas's width to fit the inner frame
                self.IOcanvas.config(width=self.IOFrame.winfo_reqwidth())
        self.IOFrame.bind('<Configure>', _configure_IOFrame)
        self.IOFrame.bind("<<updatePort>>", self.updatePort)

        def _configure_IOcanvas(event=None):
            #self.IOcanvas.configure(scrollregion=self.IOcanvas.bbox("all"))
            if self.IOFrame.winfo_reqwidth() != self.IOcanvas.winfo_width():
                # update the inner frame's width to fill the canvas
                self.IOcanvas.itemconfigure(self.IOFrame_win, width=self.IOcanvas.winfo_width())
        self.IOcanvas.bind('<Configure>', _configure_IOcanvas)
        
        # Intialise the IOFrame and IOcanvas- just use the callbacks
        _configure_IOFrame()
        _configure_IOcanvas()
        
        # Mousewheel scrolling
        def _bind_mouse(event=None):
            self.IOcanvas.bind_all("<4>", _on_mousewheel)
            self.IOcanvas.bind_all("<5>", _on_mousewheel)
            self.IOcanvas.bind_all("<MouseWheel>", _on_mousewheel)

        def _unbind_mouse(event=None):
            self.IOcanvas.unbind_all("<4>")
            self.IOcanvas.unbind_all("<5>")
            self.IOcanvas.unbind_all("<MouseWheel>")
        
        def _on_mousewheel(event):
            """Linux uses event.num; Windows / Mac uses event.delta"""
            if event.num == 4 or event.delta > 0:
                self.IOcanvas.yview_scroll(-1, "units" )
            elif event.num == 5 or event.delta < 0:
                self.IOcanvas.yview_scroll(1, "units" )
        
        self.IOcanvas.bind("<Enter>", _bind_mouse)
        self.IOcanvas.bind("<Leave>", _unbind_mouse)
        
        ### Finally load file if filename provided from commmand line
        if filename:
            try:
                self.file_open(filepath=filename)
            except:
                print("File not found!")
                exit()
        self.master.config(cursor="")

    
    #### IO Port functions
    
    def updatePort(self, event=None, portno=None):
        #for item in event:
        #print(event.state)# cludge to pass portno index back from IOport interrupt
        # if a port no is specified, either explicitly in portno, or from event.state,
        # then delete it
        n = None
        if event != None and event.state<256: 
            n = event.state
        if portno != None:# ignore any events, use portno
            n = portno
        # Delete and renumber ports if n is a port number
        if n != None and n >= 0:
            # Event generated by deleting an IO port 
            del self.IOPorts[n]
            # Update and renumber ports 
            portno=0
            for port in self.IOPorts:
                port.updatePort()
                port.portno = portno
                portno += 1
        # In all cases, update the Memory Display
        #self.dispAllMemory()
        self.updateDisp()
        return
        
    def addport(self, event=None, portno=0):
        # self.IOPorts is list of IO Port objects
        portkey = self.portVar.get()
        if portno>0 and (portno) < len(self.IOPorts): # Replace existing port
            # replace existing port
            self.updatePort(portno=portno) # delete port, then
            self.IOPorts.insert(portno, getattr(cdm8_io, portkey)(self.IOFrame, portno))
        else: # Append
            portno = len(self.IOPorts)
            self.IOPorts.append(getattr(cdm8_io, portkey)(self.IOFrame, portno))
            # renumber ports
            portno=0
            for port in self.IOPorts:
                port.portno = portno
                portno += 1
        #self.dispAllMemory()
        self.updateDisp()

    def inputPortHandler(self, event=None):
        # Interrupt handler called by the Emulator whenever an 
        # Input (data memory) Port address is read .
        # Memory address passed via self.Emu.ipAdr,
        # If found in any IP port,then sets self.Emu.ipVal to Input port value.
        # Emulator then substitutes value from ipVal instead memory adr val. 
        # Also used by OP ports to trigger a dislpay update
        for port in self.IOPorts:
            for adr in port.portIPvals.keys():
                #print(adr, self.Emu.ipAdr)
                if adr == self.Emu.ipAdr:
                    self.Emu.ipVal = port.portIPvals[adr]
        self.updateDisp()    
        return 
    
    def updateOPs(self):
        ## Check for Output port value updates
        if  self.Emu.memChanged[0]:
            adr = self.Emu.memChanged[0][-1] 
            for port in self.IOPorts:
                #print("**",list(port.getOPadr()))
                if adr in port.getOPadr():
                    #print("*", adr, list(port.getOPadr()), memval)
                    port.setOPval(adr, self.Emu.memory[0][self.Emu.datamem[0]][adr])

    
    def interruptHandler(self, event=None):
        # Interupt handler
        self.interrupt = True # set interrupt flag
        self.intVector = 0 # ???


    #### Ames functionality
    def amesStart(self, event=None):
        self.statusMsg.config(text="") # Clear any error messages
        self.disableMenus()
        errormsg = None
        self.bgColour = self.pinLabel.cget("bg")
        # get and check pin formatted ok)
        #print("*",self.pinVar.get(), "8")
        try:
            self.pin = self.pinVar.get()
            #print(type(self.pin), self.pin)#debug
        except:
            errormsg="BAD PIN!"
        #print( "*", errormsg)
        self.statusMsg.config(text="PLEASE WAIT")
        self.update()
        if errormsg == None and not ames.ping():
            errormsg = "NETWORK FAULT"
        if errormsg == None: # No network errors etc.
            #print("Ames selected", self.pin)#debug
            # get remote file and load to text editwindow
            #timeleft=None
            # First try .asm file
            received_file = None
            success = False
            timeleft = 0
            try:
                (success,received_file, timeleft) = ames.download(self.pin,"asm")
                #print("timeleft1=",timeleft)#debug
                #print(received_file[:20], success, timeleft) #debug - just first line or so
                #if timeleft and timeleft>0:
                self.timeleft = timeleft #Use AMES timeleft value
                #print("asm",success, received_file[:20])#debug
                #print("timeleft2=",self.timeleft) #debug
            except:
                #raise # debug for Mac Ames problem
                success = False
                received_file = False
            ## hack for testing
            if self.testcount:
                self.timeleft = self.testcount # hack for testing !!!!
                self.testcount = None
            ##
            #print("Timeleft = ", self.timeleft)#debug
            
            if self.timeleft and self.timeleft<=0:
                statusMsg = "AMES: TEST TIMED OUT"
            else:
                statusMsg = "AMES: TEST STARTED OK"
            if not success:
                if received_file:
                    errormsg=received_file
                else:
                    errormsg = "UNSPECIFIED NETWORK ERROR"
                    
            if success and self.timeleft: #if errormsg == None: # commnent out for testing w out ames.
                # load file into editor
                # Set current text to file contents
                self.amesRunning=True
                self.cliptext=""
                self.asstxt.delete(1.0, "end")
                self.mcode_list.delete(1.0, tk.END)
                self.asstxt.insert(1.0, received_file)
                self.asstxt.edit_modified(False)
                self.asstxt.edit_separator()
                self.asstxt.edit_reset()
                self.highlighter()
               
        if errormsg:# or self.timeleft <= 0:
            self.statusMsg.config(text=errormsg)
            self.pinVar.set("")
            self.enableMenus()
            self.amesRunning=False
        else:
            if self.timeleft >0:
                self.set_title(titletxt="AMES: TEST RUNNING")
            else:
                self.set_title(titletxt="AMES: TEST TIMED OUT")
            self.amesButton.config(text="Submit", state=tk.NORMAL, command=self.amesSubmit)
            self.statusMsg.config(text=statusMsg)#"AMES: TEST STARTED OK")
            self.amesSubmit()# Immediately submit file for timestamp
            self.amesClockUpdate(self.timeleft)

    def amesClockUpdate(self, timeleft=None):
        # When AMES is running this function runs every 10 secs
        now = time.time()
        if timeleft!=None:
            self.endtime = now + timeleft*60
        self.timeleft = self.endtime - now
        print("Ames Timeleft", int(self.timeleft)," secs")#debug
    
        if  self.amesRunning and (self.timeleft > 0 or self.timeleft < -11): # Still time left?
            if self.timeleft % 300 < 11:
                #print("amesRunning ", self.amesRunning)#debug
                self.amesSubmit(auto=True) # auto-submit every 5 mins (300 secs)
                # Update display every 10 secs approx
            #self.statusMsg.config(text="TIME LEFT = "+str(int(self.timeleft//60)+1)+" MIN ")#+str(int(self.timeleft%60))+" SEC")#, width=30)
            self.pinLabel.config(text = "AMES <"+str(int(self.timeleft//60)+1)+" MIN LEFT", fg="white", bg="black")
            # Schedule update in 10 secs
            self.after(10000, self.amesClockUpdate) # every 10 secs
            #print("Clock Update in 10secs")#debug
        else:
            self.amesStop()
        return

    def amesSubmit(self, ext="asm", auto=False):
        errormsg=None
        content = None
        content = copy.copy(self.asstxt.get(1.0, "end"))# deep copy for 00: bug?
        #if content: print(content[:20], ext, self.pin)#debug
        # init submit vars to avoid type errors on error
        PE = False
        success = False
        diag = ""
        if content and self.amesRunning:
            try:
                #print(content[:20], ext, self.pin)
                (PE,success,diag) = ames.submit(content,ext,self.pin)
            except:
                #raise # debug
                pass
            #print(PE, success, diag)# debug
            #print(diag)# debug
            if PE:
                errormsg = "Protocol error: "+diag+'\n'
            if not success:
                errormsg = "Submit Failure: "+diag+'\n'
            if not errormsg:
                if self.timeleft>0:
                    if not auto: self.statusMsg.config(text="SUBMITTED OK")
                elif self.timeleft < -1: 
                    self.statusMsg.config(text="LATE SUBMISSION!")
                else:
                    self.statusMsg.config(text="TIME UP\nSUBMITTED OK!")

                print("File Submitted to ames")#debug
            else:
                # problem submitting
                self.statusMsg.config(text=errormsg+":\nTry again, or call tutor?")
                #self.pinEntry.config(state=tk.NORMAL)

    def amesStop(self):
        self.after_cancel(self.amesClockUpdate) # cancel clock update
        self.amesSubmit()
        if self.timeleft>0:
            self.statusMsg.config(text="Exited AMES OK")
        elif self.timeleft < -10: self.statusMsg.config(text="LATE SUBMISSION\nERROR!")
        else:
            self.statusMsg.config(text="TIME UP\nSUBMITTED OK!")
        self.pinLabel.config(text = "AMES          PIN?", fg="black", bg=self.bgColour)
        self.amesRunning=False
        #self.asmActive = False
        self.asstxt.edit_modified(False)
        if self.file_path: # Restore previous file if applicable
            self.file_open(filepath=self.file_path)
        else:
            self.file_new()
        self.enableMenus()
        return "break"

    def disableMenus(self):
        # disable load/save buttons
        self.newButton.config(state=tk.DISABLED)
        self.openButton.config(state=tk.DISABLED)
        self.saveButton.config(state=tk.DISABLED)
        self.saveAsButton.config(state=tk.DISABLED)
        self.saveImageButton.config(state=tk.DISABLED)
        self.pinEntry.config(state=tk.DISABLED)
        self.amesButton.config(state=tk.DISABLED)
        self.exitButton.config(command=self.amesStop, text="End ")
        self.unbindKeys()

        # Ames backdoor
        #self.asstxt.bind("<Control-p>", self.amesPause)
        #self.asstxt.bind("<Control-P>", self.amesPause)

        # Disable load/save menus
        self.menubar.entryconfig("File", state=tk.DISABLED)

    def enableMenus(self):
        # Enable file menu and load.save buttons
        self.newButton.config(state=tk.NORMAL)
        self.openButton.config(state=tk.NORMAL)
        self.saveButton.config(state=tk.NORMAL)
        self.saveAsButton.config(state=tk.NORMAL)
        self.saveImageButton.config(state=tk.NORMAL)
        self.pinEntry.config(state=tk.NORMAL)
        #if platform == "darwin":
        #    self.amesButton.config(text="AMES Start ", state=tk.NORMAL, command=self.amesStart)
        #else:
        self.amesButton.config(text="Start ", state=tk.NORMAL, command=self.amesStart)    
        self.exitButton.config(command=self.close_window, text="Quit")#??
        self.pinVar.set("")
        # Enable Load/Save hot keys
        self.bindKeys()
        # Enable load/save menus
        self.menubar.entryconfig("File", state=tk.NORMAL)

    def bindKeys(self):
        # Bind editing keys
        self.asstxt.unbind("<Control-Tab>")
        self.asstxt.bind("<Control-n>", self.file_new)
        self.asstxt.bind("<Control-N>", self.file_new)
        self.asstxt.bind("<Control-o>", self.file_open)
        self.asstxt.bind("<Control-O>", self.file_open)
        self.asstxt.bind("<Control-S>", self.file_save)
        self.asstxt.bind("<Control-s>", self.file_save)
        self.asstxt.bind("<Control-S>", self.file_save_as)
        
        self.asstxt.bind("<Control-a>", self.file_save_as)
        self.asstxt.bind("<Control-q>", self.file_quit)
        self.asstxt.bind("<Control-Q>", self.file_quit)
        self.asstxt.bind("<Control-Y>", self.redo)
        self.asstxt.bind("<Control-y>", self.redo)
        self.asstxt.bind("<Control-Z>", self.undo)
        self.asstxt.bind("<Control-z>", self.undo)
        self.asstxt.bind("<Control-c>", self.copy)
        self.asstxt.bind("<Control-C>", self.copy)
        self.asstxt.bind("<Control-t>", self.cut)
        self.asstxt.bind("<Control-T>", self.cut)
        self.asstxt.bind("<Control-v>", self.paste)
        self.asstxt.bind("<Control-V>", self.paste)
        
        self.asstxt.bind("<Tab>", lambda e: self.tabBlock(shift=1))
        self.asstxt.bind("<Control-Tab>", lambda e: self.tabBlock(shift=-1)) 
        
        # OSX/MAC os users add cmd key options as well
        if platform == "darwin":
            self.asstxt.bind("<Command-o>", self.file_open)
            self.asstxt.bind("<Command-O>", self.file_open)
            #self.asstxt.bind("<Command-Shift-S>", self.file_save_as)
            self.asstxt.bind("<Command-S>", self.file_save_as)
            self.asstxt.bind("<Command-s>", self.file_save)
            self.asstxt.bind("<Command-n>", self.file_new)
            self.asstxt.bind("<Command-N>", self.file_new)
        
    def unbindKeys(self):
        self.asstxt.bind("<Control-N>", None)
        self.asstxt.bind("<Control-n>", None)
        self.asstxt.bind("<Control-o>", None)
        self.asstxt.bind("<Control-O>", None)
        self.asstxt.bind("<Control-S>", None)
        self.asstxt.bind("<Control-s>", None)
        self.asstxt.bind("<Control-A>", None)
        self.asstxt.bind("<Control-a>", None)
        self.asstxt.bind("<Control-Shift-S>", None)
        self.asstxt.bind("<Control-Shift-s>", None)
        
        if platform == "darwin":
            self.asstxt.bind("<Command-o>", None)
            self.asstxt.bind("<Command-O>", None)
            self.asstxt.bind("<Command-S>", None)
            self.asstxt.bind("<Command-s>", None)
            self.asstxt.bind("<Command-n>", None)
            self.asstxt.bind("<Command-N>", None)
            
        
    def amesPause(self, event=None):
        # Back door for editing #! lines when ames is running
        if self.amesRunning:
            self.amesRunning = False
            self.enableMenus()
        else:
            self.amesRunning = True
            self.asstxt.bind("<Control-p>", None)
            self.asstxt.bind("<Control-P>", None)
            self.disableMenus()

    def keydisable(self, event=None):
        # Disable ! and editing keys for lines with #! when running ames
        #print("keydisable",event.keysym, event.keycode, repr(event.char), event.type)# debug

        if self.amesRunning and event.keysym not in ["Left", "Right", "Up", "Down",
                        "Home", "End", "Prior", "Next"]:#, "Control_R"]:# Ames backdoor
            # Do not allow editing of #! lines, or inserting ! after #
            if event.keysym == "exclam" and self.asstxt.search("#", "insert-1c", "insert"):
                return "break"
            if self.asstxt.search("#!", "insert linestart", "insert lineend+1c"):
                #print(event.keysym, event.keycode)# debug
                return "break"
            if event.char=="#" and self.asstxt.search("!", "insert", "insert+1c"):
                return "break"
            if event.char=="!" and self.asstxt.search("#", "insert-1c", "insert"):
                return "break"
                

    #### Functions for updating GUI display
    
    def initMemDisplay(self, page=None):
        if not page:
            page = self.Emu.curPage
        #print("**",self.Emu.memory[0])
        for n in range(len(self.mem_frame)):
            gridpos=1
            for col in range(16):
                tk.Label(self.mem_frame[n], text=self.Emu.hx(col)[1]+" ", font=self.boldfont).grid(row=0, column=gridpos, padx=2, pady=2)
                tk.Label(self.mem_frame[n], text=self.Emu.hx(col)[1]+" ", font=self.boldfont).grid(row=gridpos, column=0, padx=2, pady=2)
                gridpos += 1
        
        self.memLabel = [0]*256*len(self.Emu.memory[0])
        self.ttArray = [0]*256*len(self.Emu.memory[0])
        #print(self.memLabel,"\n", self.ttArray)
        
        index=0
        
        for n in range(len(self.Emu.memory[self.Emu.curPage])):
            for gridy in range(1,17):
                for gridx in range(1, 17):
                    #print(n, "*", index, end=":")
                    self.memLabel[index] = tk.Label(self.mem_frame[n], font=self.defaulttxtfont, text="00", width=2, bg="white")#, font=self.smallfont)
                    self.memLabel[index].grid(row=gridy, column=gridx)
                    self.ttArray[index] = CreateToolTip(self.memLabel[index], "", 200)
                    index += 1
            #print("\n")# debug
        
    def updateDisp(self):
        self.dispAllMemory()
        self.dispCVZN()
        self.dispSP()
        self.dispRegs()
        self.updateWatchWin()
        self.dispPC()
        self.update()# TK update!

    def dispCVZN(self):
        #print(format(self.Emu.CVZN,"04b"))debug
        CVZNstr = format(self.Emu.CVZN & 0b00001111,"04b")
        pageStr = format((self.Emu.CVZN & 0b01110000)>>4, "03b")
        intStr = str(self.Emu.CVZN >> 7)
        self.CVZN_Val.config(text= "    "+intStr+" "+pageStr+"  "+CVZNstr)
        return

    def dispAllMemory(self, page=None):#?? 
        #print("\n\n", "dispAllMemory Call***",len(self.Emu.memory))
        #print("&&",self.Emu.memChanged[self.Emu.curPage])
        # Update Code and Data Memories
        index = 0
        if page:
            curPage = page
        elif self.running:
            curPage = self.Emu.curPage
        else:
            curPage = self.memPageVar.get()
        
        self.memPageVar.set(curPage)
        self.memChanged = self.Emu.memChanged[curPage]
        #print("£",page, self.Emu.curPage, curPage)#, self.Emu.memory[self.Emu.curPage])
        #print("$",self.Emu.memChanged, "\n",self.memChanged)

        # Hide bank 1 (data page) if arch=vn - switch back on if Harvard arch
        # and update Menu
        if self.Emu.arch[curPage] == "vn":
            numbanks = 1
            self.emuNb.add(self.mem_frame[0], text="Page "+str(curPage)+" Memory")
            #self.emuNb.config(0, text=)
            self.emuNb.hide(1)
            self.emumenu.entryconfig(8, label="Arch. = Harvard   ")
        else:
            numbanks = 2
            self.emuNb.add(self.mem_frame[0], text=("Page "+str(curPage)+" ROM"))
            self.emuNb.add(self.mem_frame[1], text=("Page "+str(curPage)+" RAM"))#, state="normal")#, underline=0, padding=2)
            self.emumenu.entryconfig(8, label="Arch. = Von Neuman")
            
        for n in range(numbanks): # All banks in in current CDM8 memory page
            #print("Memory page ", curPage, " bank ", n, "=",self.Emu.memory[curPage][n])
            #if n == 1, Harvard Arch,  so show bank 1
            
            for memval in self.Emu.memory[curPage][n]:#self.Emu.curPage][n]: # For each bank
                self.memLabel[index].config(text=self.Emu.hx(memval),fg=cf.memColour, bg=cf.membgColour)
                    
                if self.memChanged:
                    # Mark any changes in memory contents
                    #print(self.runFrom.get())#debug
                    #print("*", index, n, self.Emu.PC, self.memChanged)
                    if index == 0 or self.Emu.PC==self.runDict[self.runFrom.get()] == 0: # Except at start of run
                        self.memLabel[index].config(fg=cf.memColour)
                    elif n == self.Emu.datamem[curPage]: # If n = 0, then VN Arch, else 1 = HV Arch
                        for addr in self.memChanged:
                            if (index % 256) == addr:# Highlight if memory cell has changed
                                #print(index, int(addr))# debug
                                self.memLabel[index].config(fg=cf.chMemColour)
                   
                    # Runtime Self modifying code warning 
                    if  self.arch[curPage]=="vn" and (self.Emu.PC in self.memChanged): #(n < self.Emu.datamem[curPage] or
                        self.statusMsg.config(text="Runtime Warning:\nExecuting Self\nModified Code?")
                        txtaddr = self.mcode_list.search(self.Emu.hx(self.Emu.PC)+":", "1.0", tk.END)
                        if txtaddr:
                            self.mcode_list.tag_add("smod", "%s linestart+3c" % txtaddr, "%s lineend+1c" % txtaddr) # add tag to k
                            self.mcode_list.tag_config("smod", font=self.boldfont, foreground="red")
                else:
                    self.memLabel[index].config(fg=cf.memColour, text=self.Emu.hx(memval))
                
                if curPage==0: # First page of data (RAM) memory only, show IO ports
                    # Colour IO port adresses
                    indadr = index - (self.Emu.datamem[0] *256)
                    for port in self.IOPorts:
                        for adr in port.getOPadr():
                            #print("**", adr, self.Emu.datamem[self.Emu.curPage])
                            if n == self.Emu.datamem[0] and adr == indadr: # Only page 0 has IO
                                if adr in self.memChanged:
                                    fgcolour=cf.oportColour#cf.chMemColour
                                else: 
                                    fgcolour=cf.oportColour
                                self.memLabel[index].config(bg=cf.oportColour, fg=fgcolour, text=self.Emu.hx(memval))
                        for adr in port.getIPadr():
                            if n == self.Emu.datamem[0] and adr == index - (self.Emu.datamem[0] *256):
                                self.memLabel[index].config(bg=cf.iportColour, fg=cf.membgColour, text=("%02X" % port.portIPvals[adr]))#self.Emu.hx(memval))

                index += 1
        return

    def dispPC(self):
        #print("**", self.Emu.hx(self.Emu.PC))#debug
        ## Update current line, mem addr highlight
        if self.prevPC in self.Emu.BP:
            self.memLabel[self.prevPC].config(bg=cf.bpColour)
        else:
            self.memLabel[self.prevPC].config(bg="white")
        self.prevPC=self.Emu.PC
        self.pcLabVal.config(text=self.Emu.hx(self.Emu.PC))
        self.memLabel[self.Emu.PC].config(bg=cf.PCcolour)

        # Set current line highlighted in memList panel
        #print("**"+self.pcLabVal.cget('text')+":")# debug
        startIndex = self.mcode_list.search(self.pcLabVal.cget('text')+":", "1.0", tk.END, regexp=True)
        #print(startIndex)# debug
        if startIndex:
            # Highlight in mcode panel
            self.mcode_list.tag_delete("pc")
            self.mcode_list.tag_add("pc", "%s linestart+3c" % startIndex, "%s lineend+1c" % startIndex) # add tag to k
            self.mcode_list.tag_config("pc", background=cf.PCcolour)#cf.PCcolour)
            
            # and in asstxt panel
            self.asstxt.tag_delete("pc")
            self.asstxt.tag_add("pc", "%s linestart" % startIndex, "%s lineend+1c" % startIndex) # add tag to k
            self.asstxt.tag_config("pc", background=cf.PCcolour)
            self.asstxt.see("%s linestart" % startIndex)
        return

    def dispRegs(self):
        for index in range(4):
            self.regHexs[index].config(text="0x"+self.Emu.hx(self.Emu.regs[index])) # hex row
            self.regStrs[index].config(text=self.Emu.convert(2,self.Emu.regs[index])) # Char row

            self.regDecs[index].config(text="%+04d" % int(self.Emu.convert(1,self.Emu.regs[index]))+ " %03d" % self.Emu.regs[index]) # dec row
            #print("*"+convert(1,Regs[k]))# debug
            self.regBins[index].config(text=format(self.Emu.regs[index],"08b")) # Bin row
        return

    def dispSP (self):
        if self.Emu.shadowSP:
            stackpage = self.Emu.curPage
        else:
            stackpage = 0
        #print("!",self.Emu.SP)
        self.spVal.config(text=self.Emu.hx(self.Emu.SP[stackpage]))# ??
        self.memLabel[self.prevSP+self.Emu.datamem[stackpage]*256].config(bg="white")
        if self.Emu.SP[stackpage] != 0: #self.runFrom.get():
            self.memLabel[self.Emu.SP[stackpage]+self.Emu.datamem[stackpage]*256].config(bg=cf.SPcolour)
        self.prevSP = self.Emu.SP[stackpage]
        return

    def dispIR(self, IRVal):
        return

    def runProg(self, event=None):
        runAction = self.speedScale.get()
        #print("runAction= ", runAction)#debug
        if self.running: # Then stop
            self.running=False
            self.Emu.HALT=True
        else:
            self.statusMsg.config(text="")
            if runAction == 3: # Step
                # Step
                self.Emu.step(cdm8_io.interrupt, cdm8_io.interruptVector)
                self.updateOPs()
                self.updateDisp()
            elif runAction == 2: # Slow run
                self.runStopButton.config(text="Stop", fg="red", activeforeground="red")
                self.speedScale.config(state="disabled")
                self.update()
                # Slow run
                self.running = True
                self.Emu.HALT=False
                #print(self.running, self.Emu.HALT)#debug
                while self.running and not self.Emu.HALT:
                    #print(self.running, self.Emu.HALT)#debug
                    self.Emu.step(cdm8_io.interrupt, cdm8_io.interruptVector)
                    self.updateOPs()
                    self.updateDisp()
                    if self.Emu.PC in self.Emu.BP: break
                    time.sleep(0.3)
            else:
                #Fast run - 
                self.runStopButton.config(text="Stop", fg="red", activeforeground="red")
                self.update()
                self.running = True
                self.Emu.HALT=False
                stepcount = 0
                while self.running and not self.Emu.HALT:
                    #print(self.Emu.PC, self.running, self.Emu.HALT)# debug
                    self.Emu.step(cdm8_io.interrupt, cdm8_io.interruptVector)
                    self.updateOPs()
                    
                    if runAction == 1 or (runAction==0 and stepcount>23):
                        self.updateDisp()
                        stepcount = 0
                    else:
                        stepcount += 1
                    if self.Emu.PC in self.Emu.BP: break
        self.updateOPs()
        self.updateDisp()
        self.running=False
        self.runStopButton.config(text="Run ", fg="black", activeforeground="black" )
        self.speedScale.config(state="normal")
        return

    def changeTextSize(self, textsize=None):#, event=None):
        if textsize:
            self.textsize = textsize 
        else:
            self.textsize = 10
        #self.default_font = font.nametofont("TkDefaultFont")
        self.defaultfont.configure(size=self.textsize)
        self.option_add("*Font", self.defaultfont)
        
        self.defaulttxtfont.configure(size=self.textsize)
        self.boldfont.configure(size=self.textsize)
        self.smallfont.configure(size=self.textsize-2)
        for port in self.IOPorts:
            port.updateFont()

    def highlighter(self,  event=None):

        # Auto indent (=4) = Only works with tabs, not spaces.
        if event != None:
            #print(event.keysym)# debug
            if event.keysym=="Return":
                #print(event.keysym)#debug
                startIndex=self.asstxt.index("insert linestart -1 lines")
                #print(startIndex)#debug
                while startIndex:
                    startIndex = self.asstxt.search("\t", startIndex, "%s+1c" % startIndex)
                    if startIndex:
                        #print("tab ins", startIndex)
                        self.asstxt.insert("insert", "\t" )
                        startIndex = self.asstxt.index("%s +1c" % startIndex)
                        
        # Clear all highlights, set text to black
        #if self.changed == False:
        #if event!= None and (event.char == event.keysym or len(event.char)) == 1: #ignore special keys
        #msg = 'Punctuation Key %r (%r)' % (event.keysym, event.char)
        first, last = self.asstxt.yview()
        self.running=False
        self.mcode_list.delete(1.0, tk.END)
        self.watchList.delete(1.0, tk.END)
        self.updateLineNos()
        self.clearBPs()
        for n in range(8):# Clear code memory
            self.Emu.memory[n]= [[0]*256, [0]*256]
        self.runDict={"00:":0}
        self.runEPSelect['values'] = ['00:']
        self.runEPSelect.current(0)
        self.labelList=[]
        self.resetEmu()
        self.changed=True
        self.update()
        self.asstxt.yview_moveto(first)
        self.lntext.tag_delete("gline")
        for tag in self.asstxt.tag_names():
            self.asstxt.tag_delete(tag)
        self.watches=[]
        self.asstxt.tag_add("all", '1.0', tk.END) # add tag to k
        self.asstxt.tag_config("all", foreground="black", font=self.defaulttxtfont)#font=self.boldfont)

        # Highlight keywords as defined in cdm8_XXX.py file
        for fgcolour in cf.highlights:
            #print(fgcolour) # Debug
            for word in cf.highlights[fgcolour]: # iterate over directive words list
                startIndex = '1.0'
                while startIndex:
                    startIndex = self.asstxt.search("\\y"+word+"\\y", startIndex, tk.END, regexp=True)
                    if startIndex:
                        endIndex = self.asstxt.index('%s+%dc' % (startIndex, (len(word)))) # find end of word
                        self.asstxt.tag_add(word, startIndex, endIndex) # add tag to k
                        self.asstxt.tag_config(word, foreground=fgcolour, font=self.boldfont)      # and color it with v
                        startIndex = endIndex # reset startIndex to continue searching
                        
                        # Update Watches list
                        # if a line contains a dc or ds (and not commented!)
                        if not self.asstxt.search(cf.commentprefix, "%s linestart"% startIndex, startIndex):
                            for watchword in cf.watchtrigs:
                                if watchword == word:
                                    fmtStr = self.asstxt.search(cf.commentprefix+"$", "%s linestart"% startIndex, "%s lineend" % startIndex)
                                    if fmtStr:
                                        fmtStr = self.asstxt.get("%s + 2c" % fmtStr, "%s + 5c" % fmtStr)
                                        #print("*"+fmtStr)# debug
                                    else:
                                        fmtStr=None
                                    self.watches.append([int(float(startIndex))-1, None, None, fmtStr, 0]) #[lineno, label, adr, disp, items]
                                    # Note, line nos start from 1.0, but indexes from 0

                #print(self.watches)#debug

        # Highlight comments
        fgcolour=cf.commentcolour
        #print("**"+commentcolour)# debug
        startIndex = '1.0'
        while startIndex:
            startIndex = self.asstxt.search(cf.commentprefix, startIndex, tk.END, regexp=False)
            if startIndex:
                endIndex = self.asstxt.index('%s lineend'% startIndex)
                self.asstxt.tag_add("comment", startIndex, endIndex) # add tag to k
                self.asstxt.tag_config("comment", foreground=fgcolour, font=self.asstxt['font'])
                startIndex = endIndex

        # Parse labels and add to self.watches as required
        self.labelList=[]
        fgcolour=cf.labelcolour
        for token in cf.labelspec:
            startIndex = '1.0'
            while startIndex:
                # need to look for : and > as label specifiers
                startIndex = self.asstxt.search(token, startIndex, tk.END, regexp=False)
                if startIndex:
                    lineStart = self.asstxt.index('%s linestart ' % startIndex)
                    lineno = int(lineStart.split(".")[0])
                    # Check line not commented
                    if  not self.asstxt.search(cf.commentprefix, lineStart, startIndex):
                        tagname=self.asstxt.get(lineStart, startIndex).lstrip()
                        #print(tagname)#debug
                        if tagname !="" : # Ignore if not a valid label
                            self.labelList.append(tagname) # Here store lineno as well for runfrom??
                            self.asstxt.tag_add(tagname, lineStart, startIndex) # add tag to k
                            self.asstxt.tag_config(tagname, foreground=fgcolour, font=self.asstxt['font'])
                            #print(lineStart, startIndex)# debug
                            # Check if a watch line
                        # Might still be a label-less dc/ds line, so still add the memory address
                        for index in range(len(self.watches)):
                            if lineno-1 == self.watches[index][0]:
                                self.watches[index][1] = tagname # Store label line num for adding watches
                                #print(self.watches[index][0],"**", lineno, tagname)# debug

                    startIndex = str(float(lineStart)+1) # start from next line
                    #print(self.labelList)#, labelLines)#debug

        # And highlights to brs, ldis etc. where the labels are used
        for word in self.labelList: # iterate over directive words list
            startIndex = '1.0'
            while startIndex:
                startIndex = self.asstxt.search("\\y"+word+"\\y", startIndex, tk.END, regexp=True)
                if startIndex:
                    #print(startIndex)# debug
                    lineStart = startIndex#self.asstxt.index('%s linestart ' % startIndex)
                    endIndex = self.asstxt.index('%s+%dc' % (startIndex, (len(word)))) # find end of word
                    #print(self.asstxt.get(startIndex, endIndex))
                    # Check line not commented
                    if not self.asstxt.search(cf.commentprefix, lineStart+" linestart", lineStart):
                        self.asstxt.tag_add(word, startIndex, endIndex) # add tag to k
                        self.asstxt.tag_config(word, foreground=fgcolour, font=self.boldfont)      # and color it with v
                    startIndex = endIndex # reset startIndex to continue searching
        return



    # Callback to toggle breakpoints
    def toggleBP(self, event=None):
        try:
            bpAdrStr = str(self.mcode_list.get("insert linestart", "insert linestart +2c"))
            #print("*"+bpAdrStr+"*") debug
            bpAdr = int(bpAdrStr, 16)
            #bpAdrStr = "adr"+bpAdrStr
            if bpAdr in self.Emu.BP:
                self.mcode_list.tag_remove(bpAdrStr, 1.0, "end")
                self.asstxt.tag_remove(bpAdrStr, 1.0, "end")
                for n in range(len(self.bpTagNames)):
                    if bpAdrStr == self.bpTagNames[n]: del self.bpTagNames[n]

                self.memLabel[bpAdr].config(bg="white")
                for adr in range(len(self.Emu.BP)):
                    if self.Emu.BP[adr]==bpAdr:
                        del self.Emu.BP[adr]
            else:
                self.Emu.BP.append(bpAdr)
                self.mcode_list.tag_add(bpAdrStr, "insert linestart", "insert lineend+1c")
                self.mcode_list.tag_configure(bpAdrStr, background=cf.bpColour)
                self.bpTagNames.append(bpAdrStr)
                lineNum = self.mcode_list.index("insert linestart")# debug
                self.asstxt.tag_add(bpAdrStr, lineNum+" linestart", lineNum+" lineend+1c")
                self.asstxt.tag_configure(bpAdrStr, background=cf.bpColour)
                self.memLabel[bpAdr].config(bg=cf.bpColour)
        except:
            bpAdr=None
            #print(bpAdr)#debug
        return 'break'

    def clearBPs(self):
        # clear all break points
        self.Emu.BP = []
        for tag in self.bpTagNames:
            self.asstxt.tag_delete(tag)
            #self.mcode_list.delete(tag)
        for mlab in self.memLabel:
            mlab.config(bg="white")
        self.bpTagNames = []

    # Handler when run entry point changed
    def initPC(self, event=None):
        self.Emu.PC = self.runDict[self.runFrom.get()]
        self.Emu.curPage = 0
        self.dispPC()
        #self.dispAllMemory()

    # Callbacks for syncing the text and mcode windows scrolling
    def yscroll1(self, *args):
        if self.asstxt.yview() != self.mcode_list.yview():
            self.mcode_list.yview_moveto(args[0])
            self.lntext.yview_moveto(args[0])
        self.vscroll.set(*args)

    def yscroll2(self, *args):
        if self.asstxt.yview() != self.mcode_list.yview():
            self.asstxt.yview_moveto(args[0])
            self.lntext.yview_moveto(args[0])
        self.vscroll.set(*args)
        
    def yscroll3(self, *args):
        if self.asstxt.yview() != self.lntext.yview():
            self.asstxt.yview_moveto(args[0])
            self.mcode_list.yview_moveto(args[0])
        self.vscroll.set(*args)

    def yview(self, *args):
        #connect the yview (scroll) actions together for asstxt, lntext and mcode_list panels
        self.asstxt.yview(*args)
        self.mcode_list.yview(*args)
        self.lntext.yview_moveto(self.asstxt.yview()[0])
        return
        
    def updateLineNos(self, event=None):
        #numOfLines = int(float(self.asstxt.index('end'))) # Get lines already present in asstxt window
        #endlines = int(self.asstxt.index('end').split('.')[0])
        #print(numOfLines, endlines)
        # Then pack out with same numberof lines as in text window
        
        #print("updateLineNos", self.asstxt.index('end').split('.')[0])# debug
        if self.asstxt.index('end').split('.')[0] != self.lntext.index('end').split('.')[0]:
            self.lntext.config(state="normal")
            self.lntext.delete(1.0, tk.END)
            for n in range(1, int(self.asstxt.index('end').split('.')[0])-1):
                self.lntext.insert(tk.END, str(n)+'\n')
            first, last = self.asstxt.yview()
            self.lntext.yview_moveto(first)
            self.lntext.config(state="disabled")
        return

    # Editor Popup menu for asstxt widget
    def popmenu(self, event=None):
        menu = tk.Menu(self.master,tearoff=0)
        menu.add_command(label="Cut",command=self.cut)
        menu.add_command(label="Copy",command=self.copy)
        menu.add_command(label="Paste",command=self.paste)
        menu.add_command(label="Cancel")
        menu.post(event.x_root,event.y_root)
        return

    # Mcode listing Popup memeu for mcode_list widget
    def popmenuBrk(self, event=None):
        menu = tk.Menu(self.master,tearoff=0)
        menu.add_command(label="Toggle Break",command=self.toggleBP())
        menu.add_command(label="Cancel")
        menu.post(event.x_root,event.y_root)
        return

    def close_window(self):
        if self.running: # Stop Emulator
            self.running = False
            self.Emu.HALT=True
            
        if self.amesRunning:
            self.amesSubmit()
            self.changed=False
            #print(self.timeleft)#debug
            if self.timeleft and self.timeleft>0:
                if not messagebox.askyesno("Are you sure?","Exit Test?"):
                    return
        else:
            if self.asstxt.edit_modified():
                if messagebox.askyesno("Quit","Do you want to save the file..."):
                        self.file_save()
        self.after(100, self.master.destroy)
        return

    ### Editor functions
    def save_if_modified(self, event=None):
        if self.asstxt.edit_modified(): #modified
            response = messagebox.askyesnocancel("Save?", "This document has been modified. Do you want to save changes?") #yes = True, no = False, cancel = None
            if response: #yes/save
                result = self.file_save()
                if result == "saved": #saved
                    return True
                else: #save cancelled
                    return None
            else:
                return response #None = cancel/abort, False = no/discard
        else: #not modified
            return True

    def file_new(self, event=None):
        result = self.save_if_modified()
        if result != None: #None => Aborted or Save cancelled, False => Discarded, True = Saved or Not modified
            self.asstxt.delete(1.0, "end")
            self.mcode_list.delete(1.0, tk.END)
            self.asstxt.edit_modified(False)
            self.labelList = []
            self.changed=True
            self.asstxt.edit_reset()
            self.file_path = None
            self.set_title()
            for n in range(len(self.Emu.memory)):# Clear code memory
                self.Emu.memory[n] = [[0]*256, [0]*256]
                
            self.watches=[]
            self.resetEmu()
        return "break"

    def file_open(self, event=None, filepath=None):
        result = self.save_if_modified()
        if result != None: #None => Aborted or Save cancelled, False => Discarded, True = Saved or Not modified
            if filepath == None:
                filepath = filedialog.askopenfilename(filetypes=(('CDM8 Assembly', '*.asm'), ('All files', '*.*')))
            fileContents=""
            if filepath != None  and filepath != '':
                try:
                    #Python 3
                    with open(filepath, encoding="utf-8") as f:
                        fileContents = f.read()# Get all the text from file.
                except:
                    try:
                        # Python 2
                        with open(filepath) as f:#, encoding="utf-8") as f:
                            fileContents = f.read()# Get all the text from file.
                    except:
                        pass
                # Set current text to file contents
                if fileContents != "":
                    self.asstxt.delete(1.0, "end")
                    self.asstxt.edit_reset()
                    self.asstxt.edit_separator()
                    self.mcode_list.delete(1.0, tk.END)
                    self.asstxt.insert(1.0, fileContents)
                    self.asstxt.edit_modified(False)
                    self.file_path = filepath
                    self.set_title()
                    self.changed=True
                    self.highlighter()
                    self.asstxt.see("1.0")
        return "break"

    def file_save(self, event=None):
        #if platform != "darwin":#Fix for Mac Save problem??
        self.master.config(cursor="watch")
        self.asstxt.config(cursor="watch")

        if self.file_path == None:
            result = self.file_save_as()
        else:
            result = self.file_save_as(filepath=self.file_path)

        self.update()
        time.sleep(0.5)
        #if platform != "darwin":
        self.master.config(cursor="")
        self.asstxt.config(cursor="")
        return "break"

    def file_save_as(self, event=None, filepath=None, ext=".asm", text=None):
        if ext == ".asm": filetype="CDM8 Assembly"
        if ext == ".obj": filetype = "CDM8 Object File"
        if filepath == None:
            if self.file_path:
                self.file_path = str(self.file_path)[:-4]+ext
                filepath = filedialog.asksaveasfilename(filetypes=((filetype, '*'+ext), ('All files', '*.*')),
                        defaultextension ="ext", initialfile=self.file_path.split("/")[-1])
            else:
                filepath = filedialog.asksaveasfilename(filetypes=((filetype, '*'+ext), ('All files', '*.*')),
                    defaultextension=ext) #defaultextension='.asm'
        try:
            with open(filepath, 'wb') as f:
                if text == None:
                    text = self.asstxt.get(1.0, "end-1c")
                try: # Python3
                    # python 3
                    f.write(bytes(text, 'UTF-8'))
                except: # Python2
                    # python 2
                    f.write(bytes(text))#, 'UTF-8'))
                self.asstxt.edit_modified(False)
                self.file_path = filepath
                self.set_title()
                return "Saved"
        except TypeError:
            return "break"
        except:
            #('FileNotFoundError')
            return "break"

        return "break"

    def file_quit(self, event=None):
        result = self.save_if_modified()
        if result != None: #None => Aborted or Save cancelled, False => Discarded, True = Saved or Not modified
            self.root.destroy() #sys.exit(0)

    def set_title(self, event=None, titletxt=None):
        if titletxt != None:
            title=titletxt
        elif self.file_path != None:
            title = os.path.basename(self.file_path)
        else:
            title = "Untitled"
        self.master.title(title + " - " + self.TITLE)
        return

    def undo(self, event=None):
        try:
            self.asstxt.edit_undo()
        except:
            pass
        return "break"

    def redo(self, event=None):
        try:
            self.asstxt.edit_redo()
        except:
            pass
        return "break"

    def cut(self, event=None):
        try:
            self.copy() # selected text in self.cliptext
            if self.amesRunning and ("#!" in self.cliptext): # DO not delete #! lines
                self.cliptext = ""
            elif not(self.amesRunning and self.asstxt.search("#!", "insert linestart", "insert lineend")):
                self.asstxt.delete("sel.first","sel.last")
                self.changed = True
                self.highlighter()
        except tk.TclError:
            pass
        return "break"

    def copy(self, event=None):
        try:
            self.asstxt.clipboard_clear()
            self.cliptext = self.asstxt.get("sel.first","sel.last")
            if self.amesRunning: #Do not use system clipboard
                if ("#!" in self.cliptext): # DO not copy whole #! lines
                    self.cliptext = ""
            else:
                self.asstxt.clipboard_append(self.cliptext)
                #self.cliptext=""
        except tk.TclError:
            pass
        return "break"

    def paste(self, event=None):
        try:
            if self.amesRunning: #Do not use system clipboard
                if ("#!" in self.cliptext): # DO not paste  #! into lines ##
                    self.cliptext = ""
                if self.asstxt.search("#!", "insert linestart", "insert lineend"):
                    # or paste into #! lines
                    self.cliptext = ""
                if self.asstxt.search("#", "insert-1c", "insert") and self.cliptext=="!":
                    self.cliptext=""
                if self.asstxt.search("!", "insert", "insert+1c") and self.cliptext=="#":
                    self.cliptext=""
            else:
                self.cliptext = self.asstxt.selection_get(selection="CLIPBOARD")

            try:
                self.asstxt.delete("sel.first","sel.last")
            except tk.TclError:
                pass #nothing selected
            #print("*", self.cliptext, "*")# debug
            self.asstxt.insert(tk.INSERT,self.cliptext)
            self.changed = True
            self.highlighter()
        except tk.TclError:
            #print("paste error")#debug
            #raise#debug
            pass
        return "break"

    def gotoLine(self, event=None):
        #print(type(self.lineBox.get()))# debug
        try:
            linno = self.lineBox.get()
            #print(linno)
            if linno:
                linno = self.lntext.search(linno, "1.0")
                self.highlightLine(linno)
                self.asstxt.see(linno)
            else: 
                self.highlightLine()
        except:
            pass
        return "break"

    def searchText(self, event=None):
        try:
            searchStr = self.searchBox.get()
            if searchStr:
                if self.prevStr != searchStr:
                    self.startIndex = "1.0"
                self.prevStr = searchStr
                self.startIndex = self.asstxt.search(searchStr, self.startIndex, tk.END)
                endIndex = self.asstxt.index('%s+%dc' % (self.startIndex, (len(searchStr)))) # find end of word
                #print(self.startIndex)
                self.highlightLine(self.startIndex, endIndex)
                self.asstxt.see(self.startIndex)
                self.startIndex = endIndex
            else:
                self.highlightLine()
        except:
            self.startIndex = "1.0"
        return "break"

    def tabBlock(self, event=None, shift=0):
        #if event:
        #    print("*", event.keysym)
        #print(shift)#event.keysym)
        if self.asstxt.tag_ranges("sel"): 
            selecttext = self.asstxt.get("sel.first linestart","sel.last")
            #print("*", selecttext, "*")
            startline = int(self.asstxt.index("sel.first linestart").split('.')[0])
            endline = int(self.asstxt.index("sel.last linestart").split('.')[0])
            if shift == 1: 
                #print(shift)
                # Add a tab char to each line
                for line in range(startline, endline+1):
                    self.asstxt.insert(str(line)+".00", "\t")
                return "break"
            elif shift == -1: #remove tab from all lines of selected text
                #print(shift)
                # check all lines have a tab in forst position
                tabfirst = True 
                for line in range(startline, endline+1):
                    if self.asstxt.get(str(line)+".00", str(line)+".01") != "\t":
                        tabfirst = False
                if tabfirst:
                    for line in range(startline, endline+1):
                        self.asstxt.delete(str(line)+".00", str(line)+".01")                    
            return "break"
        return

        

    #### CDM8 Functions
    def toggleMemPageDisp(self, event=None):
        if self.pageDisp:
            self.memPageFrame.grid_remove()
            self.pageDisp = False
            self.emumenu.entryconfig(9, label="Paged Memory   ")
        else:
            self.memPageFrame.grid()
            self.pageDisp = True
            self.emumenu.entryconfig(9, label="Paged Memory  ✔")
    
    
    def toggleArch(self, event=None):
        if self.arch[self.memPageVar.get()]=="vn":
            self.setArch(arch="hv", page=self.memPageVar.get())
        else:
            self.setArch(arch="vn", page=self.memPageVar.get())
            
    def setShadowSP(self, event=None):
        if self.Emu.shadowSP == False:
            # Toggle off shadow SPs
            self.emumenu.entryconfig(10, label="Shadow SPs      ✔")
            self.Emu.shadowSP = True
        else:
            self.emumenu.entryconfig(10, label="Shadow SPs       ")
            self.Emu.shadowSP = False
        
    def setArch(self, arch="vn", page=0, event=None):
   
        if self.Emu.setArch(arch, page=page)!="Unrecognised Architecture":
            self.arch[page]=arch
        else:
            return "Unrecognised Architecture"
        
        #print(self.memChanged)
        ## Whole Memory display with tooltips to see content!
        self.initMemDisplay(page=page)
        #print("$",self.Emu.memory[page])
        self.updateDisp()
        self.highlighter()
    
    def saveImage(self, event=None, filepath=None):
        #print("save Image")#debug
        self.compileText()
        if filepath == None:
            filepath = filedialog.asksaveasfilename(filetypes=(('Logisim Memory Image', '*.img'), ('All files', '*.*')), defaultextension =".img") #defaultextension='.txt'
        
        try:
            with open(filepath, 'wb') as f:
                try: # Python3
                        f.write(bytes("v2.0 raw\n", 'UTF-8'))
                except: # Python2
                        # python 2
                        f.write(bytes("v2.0 raw\n"))#, 'UTF-8'))
                for memVal in self.Emu.memory[0][0]:
                    try: # Python3
                        f.write(bytes(self.Emu.convert(0, memVal)+"\n", 'UTF-8'))
                    except: # Python2
                        f.write(bytes(self.Emu.convert(0, memVal)+"\n"))#, 'UTF-8'))
                return "saved"
        #except IOError:
        #    return "Cancelled"
        except:
            #raise
            #('FileNotFoundError')
            return "Cancelled"
        
            
    def saveObjFile(self, event=None):
        objText = self.compileText()
        self.file_save_as(ext=".obj", text=objText)

    #def linkObjFiles(self, event=None):
    #    print("Linker")
    #    import cocol
    #    cocol.CocoLink(master=self.master, sym=True)

    def compileText(self, event=None):
        #print("Compiling")
        self.changed=False
        self.running=False
        self.Emu.curPage = 0
        textList=[]
        errorMsg=None
        cocas.errLine=None
        overlapStart = None
        page = 0
        
        self.statusMsg.config(text="")
         # Clear errLine tag from asstxt window
        self.asstxt.tag_delete("err")
        self.mcode_list.delete(1.0, tk.END)
        self.mcode_list.config(wrap=tk.NONE)
        # Clear memory
        #print("£",self.Emu.memory[0])# debug
        for n in range(len(self.Emu.memory)):# Clear code(/data) memory
            self.Emu.memory[n]= [[0]*256, [0]*256] 
        #self.dispAllMemory()
        
        
        ## Compile the program!
        text = self.asstxt.get("1.0", tk.END)
        
        
        
        # IDE directives (prefixed by #$<keyw>=<option>
        # Set architecture. # Harvard $arch=hv, Von Neuman #$arch=vn 
        archpos = self.asstxt.search('#$arch=', "1.0", tk.END)
        if archpos:
            self.arch[0] = self.asstxt.get(archpos+"+7c", archpos+"+9c")
            if self.setArch(self.arch[0], page=0) == "Unrecognised Architecture":
                cocas.errLine = int(float(archpos))
                errorMsg="Line "+str(cocas.errLine)+": Unrecognised Architecture, expected 'hv' or 'vn'"
        
        # Include IO 
        
        
        # Call the Compiler (cocas.py)
        codetext=None
        obj_code=None
        if errorMsg==None:
            filebuff = io.StringIO(text) # cocas likes to use a file type object!
            try:
                obj_code, codetext, errorMsg = cocas.compile_asm(filebuff, self.cdm8ver)
            except Exception as e:
                errorMsg = e

        #print("33", errorMsg)#debug
        
        # If no errors, load CocoEmu, and Update the GUI display
        if codetext != None or obj_code != None and errorMsg==None:
            strippedCode = ""
            for line in codetext.splitlines():#("\n"):
                textList += line
                if line[18:20] == "  " and line[2] == ":":# line is fragmented
                    strippedCode += line[3:15].rstrip(" ")
                else:
                    strippedCode += '\n'+line[:15].rstrip(" ")
            strippedCode = strippedCode[1:]
            #print(strippedCode)# debug
            # Insert in Mcode list window
            index = 0
            for line in strippedCode.splitlines():
                line=line.upper()
                self.mcode_list.insert("end",line+'\n')
                self.mcode_list.tag_add("hexNo", "end -1 lines", tk.END) # add tag to k
                self.asstxt.tag_config("hexNo", font=self.boldfont)

                # Uddate the watch window
                for watch in self.watches:
                    #print(index, watch[0])#debug
                    if index == watch[0]: # Is a watch line
                        #print(line)
                        if ":" in line:
                            watchLine = line.split(":")
                            #print(index, watch[0],"watchLine", watchLine)#debug
                            watch[2] = watchLine[0] # Memory address as str
                            watchLine[1] = watchLine[1].split(" ")
                            #print(watchLine)#debug
                            if type(watchLine[1])==list:
                                watch[4] = len(watchLine[1])-1 # split(" ") inserts an empty item in list???
                            else:
                                watch[4]=1 # Probably not needed?
                            linno = str(index+1)+".0"

                            if not watch[3]:
                                if self.asstxt.search('"', linno, "%s lineend" % linno): watch[3]="str"
                                elif self.asstxt.search('0b', linno, "%s lineend" % linno): watch[3]="bin"
                                elif self.asstxt.search('0x', linno, "%s lineend" % linno): watch[3]="hex"
                                else: watch[3] = "dec"
                index += 1
            #print("**",self.watches)#debug

            # Put memory values in list and load to emulator
            memChunks=obj_code.splitlines()
            lineNum = 1
            for line in memChunks:
                #print("*"+line)#[:4])#debug
                if "ABS" in line:
                    mem = [line.lstrip("ABS").split(":")]
                    for item in mem:
                        memadr = int("0x"+item[0].lstrip(" "), 16)
                        memlist = item[1].lstrip(" ").split(" ")
                        for val in memlist: # Check here for overlapping sects
                            if memadr > 255:
                                errorMsg = "Memory Overflow - program too large"
                                break
                            elif self.Emu.memory[0][0][memadr] > 0 : # Already occupied by code?
                                #print(memadr, lineNum, line)
                                #Find line where asect overlaps?? Problem here??
                                overlapStart=None
                                overlapStart = self.mcode_list.search('%02x:' % memadr, "1.0", stopindex="end" )
                                #print("**",overlapStart, " memadr ", memadr)#debug
                                overlapStart = self.mcode_list.search('%02x:' % memadr, overlapStart, stopindex="end" )
                                #overlapStart = self.asstxt.search('assect %02x:' % memadr, "1.0", stopindex="end" )
                                
                                if overlapStart:
                                    #adr = self.mcode_list.search('%02X:' % memadr, overlapStart+"+1l", stopindex="end" )
                                    #print("*", adr)
                                    #if adr:
                                    cocas.errLine = int(float(overlapStart))
                                    errorMsg = "On line "+str(cocas.errLine)+" ERROR: Overlapping asect! Code from this line onward, will overwrite previously compiled code"
                                    
                                    break
                            else:
                                self.Emu.memory[0][0][memadr]=int("0x"+val, 16)
                                memadr += 1
                        if errorMsg: break
            #print("%",self.Emu.memory[0][1])# debug Memory array image
            if not errorMsg:
                self.resetEmu()
                # Clear errLine tag from asstxt window
                self.asstxt.tag_delete("err")
                self.update()

        # Show error/warning in mcode window
        if errorMsg:
            #print("*"+errorMsg)#DEBUG
            errorMsg = errorMsg.split(" ")
            #print(retError)
            self.mcode_list.delete(1.0, tk.END)
            self.mcode_list.config(wrap=tk.WORD)
            #self.mcode_list.insert(tk.END, errorMsg)#[0]+":\n")
            if len(errorMsg)>6:
                errorMsg.insert(4, "\n")
            if len(errorMsg)>10:
                errorMsg.insert(10, "\n")
            msg = ""
            for word in errorMsg:
                msg += (word + " ")
            errorMsg = msg 
            self.statusMsg.config(text=errorMsg)
            
            # Then highlight line in text editor where error occurs
            if "On line" in errorMsg:
                #print("error line =", int(errorMsg[0][8:11]))
                # cocas.errline picks up some error line numbers, but not all!
                # So more reliable to check the error message directly
                if not cocas.errLine:
                    cocas.errLine = int(errorMsg[8:errorMsg.find(" ", 8)])
                #cocas.errline = int(errorMsg[0][8:11])
                #print(cocas.errLine)# debug
            if cocas.errLine: # Highlights error line
                self.highlightLine(str(cocas.errLine)+".0")
                #self.asstxt.tag_delete("err")
                #self.asstxt.tag_add("err", "%s linestart" % (str(cocas.errLine)+".0"), "%s lineend+1c" % (str(cocas.errLine)+".0")) # add tag to k
                #self.asstxt.tag_config("err", background=cf.errColour)
                #self.updateLineNos()
                #self.update()
                #self.asstxt.see("%s" % str(cocas.errLine)+".0")# scroll to see error line

            for n in range(self.Emu.datamem[self.Emu.curPage],len(self.Emu.memory[self.Emu.curPage])):# Clear data memory
                 self.Emu.memory[n]= [[0]*256]#??
            return None
        else:
            self.resetEmu()
            #print(self.Emu.memory[0][1]) # debug data mem for hv arch
            self.updateDisp()
            #self.dispAllMemory()
        return obj_code # For when cocas called from CLI.
    
    def highlightLine(self, lintxt=None, linend=None):
        if linend ==None:
            linend = "%s lineend+1c" % (lintxt)
        self.asstxt.tag_delete("err")
        if lintxt:
            self.asstxt.tag_add("err", lintxt, linend)#"%s linestart" % (lintxt), "%s lineend+1c" % (lintxt)) # add tag to k
            self.asstxt.tag_config("err", background=cf.errColour)
            self.update()
            self.asstxt.see(lintxt)# scroll to see error line

    def cocolnk(self, event=None):
        try:
            import cocol
        except ImportError:
            print("Cocol Linker not found")
        cocol.CocoLink(self.master)


    def resetEmu(self, event=None):
        self.initPC()
        self.Emu.SP = [0] * 8
        self.Emu.curPage = 0
        if self.changed:
            self.clearBPs()
            #print("BPs cleared")#debug
        else:
            #otherwise replace breakpoints
            #print(self.Emu.BP)
            #print("*",self.mcode_list.tag_names())
            for tagname in self.bpTagNames:
                #print(tagname)#debug
                tagpos = self.mcode_list.search(tagname+":", "1.0", tk.END)
                #print(tagpos)#debug
                self.mcode_list.tag_add(tagname, tagpos, "%s lineend+1c" % tagpos)
                self.mcode_list.tag_configure(tagname, background=cf.bpColour)
                self.asstxt.tag_add(tagname, tagpos, "%s lineend+1c" % tagpos)
                self.asstxt.tag_configure(tagname, background=cf.bpColour)
        self.initPC()
        self.Emu.regs = [0]*4
        self.Emu.CVZN = 0
        # need to clear all memory changed pages
        for n in range(len(self.Emu.memChanged)):
            self.Emu.memChanged[n]=[]
        #self.highlighter()
        # Reset IO ports
        for port in self.IOPorts:
            port.resetPort()
        self.updateDisp()
        self.updateRunSelect()

    def updateRunSelect(self):
        self.runDict=colls.OrderedDict()
        self.runDict["00:"] = 0x00
        for label in self.labelList:
            if cf.entrySpec in label[0]:
                # Add to runDict
                labline=self.asstxt.search("\n"+label, "1.0", tk.END)
                #print(labline, "*"+label+"*")#debug
                runAddr=self.mcode_list.search(":", labline, tk.END)
                #print("**"+runAddr+"**")#debug
                if runAddr != "" and runAddr != "0.0":
                    self.runDict[label]= int(self.mcode_list.get("%s-2c"% runAddr, runAddr),16)
        self.runEPSelect['values']= list(self.runDict.keys())
        self.runEPSelect.current(0)
        self.runFrom.set("00:")#??
        self.Emu.PC = 0
        self.initPC()

    def updateWatchWin(self):
        #print(self.watches)#debug
        self.watchList.delete(1.0, tk.END)
        for watch in self.watches:
            # get watch cells from memory
            #print("watch=", watch)
            if watch[2]:
                memStart=int(watch[2],16)
                if watch[1]:
                    watchLine = watch[2]+"  "+watch[1]
                else:
                    watchLine = watch[2]
                watchLine += " "* (15-len(watchLine))# Align to len = 30
                for n in range(memStart, memStart + watch[4]):
                    
                    #if self.arch == "vn":
                    memCell = self.memLabel[n].cget("text")
                    #else: # harvard
                    #    memCell = self.memLabel[n+256*self.Emu.datamem[self.Emu.curPage]].cget("text")
                    fmt=0 #default = hex
                    if watch[3] == "hex": fmt=0
                    if watch[3] == "dec": fmt=1
                    if watch[3] == "bin": fmt=3
                    if watch[3]== "str": fmt=2
                    #print(watch)#debug
                    #int(self.Emu.convert(1,self.Emu.regs[index]))
                    memCell = self.Emu.convert(fmt, int(memCell,16))
                    if fmt == 0:
                        memCell="Ox"+memCell

                    #print(memCell)#debug
                    watchLine += memCell + " "
                #print(watchLine)# debug
                self.watchList.insert("end", watchLine+'\n')

    # Help function
    def helpwin(self, event=None):
        import webbrowser
        webbrowser.open(cf.helpFile)

#### Final error save files!
def savefiles():
    print("Fatal Error!!! Trying to save files for recovery")
    #CocoIDE.file_save_as(CocoIDE, filepath="AMES-recovery.asm")



def main():
    parser = argparse.ArgumentParser(description='CocoIDE V0.92')
    #parser.add_argument('-p',dest='scrScale',action='store_const',const=True,default=False, help="-p  Presenter mode, expands program window to fill screen")
    parser.add_argument('filename', nargs='?', help="Option <filename>")
    #parser.add_argument("--file", "-f", type=str, required=False)
    args = parser.parse_args()
    #print(args.scrScale, args.filename)#debug
    #sys.excepthook = savefiles # If fatal error save files!
    Emu=cdm8_emu.CDM8Emu()
    CocoIDE(Emu, filename=args.filename).mainloop()
    #savefiles()

if __name__ == '__main__':
    main()



