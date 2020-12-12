import numpy as np
from PIL import Image
import SRSI
import sr_image_util


class SRImage(object):
    def __init__(self, y_channel, cb_channel=None, cr_channel=None):
        self._y_channel = y_channel
        self._cb_channel, self._cr_channel = cb_channel, cr_channel
        self._size = np.shape(self._y_channel)

    @classmethod
    def create_sr_image_from(cls,image_path):
        image = Image.open(image_path)
        return SRImage.create_sr_image(image)

    @classmethod
    def create_sr_image(cls, image):
        y_image, cb_image, cr_image = sr_image_util.decompose(image)
        return SRImage(y_image, cb_image, cr_image)

    @property
    def size(self):
        return self._size

    def get_data(self):
        return self._y_channel

    def resize_by_ratio(self, ratio):
        size = sr_image_util.create_new_size(self.size, ratio)
        resized_image = sr_image_util.resize(self._y_channel, size)
        return SRImage(resized_image, self._cb_channel, self._cr_channel)

    def put_data(self, data):
        self._y_channel = data

    def upgrade_size(self, size, kernel):
        upgraded_image = sr_image_util.resize(self._y_channel, size)
        blurred_image = sr_image_util.filter(upgraded_image, kernel)
        return SRImage(blurred_image, self._cb_channel, self._cr_channel)

    def downgrade_size(self, size, kernel):
        blurred_image = sr_image_util.filter(self._y_channel, kernel)
        downgraded_image = sr_image_util.resize(blurred_image, size)
        return SRImage(downgraded_image, self._cb_channel, self._cr_channel)

    def _downgrade(self, ratio, kernel):
        size = sr_image_util.create_new_size(self.size, 1.0 / ratio)
        blurred_image = sr_image_util.filter(self._y_channel, kernel)
        downgraded_image = sr_image_util.resize(blurred_image, size)
        downgraded_image = sr_image_util.resize(downgraded_image, self._size)
        return SRImage(downgraded_image, self._cb_channel, self._cr_channel)

    def get_pyramid(self, level, ratio):
        pyramid = []
        r = 1.0
        ALPHA = 2 ** (1.0 / 3)
        for i in range(level):
            r *= ratio
            gaussian_kernel = sr_image_util.gaussian_kernel(sigma=(ALPHA ** i) / 3.0)
            pyramid.append(self._downgrade(r, gaussian_kernel))
        return pyramid

    def get_image_patches(self, patch_size, interval=1):
        image_array = self._y_channel
        return sr_image_util.get_patches(image_array, patch_size, interval)

    def save_image(self, path, extension):
        if self._cb_channel is not None and self._cr_channel is not None:
            self._cb_channel = sr_image_util.resize(self._cb_channel, self._size)
            self._cr_channel = sr_image_util.resize(self._cr_channel, self._size)
        image = sr_image_util.compose(self._y_channel, self._cb_channel, self._cr_channel)
        image.save(path, extension)

    def __add__(self, other_sr_image):
        my_image_data = self._y_channel
        other_image_data = other_sr_image.get_data()
        image_data = my_image_data + other_image_data
        return SRImage(image_data, self._cb_channel, self._cr_channel)

    def __sub__(self, other_sr_image):
        my_image_data = self._y_channel
        other_image_data = other_sr_image.get_data()
        image_data = my_image_data - other_image_data
        return SRImage(image_data, self._cb_channel, self._cr_channel)

    def __mul__(self, factor):
        my_image_data = self._y_channel
        image_data = my_image_data * factor
        return SRImage(image_data, self._cb_channel, self._cr_channel)
