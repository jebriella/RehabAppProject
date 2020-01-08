function NEWESTIMATE = pseudokalman(ACC,GYR,ANGLE)

    dT = 0.02;
    alpha = 0.1;
    
    AccX = ACC(:,1);
    AccY = ACC(:,2);
    AccZ = ACC(:,3);
    
    phiXa  = AccX./sqrt(AccY.^2+AccZ.^2);
    phiYa  = AccY./sqrt(AccX.^2+AccZ.^2);
    phiZa  = AccZ./sqrt(AccY.^2+AccX.^2);
    
    Phi = [phiXa phiYa phiZa];

    omegaX = GYR(:,1);
    omegaY = GYR(:,2);
    omageZ = GYR(:,3);
    
%     for ii = 1:length(GYR)
%         if ii > 1
%             Angle(ii,:) = Angle(ii-1) + (GYR(ii-1)+GYR(ii))*dT/2;
%         else 
%             Angle(ii,:) = ANGLE + GYR(ii)*dT/2;
%         end
%     end

    
    
    PHI = (1-alpha)*Angle + alpha*Phi;
    
    NEWESTIMATE = PHI;
        
end