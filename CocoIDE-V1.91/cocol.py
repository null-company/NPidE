#!/usr/bin/env python
# Python compatibility
# V1 By Prof. Alex Shaferenko.  July 2015
# V1.1. Some modifications by M L Walters, August 2015
# V2.0  GUI Added M.L.Walters, Oct 2017


# Python 2 and 3 compatibility
from __future__ import absolute_import, division, print_function
try:
        input=raw_input # Python 3 style input()
except:
        pass # Running on Python 3

TITLE = "Cocol Linker GUI V2.0"
import argparse
import sys
try:
    # Python 3 tk
    import tkinter as tk
    from tkinter import ttk
    from tkinter import filedialog
    from tkinter import messagebox
    from tkinter import scrolledtext as sctx
    import tkinter.font as font

except:
    # Python 2 tk (runs but not exhaustively tested!)
    # Ames lib (sendfile.py) not python 2 compatible (urllib)
    import Tkinter as tk
    import ttk
    import tkFileDialog as filedialog
    import tkMessageBox as messagebox
    import ScrolledText as sctx
    import tkFont as font





IMG=[0]*256
taken=[]
sects={}
xtrns={}
objfiles=[]
errormsg=""
term=False

class CocoLink(tk.Tk):
    def __init__(self, master=None, name="cocol", exitroot=False, sym=True):
        self.master=master
        self.mainWin = tk.Toplevel(master=master)
        self.mainWin.lift()
        #self.mainWin.update()
        #self.mainWin.wm_attributes("-topmost", False)

        #self.mainWin.__init__()
        self.mainWin.resizable(width=False, height=False)#width=False, height=False)
        self.mainWin.title("Link files: cocol GUI")
        if __name__ == "__main__": self.mainWin.protocol("WM_DELETE_WINDOW",self.closeCocol) #Only if main module
        self.mainWin.focus()
        ## Create buttonbar, link and status panels
        buttonBar = tk.Frame(self.mainWin, name="buttonbar", height=35, width=400, border=2, pady=5)#, bg="red")
        buttonBar.pack(side=tk.TOP, fill=tk.X, expand=False)

        linkPanel = tk.Frame(self.mainWin, name='link', border=2,  relief="sunken", pady=5)#, bg="white")
        linkPanel.pack(side=tk.TOP, fill=tk.BOTH,  expand=1)
        self.linkText=sctx.ScrolledText(linkPanel, height=10)
        self.linkText.pack(expand=1)
        self.linkText.bind("<Key>", lambda e: "break")

        seperator = tk.Frame(self.mainWin, name='sep1', height=35, border=2, pady=5)
        seperator.pack(side=tk.TOP, fill=tk.BOTH, expand=False)

        statusPanel = tk.Frame(self.mainWin, name='status', border=2, relief="sunken", pady=5, bg="white")
        statusPanel.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        self.statusText=sctx.ScrolledText(statusPanel, height=25)
        self.statusText.pack(expand=1)
        self.statusText.bind("<Key>", lambda e: "break")

        #self.linkText.config(state=tk.DISABLED)

        # buttons
        addButton = tk.Button(buttonBar, text="Add\n OBJ File", command=self.addObjFile)#, height=2)
        addButton.pack(side=tk.LEFT)
        remButton = tk.Button(buttonBar, text="Remove\n OBJ File", command=self.remObjFile)#, height=2)
        remButton.pack(side=tk.LEFT)
        linkButton = tk.Button(buttonBar, text="Link\n Include Symbols", command=lambda: self.linkFiles(sym=sym))#, height=2)
        linkButton.pack(side=tk.LEFT)
        linkButton = tk.Button(buttonBar, text="Link\n Logisim Image", command=self.linkFiles)#, height=2)
        linkButton.pack(side=tk.LEFT)
        quitButton = tk.Button(buttonBar, text="Quit\n Linker", command=self.closeCocol)#, height=2)
        quitButton.pack(side=tk.LEFT)

    def addObjFile(self, event=None):
        global objfiles
        #print("add file")
        filepath=None
        try:
            filepath = filedialog.askopenfilename(filetypes=(('CDM8 Object File', '*.obj'), ('All files', '*.*')))
        except:
            pass
        self.mainWin.lift()
        if filepath:
            objfiles.append(filepath)
            #print(objfiles)
            self.linkText.delete(1.0, tk.END)
            for filepath in objfiles:
                #print(filepath)
                self.linkText.insert(tk.END, filepath+"\n")

    def remObjFile(self, event=None):
        print("rem file")
        linno= int(float(self.linkText.index("insert linestart")))
        #print( linno)
        if linno <= len(objfiles):
            del objfiles[linno-1]
            self.linkText.delete("insert linestart", "insert lineend +1c")

    def linkFiles(self, event=None, sym=False):
        global args
        args.sym = sym
        args.lst=True

        #self.statusText.delete("1.0", tk.END)
        if len(objfiles) == 0:
            errormsg = "No files to Link!\n"
        else:
            errormsg, listing = link(objfiles, termp=False)
        if errormsg:
            self.statusText.insert(tk.END, errormsg)
        else:
            self.statusText.insert(tk.END, "\n\nLINKED OK! Image written to:\n "+ objfiles[0][:-4]+".img\n")
            self.statusText.insert(tk.END, "\nLINKER REPORT LISTING:\n"+listing)
            self.statusText.see(tk.END)
        #while int(float(self.statusText.index("end linestart ")))>11: # Scroll
        #    #print("link files", int(float(self.statusText.index("end linestart "))))
        #    self.statusText.delete("1.0", "1.0 lineend +1c")# Scroll


    def closeCocol(self):
        self.mainWin.destroy()
        if __name__ == "__main__":
            self.master.destroy()



