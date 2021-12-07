# For import only!
# Sendfile - sendfile.py
# Ames Library for submitting/recieving files from Ames Automatic
# Marking Exam System. Used with CocoIDE (V0.93)
# Author Prof. Alex Shaferenko, V1.0 Dec 2016
# V1.1 - A Shaferenko and M L Walters, Dec 2016:
#   Smallchanges to tidy up code, and prime Ames if file not downloaded intially.
#   Alex: bug fixed that added an extra line to start of every file received.
# V1.2 - Python 3 and Python 3 compatible

version = "AMES sendfile.py V1.2"

import itertools
import urllib,sys
from email import generator
from io import StringIO
try:
    # Python 3
    from urllib.request import urlopen
except ImportError:
    # Python 2
    from urllib import urlopen
    import urlparse
    import urllib2
    #print("Python2 detected")
    #raise
    
import platform, os, subprocess

class MultiPartForm(object):
    """Accumulate the data to be used when posting a form."""

    def __init__(self):
        self.form_fields = []
        self.files = []
        self.boundary = generator._make_boundary()
        return

    def get_content_type(self):
        return 'multipart/form-data; boundary=%s' % self.boundary

    def add_field(self, name, value):
        """Add a simple field to the form data."""
        self.form_fields.append((name, value))
        return

    def add_file(self, fieldname, filename, fileHandle):
        """Add a file to be uploaded."""
        body = fileHandle.read()
        mimetype = 'application/octet-stream'
        self.files.append((fieldname, filename, mimetype, body))
        return

    def __str__(self):
        """Return a string representing the form data, including attached files."""
        # Build a list of lists, each containing "lines" of the
        # request.  Each part is separated by a boundary string.
        # Once the list is built, return a string where each
        # line is separated by '\r\n'.
        parts = []
        part_boundary = '--' + self.boundary

        # Add the form fields
        parts.extend(
            [ part_boundary,
              'Content-Disposition: form-data; name="%s"' % name,
              '',
              value,
         
   ]
            for name, value in self.form_fields
            )

        # Add the files to upload
        parts.extend(
            [ part_boundary,
              'Content-Disposition: file; name="%s"; filename="%s"' % \
                 (field_name, filename),
              'Content-Type: %s' % content_type,
              '',
              body,
            ]
            for field_name, filename, content_type, body in self.files
            )

        # Flatten the list and add closing boundary marker,
        # then return CR+LF separated data
        flattened = list(itertools.chain(*parts))
        flattened.append('--' + self.boundary + '--')
        flattened.append('')
        return '\r\n'.join(flattened)





def submit(content,ext,pin):
    """
    PARAMETERS
    content: a single string representing file content
    ext: 'asm' for an assembler file, 'circ' for Logisim, etc.
    pin: a 4-digit PIN copied from the PIN button of the AMES passport page

    For any extension the server holds at most one file
    (and all prior versions thereof, named cl<SRN>.<ext>.000, <file>.<ext>.001, etc)
    This function uploads the file into the tests/<module>/<EVENTid> subdirectory
    which is always created on AMES whenever an EVENT is set up on the schedule.
    The file name is cl<SRN>.<ext>, where "cl" is a 2-letter prefix that stands for "client".
    If the file exists already (and it would to start with as it contains the test assignment
    for the EVENT, it is versioned to cl<SRN>.<ext>.000, cl<SRN>.<ext>.001, etc. so that
    no file will be lost due to overwriting.

    RETURNED VALUE: (PE,succ,diag)
    where
        PE=True if a protocol error occurred during the HTTP exchange. SHOULD NOT EVER BE True!
        succ=True, if the writing was a success, diag has the confirmation message (server file spec) as a receipt.
        succ=False, if the user is outside the session, not logged in, or have logged out; diag has the reason.
        diag is diagnostic text (usually single line).
    """

    form = MultiPartForm()
    form.add_field('pin', str(format(pin,"04d")))
    # Add a fake file
    try:

        form.add_file('solution', ext, fileHandle=StringIO(content))
    except TypeError:
        form.add_file('solution', ext, fileHandle=StringIO(unicode(content, 'utf-8')))
    except:
        raise
    
    # Build the request
    response = ""
    body = str(form).encode('utf-8')
    headers={'Content-type': form.get_content_type(),'Content-length': len(body)}
    try:
        ping()
        try:
            #Python 3
            req = urllib.request.Request('https://ames.herts.ac.uk/cgi-bin/storefile.py',body,headers)
            response = urllib.request.urlopen(req).read().decode('utf-8')
        except:
            # Python 2
            req = urllib2.Request('https://ames.herts.ac.uk/cgi-bin/storefile.py',body,headers)
            response = urllib2.urlopen(req).read().decode('utf-8')
        response=response.split('\n')
        protocol_error=False
        success=False
        diag=""
    except:
        #raise # debug
        protocol_error=True
        diag="File submit error"
        success = False
    if len(response)<4:
        protocol_error=True
        diag="Short message"
    else:
        if response[0]!="Content-type: application/ames-client":
            protocol_error=True
            diag="wrong header"
            return (protocol_error,success,diag)
        if response[1].rstrip()!="":
            protocol_error=True
            diag="no blank line after header"
            return (protocol_error,success,diag)
        if response[2].rstrip()=="OK":
            success=True
            diag=response[3]
            return (protocol_error,success,diag)
        if response[2].rstrip()=="ERROR":
            success=False
            diag=response[3]
            return (protocol_error,success,diag)

