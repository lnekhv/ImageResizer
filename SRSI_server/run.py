import time

from PIL import Image

from SRSI import SRSI, DEFAULT_STATUS_FILEPATH
from sr_image import SRImage

#TESTS FOR SRSI ALGORITHM#

def letter_example(resolution):
    image = Image.open("test_data/letter.png")
    print("Input resolution: ", image.size)
    sr_image = SRImage.create_sr_image(image)
    reconstructed_sr_image = SRSI().reconstruct(resolution, sr_image)
    reconstructed_sr_image.save_image("tests_images_results/letter" + str(resolution) + ".png", "png")
    print("Output resolution: ", reconstructed_sr_image.size)


def babyface_example(resolution):
    image = Image.open("test_data/babyface.png")
    print("Input resolution: ", image.size)
    sr_image = SRImage.create_sr_image(image)
    reconstructed_sr_image = SRSI().reconstruct(resolution, sr_image)
    reconstructed_sr_image.save_image("tests_images_results/babyface" + str(resolution) + ".png", "png")
    print("Output resolution: ", reconstructed_sr_image.size)


def monarch_example(resolution):
    image = Image.open("test_data/monarch.png")
    print("Input resolution: ", image.size)
    sr_image = SRImage.create_sr_image(image)
    reconstructed_sr_image = SRSI().reconstruct(resolution, sr_image)
    reconstructed_sr_image.save_image("tests_images_results/monarch" + str(resolution) + ".png", "png")
    print("Output resolution: ", reconstructed_sr_image.size)


if __name__ == '__main__':
    resolution = 2

    start = time.time()
    babyface_example(resolution)
    end = time.time()

    print("Time execution:", str(end - start))
