close all
clear all
clc

% Data upload
filename = 'Prova fabi sensore.csv';
[data, TXT, RAW] = xlsread(filename);

% Setting data from the output matrix
sensorid1 = 1;
sensorid2 = 3;
EulerRange = 13:15;
AccRange = 4:6;
GyrRange = 7:9;

% Separation of the data
sensor1data = data(find(data(:,1) == sensorid1),:);
sensor2data = data(find(data(:,1) == sensorid2),:);

% setting of the length of the packet for the loop
packetlength = 50;
angles = zeros(1,3);
angleskalman = zeros(1,3);

% Selection of the columns for each type of data
Euler1 = sensor1data(:,EulerRange); 
Euler2 = sensor2data(:,EulerRange); 
Acc1 = sensor1data(:,AccRange);
Acc2 = sensor2data(:,AccRange);
Gyr1 = sensor1data(:,GyrRange);
Gyr2 = sensor2data(:,GyrRange);

%% Simulation of dataflow
for i = 1:packetlength:(size(sensor1data,1) - packetlength + 1)
    
    if i == 1
        EulZero1 = Euler1(1,:);
        EulZero2 = Euler2(1,:);
    end
    
    CurrentPacket = i:(i+packetlength-1);
    
    EulerPk1 = Euler1(CurrentPacket,:);
    EulerPk2 = Euler2(CurrentPacket,:);
    AccPk1 = Acc1(CurrentPacket,:);
    AccPk2 = Acc2(CurrentPacket,:);
    GyrPk1 = Gyr1(CurrentPacket,:);
    GyrPk2 = Gyr2(CurrentPacket,:);
        
%      figure()
%      subplot(1,2,1)
    newangle = angledetection(EulerPk1,EulerPk2,EulZero1,EulZero2);
    angles(i:(i+packetlength-1),:) = newangle;
%      plot(newangle)
%     subplot(1,2,2)
%     kalmangle = pseudokalman(AccPk1,GyrPk1,angleskalman(size(angleskalman,1),:));
%     angleskalman(i:(i+packetlength-1),:) = kalmangle;
%     plot(kalmangle)
    
end

% figure(2)
% subplot(1,2,1)
% plot(angles)
% subplot(1,2,2)
% plot(angleskalman)

%% Method 1 (Just for knee joint)
subplot(1,3,1)
plot((1:length(Euler1))*0.02,Euler1(:,2))
subplot(1,3,2)
plot((1:length(Euler1))*0.02,Euler2(1:length(Euler1),2))
subplot(1,3,3)
plot(-(Euler2(1:length(Euler1),2)-Euler1(:,2)))
ANGLE = -(Euler2(1:length(Euler1),2)-Euler1(:,2));

%% Method 2 (Universal for all joints) 

Angle1 = Euler1
Angle2 = Euler2

figure(1)
subplot(2,1,1)
plot(Angle1)
subplot(2,1,2)
plot(Angle2)

% Set of zero point
Angle1 = Angle1 - Angle1(1,:);
Angle2 = Angle2 - Angle2(1,:);

figure(1)
subplot(2,1,1)
plot(Angle1)
subplot(2,1,2)
plot(Angle2)

% Calculation of roll, pitch and yaw angle
yaw1 = Angle1(:,1);
pitch1 = Angle1(:,2);
yaw2 = Angle2(:,1);
pitch2 = Angle2(:,2);

x1 = cosd(yaw1).*cosd(pitch1);
y1 = sind(yaw1).*cosd(pitch1);
z1 = sind(pitch1);

x2 = cosd(yaw2).*cosd(pitch2);
y2 = sind(yaw2).*cosd(pitch2);
z2 = sind(pitch2);

for ii = 1: length(Euler1)
    coord1 = [x1(ii) y1(ii) z1(ii)];
    coord2 = [x2(ii) y2(ii) z2(ii)];
    angleFINAL(ii) = acosd(sum(coord1.*coord2));
end

figure(1)
plot(angleFINAL)