def download(pin,ext):
    """
    Downloads the "current file" from the server with the extension "ext"
    For any extension the server holds at most one file
    (and all prior versions thereof, named cl<SRN>.<ext>.000, <file>.<ext>.001, etc)
    This function downloads the most recently created file cl<SRN>.ext

    PARAMETERS

    pin: integer 4-digit pin, 0<=pin<=9999
    ext: 'asm', 'circ', etc.

    RETURNED VALUE: (succ,string)

    if succ: string contains the current file with the ext "ext"
    if not succ: string contains diagnostic on lines 1-2, and line0=="ERROR"

    """
    url = 'https://ames.herts.ac.uk/cgi-bin/fetchfile.py'
    timeleft=0 # spaceholder for Alex's timeleft variable
    try:
        ping()
        try:
            #Python 3
            data = urllib.parse.urlencode({'pin' : str(format(pin,"04d")), 'ext':ext})
        except:
            #Python 2
            data = urllib.urlencode({'pin' : str(format(pin,"04d")), 'ext':ext})
        binary_data=data.encode('utf-8')
        response = urlopen(url,binary_data)
        headers=dict(response.info())
        success=True
        print(headers)

        try:
            #Python 3
            if headers['Content-Type']== 'application/ames-client':
                success=False
        except:
            #Python 2
            if headers['content-type']== 'application/ames-client': 
                success=False
        bigstring=response.read().decode('utf-8')
        firstnl=bigstring.find('\n')
        if firstnl<0:
            success=False
            filedata+='\nIllegal file content, only one line supplied'
        else:
            try:
                if bigstring[0:2]!="#!":
                    raise ValueError
                timeleft=int(bigstring[2:firstnl])
                filedata=bigstring[firstnl+1:]
            except:                 # files may or may not come with 'timeleft'
                #raise#debug
                timeleft=None
                filedata=bigstring
    except:
        #raise #debug
        success=False
        filedata ="\nFile download error"

    return (success,filedata,timeleft)


def ping(host="ames.herts.ac.uk"):
    """
    Returns True if host responds to a ping request
    """

    # Ping parameters as function of OS
    if  platform.system().lower()=="windows":
        DETACHED_PROCESS = 8 # Need this to avopid opening console window. Timeout 60 secs!
        return subprocess.call("ping -n 1 -t 60000" + host, creationflags=DETACHED_PROCESS) #Windows version!
    else:
        # Linux/Max OSX
        return os.system("ping -c 1 " + host+"> nul") == 0 # Works ok on windows, but open DOS win!


