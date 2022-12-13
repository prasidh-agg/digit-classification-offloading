import os
from flask import Flask, request
import pathlib
import time
import base64
import cv2
import numpy as np
from keras.models import load_model
from io import BytesIO
UPLOAD_DIRECTORY = './uploads/'
model = load_model("model_quadrant1.h5")
app = Flask(__name__)
app.config['uploadDirectory'] = UPLOAD_DIRECTORY


def set_image_shape(original_image):
    set_shape_to_predict = cv2.resize(original_image, (14,14))
    set_shape_to_predict = set_shape_to_predict.astype('float32')
    set_shape_to_predict = set_shape_to_predict.reshape(1,14,14,1)
    set_shape_to_predict = 255-set_shape_to_predict
    set_shape_to_predict /= 255
    return set_shape_to_predict

@app.route('/upload', methods=['POST'])
def predictAndSaveFile():
    if request.method == 'POST':
        if not ('photoFile' in request.values):
            return "No photo received", 400

        # We are passing the image in base 64 encoding and decoding it back here.
        encoded_photo = request.values['photoFile']
        decoded_to_original_photo = base64.b64decode(encoded_photo)

        temp_path = "tempFile.png"
        with open(temp_path, "wb") as f:
            f.write(decoded_to_original_photo)

        orignal_photo_to_numpy = np.frombuffer(decoded_to_original_photo, dtype=np.uint8)
        cv2Img = cv2.imdecode(orignal_photo_to_numpy, flags=1)

        image_ready_to_predict = set_image_shape(cv2.imread("tempFile.png", cv2.IMREAD_GRAYSCALE))
        cv2.imwrite("testFile.png", image_ready_to_predict)
        #predict the number
        final_prediction = model.predict(image_ready_to_predict)
        get_classes_result = np.argmax(final_prediction, axis=1)
        print("value ", final_prediction[0][get_classes_result])

        final_output_of_this_server = str(get_classes_result[0]) + "," + str(final_prediction[0][get_classes_result][0])
        
        print(final_output_of_this_server)
        return final_output_of_this_server, 200
    else:
        return "Invalid Request",400

if __name__ == '__main__':
    app.run(host="0.0.0.0", port="45")
