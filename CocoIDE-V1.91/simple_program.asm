asect 0x00

ldi r0, 0x0f
ldi r1, 0xe0
loop:
clr r3
add r2, r3
add r1, r3
st r3, r2
inc r2
cmp r2, r0
bne loop

ldi r0, 0x0a
clr r3
add r0, r3
add r1, r3
ld r3, r2
cmp r0, r2

inc r0
clr r3
add r0, r3
add r1, r3
ld r3, r2
cmp r0, r2

halt

# comment
end