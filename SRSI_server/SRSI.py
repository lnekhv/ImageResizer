import math
import sys

import sr_image_util
from sr_databset import SRDataSet

DEFAULT_STATUS_FILEPATH = "status.txt"
DEFAULT_RECONSTRUCT_LEVEL = 6
ALPHA = 2 ** (1.0 / 3)


class SRSI(object):

    def __init__(self):
        self._kernel = sr_image_util.create_gaussian_kernel()
        self.cursor = None

    def reconstruct(self, ratio, sr_image):
        construct_level = int(math.log(ratio, ALPHA) + 0.5)

        sr_dataset = SRDataSet.get_dataset_from_sr_image(sr_image)
        reconstructed_sr_image = sr_image

        for level in range(construct_level):
            reconstructed_sr_image = self._reconstruct(ALPHA, reconstructed_sr_image, sr_dataset)

            reconstructed_sr_image.save_image("../images_levels/level_" + str(level) + ".png", "png")

            reconstructed_sr_image = sr_image_util.reduce_image(reconstructed_sr_image, sr_image, 3, level + 1)
            new_sr_dataset = SRDataSet.get_dataset_from_sr_image(reconstructed_sr_image)
            sr_dataset.merge(new_sr_dataset)

            RECONSTRUCTION_STATUS = (float(level + 1) / construct_level * 100)
            save_status(str(int(RECONSTRUCTION_STATUS)))
            read_status()
            print("Reconstructing: ", int(RECONSTRUCTION_STATUS), " %")
            sys.stdout.flush()

        return reconstructed_sr_image

    def _reconstruct(self, ratio, sr_image, sr_dataset):
        resized_sr_image = sr_image.resize_by_ratio(ratio)
        patches_without_dc, patches_dc = sr_image_util.get_patches_from(resized_sr_image, interval=4)
        high_res_patches_without_dc = sr_dataset.multitask_query(patches_without_dc)
        high_res_patches = high_res_patches_without_dc + patches_dc
        high_res_data = sr_image_util.merge_patches(high_res_patches, resized_sr_image.size, self._kernel)
        resized_sr_image.put_data(high_res_data)
        return resized_sr_image


def save_status(status):
    f = open(DEFAULT_STATUS_FILEPATH, "w")
    f.write(status)
    f.close()


def read_status():
    filepath = DEFAULT_STATUS_FILEPATH
    file = open(filepath, "r").read()
    if file == "":
        return 0
    print("Current status ", file)
