# -*- coding: utf-8 -*-
"""AppData.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1kKkqFZjQGksT51AdL3kZHYK-qGzAv1_W
"""

from google.colab import drive
drive.mount('/content/drive/')

import csv
import os
from random import shuffle
import numpy as np

def dataloader():
    
    file_list = []
    labels = []
    
    with open('/content/drive/My Drive/Colab Notebooks/App/App-data-list.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=';')
        for row in csv_reader:
          file_list.append(row[0])
          labels.append(row[1])
    

    data_list = list(zip(file_list, labels))
    shuffle(data_list)
    file_list[:], labels[:] = zip(*data_list)
    
    tot = len(file_list)
    nrOfTrain = round(tot*0.8)
    
    all_data = np.zeros((tot, 80, 60, 1), dtype='float32')
    
    for i in range(tot):
        with open('/content/drive/My Drive/Colab Notebooks/App/' + file_list[i] + '.csv') as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=',')
            row_counter = 0;
            data = np.zeros((400, 10, 1), dtype='float32')
            for row in csv_reader:
                if (row_counter < 401 and row_counter > 0):
                    d_row = np.zeros((10, 1), dtype='float32')
                    d_row[0] = float(row[3])
                    d_row[1] = float(row[4])
                    d_row[2] = float(row[5])
                    d_row[3] = float(row[6])
                    d_row[4] = float(row[7])
                    d_row[5] = float(row[8])
                    d_row[6] = float(row[15])
                    d_row[7] = float(row[16])
                    d_row[8] = float(row[17])
                    d_row[9] = float(row[18])
                    data[row_counter-1] = d_row
                row_counter = row_counter + 1
        data = np.resize(data, [80, 60, 1])
        all_data[i] = data
        
    train_data = all_data[0:nrOfTrain,:,:]
    val_data = all_data[nrOfTrain:tot,:,:]
    
    train_labels = np.array(labels[0:nrOfTrain])
    val_labels = np.array(labels[nrOfTrain:tot])
    
    return train_data, train_labels, val_data, val_labels

# VGG
def modelVGG(base, data_width, data_height):
    
    model = Sequential()
    
    #1 2
    model.add(Conv2D(filters=Base, input_shape=(data_width, data_height, 1),
                     kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2,2)))
    
    #3 4
    model.add(Conv2D(filters=Base*2, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*2, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2,2)))
    
    #5 6
    model.add(Conv2D(filters=Base*4, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*4, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2,2)))
    
    #7 8 9
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2,2)))
    
    #10 11 12
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(Conv2D(filters=Base*8, kernel_size=(3,3), strides=(1,1), padding='same'))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2,2)))
    
    #13 14 15
    model.add(Flatten())
    model.add(Dense(64))
    model.add(Activation('relu'))
    model.add(Dense(64))
    model.add(Activation('relu'))
    
    #Output
    model.add(Dense(1))
    model.add(Activation('sigmoid'))
    
    model.summary()
    return model

from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Flatten, Dense, Dropout, ZeroPadding2D
from tensorflow.keras.layers import Convolution2D, MaxPooling2D
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Conv2D, MaxPooling2D, Activation, Flatten
from tensorflow.keras.optimizers import Adam
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

img_h = 80
img_w = 60
Base = 32
lr = 0.0001
batch_size = 16
nr_epochs = 10

train_data, train_labels, val_data, val_labels = dataloader()

model = modelVGG(Base, img_h, img_w)

model.compile(optimizer=Adam(lr),
              loss='binary_crossentropy',
              metrics=['accuracy'])

History = model.fit(train_data, 
                    train_labels, 
                    epochs=nr_epochs, 
                    batch_size = batch_size, 
                    validation_data = (val_data, val_labels))

plt.figure(figsize=(4, 4))
plt.title("Learning curve")
plt.plot(History.history["loss"], label="loss")
plt.plot(History.history["val_loss"], label="val_loss")
plt.plot( np.argmin(History.history["val_loss"]),
         np.min(History.history["val_loss"]),
         marker="x", color="r", label="best model")

plt.xlabel("Epochs")
plt.ylabel("Loss Value")
plt.legend();