#Functions

def EP(s):
    global errormsg, term
        #printed output
    sys.stderr.write(s+'\n')
    errormsg = s+'\n'
    if term:
        print("exit!!!")
        quit(-1)

def yieldimg(outfilename="out"):
    global args
    file=open(outfilename+".img","w")
    if args.encrypt:
        import random
        random.seed()
        thingy=random.getrandbits(32)
        file.write("v2.0 crypt"+format(thingy,"012d")+"\n")
        random.seed(thingy)
        for k in IMG:
            x=random.getrandbits(8)
            file.write(format(int(k)^x,"02x")[:2]+"\n")
        file.close()
        return
    if args.sym:
        file.write("v1.0 sym\n")
        file.write(':'.join([format(int(k),"02x")[:2] for k in IMG])+'\n')
        for sectname in sects:
            sc=sects[sectname]
            secstart=sc['start']
            for (nm,addr) in sc["ents"]:
                file.write(str(nm)+":"+format(addr+secstart,"02x")+"\n")
        return
    file.write("v2.0 raw\n")
    for k in IMG:
        file.write(format(int(k),"02x")[:2]+"\n")
    file.close()
    return IMG

def deploy(name,addr):
        data=sects[name]["data"]
        rel=sects[name]["rel"]
        k=addr
        sects[name]["start"]=k
        for r in rel:
                data[r]+=k
        for d in data:
                if k>255:
                        EP("ERROR: section "+name+" attempts to deploy at address 0x"+format(k,"04x"))
                        return errormsg
                IMG[k]=d
                k+=1
        return



