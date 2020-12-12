import unittest

import numpy as np
from PIL import Image

from sr_image import SRImage


class TestSRImage(unittest.TestCase):

    def setUp(self):
        self.image = Image.open("../test_data/babyface.png")
        self.sr_image = SRImage.create_sr_image_from("../test_data/babyface.png")

    def test_image_size(self):
        self.assertEqual(self.image.size, self.sr_image.size)

    def test_get_pyramid(self):
        pyramid = self.sr_image.get_pyramid(6, 1.25)
        self.assertEqual(len(pyramid), 6)
        for downgraded_sr_image in pyramid:
            self.assertEqual(self.sr_image.size, downgraded_sr_image.size)

    def test_patchify(self):
        patch_width = 5
        patches = self.sr_image.get_image_patches([patch_width, patch_width])
        height, width = self.sr_image.size
        patches_size = np.shape(patches)
        self.assertEqual(patches_size[0], (height+1)*(width+1))
        self.assertEqual(patches_size[1], 25)

if __name__ == "__main__":
    unittest.main()