if __name__ == '__main__':
    print(version)
    ext='asm'
    pin=int(input("Type PIN: "))#8895
    recieved_file= u""
    # ASM Test file to send to AMES
    asmFile="""#!05
#! Ames stuff - do not delete.
#CDM8 Assembly language programming example
# M L Walters / S.P.Hunt, Sept 2015
# V1.1
    asect   0x00    # Program begins at memory address 0
    br  _Start  # Skip over the Data section so that the
                # CPU does not attempt to execute the 'data' section



    asect   0x20
    # test tab
    # test tab
        #test tab#
Data:  # Data section - not executed!


pt1:    dc  100, 108, 114, 111, 87      # Decimal integers
pt2:    dc  0b00100000              # Binary number
pt3:    dc  0x6f, 0x6c, 0x6c, 0x65, 0x48    # Hexadecimal neumbers
pt4: dc 0                   # NULL terminates data values
pt5:    dc "hello"
pt6: dc "hi"
# Memory locations 0x02 to 0x13 (2 to 19) will normally be overwritten with
# initial and testing data by the The CoCoMaRo testing robot. The robot is
# not testing this program, but we use the same locations anyway.
    asect   0x40    # Address 0x30, Start of executable machine code
_Start: #!
# Part 1: Pushes data onto the stack (reverses order)
    ldi  r0, 0  # Load r0 with 0 = NUL = End Of Data (EOD) marker
    push r0    # So we know when to stop in 'part2'

    ldi  r0, Data           # Set r0 to point to the locati on called 'data'
    ld   r0, r1                     # Copy the value pointed to by r0 into r1,
                        # so now r1 contains the first data byt

while       # Start of iterated (loop) section
    tst  r1     # Is r1 zero? (= NUL= EOD)
stays   nz              # If not zero, keep iterating, else exit loop
    push r1     # Push the r1 value onto the stack
    inc  r0     # Add 1 to r0 to point at the next data item
    ld   r0, r1 # Copy the next data item into r1
wend                    # Repeat again from while

# Part 2: Pops data from stack and overwrites the original data
    ldi  r0, msg    # load r0 with start address of data
do                  # Start of program loop
    pop  r1     # Get   byte of data from stack to r1
    st   r0, r1 # Copy byte to data section, last byte first.
    inc  r0     # Increment data pointer (r0)
    tst  r1     # Exit loop if r1 is 0 (i.e EOD)
until z                 # Otherwise repeat from do

#Part 3: Tidy up

    ldi  r0, Data # Copy the address of the result (i.e stack pointer)
                 # data into r0 for CocoMaRo / CoCheck to test.
                 # Note, CocoMaRo not used for this exercise.
    halt                  # Stop the processor

    asect 0x60
x:  dc  100, 108, 114, 111, 87      # Decimal integers
msg: ds 12      #$hex
end         # End of program listing

# Note, when runing in emulator:
# Toggle view mode for row 0 of memory to see the message!
# To view the stack contents, toggle view mode for row f.

    """

    success = False
    received_file = u""
    timeleft = None
    try:
        success,received_file,timeleft=download(pin,ext)
    except:
        #raise#debug
        success = False
        pass
    if success:
        print ('timeleft:',timeleft)
        print('\n--\n'+received_file+'\n---')
    else:
        print('FAILURE:\n'+received_file)
        #quit()
    #print(recieved_file) # debug
    #received_file = content
    # Send asm File
    #print (ext, pin, "\n", content)
    PE,success,diag = submit(asmFile,ext,pin)
    #print ("(",submit(content, ext, pin), ")")
    if PE:
        sys.stderr.write("Protocol error: "+diag+'\n')
        quit()
    if success:
        sys.stderr.write("success: "+diag+'\n')
    else:
        sys.stderr.write("failure: "+diag+'\n')

    # send .circ file
    #ext = "circ"
    """
    PE,success,diag = submit(circ_file,ext,pin)
    if PE:
        sys.stderr.write("Protocol error: "+diag+'\n')
        quit()
    if success:
        sys.stderr.write("success: "+diag+'\n')
    else:
        sys.stderr.write("failure: "+diag+'\n')
    """