def link(objectfiles=[] , termp=False):
        global IMG, taken, sects, xtrns, args, errormsg, term, args

        print("Linking", args.sym)
        IMG=[0]*256
        taken=[]
        sects={}
        xtrns={}
        objfiles=[]
        errormsg=""
        term = termp
        listing=None

        if args.zero_bound:
                lowbound=0
        else:
                lowbound=0x20
        sects["$abs"]={}
        sects["$abs"]["ents"]=[]
        sects["$abs"]["start"]=0



        listofobj=[]
        for filename in objectfiles:
                if filename[-4:]==".obj":
                        filename=filename[:-4]
                listofobj += [filename]
        outfilename=listofobj[0]


        # Files concatenated to variable text here
        text=[]
        for filename in listofobj:
                try:
                        obfile=open(filename+'.obj','r')
                except IOError:
                        print( filename+".obj: file not found")
                        quit(-1)
                for line in obfile:
                        line=line[:-1]
                        text += [(line,filename)]
                obfile.close()



        # input current object module
        # Note, object text from all filesis contained in variable text

        Name="$abs"

        for (line,filename) in text:
                if line[0:4]== "ABS ":
                        addr=int(line[5:7],16)
                        line=line[9:]
                        cnt=0
                        p=addr
                        while len(line)>=2:
                                if p>255:
                                        EP("ERROR: absolute section in file '"+filename+".obj' requests memory beyond 256 byte limit")
                                        return errormsg, listing
                                IMG[p]=int(line[0:2],16)
                                line=line[3:]
                                cnt=cnt+1
                                p=p+1
                        for (a,c,f) in taken:
                                if addr>=a and addr<a+c:
                                        clash=True
                                elif addr+cnt>a and addr+cnt<=a+c:
                                        clash=True
                                else:
                                        clash=False
                                if clash:
                                        EP("ERROR: absolute section in file '"+f+".obj' " \
                                                                "("+format(a,"02x")+":"+format(c,"02x")+") overlaps with " \
                                                                "("+format(addr,"02x")+":"+format(cnt,"02x")+") in file '"+filename+".obj'")
                                        return errormsg, listing
                        taken+=[(addr,cnt, filename)]
                        continue

                if  line[0:4]== "NAME":
                        Name=line[5:]
                        if Name in sects:
                                EP("ERROR: duplicate section '"+Name+"' in files: '"+sects[Name]["file"]+"' and '"+filename+"'")
                                return errormsg, listing
                        sects[Name]={}
                        sects[Name]["file"]=filename
                        sects[Name]["ents"]=[]
                        continue

                if line[0:4]== "REL ":
                        sects[Name]["rel"]=[]
                        line=line[5:]
                        while len(line)>=2:
                                sects[Name]["rel"]+=[int(line[0:2],16)]
                                line=line[3:]
                        continue

                if line[0:4]=="NTRY":
                        line=line[5:]
                        nm=''
                        while line[0]!=' ':
                                nm+=line[0]
                                line=line[1:]
                        oset=int(line[1:3],16)
                        sects[Name]["ents"]+=[(nm,oset)]
                        continue

                if line[0:4]=="DATA":
                        line=line[5:]
                        data=[]
                        while line!='':
                                data+=[int(line[0:2],16)]
                                line=line[3:]
                        sects[Name]["data"]=data
                        continue

                if line[0:4]=="XTRN":
                        line=line[5:]
                        nm=''
                        while line[0]!=':':
                                nm+=line[0]
                                line=line[1:]
                        if nm not in xtrns:
                                xtrns[nm]=[]
                        line=line[2:]
                        while line!='':
                                sect=''
                                while line[0]!=' ':
                                        sect+=line[0]
                                        line=line[1:]
                                oset=int(line[1:3],16)
                                line=line[3:]
                                xtrns[nm]+=[(sect,oset)]
                                if line=='':
                                        break
                                else:
                                        line=line[1:]
                        continue



        ###### Now start working!
        # check if only asects are present


        if sects=={}:
                yieldimg(outfilename)
                quit(0)

        # Compute free memory segments

        low=0
        free=[]
        if taken!=[]:
                taken = sorted(taken,key=lambda x: x[0])
                f0=''
                for t in taken:
                        (start, length,f)=t

                        if start!=low:
                                free+=[(low,start-low)]
                        low=start+length
                        if low>256:
                                EP("ERROR: asect "+hex(start)+" in file "+f+".obj exceeds memory limit")
                                return errormsg, listing

        if low!=256:
                free+=[(low,256-low)]


        # clip free memory segments up to 'lowbound'

        free0=free
        free=[]
        for seg in free0:
                (start,length)=seg
                if start>=lowbound:
                        free+=[seg]
                elif start+length>lowbound:
                        free+=[(lowbound,start+length-lowbound)]



        # for each sect build a list of entries and another one of exts
        # for each sect build a list of sects it refers to by sharing an ext/entry. Call it a binary relation ref
        # compute the relation ref^t and find which sections are related to "MAIN" by ref^t
        # discard the rest of the sections


        entab={}

        for sect in sects:
                sects[sect]["exts"]=[]
                sects[sect]["refs"]={}

        for sect in sects:
                for (nm,oset) in sects[sect]["ents"]:
                        if nm in entab:
                                EP("ERROR: multiple entry point: '"+nm+"', found in sections '"+entab[nm][0]+"' and '"+sect+"'")
                                return errormsg, listing
                        entab[nm]=(sect,oset)              # entab is a table of entry points yielding (section,offset)





        for xtrn_nm in xtrns:                        # "exts" is a list of sections referred to in this one
                if xtrn_nm not in entab:
                        EP("ERROR: unresolved ext: "+xtrn_nm)
                        return errormsg, listing
                for (sect_where_xtrn_occurs,oset) in xtrns[xtrn_nm]:
                        sects[sect_where_xtrn_occurs]["exts"]+=[entab[xtrn_nm][0]]



        toload={}

        if not args.abs and args.rel:
                if "main" not in sects:
                        EP("ERROR: main section missing")
                        return errormsg, listing
                toload["main"]=len(sects["main"]["data"])
                active_sects=["main","$abs"]
        else:
                active_sects=["$abs"]

        while active_sects!=[]:
                current=active_sects[0]
                del active_sects[0]
                sc=sects[current]
                for nm in sc["exts"]:
                        if nm=="$abs":              # already selected
                                continue
                        if nm==current:             # self-reference, ignore
                                continue
                        if nm in toload:
                                continue                # already selected
                        active_sects+=[nm]
                        toload[nm]=len(sects[nm]["data"])

        loadlist=[]
        for nm in toload:
                loadlist+=[(nm,toload[nm])]
        toload = sorted(loadlist,key=lambda x: x[1],reverse=True)


        for (name,size) in toload:
                free = sorted(free,key=lambda x: x[1])
                found=False
                for k in range(0,len(free)):
                        if size==free[k][1]:
                                deploy(name,free[k][0])
                                del free[k]
                                found=True
                                break
                        if size < free[k][1]:
                                deploy(name,free[k][0])
                                free[k]=(free[k][0]+size,free[k][1]-size)
                                found=True
                                break
                if not found:
                        EP("ERROR: section '"+name+"' size 0x"+format(size,"04x")+" too large to allocate")
                        return errormsg, listing

        for xt in xtrns:
                for (name,offset) in xtrns[xt]:
                        if "start" in sects[name]:
                                (sect_entry,offset_entry)=entab[xt]
                                if sect_entry=="$abs":
                                        entry_address=offset_entry
                                else:
                                        entry_address=sects[sect_entry]["start"]+offset_entry
                                IMG[ sects[name]["start"]+offset ]+= entry_address

        IMG = yieldimg(outfilename)



        if args.lst and not errormsg:
                listing=""
                for sect in sects:
                        sc=sects[sect]
                        #print("&&&&&&\n",sc,"\n\n")
                        if "file" in sc:
                                frf="' from file:"+sc["file"]+".obj"
                        else:
                                frf="'"
                        print ("\nSECTION '"+sect+frf)
                        listing+=("\nSECTION '"+sect+frf)
                        if "start" not in sc:
                                print ("\t\t<< not deployed >>\n")
                                listing+=("\n\t\t<< not deployed >>\n")
                        else:
                                start=sc["start"]
                                if "data" in sc:
                                        size=len(sc["data"])
                                        print ("\tALLOCATION start: "+format(start,"02x")+" size: "+format(size,"02x"))
                                        listing+=("\n\tALLOCATION start: "+format(start,"02x")+" size: "+format(size,"02x"))
                                print ("\t\tENTRY points")
                                listing+=("\n\t\tENTRY points")
                                for (nm,offset) in sc["ents"]:
                                        print ("\t\t'"+nm+"':\t"+format(start+offset,"02x"))
                                        listing+=("\n\t\t'"+nm+"':\t"+format(start+offset,"02x"))

                if taken!=[]:
                        print ("\n\nABSOLUTE SEGMENTS ALLOCATED:\n")
                        listing+=("\n\nABSOLUTE SEGMENTS ALLOCATED:")
                        for (addr,size,f) in taken:
                                print("From file: "+f+".obj\n\t\t start: "+format(addr,"02x")+" size:"+format(size,"02x"))
                                listing+=("from file: "+f+".obj\n\t\t start: "+format(addr,"02x")+" size:"+format(size,"02x"))
        return errormsg, listing

 # Parse command line
parser = argparse.ArgumentParser(description='CdM-8 Linker v1.0')
parser.add_argument('objfile',type=str, nargs='*', help='objfile[.obj] ...')

parser.add_argument('-l',dest='lst',action='store_const',const=True,default=False, help="produce summary")
parser.add_argument('-a',dest='abs',action='store_const',const=True,default=False, help="absolute code")
parser.add_argument('-r',dest='rel',action='store_const',const=True,default=False, help="load starting with main")
parser.add_argument('-y',dest='encrypt',action='store_const',const=True,default=False, help="encrypt the image file")
parser.add_argument('-z',dest='zero_bound',action='store_const',const=True,default=True, help="start from 0")
parser.add_argument('-s',dest='sym',action='store_const',const=True,default=False, help="symbol-enhanced image")
args = parser.parse_args()




if __name__ == "__main__":
    if args.objfile:
        link(args.objfile, termp=True)
    else:
        args.lst=True
        #args.sym=True
        root= tk.Tk()
        root.withdraw()
        cocolGUI = CocoLink(master=root, name="cocol", exitroot=True)
        root.mainloop()



