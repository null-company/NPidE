#!    Ames stuff - do not delete.
# CDM8 Assembly language programming example
# M L Walters / S.P.Hunt, Sept 2015
# V1.1 
#$arch=hv # Architecture = Von Neuman
# Configure OP_Disp_16Chr at address 0xE0 to see message
#tabs
	#	#	#	#	#	#	#	#	#	#	#	#	#	#	#	#
	asect		0x00	# Program begins at memory address 0
	br	_Start	#!    Skip over the Data section so that the
				# CPU does not attempt to execute the 'data' section
	asect	0x20	
	# 	test ta	#	#	#	#	#
	# 	test tab	#	
	#	test
	# 	test1				
 
Data:  	# Data section - not executed!
pt1:	dc	100, 108, 114, 111, 87 	#$str Decimal integers   
pt2:	dc	0b00100000				#$bin Binary number
pt3:	dc	0x6f, 0x6c, 0x6c, 0x65, 0x48	#$hex Hexadecimal numbers
pt4: dc	0							# NULL terminates data values
pt5:	dc "hello" 
pt6> dc "hi"	#$hex 
# Memory locations 0x02 to 0x13 (2 to 19) will normally be overwritten with
# initial and testing data by the The CoCoMaRo testing robot. The robot is
# not testing this program, but we use the same locations anyway.
	asect	0x40	# Address 0x30, Start of executable machine code
_Start:	#! Do not change!
# Part 1: Pushes data onto the stack (reverses order)
	ldi  r0, 0	# Load r0 with 0 = NUL = End Of Data (EOD) marker
	push r0		# So we know when to stop in 'part2'  

	ldi  r0, Data	# Set r0 to point to the location called 'data'
	ldc   r0, r1		# Copy the value pointed to by r0 into r1,
					# so now r1 contains the first data byte 
Start2>
 while			# Start of iterated (loop) section
	tst  r1		# Is r1 zero? (= NUL= EOD)
stays	nz		# If not zero, keep iterating, else exit loop
	push r1		# Push the r1 value onto the stack
	inc  r0		# Add 1 to r0 to point at the next data item
	ldc   r0, r1	# Copy the next data item into r1
wend			# Repeat again from while 

# Part 2: Pops data from stack and overwrites the original data
	ldi  r0, msg	# load r0 with start address of data
do 					# Start of program loop
	pop  r1	    	# Get	byte of data from stack to r1
	st   r0, r1		# Copy byte to data section, last byte first.
	inc  r0	    	# Increment data pointer (r0)
	tst  r1	    	# Exit loop if r1 is 0 (i.e EOD)
until z				# Otherwise repeat from do

#Part 3: Tidy up

	ldi  r0, Data	# Copy the address of the result (i.e stack pointer)
                 	# data into r0 for CocoMaRo / CoCheck to test.
                 	# Note, CocoMaRo not used for this exercise.
	halt			# Stop the processor

	asect 0x60
x:	dc	100, 108, 114, 111, 87 	#$dec Decimal integers

# Configure OP_Disp_16Chr at address 0xE0
	asect 0xE0
msg: ds 12		#$str
end	#End of program listing

# Note, when runing in emulator:
# Toggle view mode for row 0 of memory to see the message!
# To view the stack contents, toggle view mode for row f.
