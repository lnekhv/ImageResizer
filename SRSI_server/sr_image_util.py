import numpy as np
import scipy.signal
import scipy.ndimage.filters
import scipy.ndimage.interpolation
import scipy.misc
from PIL import Image
import scipy.ndimage.filters

DEFAULT_PATCH_SIZE = [5, 5]
ALPHA = 2 ** (1.0 / 3)


def create_new_size(size, ratio):
    new_size = map(int, [dimension * ratio + 0.5 for dimension in size])
    return new_size


def create_gaussian_kernel(radius=2, sigma=1.0):
    y, x = np.mgrid[-radius:radius + 1, -radius:radius + 1]
    unnormalized_kernel = np.exp(-(x ** 2 + y ** 2) // (2 * sigma * sigma))
    return unnormalized_kernel / np.sum(unnormalized_kernel)


def _valid_patch_size(patch_size):
    if len(patch_size) != 2:
        return False

    height, width = patch_size
    return height == width and height % 2 != 0


def _valid_patch_dimension(patch_dimension):
    patch_height = round(patch_dimension ** (.5))
    patch_width = patch_dimension / patch_height
    return _valid_patch_size([patch_height, patch_width])


def get_pad_size(height, width, patch_width, interval):
    patch_radius = round(patch_width / 2)
    pad_height = patch_width - patch_radius - height % interval
    pad_width = patch_width - patch_radius - width % interval
    return (pad_height, pad_width)


def get_patches(array, patch_size, interval=1):
    if not _valid_patch_size(patch_size):
        raise Exception("Invalid patch size")

    patch_width = patch_size[0]
    patch_dimension = patch_width * patch_width
    patch_radius = round(patch_width / 2)
    patch_y, patch_x = np.mgrid[-patch_radius:patch_radius + 1, -patch_radius:patch_radius + 1]
    array_height, array_width = np.shape(array)
    pad_height, pad_width = get_pad_size(array_height, array_width, patch_width, interval)
    pad_array = np.pad(array, ((patch_radius, pad_height), (patch_radius, pad_width)), 'reflect')
    padded_height, padded_width = np.shape(pad_array)
    patches_y, patches_x = np.mgrid[patch_radius:padded_height - patch_radius:interval,
                           patch_radius:padded_width - patch_radius:interval]
    patches_number = len(patches_y.flat)
    patches_y_vector = np.tile(patch_y.flatten(), (patches_number, 1)) + np.tile(patches_y.flatten(),
                                                                                 (patch_dimension, 1)).transpose()
    patches_x_vector = np.tile(patch_x.flatten(), (patches_number, 1)) + np.tile(patches_x.flatten(),
                                                                                 (patch_dimension, 1)).transpose()
    index = np.ravel_multi_index([patches_y_vector, patches_x_vector], (padded_height, padded_width))
    return np.reshape(pad_array.flatten()[index], (patches_number, patch_dimension))


def merge_patches(patches, output_array_size, kernel, overlap=1):
    patches_number, patch_dimension = np.shape(patches)
    if not _valid_patch_dimension(patch_dimension):
        raise Exception("Invalid patch dimension")
    if patch_dimension != np.shape(kernel.flatten())[0]:
        raise ("Invalid kernel size, kernel size should be equal to patch size.")
    patch_width = round(patch_dimension ** (.5))
    patch_radius = round(patch_width / 2)
    interval = patch_width - overlap
    output_array_height, output_array_width = output_array_size
    pad_size = get_pad_size(output_array_height, output_array_width, patch_width, interval)
    padded_array_size = [d + patch_radius + p for d, p in zip(output_array_size, pad_size)]
    padded_array_height, padded_array_width = padded_array_size
    padded_array = np.zeros(padded_array_size)
    weight = np.zeros(padded_array_size)
    patches_y, patches_x = np.mgrid[patch_radius:padded_array_height - patch_radius:interval,
                           patch_radius:padded_array_width - patch_radius:interval]
    h, w = np.shape(patches_x)
    patch_idx = 0
    for i in range(h):
        for j in range(w):
            patch_x = patches_x[i, j]
            patch_y = patches_y[i, j]
            padded_array[patch_y - patch_radius:patch_y + patch_radius + 1,
            patch_x - patch_radius:patch_x + patch_radius + 1] += \
                np.reshape(patches[patch_idx], (patch_width, patch_width)) * kernel
            weight[patch_y - patch_radius:patch_y + patch_radius + 1,
            patch_x - patch_radius:patch_x + patch_radius + 1] += kernel
            patch_idx += 1
    padded_array /= weight
    output_array_height, output_array_width = output_array_size
    return padded_array[patch_radius:output_array_height + patch_radius,
           patch_radius:output_array_width + patch_radius]


def normalize(array):
    return array.astype(float) / np.sum(array, axis=1)[:, np.newaxis]


def get_patches_without_dc(sr_image, patch_size=DEFAULT_PATCH_SIZE, interval=1):
    patches_without_dc, patches_dc = get_patches_from(sr_image, patch_size, interval)
    return patches_without_dc


def get_patches_from(sr_image, patch_size=DEFAULT_PATCH_SIZE, interval=1):
    patches = sr_image.get_image_patches(patch_size, interval)
    patches_dc = get_dc(patches)
    return patches - patches_dc, patches_dc


def get_dc(patches):
    h, w = np.shape(patches)
    patches_dc = np.mean(patches, axis=1)
    patches_dc = np.tile(patches_dc, [w, 1]).transpose()
    return patches_dc


def reduce_image(high_res_sr_img, low_res_sr_img, iteration, level):
    sigma = (ALPHA ** level) / 3.0

    g_kernel = gaussian_kernel(sigma=sigma)
    back_projected_sr_img = high_res_sr_img

    for i in range(iteration):
        downgraded_sr_image = back_projected_sr_img.downgrade_size(low_res_sr_img.size, g_kernel)
        diff_sr_image = low_res_sr_img - downgraded_sr_image
        upgraded_diff_sr_image = diff_sr_image.upgrade_size(back_projected_sr_img.size, g_kernel)
        back_projected_sr_img = back_projected_sr_img + upgraded_diff_sr_image * 0.5
    return back_projected_sr_img


def gaussian_kernel(radius=2, sigma=1.0):
    gaussian_kernel = create_gaussian_kernel(radius, sigma)
    return gaussian_kernel


def decompose(image):
    image = image.convert('YCbCr')

    image_data = []
    for im in image.split():
        image_data.append(image_to_data(im))
    return image_data


def compose(y_data, cb_data, cr_data):
    y_image = data_to_image(y_data)

    if cb_data is not None and cr_data is not None:
        cb_image = data_to_image(cb_data)
        cr_image = data_to_image(cr_data)
        image = Image.merge("YCbCr", (y_image, cb_image, cr_image))
        image = image.convert("RGB")
        return image
    else:
        return y_image


def filter(image_data, kernel):
    image_data = scipy.ndimage.filters.convolve(image_data, kernel, mode='reflect')
    return image_data


def resize(image_data, size):
    image = data_to_image(image_data, 'F')
    shape = list(map(int, size))

    image = image.resize((shape[1], shape[0]), Image.BICUBIC)
    resized_image_data = image_to_data(image)
    return resized_image_data


def image_to_data(image):
    shape = image.size[1], image.size[0]
    image_data = np.array(list(image.getdata())).reshape(shape)
    return image_data / 255.0


def data_to_image(image_data, mode='L'):
    size = (np.shape(image_data)[1], np.shape(image_data)[0])
    image = Image.new(mode, size)

    image_data = list(image_data.flatten() * 255)
    image_data = list(map(int, image_data))
    image.putdata(image_data)

    return image


def show(image_data):
    im = compose(image_data * 255, None, None)
    im.show()
