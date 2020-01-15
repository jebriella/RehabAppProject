function NEWANGLE = angledetection(EUL1,EUL2,Eulzero1,Eulzero2)
% Function to be recalled during the loop for angle detection

    EUL1 = EUL1 - Eulzero1;
    EUL2 = EUL2 - Eulzero2;
    NEWANGLE = EUL2 - EUL1 +180;   
        
end

