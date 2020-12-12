import numpy as np
from sklearn.neighbors import NearestNeighbors
import multiprocessing
from multiprocessing import Process, Manager

import sr_image_util
from sr_image_util import get_patches_without_dc

DEFAULT_PYRAMID_LEVEL = 3
DEFAULT_DOWNGRADE_RATIO = 2 ** (1.0 / 3)
DEFAULT_NEIGHBORS = 9


class SRDataSet(object):

    def __init__(self, low_res_patches, high_res_patches, neighbors=DEFAULT_NEIGHBORS):
        self._low_res_patches = low_res_patches
        self._high_res_patches = high_res_patches
        self._nearest_neighbor = None
        self._neighbors = neighbors
        self._need_update = True
        self._update()

    @classmethod
    def get_dataset_from_sr_image(cls, sr_image):
        high_res_patches = sr_image_util.get_patches_without_dc(sr_image)
        sr_dataset = None
        for downgraded_sr_image in sr_image.get_pyramid(DEFAULT_PYRAMID_LEVEL, DEFAULT_DOWNGRADE_RATIO):
            low_res_patches = sr_image_util.get_patches_without_dc(downgraded_sr_image)
            if sr_dataset is None:
                sr_dataset = SRDataSet(low_res_patches, high_res_patches)
            else:
                sr_dataset.add(low_res_patches, high_res_patches)
        return sr_dataset

    @property
    def low_res_patches(self):
        return self._low_res_patches

    @property
    def high_res_patches(self):
        return self._high_res_patches

    def _update(self):
        self._nearest_neighbor = NearestNeighbors(n_neighbors=self._neighbors,
                                                  algorithm='kd_tree').fit(self._low_res_patches)
        self._need_update = False

    def add(self, low_res_patches, high_res_patches):
        self._low_res_patches = np.concatenate((self._low_res_patches, low_res_patches))
        self._high_res_patches = np.concatenate((self._high_res_patches, high_res_patches))
        self._need_update = True

    def merge(self, sr_dataset):
        low_res_patches = sr_dataset.low_res_patches
        high_res_patches = sr_dataset.high_res_patches
        self.add(low_res_patches, high_res_patches)

    def multitask_query(self, low_res_patches):
        if self._need_update:
            self._update()
        cpu_count = multiprocessing.cpu_count()
        patch_number, patch_dimension = np.shape(low_res_patches)
        batch_number = patch_number / cpu_count + 1
        jobs = []
        result = Manager().dict()
        for id in range(cpu_count):
            batch = low_res_patches[int(id * batch_number):int((id + 1) * batch_number), :]
            job = Process(target=self.query, args=(batch, id, result))
            jobs.append(job)
            job.start()
        for job in jobs:
            job.join()
        high_res_patches = np.concatenate(result.values())
        return high_res_patches

    def query(self, low_res_patches, id=1, result=None):
        if self._need_update:
            self._update()
        distances, indices = self._nearest_neighbor.kneighbors(low_res_patches,
                                                               n_neighbors=self._neighbors)
        neighbor_patches = self.high_res_patches[indices]
        high_res_patches = self._merge_high_res_patches(neighbor_patches, distances) if \
            self._neighbors > 1 else neighbor_patches
        if result is not None:
            result[id] = high_res_patches
        return high_res_patches

    def _merge_high_res_patches(self, neighbor_patches, distances):
        patch_number, neighbor_number, patch_dimension = np.shape(neighbor_patches)
        weights = sr_image_util.normalize(np.exp(-0.25 * distances))
        weights = weights[:, np.newaxis].reshape(patch_number, neighbor_number, 1)
        high_res_patches = np.sum(neighbor_patches * weights, axis=1)
        return high_res_patches
