import base64
import os
import string
from datetime import datetime

import numpy as np
import cv2
from PIL import Image
from flask import Flask, send_file, request, jsonify
from flask_restful import Resource, Api, reqparse
from werkzeug.datastructures import FileStorage

from SRSI import SRSI, DEFAULT_STATUS_FILEPATH, save_status
from sr_image import SRImage

app = Flask('SRSI')
api = Api(app)
segments_parser = reqparse.RequestParser()
segments_parser.add_argument("img", type=FileStorage, location="files", required=True)

output_json = {"name": "val1", "timestamp": "val2", "resolution": "val3"}
input_json = {"img": "val1", "name": "val2", "resolution": "val3", "timestamp": "val4"}


def convert_to_base64(img):
    return base64.b64encode(cv2.imencode('.png', img)[1]).decode()


class ImgController(Resource):
    def get(self):
        # img_return_path = os.path.join("../reconstructed_images/babyface_2.png")
        img_return_path = os.path.join("../reconstructed_images/" + output_json['name'] + '.png')
        print("return_path", img_return_path)

        image = cv2.imread(img_return_path)

        output_json['result_img'] = convert_to_base64(image)

        now = datetime.now()
        current_time = now.strftime("%H:%M:%S")
        print("Current Time =", current_time)

        output_json['resolution'] = output_json['resolution']
        output_json['timestamp'] = current_time
        output_json['name'] = output_json['name']

        print('Output json: ', output_json['timestamp'], output_json['name'], output_json['resolution'])

        return jsonify(output_json)

    def post(self):
        save_status("0")

        print("Check request result", request.is_json)
        request_data = request.get_json()

        image = request_data['img']
        name = request_data['name']
        resolution = request_data['resolution']
        timestamp = request_data['timestamp']

        input_json['img'] = image
        input_json['name'] = name
        input_json['resolution'] = resolution
        input_json['timestamp'] = timestamp

        print('Input json: ', input_json['name'], input_json['resolution'], input_json['timestamp'])

        jpg_original = base64.b64decode(image)
        jpg_as_np = np.frombuffer(jpg_original, dtype=np.uint8)
        img_to_reconstruct = cv2.imdecode(jpg_as_np, flags=1)

        img_path = os.path.join("../upload_images/", name + ".png")
        cv2.imwrite(img_path, img_to_reconstruct)

        print("upload_path", img_path)

        image_input = Image.open(os.path.join(img_path))
        wid, hgt = image_input.size
        print("input_image_resolution", wid, hgt)

        sr_image = SRImage.create_sr_image(image_input)
        reconstructed_sr_image = SRSI().reconstruct(resolution, sr_image)
        # reconstructed_sr_image.save_image(os.path.join("../reconstructed_images/babyface_228.png"), "png")
        reconstructed_sr_image.save_image(os.path.join("../reconstructed_images/", name + ".png"), "png")

        # img_return_path = os.path.join("../reconstructed_images/babyface_2.png")
        img_return_path = os.path.join("../reconstructed_images/", name + ".png")
        print("return_path", img_return_path)

        image_open = Image.open(os.path.join(img_return_path))
        wid, hgt = image_open.size
        print("output_image_resolution", wid, hgt)
        resolution = "(" + str(wid) + "," + str(hgt) + ")"

        image = cv2.imread(img_return_path)

        new_file_name = str(name)

        output_json['result_img'] = convert_to_base64(image)
        output_json['timestamp'] = request_data['time']
        output_json['name'] = new_file_name
        output_json['resolution'] = resolution

        print('Output json: ', output_json['timestamp'], output_json['name'])

        return jsonify(output_json)


class StatusController(Resource):
    def get(self):
        filepath = DEFAULT_STATUS_FILEPATH

        file = open(filepath, "r").read()
        if file == "":
            return 0
        print("Current status ", file)
        number = int(str(file))
        return number


api.add_resource(ImgController, '/img')
api.add_resource(StatusController, '/status')


def save_status(status):
    filepath = DEFAULT_STATUS_FILEPATH
    f = open(filepath, "w")
    f.write(status)
    f.close()


if __name__ == '__main__':
    RECONSTRUCTION_STATUS = 0
    app.run(host='0.0.0.0', port=1000, debug=True)
